package com.example.boxview.ui.view

// Pantallas: Pantallas compartidas de operaciones.
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import com.example.boxview.modelo.*
import com.example.boxview.ui.components.*
import com.example.boxview.viewmodel.OperadorViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Componente visual de columna operador.
@Composable
fun ColumnaOperador(contenido: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp), content = contenido)
}

fun formatoFecha(valor: Long): String = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CL")).format(Date(valor))

// Componente visual de selector simple.
@Composable
fun SelectorSimple(etiqueta: String, opciones: List<Pair<Long, String>>, elegido: Long?, alElegir: (Long) -> Unit) {
    var expandido by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(etiqueta, style = MaterialTheme.typography.labelMedium)
        Box {
            OutlinedButton(onClick = { expandido = true }, modifier = Modifier.fillMaxWidth()) {
                Text(opciones.firstOrNull { it.first == elegido }?.second ?: "Seleccionar", modifier = Modifier.weight(1f))
                Text("▾")
            }
            DropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
                opciones.forEach { (id, titulo) ->
                    DropdownMenuItem(text = { Text(titulo) }, onClick = { expandido = false; alElegir(id) })
                }
            }
        }
    }
}

// Muestra la pantalla de traslado operador.
@Composable
fun PantallaTrasladoOperador(vm: OperadorViewModel) {
    // Datos observados por esta pantalla.
    val productos by vm.productos.collectAsState(initial = emptyList())
    val posiciones by remember(vm.bodegaActiva?.id) { vm.posiciones() }.collectAsState(initial = emptyList())
    val existencias by remember(vm.bodegaActiva?.id) { vm.existencias() }.collectAsState(initial = emptyList())
    val reservas by remember(vm.bodegaActiva?.id) { vm.reservasOtrasCajas() }.collectAsState(initial = emptyList())
    // Selecciones temporales del traslado.
    var producto by remember(vm.bodegaActiva?.id) { mutableStateOf<Long?>(null) }
    var origen by remember(vm.bodegaActiva?.id) { mutableStateOf<Long?>(null) }
    var destino by remember(vm.bodegaActiva?.id) { mutableStateOf<Long?>(null) }
    var cantidad by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    ColumnaOperador {
        TituloSeccion("MOVIMIENTOS", "Traslado interno", "Movimiento entre estanterías de la bodega")
        TarjetaPanel {
            SelectorSimple("Producto", productos.map { it.id to "${it.codigo} · ${it.nombre}" }, producto) {
                producto = it; origen = null; mensaje = ""
            }
            fun stockPosicion(id: Long): Int = existencias.firstOrNull { it.productoId == producto && it.posicionId == id }?.cantidad ?: 0
            fun reservadoPosicion(id: Long): Int = reservas.firstOrNull { it.productoId == producto && it.posicionId == id }?.cantidadReservada ?: 0
            fun librePosicion(id: Long): Int = (stockPosicion(id) - reservadoPosicion(id)).coerceAtLeast(0)
            val disponibles = posiciones.filter { pos -> (librePosicion(pos.id) > 0 || pos.id == origen) && pos.tipo != "SECCION" }
            SelectorSimple("Desde la posición", disponibles.map { it.id to "${rutaBodega(it, posiciones)} · ${librePosicion(it.id)} libres" }, origen) { origen = it; mensaje = "" }
            val categoriaElegida = productos.firstOrNull { it.id == producto }?.categoria.orEmpty()
            val destinos = posiciones.filter { it.id != origen && it.activa && it.tipo != "SECCION" }.sortedWith(
                compareByDescending<Posicion> { it.codigo.endsWith("· $categoriaElegida") }.thenBy { it.codigo }
            )
            SelectorSimple("Hacia la posición", destinos.map { it.id to rutaBodega(it, posiciones) }, destino) {
                destino = it; mensaje = ""
            }
            if (categoriaElegida.isNotBlank()) Text("Sección recomendada para el producto: $categoriaElegida",
                style = MaterialTheme.typography.bodySmall)
            val stock = origen?.let { stockPosicion(it) } ?: 0
            val reservado = origen?.let { reservadoPosicion(it) } ?: 0
            val libre = (stock - reservado).coerceAtLeast(0)
            val totalBodega = existencias.filter { it.productoId == producto }.sumOf { it.cantidad }
            Text("Stock total en bodega: $totalBodega unidades", style = MaterialTheme.typography.bodySmall)
            Text("En origen: $stock · Reservadas: $reservado · Disponibles para traslado: $libre",
                style = MaterialTheme.typography.bodySmall)
            CampoFormulario("Cantidad", cantidad, { cantidad = it; mensaje = "" }, keyboardType = KeyboardType.Number)
            if (mensaje.isNotBlank()) Text(mensaje)
            // Envia los datos seleccionados al ViewModel.
            BotonAccion("Guardar traslado", enabled = producto != null && origen != null && destino != null &&
                cantidad.toIntOrNull()?.let { it > 0 && it <= libre } == true,
                onClick = {
                    vm.trasladar(producto!!, origen!!, destino!!, cantidad.toInt()) { ok, msg ->
                        mensaje = msg
                        if (ok) { cantidad = ""; origen = null; destino = null }
                    }
                })
        }
    }
}

// Muestra la pantalla de preparar despacho.
@Composable
fun PantallaPrepararDespacho(vm: OperadorViewModel, ir: (String) -> Unit) {
    // Datos observados por esta pantalla.
    val ordenes by remember(vm.bodegaActiva?.id, vm.sesion?.id) { vm.ordenesSalida() }
        .collectAsState(initial = emptyList())
    val tiendas by vm.sucursales.collectAsState(initial = emptyList())
    val enviadas by remember(vm.bodegaActiva?.id, vm.sesion?.id) { vm.salidasEnviadas() }
        .collectAsState(initial = emptyList())
    var error by remember { mutableStateOf("") }
    fun abrir(codigo: String) {
        vm.abrirCaja(codigo, "SALIDA") { ok, mensaje ->
            if (ok) ir(Destinos.DetalleCaja.ruta) else error = mensaje
        }
    }
    ColumnaOperador {
        TituloSeccion("TAREAS ASIGNADAS", "Salidas pendientes", "Órdenes recibidas de jefatura")
        if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
        if (ordenes.isEmpty()) Text("No hay órdenes asignadas en esta bodega.")
        ordenes.forEach { orden ->
            val destino = tiendas.find { it.id == orden.sucursalDestinoId }?.nombre ?: "Salida histórica"
            FilaOpcion(orden.codigo, "Destino: $destino · ${orden.solicitadoPor}",
                icon = androidx.compose.material.icons.Icons.Default.LocalShipping,
                badge = if (orden.estado == "ORDEN_ASIGNADA") "Por preparar" else "Continuar preparación",
                warning = orden.estado == "ORDEN_ASIGNADA", onClick = { abrir(orden.codigo) })
        }
        if (enviadas.isNotEmpty()) {
            TituloSeccion("DESPACHOS", "Salidas enviadas", "Consultar QR de las cajas despachadas")
            enviadas.forEach { caja ->
                FilaOpcion(caja.codigo, "${tiendas.find { it.id == caja.sucursalDestinoId }?.nombre ?: "Salida histórica"} · ${estadoDespachoParaBodega(caja.estado)}",
                    icon = androidx.compose.material.icons.Icons.Default.LocalShipping,
                    badge = "Ver QR", onClick = {
                        vm.abrirCaja(caja.codigo, "CONSULTA_SALIDA") { ok, msg ->
                            if (ok) ir(Destinos.DetalleCaja.ruta) else error = msg
                        }
                    })
            }
        }
    }
}

