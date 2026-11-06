package com.example.boxview.ui.view

// Pantallas: Equipo y permisos del jefe de bodega.
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import androidx.compose.ui.text.font.FontWeight
import com.example.boxview.ui.components.*
import com.example.boxview.viewmodel.JefeBodegaViewModel
import com.example.boxview.viewmodel.OperadorViewModel

// Muestra la pantalla de equipo jefe.
@Composable
fun PantallaEquipoJefe(base: OperadorViewModel, jefe: JefeBodegaViewModel) {
    val bodega = base.bodegaActiva ?: return
    val usuario = base.sesion ?: return
    // Datos observados por esta pantalla.
    val asignados by remember(bodega.id) { jefe.operadores(bodega.id) }.collectAsState(initial = emptyList())
    val todos by remember(base.bodegaActiva?.id) { jefe.todosOperadores(base.bodegaActiva?.id ?: -1) }.collectAsState(initial = emptyList())
    // Estado del formulario de operador.
    val formulario by jefe.formularioOperador.collectAsState()
    val nombre = formulario.nombre
    val correo = formulario.correo
    val rut = formulario.rut
    val clave = formulario.clave
    var existente by remember { mutableStateOf<Long?>(null) }
    var procesando by remember { mutableStateOf(false) }
    var procesandoCreacion by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }
    var retirar by remember { mutableStateOf<Long?>(null) }
    var mostrandoExito by remember { mutableStateOf(false) }
    var formVersion by remember { mutableStateOf(0) }

    ColumnaOperador {
        TituloSeccion("JEFATURA", "Equipo y permisos", "Operadores autorizados en ${bodega.nombre}")
        key(formVersion) {
            TarjetaPanel {
                Text("Crear operador de bodega", fontWeight = FontWeight.Bold)
                CampoFormulario("Nombre completo", nombre, { jefe.cambiarNombreOperador(it); mensaje = "" })
                CampoFormulario("Correo", correo, { jefe.cambiarCorreoOperador(it); mensaje = "" })
                CampoFormulario("RUT (con dígito verificador)", rut, { jefe.cambiarRutOperador(it); mensaje = "" })
                CampoFormulario("Contraseña inicial (mínimo 8 caracteres)", clave, { jefe.cambiarClaveOperador(it); mensaje = "" }, esContrasena = true)
                BotonAccion("Crear y asignar operador", enabled = !procesando && !procesandoCreacion && nombre.trim().length >= 3 &&
                    correoBienFormado(correo) && clave.length >= 8 && com.example.boxview.viewmodel.rutValido(rut), onClick = {
                    procesando = true
                    procesandoCreacion = true
                    // Solicitud de alta al ViewModel.
                    jefe.crearOperador(usuario, bodega.id, nombre, correo, clave, rut) { ok, msg ->
                        procesando = false; procesandoCreacion = false; mensaje = msg
                        if (ok) {
                            jefe.reiniciarFormularioOperador()
                            formVersion += 1
                            mostrandoExito = true
                        }
                    }
                })
            }
        }
        TarjetaPanel {
            Text("Asignar un operador existente", fontWeight = FontWeight.Bold)
            val disponibles = todos.filter { operador -> asignados.none { it.id == operador.id } }
            SelectorSimple("Operador", disponibles.map { it.id to "${it.nombre} · ${it.correo}" }, existente) {
                existente = it
            }
            BotonAccion("Autorizar para esta bodega", enabled = !procesando && existente != null, onClick = {
                procesando = true
                jefe.asignarOperador(usuario, bodega.id, existente!!) { ok, msg ->
                    procesando = false; mensaje = msg
                    if (ok) existente = null
                }
            })
        }
        if (mensaje.isNotBlank()) TarjetaPanel { Text(mensaje) }
        Text("Operadores asignados", fontWeight = FontWeight.Bold)
        if (asignados.isEmpty()) Text("No hay operadores asignados")
        asignados.forEach { operador ->
            TarjetaPanel {
                Text(operador.nombre, fontWeight = FontWeight.Bold)
                Text(operador.correo)
                operador.rut?.let { Text("RUT: $it") }
                BotonAccion("Quitar acceso a esta bodega", outlined = true,
                    onClick = { retirar = operador.id })
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
        mensajeExito = "Operador creado con éxito")

    if (retirar != null) AlertDialog(onDismissRequest = { retirar = null },        title = { Text("¿Quitar acceso a esta bodega?") },
        text = { Text("La cuenta no se elimina. No se puede quitar acceso si tiene órdenes o inventarios pendientes.") },
        confirmButton = { TextButton(enabled = !procesando, onClick = {
            val id = retirar ?: return@TextButton
            procesando = true
            jefe.retirarOperador(usuario, bodega.id, id) { _, msg ->
                procesando = false; mensaje = msg; retirar = null
            }
        }) { Text("Quitar permiso") } },
        dismissButton = { TextButton(onClick = { retirar = null }) { Text("Volver") } })
}
