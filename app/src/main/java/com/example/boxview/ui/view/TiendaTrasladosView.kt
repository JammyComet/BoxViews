package com.example.boxview.ui.view

// Pantallas: Traslados de productos entre ubicaciones.
import com.example.boxview.modelo.*

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import com.example.boxview.ui.components.*
import com.example.boxview.viewmodel.OperadorViewModel
import com.example.boxview.viewmodel.TrabajadorTiendaViewModel

// traslado entre posiciones de tienda
// Muestra la pantalla de traslados tienda.
@Composable
fun PantallaTrasladosTienda(vm: OperadorViewModel, tiendaVm: TrabajadorTiendaViewModel) {
    val sid = tiendaVm.tiendaActiva?.id
    // Datos observados por esta pantalla.
    val productos by vm.productos.collectAsState(initial = emptyList())
    val posiciones by remember(sid) { tiendaVm.posiciones() }.collectAsState(initial = emptyList())
    val todasLasPosiciones by remember(sid) { tiendaVm.todasLasPosiciones() }.collectAsState(initial = emptyList())
    val saldos by remember(sid) { tiendaVm.stockPorUbicacion() }.collectAsState(initial = emptyList())
    val formulario by tiendaVm.formularioTraslado.collectAsState()
    val productoId = formulario.productoId
    val origenId = formulario.origenId
    val destinoId = formulario.destinoId
    val cantidad = formulario.cantidad
    var ocupado by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }
    val producto = productos.firstOrNull { it.id == productoId }
    val ubicacionesOrigen = saldos.filter { it.productoId == productoId && it.cantidad > 0 }
        .mapNotNull { s -> todasLasPosiciones.firstOrNull { it.id == s.posicionId }?.let { it to s.cantidad } }
    val ubicacionesDestino = posiciones.filter { it.categoria == "General" || it.categoria == producto?.categoria }
    val origen = saldos.firstOrNull { it.productoId == productoId && it.posicionId == origenId }?.cantidad ?: 0
    fun nombre(zona: String, codigo: String): String =
        if (zona == "RECEPCION") "Zona de recepción" else
            "$codigo · ${if (zona == "VENTAS") "Sala de ventas" else "Almacén"}"
    ColumnaOperador {
        TituloSeccion("UBICACIONES", "Mover productos", "${tiendaVm.tiendaActiva?.nombre.orEmpty()} · Traslado interno de tienda")
        TarjetaPanel {
            Text("Colocar mercadería en estanterías", style = MaterialTheme.typography.titleMedium)
            SelectorSimple("Producto", productos.map { it.id to "${it.codigo} · ${it.nombre}" }, productoId) {
                tiendaVm.elegirProductoTraslado(it); mensaje = ""
            }
            if (producto != null) {
                SelectorSimple("Desde la ubicación (solo con stock)", ubicacionesOrigen.map { (p,n) ->
                    p.id to "${nombre(p.zona, rutaTienda(p, todasLasPosiciones))}${if (!p.activa) " (inactiva)" else ""} · $n disponibles"
                }, origenId) { tiendaVm.elegirOrigenTraslado(it); mensaje = "" }
                Text("Stock en origen: $origen unidades", style = MaterialTheme.typography.bodySmall)
                SelectorSimple("Hacia la ubicación", ubicacionesDestino.map { it.id to nombre(it.zona, rutaTienda(it, todasLasPosiciones)) }, destinoId) {
                    tiendaVm.elegirDestinoTraslado(it); mensaje = ""
                }
                CampoFormulario("Cantidad a trasladar", cantidad, { tiendaVm.cambiarCantidadTraslado(it) },
                    keyboardType = KeyboardType.Number)
                val n = cantidad.toIntOrNull() ?: 0
                BotonAccion("Guardar traslado", enabled = !ocupado && origenId != null && destinoId != null &&
                    origenId != destinoId && n > 0 && n <= origen,
                    icon = Icons.Default.MoveUp, onClick = {
                        ocupado = true
                        tiendaVm.trasladarEnTienda(vm.sesion, productoId!!, origenId!!, destinoId!!, n) { ok, msg ->
                            ocupado = false; mensaje = msg
                            if (ok) tiendaVm.elegirProductoTraslado(productoId)
                        }
                    })
            }
            if (mensaje.isNotBlank()) Text(mensaje, color = MaterialTheme.colorScheme.secondary)
        }
    }
}
