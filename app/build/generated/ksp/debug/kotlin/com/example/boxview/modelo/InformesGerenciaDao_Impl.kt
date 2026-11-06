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
public class InformesGerenciaDao_Impl(
  __db: RoomDatabase,
) : InformesGerenciaDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfInformeInventario: EntityInsertAdapter<InformeInventario>

  private val __insertAdapterOfDetalleInformeInventario:
      EntityInsertAdapter<DetalleInformeInventario>
  init {
    this.__db = __db
    this.__insertAdapterOfInformeInventario = object : EntityInsertAdapter<InformeInventario>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `informes_inventario` (`id`,`inventarioId`,`sucursalId`,`bodegaId`,`sucursalNombre`,`bodegaNombre`,`nombre`,`estadoCierre`,`fechaApertura`,`fechaCierre`,`cerradoPor`,`posiciones`,`productosContados`,`productosSinContar`,`lineasConDiferencia`,`unidadesSistema`,`unidadesContadas`,`ajustesAplicados`,`estadoRevision`,`revisadoPor`,`fechaRevision`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: InformeInventario) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.inventarioId)
        statement.bindLong(3, entity.sucursalId)
        statement.bindLong(4, entity.bodegaId)
        statement.bindText(5, entity.sucursalNombre)
        statement.bindText(6, entity.bodegaNombre)
        statement.bindText(7, entity.nombre)
        statement.bindText(8, entity.estadoCierre)
        statement.bindLong(9, entity.fechaApertura)
        statement.bindLong(10, entity.fechaCierre)
        statement.bindLong(11, entity.cerradoPor)
        statement.bindLong(12, entity.posiciones.toLong())
        statement.bindLong(13, entity.productosContados.toLong())
        statement.bindLong(14, entity.productosSinContar.toLong())
        statement.bindLong(15, entity.lineasConDiferencia.toLong())
        statement.bindLong(16, entity.unidadesSistema.toLong())
        statement.bindLong(17, entity.unidadesContadas.toLong())
        statement.bindLong(18, entity.ajustesAplicados.toLong())
        statement.bindText(19, entity.estadoRevision)
        val _tmpRevisadoPor: Long? = entity.revisadoPor
        if (_tmpRevisadoPor == null) {
          statement.bindNull(20)
        } else {
          statement.bindLong(20, _tmpRevisadoPor)
        }
        val _tmpFechaRevision: Long? = entity.fechaRevision
        if (_tmpFechaRevision == null) {
          statement.bindNull(21)
        } else {
          statement.bindLong(21, _tmpFechaRevision)
        }
      }
    }
    this.__insertAdapterOfDetalleInformeInventario = object :
        EntityInsertAdapter<DetalleInformeInventario>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `detalles_informe_inventario` (`id`,`informeId`,`productoId`,`codigo`,`producto`,`posicion`,`cantidadSistema`,`cantidadContada`,`ajusteAplicado`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: DetalleInformeInventario) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.informeId)
        statement.bindLong(3, entity.productoId)
        statement.bindText(4, entity.codigo)
        statement.bindText(5, entity.producto)
        statement.bindText(6, entity.posicion)
        statement.bindLong(7, entity.cantidadSistema.toLong())
        val _tmpCantidadContada: Int? = entity.cantidadContada
        if (_tmpCantidadContada == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpCantidadContada.toLong())
        }
        val _tmp: Int = if (entity.ajusteAplicado) 1 else 0
        statement.bindLong(9, _tmp.toLong())
      }
    }
  }

  public override suspend fun guardarInforme(valor: InformeInventario): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfInformeInventario.insertAndReturnId(_connection, valor)
    _result
  }

  public override suspend fun guardarDetalleInforme(valor: DetalleInformeInventario): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfDetalleInformeInventario.insertAndReturnId(_connection,
        valor)
    _result
  }

  public override fun informesTienda(): Flow<List<InventarioTienda>> {
    val _sql: String =
        "SELECT * FROM inventarios_tienda WHERE estado='CERRADO' AND campaniaId IS NULL ORDER BY CASE WHEN estadoRevision='NUEVO' THEN 0 ELSE 1 END, fechaCierre DESC,id DESC"
    return createFlow(__db, false, arrayOf("inventarios_tienda")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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

  public override fun nuevosTienda(): Flow<Int> {
    val _sql: String =
        "SELECT COUNT(*) FROM inventarios_tienda WHERE estado='CERRADO' AND campaniaId IS NULL AND estadoRevision='NUEVO'"
    return createFlow(__db, false, arrayOf("inventarios_tienda")) { _connection ->
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

  public override suspend fun todosInformesBodega(): List<InformeInventario> {
    val _sql: String = "SELECT * FROM informes_inventario ORDER BY fechaCierre,id"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfInventarioId: Int = getColumnIndexOrThrow(_stmt, "inventarioId")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfBodegaId: Int = getColumnIndexOrThrow(_stmt, "bodegaId")
        val _columnIndexOfSucursalNombre: Int = getColumnIndexOrThrow(_stmt, "sucursalNombre")
        val _columnIndexOfBodegaNombre: Int = getColumnIndexOrThrow(_stmt, "bodegaNombre")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _columnIndexOfEstadoCierre: Int = getColumnIndexOrThrow(_stmt, "estadoCierre")
        val _columnIndexOfFechaApertura: Int = getColumnIndexOrThrow(_stmt, "fechaApertura")
        val _columnIndexOfFechaCierre: Int = getColumnIndexOrThrow(_stmt, "fechaCierre")
        val _columnIndexOfCerradoPor: Int = getColumnIndexOrThrow(_stmt, "cerradoPor")
        val _columnIndexOfPosiciones: Int = getColumnIndexOrThrow(_stmt, "posiciones")
        val _columnIndexOfProductosContados: Int = getColumnIndexOrThrow(_stmt, "productosContados")
        val _columnIndexOfProductosSinContar: Int = getColumnIndexOrThrow(_stmt,
            "productosSinContar")
        val _columnIndexOfLineasConDiferencia: Int = getColumnIndexOrThrow(_stmt,
            "lineasConDiferencia")
        val _columnIndexOfUnidadesSistema: Int = getColumnIndexOrThrow(_stmt, "unidadesSistema")
        val _columnIndexOfUnidadesContadas: Int = getColumnIndexOrThrow(_stmt, "unidadesContadas")
        val _columnIndexOfAjustesAplicados: Int = getColumnIndexOrThrow(_stmt, "ajustesAplicados")
        val _columnIndexOfEstadoRevision: Int = getColumnIndexOrThrow(_stmt, "estadoRevision")
        val _columnIndexOfRevisadoPor: Int = getColumnIndexOrThrow(_stmt, "revisadoPor")
        val _columnIndexOfFechaRevision: Int = getColumnIndexOrThrow(_stmt, "fechaRevision")
        val _result: MutableList<InformeInventario> = mutableListOf()
        while (_stmt.step()) {
          val _item: InformeInventario
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpInventarioId: Long
          _tmpInventarioId = _stmt.getLong(_columnIndexOfInventarioId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpBodegaId: Long
          _tmpBodegaId = _stmt.getLong(_columnIndexOfBodegaId)
          val _tmpSucursalNombre: String
          _tmpSucursalNombre = _stmt.getText(_columnIndexOfSucursalNombre)
          val _tmpBodegaNombre: String
          _tmpBodegaNombre = _stmt.getText(_columnIndexOfBodegaNombre)
          val _tmpNombre: String
          _tmpNombre = _stmt.getText(_columnIndexOfNombre)
          val _tmpEstadoCierre: String
          _tmpEstadoCierre = _stmt.getText(_columnIndexOfEstadoCierre)
          val _tmpFechaApertura: Long
          _tmpFechaApertura = _stmt.getLong(_columnIndexOfFechaApertura)
          val _tmpFechaCierre: Long
          _tmpFechaCierre = _stmt.getLong(_columnIndexOfFechaCierre)
          val _tmpCerradoPor: Long
          _tmpCerradoPor = _stmt.getLong(_columnIndexOfCerradoPor)
          val _tmpPosiciones: Int
          _tmpPosiciones = _stmt.getLong(_columnIndexOfPosiciones).toInt()
          val _tmpProductosContados: Int
          _tmpProductosContados = _stmt.getLong(_columnIndexOfProductosContados).toInt()
          val _tmpProductosSinContar: Int
          _tmpProductosSinContar = _stmt.getLong(_columnIndexOfProductosSinContar).toInt()
          val _tmpLineasConDiferencia: Int
          _tmpLineasConDiferencia = _stmt.getLong(_columnIndexOfLineasConDiferencia).toInt()
          val _tmpUnidadesSistema: Int
          _tmpUnidadesSistema = _stmt.getLong(_columnIndexOfUnidadesSistema).toInt()
          val _tmpUnidadesContadas: Int
          _tmpUnidadesContadas = _stmt.getLong(_columnIndexOfUnidadesContadas).toInt()
          val _tmpAjustesAplicados: Int
          _tmpAjustesAplicados = _stmt.getLong(_columnIndexOfAjustesAplicados).toInt()
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
          _item =
              InformeInventario(_tmpId,_tmpInventarioId,_tmpSucursalId,_tmpBodegaId,_tmpSucursalNombre,_tmpBodegaNombre,_tmpNombre,_tmpEstadoCierre,_tmpFechaApertura,_tmpFechaCierre,_tmpCerradoPor,_tmpPosiciones,_tmpProductosContados,_tmpProductosSinContar,_tmpLineasConDiferencia,_tmpUnidadesSistema,_tmpUnidadesContadas,_tmpAjustesAplicados,_tmpEstadoRevision,_tmpRevisadoPor,_tmpFechaRevision)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun todosDetallesBodega(): List<DetalleInformeInventario> {
    val _sql: String = "SELECT * FROM detalles_informe_inventario ORDER BY informeId,id"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfInformeId: Int = getColumnIndexOrThrow(_stmt, "informeId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfCodigo: Int = getColumnIndexOrThrow(_stmt, "codigo")
        val _columnIndexOfProducto: Int = getColumnIndexOrThrow(_stmt, "producto")
        val _columnIndexOfPosicion: Int = getColumnIndexOrThrow(_stmt, "posicion")
        val _columnIndexOfCantidadSistema: Int = getColumnIndexOrThrow(_stmt, "cantidadSistema")
        val _columnIndexOfCantidadContada: Int = getColumnIndexOrThrow(_stmt, "cantidadContada")
        val _columnIndexOfAjusteAplicado: Int = getColumnIndexOrThrow(_stmt, "ajusteAplicado")
        val _result: MutableList<DetalleInformeInventario> = mutableListOf()
        while (_stmt.step()) {
          val _item: DetalleInformeInventario
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpInformeId: Long
          _tmpInformeId = _stmt.getLong(_columnIndexOfInformeId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpCodigo: String
          _tmpCodigo = _stmt.getText(_columnIndexOfCodigo)
          val _tmpProducto: String
          _tmpProducto = _stmt.getText(_columnIndexOfProducto)
          val _tmpPosicion: String
          _tmpPosicion = _stmt.getText(_columnIndexOfPosicion)
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
          _item =
              DetalleInformeInventario(_tmpId,_tmpInformeId,_tmpProductoId,_tmpCodigo,_tmpProducto,_tmpPosicion,_tmpCantidadSistema,_tmpCantidadContada,_tmpAjusteAplicado)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun todosInventariosTienda(): List<InventarioTienda> {
    val _sql: String =
        "SELECT * FROM inventarios_tienda WHERE estado='CERRADO' ORDER BY fechaCierre,id"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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

  public override suspend fun todasLineasTienda(): List<LineaInventarioTienda> {
    val _sql: String =
        "SELECT l.* FROM lineas_inventario_tienda l JOIN inventarios_tienda i ON i.id=l.inventarioId WHERE i.estado='CERRADO' ORDER BY l.inventarioId,l.id"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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

  public override fun detalleTienda(id: Long): Flow<List<LineaInventarioTienda>> {
    val _sql: String =
        "SELECT * FROM lineas_inventario_tienda WHERE inventarioId=? ORDER BY productoId,posicionId"
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

  public override suspend fun sucursalInventarioTienda(id: Long): Long? {
    val _sql: String =
        "SELECT sucursalId FROM inventarios_tienda WHERE id=? AND estado='CERRADO' LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
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

  public override suspend fun sucursalInformeBodega(id: Long): Long? {
    val _sql: String = "SELECT sucursalId FROM informes_inventario WHERE id=? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
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

  public override suspend fun informeDeParcial(id: Long): InformeInventario? {
    val _sql: String = "SELECT * FROM informes_inventario WHERE inventarioId=? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfInventarioId: Int = getColumnIndexOrThrow(_stmt, "inventarioId")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfBodegaId: Int = getColumnIndexOrThrow(_stmt, "bodegaId")
        val _columnIndexOfSucursalNombre: Int = getColumnIndexOrThrow(_stmt, "sucursalNombre")
        val _columnIndexOfBodegaNombre: Int = getColumnIndexOrThrow(_stmt, "bodegaNombre")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _columnIndexOfEstadoCierre: Int = getColumnIndexOrThrow(_stmt, "estadoCierre")
        val _columnIndexOfFechaApertura: Int = getColumnIndexOrThrow(_stmt, "fechaApertura")
        val _columnIndexOfFechaCierre: Int = getColumnIndexOrThrow(_stmt, "fechaCierre")
        val _columnIndexOfCerradoPor: Int = getColumnIndexOrThrow(_stmt, "cerradoPor")
        val _columnIndexOfPosiciones: Int = getColumnIndexOrThrow(_stmt, "posiciones")
        val _columnIndexOfProductosContados: Int = getColumnIndexOrThrow(_stmt, "productosContados")
        val _columnIndexOfProductosSinContar: Int = getColumnIndexOrThrow(_stmt,
            "productosSinContar")
        val _columnIndexOfLineasConDiferencia: Int = getColumnIndexOrThrow(_stmt,
            "lineasConDiferencia")
        val _columnIndexOfUnidadesSistema: Int = getColumnIndexOrThrow(_stmt, "unidadesSistema")
        val _columnIndexOfUnidadesContadas: Int = getColumnIndexOrThrow(_stmt, "unidadesContadas")
        val _columnIndexOfAjustesAplicados: Int = getColumnIndexOrThrow(_stmt, "ajustesAplicados")
        val _columnIndexOfEstadoRevision: Int = getColumnIndexOrThrow(_stmt, "estadoRevision")
        val _columnIndexOfRevisadoPor: Int = getColumnIndexOrThrow(_stmt, "revisadoPor")
        val _columnIndexOfFechaRevision: Int = getColumnIndexOrThrow(_stmt, "fechaRevision")
        val _result: InformeInventario?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpInventarioId: Long
          _tmpInventarioId = _stmt.getLong(_columnIndexOfInventarioId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpBodegaId: Long
          _tmpBodegaId = _stmt.getLong(_columnIndexOfBodegaId)
          val _tmpSucursalNombre: String
          _tmpSucursalNombre = _stmt.getText(_columnIndexOfSucursalNombre)
          val _tmpBodegaNombre: String
          _tmpBodegaNombre = _stmt.getText(_columnIndexOfBodegaNombre)
          val _tmpNombre: String
          _tmpNombre = _stmt.getText(_columnIndexOfNombre)
          val _tmpEstadoCierre: String
          _tmpEstadoCierre = _stmt.getText(_columnIndexOfEstadoCierre)
          val _tmpFechaApertura: Long
          _tmpFechaApertura = _stmt.getLong(_columnIndexOfFechaApertura)
          val _tmpFechaCierre: Long
          _tmpFechaCierre = _stmt.getLong(_columnIndexOfFechaCierre)
          val _tmpCerradoPor: Long
          _tmpCerradoPor = _stmt.getLong(_columnIndexOfCerradoPor)
          val _tmpPosiciones: Int
          _tmpPosiciones = _stmt.getLong(_columnIndexOfPosiciones).toInt()
          val _tmpProductosContados: Int
          _tmpProductosContados = _stmt.getLong(_columnIndexOfProductosContados).toInt()
          val _tmpProductosSinContar: Int
          _tmpProductosSinContar = _stmt.getLong(_columnIndexOfProductosSinContar).toInt()
          val _tmpLineasConDiferencia: Int
          _tmpLineasConDiferencia = _stmt.getLong(_columnIndexOfLineasConDiferencia).toInt()
          val _tmpUnidadesSistema: Int
          _tmpUnidadesSistema = _stmt.getLong(_columnIndexOfUnidadesSistema).toInt()
          val _tmpUnidadesContadas: Int
          _tmpUnidadesContadas = _stmt.getLong(_columnIndexOfUnidadesContadas).toInt()
          val _tmpAjustesAplicados: Int
          _tmpAjustesAplicados = _stmt.getLong(_columnIndexOfAjustesAplicados).toInt()
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
          _result =
              InformeInventario(_tmpId,_tmpInventarioId,_tmpSucursalId,_tmpBodegaId,_tmpSucursalNombre,_tmpBodegaNombre,_tmpNombre,_tmpEstadoCierre,_tmpFechaApertura,_tmpFechaCierre,_tmpCerradoPor,_tmpPosiciones,_tmpProductosContados,_tmpProductosSinContar,_tmpLineasConDiferencia,_tmpUnidadesSistema,_tmpUnidadesContadas,_tmpAjustesAplicados,_tmpEstadoRevision,_tmpRevisadoPor,_tmpFechaRevision)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun detallesActuales(id: Long): List<DetalleInformeInventario> {
    val _sql: String = "SELECT * FROM detalles_informe_inventario WHERE informeId=? ORDER BY id"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfInformeId: Int = getColumnIndexOrThrow(_stmt, "informeId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfCodigo: Int = getColumnIndexOrThrow(_stmt, "codigo")
        val _columnIndexOfProducto: Int = getColumnIndexOrThrow(_stmt, "producto")
        val _columnIndexOfPosicion: Int = getColumnIndexOrThrow(_stmt, "posicion")
        val _columnIndexOfCantidadSistema: Int = getColumnIndexOrThrow(_stmt, "cantidadSistema")
        val _columnIndexOfCantidadContada: Int = getColumnIndexOrThrow(_stmt, "cantidadContada")
        val _columnIndexOfAjusteAplicado: Int = getColumnIndexOrThrow(_stmt, "ajusteAplicado")
        val _result: MutableList<DetalleInformeInventario> = mutableListOf()
        while (_stmt.step()) {
          val _item: DetalleInformeInventario
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpInformeId: Long
          _tmpInformeId = _stmt.getLong(_columnIndexOfInformeId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpCodigo: String
          _tmpCodigo = _stmt.getText(_columnIndexOfCodigo)
          val _tmpProducto: String
          _tmpProducto = _stmt.getText(_columnIndexOfProducto)
          val _tmpPosicion: String
          _tmpPosicion = _stmt.getText(_columnIndexOfPosicion)
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
          _item =
              DetalleInformeInventario(_tmpId,_tmpInformeId,_tmpProductoId,_tmpCodigo,_tmpProducto,_tmpPosicion,_tmpCantidadSistema,_tmpCantidadContada,_tmpAjusteAplicado)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun informes(): Flow<List<InformeInventario>> {
    val _sql: String =
        "SELECT f.* FROM informes_inventario f LEFT JOIN inventarios_bodega i ON i.id=f.inventarioId WHERE i.campaniaId IS NULL ORDER BY CASE WHEN f.estadoRevision='NUEVO' THEN 0 ELSE 1 END, f.fechaCierre DESC, f.id DESC"
    return createFlow(__db, false, arrayOf("informes_inventario", "inventarios_bodega")) {
        _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfInventarioId: Int = getColumnIndexOrThrow(_stmt, "inventarioId")
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfBodegaId: Int = getColumnIndexOrThrow(_stmt, "bodegaId")
        val _columnIndexOfSucursalNombre: Int = getColumnIndexOrThrow(_stmt, "sucursalNombre")
        val _columnIndexOfBodegaNombre: Int = getColumnIndexOrThrow(_stmt, "bodegaNombre")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _columnIndexOfEstadoCierre: Int = getColumnIndexOrThrow(_stmt, "estadoCierre")
        val _columnIndexOfFechaApertura: Int = getColumnIndexOrThrow(_stmt, "fechaApertura")
        val _columnIndexOfFechaCierre: Int = getColumnIndexOrThrow(_stmt, "fechaCierre")
        val _columnIndexOfCerradoPor: Int = getColumnIndexOrThrow(_stmt, "cerradoPor")
        val _columnIndexOfPosiciones: Int = getColumnIndexOrThrow(_stmt, "posiciones")
        val _columnIndexOfProductosContados: Int = getColumnIndexOrThrow(_stmt, "productosContados")
        val _columnIndexOfProductosSinContar: Int = getColumnIndexOrThrow(_stmt,
            "productosSinContar")
        val _columnIndexOfLineasConDiferencia: Int = getColumnIndexOrThrow(_stmt,
            "lineasConDiferencia")
        val _columnIndexOfUnidadesSistema: Int = getColumnIndexOrThrow(_stmt, "unidadesSistema")
        val _columnIndexOfUnidadesContadas: Int = getColumnIndexOrThrow(_stmt, "unidadesContadas")
        val _columnIndexOfAjustesAplicados: Int = getColumnIndexOrThrow(_stmt, "ajustesAplicados")
        val _columnIndexOfEstadoRevision: Int = getColumnIndexOrThrow(_stmt, "estadoRevision")
        val _columnIndexOfRevisadoPor: Int = getColumnIndexOrThrow(_stmt, "revisadoPor")
        val _columnIndexOfFechaRevision: Int = getColumnIndexOrThrow(_stmt, "fechaRevision")
        val _result: MutableList<InformeInventario> = mutableListOf()
        while (_stmt.step()) {
          val _item: InformeInventario
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpInventarioId: Long
          _tmpInventarioId = _stmt.getLong(_columnIndexOfInventarioId)
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpBodegaId: Long
          _tmpBodegaId = _stmt.getLong(_columnIndexOfBodegaId)
          val _tmpSucursalNombre: String
          _tmpSucursalNombre = _stmt.getText(_columnIndexOfSucursalNombre)
          val _tmpBodegaNombre: String
          _tmpBodegaNombre = _stmt.getText(_columnIndexOfBodegaNombre)
          val _tmpNombre: String
          _tmpNombre = _stmt.getText(_columnIndexOfNombre)
          val _tmpEstadoCierre: String
          _tmpEstadoCierre = _stmt.getText(_columnIndexOfEstadoCierre)
          val _tmpFechaApertura: Long
          _tmpFechaApertura = _stmt.getLong(_columnIndexOfFechaApertura)
          val _tmpFechaCierre: Long
          _tmpFechaCierre = _stmt.getLong(_columnIndexOfFechaCierre)
          val _tmpCerradoPor: Long
          _tmpCerradoPor = _stmt.getLong(_columnIndexOfCerradoPor)
          val _tmpPosiciones: Int
          _tmpPosiciones = _stmt.getLong(_columnIndexOfPosiciones).toInt()
          val _tmpProductosContados: Int
          _tmpProductosContados = _stmt.getLong(_columnIndexOfProductosContados).toInt()
          val _tmpProductosSinContar: Int
          _tmpProductosSinContar = _stmt.getLong(_columnIndexOfProductosSinContar).toInt()
          val _tmpLineasConDiferencia: Int
          _tmpLineasConDiferencia = _stmt.getLong(_columnIndexOfLineasConDiferencia).toInt()
          val _tmpUnidadesSistema: Int
          _tmpUnidadesSistema = _stmt.getLong(_columnIndexOfUnidadesSistema).toInt()
          val _tmpUnidadesContadas: Int
          _tmpUnidadesContadas = _stmt.getLong(_columnIndexOfUnidadesContadas).toInt()
          val _tmpAjustesAplicados: Int
          _tmpAjustesAplicados = _stmt.getLong(_columnIndexOfAjustesAplicados).toInt()
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
          _item =
              InformeInventario(_tmpId,_tmpInventarioId,_tmpSucursalId,_tmpBodegaId,_tmpSucursalNombre,_tmpBodegaNombre,_tmpNombre,_tmpEstadoCierre,_tmpFechaApertura,_tmpFechaCierre,_tmpCerradoPor,_tmpPosiciones,_tmpProductosContados,_tmpProductosSinContar,_tmpLineasConDiferencia,_tmpUnidadesSistema,_tmpUnidadesContadas,_tmpAjustesAplicados,_tmpEstadoRevision,_tmpRevisadoPor,_tmpFechaRevision)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun nuevos(): Flow<Int> {
    val _sql: String =
        "SELECT COUNT(*) FROM informes_inventario f LEFT JOIN inventarios_bodega i ON i.id=f.inventarioId WHERE f.estadoRevision='NUEVO' AND i.campaniaId IS NULL"
    return createFlow(__db, false, arrayOf("informes_inventario", "inventarios_bodega")) {
        _connection ->
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

  public override fun detalles(id: Long): Flow<List<DetalleInformeInventario>> {
    val _sql: String =
        "SELECT * FROM detalles_informe_inventario WHERE informeId=? ORDER BY codigo, posicion"
    return createFlow(__db, false, arrayOf("detalles_informe_inventario")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfInformeId: Int = getColumnIndexOrThrow(_stmt, "informeId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfCodigo: Int = getColumnIndexOrThrow(_stmt, "codigo")
        val _columnIndexOfProducto: Int = getColumnIndexOrThrow(_stmt, "producto")
        val _columnIndexOfPosicion: Int = getColumnIndexOrThrow(_stmt, "posicion")
        val _columnIndexOfCantidadSistema: Int = getColumnIndexOrThrow(_stmt, "cantidadSistema")
        val _columnIndexOfCantidadContada: Int = getColumnIndexOrThrow(_stmt, "cantidadContada")
        val _columnIndexOfAjusteAplicado: Int = getColumnIndexOrThrow(_stmt, "ajusteAplicado")
        val _result: MutableList<DetalleInformeInventario> = mutableListOf()
        while (_stmt.step()) {
          val _item: DetalleInformeInventario
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpInformeId: Long
          _tmpInformeId = _stmt.getLong(_columnIndexOfInformeId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpCodigo: String
          _tmpCodigo = _stmt.getText(_columnIndexOfCodigo)
          val _tmpProducto: String
          _tmpProducto = _stmt.getText(_columnIndexOfProducto)
          val _tmpPosicion: String
          _tmpPosicion = _stmt.getText(_columnIndexOfPosicion)
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
          _item =
              DetalleInformeInventario(_tmpId,_tmpInformeId,_tmpProductoId,_tmpCodigo,_tmpProducto,_tmpPosicion,_tmpCantidadSistema,_tmpCantidadContada,_tmpAjusteAplicado)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun gerenteValido(uid: Long): Int {
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

  public override suspend fun revisarTienda(
    id: Long,
    uid: Long,
    fecha: Long,
  ): Int {
    val _sql: String =
        "UPDATE inventarios_tienda SET estadoRevision='REVISADO',revisadoPor=?,fechaRevision=? WHERE id=? AND estado='CERRADO' AND estadoRevision='NUEVO'"
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

  public override suspend fun marcarRevisado(
    id: Long,
    uid: Long,
    fecha: Long,
  ): Int {
    val _sql: String =
        "UPDATE informes_inventario SET estadoRevision='REVISADO', revisadoPor=?, fechaRevision=? WHERE id=? AND estadoRevision='NUEVO'"
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
