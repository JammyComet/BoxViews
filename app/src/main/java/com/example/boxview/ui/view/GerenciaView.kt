package com.example.boxview.ui.view

// Pantallas: Panel de gerencia e informes.
import com.example.boxview.mensajeErrorUsuario
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.boxview.modelo.*
import com.example.boxview.ui.components.*
import com.example.boxview.viewmodel.AbastecimientoExternoViewModel
import com.example.boxview.viewmodel.InformeGerenciaViewModel
import com.example.boxview.viewmodel.OperadorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Muestra la pantalla de inicio gerencia.
@Composable
fun PantallaInicioGerencia(vm: AbastecimientoExternoViewModel, informesVm: InformeGerenciaViewModel, ir: (String) -> Unit) {
    // Datos observados por esta pantalla.
    val antiguosBodega by remember { informesVm.nuevos() }.collectAsState(initial = 0)
    val antiguosTienda by remember { informesVm.nuevosTienda() }.collectAsState(initial = 0)
    val mensuales by remember { informesVm.informesConsolidadosNuevos() }.collectAsState(initial = 0)
    val indicadores by remember { vm.indicadores() }.collectAsState(initial = emptyList())
    val solicitudes by remember { vm.solicitudesGerencia() }.collectAsState(initial = emptyList())
    val alertas by remember { vm.alertas() }.collectAsState(initial = emptyList())
    val pendientes = antiguosBodega + antiguosTienda + mensuales
    ColumnaOperador {
        TituloSeccion("GERENCIA", "Resumen general", "Resultados e informes de las sucursales")
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TarjetaResumen("Sucursales", indicadores.size.toString(), Modifier.weight(1f))
            TarjetaResumen("Informes nuevos", pendientes.toString(), Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TarjetaResumen("Unidades bodega", indicadores.sumOf { it.unidadesBodega }.toString(), Modifier.weight(1f))
            TarjetaResumen("Unidades tienda", indicadores.sumOf { it.unidadesTienda }.toString(), Modifier.weight(1f))
        }
        if (pendientes > 0) FilaOpcion("Informes recibidos", "$pendientes por revisar", Icons.Default.Description,
            onClick = { ir(Destinos.GerenciaReportes.ruta) })
        if (solicitudes.any { it.estado == "EN_GERENCIA" }) FilaOpcion("Solicitudes de abastecimiento",
            "Pendientes de aprobación o rechazo", Icons.Default.AssignmentTurnedIn,
            onClick = { ir(Destinos.GerenciaSolicitudes.ruta) })
        if (alertas.isNotEmpty()) FilaOpcion("Alertas de stock", "${alertas.size} productos en el mínimo o por debajo",
            Icons.Default.Warning, onClick = { ir(Destinos.GerenciaReportes.ruta) })
        Text("Resultados por sucursal", fontWeight = FontWeight.Bold)
        indicadores.forEach { sede -> TarjetaPanel {
            Text(sede.nombre, fontWeight = FontWeight.Bold)
            Text("Stock en bodega: ${sede.unidadesBodega} · Stock en tienda: ${sede.unidadesTienda}")
            Text("Incidencias pendientes: ${sede.incidencias}")
        } }
        BotonAccion("Consultar informes", outlined = true, onClick = { ir(Destinos.GerenciaReportes.ruta) })
    }
}

private fun resumenBodega(detalle: List<DetalleInformeInventario>): List<ResumenProductoInventario> =
    resumirProductosInventario(detalle.map { FilaResumenInventario(it.codigo, it.producto, it.cantidadSistema, it.cantidadContada) })

private fun resumenTienda(detalle: List<LineaInventarioTienda>): List<ResumenProductoInventario> =
    resumirProductosInventario(detalle.map {
        FilaResumenInventario(it.codigoProductoCierre ?: "#${it.productoId}",
            it.nombreProductoCierre ?: "Producto #${it.productoId}", it.stockInicio, it.cantidadContada)
    })

