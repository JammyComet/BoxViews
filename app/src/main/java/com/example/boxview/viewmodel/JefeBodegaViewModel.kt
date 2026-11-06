package com.example.boxview.viewmodel

// Lógica: Órdenes, stock e inventarios del jefe de bodega.
import com.example.boxview.mensajeErrorUsuario
import android.app.Application
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Base64
import java.security.SecureRandom

class JefeBodegaViewModel(app: Application) : AndroidViewModel(app) {
    // Conexion con la capa de datos.
    private val db = AppDatabase.getDatabase(app)
    private val dao = db.inventarioDao()

    data class LineaOrden(val productoId: Long, val cantidad: Int)

    // estados de los formularios
    private val _formularioOrden = MutableStateFlow(FormularioOrdenModel())
    val formularioOrden: StateFlow<FormularioOrdenModel> = _formularioOrden.asStateFlow()

    fun prepararOrden(bodegaId: Long) {
        if (_formularioOrden.value.bodegaId != bodegaId) {
            _formularioOrden.value = FormularioOrdenModel(bodegaId = bodegaId)
        }
    }
    fun elegirDestinoOrden(id: Long?) { _formularioOrden.update { it.copy(destino = id) } }
    fun elegirOperadorOrden(id: Long?) { _formularioOrden.update { it.copy(operador = id) } }
    fun quitarProductoOrden(idFila: Int) {
        _formularioOrden.update { estado ->
            if (estado.filas.size <= 1) estado
            else estado.copy(filas = estado.filas.filterNot { it.id == idFila })
        }
    }
    fun elegirProductoOrden(idFila: Int, productoId: Long?) {
        _formularioOrden.update { estado ->
            estado.copy(filas = estado.filas.map { f ->
                if (f.id == idFila) f.copy(productoId = productoId, cantidad = "") else f
            })
        }
    }
    fun cambiarCantidadOrden(idFila: Int, cantidad: String) {
        val limpia = cantidad.filter(Char::isDigit).take(6)
        _formularioOrden.update { estado ->
            estado.copy(filas = estado.filas.map { f ->
                if (f.id == idFila) f.copy(cantidad = limpia) else f
            })
        }
    }
    fun anadirProductoOrden() {
        _formularioOrden.update { estado ->
            estado.copy(filas = estado.filas + FilaOrdenNueva(estado.siguienteFila),
                siguienteFila = estado.siguienteFila + 1)
        }
    }
    fun reiniciarOrden() {
        _formularioOrden.update { FormularioOrdenModel(bodegaId = it.bodegaId) }
    }

    private val _formularioOperador = MutableStateFlow(FormularioOperadorModel())
    val formularioOperador: StateFlow<FormularioOperadorModel> = _formularioOperador.asStateFlow()
    fun cambiarNombreOperador(nombre: String) { _formularioOperador.update { it.copy(nombre = nombre) } }
    fun cambiarCorreoOperador(correo: String) { _formularioOperador.update { it.copy(correo = correo) } }
    fun cambiarRutOperador(rut: String) { _formularioOperador.update { it.copy(rut = rut) } }
    fun cambiarClaveOperador(clave: String) { _formularioOperador.update { it.copy(clave = clave) } }
    fun reiniciarFormularioOperador() { _formularioOperador.value = FormularioOperadorModel() }
    fun cerrarSesion() {
        _formularioOrden.value = FormularioOrdenModel()
        reiniciarFormularioOperador()
    }



    // Equipo asociado a una bodega.
    fun operadores(bodega: Long): Flow<List<Usuario>> = dao.operadoresDeBodega(bodega)
    fun ordenes(bodega: Long): Flow<List<Caja>> = dao.ordenesJefatura(bodega)
    fun compromisos(bodega: Long): Flow<List<CompromisoProducto>> = dao.compromisosSalida(bodega)
    fun periodos(bodega: Long): Flow<List<PeriodoInventario>> = dao.periodosJefatura(bodega)
    fun incidencias(bodega: Long): Flow<List<IncidenciaCaja>> = dao.incidenciasDeBodega(bodega)
    fun cajasDestino(bodega: Long): Flow<List<Caja>> = dao.cajasDeDestino(bodega)
    fun tiendasAbastecidas(bodega: Long): Flow<List<Sucursal>> = dao.tiendasAbastecidas(bodega)
    fun stockTienda(sucursalId: Long): Flow<List<ExistenciaTienda>> = dao.stockTienda(sucursalId)
    fun detalles(caja: Long): Flow<List<DetalleCaja>> = dao.observarDetallesCaja(caja)
    fun preparaciones(caja: Long): Flow<List<PreparacionCaja>> = dao.observarPreparacionesCaja(caja)
    fun lineas(periodo: Long): Flow<List<LineaInventario>> = dao.lineasInventario(periodo)
    fun ajustes(periodo: Long): Flow<List<AjusteInventario>> = dao.ajustesInventario(periodo)
    fun inventariosGenerales(bodega: Long) = dao.inventariosBodega(bodega)
    fun campaniasMensuales(bodega: Long) = db.campaniasMensualesDao().listarPorUbicacion("BODEGA", bodega)
    fun zonasCampania(id: Long) = db.campaniasMensualesDao().observarZonas(id)
    fun zonasGeneral(id: Long) = dao.zonasInventario(id)
    fun tareasGeneral(id: Long) = dao.tareasInventarioBodega(id)
    fun revisionesCaja(id: Long) = dao.revisionesCaja(id)

    private suspend fun comprobarJefe(jefeId: Long, bodegaId: Long) {
        require(dao.autorizadoJefe(jefeId, bodegaId) == 1) { "Acceso de jefatura no autorizado en esta bodega" }
    }

