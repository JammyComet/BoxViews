package com.example.boxview.modelo

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
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
public class PreciosVentaDao_Impl(
  __db: RoomDatabase,
) : PreciosVentaDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfPrecioVentaSucursal: EntityInsertAdapter<PrecioVentaSucursal>

  private val __insertAdapterOfPrecioVentaSucursal_1: EntityInsertAdapter<PrecioVentaSucursal>
  init {
    this.__db = __db
    this.__insertAdapterOfPrecioVentaSucursal = object : EntityInsertAdapter<PrecioVentaSucursal>()
        {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `precios_venta_sucursal` (`sucursalId`,`productoId`,`precioClp`,`actualizadoPor`,`fechaActualizacion`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: PrecioVentaSucursal) {
        statement.bindLong(1, entity.sucursalId)
        statement.bindLong(2, entity.productoId)
        statement.bindLong(3, entity.precioClp)
        statement.bindLong(4, entity.actualizadoPor)
        statement.bindLong(5, entity.fechaActualizacion)
      }
    }
    this.__insertAdapterOfPrecioVentaSucursal_1 = object :
        EntityInsertAdapter<PrecioVentaSucursal>() {
      protected override fun createQuery(): String =
          "INSERT OR IGNORE INTO `precios_venta_sucursal` (`sucursalId`,`productoId`,`precioClp`,`actualizadoPor`,`fechaActualizacion`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: PrecioVentaSucursal) {
        statement.bindLong(1, entity.sucursalId)
        statement.bindLong(2, entity.productoId)
        statement.bindLong(3, entity.precioClp)
        statement.bindLong(4, entity.actualizadoPor)
        statement.bindLong(5, entity.fechaActualizacion)
      }
    }
  }

  public override suspend fun guardarPrecio(precio: PrecioVentaSucursal): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfPrecioVentaSucursal.insert(_connection, precio)
  }

  public override suspend fun guardarPrecioSiFalta(precio: PrecioVentaSucursal): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfPrecioVentaSucursal_1.insertAndReturnId(_connection,
        precio)
    _result
  }

  public override fun preciosSucursal(sid: Long): Flow<List<PrecioVentaSucursal>> {
    val _sql: String = "SELECT * FROM precios_venta_sucursal WHERE sucursalId=? ORDER BY productoId"
    return createFlow(__db, false, arrayOf("precios_venta_sucursal")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfPrecioClp: Int = getColumnIndexOrThrow(_stmt, "precioClp")
        val _columnIndexOfActualizadoPor: Int = getColumnIndexOrThrow(_stmt, "actualizadoPor")
        val _columnIndexOfFechaActualizacion: Int = getColumnIndexOrThrow(_stmt,
            "fechaActualizacion")
        val _result: MutableList<PrecioVentaSucursal> = mutableListOf()
        while (_stmt.step()) {
          val _item: PrecioVentaSucursal
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpPrecioClp: Long
          _tmpPrecioClp = _stmt.getLong(_columnIndexOfPrecioClp)
          val _tmpActualizadoPor: Long
          _tmpActualizadoPor = _stmt.getLong(_columnIndexOfActualizadoPor)
          val _tmpFechaActualizacion: Long
          _tmpFechaActualizacion = _stmt.getLong(_columnIndexOfFechaActualizacion)
          _item =
              PrecioVentaSucursal(_tmpSucursalId,_tmpProductoId,_tmpPrecioClp,_tmpActualizadoPor,_tmpFechaActualizacion)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun precioActual(sid: Long, pid: Long): PrecioVentaSucursal? {
    val _sql: String =
        "SELECT * FROM precios_venta_sucursal WHERE sucursalId=? AND productoId=? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, pid)
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfPrecioClp: Int = getColumnIndexOrThrow(_stmt, "precioClp")
        val _columnIndexOfActualizadoPor: Int = getColumnIndexOrThrow(_stmt, "actualizadoPor")
        val _columnIndexOfFechaActualizacion: Int = getColumnIndexOrThrow(_stmt,
            "fechaActualizacion")
        val _result: PrecioVentaSucursal?
        if (_stmt.step()) {
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpPrecioClp: Long
          _tmpPrecioClp = _stmt.getLong(_columnIndexOfPrecioClp)
          val _tmpActualizadoPor: Long
          _tmpActualizadoPor = _stmt.getLong(_columnIndexOfActualizadoPor)
          val _tmpFechaActualizacion: Long
          _tmpFechaActualizacion = _stmt.getLong(_columnIndexOfFechaActualizacion)
          _result =
              PrecioVentaSucursal(_tmpSucursalId,_tmpProductoId,_tmpPrecioClp,_tmpActualizadoPor,_tmpFechaActualizacion)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun resumenVentasGerencia(uid: Long): Flow<List<ResumenVentasSucursal>> {
    val _sql: String = """
        |SELECT s.id AS sucursalId, s.nombre AS sucursal,
        |        COALESCE(SUM(CASE WHEN v.tipo='VENTA' AND v.totalVentaClp IS NOT NULL THEN v.cantidad ELSE 0 END),0) AS unidadesVendidas,
        |        COALESCE(SUM(CASE WHEN v.tipo='VENTA' AND v.totalVentaClp IS NOT NULL THEN 1 ELSE 0 END),0) AS ventasValoradas,
        |        COALESCE(SUM(CASE WHEN v.tipo='VENTA' AND v.totalVentaClp IS NOT NULL THEN v.totalVentaClp ELSE 0 END),0) AS ingresoVentaClp,
        |        COALESCE(SUM(CASE WHEN v.tipo='VENTA' AND v.totalVentaClp IS NULL THEN 1 ELSE 0 END),0) AS ventasAnterioresSinValor
        |        FROM sucursales s LEFT JOIN salidas_tienda v ON v.sucursalId=s.id
        |        WHERE EXISTS(SELECT 1 FROM usuarios u WHERE u.id=? AND u.rol='GERENTE' AND u.activo=1)
        |        GROUP BY s.id,s.nombre ORDER BY s.nombre
        """.trimMargin()
    return createFlow(__db, false, arrayOf("sucursales", "salidas_tienda", "usuarios")) {
        _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, uid)
        val _columnIndexOfSucursalId: Int = 0
        val _columnIndexOfSucursal: Int = 1
        val _columnIndexOfUnidadesVendidas: Int = 2
        val _columnIndexOfVentasValoradas: Int = 3
        val _columnIndexOfIngresoVentaClp: Int = 4
        val _columnIndexOfVentasAnterioresSinValor: Int = 5
        val _result: MutableList<ResumenVentasSucursal> = mutableListOf()
        while (_stmt.step()) {
          val _item: ResumenVentasSucursal
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpSucursal: String
          _tmpSucursal = _stmt.getText(_columnIndexOfSucursal)
          val _tmpUnidadesVendidas: Long
          _tmpUnidadesVendidas = _stmt.getLong(_columnIndexOfUnidadesVendidas)
          val _tmpVentasValoradas: Long
          _tmpVentasValoradas = _stmt.getLong(_columnIndexOfVentasValoradas)
          val _tmpIngresoVentaClp: Long
          _tmpIngresoVentaClp = _stmt.getLong(_columnIndexOfIngresoVentaClp)
          val _tmpVentasAnterioresSinValor: Long
          _tmpVentasAnterioresSinValor = _stmt.getLong(_columnIndexOfVentasAnterioresSinValor)
          _item =
              ResumenVentasSucursal(_tmpSucursalId,_tmpSucursal,_tmpUnidadesVendidas,_tmpVentasValoradas,_tmpIngresoVentaClp,_tmpVentasAnterioresSinValor)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun ventasValorizadasGerencia(uid: Long): List<SalidaTienda> {
    val _sql: String = """
        |SELECT v.* FROM salidas_tienda v WHERE v.tipo='VENTA' AND v.totalVentaClp IS NOT NULL
        |        AND EXISTS(SELECT 1 FROM usuarios u WHERE u.id=? AND u.rol='GERENTE' AND u.activo=1)
        |        ORDER BY v.fecha DESC,v.id DESC
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, uid)
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

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
