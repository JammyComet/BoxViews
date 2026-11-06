package com.example.boxview.ui.components

// Componente: Componente para organizar y editar ubicaciones.
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import com.example.boxview.ui.view.SelectorSimple
import com.example.boxview.ui.view.ColumnaOperador

// datos de ubicaciones
data class UbicacionFila(
    val id: Long,
    val nombre: String,
    val ruta: String,
    val tipo: String,
    val padreId: Long?,
    val zona: String,
    val categoria: String,
    val activa: Boolean,
    val unidades: Int,
    val protegida: Boolean = false
)

// Muestra y edita estantes o posiciones.
@Composable
fun GestorUbicaciones(
    titulo: String,
    filas: List<UbicacionFila>,
    categorias: List<String>,
    tienda: Boolean,
    crear: (String, String, Long?, String, String, (Boolean, String) -> Unit) -> Unit,
    renombrar: (Long, String, (Boolean, String) -> Unit) -> Unit,
    cambiarEstado: (Long, Boolean, (Boolean, String) -> Unit) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("SECCION") }
    var padreId by remember { mutableStateOf<Long?>(null) }
    var zona by remember { mutableStateOf("ALMACEN") }
    var categoria by remember { mutableStateOf("General") }
    var mensaje by remember { mutableStateOf("") }
    var ocupado by remember { mutableStateOf(false) }
    val opcionesPadre = filas.filter {
        it.activa && it.tipo == (if (tipo == "ESTANTERIA") "SECCION" else "ESTANTERIA")
    }
    val padre = opcionesPadre.firstOrNull { it.id == padreId }
    val zonaFinal = if (padre != null) padre.zona else zona
    val categoriaFinal = if (padre != null) padre.categoria else categoria

    ColumnaOperador {
        TituloSeccion("UBICACIONES", titulo, "Secciones, estanterías y subestantes")
        TarjetaPanel {
            Text("Nueva ubicación", fontWeight = FontWeight.Bold)
            CampoFormulario("Nombre", nombre, { nombre = it.take(60) })
            SelectorSimple("Nivel", listOf(1L to "Sección o pasillo", 2L to "Estantería", 3L to "Subestante"),
                when (tipo) { "ESTANTERIA" -> 2L; "SUBESTANTE" -> 3L; else -> 1L }) { id ->
                tipo = when (id) { 2L -> "ESTANTERIA"; 3L -> "SUBESTANTE"; else -> "SECCION" }
                padreId = null
            }
            if (tipo != "SECCION") {
                SelectorSimple("Ubicación superior", opcionesPadre.map { it.id to it.ruta }, padreId) {
                    padreId = it
                }
                if (opcionesPadre.isEmpty()) Text("Primero se debe crear una ubicación del nivel anterior")
            }
            if (tienda) {
                if (padre == null) SelectorSimple("Zona", listOf(1L to "Sala de ventas", 2L to "Almacén"),
                    if (zona == "VENTAS") 1L else 2L) { zona = if (it == 1L) "VENTAS" else "ALMACEN" }
                if (padre == null) {
                    val opciones = (listOf("General") + categorias).distinct()
                    SelectorSimple("Categoría", opciones.mapIndexed { i, c -> (i + 1).toLong() to c },
                        (opciones.indexOf(categoria) + 1).toLong()) {
                        categoria = opciones[(it - 1).toInt()]
                    }
                } else Text("Zona: $zonaFinal · Categoría: $categoriaFinal")
            }
            BotonAccion("Crear ubicación", enabled = !ocupado && nombre.trim().length >= 2 &&
                (tipo == "SECCION" || padre != null), onClick = {
                ocupado = true
                crear(nombre, tipo, padre?.id, zonaFinal, categoriaFinal) { ok, msg ->
                    ocupado = false; mensaje = msg
                    if (ok) { nombre = ""; padreId = null }
                }
            })
        }
        if (mensaje.isNotEmpty()) TarjetaPanel { Text(mensaje) }
        TituloSeccion("REGISTRO", "Ubicaciones registradas")
        filas.sortedWith(compareBy<UbicacionFila> { it.ruta }.thenBy { it.id }).forEach { fila ->
            key(fila.id) {
                var editando by remember { mutableStateOf(false) }
                var nuevoNombre by remember { mutableStateOf(fila.nombre) }
                TarjetaPanel {
                    Text(fila.ruta, fontWeight = FontWeight.Bold)
                    val totalConInferiores = filas.filter { otra ->
                        otra.id == fila.id || otra.ruta.startsWith("${fila.ruta} / ")
                    }.sumOf { it.unidades }
                    val resumenUnidades = if (filas.any { it.padreId == fila.id })
                        "${fila.unidades} directas · $totalConInferiores en total"
                    else "${fila.unidades} unidades"
                    Text("${when(fila.tipo) { "SECCION" -> "Sección"; "SUBESTANTE" -> "Subestante"; "RECEPCION" -> "Recepción"; else -> "Estantería" }} · $resumenUnidades · ${if (fila.activa) "Activa" else "Inactiva"}",
                        style = MaterialTheme.typography.bodySmall)
                    if (tienda) Text("${fila.zona} · ${fila.categoria}", style = MaterialTheme.typography.bodySmall)
                    if (!fila.protegida) {
                        if (editando) {
                            CampoFormulario("Nuevo nombre", nuevoNombre, { nuevoNombre = it.take(60) })
                            BotonAccion("Guardar nombre", enabled = !ocupado && nuevoNombre.trim().length >= 2,
                                onClick = {
                                    ocupado = true
                                    renombrar(fila.id, nuevoNombre) { ok, msg ->
                                        ocupado = false; mensaje = msg
                                        if (ok) editando = false
                                    }
                                })
                            BotonAccion("Cancelar edición", outlined = true,
                                onClick = { editando = false; nuevoNombre = fila.nombre })
                        } else {
                            BotonAccion("Cambiar nombre", outlined = true, onClick = {
                                nuevoNombre = fila.nombre; editando = true
                            })
                        }
                        BotonAccion(if (fila.activa) "Desactivar" else "Activar", outlined = true,
                            enabled = !ocupado, onClick = {
                                ocupado = true
                                cambiarEstado(fila.id, !fila.activa) { _, msg ->
                                    ocupado = false; mensaje = msg
                                }
                            })
                    }
                }
            }
        }
    }
}
