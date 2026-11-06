package com.example.boxview.ui.components

// Componente: Marco y navegación del jefe de bodega.
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.boxview.modelo.Destinos
import com.example.boxview.ui.view.AccesoNoDisponible
import com.example.boxview.viewmodel.OperadorViewModel

// Construye el menú y marco del perfil marco jefe.
@Composable
fun MarcoJefe(vm: OperadorViewModel, ruta: String, ir: (String) -> Unit,
              cerrar: () -> Unit, contenido: @Composable () -> Unit) {
    if (vm.sesion?.rol != "JEFE_BODEGA" || vm.bodegaActiva == null) {
        AccesoNoDisponible(cerrar)
        return
    }
    // comprobar permisos mientras la sesion esta abierta
    val uid = vm.sesion!!.id
    val sedeId = vm.bodegaActiva!!.id
    val acceso by remember(uid, sedeId) { vm.permisoBodegaActivo(uid, sedeId, "JEFE_BODEGA") }
        .collectAsState(initial = -1)
    if (!PermisoRol(acceso, cerrar)) return
    val opciones = remember {
        listOf(
            EntradaMenu("Inicio", Destinos.JefeInicio.ruta, Icons.Default.Home),
            EntradaMenu("Órdenes", Destinos.JefeOrdenes.ruta, Icons.Default.LocalShipping),
            EntradaMenu("Inventarios", Destinos.JefeInventarios.ruta, Icons.Default.FactCheck),
            EntradaMenu("Incidencias", Destinos.JefeIncidencias.ruta, Icons.Default.ReportProblem),
            EntradaMenu("Stock", Destinos.JefeStock.ruta, Icons.Default.Inventory2)
        )
    }
    val gestion = remember {
        listOf(
            EntradaMenu("Ubicaciones", Destinos.JefeUbicaciones.ruta, Icons.Default.Warehouse),
            EntradaMenu("Abastecimiento externo", Destinos.JefeAbastecimiento.ruta, Icons.Default.ShoppingCart),
            EntradaMenu("Equipo y permisos", Destinos.JefeEquipo.ruta, Icons.Default.Group)
        )
    }
    MenuRol("BoxView · Jefe de bodega", ruta, opciones, ir, cerrar, contenido,
        adicionales = gestion, tituloAdicional = "Gestión")
}
