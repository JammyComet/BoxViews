package com.example.boxview.modelo

// Modelo: Entidades de inventarios y zonas.
import androidx.room.*

// inventario mensual por bodega
@Entity(tableName = "inventarios_bodega", indices = [Index(value = ["bodegaId", "nombre"], unique = true)])
data class InventarioBodega(
    @PrimaryKey val id: Long,
    val bodegaId: Long,
    val nombre: String,
    val estado: String = "ABIERTO", // ABIERTO, PENDIENTE_REVISION, CONCILIADO, CERRADO
    val fechaApertura: Long = System.currentTimeMillis(),
    val revisadoPor: Long? = null,
    val campaniaId: Long? = null
)

@Entity(tableName = "zonas_inventario", primaryKeys = ["inventarioId", "posicionId"])
data class ZonaInventario(
    val inventarioId: Long,
    val posicionId: Long,
    val operadorId: Long,
    val periodoId: Long
)

// saldos al iniciar el conteo
@Entity(tableName = "referencias_inventario", primaryKeys = ["inventarioId", "productoId", "posicionId"])
data class ReferenciaInventario(
    val inventarioId: Long,
    val productoId: Long,
    val posicionId: Long,
    val cantidad: Int
)

// Constancia física de estante declarado vacío durante el inventario.
@Entity(tableName = "verificaciones_estante_vacio", primaryKeys = ["tipo", "inventarioId", "posicionId"])
data class VerificacionEstanteVacio(
    val tipo: String, val inventarioId: Long, val posicionId: Long,
    val usuarioId: Long, val fecha: Long = System.currentTimeMillis()
)
