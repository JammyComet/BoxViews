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
public class AdministracionDao_Impl(
  __db: RoomDatabase,
) : AdministracionDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfConfiguracionSucursal: EntityInsertAdapter<ConfiguracionSucursal>

  private val __insertAdapterOfMinimoSucursal: EntityInsertAdapter<MinimoSucursal>

  private val __insertAdapterOfMinimoSucursal_1: EntityInsertAdapter<MinimoSucursal>

  private val __insertAdapterOfEventoAdministrativo: EntityInsertAdapter<EventoAdministrativo>
  init {
    this.__db = __db
    this.__insertAdapterOfConfiguracionSucursal = object :
        EntityInsertAdapter<ConfiguracionSucursal>() {
      protected override fun createQuery(): String =
          "INSERT OR IGNORE INTO `configuracion_sucursal` (`sucursalId`,`bodegaPrincipalId`) VALUES (?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ConfiguracionSucursal) {
        statement.bindLong(1, entity.sucursalId)
        val _tmpBodegaPrincipalId: Long? = entity.bodegaPrincipalId
        if (_tmpBodegaPrincipalId == null) {
          statement.bindNull(2)
        } else {
          statement.bindLong(2, _tmpBodegaPrincipalId)
        }
      }
    }
    this.__insertAdapterOfMinimoSucursal = object : EntityInsertAdapter<MinimoSucursal>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `minimos_sucursal` (`sucursalId`,`productoId`,`minimo`) VALUES (?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: MinimoSucursal) {
        statement.bindLong(1, entity.sucursalId)
        statement.bindLong(2, entity.productoId)
        statement.bindLong(3, entity.minimo.toLong())
      }
    }
    this.__insertAdapterOfMinimoSucursal_1 = object : EntityInsertAdapter<MinimoSucursal>() {
      protected override fun createQuery(): String =
          "INSERT OR IGNORE INTO `minimos_sucursal` (`sucursalId`,`productoId`,`minimo`) VALUES (?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: MinimoSucursal) {
        statement.bindLong(1, entity.sucursalId)
        statement.bindLong(2, entity.productoId)
        statement.bindLong(3, entity.minimo.toLong())
      }
    }
    this.__insertAdapterOfEventoAdministrativo = object :
        EntityInsertAdapter<EventoAdministrativo>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `eventos_administracion` (`id`,`sucursalId`,`usuarioId`,`accion`,`detalle`,`fecha`) VALUES (nullif(?, 0),?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: EventoAdministrativo) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.sucursalId)
        statement.bindLong(3, entity.usuarioId)
        statement.bindText(4, entity.accion)
        statement.bindText(5, entity.detalle)
        statement.bindLong(6, entity.fecha)
      }
    }
  }

  public override suspend fun crearConfiguracionSucursal(valor: ConfiguracionSucursal): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfConfiguracionSucursal.insertAndReturnId(_connection, valor)
    _result
  }

  public override suspend fun guardarMinimoSucursal(valor: MinimoSucursal): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfMinimoSucursal.insertAndReturnId(_connection, valor)
    _result
  }

  public override suspend fun guardarMinimoSiFalta(valor: MinimoSucursal): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfMinimoSucursal_1.insertAndReturnId(_connection, valor)
    _result
  }

  public override suspend fun guardarEventoAdministrativo(evento: EventoAdministrativo): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfEventoAdministrativo.insertAndReturnId(_connection, evento)
    _result
  }

  public override fun sucursalesAdministradas(uid: Long): Flow<List<Sucursal>> {
    val _sql: String =
        "SELECT s.* FROM sucursales s JOIN usuario_sucursal us ON us.sucursalId=s.id JOIN usuarios u ON u.id=us.usuarioId WHERE us.usuarioId=? AND u.rol='ADMIN_SUCURSAL' AND u.activo=1 ORDER BY s.nombre"
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

  public override suspend fun administradorAutorizado(uid: Long, sid: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM usuarios u JOIN usuario_sucursal us ON us.usuarioId=u.id WHERE u.id=? AND us.sucursalId=? AND u.rol='ADMIN_SUCURSAL' AND u.activo=1"
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

  public override fun observarAdministrador(uid: Long, sid: Long): Flow<Int> {
    val _sql: String =
        "SELECT COUNT(*) FROM usuarios u JOIN usuario_sucursal us ON us.usuarioId=u.id WHERE u.id=? AND us.sucursalId=? AND u.rol='ADMIN_SUCURSAL' AND u.activo=1"
    return createFlow(__db, false, arrayOf("usuarios", "usuario_sucursal")) { _connection ->
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

  public override fun bodegasAdministradas(sid: Long): Flow<List<Bodega>> {
    val _sql: String = "SELECT * FROM bodegas WHERE sucursalId=? ORDER BY activa DESC, nombre"
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

  public override suspend fun bodegaAdministradaPorId(bid: Long): Bodega? {
    val _sql: String = "SELECT * FROM bodegas WHERE id=? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, bid)
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

  public override suspend fun siguienteIdBodega(): Long {
    val _sql: String = "SELECT COALESCE(MAX(id),0)+1 FROM bodegas"
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

  public override suspend fun nombreBodegaExiste(sid: Long, nombre: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM bodegas WHERE sucursalId=? AND LOWER(nombre)=LOWER(?)"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
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

  public override suspend fun stockTotalBodega(bid: Long): Long {
    val _sql: String =
        "SELECT COALESCE(SUM(e.cantidad),0) FROM existencias e JOIN posiciones p ON p.id=e.posicionId WHERE p.bodegaId=?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, bid)
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

  public override suspend fun cajasActivasBodega(bid: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM cajas WHERE (bodegaOrigenId=? OR bodegaDestinoId=?) AND estado NOT IN ('RECIBIDA','CON_DIFERENCIA','CANCELADA')"
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

  public override suspend fun solicitudesActivasBodega(bid: Long): Int {
    val _sql: String = """
        |SELECT COUNT(*) FROM solicitudes_reposicion s LEFT JOIN cajas c ON c.id=s.cajaId
        |        WHERE s.bodegaId=? AND (s.estado='PENDIENTE' OR
        |            (s.estado='ATENDIDA' AND (c.id IS NULL OR c.estado NOT IN ('RECIBIDA','CON_DIFERENCIA','CANCELADA'))))
        """.trimMargin()
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

  public override suspend fun solicitudesExternasActivasBodega(bid: Long): Int {
    val _sql: String = """
        |SELECT COUNT(*) FROM solicitudes_externas WHERE bodegaId=? AND
        |        estado IN ('PENDIENTE_ADMIN','EN_GERENCIA','APROBADA','ENTREGA_PROGRAMADA')
        """.trimMargin()
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

  public override suspend fun personalActivoBodega(bid: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM usuario_bodega ub JOIN usuarios u ON u.id=ub.usuarioId WHERE ub.bodegaId=? AND u.activo=1"
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

  public override suspend fun inventariosAbiertosBodega(bid: Long): Int {
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

  public override fun configuracionSucursal(sid: Long): Flow<ConfiguracionSucursal?> {
    val _sql: String = "SELECT * FROM configuracion_sucursal WHERE sucursalId=? LIMIT 1"
    return createFlow(__db, false, arrayOf("configuracion_sucursal")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfBodegaPrincipalId: Int = getColumnIndexOrThrow(_stmt, "bodegaPrincipalId")
        val _result: ConfiguracionSucursal?
        if (_stmt.step()) {
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpBodegaPrincipalId: Long?
          if (_stmt.isNull(_columnIndexOfBodegaPrincipalId)) {
            _tmpBodegaPrincipalId = null
          } else {
            _tmpBodegaPrincipalId = _stmt.getLong(_columnIndexOfBodegaPrincipalId)
          }
          _result = ConfiguracionSucursal(_tmpSucursalId,_tmpBodegaPrincipalId)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun configuracionSucursalActual(sid: Long): ConfiguracionSucursal? {
    val _sql: String = "SELECT * FROM configuracion_sucursal WHERE sucursalId=? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfBodegaPrincipalId: Int = getColumnIndexOrThrow(_stmt, "bodegaPrincipalId")
        val _result: ConfiguracionSucursal?
        if (_stmt.step()) {
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpBodegaPrincipalId: Long?
          if (_stmt.isNull(_columnIndexOfBodegaPrincipalId)) {
            _tmpBodegaPrincipalId = null
          } else {
            _tmpBodegaPrincipalId = _stmt.getLong(_columnIndexOfBodegaPrincipalId)
          }
          _result = ConfiguracionSucursal(_tmpSucursalId,_tmpBodegaPrincipalId)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun empleadosSucursal(sid: Long): Flow<List<Usuario>> {
    val _sql: String = """
        |SELECT u.* FROM usuarios u WHERE
        |        (EXISTS (SELECT 1 FROM usuario_sucursal us WHERE us.usuarioId=u.id AND us.sucursalId=?)
        |        OR EXISTS (SELECT 1 FROM usuario_bodega ub JOIN bodegas b ON b.id=ub.bodegaId
        |            WHERE ub.usuarioId=u.id AND b.sucursalId=?)
        |        OR (u.sucursalResponsableId=?
        |            AND NOT EXISTS (SELECT 1 FROM usuario_sucursal us WHERE us.usuarioId=u.id)
        |            AND NOT EXISTS (SELECT 1 FROM usuario_bodega ub WHERE ub.usuarioId=u.id)))
        |        AND u.rol IN ('ADMIN_SUCURSAL','JEFE_BODEGA','JEFE_TIENDA','OPERADOR_BODEGA','TRABAJADOR_TIENDA')
        |        ORDER BY u.activo DESC,u.nombre
        """.trimMargin()
    return createFlow(__db, false, arrayOf("usuarios", "usuario_sucursal", "usuario_bodega",
        "bodegas")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, sid)
        _argIndex = 3
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

  public override suspend fun empleadoExclusivoSucursal(uid: Long, sid: Long): Int {
    val _sql: String = """
        |SELECT COUNT(*) FROM usuarios u WHERE u.id=? AND u.rol!='ADMIN_SUCURSAL'
        |        AND (EXISTS (SELECT 1 FROM usuario_sucursal us WHERE us.usuarioId=u.id AND us.sucursalId=?)
        |             OR EXISTS (SELECT 1 FROM usuario_bodega ub JOIN bodegas b ON b.id=ub.bodegaId WHERE ub.usuarioId=u.id AND b.sucursalId=?))
        |        AND NOT EXISTS (SELECT 1 FROM usuario_sucursal us WHERE us.usuarioId=u.id AND us.sucursalId!=?)
        |        AND NOT EXISTS (SELECT 1 FROM usuario_bodega ub JOIN bodegas b ON b.id=ub.bodegaId WHERE ub.usuarioId=u.id AND b.sucursalId!=?)
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, uid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, sid)
        _argIndex = 3
        _stmt.bindLong(_argIndex, sid)
        _argIndex = 4
        _stmt.bindLong(_argIndex, sid)
        _argIndex = 5
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

  public override suspend fun sucursalResponsableEmpleado(uid: Long, sid: Long): Int {
    val _sql: String = """
        |SELECT CASE WHEN ? = COALESCE((
        |        SELECT MIN(sucursalId) FROM (
        |            SELECT us.sucursalId AS sucursalId FROM usuario_sucursal us WHERE us.usuarioId=?
        |            UNION SELECT b.sucursalId AS sucursalId FROM usuario_bodega ub
        |            JOIN bodegas b ON b.id=ub.bodegaId WHERE ub.usuarioId=?
        |        )
        |    ), (SELECT sucursalResponsableId FROM usuarios WHERE id=?))
        |    AND (SELECT rol FROM usuarios WHERE id=?) IN
        |        ('JEFE_BODEGA','OPERADOR_BODEGA','JEFE_TIENDA','TRABAJADOR_TIENDA') THEN 1 ELSE 0 END
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, uid)
        _argIndex = 3
        _stmt.bindLong(_argIndex, uid)
        _argIndex = 4
        _stmt.bindLong(_argIndex, uid)
        _argIndex = 5
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

  public override suspend fun operadorDeSucursal(uid: Long, sid: Long): Int {
    val _sql: String = """
        |SELECT COUNT(*) FROM usuarios u WHERE u.id=? AND u.rol='OPERADOR_BODEGA'
        |        AND EXISTS (SELECT 1 FROM usuario_bodega ub JOIN bodegas b ON b.id=ub.bodegaId
        |            WHERE ub.usuarioId=? AND b.sucursalId=?)
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, uid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, uid)
        _argIndex = 3
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

  public override suspend fun tiendasActualesEmpleado(uid: Long): List<Long> {
    val _sql: String = "SELECT sucursalId FROM usuario_sucursal WHERE usuarioId=?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, uid)
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

  public override suspend fun bodegasActualesEmpleado(uid: Long): List<Bodega> {
    val _sql: String =
        "SELECT b.* FROM bodegas b JOIN usuario_bodega ub ON ub.bodegaId=b.id WHERE ub.usuarioId=?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, uid)
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

  public override suspend fun empleadoAsignadoBodega(uid: Long, bid: Long): Int {
    val _sql: String = "SELECT COUNT(*) FROM usuario_bodega WHERE usuarioId=? AND bodegaId=?"
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

  public override fun minimosSucursal(sid: Long): Flow<List<MinimoSucursal>> {
    val _sql: String = "SELECT * FROM minimos_sucursal WHERE sucursalId=?"
    return createFlow(__db, false, arrayOf("minimos_sucursal")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
        val _columnIndexOfSucursalId: Int = getColumnIndexOrThrow(_stmt, "sucursalId")
        val _columnIndexOfProductoId: Int = getColumnIndexOrThrow(_stmt, "productoId")
        val _columnIndexOfMinimo: Int = getColumnIndexOrThrow(_stmt, "minimo")
        val _result: MutableList<MinimoSucursal> = mutableListOf()
        while (_stmt.step()) {
          val _item: MinimoSucursal
          val _tmpSucursalId: Long
          _tmpSucursalId = _stmt.getLong(_columnIndexOfSucursalId)
          val _tmpProductoId: Long
          _tmpProductoId = _stmt.getLong(_columnIndexOfProductoId)
          val _tmpMinimo: Int
          _tmpMinimo = _stmt.getLong(_columnIndexOfMinimo).toInt()
          _item = MinimoSucursal(_tmpSucursalId,_tmpProductoId,_tmpMinimo)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun historialAdministrativo(sid: Long): Flow<List<EventoAdministrativo>> {
    val _sql: String =
        "SELECT * FROM eventos_administracion WHERE sucursalId=? ORDER BY fecha DESC,id DESC LIMIT 100"
    return createFlow(__db, false, arrayOf("eventos_administracion")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
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

  public override suspend fun renombrarBodegaAdmin(
    bid: Long,
    sid: Long,
    nombre: String,
  ): Int {
    val _sql: String = "UPDATE bodegas SET nombre=? WHERE id=? AND sucursalId=?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, nombre)
        _argIndex = 2
        _stmt.bindLong(_argIndex, bid)
        _argIndex = 3
        _stmt.bindLong(_argIndex, sid)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun estadoBodegaAdmin(
    bid: Long,
    sid: Long,
    activa: Boolean,
  ): Int {
    val _sql: String = "UPDATE bodegas SET activa=? WHERE id=? AND sucursalId=?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        val _tmp: Int = if (activa) 1 else 0
        _stmt.bindLong(_argIndex, _tmp.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, bid)
        _argIndex = 3
        _stmt.bindLong(_argIndex, sid)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun guardarPrincipal(sid: Long, bid: Long): Int {
    val _sql: String = "UPDATE configuracion_sucursal SET bodegaPrincipalId=? WHERE sucursalId=?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, bid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, sid)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun guardarResponsableTrabajadorTienda(uid: Long, sid: Long): Int {
    val _sql: String =
        "UPDATE usuarios SET sucursalResponsableId=? WHERE id=? AND rol='TRABAJADOR_TIENDA'"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, uid)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun editarDatosEmpleado(
    uid: Long,
    nombre: String,
    correo: String,
  ): Int {
    val _sql: String = "UPDATE usuarios SET nombre=?, correo=? WHERE id=?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, nombre)
        _argIndex = 2
        _stmt.bindText(_argIndex, correo)
        _argIndex = 3
        _stmt.bindLong(_argIndex, uid)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun activarEmpleado(uid: Long, activo: Boolean): Int {
    val _sql: String = "UPDATE usuarios SET activo=? WHERE id=?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        val _tmp: Int = if (activo) 1 else 0
        _stmt.bindLong(_argIndex, _tmp.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, uid)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun retirarBodegaAdmin(uid: Long, bid: Long): Int {
    val _sql: String = "DELETE FROM usuario_bodega WHERE usuarioId=? AND bodegaId=?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, uid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, bid)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun borrarMinimoSucursal(sid: Long, pid: Long): Int {
    val _sql: String = "DELETE FROM minimos_sucursal WHERE sucursalId=? AND productoId=?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, pid)
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
