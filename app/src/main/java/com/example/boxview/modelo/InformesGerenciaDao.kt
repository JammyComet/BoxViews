package com.example.boxview.modelo

// Modelo: Consultas Room de informes gerenciales.
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface InformesGerenciaDao {
    // los inventarios cerrados de tienda tambien llegan a gerencia
    // Consulta observable para las pantallas.
    @Query("SELECT * FROM inventarios_tienda WHERE estado='CERRADO' AND campaniaId IS NULL ORDER BY CASE WHEN estadoRevision='NUEVO' THEN 0 ELSE 1 END, fechaCierre DESC,id DESC")
    fun informesTienda(): Flow<List<InventarioTienda>>

    @Query("SELECT COUNT(*) FROM inventarios_tienda WHERE estado='CERRADO' AND campaniaId IS NULL AND estadoRevision='NUEVO'")
    fun nuevosTienda(): Flow<Int>

    @Query("SELECT * FROM informes_inventario ORDER BY fechaCierre,id")
    suspend fun todosInformesBodega(): List<InformeInventario>
    @Query("SELECT * FROM detalles_informe_inventario ORDER BY informeId,id")
    suspend fun todosDetallesBodega(): List<DetalleInformeInventario>
    @Query("SELECT * FROM inventarios_tienda WHERE estado='CERRADO' ORDER BY fechaCierre,id")
    suspend fun todosInventariosTienda(): List<InventarioTienda>
    @Query("SELECT l.* FROM lineas_inventario_tienda l JOIN inventarios_tienda i ON i.id=l.inventarioId WHERE i.estado='CERRADO' ORDER BY l.inventarioId,l.id")
    suspend fun todasLineasTienda(): List<LineaInventarioTienda>

    @Query("SELECT * FROM lineas_inventario_tienda WHERE inventarioId=:id ORDER BY productoId,posicionId")
    fun detalleTienda(id: Long): Flow<List<LineaInventarioTienda>>

    @Query("SELECT sucursalId FROM inventarios_tienda WHERE id=:id AND estado='CERRADO' LIMIT 1")
    suspend fun sucursalInventarioTienda(id: Long): Long?

    @Query("SELECT sucursalId FROM informes_inventario WHERE id=:id LIMIT 1")
    suspend fun sucursalInformeBodega(id: Long): Long?

    @Query("UPDATE inventarios_tienda SET estadoRevision='REVISADO',revisadoPor=:uid,fechaRevision=:fecha WHERE id=:id AND estado='CERRADO' AND estadoRevision='NUEVO'")
    suspend fun revisarTienda(id: Long, uid: Long, fecha: Long): Int

    @Query("SELECT * FROM informes_inventario WHERE inventarioId=:id LIMIT 1")
    suspend fun informeDeParcial(id: Long): InformeInventario?
    // Operacion que modifica datos locales.
    @Query("SELECT * FROM detalles_informe_inventario WHERE informeId=:id ORDER BY id")
    suspend fun detallesActuales(id: Long): List<DetalleInformeInventario>

    @Insert suspend fun guardarInforme(valor: InformeInventario): Long
    @Insert suspend fun guardarDetalleInforme(valor: DetalleInformeInventario): Long

    @Query("SELECT f.* FROM informes_inventario f LEFT JOIN inventarios_bodega i ON i.id=f.inventarioId WHERE i.campaniaId IS NULL ORDER BY CASE WHEN f.estadoRevision='NUEVO' THEN 0 ELSE 1 END, f.fechaCierre DESC, f.id DESC")
    fun informes(): Flow<List<InformeInventario>>

    @Query("SELECT COUNT(*) FROM informes_inventario f LEFT JOIN inventarios_bodega i ON i.id=f.inventarioId WHERE f.estadoRevision='NUEVO' AND i.campaniaId IS NULL")
    fun nuevos(): Flow<Int>

    @Query("SELECT * FROM detalles_informe_inventario WHERE informeId=:id ORDER BY codigo, posicion")
    fun detalles(id: Long): Flow<List<DetalleInformeInventario>>

    @Query("SELECT COUNT(*) FROM usuarios WHERE id=:uid AND rol='GERENTE' AND activo=1")
    suspend fun gerenteValido(uid: Long): Int

    @Query("UPDATE informes_inventario SET estadoRevision='REVISADO', revisadoPor=:uid, fechaRevision=:fecha WHERE id=:id AND estadoRevision='NUEVO'")
    suspend fun marcarRevisado(id: Long, uid: Long, fecha: Long): Int
}
