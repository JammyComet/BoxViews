package com.example.boxview.ui.components

// Componente: Marco y navegación del operador.
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.boxview.modelo.Destinos
import com.example.boxview.ui.view.AccesoNoDisponible
import com.example.boxview.viewmodel.OperadorViewModel

data class EntradaMenu(val texto: String, val ruta: String, val icono: ImageVector)

// Construye el menú y marco del perfil marco operador.
@Composable
fun MarcoOperador(
    vm: OperadorViewModel, ruta: String,
    ir: (String) -> Unit, cerrar: () -> Unit,
    contenido: @Composable () -> Unit
) {
    if (vm.sesion?.rol != "OPERADOR_BODEGA" || vm.bodegaActiva == null) {
        AccesoNoDisponible(cerrar)
        return
    }
    // comprobar permisos mientras la sesion esta abierta
    val uid = vm.sesion!!.id
    val sedeId = vm.bodegaActiva!!.id
    val acceso by remember(uid, sedeId) { vm.permisoBodegaActivo(uid, sedeId, "OPERADOR_BODEGA") }
        .collectAsState(initial = -1)
    if (!PermisoRol(acceso, cerrar)) return
    val menu = remember {
        listOf(
            EntradaMenu("Inicio", Destinos.Inicio.ruta, Icons.Default.Home),
            EntradaMenu("Existencias", Destinos.Stock.ruta, Icons.Default.Inventory2),
            EntradaMenu("Traslado interno", Destinos.Movimientos.ruta, Icons.Default.SwapHoriz),
            EntradaMenu("Operaciones", Destinos.Operaciones.ruta, Icons.Default.LocalShipping),
            EntradaMenu("Inventario mensual", Destinos.Inventario.ruta, Icons.Default.FactCheck),
            EntradaMenu("Mi historial", Destinos.Historial.ruta, Icons.Default.History),
            EntradaMenu("Escanear código", Destinos.Escaner.ruta, Icons.Default.QrCodeScanner),
            EntradaMenu("Perfil", Destinos.Perfil.ruta, Icons.Default.Person)
        )
    }
    MenuRol("BoxView · Operador de bodega", ruta, menu, ir, cerrar, contenido,
        barra = listOf(menu[0], menu[1], menu[3], menu[4], menu[7]), cambiarBodega = true,
        seleccionada = { destino ->
            ruta == destino || (destino == Destinos.Operaciones.ruta && ruta in listOf(
                Destinos.Ingresos.ruta, Destinos.Preparacion.ruta, Destinos.DetalleCaja.ruta))
        },
        nombreBarra = { when (it.texto) {
            "Existencias" -> "Stock"
            "Inventario mensual" -> "Inventario"
            else -> it.texto
        } })
}
