package com.example.boxview.ui.view

// Pantallas: Ingresos autorizados y lectura de cajas.
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.boxview.modelo.Destinos
import com.example.boxview.ui.components.*
import com.example.boxview.viewmodel.OperadorViewModel

// recepcion de cajas
// Muestra la pantalla de ingresos autorizados.
@Composable
fun PantallaIngresosAutorizados(vm: OperadorViewModel, ir: (String) -> Unit) {
    // Datos observados por esta pantalla.
    val cajas by remember(vm.bodegaActiva?.id) { vm.cajasIngreso() }.collectAsState(initial = emptyList())
    var codigo by remember(vm.bodegaActiva?.id) { mutableStateOf("") }
    var camara by remember { mutableStateOf(false) }
    var ocupado by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    fun abrir(valor: String) {
        if (ocupado) return
        ocupado = true
        error = ""
        // Abre el flujo de recepcion de la caja.
        vm.abrirCaja(valor, "INGRESO") { ok, msg ->
            ocupado = false
            if (ok) { camara = false; ir(Destinos.DetalleCaja.ruta) } else error = msg
        }
    }
    if (camara) ModalEscaner("Escanear QR de caja de bodega", cerrar = { camara = false }) { leido ->
        camara = false
        codigo = leido
        abrir(leido)
    }
    ColumnaOperador {
        TituloSeccion("ZONA DE RECEPCIÓN", "Ingresos pendientes", "Cajas esperadas en ${vm.bodegaActiva?.nombre.orEmpty()}")
        TarjetaPanel {
            CampoFormulario("Código QR de la caja", codigo, { codigo = it; error = "" })
            Text("Al registrar la llegada (QR o código manual) la caja queda pendiente de comprobación física. El stock no aumenta hasta confirmar.",
                style = MaterialTheme.typography.bodySmall)
            BotonAccion("Registrar llegada y comprobar", enabled = codigo.isNotBlank() && !ocupado,
                onClick = { abrir(codigo) })
            BotonAccion("Escanear QR de caja", outlined = true, enabled = !ocupado,
                icon = Icons.Default.QrCodeScanner, onClick = { camara = true })
            // La cámara se abre como modal, sin ocupar el formulario.
            if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
        }
        TituloSeccion("ENTREGAS", "Cajas pendientes (${cajas.size})")
        if (cajas.isEmpty()) Text("No hay cajas pendientes en esta bodega.")
        cajas.forEach { caja ->
            val registrada = caja.estado == "EN_REVISION"
            val estado = when (caja.estado) {
                "EN_REVISION" -> "Recibida en bodega · por comprobar"
                "EN_TRANSITO" -> "En tránsito · escanea al llegar"
                else -> "Esperada · escanea al llegar"
            }
            FilaOpcion(caja.codigo, "${caja.origen} · $estado",
                icon = Icons.Default.Inventory2,
                badge = if (registrada) "Reanudar" else "Pendiente",
                warning = !registrada,
                onClick = if (registrada) ({ abrir(caja.codigo) }) else null)
        }
    }
}
