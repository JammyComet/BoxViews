package com.example.boxview.viewmodel

// Lógica: Recepción, movimientos e inventarios de tienda.
import com.example.boxview.mensajeErrorUsuario
import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.example.boxview.modelo.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// operaciones de tienda
class TrabajadorTiendaViewModel(app: Application) : AndroidViewModel(app) {
    // Conexion con la capa de datos.
    private val db = AppDatabase.getDatabase(app)
    private val dao = db.inventarioDao()
    private val operaciones = db.operacionesTiendaDao()
    private val preciosVenta = db.preciosVentaDao()

    private val _formularioRecepcion = MutableStateFlow(FormularioRecepcionModel())
    val formularioRecepcion: StateFlow<FormularioRecepcionModel> = _formularioRecepcion.asStateFlow()

    private val _formularioTraslado = MutableStateFlow(FormularioTrasladoModel())
    val formularioTraslado: StateFlow<FormularioTrasladoModel> = _formularioTraslado.asStateFlow()

    fun elegirProductoTraslado(id: Long?) {
        _formularioTraslado.value = FormularioTrasladoModel(productoId = id)
    }
    fun elegirOrigenTraslado(id: Long?) {
        _formularioTraslado.update { it.copy(origenId = id) }
    }
    fun elegirDestinoTraslado(id: Long?) {
        _formularioTraslado.update { it.copy(destinoId = id) }
    }
    fun cambiarCantidadTraslado(valor: String) {
        if (valor.isEmpty() || (valor.all(Char::isDigit) && valor.length <= 6)) {
            _formularioTraslado.update { it.copy(cantidad = valor) }
        }
    }
    fun limpiarFormularioTraslado() { _formularioTraslado.value = FormularioTrasladoModel() }
    private fun limpiarFormularioRecepcion() {
        _formularioRecepcion.value = FormularioRecepcionModel()
        siguienteUbicacionId = 1L
    }

    fun prepararRecepcion(cajaId: Long?) {
        if (_formularioRecepcion.value.cajaId != cajaId) {
            _formularioRecepcion.value = FormularioRecepcionModel(cajaId = cajaId)
        }
    }
    fun cambiarCantidadRecepcion(detalleId: Long, texto: String, esperado: Int) {
        if (texto.isNotEmpty() && (!texto.all(Char::isDigit) || texto.length > 6)) return
        _formularioRecepcion.update { actual ->
            val anterior = minOf(actual.cantidades[detalleId]?.toIntOrNull() ?: 0, esperado)
            val aceptado = minOf(texto.toIntOrNull() ?: 0, esperado)
            val filas = actual.ubicaciones.filter { it.detalleId==detalleId }
            actual.copy(cantidades = actual.cantidades + (detalleId to texto),
                ubicaciones = if (filas.size==1 && (filas.first().cantidad.isBlank() ||
                    filas.first().cantidad==anterior.toString())) actual.ubicaciones.map { fila ->
                    if (fila.clave==filas.first().clave)
                        fila.copy(cantidad=if(texto.isBlank()) "" else aceptado.toString()) else fila
                } else actual.ubicaciones, comprobado=false)
        }
    }
    fun sumarUnidadRecepcion(detalleId: Long, esperado: Int) {
        _formularioRecepcion.update { estado ->
            val cantidad = estado.cantidades[detalleId]?.toIntOrNull() ?: 0
            if (cantidad >= 100000) estado else {
                val filas = estado.ubicaciones.filter { it.detalleId==detalleId }
                estado.copy(cantidades = estado.cantidades + (detalleId to (cantidad + 1).toString()),
                    ubicaciones = if (filas.size==1 && (filas.first().cantidad.isBlank() ||
                        filas.first().cantidad==minOf(cantidad,esperado).toString())) estado.ubicaciones.map { fila ->
                            if (fila.clave==filas.first().clave) fila.copy(cantidad=minOf(cantidad+1,esperado).toString())
                            else fila
                        } else estado.ubicaciones, comprobado=false)
            }
        }
    }
    private var siguienteUbicacionId = 1L

    fun agregarUbicacionRecepcion(detalleId: Long, posicionInicial: Long? = null) {
        val nueva = UbicacionRecepcionForm(siguienteUbicacionId++, detalleId, posicionInicial)
        _formularioRecepcion.update { it.copy(ubicaciones = it.ubicaciones + nueva, comprobado = false) }
    }
    fun cambiarPosicionRecepcion(clave: Long, posicionId: Long, esperado: Int) {
        _formularioRecepcion.update { actual ->
            val linea = actual.ubicaciones.firstOrNull { it.clave == clave } ?: return@update actual
            val contadas = actual.cantidades[linea.detalleId]?.toIntOrNull() ?: 0
            val aceptadas = minOf(contadas, esperado).coerceAtLeast(0)
            val asignadasEnOtras = actual.ubicaciones.filter { it.detalleId == linea.detalleId && it.clave != clave }
                .sumOf { it.cantidad.toIntOrNull() ?: 0 }
            val disponibles = (aceptadas - asignadasEnOtras).coerceAtLeast(0)
            actual.copy(ubicaciones = actual.ubicaciones.map {
                if (it.clave == clave) it.copy(posicionId = posicionId,
                    cantidad = if (it.cantidad.isBlank() && disponibles>0) disponibles.toString() else it.cantidad)
                else it
            }, comprobado = false)
        }
    }
    // recepcion de productos
    fun ubicarTodoEnRecepcion(detalleId: Long, posicionRecepcionId: Long, cantidad: Int) {
        _formularioRecepcion.update { anterior ->
            val resto = anterior.ubicaciones.filterNot { it.detalleId == detalleId }
            val linea = if (cantidad > 0) listOf(UbicacionRecepcionForm(
                siguienteUbicacionId++, detalleId, posicionRecepcionId, cantidad.toString())) else emptyList()
            anterior.copy(ubicaciones = resto + linea, comprobado = false)
        }
    }
    fun cambiarUnidadesUbicacion(clave: Long, valor: String) {
        if (valor.isNotEmpty() && (!valor.all(Char::isDigit) || valor.length > 6)) return
        _formularioRecepcion.update { actual -> actual.copy(
            ubicaciones = actual.ubicaciones.map { if (it.clave == clave) it.copy(cantidad = valor) else it },
            comprobado = false) }
    }
    fun quitarUbicacionRecepcion(clave: Long) {
        _formularioRecepcion.update { it.copy(ubicaciones = it.ubicaciones.filterNot { linea -> linea.clave == clave }, comprobado = false) }
    }
    fun cambiarObservacionRecepcion(valor: String) {
        _formularioRecepcion.update { it.copy(observacion = valor.take(300)) }
    }
    fun marcarRecepcionComprobada(valor: Boolean) {
        _formularioRecepcion.update { it.copy(comprobado = valor) }
    }


