package com.example.boxview.ui.components

// Componente: Ventana para leer un QR sin salir de la pantalla.
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/** Lector separado del formulario para pantallas compactas o emuladores. */
// Abre el lector QR en un diálogo.
@Composable
fun ModalEscaner(titulo: String, cerrar: () -> Unit, alLeer: (String) -> Unit) {
    Dialog(onDismissRequest = cerrar, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(modifier = Modifier.fillMaxWidth(0.94f), shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(titulo, style = MaterialTheme.typography.titleMedium)
                Text("Enfoca el código dentro del recuadro. Si tu cámara no funciona, cierra y escribe el código manualmente.",
                    style = MaterialTheme.typography.bodySmall)
                CamaraEscaner(alLeer, modifier = Modifier.fillMaxWidth().height(260.dp))
                OutlinedButton(onClick = cerrar, modifier = Modifier.fillMaxWidth()) { Text("Cerrar cámara") }
            }
        }
    }
}
