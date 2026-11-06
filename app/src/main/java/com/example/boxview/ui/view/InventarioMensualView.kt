package com.example.boxview.ui.view

// Pantallas: Conteo mensual de productos en bodega.
import com.example.boxview.modelo.*

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.example.boxview.modelo.BorradorInventario
import com.example.boxview.ui.components.*
import com.example.boxview.viewmodel.OperadorViewModel
import kotlinx.coroutines.flow.map

private data class FilaFisica(val productoId: Long, val posicionId: Long, val cantidad: String)

// Muestra la pantalla de inventario mensual.
@Composable
fun PantallaInventarioMensual(vm: OperadorViewModel) {
    // Datos observados por esta pantalla.
    val periodo by remember(vm.bodegaActiva?.id, vm.sesion?.id) { vm.periodoInventario() }
        .collectAsState(initial = null)
    val productos by vm.productos.collectAsState(initial = emptyList())
    val posiciones by remember(vm.bodegaActiva?.id) { vm.posiciones() }.collectAsState(initial = emptyList())
    val zonasIds by remember(periodo?.id) { vm.zonasAsignadasInventario(periodo?.id ?: -1) }
        .collectAsState(initial = emptyList())
    val verificadosVacios by remember(periodo?.id) { vm.estantesVaciosInventario(periodo?.id ?: -1) }
        .collectAsState(initial = emptyList())
    val estantesVacios = verificadosVacios.map { it.posicionId }.toSet()
    val referencias by remember(periodo?.inventarioBodegaId) {
        vm.referenciasInventario(periodo?.inventarioBodegaId ?: -1)
    }.collectAsState(initial = emptyList())
    val borrador by remember(periodo?.id) {
        vm.borradorInventario(periodo?.id ?: -1).map<List<BorradorInventario>, List<BorradorInventario>?> { it }
    }.collectAsState(initial = null)
    val filas = remember(vm.bodegaActiva?.id, periodo?.id, periodo?.estado) { mutableStateListOf<FilaFisica>() }
    var borradorCargado by remember(vm.bodegaActiva?.id, periodo?.id, periodo?.estado) { mutableStateOf(false) }
    var posicionActiva by remember(vm.bodegaActiva?.id, periodo?.id) { mutableStateOf<Long?>(null) }
    var codigoManual by remember(vm.bodegaActiva?.id, periodo?.id) { mutableStateOf("") }
    var lectorAbierto by remember(vm.bodegaActiva?.id, periodo?.id) { mutableStateOf(false) }
    var guardando by remember(vm.bodegaActiva?.id, periodo?.id) { mutableStateOf(false) }
    var mensaje by remember(vm.bodegaActiva?.id, periodo?.id) { mutableStateOf("") }
    var confirmacion by remember(vm.bodegaActiva?.id, periodo?.id) { mutableStateOf(false) }
    var revisionFisica by remember(vm.bodegaActiva?.id, periodo?.id, periodo?.estado) { mutableStateOf(false) }
    var confirmarVacio by remember(periodo?.id) { mutableStateOf(false) }
    val posicionesAsignadas = posiciones.filter { it.id in zonasIds }
    val referenciasAsignadas = referencias.filter { it.posicionId in zonasIds }
    val faltantes = referenciasAsignadas.filter { ref ->
        filas.none { it.productoId == ref.productoId && it.posicionId == ref.posicionId }
    }
    LaunchedEffect(zonasIds) {
        if (posicionActiva !in zonasIds) posicionActiva = zonasIds.firstOrNull()
    }
    val actual = periodo

    LaunchedEffect(actual?.id, actual?.estado, borrador) {
        if (actual != null && borrador != null && !borradorCargado) {
            filas.clear()
            borrador.orEmpty().forEach { d ->
                filas.add(FilaFisica(d.productoId, d.posicionId, d.cantidad.toString()))
            }
            borradorCargado = true
        }
    }

    fun agregarProducto(codigo: String) {
        val pos = posicionActiva
        if (pos in estantesVacios) { mensaje = "El estante está confirmado vacío. Selecciona Volver a contar para editarlo"; return }
        if (pos == null) { mensaje = "Selecciona el estante que estás contando"; return }
        val producto = productos.firstOrNull { it.codigo.equals(codigo.trim(), ignoreCase = true) }
        if (producto == null) { mensaje = "Código no registrado en el catálogo"; return }
        val i = filas.indexOfFirst { it.productoId == producto.id && it.posicionId == pos }
        if (i >= 0) {
            val n = filas[i].cantidad.toIntOrNull() ?: 0
            if (n >= 100000) { mensaje = "Límite de unidades alcanzado"; return }
            filas[i] = filas[i].copy(cantidad = (n + 1).toString())
        } else {
            filas.add(FilaFisica(producto.id, pos, "1"))
        }
        codigoManual = ""
        mensaje = ""
    }

    fun registros() = filas.map { f -> OperadorViewModel.RegistroInventario(
        f.productoId, f.posicionId, f.cantidad.toInt()) }

    ColumnaOperador {
        TituloSeccion("INVENTARIO", "Inventario mensual", vm.bodegaActiva?.nombre.orEmpty())
        if (actual == null) {
            TarjetaPanel {
                Text("No hay un inventario mensual abierto para esta bodega.")
                Text("Sin estantes asignados", style = MaterialTheme.typography.bodySmall)
            }
            return@ColumnaOperador
        }
        TarjetaPanel {
            Text(actual.nombre, fontWeight = FontWeight.Bold)
            Text("Estantes asignados: ${posicionesAsignadas.size}. Mientras el inventario esté abierto, quedan bloqueados para movimientos.", style = MaterialTheme.typography.bodySmall)
            EtiquetaEstado(if (actual.estado == "ABIERTO") "Inventario activo" else "Inventario entregado",
                success = actual.estado == "ENTREGADO")
        }
        if (actual.estado != "ABIERTO") {
            val guardadas by remember(actual.id) { vm.lineasInventario(actual.id) }
                .collectAsState(initial = emptyList())
            TarjetaPanel {
                Text("Inventario mensual guardado", fontWeight = FontWeight.Bold)
                Text("Productos y posiciones registrados: ${guardadas.size}")
                Text("Pendiente de revisión por jefatura.", style = MaterialTheme.typography.bodySmall)
            }
            return@ColumnaOperador
        }
        if (!borradorCargado) {
            TarjetaPanel { Text("Cargando inventario…") }
            return@ColumnaOperador
        }
        TarjetaPanel {
            Text("1. Seleccionar estante", fontWeight = FontWeight.Bold)
            SelectorSimple("Estante actual", posicionesAsignadas.map { it.id to rutaBodega(it, posiciones) }, posicionActiva) {
                posicionActiva = it; lectorAbierto = false; mensaje = ""
            }
            val estante = posicionActiva
            if (estante != null && estante in estantesVacios) {
                EtiquetaEstado("Estante vacío confirmado", success = true)
                BotonAccion("Volver a contar este estante", outlined = true, enabled = !guardando, onClick = {
                    guardando = true
                    vm.reabrirEstanteVacioInventario(actual.id, estante) { _, texto ->
                        mensaje = texto; guardando = false
                    }
                })
            } else if (estante != null) {
                BotonAccion("Confirmar estante vacío", outlined = true, enabled = !guardando, onClick = {
                    confirmarVacio = true
                })
            }
        }
        TarjetaPanel {
            Text("2. Registrar productos", fontWeight = FontWeight.Bold)
            Text("Escanea los productos o introduce las cantidades encontradas.",
                style = MaterialTheme.typography.bodySmall)
            BotonAccion("Cargar productos esperados de mis estantes", outlined = true, enabled = !guardando, onClick = {
                referenciasAsignadas.forEach { ref ->
                    if (filas.none { it.productoId == ref.productoId && it.posicionId == ref.posicionId })
                        filas.add(FilaFisica(ref.productoId, ref.posicionId, ""))
                }
                mensaje = "Completa la cantidad física de cada producto, incluso si es cero"
            })
            CampoFormulario("Código de producto", codigoManual, { codigoManual = it })
            BotonAccion("Añadir código manual", icon = Icons.Default.Add,
                enabled = !guardando && codigoManual.isNotBlank() && posicionActiva != null && posicionActiva !in estantesVacios,
                onClick = { agregarProducto(codigoManual) })
            BotonAccion(if (lectorAbierto) "Cerrar cámara" else "Escanear QR", outlined = true,
                icon = Icons.Default.QrCodeScanner, enabled = !guardando && posicionActiva != null && posicionActiva !in estantesVacios,
                onClick = { lectorAbierto = !lectorAbierto })
            if (lectorAbierto) {
                CamaraEscaner(alLeer = { codigo ->
                    lectorAbierto = false
                    agregarProducto(codigo)
                })
            }
        }
        if (filas.isNotEmpty()) {
            Text("3. Revisar cantidades (${filas.size} registros)", style = MaterialTheme.typography.titleMedium)
            filas.forEach { fila ->
                key(fila.productoId, fila.posicionId) {
                    val producto = productos.firstOrNull { it.id == fila.productoId }
                    val pos = posiciones.firstOrNull { it.id == fila.posicionId }
                    TarjetaPanel {
                        Text(producto?.nombre ?: "Producto", fontWeight = FontWeight.Bold)
                        Text("${producto?.codigo.orEmpty()} · ${pos?.let { rutaBodega(it, posiciones) } ?: "Posición"}")
                        CampoFormulario("Cantidad física", fila.cantidad, { nuevo ->
                            if (fila.posicionId in estantesVacios) return@CampoFormulario
                            val i = filas.indexOfFirst { it.productoId == fila.productoId && it.posicionId == fila.posicionId }
                            if (i >= 0) filas[i] = filas[i].copy(cantidad = nuevo)
                        }, keyboardType = KeyboardType.Number, enabled = fila.posicionId !in estantesVacios)
                        TextButton(enabled = !guardando && fila.posicionId !in estantesVacios, onClick = {
                            filas.removeAll { it.productoId == fila.productoId && it.posicionId == fila.posicionId }
                        }) { Text("Quitar producto de este estante") }
                    }
                }
            }
        }
        if (faltantes.isNotEmpty()) TarjetaPanel {
            Text("Productos pendientes de comprobar", fontWeight = FontWeight.Bold)
            Text("Registra cero cuando no encuentres unidades.",
                style = MaterialTheme.typography.bodySmall)
            faltantes.forEach { ref ->
                val producto = productos.firstOrNull { it.id == ref.productoId }
                val estante = posiciones.firstOrNull { it.id == ref.posicionId }
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = androidx.compose.ui.Modifier.fillMaxWidth()) {
                    Text("${producto?.codigo ?: ref.productoId} · ${estante?.let { rutaBodega(it, posiciones) } ?: ref.posicionId}",
                        modifier = androidx.compose.ui.Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                    TextButton(enabled = !guardando && ref.posicionId !in estantesVacios, onClick = {
                        filas.add(FilaFisica(ref.productoId, ref.posicionId, "0"))
                    }) { Text("Registrar 0") }
                }
            }
        }
        Row { Checkbox(checked = revisionFisica, onCheckedChange = { revisionFisica = it })
            Text("Comprobé físicamente todos los estantes asignados y registré sus productos, incluso los que faltan.",
                style = MaterialTheme.typography.bodySmall)
        }
        if (mensaje.isNotBlank()) Text(mensaje, color = MaterialTheme.colorScheme.primary)
        val validas = filas.all { it.cantidad.toIntOrNull()?.let { n -> n in 0..100000 } == true }
        BotonAccion("Guardar borrador", outlined = true, enabled = !guardando && validas, onClick = {
            guardando = true
            vm.guardarBorradorInventario(actual.id, registros()) { _, respuesta ->
                mensaje = respuesta; guardando = false
            }
        })
        BotonAccion("Entregar el conteo a jefatura", enabled = !guardando && validas && faltantes.isEmpty() && revisionFisica && zonasIds.isNotEmpty(),
            onClick = { confirmacion = true })
        if (confirmarVacio && posicionActiva != null) {
            val estanteId = posicionActiva!!
            val nombreEstante = posiciones.firstOrNull { it.id == estanteId }
                ?.let { rutaBodega(it, posiciones) } ?: "Estante $estanteId"
            AlertDialog(onDismissRequest = { confirmarVacio = false },
                title = { Text("Confirmar estante vacío") },
                text = { Text("¿Confirmas que $nombreEstante está vacío? Se registrará un conteo de cero unidades.") },
                confirmButton = { TextButton(enabled = !guardando, onClick = {
                    confirmarVacio = false
                    guardando = true
                    vm.confirmarEstanteVacioInventario(actual.id, estanteId) { correcto, texto ->
                        guardando = false; mensaje = texto
                        if (correcto) {
                            filas.removeAll { it.posicionId == estanteId }
                            referenciasAsignadas.filter { it.posicionId == estanteId }.forEach { ref ->
                                filas.add(FilaFisica(ref.productoId, ref.posicionId, "0"))
                            }
                            lectorAbierto = false
                        }
                    }
                }) { Text("Confirmar vacío") } },
                dismissButton = { TextButton(onClick = { confirmarVacio = false }) { Text("Volver") } })
        }
        if (confirmacion) AlertDialog(onDismissRequest = { confirmacion = false },
            title = { Text("Entregar inventario mensual") },
            text = { Text("Entregarás el conteo de ${zonasIds.size} estantes y ${filas.size} registros. Después no podrás modificarlo.") },
            confirmButton = { TextButton(enabled = !guardando, onClick = {
                confirmacion = false
                guardando = true
                vm.guardarInventarioMensual(actual.id, registros(), revisionFisica) { _, respuesta ->
                    mensaje = respuesta; guardando = false
                }
            }) { Text("Guardar") } },
            dismissButton = { TextButton(enabled = !guardando, onClick = { confirmacion = false }) { Text("Cancelar") } })
    }
}
