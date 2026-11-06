package com.example.boxview.viewmodel

// Lógica: Login, sesión y operaciones de bodega.
import com.example.boxview.mensajeErrorUsuario
import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.boxview.modelo.*
import android.util.Base64
import androidx.room.withTransaction
import java.security.MessageDigest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class OperadorViewModel(app: Application) : AndroidViewModel(app) {
    // conexion a la base de datos
    // Conexion con la capa de datos.
    private val db = AppDatabase.getDatabase(app)
    private val dao = db.inventarioDao()
    var preparado by mutableStateOf(false)
        private set
    var errorInicio by mutableStateOf("")
        private set
    var avisoInicio by mutableStateOf("")
        private set
    var sesion by mutableStateOf<Usuario?>(null)
        private set
    var bodegaActiva by mutableStateOf<Bodega?>(null)
        private set
    var codigoEscaneado by mutableStateOf("")
    var cajaActivaId by mutableStateOf<Long?>(null)
        private set
    var modoCaja by mutableStateOf("INGRESO")
        private set

    init {
        viewModelScope.launch {
            try {
                inicializar()
            } catch (e: Exception) {
                Log.e("BoxViewAcceso", "Fallo al preparar los datos iniciales", e)
                // una falla de carga no debe bloquear cuentas que ya existen
                try {
                    val cuentas = dao.numeroUsuarios()
                    if (cuentas > 0) {
                        // crear gerencia por separado si faltó durante la carga
                        try {
                            // Operacion agrupada en Room.
                            db.withTransaction { asegurarGerencia(dao, ::hash) }
                        } catch (errorGerencia: Exception) {
                            Log.e("BoxViewAcceso", "No se pudo comprobar la cuenta de gerencia", errorGerencia)
                        }
                        avisoInicio = "Algunas funciones podrían no estar disponibles. Puedes intentar ingresar."
                    } else {
                        // El catálogo de pruebas y los usuarios se cargan juntos. Si falla
                        // cualquier dato de muestra, Room revierte también todas las cuentas.
                        // Recuperar SOLO acceso básico, sin ocultar el problema del catálogo.
                        try {
                            db.withTransaction {
                                asegurarAccesoBasicoTrasFalloInicial(dao, ::hash)
                            }
                            avisoInicio = "Algunas funciones podrían no estar disponibles. Puedes intentar ingresar."
                            Log.w("BoxViewAcceso", "Cuentas recuperadas; carga completa pendiente")
                        } catch (errorRecuperacion: Exception) {
                            Log.e("BoxViewAcceso", "Tampoco se pudieron crear las cuentas básicas", errorRecuperacion)
                            errorInicio = "No se pudo preparar el acceso. Inténtalo nuevamente."
                        }
                    }
                } catch (errorBase: Exception) {
                    Log.e("BoxViewAcceso", "No se puede abrir la base de datos Room", errorBase)
                    errorInicio = "No se pudo iniciar BoxView. Inténtalo nuevamente."
                }
            } finally {
                preparado = true
            }
        }
    }
    fun ingresar(correo: String, clave: String, resultado: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            if (!preparado || errorInicio.isNotBlank()) { resultado(false, errorInicio.ifBlank { "Iniciando BoxView..." }); return@launch }
            try {
                val user = autenticar(correo, clave)
                sesion = user
                bodegaActiva = null
                resultado(user != null, if (user == null) "Correo o contraseña incorrectos, o cuenta inactiva" else "")
            } catch (e: Exception) {
                Log.e("BoxViewAcceso", "Error al consultar usuario o validar credenciales", e)
                resultado(false, "No se pudo comprobar el acceso. Inténtalo nuevamente.")
            }
        }
    }
    fun elegirBodega(bodega: Bodega, resultado: (Boolean, String) -> Unit) {
        val user = sesion ?: return resultado(false, "Debes iniciar sesión")
        viewModelScope.launch {
            if (dao.accesoBodega(user.id, bodega.id) == 0) resultado(false, "Bodega no autorizada")
            else { bodegaActiva = bodega; cajaActivaId = null; resultado(true, "") }
        }
    }
    fun cerrarSesion() { sesion = null; bodegaActiva = null; codigoEscaneado = ""; cajaActivaId = null; modoCaja = "INGRESO" }
    // Datos consultados para las pantallas.
    fun bodegasAutorizadas(): Flow<List<Bodega>> = dao.bodegasAutorizadas(sesion?.id ?: -1)
    fun sucursalesAdministrador(): Flow<List<Sucursal>> = dao.sucursalesAdministradas(sesion?.id ?: -1)
    fun permisoAdministrador(uid: Long, sid: Long): Flow<Int> = dao.observarAdministrador(uid, sid)
    fun minimosSucursal(sid: Long): Flow<List<MinimoSucursal>> = dao.minimosSucursal(sid)
    fun configuracionSucursal(sid: Long): Flow<ConfiguracionSucursal?> = dao.configuracionSucursal(sid)
    fun permisoBodegaActivo(uid: Long, bid: Long, rol: String): Flow<Int> =
        dao.observarAccesoBodega(uid, bid, rol)
    fun permisoTiendaActivo(uid: Long, sid: Long, rol: String): Flow<Int> =
        dao.observarAccesoTienda(uid, sid, rol)
    val bodegas = dao.todasBodegas()
    val sucursales = dao.sucursales()
    // Catalogo que consultan las pantallas.
    val productos = dao.productos()
    fun posiciones() = dao.posiciones(bodegaActiva?.id ?: -1)
    fun campaniasBodega() = db.campaniasMensualesDao().listarPorUbicacion("BODEGA", bodegaActiva?.id ?: -1)
    // Existencias de la bodega activa.
    fun existencias() = dao.existencias(bodegaActiva?.id ?: -1)
    fun movimientos() = dao.movimientos(sesion?.id ?: -1, bodegaActiva?.id ?: -1)
    fun periodoInventario() = dao.periodoInventario(sesion?.id ?: -1, bodegaActiva?.id ?: -1)
    fun lineasInventario(periodo: Long) = dao.lineasInventario(periodo)
    fun cajasIngreso() = dao.cajasIngreso(bodegaActiva?.id ?: -1, sesion?.id ?: -1)
    fun ordenesSalida() = dao.ordenesSalida(sesion?.id ?: -1, bodegaActiva?.id ?: -1)
    fun salidasEnviadas() = dao.salidasEnviadas(sesion?.id ?: -1, bodegaActiva?.id ?: -1)
    fun cajaActiva() = dao.observarCaja(cajaActivaId ?: -1)
    fun detallesCajaActiva() = dao.observarDetallesCaja(cajaActivaId ?: -1)
    fun preparacionesCajaActiva() = dao.observarPreparacionesCaja(cajaActivaId ?: -1)
    fun compromisosSalida() = dao.compromisosSalida(bodegaActiva?.id ?: -1)
    fun reservasOtrasCajas(excluirCaja: Long = 0) = dao.reservasOtrasCajas(bodegaActiva?.id ?: -1, excluirCaja)
    fun borradorInventario(periodoId: Long) = dao.borradorInventario(periodoId)
    fun zonasAsignadasInventario(periodoId: Long) = dao.zonasPeriodo(periodoId)
    fun referenciasInventario(inventarioId: Long) = dao.observarReferenciasInventario(inventarioId)

    fun abrirCaja(codigo: String, tipo: String, resultado: (Boolean, String) -> Unit) {
        val usuario = sesion ?: return resultado(false, "Inicia sesión")
        val bodega = bodegaActiva?.id ?: return resultado(false, "Selecciona una bodega")
        viewModelScope.launch {
            try {
                val caja = dao.cajaPorCodigo(codigo.trim()) ?: error("Caja no registrada")
                require(dao.autorizado(usuario.id, bodega) > 0) { "Bodega no autorizada" }
                val ingreso = tipo == "INGRESO"
                val consulta = tipo == "CONSULTA_SALIDA"
                require(if (ingreso) caja.tipo == "INGRESO" else caja.tipo == "SALIDA") { "El código no corresponde a esta operación" }
                require(if (ingreso) caja.bodegaDestinoId == bodega && caja.asignadoA == usuario.id
                    else caja.bodegaOrigenId == bodega && caja.asignadoA == usuario.id) {
                    "La caja no corresponde a la bodega o al operador seleccionado"
                }
                if (!ingreso && !consulta) require(caja.sucursalDestinoId != null) { "La salida no tiene tienda de destino" }
                require(if (ingreso) caja.estado in listOf("ESPERADA", "EN_TRANSITO", "EN_REVISION")
                        else if (consulta) caja.estado in listOf("EN_TRANSITO", "EN_REVISION", "RECIBIDA", "CON_DIFERENCIA")
                        else caja.estado == "ORDEN_ASIGNADA") {
                    "La caja ya fue procesada o no está disponible"
                }
                if (ingreso) {
                    require(dao.detallesCaja(caja.id).isNotEmpty()) { "La caja no contiene productos" }
                    // Al identificar la caja por QR (o por código manual de respaldo),
                    // se registra su llegada. La comprobación y el stock son posteriores.
                    if (caja.estado != "EN_REVISION") {
                        db.withTransaction {
                            require(dao.autorizado(usuario.id, bodega) > 0) { "Bodega no autorizada" }
                            require(dao.registrarLlegadaBodega(caja.id, bodega, usuario.id) == 1) {
                                "La caja cambió de estado. Actualiza la lista e inténtalo nuevamente"
                            }
                            dao.guardarEventoCaja(EventoCaja(cajaId = caja.id, usuarioId = usuario.id,
                                accion = "LLEGADA_BODEGA", observacion = "Llegada registrada en ${bodegaActiva?.nombre.orEmpty()}"))
                        }
                    }
                }
                cajaActivaId = caja.id
                modoCaja = tipo
                resultado(true, "")
            } catch (e: Exception) { resultado(false, mensajeErrorUsuario(e, "No se pudo abrir la caja")) }
        }
    }

    // registro de ingresos
    fun guardarAvanceCaja(cantidades: Map<Long, Int>, posiciones: Map<Long, Long>, resultado: (Boolean, String) -> Unit) =
        operarIngreso(cantidades, posiciones, false, resultado)

    fun confirmarCaja(cantidades: Map<Long, Int>, posiciones: Map<Long, Long>, resultado: (Boolean, String) -> Unit) =
        operarIngreso(cantidades, posiciones, true, resultado)

    private fun operarIngreso(cantidades: Map<Long, Int>, posiciones: Map<Long, Long>, confirmar: Boolean,
                             resultado: (Boolean, String) -> Unit) {
        val usuario = sesion ?: return resultado(false, "Inicia sesión")
        val bodega = bodegaActiva?.id ?: return resultado(false, "Selecciona bodega")
        val id = cajaActivaId ?: return resultado(false, "Selecciona una caja")
        if (modoCaja != "INGRESO") return resultado(false, "La caja no está en modo recepción")
        viewModelScope.launch {
            try {
                db.withTransaction {
                    require(dao.autorizado(usuario.id, bodega) > 0) { "Sin permiso en esta bodega" }
                    val caja = dao.cajaPorId(id) ?: error("Caja inexistente")
                    require(caja.bodegaDestinoId == bodega && caja.asignadoA == usuario.id &&
                        caja.estado == "EN_REVISION" && caja.tipo == "INGRESO") {
                        "Registra primero la llegada de la caja por QR o código manual"
                    }
                    val detalles = dao.detallesCaja(id)
                    require(detalles.isNotEmpty()) { "Caja sin productos" }
                    if (confirmar) require(cantidades.keys == detalles.map { it.id }.toSet()) {
                        "Debes contar expresamente todas las líneas (cero también se registra)"
                    }
                    val campañaEnCurso = db.campaniasMensualesDao().campañaActiva("BODEGA", bodega) > 0
                    val recepcion = if (campañaEnCurso) dao.posicionesActuales(bodega).firstOrNull {
                        it.tipo == "RECEPCION" && it.activa
                    } ?: error("Esta bodega no tiene zona de recepción") else null
                    detalles.forEach { d ->
                        val unidades = cantidades[d.id] ?: if (confirmar) error("Falta un conteo") else 0
                        val posicion = recepcion?.id ?: posiciones[d.id] ?: error("Indica una posición por producto")
                        require(unidades in 0..100000) { "Cantidad de recepción fuera de rango" }
                        require(dao.posicionPorId(posicion)?.bodegaId == bodega) { "Posición fuera de la bodega" }
                        val categoria = dao.productoPorId(d.productoId)?.categoria ?: error("Producto no encontrado")
                        require(posicionCompatibleBodega(dao, dao.posicionPorId(posicion)!!, categoria)) {
                            "Elige un estante de $categoria o Reserva para recibir este producto"
                        }
                        if (confirmar) require(dao.posicionBloqueada(posicion) == 0 &&
                            dao.posicionCongeladaPorCampania(posicion) == 0) { "Estante bloqueado por inventario mensual" }
                        dao.actualizarDetalleCaja(d.copy(cantidadContadaFisica = unidades,
                            cantidadRecibida = if (confirmar) minOf(unidades, d.cantidadEsperada) else 0,
                            posicionDestinoId = posicion))
                    }
                    if (confirmar) {
                        val diferencia = detalles.any { (cantidades[it.id] ?: 0) != it.cantidadEsperada }
                        detalles.forEach { d ->
                            val contadas = cantidades[d.id] ?: 0
                            val posicion = recepcion?.id ?: posiciones.getValue(d.id)
                            val aceptadas = minOf(contadas, d.cantidadEsperada)
                            if (contadas != d.cantidadEsperada) dao.guardarIncidenciaCaja(
                                IncidenciaCaja(cajaId = id, productoId = d.productoId,
                                    esperado = d.cantidadEsperada, contado = contadas))
                            if (aceptadas > 0) {
                                val actual = dao.existencia(d.productoId, posicion)
                                if (actual == null) dao.guardarExistencia(
                                    Existencia(productoId = d.productoId, posicionId = posicion, cantidad = aceptadas))
                                else dao.sumar(actual.id, aceptadas)
                                dao.guardarMovimiento(Movimiento(usuarioId = usuario.id, bodegaId = bodega,
                                    productoId = d.productoId, posicionOrigen = 0, posicionDestino = posicion,
                                    cantidad = aceptadas, tipo = "RECEPCION_CAJA"))
                            }
                        }
                        dao.actualizarCaja(caja.copy(estado = if (diferencia) "CON_DIFERENCIA" else "RECIBIDA"))
                        // finalizar la solicitud externa al recibir la caja
                        val externas = db.abastecimientoExternoDao()
                        val solicitudExterna = externas.solicitudPorCaja(id)
                        if (solicitudExterna != null) {
                            val estadoExterno = if (diferencia) "CON_DIFERENCIA" else "RECIBIDA"
                            if (externas.completarEntrega(id, estadoExterno) == 1) {
                                externas.nuevoEvento(EventoSolicitudExterna(solicitudId = solicitudExterna.id,
                                    usuarioId = usuario.id, accion = estadoExterno,
                                    detalle = "Recepción de ${caja.codigo}"))
                            }
                        }
                        dao.guardarEventoCaja(EventoCaja(cajaId = id, usuarioId = usuario.id,
                            accion = if (diferencia) "RECEPCION_CON_DIFERENCIA" else "RECEPCION_CONFIRMADA"))
                    } else {
                        // Guardar un avance no sustituye el registro de llegada.
                        dao.guardarEventoCaja(EventoCaja(cajaId = id, usuarioId = usuario.id, accion = "AVANCE_RECEPCION"))
                    }
                }
                resultado(true, if (confirmar) "Recepción registrada" else "Avance guardado")
            } catch (e: Exception) { resultado(false, mensajeErrorUsuario(e, "No se pudo guardar")) }
        }
    }

    // preparar orden de salida
    data class RegistroPreparacion(val detalleId: Long, val posicionId: Long, val cantidad: Int)

    fun guardarAvanceSalida(lineas: List<RegistroPreparacion>, resultado: (Boolean, String) -> Unit) =
        operarSalida(lineas, false, resultado)

    fun confirmarSalida(lineas: List<RegistroPreparacion>, resultado: (Boolean, String) -> Unit) =
        operarSalida(lineas, true, resultado)

    private fun operarSalida(lineas: List<RegistroPreparacion>, confirmar: Boolean,
                            resultado: (Boolean, String) -> Unit) {
        val usuario = sesion ?: return resultado(false, "Inicia sesión")
        val bodega = bodegaActiva?.id ?: return resultado(false, "Selecciona bodega")
        val id = cajaActivaId ?: return resultado(false, "Selecciona una caja")
        if (modoCaja != "SALIDA") return resultado(false, "La caja no está en preparación")
        viewModelScope.launch {
            try {
                db.withTransaction {
                    require(dao.autorizado(usuario.id, bodega) > 0) { "Sin permiso en esta bodega" }
                    val caja = dao.cajaPorId(id) ?: error("Caja inexistente")
                    require(caja.tipo == "SALIDA" && caja.bodegaOrigenId == bodega &&
                        caja.asignadoA == usuario.id && caja.estado == "ORDEN_ASIGNADA") {
                        "Esta orden ya fue enviada o no está asignada a ti"
                    }
                    val tiendaId = caja.sucursalDestinoId ?: error("La orden no tiene tienda de destino")
                    require(dao.existeSucursal(tiendaId) == 1 && dao.abasteceSucursal(bodega, tiendaId) == 1) {
                        "La tienda de destino no está autorizada para esta bodega"
                    }
                    val detalles = dao.detallesCaja(id)
                    require(detalles.isNotEmpty()) { "Orden sin productos" }
                    val ids = detalles.associateBy { it.id }
                    require(lineas.all { it.cantidad in 0..100000 && it.detalleId in ids }) { "Preparación no válida" }
                    val registradas = lineas.filter { it.cantidad > 0 }
                    require(registradas.map { it.detalleId to it.posicionId }.distinct().size == registradas.size) {
                        "No repitas un mismo producto y estante"
                    }
                    val porDetalle = registradas.groupBy { it.detalleId }
                    detalles.forEach { d ->
                        val total = porDetalle[d.id].orEmpty().sumOf { it.cantidad }
                        require(total <= d.cantidadEsperada) { "La preparación excede la orden" }
                        if (confirmar) require(total == d.cantidadEsperada) {
                            "Completa todas las unidades de ${d.productoId} antes de despachar"
                        }
                    }
                    registradas.forEach { fila ->
                        require(dao.posicionBloqueada(fila.posicionId) == 0 && dao.posicionCongeladaPorCampania(fila.posicionId) == 0) {
                            "Este estante está congelado hasta finalizar la campaña mensual"
                        }
                        require(dao.posicionPorId(fila.posicionId)?.bodegaId == bodega) {
                            "Una posición no corresponde a la bodega"
                        }
                    }
                    // validacion del stock reservado
                    val agrupadas = registradas.groupBy { ids.getValue(it.detalleId).productoId to it.posicionId }
                    agrupadas.forEach { (clave, filas) ->
                        val (producto, posicion) = clave
                        val stock = dao.existencia(producto, posicion)?.cantidad ?: 0
                        val reservadoOtros = dao.stockReservadoCajas(producto, posicion, id)
                        require(stock - reservadoOtros >= filas.sumOf { it.cantidad }) {
                            "Stock insuficiente: hay unidades preparadas para otra orden"
                        }
                    }
                    dao.borrarPreparacionesCaja(id)
                    registradas.forEach { fila ->
                        dao.guardarPreparacionCaja(PreparacionCaja(cajaId = id,
                            productoId = ids.getValue(fila.detalleId).productoId,
                            posicionId = fila.posicionId, cantidad = fila.cantidad))
                    }
                    detalles.forEach { d ->
                        val asignadas = porDetalle[d.id].orEmpty()
                        dao.actualizarDetalleCaja(d.copy(cantidadVerificada = asignadas.sumOf { it.cantidad },
                            posicionId = asignadas.firstOrNull()?.posicionId))
                    }
                    if (confirmar) {
                        // queda reservado hasta la revision del jefe
                        dao.actualizarCaja(caja.copy(estado = "PENDIENTE_REVISION", preparadoPor = usuario.id,
                            revisorId = null))
                        dao.guardarEventoCaja(EventoCaja(cajaId = id, usuarioId = usuario.id,
                            accion = "PREPARACION_TERMINADA", observacion = "Pendiente de revisión de jefatura"))
                    } else {
                        if (caja.estado != "ORDEN_ASIGNADA") dao.actualizarCaja(caja.copy(estado = "ORDEN_ASIGNADA"))
                        dao.guardarEventoCaja(EventoCaja(cajaId = id, usuarioId = usuario.id,
                            accion = "AVANCE_PREPARACION"))
                    }
                }
                resultado(true, if (confirmar) "Preparación enviada a revisión de jefatura" else "Avance guardado")
            } catch (e: Exception) { resultado(false, mensajeErrorUsuario(e, "No se pudo preparar la salida")) }
        }
    }

    // Control comun de sesion y resultado.
    private fun ejecutar(tarea: suspend (Long, Long) -> Unit, resultado: (Boolean, String) -> Unit) {
        val user = sesion ?: return resultado(false, "No has iniciado sesión")
        val bid = bodegaActiva?.id ?: return resultado(false, "Elige una bodega")
        viewModelScope.launch {
            try { tarea(user.id, bid); resultado(true, "Guardado correctamente") }
            catch (e: Exception) { resultado(false, mensajeErrorUsuario(e, "Error al guardar")) }
        }
    }
    // Entrada de la solicitud de traslado.
    fun trasladar(producto: Long, origen: Long, destino: Long, cantidad: Int, resultado: (Boolean, String) -> Unit) =
        ejecutar({ uid, bid -> registrarTraslado(uid, bid, producto, origen, destino, cantidad) }, resultado)

    private fun hash(clave: String, sal: ByteArray): String = hashClave(clave, sal)

    private suspend fun inicializar() {
        // Primera transacción: acceso garantizado aunque la demo de inventario falle.
        db.withTransaction {
            if (dao.numeroUsuarios() == 0) {
                asegurarAccesoBasicoTrasFalloInicial(dao, ::hash)
            }
        }
        // Segunda transacción: catálogo y operaciones. Reintentable en próximas aperturas
        // SOLO cuando el catálogo demo sigue vacío, sin sobrescribir operaciones existentes.
        db.withTransaction {
            cargarDatosIniciales(dao, db.preciosVentaDao(), db.abastecimientoExternoDao(),
                db.operacionesTiendaDao(), db.informesGerenciaDao(), ::hash)
        }
    }

    // estantes verificados vacíos, asociados a la tarea del operador.
    fun estantesVaciosInventario(periodoId: Long): Flow<List<VerificacionEstanteVacio>> =
        dao.estantesVacios("BODEGA", periodoId)

    fun confirmarEstanteVacioInventario(periodoId: Long, posicionId: Long,
                                        resultado: (Boolean, String) -> Unit) {
        val usuario = sesion ?: return resultado(false, "Inicia sesión")
        val bodega = bodegaActiva ?: return resultado(false, "Selecciona una bodega")
        if (usuario.rol != "OPERADOR_BODEGA") return resultado(false, "Solo el operador puede confirmar estantes")
        viewModelScope.launch {
            try {
                db.withTransaction {
                    validarInventario(usuario.id, bodega.id, periodoId, emptyList(), permitirVacio = true)
                    require(posicionId in dao.zonasPeriodoActuales(periodoId)) { "Estante no asignado" }
                    val general = dao.periodoPorId(periodoId)?.inventarioBodegaId ?: error("Inventario no disponible")
                    dao.borrarBorradorEstante(periodoId, posicionId)
                    dao.referenciasInventario(general).filter { it.posicionId == posicionId }
                        .forEach { ref ->
                            dao.guardarFilaBorrador(BorradorInventario(periodoId = periodoId,
                                productoId = ref.productoId, posicionId = posicionId, cantidad = 0))
                        }
                    dao.guardarEstanteVacio(VerificacionEstanteVacio("BODEGA", periodoId,
                        posicionId, usuario.id))
                    dao.guardarEventoAdministrativo(EventoAdministrativo(sucursalId = bodega.sucursalId,
                        usuarioId = usuario.id, accion = "ESTANTE_VACIO_BODEGA",
                        detalle = "Inventario $general; tarea $periodoId; estante $posicionId; revisión física sin productos"))
                }
                resultado(true, "Estante vacío confirmado. Jefatura revisará las diferencias")
            } catch (e: Exception) { resultado(false, mensajeErrorUsuario(e, "No se pudo confirmar el estante")) }
        }
    }

    fun reabrirEstanteVacioInventario(periodoId: Long, posicionId: Long,
                                      resultado: (Boolean, String) -> Unit) {
        val usuario = sesion ?: return resultado(false, "Inicia sesión")
        val bodega = bodegaActiva ?: return resultado(false, "Selecciona una bodega")
        if (usuario.rol != "OPERADOR_BODEGA") return resultado(false, "Acceso de operador requerido")
        viewModelScope.launch {
            try {
                db.withTransaction {
                    validarInventario(usuario.id, bodega.id, periodoId, emptyList(), permitirVacio = true)
                    require(dao.quitarEstanteVacio("BODEGA", periodoId, posicionId) == 1) { "El estante no estaba marcado vacío" }
                    dao.guardarEventoAdministrativo(EventoAdministrativo(sucursalId = bodega.sucursalId,
                        usuarioId = usuario.id, accion = "REABRIR_ESTANTE_BODEGA",
                        detalle = "Tarea $periodoId; estante $posicionId; se vuelve al conteo manual"))
                }
                resultado(true, "Puedes volver a registrar productos en este estante")
            } catch (e: Exception) { resultado(false, mensajeErrorUsuario(e, "No se pudo reabrir el estante")) }
        }
    }

    // registro del inventario
    data class RegistroInventario(val productoId: Long, val posicionId: Long, val cantidad: Int)

    fun guardarBorradorInventario(periodoId: Long, lineas: List<RegistroInventario>,
                                resultado: (Boolean, String) -> Unit) {
        val usuario = sesion ?: return resultado(false, "Inicia sesión")
        val bodega = bodegaActiva?.id ?: return resultado(false, "Selecciona una bodega")
        viewModelScope.launch {
            try {
                db.withTransaction {
                    validarInventario(usuario.id, bodega, periodoId, lineas, permitirVacio = true)
                    val vacios = dao.estantesVaciosActuales("BODEGA", periodoId).map { it.posicionId }.toSet()
                    require(lineas.none { it.posicionId in vacios && it.cantidad != 0 }) {
                        "Un estante confirmado vacío no puede contener cantidades positivas"
                    }
                    dao.borrarBorradorInventario(periodoId)
                    lineas.forEach { l ->
                        dao.guardarFilaBorrador(BorradorInventario(periodoId = periodoId,
                            productoId = l.productoId, posicionId = l.posicionId, cantidad = l.cantidad))
                    }
                }
                resultado(true, "Borrador guardado correctamente")
            } catch (e: Exception) { resultado(false, mensajeErrorUsuario(e, "No se pudo guardar borrador")) }
        }
    }

    private suspend fun validarInventario(uid: Long, bid: Long, periodoId: Long,
                                          lineas: List<RegistroInventario>, permitirVacio: Boolean) {
        require(dao.autorizado(uid, bid) > 0) { "Bodega no autorizada" }
        val periodo = dao.periodoPorId(periodoId) ?: error("Inventario no encontrado")
        require(periodo.estado == "ABIERTO" && periodo.bodegaId == bid && periodo.asignadoA == uid) {
            "No hay un inventario mensual abierto para ti"
        }
        val general = periodo.inventarioBodegaId ?: error("Inventario anterior: solicita a jefatura abrir uno nuevo por zonas")
        require(dao.inventarioBodegaPorId(general)?.estado == "ABIERTO") { "El inventario no está abierto" }
        val zonas = dao.zonasPeriodoActuales(periodoId).toSet()
        require(zonas.isNotEmpty()) { "No hay estanterías asignadas" }
        require(lineas.all { it.posicionId in zonas }) { "El conteo incluye una estantería sin asignación" }
        if (!permitirVacio) {
            val referencia = dao.referenciasInventario(general).filter { it.posicionId in zonas }
            val registradas = lineas.map { it.productoId to it.posicionId }.toSet()
            require(referencia.all { (it.productoId to it.posicionId) in registradas }) {
                "Faltan productos del stock inicial. Regístralos, incluso si físicamente encontraste cero"
            }
        }
        require(lineas.distinctBy { it.productoId to it.posicionId }.size == lineas.size) {
            "Hay productos y posiciones repetidos"
        }
        lineas.forEach { l ->
            require(l.cantidad in 0..100000) { "Cantidad de inventario fuera de rango" }
            require(dao.productoPorId(l.productoId) != null) { "Producto no registrado" }
            require(dao.posicionPorId(l.posicionId)?.bodegaId == bid) {
                "Selecciona una posición válida para cada producto"
            }
        }
    }

    fun guardarInventarioMensual(periodoId: Long, lineas: List<RegistroInventario>, revisionFisica: Boolean,
                                resultado: (Boolean, String) -> Unit) {
        val usuario = sesion ?: return resultado(false, "Inicia sesión")
        val bodega = bodegaActiva?.id ?: return resultado(false, "Selecciona una bodega")
        viewModelScope.launch {
            try {
                db.withTransaction {
                    validarInventario(usuario.id, bodega, periodoId, lineas, permitirVacio = false)
                    val zonas = dao.zonasPeriodoActuales(periodoId).toSet()
                    val vacios = dao.estantesVaciosActuales("BODEGA", periodoId).map { it.posicionId }.toSet()
                    require(vacios.all { it in zonas }) { "Hay verificaciones fuera de la tarea" }
                    require(lineas.none { it.posicionId in vacios && it.cantidad != 0 }) {
                        "Hay unidades positivas en un estante confirmado vacío"
                    }
                    require(zonas.all { z -> z in vacios || lineas.any { it.posicionId == z } }) {
                        "Marca los estantes sin productos como vacíos o registra sus conteos"
                    }
                    require(revisionFisica) { "Confirme que comprobó físicamente los estantes asignados" }
                    require(dao.guardarVerificacionFisica(periodoId, usuario.id, bodega,
                        System.currentTimeMillis()) == 1) { "No se pudo registrar la verificación física" }
                    require(dao.entregarPeriodo(periodoId, usuario.id, bodega) == 1) { "Este inventario ya fue entregado" }
                    lineas.forEach { linea ->
                        val saldoAlEntregar = dao.referenciasInventario(
                            dao.periodoPorId(periodoId)!!.inventarioBodegaId!!)
                            .firstOrNull { it.productoId == linea.productoId && it.posicionId == linea.posicionId }?.cantidad ?: 0
                        require((dao.existencia(linea.productoId, linea.posicionId)?.cantidad ?: 0) == saldoAlEntregar) {
                            "El stock cambió desde la apertura: informa al jefe y repite el conteo"
                        }
                        dao.guardarLineaInventario(LineaInventario(periodoId = periodoId,
                            productoId = linea.productoId, posicionId = linea.posicionId,
                            cantidadContada = linea.cantidad, stockAlEntregar = saldoAlEntregar))
                    }
                    dao.borrarBorradorInventario(periodoId)
                    val general = dao.periodoPorId(periodoId)!!.inventarioBodegaId!!
                    if (dao.tareasSinEntregar(general) == 0) dao.marcarInventarioParaRevision(general)
                }
                resultado(true, "Conteo entregado a jefatura")
            } catch (e: Exception) { resultado(false, mensajeErrorUsuario(e, "No se pudo guardar el inventario")) }
        }
    }

    private suspend fun autenticar(correo: String, clave: String): Usuario? {
        val usuario = dao.usuarioPorCorreo(correo.trim().lowercase()) ?: run {
            Log.w("BoxViewAcceso", "Intento de acceso: correo no encontrado en la base instalada")
            return null
        }
        if (!usuario.activo || usuario.rol !in listOf("OPERADOR_BODEGA", "JEFE_BODEGA", "TRABAJADOR_TIENDA", "JEFE_TIENDA", "ADMIN_SUCURSAL", "GERENTE")) {
            Log.w("BoxViewAcceso", "Intento de acceso: usuario inactivo o rol no permitido")
            return null
        }
        // comprobar contraseña
        val coincide = withContext(Dispatchers.Default) {
            val hashEsperado = hash(clave, Base64.decode(usuario.claveSal, Base64.NO_WRAP))
            MessageDigest.isEqual(hashEsperado.toByteArray(), usuario.claveHash.toByteArray())
        }
        if (!coincide) Log.w("BoxViewAcceso", "Intento de acceso: contraseña distinta de la almacenada")
        return if (coincide) usuario else null
    }

    // Reglas y cambios del traslado.
    private suspend fun registrarTraslado(usuario: Long, bodega: Long, producto: Long, origen: Long, destino: Long, cantidad: Int) = db.withTransaction {
        require(cantidad > 0) { "La cantidad debe ser mayor a cero" }
        require(origen != destino) { "Selecciona una posición de destino diferente" }
        require(dao.autorizado(usuario, bodega) > 0) { "Acceso no autorizado a esta bodega" }
        require(dao.posicionPorId(origen)?.bodegaId == bodega && dao.posicionPorId(destino)?.bodegaId == bodega) {
            "El traslado debe ocurrir dentro de la bodega seleccionada"
        }
        require(dao.posicionBloqueada(origen) == 0 && dao.posicionBloqueada(destino) == 0 &&
            dao.posicionCongeladaPorCampania(origen) == 0 && dao.posicionCongeladaPorCampania(destino) == 0) {
            "Origen o destino bloqueado por inventario mensual"
        }
        val productoActual = dao.productoPorId(producto) ?: error("Producto no registrado")
        val estanteDestino = dao.posicionPorId(destino) ?: error("Estante de destino inexistente")
        require(posicionCompatibleBodega(dao, estanteDestino, productoActual.categoria)) {
            "Destino de categoría incorrecta: elige un estante de ${productoActual.categoria} o Reserva"
        }
        val actual = dao.existencia(producto, origen) ?: error("No hay stock de ese producto en el origen")
        val reservado = dao.stockReservadoCajas(producto, origen, 0)
        require(actual.cantidad - reservado >= cantidad) { "Hay unidades reservadas en preparaciones pendientes" }
        // Comprueba que pueda descontarse el stock.
        require(dao.restarSiAlcanza(actual.id, cantidad) == 1) { "Stock insuficiente" }
        val destinoActual = dao.existencia(producto, destino)
        if (destinoActual == null) dao.guardarExistencia(Existencia(productoId = producto, posicionId = destino, cantidad = cantidad))
        else dao.sumar(destinoActual.id, cantidad)
        // Registro del movimiento realizado.
        dao.guardarMovimiento(Movimiento(usuarioId = usuario, bodegaId = bodega, productoId = producto,
            posicionOrigen = origen, posicionDestino = destino, cantidad = cantidad))
    }

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = OperadorViewModel(app) as T
    }
}
