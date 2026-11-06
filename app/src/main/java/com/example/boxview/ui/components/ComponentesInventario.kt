package com.example.boxview.ui.components

// Componente: Botones, tarjetas, filas y campos reutilizables.
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.focus.onFocusChanged

val CAFE = Color(0xFF74523D)
val CAFE_CLARO = Color(0xFF976B4E)
val CREMA = Color(0xFFFBF7F0)
val TINTA = Color(0xFF32261E)
val VERDE = Color(0xFF4B7B60)
val ROJO = Color(0xFFB36142)
val ARENA = Color(0xFFF3E7D9)

// Muestra título y subtítulo de una sección.
@Composable
fun TituloSeccion(eyebrow: String, title: String, subtitle: String? = null) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(eyebrow.uppercase(), fontSize = 10.sp, letterSpacing = 2.sp,
            fontWeight = FontWeight.SemiBold, color = CAFE_CLARO)
        Text(title, fontSize = 26.sp, lineHeight = 30.sp, fontWeight = FontWeight.ExtraBold,
            color = TINTA)
        if (subtitle != null) Text(subtitle, color = Color(0xFF877366), fontSize = 13.sp)
    }
}

// Card reutilizable que agrupa contenido.
@Composable
fun TarjetaPanel(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Surface(modifier = modifier.fillMaxWidth().animateContentSize(),
        shape = RoundedCornerShape(18.dp), color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFEADFD5)),
        shadowElevation = 1.dp) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp), content = content)
    }
}

// Botón reutilizable con estado habilitado y clic.
@Composable
fun BotonAccion(text: String, onClick: () -> Unit, modifier: Modifier = Modifier,
    outlined: Boolean = false, enabled: Boolean = true,
    icon: ImageVector? = null) {
    val animatedBackground by animateColorAsState(
        targetValue = if (enabled) CAFE else Color(0xFFB8ABA0), label = "color-boton"
    )
    if (outlined) {
        OutlinedButton(onClick = onClick, enabled = enabled, modifier = modifier.fillMaxWidth().heightIn(min = 46.dp),
            shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, CAFE)) {
            if (icon != null) { Icon(icon, null, modifier = Modifier.size(17.dp)); Spacer(Modifier.width(8.dp)) }
            Text(text, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        }
    } else {
        Button(onClick = onClick, enabled = enabled, modifier = modifier.fillMaxWidth().heightIn(min = 46.dp),
            shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = animatedBackground)) {
            if (icon != null) { Icon(icon, null, modifier = Modifier.size(17.dp)); Spacer(Modifier.width(8.dp)) }
            Text(text, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        }
    }
}

// Etiqueta coloreada para mostrar estados.
@Composable
fun EtiquetaEstado(text: String, warning: Boolean = false, success: Boolean = false) {
    val color = if (warning) ROJO else if (success) VERDE else CAFE
    Surface(color = color.copy(alpha = 0.11f), shape = RoundedCornerShape(50)) {
        Text(text, color = color, fontWeight = FontWeight.Bold, fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
    }
}

// Card con un número o dato resumido.
@Composable
fun TarjetaResumen(title: String, value: String, modifier: Modifier = Modifier, icon: ImageVector? = null) {
    TarjetaPanel(modifier) {
        if (icon != null) Icon(icon, null, tint = CAFE_CLARO, modifier = Modifier.size(19.dp))
        Text(value, fontSize = 27.sp, fontWeight = FontWeight.Bold, color = CAFE)
        Text(title, fontSize = 12.sp, color = Color(0xFF817165))
    }
}

// Fila pulsable con icono y descripción.
@Composable
fun FilaOpcion(title: String, subtitle: String, icon: ImageVector = Icons.Default.Inventory2,
    badge: String? = null, warning: Boolean = false, onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null) {
    val content: @Composable () -> Unit = {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.size(43.dp).background(ARENA, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = CAFE, modifier = Modifier.size(21.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(title, color = TINTA, fontWeight = FontWeight.Bold,
                    maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 13.sp)
                Text(subtitle, color = Color(0xFF84776B), fontSize = 11.sp,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            if (badge != null) EtiquetaEstado(badge, warning = warning, success = !warning)
            if (trailing != null) trailing()
            if (onClick != null) Icon(Icons.Default.ChevronRight, null, tint = CAFE)
        }
    }
    Surface(shape = RoundedCornerShape(15.dp), color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFEEE4DA)),
        modifier = if (onClick != null) Modifier.fillMaxWidth().clickable { onClick() } else Modifier.fillMaxWidth()) {
        content()
    }
}

// Campo de texto con validación y mensaje de error.
@Composable
fun CampoFormulario(label: String, value: String, onChange: (String) -> Unit,
    modifier: Modifier = Modifier, error: String? = null,
    singleLine: Boolean = true,
    keyboardType: androidx.compose.ui.text.input.KeyboardType = androidx.compose.ui.text.input.KeyboardType.Text,
    esContrasena: Boolean = false, enabled: Boolean = true) {
    var mostrar by remember { mutableStateOf(false) }
    var modificado by remember(label) { mutableStateOf(false) }
    var recibioFoco by remember(label) { mutableStateOf(false) }
    var salioDelCampo by remember(label) { mutableStateOf(false) }
    val errorVisible = error ?: if (modificado || salioDelCampo) errorDeCampo(label, value) else null
    OutlinedTextField(value = value, onValueChange = { modificado = true; onChange(it) },
        enabled = enabled, modifier = modifier.fillMaxWidth().onFocusChanged { foco ->
            if (foco.isFocused) recibioFoco = true
            else if (recibioFoco) salioDelCampo = true
        },
        label = { Text(label, fontSize = 13.sp) }, isError = errorVisible != null,
        supportingText = if (errorVisible != null) ({ Text(errorVisible, color = MaterialTheme.colorScheme.error) }) else null,
        singleLine = singleLine,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType =
            if (label.trim().equals("correo", ignoreCase = true)) androidx.compose.ui.text.input.KeyboardType.Email
            else if (label.lowercase().startsWith("rut")) androidx.compose.ui.text.input.KeyboardType.Text
            else keyboardType),
        visualTransformation = if (esContrasena && !mostrar)
            androidx.compose.ui.text.input.PasswordVisualTransformation()
        else androidx.compose.ui.text.input.VisualTransformation.None,
        trailingIcon = if (esContrasena) ({
            TextButton(onClick = { mostrar = !mostrar }) { Text(if (mostrar) "Ocultar" else "Ver") }
        }) else null)
}
