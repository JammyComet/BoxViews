package com.example.boxview.modelo

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
public class InventariosDao_Impl(
  __db: RoomDatabase,
) : InventariosDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfVerificacionEstanteVacio:
      EntityInsertAdapter<VerificacionEstanteVacio>

  private val __insertAdapterOfPeriodoInventario: EntityInsertAdapter<PeriodoInventario>

  private val __insertAdapterOfLineaInventario: EntityInsertAdapter<LineaInventario>

  private val __insertAdapterOfBorradorInventario: EntityInsertAdapter<BorradorInventario>

  private val __insertAdapterOfAjusteInventario: EntityInsertAdapter<AjusteInventario>

  private val __insertAdapterOfInventarioBodega: EntityInsertAdapter<InventarioBodega>

  private val __insertAdapterOfZonaInventario: EntityInsertAdapter<ZonaInventario>

  private val __insertAdapterOfReferenciaInventario: EntityInsertAdapter<ReferenciaInventario>
  init {
    this.__db = __db
    this.__insertAdapterOfVerificacionEstanteVacio = object :
        EntityInsertAdapter<VerificacionEstanteVacio>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `verificaciones_estante_vacio` (`tipo`,`inventarioId`,`posicionId`,`usuarioId`,`fecha`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: VerificacionEstanteVacio) {
        statement.bindText(1, entity.tipo)
        statement.bindLong(2, entity.inventarioId)
        statement.bindLong(3, entity.posicionId)
        statement.bindLong(4, entity.usuarioId)
        statement.bindLong(5, entity.fecha)
      }
    }
    this.__insertAdapterOfPeriodoInventario = object : EntityInsertAdapter<PeriodoInventario>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `periodos_inventario` (`id`,`bodegaId`,`asignadoA`,`nombre`,`estado`,`fechaApertura`,`inventarioBodegaId`,`verificacionFisicaPor`,`fechaVerificacionFisica`) VALUES (?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: PeriodoInventario) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.bodegaId)
        statement.bindLong(3, entity.asignadoA)
        statement.bindText(4, entity.nombre)
        statement.bindText(5, entity.estado)
        statement.bindLong(6, entity.fechaApertura)
        val _tmpInventarioBodegaId: Long? = entity.inventarioBodegaId
        if (_tmpInventarioBodegaId == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpInventarioBodegaId)
        }
        val _tmpVerificacionFisicaPor: Long? = entity.verificacionFisicaPor
        if (_tmpVerificacionFisicaPor == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpVerificacionFisicaPor)
        }
        val _tmpFechaVerificacionFisica: Long? = entity.fechaVerificacionFisica
        if (_tmpFechaVerificacionFisica == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpFechaVerificacionFisica)
        }
      }
    }
    this.__insertAdapterOfLineaInventario = object : EntityInsertAdapter<LineaInventario>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `lineas_inventario` (`id`,`periodoId`,`productoId`,`posicionId`,`cantidadContada`,`stockAlEntregar`) VALUES (nullif(?, 0),?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: LineaInventario) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.periodoId)
        statement.bindLong(3, entity.productoId)
        statement.bindLong(4, entity.posicionId)
        statement.bindLong(5, entity.cantidadContada.toLong())
        val _tmpStockAlEntregar: Int? = entity.stockAlEntregar
        if (_tmpStockAlEntregar == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpStockAlEntregar.toLong())
        }
      }
    }
    this.__insertAdapterOfBorradorInventario = object : EntityInsertAdapter<BorradorInventario>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `borrador_inventario` (`id`,`periodoId`,`productoId`,`posicionId`,`cantidad`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: BorradorInventario) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.periodoId)
        statement.bindLong(3, entity.productoId)
        statement.bindLong(4, entity.posicionId)
        statement.bindLong(5, entity.cantidad.toLong())
      }
    }
    this.__insertAdapterOfAjusteInventario = object : EntityInsertAdapter<AjusteInventario>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `ajustes_inventario` (`id`,`periodoId`,`productoId`,`posicionId`,`stockAnterior`,`stockNuevo`,`revisadoPor`,`fecha`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: AjusteInventario) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.periodoId)
        statement.bindLong(3, entity.productoId)
        statement.bindLong(4, entity.posicionId)
        statement.bindLong(5, entity.stockAnterior.toLong())
        statement.bindLong(6, entity.stockNuevo.toLong())
        statement.bindLong(7, entity.revisadoPor)
        statement.bindLong(8, entity.fecha)
      }
    }
    this.__insertAdapterOfInventarioBodega = object : EntityInsertAdapter<InventarioBodega>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `inventarios_bodega` (`id`,`bodegaId`,`nombre`,`estado`,`fechaApertura`,`revisadoPor`,`campaniaId`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: InventarioBodega) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.bodegaId)
        statement.bindText(3, entity.nombre)
        statement.bindText(4, entity.estado)
        statement.bindLong(5, entity.fechaApertura)
        val _tmpRevisadoPor: Long? = entity.revisadoPor
        if (_tmpRevisadoPor == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpRevisadoPor)
        }
        val _tmpCampaniaId: Long? = entity.campaniaId
        if (_tmpCampaniaId == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpCampaniaId)
        }
      }
    }
    this.__insertAdapterOfZonaInventario = object : EntityInsertAdapter<ZonaInventario>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `zonas_inventario` (`inventarioId`,`posicionId`,`operadorId`,`periodoId`) VALUES (?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ZonaInventario) {
        statement.bindLong(1, entity.inventarioId)
        statement.bindLong(2, entity.posicionId)
        statement.bindLong(3, entity.operadorId)
        statement.bindLong(4, entity.periodoId)
      }
    }
    this.__insertAdapterOfReferenciaInventario = object :
        EntityInsertAdapter<ReferenciaInventario>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `referencias_inventario` (`inventarioId`,`productoId`,`posicionId`,`cantidad`) VALUES (?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ReferenciaInventario) {
        statement.bindLong(1, entity.inventarioId)
        statement.bindLong(2, entity.productoId)
        statement.bindLong(3, entity.posicionId)
        statement.bindLong(4, entity.cantidad.toLong())
      }
    }
  }

  public override suspend fun guardarEstanteVacio(valor: VerificacionEstanteVacio): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfVerificacionEstanteVacio.insert(_connection, valor)
  }

  public override suspend fun guardarPeriodoInventario(valor: PeriodoInventario): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfPeriodoInventario.insert(_connection, valor)
  }

  public override suspend fun guardarLineaInventario(valor: LineaInventario): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfLineaInventario.insertAndReturnId(_connection, valor)
    _result
  }

  public override suspend fun guardarFilaBorrador(valor: BorradorInventario): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfBorradorInventario.insertAndReturnId(_connection, valor)
    _result
  }

  public override suspend fun guardarAjusteInventario(valor: AjusteInventario): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfAjusteInventario.insertAndReturnId(_connection, valor)
    _result
  }

  public override suspend fun guardarInventarioBodega(valor: InventarioBodega): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfInventarioBodega.insert(_connection, valor)
  }

  public override suspend fun guardarZonaInventario(valor: ZonaInventario): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfZonaInventario.insert(_connection, valor)
  }

  public override suspend fun guardarReferenciaInventario(valor: ReferenciaInventario): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfReferenciaInventario.insert(_connection, valor)
  }

  public override fun estantesVacios(tipo: String, id: Long): Flow<List<VerificacionEstanteVacio>> {
    val _sql: String =
        "SELECT * FROM verificaciones_estante_vacio WHERE tipo=? AND inventarioId=? ORDER BY posicionId"
    return createFlow(__db, false, arrayOf("verificaciones_estante_vacio")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, tipo)
        _argIndex = 2
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfTipo: Int = getColumnIndexOrThrow(_stmt, "tipo")
        val _columnIndexOfInventarioId: Int = getColumnIndexOrThrow(_stmt, "inventarioId")
        val _columnIndexOfPosicionId: Int = getColumnIndexOrThrow(_stmt, "posicionId")
        val _columnIndexOfUsuarioId: Int = getColumnIndexOrThrow(_stmt, "usuarioId")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _result: MutableList<VerificacionEstanteVacio> = mutableListOf()
        while (_stmt.step()) {
          val _item: VerificacionEstanteVacio
          val _tmpTipo: String
          _tmpTipo = _stmt.getText(_columnIndexOfTipo)
          val _tmpInventarioId: Long
          _tmpInventarioId = _stmt.getLong(_columnIndexOfInventarioId)
          val _tmpPosicionId: Long
          _tmpPosicionId = _stmt.getLong(_columnIndexOfPosicionId)
          val _tmpUsuarioId: Long
          _tmpUsuarioId = _stmt.getLong(_columnIndexOfUsuarioId)
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          _item =
              VerificacionEstanteVacio(_tmpTipo,_tmpInventarioId,_tmpPosicionId,_tmpUsuarioId,_tmpFecha)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun estantesVaciosActuales(tipo: String, id: Long):
      List<VerificacionEstanteVacio> {
    val _sql: String = "SELECT * FROM verificaciones_estante_vacio WHERE tipo=? AND inventarioId=?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, tipo)
        _argIndex = 2
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfTipo: Int = getColumnIndexOrThrow(_stmt, "tipo")
        val _columnIndexOfInventarioId: Int = getColumnIndexOrThrow(_stmt, "inventarioId")
        val _columnIndexOfPosicionId: Int = getColumnIndexOrThrow(_stmt, "posicionId")
        val _columnIndexOfUsuarioId: Int = getColumnIndexOrThrow(_stmt, "usuarioId")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _result: MutableList<VerificacionEstanteVacio> = mutableListOf()
        while (_stmt.step()) {
          val _item: VerificacionEstanteVacio
          val _tmpTipo: String
          _tmpTipo = _stmt.getText(_columnIndexOfTipo)
          val _tmpInventarioId: Long
          _tmpInventarioId = _stmt.getLong(_columnIndexOfInventarioId)
          val _tmpPosicionId: Long
          _tmpPosicionId = _stmt.getLong(_columnIndexOfPosicionId)
          val _tmpUsuarioId: Long
          _tmpUsuarioId = _stmt.getLong(_columnIndexOfUsuarioId)
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          _item =
              VerificacionEstanteVacio(_tmpTipo,_tmpInventarioId,_tmpPosicionId,_tmpUsuarioId,_tmpFecha)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun borradorActualInventario(id: Long): List<BorradorInventario> {
    val _sql: String = "SELECT * FROM borrador_inventario WHERE periodoId=? ORDER BY id"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPeriodoId: Int = getColumnIndexOrThrow(_stmt, "periodoId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfPosicionId: Int = getColumnIndexOrThrow(_stmt, "posicionId")
        val _columnIndexOfCantidad: Int = getColumnIndexOrThrow(_stmt, "cantidad")
        val _result: MutableList<BorradorInventario> = mutableListOf()
        while (_stmt.step()) {
          val _item: BorradorInventario
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpPeriodoId: Long
          _tmpPeriodoId = _stmt.getLong(_columnIndexOfPeriodoId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpPosicionId: Long
          _tmpPosicionId = _stmt.getLong(_columnIndexOfPosicionId)
          val _tmpCantidad: Int
          _tmpCantidad = _stmt.getLong(_columnIndexOfCantidad).toInt()
          _item =
              BorradorInventario(_tmpId,_tmpPeriodoId,_tmpProductoId,_tmpPosicionId,_tmpCantidad)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun borradorInventario(id: Long): Flow<List<BorradorInventario>> {
    val _sql: String = "SELECT * FROM borrador_inventario WHERE periodoId=? ORDER BY id"
    return createFlow(__db, false, arrayOf("borrador_inventario")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPeriodoId: Int = getColumnIndexOrThrow(_stmt, "periodoId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfPosicionId: Int = getColumnIndexOrThrow(_stmt, "posicionId")
        val _columnIndexOfCantidad: Int = getColumnIndexOrThrow(_stmt, "cantidad")
        val _result: MutableList<BorradorInventario> = mutableListOf()
        while (_stmt.step()) {
          val _item: BorradorInventario
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpPeriodoId: Long
          _tmpPeriodoId = _stmt.getLong(_columnIndexOfPeriodoId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpPosicionId: Long
          _tmpPosicionId = _stmt.getLong(_columnIndexOfPosicionId)
          val _tmpCantidad: Int
          _tmpCantidad = _stmt.getLong(_columnIndexOfCantidad).toInt()
          _item =
              BorradorInventario(_tmpId,_tmpPeriodoId,_tmpProductoId,_tmpPosicionId,_tmpCantidad)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun numeroPeriodos(): Int {
    val _sql: String = "SELECT COUNT(*) FROM periodos_inventario"
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

  public override fun periodoInventario(uid: Long, bid: Long): Flow<PeriodoInventario?> {
    val _sql: String =
        "SELECT * FROM periodos_inventario WHERE bodegaId=? AND asignadoA=? AND inventarioBodegaId IS NOT NULL AND estado IN ('ABIERTO', 'ENTREGADO') ORDER BY CASE WHEN estado='ABIERTO' THEN 0 ELSE 1 END, fechaApertura DESC,id DESC LIMIT 1"
    return createFlow(__db, false, arrayOf("periodos_inventario")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, bid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, uid)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfBodegaId: Int = getColumnIndexOrThrow(_stmt, "bodegaId")
        val _columnIndexOfAsignadoA: Int = getColumnIndexOrThrow(_stmt, "asignadoA")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _columnIndexOfEstado: Int = getColumnIndexOrThrow(_stmt, "estado")
        val _columnIndexOfFechaApertura: Int = getColumnIndexOrThrow(_stmt, "fechaApertura")
        val _columnIndexOfInventarioBodegaId: Int = getColumnIndexOrThrow(_stmt,
            "inventarioBodegaId")
        val _columnIndexOfVerificacionFisicaPor: Int = getColumnIndexOrThrow(_stmt,
            "verificacionFisicaPor")
        val _columnIndexOfFechaVerificacionFisica: Int = getColumnIndexOrThrow(_stmt,
            "fechaVerificacionFisica")
        val _result: PeriodoInventario?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpBodegaId: Long
          _tmpBodegaId = _stmt.getLong(_columnIndexOfBodegaId)
          val _tmpAsignadoA: Long
          _tmpAsignadoA = _stmt.getLong(_columnIndexOfAsignadoA)
          val _tmpNombre: String
          _tmpNombre = _stmt.getText(_columnIndexOfNombre)
          val _tmpEstado: String
          _tmpEstado = _stmt.getText(_columnIndexOfEstado)
          val _tmpFechaApertura: Long
          _tmpFechaApertura = _stmt.getLong(_columnIndexOfFechaApertura)
          val _tmpInventarioBodegaId: Long?
          if (_stmt.isNull(_columnIndexOfInventarioBodegaId)) {
            _tmpInventarioBodegaId = null
          } else {
            _tmpInventarioBodegaId = _stmt.getLong(_columnIndexOfInventarioBodegaId)
          }
          val _tmpVerificacionFisicaPor: Long?
          if (_stmt.isNull(_columnIndexOfVerificacionFisicaPor)) {
            _tmpVerificacionFisicaPor = null
          } else {
            _tmpVerificacionFisicaPor = _stmt.getLong(_columnIndexOfVerificacionFisicaPor)
          }
          val _tmpFechaVerificacionFisica: Long?
          if (_stmt.isNull(_columnIndexOfFechaVerificacionFisica)) {
            _tmpFechaVerificacionFisica = null
          } else {
            _tmpFechaVerificacionFisica = _stmt.getLong(_columnIndexOfFechaVerificacionFisica)
          }
          _result =
              PeriodoInventario(_tmpId,_tmpBodegaId,_tmpAsignadoA,_tmpNombre,_tmpEstado,_tmpFechaApertura,_tmpInventarioBodegaId,_tmpVerificacionFisicaPor,_tmpFechaVerificacionFisica)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun periodoPorId(id: Long): PeriodoInventario? {
    val _sql: String = "SELECT * FROM periodos_inventario WHERE id=? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfBodegaId: Int = getColumnIndexOrThrow(_stmt, "bodegaId")
        val _columnIndexOfAsignadoA: Int = getColumnIndexOrThrow(_stmt, "asignadoA")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _columnIndexOfEstado: Int = getColumnIndexOrThrow(_stmt, "estado")
        val _columnIndexOfFechaApertura: Int = getColumnIndexOrThrow(_stmt, "fechaApertura")
        val _columnIndexOfInventarioBodegaId: Int = getColumnIndexOrThrow(_stmt,
            "inventarioBodegaId")
        val _columnIndexOfVerificacionFisicaPor: Int = getColumnIndexOrThrow(_stmt,
            "verificacionFisicaPor")
        val _columnIndexOfFechaVerificacionFisica: Int = getColumnIndexOrThrow(_stmt,
            "fechaVerificacionFisica")
        val _result: PeriodoInventario?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpBodegaId: Long
          _tmpBodegaId = _stmt.getLong(_columnIndexOfBodegaId)
          val _tmpAsignadoA: Long
          _tmpAsignadoA = _stmt.getLong(_columnIndexOfAsignadoA)
          val _tmpNombre: String
          _tmpNombre = _stmt.getText(_columnIndexOfNombre)
          val _tmpEstado: String
          _tmpEstado = _stmt.getText(_columnIndexOfEstado)
          val _tmpFechaApertura: Long
          _tmpFechaApertura = _stmt.getLong(_columnIndexOfFechaApertura)
          val _tmpInventarioBodegaId: Long?
          if (_stmt.isNull(_columnIndexOfInventarioBodegaId)) {
            _tmpInventarioBodegaId = null
          } else {
            _tmpInventarioBodegaId = _stmt.getLong(_columnIndexOfInventarioBodegaId)
          }
          val _tmpVerificacionFisicaPor: Long?
          if (_stmt.isNull(_columnIndexOfVerificacionFisicaPor)) {
            _tmpVerificacionFisicaPor = null
          } else {
            _tmpVerificacionFisicaPor = _stmt.getLong(_columnIndexOfVerificacionFisicaPor)
          }
          val _tmpFechaVerificacionFisica: Long?
          if (_stmt.isNull(_columnIndexOfFechaVerificacionFisica)) {
            _tmpFechaVerificacionFisica = null
          } else {
            _tmpFechaVerificacionFisica = _stmt.getLong(_columnIndexOfFechaVerificacionFisica)
          }
          _result =
              PeriodoInventario(_tmpId,_tmpBodegaId,_tmpAsignadoA,_tmpNombre,_tmpEstado,_tmpFechaApertura,_tmpInventarioBodegaId,_tmpVerificacionFisicaPor,_tmpFechaVerificacionFisica)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun lineasInventario(id: Long): Flow<List<LineaInventario>> {
    val _sql: String = "SELECT * FROM lineas_inventario WHERE periodoId=? ORDER BY id"
    return createFlow(__db, false, arrayOf("lineas_inventario")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPeriodoId: Int = getColumnIndexOrThrow(_stmt, "periodoId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfPosicionId: Int = getColumnIndexOrThrow(_stmt, "posicionId")
        val _columnIndexOfCantidadContada: Int = getColumnIndexOrThrow(_stmt, "cantidadContada")
        val _columnIndexOfStockAlEntregar: Int = getColumnIndexOrThrow(_stmt, "stockAlEntregar")
        val _result: MutableList<LineaInventario> = mutableListOf()
        while (_stmt.step()) {
          val _item: LineaInventario
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpPeriodoId: Long
          _tmpPeriodoId = _stmt.getLong(_columnIndexOfPeriodoId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpPosicionId: Long
          _tmpPosicionId = _stmt.getLong(_columnIndexOfPosicionId)
          val _tmpCantidadContada: Int
          _tmpCantidadContada = _stmt.getLong(_columnIndexOfCantidadContada).toInt()
          val _tmpStockAlEntregar: Int?
          if (_stmt.isNull(_columnIndexOfStockAlEntregar)) {
            _tmpStockAlEntregar = null
          } else {
            _tmpStockAlEntregar = _stmt.getLong(_columnIndexOfStockAlEntregar).toInt()
          }
          _item =
              LineaInventario(_tmpId,_tmpPeriodoId,_tmpProductoId,_tmpPosicionId,_tmpCantidadContada,_tmpStockAlEntregar)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun lineasInventarioActuales(id: Long): List<LineaInventario> {
    val _sql: String = "SELECT * FROM lineas_inventario WHERE periodoId=? ORDER BY id"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPeriodoId: Int = getColumnIndexOrThrow(_stmt, "periodoId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfPosicionId: Int = getColumnIndexOrThrow(_stmt, "posicionId")
        val _columnIndexOfCantidadContada: Int = getColumnIndexOrThrow(_stmt, "cantidadContada")
        val _columnIndexOfStockAlEntregar: Int = getColumnIndexOrThrow(_stmt, "stockAlEntregar")
        val _result: MutableList<LineaInventario> = mutableListOf()
        while (_stmt.step()) {
          val _item: LineaInventario
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpPeriodoId: Long
          _tmpPeriodoId = _stmt.getLong(_columnIndexOfPeriodoId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpPosicionId: Long
          _tmpPosicionId = _stmt.getLong(_columnIndexOfPosicionId)
          val _tmpCantidadContada: Int
          _tmpCantidadContada = _stmt.getLong(_columnIndexOfCantidadContada).toInt()
          val _tmpStockAlEntregar: Int?
          if (_stmt.isNull(_columnIndexOfStockAlEntregar)) {
            _tmpStockAlEntregar = null
          } else {
            _tmpStockAlEntregar = _stmt.getLong(_columnIndexOfStockAlEntregar).toInt()
          }
          _item =
              LineaInventario(_tmpId,_tmpPeriodoId,_tmpProductoId,_tmpPosicionId,_tmpCantidadContada,_tmpStockAlEntregar)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun ajustesInventario(id: Long): Flow<List<AjusteInventario>> {
    val _sql: String = "SELECT * FROM ajustes_inventario WHERE periodoId=? ORDER BY id"
    return createFlow(__db, false, arrayOf("ajustes_inventario")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPeriodoId: Int = getColumnIndexOrThrow(_stmt, "periodoId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfPosicionId: Int = getColumnIndexOrThrow(_stmt, "posicionId")
        val _columnIndexOfStockAnterior: Int = getColumnIndexOrThrow(_stmt, "stockAnterior")
        val _columnIndexOfStockNuevo: Int = getColumnIndexOrThrow(_stmt, "stockNuevo")
        val _columnIndexOfRevisadoPor: Int = getColumnIndexOrThrow(_stmt, "revisadoPor")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _result: MutableList<AjusteInventario> = mutableListOf()
        while (_stmt.step()) {
          val _item: AjusteInventario
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpPeriodoId: Long
          _tmpPeriodoId = _stmt.getLong(_columnIndexOfPeriodoId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpPosicionId: Long
          _tmpPosicionId = _stmt.getLong(_columnIndexOfPosicionId)
          val _tmpStockAnterior: Int
          _tmpStockAnterior = _stmt.getLong(_columnIndexOfStockAnterior).toInt()
          val _tmpStockNuevo: Int
          _tmpStockNuevo = _stmt.getLong(_columnIndexOfStockNuevo).toInt()
          val _tmpRevisadoPor: Long
          _tmpRevisadoPor = _stmt.getLong(_columnIndexOfRevisadoPor)
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          _item =
              AjusteInventario(_tmpId,_tmpPeriodoId,_tmpProductoId,_tmpPosicionId,_tmpStockAnterior,_tmpStockNuevo,_tmpRevisadoPor,_tmpFecha)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun ajustesInventarioActuales(id: Long): List<AjusteInventario> {
    val _sql: String = "SELECT * FROM ajustes_inventario WHERE periodoId=? ORDER BY id"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPeriodoId: Int = getColumnIndexOrThrow(_stmt, "periodoId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfPosicionId: Int = getColumnIndexOrThrow(_stmt, "posicionId")
        val _columnIndexOfStockAnterior: Int = getColumnIndexOrThrow(_stmt, "stockAnterior")
        val _columnIndexOfStockNuevo: Int = getColumnIndexOrThrow(_stmt, "stockNuevo")
        val _columnIndexOfRevisadoPor: Int = getColumnIndexOrThrow(_stmt, "revisadoPor")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _result: MutableList<AjusteInventario> = mutableListOf()
        while (_stmt.step()) {
          val _item: AjusteInventario
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpPeriodoId: Long
          _tmpPeriodoId = _stmt.getLong(_columnIndexOfPeriodoId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpPosicionId: Long
          _tmpPosicionId = _stmt.getLong(_columnIndexOfPosicionId)
          val _tmpStockAnterior: Int
          _tmpStockAnterior = _stmt.getLong(_columnIndexOfStockAnterior).toInt()
          val _tmpStockNuevo: Int
          _tmpStockNuevo = _stmt.getLong(_columnIndexOfStockNuevo).toInt()
          val _tmpRevisadoPor: Long
          _tmpRevisadoPor = _stmt.getLong(_columnIndexOfRevisadoPor)
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          _item =
              AjusteInventario(_tmpId,_tmpPeriodoId,_tmpProductoId,_tmpPosicionId,_tmpStockAnterior,_tmpStockNuevo,_tmpRevisadoPor,_tmpFecha)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun periodosJefatura(bid: Long): Flow<List<PeriodoInventario>> {
    val _sql: String =
        "SELECT * FROM periodos_inventario WHERE bodegaId=? ORDER BY fechaApertura DESC, id DESC"
    return createFlow(__db, false, arrayOf("periodos_inventario")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, bid)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfBodegaId: Int = getColumnIndexOrThrow(_stmt, "bodegaId")
        val _columnIndexOfAsignadoA: Int = getColumnIndexOrThrow(_stmt, "asignadoA")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _columnIndexOfEstado: Int = getColumnIndexOrThrow(_stmt, "estado")
        val _columnIndexOfFechaApertura: Int = getColumnIndexOrThrow(_stmt, "fechaApertura")
        val _columnIndexOfInventarioBodegaId: Int = getColumnIndexOrThrow(_stmt,
            "inventarioBodegaId")
        val _columnIndexOfVerificacionFisicaPor: Int = getColumnIndexOrThrow(_stmt,
            "verificacionFisicaPor")
        val _columnIndexOfFechaVerificacionFisica: Int = getColumnIndexOrThrow(_stmt,
            "fechaVerificacionFisica")
        val _result: MutableList<PeriodoInventario> = mutableListOf()
        while (_stmt.step()) {
          val _item: PeriodoInventario
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpBodegaId: Long
          _tmpBodegaId = _stmt.getLong(_columnIndexOfBodegaId)
          val _tmpAsignadoA: Long
          _tmpAsignadoA = _stmt.getLong(_columnIndexOfAsignadoA)
          val _tmpNombre: String
          _tmpNombre = _stmt.getText(_columnIndexOfNombre)
          val _tmpEstado: String
          _tmpEstado = _stmt.getText(_columnIndexOfEstado)
          val _tmpFechaApertura: Long
          _tmpFechaApertura = _stmt.getLong(_columnIndexOfFechaApertura)
          val _tmpInventarioBodegaId: Long?
          if (_stmt.isNull(_columnIndexOfInventarioBodegaId)) {
            _tmpInventarioBodegaId = null
          } else {
            _tmpInventarioBodegaId = _stmt.getLong(_columnIndexOfInventarioBodegaId)
          }
          val _tmpVerificacionFisicaPor: Long?
          if (_stmt.isNull(_columnIndexOfVerificacionFisicaPor)) {
            _tmpVerificacionFisicaPor = null
          } else {
            _tmpVerificacionFisicaPor = _stmt.getLong(_columnIndexOfVerificacionFisicaPor)
          }
          val _tmpFechaVerificacionFisica: Long?
          if (_stmt.isNull(_columnIndexOfFechaVerificacionFisica)) {
            _tmpFechaVerificacionFisica = null
          } else {
            _tmpFechaVerificacionFisica = _stmt.getLong(_columnIndexOfFechaVerificacionFisica)
          }
          _item =
              PeriodoInventario(_tmpId,_tmpBodegaId,_tmpAsignadoA,_tmpNombre,_tmpEstado,_tmpFechaApertura,_tmpInventarioBodegaId,_tmpVerificacionFisicaPor,_tmpFechaVerificacionFisica)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun siguienteIdPeriodo(): Long {
    val _sql: String = "SELECT COALESCE(MAX(id),0)+1 FROM periodos_inventario"
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

  public override suspend fun existePeriodoMes(
    bid: Long,
    uid: Long,
    nombre: String,
  ): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM periodos_inventario WHERE bodegaId=? AND asignadoA=? AND nombre=? COLLATE NOCASE"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, bid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, uid)
        _argIndex = 3
        _stmt.bindText(_argIndex, nombre)
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

  public override suspend fun periodoPendiente(bid: Long, uid: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM periodos_inventario WHERE bodegaId=? AND asignadoA=? AND inventarioBodegaId IS NOT NULL AND estado IN ('ABIERTO','ENTREGADO')"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, bid)
        _argIndex = 2
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

  public override suspend fun siguienteIdInventarioBodega(): Long {
    val _sql: String = "SELECT COALESCE(MAX(id),0)+1 FROM inventarios_bodega"
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

  public override suspend fun existeInventarioBodegaMes(bid: Long, nombre: String): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM inventarios_bodega WHERE bodegaId=? AND nombre=? COLLATE NOCASE"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, bid)
        _argIndex = 2
        _stmt.bindText(_argIndex, nombre)
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

  public override suspend fun inventarioBodegaPendiente(bid: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM inventarios_bodega WHERE bodegaId=? AND estado IN ('ABIERTO','PENDIENTE_REVISION')"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
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

  public override fun inventariosBodega(bid: Long): Flow<List<InventarioBodega>> {
    val _sql: String =
        "SELECT * FROM inventarios_bodega WHERE bodegaId=? ORDER BY fechaApertura DESC,id DESC"
    return createFlow(__db, false, arrayOf("inventarios_bodega")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, bid)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfBodegaId: Int = getColumnIndexOrThrow(_stmt, "bodegaId")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _columnIndexOfEstado: Int = getColumnIndexOrThrow(_stmt, "estado")
        val _columnIndexOfFechaApertura: Int = getColumnIndexOrThrow(_stmt, "fechaApertura")
        val _columnIndexOfRevisadoPor: Int = getColumnIndexOrThrow(_stmt, "revisadoPor")
        val _columnIndexOfCampaniaId: Int = getColumnIndexOrThrow(_stmt, "campaniaId")
        val _result: MutableList<InventarioBodega> = mutableListOf()
        while (_stmt.step()) {
          val _item: InventarioBodega
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpBodegaId: Long
          _tmpBodegaId = _stmt.getLong(_columnIndexOfBodegaId)
          val _tmpNombre: String
          _tmpNombre = _stmt.getText(_columnIndexOfNombre)
          val _tmpEstado: String
          _tmpEstado = _stmt.getText(_columnIndexOfEstado)
          val _tmpFechaApertura: Long
          _tmpFechaApertura = _stmt.getLong(_columnIndexOfFechaApertura)
          val _tmpRevisadoPor: Long?
          if (_stmt.isNull(_columnIndexOfRevisadoPor)) {
            _tmpRevisadoPor = null
          } else {
            _tmpRevisadoPor = _stmt.getLong(_columnIndexOfRevisadoPor)
          }
          val _tmpCampaniaId: Long?
          if (_stmt.isNull(_columnIndexOfCampaniaId)) {
            _tmpCampaniaId = null
          } else {
            _tmpCampaniaId = _stmt.getLong(_columnIndexOfCampaniaId)
          }
          _item =
              InventarioBodega(_tmpId,_tmpBodegaId,_tmpNombre,_tmpEstado,_tmpFechaApertura,_tmpRevisadoPor,_tmpCampaniaId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun inventarioBodegaPorId(id: Long): InventarioBodega? {
    val _sql: String = "SELECT * FROM inventarios_bodega WHERE id=? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfBodegaId: Int = getColumnIndexOrThrow(_stmt, "bodegaId")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _columnIndexOfEstado: Int = getColumnIndexOrThrow(_stmt, "estado")
        val _columnIndexOfFechaApertura: Int = getColumnIndexOrThrow(_stmt, "fechaApertura")
        val _columnIndexOfRevisadoPor: Int = getColumnIndexOrThrow(_stmt, "revisadoPor")
        val _columnIndexOfCampaniaId: Int = getColumnIndexOrThrow(_stmt, "campaniaId")
        val _result: InventarioBodega?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpBodegaId: Long
          _tmpBodegaId = _stmt.getLong(_columnIndexOfBodegaId)
          val _tmpNombre: String
          _tmpNombre = _stmt.getText(_columnIndexOfNombre)
          val _tmpEstado: String
          _tmpEstado = _stmt.getText(_columnIndexOfEstado)
          val _tmpFechaApertura: Long
          _tmpFechaApertura = _stmt.getLong(_columnIndexOfFechaApertura)
          val _tmpRevisadoPor: Long?
          if (_stmt.isNull(_columnIndexOfRevisadoPor)) {
            _tmpRevisadoPor = null
          } else {
            _tmpRevisadoPor = _stmt.getLong(_columnIndexOfRevisadoPor)
          }
          val _tmpCampaniaId: Long?
          if (_stmt.isNull(_columnIndexOfCampaniaId)) {
            _tmpCampaniaId = null
          } else {
            _tmpCampaniaId = _stmt.getLong(_columnIndexOfCampaniaId)
          }
          _result =
              InventarioBodega(_tmpId,_tmpBodegaId,_tmpNombre,_tmpEstado,_tmpFechaApertura,_tmpRevisadoPor,_tmpCampaniaId)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun zonasInventario(id: Long): Flow<List<ZonaInventario>> {
    val _sql: String = "SELECT * FROM zonas_inventario WHERE inventarioId=? ORDER BY posicionId"
    return createFlow(__db, false, arrayOf("zonas_inventario")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfInventarioId: Int = getColumnIndexOrThrow(_stmt, "inventarioId")
        val _columnIndexOfPosicionId: Int = getColumnIndexOrThrow(_stmt, "posicionId")
        val _columnIndexOfOperadorId: Int = getColumnIndexOrThrow(_stmt, "operadorId")
        val _columnIndexOfPeriodoId: Int = getColumnIndexOrThrow(_stmt, "periodoId")
        val _result: MutableList<ZonaInventario> = mutableListOf()
        while (_stmt.step()) {
          val _item: ZonaInventario
          val _tmpInventarioId: Long
          _tmpInventarioId = _stmt.getLong(_columnIndexOfInventarioId)
          val _tmpPosicionId: Long
          _tmpPosicionId = _stmt.getLong(_columnIndexOfPosicionId)
          val _tmpOperadorId: Long
          _tmpOperadorId = _stmt.getLong(_columnIndexOfOperadorId)
          val _tmpPeriodoId: Long
          _tmpPeriodoId = _stmt.getLong(_columnIndexOfPeriodoId)
          _item = ZonaInventario(_tmpInventarioId,_tmpPosicionId,_tmpOperadorId,_tmpPeriodoId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun zonasInventarioActuales(id: Long): List<ZonaInventario> {
    val _sql: String = "SELECT * FROM zonas_inventario WHERE inventarioId=? ORDER BY posicionId"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfInventarioId: Int = getColumnIndexOrThrow(_stmt, "inventarioId")
        val _columnIndexOfPosicionId: Int = getColumnIndexOrThrow(_stmt, "posicionId")
        val _columnIndexOfOperadorId: Int = getColumnIndexOrThrow(_stmt, "operadorId")
        val _columnIndexOfPeriodoId: Int = getColumnIndexOrThrow(_stmt, "periodoId")
        val _result: MutableList<ZonaInventario> = mutableListOf()
        while (_stmt.step()) {
          val _item: ZonaInventario
          val _tmpInventarioId: Long
          _tmpInventarioId = _stmt.getLong(_columnIndexOfInventarioId)
          val _tmpPosicionId: Long
          _tmpPosicionId = _stmt.getLong(_columnIndexOfPosicionId)
          val _tmpOperadorId: Long
          _tmpOperadorId = _stmt.getLong(_columnIndexOfOperadorId)
          val _tmpPeriodoId: Long
          _tmpPeriodoId = _stmt.getLong(_columnIndexOfPeriodoId)
          _item = ZonaInventario(_tmpInventarioId,_tmpPosicionId,_tmpOperadorId,_tmpPeriodoId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun zonasPeriodo(periodoId: Long): Flow<List<Long>> {
    val _sql: String =
        "SELECT posicionId FROM zonas_inventario WHERE periodoId=? ORDER BY posicionId"
    return createFlow(__db, false, arrayOf("zonas_inventario")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, periodoId)
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

  public override suspend fun zonasPeriodoActuales(periodoId: Long): List<Long> {
    val _sql: String =
        "SELECT posicionId FROM zonas_inventario WHERE periodoId=? ORDER BY posicionId"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, periodoId)
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

  public override suspend fun referenciasInventario(id: Long): List<ReferenciaInventario> {
    val _sql: String =
        "SELECT * FROM referencias_inventario WHERE inventarioId=? ORDER BY posicionId,productoId"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfInventarioId: Int = getColumnIndexOrThrow(_stmt, "inventarioId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfPosicionId: Int = getColumnIndexOrThrow(_stmt, "posicionId")
        val _columnIndexOfCantidad: Int = getColumnIndexOrThrow(_stmt, "cantidad")
        val _result: MutableList<ReferenciaInventario> = mutableListOf()
        while (_stmt.step()) {
          val _item: ReferenciaInventario
          val _tmpInventarioId: Long
          _tmpInventarioId = _stmt.getLong(_columnIndexOfInventarioId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpPosicionId: Long
          _tmpPosicionId = _stmt.getLong(_columnIndexOfPosicionId)
          val _tmpCantidad: Int
          _tmpCantidad = _stmt.getLong(_columnIndexOfCantidad).toInt()
          _item = ReferenciaInventario(_tmpInventarioId,_tmpProductoId,_tmpPosicionId,_tmpCantidad)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observarReferenciasInventario(id: Long): Flow<List<ReferenciaInventario>> {
    val _sql: String =
        "SELECT * FROM referencias_inventario WHERE inventarioId=? ORDER BY posicionId,productoId"
    return createFlow(__db, false, arrayOf("referencias_inventario")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfInventarioId: Int = getColumnIndexOrThrow(_stmt, "inventarioId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfPosicionId: Int = getColumnIndexOrThrow(_stmt, "posicionId")
        val _columnIndexOfCantidad: Int = getColumnIndexOrThrow(_stmt, "cantidad")
        val _result: MutableList<ReferenciaInventario> = mutableListOf()
        while (_stmt.step()) {
          val _item: ReferenciaInventario
          val _tmpInventarioId: Long
          _tmpInventarioId = _stmt.getLong(_columnIndexOfInventarioId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpPosicionId: Long
          _tmpPosicionId = _stmt.getLong(_columnIndexOfPosicionId)
          val _tmpCantidad: Int
          _tmpCantidad = _stmt.getLong(_columnIndexOfCantidad).toInt()
          _item = ReferenciaInventario(_tmpInventarioId,_tmpProductoId,_tmpPosicionId,_tmpCantidad)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun posicionCongeladaPorCampania(pos: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM campanias_mensuales c JOIN posiciones p ON p.bodegaId=c.ubicacionId WHERE p.id=? AND c.tipo='BODEGA' AND c.estado='ABIERTA' AND p.tipo!='RECEPCION'"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, pos)
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

  public override suspend fun posicionBloqueada(pos: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM zonas_inventario z JOIN inventarios_bodega i ON i.id=z.inventarioId WHERE z.posicionId=? AND i.estado IN ('ABIERTO','PENDIENTE_REVISION')"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, pos)
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

  public override suspend fun zonasSinAsignar(id: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM zonas_inventario WHERE inventarioId=? AND (operadorId=0 OR periodoId=0)"
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

  public override suspend fun tareasDeOperador(id: Long, usuario: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM periodos_inventario WHERE inventarioBodegaId=? AND asignadoA=?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        _argIndex = 2
        _stmt.bindLong(_argIndex, usuario)
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

  public override suspend fun tareaDeOperador(id: Long, usuario: Long): Long? {
    val _sql: String =
        "SELECT id FROM periodos_inventario WHERE inventarioBodegaId=? AND asignadoA=? AND estado='ABIERTO' ORDER BY id DESC LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        _argIndex = 2
        _stmt.bindLong(_argIndex, usuario)
        val _result: Long?
        if (_stmt.step()) {
          if (_stmt.isNull(0)) {
            _result = null
          } else {
            _result = _stmt.getLong(0)
          }
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun tareasSinEntregar(id: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM periodos_inventario WHERE inventarioBodegaId=? AND estado!='ENTREGADO'"
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

  public override suspend fun tareasEntregadas(id: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM periodos_inventario WHERE inventarioBodegaId=? AND estado='ENTREGADO'"
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

  public override fun tareasInventarioBodega(id: Long): Flow<List<PeriodoInventario>> {
    val _sql: String = "SELECT * FROM periodos_inventario WHERE inventarioBodegaId=? ORDER BY id"
    return createFlow(__db, false, arrayOf("periodos_inventario")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfBodegaId: Int = getColumnIndexOrThrow(_stmt, "bodegaId")
        val _columnIndexOfAsignadoA: Int = getColumnIndexOrThrow(_stmt, "asignadoA")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _columnIndexOfEstado: Int = getColumnIndexOrThrow(_stmt, "estado")
        val _columnIndexOfFechaApertura: Int = getColumnIndexOrThrow(_stmt, "fechaApertura")
        val _columnIndexOfInventarioBodegaId: Int = getColumnIndexOrThrow(_stmt,
            "inventarioBodegaId")
        val _columnIndexOfVerificacionFisicaPor: Int = getColumnIndexOrThrow(_stmt,
            "verificacionFisicaPor")
        val _columnIndexOfFechaVerificacionFisica: Int = getColumnIndexOrThrow(_stmt,
            "fechaVerificacionFisica")
        val _result: MutableList<PeriodoInventario> = mutableListOf()
        while (_stmt.step()) {
          val _item: PeriodoInventario
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpBodegaId: Long
          _tmpBodegaId = _stmt.getLong(_columnIndexOfBodegaId)
          val _tmpAsignadoA: Long
          _tmpAsignadoA = _stmt.getLong(_columnIndexOfAsignadoA)
          val _tmpNombre: String
          _tmpNombre = _stmt.getText(_columnIndexOfNombre)
          val _tmpEstado: String
          _tmpEstado = _stmt.getText(_columnIndexOfEstado)
          val _tmpFechaApertura: Long
          _tmpFechaApertura = _stmt.getLong(_columnIndexOfFechaApertura)
          val _tmpInventarioBodegaId: Long?
          if (_stmt.isNull(_columnIndexOfInventarioBodegaId)) {
            _tmpInventarioBodegaId = null
          } else {
            _tmpInventarioBodegaId = _stmt.getLong(_columnIndexOfInventarioBodegaId)
          }
          val _tmpVerificacionFisicaPor: Long?
          if (_stmt.isNull(_columnIndexOfVerificacionFisicaPor)) {
            _tmpVerificacionFisicaPor = null
          } else {
            _tmpVerificacionFisicaPor = _stmt.getLong(_columnIndexOfVerificacionFisicaPor)
          }
          val _tmpFechaVerificacionFisica: Long?
          if (_stmt.isNull(_columnIndexOfFechaVerificacionFisica)) {
            _tmpFechaVerificacionFisica = null
          } else {
            _tmpFechaVerificacionFisica = _stmt.getLong(_columnIndexOfFechaVerificacionFisica)
          }
          _item =
              PeriodoInventario(_tmpId,_tmpBodegaId,_tmpAsignadoA,_tmpNombre,_tmpEstado,_tmpFechaApertura,_tmpInventarioBodegaId,_tmpVerificacionFisicaPor,_tmpFechaVerificacionFisica)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun tareasInventarioActuales(id: Long): List<PeriodoInventario> {
    val _sql: String = "SELECT * FROM periodos_inventario WHERE inventarioBodegaId=? ORDER BY id"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfBodegaId: Int = getColumnIndexOrThrow(_stmt, "bodegaId")
        val _columnIndexOfAsignadoA: Int = getColumnIndexOrThrow(_stmt, "asignadoA")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _columnIndexOfEstado: Int = getColumnIndexOrThrow(_stmt, "estado")
        val _columnIndexOfFechaApertura: Int = getColumnIndexOrThrow(_stmt, "fechaApertura")
        val _columnIndexOfInventarioBodegaId: Int = getColumnIndexOrThrow(_stmt,
            "inventarioBodegaId")
        val _columnIndexOfVerificacionFisicaPor: Int = getColumnIndexOrThrow(_stmt,
            "verificacionFisicaPor")
        val _columnIndexOfFechaVerificacionFisica: Int = getColumnIndexOrThrow(_stmt,
            "fechaVerificacionFisica")
        val _result: MutableList<PeriodoInventario> = mutableListOf()
        while (_stmt.step()) {
          val _item: PeriodoInventario
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpBodegaId: Long
          _tmpBodegaId = _stmt.getLong(_columnIndexOfBodegaId)
          val _tmpAsignadoA: Long
          _tmpAsignadoA = _stmt.getLong(_columnIndexOfAsignadoA)
          val _tmpNombre: String
          _tmpNombre = _stmt.getText(_columnIndexOfNombre)
          val _tmpEstado: String
          _tmpEstado = _stmt.getText(_columnIndexOfEstado)
          val _tmpFechaApertura: Long
          _tmpFechaApertura = _stmt.getLong(_columnIndexOfFechaApertura)
          val _tmpInventarioBodegaId: Long?
          if (_stmt.isNull(_columnIndexOfInventarioBodegaId)) {
            _tmpInventarioBodegaId = null
          } else {
            _tmpInventarioBodegaId = _stmt.getLong(_columnIndexOfInventarioBodegaId)
          }
          val _tmpVerificacionFisicaPor: Long?
          if (_stmt.isNull(_columnIndexOfVerificacionFisicaPor)) {
            _tmpVerificacionFisicaPor = null
          } else {
            _tmpVerificacionFisicaPor = _stmt.getLong(_columnIndexOfVerificacionFisicaPor)
          }
          val _tmpFechaVerificacionFisica: Long?
          if (_stmt.isNull(_columnIndexOfFechaVerificacionFisica)) {
            _tmpFechaVerificacionFisica = null
          } else {
            _tmpFechaVerificacionFisica = _stmt.getLong(_columnIndexOfFechaVerificacionFisica)
          }
          _item =
              PeriodoInventario(_tmpId,_tmpBodegaId,_tmpAsignadoA,_tmpNombre,_tmpEstado,_tmpFechaApertura,_tmpInventarioBodegaId,_tmpVerificacionFisicaPor,_tmpFechaVerificacionFisica)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun quitarEstanteVacio(
    tipo: String,
    id: Long,
    pos: Long,
  ): Int {
    val _sql: String =
        "DELETE FROM verificaciones_estante_vacio WHERE tipo=? AND inventarioId=? AND posicionId=?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, tipo)
        _argIndex = 2
        _stmt.bindLong(_argIndex, id)
        _argIndex = 3
        _stmt.bindLong(_argIndex, pos)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun borrarEstantesVacios(tipo: String, id: Long): Int {
    val _sql: String = "DELETE FROM verificaciones_estante_vacio WHERE tipo=? AND inventarioId=?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, tipo)
        _argIndex = 2
        _stmt.bindLong(_argIndex, id)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun borrarVaciosInventarioBodegaGeneral(id: Long): Int {
    val _sql: String = """
        |DELETE FROM verificaciones_estante_vacio WHERE tipo='BODEGA' AND inventarioId IN
        |        (SELECT id FROM periodos_inventario WHERE inventarioBodegaId=?)
        """.trimMargin()
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

  public override suspend fun borrarBorradorEstante(id: Long, pos: Long): Int {
    val _sql: String = "DELETE FROM borrador_inventario WHERE periodoId=? AND posicionId=?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        _argIndex = 2
        _stmt.bindLong(_argIndex, pos)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun borrarBorradorInventario(id: Long) {
    val _sql: String = "DELETE FROM borrador_inventario WHERE periodoId=?"
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

  public override suspend fun ajustarExistenciaSiCoincide(
    id: Long,
    anterior: Int,
    nueva: Int,
  ): Int {
    val _sql: String = "UPDATE existencias SET cantidad=? WHERE id=? AND cantidad=?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, nueva.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, id)
        _argIndex = 3
        _stmt.bindLong(_argIndex, anterior.toLong())
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun guardarVerificacionFisica(
    id: Long,
    uid: Long,
    bid: Long,
    fecha: Long,
  ): Int {
    val _sql: String = """
        |UPDATE periodos_inventario SET verificacionFisicaPor=?, fechaVerificacionFisica=?
        |        WHERE id=? AND estado='ABIERTO' AND asignadoA=? AND bodegaId=?
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, uid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, fecha)
        _argIndex = 3
        _stmt.bindLong(_argIndex, id)
        _argIndex = 4
        _stmt.bindLong(_argIndex, uid)
        _argIndex = 5
        _stmt.bindLong(_argIndex, bid)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun entregarPeriodo(
    id: Long,
    uid: Long,
    bid: Long,
  ): Int {
    val _sql: String =
        "UPDATE periodos_inventario SET estado='ENTREGADO' WHERE id=? AND estado='ABIERTO' AND asignadoA=? AND bodegaId=?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        _argIndex = 2
        _stmt.bindLong(_argIndex, uid)
        _argIndex = 3
        _stmt.bindLong(_argIndex, bid)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun conciliarPeriodo(id: Long): Int {
    val _sql: String =
        "UPDATE periodos_inventario SET estado='CONCILIADO' WHERE id=? AND estado='ENTREGADO'"
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

  public override suspend fun cerrarPeriodo(id: Long): Int {
    val _sql: String =
        "UPDATE periodos_inventario SET estado='CERRADO' WHERE id=? AND estado='ENTREGADO'"
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

  public override suspend fun asignarZonaPendiente(
    inventario: Long,
    pos: Long,
    operador: Long,
    periodo: Long,
  ): Int {
    val _sql: String =
        "UPDATE zonas_inventario SET operadorId=?,periodoId=? WHERE inventarioId=? AND posicionId=? AND operadorId=0 AND periodoId=0"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, operador)
        _argIndex = 2
        _stmt.bindLong(_argIndex, periodo)
        _argIndex = 3
        _stmt.bindLong(_argIndex, inventario)
        _argIndex = 4
        _stmt.bindLong(_argIndex, pos)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun borrarAjustesInventarioGeneral(id: Long): Int {
    val _sql: String =
        "DELETE FROM ajustes_inventario WHERE periodoId IN (SELECT id FROM periodos_inventario WHERE inventarioBodegaId=?)"
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

  public override suspend fun borrarLineasInventarioGeneral(id: Long): Int {
    val _sql: String =
        "DELETE FROM lineas_inventario WHERE periodoId IN (SELECT id FROM periodos_inventario WHERE inventarioBodegaId=?)"
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

  public override suspend fun borrarBorradoresInventarioGeneral(id: Long): Int {
    val _sql: String =
        "DELETE FROM borrador_inventario WHERE periodoId IN (SELECT id FROM periodos_inventario WHERE inventarioBodegaId=?)"
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

  public override suspend fun borrarTareasInventarioGeneral(id: Long): Int {
    val _sql: String = "DELETE FROM periodos_inventario WHERE inventarioBodegaId=?"
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

  public override suspend fun borrarReferenciasInventarioGeneral(id: Long): Int {
    val _sql: String = "DELETE FROM referencias_inventario WHERE inventarioId=?"
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

  public override suspend fun borrarZonasInventarioGeneral(id: Long): Int {
    val _sql: String = "DELETE FROM zonas_inventario WHERE inventarioId=?"
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

  public override suspend fun borrarInventarioGeneral(id: Long): Int {
    val _sql: String =
        "DELETE FROM inventarios_bodega WHERE id=? AND estado IN ('ABIERTO','PENDIENTE_REVISION')"
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

  public override suspend fun marcarInventarioParaRevision(id: Long): Int {
    val _sql: String = """
        |UPDATE inventarios_bodega SET estado='PENDIENTE_REVISION' WHERE id=? AND estado='ABIERTO'
        |       AND NOT EXISTS (SELECT 1 FROM zonas_inventario WHERE inventarioId=? AND (operadorId=0 OR periodoId=0))
        |       AND EXISTS (SELECT 1 FROM periodos_inventario WHERE inventarioBodegaId=?)
        |       AND NOT EXISTS (SELECT 1 FROM periodos_inventario WHERE inventarioBodegaId=? AND estado!='ENTREGADO')
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        _argIndex = 2
        _stmt.bindLong(_argIndex, id)
        _argIndex = 3
        _stmt.bindLong(_argIndex, id)
        _argIndex = 4
        _stmt.bindLong(_argIndex, id)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun finalizarInventarioBodega(
    id: Long,
    estado: String,
    jefeId: Long,
  ): Int {
    val _sql: String =
        "UPDATE inventarios_bodega SET estado=?, revisadoPor=? WHERE id=? AND estado='PENDIENTE_REVISION'"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, estado)
        _argIndex = 2
        _stmt.bindLong(_argIndex, jefeId)
        _argIndex = 3
        _stmt.bindLong(_argIndex, id)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun finalizarTareasInventario(id: Long, estado: String): Int {
    val _sql: String =
        "UPDATE periodos_inventario SET estado=? WHERE inventarioBodegaId=? AND estado='ENTREGADO'"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, estado)
        _argIndex = 2
        _stmt.bindLong(_argIndex, id)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun reabrirInventarioBodega(id: Long): Int {
    val _sql: String =
        "UPDATE inventarios_bodega SET estado='ABIERTO' WHERE id=? AND estado='PENDIENTE_REVISION'"
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

  public override suspend fun reabrirTareaInventario(id: Long): Int {
    val _sql: String =
        "UPDATE periodos_inventario SET estado='ABIERTO' WHERE id=? AND estado='ENTREGADO'"
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

  public override suspend fun borrarLineasInventario(id: Long): Int {
    val _sql: String = "DELETE FROM lineas_inventario WHERE periodoId=?"
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
