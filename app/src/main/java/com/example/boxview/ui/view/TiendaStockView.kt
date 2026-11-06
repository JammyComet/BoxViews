package com.example.boxview.ui.view

// Pantallas: Consulta de stock disponible en tienda.
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.boxview.modelo.*
import com.example.boxview.ui.components.*
import com.example.boxview.viewmodel.OperadorViewModel
import com.example.boxview.viewmodel.TrabajadorTiendaViewModel

// Muestra la pantalla de stock tienda.
@Composable
fun PantallaStockTienda(vm: OperadorViewModel, tiendaVm: TrabajadorTiendaViewModel, ir: (String) -> Unit) {
    val sid = tiendaVm.tiendaActiva?.id
    // Datos observados por esta pantalla.
    val productos by vm.productos.collectAsState(initial = emptyList())
    val existencias by remember(sid) { tiendaVm.existencias() }.collectAsState(initial = emptyList())
    val ubicaciones by remember(sid) { tiendaVm.todasLasPosiciones() }.collectAsState(initial = emptyList())
    val precios by remember(sid) { tiendaVm.preciosVentaTienda() }.collectAsState(initial = emptyList())
    val stockUbicado by remember(sid) { tiendaVm.stockPorUbicacion() }.collectAsState(initial = emptyList())
    var filtro by remember(sid) { mutableStateOf("") }
    var escaneando by remember(sid) { mutableStateOf(false) }
    var consultaQr by remember(sid) { mutableStateOf("") }
    if (escaneando) ModalEscaner("Consultar stock por QR o código de barras",
        cerrar = { escaneando = false }) { leido ->
        escaneando = false
        filtro = leido.trim()
        consultaQr = leido.trim()
    }
    ColumnaOperador {
        TituloSeccion("EXISTENCIAS", "Stock de tienda", "${tiendaVm.tiendaActiva?.nombre.orEmpty()} · Sala de ventas")
        BotonAccion("Mover entre estanterías", onClick = { ir(Destinos.TiendaTraslados.ruta) },
            outlined = true, icon = androidx.compose.material.icons.Icons.Default.MoveUp)
        BotonAccion("Escanear producto para consultar stock", outlined = true,
            onClick = { consultaQr = ""; escaneando = true },
            icon = androidx.compose.material.icons.Icons.Default.QrCodeScanner)
        CampoFormulario("Buscar producto o código", filtro, { filtro = it; consultaQr = "" })
        if (consultaQr.isNotBlank() && productos.none { it.codigo.equals(consultaQr, ignoreCase = true) }) {
            Text("El código leído no corresponde a un producto del catálogo.",
                color = MaterialTheme.colorScheme.error)
        }
        val encontrados = productos.filter { it.nombre.contains(filtro, ignoreCase = true) ||
            it.codigo.contains(filtro, ignoreCase = true) }
        if (encontrados.isEmpty()) Text("No hay productos que coincidan con el código o búsqueda.")
        encontrados.forEach { p ->
                val n = existencias.firstOrNull { it.productoId == p.id }?.cantidad ?: 0
                TarjetaPanel {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(p.nombre, style = MaterialTheme.typography.titleMedium)
                            Text("${p.codigo} · ${p.categoria}", style = MaterialTheme.typography.bodySmall)
                            val precio = precios.firstOrNull { it.productoId == p.id }?.precioClp
                            Text("Precio venta: " + (precio?.let { "$" + java.text.NumberFormat.getIntegerInstance(java.util.Locale("es", "CL")).format(it) + " CLP" } ?: "Sin configurar"), style = MaterialTheme.typography.bodySmall)
                        }
                        EtiquetaEstado(if (n == 0) "Agotado" else "$n unidades", warning = n == 0)
                    }
                    stockUbicado.filter { it.productoId == p.id && it.cantidad > 0 }.forEach { e ->
                        val ubicacion = ubicaciones.firstOrNull { it.id == e.posicionId }
                        Text(
                            "${if (ubicacion == null) "Ubicación #${e.posicionId}" else if (ubicacion.zona == "RECEPCION") "Zona de recepción" else rutaTienda(ubicacion, ubicaciones)}" +
                                "${if (ubicacion?.activa == false) " (inactiva)" else ""} · ${e.cantidad} unidades",
                            style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
    }
}
