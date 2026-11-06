package com.example.boxview.modelo

// Modelo: Datos temporales de formularios de tienda.
// datos de los formularios de jefatura
data class LineaReposicionTiendaModel(
    val productoId: Long,
    val cantidad: String
)

data class FormularioReposicionTiendaModel(
    val bodegaId: Long? = null,
    val productoId: Long? = null,
    val cantidad: String = "",
    val lineas: List<LineaReposicionTiendaModel> = emptyList()
)

data class FormularioTrabajadorTiendaModel(
    val nombre: String = "",
    val correo: String = "",
    val rut: String = "",
    val clave: String = ""
)
