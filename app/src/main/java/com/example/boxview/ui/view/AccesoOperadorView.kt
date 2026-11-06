package com.example.boxview.ui.view

// Pantallas: Inicio de sesión y selección de bodega.
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import com.example.boxview.R
import com.example.boxview.ui.components.*
import com.example.boxview.viewmodel.OperadorViewModel

// Formulario de acceso con estado y validaciones.
@Composable
fun PantallaLoginOperador(vm: OperadorViewModel, continuar: () -> Unit) {
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var enviando by remember { mutableStateOf(false) }
    var accesoCorrecto by remember { mutableStateOf(false) }
    val teclado = LocalSoftwareKeyboardController.current

    if (accesoCorrecto) {
        AnimacionLoginMascota(alTerminar = continuar)
        return
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Spacer(Modifier.height(18.dp))
        CajaLoginNormal(Modifier.size(115.dp))
        TituloSeccion("BOXVIEW", "Iniciar sesión", "Acceso a BoxView")
        TarjetaPanel {
            CampoFormulario("Correo", correo, { correo = it; error = "" }, keyboardType = KeyboardType.Email)
            CampoFormulario("Contraseña", clave, { clave = it; error = "" }, esContrasena = true)
            if (!vm.preparado) {
                CircularProgressIndicator()
                Text("Iniciando BoxView...")
            }
            if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
            if (vm.errorInicio.isNotBlank()) Text(vm.errorInicio, color = MaterialTheme.colorScheme.error)
            if (vm.avisoInicio.isNotBlank()) Text(vm.avisoInicio, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (correo.isNotBlank() && !correoBienFormado(correo))
                Text("Revisa el formato del correo para iniciar sesión", color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall)
            BotonAccion("Ingresar", enabled = vm.preparado && vm.errorInicio.isBlank() && !enviando && correoBienFormado(correo) && clave.isNotBlank(),
                onClick = {
                    enviando = true
                    teclado?.hide()
                    vm.ingresar(correo, clave) { exito, mensaje ->
                        if (exito) accesoCorrecto = true
                        else {
                            enviando = false
                            error = mensaje
                        }
                    }
                })
        }
    }
}

// Lista de bodegas autorizadas para ingresar.
@Composable
fun PantallaElegirBodega(vm: OperadorViewModel, continuar: () -> Unit) {
    // Datos observados por esta pantalla.
    val autorizadas by remember(vm.sesion?.id) { vm.bodegasAutorizadas() }.collectAsState(initial = emptyList())
    val sucursales by vm.sucursales.collectAsState(initial = emptyList())
    var seleccion by remember { mutableStateOf<Long?>(null) }
    var error by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)) {
        TituloSeccion("BODEGAS", if (vm.sesion?.rol == "JEFE_BODEGA") "Bodega asignada" else "Seleccionar bodega",
            if (vm.sesion?.rol == "JEFE_BODEGA") "Jefatura administra una única bodega" else "Bodegas habilitadas para esta cuenta")
        Image(painterResource(R.drawable.mascota_jefe), null, Modifier.size(80.dp).align(Alignment.CenterHorizontally))
        autorizadas.forEach { b ->
            val nombreSucursal = sucursales.find { it.id == b.sucursalId }?.nombre.orEmpty()
            FilaOpcion(b.nombre, nombreSucursal, icon = Icons.Default.Warehouse,
                badge = if (seleccion == b.id) "Seleccionada" else null,
                onClick = { seleccion = b.id; error = "" })
        }
        if (autorizadas.isEmpty()) Text("No hay bodegas asignadas", color = MaterialTheme.colorScheme.error)
        if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
        BotonAccion("Continuar", enabled = seleccion != null, onClick = {
            val bodega = autorizadas.find { it.id == seleccion }
            if (bodega == null) error = "Selecciona una bodega"
            else vm.elegirBodega(bodega) { ok, msg -> if (ok) continuar() else error = msg }
        })
    }
}

// Aviso cuando falta una sesión válida.
@Composable
fun AccesoNoDisponible(cerrar: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Debes iniciar sesión y elegir una bodega")
        Spacer(Modifier.height(16.dp))
        Button(onClick = cerrar) { Text("Volver al inicio de sesión") }
    }
}
