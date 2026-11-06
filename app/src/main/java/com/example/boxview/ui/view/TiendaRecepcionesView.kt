package com.example.boxview.ui.view

// Pantallas: Recepción y comprobación de cajas en tienda.
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.KeyboardType
import com.example.boxview.modelo.*
import com.example.boxview.ui.components.*
import com.example.boxview.viewmodel.OperadorViewModel
import com.example.boxview.viewmodel.TrabajadorTiendaViewModel

// Muestra la pantalla de recepciones tienda.
@Composable
fun PantallaRecepcionesTienda(vm: OperadorViewModel, tiendaVm: TrabajadorTiendaViewModel,
                            ir: (String) -> Unit) {
    val sid = tiendaVm.tiendaActiva?.id
    // Datos observados por esta pantalla.
    val pendientes by remember(sid) { tiendaVm.pendientes() }.collectAsState(initial = emptyList())
    val bodegas by vm.bodegas.collectAsState(initial = emptyList())
    var codigo by remember(sid) { mutableStateOf("") }
    var leyendo by remember { mutableStateOf(false) }
    var ocupado by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    fun abrir(valor: String) {
        if (ocupado) return
        ocupado = true
        // Busca la caja y comprueba que se pueda recibir.
        tiendaVm.abrirCaja(vm.sesion, valor) { ok, mensaje ->
            ocupado = false
            if (ok) { leyendo = false; ir(Destinos.TiendaDetalle.ruta) }
            else error = mensaje
        }
    }
    if (leyendo) ModalEscaner("Escanear caja al llegar a tienda", cerrar = { leyendo = false }) { leido ->
        leyendo = false
        codigo = leido
        abrir(leido)
    }
    ColumnaOperador {
        TituloSeccion("RECEPCIÓN", "Cajas pendientes", "${tiendaVm.tiendaActiva?.nombre.orEmpty()} · Verifica cada entrega")
        TarjetaPanel {
            CampoFormulario("Código QR de la caja", codigo, { codigo = it; error = "" })
            Text("Al registrar el QR, la caja queda como recibida en tienda y pendiente de comprobación física. Todavía no entra al stock.",
                style = MaterialTheme.typography.bodySmall)
            BotonAccion("Registrar llegada y comprobar", enabled = codigo.isNotBlank() && !ocupado, onClick = { abrir(codigo) })
            BotonAccion("Escanear QR de caja", outlined = true,
                onClick = { leyendo = true }, icon = Icons.Default.QrCodeScanner)
            if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
        }
        TituloSeccion("ENTREGAS", "Por recibir (${pendientes.size})")
        if (pendientes.isEmpty()) Text("No hay cajas en tránsito hacia esta tienda.")
        pendientes.forEach { caja ->
            val llegadaRegistrada = caja.estado == "EN_REVISION"
            FilaOpcion(caja.codigo,
                "${bodegas.firstOrNull { it.id == caja.bodegaOrigenId }?.nombre ?: "Bodega de origen"} · " +
                    if (llegadaRegistrada) "Recibida en tienda · por comprobar" else "En tránsito · escanea al llegar",
                icon = Icons.Default.LocalShipping,
                badge = if (llegadaRegistrada) "Reanudar" else "Pendiente",
                onClick = if (llegadaRegistrada) ({ abrir(caja.codigo) }) else null)
        }
    }
}