    var tiendaActiva by mutableStateOf<Sucursal?>(null)
        private set
    var cajaActivaId by mutableStateOf<Long?>(null)
        private set

    // Datos consultados para las pantallas.
    fun tiendasAsignadas(usuarioId: Long): Flow<List<Sucursal>> = dao.tiendasAutorizadas(usuarioId)
    // Cajas pendientes para la tienda.
    fun pendientes(): Flow<List<Caja>> = dao.cajasPendientesTienda(tiendaActiva?.id ?: -1)
    fun recibidas(): Flow<List<Caja>> = dao.cajasRecibidasTienda(tiendaActiva?.id ?: -1)
    // Stock observable de la tienda.
    fun existencias(): Flow<List<ExistenciaTienda>> = dao.stockTienda(tiendaActiva?.id ?: -1)
    fun diferenciasStock(): Flow<List<DiferenciaStockTienda>> = dao.diferenciasStockTienda(tiendaActiva?.id ?: -1)
    fun posiciones(): Flow<List<PosicionTienda>> = dao.posicionesTienda(tiendaActiva?.id ?: -1)
    fun todasLasPosiciones(): Flow<List<PosicionTienda>> = dao.posicionesTiendaTodas(tiendaActiva?.id ?: -1)
    fun stockPorUbicacion(): Flow<List<ExistenciaUbicacionTienda>> = dao.stockUbicacionesTienda(tiendaActiva?.id ?: -1)
    fun movimientosUbicaciones(): Flow<List<MovimientoUbicacionTienda>> = dao.movimientosUbicacionTienda(tiendaActiva?.id ?: -1)

    fun movimientos(): Flow<List<MovimientoTienda>> = dao.movimientosDeTienda(tiendaActiva?.id ?: -1)
    fun incidencias(): Flow<List<IncidenciaCaja>> = dao.incidenciasDeTienda(tiendaActiva?.id ?: -1)
    fun eventosRecepcion(): Flow<List<EventoCaja>> = dao.eventosRecepcionTienda(tiendaActiva?.id ?: -1)
    fun observarCaja() = dao.observarCaja(cajaActivaId ?: -1)
    fun detallesCaja() = dao.observarDetallesCaja(cajaActivaId ?: -1)

    fun seleccionarTienda(usuario: Usuario?, tienda: Sucursal, respuesta: (Boolean, String) -> Unit) {
        if (usuario == null || usuario.rol !in listOf("TRABAJADOR_TIENDA", "JEFE_TIENDA")) {
            respuesta(false, "Acceso exclusivo de personal de tienda")
            return
        }
        viewModelScope.launch {
            try {
                val acceso = if (usuario.rol == "JEFE_TIENDA")
                    dao.jefeAutorizadoTienda(usuario.id, tienda.id)
                    else dao.trabajadorAutorizadoTienda(usuario.id, tienda.id)
                require(acceso == 1) { "Acceso no autorizado a esta sucursal" }
                // cargar posiciones de tienda
                // Operacion agrupada en Room.
                db.withTransaction { asegurarUbicacionesTienda(dao, tienda.id) }
                tiendaActiva = tienda
                cajaActivaId = null
                limpiarFormularioRecepcion()
                limpiarFormularioTraslado()
                respuesta(true, "")
            } catch (e: Exception) { respuesta(false, mensajeErrorUsuario(e, "No se pudo seleccionar la tienda")) }
        }
    }

    fun abrirCaja(usuario: Usuario?, codigo: String, respuesta: (Boolean, String) -> Unit) {
        val sid = tiendaActiva?.id ?: return respuesta(false, "Selecciona una tienda")
        if (usuario == null) return respuesta(false, "Inicia sesión")
        viewModelScope.launch {
            try {
                require(dao.trabajadorAutorizadoTienda(usuario.id, sid) == 1) { "Tienda no autorizada" }
                val caja = dao.cajaPorCodigo(codigo.trim()) ?: error("No existe una caja con ese código")
                require(caja.tipo == "SALIDA" && caja.sucursalDestinoId == sid) {
                    "La caja no está dirigida a esta tienda"
                }
                require(caja.estado in listOf("EN_TRANSITO", "EN_REVISION")) {
                    "Esta caja ya fue procesada o no está pendiente de recepción"
                }
                require(dao.detallesCaja(caja.id).isNotEmpty()) { "La caja no contiene productos" }
                // La lectura QR (o código manual equivalente) confirma la llegada física,
                // pero no aumenta el stock: éste se registra tras la comprobación.
                if (caja.estado == "EN_TRANSITO") {
                    db.withTransaction {
                        require(dao.registrarLlegadaTienda(caja.id, sid) == 1) {
                            "La caja cambió de estado. Actualiza la lista e inténtalo nuevamente"
                        }
                        dao.guardarEventoCaja(EventoCaja(cajaId = caja.id, usuarioId = usuario.id,
                            accion = "LLEGADA_TIENDA", observacion = "Llegada registrada en ${tiendaActiva?.nombre.orEmpty()}"))
                    }
                }
                cajaActivaId = caja.id
                respuesta(true, "")
            } catch (e: Exception) { respuesta(false, mensajeErrorUsuario(e, "No se pudo abrir la caja")) }
        }
    }

