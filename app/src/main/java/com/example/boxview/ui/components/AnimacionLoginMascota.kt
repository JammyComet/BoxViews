package com.example.boxview.ui.components

// Componente: Mascota y animación al completar el acceso.
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.boxview.R
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

// Muestra el dibujo de mascota en el login.
@Composable
fun CajaLoginNormal(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val x = w * .13f
        val y = h * .22f
        val ancho = w * .74f
        val alto = h * .64f
        drawRoundRect(Color(0xFFBE906B), Offset(x, y), Size(ancho, alto), CornerRadius(w * .08f))
        drawRoundRect(Color(0xFF916547), Offset(x + ancho * .10f, y + alto * .14f),
            Size(ancho * .80f, alto * .76f), CornerRadius(w * .055f))
        drawRoundRect(Color(0xFFEED0AB), Offset(x, y - h * .075f),
            Size(ancho, h * .22f), CornerRadius(w * .04f))
        drawRect(Color(0xFFCCA77E), Offset(x + ancho * .43f, y - h * .075f),
            Size(ancho * .14f, h * .22f))
        drawRoundRect(Color(0xFFF6E6CD), Offset(x + ancho * .31f, y + alto * .40f),
            Size(ancho * .38f, alto * .20f), CornerRadius(w * .025f))
    }
}

// animacion al iniciar sesion
// Animación de mascota antes de continuar.
@Composable
fun AnimacionLoginMascota(alTerminar: () -> Unit) {
    val acercar = remember { Animatable(0f) }
    val transformacion = remember { Animatable(0f) }
    val caminar = remember { Animatable(0f) }
    val salto = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        acercar.animateTo(1f, keyframes {
            durationMillis = 700
            0f at 0
            1.09f at 540
            1f at 700
        })
        transformacion.animateTo(1f, tween(400))
        coroutineScope {
            launch { caminar.animateTo(1f, tween(1150, easing = FastOutSlowInEasing)) }
            launch {
                repeat(5) {
                    salto.animateTo(-8f, tween(115))
                    salto.animateTo(0f, tween(115))
                }
            }
        }
        alTerminar()
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        val density = LocalDensity.current
        val distanciaCentro = with(density) { (maxHeight / 2 - 42.dp - 58.dp).toPx() }
        val distanciaSalida = with(density) { (maxWidth / 2 + 145.dp).toPx() }
        val saltoPx = with(density) { salto.value.dp.toPx() }
        Box(
            modifier = Modifier
                .offset(y = 42.dp)
                .size(116.dp)
                .graphicsLayer {
                    translationY = distanciaCentro * acercar.value + saltoPx
                    translationX = distanciaSalida * caminar.value
                    scaleX = 1f + .54f * acercar.value
                    scaleY = 1f + .54f * acercar.value
                    rotationZ = if (caminar.value > 0f) salto.value * .65f else 0f
                },
            contentAlignment = Alignment.Center
        ) {
            CajaLoginNormal(Modifier.fillMaxSize().graphicsLayer {
                alpha = 1f - transformacion.value
                scaleX = 1f - .18f * transformacion.value
                scaleY = 1f - .18f * transformacion.value
            })
            Image(
                painter = painterResource(R.drawable.mascota_saludando),
                contentDescription = "Mascota de BoxView entrando",
                modifier = Modifier.fillMaxSize().graphicsLayer {
                    alpha = transformacion.value
                    scaleX = .72f + .28f * transformacion.value
                    scaleY = .72f + .28f * transformacion.value
                }
            )
        }
    }
}
