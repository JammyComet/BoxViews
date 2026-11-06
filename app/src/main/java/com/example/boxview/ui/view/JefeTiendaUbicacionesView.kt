package com.example.boxview.ui.view

// Pantallas: Ubicaciones y estantes de tienda.
import androidx.compose.runtime.*
import com.example.boxview.modelo.*
import com.example.boxview.ui.components.*
import com.example.boxview.viewmodel.JefeTiendaViewModel
import com.example.boxview.viewmodel.OperadorViewModel
import com.example.boxview.viewmodel.TrabajadorTiendaViewModel

// Muestra la pantalla de ubicaciones jefe tienda.
@Composable
fun PantallaUbicacionesJefeTienda(base: OperadorViewModel, tienda: TrabajadorTiendaViewModel,
                                  jefe: JefeTiendaViewModel) {
    val sid = tienda.tiendaActiva?.id ?: return
    val usuario = base.sesion ?: return
    // Datos observados por esta pantalla.
    val productos by base.productos.collectAsState(initial = emptyList())
    val ubicaciones by remember(sid) { tienda.todasLasPosiciones() }.collectAsState(initial = emptyList())
    val stock by remember(sid) { tienda.stockPorUbicacion() }.collectAsState(initial = emptyList())
    val filas = ubicaciones.map { lugar ->
        UbicacionFila(lugar.id, lugar.etiqueta(), rutaTienda(lugar, ubicaciones), lugar.tipo,
            lugar.padreId, lugar.zona, lugar.categoria, lugar.activa,
            stock.filter { it.posicionId == lugar.id }.sumOf { it.cantidad },
            protegida = lugar.zona == "RECEPCION")
    }
    key(sid) {
        GestorUbicaciones("Ubicaciones de tienda", filas, productos.map { it.categoria }.distinct(), true,
            crear = { nombre, tipo, padre, zona, categoria, cb ->
                jefe.crearUbicacionJerarquica(usuario, sid, nombre, tipo, padre, zona, categoria, cb)
            },
            renombrar = { id, nombre, cb -> jefe.renombrarUbicacionTienda(usuario, sid, id, nombre, cb) },
            cambiarEstado = { id, activar, cb -> jefe.cambiarUbicacion(usuario, sid, id, activar, cb) })
    }
}