    // reglas de recepcion
    fun confirmarRecepcion(usuario: Usuario?, conteos: Map<Long, Int>, distribuciones: List<DistribucionRecepcion>, observacion: String, respuesta: (Boolean, String) -> Unit) {
        val sid = tiendaActiva?.id ?: return respuesta(false, "Selecciona una tienda")
        val id = cajaActivaId ?: return respuesta(false, "Abre una caja")
        if (usuario == null || usuario.rol != "TRABAJADOR_TIENDA") {
            respuesta(false, "Permiso de recepción no disponible")
            return
        }
        viewModelScope.launch {
            try {
                val conDiferencia = db.withTransaction {
                    require(dao.trabajadorAutorizadoTienda(usuario.id, sid) == 1) { "Tienda no autorizada" }
                    val campañaEnCurso = db.campaniasMensualesDao().campañaActiva("TIENDA", sid) > 0
                    val caja = dao.cajaPorId(id) ?: error("La caja ya no existe")
                    require(caja.tipo == "SALIDA" && caja.sucursalDestinoId == sid && caja.estado == "EN_REVISION") {
                        "Primero registra la llegada de la caja mediante su QR; o ya fue recibida"
                    }
                    val detalles = dao.detallesCaja(id)
                    require(detalles.isNotEmpty()) { "No se puede recibir una caja vacía" }
                    require(conteos.keys == detalles.map { it.id }.toSet()) {
                        "Debes comprobar la cantidad de cada producto, incluso si es cero"
                    }
                    detalles.forEach { detalle ->
                        val cantidad = conteos.getValue(detalle.id)
                        require(cantidad in 0..100000) { "Cantidad fuera de rango" }
                        require(detalle.cantidadEsperada >= 0) { "Cantidad solicitada inválida" }
                    }
                    // validacion de ubicaciones
                    val recepcion = dao.posicionesTiendaActuales(sid).firstOrNull {
                        it.activa && it.tipo == "RECEPCION"
                    } ?: error("La tienda no tiene zona de recepción")
                    // En campaña, ignorar destinos de formulario y almacenar TODO en RECEPCIÓN.
                    val distribucionReal = if (campañaEnCurso) detalles.mapNotNull { d ->
                        val aceptadas = minOf(conteos.getValue(d.id), d.cantidadEsperada)
                        if (aceptadas > 0) DistribucionRecepcion(d.id, recepcion.id, aceptadas) else null
                    } else distribuciones
                    val ubicaciones = distribucionReal.groupBy { it.detalleId }
                    require(ubicaciones.keys.all { clave -> detalles.any { it.id == clave } }) {
                        "La distribución contiene productos ajenos a la caja"
                    }
                    val posicionesValidas = dao.posicionesTiendaActuales(sid).filter { it.activa && it.tipo != "SECCION" }.associateBy { it.id }
                    detalles.forEach { detalle ->
                        val aceptadas = minOf(conteos.getValue(detalle.id), detalle.cantidadEsperada)
                        val lineas = ubicaciones[detalle.id].orEmpty()
                        require(lineas.all { it.cantidad > 0 }) { "Las cantidades por estante deben ser positivas" }
                        require(lineas.distinctBy { it.posicionId }.size == lineas.size) {
                            "No repitas la misma ubicación: suma sus unidades en una sola línea"
                        }
                        val producto = dao.productoPorId(detalle.productoId) ?: error("Producto inexistente")
                        lineas.forEach { linea ->
                            val posicion = posicionesValidas[linea.posicionId]
                                ?: error("Ubicación no autorizada para esta tienda")
                            require(posicion.categoria == "General" || posicion.categoria == producto.categoria) {
                                "La ubicación ${posicion.codigo} no corresponde a ${producto.categoria}"
                            }
                        }
                        require(lineas.sumOf { it.cantidad.toLong() } == aceptadas.toLong()) {
                            "Debes distribuir las $aceptadas unidades aceptadas de ${producto.nombre} entre las ubicaciones"
                        }
                    }
                    val diferencia = detalles.any { conteos.getValue(it.id) != it.cantidadEsperada }
                    require(dao.marcarRecepcionTienda(id, sid, if (diferencia) "CON_DIFERENCIA" else "RECIBIDA") == 1) {
                        "La caja ya fue procesada"
                    }
                    detalles.forEach { detalle ->
                        val contadas = conteos.getValue(detalle.id)
                        val aceptadas = minOf(contadas, detalle.cantidadEsperada)
                        dao.actualizarDetalleCaja(detalle.copy(cantidadContadaFisica = contadas, cantidadRecibida = aceptadas))
                        if (contadas != detalle.cantidadEsperada) {
                            dao.guardarIncidenciaCaja(IncidenciaCaja(cajaId = id, productoId = detalle.productoId,
                                esperado = detalle.cantidadEsperada, contado = contadas))
                        }
                        if (aceptadas > 0) {
                            val actual = dao.existenciaTienda(sid, detalle.productoId)
                            if (actual == null) dao.guardarExistenciaTienda(
                                ExistenciaTienda(sucursalId = sid, productoId = detalle.productoId, cantidad = aceptadas))
                            else require(dao.sumarStockTienda(actual.id, aceptadas) == 1) { "No se pudo actualizar stock de tienda" }
                            dao.guardarMovimientoTienda(MovimientoTienda(usuarioId = usuario.id,
                                sucursalId = sid, cajaId = id, productoId = detalle.productoId, cantidad = aceptadas))
                            ubicaciones[detalle.id].orEmpty().forEach { linea ->
                                val previo = dao.stockUbicacionTienda(sid, detalle.productoId, linea.posicionId)
                                if (previo == null) dao.guardarStockUbicacionTienda(
                                    ExistenciaUbicacionTienda(sucursalId = sid, productoId = detalle.productoId,
                                        posicionId = linea.posicionId, cantidad = linea.cantidad))
                                else require(dao.sumarStockUbicacionTienda(previo.id, linea.cantidad) == 1) {
                                    "No se pudo actualizar la ubicación de tienda"
                                }
                                dao.guardarMovimientoUbicacionTienda(MovimientoUbicacionTienda(
                                    usuarioId = usuario.id, sucursalId = sid, productoId = detalle.productoId,
                                    posicionDestinoId = linea.posicionId, cantidad = linea.cantidad,
                                    tipo = if (campañaEnCurso) "RECEPCION_CAMPANIA" else "RECEPCION_UBICACION", cajaId = id))
                            }
                        }
                    }
                    dao.guardarEventoCaja(EventoCaja(cajaId = id, usuarioId = usuario.id,
                        accion = if (diferencia) "RECEPCION_TIENDA_CON_DIFERENCIA" else "RECEPCION_TIENDA_CONFIRMADA",
                        observacion = "${tiendaActiva?.nombre.orEmpty()} · ${observacion.trim().take(300)}"))
                    diferencia
                }
                cajaActivaId = null
                limpiarFormularioRecepcion()
                respuesta(true, if (conDiferencia) "Recepción con diferencias; los excedentes no se ingresan y quedan registrados para revisión" else "Caja recibida correctamente")
            } catch (e: Exception) { respuesta(false, mensajeErrorUsuario(e, "Error al recibir la caja")) }
        }
    }

