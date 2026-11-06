package com.example.boxview.ui.view

// Pantallas: Panel, stock y perfil del operador.
import com.example.boxview.modelo.*

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.example.boxview.R
import com.example.boxview.modelo.Destinos
import com.example.boxview.ui.components.*
import com.example.boxview.viewmodel.OperadorViewModel

// Muestra la pantalla de inicio operador.
@Composable
fun PantallaInicioOperador(vm: OperadorViewModel, ir: (String) -> Unit) {
    // Datos observados por esta pantalla.
    val existencias by remember(vm.bodegaActiva?.id) { vm.existencias() }.collectAsState(initial = emptyList())
    val periodo by remember(vm.bodegaActiva?.id, vm.sesion?.id) { vm.periodoInventario() }.collectAsState(initial = null)
    val envios by remember(vm.bodegaActiva?.id, vm.sesion?.id) { vm.ordenesSalida() }.collectAsState(initial = emptyList())
    val cajasIngreso by remember(vm.bodegaActiva?.id) { vm.cajasIngreso() }.collectAsState(initial = emptyList())
    ColumnaOperador {
        TituloSeccion("OPERADOR DE BODEGA", "¡Hola, ${vm.sesion?.nombre?.substringBefore(' ') ?: ""}!",
            "${vm.bodegaActiva?.nombre ?: ""} · Operaciones de bodega")
        TarjetaPanel {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Panel de operaciones", style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold)
                }
                Image(painterResource(R.drawable.mascota_saludando), null, Modifier.size(83.dp))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            TarjetaResumen("Unidades", existencias.sumOf { it.cantidad }.toString(), Modifier.weight(1f), Icons.Default.Inventory2)
            TarjetaResumen("Inventario", if (periodo?.estado == "ABIERTO") "Activo" else "—", Modifier.weight(1f), Icons.Default.FactCheck)
        }
        TituloSeccion("ACCESOS RÁPIDOS", "¿Qué necesitas hacer?")
        FilaOpcion("Ver existencias", "Buscar ropa y localizar pasillos y estantes", Icons.Default.Inventory2,
            onClick = { ir(Destinos.Stock.ruta) })
        FilaOpcion("Mover entre estantes", "Traslado interno dentro de la bodega", Icons.Default.SwapHoriz,
            onClick = { ir(Destinos.Movimientos.ruta) })
        FilaOpcion("Ingresos pendientes", "Escanear caja y verificar su contenido", Icons.Default.MoveToInbox,
            onClick = { ir(Destinos.Ingresos.ruta) })
        FilaOpcion("Salidas pendientes", "Preparar órdenes asignadas por el jefe de bodega", Icons.Default.LocalShipping,
            onClick = { ir(Destinos.Preparacion.ruta) })
        FilaOpcion("Inventario mensual", "Escanear y registrar productos del período abierto", Icons.Default.FactCheck,
            onClick = { ir(Destinos.Inventario.ruta) })
        FilaOpcion("Escanear producto", "Cámara o código manual", Icons.Default.QrCodeScanner,
            onClick = { ir(Destinos.Escaner.ruta) })
        if (envios.isNotEmpty())
            EtiquetaEstado("Órdenes por preparar: ${envios.size}", warning = true)
        if (cajasIngreso.isNotEmpty())
            EtiquetaEstado("Cajas esperando recepción: ${cajasIngreso.size}", warning = true)
    }
}