    private fun realizar(tarea: suspend () -> String, resultado: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try { resultado(true, tarea()) }
            catch (e: Exception) { resultado(false, mensajeErrorUsuario(e, "No se pudo completar la operación")) }
        }
    }

    // Alta y asignacion de operador.
    fun crearOperador(jefe: Usuario, bodegaId: Long, nombre: String, correo: String, clave: String, rut: String,
                      resultado: (Boolean, String) -> Unit) = realizar({
        val limpio = correo.trim().lowercase()
        require(nombre.trim().length >= 3) { "Escribe el nombre completo" }
        require(limpio.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))) { "Correo no válido" }
        require(clave.length >= 8) { "La contraseña debe tener al menos 8 caracteres" }
        require(rutValido(rut)) { "RUT no válido (revisa el dígito verificador)" }
        val rutNormalizado = rut.replace(".", "").replace("-", "").trim().uppercase()
        val sal = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val claveHash = withContext(Dispatchers.Default) { hashClave(clave, sal) }
        // Operacion agrupada en Room.
        db.withTransaction {
            comprobarJefe(jefe.id, bodegaId)
            require(dao.usuarioPorCorreo(limpio) == null) { "Ese correo ya está registrado" }
            val id = dao.siguienteIdUsuario()
            dao.guardarUsuario(Usuario(id, nombre.trim(), limpio, claveHash,
                Base64.encodeToString(sal, Base64.NO_WRAP), "OPERADOR_BODEGA", rut = rutNormalizado))
            dao.guardarPermiso(UsuarioBodega(id, bodegaId))
            "Operador creado y asignado a la bodega"
        }
    }, resultado)

    fun asignarOperador(jefe: Usuario, bodegaId: Long, operadorId: Long,
                        resultado: (Boolean, String) -> Unit) = realizar({
        db.withTransaction {
            comprobarJefe(jefe.id, bodegaId)
            val user = dao.usuarioPorId(operadorId) ?: error("Usuario no encontrado")
            require(user.activo && user.rol == "OPERADOR_BODEGA") { "Solo se pueden asignar operadores activos" }
            require(dao.empleadoExclusivoSucursal(operadorId,
                dao.bodegaAdministradaPorId(bodegaId)?.sucursalId ?: -1) == 1 ||
                dao.operadorSinBodegas(operadorId) == 1) {
                "El operador pertenece a otra sucursal"
            }
            require(dao.autorizado(operadorId, bodegaId) == 0) { "Ya pertenece a esta bodega" }
            dao.guardarPermisoSiFalta(UsuarioBodega(operadorId, bodegaId))
            "Operador autorizado para esta bodega"
        }
    }, resultado)

    fun retirarOperador(jefe: Usuario, bodegaId: Long, operadorId: Long,
                        resultado: (Boolean, String) -> Unit) = realizar({
        db.withTransaction {
            comprobarJefe(jefe.id, bodegaId)
            comprobarRetiroOperadorBodega(dao, operadorId, bodegaId)
            require(dao.retirarPermiso(operadorId, bodegaId) == 1) { "No se pudo retirar el acceso" }
            "Permiso de bodega retirado (la cuenta permanece activa)"
        }
    }, resultado)

    fun todosOperadores(bid: Long): Flow<List<Usuario>> = dao.operadoresDeSucursalDisponibles(bid)

    fun crearOrden(jefe: Usuario, origen: Bodega, destinoId: Long, operadorId: Long,
                   lineas: List<LineaOrden>, resultado: (Boolean, String) -> Unit) = realizar({
        db.withTransaction {
            comprobarJefe(jefe.id, origen.id)
            require(dao.existeSucursal(destinoId) == 1) { "La tienda de destino no existe" }
            require(dao.abasteceSucursal(origen.id, destinoId) == 1) { "La tienda de destino no corresponde a esta bodega" }
            require(dao.autorizado(operadorId, origen.id) == 1) { "Selecciona un operador autorizado en esta bodega" }
            require(lineas.isNotEmpty()) { "Selecciona al menos un producto" }
            require(lineas.distinctBy { it.productoId }.size == lineas.size) { "Hay productos repetidos" }
            require(lineas.all { it.cantidad in 1..100000 }) { "Las cantidades deben ser mayores que cero" }
            val posiciones = dao.posicionesActuales(origen.id)
            val existencias = dao.existenciasActuales(origen.id)
            for (l in lineas) {
                require(dao.productoPorId(l.productoId) != null) { "Producto no registrado" }
                val totalLibre = posiciones.sumOf { pos ->
                    val fisico = existencias.firstOrNull { it.productoId == l.productoId && it.posicionId == pos.id }?.cantidad ?: 0
                    (fisico - dao.stockReservadoCajas(l.productoId, pos.id, 0)).coerceAtLeast(0)
                }
                val totalFisico = existencias.filter { it.productoId == l.productoId }.sumOf { it.cantidad }
                val comprometido = dao.unidadesPendientes(l.productoId, origen.id)
                require(totalFisico - comprometido >= l.cantidad && totalLibre >= l.cantidad) {
                    "No hay suficientes unidades sin asignar para una de las prendas"
                }
            }
            val id = dao.siguienteIdCaja()
            val codigo = "CAJ-JEF-${id.toString().padStart(5, '0')}"
            dao.guardarCaja(Caja(id = id, codigo = codigo, tipo = "SALIDA", origen = origen.nombre,
                bodegaOrigenId = origen.id, bodegaDestinoId = 0, sucursalDestinoId = destinoId, solicitadoPor = jefe.nombre,
                asignadoA = operadorId, estado = "ORDEN_ASIGNADA"))
            lineas.forEach { dao.guardarDetalleCaja(DetalleCaja(cajaId = id,
                productoId = it.productoId, cantidadEsperada = it.cantidad)) }
            dao.guardarEventoCaja(EventoCaja(cajaId = id, usuarioId = jefe.id,
                accion = "ORDEN_CREADA", observacion = "Orden asignada al operador $operadorId"))
            "Orden $codigo asignada correctamente"
        }
    }, resultado)

    fun solicitudesDeReposicion(bodegaId: Long): Flow<List<SolicitudReposicion>> = dao.solicitudesDeBodega(bodegaId)

    // orden desde un pedido de tienda
    fun atenderSolicitud(jefe: Usuario, bodega: Bodega, solicitudId: Long, operadorId: Long,
                        resultado: (Boolean, String) -> Unit) = realizar({
        db.withTransaction {
            comprobarJefe(jefe.id, bodega.id)
            val primera = dao.solicitudPorId(solicitudId) ?: error("Solicitud inexistente")
            require(primera.estado == "PENDIENTE" && primera.bodegaId == bodega.id) {
                "El pedido no está pendiente en esta bodega"
            }
            val lineas = dao.solicitudesPendientesGrupo(primera.id, primera.loteId)
            require(lineas.isNotEmpty() && lineas.all {
                it.estado == "PENDIENTE" && it.sucursalId == primera.sucursalId && it.bodegaId == bodega.id
            }) { "El pedido cambió de estado" }
            require(dao.abasteceSucursal(bodega.id, primera.sucursalId) == 1) {
                "La bodega no pertenece a la sucursal de destino"
            }
            require(dao.autorizado(operadorId, bodega.id) == 1) { "Selecciona un operador autorizado" }
            require(lineas.distinctBy { it.productoId }.size == lineas.size) { "Pedido con productos repetidos" }
            val posiciones = dao.posicionesActuales(bodega.id)
            val existencias = dao.existenciasActuales(bodega.id)
            lineas.forEach { solicitud ->
                require(solicitud.cantidad in 1..100000 && dao.productoPorId(solicitud.productoId) != null) {
                    "Producto o cantidad inválida"
                }
                val fisico = existencias.filter { it.productoId == solicitud.productoId }.sumOf { it.cantidad }
                val comprometido = dao.unidadesPendientes(solicitud.productoId, bodega.id)
                val libreEstantes = posiciones.sumOf { pos ->
                    val unidades = existencias.firstOrNull {
                        it.productoId == solicitud.productoId && it.posicionId == pos.id
                    }?.cantidad ?: 0
                    (unidades - dao.stockReservadoCajas(solicitud.productoId, pos.id, 0)).coerceAtLeast(0)
                }
                require(fisico - comprometido >= solicitud.cantidad && libreEstantes >= solicitud.cantidad) {
                    "Stock insuficiente para ${dao.productoPorId(solicitud.productoId)?.nombre ?: "un producto"}. El pedido permanece pendiente"
                }
            }
            val id = dao.siguienteIdCaja()
            val codigo = "CAJ-JEF-${id.toString().padStart(5, '0')}"
            dao.guardarCaja(Caja(id = id, codigo = codigo, tipo = "SALIDA", origen = bodega.nombre,
                bodegaOrigenId = bodega.id, bodegaDestinoId = 0, sucursalDestinoId = primera.sucursalId,
                solicitadoPor = "Pedido tienda #${primera.id}", asignadoA = operadorId, estado = "ORDEN_ASIGNADA"))
            lineas.forEach { solicitud ->
                dao.guardarDetalleCaja(DetalleCaja(cajaId = id, productoId = solicitud.productoId,
                    cantidadEsperada = solicitud.cantidad))
                require(dao.atenderSolicitud(solicitud.id, bodega.id, id) == 1) { "El pedido cambió de estado" }
            }
            dao.guardarEventoCaja(EventoCaja(cajaId = id, usuarioId = jefe.id,
                accion = "ORDEN_DESDE_SOLICITUD", observacion = "Pedido #${primera.id} con ${lineas.size} productos"))
            "Orden $codigo generada con ${lineas.size} productos"
        }
    }, resultado)

    // revision de la preparacion antes del despacho
    fun devolverPreparacion(jefe: Usuario, bodegaId: Long, cajaId: Long, motivo: String,
                            respuesta: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                db.withTransaction {
                    require(dao.autorizadoJefe(jefe.id, bodegaId) == 1) { "Jefatura no autorizada" }
                    require(motivo.trim().length in 5..250) { "Indica el motivo de la devolución" }
                    val caja = dao.cajaPorId(cajaId) ?: error("Orden inexistente")
                    require(caja.tipo == "SALIDA" && caja.bodegaOrigenId == bodegaId &&
                        caja.estado == "PENDIENTE_REVISION") { "Orden no disponible" }
                    require(caja.preparadoPor != jefe.id) { "No puedes revisar tu propia preparación" }
                    dao.actualizarCaja(caja.copy(estado = "ORDEN_ASIGNADA", preparadoPor = null,
                        revisorId = null))
                    dao.guardarEventoCaja(EventoCaja(cajaId = cajaId, usuarioId = jefe.id,
                        accion = "PREPARACION_DEVUELTA", observacion = motivo.trim()))
                }
                respuesta(true, "Preparación devuelta al operador")
            } catch (e: Exception) { respuesta(false, mensajeErrorUsuario(e, "No se pudo devolver la preparación")) }
        }
    }

    fun revisarDespacho(jefe: Usuario, bodegaId: Long, cajaId: Long,
                        respuesta: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                db.withTransaction {
                    require(dao.autorizadoJefe(jefe.id, bodegaId) == 1) { "Jefatura no autorizada" }
                    val caja = dao.cajaPorId(cajaId) ?: error("Orden inexistente")
                    require(caja.tipo == "SALIDA" && caja.bodegaOrigenId == bodegaId &&
                        caja.estado == "PENDIENTE_REVISION") { "La orden no está pendiente de revisión" }
                    val preparador = caja.preparadoPor ?: error("La orden no tiene preparador")
                    require(preparador != jefe.id) { "No puedes revisar tu propia preparación" }
                    val destino = caja.sucursalDestinoId ?: error("Falta tienda de destino")
                    require(dao.abasteceSucursal(bodegaId, destino) == 1) { "Destino no autorizado" }
                    val detalles = dao.detallesCaja(cajaId)
                    val preparaciones = dao.preparacionesCaja(cajaId)
                    require(detalles.isNotEmpty() && preparaciones.isNotEmpty()) { "Preparación incompleta" }
                    require(preparaciones.all { it.cantidad > 0 }) { "Cantidad inválida" }
                    detalles.forEach { detalle ->
                        require(preparaciones.filter { it.productoId == detalle.productoId }
                            .sumOf { it.cantidad } == detalle.cantidadEsperada) {
                            "La preparación de un producto no coincide con la orden"
                        }
                    }
                    require(preparaciones.all { p -> detalles.any { it.productoId == p.productoId } }) {
                        "La preparación contiene productos ajenos a la orden"
                    }
                    val agrupadas = preparaciones.groupBy { it.productoId to it.posicionId }
                    for ((clave, filas) in agrupadas) {
                        val (producto, posicion) = clave
                        val ubicacion = dao.posicionPorId(posicion) ?: error("Estante inexistente")
                        require(ubicacion.bodegaId == bodegaId && ubicacion.tipo != "SECCION") {
                            "Estante ajeno a esta bodega"
                        }
                        require(dao.posicionBloqueada(posicion) == 0 && dao.posicionCongeladaPorCampania(posicion) == 0) {
                            "El estante está congelado hasta finalizar la campaña mensual"
                        }
                        val cantidad = filas.sumOf { it.cantidad }
                        val disponible = dao.existencia(producto, posicion) ?: error("Stock inexistente")
                        require(disponible.cantidad - dao.stockReservadoCajas(producto, posicion, cajaId) >= cantidad) {
                            "Stock reservado para otra orden"
                        }
                        require(dao.restarSiAlcanza(disponible.id, cantidad) == 1) { "Stock insuficiente" }
                        dao.guardarMovimiento(Movimiento(usuarioId = jefe.id, bodegaId = bodegaId,
                            productoId = producto, posicionOrigen = posicion, posicionDestino = 0,
                            cantidad = cantidad, tipo = "SALIDA_CAJA"))
                    }
                    dao.actualizarCaja(caja.copy(estado = "EN_TRANSITO", revisorId = jefe.id))
                    dao.guardarEventoCaja(EventoCaja(cajaId = cajaId, usuarioId = jefe.id,
                        accion = "DESPACHO_REVISADO", observacion = "Preparación comprobada y salida autorizada"))
                }
                respuesta(true, "Despacho aprobado y caja en tránsito")
            } catch (e: Exception) { respuesta(false, mensajeErrorUsuario(e, "No se pudo revisar el despacho")) }
        }
    }

    fun cancelarOrden(jefe: Usuario, bodegaId: Long, cajaId: Long,
                      resultado: (Boolean, String) -> Unit) = realizar({
        db.withTransaction {
            comprobarJefe(jefe.id, bodegaId)
            val caja = dao.cajaPorId(cajaId) ?: error("Orden inexistente")
            require(caja.bodegaOrigenId == bodegaId && caja.tipo == "SALIDA" && caja.estado == "ORDEN_ASIGNADA") {
                "Solo se pueden cancelar órdenes asignadas que no se han despachado"
            }
            require(dao.avancesDeCaja(cajaId) == 0) { "El operador ya preparó esta orden; no puede cancelarse" }
            require(dao.cancelarOrden(cajaId) == 1) { "La orden cambió de estado" }
            dao.reabrirSolicitudCaja(cajaId)
            dao.guardarEventoCaja(EventoCaja(cajaId = cajaId, usuarioId = jefe.id, accion = "ORDEN_CANCELADA"))
            "Orden cancelada"
        }
    }, resultado)

    // abrir inventario mensual
    fun abrirInventario(jefe: Usuario, bodegaId: Long, nombre: String,
                        asignaciones: Map<Long, Long>, resultado: (Boolean, String) -> Unit) = realizar({
        db.withTransaction {
            comprobarJefe(jefe.id, bodegaId)
            val titulo = nombre.trim().lowercase().replaceFirstChar { it.uppercase() }
            require(Regex("^(enero|febrero|marzo|abril|mayo|junio|julio|agosto|septiembre|octubre|noviembre|diciembre)\\s+20\\d{2}(\\s*-\\s*[\\p{L}0-9][\\p{L}0-9 _-]{1,50})?$", RegexOption.IGNORE_CASE).matches(titulo)) {
                "Escribe mes y año, o añade una zona: Noviembre 2026 - Estante A"
            }
            require(dao.existeInventarioBodegaMes(bodegaId, titulo) == 0) { "Ya existe inventario de este mes para la bodega" }
            require(dao.inventarioBodegaPendiente(bodegaId) == 0) { "Primero termina el inventario mensual activo" }
            val posicionesConStock = dao.existenciasActuales(bodegaId)
                .filter { it.cantidad > 0 }.map { it.posicionId }.toSet()
            val mesAnterior = db.campaniasMensualesDao().buscar("BODEGA", bodegaId,
                mesCampaniaInventario(titulo))
            val estantesPendientesMes = mesAnterior?.let { db.campaniasMensualesDao().zonas(it.id) }
                ?.filter { it.estado == "PENDIENTE" }?.map { it.posicionId }?.toSet().orEmpty()
            val posiciones = dao.posicionesActuales(bodegaId).filter {
                it.tipo != "SECCION" && it.tipo != "RECEPCION" && (it.activa || it.id in posicionesConStock || it.id in estantesPendientesMes)
            }
            require(posiciones.isNotEmpty()) { "No hay estantes registrados" }
            require(asignaciones.isNotEmpty() && asignaciones.keys.all { it in posiciones.map(Posicion::id) }) {
                "Selecciona al menos una estantería válida para el inventario"
            }
            for (operador in asignaciones.values.filter { it > 0L }.distinct()) {
                require(dao.operadorActivoDeBodega(operador, bodegaId) == 1) { "Hay un operador no autorizado" }
                require(dao.periodoPendiente(bodegaId, operador) == 0) { "Un operador tiene otro conteo pendiente" }
            }
            // El primer parcial congela el listado de estantes del mes.
            val campania = ConsolidacionMensual.preparar(db, "BODEGA", bodegaId,
                (dao.bodegaAdministradaPorId(bodegaId) ?: error("Bodega no encontrada")).sucursalId, titulo,
                posiciones.associate { it.id to rutaBodega(it, posiciones) })
            val id = dao.siguienteIdInventarioBodega()
            dao.guardarInventarioBodega(InventarioBodega(id, bodegaId, titulo, campaniaId = campania.id))
            ConsolidacionMensual.reservar(db, campania, asignaciones.keys, id)
            val tareas = mutableMapOf<Long, Long>()
            for (operador in asignaciones.values.filter { it > 0L }.distinct()) {
                val periodoId = dao.siguienteIdPeriodo()
                dao.guardarPeriodoInventario(PeriodoInventario(periodoId, bodegaId, operador, titulo,
                    inventarioBodegaId = id))
                tareas[operador] = periodoId
            }
            asignaciones.forEach { (posicionId, operador) ->
                dao.guardarZonaInventario(ZonaInventario(id, posicionId, operador, tareas[operador] ?: 0L))
            }
            // Solo congelar saldos de las zonas incluidas en este conteo.
            dao.existenciasActuales(bodegaId).filter { it.posicionId in asignaciones.keys }.forEach { e ->
                dao.guardarReferenciaInventario(ReferenciaInventario(id, e.productoId,
                    e.posicionId, e.cantidad))
            }
            "Inventario $titulo abierto: ${asignaciones.size} estantes, ${tareas.size} operadores y ${asignaciones.values.count { it == 0L }} sin asignar"
        }
    }, resultado)

    fun asignarZonaInventario(jefe: Usuario, bodegaId: Long, inventarioId: Long,
                             posicionId: Long, operadorId: Long,
                             resultado: (Boolean, String) -> Unit) = realizar({
        db.withTransaction {
            comprobarJefe(jefe.id, bodegaId)
            val inv = dao.inventarioBodegaPorId(inventarioId) ?: error("Inventario no encontrado")
            require(inv.bodegaId == bodegaId && inv.estado == "ABIERTO") { "El inventario ya no acepta asignaciones" }
            require(dao.operadorActivoDeBodega(operadorId, bodegaId) == 1) { "Operador no autorizado" }
            require(dao.periodoPendiente(bodegaId, operadorId) == 0 || dao.tareasDeOperador(inventarioId, operadorId) > 0) {
                "El operador tiene otro inventario pendiente"
            }
            // Si ya entregó la tarea anterior, se crea otra tarea para el estante nuevo.
            val existente = dao.tareaDeOperador(inventarioId, operadorId)
            val tarea = existente ?: run {
                val nueva = dao.siguienteIdPeriodo()
                dao.guardarPeriodoInventario(PeriodoInventario(nueva, bodegaId, operadorId, inv.nombre,
                    inventarioBodegaId = inventarioId))
                nueva
            }
            require(dao.asignarZonaPendiente(inventarioId, posicionId, operadorId, tarea) == 1) {
                "Esta zona ya fue asignada o no pertenece al inventario"
            }
            "Estantería asignada correctamente"
        }
    }, resultado)

    fun cancelarInventarioGeneral(jefe: Usuario, bodegaId: Long, inventarioId: Long,
                                  motivo: String, resultado: (Boolean, String) -> Unit) = realizar({
        db.withTransaction {
            comprobarJefe(jefe.id, bodegaId)
            val inv = dao.inventarioBodegaPorId(inventarioId) ?: error("Inventario no encontrado")
            require(inv.bodegaId == bodegaId && inv.estado in listOf("ABIERTO", "PENDIENTE_REVISION")) {
                "Solo se puede cancelar un inventario sin conciliar"
            }
            require(motivo.trim().length in 5..250) { "Explica el motivo de cancelación" }
            val bodega = dao.bodegaAdministradaPorId(bodegaId) ?: error("Bodega no disponible")
            // No se altera el stock. Se retira del historial de inventarios y solo queda
            // constancia de la cancelación en la auditoría administrativa.
            dao.guardarEventoAdministrativo(EventoAdministrativo(
                sucursalId = bodega.sucursalId, usuarioId = jefe.id,
                accion = "INVENTARIO_CANCELADO",
                detalle = "Bodega $bodegaId · ${inv.nombre} · ${motivo.trim()}"))
            ConsolidacionMensual.liberar(db, inv.campaniaId, inventarioId)
            dao.borrarAjustesInventarioGeneral(inventarioId)
            dao.borrarLineasInventarioGeneral(inventarioId)
            dao.borrarBorradoresInventarioGeneral(inventarioId)
            dao.borrarVaciosInventarioBodegaGeneral(inventarioId)
            dao.borrarTareasInventarioGeneral(inventarioId)
            dao.borrarReferenciasInventarioGeneral(inventarioId)
            dao.borrarZonasInventarioGeneral(inventarioId)
            require(dao.borrarInventarioGeneral(inventarioId) == 1) { "El inventario cambió de estado" }
            "Inventario cancelado. Puedes abrir otro sin que figure como inventario finalizado"
        }
    }, resultado)

    fun solicitarReconteo(jefe: Usuario, bodegaId: Long, inventarioId: Long, tareaId: Long,
                           resultado: (Boolean, String) -> Unit) = realizar({
        db.withTransaction {
            comprobarJefe(jefe.id, bodegaId)
            val general = dao.inventarioBodegaPorId(inventarioId) ?: error("Inventario no encontrado")
            val tarea = dao.periodoPorId(tareaId) ?: error("Tarea no encontrada")
            require(general.bodegaId == bodegaId && general.estado == "PENDIENTE_REVISION" &&
                tarea.inventarioBodegaId == inventarioId && tarea.estado == "ENTREGADO") {
                "El inventario aún no está listo para solicitar un recuento"
            }
            require(dao.reabrirInventarioBodega(inventarioId) == 1) { "El inventario cambió de estado" }
            require(dao.reabrirTareaInventario(tareaId) == 1) { "La tarea cambió de estado" }
            dao.borrarLineasInventario(tareaId)
            dao.borrarBorradorInventario(tareaId)
            dao.borrarEstantesVacios("BODEGA", tareaId)
            "Recuento solicitado: el operador debe verificar de nuevo sus estantes"
        }
    }, resultado)

    // guardar la foto del inventario al cerrarlo
    private suspend fun guardarInformeGeneral(inv: InventarioBodega, jefeId: Long, estado: String) {
        val bodega = dao.bodegaAdministradaPorId(inv.bodegaId) ?: error("Bodega no encontrada")
        val sucursal = dao.sucursalesActuales().firstOrNull { it.id == bodega.sucursalId }
            ?: error("Sucursal no encontrada")
        val tareas = dao.tareasInventarioActuales(inv.id)
        val referencias = dao.referenciasInventario(inv.id)
        val zonas = dao.zonasInventarioActuales(inv.id)
        val lineas = tareas.flatMap { dao.lineasInventarioActuales(it.id) }
        val ajustes = tareas.flatMap { dao.ajustesInventarioActuales(it.id) }
        val contadas = lineas.associateBy { it.productoId to it.posicionId }
        require(contadas.size == lineas.size) { "Hay líneas duplicadas en el inventario" }
        val aplicadas = ajustes.map { it.productoId to it.posicionId }.toSet()
        val claves = (referencias.map { it.productoId to it.posicionId } + contadas.keys).distinct()
        val referenciasMapa = referencias.associateBy { it.productoId to it.posicionId }
        val detalle = claves.map { (productoId, posicionId) ->
            val producto = dao.productoPorId(productoId)
            val posicion = dao.posicionPorId(posicionId)
            var actual = posicion
            val ruta = mutableListOf<String>()
            val visitadas = mutableSetOf<Long>()
            while (true) {
                val actualPosicion = actual ?: break
                if (!visitadas.add(actualPosicion.id) || ruta.size >= 10) break
                ruta.add(actualPosicion.nombre.ifBlank { actualPosicion.codigo })
                actual = actualPosicion.padreId?.let { dao.posicionPorId(it) }
            }
            DetalleInformeInventario(
                informeId = 0, productoId = productoId,
                codigo = producto?.codigo ?: "ID-$productoId",
                producto = producto?.nombre ?: "Producto $productoId",
                posicion = ruta.reversed().joinToString(" / ").ifBlank { "Ubicación $posicionId" },
                cantidadSistema = referenciasMapa[productoId to posicionId]?.cantidad ?: 0,
                cantidadContada = contadas[productoId to posicionId]?.cantidadContada,
                ajusteAplicado = (productoId to posicionId) in aplicadas
            )
        }
        val nuevo = InformeInventario(
            inventarioId = inv.id, sucursalId = sucursal.id, bodegaId = bodega.id,
            sucursalNombre = sucursal.nombre, bodegaNombre = bodega.nombre,
            nombre = inv.nombre, estadoCierre = estado,
            fechaApertura = inv.fechaApertura, fechaCierre = System.currentTimeMillis(),
            cerradoPor = jefeId, posiciones = zonas.size,
            productosContados = detalle.count { it.cantidadContada != null },
            productosSinContar = detalle.count { it.cantidadContada == null },
            lineasConDiferencia = detalle.count { it.cantidadContada != null && it.cantidadContada != it.cantidadSistema },
            unidadesSistema = detalle.sumOf { it.cantidadSistema },
            unidadesContadas = detalle.sumOf { it.cantidadContada ?: 0 },
            ajustesAplicados = ajustes.size
        )
        val informeId = db.informesGerenciaDao().guardarInforme(nuevo)
        detalle.forEach { db.informesGerenciaDao().guardarDetalleInforme(it.copy(informeId = informeId)) }
    }

    fun cerrarInventarioGeneral(jefe: Usuario, bodegaId: Long, id: Long, motivo: String,
                                 resultado: (Boolean, String) -> Unit) = realizar({
        db.withTransaction {
            comprobarJefe(jefe.id, bodegaId)
            val inv = dao.inventarioBodegaPorId(id) ?: error("Inventario inexistente")
            require(inv.bodegaId == bodegaId && inv.estado == "PENDIENTE_REVISION") { "Faltan conteos o ya fue cerrado" }
            require(dao.zonasSinAsignar(id) == 0) { "Quedan estanterías sin asignar" }
            require(dao.tareasSinEntregar(id) == 0) { "Faltan operadores por entregar" }
            require(motivo.trim().length in 5..250) { "Indica por qué se cierra sin ajustes" }
            val sede = dao.bodegaAdministradaPorId(bodegaId) ?: error("Bodega inexistente")
            dao.guardarEventoAdministrativo(EventoAdministrativo(
                sucursalId = sede.sucursalId, usuarioId = jefe.id,
                accion = "INVENTARIO_CERRADO_SIN_AJUSTES",
                detalle = "Inventario $id · ${inv.nombre} · Diferencias sin ajuste · ${motivo.trim()}"))
            require(dao.finalizarInventarioBodega(id, "CERRADO", jefe.id) == 1) { "Ya fue cerrado" }
            dao.finalizarTareasInventario(id, "CERRADO")
            guardarInformeGeneral(inv, jefe.id, "CERRADO")
            ConsolidacionMensual.cerrarBodega(db, inv)
            "Inventario revisado y cerrado; informe enviado a gerencia"
        }
    }, resultado)

    fun aprobarInventarioGeneral(jefe: Usuario, bodegaId: Long, id: Long,
                                  resultado: (Boolean, String) -> Unit) = realizar({
        db.withTransaction {
            comprobarJefe(jefe.id, bodegaId)
            val inv = dao.inventarioBodegaPorId(id) ?: error("Inventario inexistente")
            require(inv.bodegaId == bodegaId && inv.estado == "PENDIENTE_REVISION") { "El período no está listo" }
            require(dao.zonasSinAsignar(id) == 0) { "Quedan estanterías sin asignar" }
            require(dao.tareasSinEntregar(id) == 0) { "Faltan conteos" }
            val tareas = dao.tareasInventarioActuales(id)
            val lineas = tareas.flatMap { dao.lineasInventarioActuales(it.id) }
            val referencias = dao.referenciasInventario(id)
            val claves = lineas.map { it.productoId to it.posicionId }
            require(claves.distinct().size == claves.size) { "Hay zonas duplicadas" }
            require(referencias.all { (it.productoId to it.posicionId) in claves }) {
                "Faltan productos por contar, pide un recuento"
            }
            val saldos = dao.existenciasActuales(bodegaId)
            val diferencias = mutableMapOf<Long, Int>()
            lineas.forEach { l ->
                require(l.cantidadContada in 0..100000) { "Cantidad incorrecta" }
                require(dao.posicionPorId(l.posicionId)?.bodegaId == bodegaId) { "Estante ajeno" }
                val anterior = referencias.firstOrNull {
                    it.productoId == l.productoId && it.posicionId == l.posicionId
                }?.cantidad ?: 0
                val ahora = dao.existencia(l.productoId, l.posicionId)?.cantidad ?: 0
                require(ahora == anterior) { "Cambió el stock durante el inventario. Repite el conteo antes de ajustar" }
                require(l.cantidadContada >= dao.stockReservadoCajas(l.productoId, l.posicionId, 0)) {
                    "Hay unidades preparadas por órdenes: revisa antes de conciliar"
                }
                diferencias[l.productoId] = (diferencias[l.productoId] ?: 0) + l.cantidadContada - anterior
            }
            diferencias.forEach { (producto, diferencia) ->
                val actual = saldos.filter { it.productoId == producto }.sumOf { it.cantidad }
                require(actual + diferencia >= dao.unidadesPendientes(producto, bodegaId)) {
                    "El inventario dejaría órdenes sin stock: comprueba las cantidades"
                }
            }
            var ajustes = 0
            lineas.forEach { l ->
                val anterior = referencias.firstOrNull {
                    it.productoId == l.productoId && it.posicionId == l.posicionId
                }?.cantidad ?: 0
                if (l.cantidadContada != anterior) {
                    val existencia = dao.existencia(l.productoId, l.posicionId)
                    if (existencia == null) {
                        require(anterior == 0) { "La posición cambió" }
                        dao.guardarExistencia(Existencia(productoId = l.productoId, posicionId = l.posicionId,
                            cantidad = l.cantidadContada))
                    } else require(dao.ajustarExistenciaSiCoincide(existencia.id, anterior, l.cantidadContada) == 1) {
                        "Las cantidades cambiaron inesperadamente"
                    }
                    dao.guardarAjusteInventario(AjusteInventario(periodoId = l.periodoId,
                        productoId = l.productoId, posicionId = l.posicionId,
                        stockAnterior = anterior, stockNuevo = l.cantidadContada, revisadoPor = jefe.id))
                    dao.guardarMovimiento(Movimiento(usuarioId = jefe.id, bodegaId = bodegaId,
                        productoId = l.productoId, posicionOrigen = l.posicionId, posicionDestino = l.posicionId,
                        cantidad = kotlin.math.abs(l.cantidadContada - anterior),
                        tipo = if (l.cantidadContada > anterior) "AJUSTE_INVENTARIO_ENTRADA" else "AJUSTE_INVENTARIO_SALIDA"))
                    ajustes++
                }
            }
            require(dao.finalizarInventarioBodega(id, "CONCILIADO", jefe.id) == 1) { "Inventario ya conciliado" }
            dao.finalizarTareasInventario(id, "CONCILIADO")
            guardarInformeGeneral(inv, jefe.id, "CONCILIADO")
            ConsolidacionMensual.cerrarBodega(db, inv)
            "Inventario conciliado: $ajustes ajustes; informe enviado a gerencia"
        }
    }, resultado)

    fun cerrarInventario(jefe: Usuario, bodegaId: Long, periodoId: Long,
                         resultado: (Boolean, String) -> Unit) = realizar({
        db.withTransaction {
            comprobarJefe(jefe.id, bodegaId)
            val periodo = dao.periodoPorId(periodoId) ?: error("Inventario inexistente")
            require(periodo.bodegaId == bodegaId && periodo.estado == "ENTREGADO") {
                "Solo se pueden cerrar inventarios entregados de esta bodega"
            }
            require(dao.cerrarPeriodo(periodoId) == 1) { "El inventario ya se cerró" }
            "Revisión cerrada; stock sin cambios"
        }
    }, resultado)

    // aplicar productos contados
    fun aprobarInventario(jefe: Usuario, bodegaId: Long, periodoId: Long,
                          resultado: (Boolean, String) -> Unit) = realizar({
        db.withTransaction {
            comprobarJefe(jefe.id, bodegaId)
            val periodo = dao.periodoPorId(periodoId) ?: error("Inventario inexistente")
            require(periodo.bodegaId == bodegaId && periodo.estado == "ENTREGADO") {
                "El inventario ya fue cerrado o no está entregado"
            }
            val lineas = dao.lineasInventarioActuales(periodoId)
            require(lineas.isNotEmpty()) { "No hay productos contados para aprobar" }
            val saldos = dao.existenciasActuales(bodegaId)
            val diferenciasPorProducto = mutableMapOf<Long, Int>()
            lineas.forEach { l ->
                require(l.cantidadContada in 0..100000) { "Cantidad fuera de rango" }
                require(dao.posicionPorId(l.posicionId)?.bodegaId == bodegaId) { "Estante ajeno a la bodega" }
                val anterior = l.stockAlEntregar ?: error("Este inventario antiguo no tiene saldo de referencia; ciérralo sin ajustar")
                val ahora = dao.existencia(l.productoId, l.posicionId)?.cantidad ?: 0
                require(ahora == anterior) {
                    "Cambió el stock de un producto desde que entregaron el conteo. Verifica y realiza un nuevo conteo antes de ajustar"
                }
                val reservadas = dao.stockReservadoCajas(l.productoId, l.posicionId, 0)
                require(l.cantidadContada >= reservadas) {
                    "El conteo es menor que las unidades ya preparadas para salidas. Revisa las órdenes antes de ajustar"
                }
                diferenciasPorProducto[l.productoId] = (diferenciasPorProducto[l.productoId] ?: 0) + l.cantidadContada - anterior
            }
            diferenciasPorProducto.forEach { (productoId, diferencia) ->
                val totalActual = saldos.filter { it.productoId == productoId }.sumOf { it.cantidad }
                val pendientes = dao.unidadesPendientes(productoId, bodegaId)
                require(totalActual + diferencia >= pendientes) {
                    "El stock conciliado quedaría por debajo de las unidades solicitadas en órdenes pendientes"
                }
            }
            var ajustes = 0
            lineas.forEach { l ->
                val anterior = l.stockAlEntregar!!
                val nuevo = l.cantidadContada
                if (nuevo != anterior) {
                    val existente = dao.existencia(l.productoId, l.posicionId)
                    if (existente == null) {
                        require(anterior == 0) { "La posición cambió desde la entrega" }
                        dao.guardarExistencia(Existencia(productoId = l.productoId,
                            posicionId = l.posicionId, cantidad = nuevo))
                    } else require(dao.ajustarExistenciaSiCoincide(existente.id, anterior, nuevo) == 1) {
                        "Las existencias cambiaron durante la conciliación"
                    }
                    dao.guardarAjusteInventario(AjusteInventario(periodoId = periodoId,
                        productoId = l.productoId, posicionId = l.posicionId,
                        stockAnterior = anterior, stockNuevo = nuevo, revisadoPor = jefe.id))
                    dao.guardarMovimiento(Movimiento(usuarioId = jefe.id, bodegaId = bodegaId,
                        productoId = l.productoId, posicionOrigen = l.posicionId,
                        posicionDestino = l.posicionId, cantidad = kotlin.math.abs(nuevo - anterior),
                        tipo = if (nuevo > anterior) "AJUSTE_INVENTARIO_ENTRADA" else "AJUSTE_INVENTARIO_SALIDA"))
                    ajustes++
                }
            }
            require(dao.conciliarPeriodo(periodoId) == 1) { "El inventario ya fue conciliado" }
            "Inventario aprobado: $ajustes diferencias aplicadas a stock y registradas en movimientos"
        }
    }, resultado)

    fun resolverIncidencia(jefe: Usuario, bodegaId: Long, incidenciaId: Long, observacion: String,
                           resultado: (Boolean, String) -> Unit) = realizar({
        db.withTransaction {
            comprobarJefe(jefe.id, bodegaId)
            require(observacion.trim().length >= 5) { "Escribe una observación de revisión" }
            val incidencia = dao.incidenciaPorId(incidenciaId) ?: error("Incidencia inexistente")
            val caja = dao.cajaPorId(incidencia.cajaId) ?: error("Caja inexistente")
            require(caja.bodegaDestinoId == bodegaId) { "La incidencia corresponde a otra bodega" }
            require(dao.resolverIncidencia(incidenciaId) == 1) { "La incidencia ya se resolvió" }
            dao.guardarEventoCaja(EventoCaja(cajaId = caja.id, usuarioId = jefe.id,
                accion = "INCIDENCIA_REVISADA", observacion = "Incidencia ${incidencia.id}: ${observacion.trim()}"))
            "Incidencia marcada como revisada (no resuelta); stock sin cambios"
        }
    }, resultado)

    // ubicaciones de bodega
    fun ubicaciones(bodegaId: Long): Flow<List<Posicion>> = dao.posiciones(bodegaId)

    fun crearUbicacionBodega(jefe: Usuario, bodegaId: Long, nombre: String, tipo: String,
                             padreId: Long?, resultado: (Boolean, String) -> Unit) = realizar({
        db.withTransaction {
            comprobarJefe(jefe.id, bodegaId)
            require(db.campaniasMensualesDao().campañaActiva("BODEGA", bodegaId) == 0) {
                "No se pueden renombrar ubicaciones mientras la campaña mensual esté abierta"
            }
            val limpio = nombre.trim()
            require(limpio.length in 2..60) { "Nombre de 2 a 60 caracteres" }
            require(tipo in listOf("SECCION", "ESTANTERIA", "SUBESTANTE")) { "Tipo no válido" }
            require(dao.inventarioBodegaPendiente(bodegaId) == 0) {
                "Cierra el inventario mensual antes de crear ubicaciones"
            }
            val actuales = dao.posicionesActuales(bodegaId)
            require(actuales.none { it.padreId == padreId && it.etiqueta().equals(limpio, true) }) {
                "Ya existe ese nombre en el mismo nivel"
            }
            if (tipo == "SECCION") require(padreId == null) { "La sección no admite ubicación superior" }
            else {
                val padre = actuales.firstOrNull { it.id == padreId } ?: error("Selecciona una ubicación superior")
                require(padre.activa) { "La ubicación superior está inactiva" }
                require(padre.tipo == (if (tipo == "ESTANTERIA") "SECCION" else "ESTANTERIA")) {
                    "Nivel superior no válido"
                }
            }
            val id = dao.siguienteIdPosicion()
            dao.guardarPosicion(Posicion(id, bodegaId, "UB-$id", limpio, tipo, padreId))
            "Ubicación creada"
        }
    }, resultado)

    fun renombrarUbicacionBodega(jefe: Usuario, bodegaId: Long, id: Long, nombre: String,
                                 resultado: (Boolean, String) -> Unit) = realizar({
        db.withTransaction {
            comprobarJefe(jefe.id, bodegaId)
            val lugar = dao.posicionPorId(id) ?: error("Ubicación no encontrada")
            require(lugar.bodegaId == bodegaId) { "Ubicación ajena a esta bodega" }
            require(db.campaniasMensualesDao().campañaActiva("BODEGA", bodegaId) == 0) {
                "No se pueden renombrar ubicaciones mientras la campaña mensual esté abierta"
            }
            require(lugar.tipo != "RECEPCION") { "Recepción es una zona fija" }
            val limpio = nombre.trim()
            require(limpio.length in 2..60) { "Nombre de 2 a 60 caracteres" }
            require(dao.posicionesActuales(bodegaId).none {
                it.id != id && it.padreId == lugar.padreId && it.etiqueta().equals(limpio, true)
            }) { "Ya existe ese nombre en el mismo nivel" }
            require(dao.renombrarPosicion(id, bodegaId, limpio) == 1) { "No se pudo cambiar el nombre" }
            "Nombre actualizado"
        }
    }, resultado)

    fun activarUbicacionBodega(jefe: Usuario, bodegaId: Long, id: Long, activar: Boolean,
                                resultado: (Boolean, String) -> Unit) = realizar({
        db.withTransaction {
            comprobarJefe(jefe.id, bodegaId)
            val actuales = dao.posicionesActuales(bodegaId)
            val lugar = actuales.firstOrNull { it.id == id } ?: error("Ubicación no encontrada")
            require(dao.posicionBloqueada(id) == 0 &&
                db.campaniasMensualesDao().campañaActiva("BODEGA", bodegaId) == 0) {
                "La ubicación no se puede modificar durante un inventario mensual"
            }
            if (activar && lugar.padreId != null) require(actuales.firstOrNull { it.id == lugar.padreId }?.activa == true) {
                "Activa primero la ubicación superior"
            }
            if (!activar) require(actuales.none { it.padreId == id && it.activa }) {
                "Desactiva primero sus ubicaciones inferiores"
            }
            require(dao.estadoPosicion(id, bodegaId, activar) == 1) { "No se pudo guardar el estado" }
            if (activar) "Ubicación activada" else "Ubicación desactivada. El stock se conserva."
        }
    }, resultado)

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = JefeBodegaViewModel(app) as T
    }
}
