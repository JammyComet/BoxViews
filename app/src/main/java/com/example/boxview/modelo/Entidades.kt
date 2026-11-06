package com.example.boxview.modelo

// Modelo: Tablas principales: usuarios, productos, sucursales y stock.
import androidx.room.*

@Entity(tableName = "usuarios", indices = [Index(value = ["correo"], unique = true), Index(value = ["rut"], unique = true)])
data class Usuario(@PrimaryKey val id: Long, val nombre: String, val correo: String,
    val claveHash: String, val claveSal: String, val rol: String, val activo: Boolean = true,
    val rut: String? = null,
    @ColumnInfo(defaultValue = "NULL") val sucursalResponsableId: Long? = null)

@Entity(tableName = "sucursales")
data class Sucursal(@PrimaryKey val id: Long, val nombre: String)

@Entity(tableName = "bodegas")
data class Bodega(@PrimaryKey val id: Long, val sucursalId: Long, val nombre: String,
    val activa: Boolean = true)

@Entity(tableName = "bodega_sucursal", primaryKeys = ["bodegaId", "sucursalId"])
data class BodegaSucursal(val bodegaId: Long, val sucursalId: Long)

// stock de tienda
@Entity(tableName = "existencias_tienda", indices = [Index(value = ["sucursalId", "productoId"], unique = true)])
data class ExistenciaTienda(@PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sucursalId: Long, val productoId: Long, val cantidad: Int)

@Entity(tableName = "usuario_bodega", primaryKeys = ["usuarioId", "bodegaId"])
data class UsuarioBodega(val usuarioId: Long, val bodegaId: Long)

@Entity(tableName = "posiciones")
data class Posicion(@PrimaryKey val id: Long, val bodegaId: Long, val codigo: String,
    @ColumnInfo(defaultValue = "''") val nombre: String = "",
    @ColumnInfo(defaultValue = "'ESTANTERIA'") val tipo: String = "ESTANTERIA",
    val padreId: Long? = null,
    @ColumnInfo(defaultValue = "1") val activa: Boolean = true)

@Entity(tableName = "productos", indices = [Index(value = ["codigo"], unique = true)])
data class Producto(@PrimaryKey val id: Long, val codigo: String, val nombre: String,
    val categoria: String, val minimo: Int = 5)

@Entity(tableName = "existencias", indices = [Index(value = ["productoId", "posicionId"], unique = true)])
data class Existencia(@PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productoId: Long, val posicionId: Long, val cantidad: Int)

@Entity(tableName = "movimientos")
data class Movimiento(@PrimaryKey(autoGenerate = true) val id: Long = 0, val usuarioId: Long,
    val bodegaId: Long, val productoId: Long, val posicionOrigen: Long, val posicionDestino: Long,
    val cantidad: Int, val fecha: Long = System.currentTimeMillis(), val tipo: String = "TRASLADO")

// periodo de inventario mensual
@Entity(tableName = "periodos_inventario")
data class PeriodoInventario(
    @PrimaryKey val id: Long,
    val bodegaId: Long,
    val asignadoA: Long,
    val nombre: String,
    val estado: String = "ABIERTO", // ABIERTO, ENTREGADO, CERRADO
    val fechaApertura: Long = System.currentTimeMillis(),
    val inventarioBodegaId: Long? = null, // tarea de uno de los operadores del inventario general
    val verificacionFisicaPor: Long? = null,
    val fechaVerificacionFisica: Long? = null
)

@Entity(tableName = "lineas_inventario", indices = [Index(value = ["periodoId", "productoId", "posicionId"], unique = true)])
data class LineaInventario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val periodoId: Long,
    val productoId: Long,
    val posicionId: Long,
    val cantidadContada: Int,
    val stockAlEntregar: Int? = null // Foto del saldo cuando el operador entrega su conteo
)

// preparacion de ordenes
@Entity(tableName = "preparaciones_caja", indices = [Index(value = ["cajaId", "productoId", "posicionId"], unique = true)])
data class PreparacionCaja(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cajaId: Long,
    val productoId: Long,
    val posicionId: Long,
    val cantidad: Int
)

// avance del inventario
@Entity(tableName = "borrador_inventario", indices = [Index(value = ["periodoId", "productoId", "posicionId"], unique = true)])
data class BorradorInventario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val periodoId: Long,
    val productoId: Long,
    val posicionId: Long,
    val cantidad: Int
)

data class ReservaEstante(
    val productoId: Long,
    val posicionId: Long,
    val cantidadReservada: Int
)

data class CompromisoProducto(val productoId: Long, val cantidadSolicitada: Int)

@Entity(tableName = "ajustes_inventario", indices = [Index(value = ["periodoId", "productoId", "posicionId"], unique = true)])
data class AjusteInventario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val periodoId: Long,
    val productoId: Long,
    val posicionId: Long,
    val stockAnterior: Int,
    val stockNuevo: Int,
    val revisadoPor: Long,
    val fecha: Long = System.currentTimeMillis()
)

// asignacion de trabajadores a tiendas
@Entity(tableName = "usuario_sucursal", primaryKeys = ["usuarioId", "sucursalId"])
data class UsuarioSucursal(val usuarioId: Long, val sucursalId: Long)

@Entity(tableName = "movimientos_tienda", indices = [Index(value = ["cajaId", "productoId"])])
data class MovimientoTienda(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: Long,
    val sucursalId: Long,
    val cajaId: Long,
    val productoId: Long,
    val cantidad: Int,
    val fecha: Long = System.currentTimeMillis()
)


// diferencias entre stock general y ubicaciones
data class DiferenciaStockTienda(val sucursalId: Long, val productoId: Long,
    val stockGeneral: Int, val stockUbicaciones: Int)

// posiciones de tienda
@Entity(tableName = "posiciones_tienda", indices = [Index(value = ["sucursalId", "codigo"], unique = true)])
data class PosicionTienda(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sucursalId: Long,
    val codigo: String,
    val zona: String, // RECEPCION, VENTAS, ALMACEN
    val categoria: String, // Categoría sugerida o General
    val activa: Boolean = true,
    @ColumnInfo(defaultValue = "''") val nombre: String = "",
    @ColumnInfo(defaultValue = "'ESTANTERIA'") val tipo: String = "ESTANTERIA",
    val padreId: Long? = null
)

// stock por ubicacion
@Entity(tableName = "existencias_ubicacion_tienda", indices = [Index(value = ["sucursalId", "productoId", "posicionId"], unique = true)])
data class ExistenciaUbicacionTienda(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sucursalId: Long,
    val productoId: Long,
    val posicionId: Long,
    val cantidad: Int
)

// movimientos de tienda
@Entity(tableName = "movimientos_ubicacion_tienda")
data class MovimientoUbicacionTienda(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: Long,
    val sucursalId: Long,
    val productoId: Long,
    val posicionOrigenId: Long? = null,
    val posicionDestinoId: Long,
    val cantidad: Int,
    val tipo: String, // RECEPCION_UBICACION, TRASLADO_TIENDA
    val cajaId: Long? = null,
    val fecha: Long = System.currentTimeMillis()
)

// solicitudes de reposicion de tienda
@Entity(tableName = "solicitudes_reposicion", indices = [Index(value = ["sucursalId", "estado"])])
data class SolicitudReposicion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sucursalId: Long,
    val bodegaId: Long,
    val productoId: Long,
    val cantidad: Int,
    val solicitadoPor: Long,
    val fecha: Long = System.currentTimeMillis(),
    val estado: String = "PENDIENTE",
    val cajaId: Long? = null,
    val loteId: String? = null
)
