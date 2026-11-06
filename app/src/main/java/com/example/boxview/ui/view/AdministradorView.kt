package com.example.boxview.ui.view

// Pantallas: Sucursales, personal, bodegas y stock mínimo.
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import kotlinx.coroutines.delay
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.boxview.R
import com.example.boxview.modelo.*
import com.example.boxview.ui.components.*
import com.example.boxview.viewmodel.AdministradorViewModel
import com.example.boxview.viewmodel.OperadorViewModel

// Permite elegir la sucursal autorizada.
@Composable
fun PantallaElegirSucursalAdmin(base: OperadorViewModel, admin: AdministradorViewModel, continuar: () -> Unit) {
    val usuario = base.sesion ?: return
    // Datos observados por esta pantalla.
    val sucursales by remember(usuario.id) { admin.sucursales(usuario.id) }.collectAsState(initial = emptyList())
    var seleccion by remember(usuario.id) { mutableStateOf<Long?>(null) }
    var mensaje by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)) {
        TituloSeccion("ADMINISTRACIÓN", "Sucursal asignada", "Acceso limitado a la sucursal autorizada")
        Image(painter = painterResource(R.drawable.mascota_jefe), contentDescription = "Mascota BoxView",
            modifier = Modifier.size(80.dp).align(Alignment.CenterHorizontally))
        sucursales.forEach { s ->
            FilaOpcion(s.nombre, "Administración de sucursal", Icons.Default.Store,
                badge = if (seleccion == s.id) "Seleccionada" else null,
                onClick = { seleccion = s.id })
        }
        if (sucursales.isEmpty()) Text("No hay sucursales asignadas a esta cuenta")
        if (mensaje.isNotBlank()) Text(mensaje, color = MaterialTheme.colorScheme.error)
        BotonAccion("Continuar", enabled = seleccion != null, onClick = {
            val sede = sucursales.firstOrNull { it.id == seleccion }
            if (sede != null) admin.elegirSucursal(usuario, sede) { ok, msg ->
                mensaje = msg; if (ok) continuar()
            }
        })
    }
}

// Panel principal con accesos de administración.
@Composable
fun PantallaInicioAdmin(base: OperadorViewModel, admin: AdministradorViewModel, ir: (String) -> Unit) {
    val sid = admin.sucursalActiva?.id ?: return
    // Datos observados por esta pantalla.
    val bodegas by remember(sid) { admin.bodegas(sid) }.collectAsState(initial = emptyList())
    val empleados by remember(sid) { admin.personal(sid) }.collectAsState(initial = emptyList())
    val configuracion by remember(sid) { admin.configuracion(sid) }.collectAsState(initial = null)
    ColumnaOperador {
        TituloSeccion("ADMINISTRACIÓN", "Panel de sucursal", admin.sucursalActiva?.nombre)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TarjetaResumen("Bodegas activas", bodegas.count { it.activa }.toString(), Modifier.weight(1f), Icons.Default.Warehouse)
            TarjetaResumen("Personal activo", empleados.count { it.activo }.toString(), Modifier.weight(1f), Icons.Default.Group)
        }
        TarjetaPanel {
            Text("Bodega principal", fontWeight = FontWeight.SemiBold)
            Text(bodegas.firstOrNull { it.id == configuracion?.bodegaPrincipalId }?.nombre ?: "Sin bodega principal")
            BotonAccion("Administrar bodegas", onClick = { ir(Destinos.AdminBodegas.ruta) })
        }
        TarjetaPanel {
            Text("Gestión de sucursal", fontWeight = FontWeight.SemiBold)
            BotonAccion("Abastecimiento externo", outlined = true, onClick = { ir(Destinos.AdminAbastecimiento.ruta) })
            BotonAccion("Informes operativos y stock crítico", outlined = true, onClick = { ir(Destinos.AdminReportes.ruta) })
            BotonAccion("Administrar personal", outlined = true, onClick = { ir(Destinos.AdminPersonal.ruta) })
            BotonAccion("Configurar precios de venta", outlined = true, onClick = { ir(Destinos.AdminPrecios.ruta) })
            BotonAccion("Configurar stock mínimo", outlined = true, onClick = { ir(Destinos.AdminMinimos.ruta) })
        }
    }
}

