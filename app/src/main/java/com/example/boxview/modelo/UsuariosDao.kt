package com.example.boxview.modelo

// Modelo: Consultas Room de usuarios y permisos.
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuariosDao {
    // Operacion que modifica datos locales.
    @Query("SELECT COUNT(*) FROM usuarios") suspend fun numeroUsuarios(): Int

    @Insert suspend fun guardarUsuario(valor: Usuario)

    @Insert suspend fun guardarPermiso(valor: UsuarioBodega)

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun usuarioPorCorreo(correo: String): Usuario?

    @Query("SELECT COUNT(*) FROM usuario_bodega b INNER JOIN usuarios u ON b.usuarioId = u.id WHERE b.usuarioId=:uid AND b.bodegaId=:bid AND EXISTS (SELECT 1 FROM bodegas bo WHERE bo.id=b.bodegaId AND bo.activa=1) AND u.activo=1 AND u.rol='OPERADOR_BODEGA'")
    suspend fun autorizado(uid: Long, bid: Long): Int

    // Consulta observable para las pantallas.
    @Query("SELECT b.* FROM bodegas b JOIN usuario_bodega ub ON ub.bodegaId=b.id JOIN usuarios u ON u.id=ub.usuarioId WHERE ub.usuarioId=:uid AND b.activa=1 AND u.activo=1 AND u.rol IN ('OPERADOR_BODEGA','JEFE_BODEGA') ORDER BY b.nombre")
    fun bodegasAutorizadas(uid: Long): Flow<List<Bodega>>

    @Query("SELECT COUNT(*) FROM usuario_bodega ub JOIN usuarios u ON u.id=ub.usuarioId JOIN bodegas b ON b.id=ub.bodegaId WHERE ub.usuarioId=:uid AND ub.bodegaId=:bid AND u.activo=1 AND b.activa=1 AND u.rol=:rol AND :rol IN ('OPERADOR_BODEGA','JEFE_BODEGA')")
    fun observarAccesoBodega(uid: Long, bid: Long, rol: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM usuario_bodega ub JOIN usuarios u ON ub.usuarioId=u.id JOIN bodegas b ON b.id=ub.bodegaId WHERE ub.usuarioId=:uid AND ub.bodegaId=:bid AND u.activo=1 AND b.activa=1 AND u.rol IN ('OPERADOR_BODEGA','JEFE_BODEGA')")
    suspend fun accesoBodega(uid: Long, bid: Long): Int

    @Query("SELECT COUNT(*) FROM usuario_bodega ub JOIN usuarios u ON ub.usuarioId=u.id JOIN bodegas b ON b.id=ub.bodegaId WHERE ub.usuarioId=:uid AND ub.bodegaId=:bid AND u.activo=1 AND b.activa=1 AND u.rol='JEFE_BODEGA'")
    suspend fun autorizadoJefe(uid: Long, bid: Long): Int

    @Query("SELECT COUNT(*) FROM usuarios u WHERE u.id=:uid AND u.rol='OPERADOR_BODEGA' AND u.activo=1 AND NOT EXISTS (SELECT 1 FROM usuario_bodega ub WHERE ub.usuarioId=u.id)")
    suspend fun operadorSinBodegas(uid: Long): Int

    @Query("SELECT COALESCE(MAX(id),0)+1 FROM usuarios")
    suspend fun siguienteIdUsuario(): Long

    @Query("SELECT u.* FROM usuarios u JOIN usuario_bodega ub ON ub.usuarioId=u.id WHERE u.activo=1 AND u.rol='OPERADOR_BODEGA' AND ub.bodegaId=:bid ORDER BY u.nombre")
    fun operadoresDeBodega(bid: Long): Flow<List<Usuario>>

    @Query("SELECT COUNT(*) FROM usuarios WHERE rut=:rut")
    suspend fun existeRut(rut: String): Int

    @Query("SELECT * FROM usuarios WHERE id=:id LIMIT 1")
    suspend fun usuarioPorId(id: Long): Usuario?

    @Query("""SELECT u.* FROM usuarios u WHERE u.rol='OPERADOR_BODEGA' AND u.activo=1
        AND NOT EXISTS (SELECT 1 FROM usuario_bodega ub JOIN bodegas b ON b.id=ub.bodegaId
            WHERE ub.usuarioId=u.id AND b.sucursalId!=(SELECT origen.sucursalId FROM bodegas origen WHERE origen.id=:bid))
        ORDER BY u.nombre""")
    fun operadoresDeSucursalDisponibles(bid: Long): Flow<List<Usuario>>

    @Query("SELECT * FROM usuarios WHERE rol='OPERADOR_BODEGA' AND activo=1 ORDER BY nombre")
    fun todosLosOperadores(): Flow<List<Usuario>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun guardarPermisoSiFalta(valor: UsuarioBodega): Long

    @Query("DELETE FROM usuario_bodega WHERE usuarioId=:uid AND bodegaId=:bid")
    suspend fun retirarPermiso(uid: Long, bid: Long): Int

    @Query("SELECT COUNT(*) FROM usuario_bodega ub JOIN usuarios u ON u.id=ub.usuarioId WHERE ub.bodegaId=:bid AND ub.usuarioId=:uid AND u.rol='OPERADOR_BODEGA' AND u.activo=1")
    suspend fun operadorActivoDeBodega(uid: Long, bid: Long): Int

    @Query("DELETE FROM usuario_bodega WHERE usuarioId IN (SELECT id FROM usuarios WHERE rol='JEFE_BODEGA') AND bodegaId NOT IN (SELECT MIN(bodegaId) FROM usuario_bodega AS ub2 WHERE ub2.usuarioId=usuario_bodega.usuarioId)")
    suspend fun limpiarBodegasExtraJefes(): Int

}
