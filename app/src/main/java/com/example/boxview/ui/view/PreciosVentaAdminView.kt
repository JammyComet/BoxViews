package com.example.boxview.ui.view

// Pantallas: Precios de venta de la sucursal.
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.example.boxview.ui.components.*
import com.example.boxview.viewmodel.AdministradorViewModel
import com.example.boxview.viewmodel.OperadorViewModel
import java.text.NumberFormat
import java.util.Locale

private fun precioClpAdmin(v: Long): String = "$" + NumberFormat.getIntegerInstance(Locale("es", "CL")).format(v)

// Muestra la pantalla de precios venta admin.
@Composable
fun PantallaPreciosVentaAdmin(base: OperadorViewModel, admin: AdministradorViewModel) {
    val sid = admin.sucursalActiva?.id ?: return
    val usuario = base.sesion ?: return
    // Datos observados por esta pantalla.
    val productos by remember(sid) { admin.productos() }.collectAsState(initial = emptyList())
    val precios by remember(sid) { admin.preciosVenta(sid) }.collectAsState(initial = emptyList())
    var elegido by remember(sid) { mutableStateOf<Long?>(null) }
    var monto by remember(sid) { mutableStateOf("") }
    var ocupado by remember(sid) { mutableStateOf(false) }
    var mensaje by remember(sid) { mutableStateOf("") }
    val precioActual = precios.firstOrNull { it.productoId == elegido }?.precioClp
    ColumnaOperador {
        TituloSeccion("ADMINISTRACIÓN", "Precios de venta", "Valores CLP por producto y sucursal")
        TarjetaPanel {
            SelectorSimple("Producto", productos.map { it.id to "${it.codigo} · ${it.nombre}" }, elegido) {
                elegido = it
                monto = precios.firstOrNull { p -> p.productoId == it }?.precioClp?.toString().orEmpty()
                mensaje = ""
            }
            if (elegido != null) Text("Precio vigente: ${precioActual?.let(::precioClpAdmin) ?: "Sin configurar"}")
            CampoFormulario("Precio de venta (CLP)", monto, {
                if (it.length <= 9 && it.all(Char::isDigit)) monto = it
            }, keyboardType = KeyboardType.Number)
            val valor = monto.toLongOrNull()
            BotonAccion("Guardar precio", enabled = !ocupado && elegido != null && valor != null && valor in 1L..100_000_000L,
                onClick = {
                    ocupado = true
                    admin.configurarPrecioVenta(usuario, elegido!!, valor!!) { _, msg -> ocupado = false; mensaje = msg }
                })
            if (mensaje.isNotBlank()) Text(mensaje)
        }
        TituloSeccion("CATÁLOGO", "Precios configurados")
        productos.forEach { producto ->
            val precio = precios.firstOrNull { it.productoId == producto.id }
            TarjetaPanel {
                Text("${producto.codigo} · ${producto.nombre}")
                Text(precio?.precioClp?.let(::precioClpAdmin) ?: "Precio pendiente de configurar",
                    color = if (precio == null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}
