package com.example.boxview.modelo

// Modelo: Entidades de campañas e inventarios mensuales.
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Campaña mensual. Un estante cuenta una sola vez, aunque se cierre en otro día.
@Entity(tableName = "campanias_mensuales", indices = [Index(value = ["tipo", "ubicacionId", "mes"], unique = true)])
data class CampaniaMensual(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tipo: String, val ubicacionId: Long, val sucursalId: Long, val mes: String,
    val estado: String = "ABIERTA", val fechaInicio: Long = System.currentTimeMillis(),
    val fechaFinal: Long? = null, val revision: String = "NUEVO",
    val revisadaPor: Long? = null, val fechaRevision: Long? = null
)

@Entity(tableName = "zonas_campania", primaryKeys = ["campaniaId", "posicionId"])
data class ZonaCampania(
    val campaniaId: Long, val posicionId: Long, val ruta: String,
    val estado: String = "PENDIENTE", val inventarioId: Long? = null,
    val fechaConteo: Long? = null
)

@Entity(tableName = "detalles_campania", indices = [Index(value = ["campaniaId", "posicionId"])])
data class DetalleCampania(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val campaniaId: Long, val inventarioId: Long, val posicionId: Long,
    val codigo: String, val producto: String, val ruta: String,
    val cantidadSistema: Int, val cantidadContada: Int?,
    val ajusteAplicado: Boolean, val fechaCierre: Long
)

fun mesInventarioActual(): String = SimpleDateFormat("yyyy-MM", Locale.ROOT).format(Date())

@Dao
interface CampaniasMensualesDao {
    // Operacion que modifica datos locales.
    @Insert suspend fun crearCampania(valor: CampaniaMensual): Long
    @Insert suspend fun crearZona(valor: ZonaCampania)
    @Insert suspend fun guardarDetalle(valor: DetalleCampania): Long

    @Query("SELECT * FROM campanias_mensuales WHERE tipo=:tipo AND ubicacionId=:ubicacion AND mes=:mes LIMIT 1")
    suspend fun buscar(tipo: String, ubicacion: Long, mes: String): CampaniaMensual?

    // Congelación integral de estantes por campaña, aun entre inventarios parciales.
    @Query("SELECT COUNT(*) FROM campanias_mensuales WHERE tipo=:tipo AND ubicacionId=:ubicacion AND estado='ABIERTA'")
    suspend fun campañaActiva(tipo: String, ubicacion: Long): Int

    @Query("SELECT * FROM campanias_mensuales WHERE id=:id LIMIT 1")
    suspend fun buscarId(id: Long): CampaniaMensual?

    // Consulta observable para las pantallas.
    @Query("SELECT * FROM campanias_mensuales WHERE tipo=:tipo AND ubicacionId=:ubicacion ORDER BY mes DESC,id DESC")
    fun listarPorUbicacion(tipo: String, ubicacion: Long): Flow<List<CampaniaMensual>>

    @Query("SELECT * FROM campanias_mensuales WHERE estado='FINALIZADA' ORDER BY CASE WHEN revision='NUEVO' THEN 0 ELSE 1 END,fechaFinal DESC")
    fun finalizadas(): Flow<List<CampaniaMensual>>

    @Query("SELECT COUNT(*) FROM campanias_mensuales WHERE estado='FINALIZADA' AND revision='NUEVO'")
    fun pendientesGerencia(): Flow<Int>

    @Query("SELECT * FROM zonas_campania WHERE campaniaId=:id ORDER BY posicionId")
    fun observarZonas(id: Long): Flow<List<ZonaCampania>>

    @Query("SELECT * FROM zonas_campania WHERE campaniaId=:id ORDER BY posicionId")
    suspend fun zonas(id: Long): List<ZonaCampania>

    @Query("SELECT * FROM detalles_campania WHERE campaniaId=:id ORDER BY posicionId,producto")
    fun observarDetalles(id: Long): Flow<List<DetalleCampania>>

    @Query("SELECT * FROM detalles_campania ORDER BY campaniaId,posicionId,producto")
    suspend fun todosDetalles(): List<DetalleCampania>

    @Query("SELECT * FROM campanias_mensuales ORDER BY mes, id")
    suspend fun todasCampanias(): List<CampaniaMensual>

    @Query("UPDATE zonas_campania SET estado='EN_CURSO',inventarioId=:inventario WHERE campaniaId=:id AND posicionId=:pos AND estado='PENDIENTE' AND inventarioId IS NULL")
    suspend fun reservarZona(id: Long, pos: Long, inventario: Long): Int

    @Query("UPDATE zonas_campania SET estado='PENDIENTE',inventarioId=NULL WHERE campaniaId=:id AND inventarioId=:inventario AND estado='EN_CURSO'")
    suspend fun liberarZonas(id: Long, inventario: Long): Int

    @Query("UPDATE zonas_campania SET estado='CONTADA',fechaConteo=:fecha WHERE campaniaId=:id AND inventarioId=:inventario AND estado='EN_CURSO'")
    suspend fun entregarZonas(id: Long, inventario: Long, fecha: Long): Int

    @Query("SELECT COUNT(*) FROM zonas_campania WHERE campaniaId=:id AND estado!='CONTADA'")
    suspend fun zonasPendientes(id: Long): Int

    @Query("SELECT COUNT(*) FROM zonas_campania WHERE campaniaId=:id")
    suspend fun numeroZonas(id: Long): Int

    @Query("UPDATE campanias_mensuales SET estado='FINALIZADA',fechaFinal=:fecha WHERE id=:id AND estado='ABIERTA' AND NOT EXISTS(SELECT 1 FROM zonas_campania WHERE campaniaId=:id AND estado!='CONTADA')")
    suspend fun finalizar(id: Long, fecha: Long): Int

    @Query("UPDATE campanias_mensuales SET revision='REVISADO',revisadaPor=:uid,fechaRevision=:fecha WHERE id=:id AND estado='FINALIZADA' AND revision='NUEVO'")
    suspend fun revisar(id: Long, uid: Long, fecha: Long): Int
}

fun mesCampaniaInventario(nombre: String): String {
    val meses = listOf("enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre")
    val coincidencia = Regex("^(\\p{L}+)\\s+(20\\d{2})(?:\\b|\\s|-)", RegexOption.IGNORE_CASE).find(nombre.trim())
    if (coincidencia != null) {
        val numero = meses.indexOf(coincidencia.groupValues[1].lowercase(Locale.ROOT))
        if (numero >= 0) return "${coincidencia.groupValues[2]}-${(numero + 1).toString().padStart(2, '0')}"
    }
    return ""
}
