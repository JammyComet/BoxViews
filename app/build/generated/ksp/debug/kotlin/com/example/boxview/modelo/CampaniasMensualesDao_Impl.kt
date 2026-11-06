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
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class CampaniasMensualesDao_Impl(
  __db: RoomDatabase,
) : CampaniasMensualesDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfCampaniaMensual: EntityInsertAdapter<CampaniaMensual>

  private val __insertAdapterOfZonaCampania: EntityInsertAdapter<ZonaCampania>

  private val __insertAdapterOfDetalleCampania: EntityInsertAdapter<DetalleCampania>
  init {
    this.__db = __db
    this.__insertAdapterOfCampaniaMensual = object : EntityInsertAdapter<CampaniaMensual>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `campanias_mensuales` (`id`,`tipo`,`ubicacionId`,`sucursalId`,`mes`,`estado`,`fechaInicio`,`fechaFinal`,`revision`,`revisadaPor`,`fechaRevision`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: CampaniaMensual) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.tipo)
        statement.bindLong(3, entity.ubicacionId)
        statement.bindLong(4, entity.sucursalId)
        statement.bindText(5, entity.mes)
        statement.bindText(6, entity.estado)
        statement.bindLong(7, entity.fechaInicio)
        val _tmpFechaFinal: Long? = entity.fechaFinal
        if (_tmpFechaFinal == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpFechaFinal)
        }
        statement.bindText(9, entity.revision)
        val _tmpRevisadaPor: Long? = entity.revisadaPor
        if (_tmpRevisadaPor == null) {
          statement.bindNull(10)
        } else {
          statement.bindLong(10, _tmpRevisadaPor)
        }
        val _tmpFechaRevision: Long? = entity.fechaRevision
        if (_tmpFechaRevision == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmpFechaRevision)
        }
      }
    }
    this.__insertAdapterOfZonaCampania = object : EntityInsertAdapter<ZonaCampania>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `zonas_campania` (`campaniaId`,`posicionId`,`ruta`,`estado`,`inventarioId`,`fechaConteo`) VALUES (?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ZonaCampania) {
        statement.bindLong(1, entity.campaniaId)
        statement.bindLong(2, entity.posicionId)
        statement.bindText(3, entity.ruta)
        statement.bindText(4, entity.estado)
        val _tmpInventarioId: Long? = entity.inventarioId
        if (_tmpInventarioId == null) {
          statement.bindNull(5)
        } else {
          statement.bindLong(5, _tmpInventarioId)
        }
        val _tmpFechaConteo: Long? = entity.fechaConteo
        if (_tmpFechaConteo == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpFechaConteo)
        }
      }
    }
    this.__insertAdapterOfDetalleCampania = object : EntityInsertAdapter<DetalleCampania>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `detalles_campania` (`id`,`campaniaId`,`inventarioId`,`posicionId`,`codigo`,`producto`,`ruta`,`cantidadSistema`,`cantidadContada`,`ajusteAplicado`,`fechaCierre`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: DetalleCampania) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.campaniaId)
        statement.bindLong(3, entity.inventarioId)
        statement.bindLong(4, entity.posicionId)
        statement.bindText(5, entity.codigo)
        statement.bindText(6, entity.producto)
        statement.bindText(7, entity.ruta)
        statement.bindLong(8, entity.cantidadSistema.toLong())
        val _tmpCantidadContada: Int? = entity.cantidadContada
        if (_tmpCantidadContada == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpCantidadContada.toLong())
        }
        val _tmp: Int = if (entity.ajusteAplicado) 1 else 0
        statement.bindLong(10, _tmp.toLong())
        statement.bindLong(11, entity.fechaCierre)
      }
    }
  }

  public override suspend fun crearCampania(valor: CampaniaMensual): Long = performSuspending(__db,
      false, true) { _connection ->
    val _result: Long = __insertAdapterOfCampaniaMensual.insertAndReturnId(_connection, valor)
    _result
  }

  public override suspend fun crearZona(valor: ZonaCampania): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfZonaCampania.insert(_connection, valor)
  }

  public override suspend fun guardarDetalle(valor: DetalleCampania): Long = performSuspending(__db,
      false, true) { _connection ->
    val _result: Long = __insertAdapterOfDetalleCampania.insertAndReturnId(_connection, valor)
    _result
  }

  public override suspend fun buscar(
    tipo: String,
    ubicacion: Long,
    mes: String,
  ): CampaniaMensual? {
    val _sql: String =
        "SELECT * FROM campanias_mensuales WHERE tipo=? AND ubicacionId=? AND mes=? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, tipo)
        _argIndex = 2
        _stmt.bindLong(_argIndex, ubicacion)
        _argIndex = 3
        _stmt.bindText(_argIndex, mes)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTipo: Int = getColumnIndexOrThrow(_stmt, "tipo")
        val _columnIndexOfUbicacionId: Int = getColumnIndexOrThrow(_stmt, "ubicacionId")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfMes: Int = getColumnIndexOrThrow(_stmt, "mes")
        val _columnIndexOfEstado: Int = getColumnIndexOrThrow(_stmt, "estado")
        val _columnIndexOfFechaInicio: Int = getColumnIndexOrThrow(_stmt, "fechaInicio")
        val _columnIndexOfFechaFinal: Int = getColumnIndexOrThrow(_stmt, "fechaFinal")
        val _columnIndexOfRevision: Int = getColumnIndexOrThrow(_stmt, "revision")
        val _columnIndexOfRevisadaPor: Int = getColumnIndexOrThrow(_stmt, "revisadaPor")
        val _columnIndexOfFechaRevision: Int = getColumnIndexOrThrow(_stmt, "fechaRevision")
        val _result: CampaniaMensual?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTipo: String
          _tmpTipo = _stmt.getText(_columnIndexOfTipo)
          val _tmpUbicacionId: Long
          _tmpUbicacionId = _stmt.getLong(_columnIndexOfUbicacionId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpMes: String
          _tmpMes = _stmt.getText(_columnIndexOfMes)
          val _tmpEstado: String
          _tmpEstado = _stmt.getText(_columnIndexOfEstado)
          val _tmpFechaInicio: Long
          _tmpFechaInicio = _stmt.getLong(_columnIndexOfFechaInicio)
          val _tmpFechaFinal: Long?
          if (_stmt.isNull(_columnIndexOfFechaFinal)) {
            _tmpFechaFinal = null
          } else {
            _tmpFechaFinal = _stmt.getLong(_columnIndexOfFechaFinal)
          }
          val _tmpRevision: String
          _tmpRevision = _stmt.getText(_columnIndexOfRevision)
          val _tmpRevisadaPor: Long?
          if (_stmt.isNull(_columnIndexOfRevisadaPor)) {
            _tmpRevisadaPor = null
          } else {
            _tmpRevisadaPor = _stmt.getLong(_columnIndexOfRevisadaPor)
          }
          val _tmpFechaRevision: Long?
          if (_stmt.isNull(_columnIndexOfFechaRevision)) {
            _tmpFechaRevision = null
          } else {
            _tmpFechaRevision = _stmt.getLong(_columnIndexOfFechaRevision)
          }
          _result =
              CampaniaMensual(_tmpId,_tmpTipo,_tmpUbicacionId,_tmpSucursalId,_tmpMes,_tmpEstado,_tmpFechaInicio,_tmpFechaFinal,_tmpRevision,_tmpRevisadaPor,_tmpFechaRevision)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun campañaActiva(tipo: String, ubicacion: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM campanias_mensuales WHERE tipo=? AND ubicacionId=? AND estado='ABIERTA'"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, tipo)
        _argIndex = 2
        _stmt.bindLong(_argIndex, ubicacion)
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

  public override suspend fun buscarId(id: Long): CampaniaMensual? {
    val _sql: String = "SELECT * FROM campanias_mensuales WHERE id=? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTipo: Int = getColumnIndexOrThrow(_stmt, "tipo")
        val _columnIndexOfUbicacionId: Int = getColumnIndexOrThrow(_stmt, "ubicacionId")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfMes: Int = getColumnIndexOrThrow(_stmt, "mes")
        val _columnIndexOfEstado: Int = getColumnIndexOrThrow(_stmt, "estado")
        val _columnIndexOfFechaInicio: Int = getColumnIndexOrThrow(_stmt, "fechaInicio")
        val _columnIndexOfFechaFinal: Int = getColumnIndexOrThrow(_stmt, "fechaFinal")
        val _columnIndexOfRevision: Int = getColumnIndexOrThrow(_stmt, "revision")
        val _columnIndexOfRevisadaPor: Int = getColumnIndexOrThrow(_stmt, "revisadaPor")
        val _columnIndexOfFechaRevision: Int = getColumnIndexOrThrow(_stmt, "fechaRevision")
        val _result: CampaniaMensual?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTipo: String
          _tmpTipo = _stmt.getText(_columnIndexOfTipo)
          val _tmpUbicacionId: Long
          _tmpUbicacionId = _stmt.getLong(_columnIndexOfUbicacionId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpMes: String
          _tmpMes = _stmt.getText(_columnIndexOfMes)
          val _tmpEstado: String
          _tmpEstado = _stmt.getText(_columnIndexOfEstado)
          val _tmpFechaInicio: Long
          _tmpFechaInicio = _stmt.getLong(_columnIndexOfFechaInicio)
          val _tmpFechaFinal: Long?
          if (_stmt.isNull(_columnIndexOfFechaFinal)) {
            _tmpFechaFinal = null
          } else {
            _tmpFechaFinal = _stmt.getLong(_columnIndexOfFechaFinal)
          }
          val _tmpRevision: String
          _tmpRevision = _stmt.getText(_columnIndexOfRevision)
          val _tmpRevisadaPor: Long?
          if (_stmt.isNull(_columnIndexOfRevisadaPor)) {
            _tmpRevisadaPor = null
          } else {
            _tmpRevisadaPor = _stmt.getLong(_columnIndexOfRevisadaPor)
          }
          val _tmpFechaRevision: Long?
          if (_stmt.isNull(_columnIndexOfFechaRevision)) {
            _tmpFechaRevision = null
          } else {
            _tmpFechaRevision = _stmt.getLong(_columnIndexOfFechaRevision)
          }
          _result =
              CampaniaMensual(_tmpId,_tmpTipo,_tmpUbicacionId,_tmpSucursalId,_tmpMes,_tmpEstado,_tmpFechaInicio,_tmpFechaFinal,_tmpRevision,_tmpRevisadaPor,_tmpFechaRevision)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun listarPorUbicacion(tipo: String, ubicacion: Long):
      Flow<List<CampaniaMensual>> {
    val _sql: String =
        "SELECT * FROM campanias_mensuales WHERE tipo=? AND ubicacionId=? ORDER BY mes DESC,id DESC"
    return createFlow(__db, false, arrayOf("campanias_mensuales")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, tipo)
        _argIndex = 2
        _stmt.bindLong(_argIndex, ubicacion)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTipo: Int = getColumnIndexOrThrow(_stmt, "tipo")
        val _columnIndexOfUbicacionId: Int = getColumnIndexOrThrow(_stmt, "ubicacionId")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfMes: Int = getColumnIndexOrThrow(_stmt, "mes")
        val _columnIndexOfEstado: Int = getColumnIndexOrThrow(_stmt, "estado")
        val _columnIndexOfFechaInicio: Int = getColumnIndexOrThrow(_stmt, "fechaInicio")
        val _columnIndexOfFechaFinal: Int = getColumnIndexOrThrow(_stmt, "fechaFinal")
        val _columnIndexOfRevision: Int = getColumnIndexOrThrow(_stmt, "revision")
        val _columnIndexOfRevisadaPor: Int = getColumnIndexOrThrow(_stmt, "revisadaPor")
        val _columnIndexOfFechaRevision: Int = getColumnIndexOrThrow(_stmt, "fechaRevision")
        val _result: MutableList<CampaniaMensual> = mutableListOf()
        while (_stmt.step()) {
          val _item: CampaniaMensual
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTipo: String
          _tmpTipo = _stmt.getText(_columnIndexOfTipo)
          val _tmpUbicacionId: Long
          _tmpUbicacionId = _stmt.getLong(_columnIndexOfUbicacionId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpMes: String
          _tmpMes = _stmt.getText(_columnIndexOfMes)
          val _tmpEstado: String
          _tmpEstado = _stmt.getText(_columnIndexOfEstado)
          val _tmpFechaInicio: Long
          _tmpFechaInicio = _stmt.getLong(_columnIndexOfFechaInicio)
          val _tmpFechaFinal: Long?
          if (_stmt.isNull(_columnIndexOfFechaFinal)) {
            _tmpFechaFinal = null
          } else {
            _tmpFechaFinal = _stmt.getLong(_columnIndexOfFechaFinal)
          }
          val _tmpRevision: String
          _tmpRevision = _stmt.getText(_columnIndexOfRevision)
          val _tmpRevisadaPor: Long?
          if (_stmt.isNull(_columnIndexOfRevisadaPor)) {
            _tmpRevisadaPor = null
          } else {
            _tmpRevisadaPor = _stmt.getLong(_columnIndexOfRevisadaPor)
          }
          val _tmpFechaRevision: Long?
          if (_stmt.isNull(_columnIndexOfFechaRevision)) {
            _tmpFechaRevision = null
          } else {
            _tmpFechaRevision = _stmt.getLong(_columnIndexOfFechaRevision)
          }
          _item =
              CampaniaMensual(_tmpId,_tmpTipo,_tmpUbicacionId,_tmpSucursalId,_tmpMes,_tmpEstado,_tmpFechaInicio,_tmpFechaFinal,_tmpRevision,_tmpRevisadaPor,_tmpFechaRevision)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun finalizadas(): Flow<List<CampaniaMensual>> {
    val _sql: String =
        "SELECT * FROM campanias_mensuales WHERE estado='FINALIZADA' ORDER BY CASE WHEN revision='NUEVO' THEN 0 ELSE 1 END,fechaFinal DESC"
    return createFlow(__db, false, arrayOf("campanias_mensuales")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTipo: Int = getColumnIndexOrThrow(_stmt, "tipo")
        val _columnIndexOfUbicacionId: Int = getColumnIndexOrThrow(_stmt, "ubicacionId")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfMes: Int = getColumnIndexOrThrow(_stmt, "mes")
        val _columnIndexOfEstado: Int = getColumnIndexOrThrow(_stmt, "estado")
        val _columnIndexOfFechaInicio: Int = getColumnIndexOrThrow(_stmt, "fechaInicio")
        val _columnIndexOfFechaFinal: Int = getColumnIndexOrThrow(_stmt, "fechaFinal")
        val _columnIndexOfRevision: Int = getColumnIndexOrThrow(_stmt, "revision")
        val _columnIndexOfRevisadaPor: Int = getColumnIndexOrThrow(_stmt, "revisadaPor")
        val _columnIndexOfFechaRevision: Int = getColumnIndexOrThrow(_stmt, "fechaRevision")
        val _result: MutableList<CampaniaMensual> = mutableListOf()
        while (_stmt.step()) {
          val _item: CampaniaMensual
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTipo: String
          _tmpTipo = _stmt.getText(_columnIndexOfTipo)
          val _tmpUbicacionId: Long
          _tmpUbicacionId = _stmt.getLong(_columnIndexOfUbicacionId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpMes: String
          _tmpMes = _stmt.getText(_columnIndexOfMes)
          val _tmpEstado: String
          _tmpEstado = _stmt.getText(_columnIndexOfEstado)
          val _tmpFechaInicio: Long
          _tmpFechaInicio = _stmt.getLong(_columnIndexOfFechaInicio)
          val _tmpFechaFinal: Long?
          if (_stmt.isNull(_columnIndexOfFechaFinal)) {
            _tmpFechaFinal = null
          } else {
            _tmpFechaFinal = _stmt.getLong(_columnIndexOfFechaFinal)
          }
          val _tmpRevision: String
          _tmpRevision = _stmt.getText(_columnIndexOfRevision)
          val _tmpRevisadaPor: Long?
          if (_stmt.isNull(_columnIndexOfRevisadaPor)) {
            _tmpRevisadaPor = null
          } else {
            _tmpRevisadaPor = _stmt.getLong(_columnIndexOfRevisadaPor)
          }
          val _tmpFechaRevision: Long?
          if (_stmt.isNull(_columnIndexOfFechaRevision)) {
            _tmpFechaRevision = null
          } else {
            _tmpFechaRevision = _stmt.getLong(_columnIndexOfFechaRevision)
          }
          _item =
              CampaniaMensual(_tmpId,_tmpTipo,_tmpUbicacionId,_tmpSucursalId,_tmpMes,_tmpEstado,_tmpFechaInicio,_tmpFechaFinal,_tmpRevision,_tmpRevisadaPor,_tmpFechaRevision)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun pendientesGerencia(): Flow<Int> {
    val _sql: String =
        "SELECT COUNT(*) FROM campanias_mensuales WHERE estado='FINALIZADA' AND revision='NUEVO'"
    return createFlow(__db, false, arrayOf("campanias_mensuales")) { _connection ->
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

  public override fun observarZonas(id: Long): Flow<List<ZonaCampania>> {
    val _sql: String = "SELECT * FROM zonas_campania WHERE campaniaId=? ORDER BY posicionId"
    return createFlow(__db, false, arrayOf("zonas_campania")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfCampaniaId: Int = getColumnIndexOrThrow(_stmt, "campaniaId")
        val _columnIndexOfPosicionId: Int = getColumnIndexOrThrow(_stmt, "posicionId")
        val _columnIndexOfRuta: Int = getColumnIndexOrThrow(_stmt, "ruta")
        val _columnIndexOfEstado: Int = getColumnIndexOrThrow(_stmt, "estado")
        val _columnIndexOfInventarioId: Int = getColumnIndexOrThrow(_stmt, "inventarioId")
        val _columnIndexOfFechaConteo: Int = getColumnIndexOrThrow(_stmt, "fechaConteo")
        val _result: MutableList<ZonaCampania> = mutableListOf()
        while (_stmt.step()) {
          val _item: ZonaCampania
          val _tmpCampaniaId: Long
          _tmpCampaniaId = _stmt.getLong(_columnIndexOfCampaniaId)
          val _tmpPosicionId: Long
          _tmpPosicionId = _stmt.getLong(_columnIndexOfPosicionId)
          val _tmpRuta: String
          _tmpRuta = _stmt.getText(_columnIndexOfRuta)
          val _tmpEstado: String
          _tmpEstado = _stmt.getText(_columnIndexOfEstado)
          val _tmpInventarioId: Long?
          if (_stmt.isNull(_columnIndexOfInventarioId)) {
            _tmpInventarioId = null
          } else {
            _tmpInventarioId = _stmt.getLong(_columnIndexOfInventarioId)
          }
          val _tmpFechaConteo: Long?
          if (_stmt.isNull(_columnIndexOfFechaConteo)) {
            _tmpFechaConteo = null
          } else {
            _tmpFechaConteo = _stmt.getLong(_columnIndexOfFechaConteo)
          }
          _item =
              ZonaCampania(_tmpCampaniaId,_tmpPosicionId,_tmpRuta,_tmpEstado,_tmpInventarioId,_tmpFechaConteo)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun zonas(id: Long): List<ZonaCampania> {
    val _sql: String = "SELECT * FROM zonas_campania WHERE campaniaId=? ORDER BY posicionId"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfCampaniaId: Int = getColumnIndexOrThrow(_stmt, "campaniaId")
        val _columnIndexOfPosicionId: Int = getColumnIndexOrThrow(_stmt, "posicionId")
        val _columnIndexOfRuta: Int = getColumnIndexOrThrow(_stmt, "ruta")
        val _columnIndexOfEstado: Int = getColumnIndexOrThrow(_stmt, "estado")
        val _columnIndexOfInventarioId: Int = getColumnIndexOrThrow(_stmt, "inventarioId")
        val _columnIndexOfFechaConteo: Int = getColumnIndexOrThrow(_stmt, "fechaConteo")
        val _result: MutableList<ZonaCampania> = mutableListOf()
        while (_stmt.step()) {
          val _item: ZonaCampania
          val _tmpCampaniaId: Long
          _tmpCampaniaId = _stmt.getLong(_columnIndexOfCampaniaId)
          val _tmpPosicionId: Long
          _tmpPosicionId = _stmt.getLong(_columnIndexOfPosicionId)
          val _tmpRuta: String
          _tmpRuta = _stmt.getText(_columnIndexOfRuta)
          val _tmpEstado: String
          _tmpEstado = _stmt.getText(_columnIndexOfEstado)
          val _tmpInventarioId: Long?
          if (_stmt.isNull(_columnIndexOfInventarioId)) {
            _tmpInventarioId = null
          } else {
            _tmpInventarioId = _stmt.getLong(_columnIndexOfInventarioId)
          }
          val _tmpFechaConteo: Long?
          if (_stmt.isNull(_columnIndexOfFechaConteo)) {
            _tmpFechaConteo = null
          } else {
            _tmpFechaConteo = _stmt.getLong(_columnIndexOfFechaConteo)
          }
          _item =
              ZonaCampania(_tmpCampaniaId,_tmpPosicionId,_tmpRuta,_tmpEstado,_tmpInventarioId,_tmpFechaConteo)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observarDetalles(id: Long): Flow<List<DetalleCampania>> {
    val _sql: String =
        "SELECT * FROM detalles_campania WHERE campaniaId=? ORDER BY posicionId,producto"
    return createFlow(__db, false, arrayOf("detalles_campania")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCampaniaId: Int = getColumnIndexOrThrow(_stmt, "campaniaId")
        val _columnIndexOfInventarioId: Int = getColumnIndexOrThrow(_stmt, "inventarioId")
        val _columnIndexOfPosicionId: Int = getColumnIndexOrThrow(_stmt, "posicionId")
        val _columnIndexOfCodigo: Int = getColumnIndexOrThrow(_stmt, "codigo")
        val _columnIndexOfProducto: Int = getColumnIndexOrThrow(_stmt, "producto")
        val _columnIndexOfRuta: Int = getColumnIndexOrThrow(_stmt, "ruta")
        val _columnIndexOfCantidadSistema: Int = getColumnIndexOrThrow(_stmt, "cantidadSistema")
        val _columnIndexOfCantidadContada: Int = getColumnIndexOrThrow(_stmt, "cantidadContada")
        val _columnIndexOfAjusteAplicado: Int = getColumnIndexOrThrow(_stmt, "ajusteAplicado")
        val _columnIndexOfFechaCierre: Int = getColumnIndexOrThrow(_stmt, "fechaCierre")
        val _result: MutableList<DetalleCampania> = mutableListOf()
        while (_stmt.step()) {
          val _item: DetalleCampania
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpCampaniaId: Long
          _tmpCampaniaId = _stmt.getLong(_columnIndexOfCampaniaId)
          val _tmpInventarioId: Long
          _tmpInventarioId = _stmt.getLong(_columnIndexOfInventarioId)
          val _tmpPosicionId: Long
          _tmpPosicionId = _stmt.getLong(_columnIndexOfPosicionId)
          val _tmpCodigo: String
          _tmpCodigo = _stmt.getText(_columnIndexOfCodigo)
          val _tmpProducto: String
          _tmpProducto = _stmt.getText(_columnIndexOfProducto)
          val _tmpRuta: String
          _tmpRuta = _stmt.getText(_columnIndexOfRuta)
          val _tmpCantidadSistema: Int
          _tmpCantidadSistema = _stmt.getLong(_columnIndexOfCantidadSistema).toInt()
          val _tmpCantidadContada: Int?
          if (_stmt.isNull(_columnIndexOfCantidadContada)) {
            _tmpCantidadContada = null
          } else {
            _tmpCantidadContada = _stmt.getLong(_columnIndexOfCantidadContada).toInt()
          }
          val _tmpAjusteAplicado: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfAjusteAplicado).toInt()
          _tmpAjusteAplicado = _tmp != 0
          val _tmpFechaCierre: Long
          _tmpFechaCierre = _stmt.getLong(_columnIndexOfFechaCierre)
          _item =
              DetalleCampania(_tmpId,_tmpCampaniaId,_tmpInventarioId,_tmpPosicionId,_tmpCodigo,_tmpProducto,_tmpRuta,_tmpCantidadSistema,_tmpCantidadContada,_tmpAjusteAplicado,_tmpFechaCierre)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun todosDetalles(): List<DetalleCampania> {
    val _sql: String = "SELECT * FROM detalles_campania ORDER BY campaniaId,posicionId,producto"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCampaniaId: Int = getColumnIndexOrThrow(_stmt, "campaniaId")
        val _columnIndexOfInventarioId: Int = getColumnIndexOrThrow(_stmt, "inventarioId")
        val _columnIndexOfPosicionId: Int = getColumnIndexOrThrow(_stmt, "posicionId")
        val _columnIndexOfCodigo: Int = getColumnIndexOrThrow(_stmt, "codigo")
        val _columnIndexOfProducto: Int = getColumnIndexOrThrow(_stmt, "producto")
        val _columnIndexOfRuta: Int = getColumnIndexOrThrow(_stmt, "ruta")
        val _columnIndexOfCantidadSistema: Int = getColumnIndexOrThrow(_stmt, "cantidadSistema")
        val _columnIndexOfCantidadContada: Int = getColumnIndexOrThrow(_stmt, "cantidadContada")
        val _columnIndexOfAjusteAplicado: Int = getColumnIndexOrThrow(_stmt, "ajusteAplicado")
        val _columnIndexOfFechaCierre: Int = getColumnIndexOrThrow(_stmt, "fechaCierre")
        val _result: MutableList<DetalleCampania> = mutableListOf()
        while (_stmt.step()) {
          val _item: DetalleCampania
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpCampaniaId: Long
          _tmpCampaniaId = _stmt.getLong(_columnIndexOfCampaniaId)
          val _tmpInventarioId: Long
          _tmpInventarioId = _stmt.getLong(_columnIndexOfInventarioId)
          val _tmpPosicionId: Long
          _tmpPosicionId = _stmt.getLong(_columnIndexOfPosicionId)
          val _tmpCodigo: String
          _tmpCodigo = _stmt.getText(_columnIndexOfCodigo)
          val _tmpProducto: String
          _tmpProducto = _stmt.getText(_columnIndexOfProducto)
          val _tmpRuta: String
          _tmpRuta = _stmt.getText(_columnIndexOfRuta)
          val _tmpCantidadSistema: Int
          _tmpCantidadSistema = _stmt.getLong(_columnIndexOfCantidadSistema).toInt()
          val _tmpCantidadContada: Int?
          if (_stmt.isNull(_columnIndexOfCantidadContada)) {
            _tmpCantidadContada = null
          } else {
            _tmpCantidadContada = _stmt.getLong(_columnIndexOfCantidadContada).toInt()
          }
          val _tmpAjusteAplicado: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfAjusteAplicado).toInt()
          _tmpAjusteAplicado = _tmp != 0
          val _tmpFechaCierre: Long
          _tmpFechaCierre = _stmt.getLong(_columnIndexOfFechaCierre)
          _item =
              DetalleCampania(_tmpId,_tmpCampaniaId,_tmpInventarioId,_tmpPosicionId,_tmpCodigo,_tmpProducto,_tmpRuta,_tmpCantidadSistema,_tmpCantidadContada,_tmpAjusteAplicado,_tmpFechaCierre)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun todasCampanias(): List<CampaniaMensual> {
    val _sql: String = "SELECT * FROM campanias_mensuales ORDER BY mes, id"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTipo: Int = getColumnIndexOrThrow(_stmt, "tipo")
        val _columnIndexOfUbicacionId: Int = getColumnIndexOrThrow(_stmt, "ubicacionId")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfMes: Int = getColumnIndexOrThrow(_stmt, "mes")
        val _columnIndexOfEstado: Int = getColumnIndexOrThrow(_stmt, "estado")
        val _columnIndexOfFechaInicio: Int = getColumnIndexOrThrow(_stmt, "fechaInicio")
        val _columnIndexOfFechaFinal: Int = getColumnIndexOrThrow(_stmt, "fechaFinal")
        val _columnIndexOfRevision: Int = getColumnIndexOrThrow(_stmt, "revision")
        val _columnIndexOfRevisadaPor: Int = getColumnIndexOrThrow(_stmt, "revisadaPor")
        val _columnIndexOfFechaRevision: Int = getColumnIndexOrThrow(_stmt, "fechaRevision")
        val _result: MutableList<CampaniaMensual> = mutableListOf()
        while (_stmt.step()) {
          val _item: CampaniaMensual
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTipo: String
          _tmpTipo = _stmt.getText(_columnIndexOfTipo)
          val _tmpUbicacionId: Long
          _tmpUbicacionId = _stmt.getLong(_columnIndexOfUbicacionId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpMes: String
          _tmpMes = _stmt.getText(_columnIndexOfMes)
          val _tmpEstado: String
          _tmpEstado = _stmt.getText(_columnIndexOfEstado)
          val _tmpFechaInicio: Long
          _tmpFechaInicio = _stmt.getLong(_columnIndexOfFechaInicio)
          val _tmpFechaFinal: Long?
          if (_stmt.isNull(_columnIndexOfFechaFinal)) {
            _tmpFechaFinal = null
          } else {
            _tmpFechaFinal = _stmt.getLong(_columnIndexOfFechaFinal)
          }
          val _tmpRevision: String
          _tmpRevision = _stmt.getText(_columnIndexOfRevision)
          val _tmpRevisadaPor: Long?
          if (_stmt.isNull(_columnIndexOfRevisadaPor)) {
            _tmpRevisadaPor = null
          } else {
            _tmpRevisadaPor = _stmt.getLong(_columnIndexOfRevisadaPor)
          }
          val _tmpFechaRevision: Long?
          if (_stmt.isNull(_columnIndexOfFechaRevision)) {
            _tmpFechaRevision = null
          } else {
            _tmpFechaRevision = _stmt.getLong(_columnIndexOfFechaRevision)
          }
          _item =
              CampaniaMensual(_tmpId,_tmpTipo,_tmpUbicacionId,_tmpSucursalId,_tmpMes,_tmpEstado,_tmpFechaInicio,_tmpFechaFinal,_tmpRevision,_tmpRevisadaPor,_tmpFechaRevision)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun zonasPendientes(id: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM zonas_campania WHERE campaniaId=? AND estado!='CONTADA'"
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

  public override suspend fun numeroZonas(id: Long): Int {
    val _sql: String = "SELECT COUNT(*) FROM zonas_campania WHERE campaniaId=?"
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

  public override suspend fun reservarZona(
    id: Long,
    pos: Long,
    inventario: Long,
  ): Int {
    val _sql: String =
        "UPDATE zonas_campania SET estado='EN_CURSO',inventarioId=? WHERE campaniaId=? AND posicionId=? AND estado='PENDIENTE' AND inventarioId IS NULL"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, inventario)
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

  public override suspend fun liberarZonas(id: Long, inventario: Long): Int {
    val _sql: String =
        "UPDATE zonas_campania SET estado='PENDIENTE',inventarioId=NULL WHERE campaniaId=? AND inventarioId=? AND estado='EN_CURSO'"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        _argIndex = 2
        _stmt.bindLong(_argIndex, inventario)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun entregarZonas(
    id: Long,
    inventario: Long,
    fecha: Long,
  ): Int {
    val _sql: String =
        "UPDATE zonas_campania SET estado='CONTADA',fechaConteo=? WHERE campaniaId=? AND inventarioId=? AND estado='EN_CURSO'"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, fecha)
        _argIndex = 2
        _stmt.bindLong(_argIndex, id)
        _argIndex = 3
        _stmt.bindLong(_argIndex, inventario)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun finalizar(id: Long, fecha: Long): Int {
    val _sql: String =
        "UPDATE campanias_mensuales SET estado='FINALIZADA',fechaFinal=? WHERE id=? AND estado='ABIERTA' AND NOT EXISTS(SELECT 1 FROM zonas_campania WHERE campaniaId=? AND estado!='CONTADA')"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, fecha)
        _argIndex = 2
        _stmt.bindLong(_argIndex, id)
        _argIndex = 3
        _stmt.bindLong(_argIndex, id)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun revisar(
    id: Long,
    uid: Long,
    fecha: Long,
  ): Int {
    val _sql: String =
        "UPDATE campanias_mensuales SET revision='REVISADO',revisadaPor=?,fechaRevision=? WHERE id=? AND estado='FINALIZADA' AND revision='NUEVO'"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, uid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, fecha)
        _argIndex = 3
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
