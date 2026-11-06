package com.example.boxview.ui.view

// Pantallas: Exportación de informes operativos en PDF.
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.boxview.modelo.*
import com.example.boxview.ui.components.*
import com.example.boxview.viewmodel.AdministradorViewModel
import com.example.boxview.viewmodel.AbastecimientoExternoViewModel
import com.example.boxview.viewmodel.InformeGerenciaViewModel

// Informes de operación restringidos a la sucursal actualmente administrada.
// Arma y guarda informes operativos PDF.
@Composable
fun PantallaInformesOperativosAdmin(base: com.example.boxview.viewmodel.OperadorViewModel,
                                   admin: AdministradorViewModel, externo: AbastecimientoExternoViewModel,
                                   informes: InformeGerenciaViewModel) {
    val sede = admin.sucursalActiva ?: return
    val sid = sede.id
    val usuario = base.sesion ?: return
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var exportando by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }
    var revisando by remember { mutableStateOf(false) }
    fun revisar(tipo: String, id: Long) {
        if (!revisando) {
            revisando = true
            informes.revisarAdmin(usuario, sid, tipo, id) { _, msg -> mensaje = msg; revisando = false }
        }
    }
    // Datos observados por esta pantalla.
    val indicadores by remember { externo.indicadores() }.collectAsState(initial = emptyList())
    val alertas by remember { externo.alertas() }.collectAsState(initial = emptyList())
    val campanias by remember { informes.campaniasFinalizadas() }.collectAsState(initial = emptyList())
    val historicosBodega by remember { informes.informes() }.collectAsState(initial = emptyList())
    val historicosTienda by remember { informes.informesTienda() }.collectAsState(initial = emptyList())
    val bodegas by remember(sid) { admin.bodegas(sid) }.collectAsState(initial = emptyList())
    val movimientosBodega by remember { externo.movimientosBodega() }.collectAsState(initial = emptyList())
    val movimientosTienda by remember { externo.movimientosTienda() }.collectAsState(initial = emptyList())
    val sedeDatos = indicadores.firstOrNull { it.sucursalId == sid }
    val faltantes = alertas.filter { it.sucursalId == sid }
    val mensuales = campanias.filter { it.sucursalId == sid }
    var pdfReciente by remember { mutableStateOf<android.net.Uri?>(null) }
    val exportar = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        if (uri != null && !exportando) scope.launch {
            exportando = true
            try {
                val sedeActual = sede
                val indicadoresActuales = indicadores.firstOrNull { it.sucursalId == sid }
                val alertasActuales = alertas.filter { it.sucursalId == sid }.toList()
                val mensualesActuales = campanias.filter { it.sucursalId == sid }.toList()
                val historicosB = historicosBodega.filter { it.sucursalId == sid }.toList()
                val historicosT = historicosTienda.filter { it.sucursalId == sid }.toList()
                val ids = bodegas.map { it.id }.toSet()
                val movimientosB = movimientosBodega.filter { it.bodegaId in ids }.toList()
                val movimientosT = movimientosTienda.filter { it.sucursalId == sid }.toList()
                withContext(Dispatchers.IO) {
                    val pdf = InformePdf("Informe de operación", "${sedeActual.nombre} · stock, alertas e inventarios", "Administración")
                    pdf.metricas(listOf(
                        "Unidades en bodega" to pdf.numero(indicadoresActuales?.unidadesBodega ?: 0),
                        "Unidades en tienda" to pdf.numero(indicadoresActuales?.unidadesTienda ?: 0),
                        "Alertas de stock crítico" to pdf.numero(alertasActuales.size),
                        "Incidencias pendientes" to pdf.numero(indicadoresActuales?.incidencias ?: 0),
                        "Inventarios finalizados" to pdf.numero(mensualesActuales.size),
                        "Movimientos registrados" to pdf.numero(movimientosB.size + movimientosT.size)
                    ))
                    pdf.barras("Movimientos por ámbito (unidades)", listOf(
                        "Bodega" to movimientosB.sumOf { it.cantidad }.toLong(),
                        "Tienda" to movimientosT.sumOf { it.cantidad }.toLong()))
                    pdf.tabla("Productos bajo mínimo", listOf(
                        ColumnaInforme("Código", 1f), ColumnaInforme("Producto", 2.25f),
                        ColumnaInforme("Actual", 1f), ColumnaInforme("Mínimo", 1f), ColumnaInforme("Faltan", 1f)),
                        alertasActuales.sortedByDescending { it.minimo - it.disponible }.map { a -> listOf(
                            a.codigo, a.nombre, a.disponible.toString(), a.minimo.toString(),
                            (a.minimo - a.disponible).coerceAtLeast(0).toString()) }, "No hay productos bajo el mínimo en esta sucursal.")
                    pdf.tabla("Inventarios mensuales finalizados", listOf(
                        ColumnaInforme("Mes", 1.6f), ColumnaInforme("Ámbito", 1.1f),
                        ColumnaInforme("Finalización", 1.6f), ColumnaInforme("Revisión", 1.1f)),
                        mensualesActuales.map { c -> listOf(c.mes, c.tipo,
                            c.fechaFinal?.let { java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale("es", "CL")).format(java.util.Date(it)) } ?: "Sin fecha", c.revision) })
                    pdf.tabla("Histórico de conteos de bodega", listOf(
                        ColumnaInforme("Informe", 2.3f), ColumnaInforme("Registros", 1f),
                        ColumnaInforme("Diferencias", 1f), ColumnaInforme("Revisión", 1.1f)),
                        historicosB.map { listOf(it.nombre, it.productosContados.toString(), it.lineasConDiferencia.toString(), it.estadoRevision) })
                    pdf.tabla("Histórico de conteos de tienda", listOf(
                        ColumnaInforme("Informe", 2.4f), ColumnaInforme("Fecha", 1.5f), ColumnaInforme("Revisión", 1.1f)),
                        historicosT.map { listOf(it.nombre, it.fechaCierre?.let { f ->
                            java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale("es", "CL")).format(java.util.Date(f))
                        } ?: "Sin fecha", it.estadoRevision) })
                    pdf.tabla("Tipos de movimiento en bodega", listOf(
                        ColumnaInforme("Tipo", 2.6f), ColumnaInforme("Operaciones", 1.1f), ColumnaInforme("Unidades", 1.2f)),
                        movimientosB.groupBy { it.tipo }.map { (tipo, lista) -> listOf(tipo, lista.size.toString(), pdf.numero(lista.sumOf { it.cantidad })) })
                    pdf.guardar(context, uri)
                }
                pdfReciente = uri
                mensaje = "Informe operativo PDF guardado correctamente"
            } catch (e: Exception) { mensaje = mensajeErrorUsuario(e, "No se pudo exportar el PDF") }
            finally { exportando = false }
        }
    }
    ColumnaOperador {
        TituloSeccion("ADMINISTRACIÓN", "Informes operativos", "Stock, mínimos e inventarios de ${sede.nombre}")
        BotonAccion(if (exportando) "Generando PDF…" else "Descargar informe operativo (PDF)", enabled = !exportando,
            onClick = { exportar.launch("boxview_informe_operativo_${sid}.pdf") })
        pdfReciente?.let { archivo -> BotonAccion("Abrir último PDF", outlined = true,
            onClick = { abrirInformePdf(context, archivo)?.let { mensaje = it } }) }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TarjetaResumen("Bodega", "${sedeDatos?.unidadesBodega ?: 0} uds.", Modifier.weight(1f))
            TarjetaResumen("Tienda", "${sedeDatos?.unidadesTienda ?: 0} uds.", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TarjetaResumen("Stock crítico", "${faltantes.size}", Modifier.weight(1f))
            TarjetaResumen("Incidencias", "${sedeDatos?.incidencias ?: 0}", Modifier.weight(1f))
        }
        if (mensaje.isNotBlank()) Text(mensaje)
        Text("Productos en mínimo o por debajo", fontWeight = FontWeight.Bold)
        if (faltantes.isEmpty()) Text("No hay alertas de mínimos")
        faltantes.forEach { a -> Text("${a.codigo} · ${a.nombre}: ${a.disponible} / mínimo ${a.minimo}",
            style = MaterialTheme.typography.bodySmall) }
        HorizontalDivider()
        Text("Campañas mensuales finalizadas", fontWeight = FontWeight.Bold)
        if (mensuales.isEmpty()) Text("No hay campañas finalizadas")
        mensuales.forEach { c ->
            var abierto by remember(c.id) { mutableStateOf(false) }
            TarjetaPanel {
                Text("${c.mes} · ${c.tipo} · ${c.revision}", fontWeight = FontWeight.SemiBold)
                BotonAccion(if (abierto) "Ocultar resultado" else "Ver resultado por producto", outlined = true,
                    onClick = { abierto = !abierto })
                if (c.revision == "NUEVO") BotonAccion("Marcar revisado", enabled = !revisando, onClick = {
                    revisar("MENSUAL", c.id)
                })
                if (abierto) {
                    val detalle by remember(c.id) { informes.resumenCampania(c.id) }.collectAsState(initial = emptyList())
                    detalle.forEach { d -> Text("${d.codigo} · ${d.producto}: sistema ${d.sistema}, contado ${d.fisico}, " +
                        "faltantes ${d.faltantesBrutos}, sobrantes ${d.sobrantesBrutos}, pendientes ${d.registrosSinContar}",
                        style = MaterialTheme.typography.bodySmall) }
                }
            }
        }
        Text("Historial de inventarios", fontWeight = FontWeight.Bold)
        historicosBodega.filter { it.sucursalId == sid }.forEach { i -> TarjetaPanel {
            Text("Bodega · ${i.nombre} · ${i.productosContados} registros · ${i.lineasConDiferencia} diferencias")
            if (i.estadoRevision == "NUEVO") BotonAccion("Marcar revisado", enabled = !revisando,
                onClick = { revisar("BODEGA", i.id) })
        } }
        historicosTienda.filter { it.sucursalId == sid }.forEach { i -> TarjetaPanel {
            Text("Tienda · ${i.nombre} · ${i.estadoRevision}")
            if (i.estadoRevision == "NUEVO") BotonAccion("Marcar revisado", enabled = !revisando,
                onClick = { revisar("TIENDA", i.id) })
        } }
        HorizontalDivider()
        val idsBodega = bodegas.map { it.id }.toSet()
        val mb = movimientosBodega.filter { it.bodegaId in idsBodega }
        val mt = movimientosTienda.filter { it.sucursalId == sid }
        Text("Actividad reciente", fontWeight = FontWeight.Bold)
        Text("Bodega: ${mb.size} movimientos recientes · Tienda: ${mt.size} movimientos recientes",
            style = MaterialTheme.typography.bodySmall)
    }
}
