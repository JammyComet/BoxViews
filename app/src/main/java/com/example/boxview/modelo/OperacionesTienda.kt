package com.example.boxview.modelo

// Modelo: Entidades de ventas y operaciones de tienda.
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "salidas_tienda")
data class SalidaTienda(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sucursalId: Long, val usuarioId: Long, val productoId: Long,
    val posicionId: Long, val cantidad: Int, val tipo: String,
    val motivo: String, val fecha: Long = System.currentTimeMillis(),
    // Instantánea financiera inmutable: no recalcular ventas pasadas con el precio actual.
    val precioUnitarioVentaClp: Long? = null, val totalVentaClp: Long? = null
)

@Entity(tableName = "inventarios_tienda", indices = [Index(value = ["sucursalId"])])
data class InventarioTienda(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sucursalId: Long, val nombre: String, val estado: String = "ABIERTO",
    val fechaApertura: Long = System.currentTimeMillis(),
    val cerradoPor: Long? = null, val fechaCierre: Long? = null,
    val estadoRevision: String = "NUEVO", val revisadoPor: Long? = null,
    val fechaRevision: Long? = null, val motivoCierre: String = "",
    val verificacionVaciaPor: Long? = null, val fechaVerificacionVacia: Long? = null,
    val campaniaId: Long? = null
)

@Entity(tableName = "lineas_inventario_tienda",
    indices = [Index(value = ["inventarioId", "productoId", "posicionId"], unique = true)])
data class LineaInventarioTienda(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val inventarioId: Long, val productoId: Long, val posicionId: Long,
    val stockInicio: Int, val cantidadContada: Int? = null,
    val contadoPor: Long? = null, val fechaConteo: Long? = null,
    val codigoProductoCierre: String? = null,
    val nombreProductoCierre: String? = null,
    val rutaUbicacionCierre: String? = null
)


// cada posición física pertenece a un único trabajador durante el conteo
@Entity(tableName = "zonas_conteo_tienda", primaryKeys = ["inventarioId", "posicionId"])
data class ZonaConteoTienda(
    val inventarioId: Long, val posicionId: Long, val asignadoA: Long = 0,
    val confirmadoPor: Long? = null, val fechaConfirmacion: Long? = null
)

// registro inmutable de conteos y reconteos
@Entity(tableName = "eventos_conteo_tienda", indices = [Index(value = ["inventarioId", "fecha"])])
data class EventoConteoTienda(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val inventarioId: Long, val lineaId: Long, val productoId: Long, val posicionId: Long,
    val usuarioId: Long, val cantidadAnterior: Int?, val cantidadNueva: Int,
    val tipo: String, val fecha: Long = System.currentTimeMillis()
)

@Dao
interface OperacionesTiendaDao {
    // Operacion que modifica datos locales.
    @Query("SELECT COUNT(*) FROM salidas_tienda")
    suspend fun numeroSalidas(): Int

    @Insert suspend fun agregarZonaTienda(zona: ZonaConteoTienda)
    // Consulta observable para las pantallas.
    @Query("SELECT * FROM zonas_conteo_tienda WHERE inventarioId=:id ORDER BY posicionId")
    fun zonasInventarioTienda(id: Long): Flow<List<ZonaConteoTienda>>
    @Query("SELECT * FROM zonas_conteo_tienda WHERE inventarioId=:id ORDER BY posicionId")
    suspend fun zonasTiendaActuales(id: Long): List<ZonaConteoTienda>
    @Query("UPDATE zonas_conteo_tienda SET asignadoA=:uid WHERE inventarioId=:inv AND posicionId=:pos AND asignadoA=0 AND confirmadoPor IS NULL")
    suspend fun asignarZonaTienda(inv: Long, pos: Long, uid: Long): Int
    @Query("""UPDATE zonas_conteo_tienda SET confirmadoPor=:uid,fechaConfirmacion=:fecha
        WHERE inventarioId=:inv AND posicionId=:pos AND asignadoA=:uid AND confirmadoPor IS NULL
        AND EXISTS(SELECT 1 FROM inventarios_tienda i WHERE i.id=:inv AND i.estado='ABIERTO')
        AND NOT EXISTS(SELECT 1 FROM lineas_inventario_tienda l WHERE l.inventarioId=:inv
            AND l.posicionId=:pos AND l.cantidadContada IS NULL)""")
    suspend fun confirmarZonaTienda(inv: Long, pos: Long, uid: Long, fecha: Long): Int
    @Query("UPDATE zonas_conteo_tienda SET confirmadoPor=NULL,fechaConfirmacion=NULL WHERE inventarioId=:inv AND posicionId=:pos AND confirmadoPor IS NOT NULL")
    suspend fun reabrirZonaTienda(inv: Long, pos: Long): Int
    @Query("UPDATE lineas_inventario_tienda SET cantidadContada=NULL,contadoPor=NULL,fechaConteo=NULL WHERE inventarioId=:inv AND posicionId=:pos")
    suspend fun reiniciarConteosZonaTienda(inv: Long, pos: Long): Int
    @Query("SELECT COUNT(*) FROM zonas_conteo_tienda WHERE inventarioId=:inv AND (asignadoA=0 OR confirmadoPor IS NULL)")
    suspend fun zonasTiendaPendientes(inv: Long): Int
    @Query("""SELECT COUNT(*) FROM zonas_conteo_tienda z JOIN inventarios_tienda i ON i.id=z.inventarioId
        WHERE i.sucursalId=:sid AND i.estado='ABIERTO' AND z.asignadoA=:uid""")
    suspend fun zonasTiendaAsignadasAbiertas(sid: Long, uid: Long): Int