// Muestra la pantalla de bodegas admin.
@Composable
fun PantallaBodegasAdmin(base: OperadorViewModel, admin: AdministradorViewModel) {
    val user = base.sesion ?: return
    val sid = admin.sucursalActiva?.id ?: return
    // Datos observados por esta pantalla.
    val bodegas by remember(sid) { admin.bodegas(sid) }.collectAsState(initial = emptyList())
    val config by remember(sid) { admin.configuracion(sid) }.collectAsState(initial = null)
    var nombre by remember(sid) { mutableStateOf("") }
    var mensaje by remember(sid) { mutableStateOf("") }
    var bloqueado by remember { mutableStateOf(false) }
    var editar by remember { mutableStateOf<Long?>(null) }
    var nombreEditado by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf<Bodega?>(null) }
    ColumnaOperador {
        TituloSeccion("CONFIGURACIÓN", "Bodegas", admin.sucursalActiva?.nombre)
        TarjetaPanel {
            Text("Registrar bodega", fontWeight = FontWeight.Bold)
            CampoFormulario("Nombre de bodega", nombre, { nombre = it })
            BotonAccion("Crear bodega", enabled = !bloqueado && nombre.trim().length >= 3, onClick = {
                bloqueado = true
                admin.crearBodega(user, sid, nombre) { ok, msg ->
                    bloqueado = false; mensaje = msg; if (ok) nombre = ""
                }
            })
        }
        if (mensaje.isNotBlank()) Text(mensaje)
        bodegas.forEach { b ->
            TarjetaPanel {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(b.nombre, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    EtiquetaEstado(if (!b.activa) "Inactiva" else if (config?.bodegaPrincipalId == b.id) "Principal" else "Secundaria", warning = !b.activa)
                }
                if (editar == b.id) {
                    CampoFormulario("Nuevo nombre", nombreEditado, { nombreEditado = it })
                    BotonAccion("Guardar nombre", enabled = !bloqueado && nombreEditado.trim().length >= 3, onClick = {
                        bloqueado = true
                        admin.renombrarBodega(user, sid, b.id, nombreEditado) { ok, msg ->
                            bloqueado = false; mensaje = msg; if (ok) editar = null
                        }
                    })
                } else BotonAccion("Cambiar nombre", outlined = true, enabled = !bloqueado,
                    onClick = { editar = b.id; nombreEditado = b.nombre })
                if (b.activa && config?.bodegaPrincipalId != b.id) {
                    BotonAccion("Establecer como principal", outlined = true, enabled = !bloqueado, onClick = {
                        bloqueado = true
                        admin.elegirPrincipal(user, sid, b.id) { _, msg -> bloqueado = false; mensaje = msg }
                    })
                }
                BotonAccion(if (b.activa) "Desactivar bodega" else "Activar bodega", outlined = true,
                    enabled = !bloqueado && (!b.activa || config?.bodegaPrincipalId != b.id),
                    onClick = { confirmar = b })
            }
        }
    }
    confirmar?.let { b ->
        AlertDialog(onDismissRequest = { confirmar = null }, title = { Text("Confirmar cambio") },
            text = { Text("Solo es posible desactivar bodegas sin stock, personal ni operaciones pendientes.") },
            confirmButton = { TextButton(onClick = {
                confirmar = null; bloqueado = true
                admin.cambiarEstadoBodega(user, sid, b.id, !b.activa) { _, msg -> bloqueado = false; mensaje = msg }
            }) { Text("Confirmar") } }, dismissButton = { TextButton(onClick = { confirmar = null }) { Text("Cancelar") } })
    }
}

