package com.example.boxview.modelo

// Modelo: Datos temporales del formulario de traslado.
// estado del formulario de traslado
data class FormularioTrasladoModel(
    val productoId: Long? = null,
    val origenId: Long? = null,
    val destinoId: Long? = null,
    val cantidad: String = ""
)