// Muestra la pantalla de stock operador.
@Composable
fun PantallaStockOperador(vm: OperadorViewModel, ir: (String) -> Unit) {
    // Datos observados por esta pantalla.
    val productos by vm.productos.collectAsState(initial = emptyList())
    val posiciones by remember(vm.bodegaActiva?.id) { vm.posiciones() }.collectAsState(initial = emptyList())
    val existencias by remember(vm.bodegaActiva?.id) { vm.existencias() }.collectAsState(initial = emptyList())
    var filtro by remember { mutableStateOf("") }
    LaunchedEffect(vm.codigoEscaneado) {
        if (vm.codigoEscaneado.isNotBlank()) filtro = vm.codigoEscaneado
    }
    ColumnaOperador {
        TituloSeccion("INVENTARIO", "Existencias", "Stock de ${vm.bodegaActiva?.nombre}")
        CampoFormulario("Buscar nombre o código", filtro, { filtro = it })
        BotonAccion("Escanear código", outlined = true, icon = Icons.Default.QrCodeScanner,
            onClick = { ir(Destinos.Escaner.ruta) })
        val visibles = productos.filter { p ->
            p.nombre.contains(filtro, ignoreCase = true) || p.codigo.contains(filtro, ignoreCase = true)
        }
        val sid = vm.bodegaActiva?.sucursalId ?: -1L
        val minimos by remember(sid) { vm.minimosSucursal(sid) }.collectAsState(initial = emptyList())
        val mapaMinimos = minimos.associate { it.productoId to it.minimo }
        visibles.forEach { p ->
            // Stock por posicion para el producto mostrado.
            val filas = existencias.filter { it.productoId == p.id && it.cantidad > 0 }
            val total = filas.sumOf { it.cantidad }
            TarjetaPanel {
                Text(p.nombre, fontWeight = FontWeight.Bold)
                Text("${p.codigo} · ${p.categoria}", style = MaterialTheme.typography.bodySmall)
                Text("Total: $total unidades", color = MaterialTheme.colorScheme.primary)
                if (filas.isEmpty()) Text("Sin existencias en esta bodega", style = MaterialTheme.typography.bodySmall)
                filas.forEach { stock ->
                    Text("${posiciones.find { it.id == stock.posicionId }?.let { rutaBodega(it, posiciones) } ?: "Posición"}: ${stock.cantidad}",
                        style = MaterialTheme.typography.bodySmall)
                }
                if (total <= (mapaMinimos[p.id] ?: p.minimo)) EtiquetaEstado(if (total == 0) "Agotado" else "Stock bajo", warning = true)
            }
        }
        if (visibles.isEmpty()) Text("No hay productos que coincidan con la búsqueda.")
    }
}

// Muestra la pantalla de historial operador.
@Composable
fun PantallaHistorialOperador(vm: OperadorViewModel) {
    // Datos observados por esta pantalla.
    val movimientos by remember(vm.bodegaActiva?.id) { vm.movimientos() }.collectAsState(initial = emptyList())
    val productos by vm.productos.collectAsState(initial = emptyList())
    val posiciones by remember(vm.bodegaActiva?.id) { vm.posiciones() }.collectAsState(initial = emptyList())
    ColumnaOperador {
        TituloSeccion("MIS ACTIVIDADES", "Historial", "Movimientos que registraste en esta bodega")
        if (movimientos.isEmpty()) Text("Aún no has registrado movimientos.")
        movimientos.forEach { m ->
            val nombreProducto = productos.find { it.id == m.productoId }?.nombre ?: "Producto"
            val ubicacionOrigen = posiciones.find { it.id == m.posicionOrigen }?.let { rutaBodega(it, posiciones) }
            val ubicacionDestino = posiciones.find { it.id == m.posicionDestino }?.let { rutaBodega(it, posiciones) }
            val titulo = when (m.tipo) {
                "RECEPCION_CAJA" -> "Recepción de caja"
                "SALIDA_CAJA" -> "Despacho de caja"
                "INGRESO_AUTORIZADO" -> "Ingreso autorizado"
                else -> "Traslado interno"
            }
            val ubicacion = when (m.tipo) {
                "RECEPCION_CAJA", "INGRESO_AUTORIZADO" -> "Ingreso en ${ubicacionDestino ?: "posición registrada"}"
                "SALIDA_CAJA" -> "Salida desde ${ubicacionOrigen ?: "posición registrada"}"
                else -> "${ubicacionOrigen ?: "Origen"} → ${ubicacionDestino ?: "Destino"}"
            }
            TarjetaPanel {
                Text("$titulo · $nombreProducto", fontWeight = FontWeight.Bold)
                Text("${m.cantidad} unidad(es)")
                Text(ubicacion, style = MaterialTheme.typography.bodySmall)
                Text(formatoFecha(m.fecha), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

// Muestra la pantalla de perfil operador.
@Composable
fun PantallaPerfilOperador(vm: OperadorViewModel, ir: (String) -> Unit, cerrar: () -> Unit) {
    ColumnaOperador {
        TituloSeccion("CUENTA", "Perfil", "Información de la sesión")
        TarjetaPanel {
            Text(vm.sesion?.nombre.orEmpty(), style = MaterialTheme.typography.titleLarge)
            Text(vm.sesion?.correo.orEmpty())
            EtiquetaEstado("Operador de bodega", success = true)
            Text("Bodega activa: ${vm.bodegaActiva?.nombre.orEmpty()}")
        }
        BotonAccion("Cambiar bodega", outlined = true, icon = Icons.Default.Warehouse,
            onClick = { ir(Destinos.Seleccion.ruta) })
        BotonAccion("Cerrar sesión", onClick = cerrar)
    }
}
