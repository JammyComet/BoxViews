package com.example.boxview.ui.view

// Pantallas: Ubicaciones y estantes de la bodega.
import androidx.compose.runtime.*
import com.example.boxview.modelo.*
import com.example.boxview.ui.components.*
import com.example.boxview.viewmodel.JefeBodegaViewModel
import com.example.boxview.viewmodel.OperadorViewModel

// Muestra la pantalla de ubicaciones jefe bodega.
@Composable
fun PantallaUbicacionesJefeBodega(base: OperadorViewModel, jefe: JefeBodegaViewModel) {
    val bodegaId = base.bodegaActiva?.id ?: return
    val usuario = base.sesion ?: return
    // Datos observados por esta pantalla.
    val ubicaciones by remember(bodegaId) { jefe.ubicaciones(bodegaId) }.collectAsState(initial = emptyList())
    val stock by remember(bodegaId) { base.existencias() }.collectAsState(initial = emptyList())
    val filas = ubicaciones.map { lugar ->
        UbicacionFila(lugar.id, lugar.etiqueta(), rutaBodega(lugar, ubicaciones), lugar.tipo,
            lugar.padreId, "BODEGA", "General", lugar.activa,
            stock.filter { it.posicionId == lugar.id }.sumOf { it.cantidad })
    }
    key(bodegaId) {
        GestorUbicaciones("Ubicaciones de bodega", filas, emptyList(), false,
            crear = { nombre, tipo, padre, _, _, cb ->
                jefe.crearUbicacionBodega(usuario, bodegaId, nombre, tipo, padre, cb)
            },
            renombrar = { id, nombre, cb -> jefe.renombrarUbicacionBodega(usuario, bodegaId, id, nombre, cb) },
            cambiarEstado = { id, activar, cb -> jefe.activarUbicacionBodega(usuario, bodegaId, id, activar, cb) })
    }
}
