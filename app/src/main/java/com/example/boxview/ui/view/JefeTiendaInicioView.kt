package com.example.boxview.ui.view

// Pantallas: Panel principal del jefe de tienda.
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.boxview.modelo.Destinos
import com.example.boxview.ui.components.*
import com.example.boxview.viewmodel.JefeTiendaViewModel
import com.example.boxview.viewmodel.OperadorViewModel
import com.example.boxview.viewmodel.TrabajadorTiendaViewModel

// Muestra la pantalla de inicio jefe tienda.
@Composable
fun PantallaInicioJefeTienda(base: OperadorViewModel, tienda: TrabajadorTiendaViewModel,
                            jefe: JefeTiendaViewModel, ir: (String) -> Unit) {
    val sid = tienda.tiendaActiva?.id ?: return
    // Datos observados por esta pantalla.
    val stock by remember(sid) { tienda.existencias() }.collectAsState(initial = emptyList())
    val productos by base.productos.collectAsState(initial = emptyList())
    val minimos by remember(sid) { base.minimosSucursal(sid) }.collectAsState(initial = emptyList())
    val mapaMinimos = minimos.associate { it.productoId to it.minimo }
    val bajoMinimo = productos.count { p ->
        (stock.firstOrNull { it.productoId == p.id }?.cantidad ?: 0) <= (mapaMinimos[p.id] ?: p.minimo)
    }
    val cajas by remember(sid) { tienda.pendientes() }.collectAsState(initial = emptyList())
    val equipo by remember(sid) { jefe.equipo(sid) }.collectAsState(initial = emptyList())
    val solicitudes by remember(sid) { jefe.solicitudes(sid) }.collectAsState(initial = emptyList())
    val incidencias by remember(sid) { tienda.incidencias() }.collectAsState(initial = emptyList())
    ColumnaOperador {
        TituloSeccion("JEFATURA DE TIENDA", "Bienvenido, ${base.sesion?.nombre?.substringBefore(' ').orEmpty()}",
            tienda.tiendaActiva?.nombre)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TarjetaResumen("Unidades", stock.sumOf { it.cantidad }.toString(), Modifier.weight(1f), Icons.Default.Inventory2)
            TarjetaResumen("Cajas por recibir", cajas.size.toString(), Modifier.weight(1f), Icons.Default.LocalShipping)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TarjetaResumen("Trabajadores", equipo.size.toString(), Modifier.weight(1f), Icons.Default.Group)
            TarjetaResumen("Pedidos pendientes", solicitudes.filter { it.estado == "PENDIENTE" }.distinctBy { it.loteId ?: "solicitud-${it.id}" }.size.toString(),
                Modifier.weight(1f), Icons.Default.Assignment)
        }
        if (bajoMinimo > 0) TarjetaPanel {
            Text("Reposición pendiente de evaluación", style = MaterialTheme.typography.titleMedium)
            Text("$bajoMinimo productos en o bajo el mínimo de existencias")
            BotonAccion("Revisar reposición sugerida", onClick = { ir(Destinos.JefeTiendaReposicion.ruta) })
        }
        TarjetaPanel {
            Text("Supervisión de la sucursal", style = MaterialTheme.typography.titleMedium)
            BotonAccion("Solicitar reposición", onClick = { ir(Destinos.JefeTiendaReposicion.ruta) })
            BotonAccion("Ver stock y estanterías", outlined = true, onClick = { ir(Destinos.JefeTiendaStock.ruta) })
            BotonAccion("Inventarios de tienda", outlined = true, onClick = { ir(Destinos.TiendaInventarios.ruta) })
            BotonAccion("Administrar equipo", outlined = true, onClick = { ir(Destinos.JefeTiendaEquipo.ruta) })
            BotonAccion("Administrar ubicaciones", outlined = true, onClick = { ir(Destinos.JefeTiendaUbicaciones.ruta) })
        }
        TituloSeccion("CONTROL", "Diferencias de recepción")
        Text("${incidencias.count { it.estado == "PENDIENTE" }} incidencias pendientes de revisar")
        BotonAccion("Revisar incidencias", outlined = true, onClick = { ir(Destinos.JefeTiendaIncidencias.ruta) })
    }
}
