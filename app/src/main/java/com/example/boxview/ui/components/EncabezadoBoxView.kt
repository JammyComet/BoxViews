package com.example.boxview.ui.components

// Componente: Encabezado común con el nombre de BoxView.
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.boxview.R

// Componente visual de encabezado box.
@Composable
fun EncabezadoBoxView(
    titulo: String = "BoxView",
    modifier: Modifier = Modifier,
    compacto: Boolean = false,
    onMenu: (() -> Unit)? = null,
    onAlertas: (() -> Unit)? = null
) {
    Box(
        modifier = modifier.fillMaxWidth().height(if (compacto) 108.dp else 160.dp)
    ) {
        Canvas(Modifier.matchParentSize()) {
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width, size.height * 0.84f)
                cubicTo(size.width * 0.66f, size.height * 0.95f,
                    size.width * 0.39f, size.height * 0.82f,
                    0f, size.height * 0.94f)
                close()
            }
            drawPath(path, Color(0xFF74523D))
        }
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = if (compacto) 26.dp else 48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (onMenu != null) {
                IconButton(onClick = onMenu, modifier = Modifier.size(34.dp)) {
                    Icon(Icons.Default.Menu,
                        contentDescription = "Abrir menú", tint = Color.White)
                }
            }
            Box(
                modifier = Modifier.size(37.dp).background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(painterResource(R.drawable.mascota_logo), contentDescription = "Mascota BoxView",
                    modifier = Modifier.size(32.dp))
            }
            Text(titulo, color = Color.White, fontWeight = FontWeight.Bold,
                fontSize = if (compacto) 20.sp else 29.sp,
                modifier = Modifier.weight(1f))
            if (onAlertas != null) {
                IconButton(onClick = onAlertas) {
                    Icon(Icons.Default.Notifications,
                        contentDescription = "Alertas", tint = Color.White)
                }
            }
        }
    }
}
