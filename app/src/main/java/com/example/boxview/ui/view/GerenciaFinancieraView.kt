package com.example.boxview.ui.view

// Pantallas: Panel y reportes financieros de gerencia.
import com.example.boxview.mensajeErrorUsuario
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.boxview.modelo.*
import com.example.boxview.ui.components.*
import com.example.boxview.viewmodel.AbastecimientoExternoViewModel
import com.example.boxview.viewmodel.OperadorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale

private fun dineroClp(valor: Long) = "$" + NumberFormat.getIntegerInstance(Locale("es", "CL")).format(valor) + " CLP"
// Muestra la pantalla de inicio financiero gerencia.
@Composable
fun PantallaInicioFinancieroGerencia(base: OperadorViewModel, vm: AbastecimientoExternoViewModel, ir: (String) -> Unit) {
    val uid = base.sesion?.id ?: return
    // Datos observados por esta pantalla.
    val resumen by remember(uid) { vm.resumenFinanciero(uid) }.collectAsState(initial = emptyList())
    val ventas by remember(uid) { vm.resumenVentas(uid) }.collectAsState(initial = emptyList())
    val compras by remember { vm.solicitudesGerencia() }.collectAsState(initial = emptyList())
    ColumnaOperador {
        TituloSeccion("GERENCIA", "Resumen financiero", "Compras y ventas por sucursal")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TarjetaResumen("Aprobado", dineroClp(resumen.sumOf { it.totalAprobado }), Modifier.weight(1f))
            TarjetaResumen("Recibido", dineroClp(resumen.sumOf { it.totalRecibido }), Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TarjetaResumen("Por aprobar", dineroClp(resumen.sumOf { it.totalPendiente }), Modifier.weight(1f))
            TarjetaResumen("Solicitudes", compras.count { it.estado == "EN_GERENCIA" }.toString(), Modifier.weight(1f))
        }
        TarjetaPanel {
            Text("Ingresos por ventas registradas: ${dineroClp(ventas.sumOf { it.ingresoVentaClp })}", fontWeight = FontWeight.Bold)
            Text("${ventas.sumOf { it.ventasValoradas }} ventas con precio · ${ventas.sumOf { it.unidadesVendidas }} unidades")
            if (ventas.any { it.ventasAnterioresSinValor > 0 }) Text("Ventas sin importe registrado: ${ventas.sumOf { it.ventasAnterioresSinValor }}.",
                color = MaterialTheme.colorScheme.error)
        }
        if (resumen.any { it.solicitudesSinCotizacion > 0 }) Text("Solicitudes sin cotización: ${resumen.sumOf { it.solicitudesSinCotizacion }}.",
            color = MaterialTheme.colorScheme.error)
        BotonAccion("Revisar solicitudes", onClick = { ir(Destinos.GerenciaSolicitudes.ruta) })
        BotonAccion("Informes financieros", outlined = true, onClick = { ir(Destinos.GerenciaReportes.ruta) })
        resumen.forEach { sede -> TarjetaPanel {
            Text(sede.sucursal, fontWeight = FontWeight.Bold)
            Text("Aprobado ${dineroClp(sede.totalAprobado)} · Recibido ${dineroClp(sede.totalRecibido)}")
            Text("Pendiente de decisión ${dineroClp(sede.totalPendiente)}", style = MaterialTheme.typography.bodySmall)
        } }
    }
}