// Formulario para crear y asignar usuarios.
@Composable
fun PantallaPersonalAdmin(base: OperadorViewModel, admin: AdministradorViewModel) {
    val usuario = base.sesion ?: return
    val sid = admin.sucursalActiva?.id ?: return
    // Datos observados por esta pantalla.
    val personas by remember(sid) { admin.personal(sid) }.collectAsState(initial = emptyList())
    val bodegas by remember(sid) { admin.bodegas(sid) }.collectAsState(initial = emptyList())
    var nombre by remember(sid) { mutableStateOf("") }
    var correo by remember(sid) { mutableStateOf("") }
    var rut by remember(sid) { mutableStateOf("") }
    var clave by remember(sid) { mutableStateOf("") }
    var rol by remember(sid) { mutableStateOf("OPERADOR_BODEGA") }
    var bodegaId by remember(sid) { mutableStateOf<Long?>(null) }
    var editando by remember { mutableStateOf<Long?>(null) }
    var editNombre by remember { mutableStateOf("") }
    var editCorreo by remember { mutableStateOf("") }
    var permisoUsuario by remember { mutableStateOf<Long?>(null) }
    var permisoBodega by remember { mutableStateOf<Long?>(null) }
    var mensaje by remember(sid) { mutableStateOf("") }
    var procesando by remember { mutableStateOf(false) }
    var procesandoCreacion by remember { mutableStateOf(false) }
    var mostrandoExito by remember(sid) { mutableStateOf(false) }
    var formVersion by remember(sid) { mutableStateOf(0) }
    val roles = listOf("JEFE_BODEGA" to "Jefe de bodega", "OPERADOR_BODEGA" to "Operador de bodega",
        "JEFE_TIENDA" to "Jefe de tienda", "TRABAJADOR_TIENDA" to "Trabajador de tienda")
    ColumnaOperador {
        TituloSeccion("PERSONAL", "Usuarios y permisos", admin.sucursalActiva?.nombre)
        key(formVersion) {
            TarjetaPanel {
                Text("Crear usuario", fontWeight = FontWeight.Bold)
                CampoFormulario("Nombre completo", nombre, { nombre = it })
                CampoFormulario("Correo", correo, { correo = it }, keyboardType = KeyboardType.Email)
                CampoFormulario("RUT", rut, { rut = it })
                CampoFormulario("Contraseña inicial", clave, { clave = it }, esContrasena = true)
                SelectorSimple("Rol", roles.mapIndexed { i, r -> i.toLong() to r.second }, roles.indexOfFirst { it.first == rol }.toLong()) {
                    rol = roles[it.toInt()].first
                }
                if (rol.endsWith("BODEGA")) {
                    SelectorSimple("Bodega asignada", bodegas.filter { it.activa }.map { it.id to it.nombre }, bodegaId) {
                        bodegaId = it
                    }
                }
                BotonAccion("Crear y asignar usuario", enabled = !procesando && !procesandoCreacion && nombre.trim().length >= 3 && correoBienFormado(correo) &&
                    com.example.boxview.viewmodel.rutValido(rut) && clave.length >= 8 && (!rol.endsWith("BODEGA") || bodegas.any { it.id == bodegaId && it.activa }),
                    onClick = {
                        procesando = true
                        procesandoCreacion = true
                        admin.crearEmpleado(usuario, sid, nombre, correo, rut, clave, rol, bodegaId) { ok, msg ->
                            mensaje = msg
                            procesando = false
                            procesandoCreacion = false
                            if (ok) {
                                nombre = ""; correo = ""; rut = ""; clave = ""; bodegaId = null
                                formVersion += 1
                                mostrandoExito = true
                            }
                        }
                    })
            }
        }
        if (mensaje.isNotBlank()) Text(mensaje)
        personas.forEach { p ->
            TarjetaPanel {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(p.nombre, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    EtiquetaEstado(if (p.activo) "Activo" else "Inactivo", warning = !p.activo)
                }
                Text("${p.correo} · ${roles.firstOrNull { it.first == p.rol }?.second ?: "Administrador"}",
                    style = MaterialTheme.typography.bodySmall)
                if (p.rol != "ADMIN_SUCURSAL") {
                    if (editando == p.id) {
                        CampoFormulario("Nombre", editNombre, { editNombre = it })
                        CampoFormulario("Correo", editCorreo, { editCorreo = it }, keyboardType = KeyboardType.Email)
                    BotonAccion("Guardar cambios", enabled = !procesando && editNombre.trim().length >= 3 &&
                        correoBienFormado(editCorreo), onClick = {
                            procesando = true
                            admin.editarEmpleado(usuario, sid, p.id, editNombre, editCorreo) { ok, msg ->
                                procesando = false; procesandoCreacion = false; mensaje = msg; if (ok) editando = null
                            }
                        })
                    } else BotonAccion("Editar datos", outlined = true, enabled = !procesando,
                        onClick = { editando = p.id; editNombre = p.nombre; editCorreo = p.correo })
                    BotonAccion(if (p.activo) "Desactivar cuenta" else "Activar cuenta", outlined = true,
                        enabled = !procesando, onClick = {
                            procesando = true
                            admin.activarEmpleado(usuario, sid, p.id, !p.activo) { _, msg ->
                                procesando = false; mensaje = msg
                            }
                        })
                    if (p.rol == "OPERADOR_BODEGA" || p.rol == "JEFE_BODEGA") {
                        BotonAccion(if (p.rol == "JEFE_BODEGA") "Cambiar bodega del jefe" else "Gestionar bodegas del operador",
                            outlined = true, enabled = !procesando, onClick = {
                                permisoUsuario = p.id; permisoBodega = null
                            })
                    }
                }
            }
        }
    
    LaunchedEffect(mostrandoExito) {
        if (mostrandoExito) {
            delay(1800)
            mostrandoExito = false
        }
    }
    DialogoEstadoCreacionUsuario(visible = procesandoCreacion || mostrandoExito, creando = procesandoCreacion,
        mensajeExito = "Usuario creado con éxito")

    if (permisoUsuario != null) TarjetaPanel {            val jefe = personas.firstOrNull { it.id == permisoUsuario }?.rol == "JEFE_BODEGA"
            Text(if (jefe) "Asignación del jefe" else "Permisos del operador", fontWeight = FontWeight.Bold)
            SelectorSimple("Bodega", bodegas.filter { it.activa }.map { it.id to it.nombre }, permisoBodega) {
                permisoBodega = it
            }
            if (jefe) BotonAccion("Reasignar jefatura", enabled = !procesando && permisoBodega != null, onClick = {
                val uid = permisoUsuario ?: return@BotonAccion
                val bid = permisoBodega ?: return@BotonAccion
                procesando = true
                admin.reasignarJefeBodega(usuario, sid, uid, bid) { _, msg ->
                    procesando = false; mensaje = msg; permisoUsuario = null
                }
            })
            else {
            BotonAccion("Conceder acceso", enabled = !procesando && permisoBodega != null, onClick = {
                val uid = permisoUsuario ?: return@BotonAccion
                val bid = permisoBodega ?: return@BotonAccion
                procesando = true
                admin.asignarOperadorBodega(usuario, sid, uid, bid, false) { _, msg ->
                    procesando = false; mensaje = msg; permisoUsuario = null
                }
            })
            BotonAccion("Retirar acceso", outlined = true, enabled = !procesando && permisoBodega != null,
                onClick = {
                    val uid = permisoUsuario ?: return@BotonAccion
                    val bid = permisoBodega ?: return@BotonAccion
                    procesando = true
                    admin.asignarOperadorBodega(usuario, sid, uid, bid, true) { _, msg ->
                        procesando = false; mensaje = msg; permisoUsuario = null
                    }
                })
            }
        }
    }
}

