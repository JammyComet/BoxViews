package com.example.boxview.modelo

// Modelo: Datos temporales del formulario de recepción.
// estado del formulario de recepcion
data class FormularioRecepcionModel(
    val cajaId: Long? = null,
    val cantidades: Map<Long, String> = emptyMap(),
    val ubicaciones: List<UbicacionRecepcionForm> = emptyList(),
    val observacion: String = "",
    val comprobado: Boolean = false
)


// posiciones de los productos
data class UbicacionRecepcionForm(
    val clave: Long,
    val detalleId: Long,
    val posicionId: Long? = null,
    val cantidad: String = ""
)

// datos para guardar
data class DistribucionRecepcion(
    val detalleId: Long,
    val posicionId: Long,
    val cantidad: Int
)
