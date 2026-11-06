package com.example.boxview

// Convierte errores inesperados en mensajes comprensibles para quien usa BoxView.
private val detallesTecnicos = listOf(
    "logcat", "sqlite", "room", "android.", "androidx.", "java.", "kotlin.",
    "exception", "database", "constraint", "foreign key", "migration", "migración",
    "stacktrace", ".kt", ".db", "demostración", "datos iniciales", "\n", "\r"
)

// Utilidad compartida; no es una segunda Activity.
fun mensajeErrorUsuario(error: Throwable, mensajeAlternativo: String): String {
    val detalle = error.message?.trim().orEmpty()
    val validacion = error is IllegalArgumentException || error is IllegalStateException
    val esTecnico = detallesTecnicos.any { detalle.contains(it, ignoreCase = true) }
    return if (validacion && detalle.length in 1..140 && !esTecnico) detalle else mensajeAlternativo
}
