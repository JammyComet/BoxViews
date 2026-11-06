package com.example.boxview.viewmodel

// Lógica: Gestiona usuarios, sucursales y permisos de administración.
import com.example.boxview.mensajeErrorUsuario
import android.app.Application
import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.example.boxview.modelo.*
import java.security.SecureRandom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdministradorViewModel(app: Application) : AndroidViewModel(app) {
    // Conexion con la capa de datos.
    private val db = AppDatabase.getDatabase(app)
    private val dao = db.inventarioDao()
    private val operacionesTienda = db.operacionesTiendaDao()
    private val preciosVenta = db.preciosVentaDao()

    var sucursalActiva by mutableStateOf<Sucursal?>(null)
        private set

    fun cerrarSesion() { sucursalActiva = null }
    // Sucursales asociadas al usuario.
    fun sucursales(uid: Long): Flow<List<Sucursal>> = dao.sucursalesAdministradas(uid)
    fun permiso(uid: Long, sid: Long): Flow<Int> = dao.observarAdministrador(uid, sid)
    fun bodegas(sid: Long): Flow<List<Bodega>> = dao.bodegasAdministradas(sid)
    fun configuracion(sid: Long): Flow<ConfiguracionSucursal?> = dao.configuracionSucursal(sid)
    fun personal(sid: Long): Flow<List<Usuario>> = dao.empleadosSucursal(sid)
    fun minimos(sid: Long): Flow<List<MinimoSucursal>> = dao.minimosSucursal(sid)
    fun historial(sid: Long): Flow<List<EventoAdministrativo>> = dao.historialAdministrativo(sid)
    fun productos(): Flow<List<Producto>> = dao.productos()
    fun preciosVenta(sid: Long): Flow<List<PrecioVentaSucursal>> = preciosVenta.preciosSucursal(sid)

    fun configurarPrecioVenta(admin: Usuario?, productoId: Long, precio: Long,
                             respuesta: (Boolean, String) -> Unit) {
        val sid = sucursalActiva?.id ?: return respuesta(false, "Selecciona una sucursal")
        if (admin?.rol != "ADMIN_SUCURSAL") return respuesta(false, "Acceso reservado a Administración")
        viewModelScope.launch {
            try {
                // Operacion agrupada en Room.
                db.withTransaction {
                    require(dao.administradorAutorizado(admin.id, sid) == 1) { "No administras esta sucursal" }
                    require(dao.productoPorId(productoId) != null) { "Producto inexistente" }
                    require(precio in 1L..100_000_000L) { "Ingrese un precio CLP válido (1 a 100.000.000)" }
                    val anterior = preciosVenta.precioActual(sid, productoId)?.precioClp
                    preciosVenta.guardarPrecio(PrecioVentaSucursal(sid, productoId, precio, admin.id))
                    dao.guardarEventoAdministrativo(EventoAdministrativo(sucursalId = sid, usuarioId = admin.id,
                        accion = "PRECIO_VENTA", detalle = "Producto #$productoId: ${anterior ?: "sin precio"} -> $precio CLP"))
                }
                respuesta(true, "Precio de venta actualizado")
            } catch (e: Exception) { respuesta(false, mensajeErrorUsuario(e, "No se pudo actualizar el precio")) }
        }
    }


    fun elegirSucursal(admin: Usuario, sucursal: Sucursal, respuesta: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                require(dao.administradorAutorizado(admin.id, sucursal.id) == 1) { "Sucursal no autorizada" }
                sucursalActiva = sucursal
                respuesta(true, "")
            } catch (e: Exception) { respuesta(false, mensajeErrorUsuario(e, "No se pudo seleccionar la sucursal")) }
        }
    }

    private suspend fun comprobar(admin: Usuario, sid: Long) {
        require(admin.rol == "ADMIN_SUCURSAL" && dao.administradorAutorizado(admin.id, sid) == 1) {
            "No tiene permisos de administración sobre esta sucursal"
        }
    }

    private fun accion(operacion: suspend () -> String, respuesta: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try { respuesta(true, operacion()) }
            catch (e: Exception) { respuesta(false, mensajeErrorUsuario(e, "No se pudo completar la operación")) }
        }
    }

    private suspend fun evento(admin: Usuario, sid: Long, accion: String, detalle: String) {
        dao.guardarEventoAdministrativo(EventoAdministrativo(sucursalId = sid, usuarioId = admin.id,
            accion = accion, detalle = detalle))
    }

    fun crearBodega(admin: Usuario, sid: Long, nombre: String, respuesta: (Boolean, String) -> Unit) = accion({
        db.withTransaction {
            comprobar(admin, sid)
            val limpio = nombre.trim()
            require(limpio.length in 3..60) { "Nombre de 3 a 60 caracteres" }
            require(dao.nombreBodegaExiste(sid, limpio) == 0) { "Ya existe una bodega con ese nombre" }
            val bid = dao.siguienteIdBodega()
            dao.guardarBodega(Bodega(bid, sid, limpio))
            dao.guardarAbastecimiento(BodegaSucursal(bid, sid))
            // posicion inicial para el ingreso de mercaderia
            dao.guardarPosicion(Posicion(dao.siguienteIdPosicion(), bid, "Reserva", "Reserva"))
            dao.crearConfiguracionSucursal(ConfiguracionSucursal(sid))
            if (dao.configuracionSucursalActual(sid)?.bodegaPrincipalId == null) dao.guardarPrincipal(sid, bid)
            evento(admin, sid, "BODEGA_CREADA", "$limpio (ID $bid)")
            "Bodega registrada correctamente"
        }
    }, respuesta)

    fun renombrarBodega(admin: Usuario, sid: Long, bid: Long, nombre: String, respuesta: (Boolean, String) -> Unit) = accion({
        db.withTransaction {
            comprobar(admin, sid)
            val bodega = dao.bodegaAdministradaPorId(bid) ?: error("Bodega no encontrada")
            require(bodega.sucursalId == sid) { "La bodega no pertenece a esta sucursal" }
            val limpio = nombre.trim()
            require(limpio.length in 3..60) { "Nombre de 3 a 60 caracteres" }
            if (!bodega.nombre.equals(limpio, true)) require(dao.nombreBodegaExiste(sid, limpio) == 0) { "Nombre ya utilizado" }
            require(dao.renombrarBodegaAdmin(bid, sid, limpio) == 1) { "No se pudo actualizar" }
            evento(admin, sid, "BODEGA_EDITADA", "${bodega.nombre} → $limpio")
            "Nombre actualizado"
        }
    }, respuesta)

    fun cambiarEstadoBodega(admin: Usuario, sid: Long, bid: Long, activar: Boolean,
                            respuesta: (Boolean, String) -> Unit) = accion({
        db.withTransaction {
            comprobar(admin, sid)
            val b = dao.bodegaAdministradaPorId(bid) ?: error("Bodega no encontrada")
            require(b.sucursalId == sid) { "Bodega fuera de la sucursal" }
            if (!activar) {
                require(dao.configuracionSucursalActual(sid)?.bodegaPrincipalId != bid) { "Primero cambie la bodega principal" }
                require(dao.stockTotalBodega(bid) == 0L) { "La bodega todavía contiene stock" }
                require(dao.cajasActivasBodega(bid) == 0 && dao.solicitudesActivasBodega(bid) == 0 &&
                    dao.solicitudesExternasActivasBodega(bid) == 0) {
                    "Hay cajas, reposiciones o solicitudes de abastecimiento externo pendientes"
                }
                require(dao.personalActivoBodega(bid) == 0) { "Hay personal activo asignado a esta bodega" }
                require(dao.inventariosAbiertosBodega(bid) == 0) { "Hay inventarios abiertos" }
            }
            require(dao.estadoBodegaAdmin(bid, sid, activar) == 1) { "No se pudo cambiar el estado" }
            evento(admin, sid, if (activar) "BODEGA_ACTIVADA" else "BODEGA_DESACTIVADA", b.nombre)
            if (activar) "Bodega activada" else "Bodega desactivada"
        }
    }, respuesta)

    fun elegirPrincipal(admin: Usuario, sid: Long, bid: Long, respuesta: (Boolean, String) -> Unit) = accion({
        db.withTransaction {
            comprobar(admin, sid)
            require(dao.abasteceSucursal(bid, sid) == 1) { "Seleccione una bodega activa de la sucursal" }
            dao.crearConfiguracionSucursal(ConfiguracionSucursal(sid))
            val anterior = dao.configuracionSucursalActual(sid)?.bodegaPrincipalId
            if (anterior != null && anterior != bid) {
                require(dao.cajasActivasBodega(anterior) == 0 && dao.solicitudesActivasBodega(anterior) == 0) {
                    "La bodega principal anterior tiene operaciones pendientes"
                }
            }
            require(dao.guardarPrincipal(sid, bid) == 1) { "No se pudo configurar la bodega" }
            evento(admin, sid, "BODEGA_PRINCIPAL", "Bodega $bid")
            "Bodega principal actualizada"
        }
    }, respuesta)

    fun crearEmpleado(admin: Usuario, sid: Long, nombre: String, correo: String, rut: String,
                      clave: String, rol: String, bodegaId: Long?, respuesta: (Boolean, String) -> Unit) = accion({
        val nombreLimpio = nombre.trim()
        val mail = correo.trim().lowercase()
        val rutLimpio = rut.replace(".", "").replace("-", "").trim().uppercase()
        require(nombreLimpio.length in 3..90) { "Escriba el nombre completo" }
        require(mail.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))) { "Correo no válido" }
        require(rutValido(rutLimpio)) { "RUT no válido" }
        require(clave.length >= 8) { "La contraseña debe tener al menos 8 caracteres" }
        require(rol in setOf("JEFE_BODEGA", "OPERADOR_BODEGA", "JEFE_TIENDA", "TRABAJADOR_TIENDA")) { "Rol no permitido" }
        val sal = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val hash = withContext(Dispatchers.Default) { hashClave(clave, sal) }
        db.withTransaction {
            comprobar(admin, sid)
            if (rol.endsWith("BODEGA")) require(bodegaId != null && dao.abasteceSucursal(bodegaId, sid) == 1) {
                "Elija una bodega activa de esta sucursal"
            }
            require(dao.usuarioPorCorreo(mail) == null) { "El correo ya está registrado" }
            require(dao.existeRut(rutLimpio) == 0) { "El RUT ya está registrado" }
            val uid = dao.siguienteIdUsuario()
            dao.guardarUsuario(Usuario(uid, nombreLimpio, mail, hash,
                Base64.encodeToString(sal, Base64.NO_WRAP), rol, rut = rutLimpio))
            if (rol.endsWith("BODEGA")) dao.guardarPermiso(UsuarioBodega(uid, bodegaId!!))
            else dao.asignarUsuarioTienda(UsuarioSucursal(uid, sid))
            evento(admin, sid, "USUARIO_CREADO", "$nombreLimpio · $rol")
            "Usuario creado y asignado"
        }
    }, respuesta)

    fun editarEmpleado(admin: Usuario, sid: Long, uid: Long, nombre: String, correo: String,
                       respuesta: (Boolean, String) -> Unit) = accion({
        db.withTransaction {
            comprobar(admin, sid)
            require(dao.sucursalResponsableEmpleado(uid, sid) == 1) { "La edición de esta cuenta corresponde a su sucursal responsable" }
            val limpio = nombre.trim()
            val mail = correo.trim().lowercase()
            require(limpio.length in 3..90) { "Nombre no válido" }
            require(mail.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))) { "Correo no válido" }
            val anterior = dao.usuarioPorId(uid) ?: error("Cuenta inexistente")
            if (anterior.correo != mail) require(dao.usuarioPorCorreo(mail) == null) { "El correo ya existe" }
            dao.editarDatosEmpleado(uid, limpio, mail)
            evento(admin, sid, "USUARIO_EDITADO", "Usuario $uid")
            "Datos actualizados"
        }
    }, respuesta)

    fun activarEmpleado(admin: Usuario, sid: Long, uid: Long, activo: Boolean,
                        respuesta: (Boolean, String) -> Unit) = accion({
        db.withTransaction {
            comprobar(admin, sid)
            require(dao.sucursalResponsableEmpleado(uid, sid) == 1) { "El estado de esta cuenta lo gestiona su sucursal responsable" }
            if (!activo) {
                val rolEmpleado = dao.usuarioPorId(uid)?.rol
                if (rolEmpleado == "JEFE_TIENDA") {
                    dao.tiendasActualesEmpleado(uid).forEach { sidTienda ->
                        require(operacionesTienda.inventarioAbierto(sidTienda) == 0) {
                            "Cierre el inventario abierto antes de desactivar a su jefe"
                        }
                    }
                }
                if (rolEmpleado == "JEFE_BODEGA") {
                    dao.bodegasActualesEmpleado(uid).forEach { b ->
                        require(dao.inventariosAbiertosBodega(b.id) == 0) {
                            "Cierre el inventario de bodega antes de desactivar a su jefe"
                        }
                    }
                }
                if (rolEmpleado == "TRABAJADOR_TIENDA") {
                    dao.tiendasActualesEmpleado(uid).forEach { sidTienda ->
                        require(operacionesTienda.zonasTiendaAsignadasAbiertas(sidTienda,uid)==0) {
                            "El trabajador tiene estantes asignados en un inventario abierto"
                        }
                        require(operacionesTienda.inventarioAbierto(sidTienda) == 0 ||
                            dao.trabajadoresActivosTienda(sidTienda) > 1) {
                            "No desactive al último trabajador con un inventario de tienda abierto"
                        }
                    }
                }
                dao.bodegasActualesEmpleado(uid).forEach { b ->
                    require(dao.ordenesActivasOperador(uid, b.id) == 0 &&
                        dao.periodoPendiente(b.id, uid) == 0 && dao.ingresosPendientesOperador(uid, b.id) == 0) {
                        "El usuario tiene órdenes, recepciones asignadas o conteos pendientes"
                    }
                }
            }
            require(dao.activarEmpleado(uid, activo) == 1) { "No se pudo actualizar" }
            evento(admin, sid, if (activo) "USUARIO_ACTIVADO" else "USUARIO_DESACTIVADO", "Usuario $uid")
            "Estado de cuenta actualizado"
        }
    }, respuesta)

    fun asignarOperadorBodega(admin: Usuario, sid: Long, uid: Long, bid: Long, quitar: Boolean,
                              respuesta: (Boolean, String) -> Unit) = accion({
        db.withTransaction {
            comprobar(admin, sid)
            require(dao.operadorDeSucursal(uid, sid) == 1) { "El operador no está asignado a esta sucursal" }
            require(dao.usuarioPorId(uid)?.rol == "OPERADOR_BODEGA") { "Solo los operadores pueden trabajar en varias bodegas" }
            require(dao.bodegaAdministradaPorId(bid)?.sucursalId == sid) { "Bodega fuera de la sucursal" }
            if (quitar) {
                comprobarRetiroOperadorBodega(dao, uid, bid)
                require(dao.retirarBodegaAdmin(uid, bid) == 1) { "Permiso no encontrado" }
            }
            else {
                require(dao.abasteceSucursal(bid, sid) == 1) { "La bodega está desactivada" }
                require(dao.empleadoAsignadoBodega(uid, bid) == 0) { "Permiso existente" }
                dao.guardarPermiso(UsuarioBodega(uid, bid))
            }
            evento(admin, sid, "PERMISO_BODEGA", "Usuario $uid · Bodega $bid · ${if (quitar) "Retirado" else "Asignado"}")
            "Permiso actualizado"
        }
    }, respuesta)

    fun reasignarJefeBodega(admin: Usuario, sid: Long, uid: Long, nuevaBodega: Long,
                             respuesta: (Boolean, String) -> Unit) = accion({
        db.withTransaction {
            comprobar(admin, sid)
            require(dao.empleadoExclusivoSucursal(uid, sid) == 1) { "La cuenta no pertenece exclusivamente a esta sucursal" }
            val jefe = dao.usuarioPorId(uid) ?: error("Usuario no encontrado")
            require(jefe.rol == "JEFE_BODEGA" && jefe.activo) { "Seleccione un jefe de bodega activo" }
            require(dao.abasteceSucursal(nuevaBodega, sid) == 1) { "Bodega de destino no autorizada" }
            val asignadas = dao.bodegasActualesEmpleado(uid)
            require(asignadas.size == 1 && asignadas[0].sucursalId == sid && asignadas[0].id != nuevaBodega) {
                "Seleccione una bodega diferente de la misma sucursal"
            }
            val antigua = asignadas.first()
            require(dao.inventariosAbiertosBodega(antigua.id) == 0) {
                "Cierre los inventarios pendientes antes de cambiar de jefatura"
            }
            require(dao.retirarBodegaAdmin(uid, antigua.id) == 1) { "No se pudo retirar la asignación anterior" }
            dao.guardarPermiso(UsuarioBodega(uid, nuevaBodega))
            evento(admin, sid, "JEFE_REASIGNADO", "Usuario $uid · Bodega ${antigua.id} → $nuevaBodega")
            "Jefatura asignada a la nueva bodega"
        }
    }, respuesta)

    fun guardarMinimo(admin: Usuario, sid: Long, pid: Long, valor: Int?,
                      respuesta: (Boolean, String) -> Unit) = accion({
        db.withTransaction {
            comprobar(admin, sid)
            require(dao.productoPorId(pid) != null) { "Producto inexistente" }
            require(valor == null || valor in 0..100000) { "Ingrese un mínimo entre 0 y 100000" }
            if (valor == null) dao.borrarMinimoSucursal(sid, pid)
            else dao.guardarMinimoSucursal(MinimoSucursal(sid, pid, valor))
            evento(admin, sid, "MINIMO_STOCK", "Producto $pid · ${valor?.toString() ?: "Valor general"}")
            "Stock mínimo guardado"
        }
    }, respuesta)

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = AdministradorViewModel(app) as T
    }
}
