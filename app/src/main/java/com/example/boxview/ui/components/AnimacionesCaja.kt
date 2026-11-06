package com.example.boxview.ui.components

// Componente: Animaciones visuales al ingresar o sacar cajas.
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

private val marron = Color(0xFF976B4E)
private val marronOscuro = Color(0xFF644531)
private val fondo = Color(0xFFF3E7D9)

private fun DrawScope.caja(x: Float, y: Float, abierta: Float, alas: Float = 0f, carga: Float = 0f) {
    val ancho = 136f
    val alto = 90f
    val tonos = listOf(Color(0xFFB76B5A), Color(0xFF4B7B60), Color(0xFF6687AD), Color(0xFFDEB671))
    tonos.forEachIndexed { i, color ->
        val entrada = (carga * 4f - i).coerceIn(0f, 1f)
        if (entrada > 0f) {
            val yy = y - 95f + 76f * entrada
            drawRoundRect(color, Offset(x + 13f + i * 24f, yy), Size(20f, 18f), CornerRadius(5f))
        }
    }
    if (alas > 0f) {
        val alaL = Path().apply {
            moveTo(x + 4, y + 18)
            quadraticTo(x - 70, y - 60 * alas, x - 78, y - 8 * alas)
            quadraticTo(x - 40, y + 19, x + 10, y + 45)
            close()
        }
        val alaR = Path().apply {
            moveTo(x + ancho - 4, y + 18)
            quadraticTo(x + ancho + 70, y - 60 * alas, x + ancho + 78, y - 8 * alas)
            quadraticTo(x + ancho + 40, y + 19, x + ancho - 10, y + 45)
            close()
        }
        drawPath(alaL, Color(0xFFFDF8EE))
        drawPath(alaR, Color(0xFFFDF8EE))
    }
    drawRoundRect(marron, Offset(x, y), Size(ancho, alto), CornerRadius(9f))
    drawRect(marronOscuro.copy(alpha = .40f), Offset(x + ancho / 2f - 3f, y), Size(6f, alto))
    val levanta = 55f * abierta
    val izquierda = Path().apply {
        moveTo(x, y + 4)
        lineTo(x + ancho / 2f, y + 4)
        lineTo(x + ancho / 2f - 10, y - levanta)
        lineTo(x - 18, y - levanta)
        close()
    }
    val derecha = Path().apply {
        moveTo(x + ancho / 2f, y + 4)
        lineTo(x + ancho, y + 4)
        lineTo(x + ancho + 18, y - levanta)
        lineTo(x + ancho / 2f + 10, y - levanta)
        close()
    }
    drawPath(izquierda, Color(0xFFBE906B))
    drawPath(derecha, Color(0xFFD2A57A))
    drawRoundRect(Color(0xFFF8E3C7), Offset(x + 45, y + 32), Size(47f, 29f), CornerRadius(4f))
    drawRect(marronOscuro, Offset(x + 53, y + 39), Size(3f, 14f))
    drawRect(marronOscuro, Offset(x + 61, y + 39), Size(2f, 14f))
    drawRect(marronOscuro, Offset(x + 70, y + 39), Size(3f, 14f))
    drawRect(marronOscuro, Offset(x + 79, y + 39), Size(2f, 14f))
}

// Componente visual de animacion caja entrada.
@Composable
fun AnimacionCajaEntrada(alTerminar: () -> Unit) {
    val desplazamiento = remember { Animatable(-1.35f) }
    val tapa = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        desplazamiento.animateTo(.14f, tween(420, easing = FastOutSlowInEasing))
        desplazamiento.animateTo(-.065f, tween(145))
        desplazamiento.animateTo(.03f, tween(125))
        desplazamiento.animateTo(0f, tween(140))
        tapa.animateTo(1f, tween(480))
        delay(350)
        alTerminar()
    }
    Column(Modifier.fillMaxWidth().background(fondo, RoundedCornerShape(18.dp)).padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(Modifier.fillMaxWidth().height(212.dp)) {
            val x = (size.width - 136f) / 2f + size.width * desplazamiento.value
            caja(x, size.height / 2f - 35f, tapa.value)
        }
        Text("Abriendo caja…", color = marronOscuro, fontWeight = FontWeight.SemiBold)
    }
}

// animacion de salida de la caja
// Componente visual de animacion caja salida.
@Composable
fun AnimacionCajaSalida(alTerminar: () -> Unit) {
    val carga = remember { Animatable(0f) }
    val tapa = remember { Animatable(1f) }
    val alas = remember { Animatable(0f) }
    val desplazamiento = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        carga.animateTo(1f, tween(700))
        tapa.animateTo(0f, tween(330))
        alas.animateTo(1f, tween(350))
        desplazamiento.animateTo(1.35f, tween(650, easing = FastOutSlowInEasing))
        alTerminar()
    }
    Column(Modifier.fillMaxWidth().background(fondo, RoundedCornerShape(18.dp)).padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(Modifier.fillMaxWidth().height(220.dp)) {
            val x = (size.width - 136f) / 2f + size.width * desplazamiento.value
            caja(x, size.height / 2f - 23f, tapa.value, alas.value, carga.value)
        }
        Text("Despachando caja…", color = marronOscuro, fontWeight = FontWeight.SemiBold)
    }
}
