package com.example.boxview.modelo

// Modelo: Entidades de compras y solicitudes externas.
import androidx.room.*

// solicitud de compra para una bodega
@Entity(tableName = "solicitudes_externas", indices = [Index(value = ["sucursalId", "estado"]), Index(value = ["bodegaId"])])
data class SolicitudExterna(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sucursalId: Long,
    val bodegaId: Long,
    val solicitanteId: Long,
    val motivo: String,
    val estado: String = "PENDIENTE_ADMIN",
    val fecha: Long = System.currentTimeMillis(),
    val fechaRevision: Long? = null,
    val revisadoPor: Long? = null,
    val fechaDecision: Long? = null,
    val decididoPor: Long? = null,
    val observacion: String = "",
    val entregaCajaId: Long? = null,
    @ColumnInfo(defaultValue = "''") val proveedorCotizado: String = ""
)

@Entity(tableName = "lineas_solicitud_externa", indices = [Index(value = ["solicitudId"])])
data class LineaSolicitudExterna(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val solicitudId: Long,
    val productoId: Long,
    val cantidad: Int,
    val precioUnitarioCotizado: Long? = null // CLP; exclusivo de administración y gerencia
)

@Entity(tableName = "eventos_solicitud_externa", indices = [Index(value = ["solicitudId"])])
data class EventoSolicitudExterna(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val solicitudId: Long,
    val usuarioId: Long,
    val accion: String,
    val detalle: String = "",
    val fecha: Long = System.currentTimeMillis()
)

// indicadores para gerencia
data class IndicadorSucursal(
    val sucursalId: Long,
    val nombre: String,
    val bodegas: Int,
    val unidadesBodega: Long,
    val unidadesTienda: Long,
    val incidencias: Int
)

data class ProductoBajoMinimo(
    val sucursalId: Long,
    val sucursal: String,
    val codigo: String,
    val nombre: String,
    val disponible: Int,
    val minimo: Int
)

// Montos derivados de cotizaciones administrativas, no de precios de productos en bodega.
data class ResumenFinancieroSucursal(
    val sucursalId: Long,
    val sucursal: String,
    val totalAprobado: Long,
    val totalRecibido: Long,
    val totalPendiente: Long,
    val solicitudesPendientes: Int,
    val recepcionesCompletadas: Int,
    val solicitudesSinCotizacion: Int
)
