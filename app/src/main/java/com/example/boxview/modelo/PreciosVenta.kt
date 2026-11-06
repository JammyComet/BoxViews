package com.example.boxview.modelo

// Modelo: Entidades y consultas de precios de venta.
import androidx.room.*
import kotlinx.coroutines.flow.Flow

/** Precio de venta vigente por sucursal, definido únicamente por Administración. CLP enteros. */
@Entity(tableName = "precios_venta_sucursal", primaryKeys = ["sucursalId", "productoId"])
data class PrecioVentaSucursal(
    val sucursalId: Long,
    val productoId: Long,
    val precioClp: Long,
    val actualizadoPor: Long,
    val fechaActualizacion: Long = System.currentTimeMillis()
)

/** Totales basados solamente en ventas efectivamente confirmadas y valorizadas. */
data class ResumenVentasSucursal(
    val sucursalId: Long,
    val sucursal: String,
    val unidadesVendidas: Long,
    val ventasValoradas: Long,
    val ingresoVentaClp: Long,
    val ventasAnterioresSinValor: Long
)

@Dao
interface PreciosVentaDao {
    // Consulta observable para las pantallas.
    @Query("SELECT * FROM precios_venta_sucursal WHERE sucursalId=:sid ORDER BY productoId")
    fun preciosSucursal(sid: Long): Flow<List<PrecioVentaSucursal>>

    @Query("SELECT * FROM precios_venta_sucursal WHERE sucursalId=:sid AND productoId=:pid LIMIT 1")
    suspend fun precioActual(sid: Long, pid: Long): PrecioVentaSucursal?

    // Operacion que modifica datos locales.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarPrecio(precio: PrecioVentaSucursal)

    // Solo carga inicial: jamás sobrescribir un precio configurado por Administración.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun guardarPrecioSiFalta(precio: PrecioVentaSucursal): Long

    @Query("""SELECT s.id AS sucursalId, s.nombre AS sucursal,
        COALESCE(SUM(CASE WHEN v.tipo='VENTA' AND v.totalVentaClp IS NOT NULL THEN v.cantidad ELSE 0 END),0) AS unidadesVendidas,
        COALESCE(SUM(CASE WHEN v.tipo='VENTA' AND v.totalVentaClp IS NOT NULL THEN 1 ELSE 0 END),0) AS ventasValoradas,
        COALESCE(SUM(CASE WHEN v.tipo='VENTA' AND v.totalVentaClp IS NOT NULL THEN v.totalVentaClp ELSE 0 END),0) AS ingresoVentaClp,
        COALESCE(SUM(CASE WHEN v.tipo='VENTA' AND v.totalVentaClp IS NULL THEN 1 ELSE 0 END),0) AS ventasAnterioresSinValor
        FROM sucursales s LEFT JOIN salidas_tienda v ON v.sucursalId=s.id
        WHERE EXISTS(SELECT 1 FROM usuarios u WHERE u.id=:uid AND u.rol='GERENTE' AND u.activo=1)
        GROUP BY s.id,s.nombre ORDER BY s.nombre""")
    fun resumenVentasGerencia(uid: Long): Flow<List<ResumenVentasSucursal>>

    @Query("""SELECT v.* FROM salidas_tienda v WHERE v.tipo='VENTA' AND v.totalVentaClp IS NOT NULL
        AND EXISTS(SELECT 1 FROM usuarios u WHERE u.id=:uid AND u.rol='GERENTE' AND u.activo=1)
        ORDER BY v.fecha DESC,v.id DESC""")
    suspend fun ventasValorizadasGerencia(uid: Long): List<SalidaTienda>
}
