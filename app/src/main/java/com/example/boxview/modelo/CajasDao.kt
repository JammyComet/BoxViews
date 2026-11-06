package com.example.boxview.modelo

// Modelo: Consultas y cambios Room de cajas.
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CajasDao {
    // Operacion que modifica datos locales.
    @Query("SELECT COUNT(*) FROM cajas")
    suspend fun numeroCajas(): Int

    @Insert suspend fun guardarCaja(valor: Caja)

    @Insert suspend fun guardarDetalleCaja(valor: DetalleCaja): Long

    @Insert suspend fun guardarEventoCaja(valor: EventoCaja): Long

    @Insert suspend fun guardarIncidenciaCaja(valor: IncidenciaCaja): Long

    @Update suspend fun actualizarCaja(valor: Caja)

    @Update suspend fun actualizarDetalleCaja(valor: DetalleCaja)

    // Registra la llegada física de una caja a bodega. No suma unidades al inventario.
    // Solo cambia cajas esperadas o en tránsito; una recepción confirmada no puede reabrirse.
    @Query("UPDATE cajas SET estado='EN_REVISION' WHERE id=:id AND tipo='INGRESO' AND bodegaDestinoId=:bid AND asignadoA=:uid AND estado IN ('ESPERADA','EN_TRANSITO')")
    suspend fun registrarLlegadaBodega(id: Long, bid: Long, uid: Long): Int

    // Consulta observable para las pantallas.
    @Query("SELECT * FROM cajas WHERE bodegaDestinoId=:bid AND asignadoA=:uid AND tipo='INGRESO' AND estado IN ('ESPERADA','EN_TRANSITO','EN_REVISION') ORDER BY fecha DESC")
    fun cajasIngreso(bid: Long, uid: Long): Flow<List<Caja>>

    @Query("SELECT * FROM cajas WHERE bodegaOrigenId=:bid AND tipo='SALIDA' AND asignadoA=:uid AND estado='ORDEN_ASIGNADA' ORDER BY fecha DESC")
    fun ordenesSalida(uid: Long, bid: Long): Flow<List<Caja>>

    @Query("SELECT * FROM cajas WHERE bodegaOrigenId=:bid AND tipo='SALIDA' AND asignadoA=:uid AND estado IN ('EN_TRANSITO','EN_REVISION','RECIBIDA','CON_DIFERENCIA') ORDER BY fecha DESC LIMIT 20")
    fun salidasEnviadas(uid: Long, bid: Long): Flow<List<Caja>>

    @Query("SELECT * FROM cajas WHERE id=:id LIMIT 1")
    fun observarCaja(id: Long): Flow<Caja?>

    @Query("SELECT * FROM cajas WHERE id=:id LIMIT 1")
    suspend fun cajaPorId(id: Long): Caja?

    @Query("SELECT * FROM cajas WHERE codigo=:codigo COLLATE NOCASE LIMIT 1")
    suspend fun cajaPorCodigo(codigo: String): Caja?

    @Query("SELECT * FROM detalle_caja WHERE cajaId=:id ORDER BY id")
    fun observarDetallesCaja(id: Long): Flow<List<DetalleCaja>>

    @Query("SELECT * FROM detalle_caja WHERE cajaId=:id ORDER BY id")
    suspend fun detallesCaja(id: Long): List<DetalleCaja>

    @Query("SELECT COALESCE(SUM(p.cantidad),0) FROM preparaciones_caja p JOIN cajas c ON c.id=p.cajaId WHERE p.productoId=:pid AND p.posicionId=:pos AND c.tipo='SALIDA' AND c.estado IN ('ORDEN_ASIGNADA','PENDIENTE_REVISION') AND c.id!=:excluir")
    suspend fun stockReservadoCajas(pid: Long, pos: Long, excluir: Long): Int

    @Query("SELECT p.productoId AS productoId, p.posicionId AS posicionId, COALESCE(SUM(p.cantidad),0) AS cantidadReservada FROM preparaciones_caja p JOIN cajas c ON c.id=p.cajaId WHERE c.bodegaOrigenId=:bid AND c.tipo='SALIDA' AND c.estado IN ('ORDEN_ASIGNADA','PENDIENTE_REVISION') AND c.id!=:excluir GROUP BY p.productoId,p.posicionId")
    fun reservasOtrasCajas(bid: Long, excluir: Long): Flow<List<ReservaEstante>>

    @Query("SELECT * FROM preparaciones_caja WHERE cajaId=:id ORDER BY id")
    fun observarPreparacionesCaja(id: Long): Flow<List<PreparacionCaja>>

    @Query("SELECT * FROM preparaciones_caja WHERE cajaId=:id ORDER BY id")
    suspend fun preparacionesCaja(id: Long): List<PreparacionCaja>

    @Query("DELETE FROM preparaciones_caja WHERE cajaId=:id")
    suspend fun borrarPreparacionesCaja(id: Long)

    @Insert suspend fun guardarPreparacionCaja(valor: PreparacionCaja): Long

    @Query("SELECT * FROM cajas WHERE bodegaOrigenId=:bid AND tipo='SALIDA' ORDER BY fecha DESC, id DESC")
    fun ordenesJefatura(bid: Long): Flow<List<Caja>>

    @Query("SELECT COALESCE(MAX(id),0)+1 FROM cajas")
    suspend fun siguienteIdCaja(): Long

    @Query("SELECT COUNT(*) FROM preparaciones_caja WHERE cajaId=:id AND cantidad>0")
    suspend fun avancesDeCaja(id: Long): Int

    @Query("UPDATE cajas SET estado='CANCELADA' WHERE id=:id AND tipo='SALIDA' AND estado='ORDEN_ASIGNADA'")
    suspend fun cancelarOrden(id: Long): Int

    @Query("SELECT i.* FROM incidencias_caja i JOIN cajas c ON c.id=i.cajaId WHERE c.bodegaDestinoId=:bid AND c.tipo='INGRESO' ORDER BY i.fecha DESC, i.id DESC")
    fun incidenciasDeBodega(bid: Long): Flow<List<IncidenciaCaja>>

    @Query("SELECT * FROM cajas WHERE bodegaDestinoId=:bid AND tipo='INGRESO' ORDER BY fecha DESC")
    fun cajasDeDestino(bid: Long): Flow<List<Caja>>

    @Query("UPDATE incidencias_caja SET estado='REVISADA' WHERE id=:id AND estado='PENDIENTE'")
    suspend fun resolverIncidencia(id: Long): Int

    @Query("SELECT * FROM incidencias_caja WHERE id=:id LIMIT 1")
    suspend fun incidenciaPorId(id: Long): IncidenciaCaja?

    @Query("SELECT COUNT(*) FROM cajas WHERE asignadoA=:uid AND bodegaDestinoId=:bid AND tipo='INGRESO' AND estado IN ('ESPERADA','EN_TRANSITO','EN_REVISION')")
    suspend fun ingresosPendientesOperador(uid: Long, bid: Long): Int

    @Query("SELECT COUNT(*) FROM cajas WHERE asignadoA=:uid AND bodegaOrigenId=:bid AND tipo='SALIDA' AND estado IN ('ORDEN_ASIGNADA','PENDIENTE_REVISION')")
    suspend fun ordenesActivasOperador(uid: Long, bid: Long): Int

    @Query("SELECT d.productoId AS productoId, COALESCE(SUM(d.cantidadEsperada),0) AS cantidadSolicitada FROM detalle_caja d JOIN cajas c ON c.id=d.cajaId WHERE c.bodegaOrigenId=:bid AND c.tipo='SALIDA' AND c.estado IN ('ORDEN_ASIGNADA','PENDIENTE_REVISION') GROUP BY d.productoId")
    fun compromisosSalida(bid: Long): Flow<List<CompromisoProducto>>

    @Query("SELECT COALESCE(SUM(d.cantidadEsperada),0) FROM detalle_caja d JOIN cajas c ON c.id=d.cajaId WHERE c.bodegaOrigenId=:bid AND c.tipo='SALIDA' AND c.estado IN ('ORDEN_ASIGNADA','PENDIENTE_REVISION') AND d.productoId=:pid")
    suspend fun unidadesPendientes(pid: Long, bid: Long): Int



    @Query("SELECT * FROM eventos_caja WHERE cajaId=:id AND accion='INCIDENCIA_REVISADA' ORDER BY fecha DESC")
    fun revisionesCaja(id: Long): Flow<List<EventoCaja>>

    // cajas asignadas a la tienda

    @Query("SELECT i.* FROM incidencias_caja i JOIN cajas c ON i.cajaId=c.id WHERE c.bodegaDestinoId=:bid AND c.tipo='INGRESO'")
    suspend fun incidenciasActualesBodega(bid: Long): List<IncidenciaCaja>
}
