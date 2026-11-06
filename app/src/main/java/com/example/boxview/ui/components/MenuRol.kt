package com.example.boxview.ui.components

// Componente: Menú de opciones según el rol y sus permisos.
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

// Menú y barra inferior comunes para todos los perfiles.
// Componente visual de menu rol.
@Composable
fun MenuRol(
    titulo: String,
    ruta: String,
    principales: List<EntradaMenu>,
    ir: (String) -> Unit,
    cerrar: () -> Unit,
    contenido: @Composable () -> Unit,
    adicionales: List<EntradaMenu> = emptyList(),
    barra: List<EntradaMenu> = principales,
    tituloAdicional: String? = null,
    cambiarBodega: Boolean = false,
    seleccionada: (String) -> Boolean = { it == ruta },
    nombreBarra: (EntradaMenu) -> String = { it.texto }
) {
    val drawer = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(drawerState = drawer, drawerContent = {
        ModalDrawerSheet(drawerContainerColor = MaterialTheme.colorScheme.background) {
            Text(titulo, Modifier.padding(20.dp), style = MaterialTheme.typography.titleMedium)
            HorizontalDivider()
            principales.forEach { opcion ->
                NavigationDrawerItem(
                    label = { Text(opcion.texto) }, selected = seleccionada(opcion.ruta),
                    icon = { Icon(opcion.icono, null) },
                    onClick = { scope.launch { drawer.close() }; ir(opcion.ruta) }
                )
            }
            if (adicionales.isNotEmpty()) {
                if (tituloAdicional != null) {
                    HorizontalDivider()
                    Text(tituloAdicional, Modifier.padding(start = 28.dp, top = 16.dp, bottom = 8.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                adicionales.forEach { opcion ->
                    NavigationDrawerItem(
                        label = { Text(opcion.texto) }, selected = seleccionada(opcion.ruta),
                        icon = { Icon(opcion.icono, null) },
                        onClick = { scope.launch { drawer.close() }; ir(opcion.ruta) }
                    )
                }
            }
            HorizontalDivider()
            if (cambiarBodega) {
                NavigationDrawerItem(label = { Text("Cambiar bodega") }, selected = false,
                    icon = { Icon(Icons.Default.Warehouse, null) },
                    onClick = { scope.launch { drawer.close() }; ir(com.example.boxview.modelo.Destinos.Seleccion.ruta) })
            }
            NavigationDrawerItem(label = { Text("Cerrar sesión") }, selected = false,
                icon = { Icon(Icons.Default.Logout, null) }, onClick = cerrar)
        }
    }) {
        Scaffold(
            topBar = { EncabezadoBoxView(compacto = true, onMenu = { scope.launch { drawer.open() } }) },
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    barra.forEach { opcion ->
                        NavigationBarItem(selected = seleccionada(opcion.ruta),
                            onClick = { ir(opcion.ruta) },
                            icon = { Icon(opcion.icono, opcion.texto) },
                            label = { Text(nombreBarra(opcion), maxLines = 1) })
                    }
                }
            }
        ) { padding -> Box(Modifier.fillMaxSize().padding(padding)) { contenido() } }
    }
}

// El permiso viene de Room: -1 cargando, 0 denegado y 1 autorizado.
// Componente visual de permiso rol.
@Composable
fun PermisoRol(acceso: Int, cerrar: () -> Unit): Boolean {
    LaunchedEffect(acceso) { if (acceso == 0) cerrar() }
    if (acceso == -1) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
    }
    return acceso == 1
}