// Muestra la pantalla de finanzas gerencia.
@Composable
fun PantallaFinanzasGerencia(base: OperadorViewModel, vm: AbastecimientoExternoViewModel) {
    val uid = base.sesion?.id ?: return
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    // Datos observados por esta pantalla.
    val resumen by remember(uid) { vm.resumenFinanciero(uid) }.collectAsState(initial = emptyList())
    val ventas by remember(uid) { vm.resumenVentas(uid) }.collectAsState(initial = emptyList())
    val solicitudes by remember { vm.solicitudesGerencia() }.collectAsState(initial = emptyList())
    val catalogo by remember { vm.productos() }.collectAsState(initial = emptyList())
    var filtro by remember { mutableStateOf<Long?>(null) }
    var mensaje by remember { mutableStateOf("") }
    var exportando by remember { mutableStateOf(false) }
    var pdfReciente by remember { mutableStateOf<android.net.Uri?>(null) }
    val exportar = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        if (uri != null && !exportando) scope.launch {
            exportando = true
            try {
                val user = base.sesion ?: error("Sesión no disponible")
                val sucursalElegida = filtro
                val listado = resumen.filter { sucursalElegida == null || it.sucursalId == sucursalElegida }.toList()
                val ventasResumen = ventas.filter { sucursalElegida == null || it.sucursalId == sucursalElegida }.toList()
                val nombresProductos = catalogo.associate { it.id to (it.codigo + " · " + it.nombre) }
                val (compras, lineas) = withContext(Dispatchers.IO) { vm.datosFinancierosExportacion(user) }
                val ventasDetalle = withContext(Dispatchers.IO) { vm.ventasParaExportar(user) }
                withContext(Dispatchers.IO) {
                    val pdf = InformePdf("Reporte financiero", "Compras cotizadas y ventas registradas", "Gerencia")
                    val nombreSede = { id: Long -> listado.firstOrNull { it.sucursalId == id }?.sucursal ?: "Sucursal #$id" }
                    pdf.metricas(listOf(
                        "Compras aprobadas" to pdf.moneda(listado.sumOf { it.totalAprobado }),
                        "Compras recibidas" to pdf.moneda(listado.sumOf { it.totalRecibido }),
                        "Pendiente de aprobación" to pdf.moneda(listado.sumOf { it.totalPendiente }),
                        "Ingresos de ventas" to pdf.moneda(ventasResumen.sumOf { it.ingresoVentaClp }),
                        "Ventas registradas" to pdf.numero(ventasResumen.sumOf { it.ventasValoradas }),
                        "Recepciones completadas" to pdf.numero(listado.sumOf { it.recepcionesCompletadas })
                    ))
                    pdf.barras("Ingresos por ventas según sucursal", ventasResumen.map { nombreSede(it.sucursalId) to it.ingresoVentaClp }, true)
                    pdf.barras("Compras aprobadas por sucursal", listado.map { it.sucursal to it.totalAprobado }, true)
                    pdf.tabla("Consolidado por sucursal", listOf(
                        ColumnaInforme("Sucursal", 2f), ColumnaInforme("Aprobado", 1.4f),
                        ColumnaInforme("Recibido", 1.4f), ColumnaInforme("Pendiente", 1.4f)),
                        listado.map { listOf(it.sucursal, pdf.moneda(it.totalAprobado), pdf.moneda(it.totalRecibido), pdf.moneda(it.totalPendiente)) })
                    pdf.tabla("Actividad y control por sucursal", listOf(
                        ColumnaInforme("Sucursal", 2f), ColumnaInforme("Recepciones", 1f),
                        ColumnaInforme("Por aprobar", 1f), ColumnaInforme("Sin cotización", 1.2f),
                        ColumnaInforme("Ventas (CLP)", 1.6f)),
                        listado.map { s -> listOf(s.sucursal, s.recepcionesCompletadas.toString(),
                            s.solicitudesPendientes.toString(), s.solicitudesSinCotizacion.toString(),
                            pdf.moneda(ventasResumen.firstOrNull { it.sucursalId == s.sucursalId }?.ingresoVentaClp ?: 0L)) })
                    val detalleVentas = ventasDetalle.filter { sucursalElegida == null || it.sucursalId == sucursalElegida }
                    pdf.tabla("Ventas confirmadas - detalle", listOf(
                        ColumnaInforme("Venta / fecha", 1.8f), ColumnaInforme("Sucursal", 1.8f),
                        ColumnaInforme("Producto", 1f), ColumnaInforme("Unid.", .65f), ColumnaInforme("Total CLP", 1.5f)),
                        detalleVentas.sortedByDescending { it.fecha }.map { v -> listOf(
                            "#${v.id} · ${java.text.SimpleDateFormat("dd/MM/yy", Locale("es", "CL")).format(java.util.Date(v.fecha))}",
                            nombreSede(v.sucursalId), nombresProductos[v.productoId] ?: "#${v.productoId}", v.cantidad.toString(), pdf.moneda(v.totalVentaClp ?: 0L)) })
                    val comprasFiltradas = compras.filter { sucursalElegida == null || it.sucursalId == sucursalElegida }
                    val lineasPorSolicitud = lineas.groupBy { it.solicitudId }
                    pdf.tabla("Solicitudes de abastecimiento", listOf(
                        ColumnaInforme("Solicitud / sucursal", 1.7f), ColumnaInforme("Proveedor", 1.6f),
                        ColumnaInforme("Estado", 1.25f), ColumnaInforme("Cotizado", 1.35f)),
                        comprasFiltradas.sortedByDescending { it.fecha }.map { s ->
                            val lineasSolicitud = lineasPorSolicitud[s.id].orEmpty()
                            val total = lineasSolicitud.sumOf { (it.precioUnitarioCotizado ?: 0L) * it.cantidad.toLong() }
                            listOf("#${s.id} · ${nombreSede(s.sucursalId)}", s.proveedorCotizado.ifBlank { "Sin cotización" },
                                s.estado, if (lineasSolicitud.isNotEmpty() && lineasSolicitud.all { it.precioUnitarioCotizado != null }) pdf.moneda(total) else "Sin valor completo")
                        })
                    pdf.tabla("Detalle de productos cotizados", listOf(
                        ColumnaInforme("Solicitud", 1f), ColumnaInforme("Producto", 1f), ColumnaInforme("Cantidad", .8f),
                        ColumnaInforme("Unitario CLP", 1.4f), ColumnaInforme("Total CLP", 1.5f)),
                        comprasFiltradas.flatMap { s -> lineasPorSolicitud[s.id].orEmpty().map { l ->
                            listOf("#${s.id}", nombresProductos[l.productoId] ?: "#${l.productoId}", l.cantidad.toString(),
                                l.precioUnitarioCotizado?.let(pdf::moneda) ?: "No cotizado",
                                l.precioUnitarioCotizado?.let { pdf.moneda(it * l.cantidad.toLong()) } ?: "No cotizado")
                        } })
                    pdf.guardar(context, uri)
                }
                pdfReciente = uri
                mensaje = "Reporte financiero PDF guardado correctamente"
            } catch (e: Exception) { mensaje = mensajeErrorUsuario(e, "No se pudo exportar el PDF") }
            finally { exportando = false }
        }
    }
    ColumnaOperador {
        TituloSeccion("GERENCIA", "Informes financieros", "Ingresos por ventas y compras cotizadas")
        SelectorSimple("Sucursal", listOf(0L to "Todas") + resumen.map { it.sucursalId to it.sucursal },
            filtro ?: 0L) { filtro = it.takeUnless { id -> id == 0L } }
        val seleccion = resumen.filter { filtro == null || filtro == it.sucursalId }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TarjetaResumen("Aprobado", dineroClp(seleccion.sumOf { it.totalAprobado }), Modifier.weight(1f))
            TarjetaResumen("Recibido", dineroClp(seleccion.sumOf { it.totalRecibido }), Modifier.weight(1f))
        }
        Text("Pendiente de decisión: ${dineroClp(seleccion.sumOf { it.totalPendiente })}", fontWeight = FontWeight.Bold)

        val ventasSeleccion = ventas.filter { filtro == null || filtro == it.sucursalId }
        TarjetaPanel {
            Text("Ingresos por ventas confirmadas: ${dineroClp(ventasSeleccion.sumOf { it.ingresoVentaClp })}", fontWeight = FontWeight.Bold)
            Text("${ventasSeleccion.sumOf { it.unidadesVendidas }} unidades vendidas con precio registrado")
            val anteriores = ventasSeleccion.sumOf { it.ventasAnterioresSinValor }
            if (anteriores > 0) Text("Ventas sin importe registrado: $anteriores.",
                color = MaterialTheme.colorScheme.error)
        }
        if (seleccion.any { it.solicitudesSinCotizacion > 0 }) Text("Solicitudes sin cotización: ${seleccion.sumOf { it.solicitudesSinCotizacion }}.",
            color = MaterialTheme.colorScheme.error)
        BotonAccion(if (exportando) "Generando PDF…" else "Descargar reporte financiero (PDF)", enabled = !exportando,
            onClick = { exportar.launch("boxview_reporte_financiero.pdf") })
        pdfReciente?.let { archivo -> BotonAccion("Abrir último PDF", outlined = true,
            onClick = { abrirInformePdf(context, archivo)?.let { mensaje = it } }) }
        if (mensaje.isNotBlank()) Text(mensaje)
        Text("Resultados por sucursal", fontWeight = FontWeight.Bold)
        seleccion.forEach { s -> TarjetaPanel {
            Text(s.sucursal, fontWeight = FontWeight.SemiBold)
            Text("Aprobado: ${dineroClp(s.totalAprobado)}")
            Text("Recibido: ${dineroClp(s.totalRecibido)}")
            Text("Cotizaciones pendientes: ${dineroClp(s.totalPendiente)} · ${s.solicitudesPendientes} solicitudes")
            Text("Ventas registradas: ${dineroClp(ventas.firstOrNull { it.sucursalId == s.sucursalId }?.ingresoVentaClp ?: 0L)}")
            Text("Recepciones confirmadas: ${s.recepcionesCompletadas}", style = MaterialTheme.typography.bodySmall)
        } }
    }
}
