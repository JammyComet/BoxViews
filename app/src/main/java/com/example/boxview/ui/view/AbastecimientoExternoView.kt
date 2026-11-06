package com.example.boxview.ui.view

// Pantallas: Solicitudes y pedidos a proveedores externos.
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
import com.example.boxview.viewmodel.AbastecimientoExternoViewModel
import com.example.boxview.viewmodel.OperadorViewModel
import com.example.boxview.viewmodel.AdministradorViewModel

// estados de abastecimiento
private fun nombreEstadoExterno(estado: String) = when (estado) {
    "PENDIENTE_ADMIN" -> "Pendiente en administración"
    "EN_GERENCIA" -> "En revisión de gerencia"
    "APROBADA" -> "Aprobada"
    "RECHAZADA" -> "Rechazada"
    "ENTREGA_PROGRAMADA" -> "Entrega esperada"
    "RECIBIDA" -> "Recibida"
    "CON_DIFERENCIA" -> "Recepción con diferencia"
    else -> estado
}

// lineas y estado del pedido
// Componente visual de detalles externos.
@Composable
private fun DetallesExternos(pedido: SolicitudExterna, vm: AbastecimientoExternoViewModel, productos: List<Producto>, destino: String) {
    // Datos observados por esta pantalla.
    val lineas by remember(pedido.id) { vm.lineas(pedido.id) }.collectAsState(initial = emptyList())
    Text("Solicitud #${pedido.id} · ${nombreEstadoExterno(pedido.estado)}", fontWeight = FontWeight.Bold)
    Text("Fecha: ${formatoFecha(pedido.fecha)}", style = MaterialTheme.typography.bodySmall)
    Text("Motivo: ${pedido.motivo}", style = MaterialTheme.typography.bodySmall)
    lineas.forEach { linea ->
        val producto = productos.firstOrNull { it.id == linea.productoId }
        Text("${producto?.nombre ?: "Producto #${linea.productoId}"} · ${linea.cantidad} unidades")
    }
    if (pedido.observacion.isNotBlank()) Text("Observación: ${pedido.observacion}", style = MaterialTheme.typography.bodySmall)
    val cajaId = pedido.entregaCajaId
    if (cajaId != null) {
        val caja by remember(cajaId) { vm.entrega(cajaId) }.collectAsState(initial = null)
        val estadoEntrega = when (caja?.estado) {
            "EN_TRANSITO" -> "En tránsito hacia bodega"
            "EN_REVISION" -> "Llegó a bodega · por comprobar"
            "RECIBIDA" -> "Recibida y comprobada"
            "CON_DIFERENCIA" -> "Recibida con diferencia"
            "ESPERADA" -> "Pendiente de despacho"
            else -> caja?.estado ?: "Pendiente"
        }
        Text("Entrega: ${caja?.codigo ?: "#$cajaId"} · $estadoEntrega", fontWeight = FontWeight.SemiBold)
        val detalleEntrega by remember(cajaId) { vm.detallesCaja(cajaId) }.collectAsState(initial = emptyList())
        if (caja != null && detalleEntrega.isNotEmpty()) {
            CodigoQrCaja(caja!!.codigo)
            EtiquetaCajaDescargable(caja!!, detalleEntrega, productos, destino = destino)
        }
    }
}

// Solo se utiliza desde las pantallas de Administración y Gerencia, nunca en Bodega.
// Componente visual de cotizacion visible.
@Composable
private fun CotizacionVisible(pedido: SolicitudExterna, vm: AbastecimientoExternoViewModel) {
    // Datos observados por esta pantalla.
    val lineas by remember(pedido.id) { vm.lineas(pedido.id) }.collectAsState(initial = emptyList())
    if (pedido.proveedorCotizado.isBlank()) {
        if (pedido.estado != "PENDIENTE_ADMIN") Text("Solicitud histórica sin cotización registrada",
            style = MaterialTheme.typography.bodySmall)
        return
    }
    Text("Proveedor cotizado: ${pedido.proveedorCotizado}", fontWeight = FontWeight.SemiBold)
    lineas.forEach { linea ->
        Text("${linea.cantidad} uds. × ${linea.precioUnitarioCotizado?.let { "$${it} CLP" } ?: "Sin valor"}",
            style = MaterialTheme.typography.bodySmall)
    }
    if (lineas.isNotEmpty() && lineas.all { (it.precioUnitarioCotizado ?: 0L) > 0 }) {
        Text("Total solicitado: $${lineas.sumOf { it.cantidad.toLong() * (it.precioUnitarioCotizado ?: 0) }} CLP",
            fontWeight = FontWeight.Bold)
    }
}

private data class LineaFormularioExterno(val clave: Int, val producto: Long? = null, val cantidad: String = "")

// Muestra la pantalla de solicitudes externas jefe.
@Composable
fun PantallaSolicitudesExternasJefe(base: OperadorViewModel, vm: AbastecimientoExternoViewModel) {
    val bodega = base.bodegaActiva ?: return
    val user = base.sesion ?: return
    // Datos observados por esta pantalla.
    val productos by remember { vm.productos() }.collectAsState(initial = emptyList())
    val pedidos by remember(bodega.id) { vm.solicitudesBodega(bodega.id) }.collectAsState(initial = emptyList())
    var motivo by remember(bodega.id) { mutableStateOf("") }
    var filas by remember(bodega.id) { mutableStateOf(listOf(LineaFormularioExterno(0))) }
    var secuencia by remember(bodega.id) { mutableIntStateOf(1) }
    var procesando by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }
    ColumnaOperador {
        TituloSeccion("BODEGA", "Abastecimiento externo", "Solicitudes de mercadería para ${bodega.nombre}")
        TarjetaPanel {
            Text("Nueva solicitud", fontWeight = FontWeight.Bold)
            CampoFormulario("Motivo de reposición", motivo, { motivo = it.take(300) })
            filas.forEachIndexed { indice, fila ->
                key(fila.clave) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Producto ${indice + 1}", fontWeight = FontWeight.SemiBold)
                            if (filas.size > 1) TextButton(onClick = { filas = filas.filterNot { it.clave == fila.clave } }) { Text("Quitar") }
                        }
                        SelectorSimple("Producto", productos.filter { p -> filas.none { it.clave != fila.clave && it.producto == p.id } }
                            .map { it.id to "${it.codigo} · ${it.nombre}" }, fila.producto) { nuevo ->
                            filas = filas.map { if (it.clave == fila.clave) it.copy(producto = nuevo) else it }
                        }
                        CampoFormulario("Cantidad", fila.cantidad, { valor ->
                            filas = filas.map { if (it.clave == fila.clave) it.copy(cantidad = valor.filter(Char::isDigit).take(6)) else it }
                        }, keyboardType = KeyboardType.Number)
                    }
                    HorizontalDivider()
                }
            }
            BotonAccion("Añadir producto", outlined = true, enabled = filas.size < productos.size && filas.size < 50,
                onClick = { filas = filas + LineaFormularioExterno(secuencia++ ) })
            val lista = filas.mapNotNull { f -> if (f.producto != null && (f.cantidad.toIntOrNull() ?: 0) > 0)
                AbastecimientoExternoViewModel.LineaPedido(f.producto, f.cantidad.toInt()) else null }
            BotonAccion("Enviar a administración", enabled = !procesando && motivo.trim().length >= 5 &&
                filas.isNotEmpty() && lista.size == filas.size && lista.distinctBy { it.productoId }.size == lista.size,
                onClick = {
                    procesando = true
                    vm.solicitar(user, bodega, motivo, lista) { ok, msg ->
                        procesando = false; mensaje = msg
                        if (ok) { motivo = ""; filas = listOf(LineaFormularioExterno(0)); secuencia = 1 }
                    }
                })
        }
        if (mensaje.isNotBlank()) Text(mensaje)
        Text("Solicitudes registradas", fontWeight = FontWeight.Bold)
        if (pedidos.isEmpty()) Text("No existen solicitudes de abastecimiento")
        pedidos.forEach { pedido -> TarjetaPanel { DetallesExternos(pedido, vm, productos, bodega.nombre) } }
    }
}

