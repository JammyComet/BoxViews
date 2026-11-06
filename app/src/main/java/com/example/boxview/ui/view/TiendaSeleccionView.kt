package com.example.boxview.ui.view

// Pantallas: Selección de tienda del trabajador.
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.boxview.R
import com.example.boxview.modelo.*
import com.example.boxview.ui.components.*
import com.example.boxview.viewmodel.OperadorViewModel
import com.example.boxview.viewmodel.TrabajadorTiendaViewModel

// Muestra la pantalla de elegir tienda.
@Composable
fun PantallaElegirTienda(vm: OperadorViewModel, tiendaVm: TrabajadorTiendaViewModel, continuar: () -> Unit) {
    val uid: Long = vm.sesion?.id ?: -1L
    // Datos observados por esta pantalla.
    val tiendas by remember(uid) { tiendaVm.tiendasAsignadas(uid) }.collectAsState(initial = emptyList())
    var seleccion by remember(uid) { mutableStateOf<Long?>(null) }
    var error by remember { mutableStateOf("") }
    ColumnaOperador {
        TituloSeccion("SUCURSALES", if (vm.sesion?.rol == "JEFE_TIENDA") "Sucursal asignada" else "Seleccionar sucursal",
            "Sucursales habilitadas para esta cuenta")
        Image(painter = painterResource(R.drawable.mascota_jefe), contentDescription = "Mascota BoxView",
            modifier = Modifier.size(80.dp).align(Alignment.CenterHorizontally))
        tiendas.forEach { tienda ->
            FilaOpcion(tienda.nombre, "Sala de ventas · Inventario", icon = Icons.Default.Store,
                badge = if (seleccion == tienda.id) "Seleccionada" else null,
                onClick = { seleccion = tienda.id; error = "" })
        }
        if (tiendas.isEmpty()) Text("No hay sucursales asignadas", color = MaterialTheme.colorScheme.error)
        if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
        BotonAccion("Continuar", enabled = seleccion != null, onClick = {
            val elegida = tiendas.firstOrNull { it.id == seleccion }
            if (elegida == null) error = "Selecciona una tienda"
            else tiendaVm.seleccionarTienda(vm.sesion, elegida) { ok, msg ->
                if (ok) continuar() else error = msg
            }
        })
    }
}
