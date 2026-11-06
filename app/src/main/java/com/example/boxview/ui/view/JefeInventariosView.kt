package com.example.boxview.ui.view

// Pantallas: Gestión de inventarios del jefe de bodega.
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import com.example.boxview.modelo.*
import com.example.boxview.ui.components.*
import com.example.boxview.viewmodel.JefeBodegaViewModel
import com.example.boxview.viewmodel.OperadorViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Muestra la pantalla de inventarios jefe.
@Composable
fun PantallaInventariosJefe(base: OperadorViewModel, jefe: JefeBodegaViewModel) {
    val bodega = base.bodegaActiva ?: return
    val usuario = base.sesion ?: return
    // Datos observados por esta pantalla.
    val trabajadores by remember(bodega.id) { jefe.operadores(bodega.id) }.collectAsState(initial = emptyList())
    var nombre by remember(bodega.id) {
        mutableStateOf(SimpleDateFormat("MMMM yyyy", Locale("es", "CL")).format(Date()).replaceFirstChar { it.uppercase() })
    }
    val campanias by remember(bodega.id) { jefe.campaniasMensuales(bodega.id) }.collectAsState(initial = emptyList())
    val campaniaMes = campanias.firstOrNull { it.mes == mesCampaniaInventario(nombre) }
    val zonasCampania by remember(campaniaMes?.id) { jefe.zonasCampania(campaniaMes?.id ?: -1) }
        .collectAsState(initial = emptyList())
    val bloqueadas = zonasCampania.filter { it.estado != "PENDIENTE" }.map { it.posicionId }.toSet()
    val inventarios by remember(bodega.id) { jefe.inventariosGenerales(bodega.id) }.collectAsState(initial = emptyList())
    val anteriores by remember(bodega.id) { jefe.periodos(bodega.id) }.collectAsState(initial = emptyList())
    val productos by base.productos.collectAsState(initial = emptyList())
    val posiciones by remember(bodega.id) { base.posiciones() }.collectAsState(initial = emptyList())
    val existencias by remember(bodega.id) { base.existencias() }.collectAsState(initial = emptyList())
    val posicionesConStock = existencias.filter { it.cantidad > 0 }.map { it.posicionId }.toSet()
    val posicionesConteo = posiciones.filter {
        it.tipo != "SECCION" && it.tipo != "RECEPCION" && (it.activa || it.id in posicionesConStock ||
            zonasCampania.any { zona -> zona.posicionId == it.id && zona.estado == "PENDIENTE" })
    }
    var operadorDefecto by remember(bodega.id) { mutableStateOf<Long?>(0L) }
    val zonasSeleccionadas = remember(bodega.id) { mutableStateMapOf<Long, Boolean>() }
    val posicionesIncluidas = posicionesConteo.filter { it.id !in bloqueadas && zonasSeleccionadas[it.id] == true }
    var cancelacionId by remember { mutableStateOf<Long?>(null) }
    var motivoCancelacion by remember { mutableStateOf("") }
    val reparto = remember(bodega.id) { mutableStateMapOf<Long, Long>() }
    var mensaje by remember { mutableStateOf("") }
    var trabajando by remember { mutableStateOf(false) }
    var detalleId by remember { mutableStateOf<Long?>(null) }
    var cierreId by remember { mutableStateOf<Long?>(null) }
    var aplicarAjustes by remember { mutableStateOf(false) }
    var motivoSinAjustes by remember { mutableStateOf("") }
    ColumnaOperador {
        TituloSeccion("JEFATURA", "Inventario mensual", bodega.nombre)
        if (campaniaMes != null) TarjetaPanel {
            Text("Campaña mensual ${campaniaMes.mes}", fontWeight = FontWeight.Bold)
            Text("Estantes completados: ${zonasCampania.count { it.estado == "CONTADA" }} / ${zonasCampania.size}")
            Text(if (campaniaMes.estado == "FINALIZADA") "Informe mensual consolidado enviado a Gerencia"
                else "Abre conteos parciales para los estantes pendientes. Los ya contados no se repiten.")
        }
        TarjetaPanel {
            Text("Inventario por estanterías", fontWeight = FontWeight.Bold)
            CampoFormulario("Período o conteo parcial", nombre, { nombre = it; mensaje = "" })
            val opciones = listOf(0L to "Sin asignar por ahora") + trabajadores.map { it.id to it.nombre }
            SelectorSimple("Asignación inicial (opcional)", opciones, operadorDefecto) { id ->
                operadorDefecto = id
                posicionesConteo.forEach { reparto[it.id] = id }
            }
            Text("Estantes incluidos (${posicionesIncluidas.size} de ${posicionesConteo.size})", fontWeight = FontWeight.SemiBold)
            Row {
                TextButton(onClick = { posicionesConteo.forEach { zonasSeleccionadas[it.id] = true } }) { Text("Seleccionar todos") }
                TextButton(onClick = { posicionesConteo.forEach { zonasSeleccionadas[it.id] = false } }) { Text("Ninguno") }
            }
            posicionesConteo.forEach { pos ->
                val bloqueada = pos.id in bloqueadas
                val incluido = !bloqueada && zonasSeleccionadas[pos.id] == true
                Row {
                    Checkbox(checked = incluido, enabled = !bloqueada, onCheckedChange = { zonasSeleccionadas[pos.id] = it })
                    Text(rutaBodega(pos, posiciones), modifier = androidx.compose.ui.Modifier.weight(1f))
                    TextButton(enabled = !bloqueada, onClick = {
                        posicionesConteo.forEach { zonasSeleccionadas[it.id] = it.id == pos.id }
                    }) { Text("Solo este") }
                }
                if (bloqueada) Text("Estante ya contado o reservado", style = MaterialTheme.typography.bodySmall)
                if (incluido) SelectorSimple("Responsable de este estante", opciones,
                    reparto[pos.id] ?: operadorDefecto ?: 0L) { reparto[pos.id] = it }
            }
            BotonAccion("Abrir inventario de zonas seleccionadas", enabled = !trabajando &&
                nombre.trim().length >= 3 && posicionesIncluidas.isNotEmpty(),
                onClick = {
                    trabajando = true
                    val asignacion = posicionesIncluidas.associate { it.id to (reparto[it.id] ?: operadorDefecto ?: 0L) }
                    jefe.abrirInventario(usuario, bodega.id, nombre, asignacion) { ok, msg ->
                        trabajando = false; mensaje = msg
                        if (ok) { reparto.clear(); zonasSeleccionadas.clear() }
                    }
                })
            if (mensaje.isNotBlank()) Text(mensaje)
        }
        Text("Inventarios generales", fontWeight = FontWeight.Bold)
        if (inventarios.isEmpty()) TarjetaPanel { Text("Aún no se ha abierto un inventario general") }
        inventarios.forEach { inv ->
            TarjetaPanel {
                Text(inv.nombre, fontWeight = FontWeight.Bold)
                EtiquetaEstado(inv.estado, success = inv.estado in listOf("CERRADO", "CONCILIADO"))
                Text("Abierto: ${formatoFecha(inv.fechaApertura)}", style = MaterialTheme.typography.bodySmall)
                BotonAccion(if (detalleId == inv.id) "Ocultar zonas y conteos" else "Ver zonas y conteos",
                    outlined = true, onClick = { detalleId = if (detalleId == inv.id) null else inv.id })
                if (detalleId == inv.id) DetalleInventarioGeneralJefe(jefe, inv.id, productos, posiciones, existencias, trabajadores,
                    inv.estado == "PENDIENTE_REVISION", usuario, bodega.id)
                if (inv.estado == "PENDIENTE_REVISION") {
                    BotonAccion("Aprobar diferencias y actualizar stock", enabled = !trabajando,
                        onClick = { cierreId = inv.id; aplicarAjustes = true })
                    BotonAccion("Cerrar sin aplicar ajustes", outlined = true, enabled = !trabajando,
                        onClick = { cierreId = inv.id; aplicarAjustes = false; motivoSinAjustes = "" })
                } else if (inv.estado == "ABIERTO") Text("En conteo",
                    style = MaterialTheme.typography.bodySmall)
                if (inv.estado in listOf("ABIERTO", "PENDIENTE_REVISION"))
                    BotonAccion("Cancelar inventario", outlined = true, enabled = !trabajando,
                        onClick = { cancelacionId = inv.id; motivoCancelacion = "" })
            }
        }
        val legados = anteriores.filter { it.inventarioBodegaId == null }
        if (legados.isNotEmpty()) {
            Text("Registros de versiones anteriores", fontWeight = FontWeight.SemiBold)
            legados.forEach { p -> Text("${p.nombre} · ${p.estado}", style = MaterialTheme.typography.bodySmall) }
        }
    }
    if (cancelacionId != null) AlertDialog(onDismissRequest = { if (!trabajando) cancelacionId = null },
        title = { Text("Cancelar inventario mensual") },
        text = {
            Column {
                Text("Cancelar sin aplicar ajustes")
                CampoFormulario("Motivo de cancelación", motivoCancelacion, { motivoCancelacion = it.take(250) })
            }
        },
        confirmButton = { TextButton(enabled = !trabajando && motivoCancelacion.trim().length >= 5, onClick = {
            val id = cancelacionId ?: return@TextButton
            trabajando = true
            jefe.cancelarInventarioGeneral(usuario, bodega.id, id, motivoCancelacion) { _, msg ->
                trabajando = false; mensaje = msg; cancelacionId = null
                if (detalleId == id) detalleId = null
            }
        }) { Text("Confirmar cancelación") } },
        dismissButton = { TextButton(enabled = !trabajando, onClick = { cancelacionId = null }) { Text("Volver") } })
    if (cierreId != null) AlertDialog(onDismissRequest = { if (!trabajando) cierreId = null },
        title = { Text(if (aplicarAjustes) "Aprobar inventario de toda la bodega" else "Cerrar sin ajustes") },
        text = { Column {
            Text(if (aplicarAjustes)
                "Se comprobarán los conteos y se ajustará solo lo registrado. El bloqueo de estantes continúa mientras el mes esté abierto."
                else "Se cerrará sin cambiar existencias. Las diferencias quedarán registradas sin ajustes; los estantes siguen bloqueados hasta finalizar el mes.")
            if (!aplicarAjustes) CampoFormulario("Motivo obligatorio", motivoSinAjustes,
                { motivoSinAjustes = it.take(250) })
        } },
        confirmButton = { TextButton(enabled = !trabajando && (aplicarAjustes || motivoSinAjustes.trim().length >= 5), onClick = {
            val id = cierreId ?: return@TextButton
            trabajando = true
            val terminar: (Boolean, String) -> Unit = { _, msg -> mensaje = msg; trabajando = false; cierreId = null }
            if (aplicarAjustes) jefe.aprobarInventarioGeneral(usuario, bodega.id, id, terminar)
            else jefe.cerrarInventarioGeneral(usuario, bodega.id, id, motivoSinAjustes, terminar)
        }) { Text(if (aplicarAjustes) "Aplicar ajustes" else "Cerrar") } },
        dismissButton = { TextButton(enabled = !trabajando, onClick = { cierreId = null }) { Text("Volver") } })
}

