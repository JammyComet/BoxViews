package com.example.boxview.modelo

// Modelo: Operaciones Room de inventarios.
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface InventariosDao {
    // Operacion que modifica datos locales.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarEstanteVacio(valor: VerificacionEstanteVacio)

    // Consulta observable para las pantallas.
    @Query("SELECT * FROM verificaciones_estante_vacio WHERE tipo=:tipo AND inventarioId=:id ORDER BY posicionId")
    fun estantesVacios(tipo: String, id: Long): Flow<List<VerificacionEstanteVacio>>

    @Query("SELECT * FROM verificaciones_estante_vacio WHERE tipo=:tipo AND inventarioId=:id")
    suspend fun estantesVaciosActuales(tipo: String, id: Long): List<VerificacionEstanteVacio>

    @Query("DELETE FROM verificaciones_estante_vacio WHERE tipo=:tipo AND inventarioId=:id AND posicionId=:pos")
    suspend fun quitarEstanteVacio(tipo: String, id: Long, pos: Long): Int

    @Query("DELETE FROM verificaciones_estante_vacio WHERE tipo=:tipo AND inventarioId=:id")
    suspend fun borrarEstantesVacios(tipo: String, id: Long): Int

    @Query("""DELETE FROM verificaciones_estante_vacio WHERE tipo='BODEGA' AND inventarioId IN
        (SELECT id FROM periodos_inventario WHERE inventarioBodegaId=:id)""")
    suspend fun borrarVaciosInventarioBodegaGeneral(id: Long): Int

    @Query("DELETE FROM borrador_inventario WHERE periodoId=:id AND posicionId=:pos")
    suspend fun borrarBorradorEstante(id: Long, pos: Long): Int

    @Query("SELECT * FROM borrador_inventario WHERE periodoId=:id ORDER BY id")
    suspend fun borradorActualInventario(id: Long): List<BorradorInventario>

    @Insert suspend fun guardarPeriodoInventario(valor: PeriodoInventario)

    @Insert suspend fun guardarLineaInventario(valor: LineaInventario): Long

    @Query("SELECT * FROM borrador_inventario WHERE periodoId=:id ORDER BY id")
    fun borradorInventario(id: Long): Flow<List<BorradorInventario>>

    @Query("DELETE FROM borrador_inventario WHERE periodoId=:id")
    suspend fun borrarBorradorInventario(id: Long)

    @Insert suspend fun guardarFilaBorrador(valor: BorradorInventario): Long

    @Query("SELECT COUNT(*) FROM periodos_inventario") suspend fun numeroPeriodos(): Int

    @Query("SELECT * FROM periodos_inventario WHERE bodegaId=:bid AND asignadoA=:uid AND inventarioBodegaId IS NOT NULL AND estado IN ('ABIERTO', 'ENTREGADO') ORDER BY CASE WHEN estado='ABIERTO' THEN 0 ELSE 1 END, fechaApertura DESC,id DESC LIMIT 1")
    fun periodoInventario(uid: Long, bid: Long): Flow<PeriodoInventario?>

    @Query("SELECT * FROM periodos_inventario WHERE id=:id LIMIT 1")
    suspend fun periodoPorId(id: Long): PeriodoInventario?

    @Query("SELECT * FROM lineas_inventario WHERE periodoId=:id ORDER BY id")
    fun lineasInventario(id: Long): Flow<List<LineaInventario>>

    @Query("SELECT * FROM lineas_inventario WHERE periodoId=:id ORDER BY id")
    suspend fun lineasInventarioActuales(id: Long): List<LineaInventario>

    @Insert suspend fun guardarAjusteInventario(valor: AjusteInventario): Long

    @Query("SELECT * FROM ajustes_inventario WHERE periodoId=:id ORDER BY id")
    fun ajustesInventario(id: Long): Flow<List<AjusteInventario>>

    @Query("SELECT * FROM ajustes_inventario WHERE periodoId=:id ORDER BY id")
    suspend fun ajustesInventarioActuales(id: Long): List<AjusteInventario>

    @Query("UPDATE existencias SET cantidad=:nueva WHERE id=:id AND cantidad=:anterior")
    suspend fun ajustarExistenciaSiCoincide(id: Long, anterior: Int, nueva: Int): Int

    @Query("""UPDATE periodos_inventario SET verificacionFisicaPor=:uid, fechaVerificacionFisica=:fecha
        WHERE id=:id AND estado='ABIERTO' AND asignadoA=:uid AND bodegaId=:bid""")
    suspend fun guardarVerificacionFisica(id: Long, uid: Long, bid: Long, fecha: Long): Int

    @Query("UPDATE periodos_inventario SET estado='ENTREGADO' WHERE id=:id AND estado='ABIERTO' AND asignadoA=:uid AND bodegaId=:bid")
    suspend fun entregarPeriodo(id: Long, uid: Long, bid: Long): Int

    @Query("SELECT * FROM periodos_inventario WHERE bodegaId=:bid ORDER BY fechaApertura DESC, id DESC")
    fun periodosJefatura(bid: Long): Flow<List<PeriodoInventario>>

    @Query("SELECT COALESCE(MAX(id),0)+1 FROM periodos_inventario")
    suspend fun siguienteIdPeriodo(): Long

    @Query("SELECT COUNT(*) FROM periodos_inventario WHERE bodegaId=:bid AND asignadoA=:uid AND nombre=:nombre COLLATE NOCASE")
    suspend fun existePeriodoMes(bid: Long, uid: Long, nombre: String): Int

    @Query("SELECT COUNT(*) FROM periodos_inventario WHERE bodegaId=:bid AND asignadoA=:uid AND inventarioBodegaId IS NOT NULL AND estado IN ('ABIERTO','ENTREGADO')")
    suspend fun periodoPendiente(bid: Long, uid: Long): Int

    @Query("UPDATE periodos_inventario SET estado='CONCILIADO' WHERE id=:id AND estado='ENTREGADO'")
    suspend fun conciliarPeriodo(id: Long): Int

    @Query("UPDATE periodos_inventario SET estado='CERRADO' WHERE id=:id AND estado='ENTREGADO'")
    suspend fun cerrarPeriodo(id: Long): Int

    @Insert suspend fun guardarInventarioBodega(valor: InventarioBodega)

    @Insert suspend fun guardarZonaInventario(valor: ZonaInventario)

    @Insert suspend fun guardarReferenciaInventario(valor: ReferenciaInventario)

    @Query("SELECT COALESCE(MAX(id),0)+1 FROM inventarios_bodega")
    suspend fun siguienteIdInventarioBodega(): Long

    @Query("SELECT COUNT(*) FROM inventarios_bodega WHERE bodegaId=:bid AND nombre=:nombre COLLATE NOCASE")
    suspend fun existeInventarioBodegaMes(bid: Long, nombre: String): Int

    @Query("SELECT COUNT(*) FROM inventarios_bodega WHERE bodegaId=:bid AND estado IN ('ABIERTO','PENDIENTE_REVISION')")
    suspend fun inventarioBodegaPendiente(bid: Long): Int

    @Query("SELECT * FROM inventarios_bodega WHERE bodegaId=:bid ORDER BY fechaApertura DESC,id DESC")
    fun inventariosBodega(bid: Long): Flow<List<InventarioBodega>>

    @Query("SELECT * FROM inventarios_bodega WHERE id=:id LIMIT 1")
    suspend fun inventarioBodegaPorId(id: Long): InventarioBodega?

    @Query("SELECT * FROM zonas_inventario WHERE inventarioId=:id ORDER BY posicionId")
    fun zonasInventario(id: Long): Flow<List<ZonaInventario>>

    @Query("SELECT * FROM zonas_inventario WHERE inventarioId=:id ORDER BY posicionId")
    suspend fun zonasInventarioActuales(id: Long): List<ZonaInventario>

    @Query("SELECT posicionId FROM zonas_inventario WHERE periodoId=:periodoId ORDER BY posicionId")
    fun zonasPeriodo(periodoId: Long): Flow<List<Long>>

    @Query("SELECT posicionId FROM zonas_inventario WHERE periodoId=:periodoId ORDER BY posicionId")
    suspend fun zonasPeriodoActuales(periodoId: Long): List<Long>

    @Query("SELECT * FROM referencias_inventario WHERE inventarioId=:id ORDER BY posicionId,productoId")
    suspend fun referenciasInventario(id: Long): List<ReferenciaInventario>

    @Query("SELECT * FROM referencias_inventario WHERE inventarioId=:id ORDER BY posicionId,productoId")
    fun observarReferenciasInventario(id: Long): Flow<List<ReferenciaInventario>>

    // Incluye los intervalos ENTRE conteos parciales de la campaña mensual.
    @Query("SELECT COUNT(*) FROM campanias_mensuales c JOIN posiciones p ON p.bodegaId=c.ubicacionId WHERE p.id=:pos AND c.tipo='BODEGA' AND c.estado='ABIERTA' AND p.tipo!='RECEPCION'")
    suspend fun posicionCongeladaPorCampania(pos: Long): Int

    @Query("SELECT COUNT(*) FROM zonas_inventario z JOIN inventarios_bodega i ON i.id=z.inventarioId WHERE z.posicionId=:pos AND i.estado IN ('ABIERTO','PENDIENTE_REVISION')")
    suspend fun posicionBloqueada(pos: Long): Int

    @Query("SELECT COUNT(*) FROM zonas_inventario WHERE inventarioId=:id AND (operadorId=0 OR periodoId=0)")
    suspend fun zonasSinAsignar(id: Long): Int

    @Query("UPDATE zonas_inventario SET operadorId=:operador,periodoId=:periodo WHERE inventarioId=:inventario AND posicionId=:pos AND operadorId=0 AND periodoId=0")
    suspend fun asignarZonaPendiente(inventario: Long, pos: Long, operador: Long, periodo: Long): Int

    @Query("SELECT COUNT(*) FROM periodos_inventario WHERE inventarioBodegaId=:id AND asignadoA=:usuario")
    suspend fun tareasDeOperador(id: Long, usuario: Long): Int

    @Query("SELECT id FROM periodos_inventario WHERE inventarioBodegaId=:id AND asignadoA=:usuario AND estado='ABIERTO' ORDER BY id DESC LIMIT 1")
    suspend fun tareaDeOperador(id: Long, usuario: Long): Long?

    @Query("DELETE FROM ajustes_inventario WHERE periodoId IN (SELECT id FROM periodos_inventario WHERE inventarioBodegaId=:id)")
    suspend fun borrarAjustesInventarioGeneral(id: Long): Int
    @Query("DELETE FROM lineas_inventario WHERE periodoId IN (SELECT id FROM periodos_inventario WHERE inventarioBodegaId=:id)")
    suspend fun borrarLineasInventarioGeneral(id: Long): Int
    @Query("DELETE FROM borrador_inventario WHERE periodoId IN (SELECT id FROM periodos_inventario WHERE inventarioBodegaId=:id)")
    suspend fun borrarBorradoresInventarioGeneral(id: Long): Int
    @Query("DELETE FROM periodos_inventario WHERE inventarioBodegaId=:id")
    suspend fun borrarTareasInventarioGeneral(id: Long): Int
    @Query("DELETE FROM referencias_inventario WHERE inventarioId=:id")
    suspend fun borrarReferenciasInventarioGeneral(id: Long): Int
    @Query("DELETE FROM zonas_inventario WHERE inventarioId=:id")
    suspend fun borrarZonasInventarioGeneral(id: Long): Int
    @Query("DELETE FROM inventarios_bodega WHERE id=:id AND estado IN ('ABIERTO','PENDIENTE_REVISION')")
    suspend fun borrarInventarioGeneral(id: Long): Int

    @Query("SELECT COUNT(*) FROM periodos_inventario WHERE inventarioBodegaId=:id AND estado!='ENTREGADO'")
    suspend fun tareasSinEntregar(id: Long): Int

    @Query("SELECT COUNT(*) FROM periodos_inventario WHERE inventarioBodegaId=:id AND estado='ENTREGADO'")
    suspend fun tareasEntregadas(id: Long): Int

    @Query("""UPDATE inventarios_bodega SET estado='PENDIENTE_REVISION' WHERE id=:id AND estado='ABIERTO'
       AND NOT EXISTS (SELECT 1 FROM zonas_inventario WHERE inventarioId=:id AND (operadorId=0 OR periodoId=0))
       AND EXISTS (SELECT 1 FROM periodos_inventario WHERE inventarioBodegaId=:id)
       AND NOT EXISTS (SELECT 1 FROM periodos_inventario WHERE inventarioBodegaId=:id AND estado!='ENTREGADO')""")
    suspend fun marcarInventarioParaRevision(id: Long): Int

    @Query("UPDATE inventarios_bodega SET estado=:estado, revisadoPor=:jefeId WHERE id=:id AND estado='PENDIENTE_REVISION'")
    suspend fun finalizarInventarioBodega(id: Long, estado: String, jefeId: Long): Int

    @Query("UPDATE periodos_inventario SET estado=:estado WHERE inventarioBodegaId=:id AND estado='ENTREGADO'")
    suspend fun finalizarTareasInventario(id: Long, estado: String): Int

    @Query("SELECT * FROM periodos_inventario WHERE inventarioBodegaId=:id ORDER BY id")
    fun tareasInventarioBodega(id: Long): Flow<List<PeriodoInventario>>

    @Query("SELECT * FROM periodos_inventario WHERE inventarioBodegaId=:id ORDER BY id")
    suspend fun tareasInventarioActuales(id: Long): List<PeriodoInventario>

    @Query("UPDATE inventarios_bodega SET estado='ABIERTO' WHERE id=:id AND estado='PENDIENTE_REVISION'")
    suspend fun reabrirInventarioBodega(id: Long): Int

    @Query("UPDATE periodos_inventario SET estado='ABIERTO' WHERE id=:id AND estado='ENTREGADO'")
    suspend fun reabrirTareaInventario(id: Long): Int

    @Query("DELETE FROM lineas_inventario WHERE periodoId=:id")
    suspend fun borrarLineasInventario(id: Long): Int
    // una bodega por jefe
}