    // traslado de productos
    fun trasladarEnTienda(usuario: Usuario?, productoId: Long, origenId: Long, destinoId: Long,
                          cantidad: Int, respuesta: (Boolean, String) -> Unit) {
        val sid = tiendaActiva?.id ?: return respuesta(false, "Selecciona una tienda")
        if (usuario?.rol != "TRABAJADOR_TIENDA") return respuesta(false, "Acceso no autorizado")
        viewModelScope.launch {
            try {
                db.withTransaction {
                    require(dao.trabajadorAutorizadoTienda(usuario.id, sid) == 1) { "Tienda no autorizada" }
                    require(db.campaniasMensualesDao().campañaActiva("TIENDA", sid) == 0 &&
                        operaciones.inventarioAbierto(sid) == 0) { "Los estantes están congelados durante todo el inventario mensual" }
                    require(cantidad > 0 && cantidad <= 100000) { "Indica una cantidad válida" }
                    require(origenId != destinoId) { "Elige ubicaciones diferentes" }
                    val origen = dao.posicionTiendaPorId(origenId) ?: error("Origen no existe")
                    val destino = dao.posicionTiendaPorId(destinoId) ?: error("Destino no existe")
                    // permitir retirar de posiciones inactivas
                    require(origen.sucursalId == sid && destino.sucursalId == sid && destino.activa && destino.tipo != "SECCION") {
                        "Las ubicaciones deben pertenecer a la tienda y el destino debe estar activo"
                    }
                    val producto = dao.productoPorId(productoId) ?: error("Producto no existe")
                    require(destino.categoria == "General" || destino.categoria == producto.categoria) {
                        "El estante de destino no corresponde a la categoría del producto"
                    }
                    val actual = dao.stockUbicacionTienda(sid, productoId, origenId)
                        ?: error("No hay unidades de ese producto en el origen")
                    require(dao.restarStockUbicacionTienda(actual.id, cantidad) == 1) {
                        "No hay suficientes unidades en la ubicación de origen"
                    }
                    val saldoDestino = dao.stockUbicacionTienda(sid, productoId, destinoId)
                    if (saldoDestino == null) dao.guardarStockUbicacionTienda(
                        ExistenciaUbicacionTienda(sucursalId = sid, productoId = productoId,
                            posicionId = destinoId, cantidad = cantidad))
                    else require(dao.sumarStockUbicacionTienda(saldoDestino.id, cantidad) == 1) {
                        "No se pudo actualizar el destino"
                    }
                    dao.guardarMovimientoUbicacionTienda(MovimientoUbicacionTienda(
                        usuarioId = usuario.id, sucursalId = sid, productoId = productoId,
                        posicionOrigenId = origenId, posicionDestinoId = destinoId,
                        cantidad = cantidad, tipo = "TRASLADO_TIENDA"))
                }
                respuesta(true, "Productos trasladados correctamente")
            } catch (e: Exception) { respuesta(false, mensajeErrorUsuario(e, "No se pudo trasladar")) }
        }
    }

    fun salidas(): Flow<List<SalidaTienda>> = operaciones.salidas(tiendaActiva?.id ?: -1)
    fun preciosVentaTienda(): Flow<List<PrecioVentaSucursal>> = preciosVenta.preciosSucursal(tiendaActiva?.id ?: -1)
    fun inventariosTienda(): Flow<List<InventarioTienda>> = operaciones.inventarios(tiendaActiva?.id ?: -1)
    fun campaniasMensualesTienda() = db.campaniasMensualesDao().listarPorUbicacion("TIENDA", tiendaActiva?.id ?: -1)
    fun zonasCampania(id: Long) = db.campaniasMensualesDao().observarZonas(id)
    fun inventarioTiendaAbierto(): Flow<InventarioTienda?> = operaciones.inventarioAbiertoFlujo(tiendaActiva?.id ?: -1)
    fun lineasConteoTienda(id: Long): Flow<List<LineaInventarioTienda>> = operaciones.lineasFlujo(id)
    fun zonasConteoTienda(id: Long): Flow<List<ZonaConteoTienda>> = operaciones.zonasInventarioTienda(id)
    fun trabajadoresParaInventario(): Flow<List<Usuario>> = dao.trabajadoresDeTienda(tiendaActiva?.id ?: -1)

    fun asignarZonaTienda(jefe: Usuario?, invId: Long, posId: Long, uid: Long,
                          respuesta: (Boolean,String)->Unit) {
        val sid = tiendaActiva?.id ?: return respuesta(false,"Selecciona una tienda")
        if (jefe?.rol != "JEFE_TIENDA") return respuesta(false,"Acceso de jefatura requerido")
        viewModelScope.launch {
            try {
                db.withTransaction {
                    require(dao.jefeAutorizadoTienda(jefe.id,sid)==1) { "Sin permiso de jefatura" }
                    val inv=operaciones.buscarInventario(invId) ?: error("Inventario inexistente")
                    require(inv.sucursalId==sid && inv.estado=="ABIERTO") { "Inventario no abierto" }
                    require(dao.trabajadorAutorizadoTienda(uid,sid)==1) { "Trabajador no autorizado" }
                    require(operaciones.asignarZonaTienda(invId,posId,uid)==1) { "Esta zona no está pendiente de asignación" }
                }
                respuesta(true,"Zona asignada")
            } catch(e:Exception) { respuesta(false,mensajeErrorUsuario(e, "No se pudo asignar la zona")) }
        }
    }

