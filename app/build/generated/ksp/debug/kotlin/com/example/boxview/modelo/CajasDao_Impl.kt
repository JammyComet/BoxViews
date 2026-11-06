package com.example.boxview.modelo

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.getTotalChangedRows
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class CajasDao_Impl(
  __db: RoomDatabase,
) : CajasDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfCaja: EntityInsertAdapter<Caja>

  private val __insertAdapterOfDetalleCaja: EntityInsertAdapter<DetalleCaja>

  private val __insertAdapterOfEventoCaja: EntityInsertAdapter<EventoCaja>

  private val __insertAdapterOfIncidenciaCaja: EntityInsertAdapter<IncidenciaCaja>

  private val __insertAdapterOfPreparacionCaja: EntityInsertAdapter<PreparacionCaja>

  private val __updateAdapterOfCaja: EntityDeleteOrUpdateAdapter<Caja>

  private val __updateAdapterOfDetalleCaja: EntityDeleteOrUpdateAdapter<DetalleCaja>
  init {
    this.__db = __db
    this.__insertAdapterOfCaja = object : EntityInsertAdapter<Caja>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `cajas` (`id`,`codigo`,`tipo`,`origen`,`bodegaOrigenId`,`bodegaDestinoId`,`solicitadoPor`,`asignadoA`,`estado`,`preparadoPor`,`revisorId`,`fecha`,`sucursalDestinoId`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Caja) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.codigo)
        statement.bindText(3, entity.tipo)
        statement.bindText(4, entity.origen)
        statement.bindLong(5, entity.bodegaOrigenId)
        statement.bindLong(6, entity.bodegaDestinoId)
        statement.bindText(7, entity.solicitadoPor)
        statement.bindLong(8, entity.asignadoA)
        statement.bindText(9, entity.estado)
        val _tmpPreparadoPor: Long? = entity.preparadoPor
        if (_tmpPreparadoPor == null) {
          statement.bindNull(10)
        } else {
          statement.bindLong(10, _tmpPreparadoPor)
        }
        val _tmpRevisorId: Long? = entity.revisorId
        if (_tmpRevisorId == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmpRevisorId)
        }
        statement.bindLong(12, entity.fecha)
        val _tmpSucursalDestinoId: Long? = entity.sucursalDestinoId
        if (_tmpSucursalDestinoId == null) {
          statement.bindNull(13)
        } else {
          statement.bindLong(13, _tmpSucursalDestinoId)
        }
      }
    }
    this.__insertAdapterOfDetalleCaja = object : EntityInsertAdapter<DetalleCaja>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `detalle_caja` (`id`,`cajaId`,`productoId`,`cantidadEsperada`,`cantidadVerificada`,`posicionId`,`cantidadRecibida`,`posicionDestinoId`,`cantidadContadaFisica`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: DetalleCaja) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.cajaId)
        statement.bindLong(3, entity.productoId)
        statement.bindLong(4, entity.cantidadEsperada.toLong())
        statement.bindLong(5, entity.cantidadVerificada.toLong())
        val _tmpPosicionId: Long? = entity.posicionId
        if (_tmpPosicionId == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpPosicionId)
        }
        statement.bindLong(7, entity.cantidadRecibida.toLong())
        val _tmpPosicionDestinoId: Long? = entity.posicionDestinoId
        if (_tmpPosicionDestinoId == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpPosicionDestinoId)
        }
        val _tmpCantidadContadaFisica: Int? = entity.cantidadContadaFisica
        if (_tmpCantidadContadaFisica == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpCantidadContadaFisica.toLong())
        }
      }
    }
    this.__insertAdapterOfEventoCaja = object : EntityInsertAdapter<EventoCaja>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `eventos_caja` (`id`,`cajaId`,`usuarioId`,`accion`,`observacion`,`fecha`) VALUES (nullif(?, 0),?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: EventoCaja) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.cajaId)
        statement.bindLong(3, entity.usuarioId)
        statement.bindText(4, entity.accion)
        statement.bindText(5, entity.observacion)
        statement.bindLong(6, entity.fecha)
      }
    }
    this.__insertAdapterOfIncidenciaCaja = object : EntityInsertAdapter<IncidenciaCaja>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `incidencias_caja` (`id`,`cajaId`,`productoId`,`esperado`,`contado`,`estado`,`fecha`) VALUES (nullif(?, 0),?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: IncidenciaCaja) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.cajaId)
        statement.bindLong(3, entity.productoId)
        statement.bindLong(4, entity.esperado.toLong())
        statement.bindLong(5, entity.contado.toLong())
        statement.bindText(6, entity.estado)
        statement.bindLong(7, entity.fecha)
      }
    }
    this.__insertAdapterOfPreparacionCaja = object : EntityInsertAdapter<PreparacionCaja>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `preparaciones_caja` (`id`,`cajaId`,`productoId`,`posicionId`,`cantidad`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: PreparacionCaja) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.cajaId)
        statement.bindLong(3, entity.productoId)
        statement.bindLong(4, entity.posicionId)
        statement.bindLong(5, entity.cantidad.toLong())
      }
    }
    this.__updateAdapterOfCaja = object : EntityDeleteOrUpdateAdapter<Caja>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `cajas` SET `id` = ?,`codigo` = ?,`tipo` = ?,`origen` = ?,`bodegaOrigenId` = ?,`bodegaDestinoId` = ?,`solicitadoPor` = ?,`asignadoA` = ?,`estado` = ?,`preparadoPor` = ?,`revisorId` = ?,`fecha` = ?,`sucursalDestinoId` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Caja) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.codigo)
        statement.bindText(3, entity.tipo)
        statement.bindText(4, entity.origen)
        statement.bindLong(5, entity.bodegaOrigenId)
        statement.bindLong(6, entity.bodegaDestinoId)
        statement.bindText(7, entity.solicitadoPor)
        statement.bindLong(8, entity.asignadoA)
        statement.bindText(9, entity.estado)
        val _tmpPreparadoPor: Long? = entity.preparadoPor
        if (_tmpPreparadoPor == null) {
          statement.bindNull(10)
        } else {
          statement.bindLong(10, _tmpPreparadoPor)
        }
        val _tmpRevisorId: Long? = entity.revisorId
        if (_tmpRevisorId == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmpRevisorId)
        }
        statement.bindLong(12, entity.fecha)
        val _tmpSucursalDestinoId: Long? = entity.sucursalDestinoId
        if (_tmpSucursalDestinoId == null) {
          statement.bindNull(13)
        } else {
          statement.bindLong(13, _tmpSucursalDestinoId)
        }
        statement.bindLong(14, entity.id)
      }
    }
    this.__updateAdapterOfDetalleCaja = object : EntityDeleteOrUpdateAdapter<DetalleCaja>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `detalle_caja` SET `id` = ?,`cajaId` = ?,`productoId` = ?,`cantidadEsperada` = ?,`cantidadVerificada` = ?,`posicionId` = ?,`cantidadRecibida` = ?,`posicionDestinoId` = ?,`cantidadContadaFisica` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: DetalleCaja) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.cajaId)
        statement.bindLong(3, entity.productoId)
        statement.bindLong(4, entity.cantidadEsperada.toLong())
        statement.bindLong(5, entity.cantidadVerificada.toLong())
        val _tmpPosicionId: Long? = entity.posicionId
        if (_tmpPosicionId == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpPosicionId)
        }
        statement.bindLong(7, entity.cantidadRecibida.toLong())
        val _tmpPosicionDestinoId: Long? = entity.posicionDestinoId
        if (_tmpPosicionDestinoId == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpPosicionDestinoId)
        }
        val _tmpCantidadContadaFisica: Int? = entity.cantidadContadaFisica
        if (_tmpCantidadContadaFisica == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpCantidadContadaFisica.toLong())
        }
        statement.bindLong(10, entity.id)
      }
    }
  }

  public override suspend fun guardarCaja(valor: Caja): Unit = performSuspending(__db, false, true)
      { _connection ->
    __insertAdapterOfCaja.insert(_connection, valor)
  }

  public override suspend fun guardarDetalleCaja(valor: DetalleCaja): Long = performSuspending(__db,
      false, true) { _connection ->
    val _result: Long = __insertAdapterOfDetalleCaja.insertAndReturnId(_connection, valor)
    _result
  }

  public override suspend fun guardarEventoCaja(valor: EventoCaja): Long = performSuspending(__db,
      false, true) { _connection ->
    val _result: Long = __insertAdapterOfEventoCaja.insertAndReturnId(_connection, valor)
    _result
  }

  public override suspend fun guardarIncidenciaCaja(valor: IncidenciaCaja): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfIncidenciaCaja.insertAndReturnId(_connection, valor)
    _result
  }

  public override suspend fun guardarPreparacionCaja(valor: PreparacionCaja): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfPreparacionCaja.insertAndReturnId(_connection, valor)
    _result
  }

  public override suspend fun actualizarCaja(valor: Caja): Unit = performSuspending(__db, false,
      true) { _connection ->
    __updateAdapterOfCaja.handle(_connection, valor)
  }

  public override suspend fun actualizarDetalleCaja(valor: DetalleCaja): Unit =
      performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfDetalleCaja.handle(_connection, valor)
  }

  public override suspend fun numeroCajas(): Int {
    val _sql: String = "SELECT COUNT(*) FROM cajas"
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

  public override fun cajasIngreso(bid: Long, uid: Long): Flow<List<Caja>> {
    val _sql: String =
        "SELECT * FROM cajas WHERE bodegaDestinoId=? AND asignadoA=? AND tipo='INGRESO' AND estado IN ('ESPERADA','EN_TRANSITO','EN_REVISION') ORDER BY fecha DESC"
    return createFlow(__db, false, arrayOf("cajas")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, bid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, uid)
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

  public override fun ordenesSalida(uid: Long, bid: Long): Flow<List<Caja>> {
    val _sql: String =
        "SELECT * FROM cajas WHERE bodegaOrigenId=? AND tipo='SALIDA' AND asignadoA=? AND estado='ORDEN_ASIGNADA' ORDER BY fecha DESC"
    return createFlow(__db, false, arrayOf("cajas")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, bid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, uid)
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

  public override fun salidasEnviadas(uid: Long, bid: Long): Flow<List<Caja>> {
    val _sql: String =
        "SELECT * FROM cajas WHERE bodegaOrigenId=? AND tipo='SALIDA' AND asignadoA=? AND estado IN ('EN_TRANSITO','EN_REVISION','RECIBIDA','CON_DIFERENCIA') ORDER BY fecha DESC LIMIT 20"
    return createFlow(__db, false, arrayOf("cajas")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, bid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, uid)
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

  public override fun observarCaja(id: Long): Flow<Caja?> {
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

  public override suspend fun cajaPorId(id: Long): Caja? {
    val _sql: String = "SELECT * FROM cajas WHERE id=? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
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

  public override suspend fun cajaPorCodigo(codigo: String): Caja? {
    val _sql: String = "SELECT * FROM cajas WHERE codigo=? COLLATE NOCASE LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, codigo)
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

  public override fun observarDetallesCaja(id: Long): Flow<List<DetalleCaja>> {
    val _sql: String = "SELECT * FROM detalle_caja WHERE cajaId=? ORDER BY id"
    return createFlow(__db, false, arrayOf("detalle_caja")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
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

  public override suspend fun detallesCaja(id: Long): List<DetalleCaja> {
    val _sql: String = "SELECT * FROM detalle_caja WHERE cajaId=? ORDER BY id"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
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

  public override suspend fun stockReservadoCajas(
    pid: Long,
    pos: Long,
    excluir: Long,
  ): Int {
    val _sql: String =
        "SELECT COALESCE(SUM(p.cantidad),0) FROM preparaciones_caja p JOIN cajas c ON c.id=p.cajaId WHERE p.productoId=? AND p.posicionId=? AND c.tipo='SALIDA' AND c.estado IN ('ORDEN_ASIGNADA','PENDIENTE_REVISION') AND c.id!=?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, pid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, pos)
        _argIndex = 3
        _stmt.bindLong(_argIndex, excluir)
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

  public override fun reservasOtrasCajas(bid: Long, excluir: Long): Flow<List<ReservaEstante>> {
    val _sql: String =
        "SELECT p.productoId AS productoId, p.posicionId AS posicionId, COALESCE(SUM(p.cantidad),0) AS cantidadReservada FROM preparaciones_caja p JOIN cajas c ON c.id=p.cajaId WHERE c.bodegaOrigenId=? AND c.tipo='SALIDA' AND c.estado IN ('ORDEN_ASIGNADA','PENDIENTE_REVISION') AND c.id!=? GROUP BY p.productoId,p.posicionId"
    return createFlow(__db, false, arrayOf("preparaciones_caja", "cajas")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, bid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, excluir)
        val _columnIndexOfProductoId: Int = 0
        val _columnIndexOfPosicionId: Int = 1
        val _columnIndexOfCantidadReservada: Int = 2
        val _result: MutableList<ReservaEstante> = mutableListOf()
        while (_stmt.step()) {
          val _item: ReservaEstante
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpPosicionId: Long
          _tmpPosicionId = _stmt.getLong(_columnIndexOfPosicionId)
          val _tmpCantidadReservada: Int
          _tmpCantidadReservada = _stmt.getLong(_columnIndexOfCantidadReservada).toInt()
          _item = ReservaEstante(_tmpProductoId,_tmpPosicionId,_tmpCantidadReservada)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observarPreparacionesCaja(id: Long): Flow<List<PreparacionCaja>> {
    val _sql: String = "SELECT * FROM preparaciones_caja WHERE cajaId=? ORDER BY id"
    return createFlow(__db, false, arrayOf("preparaciones_caja")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCajaId: Int = getColumnIndexOrThrow(_stmt, "cajaId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfPosicionId: Int = getColumnIndexOrThrow(_stmt, "posicionId")
        val _columnIndexOfCantidad: Int = getColumnIndexOrThrow(_stmt, "cantidad")
        val _result: MutableList<PreparacionCaja> = mutableListOf()
        while (_stmt.step()) {
          val _item: PreparacionCaja
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpCajaId: Long
          _tmpCajaId = _stmt.getLong(_columnIndexOfCajaId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpPosicionId: Long
          _tmpPosicionId = _stmt.getLong(_columnIndexOfPosicionId)
          val _tmpCantidad: Int
          _tmpCantidad = _stmt.getLong(_columnIndexOfCantidad).toInt()
          _item = PreparacionCaja(_tmpId,_tmpCajaId,_tmpProductoId,_tmpPosicionId,_tmpCantidad)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun preparacionesCaja(id: Long): List<PreparacionCaja> {
    val _sql: String = "SELECT * FROM preparaciones_caja WHERE cajaId=? ORDER BY id"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCajaId: Int = getColumnIndexOrThrow(_stmt, "cajaId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfPosicionId: Int = getColumnIndexOrThrow(_stmt, "posicionId")
        val _columnIndexOfCantidad: Int = getColumnIndexOrThrow(_stmt, "cantidad")
        val _result: MutableList<PreparacionCaja> = mutableListOf()
        while (_stmt.step()) {
          val _item: PreparacionCaja
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpCajaId: Long
          _tmpCajaId = _stmt.getLong(_columnIndexOfCajaId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpPosicionId: Long
          _tmpPosicionId = _stmt.getLong(_columnIndexOfPosicionId)
          val _tmpCantidad: Int
          _tmpCantidad = _stmt.getLong(_columnIndexOfCantidad).toInt()
          _item = PreparacionCaja(_tmpId,_tmpCajaId,_tmpProductoId,_tmpPosicionId,_tmpCantidad)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun ordenesJefatura(bid: Long): Flow<List<Caja>> {
    val _sql: String =
        "SELECT * FROM cajas WHERE bodegaOrigenId=? AND tipo='SALIDA' ORDER BY fecha DESC, id DESC"
    return createFlow(__db, false, arrayOf("cajas")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, bid)
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

  public override suspend fun siguienteIdCaja(): Long {
    val _sql: String = "SELECT COALESCE(MAX(id),0)+1 FROM cajas"
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

  public override suspend fun avancesDeCaja(id: Long): Int {
    val _sql: String = "SELECT COUNT(*) FROM preparaciones_caja WHERE cajaId=? AND cantidad>0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
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

  public override fun incidenciasDeBodega(bid: Long): Flow<List<IncidenciaCaja>> {
    val _sql: String =
        "SELECT i.* FROM incidencias_caja i JOIN cajas c ON c.id=i.cajaId WHERE c.bodegaDestinoId=? AND c.tipo='INGRESO' ORDER BY i.fecha DESC, i.id DESC"
    return createFlow(__db, false, arrayOf("incidencias_caja", "cajas")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, bid)
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

  public override fun cajasDeDestino(bid: Long): Flow<List<Caja>> {
    val _sql: String =
        "SELECT * FROM cajas WHERE bodegaDestinoId=? AND tipo='INGRESO' ORDER BY fecha DESC"
    return createFlow(__db, false, arrayOf("cajas")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, bid)
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

  public override suspend fun incidenciaPorId(id: Long): IncidenciaCaja? {
    val _sql: String = "SELECT * FROM incidencias_caja WHERE id=? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCajaId: Int = getColumnIndexOrThrow(_stmt, "cajaId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfEsperado: Int = getColumnIndexOrThrow(_stmt, "esperado")
        val _columnIndexOfContado: Int = getColumnIndexOrThrow(_stmt, "contado")
        val _columnIndexOfEstado: Int = getColumnIndexOrThrow(_stmt, "estado")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _result: IncidenciaCaja?
        if (_stmt.step()) {
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
          _result =
              IncidenciaCaja(_tmpId,_tmpCajaId,_tmpProductoId,_tmpEsperado,_tmpContado,_tmpEstado,_tmpFecha)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun ingresosPendientesOperador(uid: Long, bid: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM cajas WHERE asignadoA=? AND bodegaDestinoId=? AND tipo='INGRESO' AND estado IN ('ESPERADA','EN_TRANSITO','EN_REVISION')"
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

  public override suspend fun ordenesActivasOperador(uid: Long, bid: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM cajas WHERE asignadoA=? AND bodegaOrigenId=? AND tipo='SALIDA' AND estado IN ('ORDEN_ASIGNADA','PENDIENTE_REVISION')"
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

  public override fun compromisosSalida(bid: Long): Flow<List<CompromisoProducto>> {
    val _sql: String =
        "SELECT d.productoId AS productoId, COALESCE(SUM(d.cantidadEsperada),0) AS cantidadSolicitada FROM detalle_caja d JOIN cajas c ON c.id=d.cajaId WHERE c.bodegaOrigenId=? AND c.tipo='SALIDA' AND c.estado IN ('ORDEN_ASIGNADA','PENDIENTE_REVISION') GROUP BY d.productoId"
    return createFlow(__db, false, arrayOf("detalle_caja", "cajas")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, bid)
        val _columnIndexOfProductoId: Int = 0
        val _columnIndexOfCantidadSolicitada: Int = 1
        val _result: MutableList<CompromisoProducto> = mutableListOf()
        while (_stmt.step()) {
          val _item: CompromisoProducto
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpCantidadSolicitada: Int
          _tmpCantidadSolicitada = _stmt.getLong(_columnIndexOfCantidadSolicitada).toInt()
          _item = CompromisoProducto(_tmpProductoId,_tmpCantidadSolicitada)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun unidadesPendientes(pid: Long, bid: Long): Int {
    val _sql: String =
        "SELECT COALESCE(SUM(d.cantidadEsperada),0) FROM detalle_caja d JOIN cajas c ON c.id=d.cajaId WHERE c.bodegaOrigenId=? AND c.tipo='SALIDA' AND c.estado IN ('ORDEN_ASIGNADA','PENDIENTE_REVISION') AND d.productoId=?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, bid)
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

  public override fun revisionesCaja(id: Long): Flow<List<EventoCaja>> {
    val _sql: String =
        "SELECT * FROM eventos_caja WHERE cajaId=? AND accion='INCIDENCIA_REVISADA' ORDER BY fecha DESC"
    return createFlow(__db, false, arrayOf("eventos_caja")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
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

  public override suspend fun incidenciasActualesBodega(bid: Long): List<IncidenciaCaja> {
    val _sql: String =
        "SELECT i.* FROM incidencias_caja i JOIN cajas c ON i.cajaId=c.id WHERE c.bodegaDestinoId=? AND c.tipo='INGRESO'"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, bid)
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

  public override suspend fun registrarLlegadaBodega(
    id: Long,
    bid: Long,
    uid: Long,
  ): Int {
    val _sql: String =
        "UPDATE cajas SET estado='EN_REVISION' WHERE id=? AND tipo='INGRESO' AND bodegaDestinoId=? AND asignadoA=? AND estado IN ('ESPERADA','EN_TRANSITO')"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        _argIndex = 2
        _stmt.bindLong(_argIndex, bid)
        _argIndex = 3
        _stmt.bindLong(_argIndex, uid)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun borrarPreparacionesCaja(id: Long) {
    val _sql: String = "DELETE FROM preparaciones_caja WHERE cajaId=?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun cancelarOrden(id: Long): Int {
    val _sql: String =
        "UPDATE cajas SET estado='CANCELADA' WHERE id=? AND tipo='SALIDA' AND estado='ORDEN_ASIGNADA'"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun resolverIncidencia(id: Long): Int {
    val _sql: String =
        "UPDATE incidencias_caja SET estado='REVISADA' WHERE id=? AND estado='PENDIENTE'"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
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
