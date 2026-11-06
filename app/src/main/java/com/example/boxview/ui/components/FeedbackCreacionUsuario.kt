package com.example.boxview.ui.components

// Componente: Diálogo de progreso y resultado al crear usuarios.
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.boxview.R

// Muestra el progreso de creación de personal.
@Composable
fun DialogoEstadoCreacionUsuario(
    visible: Boolean,
    creando: Boolean,
    mensajeExito: String = "Usuario creado con éxito"
) {
    if (!visible) return

    Dialog(onDismissRequest = { }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x66000000)),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                tonalElevation = 6.dp,
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (creando) {
                        Box(
                            modifier = Modifier.size(110.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CajaLoginNormal(Modifier.size(92.dp))
                            CircularProgressIndicator(color = CAFE, modifier = Modifier.size(104.dp), strokeWidth = 4.dp)
                        }
                        Text("Creando usuario...", fontWeight = FontWeight.Bold, color = TINTA, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Estamos registrando la cuenta y preparando sus permisos.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.mascota_saludando),
                            contentDescription = "Mascota BoxView",
                            modifier = Modifier.size(100.dp)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(mensajeExito, fontWeight = FontWeight.Bold, color = TINTA, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
                        Text(
                            "La cajita empresaria dejó todo listo para seguir administrando el equipo.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
