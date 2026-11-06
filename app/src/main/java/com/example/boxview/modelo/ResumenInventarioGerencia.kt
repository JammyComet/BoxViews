package com.example.boxview.modelo

// Modelo: Agrupa cifras para los resúmenes de gerencia.
// Información resumida para gerencia. Los detalles de ubicaciones quedan en Room.
data class FilaResumenInventario(
    val codigo: String,
    val producto: String,
    val cantidadSistema: Int,
    val cantidadFisica: Int?
)

data class ResumenProductoInventario(
    val codigo: String,
    val producto: String,
    val sistema: Int,
    val fisico: Int,
    val diferencia: Int,
    val registrosSinContar: Int,
    val faltantesBrutos: Int = 0,
    val sobrantesBrutos: Int = 0,
    val lineasConDiferencia: Int = 0
)

fun resumirProductosInventario(filas: List<FilaResumenInventario>): List<ResumenProductoInventario> =
    filas.groupBy { it.codigo }.map { (codigo, registros) ->
        ResumenProductoInventario(
            codigo = codigo,
            producto = registros.last().producto,
            sistema = registros.sumOf { it.cantidadSistema },
            fisico = registros.sumOf { it.cantidadFisica ?: 0 },
            diferencia = registros.sumOf { (it.cantidadFisica ?: it.cantidadSistema) - it.cantidadSistema },
            registrosSinContar = registros.count { it.cantidadFisica == null },
            faltantesBrutos = registros.sumOf { r -> r.cantidadFisica?.let { (r.cantidadSistema-it).coerceAtLeast(0) } ?: 0 },
            sobrantesBrutos = registros.sumOf { r -> r.cantidadFisica?.let { (it-r.cantidadSistema).coerceAtLeast(0) } ?: 0 },
            lineasConDiferencia = registros.count { it.cantidadFisica != null && it.cantidadFisica != it.cantidadSistema }
        )
    }.sortedWith(compareBy({ it.producto }, { it.codigo }))
