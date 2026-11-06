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
public class ProductosDao_Impl(
  __db: RoomDatabase,
) : ProductosDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfSucursal: EntityInsertAdapter<Sucursal>

  private val __insertAdapterOfBodega: EntityInsertAdapter<Bodega>

  private val __insertAdapterOfPosicion: EntityInsertAdapter<Posicion>

  private val __insertAdapterOfPosicion_1: EntityInsertAdapter<Posicion>

  private val __insertAdapterOfProducto: EntityInsertAdapter<Producto>

  private val __insertAdapterOfExistencia: EntityInsertAdapter<Existencia>

  private val __insertAdapterOfMovimiento: EntityInsertAdapter<Movimiento>

  private val __insertAdapterOfEntregaAutorizada: EntityInsertAdapter<EntregaAutorizada>
  init {
    this.__db = __db
    this.__insertAdapterOfSucursal = object : EntityInsertAdapter<Sucursal>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `sucursales` (`id`,`nombre`) VALUES (?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Sucursal) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.nombre)
      }
    }
    this.__insertAdapterOfBodega = object : EntityInsertAdapter<Bodega>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `bodegas` (`id`,`sucursalId`,`nombre`,`activa`) VALUES (?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Bodega) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.sucursalId)
        statement.bindText(3, entity.nombre)
        val _tmp: Int = if (entity.activa) 1 else 0
        statement.bindLong(4, _tmp.toLong())
      }
    }
    this.__insertAdapterOfPosicion = object : EntityInsertAdapter<Posicion>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `posiciones` (`id`,`bodegaId`,`codigo`,`nombre`,`tipo`,`padreId`,`activa`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Posicion) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.bodegaId)
        statement.bindText(3, entity.codigo)
        statement.bindText(4, entity.nombre)
        statement.bindText(5, entity.tipo)
        val _tmpPadreId: Long? = entity.padreId
        if (_tmpPadreId == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpPadreId)
        }
        val _tmp: Int = if (entity.activa) 1 else 0
        statement.bindLong(7, _tmp.toLong())
      }
    }
    this.__insertAdapterOfPosicion_1 = object : EntityInsertAdapter<Posicion>() {
      protected override fun createQuery(): String =
          "INSERT OR IGNORE INTO `posiciones` (`id`,`bodegaId`,`codigo`,`nombre`,`tipo`,`padreId`,`activa`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Posicion) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.bodegaId)
        statement.bindText(3, entity.codigo)
        statement.bindText(4, entity.nombre)
        statement.bindText(5, entity.tipo)
        val _tmpPadreId: Long? = entity.padreId
        if (_tmpPadreId == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpPadreId)
        }
        val _tmp: Int = if (entity.activa) 1 else 0
        statement.bindLong(7, _tmp.toLong())
      }
    }
    this.__insertAdapterOfProducto = object : EntityInsertAdapter<Producto>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `productos` (`id`,`codigo`,`nombre`,`categoria`,`minimo`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Producto) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.codigo)
        statement.bindText(3, entity.nombre)
        statement.bindText(4, entity.categoria)
        statement.bindLong(5, entity.minimo.toLong())
      }
    }
    this.__insertAdapterOfExistencia = object : EntityInsertAdapter<Existencia>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `existencias` (`id`,`productoId`,`posicionId`,`cantidad`) VALUES (nullif(?, 0),?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Existencia) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.productoId)
        statement.bindLong(3, entity.posicionId)
        statement.bindLong(4, entity.cantidad.toLong())
      }
    }
    this.__insertAdapterOfMovimiento = object : EntityInsertAdapter<Movimiento>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `movimientos` (`id`,`usuarioId`,`bodegaId`,`productoId`,`posicionOrigen`,`posicionDestino`,`cantidad`,`fecha`,`tipo`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Movimiento) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.usuarioId)
        statement.bindLong(3, entity.bodegaId)
        statement.bindLong(4, entity.productoId)
        statement.bindLong(5, entity.posicionOrigen)
        statement.bindLong(6, entity.posicionDestino)
        statement.bindLong(7, entity.cantidad.toLong())
        statement.bindLong(8, entity.fecha)
        statement.bindText(9, entity.tipo)
      }
    }
    this.__insertAdapterOfEntregaAutorizada = object : EntityInsertAdapter<EntregaAutorizada>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `entregas` (`id`,`bodegaId`,`productoId`,`esperada`,`recibida`,`estado`) VALUES (?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: EntregaAutorizada) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.bodegaId)
        statement.bindLong(3, entity.productoId)
        statement.bindLong(4, entity.esperada.toLong())
        statement.bindLong(5, entity.recibida.toLong())
        statement.bindText(6, entity.estado)
      }
    }
  }

  public override suspend fun guardarSucursal(valor: Sucursal): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfSucursal.insert(_connection, valor)
  }

  public override suspend fun guardarBodega(valor: Bodega): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfBodega.insert(_connection, valor)
  }

  public override suspend fun guardarPosicion(valor: Posicion): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfPosicion.insert(_connection, valor)
  }

  public override suspend fun guardarPosicionSiFalta(valor: Posicion): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfPosicion_1.insertAndReturnId(_connection, valor)
    _result
  }

  public override suspend fun guardarProducto(valor: Producto): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfProducto.insert(_connection, valor)
  }

  public override suspend fun guardarExistencia(valor: Existencia): Long = performSuspending(__db,
      false, true) { _connection ->
    val _result: Long = __insertAdapterOfExistencia.insertAndReturnId(_connection, valor)
    _result
  }

  public override suspend fun guardarMovimiento(valor: Movimiento): Long = performSuspending(__db,
      false, true) { _connection ->
    val _result: Long = __insertAdapterOfMovimiento.insertAndReturnId(_connection, valor)
    _result
  }

  public override suspend fun guardarEntrega(valor: EntregaAutorizada): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfEntregaAutorizada.insert(_connection, valor)
  }

  public override suspend fun numeroProductos(): Int {
    val _sql: String = "SELECT COUNT(*) FROM productos"
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

  public override suspend fun productoPorId(id: Long): Producto? {
    val _sql: String = "SELECT * FROM productos WHERE id=? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCodigo: Int = getColumnIndexOrThrow(_stmt, "codigo")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _columnIndexOfCategoria: Int = getColumnIndexOrThrow(_stmt, "categoria")
        val _columnIndexOfMinimo: Int = getColumnIndexOrThrow(_stmt, "minimo")
        val _result: Producto?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpCodigo: String
          _tmpCodigo = _stmt.getText(_columnIndexOfCodigo)
          val _tmpNombre: String
          _tmpNombre = _stmt.getText(_columnIndexOfNombre)
          val _tmpCategoria: String
          _tmpCategoria = _stmt.getText(_columnIndexOfCategoria)
          val _tmpMinimo: Int
          _tmpMinimo = _stmt.getLong(_columnIndexOfMinimo).toInt()
          _result = Producto(_tmpId,_tmpCodigo,_tmpNombre,_tmpCategoria,_tmpMinimo)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun historicosInventarioBodega(bid: Long): Int {
    val _sql: String = """
        |SELECT (SELECT COUNT(*) FROM inventarios_bodega WHERE bodegaId=?) +
        |        (SELECT COUNT(*) FROM campanias_mensuales WHERE tipo='BODEGA' AND ubicacionId=?)
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, bid)
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

  public override fun todasBodegas(): Flow<List<Bodega>> {
    val _sql: String = "SELECT * FROM bodegas WHERE activa=1 ORDER BY nombre"
    return createFlow(__db, false, arrayOf("bodegas")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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

  public override fun sucursales(): Flow<List<Sucursal>> {
    val _sql: String = "SELECT * FROM sucursales ORDER BY nombre"
    return createFlow(__db, false, arrayOf("sucursales")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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

  public override suspend fun sucursalesActuales(): List<Sucursal> {
    val _sql: String = "SELECT * FROM sucursales ORDER BY id"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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

  public override fun productos(): Flow<List<Producto>> {
    val _sql: String = "SELECT * FROM productos ORDER BY nombre"
    return createFlow(__db, false, arrayOf("productos")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCodigo: Int = getColumnIndexOrThrow(_stmt, "codigo")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _columnIndexOfCategoria: Int = getColumnIndexOrThrow(_stmt, "categoria")
        val _columnIndexOfMinimo: Int = getColumnIndexOrThrow(_stmt, "minimo")
        val _result: MutableList<Producto> = mutableListOf()
        while (_stmt.step()) {
          val _item: Producto
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpCodigo: String
          _tmpCodigo = _stmt.getText(_columnIndexOfCodigo)
          val _tmpNombre: String
          _tmpNombre = _stmt.getText(_columnIndexOfNombre)
          val _tmpCategoria: String
          _tmpCategoria = _stmt.getText(_columnIndexOfCategoria)
          val _tmpMinimo: Int
          _tmpMinimo = _stmt.getLong(_columnIndexOfMinimo).toInt()
          _item = Producto(_tmpId,_tmpCodigo,_tmpNombre,_tmpCategoria,_tmpMinimo)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun posiciones(bid: Long): Flow<List<Posicion>> {
    val _sql: String = "SELECT * FROM posiciones WHERE bodegaId=? ORDER BY codigo"
    return createFlow(__db, false, arrayOf("posiciones")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, bid)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfBodegaId: Int = getColumnIndexOrThrow(_stmt, "bodegaId")
        val _columnIndexOfCodigo: Int = getColumnIndexOrThrow(_stmt, "codigo")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _columnIndexOfTipo: Int = getColumnIndexOrThrow(_stmt, "tipo")
        val _columnIndexOfPadreId: Int = getColumnIndexOrThrow(_stmt, "padreId")
        val _columnIndexOfActiva: Int = getColumnIndexOrThrow(_stmt, "activa")
        val _result: MutableList<Posicion> = mutableListOf()
        while (_stmt.step()) {
          val _item: Posicion
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpBodegaId: Long
          _tmpBodegaId = _stmt.getLong(_columnIndexOfBodegaId)
          val _tmpCodigo: String
          _tmpCodigo = _stmt.getText(_columnIndexOfCodigo)
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
          val _tmpActiva: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfActiva).toInt()
          _tmpActiva = _tmp != 0
          _item =
              Posicion(_tmpId,_tmpBodegaId,_tmpCodigo,_tmpNombre,_tmpTipo,_tmpPadreId,_tmpActiva)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun siguienteIdPosicion(): Long {
    val _sql: String = "SELECT COALESCE(MAX(id),0)+1 FROM posiciones"
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

  public override fun existencias(bid: Long): Flow<List<Existencia>> {
    val _sql: String =
        "SELECT e.* FROM existencias e JOIN posiciones p ON e.posicionId=p.id WHERE p.bodegaId=? ORDER BY e.productoId, e.posicionId"
    return createFlow(__db, false, arrayOf("existencias", "posiciones")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, bid)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfPosicionId: Int = getColumnIndexOrThrow(_stmt, "posicionId")
        val _columnIndexOfCantidad: Int = getColumnIndexOrThrow(_stmt, "cantidad")
        val _result: MutableList<Existencia> = mutableListOf()
        while (_stmt.step()) {
          val _item: Existencia
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpPosicionId: Long
          _tmpPosicionId = _stmt.getLong(_columnIndexOfPosicionId)
          val _tmpCantidad: Int
          _tmpCantidad = _stmt.getLong(_columnIndexOfCantidad).toInt()
          _item = Existencia(_tmpId,_tmpProductoId,_tmpPosicionId,_tmpCantidad)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun movimientos(uid: Long, bid: Long): Flow<List<Movimiento>> {
    val _sql: String =
        "SELECT * FROM movimientos WHERE usuarioId=? AND bodegaId=? ORDER BY fecha DESC"
    return createFlow(__db, false, arrayOf("movimientos")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, uid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, bid)
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

  public override suspend fun posicionPorId(id: Long): Posicion? {
    val _sql: String = "SELECT * FROM posiciones WHERE id=? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfBodegaId: Int = getColumnIndexOrThrow(_stmt, "bodegaId")
        val _columnIndexOfCodigo: Int = getColumnIndexOrThrow(_stmt, "codigo")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _columnIndexOfTipo: Int = getColumnIndexOrThrow(_stmt, "tipo")
        val _columnIndexOfPadreId: Int = getColumnIndexOrThrow(_stmt, "padreId")
        val _columnIndexOfActiva: Int = getColumnIndexOrThrow(_stmt, "activa")
        val _result: Posicion?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpBodegaId: Long
          _tmpBodegaId = _stmt.getLong(_columnIndexOfBodegaId)
          val _tmpCodigo: String
          _tmpCodigo = _stmt.getText(_columnIndexOfCodigo)
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
          val _tmpActiva: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfActiva).toInt()
          _tmpActiva = _tmp != 0
          _result =
              Posicion(_tmpId,_tmpBodegaId,_tmpCodigo,_tmpNombre,_tmpTipo,_tmpPadreId,_tmpActiva)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun existencia(pid: Long, pos: Long): Existencia? {
    val _sql: String = "SELECT * FROM existencias WHERE productoId=? AND posicionId=? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, pid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, pos)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfPosicionId: Int = getColumnIndexOrThrow(_stmt, "posicionId")
        val _columnIndexOfCantidad: Int = getColumnIndexOrThrow(_stmt, "cantidad")
        val _result: Existencia?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpPosicionId: Long
          _tmpPosicionId = _stmt.getLong(_columnIndexOfPosicionId)
          val _tmpCantidad: Int
          _tmpCantidad = _stmt.getLong(_columnIndexOfCantidad).toInt()
          _result = Existencia(_tmpId,_tmpProductoId,_tmpPosicionId,_tmpCantidad)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun existeBodega(bid: Long): Int {
    val _sql: String = "SELECT COUNT(*) FROM bodegas WHERE id=? AND activa=1"
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

  public override suspend fun posicionesActuales(bid: Long): List<Posicion> {
    val _sql: String = "SELECT * FROM posiciones WHERE bodegaId=? ORDER BY codigo"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, bid)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfBodegaId: Int = getColumnIndexOrThrow(_stmt, "bodegaId")
        val _columnIndexOfCodigo: Int = getColumnIndexOrThrow(_stmt, "codigo")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _columnIndexOfTipo: Int = getColumnIndexOrThrow(_stmt, "tipo")
        val _columnIndexOfPadreId: Int = getColumnIndexOrThrow(_stmt, "padreId")
        val _columnIndexOfActiva: Int = getColumnIndexOrThrow(_stmt, "activa")
        val _result: MutableList<Posicion> = mutableListOf()
        while (_stmt.step()) {
          val _item: Posicion
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpBodegaId: Long
          _tmpBodegaId = _stmt.getLong(_columnIndexOfBodegaId)
          val _tmpCodigo: String
          _tmpCodigo = _stmt.getText(_columnIndexOfCodigo)
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
          val _tmpActiva: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfActiva).toInt()
          _tmpActiva = _tmp != 0
          _item =
              Posicion(_tmpId,_tmpBodegaId,_tmpCodigo,_tmpNombre,_tmpTipo,_tmpPadreId,_tmpActiva)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun existenciasActuales(bid: Long): List<Existencia> {
    val _sql: String =
        "SELECT e.* FROM existencias e JOIN posiciones p ON p.id=e.posicionId WHERE p.bodegaId=?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, bid)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfPosicionId: Int = getColumnIndexOrThrow(_stmt, "posicionId")
        val _columnIndexOfCantidad: Int = getColumnIndexOrThrow(_stmt, "cantidad")
        val _result: MutableList<Existencia> = mutableListOf()
        while (_stmt.step()) {
          val _item: Existencia
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpPosicionId: Long
          _tmpPosicionId = _stmt.getLong(_columnIndexOfPosicionId)
          val _tmpCantidad: Int
          _tmpCantidad = _stmt.getLong(_columnIndexOfCantidad).toInt()
          _item = Existencia(_tmpId,_tmpProductoId,_tmpPosicionId,_tmpCantidad)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun categoriasProductos(): List<String> {
    val _sql: String = "SELECT DISTINCT categoria FROM productos ORDER BY categoria"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: MutableList<String> = mutableListOf()
        while (_stmt.step()) {
          val _item: String
          _item = _stmt.getText(0)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun mejorarEstanteDemoBodega(
    id: Long,
    bid: Long,
    nombreAntiguo: String,
    codigo: String,
    nombre: String,
    padreId: Long,
  ): Int {
    val _sql: String = """
        |UPDATE posiciones SET codigo=?, nombre=?, padreId=?
        |        WHERE id=? AND bodegaId=? AND codigo=?
        |          AND (nombre='' OR nombre=?) AND tipo='ESTANTERIA'
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, codigo)
        _argIndex = 2
        _stmt.bindText(_argIndex, nombre)
        _argIndex = 3
        _stmt.bindLong(_argIndex, padreId)
        _argIndex = 4
        _stmt.bindLong(_argIndex, id)
        _argIndex = 5
        _stmt.bindLong(_argIndex, bid)
        _argIndex = 6
        _stmt.bindText(_argIndex, nombreAntiguo)
        _argIndex = 7
        _stmt.bindText(_argIndex, nombreAntiguo)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun renombrarPosicion(
    id: Long,
    bid: Long,
    nombre: String,
  ): Int {
    val _sql: String = "UPDATE posiciones SET nombre=? WHERE id=? AND bodegaId=?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, nombre)
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

  public override suspend fun estadoPosicion(
    id: Long,
    bid: Long,
    activa: Boolean,
  ): Int {
    val _sql: String = "UPDATE posiciones SET activa=? WHERE id=? AND bodegaId=?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        val _tmp: Int = if (activa) 1 else 0
        _stmt.bindLong(_argIndex, _tmp.toLong())
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

  public override suspend fun restarSiAlcanza(id: Long, cantidad: Int): Int {
    val _sql: String = "UPDATE existencias SET cantidad=cantidad-? WHERE id=? AND cantidad>=?"
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

  public override suspend fun sumar(id: Long, cantidad: Int) {
    val _sql: String = "UPDATE existencias SET cantidad=cantidad+? WHERE id=?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, cantidad.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