    @Insert suspend fun registrarEventoConteo(evento: EventoConteoTienda): Long
    @Query("SELECT * FROM eventos_conteo_tienda WHERE inventarioId=:id ORDER BY fecha DESC,id DESC")
    fun eventosConteo(id: Long): Flow<List<EventoConteoTienda>>
    @Query("SELECT * FROM eventos_conteo_tienda ORDER BY fecha DESC,id DESC")
    suspend fun todosEventosConteo(): List<EventoConteoTienda>
    @Query("""UPDATE inventarios_tienda SET verificacionVaciaPor=:uid,fechaVerificacionVacia=:fecha
        WHERE id=:id AND sucursalId=:sid AND estado='ABIERTO'
        AND NOT EXISTS(SELECT 1 FROM lineas_inventario_tienda l WHERE l.inventarioId=:id)""")
    suspend fun confirmarTiendaVacia(id: Long, sid: Long, uid: Long, fecha: Long): Int

    @Insert suspend fun registrarSalida(salida: SalidaTienda): Long
    @Query("SELECT * FROM salidas_tienda WHERE sucursalId=:sid ORDER BY fecha DESC,id DESC LIMIT 100")
    fun salidas(sid: Long): Flow<List<SalidaTienda>>
    @Query("SELECT * FROM salidas_tienda ORDER BY fecha DESC,id DESC")
    suspend fun salidasGlobales(): List<SalidaTienda>

    @Query("UPDATE existencias_tienda SET cantidad=cantidad-:cantidad WHERE sucursalId=:sid AND productoId=:pid AND cantidad>=:cantidad AND :cantidad>0")
    suspend fun descontarTotal(sid: Long, pid: Long, cantidad: Int): Int
    @Query("UPDATE existencias_tienda SET cantidad=cantidad+:delta WHERE sucursalId=:sid AND productoId=:pid AND cantidad+:delta>=0")
    suspend fun ajustarTotal(sid: Long, pid: Long, delta: Int): Int
    @Query("UPDATE existencias_ubicacion_tienda SET cantidad=:cantidad WHERE id=:id AND cantidad>=0 AND :cantidad>=0")
    suspend fun establecerUbicacion(id: Long, cantidad: Int): Int

    @Insert suspend fun abrirInventario(inventario: InventarioTienda): Long
    @Insert suspend fun agregarLineaInventario(linea: LineaInventarioTienda): Long
    @Query("SELECT * FROM inventarios_tienda WHERE sucursalId=:sid AND estado!='CANCELADO' ORDER BY fechaApertura DESC,id DESC")
    fun inventarios(sid: Long): Flow<List<InventarioTienda>>
    @Query("SELECT * FROM inventarios_tienda WHERE sucursalId=:sid AND estado='ABIERTO' ORDER BY id DESC LIMIT 1")
    fun inventarioAbiertoFlujo(sid: Long): Flow<InventarioTienda?>
    @Query("SELECT COUNT(*) FROM inventarios_tienda WHERE sucursalId=:sid AND estado='ABIERTO'")
    suspend fun inventarioAbierto(sid: Long): Int
    @Query("SELECT * FROM inventarios_tienda WHERE id=:id LIMIT 1")
    suspend fun buscarInventario(id: Long): InventarioTienda?
    @Query("SELECT * FROM lineas_inventario_tienda WHERE inventarioId=:id ORDER BY posicionId, productoId")
    fun lineasFlujo(id: Long): Flow<List<LineaInventarioTienda>>
    @Query("SELECT * FROM lineas_inventario_tienda WHERE inventarioId=:id ORDER BY posicionId, productoId")
    suspend fun lineasInventario(id: Long): List<LineaInventarioTienda>
    @Query("SELECT COUNT(*) FROM lineas_inventario_tienda WHERE inventarioId=:inventario AND productoId=:pid AND posicionId=:pos")
    suspend fun lineaExiste(inventario: Long, pid: Long, pos: Long): Int
    @Query("UPDATE lineas_inventario_tienda SET codigoProductoCierre=:codigo,nombreProductoCierre=:nombre,rutaUbicacionCierre=:ruta WHERE id=:id AND inventarioId=:inventario")
    suspend fun guardarInstantanea(id: Long, inventario: Long, codigo: String, nombre: String, ruta: String): Int
    @Query("UPDATE lineas_inventario_tienda SET cantidadContada=:cantidad,contadoPor=:uid,fechaConteo=:fecha WHERE id=:id AND inventarioId=:inventario AND EXISTS (SELECT 1 FROM inventarios_tienda i WHERE i.id=:inventario AND i.estado='ABIERTO')")
    suspend fun anotarConteo(id: Long, inventario: Long, cantidad: Int, uid: Long, fecha: Long): Int
    @Query("UPDATE inventarios_tienda SET estado='CERRADO',cerradoPor=:uid,fechaCierre=:fecha,motivoCierre=:motivo WHERE id=:id AND sucursalId=:sid AND estado='ABIERTO'")
    suspend fun cerrarInventario(id: Long, sid: Long, uid: Long, fecha: Long, motivo: String): Int
    @Query("UPDATE inventarios_tienda SET estado='CANCELADO',cerradoPor=:uid,fechaCierre=:fecha,motivoCierre=:motivo WHERE id=:id AND sucursalId=:sid AND estado='ABIERTO'")
    suspend fun cancelarInventario(id: Long, sid: Long, uid: Long, fecha: Long, motivo: String): Int
}
