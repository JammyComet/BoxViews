package com.example.boxview.ui.view

// Pantallas: Personal a cargo del jefe de tienda.
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import androidx.compose.ui.text.font.FontWeight
import com.example.boxview.ui.components.*
import com.example.boxview.viewmodel.JefeTiendaViewModel
import com.example.boxview.viewmodel.OperadorViewModel
import com.example.boxview.viewmodel.TrabajadorTiendaViewModel

// Muestra la pantalla de equipo jefe tienda.
@Composable
fun PantallaEquipoJefeTienda(base: OperadorViewModel, tienda: TrabajadorTiendaViewModel,
                             jefe: JefeTiendaViewModel) {
    val sid = tienda.tiendaActiva?.id ?: return
    val usuario = base.sesion ?: return
    // Datos observados por esta pantalla.
    val asignados by remember(sid) { jefe.equipo(sid) }.collectAsState(initial = emptyList())
    val todos by remember { jefe.trabajadores() }.collectAsState(initial = emptyList())
    val formulario by jefe.trabajador.collectAsState()
    var nuevoId by remember(sid) { mutableStateOf<Long?>(null) }
    var mensaje by remember(sid) { mutableStateOf("") }
    var procesando by remember(sid) { mutableStateOf(false) }
    var procesandoCreacion by remember(sid) { mutableStateOf(false) }
    var retirarId by remember(sid) { mutableStateOf<Long?>(null) }
    var mostrandoExito by remember(sid) { mutableStateOf(false) }
    var formVersion by remember(sid) { mutableStateOf(0) }
    ColumnaOperador {
        TituloSeccion("JEFATURA", "Equipo de tienda", "Trabajadores de ${tienda.tiendaActiva?.nombre.orEmpty()}")
        key(formVersion) {
            TarjetaPanel {
                Text("Crear trabajador", fontWeight = FontWeight.Bold)
                CampoFormulario("Nombre completo", formulario.nombre, { jefe.cambiarNombre(it); mensaje = "" })
                CampoFormulario("Correo", formulario.correo, { jefe.cambiarCorreo(it); mensaje = "" })
                CampoFormulario("RUT con dígito verificador", formulario.rut, { jefe.cambiarRut(it); mensaje = "" })
                CampoFormulario("Contraseña inicial (mínimo 8 caracteres)", formulario.clave,
                    { jefe.cambiarClave(it); mensaje = "" }, esContrasena = true)
                BotonAccion("Crear y asignar trabajador", enabled = !procesando && !procesandoCreacion && formulario.nombre.trim().length >= 3 &&
                    correoBienFormado(formulario.correo) && com.example.boxview.viewmodel.rutValido(formulario.rut) && formulario.clave.length >= 8,
                    onClick = {
                        procesando = true
                        procesandoCreacion = true
                        jefe.crearTrabajador(usuario, sid, formulario.nombre, formulario.correo,
                            formulario.rut, formulario.clave) { ok, msg ->
                            procesando = false; procesandoCreacion = false; mensaje = msg
                            if (ok) {
                                jefe.reiniciarTrabajador()
                                formVersion += 1
                                mostrandoExito = true
                            }
                        }
                    })
            }
        }
        TarjetaPanel {
            Text("Asignar trabajador existente", fontWeight = FontWeight.Bold)
            val disponibles = todos.filter { t -> asignados.none { it.id == t.id } }
            SelectorSimple("Trabajador activo", disponibles.map { it.id to it.nombre }, nuevoId) { nuevoId = it }
            BotonAccion("Asignar a esta tienda", enabled = !procesando && nuevoId != null,
                outlined = true, onClick = {
                    procesando = true
                    jefe.asignarTrabajador(usuario, sid, nuevoId!!) { ok, msg ->
                        procesando = false; mensaje = msg
                        if (ok) nuevoId = null
                    }
                })
        }
        if (mensaje.isNotBlank()) TarjetaPanel { Text(mensaje) }
        TituloSeccion("PERSONAL", "Trabajadores asignados (${asignados.size})")
        if (asignados.isEmpty()) Text("No hay trabajadores asignados")
        asignados.forEach { trabajador ->
            TarjetaPanel {
                Text(trabajador.nombre, fontWeight = FontWeight.Bold)
                Text(trabajador.correo, style = MaterialTheme.typography.bodySmall)
                BotonAccion("Retirar acceso a esta tienda", outlined = true, enabled = !procesando,
                    onClick = { retirarId = trabajador.id })
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
        mensajeExito = "Trabajador creado con éxito")

    if (retirarId != null) AlertDialog(onDismissRequest = { retirarId = null },        title = { Text("¿Retirar acceso?") },
        text = { Text("La cuenta seguirá activa, pero no podrá entrar a esta tienda.") },
        confirmButton = { TextButton(enabled = !procesando, onClick = {
            val id = retirarId ?: return@TextButton
            procesando = true
            jefe.quitarTrabajador(usuario, sid, id) { _, msg ->
                procesando = false; retirarId = null; mensaje = msg
            }
        }) { Text("Retirar permiso") } },
        dismissButton = { TextButton(onClick = { retirarId = null }) { Text("Volver") } })
}
