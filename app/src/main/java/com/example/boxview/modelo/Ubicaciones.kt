package com.example.boxview.modelo

// Modelo: Convierte ubicaciones en nombres y rutas legibles.
// etiquetas de ubicaciones
fun Posicion.etiqueta(): String = nombre.ifBlank { codigo }
fun PosicionTienda.etiqueta(): String = nombre.ifBlank { codigo }

fun rutaBodega(posicion: Posicion, posiciones: List<Posicion>): String {
    val niveles = mutableListOf(posicion.etiqueta())
    var padre = posicion.padreId
    val vistos = mutableSetOf(posicion.id)
    while (padre != null && vistos.add(padre)) {
        val actual = posiciones.firstOrNull { it.id == padre } ?: break
        niveles.add(actual.etiqueta())
        padre = actual.padreId
    }
    return niveles.asReversed().joinToString(" / ")
}

fun rutaTienda(posicion: PosicionTienda, posiciones: List<PosicionTienda>): String {
    val niveles = mutableListOf(posicion.etiqueta())
    var padre = posicion.padreId
    val vistos = mutableSetOf(posicion.id)
    while (padre != null && vistos.add(padre)) {
        val actual = posiciones.firstOrNull { it.id == padre } ?: break
        niveles.add(actual.etiqueta())
        padre = actual.padreId
    }
    return niveles.asReversed().joinToString(" / ")
}
