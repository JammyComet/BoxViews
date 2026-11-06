package com.example.boxview.ui.view

// Pantallas: Panel principal del jefe de bodega.
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
import com.example.boxview.viewmodel.JefeBodegaViewModel
import com.example.boxview.viewmodel.OperadorViewModel

// Muestra la pantalla de inicio jefe.
@Composable
fun PantallaInicioJefe(base: OperadorViewModel, jefe: JefeBodegaViewModel, ir: (String) -> Unit) {
    val bodega = base.bodegaActiva?.id ?: return
    // Datos observados por esta pantalla.
    val ordenes by remember(bodega) { jefe.ordenes(bodega) }.collectAsState(initial = emptyList())
    val periodos by remember(bodega) { jefe.inventariosGenerales(bodega) }.collectAsState(initial = emptyList())
    val incidencias by remember(bodega) { jefe.incidencias(bodega) }.collectAsState(initial = emptyList())
    val existencias by remember(bodega) { base.existencias() }.collectAsState(initial = emptyList())
    val pendientes by remember(bodega) { base.compromisosSalida() }.collectAsState(initial = emptyList())
    val unidadesPendientes = pendientes.sumOf { it.cantidadSolicitada }
    val productosPendientes = pendientes.count { it.cantidadSolicitada > 0 }
    ColumnaOperador {
        TituloSeccion("JEFATURA", "Panel de bodega", "${base.sesion?.nombre} · ${base.bodegaActiva?.nombre}")
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TarjetaResumen("Órdenes pendientes", ordenes.count { it.estado in listOf("ORDEN_ASIGNADA", "PENDIENTE_REVISION") }.toString(), Modifier.weight(1f))
            TarjetaResumen("Incidencias", incidencias.count { it.estado == "PENDIENTE" }.toString(), Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TarjetaResumen("Inventarios activos", periodos.count { it.estado in listOf("ABIERTO", "PENDIENTE_REVISION") }.toString(), Modifier.weight(1f))
            TarjetaResumen("Unidades en bodega", existencias.sumOf { it.cantidad }.toString(), Modifier.weight(1f))
        }
        if (unidadesPendientes > 0) TarjetaPanel {
            Text("Despachos pendientes", fontWeight = FontWeight.Bold)
            Text("$unidadesPendientes unidades de $productosPendientes productos están incluidas en órdenes pendientes de despacho.")
            BotonAccion("Revisar órdenes", outlined = true, onClick = { ir(Destinos.JefeOrdenes.ruta) })
        }
        FilaOpcion("Solicitar abastecimiento externo", "Preparar solicitud de compra para gerencia",
            Icons.Default.ShoppingCart, onClick = { ir(Destinos.JefeAbastecimiento.ruta) })
        FilaOpcion("Crear y asignar una salida", "Enviar desde bodega hacia una tienda",
            Icons.Default.LocalShipping, onClick = { ir(Destinos.JefeOrdenes.ruta) })
        FilaOpcion("Inventario mensual", "Abrir períodos y revisar los entregados",
            Icons.Default.FactCheck, onClick = { ir(Destinos.JefeInventarios.ruta) })
        FilaOpcion("Revisar incidencias", "Faltantes o sobrantes informados en recepción",
            Icons.Default.ReportProblem, onClick = { ir(Destinos.JefeIncidencias.ruta) })
        FilaOpcion("Consultar existencias", "Unidades por producto y estante",
            Icons.Default.Inventory2, onClick = { ir(Destinos.JefeStock.ruta) })
        FilaOpcion("Gestionar ubicaciones", "Crear secciones, estanterías y subestantes",
            Icons.Default.Warehouse, onClick = { ir(Destinos.JefeUbicaciones.ruta) })
        FilaOpcion("Equipo y permisos", "Gestionar operadores de esta bodega",
            Icons.Default.Group, onClick = { ir(Destinos.JefeEquipo.ruta) })
        TarjetaPanel {
            Text("Órdenes recientes", fontWeight = FontWeight.Bold)
            if (ordenes.isEmpty()) Text("Todavía no hay órdenes de salida")
            ordenes.take(4).forEach { o ->
                Text("${o.codigo} · ${o.estado}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