// Componente visual de resumen productos gerencia.
@Composable
private fun ResumenProductosGerencia(productos: List<ResumenProductoInventario>) {
    val totalSistema = productos.sumOf { it.sistema }
    val totalFisico = productos.sumOf { it.fisico }
    val diferencias = productos.count { it.lineasConDiferencia > 0 }
    val sinContar = productos.sumOf { it.registrosSinContar }
    Text("Productos: ${productos.size} · Unidades contadas: $totalFisico · Diferencias: $diferencias",
        fontWeight = FontWeight.SemiBold)
    Text("Unidades registradas al iniciar los conteos: $totalSistema", style = MaterialTheme.typography.bodySmall)
    if (sinContar > 0) Text("$sinContar registros pendientes de conteo",
        color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
    if (productos.isEmpty()) Text("Sin productos registrados")
    productos.forEach { producto ->
        Text("${producto.codigo} · ${producto.producto}", fontWeight = FontWeight.SemiBold)
        Text("Registrado: ${producto.sistema} · Contado: ${producto.fisico} · Diferencia: ${producto.diferencia}" +
            " · Faltantes: ${producto.faltantesBrutos} · Sobrantes: ${producto.sobrantesBrutos} · Líneas con diferencia: ${producto.lineasConDiferencia}" +
            if (producto.registrosSinContar > 0) " · Registros sin contar: ${producto.registrosSinContar}" else "",
            style = MaterialTheme.typography.bodySmall)
    }
}

// Componente visual de informe mensual gerencia.
@Composable
private fun InformeMensualGerencia(base: OperadorViewModel, vm: InformeGerenciaViewModel, campania: CampaniaMensual, nombreSucursal: String) {
    // Datos observados por esta pantalla.
    val productos by remember(campania.id) { vm.resumenCampania(campania.id) }.collectAsState(initial = emptyList())
    var mostrar by remember(campania.id) { mutableStateOf(false) }
    var procesando by remember(campania.id) { mutableStateOf(false) }
    var mensaje by remember(campania.id) { mutableStateOf("") }
    TarjetaPanel {
        Text("${campania.mes} · ${if (campania.tipo == "BODEGA") "Bodega" else "Tienda"}", fontWeight = FontWeight.Bold)
        Text("$nombreSucursal · Finalizado: ${campania.fechaFinal?.let(::formatoFecha) ?: ""}")
        EtiquetaEstado(campania.revision, success = campania.revision == "REVISADO")
        Text("${productos.size} productos · ${productos.sumOf { it.fisico }} unidades contadas · " +
            "${productos.count { it.lineasConDiferencia > 0 }} productos con diferencias")
        BotonAccion(if (mostrar) "Ocultar informe" else "Ver resultados", outlined = true,
            onClick = { mostrar = !mostrar })
        if (mostrar) {
            ResumenProductosGerencia(productos)
        }
        if (campania.revision == "NUEVO") BotonAccion("Marcar como revisado", enabled = !procesando, onClick = {
            val gerente = base.sesion
            if (gerente == null) mensaje = "Sesión no disponible"
            else {
                procesando = true
                vm.revisarCampania(gerente, campania.id) { _, texto -> procesando = false; mensaje = texto }
            }
        })
        if (mensaje.isNotBlank()) Text(mensaje)
    }
}

// Componente visual de informe anterior bodega gerencia.
@Composable
private fun InformeAnteriorBodegaGerencia(base: OperadorViewModel, vm: InformeGerenciaViewModel, informe: InformeInventario) {
    // Datos observados por esta pantalla.
    val detalle by remember(informe.id) { vm.detalles(informe.id) }.collectAsState(initial = emptyList())
    val productos = remember(detalle) { resumenBodega(detalle) }
    var mostrar by remember(informe.id) { mutableStateOf(false) }
    var procesando by remember(informe.id) { mutableStateOf(false) }
    var mensaje by remember(informe.id) { mutableStateOf("") }
    TarjetaPanel {
        Text("${informe.nombre} · ${informe.sucursalNombre}", fontWeight = FontWeight.Bold)
        Text("Bodega · Cerrado ${formatoFecha(informe.fechaCierre)}")
        EtiquetaEstado(informe.estadoRevision, success = informe.estadoRevision == "REVISADO")
        Text("${informe.productosContados} registros contados · ${informe.lineasConDiferencia} diferencias")
        BotonAccion(if (mostrar) "Ocultar informe" else "Ver resultados", outlined = true, onClick = { mostrar = !mostrar })
        if (mostrar) ResumenProductosGerencia(productos)
        if (informe.estadoRevision == "NUEVO") BotonAccion("Marcar como revisado", enabled = !procesando, onClick = {
            val gerente = base.sesion
            if (gerente == null) mensaje = "Sesión no disponible"
            else {
                procesando = true
                vm.marcarRevisado(gerente, informe.id) { _, texto -> procesando = false; mensaje = texto }
            }
        })
        if (mensaje.isNotBlank()) Text(mensaje)
    }
}

// Componente visual de informe anterior tienda gerencia.
@Composable
private fun InformeAnteriorTiendaGerencia(base: OperadorViewModel, vm: InformeGerenciaViewModel, informe: InventarioTienda, nombreSucursal: String) {
    // Datos observados por esta pantalla.
    val detalle by remember(informe.id) { vm.detalleTienda(informe.id) }.collectAsState(initial = emptyList())
    val productos = remember(detalle) { resumenTienda(detalle) }
    var mostrar by remember(informe.id) { mutableStateOf(false) }
    var procesando by remember(informe.id) { mutableStateOf(false) }
    var mensaje by remember(informe.id) { mutableStateOf("") }
    TarjetaPanel {
        Text(informe.nombre, fontWeight = FontWeight.Bold)
        Text("$nombreSucursal · Tienda · Cerrado ${informe.fechaCierre?.let(::formatoFecha) ?: ""}")
        EtiquetaEstado(informe.estadoRevision, success = informe.estadoRevision == "REVISADO")
        Text("${productos.size} productos · ${productos.count { it.lineasConDiferencia > 0 }} diferencias")
        BotonAccion(if (mostrar) "Ocultar informe" else "Ver resultados", outlined = true, onClick = { mostrar = !mostrar })
        if (mostrar) ResumenProductosGerencia(productos)
        if (informe.estadoRevision == "NUEVO") BotonAccion("Marcar como revisado", enabled = !procesando, onClick = {
            val gerente = base.sesion
            if (gerente == null) mensaje = "Sesión no disponible"
            else {
                procesando = true
                vm.revisarTienda(gerente, informe.id) { _, texto -> procesando = false; mensaje = texto }
            }
        })
        if (mensaje.isNotBlank()) Text(mensaje)
    }
}

// Muestra la pantalla de reportes gerencia.
@Composable
fun PantallaReportesGerencia(base: OperadorViewModel, vm: AbastecimientoExternoViewModel, informesVm: InformeGerenciaViewModel) {
    val context = LocalContext.current
    // Datos observados por esta pantalla.
    val campanias by remember { informesVm.campaniasFinalizadas() }.collectAsState(initial = emptyList())
    val anterioresBodega by remember { informesVm.informes() }.collectAsState(initial = emptyList())
    val anterioresTienda by remember { informesVm.informesTienda() }.collectAsState(initial = emptyList())
    val sucursales by remember { vm.indicadores() }.collectAsState(initial = emptyList())
    val faltantes by remember { vm.alertas() }.collectAsState(initial = emptyList())
    val solicitudes by remember { vm.solicitudesGerencia() }.collectAsState(initial = emptyList())
    val movimientosBodega by remember { vm.movimientosBodega() }.collectAsState(initial = emptyList())
    val movimientosTienda by remember { vm.movimientosTienda() }.collectAsState(initial = emptyList())
    var filtro by remember { mutableStateOf("Todos") }
    var sucursalFiltro by remember { mutableStateOf<Long?>(null) }
    var mensaje by remember { mutableStateOf("") }
    var exportando by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    var pdfReciente by remember { mutableStateOf<android.net.Uri?>(null) }
    val exportar = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        if (uri == null) mensaje = "Exportación cancelada"
        else if (!exportando) scope.launch {
            exportando = true
            try {
                val gerente = base.sesion ?: error("La sesión terminó")
                val filtroSucursal = sucursalFiltro
                val listaSucursales = sucursales.filter { filtroSucursal == null || it.sucursalId == filtroSucursal }.toList()
                val listaSolicitudes = solicitudes.filter { filtroSucursal == null || it.sucursalId == filtroSucursal }.toList()
                val actualesB = anterioresBodega.filter { filtroSucursal == null || it.sucursalId == filtroSucursal }.toList()
                val actualesT = anterioresTienda.filter { filtroSucursal == null || it.sucursalId == filtroSucursal }.toList()
                val alertasVisibles = faltantes.filter { filtroSucursal == null || it.sucursalId == filtroSucursal }.toList()
                withContext(Dispatchers.IO) {
                    val historico = vm.historialCompleto(gerente)
                    val (todasCampanias, resumenes) = informesVm.datosCampaniasParaExportar(gerente)
                    val campaniasCompletas = todasCampanias.filter {
                        it.estado == "FINALIZADA" && (filtroSucursal == null || it.sucursalId == filtroSucursal)
                    }
                    val pdf = InformePdf("Informe consolidado", "Inventarios e indicadores resumidos por sucursal", "Gerencia")
                    fun sede(id: Long) = listaSucursales.firstOrNull { it.sucursalId == id }?.nombre ?: "Sucursal #$id"
                    pdf.metricas(listOf(
                        "Sucursales" to pdf.numero(listaSucursales.size),
                        "Campañas finalizadas" to pdf.numero(campaniasCompletas.size),
                        "Informes históricos" to pdf.numero(actualesB.size + actualesT.size),
                        "Incidencias pendientes" to pdf.numero(listaSucursales.sumOf { it.incidencias }),
                        "Alertas de stock" to pdf.numero(alertasVisibles.size),
                        "Abastecimientos registrados" to pdf.numero(listaSolicitudes.size)
                    ))
                    pdf.barras("Unidades de bodega por sucursal", listaSucursales.map { it.nombre to it.unidadesBodega })
                    pdf.barras("Unidades de tienda por sucursal", listaSucursales.map { it.nombre to it.unidadesTienda })
                    pdf.tabla("Situación por sucursal", listOf(
                        ColumnaInforme("Sucursal", 2.1f), ColumnaInforme("Bodegas", .9f),
                        ColumnaInforme("Uds. bodega", 1.25f), ColumnaInforme("Uds. tienda", 1.25f),
                        ColumnaInforme("Incidencias", 1f)),
                        listaSucursales.map { listOf(it.nombre, it.bodegas.toString(), pdf.numero(it.unidadesBodega),
                            pdf.numero(it.unidadesTienda), it.incidencias.toString()) })
                    pdf.tabla("Campañas mensuales finalizadas", listOf(
                        ColumnaInforme("Sucursal / mes", 2f), ColumnaInforme("Ámbito", 1f),
                        ColumnaInforme("Contadas", 1f), ColumnaInforme("Faltantes", 1f),
                        ColumnaInforme("Sobrantes", 1f), ColumnaInforme("Sin contar", 1f)),
                        campaniasCompletas.map { c ->
                            val ps = resumenes[c.id].orEmpty()
                            listOf("${sede(c.sucursalId)} · ${c.mes}", c.tipo,
                                pdf.numero(ps.sumOf { it.fisico }), pdf.numero(ps.sumOf { it.faltantesBrutos }),
                                pdf.numero(ps.sumOf { it.sobrantesBrutos }), pdf.numero(ps.sumOf { it.registrosSinContar }))
                        })
                    pdf.tabla("Inventarios históricos de bodega", listOf(
                        ColumnaInforme("Sucursal / informe", 2.7f), ColumnaInforme("Contadas", 1f),
                        ColumnaInforme("Diferencias", 1f), ColumnaInforme("Revisión", 1.1f)),
                        actualesB.map { i ->
                            val ps = resumenBodega(historico.detallesBodega.filter { it.informeId == i.id })
                            listOf("${i.sucursalNombre} · ${i.nombre}", pdf.numero(ps.sumOf { it.fisico }),
                                pdf.numero(ps.sumOf { it.lineasConDiferencia }), i.estadoRevision)
                        })
                    pdf.tabla("Inventarios históricos de tienda", listOf(
                        ColumnaInforme("Sucursal / informe", 2.7f), ColumnaInforme("Contadas", 1f),
                        ColumnaInforme("Diferencias", 1f), ColumnaInforme("Revisión", 1.1f)),
                        actualesT.map { i ->
                            val ps = resumenTienda(historico.detallesTienda.filter { it.inventarioId == i.id })
                            listOf("${sede(i.sucursalId)} · ${i.nombre}", pdf.numero(ps.sumOf { it.fisico }),
                                pdf.numero(ps.sumOf { it.lineasConDiferencia }), i.estadoRevision)
                        })
                    pdf.tabla("Solicitudes externas por estado", listOf(
                        ColumnaInforme("Sucursal", 2.4f), ColumnaInforme("Estado", 1.9f), ColumnaInforme("Solicitudes", 1.1f)),
                        listaSolicitudes.groupBy { it.sucursalId to it.estado }.map { (key, arr) ->
                            listOf(sede(key.first), key.second, arr.size.toString())
                        })
                    pdf.guardar(context, uri)
                }
                pdfReciente = uri
                mensaje = "Informe gerencial PDF guardado correctamente"
            } catch (e: Exception) { mensaje = mensajeErrorUsuario(e, "No se pudo exportar el PDF") }
            finally { exportando = false }
        }
    }

    ColumnaOperador {
        TituloSeccion("GERENCIA", "Informes", "Resultados consolidados, diferencias y alertas por sucursal")
        BotonAccion(if (exportando) "Generando PDF…" else "Descargar informe consolidado (PDF)",
            icon = Icons.Default.FileDownload, enabled = !exportando,
            onClick = { exportar.launch("boxview_informe_consolidado.pdf") })
        pdfReciente?.let { archivo -> BotonAccion("Abrir último PDF", outlined = true,
            onClick = { abrirInformePdf(context, archivo)?.let { mensaje = it } }) }
        if (mensaje.isNotBlank()) Text(mensaje)
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Todos", "Nuevos", "Revisados").forEach { opcion ->
                FilterChip(selected = filtro == opcion, onClick = { filtro = opcion }, label = { Text(opcion) })
            }
        }
        SelectorSimple("Sucursal", listOf(0L to "Todas") + sucursales.map { it.sucursalId to it.nombre },
            sucursalFiltro ?: 0L) { sucursalFiltro = it.takeUnless { id -> id == 0L } }

        Text("Informes mensuales finalizados", fontWeight = FontWeight.Bold)
        val informesMensuales = campanias.filter { (sucursalFiltro == null || it.sucursalId == sucursalFiltro) &&
            (filtro == "Todos" || (filtro == "Nuevos" && it.revision == "NUEVO") ||
                (filtro == "Revisados" && it.revision == "REVISADO")) }
        if (informesMensuales.isEmpty()) Text("No hay informes mensuales en este filtro")
        informesMensuales.forEach { campania ->
            InformeMensualGerencia(base, informesVm, campania,
                sucursales.firstOrNull { it.sucursalId == campania.sucursalId }?.nombre ?: "Sucursal #${campania.sucursalId}")
        }

        Text("Informes históricos anteriores", fontWeight = FontWeight.Bold)
        val bodegas = anterioresBodega.filter { (sucursalFiltro == null || it.sucursalId == sucursalFiltro) &&
            (filtro == "Todos" || (filtro == "Nuevos" && it.estadoRevision == "NUEVO") ||
                (filtro == "Revisados" && it.estadoRevision == "REVISADO")) }
        val tiendas = anterioresTienda.filter { (sucursalFiltro == null || it.sucursalId == sucursalFiltro) &&
            (filtro == "Todos" || (filtro == "Nuevos" && it.estadoRevision == "NUEVO") ||
                (filtro == "Revisados" && it.estadoRevision == "REVISADO")) }
        if (bodegas.isEmpty() && tiendas.isEmpty()) Text("No hay informes anteriores en este filtro")
        bodegas.forEach { InformeAnteriorBodegaGerencia(base, informesVm, it) }
        tiendas.forEach { inventario ->
            InformeAnteriorTiendaGerencia(base, informesVm, inventario,
                sucursales.firstOrNull { it.sucursalId == inventario.sucursalId }?.nombre ?: "Sucursal #${inventario.sucursalId}")
        }

        HorizontalDivider()
        Text("Resumen de existencias", fontWeight = FontWeight.Bold)
        sucursales.filter { sucursalFiltro == null || it.sucursalId == sucursalFiltro }.forEach { sede -> TarjetaPanel {
            Text(sede.nombre, fontWeight = FontWeight.Bold)
            Text("Bodega: ${sede.unidadesBodega} unidades · Tienda: ${sede.unidadesTienda} unidades")
            Text("Incidencias pendientes: ${sede.incidencias}")
        } }
        Text("Alertas de stock mínimo (${faltantes.count { sucursalFiltro == null || it.sucursalId == sucursalFiltro }})",
            fontWeight = FontWeight.Bold)
        val alertasVisibles = faltantes.filter { sucursalFiltro == null || it.sucursalId == sucursalFiltro }
        if (alertasVisibles.size > 60) Text("Mostrando 60 alertas. El PDF incluye el total.",
            style = MaterialTheme.typography.bodySmall)
        alertasVisibles.take(60).forEach { alerta ->
            Text("${alerta.sucursal} · ${alerta.codigo} · ${alerta.nombre}: " +
                "${alerta.disponible} unidades (mínimo ${alerta.minimo})", style = MaterialTheme.typography.bodySmall)
        }
        Text("Actividad reciente", fontWeight = FontWeight.Bold)
        Text("Movimientos bodega: ${movimientosBodega.size} recientes · Movimientos tienda: ${movimientosTienda.size} recientes",
            style = MaterialTheme.typography.bodySmall)
    }
}