    fun confirmarZonaTienda(usuario: Usuario?, invId: Long, posId: Long,
                            respuesta: (Boolean,String)->Unit) {
        val sid = tiendaActiva?.id ?: return respuesta(false,"Selecciona una tienda")
        if (usuario?.rol != "TRABAJADOR_TIENDA") return respuesta(false,"Acceso de trabajador requerido")
        viewModelScope.launch {
            try {
                db.withTransaction {
                    require(dao.trabajadorAutorizadoTienda(usuario.id,sid)==1) { "Sin permiso de tienda" }
                    val inv=operaciones.buscarInventario(invId) ?: error("Inventario inexistente")
                    require(inv.sucursalId==sid && inv.estado=="ABIERTO") { "Inventario no abierto" }
                    require(operaciones.confirmarZonaTienda(invId,posId,usuario.id,System.currentTimeMillis())==1) {
                        "Zona ajena, ya cerrada o con productos sin contar"
                    }
                }
                respuesta(true,"Zona revisada. Para corregirla, jefatura deberá solicitar un recuento")
            } catch(e:Exception) { respuesta(false,mensajeErrorUsuario(e, "No se pudo cerrar la zona")) }
        }
    }

    fun estantesVaciosTienda(inventarioId: Long): Flow<List<VerificacionEstanteVacio>> =
        db.inventariosDao().estantesVacios("TIENDA", inventarioId)

    fun confirmarEstanteVacioTienda(usuario: Usuario?, invId: Long, posId: Long,
                                    respuesta: (Boolean, String) -> Unit) {
        val sid = tiendaActiva?.id ?: return respuesta(false, "Selecciona una tienda")
        if (usuario?.rol != "TRABAJADOR_TIENDA") return respuesta(false, "Acceso de trabajador requerido")
        viewModelScope.launch {
            try {
                db.withTransaction {
                    require(dao.trabajadorAutorizadoTienda(usuario.id, sid) == 1) { "Sin permiso de tienda" }
                    val inv = operaciones.buscarInventario(invId) ?: error("Inventario inexistente")
                    require(inv.sucursalId == sid && inv.estado == "ABIERTO") { "Inventario no disponible" }
                    require(operaciones.zonasTiendaActuales(invId).any { it.posicionId == posId &&
                        it.asignadoA == usuario.id && it.confirmadoPor == null }) {
                        "El estante no está asignado o ya fue entregado"
                    }
                    val lineas = operaciones.lineasInventario(invId).filter { it.posicionId == posId }
                    val fecha = System.currentTimeMillis()
                    for (linea in lineas) {
                        require(operaciones.anotarConteo(linea.id, invId, 0, usuario.id, fecha) == 1) {
                            "No se pudo registrar el conteo cero"
                        }
                        operaciones.registrarEventoConteo(EventoConteoTienda(
                            inventarioId = invId, lineaId = linea.id, productoId = linea.productoId,
                            posicionId = posId, usuarioId = usuario.id, cantidadAnterior = linea.cantidadContada,
                            cantidadNueva = 0, tipo = "ESTANTE_VACIO", fecha = fecha))
                    }
                    require(operaciones.confirmarZonaTienda(invId, posId, usuario.id, fecha) == 1) {
                        "No se pudo entregar el estante vacío"
                    }
                    db.inventariosDao().guardarEstanteVacio(VerificacionEstanteVacio(
                        "TIENDA", invId, posId, usuario.id, fecha))
                    dao.guardarEventoAdministrativo(EventoAdministrativo(sucursalId = sid, usuarioId = usuario.id,
                        accion = "ESTANTE_VACIO_TIENDA",
                        detalle = "Inventario $invId; estante $posId; revisión física sin productos", fecha = fecha))
                }
                respuesta(true, "Estante vacío entregado. Las diferencias quedan pendientes de jefatura")
            } catch (e: Exception) { respuesta(false, mensajeErrorUsuario(e, "No se pudo confirmar estante vacío")) }
        }
    }

    fun reabrirZonaTienda(jefe: Usuario?, invId: Long, posId: Long,
                           motivo: String, respuesta: (Boolean,String)->Unit) {
        val sid = tiendaActiva?.id ?: return respuesta(false,"Selecciona una tienda")
        if (jefe?.rol != "JEFE_TIENDA") return respuesta(false,"Acceso de jefatura requerido")
        viewModelScope.launch {
            try {
                db.withTransaction {
                    require(dao.jefeAutorizadoTienda(jefe.id,sid)==1) { "Sin permiso de jefatura" }
                    val inv=operaciones.buscarInventario(invId) ?: error("Inventario inexistente")
                    require(inv.sucursalId==sid && inv.estado=="ABIERTO") { "Inventario ya cerrado" }
                    require(motivo.trim().length>=5) { "Indica el motivo del recuento" }
                    require(operaciones.reabrirZonaTienda(invId,posId)==1) { "La zona no estaba cerrada" }
                    operaciones.reiniciarConteosZonaTienda(invId,posId)
                    db.inventariosDao().quitarEstanteVacio("TIENDA", invId, posId)
                    dao.guardarEventoAdministrativo(EventoAdministrativo(sucursalId=sid,usuarioId=jefe.id,
                        accion="RECONTEO_ZONA_TIENDA",detalle="Inventario $invId, ubicación $posId: ${motivo.trim().take(200)}"))
                }
                respuesta(true,"Zona reabierta para recuento")
            } catch(e:Exception) { respuesta(false,mensajeErrorUsuario(e, "No se pudo reabrir")) }
        }
    }

