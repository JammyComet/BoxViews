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
public class TiendasDao_Impl(
  __db: RoomDatabase,
) : TiendasDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfBodegaSucursal: EntityInsertAdapter<BodegaSucursal>

  private val __insertAdapterOfExistenciaTienda: EntityInsertAdapter<ExistenciaTienda>

  private val __insertAdapterOfUsuarioSucursal: EntityInsertAdapter<UsuarioSucursal>

  private val __insertAdapterOfMovimientoTienda: EntityInsertAdapter<MovimientoTienda>

  private val __insertAdapterOfPosicionTienda: EntityInsertAdapter<PosicionTienda>

  private val __insertAdapterOfExistenciaUbicacionTienda:
      EntityInsertAdapter<ExistenciaUbicacionTienda>

  private val __insertAdapterOfMovimientoUbicacionTienda:
      EntityInsertAdapter<MovimientoUbicacionTienda>

  private val __insertAdapterOfSolicitudReposicion: EntityInsertAdapter<SolicitudReposicion>
  init {
    this.__db = __db
    this.__insertAdapterOfBodegaSucursal = object : EntityInsertAdapter<BodegaSucursal>() {
      protected override fun createQuery(): String =
          "INSERT OR IGNORE INTO `bodega_sucursal` (`bodegaId`,`sucursalId`) VALUES (?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: BodegaSucursal) {
        statement.bindLong(1, entity.bodegaId)
        statement.bindLong(2, entity.sucursalId)
      }
    }
    this.__insertAdapterOfExistenciaTienda = object : EntityInsertAdapter<ExistenciaTienda>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `existencias_tienda` (`id`,`sucursalId`,`productoId`,`cantidad`) VALUES (nullif(?, 0),?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ExistenciaTienda) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.sucursalId)
        statement.bindLong(3, entity.productoId)
        statement.bindLong(4, entity.cantidad.toLong())
      }
    }
    this.__insertAdapterOfUsuarioSucursal = object : EntityInsertAdapter<UsuarioSucursal>() {
      protected override fun createQuery(): String =
          "INSERT OR IGNORE INTO `usuario_sucursal` (`usuarioId`,`sucursalId`) VALUES (?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: UsuarioSucursal) {
        statement.bindLong(1, entity.usuarioId)
        statement.bindLong(2, entity.sucursalId)
      }
    }
    this.__insertAdapterOfMovimientoTienda = object : EntityInsertAdapter<MovimientoTienda>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `movimientos_tienda` (`id`,`usuarioId`,`sucursalId`,`cajaId`,`productoId`,`cantidad`,`fecha`) VALUES (nullif(?, 0),?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: MovimientoTienda) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.usuarioId)
        statement.bindLong(3, entity.sucursalId)
        statement.bindLong(4, entity.cajaId)
        statement.bindLong(5, entity.productoId)
        statement.bindLong(6, entity.cantidad.toLong())
        statement.bindLong(7, entity.fecha)
      }
    }
    this.__insertAdapterOfPosicionTienda = object : EntityInsertAdapter<PosicionTienda>() {
      protected override fun createQuery(): String =
          "INSERT OR IGNORE INTO `posiciones_tienda` (`id`,`sucursalId`,`codigo`,`zona`,`categoria`,`activa`,`nombre`,`tipo`,`padreId`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: PosicionTienda) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.sucursalId)
        statement.bindText(3, entity.codigo)
        statement.bindText(4, entity.zona)
        statement.bindText(5, entity.categoria)
        val _tmp: Int = if (entity.activa) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        statement.bindText(7, entity.nombre)
        statement.bindText(8, entity.tipo)
        val _tmpPadreId: Long? = entity.padreId
        if (_tmpPadreId == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpPadreId)
        }
      }
    }
    this.__insertAdapterOfExistenciaUbicacionTienda = object :
        EntityInsertAdapter<ExistenciaUbicacionTienda>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `existencias_ubicacion_tienda` (`id`,`sucursalId`,`productoId`,`posicionId`,`cantidad`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ExistenciaUbicacionTienda) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.sucursalId)
        statement.bindLong(3, entity.productoId)
        statement.bindLong(4, entity.posicionId)
        statement.bindLong(5, entity.cantidad.toLong())
      }
    }
    this.__insertAdapterOfMovimientoUbicacionTienda = object :
        EntityInsertAdapter<MovimientoUbicacionTienda>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `movimientos_ubicacion_tienda` (`id`,`usuarioId`,`sucursalId`,`productoId`,`posicionOrigenId`,`posicionDestinoId`,`cantidad`,`tipo`,`cajaId`,`fecha`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: MovimientoUbicacionTienda) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.usuarioId)
        statement.bindLong(3, entity.sucursalId)
        statement.bindLong(4, entity.productoId)
        val _tmpPosicionOrigenId: Long? = entity.posicionOrigenId
        if (_tmpPosicionOrigenId == null) {
          statement.bindNull(5)
        } else {
          statement.bindLong(5, _tmpPosicionOrigenId)
        }
        statement.bindLong(6, entity.posicionDestinoId)
        statement.bindLong(7, entity.cantidad.toLong())
        statement.bindText(8, entity.tipo)
        val _tmpCajaId: Long? = entity.cajaId
        if (_tmpCajaId == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpCajaId)
        }
        statement.bindLong(10, entity.fecha)
      }
    }
    this.__insertAdapterOfSolicitudReposicion = object : EntityInsertAdapter<SolicitudReposicion>()
        {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `solicitudes_reposicion` (`id`,`sucursalId`,`bodegaId`,`productoId`,`cantidad`,`solicitadoPor`,`fecha`,`estado`,`cajaId`,`loteId`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: SolicitudReposicion) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.sucursalId)
        statement.bindLong(3, entity.bodegaId)
        statement.bindLong(4, entity.productoId)
        statement.bindLong(5, entity.cantidad.toLong())
        statement.bindLong(6, entity.solicitadoPor)
        statement.bindLong(7, entity.fecha)
        statement.bindText(8, entity.estado)
        val _tmpCajaId: Long? = entity.cajaId
        if (_tmpCajaId == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpCajaId)
        }
        val _tmpLoteId: String? = entity.loteId
        if (_tmpLoteId == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpLoteId)
        }
      }
    }
  }

  public override suspend fun guardarAbastecimiento(valor: BodegaSucursal): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfBodegaSucursal.insertAndReturnId(_connection, valor)
    _result
  }

  public override suspend fun guardarExistenciaTienda(valor: ExistenciaTienda): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfExistenciaTienda.insertAndReturnId(_connection, valor)
    _result
  }

  public override suspend fun asignarUsuarioTienda(valor: UsuarioSucursal): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfUsuarioSucursal.insertAndReturnId(_connection, valor)
    _result
  }

  public override suspend fun guardarMovimientoTienda(valor: MovimientoTienda): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfMovimientoTienda.insertAndReturnId(_connection, valor)
    _result
  }

  public override suspend fun guardarPosicionTienda(posicion: PosicionTienda): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfPosicionTienda.insertAndReturnId(_connection, posicion)
    _result
  }

  public override suspend fun guardarStockUbicacionTienda(valor: ExistenciaUbicacionTienda): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfExistenciaUbicacionTienda.insertAndReturnId(_connection,
        valor)
    _result
  }

  public override suspend fun guardarMovimientoUbicacionTienda(valor: MovimientoUbicacionTienda):
      Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfMovimientoUbicacionTienda.insertAndReturnId(_connection,
        valor)
    _result
  }

  public override suspend fun guardarSolicitud(valor: SolicitudReposicion): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfSolicitudReposicion.insertAndReturnId(_connection, valor)
    _result
  }

  public override fun tiendasAbastecidas(bid: Long): Flow<List<Sucursal>> {
    val _sql: String =
        "SELECT s.* FROM sucursales s JOIN bodegas b ON b.sucursalId=s.id WHERE b.id=? ORDER BY s.nombre"
    return createFlow(__db, false, arrayOf("sucursales", "bodegas")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, bid)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _result: MutableList<Sucursal> = mutableListOf()
        while (_stmt.step()) {
          val _item: Sucursal
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpNombre: String
          _tmpNombre = _stmt.getText(_columnIndexOfNombre)
          _item = Sucursal(_tmpId,_tmpNombre)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun abasteceSucursal(bid: Long, sid: Long): Int {
    val _sql: String = "SELECT COUNT(*) FROM bodegas WHERE id=? AND sucursalId=? AND activa=1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, bid)
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

  public override suspend fun existeSucursal(sid: Long): Int {
    val _sql: String = "SELECT COUNT(*) FROM sucursales WHERE id=?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
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

  public override fun stockTienda(sid: Long): Flow<List<ExistenciaTienda>> {
    val _sql: String = "SELECT * FROM existencias_tienda WHERE sucursalId=? ORDER BY productoId"
    return createFlow(__db, false, arrayOf("existencias_tienda")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfCantidad: Int = getColumnIndexOrThrow(_stmt, "cantidad")
        val _result: MutableList<ExistenciaTienda> = mutableListOf()
        while (_stmt.step()) {
          val _item: ExistenciaTienda
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpCantidad: Int
          _tmpCantidad = _stmt.getLong(_columnIndexOfCantidad).toInt()
          _item = ExistenciaTienda(_tmpId,_tmpSucursalId,_tmpProductoId,_tmpCantidad)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun existenciaTienda(sid: Long, pid: Long): ExistenciaTienda? {
    val _sql: String =
        "SELECT * FROM existencias_tienda WHERE sucursalId=? AND productoId=? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, pid)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfCantidad: Int = getColumnIndexOrThrow(_stmt, "cantidad")
        val _result: ExistenciaTienda?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpCantidad: Int
          _tmpCantidad = _stmt.getLong(_columnIndexOfCantidad).toInt()
          _result = ExistenciaTienda(_tmpId,_tmpSucursalId,_tmpProductoId,_tmpCantidad)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun tiendasDelTrabajador(uid: Long): Flow<List<Sucursal>> {
    val _sql: String =
        "SELECT s.* FROM sucursales s JOIN usuario_sucursal us ON us.sucursalId=s.id JOIN usuarios u ON u.id=us.usuarioId WHERE us.usuarioId=? AND u.activo=1 AND u.rol='TRABAJADOR_TIENDA' ORDER BY s.nombre"
    return createFlow(__db, false, arrayOf("sucursales", "usuario_sucursal", "usuarios")) {
        _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, uid)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _result: MutableList<Sucursal> = mutableListOf()
        while (_stmt.step()) {
          val _item: Sucursal
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpNombre: String
          _tmpNombre = _stmt.getText(_columnIndexOfNombre)
          _item = Sucursal(_tmpId,_tmpNombre)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun trabajadorAutorizadoTienda(uid: Long, sid: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM usuario_sucursal us JOIN usuarios u ON u.id=us.usuarioId JOIN sucursales s ON s.id=us.sucursalId WHERE us.usuarioId=? AND us.sucursalId=? AND u.activo=1 AND u.rol='TRABAJADOR_TIENDA'"
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

  public override fun cajasPendientesTienda(sid: Long): Flow<List<Caja>> {
    val _sql: String =
        "SELECT * FROM cajas WHERE tipo='SALIDA' AND sucursalDestinoId=? AND estado IN ('EN_TRANSITO','EN_REVISION') ORDER BY CASE WHEN estado='EN_REVISION' THEN 0 ELSE 1 END,fecha DESC,id DESC"
    return createFlow(__db, false, arrayOf("cajas")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
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

  public override fun cajasRecibidasTienda(sid: Long): Flow<List<Caja>> {
    val _sql: String =
        "SELECT * FROM cajas WHERE tipo='SALIDA' AND sucursalDestinoId=? AND estado IN ('RECIBIDA','CON_DIFERENCIA') ORDER BY fecha DESC,id DESC"
    return createFlow(__db, false, arrayOf("cajas")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
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

  public override fun movimientosDeTienda(sid: Long): Flow<List<MovimientoTienda>> {
    val _sql: String =
        "SELECT * FROM movimientos_tienda WHERE sucursalId=? ORDER BY fecha DESC,id DESC LIMIT 100"
    return createFlow(__db, false, arrayOf("movimientos_tienda")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
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

  public override fun incidenciasDeTienda(sid: Long): Flow<List<IncidenciaCaja>> {
    val _sql: String =
        "SELECT i.* FROM incidencias_caja i JOIN cajas c ON c.id=i.cajaId WHERE c.tipo='SALIDA' AND c.sucursalDestinoId=? ORDER BY i.fecha DESC,i.id DESC"
    return createFlow(__db, false, arrayOf("incidencias_caja", "cajas")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
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

  public override fun eventosRecepcionTienda(sid: Long): Flow<List<EventoCaja>> {
    val _sql: String =
        "SELECT e.* FROM eventos_caja e JOIN cajas c ON c.id=e.cajaId WHERE c.tipo='SALIDA' AND c.sucursalDestinoId=? AND e.accion IN ('RECEPCION_TIENDA_CONFIRMADA','RECEPCION_TIENDA_CON_DIFERENCIA') ORDER BY e.fecha DESC,e.id DESC"
    return createFlow(__db, false, arrayOf("eventos_caja", "cajas")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
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

  public override fun posicionesTienda(sid: Long): Flow<List<PosicionTienda>> {
    val _sql: String =
        "SELECT * FROM posiciones_tienda WHERE sucursalId=? AND activa=1 AND tipo!='SECCION' ORDER BY CASE zona WHEN 'RECEPCION' THEN 0 WHEN 'VENTAS' THEN 1 ELSE 2 END, codigo"
    return createFlow(__db, false, arrayOf("posiciones_tienda")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfCodigo: Int = getColumnIndexOrThrow(_stmt, "codigo")
        val _columnIndexOfZona: Int = getColumnIndexOrThrow(_stmt, "zona")
        val _columnIndexOfCategoria: Int = getColumnIndexOrThrow(_stmt, "categoria")
        val _columnIndexOfActiva: Int = getColumnIndexOrThrow(_stmt, "activa")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _columnIndexOfTipo: Int = getColumnIndexOrThrow(_stmt, "tipo")
        val _columnIndexOfPadreId: Int = getColumnIndexOrThrow(_stmt, "padreId")
        val _result: MutableList<PosicionTienda> = mutableListOf()
        while (_stmt.step()) {
          val _item: PosicionTienda
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpCodigo: String
          _tmpCodigo = _stmt.getText(_columnIndexOfCodigo)
          val _tmpZona: String
          _tmpZona = _stmt.getText(_columnIndexOfZona)
          val _tmpCategoria: String
          _tmpCategoria = _stmt.getText(_columnIndexOfCategoria)
          val _tmpActiva: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfActiva).toInt()
          _tmpActiva = _tmp != 0
          val _tmpNombre: String
          _tmpNombre = _stmt.getText(_columnIndexOfNombre)
          val _tmpTipo: String
          _tmpTipo = _stmt.getText(_columnIndexOfTipo)
          val _tmpPadreId: Long?
          if (_stmt.isNull(_columnIndexOfPadreId)) {
            _tmpPadreId = null
          } else {
            _tmpPadreId = _stmt.getLong(_columnIndexOfPadreId)
          }
          _item =
              PosicionTienda(_tmpId,_tmpSucursalId,_tmpCodigo,_tmpZona,_tmpCategoria,_tmpActiva,_tmpNombre,_tmpTipo,_tmpPadreId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun posicionesTiendaTodas(sid: Long): Flow<List<PosicionTienda>> {
    val _sql: String = "SELECT * FROM posiciones_tienda WHERE sucursalId=? ORDER BY codigo"
    return createFlow(__db, false, arrayOf("posiciones_tienda")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfCodigo: Int = getColumnIndexOrThrow(_stmt, "codigo")
        val _columnIndexOfZona: Int = getColumnIndexOrThrow(_stmt, "zona")
        val _columnIndexOfCategoria: Int = getColumnIndexOrThrow(_stmt, "categoria")
        val _columnIndexOfActiva: Int = getColumnIndexOrThrow(_stmt, "activa")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _columnIndexOfTipo: Int = getColumnIndexOrThrow(_stmt, "tipo")
        val _columnIndexOfPadreId: Int = getColumnIndexOrThrow(_stmt, "padreId")
        val _result: MutableList<PosicionTienda> = mutableListOf()
        while (_stmt.step()) {
          val _item: PosicionTienda
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpCodigo: String
          _tmpCodigo = _stmt.getText(_columnIndexOfCodigo)
          val _tmpZona: String
          _tmpZona = _stmt.getText(_columnIndexOfZona)
          val _tmpCategoria: String
          _tmpCategoria = _stmt.getText(_columnIndexOfCategoria)
          val _tmpActiva: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfActiva).toInt()
          _tmpActiva = _tmp != 0
          val _tmpNombre: String
          _tmpNombre = _stmt.getText(_columnIndexOfNombre)
          val _tmpTipo: String
          _tmpTipo = _stmt.getText(_columnIndexOfTipo)
          val _tmpPadreId: Long?
          if (_stmt.isNull(_columnIndexOfPadreId)) {
            _tmpPadreId = null
          } else {
            _tmpPadreId = _stmt.getLong(_columnIndexOfPadreId)
          }
          _item =
              PosicionTienda(_tmpId,_tmpSucursalId,_tmpCodigo,_tmpZona,_tmpCategoria,_tmpActiva,_tmpNombre,_tmpTipo,_tmpPadreId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun posicionesTiendaActuales(sid: Long): List<PosicionTienda> {
    val _sql: String = "SELECT * FROM posiciones_tienda WHERE sucursalId=? ORDER BY codigo"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfCodigo: Int = getColumnIndexOrThrow(_stmt, "codigo")
        val _columnIndexOfZona: Int = getColumnIndexOrThrow(_stmt, "zona")
        val _columnIndexOfCategoria: Int = getColumnIndexOrThrow(_stmt, "categoria")
        val _columnIndexOfActiva: Int = getColumnIndexOrThrow(_stmt, "activa")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _columnIndexOfTipo: Int = getColumnIndexOrThrow(_stmt, "tipo")
        val _columnIndexOfPadreId: Int = getColumnIndexOrThrow(_stmt, "padreId")
        val _result: MutableList<PosicionTienda> = mutableListOf()
        while (_stmt.step()) {
          val _item: PosicionTienda
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpCodigo: String
          _tmpCodigo = _stmt.getText(_columnIndexOfCodigo)
          val _tmpZona: String
          _tmpZona = _stmt.getText(_columnIndexOfZona)
          val _tmpCategoria: String
          _tmpCategoria = _stmt.getText(_columnIndexOfCategoria)
          val _tmpActiva: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfActiva).toInt()
          _tmpActiva = _tmp != 0
          val _tmpNombre: String
          _tmpNombre = _stmt.getText(_columnIndexOfNombre)
          val _tmpTipo: String
          _tmpTipo = _stmt.getText(_columnIndexOfTipo)
          val _tmpPadreId: Long?
          if (_stmt.isNull(_columnIndexOfPadreId)) {
            _tmpPadreId = null
          } else {
            _tmpPadreId = _stmt.getLong(_columnIndexOfPadreId)
          }
          _item =
              PosicionTienda(_tmpId,_tmpSucursalId,_tmpCodigo,_tmpZona,_tmpCategoria,_tmpActiva,_tmpNombre,_tmpTipo,_tmpPadreId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun historicosInventarioTienda(sid: Long): Int {
    val _sql: String = """
        |SELECT (SELECT COUNT(*) FROM inventarios_tienda WHERE sucursalId=?) +
        |        (SELECT COUNT(*) FROM campanias_mensuales WHERE tipo='TIENDA' AND ubicacionId=?)
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
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

  public override suspend fun posicionTiendaPorId(id: Long): PosicionTienda? {
    val _sql: String = "SELECT * FROM posiciones_tienda WHERE id=? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfCodigo: Int = getColumnIndexOrThrow(_stmt, "codigo")
        val _columnIndexOfZona: Int = getColumnIndexOrThrow(_stmt, "zona")
        val _columnIndexOfCategoria: Int = getColumnIndexOrThrow(_stmt, "categoria")
        val _columnIndexOfActiva: Int = getColumnIndexOrThrow(_stmt, "activa")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _columnIndexOfTipo: Int = getColumnIndexOrThrow(_stmt, "tipo")
        val _columnIndexOfPadreId: Int = getColumnIndexOrThrow(_stmt, "padreId")
        val _result: PosicionTienda?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpCodigo: String
          _tmpCodigo = _stmt.getText(_columnIndexOfCodigo)
          val _tmpZona: String
          _tmpZona = _stmt.getText(_columnIndexOfZona)
          val _tmpCategoria: String
          _tmpCategoria = _stmt.getText(_columnIndexOfCategoria)
          val _tmpActiva: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfActiva).toInt()
          _tmpActiva = _tmp != 0
          val _tmpNombre: String
          _tmpNombre = _stmt.getText(_columnIndexOfNombre)
          val _tmpTipo: String
          _tmpTipo = _stmt.getText(_columnIndexOfTipo)
          val _tmpPadreId: Long?
          if (_stmt.isNull(_columnIndexOfPadreId)) {
            _tmpPadreId = null
          } else {
            _tmpPadreId = _stmt.getLong(_columnIndexOfPadreId)
          }
          _result =
              PosicionTienda(_tmpId,_tmpSucursalId,_tmpCodigo,_tmpZona,_tmpCategoria,_tmpActiva,_tmpNombre,_tmpTipo,_tmpPadreId)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun numeroPosicionesTienda(sid: Long): Int {
    val _sql: String = "SELECT COUNT(*) FROM posiciones_tienda WHERE sucursalId=?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
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

  public override suspend fun siguienteIdPosicionTienda(): Long {
    val _sql: String = "SELECT COALESCE(MAX(id),0)+1 FROM posiciones_tienda"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Long
        if (_stmt.step()) {
          val _tmp: Long
          _tmp = _stmt.getLong(0)
          _result = _tmp
        } else {
          _result = 0L
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun stockTiendaActual(sid: Long): List<ExistenciaTienda> {
    val _sql: String = "SELECT * FROM existencias_tienda WHERE sucursalId=?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfCantidad: Int = getColumnIndexOrThrow(_stmt, "cantidad")
        val _result: MutableList<ExistenciaTienda> = mutableListOf()
        while (_stmt.step()) {
          val _item: ExistenciaTienda
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpCantidad: Int
          _tmpCantidad = _stmt.getLong(_columnIndexOfCantidad).toInt()
          _item = ExistenciaTienda(_tmpId,_tmpSucursalId,_tmpProductoId,_tmpCantidad)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun totalUbicadoTienda(sid: Long, pid: Long): Int {
    val _sql: String =
        "SELECT COALESCE(SUM(cantidad),0) FROM existencias_ubicacion_tienda WHERE sucursalId=? AND productoId=?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, pid)
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

  public override fun diferenciasStockTienda(sid: Long): Flow<List<DiferenciaStockTienda>> {
    val _sql: String = """
        |SELECT e.sucursalId AS sucursalId, e.productoId AS productoId,
        |            e.cantidad AS stockGeneral,
        |            COALESCE((SELECT SUM(u.cantidad) FROM existencias_ubicacion_tienda u
        |                WHERE u.sucursalId=e.sucursalId AND u.productoId=e.productoId), 0) AS stockUbicaciones
        |        FROM existencias_tienda e WHERE e.sucursalId=? AND
        |            (e.cantidad != COALESCE((SELECT SUM(u.cantidad) FROM existencias_ubicacion_tienda u
        |                WHERE u.sucursalId=e.sucursalId AND u.productoId=e.productoId), 0)
        |                OR e.cantidad < 0)
        |        UNION ALL
        |        SELECT u.sucursalId AS sucursalId, u.productoId AS productoId,
        |            0 AS stockGeneral, SUM(u.cantidad) AS stockUbicaciones
        |        FROM existencias_ubicacion_tienda u WHERE u.sucursalId=? AND
        |            NOT EXISTS (SELECT 1 FROM existencias_tienda e
        |                WHERE e.sucursalId=u.sucursalId AND e.productoId=u.productoId)
        |        GROUP BY u.sucursalId, u.productoId
        |        HAVING SUM(u.cantidad) != 0
        |        ORDER BY productoId
        """.trimMargin()
    return createFlow(__db, false, arrayOf("existencias_ubicacion_tienda", "existencias_tienda")) {
        _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, sid)
        val _columnIndexOfSucursalId: Int = 0
        val _columnIndexOfProductoId: Int = 1
        val _columnIndexOfStockGeneral: Int = 2
        val _columnIndexOfStockUbicaciones: Int = 3
        val _result: MutableList<DiferenciaStockTienda> = mutableListOf()
        while (_stmt.step()) {
          val _item: DiferenciaStockTienda
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpStockGeneral: Int
          _tmpStockGeneral = _stmt.getLong(_columnIndexOfStockGeneral).toInt()
          val _tmpStockUbicaciones: Int
          _tmpStockUbicaciones = _stmt.getLong(_columnIndexOfStockUbicaciones).toInt()
          _item =
              DiferenciaStockTienda(_tmpSucursalId,_tmpProductoId,_tmpStockGeneral,_tmpStockUbicaciones)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun stockUbicacionesTiendaActual(sid: Long):
      List<ExistenciaUbicacionTienda> {
    val _sql: String =
        "SELECT * FROM existencias_ubicacion_tienda WHERE sucursalId=? ORDER BY productoId,posicionId"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfPosicionId: Int = getColumnIndexOrThrow(_stmt, "posicionId")
        val _columnIndexOfCantidad: Int = getColumnIndexOrThrow(_stmt, "cantidad")
        val _result: MutableList<ExistenciaUbicacionTienda> = mutableListOf()
        while (_stmt.step()) {
          val _item: ExistenciaUbicacionTienda
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpPosicionId: Long
          _tmpPosicionId = _stmt.getLong(_columnIndexOfPosicionId)
          val _tmpCantidad: Int
          _tmpCantidad = _stmt.getLong(_columnIndexOfCantidad).toInt()
          _item =
              ExistenciaUbicacionTienda(_tmpId,_tmpSucursalId,_tmpProductoId,_tmpPosicionId,_tmpCantidad)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun stockUbicacionesTienda(sid: Long): Flow<List<ExistenciaUbicacionTienda>> {
    val _sql: String =
        "SELECT * FROM existencias_ubicacion_tienda WHERE sucursalId=? ORDER BY productoId, posicionId"
    return createFlow(__db, false, arrayOf("existencias_ubicacion_tienda")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfPosicionId: Int = getColumnIndexOrThrow(_stmt, "posicionId")
        val _columnIndexOfCantidad: Int = getColumnIndexOrThrow(_stmt, "cantidad")
        val _result: MutableList<ExistenciaUbicacionTienda> = mutableListOf()
        while (_stmt.step()) {
          val _item: ExistenciaUbicacionTienda
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpPosicionId: Long
          _tmpPosicionId = _stmt.getLong(_columnIndexOfPosicionId)
          val _tmpCantidad: Int
          _tmpCantidad = _stmt.getLong(_columnIndexOfCantidad).toInt()
          _item =
              ExistenciaUbicacionTienda(_tmpId,_tmpSucursalId,_tmpProductoId,_tmpPosicionId,_tmpCantidad)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun stockUbicacionTienda(
    sid: Long,
    pid: Long,
    pos: Long,
  ): ExistenciaUbicacionTienda? {
    val _sql: String =
        "SELECT * FROM existencias_ubicacion_tienda WHERE sucursalId=? AND productoId=? AND posicionId=? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, pid)
        _argIndex = 3
        _stmt.bindLong(_argIndex, pos)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfPosicionId: Int = getColumnIndexOrThrow(_stmt, "posicionId")
        val _columnIndexOfCantidad: Int = getColumnIndexOrThrow(_stmt, "cantidad")
        val _result: ExistenciaUbicacionTienda?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpPosicionId: Long
          _tmpPosicionId = _stmt.getLong(_columnIndexOfPosicionId)
          val _tmpCantidad: Int
          _tmpCantidad = _stmt.getLong(_columnIndexOfCantidad).toInt()
          _result =
              ExistenciaUbicacionTienda(_tmpId,_tmpSucursalId,_tmpProductoId,_tmpPosicionId,_tmpCantidad)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun movimientosUbicacionTienda(sid: Long): Flow<List<MovimientoUbicacionTienda>> {
    val _sql: String =
        "SELECT * FROM movimientos_ubicacion_tienda WHERE sucursalId=? ORDER BY fecha DESC,id DESC LIMIT 100"
    return createFlow(__db, false, arrayOf("movimientos_ubicacion_tienda")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUsuarioId: Int = getColumnIndexOrThrow(_stmt, "usuarioId")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfPosicionOrigenId: Int = getColumnIndexOrThrow(_stmt, "posicionOrigenId")
        val _columnIndexOfPosicionDestinoId: Int = getColumnIndexOrThrow(_stmt, "posicionDestinoId")
        val _columnIndexOfCantidad: Int = getColumnIndexOrThrow(_stmt, "cantidad")
        val _columnIndexOfTipo: Int = getColumnIndexOrThrow(_stmt, "tipo")
        val _columnIndexOfCajaId: Int = getColumnIndexOrThrow(_stmt, "cajaId")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _result: MutableList<MovimientoUbicacionTienda> = mutableListOf()
        while (_stmt.step()) {
          val _item: MovimientoUbicacionTienda
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpUsuarioId: Long
          _tmpUsuarioId = _stmt.getLong(_columnIndexOfUsuarioId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpPosicionOrigenId: Long?
          if (_stmt.isNull(_columnIndexOfPosicionOrigenId)) {
            _tmpPosicionOrigenId = null
          } else {
            _tmpPosicionOrigenId = _stmt.getLong(_columnIndexOfPosicionOrigenId)
          }
          val _tmpPosicionDestinoId: Long
          _tmpPosicionDestinoId = _stmt.getLong(_columnIndexOfPosicionDestinoId)
          val _tmpCantidad: Int
          _tmpCantidad = _stmt.getLong(_columnIndexOfCantidad).toInt()
          val _tmpTipo: String
          _tmpTipo = _stmt.getText(_columnIndexOfTipo)
          val _tmpCajaId: Long?
          if (_stmt.isNull(_columnIndexOfCajaId)) {
            _tmpCajaId = null
          } else {
            _tmpCajaId = _stmt.getLong(_columnIndexOfCajaId)
          }
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          _item =
              MovimientoUbicacionTienda(_tmpId,_tmpUsuarioId,_tmpSucursalId,_tmpProductoId,_tmpPosicionOrigenId,_tmpPosicionDestinoId,_tmpCantidad,_tmpTipo,_tmpCajaId,_tmpFecha)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun tiendasAutorizadas(uid: Long): Flow<List<Sucursal>> {
    val _sql: String =
        "SELECT s.* FROM sucursales s JOIN usuario_sucursal us ON us.sucursalId=s.id JOIN usuarios u ON u.id=us.usuarioId WHERE us.usuarioId=? AND u.activo=1 AND u.rol IN ('JEFE_TIENDA','TRABAJADOR_TIENDA') ORDER BY s.nombre"
    return createFlow(__db, false, arrayOf("sucursales", "usuario_sucursal", "usuarios")) {
        _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, uid)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _result: MutableList<Sucursal> = mutableListOf()
        while (_stmt.step()) {
          val _item: Sucursal
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpNombre: String
          _tmpNombre = _stmt.getText(_columnIndexOfNombre)
          _item = Sucursal(_tmpId,_tmpNombre)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observarAccesoTienda(
    uid: Long,
    sid: Long,
    rol: String,
  ): Flow<Int> {
    val _sql: String =
        "SELECT COUNT(*) FROM usuario_sucursal us JOIN usuarios u ON u.id=us.usuarioId JOIN sucursales s ON s.id=us.sucursalId WHERE us.usuarioId=? AND us.sucursalId=? AND u.activo=1 AND u.rol=? AND ? IN ('TRABAJADOR_TIENDA','JEFE_TIENDA')"
    return createFlow(__db, false, arrayOf("usuario_sucursal", "usuarios", "sucursales")) {
        _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, uid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, sid)
        _argIndex = 3
        _stmt.bindText(_argIndex, rol)
        _argIndex = 4
        _stmt.bindText(_argIndex, rol)
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

  public override suspend fun jefeAutorizadoTienda(uid: Long, sid: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM usuario_sucursal us JOIN usuarios u ON u.id=us.usuarioId WHERE us.usuarioId=? AND us.sucursalId=? AND u.activo=1 AND u.rol='JEFE_TIENDA'"
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

  public override fun trabajadoresDeTienda(sid: Long): Flow<List<Usuario>> {
    val _sql: String =
        "SELECT u.* FROM usuarios u JOIN usuario_sucursal us ON us.usuarioId=u.id WHERE us.sucursalId=? AND u.rol='TRABAJADOR_TIENDA' AND u.activo=1 ORDER BY u.nombre"
    return createFlow(__db, false, arrayOf("usuarios", "usuario_sucursal")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
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

  public override suspend fun trabajadoresActivosTienda(sid: Long): Int {
    val _sql: String = """
        |SELECT COUNT(*) FROM usuarios u JOIN usuario_sucursal us ON us.usuarioId=u.id
        |        WHERE us.sucursalId=? AND u.rol='TRABAJADOR_TIENDA' AND u.activo=1
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
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

  public override fun trabajadoresTienda(): Flow<List<Usuario>> {
    val _sql: String =
        "SELECT u.* FROM usuarios u WHERE u.rol='TRABAJADOR_TIENDA' AND u.activo=1 ORDER BY u.nombre"
    return createFlow(__db, false, arrayOf("usuarios")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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

  public override fun trabajadoresSinAsignacion(): Flow<List<Usuario>> {
    val _sql: String =
        "SELECT * FROM usuarios u WHERE u.rol='TRABAJADOR_TIENDA' AND u.activo=1 AND NOT EXISTS (SELECT 1 FROM usuario_sucursal us WHERE us.usuarioId=u.id) ORDER BY u.nombre"
    return createFlow(__db, false, arrayOf("usuarios", "usuario_sucursal")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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

  public override suspend fun numeroTiendasUsuario(uid: Long): Int {
    val _sql: String = "SELECT COUNT(*) FROM usuario_sucursal WHERE usuarioId=?"
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

  public override suspend fun tieneAsignacionTienda(uid: Long, sid: Long): Int {
    val _sql: String = "SELECT COUNT(*) FROM usuario_sucursal WHERE usuarioId=? AND sucursalId=?"
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

  public override fun bodegasDeTienda(sid: Long): Flow<List<Bodega>> {
    val _sql: String =
        "SELECT b.* FROM bodegas b WHERE b.sucursalId=? AND b.activa=1 ORDER BY b.nombre"
    return createFlow(__db, false, arrayOf("bodegas")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _columnIndexOfActiva: Int = getColumnIndexOrThrow(_stmt, "activa")
        val _result: MutableList<Bodega> = mutableListOf()
        while (_stmt.step()) {
          val _item: Bodega
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
          _item = Bodega(_tmpId,_tmpSucursalId,_tmpNombre,_tmpActiva)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun productosEnReposicion(sid: Long): Flow<List<Long>> {
    val _sql: String =
        "SELECT s.productoId FROM solicitudes_reposicion s LEFT JOIN cajas c ON c.id=s.cajaId WHERE s.sucursalId=? AND (s.estado='PENDIENTE' OR (s.estado='ATENDIDA' AND c.estado NOT IN ('RECIBIDA','CON_DIFERENCIA','CANCELADA')))"
    return createFlow(__db, false, arrayOf("solicitudes_reposicion", "cajas")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
        val _result: MutableList<Long> = mutableListOf()
        while (_stmt.step()) {
          val _item: Long
          _item = _stmt.getLong(0)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun solicitudesDeTienda(sid: Long): Flow<List<SolicitudReposicion>> {
    val _sql: String =
        "SELECT * FROM solicitudes_reposicion WHERE sucursalId=? ORDER BY fecha DESC,id DESC"
    return createFlow(__db, false, arrayOf("solicitudes_reposicion")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfBodegaId: Int = getColumnIndexOrThrow(_stmt, "bodegaId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfCantidad: Int = getColumnIndexOrThrow(_stmt, "cantidad")
        val _columnIndexOfSolicitadoPor: Int = getColumnIndexOrThrow(_stmt, "solicitadoPor")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _columnIndexOfEstado: Int = getColumnIndexOrThrow(_stmt, "estado")
        val _columnIndexOfCajaId: Int = getColumnIndexOrThrow(_stmt, "cajaId")
        val _columnIndexOfLoteId: Int = getColumnIndexOrThrow(_stmt, "loteId")
        val _result: MutableList<SolicitudReposicion> = mutableListOf()
        while (_stmt.step()) {
          val _item: SolicitudReposicion
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpBodegaId: Long
          _tmpBodegaId = _stmt.getLong(_columnIndexOfBodegaId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpCantidad: Int
          _tmpCantidad = _stmt.getLong(_columnIndexOfCantidad).toInt()
          val _tmpSolicitadoPor: Long
          _tmpSolicitadoPor = _stmt.getLong(_columnIndexOfSolicitadoPor)
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          val _tmpEstado: String
          _tmpEstado = _stmt.getText(_columnIndexOfEstado)
          val _tmpCajaId: Long?
          if (_stmt.isNull(_columnIndexOfCajaId)) {
            _tmpCajaId = null
          } else {
            _tmpCajaId = _stmt.getLong(_columnIndexOfCajaId)
          }
          val _tmpLoteId: String?
          if (_stmt.isNull(_columnIndexOfLoteId)) {
            _tmpLoteId = null
          } else {
            _tmpLoteId = _stmt.getText(_columnIndexOfLoteId)
          }
          _item =
              SolicitudReposicion(_tmpId,_tmpSucursalId,_tmpBodegaId,_tmpProductoId,_tmpCantidad,_tmpSolicitadoPor,_tmpFecha,_tmpEstado,_tmpCajaId,_tmpLoteId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun solicitudesDeBodega(bid: Long): Flow<List<SolicitudReposicion>> {
    val _sql: String =
        "SELECT * FROM solicitudes_reposicion WHERE bodegaId=? ORDER BY fecha DESC,id DESC"
    return createFlow(__db, false, arrayOf("solicitudes_reposicion")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, bid)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfBodegaId: Int = getColumnIndexOrThrow(_stmt, "bodegaId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfCantidad: Int = getColumnIndexOrThrow(_stmt, "cantidad")
        val _columnIndexOfSolicitadoPor: Int = getColumnIndexOrThrow(_stmt, "solicitadoPor")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _columnIndexOfEstado: Int = getColumnIndexOrThrow(_stmt, "estado")
        val _columnIndexOfCajaId: Int = getColumnIndexOrThrow(_stmt, "cajaId")
        val _columnIndexOfLoteId: Int = getColumnIndexOrThrow(_stmt, "loteId")
        val _result: MutableList<SolicitudReposicion> = mutableListOf()
        while (_stmt.step()) {
          val _item: SolicitudReposicion
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpBodegaId: Long
          _tmpBodegaId = _stmt.getLong(_columnIndexOfBodegaId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpCantidad: Int
          _tmpCantidad = _stmt.getLong(_columnIndexOfCantidad).toInt()
          val _tmpSolicitadoPor: Long
          _tmpSolicitadoPor = _stmt.getLong(_columnIndexOfSolicitadoPor)
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          val _tmpEstado: String
          _tmpEstado = _stmt.getText(_columnIndexOfEstado)
          val _tmpCajaId: Long?
          if (_stmt.isNull(_columnIndexOfCajaId)) {
            _tmpCajaId = null
          } else {
            _tmpCajaId = _stmt.getLong(_columnIndexOfCajaId)
          }
          val _tmpLoteId: String?
          if (_stmt.isNull(_columnIndexOfLoteId)) {
            _tmpLoteId = null
          } else {
            _tmpLoteId = _stmt.getText(_columnIndexOfLoteId)
          }
          _item =
              SolicitudReposicion(_tmpId,_tmpSucursalId,_tmpBodegaId,_tmpProductoId,_tmpCantidad,_tmpSolicitadoPor,_tmpFecha,_tmpEstado,_tmpCajaId,_tmpLoteId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun solicitudPorId(id: Long): SolicitudReposicion? {
    val _sql: String = "SELECT * FROM solicitudes_reposicion WHERE id=? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfBodegaId: Int = getColumnIndexOrThrow(_stmt, "bodegaId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfCantidad: Int = getColumnIndexOrThrow(_stmt, "cantidad")
        val _columnIndexOfSolicitadoPor: Int = getColumnIndexOrThrow(_stmt, "solicitadoPor")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _columnIndexOfEstado: Int = getColumnIndexOrThrow(_stmt, "estado")
        val _columnIndexOfCajaId: Int = getColumnIndexOrThrow(_stmt, "cajaId")
        val _columnIndexOfLoteId: Int = getColumnIndexOrThrow(_stmt, "loteId")
        val _result: SolicitudReposicion?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpBodegaId: Long
          _tmpBodegaId = _stmt.getLong(_columnIndexOfBodegaId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpCantidad: Int
          _tmpCantidad = _stmt.getLong(_columnIndexOfCantidad).toInt()
          val _tmpSolicitadoPor: Long
          _tmpSolicitadoPor = _stmt.getLong(_columnIndexOfSolicitadoPor)
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          val _tmpEstado: String
          _tmpEstado = _stmt.getText(_columnIndexOfEstado)
          val _tmpCajaId: Long?
          if (_stmt.isNull(_columnIndexOfCajaId)) {
            _tmpCajaId = null
          } else {
            _tmpCajaId = _stmt.getLong(_columnIndexOfCajaId)
          }
          val _tmpLoteId: String?
          if (_stmt.isNull(_columnIndexOfLoteId)) {
            _tmpLoteId = null
          } else {
            _tmpLoteId = _stmt.getText(_columnIndexOfLoteId)
          }
          _result =
              SolicitudReposicion(_tmpId,_tmpSucursalId,_tmpBodegaId,_tmpProductoId,_tmpCantidad,_tmpSolicitadoPor,_tmpFecha,_tmpEstado,_tmpCajaId,_tmpLoteId)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun solicitudPendiente(sid: Long, pid: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM solicitudes_reposicion s LEFT JOIN cajas c ON c.id=s.cajaId WHERE s.sucursalId=? AND s.productoId=? AND (s.estado='PENDIENTE' OR (s.estado='ATENDIDA' AND c.estado NOT IN ('RECIBIDA','CON_DIFERENCIA','CANCELADA')))"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, pid)
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

  public override suspend fun solicitudesPendientesGrupo(id: Long, loteId: String?):
      List<SolicitudReposicion> {
    val _sql: String =
        "SELECT * FROM solicitudes_reposicion WHERE estado='PENDIENTE' AND ((? IS NOT NULL AND loteId=?) OR (? IS NULL AND id=?)) ORDER BY id"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        if (loteId == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, loteId)
        }
        _argIndex = 2
        if (loteId == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, loteId)
        }
        _argIndex = 3
        if (loteId == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, loteId)
        }
        _argIndex = 4
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfBodegaId: Int = getColumnIndexOrThrow(_stmt, "bodegaId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfCantidad: Int = getColumnIndexOrThrow(_stmt, "cantidad")
        val _columnIndexOfSolicitadoPor: Int = getColumnIndexOrThrow(_stmt, "solicitadoPor")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _columnIndexOfEstado: Int = getColumnIndexOrThrow(_stmt, "estado")
        val _columnIndexOfCajaId: Int = getColumnIndexOrThrow(_stmt, "cajaId")
        val _columnIndexOfLoteId: Int = getColumnIndexOrThrow(_stmt, "loteId")
        val _result: MutableList<SolicitudReposicion> = mutableListOf()
        while (_stmt.step()) {
          val _item: SolicitudReposicion
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpBodegaId: Long
          _tmpBodegaId = _stmt.getLong(_columnIndexOfBodegaId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpCantidad: Int
          _tmpCantidad = _stmt.getLong(_columnIndexOfCantidad).toInt()
          val _tmpSolicitadoPor: Long
          _tmpSolicitadoPor = _stmt.getLong(_columnIndexOfSolicitadoPor)
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          val _tmpEstado: String
          _tmpEstado = _stmt.getText(_columnIndexOfEstado)
          val _tmpCajaId: Long?
          if (_stmt.isNull(_columnIndexOfCajaId)) {
            _tmpCajaId = null
          } else {
            _tmpCajaId = _stmt.getLong(_columnIndexOfCajaId)
          }
          val _tmpLoteId: String?
          if (_stmt.isNull(_columnIndexOfLoteId)) {
            _tmpLoteId = null
          } else {
            _tmpLoteId = _stmt.getText(_columnIndexOfLoteId)
          }
          _item =
              SolicitudReposicion(_tmpId,_tmpSucursalId,_tmpBodegaId,_tmpProductoId,_tmpCantidad,_tmpSolicitadoPor,_tmpFecha,_tmpEstado,_tmpCajaId,_tmpLoteId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun sumarStockTienda(id: Long, cantidad: Int): Int {
    val _sql: String = "UPDATE existencias_tienda SET cantidad=cantidad+? WHERE id=? AND ?>=0"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, cantidad.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, id)
        _argIndex = 3
        _stmt.bindLong(_argIndex, cantidad.toLong())
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun registrarLlegadaTienda(id: Long, sid: Long): Int {
    val _sql: String =
        "UPDATE cajas SET estado='EN_REVISION' WHERE id=? AND tipo='SALIDA' AND sucursalDestinoId=? AND estado='EN_TRANSITO'"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        _argIndex = 2
        _stmt.bindLong(_argIndex, sid)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun marcarRecepcionTienda(
    id: Long,
    sid: Long,
    nuevo: String,
  ): Int {
    val _sql: String =
        "UPDATE cajas SET estado=? WHERE id=? AND tipo='SALIDA' AND sucursalDestinoId=? AND estado='EN_REVISION'"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, nuevo)
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

  public override suspend fun mejorarEstanteDemoTienda(
    sid: Long,
    codigo: String,
    nombre: String,
    padreId: Long,
  ): Int {
    val _sql: String = """
        |UPDATE posiciones_tienda SET nombre=?, padreId=?
        |        WHERE sucursalId=? AND codigo=? AND nombre=? AND tipo='ESTANTERIA'
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, nombre)
        _argIndex = 2
        _stmt.bindLong(_argIndex, padreId)
        _argIndex = 3
        _stmt.bindLong(_argIndex, sid)
        _argIndex = 4
        _stmt.bindText(_argIndex, codigo)
        _argIndex = 5
        _stmt.bindText(_argIndex, codigo)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun renombrarPosicionTienda(
    id: Long,
    sid: Long,
    nombre: String,
  ): Int {
    val _sql: String = "UPDATE posiciones_tienda SET nombre=? WHERE id=? AND sucursalId=?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, nombre)
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

  public override suspend fun sumarStockUbicacionTienda(id: Long, cantidad: Int): Int {
    val _sql: String =
        "UPDATE existencias_ubicacion_tienda SET cantidad=cantidad+? WHERE id=? AND ?>0"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, cantidad.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, id)
        _argIndex = 3
        _stmt.bindLong(_argIndex, cantidad.toLong())
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun restarStockUbicacionTienda(id: Long, cantidad: Int): Int {
    val _sql: String =
        "UPDATE existencias_ubicacion_tienda SET cantidad=cantidad-? WHERE id=? AND cantidad>=? AND ?>0"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, cantidad.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, id)
        _argIndex = 3
        _stmt.bindLong(_argIndex, cantidad.toLong())
        _argIndex = 4
        _stmt.bindLong(_argIndex, cantidad.toLong())
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun retirarAsignacionTienda(uid: Long, sid: Long): Int {
    val _sql: String = "DELETE FROM usuario_sucursal WHERE usuarioId=? AND sucursalId=?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, uid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, sid)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun cancelarGrupo(sid: Long, loteId: String): Int {
    val _sql: String =
        "UPDATE solicitudes_reposicion SET estado='CANCELADA' WHERE sucursalId=? AND loteId=? AND estado='PENDIENTE'"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
        _argIndex = 2
        _stmt.bindText(_argIndex, loteId)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun cancelarSolicitud(id: Long, sid: Long): Int {
    val _sql: String =
        "UPDATE solicitudes_reposicion SET estado='CANCELADA' WHERE id=? AND estado='PENDIENTE' AND sucursalId=?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        _argIndex = 2
        _stmt.bindLong(_argIndex, sid)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun atenderSolicitud(
    id: Long,
    bid: Long,
    cajaId: Long,
  ): Int {
    val _sql: String =
        "UPDATE solicitudes_reposicion SET estado='ATENDIDA', cajaId=? WHERE id=? AND estado='PENDIENTE' AND bodegaId=?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, cajaId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, id)
        _argIndex = 3
        _stmt.bindLong(_argIndex, bid)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun reabrirSolicitudCaja(cajaId: Long): Int {
    val _sql: String =
        "UPDATE solicitudes_reposicion SET estado='PENDIENTE', cajaId=NULL WHERE cajaId=? AND estado='ATENDIDA'"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, cajaId)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun revisarIncidenciaTienda(incidenciaId: Long, sid: Long): Int {
    val _sql: String =
        "UPDATE incidencias_caja SET estado='REVISADA' WHERE id=? AND estado='PENDIENTE' AND EXISTS (SELECT 1 FROM cajas c WHERE c.id=incidencias_caja.cajaId AND c.tipo='SALIDA' AND c.sucursalDestinoId=?)"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, incidenciaId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, sid)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun cambiarEstadoPosicionTienda(
    id: Long,
    sid: Long,
    activa: Boolean,
  ): Int {
    val _sql: String = "UPDATE posiciones_tienda SET activa=? WHERE id=? AND sucursalId=?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        val _tmp: Int = if (activa) 1 else 0
        _stmt.bindLong(_argIndex, _tmp.toLong())
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

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