// Muestra la pantalla de detalle recepcion tienda.
@Composable
fun PantallaDetalleRecepcionTienda(vm: OperadorViewModel, tiendaVm: TrabajadorTiendaViewModel,
                                  alTerminar: () -> Unit) {
    val id = tiendaVm.cajaActivaId
    // Datos observados por esta pantalla.
    val caja by remember(id) { tiendaVm.observarCaja() }.collectAsState(initial = null)
    val detalles by remember(id) { tiendaVm.detallesCaja() }.collectAsState(initial = emptyList())
    val productos by vm.productos.collectAsState(initial = emptyList())
    val bodegas by vm.bodegas.collectAsState(initial = emptyList())
    val ubicacionesDisponibles by remember(tiendaVm.tiendaActiva?.id) { tiendaVm.posiciones() }.collectAsState(initial = emptyList())
    val todasUbicaciones by remember(tiendaVm.tiendaActiva?.id) { tiendaVm.todasLasPosiciones() }.collectAsState(initial = emptyList())
    val campanias by remember(tiendaVm.tiendaActiva?.id) { tiendaVm.campaniasMensualesTienda() }.collectAsState(initial = emptyList())
    val enInventarioMensual = campanias.any { it.estado == "ABIERTA" }
    LaunchedEffect(id) { tiendaVm.prepararRecepcion(id) }
    val formulario by tiendaVm.formularioRecepcion.collectAsState()
    val cantidades = formulario.cantidades
    var escaneando by remember(id) { mutableStateOf<Long?>(null) }
    val confirmado = formulario.comprobado
    var mostrarDialogo by remember(id) { mutableStateOf(false) }
    var ocupado by remember(id) { mutableStateOf(false) }
    var mensaje by remember(id) { mutableStateOf("") }
    val observacion = formulario.observacion
    val actual = caja
    if (id == null || actual == null || actual.estado != "EN_REVISION") {
        ColumnaOperador {
            TituloSeccion("RECEPCIÓN", "Caja no disponible", "Vuelve a la lista de cajas pendientes")
            BotonAccion("Volver", onClick = alTerminar)
        }
        return
    }
    ColumnaOperador {
        TituloSeccion("COMPROBACIÓN FÍSICA", "Caja ${actual.codigo}",
            "${bodegas.firstOrNull { it.id == actual.bodegaOrigenId }?.nombre ?: "Bodega de origen"} → ${tiendaVm.tiendaActiva?.nombre.orEmpty()}")
        TarjetaPanel {
            EtiquetaEstado("Recibida en tienda · pendiente de comprobar", warning = true)
            Text("Comprueba producto por producto", style = MaterialTheme.typography.titleMedium)
            Text("Indica cero en los productos no recibidos.",
                style = MaterialTheme.typography.bodySmall)
            CodigoQrCaja(actual.codigo)
        }
        detalles.forEach { d ->
            val p = productos.firstOrNull { it.id == d.productoId }
            TarjetaPanel {
                Text(p?.nombre ?: "Producto #${d.productoId}", style = MaterialTheme.typography.titleMedium)
                Text("${p?.codigo ?: "Sin código"} · Esperadas: ${d.cantidadEsperada}",
                    color = MaterialTheme.colorScheme.secondary)
                CampoFormulario("Unidades recibidas físicamente", cantidades[d.id].orEmpty(),
                    { valor -> tiendaVm.cambiarCantidadRecepcion(d.id, valor, d.cantidadEsperada) },
                    keyboardType = KeyboardType.Number)
                BotonAccion("Escanear una unidad", outlined = true, icon = Icons.Default.QrCodeScanner,
                    onClick = { escaneando = if (escaneando == d.id) null else d.id })
                if (escaneando == d.id) ModalEscaner("Escanear ${p?.nombre ?: "producto"}",
                    cerrar = { escaneando = null }) { leido ->
                    escaneando = null
                    if (leido.trim().equals(p?.codigo, ignoreCase = true)) {
                        tiendaVm.sumarUnidadRecepcion(d.id, d.cantidadEsperada)
                    } else mensaje = "El QR leído no corresponde a ${p?.nombre ?: "este producto"}"
                }
                HorizontalDivider()
                val contadas = cantidades[d.id]?.toIntOrNull() ?: 0
                val aceptadas = minOf(contadas, d.cantidadEsperada)
                val filasProducto = formulario.ubicaciones.filter { it.detalleId == d.id }
                val yaUbicadas = filasProducto.sumOf { it.cantidad.toIntOrNull() ?: 0 }
                Text("Destino físico de las unidades", style = MaterialTheme.typography.titleSmall)
                if (enInventarioMensual) Text("Destino: Zona de recepción",
                    color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
                Text("Aceptadas: $aceptadas · Ubicadas: $yaUbicadas. Distribución entre estanterías o zona de recepción.",
                    style = MaterialTheme.typography.bodySmall)
                if (!enInventarioMensual) filasProducto.forEach { linea ->
                    val posibles = ubicacionesDisponibles.filter { lugar ->
                        lugar.categoria == "General" || lugar.categoria == p?.categoria
                    }
                    val nombres = posibles.map { lugar ->
                        lugar.id to (if (lugar.zona == "RECEPCION") "Zona de recepción" else
                            "${rutaTienda(lugar, todasUbicaciones)} · ${if (lugar.zona == "VENTAS") "Sala de ventas" else "Almacén"} · ${lugar.categoria}")
                    }
                    key(linea.clave) {
                        SelectorSimple("Estante de destino", nombres, linea.posicionId) {
                            tiendaVm.cambiarPosicionRecepcion(linea.clave, it, d.cantidadEsperada)
                        }
                        CampoFormulario("Unidades en esta ubicación", linea.cantidad,
                            { tiendaVm.cambiarUnidadesUbicacion(linea.clave, it) },
                            keyboardType = KeyboardType.Number)
                        BotonAccion("Quitar esta ubicación", outlined = true,
                            onClick = { tiendaVm.quitarUbicacionRecepcion(linea.clave) })
                    }
                }
                if (!enInventarioMensual) BotonAccion("Añadir ubicación", outlined = true, icon = Icons.Default.Add,
                    onClick = { tiendaVm.agregarUbicacionRecepcion(d.id) })
                val zonaRecepcion = ubicacionesDisponibles.firstOrNull { it.zona == "RECEPCION" }
                if (!enInventarioMensual && zonaRecepcion != null && aceptadas > 0) {
                    BotonAccion("Dejar todo en Zona de recepción", outlined = true,
                        onClick = { tiendaVm.ubicarTodoEnRecepcion(d.id, zonaRecepcion.id, aceptadas) })
                }
                if (!enInventarioMensual && aceptadas != yaUbicadas) {
                    Text("Faltan ${aceptadas - yaUbicadas} unidades por ubicar (o hay unidades de más).",
                        color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        CampoFormulario("Observaciones de recepción (opcional)", observacion,
            { tiendaVm.cambiarObservacionRecepcion(it) }, singleLine = false)
        if (mensaje.isNotBlank()) Text(mensaje, color = MaterialTheme.colorScheme.error)
        val completos = detalles.isNotEmpty() && detalles.all { d ->
            val n = cantidades[d.id]?.toIntOrNull()
            n != null && n in 0..100000
        }
        val distribucionCompleta = completos && detalles.all { d ->
            val aceptadas = minOf(cantidades[d.id]?.toIntOrNull() ?: 0, d.cantidadEsperada)
            val filas = formulario.ubicaciones.filter { it.detalleId == d.id }
            val posIds = filas.mapNotNull { it.posicionId }
            val suma = filas.sumOf { it.cantidad.toIntOrNull() ?: 0 }
            val producto = productos.firstOrNull { it.id == d.productoId }
            suma == aceptadas && posIds.size == filas.size && posIds.distinct().size == posIds.size &&
                filas.all { it.cantidad.toIntOrNull()?.let { n -> n > 0 } == true &&
                    ubicacionesDisponibles.any { lugar -> lugar.id == it.posicionId &&
                        (lugar.categoria == "General" || lugar.categoria == producto?.categoria) } }
        }
        if (completos) {
            val diferencias = detalles.count { d -> cantidades[d.id]!!.toInt() != d.cantidadEsperada }
            TarjetaPanel {
                Text(if (diferencias == 0) "Todo coincide con la orden" else "$diferencias producto(s) con diferencia",
                    style = MaterialTheme.typography.titleMedium)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = confirmado, onCheckedChange = { tiendaVm.marcarRecepcionComprobada(it) })
            Text("He comprobado físicamente los productos y cantidades indicados.",
                style = MaterialTheme.typography.bodySmall)
        }
        BotonAccion("Confirmar recepción de caja", enabled = completos && (enInventarioMensual || distribucionCompleta) && confirmado && !ocupado,
            onClick = { mostrarDialogo = true })
        BotonAccion("Volver sin recibir", outlined = true, onClick = alTerminar)
    }
    if (mostrarDialogo) AlertDialog(
        onDismissRequest = { if (!ocupado) mostrarDialogo = false },
        title = { Text("Confirmar recepción") },
        text = { Text("¿Confirmar esta recepción? No podrás confirmarla dos veces.") },
        confirmButton = {
            TextButton(enabled = !ocupado, onClick = {
                ocupado = true
                val ubicaciones = formulario.ubicaciones.mapNotNull { linea ->
                    val posicionId = linea.posicionId
                    val cantidad = linea.cantidad.toIntOrNull()
                    if (posicionId == null || cantidad == null) null
                    else DistribucionRecepcion(linea.detalleId, posicionId, cantidad)
                }
                tiendaVm.confirmarRecepcion(vm.sesion,
                    detalles.associate { it.id to cantidades.getValue(it.id).toInt() },
                    ubicaciones, observacion) { ok, msg ->
                    ocupado = false
                    mostrarDialogo = false
                    if (ok) alTerminar() else mensaje = msg
                }
            }) { Text(if (ocupado) "Guardando..." else "Confirmar") }
        },
        dismissButton = { TextButton(enabled = !ocupado, onClick = { mostrarDialogo = false }) { Text("Cancelar") } }
    )
}
