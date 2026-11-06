package com.example.boxview.ui.view

// Pantallas: Registro de salidas de tienda.
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType
import com.example.boxview.modelo.*
import com.example.boxview.ui.components.*
import com.example.boxview.viewmodel.OperadorViewModel
import com.example.boxview.viewmodel.TrabajadorTiendaViewModel

// Muestra la pantalla de salidas tienda.
@Composable
fun PantallaSalidasTienda(base: OperadorViewModel, tienda: TrabajadorTiendaViewModel) {
    val sid = tienda.tiendaActiva?.id ?: return
    // Datos observados por esta pantalla.
    val productos by base.productos.collectAsState(initial = emptyList())
    val posiciones by remember(sid) { tienda.todasLasPosiciones() }.collectAsState(initial = emptyList())
    val saldos by remember(sid) { tienda.stockPorUbicacion() }.collectAsState(initial = emptyList())
    val inventario by remember(sid) { tienda.inventarioTiendaAbierto() }.collectAsState(initial = null)
    val campanias by remember(sid) { tienda.campaniasMensualesTienda() }.collectAsState(initial = emptyList())
    val precios by remember(sid) { tienda.preciosVentaTienda() }.collectAsState(initial = emptyList())
    val bloqueandoEstantes = inventario != null || campanias.any { it.estado == "ABIERTA" }
    var productoId by remember(sid) { mutableStateOf<Long?>(null) }
    var posicionId by remember(sid) { mutableStateOf<Long?>(null) }
    var tipo by remember(sid) { mutableStateOf("VENTA") }
    var cantidad by remember(sid) { mutableStateOf("") }
    var motivo by remember(sid) { mutableStateOf("") }
    var mensaje by remember(sid) { mutableStateOf("") }
    var ocupado by remember(sid) { mutableStateOf(false) }
    val opciones = saldos.filter { it.productoId == productoId && it.cantidad > 0 }
        .mapNotNull { saldo -> posiciones.firstOrNull { it.id == saldo.posicionId && it.tipo != "SECCION" }
            ?.takeIf { !bloqueandoEstantes || it.tipo == "RECEPCION" }
            ?.let { pos -> pos.id to "${rutaTienda(pos, posiciones)} · ${saldo.cantidad} unidades" } }
    val disponible = saldos.firstOrNull { it.productoId == productoId && it.posicionId == posicionId }?.cantidad ?: 0
    ColumnaOperador {
        TituloSeccion("OPERACIONES", "Salida de mercadería", "Ventas, mermas y retiros registrados")
        if (bloqueandoEstantes) Text("Inventario activo · Salidas desde recepción",
            color = MaterialTheme.colorScheme.error)
        TarjetaPanel {
            SelectorSimple("Producto", productos.map { it.id to "${it.codigo} · ${it.nombre}" }, productoId) {
                productoId = it; posicionId = null; mensaje = ""
            }
            SelectorSimple("Ubicación de origen", opciones, posicionId) { posicionId = it; mensaje = "" }
            Text("Disponible en la ubicación: $disponible unidades", style = MaterialTheme.typography.bodySmall)
            SelectorSimple("Tipo de salida", listOf(1L to "Venta", 2L to "Merma", 3L to "Retiro"),
                when (tipo) { "MERMA" -> 2L; "RETIRO" -> 3L; else -> 1L }) {
                tipo = when (it) { 2L -> "MERMA"; 3L -> "RETIRO"; else -> "VENTA" }
            }
            CampoFormulario("Cantidad", cantidad, { if (it.isEmpty() || (it.all(Char::isDigit) && it.length <= 6)) cantidad = it },
                keyboardType = KeyboardType.Number)
            CampoFormulario("Motivo o referencia", motivo, { motivo = it.take(250) })
            val unidades = cantidad.toIntOrNull() ?: 0
            val precioVenta = precios.firstOrNull { it.productoId == productoId }?.precioClp
            if (tipo == "VENTA") {
                Text("Precio unitario: ${precioVenta?.let { "$" + java.text.NumberFormat.getIntegerInstance(java.util.Locale("es", "CL")).format(it) } ?: "Pendiente de Administración"}")
                if (precioVenta != null && unidades > 0) {
                    val total = precioVenta * unidades.toLong()
                    Text("Total automático: $" + java.text.NumberFormat.getIntegerInstance(java.util.Locale("es", "CL")).format(total) + " CLP")
                }
            }
            BotonAccion("Registrar salida", icon = Icons.Default.RemoveShoppingCart,
                enabled = !ocupado && (tipo != "VENTA" || precioVenta != null) && productoId != null && posicionId != null &&
                    (!bloqueandoEstantes || posiciones.any { it.id == posicionId && it.tipo == "RECEPCION" }) &&
                    opciones.any { it.first == posicionId } && unidades in 1..disponible && motivo.trim().length >= 5, onClick = {
                    ocupado = true
                    tienda.registrarSalidaTienda(base.sesion, productoId!!, posicionId!!, unidades, tipo, motivo) { ok, msg ->
                        ocupado = false; mensaje = msg
                        if (ok) { cantidad = ""; motivo = "" }
                    }
                })
            if (mensaje.isNotBlank()) Text(mensaje)
        }
    }
}
