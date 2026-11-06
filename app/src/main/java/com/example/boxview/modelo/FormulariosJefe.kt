package com.example.boxview.modelo

// Modelo: Datos temporales de formularios de jefatura.
// formulario de ordenes
data class FilaOrdenNueva(val id: Int, val productoId: Long? = null, val cantidad: String = "")

data class FormularioOrdenModel(
    val bodegaId: Long = -1L,
    val destino: Long? = null,
    val operador: Long? = null,
    val filas: List<FilaOrdenNueva> = listOf(FilaOrdenNueva(1)),
    val siguienteFila: Int = 2
)

// formulario de operador
data class FormularioOperadorModel(
    val nombre: String = "",
    val correo: String = "",
    val rut: String = "",
    val clave: String = ""
)
