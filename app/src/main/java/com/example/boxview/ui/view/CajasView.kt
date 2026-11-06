package com.example.boxview.ui.view

// Pantallas: Detalle y movimientos de cajas de bodega.
import com.example.boxview.modelo.*

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.boxview.modelo.PreparacionCaja
import com.example.boxview.modelo.Existencia
import com.example.boxview.modelo.ReservaEstante
import com.example.boxview.ui.components.*
import com.example.boxview.viewmodel.OperadorViewModel
import kotlinx.coroutines.flow.map

// productos por estante
private data class FilaPreparacion(val id: Int, val detalleId: Long, val posicionId: Long?, val cantidad: String)

// Muestra la pantalla de detalle caja.
@Composable
fun PantallaDetalleCaja(vm: OperadorViewModel, alDespachar: () -> Unit) {
    // Datos observados por esta pantalla.
    val caja by remember(vm.cajaActivaId) { vm.cajaActiva() }.collectAsState(initial = null)
    val detalles by remember(vm.cajaActivaId) { vm.detallesCajaActiva() }.collectAsState(initial = emptyList())
    val preparaciones by remember(vm.cajaActivaId) {
        vm.preparacionesCajaActiva().map<List<PreparacionCaja>, List<PreparacionCaja>?> { it }
    }.collectAsState(initial = null)
    val productos by vm.productos.collectAsState(initial = emptyList())
    val posiciones by remember(vm.bodegaActiva?.id) { vm.posiciones() }.collectAsState(initial = emptyList())
    val campanias by remember(vm.bodegaActiva?.id) { vm.campaniasBodega() }.collectAsState(initial = emptyList())
    val inventarioMensual = campanias.any { it.estado == "ABIERTA" }
    val posicionRecepcion = posiciones.firstOrNull { it.tipo == "RECEPCION" && it.activa }
    val existencias by remember(vm.bodegaActiva?.id) {
        vm.existencias().map<List<Existencia>, List<Existencia>?> { it }
    }.collectAsState(initial = null)
    val reservas by remember(vm.bodegaActiva?.id, vm.cajaActivaId) {
        vm.reservasOtrasCajas(vm.cajaActivaId ?: 0L)
            .map<List<ReservaEstante>, List<ReservaEstante>?> { it }
    }.collectAsState(initial = null)
    val tiendas by vm.sucursales.collectAsState(initial = emptyList())
    val cantidades = remember(vm.cajaActivaId) { mutableStateMapOf<Long, String>() }
    val ubicaciones = remember(vm.cajaActivaId) { mutableStateMapOf<Long, Long>() }
    val filasSalida = remember(vm.cajaActivaId) { mutableStateListOf<FilaPreparacion>() }
    var siguienteFila by remember(vm.cajaActivaId) { mutableIntStateOf(1) }
    var preparacionCargada by remember(vm.cajaActivaId) { mutableStateOf(false) }
    var mensaje by remember(vm.cajaActivaId) { mutableStateOf("") }
    var animEntrada by remember(vm.cajaActivaId) { mutableStateOf(true) }
    var animSalida by remember(vm.cajaActivaId) { mutableStateOf(false) }
    var terminado by remember(vm.cajaActivaId) { mutableStateOf(false) }
    var confirmar by remember(vm.cajaActivaId) { mutableStateOf(false) }
    var guardando by remember(vm.cajaActivaId) { mutableStateOf(false) }
    var declaracion by remember(vm.cajaActivaId) { mutableStateOf(false) }
    val ingreso = vm.modoCaja == "INGRESO"
    val consulta = vm.modoCaja == "CONSULTA_SALIDA"

    LaunchedEffect(detalles, ingreso) {
        if (ingreso) detalles.forEach { d ->
            if (d.id !in cantidades) cantidades[d.id] = (d.cantidadContadaFisica ?: d.cantidadRecibida).toString()
            if (d.posicionDestinoId != null && d.id !in ubicaciones) ubicaciones[d.id] = d.posicionDestinoId
        }
    }
    LaunchedEffect(detalles, preparaciones, ingreso, consulta) {
        if (!ingreso && !consulta && !preparacionCargada && detalles.isNotEmpty() && preparaciones != null) {
            detalles.forEach { d ->
                val previas = preparaciones.orEmpty().filter { it.productoId == d.productoId }
                if (previas.isEmpty()) filasSalida.add(FilaPreparacion(siguienteFila++, d.id, null, "0"))
                else previas.forEach { p ->
                    filasSalida.add(FilaPreparacion(siguienteFila++, d.id, p.posicionId, p.cantidad.toString()))
                }
            }
            preparacionCargada = true
        }
    }
    fun stockEn(productoId: Long, posicionId: Long): Int =
        existencias.orEmpty().firstOrNull { it.productoId == productoId && it.posicionId == posicionId }?.cantidad ?: 0
    fun reservadoOtros(productoId: Long, posicionId: Long): Int =
        reservas.orEmpty().firstOrNull { it.productoId == productoId && it.posicionId == posicionId }?.cantidadReservada ?: 0
    fun libreEn(productoId: Long, posicionId: Long): Int =
        (stockEn(productoId, posicionId) - reservadoOtros(productoId, posicionId)).coerceAtLeast(0)
    fun registrosSalida(): List<OperadorViewModel.RegistroPreparacion> = filasSalida.mapNotNull { fila ->
        val n = fila.cantidad.toIntOrNull() ?: return@mapNotNull null
        if (n == 0) return@mapNotNull null
        val pos = fila.posicionId ?: return@mapNotNull null
        OperadorViewModel.RegistroPreparacion(fila.detalleId, pos, n)
    }

    ColumnaOperador {
        if (caja == null) {
            Text("Abriendo caja…")
            return@ColumnaOperador
        }
        val actual = caja!!
        // Un acceso directo al detalle no permite comprobar una caja sin registrar su llegada.
        if (ingreso && actual.estado !in listOf("EN_REVISION", "RECIBIDA", "CON_DIFERENCIA")) {
            TituloSeccion("RECEPCIÓN DE CAJA", "Llegada pendiente",
                "Primero registra el QR o código de la caja desde Ingresos pendientes")
            return@ColumnaOperador
        }
        TituloSeccion(if (ingreso) "RECEPCIÓN DE CAJA" else "ORDEN DE JEFATURA", actual.codigo,
            if (ingreso) "Verifica las cantidades físicas" else "Prepara las unidades solicitadas")
        if (ingreso && animEntrada) {
            AnimacionCajaEntrada { animEntrada = false }
            return@ColumnaOperador
        }
        if (animSalida) {
            AnimacionCajaSalida { animSalida = false; alDespachar() }
            return@ColumnaOperador
        }
        TarjetaPanel {
            Text("Origen: ${actual.origen}", fontWeight = FontWeight.Bold)
            Text("Destino: ${if (ingreso) vm.bodegaActiva?.nombre.orEmpty() else tiendas.find { it.id == actual.sucursalDestinoId }?.nombre ?: "Tienda no identificada"}",
                style = MaterialTheme.typography.bodySmall)
            if (!ingreso) Text("Solicitada por: ${actual.solicitadoPor}", style = MaterialTheme.typography.bodySmall)
            EtiquetaEstado(when (actual.estado) {
                "ORDEN_ASIGNADA" -> "Por preparar"
                "PENDIENTE_REVISION" -> "Esperando revisión"
                "EN_TRANSITO" -> "En tránsito"
                "EN_REVISION" -> if (ingreso) "Recibida en bodega · por comprobar" else "Llegó a tienda"
                "RECIBIDA" -> if (ingreso) "Recibida" else "Llegó a tienda"
                "CON_DIFERENCIA" -> if (ingreso) "Recibida con diferencia" else "Llegó a tienda"
                else -> "Caja esperada"
            }, warning = actual.estado in listOf("ORDEN_ASIGNADA", "PENDIENTE_REVISION"),
                success = actual.estado in listOf("EN_TRANSITO", "RECIBIDA"))
        }
        if (!ingreso && !consulta && actual.estado in listOf("EN_TRANSITO", "EN_REVISION", "RECIBIDA", "CON_DIFERENCIA")) {
            TarjetaPanel { Text("Salida registrada correctamente") }
            return@ColumnaOperador
        }
        if (consulta) {
            TarjetaPanel {
                Text("Etiqueta QR de la caja", fontWeight = FontWeight.Bold)
                CodigoQrCaja(actual.codigo)
                EtiquetaCajaDescargable(actual, detalles, productos,
                    destino = tiendas.firstOrNull { it.id == actual.sucursalDestinoId }?.nombre ?: "Tienda de destino")
                detalles.forEach { d ->
                    val producto = productos.firstOrNull { it.id == d.productoId }
                    Text("${producto?.codigo ?: d.productoId} · ${producto?.nombre ?: "Producto"}: ${d.cantidadEsperada} unidades")
                }
                EtiquetaEstado(estadoDespachoParaBodega(actual.estado),
                    success = llegadaATiendaRegistrada(actual.estado))
            }
            return@ColumnaOperador
        }
        if (ingreso && (terminado || actual.estado in listOf("RECIBIDA", "CON_DIFERENCIA"))) {
            TarjetaPanel { EtiquetaEstado("Recepción registrada", success = true) }
            return@ColumnaOperador
        }
        if (detalles.isEmpty()) Text("Sin productos registrados para esta caja")
        detalles.forEach { d ->
            key(d.id) {
                val producto = productos.firstOrNull { it.id == d.productoId }
                val codigo = producto?.codigo.orEmpty()
                TarjetaPanel {
                    Text(producto?.nombre ?: "Producto #${d.productoId}", fontWeight = FontWeight.Bold)
                    Text("Código: $codigo", style = MaterialTheme.typography.bodyMedium)
                    Text("Solicitadas: ${d.cantidadEsperada} unidades")
                    if (ingreso) {
                        val sugeridas = posiciones.sortedWith(compareByDescending<com.example.boxview.modelo.Posicion> {
                            it.codigo.endsWith("· ${producto?.categoria.orEmpty()}")
                        }.thenBy { it.codigo })
                        Text("Sección de referencia: ${producto?.categoria.orEmpty()}",
                            style = MaterialTheme.typography.bodySmall)
                        if (inventarioMensual) Text("Destino: Zona de recepción",
                            color = MaterialTheme.colorScheme.primary)
                        else SelectorSimple("Posición de ingreso", sugeridas.map { it.id to it.codigo },
                            ubicaciones[d.id]) { ubicaciones[d.id] = it; mensaje = "" }
                        CampoFormulario("Unidades recibidas", cantidades[d.id] ?: "0", {
                            cantidades[d.id] = it; mensaje = ""
                        }, keyboardType = KeyboardType.Number)
                        var leyendo by remember { mutableStateOf(false) }
                        var errorLectura by remember { mutableStateOf("") }
                        BotonAccion(if (leyendo) "Cerrar lector" else "Escanear una unidad", outlined = true,
                            icon = Icons.Default.QrCodeScanner, enabled = !guardando, onClick = { leyendo = !leyendo })
                        if (leyendo) ModalEscaner("Escanear producto recibido", cerrar = { leyendo = false }) { codigoQr ->
                            leyendo = false
                            if (codigoQr.trim().equals(codigo, ignoreCase = true)) {
                                val n = cantidades[d.id]?.toIntOrNull() ?: 0
                                if (n < 100000) cantidades[d.id] = (n + 1).toString()
                            } else errorLectura = "El código no corresponde a $codigo"
                        }
                        if (errorLectura.isNotBlank()) Text(errorLectura, color = MaterialTheme.colorScheme.error)
                    } else {
                        val stockTotal = existencias.orEmpty().filter { it.productoId == d.productoId }.sumOf { it.cantidad }
                        val ocupadoOtras = reservas.orEmpty().filter { it.productoId == d.productoId }.sumOf { it.cantidadReservada }
                        val disponibleBodega = (stockTotal - ocupadoOtras).coerceAtLeast(0)
                        if (existencias == null || reservas == null) {
                            Text("Consultando existencias y reservas…", style = MaterialTheme.typography.bodySmall)
                        } else {
                            Text("Stock total en bodega: $stockTotal unidades", fontWeight = FontWeight.SemiBold)
                        }
                        Text("Reservadas para otras órdenes: $ocupadoOtras · Libres para esta orden: $disponibleBodega",
                            style = MaterialTheme.typography.bodySmall)
                        if (disponibleBodega < d.cantidadEsperada) {
                            Text("No hay suficientes unidades libres para completar esta orden.",
                                color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }
                        Text("Retirar desde los estantes:", fontWeight = FontWeight.SemiBold)
                        val filas = filasSalida.filter { it.detalleId == d.id }
                        filas.forEach { fila ->
                            key(fila.id) {
                                val estantesConStock = posiciones.filter { pos ->
                                    pos.tipo != "SECCION" && (stockEn(d.productoId, pos.id) > 0 || pos.id == fila.posicionId)
                                }
                                SelectorSimple("Estante de origen", estantesConStock.map { pos ->
                                    pos.id to "${rutaBodega(pos, posiciones)} · ${stockEn(d.productoId, pos.id)} uds. (${libreEn(d.productoId, pos.id)} libres)"
                                }, fila.posicionId) { pos ->
                                    val i = filasSalida.indexOfFirst { it.id == fila.id }
                                    if (i >= 0) filasSalida[i] = filasSalida[i].copy(posicionId = pos)
                                    mensaje = ""
                                }
                                fila.posicionId?.let { pid ->
                                    val fisicas = stockEn(d.productoId, pid)
                                    val otras = reservadoOtros(d.productoId, pid)
                                    Text("En este estante: $fisicas · Reservadas por otras órdenes: $otras · Disponibles: ${libreEn(d.productoId, pid)}",
                                        style = MaterialTheme.typography.bodySmall)
                                }
                                CampoFormulario("Unidades de este estante", fila.cantidad, { valor ->
                                    val i = filasSalida.indexOfFirst { it.id == fila.id }
                                    if (i >= 0) filasSalida[i] = filasSalida[i].copy(cantidad = valor)
                                    mensaje = ""
                                }, keyboardType = KeyboardType.Number)
                                var leyendo by remember { mutableStateOf(false) }
                                var errorLectura by remember { mutableStateOf("") }
                                BotonAccion(if (leyendo) "Cerrar lector" else "Escanear una unidad", outlined = true,
                                    icon = Icons.Default.QrCodeScanner, enabled = !guardando && fila.posicionId != null,
                                    onClick = { leyendo = !leyendo })
                                if (leyendo) ModalEscaner("Escanear producto para despacho", cerrar = { leyendo = false }) { qr ->
                                    leyendo = false
                                    if (qr.trim().equals(codigo, ignoreCase = true)) {
                                        val n = fila.cantidad.toIntOrNull() ?: 0
                                        val total = filasSalida.filter { it.detalleId == d.id }
                                            .sumOf { it.cantidad.toIntOrNull() ?: 0 }
                                        if (total < d.cantidadEsperada && n < 100000 &&
                                            n < libreEn(d.productoId, fila.posicionId ?: -1L)) {
                                            val i = filasSalida.indexOfFirst { it.id == fila.id }
                                            if (i >= 0) filasSalida[i] = filasSalida[i].copy(cantidad = (n + 1).toString())
                                        } else errorLectura = "Cantidad de la orden completa o sin stock libre en este estante"
                                    } else errorLectura = "El código no corresponde a $codigo"
                                }
                                if (errorLectura.isNotBlank()) Text(errorLectura, color = MaterialTheme.colorScheme.error)
                                val pedidas = fila.cantidad.toIntOrNull() ?: 0
                                if (fila.posicionId != null && pedidas > libreEn(d.productoId, fila.posicionId)) {
                                    Text("Cantidad supera las unidades disponibles de este estante.",
                                        color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                                }
                                if (filas.size > 1) TextButton(enabled = !guardando, onClick = {
                                    filasSalida.removeAll { it.id == fila.id }
                                }) { Text("Quitar estante") }
                            }
                        }
                        Text("Preparadas: ${filas.sumOf { it.cantidad.toIntOrNull() ?: 0 }} / ${d.cantidadEsperada}")
                        TextButton(enabled = !guardando, onClick = {
                            filasSalida.add(FilaPreparacion(siguienteFila++, d.id, null, "0"))
                        }) { Text("Añadir otro estante") }
                    }
                }
            }
        }
        if (detalles.isNotEmpty()) {
            val validas = if (ingreso) detalles.all { d ->
                cantidades[d.id]?.toIntOrNull()?.let { it in 0..100000 } == true && (inventarioMensual && posicionRecepcion != null || ubicaciones[d.id] != null)
            } else preparacionCargada && existencias != null && reservas != null && filasSalida.all { fila ->
                val n = fila.cantidad.toIntOrNull()
                n != null && n in 0..100000 && (n == 0 || fila.posicionId != null)
            } && filasSalida.filter { (it.cantidad.toIntOrNull() ?: 0) > 0 }
                .all { fila ->
                    val productoId = detalles.firstOrNull { it.id == fila.detalleId }?.productoId
                    val pid = fila.posicionId
                    productoId != null && pid != null && (fila.cantidad.toIntOrNull() ?: 0) <= libreEn(productoId, pid)
                } && filasSalida.filter { (it.cantidad.toIntOrNull() ?: 0) > 0 }
                .map { it.detalleId to it.posicionId }.distinct().size ==
                filasSalida.count { (it.cantidad.toIntOrNull() ?: 0) > 0 } &&
                detalles.all { d -> filasSalida.filter { it.detalleId == d.id }
                    .sumOf { it.cantidad.toIntOrNull() ?: 0 } <= d.cantidadEsperada }
            val todoPreparado = detalles.all { d -> filasSalida.filter { it.detalleId == d.id }
                .sumOf { it.cantidad.toIntOrNull() ?: 0 } == d.cantidadEsperada }
            val suma = if (ingreso) detalles.sumOf { cantidades[it.id]?.toIntOrNull() ?: 0 }
                else filasSalida.sumOf { it.cantidad.toIntOrNull() ?: 0 }
            val esperado = detalles.sumOf { it.cantidadEsperada }
            TarjetaPanel {
                Text("Solicitadas: $esperado · Verificadas: $suma", fontWeight = FontWeight.Bold)
                if (ingreso && suma != esperado) EtiquetaEstado("Hay diferencias por registrar", warning = true)
                if (ingreso) Row {
                    Checkbox(checked = declaracion, onCheckedChange = { declaracion = it })
                    Text("He comprobado físicamente las cantidades", Modifier.padding(top = 12.dp))
                }
                if (mensaje.isNotBlank()) Text(mensaje, color = MaterialTheme.colorScheme.primary)
                BotonAccion("Guardar avance", outlined = true, enabled = !guardando && validas,
                    icon = Icons.Default.Save, onClick = {
                        guardando = true
                        if (ingreso) vm.guardarAvanceCaja(
                            detalles.associate { it.id to (cantidades[it.id]?.toIntOrNull() ?: 0) }, (if (ingreso && inventarioMensual && posicionRecepcion != null)
                                detalles.associate { it.id to posicionRecepcion.id } else ubicaciones.toMap())
                        ) { _, msg -> mensaje = msg; guardando = false }
                        else vm.guardarAvanceSalida(registrosSalida()) { _, msg -> mensaje = msg; guardando = false }
                    })
                BotonAccion(if (ingreso) "Confirmar recepción" else "Finalizar preparación",
                    enabled = !guardando && validas && (if (ingreso) declaracion else todoPreparado),
                    onClick = { confirmar = true })
            }
            if (confirmar) AlertDialog(onDismissRequest = { confirmar = false },
                title = { Text(if (ingreso) "Confirmar lo recibido" else "Enviar a revisión") },
                text = { Text(if (ingreso) "¿Confirmas las cantidades recibidas? Las diferencias se registrarán como incidencias."
                    else "¿Enviar esta preparación para revisión de jefatura?") },
                confirmButton = { TextButton(enabled = !guardando, onClick = {
                    confirmar = false
                    guardando = true
                    if (ingreso) vm.confirmarCaja(
                        detalles.associate { it.id to (cantidades[it.id]?.toIntOrNull() ?: 0) }, (if (ingreso && inventarioMensual && posicionRecepcion != null)
                                detalles.associate { it.id to posicionRecepcion.id } else ubicaciones.toMap())
                    ) { ok, msg ->
                        guardando = false; mensaje = msg
                        if (ok) terminado = true
                    } else vm.confirmarSalida(registrosSalida()) { ok, msg ->
                        guardando = false; mensaje = msg
                        if (ok) alDespachar()
                    }
                }) { Text("Confirmar") } },
                dismissButton = { TextButton(enabled = !guardando, onClick = { confirmar = false }) { Text("Cancelar") } })
        }
    }
}
