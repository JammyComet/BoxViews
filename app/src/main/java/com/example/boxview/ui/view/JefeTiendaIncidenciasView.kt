package com.example.boxview.ui.view

// Pantallas: Incidencias de tienda.
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import com.example.boxview.ui.components.*
import com.example.boxview.viewmodel.JefeTiendaViewModel
import com.example.boxview.viewmodel.OperadorViewModel
import com.example.boxview.viewmodel.TrabajadorTiendaViewModel

// Muestra la pantalla de incidencias jefe tienda.
@Composable
fun PantallaIncidenciasJefeTienda(base: OperadorViewModel, tienda: TrabajadorTiendaViewModel,
                                 jefe: JefeTiendaViewModel) {
    val sid = tienda.tiendaActiva?.id ?: return
    val usuario = base.sesion ?: return
    // Datos observados por esta pantalla.
    val productos by base.productos.collectAsState(initial = emptyList())
    val incidencias by remember(sid) { tienda.incidencias() }.collectAsState(initial = emptyList())
    val cajas by remember(sid) { tienda.recibidas() }.collectAsState(initial = emptyList())
    var mensaje by remember(sid) { mutableStateOf("") }
    var procesando by remember(sid) { mutableStateOf(false) }
    ColumnaOperador {
        TituloSeccion("JEFATURA", "Incidencias de recepción")
        if (mensaje.isNotBlank()) TarjetaPanel { Text(mensaje) }
        if (incidencias.isEmpty()) Text("No se registran diferencias de recepción")
        incidencias.forEach { incidencia ->
            TarjetaPanel {
                Text(productos.firstOrNull { it.id == incidencia.productoId }?.nombre ?: "Producto ${incidencia.productoId}",
                    fontWeight = FontWeight.Bold)
                Text("Caja ${cajas.firstOrNull { it.id == incidencia.cajaId }?.codigo ?: "#${incidencia.cajaId}"}")
                Text("Esperadas: ${incidencia.esperado} · Contadas: ${incidencia.contado}")
                Text("${formatoFecha(incidencia.fecha)} · ${incidencia.estado}",
                    style = MaterialTheme.typography.bodySmall)
                if (incidencia.estado == "PENDIENTE") BotonAccion("Marcar como revisada",
                    enabled = !procesando, onClick = {
                        procesando = true
                        jefe.revisarIncidencia(usuario, sid, incidencia) { _, msg ->
                            procesando = false; mensaje = msg
                        }
                    })
            }
        }
    }
}