    // registrar salida por venta, merma o retiro
    fun registrarSalidaTienda(usuario: Usuario?, productoId: Long, posicionId: Long, cantidad: Int,
                             tipo: String, motivo: String, respuesta: (Boolean, String) -> Unit) {
        val sid = tiendaActiva?.id ?: return respuesta(false, "Selecciona una tienda")
        if (usuario?.rol != "TRABAJADOR_TIENDA") return respuesta(false, "Trabajador no autorizado")
        viewModelScope.launch {
            try {
                db.withTransaction {
                    require(dao.trabajadorAutorizadoTienda(usuario.id, sid) == 1) { "Sin permiso en tienda" }
                    val ubicacionSalida = dao.posicionTiendaPorId(posicionId) ?: error("Ubicación no encontrada")
                    require((db.campaniasMensualesDao().campañaActiva("TIENDA", sid) == 0 &&
                        operaciones.inventarioAbierto(sid) == 0) || ubicacionSalida.tipo == "RECEPCION") {
                        "No se puede descontar stock de estantes durante el inventario mensual"
                    }
                    require(tipo in listOf("VENTA", "MERMA", "RETIRO")) { "Tipo de salida inválido" }
                    require(motivo.trim().length in 5..250) { "Indica el motivo o comprobante de la salida" }
                    require(cantidad in 1..100000) { "Cantidad inválida" }
                    require(dao.productoPorId(productoId) != null) { "Producto inexistente" }
                    val posicion = dao.posicionTiendaPorId(posicionId) ?: error("Ubicación inexistente")
                    require(posicion.sucursalId == sid && posicion.tipo != "SECCION") { "Ubicación no válida" }
                    // La venta solo puede confirmarse con un precio vigente y capturado en este instante.
                    val precio = if (tipo == "VENTA") {
                        preciosVenta.precioActual(sid, productoId)?.precioClp
                            ?: error("Administración debe configurar el precio de venta antes de vender")
                    } else null
                    require(precio == null || precio in 1L..100_000_000L) { "Precio de venta inválido" }
                    val total = precio?.times(cantidad.toLong()) // máximo 10^13 CLP; Long seguro.
                    val saldo = dao.stockUbicacionTienda(sid, productoId, posicionId) ?: error("Sin existencias en esta posición")
                    require(dao.restarStockUbicacionTienda(saldo.id, cantidad) == 1) { "Stock de estante insuficiente" }
                    require(operaciones.descontarTotal(sid, productoId, cantidad) == 1) { "Stock general insuficiente" }
                    operaciones.registrarSalida(SalidaTienda(sucursalId = sid, usuarioId = usuario.id,
                        productoId = productoId, posicionId = posicionId, cantidad = cantidad,
                        tipo = tipo, motivo = motivo.trim(),
                        precioUnitarioVentaClp = precio, totalVentaClp = total))
                    dao.guardarMovimientoUbicacionTienda(MovimientoUbicacionTienda(usuarioId = usuario.id,
                        sucursalId = sid, productoId = productoId, posicionOrigenId = posicionId,
                        posicionDestinoId = 0, cantidad = cantidad, tipo = "SALIDA_$tipo"))
                }
                respuesta(true, "Salida registrada correctamente")
            } catch (e: Exception) { respuesta(false, mensajeErrorUsuario(e, "No se pudo registrar la salida")) }
        }
    }

    fun eventosConteoTienda(inventarioId: Long): Flow<List<EventoConteoTienda>> =
        operaciones.eventosConteo(inventarioId)

    // registra la verificación física de la tienda sin unidades en el sistema
    fun confirmarRevisionTiendaVacia(usuario: Usuario?, inventarioId: Long,
                                   respuesta: (Boolean, String) -> Unit) {
        val sid = tiendaActiva?.id ?: return respuesta(false, "Selecciona una tienda")
        if (usuario?.rol != "TRABAJADOR_TIENDA") return respuesta(false, "Trabajador no autorizado")
        viewModelScope.launch {
            try {
                db.withTransaction {
                    require(dao.trabajadorAutorizadoTienda(usuario.id, sid) == 1) { "Sin permiso de tienda" }
                    require(operaciones.confirmarTiendaVacia(inventarioId, sid, usuario.id,
                        System.currentTimeMillis()) == 1) { "La revisión no corresponde a una tienda vacía abierta" }
                }
                respuesta(true, "Revisión física de tienda vacía confirmada")
            } catch (e: Exception) { respuesta(false, mensajeErrorUsuario(e, "No se pudo confirmar la revisión")) }
        }
    }

    // el jefe abre el periodo de conteo de tienda
    fun abrirInventarioTienda(jefe: Usuario?, nombre: String, asignaciones: Map<Long,Long>,
                              respuesta: (Boolean, String) -> Unit) {
        val sid = tiendaActiva?.id ?: return respuesta(false, "Selecciona una tienda")
        if (jefe?.rol != "JEFE_TIENDA") return respuesta(false, "Jefatura no autorizada")
        viewModelScope.launch {
            try {
                db.withTransaction {
                    require(dao.jefeAutorizadoTienda(jefe.id, sid) == 1) { "Sin permiso de jefatura" }
                    require(nombre.trim().length in 3..80 && mesCampaniaInventario(nombre).isNotBlank()) { "Escribe mes y año al comienzo, por ejemplo Noviembre 2026 - Zona A" }
                    require(operaciones.inventarioAbierto(sid) == 0) { "Ya existe un inventario abierto" }
                    val ubicaciones = dao.stockUbicacionesTiendaActual(sid).filter { it.cantidad > 0 }
                    val conSaldo = ubicaciones.map { it.posicionId }.toSet()
                    val mesAnterior = db.campaniasMensualesDao().buscar("TIENDA", sid,
                        mesCampaniaInventario(nombre))
                    val pendientesMes = mesAnterior?.let { db.campaniasMensualesDao().zonas(it.id) }
                        ?.filter { it.estado == "PENDIENTE" }?.map { it.posicionId }?.toSet().orEmpty()
                    val posibles = dao.posicionesTiendaActuales(sid).filter {
                        it.tipo != "SECCION" && it.tipo != "RECEPCION" && (it.activa || it.id in conSaldo || it.id in pendientesMes)
                    }
                    require(asignaciones.isNotEmpty() && asignaciones.keys.all { pos -> posibles.any { it.id==pos } }) {
                        "Selecciona al menos una ubicación activa de tienda"
                    }
                    asignaciones.values.filter { it>0L }.distinct().forEach { uid ->
                        require(dao.trabajadorAutorizadoTienda(uid,sid)==1) { "Trabajador no autorizado" }
                    }
                    val general = dao.stockTiendaActual(sid).associate { it.productoId to it.cantidad }
                    require(ubicaciones.groupBy { it.productoId }.all { (pid, lineas) ->
                        lineas.sumOf { it.cantidad } == (general[pid] ?: 0)
                    } && general.all { (pid, total) ->
                        ubicaciones.filter { it.productoId == pid }.sumOf { it.cantidad } == total
                    }) { "El stock general y las ubicaciones no coinciden" }
                    val posicionesTotales = dao.posicionesTiendaActuales(sid)
                    val campania = ConsolidacionMensual.preparar(db, "TIENDA", sid, sid, nombre.trim(),
                        posibles.associate { it.id to rutaTienda(it, posicionesTotales) })
                    val id = operaciones.abrirInventario(InventarioTienda(
                        sucursalId = sid, nombre = nombre.trim(), campaniaId = campania.id))
                    ConsolidacionMensual.reservar(db, campania, asignaciones.keys, id)
                    asignaciones.forEach { (pos,uid) ->
                        operaciones.agregarZonaTienda(ZonaConteoTienda(id,pos,uid))
                    }
                    for (linea in ubicaciones.filter { it.posicionId in asignaciones.keys })
                        operaciones.agregarLineaInventario(LineaInventarioTienda(
                            inventarioId = id, productoId = linea.productoId,
                            posicionId = linea.posicionId, stockInicio = linea.cantidad))
                }
                respuesta(true, "Inventario abierto")
            } catch (e: Exception) { respuesta(false, mensajeErrorUsuario(e, "No se pudo abrir el inventario")) }
        }
    }

