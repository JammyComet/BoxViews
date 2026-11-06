package com.example.boxview.ui.view

// Pantallas: Historial del jefe de tienda.
import com.example.boxview.modelo.*

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import com.example.boxview.ui.components.*
import com.example.boxview.viewmodel.OperadorViewModel
import com.example.boxview.viewmodel.TrabajadorTiendaViewModel

// Muestra la pantalla de historial jefe tienda.
@Composable
fun PantallaHistorialJefeTienda(base: OperadorViewModel, tienda: TrabajadorTiendaViewModel) {
    val sid = tienda.tiendaActiva?.id ?: return
    // Datos observados por esta pantalla.
    val productos by base.productos.collectAsState(initial = emptyList())
    val cajas by remember(sid) { tienda.recibidas() }.collectAsState(initial = emptyList())
    val eventos by remember(sid) { tienda.eventosRecepcion() }.collectAsState(initial = emptyList())
    val movimientos by remember(sid) { tienda.movimientosUbicaciones() }.collectAsState(initial = emptyList())
    val ubicaciones by remember(sid) { tienda.todasLasPosiciones() }.collectAsState(initial = emptyList())
    val salidas by remember(sid) { tienda.salidas() }.collectAsState(initial = emptyList())
    val inventarios by remember(sid) { tienda.inventariosTienda() }.collectAsState(initial = emptyList())
    ColumnaOperador {
        TituloSeccion("JEFATURA", "Historial de tienda", "Recepciones y movimientos de ${tienda.tiendaActiva?.nombre.orEmpty()}")
        TituloSeccion("CAJAS", "Recepciones registradas")
        if (cajas.isEmpty()) Text("No hay recepciones registradas")
        cajas.forEach { caja ->
            TarjetaPanel {
                Text(caja.codigo, fontWeight = FontWeight.Bold)
                Text("${formatoFecha(caja.fecha)} · ${caja.estado}", style = MaterialTheme.typography.bodySmall)
                eventos.filter { it.cajaId == caja.id }.forEach { e ->
                    Text("${formatoFecha(e.fecha)} · ${e.accion.replace('_', ' ')}",
                        style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        TituloSeccion("SALIDAS", "Ventas, mermas y retiros")
        salidas.forEach { salida ->
            val producto = productos.firstOrNull { it.id == salida.productoId }
            TarjetaPanel {
                Text("${salida.tipo} · ${producto?.nombre ?: salida.productoId}", fontWeight = FontWeight.Bold)
                Text("${salida.cantidad} unidades · ${formatoFecha(salida.fecha)}")
                Text("Motivo: ${motivoVisibleHistorial(salida.motivo, salida.tipo)}", style = MaterialTheme.typography.bodySmall)
            }
        }
        TituloSeccion("INVENTARIOS", "Conteos de tienda")
        inventarios.forEach { inv ->
            Text("${inv.nombre} · ${inv.estado} · ${formatoFecha(inv.fechaApertura)}")
            if (inv.estado == "CERRADO" && inv.motivoCierre.isNotBlank())
                Text("Justificación: ${inv.motivoCierre}", style = MaterialTheme.typography.bodySmall)
        }
        TituloSeccion("MOVIMIENTOS", "Estanterías")
        if (movimientos.isEmpty()) Text("Todavía no hay movimientos de ubicaciones")
        movimientos.forEach { m ->
            val producto = productos.firstOrNull { it.id == m.productoId }
            val origen = ubicaciones.firstOrNull { it.id == m.posicionOrigenId }?.let { rutaTienda(it, ubicaciones) } ?: "Recepción"
            val destino = ubicaciones.firstOrNull { it.id == m.posicionDestinoId }?.let { rutaTienda(it, ubicaciones) } ?: "Ubicación ${m.posicionDestinoId}"
            TarjetaPanel {
                Text(producto?.nombre ?: "Producto ${m.productoId}", fontWeight = FontWeight.Bold)
                Text("${m.tipo.replace('_', ' ')} · $origen → $destino · ${m.cantidad} unidades")
                Text(formatoFecha(m.fecha), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
