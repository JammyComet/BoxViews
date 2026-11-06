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
public class OperacionesTiendaDao_Impl(
  __db: RoomDatabase,
) : OperacionesTiendaDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfZonaConteoTienda: EntityInsertAdapter<ZonaConteoTienda>

  private val __insertAdapterOfEventoConteoTienda: EntityInsertAdapter<EventoConteoTienda>

  private val __insertAdapterOfSalidaTienda: EntityInsertAdapter<SalidaTienda>

  private val __insertAdapterOfInventarioTienda: EntityInsertAdapter<InventarioTienda>

  private val __insertAdapterOfLineaInventarioTienda: EntityInsertAdapter<LineaInventarioTienda>
  init {
    this.__db = __db
    this.__insertAdapterOfZonaConteoTienda = object : EntityInsertAdapter<ZonaConteoTienda>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `zonas_conteo_tienda` (`inventarioId`,`posicionId`,`asignadoA`,`confirmadoPor`,`fechaConfirmacion`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ZonaConteoTienda) {
        statement.bindLong(1, entity.inventarioId)
        statement.bindLong(2, entity.posicionId)
        statement.bindLong(3, entity.asignadoA)
        val _tmpConfirmadoPor: Long? = entity.confirmadoPor
        if (_tmpConfirmadoPor == null) {
          statement.bindNull(4)
        } else {
          statement.bindLong(4, _tmpConfirmadoPor)
        }
        val _tmpFechaConfirmacion: Long? = entity.fechaConfirmacion
        if (_tmpFechaConfirmacion == null) {
          statement.bindNull(5)
        } else {
          statement.bindLong(5, _tmpFechaConfirmacion)
        }
      }
    }
    this.__insertAdapterOfEventoConteoTienda = object : EntityInsertAdapter<EventoConteoTienda>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `eventos_conteo_tienda` (`id`,`inventarioId`,`lineaId`,`productoId`,`posicionId`,`usuarioId`,`cantidadAnterior`,`cantidadNueva`,`tipo`,`fecha`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: EventoConteoTienda) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.inventarioId)
        statement.bindLong(3, entity.lineaId)
        statement.bindLong(4, entity.productoId)
        statement.bindLong(5, entity.posicionId)
        statement.bindLong(6, entity.usuarioId)
        val _tmpCantidadAnterior: Int? = entity.cantidadAnterior
        if (_tmpCantidadAnterior == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpCantidadAnterior.toLong())
        }
        statement.bindLong(8, entity.cantidadNueva.toLong())
        statement.bindText(9, entity.tipo)
        statement.bindLong(10, entity.fecha)
      }
    }
    this.__insertAdapterOfSalidaTienda = object : EntityInsertAdapter<SalidaTienda>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `salidas_tienda` (`id`,`sucursalId`,`usuarioId`,`productoId`,`posicionId`,`cantidad`,`tipo`,`motivo`,`fecha`,`precioUnitarioVentaClp`,`totalVentaClp`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: SalidaTienda) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.sucursalId)
        statement.bindLong(3, entity.usuarioId)
        statement.bindLong(4, entity.productoId)
        statement.bindLong(5, entity.posicionId)
        statement.bindLong(6, entity.cantidad.toLong())
        statement.bindText(7, entity.tipo)
        statement.bindText(8, entity.motivo)
        statement.bindLong(9, entity.fecha)
        val _tmpPrecioUnitarioVentaClp: Long? = entity.precioUnitarioVentaClp
        if (_tmpPrecioUnitarioVentaClp == null) {
          statement.bindNull(10)
        } else {
          statement.bindLong(10, _tmpPrecioUnitarioVentaClp)
        }
        val _tmpTotalVentaClp: Long? = entity.totalVentaClp
        if (_tmpTotalVentaClp == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmpTotalVentaClp)
        }
      }
    }
    this.__insertAdapterOfInventarioTienda = object : EntityInsertAdapter<InventarioTienda>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `inventarios_tienda` (`id`,`sucursalId`,`nombre`,`estado`,`fechaApertura`,`cerradoPor`,`fechaCierre`,`estadoRevision`,`revisadoPor`,`fechaRevision`,`motivoCierre`,`verificacionVaciaPor`,`fechaVerificacionVacia`,`campaniaId`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: InventarioTienda) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.sucursalId)
        statement.bindText(3, entity.nombre)
        statement.bindText(4, entity.estado)
        statement.bindLong(5, entity.fechaApertura)
        val _tmpCerradoPor: Long? = entity.cerradoPor
        if (_tmpCerradoPor == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpCerradoPor)
        }
        val _tmpFechaCierre: Long? = entity.fechaCierre
        if (_tmpFechaCierre == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpFechaCierre)
        }
        statement.bindText(8, entity.estadoRevision)
        val _tmpRevisadoPor: Long? = entity.revisadoPor
        if (_tmpRevisadoPor == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpRevisadoPor)
        }
        val _tmpFechaRevision: Long? = entity.fechaRevision
        if (_tmpFechaRevision == null) {
          statement.bindNull(10)
        } else {
          statement.bindLong(10, _tmpFechaRevision)
        }
        statement.bindText(11, entity.motivoCierre)
        val _tmpVerificacionVaciaPor: Long? = entity.verificacionVaciaPor
        if (_tmpVerificacionVaciaPor == null) {
          statement.bindNull(12)
        } else {
          statement.bindLong(12, _tmpVerificacionVaciaPor)
        }
        val _tmpFechaVerificacionVacia: Long? = entity.fechaVerificacionVacia
        if (_tmpFechaVerificacionVacia == null) {
          statement.bindNull(13)
        } else {
          statement.bindLong(13, _tmpFechaVerificacionVacia)
        }
        val _tmpCampaniaId: Long? = entity.campaniaId
        if (_tmpCampaniaId == null) {
          statement.bindNull(14)
        } else {
          statement.bindLong(14, _tmpCampaniaId)
        }
      }
    }
    this.__insertAdapterOfLineaInventarioTienda = object :
        EntityInsertAdapter<LineaInventarioTienda>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `lineas_inventario_tienda` (`id`,`inventarioId`,`productoId`,`posicionId`,`stockInicio`,`cantidadContada`,`contadoPor`,`fechaConteo`,`codigoProductoCierre`,`nombreProductoCierre`,`rutaUbicacionCierre`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: LineaInventarioTienda) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.inventarioId)
        statement.bindLong(3, entity.productoId)
        statement.bindLong(4, entity.posicionId)
        statement.bindLong(5, entity.stockInicio.toLong())
        val _tmpCantidadContada: Int? = entity.cantidadContada
        if (_tmpCantidadContada == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpCantidadContada.toLong())
        }
        val _tmpContadoPor: Long? = entity.contadoPor
        if (_tmpContadoPor == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpContadoPor)
        }
        val _tmpFechaConteo: Long? = entity.fechaConteo
        if (_tmpFechaConteo == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpFechaConteo)
        }
        val _tmpCodigoProductoCierre: String? = entity.codigoProductoCierre
        if (_tmpCodigoProductoCierre == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpCodigoProductoCierre)
        }
        val _tmpNombreProductoCierre: String? = entity.nombreProductoCierre
        if (_tmpNombreProductoCierre == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpNombreProductoCierre)
        }
        val _tmpRutaUbicacionCierre: String? = entity.rutaUbicacionCierre
        if (_tmpRutaUbicacionCierre == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpRutaUbicacionCierre)
        }
      }
    }
  }

  public override suspend fun agregarZonaTienda(zona: ZonaConteoTienda): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfZonaConteoTienda.insert(_connection, zona)
  }

  public override suspend fun registrarEventoConteo(evento: EventoConteoTienda): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfEventoConteoTienda.insertAndReturnId(_connection, evento)
    _result
  }

  public override suspend fun registrarSalida(salida: SalidaTienda): Long = performSuspending(__db,
      false, true) { _connection ->
    val _result: Long = __insertAdapterOfSalidaTienda.insertAndReturnId(_connection, salida)
    _result
  }

  public override suspend fun abrirInventario(inventario: InventarioTienda): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfInventarioTienda.insertAndReturnId(_connection, inventario)
    _result
  }

  public override suspend fun agregarLineaInventario(linea: LineaInventarioTienda): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfLineaInventarioTienda.insertAndReturnId(_connection, linea)
    _result
  }

  public override suspend fun numeroSalidas(): Int {
    val _sql: String = "SELECT COUNT(*) FROM salidas_tienda"
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

  public override fun zonasInventarioTienda(id: Long): Flow<List<ZonaConteoTienda>> {
    val _sql: String = "SELECT * FROM zonas_conteo_tienda WHERE inventarioId=? ORDER BY posicionId"
    return createFlow(__db, false, arrayOf("zonas_conteo_tienda")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfInventarioId: Int = getColumnIndexOrThrow(_stmt, "inventarioId")
        val _columnIndexOfPosicionId: Int = getColumnIndexOrThrow(_stmt, "posicionId")
        val _columnIndexOfAsignadoA: Int = getColumnIndexOrThrow(_stmt, "asignadoA")
        val _columnIndexOfConfirmadoPor: Int = getColumnIndexOrThrow(_stmt, "confirmadoPor")
        val _columnIndexOfFechaConfirmacion: Int = getColumnIndexOrThrow(_stmt, "fechaConfirmacion")
        val _result: MutableList<ZonaConteoTienda> = mutableListOf()
        while (_stmt.step()) {
          val _item: ZonaConteoTienda
          val _tmpInventarioId: Long
          _tmpInventarioId = _stmt.getLong(_columnIndexOfInventarioId)
          val _tmpPosicionId: Long
          _tmpPosicionId = _stmt.getLong(_columnIndexOfPosicionId)
          val _tmpAsignadoA: Long
          _tmpAsignadoA = _stmt.getLong(_columnIndexOfAsignadoA)
          val _tmpConfirmadoPor: Long?
          if (_stmt.isNull(_columnIndexOfConfirmadoPor)) {
            _tmpConfirmadoPor = null
          } else {
            _tmpConfirmadoPor = _stmt.getLong(_columnIndexOfConfirmadoPor)
          }
          val _tmpFechaConfirmacion: Long?
          if (_stmt.isNull(_columnIndexOfFechaConfirmacion)) {
            _tmpFechaConfirmacion = null
          } else {
            _tmpFechaConfirmacion = _stmt.getLong(_columnIndexOfFechaConfirmacion)
          }
          _item =
              ZonaConteoTienda(_tmpInventarioId,_tmpPosicionId,_tmpAsignadoA,_tmpConfirmadoPor,_tmpFechaConfirmacion)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun zonasTiendaActuales(id: Long): List<ZonaConteoTienda> {
    val _sql: String = "SELECT * FROM zonas_conteo_tienda WHERE inventarioId=? ORDER BY posicionId"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfInventarioId: Int = getColumnIndexOrThrow(_stmt, "inventarioId")
        val _columnIndexOfPosicionId: Int = getColumnIndexOrThrow(_stmt, "posicionId")
        val _columnIndexOfAsignadoA: Int = getColumnIndexOrThrow(_stmt, "asignadoA")
        val _columnIndexOfConfirmadoPor: Int = getColumnIndexOrThrow(_stmt, "confirmadoPor")
        val _columnIndexOfFechaConfirmacion: Int = getColumnIndexOrThrow(_stmt, "fechaConfirmacion")
        val _result: MutableList<ZonaConteoTienda> = mutableListOf()
        while (_stmt.step()) {
          val _item: ZonaConteoTienda
          val _tmpInventarioId: Long
          _tmpInventarioId = _stmt.getLong(_columnIndexOfInventarioId)
          val _tmpPosicionId: Long
          _tmpPosicionId = _stmt.getLong(_columnIndexOfPosicionId)
          val _tmpAsignadoA: Long
          _tmpAsignadoA = _stmt.getLong(_columnIndexOfAsignadoA)
          val _tmpConfirmadoPor: Long?
          if (_stmt.isNull(_columnIndexOfConfirmadoPor)) {
            _tmpConfirmadoPor = null
          } else {
            _tmpConfirmadoPor = _stmt.getLong(_columnIndexOfConfirmadoPor)
          }
          val _tmpFechaConfirmacion: Long?
          if (_stmt.isNull(_columnIndexOfFechaConfirmacion)) {
            _tmpFechaConfirmacion = null
          } else {
            _tmpFechaConfirmacion = _stmt.getLong(_columnIndexOfFechaConfirmacion)
          }
          _item =
              ZonaConteoTienda(_tmpInventarioId,_tmpPosicionId,_tmpAsignadoA,_tmpConfirmadoPor,_tmpFechaConfirmacion)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun zonasTiendaPendientes(inv: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM zonas_conteo_tienda WHERE inventarioId=? AND (asignadoA=0 OR confirmadoPor IS NULL)"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, inv)
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

  public override suspend fun zonasTiendaAsignadasAbiertas(sid: Long, uid: Long): Int {
    val _sql: String = """
        |SELECT COUNT(*) FROM zonas_conteo_tienda z JOIN inventarios_tienda i ON i.id=z.inventarioId
        |        WHERE i.sucursalId=? AND i.estado='ABIERTO' AND z.asignadoA=?
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
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

  public override fun eventosConteo(id: Long): Flow<List<EventoConteoTienda>> {
    val _sql: String =
        "SELECT * FROM eventos_conteo_tienda WHERE inventarioId=? ORDER BY fecha DESC,id DESC"
    return createFlow(__db, false, arrayOf("eventos_conteo_tienda")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfInventarioId: Int = getColumnIndexOrThrow(_stmt, "inventarioId")
        val _columnIndexOfLineaId: Int = getColumnIndexOrThrow(_stmt, "lineaId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfPosicionId: Int = getColumnIndexOrThrow(_stmt, "posicionId")
        val _columnIndexOfUsuarioId: Int = getColumnIndexOrThrow(_stmt, "usuarioId")
        val _columnIndexOfCantidadAnterior: Int = getColumnIndexOrThrow(_stmt, "cantidadAnterior")
        val _columnIndexOfCantidadNueva: Int = getColumnIndexOrThrow(_stmt, "cantidadNueva")
        val _columnIndexOfTipo: Int = getColumnIndexOrThrow(_stmt, "tipo")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _result: MutableList<EventoConteoTienda> = mutableListOf()
        while (_stmt.step()) {
          val _item: EventoConteoTienda
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpInventarioId: Long
          _tmpInventarioId = _stmt.getLong(_columnIndexOfInventarioId)
          val _tmpLineaId: Long
          _tmpLineaId = _stmt.getLong(_columnIndexOfLineaId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpPosicionId: Long
          _tmpPosicionId = _stmt.getLong(_columnIndexOfPosicionId)
          val _tmpUsuarioId: Long
          _tmpUsuarioId = _stmt.getLong(_columnIndexOfUsuarioId)
          val _tmpCantidadAnterior: Int?
          if (_stmt.isNull(_columnIndexOfCantidadAnterior)) {
            _tmpCantidadAnterior = null
          } else {
            _tmpCantidadAnterior = _stmt.getLong(_columnIndexOfCantidadAnterior).toInt()
          }
          val _tmpCantidadNueva: Int
          _tmpCantidadNueva = _stmt.getLong(_columnIndexOfCantidadNueva).toInt()
          val _tmpTipo: String
          _tmpTipo = _stmt.getText(_columnIndexOfTipo)
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          _item =
              EventoConteoTienda(_tmpId,_tmpInventarioId,_tmpLineaId,_tmpProductoId,_tmpPosicionId,_tmpUsuarioId,_tmpCantidadAnterior,_tmpCantidadNueva,_tmpTipo,_tmpFecha)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun todosEventosConteo(): List<EventoConteoTienda> {
    val _sql: String = "SELECT * FROM eventos_conteo_tienda ORDER BY fecha DESC,id DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfInventarioId: Int = getColumnIndexOrThrow(_stmt, "inventarioId")
        val _columnIndexOfLineaId: Int = getColumnIndexOrThrow(_stmt, "lineaId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfPosicionId: Int = getColumnIndexOrThrow(_stmt, "posicionId")
        val _columnIndexOfUsuarioId: Int = getColumnIndexOrThrow(_stmt, "usuarioId")
        val _columnIndexOfCantidadAnterior: Int = getColumnIndexOrThrow(_stmt, "cantidadAnterior")
        val _columnIndexOfCantidadNueva: Int = getColumnIndexOrThrow(_stmt, "cantidadNueva")
        val _columnIndexOfTipo: Int = getColumnIndexOrThrow(_stmt, "tipo")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _result: MutableList<EventoConteoTienda> = mutableListOf()
        while (_stmt.step()) {
          val _item: EventoConteoTienda
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpInventarioId: Long
          _tmpInventarioId = _stmt.getLong(_columnIndexOfInventarioId)
          val _tmpLineaId: Long
          _tmpLineaId = _stmt.getLong(_columnIndexOfLineaId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpPosicionId: Long
          _tmpPosicionId = _stmt.getLong(_columnIndexOfPosicionId)
          val _tmpUsuarioId: Long
          _tmpUsuarioId = _stmt.getLong(_columnIndexOfUsuarioId)
          val _tmpCantidadAnterior: Int?
          if (_stmt.isNull(_columnIndexOfCantidadAnterior)) {
            _tmpCantidadAnterior = null
          } else {
            _tmpCantidadAnterior = _stmt.getLong(_columnIndexOfCantidadAnterior).toInt()
          }
          val _tmpCantidadNueva: Int
          _tmpCantidadNueva = _stmt.getLong(_columnIndexOfCantidadNueva).toInt()
          val _tmpTipo: String
          _tmpTipo = _stmt.getText(_columnIndexOfTipo)
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          _item =
              EventoConteoTienda(_tmpId,_tmpInventarioId,_tmpLineaId,_tmpProductoId,_tmpPosicionId,_tmpUsuarioId,_tmpCantidadAnterior,_tmpCantidadNueva,_tmpTipo,_tmpFecha)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun salidas(sid: Long): Flow<List<SalidaTienda>> {
    val _sql: String =
        "SELECT * FROM salidas_tienda WHERE sucursalId=? ORDER BY fecha DESC,id DESC LIMIT 100"
    return createFlow(__db, false, arrayOf("salidas_tienda")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfUsuarioId: Int = getColumnIndexOrThrow(_stmt, "usuarioId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfPosicionId: Int = getColumnIndexOrThrow(_stmt, "posicionId")
        val _columnIndexOfCantidad: Int = getColumnIndexOrThrow(_stmt, "cantidad")
        val _columnIndexOfTipo: Int = getColumnIndexOrThrow(_stmt, "tipo")
        val _columnIndexOfMotivo: Int = getColumnIndexOrThrow(_stmt, "motivo")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _columnIndexOfPrecioUnitarioVentaClp: Int = getColumnIndexOrThrow(_stmt,
            "precioUnitarioVentaClp")
        val _columnIndexOfTotalVentaClp: Int = getColumnIndexOrThrow(_stmt, "totalVentaClp")
        val _result: MutableList<SalidaTienda> = mutableListOf()
        while (_stmt.step()) {
          val _item: SalidaTienda
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpUsuarioId: Long
          _tmpUsuarioId = _stmt.getLong(_columnIndexOfUsuarioId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpPosicionId: Long
          _tmpPosicionId = _stmt.getLong(_columnIndexOfPosicionId)
          val _tmpCantidad: Int
          _tmpCantidad = _stmt.getLong(_columnIndexOfCantidad).toInt()
          val _tmpTipo: String
          _tmpTipo = _stmt.getText(_columnIndexOfTipo)
          val _tmpMotivo: String
          _tmpMotivo = _stmt.getText(_columnIndexOfMotivo)
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          val _tmpPrecioUnitarioVentaClp: Long?
          if (_stmt.isNull(_columnIndexOfPrecioUnitarioVentaClp)) {
            _tmpPrecioUnitarioVentaClp = null
          } else {
            _tmpPrecioUnitarioVentaClp = _stmt.getLong(_columnIndexOfPrecioUnitarioVentaClp)
          }
          val _tmpTotalVentaClp: Long?
          if (_stmt.isNull(_columnIndexOfTotalVentaClp)) {
            _tmpTotalVentaClp = null
          } else {
            _tmpTotalVentaClp = _stmt.getLong(_columnIndexOfTotalVentaClp)
          }
          _item =
              SalidaTienda(_tmpId,_tmpSucursalId,_tmpUsuarioId,_tmpProductoId,_tmpPosicionId,_tmpCantidad,_tmpTipo,_tmpMotivo,_tmpFecha,_tmpPrecioUnitarioVentaClp,_tmpTotalVentaClp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun salidasGlobales(): List<SalidaTienda> {
    val _sql: String = "SELECT * FROM salidas_tienda ORDER BY fecha DESC,id DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfUsuarioId: Int = getColumnIndexOrThrow(_stmt, "usuarioId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfPosicionId: Int = getColumnIndexOrThrow(_stmt, "posicionId")
        val _columnIndexOfCantidad: Int = getColumnIndexOrThrow(_stmt, "cantidad")
        val _columnIndexOfTipo: Int = getColumnIndexOrThrow(_stmt, "tipo")
        val _columnIndexOfMotivo: Int = getColumnIndexOrThrow(_stmt, "motivo")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _columnIndexOfPrecioUnitarioVentaClp: Int = getColumnIndexOrThrow(_stmt,
            "precioUnitarioVentaClp")
        val _columnIndexOfTotalVentaClp: Int = getColumnIndexOrThrow(_stmt, "totalVentaClp")
        val _result: MutableList<SalidaTienda> = mutableListOf()
        while (_stmt.step()) {
          val _item: SalidaTienda
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpUsuarioId: Long
          _tmpUsuarioId = _stmt.getLong(_columnIndexOfUsuarioId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpPosicionId: Long
          _tmpPosicionId = _stmt.getLong(_columnIndexOfPosicionId)
          val _tmpCantidad: Int
          _tmpCantidad = _stmt.getLong(_columnIndexOfCantidad).toInt()
          val _tmpTipo: String
          _tmpTipo = _stmt.getText(_columnIndexOfTipo)
          val _tmpMotivo: String
          _tmpMotivo = _stmt.getText(_columnIndexOfMotivo)
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          val _tmpPrecioUnitarioVentaClp: Long?
          if (_stmt.isNull(_columnIndexOfPrecioUnitarioVentaClp)) {
            _tmpPrecioUnitarioVentaClp = null
          } else {
            _tmpPrecioUnitarioVentaClp = _stmt.getLong(_columnIndexOfPrecioUnitarioVentaClp)
          }
          val _tmpTotalVentaClp: Long?
          if (_stmt.isNull(_columnIndexOfTotalVentaClp)) {
            _tmpTotalVentaClp = null
          } else {
            _tmpTotalVentaClp = _stmt.getLong(_columnIndexOfTotalVentaClp)
          }
          _item =
              SalidaTienda(_tmpId,_tmpSucursalId,_tmpUsuarioId,_tmpProductoId,_tmpPosicionId,_tmpCantidad,_tmpTipo,_tmpMotivo,_tmpFecha,_tmpPrecioUnitarioVentaClp,_tmpTotalVentaClp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun inventarios(sid: Long): Flow<List<InventarioTienda>> {
    val _sql: String =
        "SELECT * FROM inventarios_tienda WHERE sucursalId=? AND estado!='CANCELADO' ORDER BY fechaApertura DESC,id DESC"
    return createFlow(__db, false, arrayOf("inventarios_tienda")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _columnIndexOfEstado: Int = getColumnIndexOrThrow(_stmt, "estado")
        val _columnIndexOfFechaApertura: Int = getColumnIndexOrThrow(_stmt, "fechaApertura")
        val _columnIndexOfCerradoPor: Int = getColumnIndexOrThrow(_stmt, "cerradoPor")
        val _columnIndexOfFechaCierre: Int = getColumnIndexOrThrow(_stmt, "fechaCierre")
        val _columnIndexOfEstadoRevision: Int = getColumnIndexOrThrow(_stmt, "estadoRevision")
        val _columnIndexOfRevisadoPor: Int = getColumnIndexOrThrow(_stmt, "revisadoPor")
        val _columnIndexOfFechaRevision: Int = getColumnIndexOrThrow(_stmt, "fechaRevision")
        val _columnIndexOfMotivoCierre: Int = getColumnIndexOrThrow(_stmt, "motivoCierre")
        val _columnIndexOfVerificacionVaciaPor: Int = getColumnIndexOrThrow(_stmt,
            "verificacionVaciaPor")
        val _columnIndexOfFechaVerificacionVacia: Int = getColumnIndexOrThrow(_stmt,
            "fechaVerificacionVacia")
        val _columnIndexOfCampaniaId: Int = getColumnIndexOrThrow(_stmt, "campaniaId")
        val _result: MutableList<InventarioTienda> = mutableListOf()
        while (_stmt.step()) {
          val _item: InventarioTienda
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpNombre: String
          _tmpNombre = _stmt.getText(_columnIndexOfNombre)
          val _tmpEstado: String
          _tmpEstado = _stmt.getText(_columnIndexOfEstado)
          val _tmpFechaApertura: Long
          _tmpFechaApertura = _stmt.getLong(_columnIndexOfFechaApertura)
          val _tmpCerradoPor: Long?
          if (_stmt.isNull(_columnIndexOfCerradoPor)) {
            _tmpCerradoPor = null
          } else {
            _tmpCerradoPor = _stmt.getLong(_columnIndexOfCerradoPor)
          }
          val _tmpFechaCierre: Long?
          if (_stmt.isNull(_columnIndexOfFechaCierre)) {
            _tmpFechaCierre = null
          } else {
            _tmpFechaCierre = _stmt.getLong(_columnIndexOfFechaCierre)
          }
          val _tmpEstadoRevision: String
          _tmpEstadoRevision = _stmt.getText(_columnIndexOfEstadoRevision)
          val _tmpRevisadoPor: Long?
          if (_stmt.isNull(_columnIndexOfRevisadoPor)) {
            _tmpRevisadoPor = null
          } else {
            _tmpRevisadoPor = _stmt.getLong(_columnIndexOfRevisadoPor)
          }
          val _tmpFechaRevision: Long?
          if (_stmt.isNull(_columnIndexOfFechaRevision)) {
            _tmpFechaRevision = null
          } else {
            _tmpFechaRevision = _stmt.getLong(_columnIndexOfFechaRevision)
          }
          val _tmpMotivoCierre: String
          _tmpMotivoCierre = _stmt.getText(_columnIndexOfMotivoCierre)
          val _tmpVerificacionVaciaPor: Long?
          if (_stmt.isNull(_columnIndexOfVerificacionVaciaPor)) {
            _tmpVerificacionVaciaPor = null
          } else {
            _tmpVerificacionVaciaPor = _stmt.getLong(_columnIndexOfVerificacionVaciaPor)
          }
          val _tmpFechaVerificacionVacia: Long?
          if (_stmt.isNull(_columnIndexOfFechaVerificacionVacia)) {
            _tmpFechaVerificacionVacia = null
          } else {
            _tmpFechaVerificacionVacia = _stmt.getLong(_columnIndexOfFechaVerificacionVacia)
          }
          val _tmpCampaniaId: Long?
          if (_stmt.isNull(_columnIndexOfCampaniaId)) {
            _tmpCampaniaId = null
          } else {
            _tmpCampaniaId = _stmt.getLong(_columnIndexOfCampaniaId)
          }
          _item =
              InventarioTienda(_tmpId,_tmpSucursalId,_tmpNombre,_tmpEstado,_tmpFechaApertura,_tmpCerradoPor,_tmpFechaCierre,_tmpEstadoRevision,_tmpRevisadoPor,_tmpFechaRevision,_tmpMotivoCierre,_tmpVerificacionVaciaPor,_tmpFechaVerificacionVacia,_tmpCampaniaId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun inventarioAbiertoFlujo(sid: Long): Flow<InventarioTienda?> {
    val _sql: String =
        "SELECT * FROM inventarios_tienda WHERE sucursalId=? AND estado='ABIERTO' ORDER BY id DESC LIMIT 1"
    return createFlow(__db, false, arrayOf("inventarios_tienda")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _columnIndexOfEstado: Int = getColumnIndexOrThrow(_stmt, "estado")
        val _columnIndexOfFechaApertura: Int = getColumnIndexOrThrow(_stmt, "fechaApertura")
        val _columnIndexOfCerradoPor: Int = getColumnIndexOrThrow(_stmt, "cerradoPor")
        val _columnIndexOfFechaCierre: Int = getColumnIndexOrThrow(_stmt, "fechaCierre")
        val _columnIndexOfEstadoRevision: Int = getColumnIndexOrThrow(_stmt, "estadoRevision")
        val _columnIndexOfRevisadoPor: Int = getColumnIndexOrThrow(_stmt, "revisadoPor")
        val _columnIndexOfFechaRevision: Int = getColumnIndexOrThrow(_stmt, "fechaRevision")
        val _columnIndexOfMotivoCierre: Int = getColumnIndexOrThrow(_stmt, "motivoCierre")
        val _columnIndexOfVerificacionVaciaPor: Int = getColumnIndexOrThrow(_stmt,
            "verificacionVaciaPor")
        val _columnIndexOfFechaVerificacionVacia: Int = getColumnIndexOrThrow(_stmt,
            "fechaVerificacionVacia")
        val _columnIndexOfCampaniaId: Int = getColumnIndexOrThrow(_stmt, "campaniaId")
        val _result: InventarioTienda?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpNombre: String
          _tmpNombre = _stmt.getText(_columnIndexOfNombre)
          val _tmpEstado: String
          _tmpEstado = _stmt.getText(_columnIndexOfEstado)
          val _tmpFechaApertura: Long
          _tmpFechaApertura = _stmt.getLong(_columnIndexOfFechaApertura)
          val _tmpCerradoPor: Long?
          if (_stmt.isNull(_columnIndexOfCerradoPor)) {
            _tmpCerradoPor = null
          } else {
            _tmpCerradoPor = _stmt.getLong(_columnIndexOfCerradoPor)
          }
          val _tmpFechaCierre: Long?
          if (_stmt.isNull(_columnIndexOfFechaCierre)) {
            _tmpFechaCierre = null
          } else {
            _tmpFechaCierre = _stmt.getLong(_columnIndexOfFechaCierre)
          }
          val _tmpEstadoRevision: String
          _tmpEstadoRevision = _stmt.getText(_columnIndexOfEstadoRevision)
          val _tmpRevisadoPor: Long?
          if (_stmt.isNull(_columnIndexOfRevisadoPor)) {
            _tmpRevisadoPor = null
          } else {
            _tmpRevisadoPor = _stmt.getLong(_columnIndexOfRevisadoPor)
          }
          val _tmpFechaRevision: Long?
          if (_stmt.isNull(_columnIndexOfFechaRevision)) {
            _tmpFechaRevision = null
          } else {
            _tmpFechaRevision = _stmt.getLong(_columnIndexOfFechaRevision)
          }
          val _tmpMotivoCierre: String
          _tmpMotivoCierre = _stmt.getText(_columnIndexOfMotivoCierre)
          val _tmpVerificacionVaciaPor: Long?
          if (_stmt.isNull(_columnIndexOfVerificacionVaciaPor)) {
            _tmpVerificacionVaciaPor = null
          } else {
            _tmpVerificacionVaciaPor = _stmt.getLong(_columnIndexOfVerificacionVaciaPor)
          }
          val _tmpFechaVerificacionVacia: Long?
          if (_stmt.isNull(_columnIndexOfFechaVerificacionVacia)) {
            _tmpFechaVerificacionVacia = null
          } else {
            _tmpFechaVerificacionVacia = _stmt.getLong(_columnIndexOfFechaVerificacionVacia)
          }
          val _tmpCampaniaId: Long?
          if (_stmt.isNull(_columnIndexOfCampaniaId)) {
            _tmpCampaniaId = null
          } else {
            _tmpCampaniaId = _stmt.getLong(_columnIndexOfCampaniaId)
          }
          _result =
              InventarioTienda(_tmpId,_tmpSucursalId,_tmpNombre,_tmpEstado,_tmpFechaApertura,_tmpCerradoPor,_tmpFechaCierre,_tmpEstadoRevision,_tmpRevisadoPor,_tmpFechaRevision,_tmpMotivoCierre,_tmpVerificacionVaciaPor,_tmpFechaVerificacionVacia,_tmpCampaniaId)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun inventarioAbierto(sid: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM inventarios_tienda WHERE sucursalId=? AND estado='ABIERTO'"
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

  public override suspend fun buscarInventario(id: Long): InventarioTienda? {
    val _sql: String = "SELECT * FROM inventarios_tienda WHERE id=? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _columnIndexOfEstado: Int = getColumnIndexOrThrow(_stmt, "estado")
        val _columnIndexOfFechaApertura: Int = getColumnIndexOrThrow(_stmt, "fechaApertura")
        val _columnIndexOfCerradoPor: Int = getColumnIndexOrThrow(_stmt, "cerradoPor")
        val _columnIndexOfFechaCierre: Int = getColumnIndexOrThrow(_stmt, "fechaCierre")
        val _columnIndexOfEstadoRevision: Int = getColumnIndexOrThrow(_stmt, "estadoRevision")
        val _columnIndexOfRevisadoPor: Int = getColumnIndexOrThrow(_stmt, "revisadoPor")
        val _columnIndexOfFechaRevision: Int = getColumnIndexOrThrow(_stmt, "fechaRevision")
        val _columnIndexOfMotivoCierre: Int = getColumnIndexOrThrow(_stmt, "motivoCierre")
        val _columnIndexOfVerificacionVaciaPor: Int = getColumnIndexOrThrow(_stmt,
            "verificacionVaciaPor")
        val _columnIndexOfFechaVerificacionVacia: Int = getColumnIndexOrThrow(_stmt,
            "fechaVerificacionVacia")
        val _columnIndexOfCampaniaId: Int = getColumnIndexOrThrow(_stmt, "campaniaId")
        val _result: InventarioTienda?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpNombre: String
          _tmpNombre = _stmt.getText(_columnIndexOfNombre)
          val _tmpEstado: String
          _tmpEstado = _stmt.getText(_columnIndexOfEstado)
          val _tmpFechaApertura: Long
          _tmpFechaApertura = _stmt.getLong(_columnIndexOfFechaApertura)
          val _tmpCerradoPor: Long?
          if (_stmt.isNull(_columnIndexOfCerradoPor)) {
            _tmpCerradoPor = null
          } else {
            _tmpCerradoPor = _stmt.getLong(_columnIndexOfCerradoPor)
          }
          val _tmpFechaCierre: Long?
          if (_stmt.isNull(_columnIndexOfFechaCierre)) {
            _tmpFechaCierre = null
          } else {
            _tmpFechaCierre = _stmt.getLong(_columnIndexOfFechaCierre)
          }
          val _tmpEstadoRevision: String
          _tmpEstadoRevision = _stmt.getText(_columnIndexOfEstadoRevision)
          val _tmpRevisadoPor: Long?
          if (_stmt.isNull(_columnIndexOfRevisadoPor)) {
            _tmpRevisadoPor = null
          } else {
            _tmpRevisadoPor = _stmt.getLong(_columnIndexOfRevisadoPor)
          }
          val _tmpFechaRevision: Long?
          if (_stmt.isNull(_columnIndexOfFechaRevision)) {
            _tmpFechaRevision = null
          } else {
            _tmpFechaRevision = _stmt.getLong(_columnIndexOfFechaRevision)
          }
          val _tmpMotivoCierre: String
          _tmpMotivoCierre = _stmt.getText(_columnIndexOfMotivoCierre)
          val _tmpVerificacionVaciaPor: Long?
          if (_stmt.isNull(_columnIndexOfVerificacionVaciaPor)) {
            _tmpVerificacionVaciaPor = null
          } else {
            _tmpVerificacionVaciaPor = _stmt.getLong(_columnIndexOfVerificacionVaciaPor)
          }
          val _tmpFechaVerificacionVacia: Long?
          if (_stmt.isNull(_columnIndexOfFechaVerificacionVacia)) {
            _tmpFechaVerificacionVacia = null
          } else {
            _tmpFechaVerificacionVacia = _stmt.getLong(_columnIndexOfFechaVerificacionVacia)
          }
          val _tmpCampaniaId: Long?
          if (_stmt.isNull(_columnIndexOfCampaniaId)) {
            _tmpCampaniaId = null
          } else {
            _tmpCampaniaId = _stmt.getLong(_columnIndexOfCampaniaId)
          }
          _result =
              InventarioTienda(_tmpId,_tmpSucursalId,_tmpNombre,_tmpEstado,_tmpFechaApertura,_tmpCerradoPor,_tmpFechaCierre,_tmpEstadoRevision,_tmpRevisadoPor,_tmpFechaRevision,_tmpMotivoCierre,_tmpVerificacionVaciaPor,_tmpFechaVerificacionVacia,_tmpCampaniaId)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun lineasFlujo(id: Long): Flow<List<LineaInventarioTienda>> {
    val _sql: String =
        "SELECT * FROM lineas_inventario_tienda WHERE inventarioId=? ORDER BY posicionId, productoId"
    return createFlow(__db, false, arrayOf("lineas_inventario_tienda")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfInventarioId: Int = getColumnIndexOrThrow(_stmt, "inventarioId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfPosicionId: Int = getColumnIndexOrThrow(_stmt, "posicionId")
        val _columnIndexOfStockInicio: Int = getColumnIndexOrThrow(_stmt, "stockInicio")
        val _columnIndexOfCantidadContada: Int = getColumnIndexOrThrow(_stmt, "cantidadContada")
        val _columnIndexOfContadoPor: Int = getColumnIndexOrThrow(_stmt, "contadoPor")
        val _columnIndexOfFechaConteo: Int = getColumnIndexOrThrow(_stmt, "fechaConteo")
        val _columnIndexOfCodigoProductoCierre: Int = getColumnIndexOrThrow(_stmt,
            "codigoProductoCierre")
        val _columnIndexOfNombreProductoCierre: Int = getColumnIndexOrThrow(_stmt,
            "nombreProductoCierre")
        val _columnIndexOfRutaUbicacionCierre: Int = getColumnIndexOrThrow(_stmt,
            "rutaUbicacionCierre")
        val _result: MutableList<LineaInventarioTienda> = mutableListOf()
        while (_stmt.step()) {
          val _item: LineaInventarioTienda
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpInventarioId: Long
          _tmpInventarioId = _stmt.getLong(_columnIndexOfInventarioId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpPosicionId: Long
          _tmpPosicionId = _stmt.getLong(_columnIndexOfPosicionId)
          val _tmpStockInicio: Int
          _tmpStockInicio = _stmt.getLong(_columnIndexOfStockInicio).toInt()
          val _tmpCantidadContada: Int?
          if (_stmt.isNull(_columnIndexOfCantidadContada)) {
            _tmpCantidadContada = null
          } else {
            _tmpCantidadContada = _stmt.getLong(_columnIndexOfCantidadContada).toInt()
          }
          val _tmpContadoPor: Long?
          if (_stmt.isNull(_columnIndexOfContadoPor)) {
            _tmpContadoPor = null
          } else {
            _tmpContadoPor = _stmt.getLong(_columnIndexOfContadoPor)
          }
          val _tmpFechaConteo: Long?
          if (_stmt.isNull(_columnIndexOfFechaConteo)) {
            _tmpFechaConteo = null
          } else {
            _tmpFechaConteo = _stmt.getLong(_columnIndexOfFechaConteo)
          }
          val _tmpCodigoProductoCierre: String?
          if (_stmt.isNull(_columnIndexOfCodigoProductoCierre)) {
            _tmpCodigoProductoCierre = null
          } else {
            _tmpCodigoProductoCierre = _stmt.getText(_columnIndexOfCodigoProductoCierre)
          }
          val _tmpNombreProductoCierre: String?
          if (_stmt.isNull(_columnIndexOfNombreProductoCierre)) {
            _tmpNombreProductoCierre = null
          } else {
            _tmpNombreProductoCierre = _stmt.getText(_columnIndexOfNombreProductoCierre)
          }
          val _tmpRutaUbicacionCierre: String?
          if (_stmt.isNull(_columnIndexOfRutaUbicacionCierre)) {
            _tmpRutaUbicacionCierre = null
          } else {
            _tmpRutaUbicacionCierre = _stmt.getText(_columnIndexOfRutaUbicacionCierre)
          }
          _item =
              LineaInventarioTienda(_tmpId,_tmpInventarioId,_tmpProductoId,_tmpPosicionId,_tmpStockInicio,_tmpCantidadContada,_tmpContadoPor,_tmpFechaConteo,_tmpCodigoProductoCierre,_tmpNombreProductoCierre,_tmpRutaUbicacionCierre)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun lineasInventario(id: Long): List<LineaInventarioTienda> {
    val _sql: String =
        "SELECT * FROM lineas_inventario_tienda WHERE inventarioId=? ORDER BY posicionId, productoId"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfInventarioId: Int = getColumnIndexOrThrow(_stmt, "inventarioId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfPosicionId: Int = getColumnIndexOrThrow(_stmt, "posicionId")
        val _columnIndexOfStockInicio: Int = getColumnIndexOrThrow(_stmt, "stockInicio")
        val _columnIndexOfCantidadContada: Int = getColumnIndexOrThrow(_stmt, "cantidadContada")
        val _columnIndexOfContadoPor: Int = getColumnIndexOrThrow(_stmt, "contadoPor")
        val _columnIndexOfFechaConteo: Int = getColumnIndexOrThrow(_stmt, "fechaConteo")
        val _columnIndexOfCodigoProductoCierre: Int = getColumnIndexOrThrow(_stmt,
            "codigoProductoCierre")
        val _columnIndexOfNombreProductoCierre: Int = getColumnIndexOrThrow(_stmt,
            "nombreProductoCierre")
        val _columnIndexOfRutaUbicacionCierre: Int = getColumnIndexOrThrow(_stmt,
            "rutaUbicacionCierre")
        val _result: MutableList<LineaInventarioTienda> = mutableListOf()
        while (_stmt.step()) {
          val _item: LineaInventarioTienda
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpInventarioId: Long
          _tmpInventarioId = _stmt.getLong(_columnIndexOfInventarioId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpPosicionId: Long
          _tmpPosicionId = _stmt.getLong(_columnIndexOfPosicionId)
          val _tmpStockInicio: Int
          _tmpStockInicio = _stmt.getLong(_columnIndexOfStockInicio).toInt()
          val _tmpCantidadContada: Int?
          if (_stmt.isNull(_columnIndexOfCantidadContada)) {
            _tmpCantidadContada = null
          } else {
            _tmpCantidadContada = _stmt.getLong(_columnIndexOfCantidadContada).toInt()
          }
          val _tmpContadoPor: Long?
          if (_stmt.isNull(_columnIndexOfContadoPor)) {
            _tmpContadoPor = null
          } else {
            _tmpContadoPor = _stmt.getLong(_columnIndexOfContadoPor)
          }
          val _tmpFechaConteo: Long?
          if (_stmt.isNull(_columnIndexOfFechaConteo)) {
            _tmpFechaConteo = null
          } else {
            _tmpFechaConteo = _stmt.getLong(_columnIndexOfFechaConteo)
          }
          val _tmpCodigoProductoCierre: String?
          if (_stmt.isNull(_columnIndexOfCodigoProductoCierre)) {
            _tmpCodigoProductoCierre = null
          } else {
            _tmpCodigoProductoCierre = _stmt.getText(_columnIndexOfCodigoProductoCierre)
          }
          val _tmpNombreProductoCierre: String?
          if (_stmt.isNull(_columnIndexOfNombreProductoCierre)) {
            _tmpNombreProductoCierre = null
          } else {
            _tmpNombreProductoCierre = _stmt.getText(_columnIndexOfNombreProductoCierre)
          }
          val _tmpRutaUbicacionCierre: String?
          if (_stmt.isNull(_columnIndexOfRutaUbicacionCierre)) {
            _tmpRutaUbicacionCierre = null
          } else {
            _tmpRutaUbicacionCierre = _stmt.getText(_columnIndexOfRutaUbicacionCierre)
          }
          _item =
              LineaInventarioTienda(_tmpId,_tmpInventarioId,_tmpProductoId,_tmpPosicionId,_tmpStockInicio,_tmpCantidadContada,_tmpContadoPor,_tmpFechaConteo,_tmpCodigoProductoCierre,_tmpNombreProductoCierre,_tmpRutaUbicacionCierre)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun lineaExiste(
    inventario: Long,
    pid: Long,
    pos: Long,
  ): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM lineas_inventario_tienda WHERE inventarioId=? AND productoId=? AND posicionId=?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, inventario)
        _argIndex = 2
        _stmt.bindLong(_argIndex, pid)
        _argIndex = 3
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

  public override suspend fun asignarZonaTienda(
    inv: Long,
    pos: Long,
    uid: Long,
  ): Int {
    val _sql: String =
        "UPDATE zonas_conteo_tienda SET asignadoA=? WHERE inventarioId=? AND posicionId=? AND asignadoA=0 AND confirmadoPor IS NULL"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, uid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, inv)
        _argIndex = 3
        _stmt.bindLong(_argIndex, pos)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun confirmarZonaTienda(
    inv: Long,
    pos: Long,
    uid: Long,
    fecha: Long,
  ): Int {
    val _sql: String = """
        |UPDATE zonas_conteo_tienda SET confirmadoPor=?,fechaConfirmacion=?
        |        WHERE inventarioId=? AND posicionId=? AND asignadoA=? AND confirmadoPor IS NULL
        |        AND EXISTS(SELECT 1 FROM inventarios_tienda i WHERE i.id=? AND i.estado='ABIERTO')
        |        AND NOT EXISTS(SELECT 1 FROM lineas_inventario_tienda l WHERE l.inventarioId=?
        |            AND l.posicionId=? AND l.cantidadContada IS NULL)
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, uid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, fecha)
        _argIndex = 3
        _stmt.bindLong(_argIndex, inv)
        _argIndex = 4
        _stmt.bindLong(_argIndex, pos)
        _argIndex = 5
        _stmt.bindLong(_argIndex, uid)
        _argIndex = 6
        _stmt.bindLong(_argIndex, inv)
        _argIndex = 7
        _stmt.bindLong(_argIndex, inv)
        _argIndex = 8
        _stmt.bindLong(_argIndex, pos)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun reabrirZonaTienda(inv: Long, pos: Long): Int {
    val _sql: String =
        "UPDATE zonas_conteo_tienda SET confirmadoPor=NULL,fechaConfirmacion=NULL WHERE inventarioId=? AND posicionId=? AND confirmadoPor IS NOT NULL"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, inv)
        _argIndex = 2
        _stmt.bindLong(_argIndex, pos)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun reiniciarConteosZonaTienda(inv: Long, pos: Long): Int {
    val _sql: String =
        "UPDATE lineas_inventario_tienda SET cantidadContada=NULL,contadoPor=NULL,fechaConteo=NULL WHERE inventarioId=? AND posicionId=?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, inv)
        _argIndex = 2
        _stmt.bindLong(_argIndex, pos)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun confirmarTiendaVacia(
    id: Long,
    sid: Long,
    uid: Long,
    fecha: Long,
  ): Int {
    val _sql: String = """
        |UPDATE inventarios_tienda SET verificacionVaciaPor=?,fechaVerificacionVacia=?
        |        WHERE id=? AND sucursalId=? AND estado='ABIERTO'
        |        AND NOT EXISTS(SELECT 1 FROM lineas_inventario_tienda l WHERE l.inventarioId=?)
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
        _stmt.bindLong(_argIndex, sid)
        _argIndex = 5
        _stmt.bindLong(_argIndex, id)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun descontarTotal(
    sid: Long,
    pid: Long,
    cantidad: Int,
  ): Int {
    val _sql: String =
        "UPDATE existencias_tienda SET cantidad=cantidad-? WHERE sucursalId=? AND productoId=? AND cantidad>=? AND ?>0"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, cantidad.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, sid)
        _argIndex = 3
        _stmt.bindLong(_argIndex, pid)
        _argIndex = 4
        _stmt.bindLong(_argIndex, cantidad.toLong())
        _argIndex = 5
        _stmt.bindLong(_argIndex, cantidad.toLong())
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun ajustarTotal(
    sid: Long,
    pid: Long,
    delta: Int,
  ): Int {
    val _sql: String =
        "UPDATE existencias_tienda SET cantidad=cantidad+? WHERE sucursalId=? AND productoId=? AND cantidad+?>=0"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, delta.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, sid)
        _argIndex = 3
        _stmt.bindLong(_argIndex, pid)
        _argIndex = 4
        _stmt.bindLong(_argIndex, delta.toLong())
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun establecerUbicacion(id: Long, cantidad: Int): Int {
    val _sql: String =
        "UPDATE existencias_ubicacion_tienda SET cantidad=? WHERE id=? AND cantidad>=0 AND ?>=0"
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

  public override suspend fun guardarInstantanea(
    id: Long,
    inventario: Long,
    codigo: String,
    nombre: String,
    ruta: String,
  ): Int {
    val _sql: String =
        "UPDATE lineas_inventario_tienda SET codigoProductoCierre=?,nombreProductoCierre=?,rutaUbicacionCierre=? WHERE id=? AND inventarioId=?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, codigo)
        _argIndex = 2
        _stmt.bindText(_argIndex, nombre)
        _argIndex = 3
        _stmt.bindText(_argIndex, ruta)
        _argIndex = 4
        _stmt.bindLong(_argIndex, id)
        _argIndex = 5
        _stmt.bindLong(_argIndex, inventario)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun anotarConteo(
    id: Long,
    inventario: Long,
    cantidad: Int,
    uid: Long,
    fecha: Long,
  ): Int {
    val _sql: String =
        "UPDATE lineas_inventario_tienda SET cantidadContada=?,contadoPor=?,fechaConteo=? WHERE id=? AND inventarioId=? AND EXISTS (SELECT 1 FROM inventarios_tienda i WHERE i.id=? AND i.estado='ABIERTO')"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, cantidad.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, uid)
        _argIndex = 3
        _stmt.bindLong(_argIndex, fecha)
        _argIndex = 4
        _stmt.bindLong(_argIndex, id)
        _argIndex = 5
        _stmt.bindLong(_argIndex, inventario)
        _argIndex = 6
        _stmt.bindLong(_argIndex, inventario)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun cerrarInventario(
    id: Long,
    sid: Long,
    uid: Long,
    fecha: Long,
    motivo: String,
  ): Int {
    val _sql: String =
        "UPDATE inventarios_tienda SET estado='CERRADO',cerradoPor=?,fechaCierre=?,motivoCierre=? WHERE id=? AND sucursalId=? AND estado='ABIERTO'"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, uid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, fecha)
        _argIndex = 3
        _stmt.bindText(_argIndex, motivo)
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

  public override suspend fun cancelarInventario(
    id: Long,
    sid: Long,
    uid: Long,
    fecha: Long,
    motivo: String,
  ): Int {
    val _sql: String =
        "UPDATE inventarios_tienda SET estado='CANCELADO',cerradoPor=?,fechaCierre=?,motivoCierre=? WHERE id=? AND sucursalId=? AND estado='ABIERTO'"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, uid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, fecha)
        _argIndex = 3
        _stmt.bindText(_argIndex, motivo)
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

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