    // registra unidades encontradas que el sistema no tenía ubicadas
    fun registrarHallazgoTienda(usuario: Usuario?, inventarioId: Long, productoId: Long,
                                posicionId: Long, cantidad: Int,
                                respuesta: (Boolean, String) -> Unit) {
        val sid = tiendaActiva?.id ?: return respuesta(false, "Selecciona una tienda")
        if (usuario?.rol != "TRABAJADOR_TIENDA") return respuesta(false, "Trabajador no autorizado")
        viewModelScope.launch {
            try {
                db.withTransaction {
                    require(dao.trabajadorAutorizadoTienda(usuario.id, sid) == 1) { "Sin permiso de tienda" }
                    val inv = operaciones.buscarInventario(inventarioId) ?: error("Inventario inexistente")
                    require(inv.sucursalId == sid && inv.estado == "ABIERTO") { "Inventario no disponible" }
                    require(cantidad in 1..100000) { "Cantidad física inválida" }
                    require(dao.productoPorId(productoId) != null) { "El producto no está en el catálogo" }
                    val posicion = dao.posicionTiendaPorId(posicionId) ?: error("Ubicación inexistente")
                    require(operaciones.zonasTiendaActuales(inventarioId).any { z ->
                        z.posicionId==posicionId && z.asignadoA==usuario.id && z.confirmadoPor==null
                    }) { "Solo puedes registrar hallazgos en ubicaciones asignadas que estén abiertas" }
                    require(posicion.sucursalId == sid && posicion.tipo != "SECCION") {
                        "Selecciona una ubicación válida de esta tienda"
                    }
                    require(operaciones.lineaExiste(inventarioId, productoId, posicionId) == 0) {
                        "Este producto y posición ya están incluidos en el conteo"
                    }
                    val stock = dao.stockUbicacionTienda(sid, productoId, posicionId)?.cantidad ?: 0
                    require(stock == 0) { "Este producto ya tenía stock en la ubicación" }
                    val fecha = System.currentTimeMillis()
                    val lineaId = operaciones.agregarLineaInventario(LineaInventarioTienda(
                        inventarioId = inventarioId, productoId = productoId, posicionId = posicionId,
                        stockInicio = 0, cantidadContada = cantidad,
                        contadoPor = usuario.id, fechaConteo = fecha))
                    operaciones.registrarEventoConteo(EventoConteoTienda(inventarioId = inventarioId,
                        lineaId = lineaId, productoId = productoId, posicionId = posicionId,
                        usuarioId = usuario.id, cantidadAnterior = null,
                        cantidadNueva = cantidad, tipo = "HALLAZGO", fecha = fecha))
                }
                respuesta(true, "Hallazgo registrado; pendiente de conciliación por jefatura")
            } catch (e: Exception) { respuesta(false, mensajeErrorUsuario(e, "No se pudo registrar el hallazgo")) }
        }
    }

    // conteo del trabajador; no se asume cero para las lineas pendientes
    fun guardarConteosTienda(usuario: Usuario?, inventarioId: Long, conteos: Map<Long, Int>,
                             respuesta: (Boolean, String) -> Unit) {
        val sid = tiendaActiva?.id ?: return respuesta(false, "Selecciona una tienda")
        if (usuario?.rol != "TRABAJADOR_TIENDA") return respuesta(false, "Trabajador no autorizado")
        viewModelScope.launch {
            try {
                db.withTransaction {
                    require(dao.trabajadorAutorizadoTienda(usuario.id, sid) == 1) { "Sin permiso de tienda" }
                    val inv = operaciones.buscarInventario(inventarioId) ?: error("Inventario inexistente")
                    require(inv.sucursalId == sid && inv.estado == "ABIERTO") { "Inventario cerrado o de otra sucursal" }
                    val zonas = operaciones.zonasTiendaActuales(inventarioId)
                        .filter { it.asignadoA==usuario.id && it.confirmadoPor==null }.map { it.posicionId }.toSet()
                    val lineas = operaciones.lineasInventario(inventarioId).associateBy { it.id }
                    require(conteos.isNotEmpty() && conteos.keys.all { it in lineas.keys && lineas[it]!!.posicionId in zonas }) {
                        "Solo puedes contar productos de tus zonas abiertas"
                    }
                    require(conteos.isNotEmpty() && conteos.all { (id, valor) -> id in lineas && valor in 0..100000 }) {
                        "Conteos inválidos"
                    }
                    for ((id, valor) in conteos) {
                        val anterior = lineas.getValue(id)
                        val fecha = System.currentTimeMillis()
                        require(operaciones.anotarConteo(id, inventarioId,
                            valor, usuario.id, fecha) == 1) { "No se pudo guardar el conteo" }
                        operaciones.registrarEventoConteo(EventoConteoTienda(inventarioId = inventarioId,
                            lineaId = id, productoId = anterior.productoId,
                            posicionId = anterior.posicionId, usuarioId = usuario.id,
                            cantidadAnterior = anterior.cantidadContada, cantidadNueva = valor,
                            tipo = if (anterior.cantidadContada == null) "CONTEO" else "RECONTEO", fecha = fecha))
                    }
                }
                respuesta(true, "Conteo guardado")
            } catch (e: Exception) { respuesta(false, mensajeErrorUsuario(e, "No se pudo guardar el conteo")) }
        }
    }

