package com.example.boxview.ui.view

// Pantallas: Perfil del trabajador de tienda.
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.boxview.modelo.*
import com.example.boxview.ui.components.*
import com.example.boxview.viewmodel.OperadorViewModel
import com.example.boxview.viewmodel.TrabajadorTiendaViewModel

// Muestra la pantalla de perfil tienda.
@Composable
fun PantallaPerfilTienda(vm: OperadorViewModel, tiendaVm: TrabajadorTiendaViewModel,
                         ir: (String) -> Unit, cerrar: () -> Unit) {
    ColumnaOperador {
        TituloSeccion("MI CUENTA", "Perfil de tienda", tiendaVm.tiendaActiva?.nombre.orEmpty())
        TarjetaPanel {
            Text(vm.sesion?.nombre.orEmpty(), style = MaterialTheme.typography.titleMedium)
            Text(vm.sesion?.correo.orEmpty())
            Text("Trabajador de tienda", style = MaterialTheme.typography.bodySmall)
            BotonAccion("Cambiar tienda", outlined = true, onClick = { ir(Destinos.Seleccion.ruta) })
            BotonAccion("Cerrar sesión", onClick = cerrar)
        }
    }
}
