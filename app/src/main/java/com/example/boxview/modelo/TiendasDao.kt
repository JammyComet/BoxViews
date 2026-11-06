package com.example.boxview.modelo

// Modelo: Consultas Room de existencias y operaciones de tienda.
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TiendasDao {
    // Operacion que modifica datos locales.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun guardarAbastecimiento(valor: BodegaSucursal): Long

    // Consulta observable para las pantallas.
    @Query("SELECT s.* FROM sucursales s JOIN bodegas b ON b.sucursalId=s.id WHERE b.id=:bid ORDER BY s.nombre")
    fun tiendasAbastecidas(bid: Long): Flow<List<Sucursal>>

    @Query("SELECT COUNT(*) FROM bodegas WHERE id=:bid AND sucursalId=:sid AND activa=1")
    suspend fun abasteceSucursal(bid: Long, sid: Long): Int

    @Query("SELECT COUNT(*) FROM sucursales WHERE id=:sid")
    suspend fun existeSucursal(sid: Long): Int

    @Query("SELECT * FROM existencias_tienda WHERE sucursalId=:sid ORDER BY productoId")
    fun stockTienda(sid: Long): Flow<List<ExistenciaTienda>>

    @Query("SELECT * FROM existencias_tienda WHERE sucursalId=:sid AND productoId=:pid LIMIT 1")
    suspend fun existenciaTienda(sid: Long, pid: Long): ExistenciaTienda?

    @Insert suspend fun guardarExistenciaTienda(valor: ExistenciaTienda): Long

    @Query("UPDATE existencias_tienda SET cantidad=cantidad+:cantidad WHERE id=:id AND :cantidad>=0")
    suspend fun sumarStockTienda(id: Long, cantidad: Int): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun asignarUsuarioTienda(valor: UsuarioSucursal): Long

    @Query("SELECT s.* FROM sucursales s JOIN usuario_sucursal us ON us.sucursalId=s.id JOIN usuarios u ON u.id=us.usuarioId WHERE us.usuarioId=:uid AND u.activo=1 AND u.rol='TRABAJADOR_TIENDA' ORDER BY s.nombre")
    fun tiendasDelTrabajador(uid: Long): Flow<List<Sucursal>>

    @Query("SELECT COUNT(*) FROM usuario_sucursal us JOIN usuarios u ON u.id=us.usuarioId JOIN sucursales s ON s.id=us.sucursalId WHERE us.usuarioId=:uid AND us.sucursalId=:sid AND u.activo=1 AND u.rol='TRABAJADOR_TIENDA'")
    suspend fun trabajadorAutorizadoTienda(uid: Long, sid: Long): Int

    @Query("SELECT * FROM cajas WHERE tipo='SALIDA' AND sucursalDestinoId=:sid AND estado IN ('EN_TRANSITO','EN_REVISION') ORDER BY CASE WHEN estado='EN_REVISION' THEN 0 ELSE 1 END,fecha DESC,id DESC")
    fun cajasPendientesTienda(sid: Long): Flow<List<Caja>>

    @Query("SELECT * FROM cajas WHERE tipo='SALIDA' AND sucursalDestinoId=:sid AND estado IN ('RECIBIDA','CON_DIFERENCIA') ORDER BY fecha DESC,id DESC")
    fun cajasRecibidasTienda(sid: Long): Flow<List<Caja>>

    @Query("UPDATE cajas SET estado='EN_REVISION' WHERE id=:id AND tipo='SALIDA' AND sucursalDestinoId=:sid AND estado='EN_TRANSITO'")
    suspend fun registrarLlegadaTienda(id: Long, sid: Long): Int

    @Query("UPDATE cajas SET estado=:nuevo WHERE id=:id AND tipo='SALIDA' AND sucursalDestinoId=:sid AND estado='EN_REVISION'")
    suspend fun marcarRecepcionTienda(id: Long, sid: Long, nuevo: String): Int

    @Insert suspend fun guardarMovimientoTienda(valor: MovimientoTienda): Long

    @Query("SELECT * FROM movimientos_tienda WHERE sucursalId=:sid ORDER BY fecha DESC,id DESC LIMIT 100")
    fun movimientosDeTienda(sid: Long): Flow<List<MovimientoTienda>>

    @Query("SELECT i.* FROM incidencias_caja i JOIN cajas c ON c.id=i.cajaId WHERE c.tipo='SALIDA' AND c.sucursalDestinoId=:sid ORDER BY i.fecha DESC,i.id DESC")
    fun incidenciasDeTienda(sid: Long): Flow<List<IncidenciaCaja>>

    @Query("SELECT e.* FROM eventos_caja e JOIN cajas c ON c.id=e.cajaId WHERE c.tipo='SALIDA' AND c.sucursalDestinoId=:sid AND e.accion IN ('RECEPCION_TIENDA_CONFIRMADA','RECEPCION_TIENDA_CON_DIFERENCIA') ORDER BY e.fecha DESC,e.id DESC")
    fun eventosRecepcionTienda(sid: Long): Flow<List<EventoCaja>>


    @Query("SELECT * FROM posiciones_tienda WHERE sucursalId=:sid AND activa=1 AND tipo!='SECCION' ORDER BY CASE zona WHEN 'RECEPCION' THEN 0 WHEN 'VENTAS' THEN 1 ELSE 2 END, codigo")
    fun posicionesTienda(sid: Long): Flow<List<PosicionTienda>>

    @Query("SELECT * FROM posiciones_tienda WHERE sucursalId=:sid ORDER BY codigo")
    fun posicionesTiendaTodas(sid: Long): Flow<List<PosicionTienda>>

    @Query("SELECT * FROM posiciones_tienda WHERE sucursalId=:sid ORDER BY codigo")
    suspend fun posicionesTiendaActuales(sid: Long): List<PosicionTienda>

    // Una ubicación de muestra solo se puede reorganizar si no hay historial de conteo.
    @Query("""SELECT (SELECT COUNT(*) FROM inventarios_tienda WHERE sucursalId=:sid) +
        (SELECT COUNT(*) FROM campanias_mensuales WHERE tipo='TIENDA' AND ubicacionId=:sid)""")
    suspend fun historicosInventarioTienda(sid: Long): Int

    @Query("""UPDATE posiciones_tienda SET nombre=:nombre, padreId=:padreId
        WHERE sucursalId=:sid AND codigo=:codigo AND nombre=:codigo AND tipo='ESTANTERIA'""")
    suspend fun mejorarEstanteDemoTienda(sid: Long, codigo: String, nombre: String, padreId: Long): Int

    @Query("SELECT * FROM posiciones_tienda WHERE id=:id LIMIT 1")
    suspend fun posicionTiendaPorId(id: Long): PosicionTienda?

    @Query("SELECT COUNT(*) FROM posiciones_tienda WHERE sucursalId=:sid")
    suspend fun numeroPosicionesTienda(sid: Long): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun guardarPosicionTienda(posicion: PosicionTienda): Long

    @Query("SELECT COALESCE(MAX(id),0)+1 FROM posiciones_tienda")
    suspend fun siguienteIdPosicionTienda(): Long

    @Query("UPDATE posiciones_tienda SET nombre=:nombre WHERE id=:id AND sucursalId=:sid")
    suspend fun renombrarPosicionTienda(id: Long, sid: Long, nombre: String): Int

    @Query("SELECT * FROM existencias_tienda WHERE sucursalId=:sid")
    suspend fun stockTiendaActual(sid: Long): List<ExistenciaTienda>

    @Query("SELECT COALESCE(SUM(cantidad),0) FROM existencias_ubicacion_tienda WHERE sucursalId=:sid AND productoId=:pid")
    suspend fun totalUbicadoTienda(sid: Long, pid: Long): Int

    // revisar diferencias de stock sin modificar existencias
    @Query("""SELECT e.sucursalId AS sucursalId, e.productoId AS productoId,
            e.cantidad AS stockGeneral,
            COALESCE((SELECT SUM(u.cantidad) FROM existencias_ubicacion_tienda u
                WHERE u.sucursalId=e.sucursalId AND u.productoId=e.productoId), 0) AS stockUbicaciones
        FROM existencias_tienda e WHERE e.sucursalId=:sid AND
            (e.cantidad != COALESCE((SELECT SUM(u.cantidad) FROM existencias_ubicacion_tienda u
                WHERE u.sucursalId=e.sucursalId AND u.productoId=e.productoId), 0)
                OR e.cantidad < 0)
        UNION ALL
        SELECT u.sucursalId AS sucursalId, u.productoId AS productoId,
            0 AS stockGeneral, SUM(u.cantidad) AS stockUbicaciones
        FROM existencias_ubicacion_tienda u WHERE u.sucursalId=:sid AND
            NOT EXISTS (SELECT 1 FROM existencias_tienda e
                WHERE e.sucursalId=u.sucursalId AND e.productoId=u.productoId)
        GROUP BY u.sucursalId, u.productoId
        HAVING SUM(u.cantidad) != 0
        ORDER BY productoId""")
    fun diferenciasStockTienda(sid: Long): Flow<List<DiferenciaStockTienda>>

    @Query("SELECT * FROM existencias_ubicacion_tienda WHERE sucursalId=:sid ORDER BY productoId,posicionId")
    suspend fun stockUbicacionesTiendaActual(sid: Long): List<ExistenciaUbicacionTienda>

    @Query("SELECT * FROM existencias_ubicacion_tienda WHERE sucursalId=:sid ORDER BY productoId, posicionId")
    fun stockUbicacionesTienda(sid: Long): Flow<List<ExistenciaUbicacionTienda>>

    @Query("SELECT * FROM existencias_ubicacion_tienda WHERE sucursalId=:sid AND productoId=:pid AND posicionId=:pos LIMIT 1")
    suspend fun stockUbicacionTienda(sid: Long, pid: Long, pos: Long): ExistenciaUbicacionTienda?

    @Insert
    suspend fun guardarStockUbicacionTienda(valor: ExistenciaUbicacionTienda): Long

    @Query("UPDATE existencias_ubicacion_tienda SET cantidad=cantidad+:cantidad WHERE id=:id AND :cantidad>0")
    suspend fun sumarStockUbicacionTienda(id: Long, cantidad: Int): Int

    @Query("UPDATE existencias_ubicacion_tienda SET cantidad=cantidad-:cantidad WHERE id=:id AND cantidad>=:cantidad AND :cantidad>0")
    suspend fun restarStockUbicacionTienda(id: Long, cantidad: Int): Int

    @Insert
    suspend fun guardarMovimientoUbicacionTienda(valor: MovimientoUbicacionTienda): Long

    @Query("SELECT * FROM movimientos_ubicacion_tienda WHERE sucursalId=:sid ORDER BY fecha DESC,id DESC LIMIT 100")
    fun movimientosUbicacionTienda(sid: Long): Flow<List<MovimientoUbicacionTienda>>


    @Query("SELECT s.* FROM sucursales s JOIN usuario_sucursal us ON us.sucursalId=s.id JOIN usuarios u ON u.id=us.usuarioId WHERE us.usuarioId=:uid AND u.activo=1 AND u.rol IN ('JEFE_TIENDA','TRABAJADOR_TIENDA') ORDER BY s.nombre")
    fun tiendasAutorizadas(uid: Long): Flow<List<Sucursal>>

    @Query("SELECT COUNT(*) FROM usuario_sucursal us JOIN usuarios u ON u.id=us.usuarioId JOIN sucursales s ON s.id=us.sucursalId WHERE us.usuarioId=:uid AND us.sucursalId=:sid AND u.activo=1 AND u.rol=:rol AND :rol IN ('TRABAJADOR_TIENDA','JEFE_TIENDA')")
    fun observarAccesoTienda(uid: Long, sid: Long, rol: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM usuario_sucursal us JOIN usuarios u ON u.id=us.usuarioId WHERE us.usuarioId=:uid AND us.sucursalId=:sid AND u.activo=1 AND u.rol='JEFE_TIENDA'")
    suspend fun jefeAutorizadoTienda(uid: Long, sid: Long): Int

    @Query("SELECT u.* FROM usuarios u JOIN usuario_sucursal us ON us.usuarioId=u.id WHERE us.sucursalId=:sid AND u.rol='TRABAJADOR_TIENDA' AND u.activo=1 ORDER BY u.nombre")
    fun trabajadoresDeTienda(sid: Long): Flow<List<Usuario>>

    @Query("""SELECT COUNT(*) FROM usuarios u JOIN usuario_sucursal us ON us.usuarioId=u.id
        WHERE us.sucursalId=:sid AND u.rol='TRABAJADOR_TIENDA' AND u.activo=1""")
    suspend fun trabajadoresActivosTienda(sid: Long): Int

    @Query("SELECT u.* FROM usuarios u WHERE u.rol='TRABAJADOR_TIENDA' AND u.activo=1 ORDER BY u.nombre")
    fun trabajadoresTienda(): Flow<List<Usuario>>

    @Query("SELECT * FROM usuarios u WHERE u.rol='TRABAJADOR_TIENDA' AND u.activo=1 AND NOT EXISTS (SELECT 1 FROM usuario_sucursal us WHERE us.usuarioId=u.id) ORDER BY u.nombre")
    fun trabajadoresSinAsignacion(): Flow<List<Usuario>>

    @Query("SELECT COUNT(*) FROM usuario_sucursal WHERE usuarioId=:uid")
    suspend fun numeroTiendasUsuario(uid: Long): Int

    @Query("SELECT COUNT(*) FROM usuario_sucursal WHERE usuarioId=:uid AND sucursalId=:sid")
    suspend fun tieneAsignacionTienda(uid: Long, sid: Long): Int

    @Query("DELETE FROM usuario_sucursal WHERE usuarioId=:uid AND sucursalId=:sid")
    suspend fun retirarAsignacionTienda(uid: Long, sid: Long): Int

    @Query("SELECT b.* FROM bodegas b WHERE b.sucursalId=:sid AND b.activa=1 ORDER BY b.nombre")
    fun bodegasDeTienda(sid: Long): Flow<List<Bodega>>

    @Query("SELECT s.productoId FROM solicitudes_reposicion s LEFT JOIN cajas c ON c.id=s.cajaId WHERE s.sucursalId=:sid AND (s.estado='PENDIENTE' OR (s.estado='ATENDIDA' AND c.estado NOT IN ('RECIBIDA','CON_DIFERENCIA','CANCELADA')))")
    fun productosEnReposicion(sid: Long): Flow<List<Long>>

    @Query("SELECT * FROM solicitudes_reposicion WHERE sucursalId=:sid ORDER BY fecha DESC,id DESC")
    fun solicitudesDeTienda(sid: Long): Flow<List<SolicitudReposicion>>

    @Query("SELECT * FROM solicitudes_reposicion WHERE bodegaId=:bid ORDER BY fecha DESC,id DESC")
    fun solicitudesDeBodega(bid: Long): Flow<List<SolicitudReposicion>>

    @Query("SELECT * FROM solicitudes_reposicion WHERE id=:id LIMIT 1")
    suspend fun solicitudPorId(id: Long): SolicitudReposicion?

    @Query("SELECT COUNT(*) FROM solicitudes_reposicion s LEFT JOIN cajas c ON c.id=s.cajaId WHERE s.sucursalId=:sid AND s.productoId=:pid AND (s.estado='PENDIENTE' OR (s.estado='ATENDIDA' AND c.estado NOT IN ('RECIBIDA','CON_DIFERENCIA','CANCELADA')))")
    suspend fun solicitudPendiente(sid: Long, pid: Long): Int

    @Query("SELECT * FROM solicitudes_reposicion WHERE estado='PENDIENTE' AND ((:loteId IS NOT NULL AND loteId=:loteId) OR (:loteId IS NULL AND id=:id)) ORDER BY id")
    suspend fun solicitudesPendientesGrupo(id: Long, loteId: String?): List<SolicitudReposicion>

    @Query("UPDATE solicitudes_reposicion SET estado='CANCELADA' WHERE sucursalId=:sid AND loteId=:loteId AND estado='PENDIENTE'")
    suspend fun cancelarGrupo(sid: Long, loteId: String): Int

    @Insert suspend fun guardarSolicitud(valor: SolicitudReposicion): Long

    @Query("UPDATE solicitudes_reposicion SET estado='CANCELADA' WHERE id=:id AND estado='PENDIENTE' AND sucursalId=:sid")
    suspend fun cancelarSolicitud(id: Long, sid: Long): Int

    @Query("UPDATE solicitudes_reposicion SET estado='ATENDIDA', cajaId=:cajaId WHERE id=:id AND estado='PENDIENTE' AND bodegaId=:bid")
    suspend fun atenderSolicitud(id: Long, bid: Long, cajaId: Long): Int
    @Query("UPDATE solicitudes_reposicion SET estado='PENDIENTE', cajaId=NULL WHERE cajaId=:cajaId AND estado='ATENDIDA'")
    suspend fun reabrirSolicitudCaja(cajaId: Long): Int

    @Query("UPDATE incidencias_caja SET estado='REVISADA' WHERE id=:incidenciaId AND estado='PENDIENTE' AND EXISTS (SELECT 1 FROM cajas c WHERE c.id=incidencias_caja.cajaId AND c.tipo='SALIDA' AND c.sucursalDestinoId=:sid)")
    suspend fun revisarIncidenciaTienda(incidenciaId: Long, sid: Long): Int

    @Query("UPDATE posiciones_tienda SET activa=:activa WHERE id=:id AND sucursalId=:sid")
    suspend fun cambiarEstadoPosicionTienda(id: Long, sid: Long, activa: Boolean): Int

}
