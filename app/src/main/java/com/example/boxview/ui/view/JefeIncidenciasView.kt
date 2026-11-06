package com.example.boxview.ui.view

// Pantallas: Incidencias de bodega.
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import com.example.boxview.modelo.*
import com.example.boxview.ui.components.*
import com.example.boxview.viewmodel.JefeBodegaViewModel
import com.example.boxview.viewmodel.OperadorViewModel

// Muestra la pantalla de incidencias jefe.
@Composable
fun PantallaIncidenciasJefe(base: OperadorViewModel, jefe: JefeBodegaViewModel) {
    val bodega = base.bodegaActiva?.id ?: return
    val usuario = base.sesion ?: return
    // Datos observados por esta pantalla.
    val incidencias by remember(bodega) { jefe.incidencias(bodega) }.collectAsState(initial = emptyList())
    val cajas by remember(bodega) { jefe.cajasDestino(bodega) }.collectAsState(initial = emptyList())
    val productos by base.productos.collectAsState(initial = emptyList())
    val observaciones = remember(bodega) { mutableStateMapOf<Long, String>() }
    var trabajando by remember { mutableStateOf<Long?>(null) }
    var mensaje by remember { mutableStateOf("") }
    ColumnaOperador {
        TituloSeccion("JEFATURA", "Incidencias", "Recepciones con diferencias en ${base.bodegaActiva?.nombre}")
        if (mensaje.isNotEmpty()) TarjetaPanel { Text(mensaje) }
        if (incidencias.isEmpty()) TarjetaPanel { Text("No hay incidencias registradas") }
        incidencias.forEach { i ->
            TarjetaPanel {
                val caja = cajas.firstOrNull { it.id == i.cajaId }
                Text("${caja?.codigo ?: "Caja ${i.cajaId}"} · ${productos.firstOrNull { it.id == i.productoId }?.nombre ?: "Producto"}",
                    fontWeight = FontWeight.Bold)
                EtiquetaEstado(i.estado, warning = i.estado == "PENDIENTE", success = i.estado == "REVISADA")
                Text("Esperado: ${i.esperado} · Contado: ${i.contado} · Diferencia: ${i.contado - i.esperado}")
                Text("Recibido: ${formatoFecha(i.fecha)}", style = MaterialTheme.typography.bodySmall)
                val revisiones by remember(i.cajaId) { jefe.revisionesCaja(i.cajaId) }.collectAsState(initial = emptyList())
                revisiones.filter { it.observacion.startsWith("Incidencia ${i.id}:") }.forEach { e ->
                    Text("Revisión: ${e.observacion.substringAfter(": ")}", style = MaterialTheme.typography.bodySmall)
                }
                if (i.estado == "PENDIENTE") {
                    CampoFormulario("Observación de revisión", observaciones[i.id] ?: "", {
                        observaciones[i.id] = it
                    })
                    BotonAccion("Registrar revisión de incidencia", enabled = trabajando == null &&
                        (observaciones[i.id]?.trim()?.length ?: 0) >= 5, onClick = {
                        trabajando = i.id
                        jefe.resolverIncidencia(usuario, bodega, i.id, observaciones[i.id].orEmpty()) { _, msg ->
                            mensaje = msg; trabajando = null
                        }
                    })
                }
            }
        }
    }
}