// Muestra la pantalla de abastecimiento admin.
@Composable
fun PantallaAbastecimientoAdmin(base: OperadorViewModel, admin: AdministradorViewModel,
                               vm: AbastecimientoExternoViewModel) {
    val user = base.sesion ?: return
    val sid = admin.sucursalActiva?.id ?: return
    // Datos observados por esta pantalla.
    val pedidos by remember(sid) { vm.solicitudesSucursal(sid) }.collectAsState(initial = emptyList())
    val productos by remember { vm.productos() }.collectAsState(initial = emptyList())
    val bodegas by remember(sid) { admin.bodegas(sid) }.collectAsState(initial = emptyList())
    var mensaje by remember { mutableStateOf("") }
    var procesando by remember { mutableStateOf(false) }
    var observacion by remember { mutableStateOf(mapOf<Long, String>()) }
    var proveedor by remember { mutableStateOf(mapOf<Long, String>()) }
    var proveedorCotizado by remember { mutableStateOf(mapOf<Long, String>()) }
    var preciosCotizados by remember { mutableStateOf(mapOf<Pair<Long, Long>, String>()) }
    var operadoresElegidos by remember { mutableStateOf(mapOf<Long, Long?>()) }
    ColumnaOperador {
        TituloSeccion("ADMINISTRACIÓN", "Abastecimiento", "Solicitudes de la sucursal")
        if (mensaje.isNotBlank()) Text(mensaje)
        if (pedidos.isEmpty()) Text("No hay solicitudes registradas")
        pedidos.forEach { pedido ->
            key(pedido.id) {
                val bodega = bodegas.firstOrNull { it.id == pedido.bodegaId }
                val operadores by remember(pedido.bodegaId) { vm.operadores(pedido.bodegaId) }.collectAsState(initial = emptyList())
                TarjetaPanel {
                    Text("Bodega: ${bodega?.nombre ?: "#${pedido.bodegaId}"}", fontWeight = FontWeight.SemiBold)
                    DetallesExternos(pedido, vm, productos, bodega?.nombre ?: "Bodega #${pedido.bodegaId}")
                    if (pedido.estado != "PENDIENTE_ADMIN") CotizacionVisible(pedido, vm)
                    if (pedido.estado == "PENDIENTE_ADMIN") {
                        val lineasCotizar by remember(pedido.id) { vm.lineas(pedido.id) }.collectAsState(initial = emptyList())
                        Text("Cotización de Administración (sin acceso desde Bodega)", fontWeight = FontWeight.Bold)
                        CampoFormulario("Proveedor cotizado", proveedorCotizado[pedido.id] ?: "",
                            { proveedorCotizado = proveedorCotizado + (pedido.id to it.take(100)) })
                        lineasCotizar.forEach { linea ->
                            val producto = productos.firstOrNull { it.id == linea.productoId }
                            CampoFormulario("${producto?.nombre ?: "Producto #${linea.productoId}"} · CLP por unidad",
                                preciosCotizados[pedido.id to linea.productoId] ?: "", { valor ->
                                    preciosCotizados = preciosCotizados +
                                        ((pedido.id to linea.productoId) to valor.filter(Char::isDigit).take(10))
                                }, keyboardType = KeyboardType.Number)
                        }
                        val costos = lineasCotizar.mapNotNull { linea ->
                            (preciosCotizados[pedido.id to linea.productoId]?.toLongOrNull())?.let { linea.productoId to it }
                        }.toMap()
                        if (costos.size == lineasCotizar.size && lineasCotizar.isNotEmpty()) {
                            Text("Total cotizado: $${lineasCotizar.sumOf { it.cantidad.toLong() * costos.getValue(it.productoId) }} CLP")
                        }
                        CampoFormulario("Observación (opcional)", observacion[pedido.id] ?: "",
                            { observacion = observacion + (pedido.id to it.take(300)) })
                        BotonAccion("Enviar cotización a gerencia", enabled = !procesando && lineasCotizar.isNotEmpty() &&
                            (proveedorCotizado[pedido.id]?.trim()?.length ?: 0) >= 3 &&
                            costos.size == lineasCotizar.size && costos.values.all { it in 1..1_000_000_000L }, onClick = {
                            procesando = true
                            vm.enviar(user, sid, pedido.id, observacion[pedido.id] ?: "",
                                proveedorCotizado[pedido.id] ?: "", costos) { _, msg ->
                                procesando = false; mensaje = msg
                            }
                        })
                    }
                    if (pedido.estado == "APROBADA") {
                        Text("Aprobada por gerencia · registrar entrega esperada", fontWeight = FontWeight.SemiBold)
                        CampoFormulario("Proveedor", proveedor[pedido.id] ?: "", { proveedor = proveedor + (pedido.id to it) })
                        SelectorSimple("Operador que recibirá", operadores.map { it.id to it.nombre },
                            operadoresElegidos[pedido.id]) { uid -> operadoresElegidos = operadoresElegidos + (pedido.id to uid) }
                        BotonAccion("Programar recepción", enabled = !procesando &&
                            (proveedor[pedido.id]?.trim()?.length ?: 0) >= 3 && operadoresElegidos[pedido.id] != null,
                            onClick = {
                                procesando = true
                                vm.programar(user, sid, pedido.id, operadoresElegidos.getValue(pedido.id)!!,
                                    proveedor[pedido.id] ?: "") { _, msg -> procesando = false; mensaje = msg }
                            })
                    }
                }
            }
        }
    }
}

// Muestra la pantalla de solicitudes gerencia.
@Composable
fun PantallaSolicitudesGerencia(base: OperadorViewModel, vm: AbastecimientoExternoViewModel) {
    val user = base.sesion ?: return
    // Datos observados por esta pantalla.
    val pedidos by remember { vm.solicitudesGerencia() }.collectAsState(initial = emptyList())
    val productos by remember { vm.productos() }.collectAsState(initial = emptyList())
    val bodegas by remember { vm.bodegas() }.collectAsState(initial = emptyList())
    val sucursales by remember { vm.sucursales() }.collectAsState(initial = emptyList())
    var mensajes by remember { mutableStateOf("") }
    var procesando by remember { mutableStateOf(false) }
    var motivos by remember { mutableStateOf(mapOf<Long, String>()) }
    ColumnaOperador {
        TituloSeccion("GERENCIA", "Solicitudes de abastecimiento", "Aprobación de productos y cantidades por sucursal")
        if (mensajes.isNotBlank()) Text(mensajes)
        if (pedidos.isEmpty()) Text("No existen solicitudes")
        pedidos.sortedWith(compareBy<SolicitudExterna> { it.estado != "EN_GERENCIA" }.thenByDescending { it.fecha }).forEach { pedido ->
            TarjetaPanel {
                Text("${sucursales.firstOrNull { it.id == pedido.sucursalId }?.nombre ?: "Sucursal #${pedido.sucursalId}"} · " +
                     (bodegas.firstOrNull { it.id == pedido.bodegaId }?.nombre ?: "Bodega #${pedido.bodegaId}"),
                    fontWeight = FontWeight.SemiBold)
                DetallesExternos(pedido, vm, productos,
                    bodegas.firstOrNull { it.id == pedido.bodegaId }?.nombre ?: "Bodega #${pedido.bodegaId}")
                CotizacionVisible(pedido, vm)
                if (pedido.estado == "EN_GERENCIA") {
                    CampoFormulario("Observación o motivo de rechazo", motivos[pedido.id] ?: "", { valor ->
                        motivos = motivos + (pedido.id to valor.take(300))
                    })
                    BotonAccion("Aprobar solicitud", enabled = !procesando, onClick = {
                        procesando = true
                        vm.decidir(user, pedido.id, true, motivos[pedido.id] ?: "") { _, msg ->
                            procesando = false; mensajes = msg
                        }
                    })
                    BotonAccion("Rechazar solicitud", outlined = true,
                        enabled = !procesando && (motivos[pedido.id]?.trim()?.length ?: 0) >= 5, onClick = {
                            procesando = true
                            vm.decidir(user, pedido.id, false, motivos[pedido.id] ?: "") { _, msg ->
                                procesando = false; mensajes = msg
                            }
                        })
                }
            }
        }
    }
}