// Componente visual de detalle inventario general jefe.
@Composable
private fun DetalleInventarioGeneralJefe(jefe: JefeBodegaViewModel, inventarioId: Long,
                                        productos: List<Producto>, posiciones: List<Posicion>,
                                        existencias: List<Existencia>, trabajadores: List<Usuario>,
                                        puedeRecontar: Boolean, jefeUsuario: Usuario, bodegaId: Long) {
    var mensajeReconteo by remember(inventarioId) { mutableStateOf("") }
    var procesandoReconteo by remember(inventarioId) { mutableStateOf(false) }
    var asignacionPendiente by remember(inventarioId) { mutableStateOf<Map<Long,Long>>(emptyMap()) }
    // Datos observados por esta pantalla.
    val zonas by remember(inventarioId) { jefe.zonasGeneral(inventarioId) }.collectAsState(initial = emptyList())
    val tareas by remember(inventarioId) { jefe.tareasGeneral(inventarioId) }.collectAsState(initial = emptyList())
    zonas.forEach { zona ->
        val pos = posiciones.firstOrNull { it.id == zona.posicionId }?.let { rutaBodega(it, posiciones) } ?: "Estante ${zona.posicionId}"
        if (zona.operadorId == 0L && !puedeRecontar) {
            Text("$pos · Sin asignar", style = MaterialTheme.typography.bodySmall)
            val elegido = asignacionPendiente[zona.posicionId] ?: 0L
            SelectorSimple("Asignar $pos", listOf(0L to "Sin asignar") + trabajadores.map { it.id to it.nombre },
                elegido) { asignacionPendiente = asignacionPendiente + (zona.posicionId to it) }
            BotonAccion("Asignar este estante", outlined = true,
                enabled = elegido > 0L && !procesandoReconteo, onClick = {
                    procesandoReconteo = true
                    jefe.asignarZonaInventario(jefeUsuario, bodegaId, inventarioId, zona.posicionId, elegido) { _, msg ->
                        procesandoReconteo = false; mensajeReconteo = msg
                    }
                })
        } else {
            val trabajador = trabajadores.firstOrNull { it.id == zona.operadorId }?.nombre ?: "Operador ${zona.operadorId}"
            Text("$pos · $trabajador", style = MaterialTheme.typography.bodySmall)
        }
    }
    HorizontalDivider()
    if (mensajeReconteo.isNotBlank()) Text(mensajeReconteo)
    tareas.forEach { tarea ->
        Text("${trabajadores.firstOrNull { it.id == tarea.asignadoA }?.nombre ?: tarea.asignadoA} · ${tarea.estado}",
            fontWeight = FontWeight.SemiBold)
        if (tarea.verificacionFisicaPor != null) {
            Text("Verificación física: usuario #${tarea.verificacionFisicaPor} · " +
                (tarea.fechaVerificacionFisica?.let(::formatoFecha) ?: "fecha no disponible"),
                style = MaterialTheme.typography.bodySmall)
        }
        if (tarea.estado != "ABIERTO") DetalleInventarioJefe(jefe, tarea.id, productos, posiciones, existencias)
        if (puedeRecontar && tarea.estado == "ENTREGADO") BotonAccion("Solicitar recuento a este operador",
            outlined = true, enabled = !procesandoReconteo, onClick = {
                procesandoReconteo = true
                jefe.solicitarReconteo(jefeUsuario, bodegaId, inventarioId, tarea.id) { _, msg ->
                    mensajeReconteo = msg; procesandoReconteo = false
                }
            })
    }
}

