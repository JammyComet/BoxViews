package com.example.boxview.modelo

// Modelo: Consultas Room de productos y existencias.
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductosDao {
    @Query("SELECT COUNT(*) FROM productos")
    suspend fun numeroProductos(): Int


    @Query("SELECT * FROM productos WHERE id=:id LIMIT 1")
    suspend fun productoPorId(id: Long): Producto?

    @Insert suspend fun guardarSucursal(valor: Sucursal)

    @Insert suspend fun guardarBodega(valor: Bodega)

    @Insert suspend fun guardarPosicion(valor: Posicion)

    @Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun guardarPosicionSiFalta(valor: Posicion): Long

    // Reetiquetar solo las posiciones originales de muestra, sin inventarios previos.
    @Query("""SELECT (SELECT COUNT(*) FROM inventarios_bodega WHERE bodegaId=:bid) +
        (SELECT COUNT(*) FROM campanias_mensuales WHERE tipo='BODEGA' AND ubicacionId=:bid)""")
    suspend fun historicosInventarioBodega(bid: Long): Int

    @Query("""UPDATE posiciones SET codigo=:codigo, nombre=:nombre, padreId=:padreId
        WHERE id=:id AND bodegaId=:bid AND codigo=:nombreAntiguo
          AND (nombre='' OR nombre=:nombreAntiguo) AND tipo='ESTANTERIA'""")
    suspend fun mejorarEstanteDemoBodega(id: Long, bid: Long, nombreAntiguo: String,
                                         codigo: String, nombre: String, padreId: Long): Int

    // Nuevo registro de stock por posicion.
    @Insert suspend fun guardarProducto(valor: Producto)

    // Insercion en el historial.
    @Insert suspend fun guardarExistencia(valor: Existencia): Long

    @Insert suspend fun guardarMovimiento(valor: Movimiento): Long

    @Insert suspend fun guardarEntrega(valor: EntregaAutorizada)

    @Query("SELECT * FROM bodegas WHERE activa=1 ORDER BY nombre")
    fun todasBodegas(): Flow<List<Bodega>>

    @Query("SELECT * FROM sucursales ORDER BY nombre")
    fun sucursales(): Flow<List<Sucursal>>

    @Query("SELECT * FROM sucursales ORDER BY id")
    suspend fun sucursalesActuales(): List<Sucursal>

    // Catalogo observable de productos.
    @Query("SELECT * FROM productos ORDER BY nombre")
    fun productos(): Flow<List<Producto>>

    // Posiciones observables por bodega.
    @Query("SELECT * FROM posiciones WHERE bodegaId=:bid ORDER BY codigo")
    fun posiciones(bid: Long): Flow<List<Posicion>>

    @Query("SELECT COALESCE(MAX(id),0)+1 FROM posiciones")
    suspend fun siguienteIdPosicion(): Long

    @Query("UPDATE posiciones SET nombre=:nombre WHERE id=:id AND bodegaId=:bid")
    suspend fun renombrarPosicion(id: Long, bid: Long, nombre: String): Int

    @Query("UPDATE posiciones SET activa=:activa WHERE id=:id AND bodegaId=:bid")
    suspend fun estadoPosicion(id: Long, bid: Long, activa: Boolean): Int

    // Consulta de existencias por bodega.
    @Query("SELECT e.* FROM existencias e JOIN posiciones p ON e.posicionId=p.id WHERE p.bodegaId=:bid ORDER BY e.productoId, e.posicionId")
    fun existencias(bid: Long): Flow<List<Existencia>>

    // Historial del operador en la bodega.
    @Query("SELECT * FROM movimientos WHERE usuarioId=:uid AND bodegaId=:bid ORDER BY fecha DESC")
    fun movimientos(uid: Long, bid: Long): Flow<List<Movimiento>>

    @Query("SELECT * FROM posiciones WHERE id=:id LIMIT 1")
    suspend fun posicionPorId(id: Long): Posicion?

    @Query("SELECT * FROM existencias WHERE productoId=:pid AND posicionId=:pos LIMIT 1")
    suspend fun existencia(pid: Long, pos: Long): Existencia?

    // Descuento condicionado por stock disponible.
    @Query("UPDATE existencias SET cantidad=cantidad-:cantidad WHERE id=:id AND cantidad>=:cantidad")
    suspend fun restarSiAlcanza(id: Long, cantidad: Int): Int

    // Actualizacion de unidades existentes.
    @Query("UPDATE existencias SET cantidad=cantidad+:cantidad WHERE id=:id")
    suspend fun sumar(id: Long, cantidad: Int)

    @Query("SELECT COUNT(*) FROM bodegas WHERE id=:bid AND activa=1")
    suspend fun existeBodega(bid: Long): Int

    @Query("SELECT * FROM posiciones WHERE bodegaId=:bid ORDER BY codigo")
    suspend fun posicionesActuales(bid: Long): List<Posicion>

    @Query("SELECT e.* FROM existencias e JOIN posiciones p ON p.id=e.posicionId WHERE p.bodegaId=:bid")
    suspend fun existenciasActuales(bid: Long): List<Existencia>
    @Query("SELECT DISTINCT categoria FROM productos ORDER BY categoria")
    suspend fun categoriasProductos(): List<String>

}
