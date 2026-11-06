package com.example.boxview.ui.view

// Pantallas: Presentación inicial y animación de entrada.
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.boxview.ui.components.CAFE
import com.example.boxview.ui.components.CAFE_CLARO
import com.example.boxview.ui.components.CREMA
import com.example.boxview.ui.components.TINTA
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// animacion de inicio
// Splash: ejecuta la animación inicial.
@Composable
fun PantallaBienvenida(alFinalizar: () -> Unit) {
    val animations = remember { List(4) { Animatable(0f) } }
    var arrived by remember { mutableStateOf(false) }
    val boxScale by animateFloatAsState(if (arrived) 1.12f else 1f, label = "caja-llegada")

    LaunchedEffect(Unit) {
        coroutineScope {
            animations.forEachIndexed { index, anim ->
                launch {
                    delay(index * 180L)
                    anim.animateTo(1f, tween(1000, easing = FastOutSlowInEasing))
                }
            }
        }
        arrived = true
        delay(450)
        alFinalizar()
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize().background(CREMA),
        contentAlignment = Alignment.Center
    ) {
        val density = LocalDensity.current
        val width = with(density) { maxWidth.toPx() }
        val height = with(density) { maxHeight.toPx() }
        val icons: List<ImageVector> = listOf(
            Icons.Default.ShoppingCart, Icons.Default.QrCode2,
            Icons.Default.LocalShipping, Icons.Default.Warehouse
        )
        val origins = listOf(
            (-width * 0.36f) to (-height * 0.25f),
            (width * 0.37f) to (-height * 0.22f),
            (-width * 0.34f) to (height * 0.23f),
            (width * 0.35f) to (height * 0.20f)
        )
        icons.forEachIndexed { index, icon ->
            val progress = animations[index].value
            val (fromX, fromY) = origins[index]
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (index % 2 == 0) CAFE else CAFE_CLARO,
                modifier = Modifier.size(45.dp).graphicsLayer {
                    translationX = fromX * (1f - progress)
                    translationY = fromY * (1f - progress)
                    rotationZ = if (index % 2 == 0) 25f * (1f - progress) else -25f * (1f - progress)
                    alpha = ((1f - progress) / 0.30f).coerceIn(0f, 1f)
                }
            )
        }
        Box(
            modifier = Modifier
                .size(118.dp)
                .graphicsLayer { scaleX = boxScale; scaleY = boxScale }
                .clip(RoundedCornerShape(28.dp))
                .background(CAFE),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Inventory2, contentDescription = "Caja BoxView",
                modifier = Modifier.size(70.dp), tint = Color.White)
        }
        Column(
            modifier = Modifier.align(Alignment.Center).offset(y = 126.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("BoxView", color = TINTA, fontSize = 31.sp,
                fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(7.dp))
            Text("Gestión de inventario y operaciones", color = CAFE_CLARO,
                fontSize = 13.sp, textAlign = TextAlign.Center)
        }
    }
}