// Componente visual de detalle inventario jefe.
@Composable
private fun DetalleInventarioJefe(jefe: JefeBodegaViewModel, periodoId: Long,
                                 productos: List<Producto>, posiciones: List<Posicion>, existencias: List<Existencia>) {
    // Datos observados por esta pantalla.
    val filas by remember(periodoId) { jefe.lineas(periodoId) }.collectAsState(initial = emptyList())
    val ajustes by remember(periodoId) { jefe.ajustes(periodoId) }.collectAsState(initial = emptyList())
    if (filas.isEmpty()) Text("No se registraron líneas en este inventario")
    filas.forEach { l ->
        val nombre = productos.firstOrNull { it.id == l.productoId }?.nombre ?: "Producto ${l.productoId}"
        val pos = posiciones.firstOrNull { it.id == l.posicionId }?.let { rutaBodega(it, posiciones) } ?: "Estante ${l.posicionId}"
        val actual = existencias.firstOrNull { it.productoId == l.productoId && it.posicionId == l.posicionId }?.cantidad ?: 0
        Text("$nombre · $pos", fontWeight = FontWeight.SemiBold)
        val referencia = l.stockAlEntregar
        if (referencia == null) {
            Text("Contado: ${l.cantidadContada} · Actual: $actual · Conteo anterior sin saldo de referencia",
                style = MaterialTheme.typography.bodySmall)
        } else {
            val diferencia = l.cantidadContada - referencia
            Text("Al abrir: ${referencia} · Contado: ${l.cantidadContada} · Diferencia: ${if (diferencia > 0) "+" else ""}$diferencia",
                style = MaterialTheme.typography.bodySmall)
            val aplicado = ajustes.any { it.productoId == l.productoId && it.posicionId == l.posicionId }
            if (actual != referencia && !aplicado) Text("Saldo actual: $actual (cambió después del conteo)",
                color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            if (aplicado) Text("Ajuste registrado · Saldo actual: $actual", style = MaterialTheme.typography.bodySmall)
        }
        HorizontalDivider()
    }
    if (ajustes.isNotEmpty()) {
        Text("Ajustes aplicados: ${ajustes.size}", fontWeight = FontWeight.SemiBold)
        ajustes.forEach { a ->
            val nombre = productos.firstOrNull { it.id == a.productoId }?.codigo ?: a.productoId.toString()
            val pos = posiciones.firstOrNull { it.id == a.posicionId }?.let { rutaBodega(it, posiciones) } ?: a.posicionId.toString()
            Text("$nombre · $pos: ${a.stockAnterior} → ${a.stockNuevo}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
