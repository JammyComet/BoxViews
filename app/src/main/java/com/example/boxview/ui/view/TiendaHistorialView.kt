package com.example.boxview.ui.view

// Pantallas: Historial de movimientos de tienda.
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.boxview.modelo.*
import com.example.boxview.ui.components.*
import com.example.boxview.viewmodel.OperadorViewModel
import com.example.boxview.viewmodel.TrabajadorTiendaViewModel
import java.text.NumberFormat
import java.util.Locale

/** Historial operativo de tienda con secciones compactas: evita titulares enormes y bloques vacíos. */
// Muestra la pantalla de historial tienda.
@Composable
fun PantallaHistorialTienda(vm: OperadorViewModel, tiendaVm: TrabajadorTiendaViewModel) {
    val sid = tiendaVm.tiendaActiva?.id
    // Datos observados por esta pantalla.
    val cajas by remember(sid) { tiendaVm.recibidas() }.collectAsState(initial = emptyList())
    val movimientos by remember(sid) { tiendaVm.movimientos() }.collectAsState(initial = emptyList())
    val incidencias by remember(sid) { tiendaVm.incidencias() }.collectAsState(initial = emptyList())
    val eventos by remember(sid) { tiendaVm.eventosRecepcion() }.collectAsState(initial = emptyList())
    val productos by vm.productos.collectAsState(initial = emptyList())
    val salidas by remember(sid) { tiendaVm.salidas() }.collectAsState(initial = emptyList())
    val movimientosUbicaciones by remember(sid) { tiendaVm.movimientosUbicaciones() }.collectAsState(initial = emptyList())
    val posiciones by remember(sid) { tiendaVm.todasLasPosiciones() }.collectAsState(initial = emptyList())
    val inventarios by remember(sid) { tiendaVm.inventariosTienda() }.collectAsState(initial = emptyList())
    var mostrarTodasSalidas by remember(sid) { mutableStateOf(false) }
    val cambios = movimientosUbicaciones.filter { it.tipo == "TRASLADO_TIENDA" }
    val moneda = remember { NumberFormat.getIntegerInstance(Locale("es", "CL")) }

    ColumnaOperador {
        TituloSeccion("ACTIVIDAD", "Historial de tienda", "Registro de ventas, recepciones y movimientos")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TarjetaResumen("Salidas", salidas.size.toString(), Modifier.weight(1f), Icons.Default.RemoveShoppingCart)
            TarjetaResumen("Cajas recibidas", cajas.size.toString(), Modifier.weight(1f), Icons.Default.Inventory2)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TarjetaResumen("Reubicaciones", cambios.size.toString(), Modifier.weight(1f), Icons.Default.SwapHoriz)
            TarjetaResumen("Incidencias", incidencias.size.toString(), Modifier.weight(1f), Icons.Default.ReportProblem)
        }
        if (salidas.isEmpty() && cajas.isEmpty() && movimientos.isEmpty() && cambios.isEmpty() && incidencias.isEmpty() && inventarios.isEmpty()) {
            TarjetaPanel { Text("Aún no hay movimientos registrados en esta tienda.") }
        }

        if (salidas.isNotEmpty()) {
            SeccionHistorialTienda("Ventas, mermas y retiros", salidas.size)
            salidas.take(if (mostrarTodasSalidas) salidas.size else 6).forEach { salida ->
                val producto = productos.firstOrNull { it.id == salida.productoId }
                TarjetaPanel {
                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(producto?.nombre ?: "Producto #${salida.productoId}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        EtiquetaEstado(salida.tipo.lowercase().replaceFirstChar { it.uppercase() },
                            warning = salida.tipo == "MERMA")
                    }
                    Text("${salida.cantidad} ${if (salida.cantidad == 1) "unidad" else "unidades"} · ${formatoFecha(salida.fecha)}",
                        style = MaterialTheme.typography.bodySmall)
                    if (salida.tipo == "VENTA" && salida.totalVentaClp != null) {
                        Text("Total venta: $" + moneda.format(salida.totalVentaClp) + " CLP",
                            style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    }
                    val razon = motivoVisibleHistorial(salida.motivo, salida.tipo)
                    if (razon.isNotBlank()) Text("Motivo: $razon", style = MaterialTheme.typography.bodySmall)
                }
            }
            if (salidas.size > 6) {
                BotonAccion(if (mostrarTodasSalidas) "Mostrar menos salidas" else "Ver las ${salidas.size} salidas",
                    outlined = true, onClick = { mostrarTodasSalidas = !mostrarTodasSalidas })
            }
        }

        if (cajas.isNotEmpty()) {
            SeccionHistorialTienda("Recepciones de cajas", cajas.size)
            cajas.forEach { caja ->
                TarjetaPanel {
                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(caja.codigo, style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        EtiquetaEstado(if (caja.estado == "CON_DIFERENCIA") "Con diferencia" else "Recibida",
                            warning = caja.estado == "CON_DIFERENCIA", success = caja.estado == "RECIBIDA")
                    }
                    Text("Orden creada: ${formatoFecha(caja.fecha)}", style = MaterialTheme.typography.bodySmall)
                    eventos.filter { it.cajaId == caja.id }.forEach { evento ->
                        Text("Confirmación: ${formatoFecha(evento.fecha)}", style = MaterialTheme.typography.bodySmall)
                        // Oculta observaciones vacías que solo repiten el nombre de la tienda.
                        if (evento.observacion.substringAfter('·', "").trim().isNotBlank())
                            Text("Observación: ${evento.observacion.substringAfter('·').trim()}",
                                style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        if (movimientos.isNotEmpty()) {
            SeccionHistorialTienda("Productos ingresados", movimientos.size)
            movimientos.forEach { mov ->
                val producto = productos.firstOrNull { it.id == mov.productoId }
                FilaOpcion(producto?.nombre ?: "Producto #${mov.productoId}",
                    "+${mov.cantidad} unidades · Caja #${mov.cajaId} · ${formatoFecha(mov.fecha)}",
                    icon = Icons.Default.CheckCircle)
            }
        }
        if (cambios.isNotEmpty()) {
            SeccionHistorialTienda("Movimientos entre estantes", cambios.size)
            cambios.forEach { mov ->
                val producto = productos.firstOrNull { it.id == mov.productoId }
                val origen = posiciones.firstOrNull { it.id == mov.posicionOrigenId }
                    ?.let { rutaTienda(it, posiciones) } ?: "Sin origen"
                val destino = posiciones.firstOrNull { it.id == mov.posicionDestinoId }
                    ?.let { rutaTienda(it, posiciones) } ?: "Sin destino"
                TarjetaPanel {
                    Text(producto?.nombre ?: "Producto #${mov.productoId}",
                        style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text("$origen → $destino", style = MaterialTheme.typography.bodySmall)
                    Text("${mov.cantidad} unidades · ${formatoFecha(mov.fecha)}",
                        style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        if (inventarios.isNotEmpty()) {
            SeccionHistorialTienda("Inventarios de tienda", inventarios.size)
            inventarios.forEach { inv ->
                TarjetaPanel {
                    Text(inv.nombre, style = MaterialTheme.typography.titleSmall)
                    Text("Inicio: ${formatoFecha(inv.fechaApertura)}", style = MaterialTheme.typography.bodySmall)
                    EtiquetaEstado(inv.estado.replace('_', ' '), success = inv.estado == "CERRADO")
                }
            }
        }
        if (incidencias.isNotEmpty()) {
            SeccionHistorialTienda("Diferencias de recepción", incidencias.size)
            incidencias.forEach { incidencia ->
                val producto = productos.firstOrNull { it.id == incidencia.productoId }
                TarjetaPanel {
                    Text(producto?.nombre ?: "Producto #${incidencia.productoId}",
                        style = MaterialTheme.typography.titleSmall)
                    Text("Esperadas: ${incidencia.esperado} · Contadas: ${incidencia.contado}",
                        style = MaterialTheme.typography.bodySmall)
                    EtiquetaEstado(incidencia.estado, warning = incidencia.estado == "PENDIENTE")
                }
            }
        }
    }
}

// Componente visual de seccion historial tienda.
@Composable
private fun SeccionHistorialTienda(titulo: String, cantidad: Int) {
    Spacer(Modifier.height(4.dp))
    HorizontalDivider()
    Text("$titulo ($cantidad)", style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold)
}
