package com.example.boxview.ui.view

// Pantallas: Conteos e inventarios de tienda.
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType
import com.example.boxview.modelo.*
import com.example.boxview.ui.components.*
import com.example.boxview.viewmodel.OperadorViewModel
import com.example.boxview.viewmodel.TrabajadorTiendaViewModel

// Muestra la pantalla de inventarios tienda.
@Composable
fun PantallaInventariosTienda(base: OperadorViewModel, tienda: TrabajadorTiendaViewModel) {
    val sid = tienda.tiendaActiva?.id ?: return
    val jefe = base.sesion?.rol == "JEFE_TIENDA"
    // Datos observados por esta pantalla.
    val inventarios by remember(sid) { tienda.inventariosTienda() }.collectAsState(initial = emptyList())
    val activo = inventarios.firstOrNull { it.estado == "ABIERTO" }
    var nombre by remember(sid) { mutableStateOf("") }
    val campanias by remember(sid) { tienda.campaniasMensualesTienda() }.collectAsState(initial = emptyList())
    val campaniaMes = campanias.firstOrNull { it.mes == mesCampaniaInventario(nombre) }
    val zonasMes by remember(campaniaMes?.id) { tienda.zonasCampania(campaniaMes?.id ?: -1) }
        .collectAsState(initial = emptyList())
    val bloqueadasMes = zonasMes.filter { it.estado != "PENDIENTE" }.map { it.posicionId }.toSet()
    val productos by base.productos.collectAsState(initial = emptyList())
    val posiciones by remember(sid) { tienda.todasLasPosiciones() }.collectAsState(initial = emptyList())
    val trabajadores by remember(sid) { tienda.trabajadoresParaInventario() }.collectAsState(initial = emptyList())
    val seleccionadas = remember(sid) { mutableStateMapOf<Long,Boolean>() }
    val reparto = remember(sid) { mutableStateMapOf<Long,Long>() }
    val saldosTienda by remember(sid) { tienda.stockPorUbicacion() }.collectAsState(initial=emptyList())
    val posicionesConSaldo = saldosTienda.filter { it.cantidad>0 }.map { it.posicionId }.toSet()
    val posicionesContables = posiciones.filter { it.tipo != "SECCION" && it.tipo != "RECEPCION" && (it.activa || it.id in posicionesConSaldo ||
        zonasMes.any { zona -> zona.posicionId == it.id && zona.estado == "PENDIENTE" }) }
    val incluidas = posicionesContables.filter { it.id !in bloqueadasMes && seleccionadas[it.id] == true }
    var motivoReconteo by remember(sid) { mutableStateOf("") }
    var confirmarVacioPosicion by remember(sid, activo?.id) { mutableStateOf<Long?>(null) }
    var responsableZona by remember(sid) { mutableStateOf<Long?>(0L) }
    var observacion by remember(sid) { mutableStateOf("") }
    var mensaje by remember(sid) { mutableStateOf("") }
    var ocupado by remember(sid) { mutableStateOf(false) }
    var historialId by remember(sid) { mutableStateOf<Long?>(null) }
    var hallazgoProducto by remember(sid) { mutableStateOf<Long?>(null) }
    var hallazgoPosicion by remember(sid) { mutableStateOf<Long?>(null) }
    var hallazgoCantidad by remember(sid) { mutableStateOf("") }
    val id = historialId ?: activo?.id
    val mostrandoActivo = activo != null && id == activo.id
    val lineasActivas by remember(activo?.id) { tienda.lineasConteoTienda(activo?.id ?: -1L) }
        .collectAsState(initial = emptyList())
    val eventos by remember(id) { tienda.eventosConteoTienda(id ?: -1L) }
        .collectAsState(initial = emptyList())
    val lineas by remember(id) { tienda.lineasConteoTienda(id ?: -1L) }.collectAsState(initial = emptyList())
    val zonas by remember(activo?.id) { tienda.zonasConteoTienda(activo?.id ?: -1L) }.collectAsState(initial = emptyList())
    val estantesVacios by remember(activo?.id) { tienda.estantesVaciosTienda(activo?.id ?: -1L) }
        .collectAsState(initial = emptyList())
    val idsVacios = estantesVacios.map { it.posicionId }.toSet()
    val misZonas = zonas.filter { it.asignadoA == base.sesion?.id && it.confirmadoPor == null }
    val posicionesPermitidas = misZonas.map { it.posicionId }.toSet()
    val lineasVisibles = if (!jefe && mostrandoActivo) lineas.filter { it.posicionId in posicionesPermitidas } else lineas
    val cantidades = remember(id) { mutableStateMapOf<Long, String>() }
    ColumnaOperador {
        TituloSeccion("INVENTARIO", "Conteo de tienda", tienda.tiendaActiva?.nombre)
        if (jefe && campaniaMes != null) TarjetaPanel {
            Text("Campaña mensual ${campaniaMes.mes}", style = MaterialTheme.typography.titleMedium)
            Text("Estantes completados: ${zonasMes.count { it.estado == "CONTADA" }} / ${zonasMes.size}")
            Text(if (campaniaMes.estado == "FINALIZADA") "Informe mensual consolidado disponible en Gerencia"
                else "Los parciales completados se acumulan para un único informe final.")
        }
        if (jefe && activo == null) TarjetaPanel {
            Text("Iniciar inventario físico", style = MaterialTheme.typography.titleMedium)
            CampoFormulario("Nombre o período", nombre, { nombre = it.take(80) })
            Text("Los conteos se agrupan por mes y año. " +
                "Al abrir el primero se fija la lista de estantes del mes.", style = MaterialTheme.typography.bodySmall)
            val opciones = listOf(0L to "Sin asignar por ahora") + trabajadores.map { it.id to it.nombre }
            SelectorSimple("Responsable inicial", opciones, responsableZona) { uid ->
                responsableZona=uid; posicionesContables.forEach { reparto[it.id]=uid }
            }
            Row {
                TextButton(onClick = { posicionesContables.forEach { seleccionadas[it.id] = true } }) { Text("Seleccionar todos") }
                TextButton(onClick = { posicionesContables.forEach { seleccionadas[it.id] = false } }) { Text("Ninguno") }
            }
            posicionesContables.forEach { pos ->
                val bloqueada = pos.id in bloqueadasMes
                val activoZona = !bloqueada && seleccionadas[pos.id] == true
                Row {
                    Checkbox(checked = activoZona, enabled = !bloqueada, onCheckedChange = { seleccionadas[pos.id]=it })
                    Text(rutaTienda(pos,posiciones), modifier = androidx.compose.ui.Modifier.weight(1f))
                    TextButton(enabled = !bloqueada, onClick = {
                        posicionesContables.forEach { seleccionadas[it.id] = it.id == pos.id }
                    }) { Text("Solo este") }
                }
                if (bloqueada) Text("Estante ya contado o reservado")
                if (activoZona) SelectorSimple("Trabajador de este estante", opciones,
                    reparto[pos.id] ?: responsableZona ?: 0L) { reparto[pos.id] = it }
            }
            BotonAccion("Abrir inventario de zonas seleccionadas", enabled = !ocupado && nombre.trim().length >= 3 && incluidas.isNotEmpty(), onClick = {
                ocupado = true
                val asignaciones = incluidas.associate { it.id to (reparto[it.id] ?: responsableZona ?: 0L) }
                tienda.abrirInventarioTienda(base.sesion, nombre, asignaciones) { ok, msg ->
                    mensaje = msg; ocupado = false
                    if (ok) { nombre = ""; historialId = null; reparto.clear(); seleccionadas.clear() }
                }
            })
        }
        if (activo != null) TarjetaPanel {
            Text("${activo.nombre} · En conteo", style = MaterialTheme.typography.titleMedium)
            val completadas = lineasActivas.count { it.cantidadContada != null }
            Text("Posiciones-producto contadas: $completadas / ${lineasActivas.size}")
            if (lineasActivas.isEmpty()) Text("Sin stock registrado en los estantes seleccionados",
                style = MaterialTheme.typography.bodySmall)
            if (jefe) {
                Text("Zonas del conteo", style = MaterialTheme.typography.titleSmall)
                val opciones = listOf(0L to "Sin asignar") + trabajadores.map { it.id to it.nombre }
                zonas.forEach { zona ->
                    val ruta = posiciones.firstOrNull { it.id==zona.posicionId }?.let { rutaTienda(it,posiciones) } ?: "Estante ${zona.posicionId}"
                    val trabajador = trabajadores.firstOrNull { it.id==zona.asignadoA }?.nombre ?: "Sin asignar"
                    Text("$ruta · $trabajador · ${if (zona.posicionId in idsVacios) "Vacío confirmado" else if (zona.confirmadoPor!=null) "Revisada" else "Pendiente"}")
                    if (zona.asignadoA==0L) {
                        SelectorSimple("Responsable de $ruta", opciones, reparto[zona.posicionId] ?: 0L) { reparto[zona.posicionId]=it }
                        BotonAccion("Asignar ubicación", outlined=true,
                            enabled=!ocupado && (reparto[zona.posicionId] ?: 0L)>0, onClick={
                            ocupado=true
                            tienda.asignarZonaTienda(base.sesion,activo.id,zona.posicionId,reparto[zona.posicionId]!!) { _,msg ->
                                ocupado=false;mensaje=msg
                            }
                        })
                    }
                    if (zona.confirmadoPor!=null) {
                        CampoFormulario("Motivo de recuento de $ruta", motivoReconteo,
                            { motivoReconteo=it.take(200) })
                        BotonAccion("Solicitar recuento de este estante", outlined=true,
                            enabled=!ocupado && motivoReconteo.trim().length>=5, onClick={
                            ocupado=true
                            tienda.reabrirZonaTienda(base.sesion,activo.id,zona.posicionId,motivoReconteo) { _,msg ->
                                ocupado=false;mensaje=msg;motivoReconteo=""
                            }
                        })
                    }
                }
                CampoFormulario("Justificación de diferencias", observacion, { observacion = it.take(250) })
                val diferencias = lineasActivas.any { it.cantidadContada != null && it.cantidadContada != it.stockInicio }
                BotonAccion("Cancelar inventario sin ajustes", outlined = true,
                    enabled = !ocupado && observacion.trim().length >= 5, onClick = {
                    ocupado = true
                    tienda.cancelarInventarioTienda(base.sesion, activo.id, observacion) { ok, msg ->
                        ocupado = false; mensaje = msg
                        if (ok) observacion = ""
                    }
                })
                val zonasCompletas = zonas.isNotEmpty() && zonas.all { it.asignadoA>0L && it.confirmadoPor!=null }
                BotonAccion("Conciliar y cerrar inventario", icon = Icons.Default.AssignmentTurnedIn,
                    enabled = !ocupado && zonasCompletas && completadas == lineasActivas.size &&
                        (!diferencias || observacion.trim().length >= 5), onClick = {
                    ocupado = true
                    tienda.cerrarInventarioTienda(base.sesion, activo.id, observacion) { ok, msg ->
                        ocupado = false; mensaje = msg
                        if (ok) observacion = ""
                    }
                })
            }
        }
        if (activo != null && !jefe && mostrandoActivo) TarjetaPanel {
            Text("Registrar mercancía encontrada", style = MaterialTheme.typography.titleMedium)
            val posicionesValidas = posiciones.filter { it.id in posicionesPermitidas && it.tipo != "SECCION" }
            SelectorSimple("Producto encontrado", productos.map { it.id to "${it.codigo} · ${it.nombre}" },
                hallazgoProducto) { hallazgoProducto = it }
            SelectorSimple("Ubicación física", posicionesValidas.map { it.id to rutaTienda(it, posiciones) },
                hallazgoPosicion) { hallazgoPosicion = it }
            CampoFormulario("Cantidad física encontrada", hallazgoCantidad, {
                if (it.isEmpty() || (it.all(Char::isDigit) && it.length <= 6)) hallazgoCantidad = it
            }, keyboardType = KeyboardType.Number)
            BotonAccion("Agregar hallazgo al inventario", enabled = !ocupado &&
                hallazgoProducto != null && hallazgoPosicion != null &&
                (hallazgoCantidad.toIntOrNull() ?: 0) in 1..100000, onClick = {
                ocupado = true
                tienda.registrarHallazgoTienda(base.sesion, activo.id, hallazgoProducto!!,
                    hallazgoPosicion!!, hallazgoCantidad.toInt()) { ok, msg ->
                    ocupado = false; mensaje = msg
                    if (ok) { hallazgoProducto = null; hallazgoPosicion = null; hallazgoCantidad = "" }
                }
            })
        }
        if (id != null) {
            val seleccionado = inventarios.firstOrNull { it.id == id }
            Text(if (mostrandoActivo) "Detalle del conteo en curso" else "Detalle del inventario histórico",
                style = MaterialTheme.typography.titleMedium)
            if (historialId != null && activo != null) BotonAccion("Volver al inventario activo",
                outlined = true, onClick = { historialId = null })
            if (!jefe && mostrandoActivo && misZonas.isEmpty()) Text("No tienes estantes pendientes asignados para este inventario.")
            if (!jefe && mostrandoActivo) {
                val entregadosVacios = zonas.filter { it.asignadoA == base.sesion?.id && it.posicionId in idsVacios }
                if (entregadosVacios.isNotEmpty()) Text("Estantes entregados como vacíos: " +
                    entregadosVacios.joinToString { z -> posiciones.firstOrNull { it.id == z.posicionId }
                        ?.let { rutaTienda(it, posiciones) } ?: "Estante ${z.posicionId}" })
            }
            lineasVisibles.forEach { linea ->
                val producto = productos.firstOrNull { it.id == linea.productoId }
                val pos = posiciones.firstOrNull { it.id == linea.posicionId }
                TarjetaPanel {
                    val codigoVisible = if (seleccionado?.estado == "CERRADO")
                        linea.codigoProductoCierre ?: producto?.codigo ?: "Producto"
                        else producto?.codigo ?: "Producto"
                    val nombreVisible = if (seleccionado?.estado == "CERRADO")
                        linea.nombreProductoCierre ?: producto?.nombre ?: "Producto #${linea.productoId}"
                        else producto?.nombre ?: "Producto #${linea.productoId}"
                    Text("$codigoVisible · $nombreVisible")
                    Text((if (seleccionado?.estado == "CERRADO") linea.rutaUbicacionCierre else null)
                        ?: pos?.let { rutaTienda(it, posiciones) } ?: "Ubicación #${linea.posicionId}",
                        style = MaterialTheme.typography.bodySmall)
                    Text("Sistema al abrir: ${linea.stockInicio} · Físico: ${linea.cantidadContada?.toString() ?: "Sin contar"}")
                    if (!jefe && mostrandoActivo) CampoFormulario("Unidades contadas",
                        cantidades[linea.id] ?: linea.cantidadContada?.toString().orEmpty(),
                        { if (it.isEmpty() || (it.all(Char::isDigit) && it.length <= 6)) cantidades[linea.id] = it },
                        keyboardType = KeyboardType.Number)
                    if (linea.cantidadContada != null && linea.cantidadContada != linea.stockInicio)
                        Text("Diferencia: ${linea.cantidadContada - linea.stockInicio}",
                            color = MaterialTheme.colorScheme.error)
                }
            }
            if (!jefe && mostrandoActivo) {
                misZonas.forEach { zona ->
                    val ruta = posiciones.firstOrNull { it.id==zona.posicionId }?.let { rutaTienda(it,posiciones) } ?: "Estante ${zona.posicionId}"
                    BotonAccion("Confirmar estante vacío: $ruta", outlined = true,
                        enabled = !ocupado, onClick = { confirmarVacioPosicion = zona.posicionId })
                    val pendientes = lineas.filter { it.posicionId==zona.posicionId && it.cantidadContada==null }
                    BotonAccion("Confirmar revisión de $ruta", outlined=true,
                        enabled=!ocupado && pendientes.isEmpty() && cantidades.keys.none { clave ->
                            lineas.any { it.id==clave && it.posicionId==zona.posicionId }
                        }, onClick={
                            ocupado=true
                            tienda.confirmarZonaTienda(base.sesion,activo!!.id,zona.posicionId) { _,msg ->
                                ocupado=false;mensaje=msg
                            }
                        })
                }
                val nuevos = cantidades.mapNotNull { (clave, valor) ->
                    valor.toIntOrNull()?.takeIf { it in 0..100000 }?.let { clave to it }
                }.toMap()
                BotonAccion("Guardar conteos registrados", enabled = !ocupado && nuevos.isNotEmpty() &&
                    cantidades.size == nuevos.size, onClick = {
                    ocupado = true
                    tienda.guardarConteosTienda(base.sesion, activo.id, nuevos) { ok, msg ->
                        ocupado = false; mensaje = msg
                        if (ok) cantidades.clear()
                    }
                })
            }
            if (eventos.isNotEmpty()) TarjetaPanel {
                Text("Historial de conteos y reconteos", style = MaterialTheme.typography.titleMedium)
                eventos.forEach { evento ->
                    Text("${formatoFecha(evento.fecha)} · ${evento.tipo} · " +
                        "Usuario #${evento.usuarioId} · Producto #${evento.productoId}: " +
                        "${evento.cantidadAnterior?.toString() ?: "Sin contar"} → ${evento.cantidadNueva}",
                        style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        if (confirmarVacioPosicion != null && activo != null && !jefe && mostrandoActivo) {
            val estanteId = confirmarVacioPosicion!!
            val nombreEstante = posiciones.firstOrNull { it.id==estanteId }
                ?.let { rutaTienda(it, posiciones) } ?: "Estante $estanteId"
            AlertDialog(onDismissRequest = { confirmarVacioPosicion = null },
                title = { Text("Confirmar estante vacío") },
                text = { Text("¿Confirmas que $nombreEstante está vacío? Se registrará un conteo físico de cero unidades.") },
                confirmButton = { TextButton(enabled = !ocupado, onClick = {
                    confirmarVacioPosicion = null
                    ocupado = true
                    tienda.confirmarEstanteVacioTienda(base.sesion, activo.id, estanteId) { correcto, texto ->
                        ocupado = false; mensaje = texto
                        if (correcto) {
                            lineas.filter { it.posicionId == estanteId }.forEach { cantidades.remove(it.id) }
                        }
                    }
                }) { Text("Confirmar vacío") } },
                dismissButton = { TextButton(onClick = { confirmarVacioPosicion = null }) { Text("Volver") } })
        }
        if (mensaje.isNotBlank()) Text(mensaje)
        TituloSeccion("HISTORIAL", "Inventarios de tienda")
        inventarios.forEach { inventario ->
            FilaOpcion(inventario.nombre, "${inventario.estado} · ${formatoFecha(inventario.fechaApertura)}",
                onClick = { historialId = if (historialId == inventario.id) null else inventario.id })
        }
    }
}
