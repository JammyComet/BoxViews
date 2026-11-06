package com.example.boxview.ui.view

// Pantallas: Reposición sugerida y pedidos para tienda.
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.example.boxview.ui.components.*
import com.example.boxview.viewmodel.JefeTiendaViewModel
import com.example.boxview.viewmodel.OperadorViewModel
import com.example.boxview.viewmodel.TrabajadorTiendaViewModel

// Muestra la pantalla de reposicion jefe tienda.
@Composable
fun PantallaReposicionJefeTienda(base: OperadorViewModel, tienda: TrabajadorTiendaViewModel,
                                jefe: JefeTiendaViewModel) {
    val sid = tienda.tiendaActiva?.id ?: return
    val usuario = base.sesion ?: return
    // Datos observados por esta pantalla.
    val productos by base.productos.collectAsState(initial = emptyList())
    val stock by remember(sid) { tienda.existencias() }.collectAsState(initial = emptyList())
    val bodegas by remember(sid) { jefe.bodegas(sid) }.collectAsState(initial = emptyList())
    val minimos by remember(sid) { base.minimosSucursal(sid) }.collectAsState(initial = emptyList())
    val configuracion by remember(sid) { base.configuracionSucursal(sid) }.collectAsState(initial = null)
    val mapaMinimos = minimos.associate { it.productoId to it.minimo }
    fun minimo(pid: Long, general: Int) = mapaMinimos[pid] ?: general
    val solicitudes by remember(sid) { jefe.solicitudes(sid) }.collectAsState(initial = emptyList())
    val enCurso by remember(sid) { jefe.productosEnReposicion(sid) }.collectAsState(initial = emptyList())
    val formulario by jefe.reposicion.collectAsState()
    var mensaje by remember(sid) { mutableStateOf("") }
    var procesando by remember(sid) { mutableStateOf(false) }
    var productoExtra by remember(sid) { mutableStateOf<Long?>(null) }
    var cantidadExtra by remember(sid) { mutableStateOf("") }

    val bodegaId = formulario.bodegaId?.takeIf { id -> bodegas.any { it.id == id } }
        ?: configuracion?.bodegaPrincipalId?.takeIf { id -> bodegas.any { it.id == id } }
        ?: bodegas.firstOrNull()?.id
    val pendientes = enCurso.toSet()
    val sugerencias = productos.mapNotNull { p ->
        val actual = stock.firstOrNull { it.productoId == p.id }?.cantidad ?: 0
        if (actual <= minimo(p.id, p.minimo)) p to (minimo(p.id, p.minimo) - actual + 1) else null
    }
    val sinPedido = sugerencias.filter { (p, _) ->
        p.id !in pendientes && formulario.lineas.none { it.productoId == p.id }
    }
    val disponibles = productos.filter { p ->
        p.id !in pendientes && formulario.lineas.none { it.productoId == p.id }
    }
    val validas = formulario.lineas.isNotEmpty() && formulario.lineas.all {
        (it.cantidad.toIntOrNull() ?: 0) in 1..100000 && it.productoId !in pendientes
    }

    ColumnaOperador {
        TituloSeccion("REPOSICIÓN", "Solicitudes de mercadería", tienda.tiendaActiva?.nombre)
        if (bodegas.isEmpty()) {
            TarjetaPanel { Text("La sucursal no tiene una bodega activa asignada.") }
        } else {
            TarjetaPanel {
                Text("Bodega de abastecimiento", fontWeight = FontWeight.Bold)
                if (bodegas.size == 1) {
                    Text(bodegas.first().nombre)
                } else {
                    SelectorSimple("Bodega de abastecimiento", bodegas.map { it.id to (it.nombre + if (it.id == configuracion?.bodegaPrincipalId) " · Principal" else "") }, bodegaId) {
                        jefe.cambiarBodega(it)
                        mensaje = ""
                    }
                }
            }
            TituloSeccion("CONTROL DE STOCK", "Productos bajo el mínimo")
            if (sugerencias.isEmpty()) {
                Text("Todos los productos cumplen con el stock mínimo establecido.")
            } else {
                Text("${sugerencias.size} productos bajo mínimo",
                    style = MaterialTheme.typography.bodySmall)
                sugerencias.forEach { (producto, faltante) ->
                    val actual = stock.firstOrNull { it.productoId == producto.id }?.cantidad ?: 0
                    val estado = if (actual == 0 || actual <= minimo(producto.id, producto.minimo) / 2) "Crítico" else "Bajo"
                    TarjetaPanel {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(producto.nombre, modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                            EtiquetaEstado(estado, warning = true)
                        }
                        Text("Actual: $actual · Mínimo: ${minimo(producto.id, producto.minimo)} · Sugerencia: $faltante unidades",
                            style = MaterialTheme.typography.bodySmall)
                        if (producto.id in pendientes) Text("Reposición en curso", style = MaterialTheme.typography.bodySmall)
                        else if (formulario.lineas.any { it.productoId == producto.id }) Text("Agregado al pedido", style = MaterialTheme.typography.bodySmall)
                        else BotonAccion("Añadir al pedido", outlined = true, enabled = !procesando,
                            onClick = { jefe.agregarLineaReposicion(producto.id, faltante); mensaje = "" })
                    }
                }
                BotonAccion("Agregar todas las sugerencias", outlined = true,
                    enabled = !procesando && sinPedido.isNotEmpty(),
                    onClick = {
                        jefe.cargarSugerencias(sinPedido.map { it.first.id to it.second })
                        mensaje = ""
                    })
            }
            TituloSeccion("NUEVO PEDIDO", "Productos y cantidades")
            TarjetaPanel {
                if (formulario.lineas.isEmpty()) Text("No hay productos agregados.")
                formulario.lineas.forEach { linea ->
                    val producto = productos.firstOrNull { it.id == linea.productoId }
                    val actual = stock.firstOrNull { it.productoId == linea.productoId }?.cantidad ?: 0
                    Text(producto?.nombre ?: "Producto #${linea.productoId}", fontWeight = FontWeight.SemiBold)
                    Text("Stock: $actual · Mínimo: ${producto?.let { minimo(it.id, it.minimo) } ?: 0}", style = MaterialTheme.typography.bodySmall)
                    CampoFormulario("Unidades solicitadas", linea.cantidad,
                        { jefe.cambiarCantidadLinea(linea.productoId, it); mensaje = "" },
                        keyboardType = KeyboardType.Number)
                    BotonAccion("Quitar producto", outlined = true, enabled = !procesando,
                        onClick = { jefe.quitarLineaReposicion(linea.productoId); mensaje = "" })
                    HorizontalDivider()
                }
                if (disponibles.isNotEmpty()) {
                    SelectorSimple("Agregar otro producto", disponibles.map { it.id to "${it.codigo} · ${it.nombre}" },
                        productoExtra?.takeIf { id -> disponibles.any { it.id == id } }) {
                        productoExtra = it
                        cantidadExtra = ""
                    }
                    CampoFormulario("Cantidad", cantidadExtra, { texto ->
                        if (texto.isEmpty() || (texto.all(Char::isDigit) && texto.length <= 6)) cantidadExtra = texto
                    }, keyboardType = KeyboardType.Number)
                    BotonAccion("Agregar al pedido", outlined = true, enabled = !procesando &&
                        productoExtra != null && (cantidadExtra.toIntOrNull() ?: 0) in 1..100000,
                        onClick = {
                            val id = productoExtra ?: return@BotonAccion
                            jefe.agregarLineaReposicion(id, cantidadExtra.toInt())
                            productoExtra = null
                            cantidadExtra = ""
                            mensaje = ""
                        })
                }
                Text("${formulario.lineas.size} productos · ${formulario.lineas.sumOf { it.cantidad.toIntOrNull() ?: 0 }} unidades",
                    fontWeight = FontWeight.SemiBold)
                BotonAccion("Confirmar solicitud de reposición", enabled = !procesando && bodegaId != null && validas,
                    onClick = {
                        val id = bodegaId ?: return@BotonAccion
                        procesando = true
                        jefe.solicitarPedido(usuario, sid, id, formulario.lineas) { ok, msg ->
                            procesando = false
                            mensaje = msg
                            if (ok) jefe.reiniciarReposicion()
                        }
                    })
            }
        }
        if (mensaje.isNotBlank()) TarjetaPanel { Text(mensaje) }
        TituloSeccion("SEGUIMIENTO", "Pedidos registrados")
        if (solicitudes.isEmpty()) Text("No hay pedidos registrados.")
        solicitudes.groupBy { it.loteId ?: "solicitud-${it.id}" }.values.forEach { grupo ->
            val primero = grupo.first()
            val bodega = bodegas.firstOrNull { it.id == primero.bodegaId }
            TarjetaPanel {
                Text("Pedido #${primero.id} · ${primero.estado}", fontWeight = FontWeight.Bold)
                Text("${formatoFecha(primero.fecha)} · ${bodega?.nombre ?: "Bodega #${primero.bodegaId}"}",
                    style = MaterialTheme.typography.bodySmall)
                grupo.forEach { sol ->
                    val p = productos.firstOrNull { it.id == sol.productoId }
                    Text("${p?.nombre ?: "Producto #${sol.productoId}"}: ${sol.cantidad} unidades")
                }
                if (primero.cajaId != null) Text("Orden de despacho #${primero.cajaId}",
                    style = MaterialTheme.typography.bodySmall)
                if (primero.estado == "PENDIENTE") {
                    BotonAccion("Cancelar pedido", outlined = true, enabled = !procesando,
                        onClick = {
                            procesando = true
                            jefe.cancelarReposicion(usuario, sid, primero.id) { _, msg ->
                                procesando = false
                                mensaje = msg
                            }
                        })
                }
            }
        }
    }
}
