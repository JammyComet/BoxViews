package com.example.boxview.viewmodel

// Lógica: Lógica de compras y abastecimiento externo.
import com.example.boxview.mensajeErrorUsuario
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.example.boxview.modelo.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AbastecimientoExternoViewModel(app: Application) : AndroidViewModel(app) {
    // Conexion con la capa de datos.
    private val db = AppDatabase.getDatabase(app)
    private val dao = db.abastecimientoExternoDao()
    private val productosDao = db.productosDao()
    private val cajasDao = db.cajasDao()
    private val administracionDao = db.administracionDao()
    private val operacionesTiendaDao = db.operacionesTiendaDao()
    private val informesDao = db.informesGerenciaDao()

    data class LineaPedido(val productoId: Long, val cantidad: Int)

    // Pedidos externos asociados a bodega.
    fun solicitudesBodega(id: Long): Flow<List<SolicitudExterna>> = dao.solicitudesDeBodega(id)
    fun solicitudesSucursal(id: Long): Flow<List<SolicitudExterna>> = dao.solicitudesDeSucursal(id)
    fun solicitudesGerencia(): Flow<List<SolicitudExterna>> = dao.todasSolicitudes()
    fun lineas(id: Long): Flow<List<LineaSolicitudExterna>> = dao.observarLineas(id)
    fun entrega(id: Long): Flow<Caja?> = dao.cajaEntrega(id)
    fun permisoGerencia(id: Long): Flow<Int> = dao.gerenteActivo(id)
    fun resumenFinanciero(gerenteId: Long): Flow<List<ResumenFinancieroSucursal>> = dao.resumenFinanciero(gerenteId)
    fun resumenVentas(gerenteId: Long): Flow<List<ResumenVentasSucursal>> = db.preciosVentaDao().resumenVentasGerencia(gerenteId)
    suspend fun ventasParaExportar(gerente: Usuario): List<SalidaTienda> {
        require(dao.gerenteAutorizado(gerente.id) == 1) { "Solo Gerencia puede exportar ventas" }
        return db.preciosVentaDao().ventasValorizadasGerencia(gerente.id)
    }

    suspend fun datosFinancierosExportacion(gerente: Usuario): Pair<List<SolicitudExterna>, List<LineaSolicitudExterna>> {
        require(dao.gerenteAutorizado(gerente.id) == 1) { "Acceso reservado a gerencia" }
        return dao.solicitudesFinancieras() to dao.lineasFinancieras()
    }

    fun detallesCaja(id: Long): Flow<List<DetalleCaja>> = cajasDao.observarDetallesCaja(id)
    fun indicadores(): Flow<List<IndicadorSucursal>> = dao.indicadoresGlobales()
    fun alertas(): Flow<List<ProductoBajoMinimo>> = dao.faltantesGlobales()
    fun historial(): Flow<List<EventoSolicitudExterna>> = dao.eventosGlobales()
    fun movimientosBodega(): Flow<List<Movimiento>> = dao.movimientosGlobalesBodega()
    fun movimientosTienda(): Flow<List<MovimientoTienda>> = dao.movimientosGlobalesTienda()
    data class HistoricoCompleto(val eventos: List<EventoSolicitudExterna>,
        val bodega: List<Movimiento>, val tienda: List<MovimientoTienda>,
        val salidas: List<SalidaTienda>,
        val informesBodega: List<InformeInventario>,
        val detallesBodega: List<DetalleInformeInventario>,
        val inventariosTienda: List<InventarioTienda>,
        val detallesTienda: List<LineaInventarioTienda>,
        val cajas: List<Caja>, val detallesCaja: List<DetalleCaja>,
        val eventosCaja: List<EventoCaja>, val incidencias: List<IncidenciaCaja>,
        val eventosConteoTienda: List<EventoConteoTienda>,
        val eventosAdministracion: List<EventoAdministrativo>)

    suspend fun historialCompleto(gerente: Usuario): HistoricoCompleto {
        require(dao.gerenteAutorizado(gerente.id) == 1) { "Se requiere una cuenta de gerencia activa" }
        // Operacion agrupada en Room.
        return db.withTransaction {
            HistoricoCompleto(dao.todosEventosExterno(), dao.todosMovimientosBodega(),
                dao.todosMovimientosTienda(), operacionesTiendaDao.salidasGlobales(),
                informesDao.todosInformesBodega(), informesDao.todosDetallesBodega(),
                informesDao.todosInventariosTienda(), informesDao.todasLineasTienda(),
                dao.todasLasCajas(), dao.todosLosDetallesCaja(), dao.todosEventosCaja(),
                dao.todasLasIncidencias(), operacionesTiendaDao.todosEventosConteo(),
                dao.todosEventosAdministracion())
        }
    }
    fun operadores(bodega: Long): Flow<List<Usuario>> = dao.operadoresRecepcion(bodega)
    fun productos(): Flow<List<Producto>> = productosDao.productos()
    fun sucursales(): Flow<List<Sucursal>> = productosDao.sucursales()
    fun bodegas(): Flow<List<Bodega>> = productosDao.todasBodegas()

    private fun accion(tarea: suspend () -> String, respuesta: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try { respuesta(true, tarea()) }
            catch (e: Exception) { respuesta(false, mensajeErrorUsuario(e, "No se pudo completar la operación")) }
        }
    }

    // solicitud del jefe a su propia bodega
    fun solicitar(jefe: Usuario, bodega: Bodega, motivo: String, lineas: List<LineaPedido>,
                 respuesta: (Boolean, String) -> Unit) = accion({
        db.withTransaction {
            require(dao.jefeAutorizadoExterno(jefe.id, bodega.id) == 1) { "Sin permiso para solicitar en esta bodega" }
            require(dao.bodegaDeSucursal(bodega.id, bodega.sucursalId) != null) { "Bodega inactiva" }
            require(motivo.trim().length in 5..300) { "Indique un motivo de 5 a 300 caracteres" }
            require(lineas.isNotEmpty() && lineas.size <= 50) { "Ingrese entre 1 y 50 productos" }
            require(lineas.distinctBy { it.productoId }.size == lineas.size) { "Hay productos repetidos" }
            require(lineas.all { it.cantidad in 1..100000 }) { "Cantidades fuera de rango" }
            for (l in lineas) require(productosDao.productoPorId(l.productoId) != null) { "Producto no registrado" }
            val id = dao.nuevaSolicitud(SolicitudExterna(sucursalId = bodega.sucursalId,
                bodegaId = bodega.id, solicitanteId = jefe.id, motivo = motivo.trim()))
            for (l in lineas) dao.nuevaLinea(LineaSolicitudExterna(solicitudId = id,
                productoId = l.productoId, cantidad = l.cantidad))
            dao.nuevoEvento(EventoSolicitudExterna(solicitudId = id, usuarioId = jefe.id,
                accion = "CREADA", detalle = motivo.trim()))
            "Solicitud #$id enviada a administración"
        }
    }, respuesta)

    // el administrador tramita, no aprueba la compra
    fun enviar(admin: Usuario, sucursalId: Long, id: Long, observacion: String,
               proveedorCotizado: String, precios: Map<Long, Long>,
               respuesta: (Boolean, String) -> Unit) = accion({
        db.withTransaction {
            require(dao.adminAutorizadoExterno(admin.id, sucursalId) == 1) { "Sin permiso en esta sucursal" }
            val solicitud = dao.solicitudPorId(id) ?: error("Solicitud inexistente")
            require(solicitud.sucursalId == sucursalId && solicitud.estado == "PENDIENTE_ADMIN") { "Solicitud no disponible" }
            require(dao.bodegaDeSucursal(solicitud.bodegaId, sucursalId) != null) { "La bodega ya no está activa" }
            val lineas = dao.lineasActuales(id)
            require(lineas.isNotEmpty()) { "Solicitud sin productos" }
            require(proveedorCotizado.trim().length in 3..100) { "Indique proveedor cotizado" }
            require(precios.keys == lineas.map { it.productoId }.toSet()) { "Cotice expresamente todas las líneas" }
            require(precios.values.all { it in 1..1_000_000_000L }) { "Precio unitario inválido" }
            require(lineas.sumOf { it.cantidad.toLong() * precios.getValue(it.productoId) } <= 1_000_000_000_000L) {
                "Cotización supera el límite permitido"
            }
            require(dao.guardarProveedorCotizado(id, sucursalId, proveedorCotizado.trim()) == 1) {
                "Solicitud no disponible para cotización"
            }
            lineas.forEach { l -> require(dao.guardarPrecioCotizado(id, l.productoId, precios.getValue(l.productoId)) == 1) {
                "No se pudo guardar la cotización completa"
            } }
            require(observacion.length <= 300) { "Observación demasiado larga" }
            require(dao.enviarAGerencia(id, sucursalId, admin.id, System.currentTimeMillis(), observacion.trim()) == 1) {
                "La solicitud cambió de estado"
            }
            dao.nuevoEvento(EventoSolicitudExterna(solicitudId = id, usuarioId = admin.id,
                accion = "ENVIADA_GERENCIA", detalle = "Proveedor cotizado: ${proveedorCotizado.trim()}; ${observacion.trim()}"))
            "Solicitud #$id enviada a gerencia"
        }
    }, respuesta)

    // la gerencia decide sobre todas las sucursales
    fun decidir(gerente: Usuario, id: Long, aprobar: Boolean, motivo: String,
               respuesta: (Boolean, String) -> Unit) = accion({
        db.withTransaction {
            require(dao.gerenteAutorizado(gerente.id) == 1) { "Solo gerencia puede aprobar solicitudes de abastecimiento" }
            val solicitud = dao.solicitudPorId(id) ?: error("Solicitud no encontrada")
            require(solicitud.estado == "EN_GERENCIA") { "Solicitud ya evaluada" }
            if (aprobar) {
                val lineas = dao.lineasActuales(id)
                require(solicitud.proveedorCotizado.isNotBlank() && lineas.isNotEmpty() &&
                    lineas.all { (it.precioUnitarioCotizado ?: 0) > 0 }) {
                    "Falta cotización de Administración; no es posible autorizar importes desconocidos"
                }
            }
            require(aprobar || motivo.trim().length >= 5) { "Indique motivo del rechazo" }
            require(motivo.length <= 300) { "Observación demasiado larga" }
            val estado = if (aprobar) "APROBADA" else "RECHAZADA"
            require(dao.decidir(id, gerente.id, estado, System.currentTimeMillis(), motivo.trim()) == 1) {
                "La solicitud fue evaluada por otra operación"
            }
            dao.nuevoEvento(EventoSolicitudExterna(solicitudId = id, usuarioId = gerente.id,
                accion = estado, detalle = motivo.trim()))
            "Solicitud #$id $estado"
        }
    }, respuesta)

    // crear recepcion esperada; aun no se modifica el stock
    fun programar(admin: Usuario, sid: Long, id: Long, operadorId: Long, proveedor: String,
                 respuesta: (Boolean, String) -> Unit) = accion({
        db.withTransaction {
            require(dao.adminAutorizadoExterno(admin.id, sid) == 1) { "Sin permiso en esta sucursal" }
            val sol = dao.solicitudPorId(id) ?: error("Solicitud inexistente")
            require(sol.sucursalId == sid && sol.estado == "APROBADA" && sol.entregaCajaId == null) {
                "Se requiere aprobación gerencial vigente y una entrega no programada"
            }
            val bodega = dao.bodegaDeSucursal(sol.bodegaId, sid) ?: error("Bodega inactiva o fuera de sucursal")
            require(dao.operadorActivoDeBodegaParaEntrega(operadorId, bodega.id) == 1) { "Operador no autorizado en esta bodega" }
            require(proveedor.trim().length in 3..100) { "Escriba el proveedor de la entrega" }
            val lineas = dao.lineasActuales(id)
            require(lineas.isNotEmpty() && lineas.all { it.cantidad in 1..100000 }) { "Productos inválidos" }
            val cajaId = cajasDao.siguienteIdCaja()
            val codigo = "EXT-${id.toString().padStart(5, '0')}"
            require(cajasDao.cajaPorCodigo(codigo) == null) { "Código de entrega duplicado" }
            cajasDao.guardarCaja(Caja(id = cajaId, codigo = codigo, tipo = "INGRESO",
                origen = proveedor.trim(), bodegaOrigenId = 0, bodegaDestinoId = bodega.id,
                solicitadoPor = "Solicitud externa #$id", asignadoA = operadorId, estado = "ESPERADA"))
            for (l in lineas) cajasDao.guardarDetalleCaja(DetalleCaja(cajaId = cajaId,
                productoId = l.productoId, cantidadEsperada = l.cantidad))
            require(dao.programarEntrega(id, sid, cajaId) == 1) { "Ya existe una entrega programada" }
            dao.nuevoEvento(EventoSolicitudExterna(solicitudId = id, usuarioId = admin.id,
                accion = "ENTREGA_PROGRAMADA", detalle = "Caja $codigo · $proveedor · Operador $operadorId"))
            administracionDao.guardarEventoAdministrativo(EventoAdministrativo(sucursalId = sid,
                usuarioId = admin.id, accion = "ABASTECIMIENTO", detalle = "Solicitud #$id, caja $codigo"))
            "Entrega $codigo programada; stock pendiente de recepción física"
        }
    }, respuesta)

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = AbastecimientoExternoViewModel(app) as T
    }
}
