package com.example.boxview.viewmodel

// Lógica: Comprueba RUT y reglas de ubicación de bodega.
fun posicionCompatible(codigo: String, categoria: String): Boolean {
    if (!codigo.startsWith("Sección ", ignoreCase = true) || !codigo.contains("·")) return true
    val etiqueta = codigo.substringAfter("·").trim()
    return etiqueta.equals(categoria, ignoreCase = true) || etiqueta.equals("Reserva", ignoreCase = true)
}

// validacion del rut
fun rutValido(rut: String): Boolean {
    val limpio = rut.replace(".", "").replace("-", "").trim().uppercase()
    if (!limpio.matches(Regex("[0-9]{7,8}[0-9K]"))) return false
    var suma = 0
    var factor = 2
    for (digito in limpio.dropLast(1).reversed()) {
        suma += (digito - '0') * factor
        factor = if (factor == 7) 2 else factor + 1
    }
    val resto = 11 - suma % 11
    val verificador = when (resto) { 11 -> "0"; 10 -> "K"; else -> resto.toString() }
    return limpio.last().toString() == verificador
}


// categoria de ubicaciones anidadas
suspend fun posicionCompatibleBodega(dao: com.example.boxview.modelo.InventarioDao,
                                      posicion: com.example.boxview.modelo.Posicion,
                                      categoria: String): Boolean {
    var actual: com.example.boxview.modelo.Posicion? = posicion
    val revisados = mutableSetOf<Long>()
    while (actual != null && revisados.add(actual.id)) {
        if (!actual.activa || actual.tipo == "SECCION") return false
        if (!posicionCompatible(actual.codigo, categoria)) return false
        actual = actual.padreId?.let { dao.posicionPorId(it) }
        if (actual?.tipo == "SECCION" && actual.activa) {
            if (!posicionCompatible(actual.codigo, categoria)) return false
            return true
        }
    }
    return actual == null
}
