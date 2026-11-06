package com.example.boxview.modelo

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.getTotalChangedRows
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AbastecimientoExternoDao_Impl(
  __db: RoomDatabase,
) : AbastecimientoExternoDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfSolicitudExterna: EntityInsertAdapter<SolicitudExterna>

  private val __insertAdapterOfLineaSolicitudExterna: EntityInsertAdapter<LineaSolicitudExterna>

  private val __insertAdapterOfEventoSolicitudExterna: EntityInsertAdapter<EventoSolicitudExterna>
  init {
    this.__db = __db
    this.__insertAdapterOfSolicitudExterna = object : EntityInsertAdapter<SolicitudExterna>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `solicitudes_externas` (`id`,`sucursalId`,`bodegaId`,`solicitanteId`,`motivo`,`estado`,`fecha`,`fechaRevision`,`revisadoPor`,`fechaDecision`,`decididoPor`,`observacion`,`entregaCajaId`,`proveedorCotizado`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: SolicitudExterna) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.sucursalId)
        statement.bindLong(3, entity.bodegaId)
        statement.bindLong(4, entity.solicitanteId)
        statement.bindText(5, entity.motivo)
        statement.bindText(6, entity.estado)
        statement.bindLong(7, entity.fecha)
        val _tmpFechaRevision: Long? = entity.fechaRevision
        if (_tmpFechaRevision == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpFechaRevision)
        }
        val _tmpRevisadoPor: Long? = entity.revisadoPor
        if (_tmpRevisadoPor == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpRevisadoPor)
        }
        val _tmpFechaDecision: Long? = entity.fechaDecision
        if (_tmpFechaDecision == null) {
          statement.bindNull(10)
        } else {
          statement.bindLong(10, _tmpFechaDecision)
        }
        val _tmpDecididoPor: Long? = entity.decididoPor
        if (_tmpDecididoPor == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmpDecididoPor)
        }
        statement.bindText(12, entity.observacion)
        val _tmpEntregaCajaId: Long? = entity.entregaCajaId
        if (_tmpEntregaCajaId == null) {
          statement.bindNull(13)
        } else {
          statement.bindLong(13, _tmpEntregaCajaId)
        }
        statement.bindText(14, entity.proveedorCotizado)
      }
    }
    this.__insertAdapterOfLineaSolicitudExterna = object :
        EntityInsertAdapter<LineaSolicitudExterna>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `lineas_solicitud_externa` (`id`,`solicitudId`,`productoId`,`cantidad`,`precioUnitarioCotizado`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: LineaSolicitudExterna) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.solicitudId)
        statement.bindLong(3, entity.productoId)
        statement.bindLong(4, entity.cantidad.toLong())
        val _tmpPrecioUnitarioCotizado: Long? = entity.precioUnitarioCotizado
        if (_tmpPrecioUnitarioCotizado == null) {
          statement.bindNull(5)
        } else {
          statement.bindLong(5, _tmpPrecioUnitarioCotizado)
        }
      }
    }
    this.__insertAdapterOfEventoSolicitudExterna = object :
        EntityInsertAdapter<EventoSolicitudExterna>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `eventos_solicitud_externa` (`id`,`solicitudId`,`usuarioId`,`accion`,`detalle`,`fecha`) VALUES (nullif(?, 0),?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: EventoSolicitudExterna) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.solicitudId)
        statement.bindLong(3, entity.usuarioId)
        statement.bindText(4, entity.accion)
        statement.bindText(5, entity.detalle)
        statement.bindLong(6, entity.fecha)
      }
    }
  }

  public override suspend fun nuevaSolicitud(valor: SolicitudExterna): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfSolicitudExterna.insertAndReturnId(_connection, valor)
    _result
  }

  public override suspend fun nuevaLinea(valor: LineaSolicitudExterna): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfLineaSolicitudExterna.insertAndReturnId(_connection, valor)
    _result
  }

  public override suspend fun nuevoEvento(valor: EventoSolicitudExterna): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfEventoSolicitudExterna.insertAndReturnId(_connection,
        valor)
    _result
  }

  public override suspend fun numeroSolicitudes(): Int {
    val _sql: String = "SELECT COUNT(*) FROM solicitudes_externas"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun resumenFinanciero(gerenteId: Long): Flow<List<ResumenFinancieroSucursal>> {
    val _sql: String = """
        |SELECT s.id AS sucursalId, s.nombre AS sucursal,
        |       COALESCE(SUM(CASE WHEN x.estado IN ('APROBADA','ENTREGA_PROGRAMADA','RECIBIDA','CON_DIFERENCIA')
        |           THEN l.cantidad * COALESCE(l.precioUnitarioCotizado,0) ELSE 0 END),0) AS totalAprobado,
        |       COALESCE(SUM(CASE WHEN x.estado IN ('RECIBIDA','CON_DIFERENCIA')
        |           THEN COALESCE(d.cantidadRecibida,0) * COALESCE(l.precioUnitarioCotizado,0) ELSE 0 END),0) AS totalRecibido,
        |       COALESCE(SUM(CASE WHEN x.estado = 'EN_GERENCIA'
        |           THEN l.cantidad * COALESCE(l.precioUnitarioCotizado,0) ELSE 0 END),0) AS totalPendiente,
        |       COUNT(DISTINCT CASE WHEN x.estado='EN_GERENCIA' THEN x.id END) AS solicitudesPendientes,
        |       COUNT(DISTINCT CASE WHEN x.estado IN ('RECIBIDA','CON_DIFERENCIA') THEN x.id END) AS recepcionesCompletadas,
        |       COUNT(DISTINCT CASE WHEN x.estado IN ('EN_GERENCIA','APROBADA','ENTREGA_PROGRAMADA','RECIBIDA','CON_DIFERENCIA')
        |           AND l.precioUnitarioCotizado IS NULL THEN x.id END) AS solicitudesSinCotizacion
        |       FROM sucursales s LEFT JOIN solicitudes_externas x ON x.sucursalId=s.id
        |       LEFT JOIN lineas_solicitud_externa l ON l.solicitudId=x.id
        |       LEFT JOIN detalle_caja d ON d.cajaId=x.entregaCajaId AND d.productoId=l.productoId
        |       WHERE EXISTS(SELECT 1 FROM usuarios u WHERE u.id=? AND u.rol='GERENTE' AND u.activo=1)
        |       GROUP BY s.id,s.nombre ORDER BY s.nombre
        """.trimMargin()
    return createFlow(__db, false, arrayOf("sucursales", "solicitudes_externas",
        "lineas_solicitud_externa", "detalle_caja", "usuarios")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, gerenteId)
        val _columnIndexOfSucursalId: Int = 0
        val _columnIndexOfSucursal: Int = 1
        val _columnIndexOfTotalAprobado: Int = 2
        val _columnIndexOfTotalRecibido: Int = 3
        val _columnIndexOfTotalPendiente: Int = 4
        val _columnIndexOfSolicitudesPendientes: Int = 5
        val _columnIndexOfRecepcionesCompletadas: Int = 6
        val _columnIndexOfSolicitudesSinCotizacion: Int = 7
        val _result: MutableList<ResumenFinancieroSucursal> = mutableListOf()
        while (_stmt.step()) {
          val _item: ResumenFinancieroSucursal
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpSucursal: String
          _tmpSucursal = _stmt.getText(_columnIndexOfSucursal)
          val _tmpTotalAprobado: Long
          _tmpTotalAprobado = _stmt.getLong(_columnIndexOfTotalAprobado)
          val _tmpTotalRecibido: Long
          _tmpTotalRecibido = _stmt.getLong(_columnIndexOfTotalRecibido)
          val _tmpTotalPendiente: Long
          _tmpTotalPendiente = _stmt.getLong(_columnIndexOfTotalPendiente)
          val _tmpSolicitudesPendientes: Int
          _tmpSolicitudesPendientes = _stmt.getLong(_columnIndexOfSolicitudesPendientes).toInt()
          val _tmpRecepcionesCompletadas: Int
          _tmpRecepcionesCompletadas = _stmt.getLong(_columnIndexOfRecepcionesCompletadas).toInt()
          val _tmpSolicitudesSinCotizacion: Int
          _tmpSolicitudesSinCotizacion =
              _stmt.getLong(_columnIndexOfSolicitudesSinCotizacion).toInt()
          _item =
              ResumenFinancieroSucursal(_tmpSucursalId,_tmpSucursal,_tmpTotalAprobado,_tmpTotalRecibido,_tmpTotalPendiente,_tmpSolicitudesPendientes,_tmpRecepcionesCompletadas,_tmpSolicitudesSinCotizacion)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun solicitudesFinancieras(): List<SolicitudExterna> {
    val _sql: String = "SELECT * FROM solicitudes_externas ORDER BY id DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfBodegaId: Int = getColumnIndexOrThrow(_stmt, "bodegaId")
        val _columnIndexOfSolicitanteId: Int = getColumnIndexOrThrow(_stmt, "solicitanteId")
        val _columnIndexOfMotivo: Int = getColumnIndexOrThrow(_stmt, "motivo")
        val _columnIndexOfEstado: Int = getColumnIndexOrThrow(_stmt, "estado")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _columnIndexOfFechaRevision: Int = getColumnIndexOrThrow(_stmt, "fechaRevision")
        val _columnIndexOfRevisadoPor: Int = getColumnIndexOrThrow(_stmt, "revisadoPor")
        val _columnIndexOfFechaDecision: Int = getColumnIndexOrThrow(_stmt, "fechaDecision")
        val _columnIndexOfDecididoPor: Int = getColumnIndexOrThrow(_stmt, "decididoPor")
        val _columnIndexOfObservacion: Int = getColumnIndexOrThrow(_stmt, "observacion")
        val _columnIndexOfEntregaCajaId: Int = getColumnIndexOrThrow(_stmt, "entregaCajaId")
        val _columnIndexOfProveedorCotizado: Int = getColumnIndexOrThrow(_stmt, "proveedorCotizado")
        val _result: MutableList<SolicitudExterna> = mutableListOf()
        while (_stmt.step()) {
          val _item: SolicitudExterna
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpBodegaId: Long
          _tmpBodegaId = _stmt.getLong(_columnIndexOfBodegaId)
          val _tmpSolicitanteId: Long
          _tmpSolicitanteId = _stmt.getLong(_columnIndexOfSolicitanteId)
          val _tmpMotivo: String
          _tmpMotivo = _stmt.getText(_columnIndexOfMotivo)
          val _tmpEstado: String
          _tmpEstado = _stmt.getText(_columnIndexOfEstado)
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          val _tmpFechaRevision: Long?
          if (_stmt.isNull(_columnIndexOfFechaRevision)) {
            _tmpFechaRevision = null
          } else {
            _tmpFechaRevision = _stmt.getLong(_columnIndexOfFechaRevision)
          }
          val _tmpRevisadoPor: Long?
          if (_stmt.isNull(_columnIndexOfRevisadoPor)) {
            _tmpRevisadoPor = null
          } else {
            _tmpRevisadoPor = _stmt.getLong(_columnIndexOfRevisadoPor)
          }
          val _tmpFechaDecision: Long?
          if (_stmt.isNull(_columnIndexOfFechaDecision)) {
            _tmpFechaDecision = null
          } else {
            _tmpFechaDecision = _stmt.getLong(_columnIndexOfFechaDecision)
          }
          val _tmpDecididoPor: Long?
          if (_stmt.isNull(_columnIndexOfDecididoPor)) {
            _tmpDecididoPor = null
          } else {
            _tmpDecididoPor = _stmt.getLong(_columnIndexOfDecididoPor)
          }
          val _tmpObservacion: String
          _tmpObservacion = _stmt.getText(_columnIndexOfObservacion)
          val _tmpEntregaCajaId: Long?
          if (_stmt.isNull(_columnIndexOfEntregaCajaId)) {
            _tmpEntregaCajaId = null
          } else {
            _tmpEntregaCajaId = _stmt.getLong(_columnIndexOfEntregaCajaId)
          }
          val _tmpProveedorCotizado: String
          _tmpProveedorCotizado = _stmt.getText(_columnIndexOfProveedorCotizado)
          _item =
              SolicitudExterna(_tmpId,_tmpSucursalId,_tmpBodegaId,_tmpSolicitanteId,_tmpMotivo,_tmpEstado,_tmpFecha,_tmpFechaRevision,_tmpRevisadoPor,_tmpFechaDecision,_tmpDecididoPor,_tmpObservacion,_tmpEntregaCajaId,_tmpProveedorCotizado)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun lineasFinancieras(): List<LineaSolicitudExterna> {
    val _sql: String = "SELECT * FROM lineas_solicitud_externa ORDER BY solicitudId,id"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSolicitudId: Int = getColumnIndexOrThrow(_stmt, "solicitudId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfCantidad: Int = getColumnIndexOrThrow(_stmt, "cantidad")
        val _columnIndexOfPrecioUnitarioCotizado: Int = getColumnIndexOrThrow(_stmt,
            "precioUnitarioCotizado")
        val _result: MutableList<LineaSolicitudExterna> = mutableListOf()
        while (_stmt.step()) {
          val _item: LineaSolicitudExterna
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSolicitudId: Long
          _tmpSolicitudId = _stmt.getLong(_columnIndexOfSolicitudId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpCantidad: Int
          _tmpCantidad = _stmt.getLong(_columnIndexOfCantidad).toInt()
          val _tmpPrecioUnitarioCotizado: Long?
          if (_stmt.isNull(_columnIndexOfPrecioUnitarioCotizado)) {
            _tmpPrecioUnitarioCotizado = null
          } else {
            _tmpPrecioUnitarioCotizado = _stmt.getLong(_columnIndexOfPrecioUnitarioCotizado)
          }
          _item =
              LineaSolicitudExterna(_tmpId,_tmpSolicitudId,_tmpProductoId,_tmpCantidad,_tmpPrecioUnitarioCotizado)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun solicitudPorId(id: Long): SolicitudExterna? {
    val _sql: String = "SELECT * FROM solicitudes_externas WHERE id=? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfBodegaId: Int = getColumnIndexOrThrow(_stmt, "bodegaId")
        val _columnIndexOfSolicitanteId: Int = getColumnIndexOrThrow(_stmt, "solicitanteId")
        val _columnIndexOfMotivo: Int = getColumnIndexOrThrow(_stmt, "motivo")
        val _columnIndexOfEstado: Int = getColumnIndexOrThrow(_stmt, "estado")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _columnIndexOfFechaRevision: Int = getColumnIndexOrThrow(_stmt, "fechaRevision")
        val _columnIndexOfRevisadoPor: Int = getColumnIndexOrThrow(_stmt, "revisadoPor")
        val _columnIndexOfFechaDecision: Int = getColumnIndexOrThrow(_stmt, "fechaDecision")
        val _columnIndexOfDecididoPor: Int = getColumnIndexOrThrow(_stmt, "decididoPor")
        val _columnIndexOfObservacion: Int = getColumnIndexOrThrow(_stmt, "observacion")
        val _columnIndexOfEntregaCajaId: Int = getColumnIndexOrThrow(_stmt, "entregaCajaId")
        val _columnIndexOfProveedorCotizado: Int = getColumnIndexOrThrow(_stmt, "proveedorCotizado")
        val _result: SolicitudExterna?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpBodegaId: Long
          _tmpBodegaId = _stmt.getLong(_columnIndexOfBodegaId)
          val _tmpSolicitanteId: Long
          _tmpSolicitanteId = _stmt.getLong(_columnIndexOfSolicitanteId)
          val _tmpMotivo: String
          _tmpMotivo = _stmt.getText(_columnIndexOfMotivo)
          val _tmpEstado: String
          _tmpEstado = _stmt.getText(_columnIndexOfEstado)
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          val _tmpFechaRevision: Long?
          if (_stmt.isNull(_columnIndexOfFechaRevision)) {
            _tmpFechaRevision = null
          } else {
            _tmpFechaRevision = _stmt.getLong(_columnIndexOfFechaRevision)
          }
          val _tmpRevisadoPor: Long?
          if (_stmt.isNull(_columnIndexOfRevisadoPor)) {
            _tmpRevisadoPor = null
          } else {
            _tmpRevisadoPor = _stmt.getLong(_columnIndexOfRevisadoPor)
          }
          val _tmpFechaDecision: Long?
          if (_stmt.isNull(_columnIndexOfFechaDecision)) {
            _tmpFechaDecision = null
          } else {
            _tmpFechaDecision = _stmt.getLong(_columnIndexOfFechaDecision)
          }
          val _tmpDecididoPor: Long?
          if (_stmt.isNull(_columnIndexOfDecididoPor)) {
            _tmpDecididoPor = null
          } else {
            _tmpDecididoPor = _stmt.getLong(_columnIndexOfDecididoPor)
          }
          val _tmpObservacion: String
          _tmpObservacion = _stmt.getText(_columnIndexOfObservacion)
          val _tmpEntregaCajaId: Long?
          if (_stmt.isNull(_columnIndexOfEntregaCajaId)) {
            _tmpEntregaCajaId = null
          } else {
            _tmpEntregaCajaId = _stmt.getLong(_columnIndexOfEntregaCajaId)
          }
          val _tmpProveedorCotizado: String
          _tmpProveedorCotizado = _stmt.getText(_columnIndexOfProveedorCotizado)
          _result =
              SolicitudExterna(_tmpId,_tmpSucursalId,_tmpBodegaId,_tmpSolicitanteId,_tmpMotivo,_tmpEstado,_tmpFecha,_tmpFechaRevision,_tmpRevisadoPor,_tmpFechaDecision,_tmpDecididoPor,_tmpObservacion,_tmpEntregaCajaId,_tmpProveedorCotizado)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun solicitudesDeBodega(bid: Long): Flow<List<SolicitudExterna>> {
    val _sql: String =
        "SELECT * FROM solicitudes_externas WHERE bodegaId=? ORDER BY fecha DESC, id DESC"
    return createFlow(__db, false, arrayOf("solicitudes_externas")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, bid)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfBodegaId: Int = getColumnIndexOrThrow(_stmt, "bodegaId")
        val _columnIndexOfSolicitanteId: Int = getColumnIndexOrThrow(_stmt, "solicitanteId")
        val _columnIndexOfMotivo: Int = getColumnIndexOrThrow(_stmt, "motivo")
        val _columnIndexOfEstado: Int = getColumnIndexOrThrow(_stmt, "estado")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _columnIndexOfFechaRevision: Int = getColumnIndexOrThrow(_stmt, "fechaRevision")
        val _columnIndexOfRevisadoPor: Int = getColumnIndexOrThrow(_stmt, "revisadoPor")
        val _columnIndexOfFechaDecision: Int = getColumnIndexOrThrow(_stmt, "fechaDecision")
        val _columnIndexOfDecididoPor: Int = getColumnIndexOrThrow(_stmt, "decididoPor")
        val _columnIndexOfObservacion: Int = getColumnIndexOrThrow(_stmt, "observacion")
        val _columnIndexOfEntregaCajaId: Int = getColumnIndexOrThrow(_stmt, "entregaCajaId")
        val _columnIndexOfProveedorCotizado: Int = getColumnIndexOrThrow(_stmt, "proveedorCotizado")
        val _result: MutableList<SolicitudExterna> = mutableListOf()
        while (_stmt.step()) {
          val _item: SolicitudExterna
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpBodegaId: Long
          _tmpBodegaId = _stmt.getLong(_columnIndexOfBodegaId)
          val _tmpSolicitanteId: Long
          _tmpSolicitanteId = _stmt.getLong(_columnIndexOfSolicitanteId)
          val _tmpMotivo: String
          _tmpMotivo = _stmt.getText(_columnIndexOfMotivo)
          val _tmpEstado: String
          _tmpEstado = _stmt.getText(_columnIndexOfEstado)
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          val _tmpFechaRevision: Long?
          if (_stmt.isNull(_columnIndexOfFechaRevision)) {
            _tmpFechaRevision = null
          } else {
            _tmpFechaRevision = _stmt.getLong(_columnIndexOfFechaRevision)
          }
          val _tmpRevisadoPor: Long?
          if (_stmt.isNull(_columnIndexOfRevisadoPor)) {
            _tmpRevisadoPor = null
          } else {
            _tmpRevisadoPor = _stmt.getLong(_columnIndexOfRevisadoPor)
          }
          val _tmpFechaDecision: Long?
          if (_stmt.isNull(_columnIndexOfFechaDecision)) {
            _tmpFechaDecision = null
          } else {
            _tmpFechaDecision = _stmt.getLong(_columnIndexOfFechaDecision)
          }
          val _tmpDecididoPor: Long?
          if (_stmt.isNull(_columnIndexOfDecididoPor)) {
            _tmpDecididoPor = null
          } else {
            _tmpDecididoPor = _stmt.getLong(_columnIndexOfDecididoPor)
          }
          val _tmpObservacion: String
          _tmpObservacion = _stmt.getText(_columnIndexOfObservacion)
          val _tmpEntregaCajaId: Long?
          if (_stmt.isNull(_columnIndexOfEntregaCajaId)) {
            _tmpEntregaCajaId = null
          } else {
            _tmpEntregaCajaId = _stmt.getLong(_columnIndexOfEntregaCajaId)
          }
          val _tmpProveedorCotizado: String
          _tmpProveedorCotizado = _stmt.getText(_columnIndexOfProveedorCotizado)
          _item =
              SolicitudExterna(_tmpId,_tmpSucursalId,_tmpBodegaId,_tmpSolicitanteId,_tmpMotivo,_tmpEstado,_tmpFecha,_tmpFechaRevision,_tmpRevisadoPor,_tmpFechaDecision,_tmpDecididoPor,_tmpObservacion,_tmpEntregaCajaId,_tmpProveedorCotizado)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun solicitudesDeSucursal(sid: Long): Flow<List<SolicitudExterna>> {
    val _sql: String =
        "SELECT * FROM solicitudes_externas WHERE sucursalId=? ORDER BY fecha DESC, id DESC"
    return createFlow(__db, false, arrayOf("solicitudes_externas")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfBodegaId: Int = getColumnIndexOrThrow(_stmt, "bodegaId")
        val _columnIndexOfSolicitanteId: Int = getColumnIndexOrThrow(_stmt, "solicitanteId")
        val _columnIndexOfMotivo: Int = getColumnIndexOrThrow(_stmt, "motivo")
        val _columnIndexOfEstado: Int = getColumnIndexOrThrow(_stmt, "estado")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _columnIndexOfFechaRevision: Int = getColumnIndexOrThrow(_stmt, "fechaRevision")
        val _columnIndexOfRevisadoPor: Int = getColumnIndexOrThrow(_stmt, "revisadoPor")
        val _columnIndexOfFechaDecision: Int = getColumnIndexOrThrow(_stmt, "fechaDecision")
        val _columnIndexOfDecididoPor: Int = getColumnIndexOrThrow(_stmt, "decididoPor")
        val _columnIndexOfObservacion: Int = getColumnIndexOrThrow(_stmt, "observacion")
        val _columnIndexOfEntregaCajaId: Int = getColumnIndexOrThrow(_stmt, "entregaCajaId")
        val _columnIndexOfProveedorCotizado: Int = getColumnIndexOrThrow(_stmt, "proveedorCotizado")
        val _result: MutableList<SolicitudExterna> = mutableListOf()
        while (_stmt.step()) {
          val _item: SolicitudExterna
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpBodegaId: Long
          _tmpBodegaId = _stmt.getLong(_columnIndexOfBodegaId)
          val _tmpSolicitanteId: Long
          _tmpSolicitanteId = _stmt.getLong(_columnIndexOfSolicitanteId)
          val _tmpMotivo: String
          _tmpMotivo = _stmt.getText(_columnIndexOfMotivo)
          val _tmpEstado: String
          _tmpEstado = _stmt.getText(_columnIndexOfEstado)
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          val _tmpFechaRevision: Long?
          if (_stmt.isNull(_columnIndexOfFechaRevision)) {
            _tmpFechaRevision = null
          } else {
            _tmpFechaRevision = _stmt.getLong(_columnIndexOfFechaRevision)
          }
          val _tmpRevisadoPor: Long?
          if (_stmt.isNull(_columnIndexOfRevisadoPor)) {
            _tmpRevisadoPor = null
          } else {
            _tmpRevisadoPor = _stmt.getLong(_columnIndexOfRevisadoPor)
          }
          val _tmpFechaDecision: Long?
          if (_stmt.isNull(_columnIndexOfFechaDecision)) {
            _tmpFechaDecision = null
          } else {
            _tmpFechaDecision = _stmt.getLong(_columnIndexOfFechaDecision)
          }
          val _tmpDecididoPor: Long?
          if (_stmt.isNull(_columnIndexOfDecididoPor)) {
            _tmpDecididoPor = null
          } else {
            _tmpDecididoPor = _stmt.getLong(_columnIndexOfDecididoPor)
          }
          val _tmpObservacion: String
          _tmpObservacion = _stmt.getText(_columnIndexOfObservacion)
          val _tmpEntregaCajaId: Long?
          if (_stmt.isNull(_columnIndexOfEntregaCajaId)) {
            _tmpEntregaCajaId = null
          } else {
            _tmpEntregaCajaId = _stmt.getLong(_columnIndexOfEntregaCajaId)
          }
          val _tmpProveedorCotizado: String
          _tmpProveedorCotizado = _stmt.getText(_columnIndexOfProveedorCotizado)
          _item =
              SolicitudExterna(_tmpId,_tmpSucursalId,_tmpBodegaId,_tmpSolicitanteId,_tmpMotivo,_tmpEstado,_tmpFecha,_tmpFechaRevision,_tmpRevisadoPor,_tmpFechaDecision,_tmpDecididoPor,_tmpObservacion,_tmpEntregaCajaId,_tmpProveedorCotizado)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun todasSolicitudes(): Flow<List<SolicitudExterna>> {
    val _sql: String = "SELECT * FROM solicitudes_externas ORDER BY fecha DESC, id DESC"
    return createFlow(__db, false, arrayOf("solicitudes_externas")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfBodegaId: Int = getColumnIndexOrThrow(_stmt, "bodegaId")
        val _columnIndexOfSolicitanteId: Int = getColumnIndexOrThrow(_stmt, "solicitanteId")
        val _columnIndexOfMotivo: Int = getColumnIndexOrThrow(_stmt, "motivo")
        val _columnIndexOfEstado: Int = getColumnIndexOrThrow(_stmt, "estado")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _columnIndexOfFechaRevision: Int = getColumnIndexOrThrow(_stmt, "fechaRevision")
        val _columnIndexOfRevisadoPor: Int = getColumnIndexOrThrow(_stmt, "revisadoPor")
        val _columnIndexOfFechaDecision: Int = getColumnIndexOrThrow(_stmt, "fechaDecision")
        val _columnIndexOfDecididoPor: Int = getColumnIndexOrThrow(_stmt, "decididoPor")
        val _columnIndexOfObservacion: Int = getColumnIndexOrThrow(_stmt, "observacion")
        val _columnIndexOfEntregaCajaId: Int = getColumnIndexOrThrow(_stmt, "entregaCajaId")
        val _columnIndexOfProveedorCotizado: Int = getColumnIndexOrThrow(_stmt, "proveedorCotizado")
        val _result: MutableList<SolicitudExterna> = mutableListOf()
        while (_stmt.step()) {
          val _item: SolicitudExterna
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpBodegaId: Long
          _tmpBodegaId = _stmt.getLong(_columnIndexOfBodegaId)
          val _tmpSolicitanteId: Long
          _tmpSolicitanteId = _stmt.getLong(_columnIndexOfSolicitanteId)
          val _tmpMotivo: String
          _tmpMotivo = _stmt.getText(_columnIndexOfMotivo)
          val _tmpEstado: String
          _tmpEstado = _stmt.getText(_columnIndexOfEstado)
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          val _tmpFechaRevision: Long?
          if (_stmt.isNull(_columnIndexOfFechaRevision)) {
            _tmpFechaRevision = null
          } else {
            _tmpFechaRevision = _stmt.getLong(_columnIndexOfFechaRevision)
          }
          val _tmpRevisadoPor: Long?
          if (_stmt.isNull(_columnIndexOfRevisadoPor)) {
            _tmpRevisadoPor = null
          } else {
            _tmpRevisadoPor = _stmt.getLong(_columnIndexOfRevisadoPor)
          }
          val _tmpFechaDecision: Long?
          if (_stmt.isNull(_columnIndexOfFechaDecision)) {
            _tmpFechaDecision = null
          } else {
            _tmpFechaDecision = _stmt.getLong(_columnIndexOfFechaDecision)
          }
          val _tmpDecididoPor: Long?
          if (_stmt.isNull(_columnIndexOfDecididoPor)) {
            _tmpDecididoPor = null
          } else {
            _tmpDecididoPor = _stmt.getLong(_columnIndexOfDecididoPor)
          }
          val _tmpObservacion: String
          _tmpObservacion = _stmt.getText(_columnIndexOfObservacion)
          val _tmpEntregaCajaId: Long?
          if (_stmt.isNull(_columnIndexOfEntregaCajaId)) {
            _tmpEntregaCajaId = null
          } else {
            _tmpEntregaCajaId = _stmt.getLong(_columnIndexOfEntregaCajaId)
          }
          val _tmpProveedorCotizado: String
          _tmpProveedorCotizado = _stmt.getText(_columnIndexOfProveedorCotizado)
          _item =
              SolicitudExterna(_tmpId,_tmpSucursalId,_tmpBodegaId,_tmpSolicitanteId,_tmpMotivo,_tmpEstado,_tmpFecha,_tmpFechaRevision,_tmpRevisadoPor,_tmpFechaDecision,_tmpDecididoPor,_tmpObservacion,_tmpEntregaCajaId,_tmpProveedorCotizado)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun lineasActuales(id: Long): List<LineaSolicitudExterna> {
    val _sql: String = "SELECT * FROM lineas_solicitud_externa WHERE solicitudId=? ORDER BY id"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSolicitudId: Int = getColumnIndexOrThrow(_stmt, "solicitudId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfCantidad: Int = getColumnIndexOrThrow(_stmt, "cantidad")
        val _columnIndexOfPrecioUnitarioCotizado: Int = getColumnIndexOrThrow(_stmt,
            "precioUnitarioCotizado")
        val _result: MutableList<LineaSolicitudExterna> = mutableListOf()
        while (_stmt.step()) {
          val _item: LineaSolicitudExterna
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSolicitudId: Long
          _tmpSolicitudId = _stmt.getLong(_columnIndexOfSolicitudId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpCantidad: Int
          _tmpCantidad = _stmt.getLong(_columnIndexOfCantidad).toInt()
          val _tmpPrecioUnitarioCotizado: Long?
          if (_stmt.isNull(_columnIndexOfPrecioUnitarioCotizado)) {
            _tmpPrecioUnitarioCotizado = null
          } else {
            _tmpPrecioUnitarioCotizado = _stmt.getLong(_columnIndexOfPrecioUnitarioCotizado)
          }
          _item =
              LineaSolicitudExterna(_tmpId,_tmpSolicitudId,_tmpProductoId,_tmpCantidad,_tmpPrecioUnitarioCotizado)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observarLineas(id: Long): Flow<List<LineaSolicitudExterna>> {
    val _sql: String = "SELECT * FROM lineas_solicitud_externa WHERE solicitudId=? ORDER BY id"
    return createFlow(__db, false, arrayOf("lineas_solicitud_externa")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSolicitudId: Int = getColumnIndexOrThrow(_stmt, "solicitudId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfCantidad: Int = getColumnIndexOrThrow(_stmt, "cantidad")
        val _columnIndexOfPrecioUnitarioCotizado: Int = getColumnIndexOrThrow(_stmt,
            "precioUnitarioCotizado")
        val _result: MutableList<LineaSolicitudExterna> = mutableListOf()
        while (_stmt.step()) {
          val _item: LineaSolicitudExterna
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSolicitudId: Long
          _tmpSolicitudId = _stmt.getLong(_columnIndexOfSolicitudId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpCantidad: Int
          _tmpCantidad = _stmt.getLong(_columnIndexOfCantidad).toInt()
          val _tmpPrecioUnitarioCotizado: Long?
          if (_stmt.isNull(_columnIndexOfPrecioUnitarioCotizado)) {
            _tmpPrecioUnitarioCotizado = null
          } else {
            _tmpPrecioUnitarioCotizado = _stmt.getLong(_columnIndexOfPrecioUnitarioCotizado)
          }
          _item =
              LineaSolicitudExterna(_tmpId,_tmpSolicitudId,_tmpProductoId,_tmpCantidad,_tmpPrecioUnitarioCotizado)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun eventosGlobales(): Flow<List<EventoSolicitudExterna>> {
    val _sql: String =
        "SELECT * FROM eventos_solicitud_externa ORDER BY fecha DESC, id DESC LIMIT 100"
    return createFlow(__db, false, arrayOf("eventos_solicitud_externa")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSolicitudId: Int = getColumnIndexOrThrow(_stmt, "solicitudId")
        val _columnIndexOfUsuarioId: Int = getColumnIndexOrThrow(_stmt, "usuarioId")
        val _columnIndexOfAccion: Int = getColumnIndexOrThrow(_stmt, "accion")
        val _columnIndexOfDetalle: Int = getColumnIndexOrThrow(_stmt, "detalle")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _result: MutableList<EventoSolicitudExterna> = mutableListOf()
        while (_stmt.step()) {
          val _item: EventoSolicitudExterna
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSolicitudId: Long
          _tmpSolicitudId = _stmt.getLong(_columnIndexOfSolicitudId)
          val _tmpUsuarioId: Long
          _tmpUsuarioId = _stmt.getLong(_columnIndexOfUsuarioId)
          val _tmpAccion: String
          _tmpAccion = _stmt.getText(_columnIndexOfAccion)
          val _tmpDetalle: String
          _tmpDetalle = _stmt.getText(_columnIndexOfDetalle)
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          _item =
              EventoSolicitudExterna(_tmpId,_tmpSolicitudId,_tmpUsuarioId,_tmpAccion,_tmpDetalle,_tmpFecha)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun eventosSolicitud(id: Long): Flow<List<EventoSolicitudExterna>> {
    val _sql: String =
        "SELECT * FROM eventos_solicitud_externa WHERE solicitudId=? ORDER BY fecha, id"
    return createFlow(__db, false, arrayOf("eventos_solicitud_externa")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSolicitudId: Int = getColumnIndexOrThrow(_stmt, "solicitudId")
        val _columnIndexOfUsuarioId: Int = getColumnIndexOrThrow(_stmt, "usuarioId")
        val _columnIndexOfAccion: Int = getColumnIndexOrThrow(_stmt, "accion")
        val _columnIndexOfDetalle: Int = getColumnIndexOrThrow(_stmt, "detalle")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _result: MutableList<EventoSolicitudExterna> = mutableListOf()
        while (_stmt.step()) {
          val _item: EventoSolicitudExterna
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSolicitudId: Long
          _tmpSolicitudId = _stmt.getLong(_columnIndexOfSolicitudId)
          val _tmpUsuarioId: Long
          _tmpUsuarioId = _stmt.getLong(_columnIndexOfUsuarioId)
          val _tmpAccion: String
          _tmpAccion = _stmt.getText(_columnIndexOfAccion)
          val _tmpDetalle: String
          _tmpDetalle = _stmt.getText(_columnIndexOfDetalle)
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          _item =
              EventoSolicitudExterna(_tmpId,_tmpSolicitudId,_tmpUsuarioId,_tmpAccion,_tmpDetalle,_tmpFecha)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun solicitudPorCaja(caja: Long): SolicitudExterna? {
    val _sql: String = "SELECT * FROM solicitudes_externas WHERE entregaCajaId=? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, caja)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfBodegaId: Int = getColumnIndexOrThrow(_stmt, "bodegaId")
        val _columnIndexOfSolicitanteId: Int = getColumnIndexOrThrow(_stmt, "solicitanteId")
        val _columnIndexOfMotivo: Int = getColumnIndexOrThrow(_stmt, "motivo")
        val _columnIndexOfEstado: Int = getColumnIndexOrThrow(_stmt, "estado")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _columnIndexOfFechaRevision: Int = getColumnIndexOrThrow(_stmt, "fechaRevision")
        val _columnIndexOfRevisadoPor: Int = getColumnIndexOrThrow(_stmt, "revisadoPor")
        val _columnIndexOfFechaDecision: Int = getColumnIndexOrThrow(_stmt, "fechaDecision")
        val _columnIndexOfDecididoPor: Int = getColumnIndexOrThrow(_stmt, "decididoPor")
        val _columnIndexOfObservacion: Int = getColumnIndexOrThrow(_stmt, "observacion")
        val _columnIndexOfEntregaCajaId: Int = getColumnIndexOrThrow(_stmt, "entregaCajaId")
        val _columnIndexOfProveedorCotizado: Int = getColumnIndexOrThrow(_stmt, "proveedorCotizado")
        val _result: SolicitudExterna?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpBodegaId: Long
          _tmpBodegaId = _stmt.getLong(_columnIndexOfBodegaId)
          val _tmpSolicitanteId: Long
          _tmpSolicitanteId = _stmt.getLong(_columnIndexOfSolicitanteId)
          val _tmpMotivo: String
          _tmpMotivo = _stmt.getText(_columnIndexOfMotivo)
          val _tmpEstado: String
          _tmpEstado = _stmt.getText(_columnIndexOfEstado)
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          val _tmpFechaRevision: Long?
          if (_stmt.isNull(_columnIndexOfFechaRevision)) {
            _tmpFechaRevision = null
          } else {
            _tmpFechaRevision = _stmt.getLong(_columnIndexOfFechaRevision)
          }
          val _tmpRevisadoPor: Long?
          if (_stmt.isNull(_columnIndexOfRevisadoPor)) {
            _tmpRevisadoPor = null
          } else {
            _tmpRevisadoPor = _stmt.getLong(_columnIndexOfRevisadoPor)
          }
          val _tmpFechaDecision: Long?
          if (_stmt.isNull(_columnIndexOfFechaDecision)) {
            _tmpFechaDecision = null
          } else {
            _tmpFechaDecision = _stmt.getLong(_columnIndexOfFechaDecision)
          }
          val _tmpDecididoPor: Long?
          if (_stmt.isNull(_columnIndexOfDecididoPor)) {
            _tmpDecididoPor = null
          } else {
            _tmpDecididoPor = _stmt.getLong(_columnIndexOfDecididoPor)
          }
          val _tmpObservacion: String
          _tmpObservacion = _stmt.getText(_columnIndexOfObservacion)
          val _tmpEntregaCajaId: Long?
          if (_stmt.isNull(_columnIndexOfEntregaCajaId)) {
            _tmpEntregaCajaId = null
          } else {
            _tmpEntregaCajaId = _stmt.getLong(_columnIndexOfEntregaCajaId)
          }
          val _tmpProveedorCotizado: String
          _tmpProveedorCotizado = _stmt.getText(_columnIndexOfProveedorCotizado)
          _result =
              SolicitudExterna(_tmpId,_tmpSucursalId,_tmpBodegaId,_tmpSolicitanteId,_tmpMotivo,_tmpEstado,_tmpFecha,_tmpFechaRevision,_tmpRevisadoPor,_tmpFechaDecision,_tmpDecididoPor,_tmpObservacion,_tmpEntregaCajaId,_tmpProveedorCotizado)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun cajaEntrega(id: Long): Flow<Caja?> {
    val _sql: String = "SELECT * FROM cajas WHERE id=? LIMIT 1"
    return createFlow(__db, false, arrayOf("cajas")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCodigo: Int = getColumnIndexOrThrow(_stmt, "codigo")
        val _columnIndexOfTipo: Int = getColumnIndexOrThrow(_stmt, "tipo")
        val _columnIndexOfOrigen: Int = getColumnIndexOrThrow(_stmt, "origen")
        val _columnIndexOfBodegaOrigenId: Int = getColumnIndexOrThrow(_stmt, "bodegaOrigenId")
        val _columnIndexOfBodegaDestinoId: Int = getColumnIndexOrThrow(_stmt, "bodegaDestinoId")
        val _columnIndexOfSolicitadoPor: Int = getColumnIndexOrThrow(_stmt, "solicitadoPor")
        val _columnIndexOfAsignadoA: Int = getColumnIndexOrThrow(_stmt, "asignadoA")
        val _columnIndexOfEstado: Int = getColumnIndexOrThrow(_stmt, "estado")
        val _columnIndexOfPreparadoPor: Int = getColumnIndexOrThrow(_stmt, "preparadoPor")
        val _columnIndexOfRevisorId: Int = getColumnIndexOrThrow(_stmt, "revisorId")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _columnIndexOfSucursalDestinoId: Int = getColumnIndexOrThrow(_stmt, "sucursalDestinoId")
        val _result: Caja?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpCodigo: String
          _tmpCodigo = _stmt.getText(_columnIndexOfCodigo)
          val _tmpTipo: String
          _tmpTipo = _stmt.getText(_columnIndexOfTipo)
          val _tmpOrigen: String
          _tmpOrigen = _stmt.getText(_columnIndexOfOrigen)
          val _tmpBodegaOrigenId: Long
          _tmpBodegaOrigenId = _stmt.getLong(_columnIndexOfBodegaOrigenId)
          val _tmpBodegaDestinoId: Long
          _tmpBodegaDestinoId = _stmt.getLong(_columnIndexOfBodegaDestinoId)
          val _tmpSolicitadoPor: String
          _tmpSolicitadoPor = _stmt.getText(_columnIndexOfSolicitadoPor)
          val _tmpAsignadoA: Long
          _tmpAsignadoA = _stmt.getLong(_columnIndexOfAsignadoA)
          val _tmpEstado: String
          _tmpEstado = _stmt.getText(_columnIndexOfEstado)
          val _tmpPreparadoPor: Long?
          if (_stmt.isNull(_columnIndexOfPreparadoPor)) {
            _tmpPreparadoPor = null
          } else {
            _tmpPreparadoPor = _stmt.getLong(_columnIndexOfPreparadoPor)
          }
          val _tmpRevisorId: Long?
          if (_stmt.isNull(_columnIndexOfRevisorId)) {
            _tmpRevisorId = null
          } else {
            _tmpRevisorId = _stmt.getLong(_columnIndexOfRevisorId)
          }
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          val _tmpSucursalDestinoId: Long?
          if (_stmt.isNull(_columnIndexOfSucursalDestinoId)) {
            _tmpSucursalDestinoId = null
          } else {
            _tmpSucursalDestinoId = _stmt.getLong(_columnIndexOfSucursalDestinoId)
          }
          _result =
              Caja(_tmpId,_tmpCodigo,_tmpTipo,_tmpOrigen,_tmpBodegaOrigenId,_tmpBodegaDestinoId,_tmpSolicitadoPor,_tmpAsignadoA,_tmpEstado,_tmpPreparadoPor,_tmpRevisorId,_tmpFecha,_tmpSucursalDestinoId)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun gerenteAutorizado(uid: Long): Int {
    val _sql: String = "SELECT COUNT(*) FROM usuarios WHERE id=? AND rol='GERENTE' AND activo=1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, uid)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun gerenteActivo(uid: Long): Flow<Int> {
    val _sql: String = "SELECT COUNT(*) FROM usuarios WHERE id=? AND rol='GERENTE' AND activo=1"
    return createFlow(__db, false, arrayOf("usuarios")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, uid)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun jefeAutorizadoExterno(uid: Long, bid: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM usuarios u JOIN usuario_bodega ub ON ub.usuarioId=u.id JOIN bodegas b ON b.id=ub.bodegaId WHERE u.id=? AND u.rol='JEFE_BODEGA' AND u.activo=1 AND ub.bodegaId=? AND b.activa=1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, uid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, bid)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun adminAutorizadoExterno(uid: Long, sid: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM usuarios u JOIN usuario_sucursal us ON us.usuarioId=u.id WHERE u.id=? AND u.rol='ADMIN_SUCURSAL' AND u.activo=1 AND us.sucursalId=?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, uid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, sid)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun operadorActivoDeBodegaParaEntrega(uid: Long, bid: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM usuarios u JOIN usuario_bodega ub ON ub.usuarioId=u.id WHERE u.id=? AND u.rol='OPERADOR_BODEGA' AND u.activo=1 AND ub.bodegaId=?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, uid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, bid)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun bodegaDeSucursal(bid: Long, sid: Long): Bodega? {
    val _sql: String = "SELECT * FROM bodegas WHERE id=? AND sucursalId=? AND activa=1 LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, bid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, sid)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _columnIndexOfActiva: Int = getColumnIndexOrThrow(_stmt, "activa")
        val _result: Bodega?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpNombre: String
          _tmpNombre = _stmt.getText(_columnIndexOfNombre)
          val _tmpActiva: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfActiva).toInt()
          _tmpActiva = _tmp != 0
          _result = Bodega(_tmpId,_tmpSucursalId,_tmpNombre,_tmpActiva)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun operadoresRecepcion(bid: Long): Flow<List<Usuario>> {
    val _sql: String =
        "SELECT u.* FROM usuarios u JOIN usuario_bodega ub ON ub.usuarioId=u.id WHERE ub.bodegaId=? AND u.activo=1 AND u.rol='OPERADOR_BODEGA' ORDER BY u.nombre"
    return createFlow(__db, false, arrayOf("usuarios", "usuario_bodega")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, bid)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _columnIndexOfCorreo: Int = getColumnIndexOrThrow(_stmt, "correo")
        val _columnIndexOfClaveHash: Int = getColumnIndexOrThrow(_stmt, "claveHash")
        val _columnIndexOfClaveSal: Int = getColumnIndexOrThrow(_stmt, "claveSal")
        val _columnIndexOfRol: Int = getColumnIndexOrThrow(_stmt, "rol")
        val _columnIndexOfActivo: Int = getColumnIndexOrThrow(_stmt, "activo")
        val _columnIndexOfRut: Int = getColumnIndexOrThrow(_stmt, "rut")
        val _columnIndexOfSucursalResponsableId: Int = getColumnIndexOrThrow(_stmt,
            "sucursalResponsableId")
        val _result: MutableList<Usuario> = mutableListOf()
        while (_stmt.step()) {
          val _item: Usuario
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpNombre: String
          _tmpNombre = _stmt.getText(_columnIndexOfNombre)
          val _tmpCorreo: String
          _tmpCorreo = _stmt.getText(_columnIndexOfCorreo)
          val _tmpClaveHash: String
          _tmpClaveHash = _stmt.getText(_columnIndexOfClaveHash)
          val _tmpClaveSal: String
          _tmpClaveSal = _stmt.getText(_columnIndexOfClaveSal)
          val _tmpRol: String
          _tmpRol = _stmt.getText(_columnIndexOfRol)
          val _tmpActivo: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfActivo).toInt()
          _tmpActivo = _tmp != 0
          val _tmpRut: String?
          if (_stmt.isNull(_columnIndexOfRut)) {
            _tmpRut = null
          } else {
            _tmpRut = _stmt.getText(_columnIndexOfRut)
          }
          val _tmpSucursalResponsableId: Long?
          if (_stmt.isNull(_columnIndexOfSucursalResponsableId)) {
            _tmpSucursalResponsableId = null
          } else {
            _tmpSucursalResponsableId = _stmt.getLong(_columnIndexOfSucursalResponsableId)
          }
          _item =
              Usuario(_tmpId,_tmpNombre,_tmpCorreo,_tmpClaveHash,_tmpClaveSal,_tmpRol,_tmpActivo,_tmpRut,_tmpSucursalResponsableId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun indicadoresGlobales(): Flow<List<IndicadorSucursal>> {
    val _sql: String = """
        |SELECT s.id AS sucursalId, s.nombre AS nombre,
        |        (SELECT COUNT(*) FROM bodegas b WHERE b.sucursalId=s.id AND b.activa=1) AS bodegas,
        |        COALESCE((SELECT SUM(e.cantidad) FROM existencias e JOIN posiciones p ON p.id=e.posicionId JOIN bodegas b ON b.id=p.bodegaId WHERE b.sucursalId=s.id),0) AS unidadesBodega,
        |        COALESCE((SELECT SUM(et.cantidad) FROM existencias_tienda et WHERE et.sucursalId=s.id),0) AS unidadesTienda,
        |        (SELECT COUNT(*) FROM incidencias_caja i JOIN cajas c ON c.id=i.cajaId JOIN bodegas b ON b.id=CASE WHEN c.tipo='SALIDA' THEN c.bodegaOrigenId ELSE c.bodegaDestinoId END WHERE b.sucursalId=s.id AND i.estado='PENDIENTE') AS incidencias
        |        FROM sucursales s ORDER BY s.nombre
        """.trimMargin()
    return createFlow(__db, false, arrayOf("bodegas", "existencias", "posiciones",
        "existencias_tienda", "incidencias_caja", "cajas", "sucursales")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfSucursalId: Int = 0
        val _columnIndexOfNombre: Int = 1
        val _columnIndexOfBodegas: Int = 2
        val _columnIndexOfUnidadesBodega: Int = 3
        val _columnIndexOfUnidadesTienda: Int = 4
        val _columnIndexOfIncidencias: Int = 5
        val _result: MutableList<IndicadorSucursal> = mutableListOf()
        while (_stmt.step()) {
          val _item: IndicadorSucursal
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpNombre: String
          _tmpNombre = _stmt.getText(_columnIndexOfNombre)
          val _tmpBodegas: Int
          _tmpBodegas = _stmt.getLong(_columnIndexOfBodegas).toInt()
          val _tmpUnidadesBodega: Long
          _tmpUnidadesBodega = _stmt.getLong(_columnIndexOfUnidadesBodega)
          val _tmpUnidadesTienda: Long
          _tmpUnidadesTienda = _stmt.getLong(_columnIndexOfUnidadesTienda)
          val _tmpIncidencias: Int
          _tmpIncidencias = _stmt.getLong(_columnIndexOfIncidencias).toInt()
          _item =
              IndicadorSucursal(_tmpSucursalId,_tmpNombre,_tmpBodegas,_tmpUnidadesBodega,_tmpUnidadesTienda,_tmpIncidencias)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun faltantesGlobales(): Flow<List<ProductoBajoMinimo>> {
    val _sql: String = """
        |SELECT s.id AS sucursalId, s.nombre AS sucursal, p.codigo AS codigo,
        |        p.nombre AS nombre, COALESCE(et.cantidad,0) AS disponible,
        |        COALESCE(ms.minimo,p.minimo) AS minimo
        |        FROM sucursales s CROSS JOIN productos p
        |        LEFT JOIN existencias_tienda et ON et.sucursalId=s.id AND et.productoId=p.id
        |        LEFT JOIN minimos_sucursal ms ON ms.sucursalId=s.id AND ms.productoId=p.id
        |        WHERE COALESCE(et.cantidad,0)<=COALESCE(ms.minimo,p.minimo)
        |        ORDER BY s.nombre, p.nombre
        """.trimMargin()
    return createFlow(__db, false, arrayOf("sucursales", "productos", "existencias_tienda",
        "minimos_sucursal")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfSucursalId: Int = 0
        val _columnIndexOfSucursal: Int = 1
        val _columnIndexOfCodigo: Int = 2
        val _columnIndexOfNombre: Int = 3
        val _columnIndexOfDisponible: Int = 4
        val _columnIndexOfMinimo: Int = 5
        val _result: MutableList<ProductoBajoMinimo> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductoBajoMinimo
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpSucursal: String
          _tmpSucursal = _stmt.getText(_columnIndexOfSucursal)
          val _tmpCodigo: String
          _tmpCodigo = _stmt.getText(_columnIndexOfCodigo)
          val _tmpNombre: String
          _tmpNombre = _stmt.getText(_columnIndexOfNombre)
          val _tmpDisponible: Int
          _tmpDisponible = _stmt.getLong(_columnIndexOfDisponible).toInt()
          val _tmpMinimo: Int
          _tmpMinimo = _stmt.getLong(_columnIndexOfMinimo).toInt()
          _item =
              ProductoBajoMinimo(_tmpSucursalId,_tmpSucursal,_tmpCodigo,_tmpNombre,_tmpDisponible,_tmpMinimo)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun todasLasCajas(): List<Caja> {
    val _sql: String = "SELECT * FROM cajas ORDER BY fecha,id"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCodigo: Int = getColumnIndexOrThrow(_stmt, "codigo")
        val _columnIndexOfTipo: Int = getColumnIndexOrThrow(_stmt, "tipo")
        val _columnIndexOfOrigen: Int = getColumnIndexOrThrow(_stmt, "origen")
        val _columnIndexOfBodegaOrigenId: Int = getColumnIndexOrThrow(_stmt, "bodegaOrigenId")
        val _columnIndexOfBodegaDestinoId: Int = getColumnIndexOrThrow(_stmt, "bodegaDestinoId")
        val _columnIndexOfSolicitadoPor: Int = getColumnIndexOrThrow(_stmt, "solicitadoPor")
        val _columnIndexOfAsignadoA: Int = getColumnIndexOrThrow(_stmt, "asignadoA")
        val _columnIndexOfEstado: Int = getColumnIndexOrThrow(_stmt, "estado")
        val _columnIndexOfPreparadoPor: Int = getColumnIndexOrThrow(_stmt, "preparadoPor")
        val _columnIndexOfRevisorId: Int = getColumnIndexOrThrow(_stmt, "revisorId")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _columnIndexOfSucursalDestinoId: Int = getColumnIndexOrThrow(_stmt, "sucursalDestinoId")
        val _result: MutableList<Caja> = mutableListOf()
        while (_stmt.step()) {
          val _item: Caja
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpCodigo: String
          _tmpCodigo = _stmt.getText(_columnIndexOfCodigo)
          val _tmpTipo: String
          _tmpTipo = _stmt.getText(_columnIndexOfTipo)
          val _tmpOrigen: String
          _tmpOrigen = _stmt.getText(_columnIndexOfOrigen)
          val _tmpBodegaOrigenId: Long
          _tmpBodegaOrigenId = _stmt.getLong(_columnIndexOfBodegaOrigenId)
          val _tmpBodegaDestinoId: Long
          _tmpBodegaDestinoId = _stmt.getLong(_columnIndexOfBodegaDestinoId)
          val _tmpSolicitadoPor: String
          _tmpSolicitadoPor = _stmt.getText(_columnIndexOfSolicitadoPor)
          val _tmpAsignadoA: Long
          _tmpAsignadoA = _stmt.getLong(_columnIndexOfAsignadoA)
          val _tmpEstado: String
          _tmpEstado = _stmt.getText(_columnIndexOfEstado)
          val _tmpPreparadoPor: Long?
          if (_stmt.isNull(_columnIndexOfPreparadoPor)) {
            _tmpPreparadoPor = null
          } else {
            _tmpPreparadoPor = _stmt.getLong(_columnIndexOfPreparadoPor)
          }
          val _tmpRevisorId: Long?
          if (_stmt.isNull(_columnIndexOfRevisorId)) {
            _tmpRevisorId = null
          } else {
            _tmpRevisorId = _stmt.getLong(_columnIndexOfRevisorId)
          }
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          val _tmpSucursalDestinoId: Long?
          if (_stmt.isNull(_columnIndexOfSucursalDestinoId)) {
            _tmpSucursalDestinoId = null
          } else {
            _tmpSucursalDestinoId = _stmt.getLong(_columnIndexOfSucursalDestinoId)
          }
          _item =
              Caja(_tmpId,_tmpCodigo,_tmpTipo,_tmpOrigen,_tmpBodegaOrigenId,_tmpBodegaDestinoId,_tmpSolicitadoPor,_tmpAsignadoA,_tmpEstado,_tmpPreparadoPor,_tmpRevisorId,_tmpFecha,_tmpSucursalDestinoId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun todosLosDetallesCaja(): List<DetalleCaja> {
    val _sql: String = "SELECT * FROM detalle_caja ORDER BY cajaId,id"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCajaId: Int = getColumnIndexOrThrow(_stmt, "cajaId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfCantidadEsperada: Int = getColumnIndexOrThrow(_stmt, "cantidadEsperada")
        val _columnIndexOfCantidadVerificada: Int = getColumnIndexOrThrow(_stmt,
            "cantidadVerificada")
        val _columnIndexOfPosicionId: Int = getColumnIndexOrThrow(_stmt, "posicionId")
        val _columnIndexOfCantidadRecibida: Int = getColumnIndexOrThrow(_stmt, "cantidadRecibida")
        val _columnIndexOfPosicionDestinoId: Int = getColumnIndexOrThrow(_stmt, "posicionDestinoId")
        val _columnIndexOfCantidadContadaFisica: Int = getColumnIndexOrThrow(_stmt,
            "cantidadContadaFisica")
        val _result: MutableList<DetalleCaja> = mutableListOf()
        while (_stmt.step()) {
          val _item: DetalleCaja
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpCajaId: Long
          _tmpCajaId = _stmt.getLong(_columnIndexOfCajaId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpCantidadEsperada: Int
          _tmpCantidadEsperada = _stmt.getLong(_columnIndexOfCantidadEsperada).toInt()
          val _tmpCantidadVerificada: Int
          _tmpCantidadVerificada = _stmt.getLong(_columnIndexOfCantidadVerificada).toInt()
          val _tmpPosicionId: Long?
          if (_stmt.isNull(_columnIndexOfPosicionId)) {
            _tmpPosicionId = null
          } else {
            _tmpPosicionId = _stmt.getLong(_columnIndexOfPosicionId)
          }
          val _tmpCantidadRecibida: Int
          _tmpCantidadRecibida = _stmt.getLong(_columnIndexOfCantidadRecibida).toInt()
          val _tmpPosicionDestinoId: Long?
          if (_stmt.isNull(_columnIndexOfPosicionDestinoId)) {
            _tmpPosicionDestinoId = null
          } else {
            _tmpPosicionDestinoId = _stmt.getLong(_columnIndexOfPosicionDestinoId)
          }
          val _tmpCantidadContadaFisica: Int?
          if (_stmt.isNull(_columnIndexOfCantidadContadaFisica)) {
            _tmpCantidadContadaFisica = null
          } else {
            _tmpCantidadContadaFisica = _stmt.getLong(_columnIndexOfCantidadContadaFisica).toInt()
          }
          _item =
              DetalleCaja(_tmpId,_tmpCajaId,_tmpProductoId,_tmpCantidadEsperada,_tmpCantidadVerificada,_tmpPosicionId,_tmpCantidadRecibida,_tmpPosicionDestinoId,_tmpCantidadContadaFisica)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun todosEventosCaja(): List<EventoCaja> {
    val _sql: String = "SELECT * FROM eventos_caja ORDER BY fecha,id"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCajaId: Int = getColumnIndexOrThrow(_stmt, "cajaId")
        val _columnIndexOfUsuarioId: Int = getColumnIndexOrThrow(_stmt, "usuarioId")
        val _columnIndexOfAccion: Int = getColumnIndexOrThrow(_stmt, "accion")
        val _columnIndexOfObservacion: Int = getColumnIndexOrThrow(_stmt, "observacion")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _result: MutableList<EventoCaja> = mutableListOf()
        while (_stmt.step()) {
          val _item: EventoCaja
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpCajaId: Long
          _tmpCajaId = _stmt.getLong(_columnIndexOfCajaId)
          val _tmpUsuarioId: Long
          _tmpUsuarioId = _stmt.getLong(_columnIndexOfUsuarioId)
          val _tmpAccion: String
          _tmpAccion = _stmt.getText(_columnIndexOfAccion)
          val _tmpObservacion: String
          _tmpObservacion = _stmt.getText(_columnIndexOfObservacion)
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          _item = EventoCaja(_tmpId,_tmpCajaId,_tmpUsuarioId,_tmpAccion,_tmpObservacion,_tmpFecha)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun todasLasIncidencias(): List<IncidenciaCaja> {
    val _sql: String = "SELECT * FROM incidencias_caja ORDER BY fecha,id"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCajaId: Int = getColumnIndexOrThrow(_stmt, "cajaId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfEsperado: Int = getColumnIndexOrThrow(_stmt, "esperado")
        val _columnIndexOfContado: Int = getColumnIndexOrThrow(_stmt, "contado")
        val _columnIndexOfEstado: Int = getColumnIndexOrThrow(_stmt, "estado")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _result: MutableList<IncidenciaCaja> = mutableListOf()
        while (_stmt.step()) {
          val _item: IncidenciaCaja
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpCajaId: Long
          _tmpCajaId = _stmt.getLong(_columnIndexOfCajaId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpEsperado: Int
          _tmpEsperado = _stmt.getLong(_columnIndexOfEsperado).toInt()
          val _tmpContado: Int
          _tmpContado = _stmt.getLong(_columnIndexOfContado).toInt()
          val _tmpEstado: String
          _tmpEstado = _stmt.getText(_columnIndexOfEstado)
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          _item =
              IncidenciaCaja(_tmpId,_tmpCajaId,_tmpProductoId,_tmpEsperado,_tmpContado,_tmpEstado,_tmpFecha)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun todosEventosAdministracion(): List<EventoAdministrativo> {
    val _sql: String = "SELECT * FROM eventos_administracion ORDER BY fecha,id"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfUsuarioId: Int = getColumnIndexOrThrow(_stmt, "usuarioId")
        val _columnIndexOfAccion: Int = getColumnIndexOrThrow(_stmt, "accion")
        val _columnIndexOfDetalle: Int = getColumnIndexOrThrow(_stmt, "detalle")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _result: MutableList<EventoAdministrativo> = mutableListOf()
        while (_stmt.step()) {
          val _item: EventoAdministrativo
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpUsuarioId: Long
          _tmpUsuarioId = _stmt.getLong(_columnIndexOfUsuarioId)
          val _tmpAccion: String
          _tmpAccion = _stmt.getText(_columnIndexOfAccion)
          val _tmpDetalle: String
          _tmpDetalle = _stmt.getText(_columnIndexOfDetalle)
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          _item =
              EventoAdministrativo(_tmpId,_tmpSucursalId,_tmpUsuarioId,_tmpAccion,_tmpDetalle,_tmpFecha)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun todosMovimientosBodega(): List<Movimiento> {
    val _sql: String = "SELECT * FROM movimientos ORDER BY fecha DESC,id DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUsuarioId: Int = getColumnIndexOrThrow(_stmt, "usuarioId")
        val _columnIndexOfBodegaId: Int = getColumnIndexOrThrow(_stmt, "bodegaId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfPosicionOrigen: Int = getColumnIndexOrThrow(_stmt, "posicionOrigen")
        val _columnIndexOfPosicionDestino: Int = getColumnIndexOrThrow(_stmt, "posicionDestino")
        val _columnIndexOfCantidad: Int = getColumnIndexOrThrow(_stmt, "cantidad")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _columnIndexOfTipo: Int = getColumnIndexOrThrow(_stmt, "tipo")
        val _result: MutableList<Movimiento> = mutableListOf()
        while (_stmt.step()) {
          val _item: Movimiento
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpUsuarioId: Long
          _tmpUsuarioId = _stmt.getLong(_columnIndexOfUsuarioId)
          val _tmpBodegaId: Long
          _tmpBodegaId = _stmt.getLong(_columnIndexOfBodegaId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpPosicionOrigen: Long
          _tmpPosicionOrigen = _stmt.getLong(_columnIndexOfPosicionOrigen)
          val _tmpPosicionDestino: Long
          _tmpPosicionDestino = _stmt.getLong(_columnIndexOfPosicionDestino)
          val _tmpCantidad: Int
          _tmpCantidad = _stmt.getLong(_columnIndexOfCantidad).toInt()
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          val _tmpTipo: String
          _tmpTipo = _stmt.getText(_columnIndexOfTipo)
          _item =
              Movimiento(_tmpId,_tmpUsuarioId,_tmpBodegaId,_tmpProductoId,_tmpPosicionOrigen,_tmpPosicionDestino,_tmpCantidad,_tmpFecha,_tmpTipo)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun todosMovimientosTienda(): List<MovimientoTienda> {
    val _sql: String = "SELECT * FROM movimientos_tienda ORDER BY fecha DESC,id DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUsuarioId: Int = getColumnIndexOrThrow(_stmt, "usuarioId")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfCajaId: Int = getColumnIndexOrThrow(_stmt, "cajaId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfCantidad: Int = getColumnIndexOrThrow(_stmt, "cantidad")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _result: MutableList<MovimientoTienda> = mutableListOf()
        while (_stmt.step()) {
          val _item: MovimientoTienda
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpUsuarioId: Long
          _tmpUsuarioId = _stmt.getLong(_columnIndexOfUsuarioId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpCajaId: Long
          _tmpCajaId = _stmt.getLong(_columnIndexOfCajaId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpCantidad: Int
          _tmpCantidad = _stmt.getLong(_columnIndexOfCantidad).toInt()
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          _item =
              MovimientoTienda(_tmpId,_tmpUsuarioId,_tmpSucursalId,_tmpCajaId,_tmpProductoId,_tmpCantidad,_tmpFecha)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun todosEventosExterno(): List<EventoSolicitudExterna> {
    val _sql: String = "SELECT * FROM eventos_solicitud_externa ORDER BY fecha DESC,id DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSolicitudId: Int = getColumnIndexOrThrow(_stmt, "solicitudId")
        val _columnIndexOfUsuarioId: Int = getColumnIndexOrThrow(_stmt, "usuarioId")
        val _columnIndexOfAccion: Int = getColumnIndexOrThrow(_stmt, "accion")
        val _columnIndexOfDetalle: Int = getColumnIndexOrThrow(_stmt, "detalle")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _result: MutableList<EventoSolicitudExterna> = mutableListOf()
        while (_stmt.step()) {
          val _item: EventoSolicitudExterna
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSolicitudId: Long
          _tmpSolicitudId = _stmt.getLong(_columnIndexOfSolicitudId)
          val _tmpUsuarioId: Long
          _tmpUsuarioId = _stmt.getLong(_columnIndexOfUsuarioId)
          val _tmpAccion: String
          _tmpAccion = _stmt.getText(_columnIndexOfAccion)
          val _tmpDetalle: String
          _tmpDetalle = _stmt.getText(_columnIndexOfDetalle)
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          _item =
              EventoSolicitudExterna(_tmpId,_tmpSolicitudId,_tmpUsuarioId,_tmpAccion,_tmpDetalle,_tmpFecha)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun movimientosGlobalesBodega(): Flow<List<Movimiento>> {
    val _sql: String = "SELECT * FROM movimientos ORDER BY fecha DESC, id DESC LIMIT 60"
    return createFlow(__db, false, arrayOf("movimientos")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUsuarioId: Int = getColumnIndexOrThrow(_stmt, "usuarioId")
        val _columnIndexOfBodegaId: Int = getColumnIndexOrThrow(_stmt, "bodegaId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfPosicionOrigen: Int = getColumnIndexOrThrow(_stmt, "posicionOrigen")
        val _columnIndexOfPosicionDestino: Int = getColumnIndexOrThrow(_stmt, "posicionDestino")
        val _columnIndexOfCantidad: Int = getColumnIndexOrThrow(_stmt, "cantidad")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _columnIndexOfTipo: Int = getColumnIndexOrThrow(_stmt, "tipo")
        val _result: MutableList<Movimiento> = mutableListOf()
        while (_stmt.step()) {
          val _item: Movimiento
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpUsuarioId: Long
          _tmpUsuarioId = _stmt.getLong(_columnIndexOfUsuarioId)
          val _tmpBodegaId: Long
          _tmpBodegaId = _stmt.getLong(_columnIndexOfBodegaId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpPosicionOrigen: Long
          _tmpPosicionOrigen = _stmt.getLong(_columnIndexOfPosicionOrigen)
          val _tmpPosicionDestino: Long
          _tmpPosicionDestino = _stmt.getLong(_columnIndexOfPosicionDestino)
          val _tmpCantidad: Int
          _tmpCantidad = _stmt.getLong(_columnIndexOfCantidad).toInt()
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          val _tmpTipo: String
          _tmpTipo = _stmt.getText(_columnIndexOfTipo)
          _item =
              Movimiento(_tmpId,_tmpUsuarioId,_tmpBodegaId,_tmpProductoId,_tmpPosicionOrigen,_tmpPosicionDestino,_tmpCantidad,_tmpFecha,_tmpTipo)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun movimientosGlobalesTienda(): Flow<List<MovimientoTienda>> {
    val _sql: String = "SELECT * FROM movimientos_tienda ORDER BY fecha DESC, id DESC LIMIT 60"
    return createFlow(__db, false, arrayOf("movimientos_tienda")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUsuarioId: Int = getColumnIndexOrThrow(_stmt, "usuarioId")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfCajaId: Int = getColumnIndexOrThrow(_stmt, "cajaId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfCantidad: Int = getColumnIndexOrThrow(_stmt, "cantidad")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _result: MutableList<MovimientoTienda> = mutableListOf()
        while (_stmt.step()) {
          val _item: MovimientoTienda
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpUsuarioId: Long
          _tmpUsuarioId = _stmt.getLong(_columnIndexOfUsuarioId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpCajaId: Long
          _tmpCajaId = _stmt.getLong(_columnIndexOfCajaId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpCantidad: Int
          _tmpCantidad = _stmt.getLong(_columnIndexOfCantidad).toInt()
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          _item =
              MovimientoTienda(_tmpId,_tmpUsuarioId,_tmpSucursalId,_tmpCajaId,_tmpProductoId,_tmpCantidad,_tmpFecha)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun guardarProveedorCotizado(
    id: Long,
    sid: Long,
    proveedor: String,
  ): Int {
    val _sql: String =
        "UPDATE solicitudes_externas SET proveedorCotizado=? WHERE id=? AND sucursalId=? AND estado='PENDIENTE_ADMIN'"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, proveedor)
        _argIndex = 2
        _stmt.bindLong(_argIndex, id)
        _argIndex = 3
        _stmt.bindLong(_argIndex, sid)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun guardarPrecioCotizado(
    id: Long,
    producto: Long,
    precio: Long,
  ): Int {
    val _sql: String =
        "UPDATE lineas_solicitud_externa SET precioUnitarioCotizado=? WHERE solicitudId=? AND productoId=?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, precio)
        _argIndex = 2
        _stmt.bindLong(_argIndex, id)
        _argIndex = 3
        _stmt.bindLong(_argIndex, producto)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun enviarAGerencia(
    id: Long,
    sid: Long,
    uid: Long,
    fecha: Long,
    observacion: String,
  ): Int {
    val _sql: String =
        "UPDATE solicitudes_externas SET estado='EN_GERENCIA', fechaRevision=?, revisadoPor=?, observacion=? WHERE id=? AND estado='PENDIENTE_ADMIN' AND sucursalId=?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, fecha)
        _argIndex = 2
        _stmt.bindLong(_argIndex, uid)
        _argIndex = 3
        _stmt.bindText(_argIndex, observacion)
        _argIndex = 4
        _stmt.bindLong(_argIndex, id)
        _argIndex = 5
        _stmt.bindLong(_argIndex, sid)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun decidir(
    id: Long,
    uid: Long,
    nuevo: String,
    fecha: Long,
    observacion: String,
  ): Int {
    val _sql: String =
        "UPDATE solicitudes_externas SET estado=?, fechaDecision=?, decididoPor=?, observacion=? WHERE id=? AND estado='EN_GERENCIA'"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, nuevo)
        _argIndex = 2
        _stmt.bindLong(_argIndex, fecha)
        _argIndex = 3
        _stmt.bindLong(_argIndex, uid)
        _argIndex = 4
        _stmt.bindText(_argIndex, observacion)
        _argIndex = 5
        _stmt.bindLong(_argIndex, id)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun programarEntrega(
    id: Long,
    sid: Long,
    caja: Long,
  ): Int {
    val _sql: String =
        "UPDATE solicitudes_externas SET estado='ENTREGA_PROGRAMADA', entregaCajaId=? WHERE id=? AND estado='APROBADA' AND sucursalId=? AND entregaCajaId IS NULL"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, caja)
        _argIndex = 2
        _stmt.bindLong(_argIndex, id)
        _argIndex = 3
        _stmt.bindLong(_argIndex, sid)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun completarEntrega(caja: Long, estado: String): Int {
    val _sql: String =
        "UPDATE solicitudes_externas SET estado=? WHERE entregaCajaId=? AND estado='ENTREGA_PROGRAMADA'"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, estado)
        _argIndex = 2
        _stmt.bindLong(_argIndex, caja)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
