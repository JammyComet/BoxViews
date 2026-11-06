package com.example.boxview.viewmodel

// Lógica: Agrupa y consolida inventarios mensuales.
import com.example.boxview.modelo.*

// Estas funciones se llaman dentro de la misma transacción que el conteo.
internal object ConsolidacionMensual {
    suspend fun preparar(db: AppDatabase, tipo: String, ubicacionId: Long, sucursalId: Long, nombre: String,
                        zonasDisponibles: Map<Long, String>): CampaniaMensual {
        val dao = db.campaniasMensualesDao()
        val mes = mesCampaniaInventario(nombre)
        require(mes.isNotBlank()) { "Escribe el mes y año al comienzo, por ejemplo Noviembre 2026 - Estante A" }
        val existente = dao.buscar(tipo, ubicacionId, mes)
        if (existente != null) {
            require(existente.estado == "ABIERTA") { "El inventario mensual $mes ya fue consolidado" }
            return existente
        }
        require(zonasDisponibles.isNotEmpty()) { "No hay estanterías para el inventario mensual" }
        val nuevo = CampaniaMensual(tipo = tipo, ubicacionId = ubicacionId, sucursalId = sucursalId, mes = mes)
        val id = dao.crearCampania(nuevo)
        zonasDisponibles.forEach { (pos, ruta) -> dao.crearZona(ZonaCampania(id, pos, ruta)) }
        return nuevo.copy(id = id)
    }

    suspend fun reservar(db: AppDatabase, campania: CampaniaMensual, zonas: Set<Long>, inventario: Long) {
        val dao = db.campaniasMensualesDao()
        require(zonas.isNotEmpty()) { "Selecciona al menos un estante" }
        zonas.forEach { pos ->
            require(dao.reservarZona(campania.id, pos, inventario) == 1) {
                "El estante $pos ya fue contado, está reservado o no pertenece a este mes"
            }
        }
    }

    suspend fun liberar(db: AppDatabase, campaniaId: Long?, inventarioId: Long) {
        if (campaniaId != null) db.campaniasMensualesDao().liberarZonas(campaniaId, inventarioId)
    }

    private suspend fun terminar(db: AppDatabase, campaniaId: Long, inventarioId: Long, detalles: List<DetalleCampania>) {
        val dao = db.campaniasMensualesDao()
        val campania = dao.buscarId(campaniaId) ?: error("Campaña mensual no encontrada")
        require(campania.estado == "ABIERTA") { "El mes ya está cerrado" }
        val zonas = dao.zonas(campaniaId).filter { it.inventarioId == inventarioId && it.estado == "EN_CURSO" }
        require(zonas.isNotEmpty()) { "Este conteo no tiene estantes reservados" }
        require(detalles.all { it.posicionId in zonas.map(ZonaCampania::posicionId) }) {
            "El informe contiene una zona fuera del conteo"
        }
        detalles.forEach { dao.guardarDetalle(it) }
        val fecha = System.currentTimeMillis()
        require(dao.entregarZonas(campaniaId, inventarioId, fecha) == zonas.size) {
            "Cambió la asignación de una zona del inventario"
        }
        if (dao.zonasPendientes(campaniaId) == 0) {
            require(dao.finalizar(campaniaId, fecha) == 1) { "No se pudo finalizar el mes" }
        }
    }

    suspend fun cerrarBodega(db: AppDatabase, inventario: InventarioBodega) {
        val campaniaId = inventario.campaniaId ?: return
        val invDao = db.inventariosDao()
        val tareas = invDao.tareasInventarioActuales(inventario.id)
        val lineas = tareas.flatMap { invDao.lineasInventarioActuales(it.id) }
        val contadas = lineas.associateBy { it.productoId to it.posicionId }
        require(contadas.size == lineas.size) { "Se contaron dos veces las unidades de un mismo estante" }
        val referencias = invDao.referenciasInventario(inventario.id).associateBy { it.productoId to it.posicionId }
        val ajustes = tareas.flatMap { invDao.ajustesInventarioActuales(it.id) }
            .map { it.productoId to it.posicionId }.toSet()
        val posiciones = db.inventarioDao().posicionesActuales(inventario.bodegaId)
        val lista = (referencias.keys + contadas.keys).distinct().map { (productoId, posicionId) ->
            val producto = db.inventarioDao().productoPorId(productoId)
            val pos = posiciones.firstOrNull { it.id == posicionId }
            DetalleCampania(
                campaniaId = campaniaId, inventarioId = inventario.id, posicionId = posicionId,
                codigo = producto?.codigo ?: "ID-$productoId", producto = producto?.nombre ?: "Producto $productoId",
                ruta = pos?.let { rutaBodega(it, posiciones) } ?: "Ubicación #$posicionId",
                cantidadSistema = referencias[productoId to posicionId]?.cantidad ?: 0,
                cantidadContada = contadas[productoId to posicionId]?.cantidadContada,
                ajusteAplicado = (productoId to posicionId) in ajustes,
                fechaCierre = System.currentTimeMillis()
            )
        }
        terminar(db, campaniaId, inventario.id, lista)
    }

    suspend fun cerrarTienda(db: AppDatabase, inventario: InventarioTienda) {
        val campaniaId = inventario.campaniaId ?: return
        val lineas = db.operacionesTiendaDao().lineasInventario(inventario.id)
        val lista = lineas.map { l ->
            DetalleCampania(
                campaniaId = campaniaId, inventarioId = inventario.id, posicionId = l.posicionId,
                codigo = l.codigoProductoCierre ?: "ID-${l.productoId}",
                producto = l.nombreProductoCierre ?: "Producto ${l.productoId}",
                ruta = l.rutaUbicacionCierre ?: "Ubicación #${l.posicionId}",
                cantidadSistema = l.stockInicio,
                cantidadContada = l.cantidadContada,
                ajusteAplicado = l.cantidadContada != null && l.cantidadContada != l.stockInicio,
                fechaCierre = System.currentTimeMillis()
            )
        }
        terminar(db, campaniaId, inventario.id, lista)
    }
}