// Muestra la pantalla de minimos admin.
@Composable
fun PantallaMinimosAdmin(base: OperadorViewModel, admin: AdministradorViewModel) {
    val usuario = base.sesion ?: return
    val sid = admin.sucursalActiva?.id ?: return
    // Datos observados por esta pantalla.
    val productos by remember { admin.productos() }.collectAsState(initial = emptyList())
    val minimos by remember(sid) { admin.minimos(sid) }.collectAsState(initial = emptyList())
    var busqueda by remember { mutableStateOf("") }
    var editando by remember { mutableStateOf<Long?>(null) }
    var valor by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var procesando by remember { mutableStateOf(false) }
    val mapa = minimos.associate { it.productoId to it.minimo }
    ColumnaOperador {
        TituloSeccion("CONFIGURACIÓN", "Stock mínimo", "Los mínimos afectan solo a esta sucursal")
        CampoFormulario("Buscar producto", busqueda, { busqueda = it })
        if (mensaje.isNotBlank()) Text(mensaje)
        productos.filter { it.codigo.contains(busqueda, true) || it.nombre.contains(busqueda, true) }.forEach { p ->
            TarjetaPanel {
                Text(p.nombre, fontWeight = FontWeight.SemiBold)
                Text("${p.codigo} · Mínimo: ${mapa[p.id] ?: p.minimo}" + if (p.id in mapa) " (sucursal)" else " (general)",
                    style = MaterialTheme.typography.bodySmall)
                if (editando == p.id) {
                    CampoFormulario("Nuevo mínimo", valor, { if (it.isEmpty() || (it.all(Char::isDigit) && it.length <= 6)) valor = it },
                        keyboardType = KeyboardType.Number)
                    BotonAccion("Guardar mínimo", enabled = !procesando && (valor.toIntOrNull() ?: -1) in 0..100000, onClick = {
                        procesando = true
                        admin.guardarMinimo(usuario, sid, p.id, valor.toIntOrNull()) { ok, msg ->
                            procesando = false; mensaje = msg; if (ok) editando = null
                        }
                    })
                    BotonAccion("Restaurar mínimo general", outlined = true, enabled = !procesando, onClick = {
                        procesando = true
                        admin.guardarMinimo(usuario, sid, p.id, null) { ok, msg ->
                            procesando = false; mensaje = msg; if (ok) editando = null
                        }
                    })
                } else BotonAccion("Configurar mínimo", outlined = true, onClick = {
                    editando = p.id; valor = (mapa[p.id] ?: p.minimo).toString()
                })
            }
        }
    }
}

// Muestra la pantalla de historial admin.
@Composable
fun PantallaHistorialAdmin(admin: AdministradorViewModel) {
    val sid = admin.sucursalActiva?.id ?: return
    // Datos observados por esta pantalla.
    val historial by remember(sid) { admin.historial(sid) }.collectAsState(initial = emptyList())
    ColumnaOperador {
        TituloSeccion("AUDITORÍA", "Historial administrativo", admin.sucursalActiva?.nombre)
        if (historial.isEmpty()) Text("No hay cambios administrativos registrados.")
        historial.forEach { evento ->
            TarjetaPanel {
                Text(evento.accion.replace('_', ' '), fontWeight = FontWeight.SemiBold)
                Text(evento.detalle)
                Text(formatoFecha(evento.fecha), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
