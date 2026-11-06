package com.example.boxview.ui.components

// Componente: Marco y navegación del perfil administrador.
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.boxview.modelo.Destinos
import com.example.boxview.ui.view.AccesoNoDisponible
import com.example.boxview.viewmodel.AdministradorViewModel
import com.example.boxview.viewmodel.OperadorViewModel

// navegacion de administracion
// Construye el menú y marco del perfil marco administrador.
@Composable
fun MarcoAdministrador(base: OperadorViewModel, admin: AdministradorViewModel, ruta: String,
                       ir: (String) -> Unit, cerrar: () -> Unit, contenido: @Composable () -> Unit) {
    val sesion = base.sesion
    val sede = admin.sucursalActiva
    if (sesion?.rol != "ADMIN_SUCURSAL" || sede == null) {
        AccesoNoDisponible(cerrar)
        return
    }
    val acceso by remember(sesion.id, sede.id) { admin.permiso(sesion.id, sede.id) }
        .collectAsState(initial = -1)
    if (!PermisoRol(acceso, cerrar)) return
    val principales = remember {
        listOf(
            EntradaMenu("Inicio", Destinos.AdminInicio.ruta, Icons.Default.Home),
            EntradaMenu("Bodegas", Destinos.AdminBodegas.ruta, Icons.Default.Warehouse),
            EntradaMenu("Personal", Destinos.AdminPersonal.ruta, Icons.Default.Group),
            EntradaMenu("Mínimos", Destinos.AdminMinimos.ruta, Icons.Default.Inventory2)
        )
    }
    val adicionales = remember {
        listOf(
            EntradaMenu("Abastecimiento externo", Destinos.AdminAbastecimiento.ruta, Icons.Default.ShoppingCart),
            EntradaMenu("Informes operativos", Destinos.AdminReportes.ruta, Icons.Default.Assessment),
            EntradaMenu("Precios de venta", Destinos.AdminPrecios.ruta, Icons.Default.AttachMoney),
            EntradaMenu("Historial administrativo", Destinos.AdminHistorial.ruta, Icons.Default.History)
        )
    }
    MenuRol("Administración · ${sede.nombre}", ruta, principales, ir, cerrar, contenido,
        adicionales = adicionales)
}
