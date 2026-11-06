package com.example.boxview.ui.view

// Pantallas: Menú de operaciones del operador.
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MoveToInbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.boxview.modelo.Destinos
import com.example.boxview.ui.components.FilaOpcion
import com.example.boxview.ui.components.TituloSeccion
import com.example.boxview.viewmodel.OperadorViewModel

// Muestra la pantalla de operaciones operador.
@Composable
fun PantallaOperacionesOperador(vm: OperadorViewModel, ir: (String) -> Unit) {
    // Datos observados por esta pantalla.
    val ingresos by remember(vm.bodegaActiva?.id) { vm.cajasIngreso() }
        .collectAsState(initial = emptyList())
    val salidas by remember(vm.bodegaActiva?.id, vm.sesion?.id) { vm.ordenesSalida() }
        .collectAsState(initial = emptyList())

    ColumnaOperador {
        TituloSeccion("BODEGA", "Operaciones", "Selecciona qué necesitas realizar")
        FilaOpcion(
            title = "Ingresos pendientes",
            subtitle = "Escanear una caja esperada y verificar sus productos",
            icon = Icons.Default.MoveToInbox,
            badge = "${ingresos.size} caja(s)",
            warning = ingresos.isNotEmpty(),
            onClick = { ir(Destinos.Ingresos.ruta) }
        )
        FilaOpcion(
            title = "Salidas pendientes",
            subtitle = "Preparar las órdenes enviadas por el jefe de bodega",
            icon = Icons.Default.LocalShipping,
            badge = "${salidas.size} orden(es)",
            warning = salidas.isNotEmpty(),
            onClick = { ir(Destinos.Preparacion.ruta) }
        )
    }
}
