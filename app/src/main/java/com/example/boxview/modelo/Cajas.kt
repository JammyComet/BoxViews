package com.example.boxview.modelo

// Modelo: Entidades de cajas y sus detalles.
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

// datos de la caja
@Entity(tableName = "cajas", indices = [Index(value = ["codigo"], unique = true)])
data class Caja(
    @PrimaryKey val id: Long,
    val codigo: String,
    val tipo: String, // INGRESO o SALIDA
    val origen: String,
    val bodegaOrigenId: Long,
    val bodegaDestinoId: Long,
    val solicitadoPor: String,
    val asignadoA: Long,
    val estado: String, // ESPERADA, ORDEN_ASIGNADA, EN_TRANSITO, EN_REVISION (llegó), RECIBIDA, CON_DIFERENCIA
    val preparadoPor: Long? = null,
    val revisorId: Long? = null,
    val fecha: Long = System.currentTimeMillis(),
    val sucursalDestinoId: Long? = null // Para salidas hacia tienda; bodegaDestinoId conserva ingresos de proveedor
)

@Entity(tableName = "detalle_caja", indices = [Index(value = ["cajaId", "productoId"], unique = true)])
data class DetalleCaja(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cajaId: Long,
    val productoId: Long,
    val cantidadEsperada: Int,
    val cantidadVerificada: Int = 0, // preparado en origen
    val posicionId: Long? = null, // posición de origen
    @ColumnInfo(defaultValue = "0") val cantidadRecibida: Int = 0, // unidades aceptadas e ingresadas
    val posicionDestinoId: Long? = null,
    val cantidadContadaFisica: Int? = null // unidades verificadas antes de aceptar
)

@Entity(tableName = "eventos_caja")
data class EventoCaja(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cajaId: Long,
    val usuarioId: Long,
    val accion: String,
    val observacion: String = "",
    val fecha: Long = System.currentTimeMillis()
)

// diferencias de recepcion
@Entity(tableName = "incidencias_caja")
data class IncidenciaCaja(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cajaId: Long,
    val productoId: Long,
    val esperado: Int,
    val contado: Int,
    val estado: String = "PENDIENTE",
    val fecha: Long = System.currentTimeMillis()
)
