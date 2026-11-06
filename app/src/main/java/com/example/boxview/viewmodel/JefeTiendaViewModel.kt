package com.example.boxview.viewmodel

// Lógica: Reposición y acciones del jefe de tienda.
import com.example.boxview.mensajeErrorUsuario
import android.app.Application
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.example.boxview.modelo.*
import java.security.SecureRandom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JefeTiendaViewModel(app: Application) : AndroidViewModel(app) {
    // Conexion con la capa de datos.
    private val db = AppDatabase.getDatabase(app)
    private val dao = db.inventarioDao()
    private val operacionesTienda = db.operacionesTiendaDao()

    // estados de los formularios
    private val _reposicion = MutableStateFlow(FormularioReposicionTiendaModel())
    val reposicion: StateFlow<FormularioReposicionTiendaModel> = _reposicion.asStateFlow()
    private val _trabajador = MutableStateFlow(FormularioTrabajadorTiendaModel())
    val trabajador: StateFlow<FormularioTrabajadorTiendaModel> = _trabajador.asStateFlow()

    fun cambiarBodega(id: Long?) { _reposicion.update { it.copy(bodegaId = id) } }
    fun cambiarProducto(id: Long?) { _reposicion.update { it.copy(productoId = id) } }
    fun cambiarCantidad(texto: String) {
        if (texto.isEmpty() || (texto.all(Char::isDigit) && texto.length <= 6))
            _reposicion.update { it.copy(cantidad = texto) }
    }
    fun agregarLineaReposicion(productoId: Long, cantidad: Int) {
        if (cantidad !in 1..100000) return
        _reposicion.update { actual ->
            if (actual.lineas.any { it.productoId == productoId }) actual
            else actual.copy(lineas = actual.lineas + LineaReposicionTiendaModel(productoId, cantidad.toString()))
        }
    }
    fun cargarSugerencias(sugerencias: List<Pair<Long, Int>>) {
        _reposicion.update { actual ->
            val nuevas = sugerencias.filter { (pid, n) ->
                n in 1..100000 && actual.lineas.none { it.productoId == pid }
            }.map { (pid, n) -> LineaReposicionTiendaModel(pid, n.toString()) }
            actual.copy(lineas = actual.lineas + nuevas)
        }
    }
    fun cambiarCantidadLinea(productoId: Long, texto: String) {
        if (texto.isNotEmpty() && (!texto.all(Char::isDigit) || texto.length > 6)) return
        _reposicion.update { actual ->
            actual.copy(lineas = actual.lineas.map {
                if (it.productoId == productoId) it.copy(cantidad = texto) else it
            })
        }
    }
    fun quitarLineaReposicion(productoId: Long) {
        _reposicion.update { actual -> actual.copy(lineas = actual.lineas.filterNot { it.productoId == productoId }) }
    }
    fun reiniciarReposicion() { _reposicion.value = FormularioReposicionTiendaModel() }
    fun cambiarNombre(texto: String) { _trabajador.update { it.copy(nombre = texto) } }
    fun cambiarCorreo(texto: String) { _trabajador.update { it.copy(correo = texto) } }
    fun cambiarRut(texto: String) { _trabajador.update { it.copy(rut = texto) } }
    fun cambiarClave(texto: String) { _trabajador.update { it.copy(clave = texto) } }
    fun reiniciarTrabajador() { _trabajador.value = FormularioTrabajadorTiendaModel() }
    fun cerrarSesion() { reiniciarReposicion(); reiniciarTrabajador() }

    // Solicitudes observadas por la jefatura.
    fun solicitudes(sid: Long): Flow<List<SolicitudReposicion>> = dao.solicitudesDeTienda(sid)
    fun productosEnReposicion(sid: Long): Flow<List<Long>> = dao.productosEnReposicion(sid)
    fun bodegas(sid: Long): Flow<List<Bodega>> = dao.bodegasDeTienda(sid)
    fun equipo(sid: Long): Flow<List<Usuario>> = dao.trabajadoresDeTienda(sid)
    fun trabajadores(): Flow<List<Usuario>> = dao.trabajadoresSinAsignacion()

    private suspend fun comprobarJefe(usuario: Usuario, sid: Long) {
        require(usuario.rol == "JEFE_TIENDA" && dao.jefeAutorizadoTienda(usuario.id, sid) == 1) {
            "Acceso no autorizado a esta sucursal"
        }
    }

    private fun realizar(operacion: suspend () -> String, respuesta: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try { respuesta(true, operacion()) }
            catch (e: Exception) { respuesta(false, mensajeErrorUsuario(e, "No se pudo completar la operación")) }
        }
    }

    // solicitudes de reposicion
    fun solicitarReposicion(jefe: Usuario, sid: Long, bodegaId: Long, productoId: Long, cantidad: Int,
                           respuesta: (Boolean, String) -> Unit) =
        solicitarPedido(jefe, sid, bodegaId, listOf(LineaReposicionTiendaModel(productoId, cantidad.toString())), respuesta)

    fun solicitarPedido(jefe: Usuario, sid: Long, bodegaId: Long,
                       lineas: List<LineaReposicionTiendaModel>,
                       respuesta: (Boolean, String) -> Unit) = realizar({
        // Operacion agrupada en Room.
        db.withTransaction {
            comprobarJefe(jefe, sid)
            require(dao.abasteceSucursal(bodegaId, sid) == 1) { "La bodega no pertenece a esta sucursal" }
            require(lineas.isNotEmpty() && lineas.size <= 100) { "Selecciona entre 1 y 100 productos" }
            require(lineas.distinctBy { it.productoId }.size == lineas.size) { "Hay productos repetidos" }
            val cantidades = lineas.map { it.productoId to (it.cantidad.toIntOrNull() ?: 0) }
            for ((pid, cantidad) in cantidades) {
                require(cantidad in 1..100000) { "Las cantidades deben estar entre 1 y 100000" }
                require(dao.productoPorId(pid) != null) { "Producto no registrado" }
                require(dao.solicitudPendiente(sid, pid) == 0) {
                    "Ya hay una reposición en curso para uno de los productos"
                }
            }
            val lote = java.util.UUID.randomUUID().toString()
            cantidades.forEach { (pid, cantidad) ->
                dao.guardarSolicitud(SolicitudReposicion(sucursalId = sid, bodegaId = bodegaId,
                    productoId = pid, cantidad = cantidad, solicitadoPor = jefe.id, loteId = lote))
            }
            "Pedido registrado: ${cantidades.size} producto(s). Pendiente de preparación en bodega."
        }
    }, respuesta)

    fun cancelarReposicion(jefe: Usuario, sid: Long, id: Long, respuesta: (Boolean, String) -> Unit) = realizar({
        db.withTransaction {
            comprobarJefe(jefe, sid)
            val solicitud = dao.solicitudPorId(id) ?: error("Solicitud inexistente")
            require(solicitud.sucursalId == sid && solicitud.estado == "PENDIENTE") { "La solicitud ya fue atendida o cancelada" }
            if (solicitud.loteId != null) {
                require(dao.cancelarGrupo(sid, solicitud.loteId) > 0) { "No se pudo cancelar el pedido" }
            } else {
                require(dao.cancelarSolicitud(id, sid) == 1) { "No se pudo cancelar la solicitud" }
            }
            "Pedido cancelado"
        }
    }, respuesta)

    // administracion de trabajadores
    fun crearTrabajador(jefe: Usuario, sid: Long, nombre: String, correo: String, rut: String, clave: String,
                       respuesta: (Boolean, String) -> Unit) = realizar({
        val limpio = correo.trim().lowercase()
        require(nombre.trim().length >= 3) { "Escribe el nombre completo" }
        require(limpio.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))) { "Correo no válido" }
        require(rutValido(rut)) { "RUT no válido" }
        require(clave.length >= 8) { "La contraseña necesita al menos 8 caracteres" }
        val rutNormalizado = rut.replace(".", "").replace("-", "").trim().uppercase()
        val sal = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val hash = withContext(Dispatchers.Default) { hashClave(clave, sal) }
        db.withTransaction {
            comprobarJefe(jefe, sid)
            require(dao.usuarioPorCorreo(limpio) == null) { "El correo ya está registrado" }
            require(dao.existeRut(rutNormalizado) == 0) { "El RUT ya está registrado" }
            val id = dao.siguienteIdUsuario()
            dao.guardarUsuario(Usuario(id, nombre.trim(), limpio, hash,
                Base64.encodeToString(sal, Base64.NO_WRAP), "TRABAJADOR_TIENDA", rut = rutNormalizado))
            dao.asignarUsuarioTienda(UsuarioSucursal(id, sid))
            "Trabajador registrado y asignado a la tienda"
        }
    }, respuesta)

    fun asignarTrabajador(jefe: Usuario, sid: Long, uid: Long,
                         respuesta: (Boolean, String) -> Unit) = realizar({
        db.withTransaction {
            comprobarJefe(jefe, sid)
            val trabajador = dao.usuarioPorId(uid) ?: error("Trabajador no encontrado")
            require(trabajador.activo && trabajador.rol == "TRABAJADOR_TIENDA") { "Solo se admiten trabajadores de tienda activos" }
            require(dao.numeroTiendasUsuario(uid) == 0) { "El trabajador ya tiene una sucursal asignada" }
            dao.asignarUsuarioTienda(UsuarioSucursal(uid, sid))
            "Trabajador asignado correctamente"
        }
    }, respuesta)

    fun quitarTrabajador(jefe: Usuario, sid: Long, uid: Long,
                        respuesta: (Boolean, String) -> Unit) = realizar({
        db.withTransaction {
            comprobarJefe(jefe, sid)
            val trabajador = dao.usuarioPorId(uid) ?: error("Trabajador no encontrado")
            require(trabajador.rol == "TRABAJADOR_TIENDA") { "No se puede retirar el acceso de un jefe de tienda" }
            require(dao.tieneAsignacionTienda(uid, sid) == 1) { "El trabajador no pertenece a esta tienda" }
            require(operacionesTienda.zonasTiendaAsignadasAbiertas(sid,uid)==0) {
                "El trabajador tiene zonas asignadas en un inventario abierto; ciérralo antes de retirarlo"
            }
            require(operacionesTienda.inventarioAbierto(sid) == 0 || !trabajador.activo ||
                dao.trabajadoresActivosTienda(sid) > 1) {
                "Asigne otro trabajador antes de retirar al último durante un inventario abierto"
            }
            require(dao.guardarResponsableTrabajadorTienda(uid, sid) == 1) {
                "No se pudo conservar la sucursal responsable del trabajador"
            }
            require(dao.retirarAsignacionTienda(uid, sid) == 1) { "El trabajador ya no está asignado" }
            "Acceso retirado. La cuenta continúa en el personal administrable de la sucursal"
        }
    }, respuesta)

    // revisar diferencias sin modificar stock
    fun revisarIncidencia(jefe: Usuario, sid: Long, incidencia: IncidenciaCaja,
                          respuesta: (Boolean, String) -> Unit) = realizar({
        db.withTransaction {
            comprobarJefe(jefe, sid)
            require(dao.revisarIncidenciaTienda(incidencia.id, sid) == 1) {
                "La incidencia ya fue revisada o no pertenece a esta tienda"
            }
            dao.guardarEventoCaja(EventoCaja(cajaId = incidencia.cajaId, usuarioId = jefe.id,
                accion = "INCIDENCIA_TIENDA_REVISADA", observacion = "Diferencia revisada por jefatura. No se ajustó stock."))
            "Incidencia marcada como revisada. No se ha ajustado el inventario."
        }
    }, respuesta)

    // activar y desactivar ubicaciones
    fun crearUbicacion(jefe: Usuario, sid: Long, codigo: String, zona: String, categoria: String,
                      respuesta: (Boolean, String) -> Unit) = realizar({
        db.withTransaction {
            comprobarJefe(jefe, sid)
            require(operacionesTienda.inventarioAbierto(sid) == 0 &&
                db.campaniasMensualesDao().campañaActiva("TIENDA", sid) == 0) {
                "No se pueden modificar ubicaciones durante toda la campaña mensual"
            }
            val limpio = codigo.trim().uppercase()
            require(limpio.matches(Regex("[A-Z0-9-]{2,15}"))) { "Código de 2 a 15 caracteres, letras, números o guion" }
            require(zona in listOf("VENTAS", "ALMACEN")) { "Zona no válida" }
            require(categoria == "General" || dao.categoriasProductos().contains(categoria)) { "Categoría no registrada" }
            require(dao.guardarPosicionTienda(PosicionTienda(sucursalId = sid, codigo = limpio,
                zona = zona, categoria = categoria)) != -1L) { "Ya existe ese código en la tienda" }
            "Estantería registrada"
        }
    }, respuesta)

    fun cambiarUbicacion(jefe: Usuario, sid: Long, id: Long, activar: Boolean,
                         respuesta: (Boolean, String) -> Unit) = realizar({
        db.withTransaction {
            comprobarJefe(jefe, sid)
            require(operacionesTienda.inventarioAbierto(sid) == 0 &&
                db.campaniasMensualesDao().campañaActiva("TIENDA", sid) == 0) {
                "No se pueden modificar ubicaciones durante toda la campaña mensual"
            }
            val lugar = dao.posicionTiendaPorId(id) ?: error("Ubicación inexistente")
            require(lugar.sucursalId == sid && lugar.zona != "RECEPCION") { "La zona de recepción no puede desactivarse" }
            val actuales = dao.posicionesTiendaActuales(sid)
            if (activar && lugar.padreId != null) require(actuales.firstOrNull { it.id == lugar.padreId }?.activa == true) {
                "Activa primero la ubicación superior"
            }
            if (!activar) require(actuales.none { it.padreId == id && it.activa }) {
                "Desactiva primero sus ubicaciones inferiores"
            }
            require(dao.cambiarEstadoPosicionTienda(id, sid, activar) == 1) { "No se pudo cambiar el estado" }
            if (activar) "Estantería activada" else "Estantería desactivada. Su stock sigue visible y podrá retirarse."
        }
    }, respuesta)

    // niveles de ubicaciones de tienda
    fun crearUbicacionJerarquica(jefe: Usuario, sid: Long, nombre: String, tipo: String,
                                 padreId: Long?, zona: String, categoria: String,
                                 respuesta: (Boolean, String) -> Unit) = realizar({
        db.withTransaction {
            comprobarJefe(jefe, sid)
            require(operacionesTienda.inventarioAbierto(sid) == 0 &&
                db.campaniasMensualesDao().campañaActiva("TIENDA", sid) == 0) {
                "No se pueden modificar ubicaciones durante toda la campaña mensual"
            }
            val limpio = nombre.trim()
            require(limpio.length in 2..60) { "Nombre de 2 a 60 caracteres" }
            require(tipo in listOf("SECCION", "ESTANTERIA", "SUBESTANTE")) { "Tipo no válido" }
            val actuales = dao.posicionesTiendaActuales(sid)
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
                require(padre.zona == zona) { "La ubicación superior pertenece a otra zona" }
            }
            require(zona in listOf("VENTAS", "ALMACEN")) { "Zona no válida" }
            require(categoria == "General" || dao.categoriasProductos().contains(categoria)) { "Categoría no registrada" }
            val padre = actuales.firstOrNull { it.id == padreId }
            val categoriaFinal = padre?.categoria ?: categoria
            val id = dao.siguienteIdPosicionTienda()
            require(dao.guardarPosicionTienda(PosicionTienda(sucursalId = sid,
                codigo = "U-$id", zona = zona, categoria = categoriaFinal,
                nombre = limpio, tipo = tipo, padreId = padreId)) != -1L) { "Código de ubicación duplicado" }
            "Ubicación creada"
        }
    }, respuesta)

    fun renombrarUbicacionTienda(jefe: Usuario, sid: Long, id: Long, nombre: String,
                                 respuesta: (Boolean, String) -> Unit) = realizar({
        db.withTransaction {
            comprobarJefe(jefe, sid)
            require(operacionesTienda.inventarioAbierto(sid) == 0 &&
                db.campaniasMensualesDao().campañaActiva("TIENDA", sid) == 0) {
                "No se pueden modificar ubicaciones durante toda la campaña mensual"
            }
            val lugar = dao.posicionTiendaPorId(id) ?: error("Ubicación no encontrada")
            require(lugar.sucursalId == sid && lugar.zona != "RECEPCION") { "No se puede renombrar recepción" }
            val limpio = nombre.trim()
            require(limpio.length in 2..60) { "Nombre de 2 a 60 caracteres" }
            require(dao.posicionesTiendaActuales(sid).none {
                it.id != id && it.padreId == lugar.padreId && it.etiqueta().equals(limpio, true)
            }) { "Ya existe ese nombre en el mismo nivel" }
            require(dao.renombrarPosicionTienda(id, sid, limpio) == 1) { "No se pudo guardar el nombre" }
            "Nombre actualizado"
        }
    }, respuesta)

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = JefeTiendaViewModel(app) as T
    }
}
