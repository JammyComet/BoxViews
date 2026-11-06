package com.example.boxview.ui.view

// Pantallas: Consulta del stock de bodega.
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import com.example.boxview.modelo.*
import com.example.boxview.ui.components.*
import com.example.boxview.viewmodel.OperadorViewModel

// Muestra la pantalla de stock jefe.
@Composable
fun PantallaStockJefe(base: OperadorViewModel) {
    // Datos observados por esta pantalla.
    val productos by base.productos.collectAsState(initial = emptyList())
    val posiciones by remember(base.bodegaActiva?.id) { base.posiciones() }.collectAsState(initial = emptyList())
    val existencias by remember(base.bodegaActiva?.id) { base.existencias() }.collectAsState(initial = emptyList())
    // unidades pendientes de despacho
    val compromisos by remember(base.bodegaActiva?.id) { base.compromisosSalida() }.collectAsState(initial = emptyList())
    val sid = base.bodegaActiva?.sucursalId ?: -1L
    val minimos by remember(sid) { base.minimosSucursal(sid) }.collectAsState(initial = emptyList())
    val mapaMinimos = minimos.associate { it.productoId to it.minimo }
    var filtro by remember { mutableStateOf("") }
    ColumnaOperador {
        TituloSeccion("JEFATURA", "Stock de la bodega", base.bodegaActiva?.nombre)
        CampoFormulario("Buscar producto o código", filtro, { filtro = it })
        productos.filter { it.nombre.contains(filtro, ignoreCase = true) ||
            it.codigo.contains(filtro, ignoreCase = true) }.forEach { p ->
            val stock = existencias.filter { it.productoId == p.id }.sumOf { it.cantidad }
            val reservado = compromisos.firstOrNull { it.productoId == p.id }?.cantidadSolicitada ?: 0
            val minimo = mapaMinimos[p.id] ?: p.minimo
            TarjetaPanel {
                Text("${p.codigo} · ${p.nombre}", fontWeight = FontWeight.Bold)
                Text("${p.categoria} · Stock físico: $stock")
                if (reservado > 0) {
                    Text("Pendiente de despacho: $reservado · Disponible para pedidos: ${(stock - reservado).coerceAtLeast(0)}",
                        style = MaterialTheme.typography.bodySmall)
                }
                if (stock <= minimo) EtiquetaEstado(if (stock == 0) "Agotado" else "Stock bajo", warning = true)
                posiciones.forEach { pos ->
                    val unidades = existencias.firstOrNull { it.productoId == p.id && it.posicionId == pos.id }?.cantidad ?: 0
                    if (unidades > 0) Text("${rutaBodega(pos, posiciones)}: $unidades unidades", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
