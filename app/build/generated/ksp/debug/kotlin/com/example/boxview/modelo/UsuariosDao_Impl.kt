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
public class UsuariosDao_Impl(
  __db: RoomDatabase,
) : UsuariosDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfUsuario: EntityInsertAdapter<Usuario>

  private val __insertAdapterOfUsuarioBodega: EntityInsertAdapter<UsuarioBodega>

  private val __insertAdapterOfUsuarioBodega_1: EntityInsertAdapter<UsuarioBodega>
  init {
    this.__db = __db
    this.__insertAdapterOfUsuario = object : EntityInsertAdapter<Usuario>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `usuarios` (`id`,`nombre`,`correo`,`claveHash`,`claveSal`,`rol`,`activo`,`rut`,`sucursalResponsableId`) VALUES (?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Usuario) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.nombre)
        statement.bindText(3, entity.correo)
        statement.bindText(4, entity.claveHash)
        statement.bindText(5, entity.claveSal)
        statement.bindText(6, entity.rol)
        val _tmp: Int = if (entity.activo) 1 else 0
        statement.bindLong(7, _tmp.toLong())
        val _tmpRut: String? = entity.rut
        if (_tmpRut == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpRut)
        }
        val _tmpSucursalResponsableId: Long? = entity.sucursalResponsableId
        if (_tmpSucursalResponsableId == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpSucursalResponsableId)
        }
      }
    }
    this.__insertAdapterOfUsuarioBodega = object : EntityInsertAdapter<UsuarioBodega>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `usuario_bodega` (`usuarioId`,`bodegaId`) VALUES (?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: UsuarioBodega) {
        statement.bindLong(1, entity.usuarioId)
        statement.bindLong(2, entity.bodegaId)
      }
    }
    this.__insertAdapterOfUsuarioBodega_1 = object : EntityInsertAdapter<UsuarioBodega>() {
      protected override fun createQuery(): String =
          "INSERT OR IGNORE INTO `usuario_bodega` (`usuarioId`,`bodegaId`) VALUES (?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: UsuarioBodega) {
        statement.bindLong(1, entity.usuarioId)
        statement.bindLong(2, entity.bodegaId)
      }
    }
  }

  public override suspend fun guardarUsuario(valor: Usuario): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfUsuario.insert(_connection, valor)
  }

  public override suspend fun guardarPermiso(valor: UsuarioBodega): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfUsuarioBodega.insert(_connection, valor)
  }

  public override suspend fun guardarPermisoSiFalta(valor: UsuarioBodega): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfUsuarioBodega_1.insertAndReturnId(_connection, valor)
    _result
  }

  public override suspend fun numeroUsuarios(): Int {
    val _sql: String = "SELECT COUNT(*) FROM usuarios"
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

  public override suspend fun usuarioPorCorreo(correo: String): Usuario? {
    val _sql: String = "SELECT * FROM usuarios WHERE correo = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, correo)
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
        val _result: Usuario?
        if (_stmt.step()) {
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
          _result =
              Usuario(_tmpId,_tmpNombre,_tmpCorreo,_tmpClaveHash,_tmpClaveSal,_tmpRol,_tmpActivo,_tmpRut,_tmpSucursalResponsableId)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun autorizado(uid: Long, bid: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM usuario_bodega b INNER JOIN usuarios u ON b.usuarioId = u.id WHERE b.usuarioId=? AND b.bodegaId=? AND EXISTS (SELECT 1 FROM bodegas bo WHERE bo.id=b.bodegaId AND bo.activa=1) AND u.activo=1 AND u.rol='OPERADOR_BODEGA'"
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

  public override fun bodegasAutorizadas(uid: Long): Flow<List<Bodega>> {
    val _sql: String =
        "SELECT b.* FROM bodegas b JOIN usuario_bodega ub ON ub.bodegaId=b.id JOIN usuarios u ON u.id=ub.usuarioId WHERE ub.usuarioId=? AND b.activa=1 AND u.activo=1 AND u.rol IN ('OPERADOR_BODEGA','JEFE_BODEGA') ORDER BY b.nombre"
    return createFlow(__db, false, arrayOf("bodegas", "usuario_bodega", "usuarios")) {
        _connection ->
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

  public override fun observarAccesoBodega(
    uid: Long,
    bid: Long,
    rol: String,
  ): Flow<Int> {
    val _sql: String =
        "SELECT COUNT(*) FROM usuario_bodega ub JOIN usuarios u ON u.id=ub.usuarioId JOIN bodegas b ON b.id=ub.bodegaId WHERE ub.usuarioId=? AND ub.bodegaId=? AND u.activo=1 AND b.activa=1 AND u.rol=? AND ? IN ('OPERADOR_BODEGA','JEFE_BODEGA')"
    return createFlow(__db, false, arrayOf("usuario_bodega", "usuarios", "bodegas")) {
        _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, uid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, bid)
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

  public override suspend fun accesoBodega(uid: Long, bid: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM usuario_bodega ub JOIN usuarios u ON ub.usuarioId=u.id JOIN bodegas b ON b.id=ub.bodegaId WHERE ub.usuarioId=? AND ub.bodegaId=? AND u.activo=1 AND b.activa=1 AND u.rol IN ('OPERADOR_BODEGA','JEFE_BODEGA')"
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

  public override suspend fun autorizadoJefe(uid: Long, bid: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM usuario_bodega ub JOIN usuarios u ON ub.usuarioId=u.id JOIN bodegas b ON b.id=ub.bodegaId WHERE ub.usuarioId=? AND ub.bodegaId=? AND u.activo=1 AND b.activa=1 AND u.rol='JEFE_BODEGA'"
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

  public override suspend fun operadorSinBodegas(uid: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM usuarios u WHERE u.id=? AND u.rol='OPERADOR_BODEGA' AND u.activo=1 AND NOT EXISTS (SELECT 1 FROM usuario_bodega ub WHERE ub.usuarioId=u.id)"
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

  public override suspend fun siguienteIdUsuario(): Long {
    val _sql: String = "SELECT COALESCE(MAX(id),0)+1 FROM usuarios"
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

  public override fun operadoresDeBodega(bid: Long): Flow<List<Usuario>> {
    val _sql: String =
        "SELECT u.* FROM usuarios u JOIN usuario_bodega ub ON ub.usuarioId=u.id WHERE u.activo=1 AND u.rol='OPERADOR_BODEGA' AND ub.bodegaId=? ORDER BY u.nombre"
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

  public override suspend fun existeRut(rut: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM usuarios WHERE rut=?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, rut)
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

  public override suspend fun usuarioPorId(id: Long): Usuario? {
    val _sql: String = "SELECT * FROM usuarios WHERE id=? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
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
        val _result: Usuario?
        if (_stmt.step()) {
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
          _result =
              Usuario(_tmpId,_tmpNombre,_tmpCorreo,_tmpClaveHash,_tmpClaveSal,_tmpRol,_tmpActivo,_tmpRut,_tmpSucursalResponsableId)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun operadoresDeSucursalDisponibles(bid: Long): Flow<List<Usuario>> {
    val _sql: String = """
        |SELECT u.* FROM usuarios u WHERE u.rol='OPERADOR_BODEGA' AND u.activo=1
        |        AND NOT EXISTS (SELECT 1 FROM usuario_bodega ub JOIN bodegas b ON b.id=ub.bodegaId
        |            WHERE ub.usuarioId=u.id AND b.sucursalId!=(SELECT origen.sucursalId FROM bodegas origen WHERE origen.id=?))
        |        ORDER BY u.nombre
        """.trimMargin()
    return createFlow(__db, false, arrayOf("usuarios", "usuario_bodega", "bodegas")) {
        _connection ->
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

  public override fun todosLosOperadores(): Flow<List<Usuario>> {
    val _sql: String =
        "SELECT * FROM usuarios WHERE rol='OPERADOR_BODEGA' AND activo=1 ORDER BY nombre"
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

  public override suspend fun operadorActivoDeBodega(uid: Long, bid: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM usuario_bodega ub JOIN usuarios u ON u.id=ub.usuarioId WHERE ub.bodegaId=? AND ub.usuarioId=? AND u.rol='OPERADOR_BODEGA' AND u.activo=1"
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

  public override suspend fun retirarPermiso(uid: Long, bid: Long): Int {
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

  public override suspend fun limpiarBodegasExtraJefes(): Int {
    val _sql: String =
        "DELETE FROM usuario_bodega WHERE usuarioId IN (SELECT id FROM usuarios WHERE rol='JEFE_BODEGA') AND bodegaId NOT IN (SELECT MIN(bodegaId) FROM usuario_bodega AS ub2 WHERE ub2.usuarioId=usuario_bodega.usuarioId)"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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
