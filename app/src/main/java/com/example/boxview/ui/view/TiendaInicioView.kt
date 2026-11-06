package com.example.boxview.ui.view

// Pantallas: Panel principal del trabajador de tienda.
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.boxview.modelo.*
import com.example.boxview.ui.components.*
import com.example.boxview.viewmodel.OperadorViewModel
import com.example.boxview.viewmodel.TrabajadorTiendaViewModel

// Muestra la pantalla de inicio tienda.
@Composable
fun PantallaInicioTienda(vm: OperadorViewModel, tiendaVm: TrabajadorTiendaViewModel, ir: (String) -> Unit) {
    val sid = tiendaVm.tiendaActiva?.id
    // Datos observados por esta pantalla.
    val pendientes by remember(sid) { tiendaVm.pendientes() }.collectAsState(initial = emptyList())
    val stock by remember(sid) { tiendaVm.existencias() }.collectAsState(initial = emptyList())
    val recibidas by remember(sid) { tiendaVm.recibidas() }.collectAsState(initial = emptyList())
    ColumnaOperador {
        TituloSeccion("TIENDA", "Bienvenido, ${vm.sesion?.nombre?.substringBefore(' ').orEmpty()}",
            tiendaVm.tiendaActiva?.nombre ?: "")
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TarjetaResumen("Cajas por recibir", pendientes.size.toString(), Modifier.weight(1f), Icons.Default.Inventory2)
            TarjetaResumen("Unidades en tienda", stock.sumOf { it.cantidad }.toString(), Modifier.weight(1f), Icons.Default.Store)
        }
        TarjetaPanel {
            Text("Recepción de mercadería", style = MaterialTheme.typography.titleMedium)
            BotonAccion("Recibir cajas", onClick = { ir(Destinos.TiendaRecepciones.ruta) }, icon = Icons.Default.QrCodeScanner)
            BotonAccion("Consultar stock", onClick = { ir(Destinos.TiendaStock.ruta) }, outlined = true)
            BotonAccion("Registrar venta, merma o retiro", onClick = { ir(Destinos.TiendaSalidas.ruta) }, outlined = true)
            BotonAccion("Conteo de inventario", onClick = { ir(Destinos.TiendaInventarios.ruta) }, outlined = true)
            BotonAccion("Ubicar productos en estanterías", onClick = { ir(Destinos.TiendaTraslados.ruta) }, outlined = true)
        }
        TituloSeccion("MOVIMIENTOS", "Últimas recepciones")
        if (recibidas.isEmpty()) Text("Aún no se han recibido cajas en esta tienda.")
        recibidas.take(4).forEach { c ->
            FilaOpcion(c.codigo, "${formatoFecha(c.fecha)} · ${c.estado.replace('_', ' ')}",
                icon = Icons.Default.CheckCircle)
        }
    }
}
