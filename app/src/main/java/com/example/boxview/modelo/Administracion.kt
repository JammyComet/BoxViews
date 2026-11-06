package com.example.boxview.modelo

// Modelo: Entidades de administración de sucursales.
import androidx.room.*

// configuracion de cada sucursal
@Entity(tableName = "configuracion_sucursal")
data class ConfiguracionSucursal(@PrimaryKey val sucursalId: Long, val bodegaPrincipalId: Long? = null)

@Entity(tableName = "minimos_sucursal", primaryKeys = ["sucursalId", "productoId"])
data class MinimoSucursal(val sucursalId: Long, val productoId: Long, val minimo: Int)

// datos del historial administrativo
@Entity(tableName = "eventos_administracion", indices = [Index(value = ["sucursalId"])])
data class EventoAdministrativo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sucursalId: Long, val usuarioId: Long, val accion: String,
    val detalle: String, val fecha: Long = System.currentTimeMillis()
)
