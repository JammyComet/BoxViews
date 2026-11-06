package com.example.boxview.ui.view

// Pantallas: Preparación y revisión de órdenes.
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.boxview.modelo.*
import com.example.boxview.ui.components.*
import com.example.boxview.viewmodel.JefeBodegaViewModel
import com.example.boxview.viewmodel.OperadorViewModel

// Muestra la pantalla de ordenes jefe.
@Composable
fun PantallaOrdenesJefe(base: OperadorViewModel, jefe: JefeBodegaViewModel) {
    val bodega = base.bodegaActiva ?: return
    val usuario = base.sesion ?: return
    // Datos observados por esta pantalla.
    val productos by base.productos.collectAsState(initial = emptyList())
    val tiendas by remember(bodega.id) { jefe.tiendasAbastecidas(bodega.id) }.collectAsState(initial = emptyList())
    val existencias by remember(bodega.id) { base.existencias() }.collectAsState(initial = emptyList())
    val reservas by remember(bodega.id) { base.reservasOtrasCajas() }.collectAsState(initial = emptyList())
    val compromisos by remember(bodega.id) { jefe.compromisos(bodega.id) }.collectAsState(initial = emptyList())
    val trabajadores by remember(bodega.id) { jefe.operadores(bodega.id) }.collectAsState(initial = emptyList())
    val ordenes by remember(bodega.id) { jefe.ordenes(bodega.id) }.collectAsState(initial = emptyList())
    val solicitudes by remember(bodega.id) { jefe.solicitudesDeReposicion(bodega.id) }.collectAsState(initial = emptyList())
    LaunchedEffect(bodega.id) { jefe.prepararOrden(bodega.id) }
    val formulario by jefe.formularioOrden.collectAsState()
    val destino = formulario.destino
    val stockTienda by remember(destino) { jefe.stockTienda(destino ?: -1) }.collectAsState(initial = emptyList())
    val operador = formulario.operador
    val posiciones by remember(bodega.id) { base.posiciones() }.collectAsState(initial = emptyList())
    val filas = formulario.filas
    var procesando by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }
    var motivoDevolucion by remember { mutableStateOf("") }
    var verNueva by remember { mutableStateOf(true) }
    var seleccionada by remember { mutableStateOf<Long?>(null) }
    var cancelarId by remember { mutableStateOf<Long?>(null) }
    var operadorSolicitud by remember(bodega.id) { mutableStateOf<Long?>(null) }
    val lineas = filas.mapNotNull { f ->
        val pid = f.productoId ?: return@mapNotNull null
        val cantidad = f.cantidad.toIntOrNull() ?: return@mapNotNull null
        if (cantidad > 0) JefeBodegaViewModel.LineaOrden(pid, cantidad) else null
    }
    ColumnaOperador {
        TituloSeccion("JEFATURA", "Órdenes de salida", "${bodega.nombre} · asignación directa al operador")
        BotonAccion(if (verNueva) "Ocultar formulario" else "Crear nueva orden", outlined = true,
            onClick = { verNueva = !verNueva })
        if (mensaje.isNotBlank()) TarjetaPanel { Text(mensaje) }
        if (verNueva) TarjetaPanel {
            Text("Nueva orden de salida", fontWeight = FontWeight.Bold)
            SelectorSimple("Tienda de destino", tiendas.map { it.id to it.nombre }, destino) {
                jefe.elegirDestinoOrden(it); mensaje = ""
            }
            SelectorSimple("Asignar al operador", trabajadores.map { it.id to it.nombre }, operador) {
                jefe.elegirOperadorOrden(it); mensaje = ""
            }
            Text("Productos solicitados", fontWeight = FontWeight.SemiBold)
            filas.toList().forEachIndexed { indice, fila ->
                key(fila.id) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Producto ${indice + 1}", fontWeight = FontWeight.SemiBold)
                            if (filas.size > 1) TextButton(enabled = !procesando, onClick = {
                                jefe.quitarProductoOrden(fila.id); mensaje = ""
                            }) { Text("Quitar") }
                        }
                        val otrosIds = filas.filter { it.id != fila.id }.mapNotNull { it.productoId }.toSet()
                        SelectorSimple("Elegir producto", productos.filter { it.id !in otrosIds }
                            .map { it.id to "${it.codigo} · ${it.nombre}" }, fila.productoId) { nuevo ->
                            jefe.elegirProductoOrden(fila.id, nuevo)
                            mensaje = ""
                        }
                        val pid = fila.productoId
                        if (pid != null) {
                            val fisico = existencias.filter { it.productoId == pid }.sumOf { it.cantidad }
                            val reservado = reservas.filter { it.productoId == pid }.sumOf { it.cantidadReservada }
                            val comprometido = compromisos.firstOrNull { it.productoId == pid }?.cantidadSolicitada ?: 0
                            val libre = (fisico - comprometido).coerceAtLeast(0)
                            Text("Stock físico en bodega: $fisico", style = MaterialTheme.typography.bodySmall)
                            if (comprometido > 0) {
                                Text("Pendiente de despacho en otras órdenes: $comprometido",
                                    style = MaterialTheme.typography.bodySmall)
                            }
                            Text("Disponible para una nueva orden: $libre",
                                color = if (libre > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.SemiBold)
                            if (destino != null) Text("Stock actual en tienda destino: ${stockTienda.firstOrNull { it.productoId == pid }?.cantidad ?: 0}",
                                style = MaterialTheme.typography.bodySmall)
                            val conStock = posiciones.mapNotNull { pos ->
                                val n = existencias.firstOrNull { it.productoId == pid && it.posicionId == pos.id }?.cantidad ?: 0
                                if (n > 0) {
                                    val prep = reservas.firstOrNull { it.productoId == pid && it.posicionId == pos.id }?.cantidadReservada ?: 0
                                    "${rutaBodega(pos, posiciones)}: $n físicas · $prep preparadas · ${(n - prep).coerceAtLeast(0)} libres físicamente"
                                } else null
                            }
                            Text("Dónde se encuentra:", fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodySmall)
                            if (conStock.isEmpty()) Text("Sin unidades en los estantes de esta bodega",
                                style = MaterialTheme.typography.bodySmall)
                            conStock.forEach { texto -> Text("• $texto", style = MaterialTheme.typography.bodySmall) }
                            CampoFormulario("Cantidad solicitada (máximo $libre)", fila.cantidad,
                                { valor ->
                                    jefe.cambiarCantidadOrden(fila.id, valor)
                                    mensaje = ""
                                }, keyboardType = KeyboardType.Number)
                        }
                    }
                    HorizontalDivider()
                }
            }
            BotonAccion("Añadir otro producto", outlined = true, icon = Icons.Default.Add,
                enabled = !procesando && filas.size < productos.size, onClick = {
                    jefe.anadirProductoOrden(); mensaje = ""
                })
            val validas = formulario.bodegaId == bodega.id && destino != null && operador != null && lineas.size == filas.size &&
                lineas.isNotEmpty() && lineas.distinctBy { it.productoId }.size == lineas.size &&
                lineas.all { l ->
                    val libre = existencias.filter { it.productoId == l.productoId }.sumOf { it.cantidad } -
                        (compromisos.firstOrNull { it.productoId == l.productoId }?.cantidadSolicitada ?: 0)
                    l.cantidad in 1..100000 && l.cantidad <= libre
                }
            BotonAccion("Asignar orden de salida", enabled = !procesando && validas, onClick = {
                procesando = true
                jefe.crearOrden(usuario, bodega, destino!!, operador!!, lineas) { ok, msg ->
                    procesando = false; mensaje = msg
                    if (ok) { jefe.reiniciarOrden(); verNueva = false }
                }
            })
        }
        TituloSeccion("SOLICITUDES DE TIENDAS", "Reposiciones pendientes")
        val pendientes = solicitudes.filter { it.estado == "PENDIENTE" }
        if (pendientes.isEmpty()) Text("No hay solicitudes pendientes para esta bodega")
        if (pendientes.isNotEmpty()) {
            SelectorSimple("Operador para preparar las solicitudes",
                trabajadores.map { it.id to it.nombre }, operadorSolicitud) { operadorSolicitud = it }
        }
        pendientes.groupBy { it.loteId ?: "solicitud-${it.id}" }.values.forEach { grupo ->
            val solicitud = grupo.first()
            val destino = tiendas.firstOrNull { it.id == solicitud.sucursalId }
            TarjetaPanel {
                Text("Pedido #${solicitud.id} · ${destino?.nombre ?: "Tienda #${solicitud.sucursalId}"}",
                    fontWeight = FontWeight.Bold)
                Text(formatoFecha(solicitud.fecha), style = MaterialTheme.typography.bodySmall)
                grupo.forEach { linea ->
                    val producto = productos.firstOrNull { it.id == linea.productoId }
                    Text("${producto?.nombre ?: "Producto #${linea.productoId}"} · ${linea.cantidad} unidades")
                }
                Text("${grupo.size} productos · ${grupo.sumOf { it.cantidad }} unidades",
                    style = MaterialTheme.typography.bodySmall)
                BotonAccion("Crear orden de despacho", enabled = !procesando && operadorSolicitud != null,
                    onClick = {
                        procesando = true
                        jefe.atenderSolicitud(usuario, bodega, solicitud.id, operadorSolicitud!!) { _, msg ->
                            mensaje = msg
                            procesando = false
                        }
                    })
            }
        }
        Text("Órdenes registradas", fontWeight = FontWeight.Bold)
        if (ordenes.isEmpty()) Text("Sin órdenes registradas en esta bodega")
        ordenes.forEach { orden ->
            TarjetaPanel {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(orden.codigo, fontWeight = FontWeight.Bold)
                    EtiquetaEstado(estadoDespachoParaBodega(orden.estado),
                        warning = orden.estado in listOf("ORDEN_ASIGNADA", "PENDIENTE_REVISION"),
                        success = llegadaATiendaRegistrada(orden.estado))
                }
                Text("Tienda destino: ${tiendas.firstOrNull { it.id == orden.sucursalDestinoId }?.nombre ?: "Histórico / no asignado"}")
                Text("Asignado a: ${trabajadores.firstOrNull { it.id == orden.asignadoA }?.nombre ?: "Operador ${orden.asignadoA}"}",
                    style = MaterialTheme.typography.bodySmall)
                Text("Creada: ${formatoFecha(orden.fecha)}", style = MaterialTheme.typography.bodySmall)
                BotonAccion(if (seleccionada == orden.id) "Ocultar productos" else "Ver productos", outlined = true,
                    onClick = { seleccionada = if (seleccionada == orden.id) null else orden.id })
                if (seleccionada == orden.id) {
                    DetallesOrdenJefe(jefe, orden.id, productos, posiciones)
                    if (orden.estado in listOf("EN_TRANSITO", "EN_REVISION", "RECIBIDA", "CON_DIFERENCIA")) {
                        val lineasEtiqueta by remember(orden.id) { jefe.detalles(orden.id) }.collectAsState(initial = emptyList())
                        EtiquetaCajaDescargable(orden, lineasEtiqueta, productos,
                            tiendas.firstOrNull { it.id == orden.sucursalDestinoId }?.nombre ?: "Tienda destino")
                    } else Text("Etiqueta disponible al autorizar el despacho.",
                        style = MaterialTheme.typography.bodySmall)
                }
                if (orden.estado == "PENDIENTE_REVISION") {
                    Text("Preparada por usuario #${orden.preparadoPor ?: 0}. Requiere revisión de jefatura.",
                        style = MaterialTheme.typography.bodySmall)
                    CampoFormulario("Motivo para devolver al operador", motivoDevolucion,
                        { motivoDevolucion = it.take(250) })
                    BotonAccion("Devolver preparación", outlined = true,
                        enabled = !procesando && motivoDevolucion.trim().length >= 5, onClick = {
                        procesando = true
                        jefe.devolverPreparacion(usuario, bodega.id, orden.id, motivoDevolucion) { ok, msg ->
                            mensaje = msg; procesando = false
                            if (ok) motivoDevolucion = ""
                        }
                    })
                    BotonAccion("Revisar y autorizar despacho", enabled = !procesando, onClick = {
                        procesando = true
                        jefe.revisarDespacho(usuario, bodega.id, orden.id) { _, msg ->
                            mensaje = msg
                            procesando = false
                        }
                    })
                }
                if (orden.estado == "ORDEN_ASIGNADA") BotonAccion("Cancelar orden sin preparación", outlined = true,
                    onClick = { cancelarId = orden.id })
            }
        }
    }
    if (cancelarId != null) AlertDialog(onDismissRequest = { cancelarId = null },
        title = { Text("¿Cancelar orden?") },
        text = { Text("Solo se cancelará si el operador todavía no ha guardado una preparación.") },
        confirmButton = { TextButton(enabled = !procesando, onClick = {
            val id = cancelarId ?: return@TextButton
            procesando = true
            jefe.cancelarOrden(usuario, bodega.id, id) { _, m ->
                mensaje = m; procesando = false; cancelarId = null
            }
        }) { Text("Cancelar orden") } },
        dismissButton = { TextButton(onClick = { cancelarId = null }) { Text("Volver") } })
}

// Componente visual de detalles orden jefe.
@Composable
private fun DetallesOrdenJefe(jefe: JefeBodegaViewModel, cajaId: Long, productos: List<Producto>, posiciones: List<Posicion>) {
    // Datos observados por esta pantalla.
    val detalles by remember(cajaId) { jefe.detalles(cajaId) }.collectAsState(initial = emptyList())
    val preparaciones by remember(cajaId) { jefe.preparaciones(cajaId) }.collectAsState(initial = emptyList())
    detalles.forEach { d ->
        val producto = productos.firstOrNull { it.id == d.productoId }
        Text("${producto?.codigo ?: d.productoId} · ${producto?.nombre ?: "Producto"}: ${d.cantidadEsperada} unidades",
            style = MaterialTheme.typography.bodySmall)
        preparaciones.filter { it.productoId == d.productoId }.forEach { p ->
            val ubicacion = posiciones.firstOrNull { it.id == p.posicionId }
            Text("Preparadas: ${p.cantidad} unidades · ${ubicacion?.let { rutaBodega(it, posiciones) } ?: "Estante #${p.posicionId}"}",
                style = MaterialTheme.typography.bodySmall)
        }
    }
}
