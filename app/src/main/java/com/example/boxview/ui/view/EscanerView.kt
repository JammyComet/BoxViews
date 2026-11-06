package com.example.boxview.ui.view

// Pantallas: Pantalla de lectura de códigos QR.
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.boxview.ui.components.CamaraEscaner
import com.example.boxview.modelo.Destinos
import com.example.boxview.ui.components.*
import com.example.boxview.viewmodel.OperadorViewModel

// Conecta la cámara con las acciones del inventario.
@Composable
fun PantallaEscanerOperador(vm: OperadorViewModel, ir: (String) -> Unit) {
    var manual by remember { mutableStateOf("") }
    var capturado by remember { mutableStateOf(false) }
    var abrirEscaner by remember { mutableStateOf(false) }
    // Datos observados por esta pantalla.
    val productos by vm.productos.collectAsState(initial = emptyList())
    fun buscar(codigo: String) {
        val producto = productos.firstOrNull { it.codigo.equals(codigo.trim(), ignoreCase = true) }
        if (producto != null) {
            vm.codigoEscaneado = producto.codigo
            ir(Destinos.Stock.ruta)
        } else {
            manual = codigo
            capturado = true
        }
    }
    if (abrirEscaner) ModalEscaner("Escanear producto en bodega", cerrar = { abrirEscaner = false }) { leido ->
        abrirEscaner = false
        buscar(leido)
    }
    ColumnaOperador {
        TituloSeccion("LECTURA", "Escanear producto")
        BotonAccion("Abrir cámara para escanear", outlined = true, onClick = { abrirEscaner = true })
        CampoFormulario("Código manual", manual, { manual = it; capturado = false })
        BotonAccion("Buscar producto", enabled = manual.isNotBlank(), onClick = { buscar(manual) })
        if (capturado) {
            Text("No encontramos ese código en el catálogo. No se modificó el stock.",
                color = MaterialTheme.colorScheme.error)
            BotonAccion("Volver a escanear", outlined = true,
                onClick = { capturado = false; manual = ""; abrirEscaner = true })
        }
    }
}
