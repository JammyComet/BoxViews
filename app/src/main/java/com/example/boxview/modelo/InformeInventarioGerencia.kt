package com.example.boxview.modelo

// Modelo: Entidades de informes de gerencia.
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// informe guardado al cerrar el inventario
@Entity(tableName = "informes_inventario", indices = [
    Index(value = ["inventarioId"], unique = true),
    Index(value = ["estadoRevision", "fechaCierre"])
])
data class InformeInventario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val inventarioId: Long,
    val sucursalId: Long,
    val bodegaId: Long,
    val sucursalNombre: String,
    val bodegaNombre: String,
    val nombre: String,
    val estadoCierre: String,
    val fechaApertura: Long,
    val fechaCierre: Long,
    val cerradoPor: Long,
    val posiciones: Int,
    val productosContados: Int,
    val productosSinContar: Int,
    val lineasConDiferencia: Int,
    val unidadesSistema: Int,
    val unidadesContadas: Int,
    val ajustesAplicados: Int,
    val estadoRevision: String = "NUEVO",
    val revisadoPor: Long? = null,
    val fechaRevision: Long? = null
)

@Entity(tableName = "detalles_informe_inventario", indices = [Index(value = ["informeId"])])
data class DetalleInformeInventario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val informeId: Long,
    val productoId: Long,
    val codigo: String,
    val producto: String,
    val posicion: String,
    val cantidadSistema: Int,
    val cantidadContada: Int?,
    val ajusteAplicado: Boolean
)
