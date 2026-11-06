package com.example.boxview.ui.theme

// Diseño: Aplica el tema Material 3 a la app.
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Paleta = lightColorScheme(
    primary = Color(0xFF74523D), onPrimary = Color.White,
    primaryContainer = Color(0xFFF1E4D6), onPrimaryContainer = Color(0xFF33251B),
    secondary = Color(0xFF956949), onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3E7D9), onSecondaryContainer = Color(0xFF33251B),
    tertiary = Color(0xFF7A6451), tertiaryContainer = Color(0xFFEFE4D8),
    background = Color(0xFFFBF7F0), onBackground = Color(0xFF33261E),
    surface = Color(0xFFFFFDF9), onSurface = Color(0xFF33261E),
    surfaceVariant = Color(0xFFF0E6DA), onSurfaceVariant = Color(0xFF766457),
    outline = Color(0xFFB9A99B), error = Color(0xFFAD5037)
)

@Composable
fun TemaBoxView(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Paleta, typography = Tipografia, content = content)
}
