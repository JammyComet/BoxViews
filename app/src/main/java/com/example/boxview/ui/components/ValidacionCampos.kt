package com.example.boxview.ui.components

// Componente: Valida correo, RUT y otros campos del formulario.
import com.example.boxview.viewmodel.rutValido

/** Reglas de presentación: la comprobación final sigue en los ViewModels. */
fun correoBienFormado(correo: String): Boolean =
    correo.trim().matches(Regex("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) &&
        !correo.contains("..")

fun errorDeCampo(etiqueta: String, texto: String): String? {
    val tipo = etiqueta.trim().lowercase()
    val v = texto.trim()
    val esCorreo = tipo == "correo" || tipo == "correo electrónico"
    val esRut = tipo == "rut" || tipo.startsWith("rut (") || tipo.startsWith("rut con")
    val esClaveInicial = tipo.startsWith("contraseña inicial")
    val esClaveAcceso = tipo == "contraseña"
    val esNombre = tipo == "nombre" || tipo == "nombre completo" || tipo == "nuevo nombre" || tipo == "nombre de bodega"
    val esMonto = tipo.contains("precio de venta") || tipo.contains("clp por unidad")
    val esNumero = tipo.startsWith("cantidad") || tipo.startsWith("unidades ") ||
        tipo.startsWith("precio ") || tipo.startsWith("nuevo mínimo") || esMonto
    val obligatorio = esCorreo || esRut || esClaveInicial || esClaveAcceso || esNombre || esNumero
    if (v.isEmpty()) return if (obligatorio) "Este campo es obligatorio" else null
    return when {
        esCorreo && !correoBienFormado(v) -> "Ingresa un correo válido, por ejemplo nombre@empresa.cl"
        esRut && !rutValido(v) -> "RUT inválido. Revisa el número y el dígito verificador"
        esClaveInicial && v.length < 8 -> "Usa al menos 8 caracteres"
        esNombre && v.length < 3 -> "Ingresa al menos 3 caracteres"
        esNumero && (!v.all(Char::isDigit) || v.toLongOrNull() == null) -> "Ingresa un número válido"
        esMonto && (v.toLongOrNull() ?: 0L) <= 0L -> "El precio debe ser mayor que cero"
        else -> null
    }
}
