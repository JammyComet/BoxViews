package com.example.boxview.ui.components

// Componente: Marco y navegación del jefe de tienda.
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

// navegacion de jefatura de tienda
// Construye el menú y marco del perfil marco jefe tienda.
@Composable
fun MarcoJefeTienda(base: OperadorViewModel, tienda: TrabajadorTiendaViewModel, ruta: String,
                    ir: (String) -> Unit, cerrar: () -> Unit, contenido: @Composable () -> Unit) {
    if (base.sesion?.rol != "JEFE_TIENDA" || tienda.tiendaActiva == null) {
        AccesoNoDisponible(cerrar)
        return
    }
    // comprobar permisos mientras la sesion esta abierta
    val uid = base.sesion!!.id
    val sedeId = tienda.tiendaActiva!!.id
    val acceso by remember(uid, sedeId) { base.permisoTiendaActivo(uid, sedeId, "JEFE_TIENDA") }
        .collectAsState(initial = -1)
    if (!PermisoRol(acceso, cerrar)) return
    val principales = remember {
        listOf(
            EntradaMenu("Inicio", Destinos.JefeTiendaInicio.ruta, Icons.Default.Home),
            EntradaMenu("Stock", Destinos.JefeTiendaStock.ruta, Icons.Default.Inventory2),
            EntradaMenu("Reposición", Destinos.JefeTiendaReposicion.ruta, Icons.Default.LocalShipping),
            EntradaMenu("Incidencias", Destinos.JefeTiendaIncidencias.ruta, Icons.Default.ReportProblem)
        )
    }
    val adicionales = remember {
        listOf(
            EntradaMenu("Equipo", Destinos.JefeTiendaEquipo.ruta, Icons.Default.Group),
            EntradaMenu("Ubicaciones", Destinos.JefeTiendaUbicaciones.ruta, Icons.Default.Inventory2),
            EntradaMenu("Inventario de tienda", Destinos.TiendaInventarios.ruta, Icons.Default.Assignment),
            EntradaMenu("Historial", Destinos.JefeTiendaHistorial.ruta, Icons.Default.History)
        )
    }
    MenuRol("BoxView · Jefe de tienda", ruta, principales, ir, cerrar, contenido,
        adicionales = adicionales)
}
