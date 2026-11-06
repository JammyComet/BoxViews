package com.example.boxview.ui.components

// Componente: Muestra nombres claros en el historial.
/** Presentación corta para movimientos antiguos creados con la base de ejemplo.
 * No altera los motivos originales almacenados por Room. */
fun motivoVisibleHistorial(motivo: String, tipo: String): String {
    val limpio = motivo.trim()
    return when {
        limpio.equals("Venta registrada en la demostración inicial", ignoreCase = true) -> "Venta registrada"
        limpio.equals("Prenda dañada, baja inicial de demostración", ignoreCase = true) -> "Prenda dañada"
        limpio.contains("demostración inicial", ignoreCase = true) && tipo == "VENTA" -> "Venta registrada"
        else -> limpio
    }
}
