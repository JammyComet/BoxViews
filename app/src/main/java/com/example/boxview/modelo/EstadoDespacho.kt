package com.example.boxview.modelo

// Modelo: Traduce el estado de los despachos.
/**
 * El estado de la caja es único en Room. Bodega solo necesita saber si
 * el paquete continúa viajando o si su llegada a tienda ya se registró.
 * La comprobación física y sus diferencias son responsabilidad de tienda.
 */
fun estadoDespachoParaBodega(estado: String): String = when (estado) {
    "EN_TRANSITO" -> "En tránsito"
    "EN_REVISION", "RECIBIDA", "CON_DIFERENCIA" -> "Llegó a tienda"
    "ORDEN_ASIGNADA" -> "Por preparar"
    "PENDIENTE_REVISION" -> "Esperando revisión de despacho"
    "CANCELADA" -> "Cancelada"
    else -> estado.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }
}

fun llegadaATiendaRegistrada(estado: String): Boolean =
    estado in setOf("EN_REVISION", "RECIBIDA", "CON_DIFERENCIA")