    fun cancelarInventarioTienda(jefe: Usuario?, inventarioId: Long, motivo: String,
                                 respuesta: (Boolean, String) -> Unit) {
        val sid = tiendaActiva?.id ?: return respuesta(false, "Selecciona una tienda")
        if (jefe?.rol != "JEFE_TIENDA") return respuesta(false, "Jefatura no autorizada")
        viewModelScope.launch {
            try {
                db.withTransaction {
                    require(dao.jefeAutorizadoTienda(jefe.id, sid) == 1) { "Sin permiso de jefatura" }
                    require(motivo.trim().length in 5..250) { "Justifica la cancelación del conteo" }
                    require(operaciones.cancelarInventario(inventarioId, sid, jefe.id,
                        System.currentTimeMillis(), motivo.trim()) == 1) { "El inventario ya terminó" }
                    val cancelado = operaciones.buscarInventario(inventarioId)
                        ?: error("Inventario no encontrado")
                    ConsolidacionMensual.liberar(db, cancelado.campaniaId, inventarioId)
                    db.inventariosDao().borrarEstantesVacios("TIENDA", inventarioId)
                }
                respuesta(true, "Inventario cancelado sin modificar existencias")
            } catch (e: Exception) { respuesta(false, mensajeErrorUsuario(e, "No se pudo cancelar el inventario")) }
        }
    }

    // el jefe concilia las diferencias del periodo y lo cierra
    fun cerrarInventarioTienda(jefe: Usuario?, inventarioId: Long, observacion: String,
                               respuesta: (Boolean, String) -> Unit) {
        val sid = tiendaActiva?.id ?: return respuesta(false, "Selecciona una tienda")
        if (jefe?.rol != "JEFE_TIENDA") return respuesta(false, "Jefatura no autorizada")
        viewModelScope.launch {
            try {
                db.withTransaction {
                    require(dao.jefeAutorizadoTienda(jefe.id, sid) == 1) { "Sin permiso de jefatura" }
                    val inv = operaciones.buscarInventario(inventarioId) ?: error("Inventario inexistente")
                    require(inv.sucursalId == sid && inv.estado == "ABIERTO") { "Inventario ya cerrado" }
                    val lineas = operaciones.lineasInventario(inventarioId)
                    require(lineas.isNotEmpty() || operaciones.zonasTiendaActuales(inventarioId).isNotEmpty()) {
                        "Selecciona y revisa las zonas físicas antes de cerrar"
                    }
                    require(operaciones.zonasTiendaPendientes(inventarioId) == 0) {
                        "Faltan estantes sin asignar o sin confirmar físicamente"
                    }
                    require(lineas.all { it.cantidadContada != null }) {
                        "Faltan productos por contar; no se consideran cero"
                    }
                    val distintas = lineas.filter { it.cantidadContada != it.stockInicio }
                    require(distintas.isEmpty() || observacion.trim().length >= 5) {
                        "Los ajustes necesitan una justificación"
                    }
                    for (linea in lineas) {
                        val actual = dao.stockUbicacionTienda(sid, linea.productoId, linea.posicionId)
                        require((actual?.cantidad ?: 0) == linea.stockInicio) {
                            "El stock cambió durante el conteo"
                        }
                    }
                    for (linea in distintas) {
                        val nuevo = linea.cantidadContada ?: error("Conteo faltante")
                        val delta = nuevo - linea.stockInicio
                        val actual = dao.stockUbicacionTienda(sid, linea.productoId, linea.posicionId)
                        if (actual == null) {
                            require(linea.stockInicio == 0 && nuevo > 0) { "Existencia de ubicación incorrecta" }
                            dao.guardarStockUbicacionTienda(ExistenciaUbicacionTienda(
                                sucursalId = sid, productoId = linea.productoId,
                                posicionId = linea.posicionId, cantidad = nuevo))
                        } else require(operaciones.establecerUbicacion(actual.id, nuevo) == 1) {
                            "Error al ajustar estante"
                        }
                        val total = dao.existenciaTienda(sid, linea.productoId)
                        if (total == null) {
                            require(delta > 0) { "El stock general no existe" }
                            dao.guardarExistenciaTienda(ExistenciaTienda(
                                sucursalId = sid, productoId = linea.productoId, cantidad = delta))
                        } else require(operaciones.ajustarTotal(sid, linea.productoId, delta) == 1) {
                            "Error al ajustar total"
                        }
                        dao.guardarMovimientoUbicacionTienda(MovimientoUbicacionTienda(
                            usuarioId = jefe.id, sucursalId = sid, productoId = linea.productoId,
                            posicionOrigenId = if (delta < 0) linea.posicionId else null,
                            posicionDestinoId = if (delta > 0) linea.posicionId else 0,
                            cantidad = kotlin.math.abs(delta), tipo = "AJUSTE_INVENTARIO_TIENDA"))
                    }
                    val posicionesCierre = dao.posicionesTiendaActuales(sid)
                    for (linea in lineas) {
                        val producto = dao.productoPorId(linea.productoId) ?: error("Producto eliminado del catálogo")
                        val posicion = posicionesCierre.firstOrNull { it.id == linea.posicionId }
                            ?: error("Ubicación inexistente al cerrar")
                        val ruta = rutaTienda(posicion, posicionesCierre)
                        require(operaciones.guardarInstantanea(linea.id, inventarioId,
                            producto.codigo, producto.nombre, ruta) == 1) { "No se guardó el detalle histórico" }
                    }
                    require(operaciones.cerrarInventario(inventarioId, sid, jefe.id, System.currentTimeMillis(), observacion.trim()) == 1) {
                        "El inventario ya fue cerrado"
                    }
                    ConsolidacionMensual.cerrarTienda(db, inv)
                }
                respuesta(true, "Inventario cerrado; ajustes registrados")
            } catch (e: Exception) { respuesta(false, mensajeErrorUsuario(e, "No se pudo cerrar el inventario")) }
        }
    }

    fun salirDetalle() { cajaActivaId = null }
    fun cerrarSesion() {
        tiendaActiva = null
        cajaActivaId = null
        limpiarFormularioRecepcion()
        limpiarFormularioTraslado()
    }

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = TrabajadorTiendaViewModel(app) as T
    }
}
