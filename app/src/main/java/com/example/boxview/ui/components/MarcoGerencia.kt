package com.example.boxview.ui.components

// Componente: Marco y navegación del perfil gerencia.
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.boxview.modelo.Destinos
import com.example.boxview.ui.view.AccesoNoDisponible
import com.example.boxview.viewmodel.AbastecimientoExternoViewModel
import com.example.boxview.viewmodel.OperadorViewModel

// navegacion de gerencia
// Construye el menú y marco del perfil marco gerencia.
@Composable
fun MarcoGerencia(base: OperadorViewModel, vm: AbastecimientoExternoViewModel, ruta: String,
                  ir: (String) -> Unit, cerrar: () -> Unit, contenido: @Composable () -> Unit) {
    val usuario = base.sesion
    if (usuario?.rol != "GERENTE") { AccesoNoDisponible(cerrar); return }
    val permiso by remember(usuario.id) { vm.permisoGerencia(usuario.id) }.collectAsState(initial = -1)
    if (!PermisoRol(permiso, cerrar)) return
    val opciones = remember {
        listOf(
            EntradaMenu("Inicio", Destinos.GerenciaInicio.ruta, Icons.Default.Home),
            EntradaMenu("Solicitudes", Destinos.GerenciaSolicitudes.ruta, Icons.Default.AssignmentTurnedIn),
            EntradaMenu("Informes", Destinos.GerenciaReportes.ruta, Icons.Default.Assessment)
        )
    }
    MenuRol("Gerencia · BoxView", ruta, opciones, ir, cerrar, contenido)
}
