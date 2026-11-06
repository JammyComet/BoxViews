package com.example.boxview.modelo

// Modelo: Consultas Room de compras y proveedores.
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AbastecimientoExternoDao {
    // Operacion que modifica datos locales.
    @Query("SELECT COUNT(*) FROM solicitudes_externas")
    suspend fun numeroSolicitudes(): Int

    @Insert suspend fun nuevaSolicitud(valor: SolicitudExterna): Long
    @Insert suspend fun nuevaLinea(valor: LineaSolicitudExterna): Long
    @Insert suspend fun nuevoEvento(valor: EventoSolicitudExterna): Long

    @Query("UPDATE solicitudes_externas SET proveedorCotizado=:proveedor WHERE id=:id AND sucursalId=:sid AND estado='PENDIENTE_ADMIN'")
    suspend fun guardarProveedorCotizado(id: Long, sid: Long, proveedor: String): Int

    @Query("UPDATE lineas_solicitud_externa SET precioUnitarioCotizado=:precio WHERE solicitudId=:id AND productoId=:producto")
    suspend fun guardarPrecioCotizado(id: Long, producto: Long, precio: Long): Int

    @Query("""SELECT s.id AS sucursalId, s.nombre AS sucursal,
       COALESCE(SUM(CASE WHEN x.estado IN ('APROBADA','ENTREGA_PROGRAMADA','RECIBIDA','CON_DIFERENCIA')
           THEN l.cantidad * COALESCE(l.precioUnitarioCotizado,0) ELSE 0 END),0) AS totalAprobado,
       COALESCE(SUM(CASE WHEN x.estado IN ('RECIBIDA','CON_DIFERENCIA')
           THEN COALESCE(d.cantidadRecibida,0) * COALESCE(l.precioUnitarioCotizado,0) ELSE 0 END),0) AS totalRecibido,
       COALESCE(SUM(CASE WHEN x.estado = 'EN_GERENCIA'
           THEN l.cantidad * COALESCE(l.precioUnitarioCotizado,0) ELSE 0 END),0) AS totalPendiente,
       COUNT(DISTINCT CASE WHEN x.estado='EN_GERENCIA' THEN x.id END) AS solicitudesPendientes,
       COUNT(DISTINCT CASE WHEN x.estado IN ('RECIBIDA','CON_DIFERENCIA') THEN x.id END) AS recepcionesCompletadas,
       COUNT(DISTINCT CASE WHEN x.estado IN ('EN_GERENCIA','APROBADA','ENTREGA_PROGRAMADA','RECIBIDA','CON_DIFERENCIA')
           AND l.precioUnitarioCotizado IS NULL THEN x.id END) AS solicitudesSinCotizacion
       FROM sucursales s LEFT JOIN solicitudes_externas x ON x.sucursalId=s.id
       LEFT JOIN lineas_solicitud_externa l ON l.solicitudId=x.id
       LEFT JOIN detalle_caja d ON d.cajaId=x.entregaCajaId AND d.productoId=l.productoId
       WHERE EXISTS(SELECT 1 FROM usuarios u WHERE u.id=:gerenteId AND u.rol='GERENTE' AND u.activo=1)
       GROUP BY s.id,s.nombre ORDER BY s.nombre""")
    // Consulta observable para las pantallas.
    fun resumenFinanciero(gerenteId: Long): Flow<List<ResumenFinancieroSucursal>>

    @Query("SELECT * FROM solicitudes_externas ORDER BY id DESC")
    suspend fun solicitudesFinancieras(): List<SolicitudExterna>

    @Query("SELECT * FROM lineas_solicitud_externa ORDER BY solicitudId,id")
    suspend fun lineasFinancieras(): List<LineaSolicitudExterna>

    @Query("SELECT * FROM solicitudes_externas WHERE id=:id LIMIT 1")
    suspend fun solicitudPorId(id: Long): SolicitudExterna?

    @Query("SELECT * FROM solicitudes_externas WHERE bodegaId=:bid ORDER BY fecha DESC, id DESC")
    fun solicitudesDeBodega(bid: Long): Flow<List<SolicitudExterna>>

    @Query("SELECT * FROM solicitudes_externas WHERE sucursalId=:sid ORDER BY fecha DESC, id DESC")
    fun solicitudesDeSucursal(sid: Long): Flow<List<SolicitudExterna>>

    @Query("SELECT * FROM solicitudes_externas ORDER BY fecha DESC, id DESC")
    fun todasSolicitudes(): Flow<List<SolicitudExterna>>

    @Query("SELECT * FROM lineas_solicitud_externa WHERE solicitudId=:id ORDER BY id")
    suspend fun lineasActuales(id: Long): List<LineaSolicitudExterna>

    @Query("SELECT * FROM lineas_solicitud_externa WHERE solicitudId=:id ORDER BY id")
    fun observarLineas(id: Long): Flow<List<LineaSolicitudExterna>>

    @Query("SELECT * FROM eventos_solicitud_externa ORDER BY fecha DESC, id DESC LIMIT 100")
    fun eventosGlobales(): Flow<List<EventoSolicitudExterna>>

    @Query("SELECT * FROM eventos_solicitud_externa WHERE solicitudId=:id ORDER BY fecha, id")
    fun eventosSolicitud(id: Long): Flow<List<EventoSolicitudExterna>>

    @Query("UPDATE solicitudes_externas SET estado='EN_GERENCIA', fechaRevision=:fecha, revisadoPor=:uid, observacion=:observacion WHERE id=:id AND estado='PENDIENTE_ADMIN' AND sucursalId=:sid")
    suspend fun enviarAGerencia(id: Long, sid: Long, uid: Long, fecha: Long, observacion: String): Int

    @Query("UPDATE solicitudes_externas SET estado=:nuevo, fechaDecision=:fecha, decididoPor=:uid, observacion=:observacion WHERE id=:id AND estado='EN_GERENCIA'")
    suspend fun decidir(id: Long, uid: Long, nuevo: String, fecha: Long, observacion: String): Int

    @Query("UPDATE solicitudes_externas SET estado='ENTREGA_PROGRAMADA', entregaCajaId=:caja WHERE id=:id AND estado='APROBADA' AND sucursalId=:sid AND entregaCajaId IS NULL")
    suspend fun programarEntrega(id: Long, sid: Long, caja: Long): Int

    @Query("SELECT * FROM solicitudes_externas WHERE entregaCajaId=:caja LIMIT 1")
    suspend fun solicitudPorCaja(caja: Long): SolicitudExterna?

    @Query("UPDATE solicitudes_externas SET estado=:estado WHERE entregaCajaId=:caja AND estado='ENTREGA_PROGRAMADA'")
    suspend fun completarEntrega(caja: Long, estado: String): Int

    @Query("SELECT * FROM cajas WHERE id=:id LIMIT 1")
    fun cajaEntrega(id: Long): Flow<Caja?>

    @Query("SELECT COUNT(*) FROM usuarios WHERE id=:uid AND rol='GERENTE' AND activo=1")
    suspend fun gerenteAutorizado(uid: Long): Int

    @Query("SELECT COUNT(*) FROM usuarios WHERE id=:uid AND rol='GERENTE' AND activo=1")
    fun gerenteActivo(uid: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM usuarios u JOIN usuario_bodega ub ON ub.usuarioId=u.id JOIN bodegas b ON b.id=ub.bodegaId WHERE u.id=:uid AND u.rol='JEFE_BODEGA' AND u.activo=1 AND ub.bodegaId=:bid AND b.activa=1")
    suspend fun jefeAutorizadoExterno(uid: Long, bid: Long): Int

    @Query("SELECT COUNT(*) FROM usuarios u JOIN usuario_sucursal us ON us.usuarioId=u.id WHERE u.id=:uid AND u.rol='ADMIN_SUCURSAL' AND u.activo=1 AND us.sucursalId=:sid")
    suspend fun adminAutorizadoExterno(uid: Long, sid: Long): Int

    @Query("SELECT COUNT(*) FROM usuarios u JOIN usuario_bodega ub ON ub.usuarioId=u.id WHERE u.id=:uid AND u.rol='OPERADOR_BODEGA' AND u.activo=1 AND ub.bodegaId=:bid")
    suspend fun operadorActivoDeBodegaParaEntrega(uid: Long, bid: Long): Int

    @Query("SELECT * FROM bodegas WHERE id=:bid AND sucursalId=:sid AND activa=1 LIMIT 1")
    suspend fun bodegaDeSucursal(bid: Long, sid: Long): Bodega?

    @Query("SELECT u.* FROM usuarios u JOIN usuario_bodega ub ON ub.usuarioId=u.id WHERE ub.bodegaId=:bid AND u.activo=1 AND u.rol='OPERADOR_BODEGA' ORDER BY u.nombre")
    fun operadoresRecepcion(bid: Long): Flow<List<Usuario>>

    @Query("""SELECT s.id AS sucursalId, s.nombre AS nombre,
        (SELECT COUNT(*) FROM bodegas b WHERE b.sucursalId=s.id AND b.activa=1) AS bodegas,
        COALESCE((SELECT SUM(e.cantidad) FROM existencias e JOIN posiciones p ON p.id=e.posicionId JOIN bodegas b ON b.id=p.bodegaId WHERE b.sucursalId=s.id),0) AS unidadesBodega,
        COALESCE((SELECT SUM(et.cantidad) FROM existencias_tienda et WHERE et.sucursalId=s.id),0) AS unidadesTienda,
        (SELECT COUNT(*) FROM incidencias_caja i JOIN cajas c ON c.id=i.cajaId JOIN bodegas b ON b.id=CASE WHEN c.tipo='SALIDA' THEN c.bodegaOrigenId ELSE c.bodegaDestinoId END WHERE b.sucursalId=s.id AND i.estado='PENDIENTE') AS incidencias
        FROM sucursales s ORDER BY s.nombre""")
    fun indicadoresGlobales(): Flow<List<IndicadorSucursal>>

    @Query("""SELECT s.id AS sucursalId, s.nombre AS sucursal, p.codigo AS codigo,
        p.nombre AS nombre, COALESCE(et.cantidad,0) AS disponible,
        COALESCE(ms.minimo,p.minimo) AS minimo
        FROM sucursales s CROSS JOIN productos p
        LEFT JOIN existencias_tienda et ON et.sucursalId=s.id AND et.productoId=p.id
        LEFT JOIN minimos_sucursal ms ON ms.sucursalId=s.id AND ms.productoId=p.id
        WHERE COALESCE(et.cantidad,0)<=COALESCE(ms.minimo,p.minimo)
        ORDER BY s.nombre, p.nombre""")
    fun faltantesGlobales(): Flow<List<ProductoBajoMinimo>>

    @Query("SELECT * FROM cajas ORDER BY fecha,id")
    suspend fun todasLasCajas(): List<Caja>
    @Query("SELECT * FROM detalle_caja ORDER BY cajaId,id")
    suspend fun todosLosDetallesCaja(): List<DetalleCaja>
    @Query("SELECT * FROM eventos_caja ORDER BY fecha,id")
    suspend fun todosEventosCaja(): List<EventoCaja>
    @Query("SELECT * FROM incidencias_caja ORDER BY fecha,id")
    suspend fun todasLasIncidencias(): List<IncidenciaCaja>
    @Query("SELECT * FROM eventos_administracion ORDER BY fecha,id")
    suspend fun todosEventosAdministracion(): List<EventoAdministrativo>

    // historial completo para exportacion
    @Query("SELECT * FROM movimientos ORDER BY fecha DESC,id DESC")
    suspend fun todosMovimientosBodega(): List<Movimiento>

    @Query("SELECT * FROM movimientos_tienda ORDER BY fecha DESC,id DESC")
    suspend fun todosMovimientosTienda(): List<MovimientoTienda>

    @Query("SELECT * FROM eventos_solicitud_externa ORDER BY fecha DESC,id DESC")
    suspend fun todosEventosExterno(): List<EventoSolicitudExterna>

    @Query("SELECT * FROM movimientos ORDER BY fecha DESC, id DESC LIMIT 60")
    fun movimientosGlobalesBodega(): Flow<List<Movimiento>>

    @Query("SELECT * FROM movimientos_tienda ORDER BY fecha DESC, id DESC LIMIT 60")
    fun movimientosGlobalesTienda(): Flow<List<MovimientoTienda>>
}
