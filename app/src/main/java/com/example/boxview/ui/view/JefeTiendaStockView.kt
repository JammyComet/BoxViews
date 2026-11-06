package com.example.boxview.ui.view

// Pantallas: Stock y diferencias de tienda.
import com.example.boxview.modelo.*

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.boxview.ui.components.*
import com.example.boxview.viewmodel.OperadorViewModel
import com.example.boxview.viewmodel.TrabajadorTiendaViewModel

// Muestra la pantalla de stock jefe tienda.
@Composable
fun PantallaStockJefeTienda(base: OperadorViewModel, tienda: TrabajadorTiendaViewModel) {
    val sid = tienda.tiendaActiva?.id ?: return
    // Datos observados por esta pantalla.
    val productos by base.productos.collectAsState(initial = emptyList())
    val stock by remember(sid) { tienda.existencias() }.collectAsState(initial = emptyList())
    val precios by remember(sid) { tienda.preciosVentaTienda() }.collectAsState(initial = emptyList())
    val minimos by remember(sid) { base.minimosSucursal(sid) }.collectAsState(initial = emptyList())
    val mapaMinimos = minimos.associate { it.productoId to it.minimo }
    val diferencias by remember(sid) { tienda.diferenciasStock() }.collectAsState(initial = emptyList())
    val ubicaciones by remember(sid) { tienda.todasLasPosiciones() }.collectAsState(initial = emptyList())
    val stockUbicado by remember(sid) { tienda.stockPorUbicacion() }.collectAsState(initial = emptyList())
    var filtro by remember(sid) { mutableStateOf("") }
    ColumnaOperador {
        TituloSeccion("JEFATURA", "Stock de la tienda", "Consulta de existencias de ${tienda.tiendaActiva?.nombre.orEmpty()}")
        if (diferencias.isNotEmpty()) {
            Text("Hay ${diferencias.size} diferencia(s) entre stock y ubicaciones. Revisa el inventario.",
                color = MaterialTheme.colorScheme.error)
        }
        CampoFormulario("Buscar producto", filtro, { filtro = it })
        productos.filter { it.nombre.contains(filtro, true) || it.codigo.contains(filtro, true) }.forEach { producto ->
            val n = stock.firstOrNull { it.productoId == producto.id }?.cantidad ?: 0
            val minimo = mapaMinimos[producto.id] ?: producto.minimo
            TarjetaPanel {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(producto.nombre, style = MaterialTheme.typography.titleMedium)
                        Text("${producto.codigo} · ${producto.categoria}", style = MaterialTheme.typography.bodySmall)
                        val precio = precios.firstOrNull { it.productoId == producto.id }?.precioClp
                        Text("Precio venta: " + (precio?.let { "$" +
                            java.text.NumberFormat.getIntegerInstance(java.util.Locale("es", "CL")).format(it) + " CLP" }
                            ?: "Sin configurar"), style = MaterialTheme.typography.bodySmall)
                    }
                    EtiquetaEstado("$n unidades", warning = n <= minimo)
                }
                if (n <= minimo) Text("${if (n == 0 || n <= minimo / 2) "Stock crítico" else "Stock bajo"} · mínimo $minimo",
                    color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                diferencias.firstOrNull { it.productoId == producto.id }?.let { d ->
                    Text("Diferencia: total ${d.stockGeneral} · ubicaciones ${d.stockUbicaciones}",
                        color = MaterialTheme.colorScheme.error)
                }
                stockUbicado.filter { it.productoId == producto.id && it.cantidad > 0 }.forEach { e ->
                    val u = ubicaciones.firstOrNull { it.id == e.posicionId }
                    Text("${u?.let { rutaTienda(it, ubicaciones) } ?: "Ubicación ${e.posicionId}"} · ${e.cantidad} unidades" +
                        if (u?.activa == false) " · inactiva" else "", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
