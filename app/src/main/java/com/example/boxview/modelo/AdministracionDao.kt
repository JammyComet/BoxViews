package com.example.boxview.modelo

// Modelo: Consultas Room para administradores.
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AdministracionDao {
    // Consulta observable para las pantallas.
    @Query("SELECT s.* FROM sucursales s JOIN usuario_sucursal us ON us.sucursalId=s.id JOIN usuarios u ON u.id=us.usuarioId WHERE us.usuarioId=:uid AND u.rol='ADMIN_SUCURSAL' AND u.activo=1 ORDER BY s.nombre")
    fun sucursalesAdministradas(uid: Long): Flow<List<Sucursal>>

    @Query("SELECT COUNT(*) FROM usuarios u JOIN usuario_sucursal us ON us.usuarioId=u.id WHERE u.id=:uid AND us.sucursalId=:sid AND u.rol='ADMIN_SUCURSAL' AND u.activo=1")
    suspend fun administradorAutorizado(uid: Long, sid: Long): Int

    @Query("SELECT COUNT(*) FROM usuarios u JOIN usuario_sucursal us ON us.usuarioId=u.id WHERE u.id=:uid AND us.sucursalId=:sid AND u.rol='ADMIN_SUCURSAL' AND u.activo=1")
    fun observarAdministrador(uid: Long, sid: Long): Flow<Int>

    @Query("SELECT * FROM bodegas WHERE sucursalId=:sid ORDER BY activa DESC, nombre")
    fun bodegasAdministradas(sid: Long): Flow<List<Bodega>>

    @Query("SELECT * FROM bodegas WHERE id=:bid LIMIT 1")
    suspend fun bodegaAdministradaPorId(bid: Long): Bodega?

    @Query("SELECT COALESCE(MAX(id),0)+1 FROM bodegas")
    suspend fun siguienteIdBodega(): Long

    @Query("SELECT COUNT(*) FROM bodegas WHERE sucursalId=:sid AND LOWER(nombre)=LOWER(:nombre)")
    suspend fun nombreBodegaExiste(sid: Long, nombre: String): Int

    @Query("UPDATE bodegas SET nombre=:nombre WHERE id=:bid AND sucursalId=:sid")
    suspend fun renombrarBodegaAdmin(bid: Long, sid: Long, nombre: String): Int

    @Query("UPDATE bodegas SET activa=:activa WHERE id=:bid AND sucursalId=:sid")
    suspend fun estadoBodegaAdmin(bid: Long, sid: Long, activa: Boolean): Int

    @Query("SELECT COALESCE(SUM(e.cantidad),0) FROM existencias e JOIN posiciones p ON p.id=e.posicionId WHERE p.bodegaId=:bid")
    suspend fun stockTotalBodega(bid: Long): Long

    @Query("SELECT COUNT(*) FROM cajas WHERE (bodegaOrigenId=:bid OR bodegaDestinoId=:bid) AND estado NOT IN ('RECIBIDA','CON_DIFERENCIA','CANCELADA')")
    suspend fun cajasActivasBodega(bid: Long): Int

    @Query("""SELECT COUNT(*) FROM solicitudes_reposicion s LEFT JOIN cajas c ON c.id=s.cajaId
        WHERE s.bodegaId=:bid AND (s.estado='PENDIENTE' OR
            (s.estado='ATENDIDA' AND (c.id IS NULL OR c.estado NOT IN ('RECIBIDA','CON_DIFERENCIA','CANCELADA'))))""")
    suspend fun solicitudesActivasBodega(bid: Long): Int

    @Query("""SELECT COUNT(*) FROM solicitudes_externas WHERE bodegaId=:bid AND
        estado IN ('PENDIENTE_ADMIN','EN_GERENCIA','APROBADA','ENTREGA_PROGRAMADA')""")
    suspend fun solicitudesExternasActivasBodega(bid: Long): Int

    @Query("SELECT COUNT(*) FROM usuario_bodega ub JOIN usuarios u ON u.id=ub.usuarioId WHERE ub.bodegaId=:bid AND u.activo=1")
    suspend fun personalActivoBodega(bid: Long): Int

    @Query("SELECT COUNT(*) FROM inventarios_bodega WHERE bodegaId=:bid AND estado IN ('ABIERTO','PENDIENTE_REVISION')")
    suspend fun inventariosAbiertosBodega(bid: Long): Int

    @Query("SELECT * FROM configuracion_sucursal WHERE sucursalId=:sid LIMIT 1")
    fun configuracionSucursal(sid: Long): Flow<ConfiguracionSucursal?>

    @Query("SELECT * FROM configuracion_sucursal WHERE sucursalId=:sid LIMIT 1")
    suspend fun configuracionSucursalActual(sid: Long): ConfiguracionSucursal?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun crearConfiguracionSucursal(valor: ConfiguracionSucursal): Long

    // Operacion que modifica datos locales.
    @Query("UPDATE configuracion_sucursal SET bodegaPrincipalId=:bid WHERE sucursalId=:sid")
    suspend fun guardarPrincipal(sid: Long, bid: Long): Int

    @Query("""SELECT u.* FROM usuarios u WHERE
        (EXISTS (SELECT 1 FROM usuario_sucursal us WHERE us.usuarioId=u.id AND us.sucursalId=:sid)
        OR EXISTS (SELECT 1 FROM usuario_bodega ub JOIN bodegas b ON b.id=ub.bodegaId
            WHERE ub.usuarioId=u.id AND b.sucursalId=:sid)
        OR (u.sucursalResponsableId=:sid
            AND NOT EXISTS (SELECT 1 FROM usuario_sucursal us WHERE us.usuarioId=u.id)
            AND NOT EXISTS (SELECT 1 FROM usuario_bodega ub WHERE ub.usuarioId=u.id)))
        AND u.rol IN ('ADMIN_SUCURSAL','JEFE_BODEGA','JEFE_TIENDA','OPERADOR_BODEGA','TRABAJADOR_TIENDA')
        ORDER BY u.activo DESC,u.nombre""")
    fun empleadosSucursal(sid: Long): Flow<List<Usuario>>

    // impide modificar cuentas con permisos en otras sucursales
    @Query("""SELECT COUNT(*) FROM usuarios u WHERE u.id=:uid AND u.rol!='ADMIN_SUCURSAL'
        AND (EXISTS (SELECT 1 FROM usuario_sucursal us WHERE us.usuarioId=u.id AND us.sucursalId=:sid)
             OR EXISTS (SELECT 1 FROM usuario_bodega ub JOIN bodegas b ON b.id=ub.bodegaId WHERE ub.usuarioId=u.id AND b.sucursalId=:sid))
        AND NOT EXISTS (SELECT 1 FROM usuario_sucursal us WHERE us.usuarioId=u.id AND us.sucursalId!=:sid)
        AND NOT EXISTS (SELECT 1 FROM usuario_bodega ub JOIN bodegas b ON b.id=ub.bodegaId WHERE ub.usuarioId=u.id AND b.sucursalId!=:sid)""")
    suspend fun empleadoExclusivoSucursal(uid: Long, sid: Long): Int

    // La sucursal responsable es la de menor identificador con asignación vigente.
    // Las demás sucursales solo pueden administrar sus propios permisos locales.
    @Query("""SELECT CASE WHEN :sid = COALESCE((
        SELECT MIN(sucursalId) FROM (
            SELECT us.sucursalId AS sucursalId FROM usuario_sucursal us WHERE us.usuarioId=:uid
            UNION SELECT b.sucursalId AS sucursalId FROM usuario_bodega ub
            JOIN bodegas b ON b.id=ub.bodegaId WHERE ub.usuarioId=:uid
        )
    ), (SELECT sucursalResponsableId FROM usuarios WHERE id=:uid))
    AND (SELECT rol FROM usuarios WHERE id=:uid) IN
        ('JEFE_BODEGA','OPERADOR_BODEGA','JEFE_TIENDA','TRABAJADOR_TIENDA') THEN 1 ELSE 0 END""")
    suspend fun sucursalResponsableEmpleado(uid: Long, sid: Long): Int

    @Query("""SELECT COUNT(*) FROM usuarios u WHERE u.id=:uid AND u.rol='OPERADOR_BODEGA'
        AND EXISTS (SELECT 1 FROM usuario_bodega ub JOIN bodegas b ON b.id=ub.bodegaId
            WHERE ub.usuarioId=:uid AND b.sucursalId=:sid)""")
    suspend fun operadorDeSucursal(uid: Long, sid: Long): Int

    @Query("UPDATE usuarios SET sucursalResponsableId=:sid WHERE id=:uid AND rol='TRABAJADOR_TIENDA'")
    suspend fun guardarResponsableTrabajadorTienda(uid: Long, sid: Long): Int

    @Query("UPDATE usuarios SET nombre=:nombre, correo=:correo WHERE id=:uid")
    suspend fun editarDatosEmpleado(uid: Long, nombre: String, correo: String): Int

    @Query("UPDATE usuarios SET activo=:activo WHERE id=:uid")
    suspend fun activarEmpleado(uid: Long, activo: Boolean): Int

    @Query("SELECT sucursalId FROM usuario_sucursal WHERE usuarioId=:uid")
    suspend fun tiendasActualesEmpleado(uid: Long): List<Long>

    @Query("SELECT b.* FROM bodegas b JOIN usuario_bodega ub ON ub.bodegaId=b.id WHERE ub.usuarioId=:uid")
    suspend fun bodegasActualesEmpleado(uid: Long): List<Bodega>

    @Query("SELECT COUNT(*) FROM usuario_bodega WHERE usuarioId=:uid AND bodegaId=:bid")
    suspend fun empleadoAsignadoBodega(uid: Long, bid: Long): Int

    @Query("DELETE FROM usuario_bodega WHERE usuarioId=:uid AND bodegaId=:bid")
    suspend fun retirarBodegaAdmin(uid: Long, bid: Long): Int

    @Query("SELECT * FROM minimos_sucursal WHERE sucursalId=:sid")
    fun minimosSucursal(sid: Long): Flow<List<MinimoSucursal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarMinimoSucursal(valor: MinimoSucursal): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun guardarMinimoSiFalta(valor: MinimoSucursal): Long

    @Query("DELETE FROM minimos_sucursal WHERE sucursalId=:sid AND productoId=:pid")
    suspend fun borrarMinimoSucursal(sid: Long, pid: Long): Int

    @Insert
    suspend fun guardarEventoAdministrativo(evento: EventoAdministrativo): Long

    @Query("SELECT * FROM eventos_administracion WHERE sucursalId=:sid ORDER BY fecha DESC,id DESC LIMIT 100")
    fun historialAdministrativo(sid: Long): Flow<List<EventoAdministrativo>>
}
