package com.example.boxview.ui.components

// Componente: Marco y navegación del trabajador de tienda.
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.boxview.modelo.Destinos
import com.example.boxview.ui.view.AccesoNoDisponible
import com.example.boxview.viewmodel.OperadorViewModel
import com.example.boxview.viewmodel.TrabajadorTiendaViewModel

// menu de navegacion de tienda
// Construye el menú y marco del perfil marco tienda.
@Composable
fun MarcoTienda(vm: OperadorViewModel, tiendaVm: TrabajadorTiendaViewModel, ruta: String,
                ir: (String) -> Unit, cerrar: () -> Unit, contenido: @Composable () -> Unit) {
    if (vm.sesion?.rol != "TRABAJADOR_TIENDA" || tiendaVm.tiendaActiva == null) {
        AccesoNoDisponible(cerrar)
        return
    }
    // comprobar permisos mientras la sesion esta abierta
    val uid = vm.sesion!!.id
    val sedeId = tiendaVm.tiendaActiva!!.id
    val acceso by remember(uid, sedeId) { vm.permisoTiendaActivo(uid, sedeId, "TRABAJADOR_TIENDA") }
        .collectAsState(initial = -1)
    if (!PermisoRol(acceso, cerrar)) return
    val opciones = remember {
        listOf(
            EntradaMenu("Inicio", Destinos.TiendaInicio.ruta, Icons.Default.Home),
            EntradaMenu("Recepciones", Destinos.TiendaRecepciones.ruta, Icons.Default.QrCodeScanner),
            EntradaMenu("Stock", Destinos.TiendaStock.ruta, Icons.Default.Inventory2),
            EntradaMenu("Mover productos", Destinos.TiendaTraslados.ruta, Icons.Default.MoveUp),
            EntradaMenu("Salidas", Destinos.TiendaSalidas.ruta, Icons.Default.RemoveShoppingCart),
            EntradaMenu("Inventarios", Destinos.TiendaInventarios.ruta, Icons.Default.Assignment),
            EntradaMenu("Historial", Destinos.TiendaHistorial.ruta, Icons.Default.History),
            EntradaMenu("Perfil", Destinos.TiendaPerfil.ruta, Icons.Default.Person)
        )
    }
    val barra = remember { opciones.filter { it.ruta !in listOf(
        Destinos.TiendaTraslados.ruta, Destinos.TiendaSalidas.ruta, Destinos.TiendaInventarios.ruta) } }
    MenuRol("BoxView · Trabajador de tienda", ruta, opciones, ir, cerrar, contenido,
        barra = barra, seleccionada = { destino ->
            destino == ruta || (ruta == Destinos.TiendaDetalle.ruta && destino == Destinos.TiendaRecepciones.ruta) ||
                (ruta == Destinos.TiendaTraslados.ruta && destino == Destinos.TiendaStock.ruta)
        })
}
