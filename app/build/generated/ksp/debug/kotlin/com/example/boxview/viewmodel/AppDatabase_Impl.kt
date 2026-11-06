package com.example.boxview.viewmodel

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.example.boxview.modelo.AbastecimientoExternoDao
import com.example.boxview.modelo.AbastecimientoExternoDao_Impl
import com.example.boxview.modelo.AdministracionDao
import com.example.boxview.modelo.AdministracionDao_Impl
import com.example.boxview.modelo.CajasDao
import com.example.boxview.modelo.CajasDao_Impl
import com.example.boxview.modelo.CampaniasMensualesDao
import com.example.boxview.modelo.CampaniasMensualesDao_Impl
import com.example.boxview.modelo.InformesGerenciaDao
import com.example.boxview.modelo.InformesGerenciaDao_Impl
import com.example.boxview.modelo.InventariosDao
import com.example.boxview.modelo.InventariosDao_Impl
import com.example.boxview.modelo.OperacionesTiendaDao
import com.example.boxview.modelo.OperacionesTiendaDao_Impl
import com.example.boxview.modelo.PreciosVentaDao
import com.example.boxview.modelo.PreciosVentaDao_Impl
import com.example.boxview.modelo.ProductosDao
import com.example.boxview.modelo.ProductosDao_Impl
import com.example.boxview.modelo.TiendasDao
import com.example.boxview.modelo.TiendasDao_Impl
import com.example.boxview.modelo.UsuariosDao
import com.example.boxview.modelo.UsuariosDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _usuariosDao: Lazy<UsuariosDao> = lazy {
    UsuariosDao_Impl(this)
  }

  private val _productosDao: Lazy<ProductosDao> = lazy {
    ProductosDao_Impl(this)
  }

  private val _cajasDao: Lazy<CajasDao> = lazy {
    CajasDao_Impl(this)
  }

  private val _inventariosDao: Lazy<InventariosDao> = lazy {
    InventariosDao_Impl(this)
  }

  private val _tiendasDao: Lazy<TiendasDao> = lazy {
    TiendasDao_Impl(this)
  }

  private val _administracionDao: Lazy<AdministracionDao> = lazy {
    AdministracionDao_Impl(this)
  }

  private val _abastecimientoExternoDao: Lazy<AbastecimientoExternoDao> = lazy {
    AbastecimientoExternoDao_Impl(this)
  }

  private val _campaniasMensualesDao: Lazy<CampaniasMensualesDao> = lazy {
    CampaniasMensualesDao_Impl(this)
  }

  private val _informesGerenciaDao: Lazy<InformesGerenciaDao> = lazy {
    InformesGerenciaDao_Impl(this)
  }

  private val _operacionesTiendaDao: Lazy<OperacionesTiendaDao> = lazy {
    OperacionesTiendaDao_Impl(this)
  }

  private val _preciosVentaDao: Lazy<PreciosVentaDao> = lazy {
    PreciosVentaDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(26,
        "1ba30b8e70003308502522fcecbf03b0", "ad8dad90a1cc87e89825e2ec6249b780") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `usuarios` (`id` INTEGER NOT NULL, `nombre` TEXT NOT NULL, `correo` TEXT NOT NULL, `claveHash` TEXT NOT NULL, `claveSal` TEXT NOT NULL, `rol` TEXT NOT NULL, `activo` INTEGER NOT NULL, `rut` TEXT, `sucursalResponsableId` INTEGER DEFAULT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_usuarios_correo` ON `usuarios` (`correo`)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_usuarios_rut` ON `usuarios` (`rut`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `sucursales` (`id` INTEGER NOT NULL, `nombre` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `bodegas` (`id` INTEGER NOT NULL, `sucursalId` INTEGER NOT NULL, `nombre` TEXT NOT NULL, `activa` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `usuario_bodega` (`usuarioId` INTEGER NOT NULL, `bodegaId` INTEGER NOT NULL, PRIMARY KEY(`usuarioId`, `bodegaId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `posiciones` (`id` INTEGER NOT NULL, `bodegaId` INTEGER NOT NULL, `codigo` TEXT NOT NULL, `nombre` TEXT NOT NULL DEFAULT '', `tipo` TEXT NOT NULL DEFAULT 'ESTANTERIA', `padreId` INTEGER, `activa` INTEGER NOT NULL DEFAULT 1, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `productos` (`id` INTEGER NOT NULL, `codigo` TEXT NOT NULL, `nombre` TEXT NOT NULL, `categoria` TEXT NOT NULL, `minimo` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_productos_codigo` ON `productos` (`codigo`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `existencias` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `productoId` INTEGER NOT NULL, `posicionId` INTEGER NOT NULL, `cantidad` INTEGER NOT NULL)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_existencias_productoId_posicionId` ON `existencias` (`productoId`, `posicionId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `movimientos` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `usuarioId` INTEGER NOT NULL, `bodegaId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `posicionOrigen` INTEGER NOT NULL, `posicionDestino` INTEGER NOT NULL, `cantidad` INTEGER NOT NULL, `fecha` INTEGER NOT NULL, `tipo` TEXT NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `despachos` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `bodegaOrigenId` INTEGER NOT NULL, `bodegaDestinoId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `posicionOrigenId` INTEGER NOT NULL, `cantidad` INTEGER NOT NULL, `preparadorId` INTEGER NOT NULL, `estado` TEXT NOT NULL, `fecha` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `conteos` (`id` INTEGER NOT NULL, `bodegaId` INTEGER NOT NULL, `posicionId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `asignadoA` INTEGER NOT NULL, `cantidadContada` INTEGER, `estado` TEXT NOT NULL, `fecha` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `entregas` (`id` INTEGER NOT NULL, `bodegaId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `esperada` INTEGER NOT NULL, `recibida` INTEGER NOT NULL, `estado` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `cajas` (`id` INTEGER NOT NULL, `codigo` TEXT NOT NULL, `tipo` TEXT NOT NULL, `origen` TEXT NOT NULL, `bodegaOrigenId` INTEGER NOT NULL, `bodegaDestinoId` INTEGER NOT NULL, `solicitadoPor` TEXT NOT NULL, `asignadoA` INTEGER NOT NULL, `estado` TEXT NOT NULL, `preparadoPor` INTEGER, `revisorId` INTEGER, `fecha` INTEGER NOT NULL, `sucursalDestinoId` INTEGER, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_cajas_codigo` ON `cajas` (`codigo`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `detalle_caja` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `cajaId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `cantidadEsperada` INTEGER NOT NULL, `cantidadVerificada` INTEGER NOT NULL, `posicionId` INTEGER, `cantidadRecibida` INTEGER NOT NULL DEFAULT 0, `posicionDestinoId` INTEGER, `cantidadContadaFisica` INTEGER)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_detalle_caja_cajaId_productoId` ON `detalle_caja` (`cajaId`, `productoId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `eventos_caja` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `cajaId` INTEGER NOT NULL, `usuarioId` INTEGER NOT NULL, `accion` TEXT NOT NULL, `observacion` TEXT NOT NULL, `fecha` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `incidencias_caja` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `cajaId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `esperado` INTEGER NOT NULL, `contado` INTEGER NOT NULL, `estado` TEXT NOT NULL, `fecha` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `periodos_inventario` (`id` INTEGER NOT NULL, `bodegaId` INTEGER NOT NULL, `asignadoA` INTEGER NOT NULL, `nombre` TEXT NOT NULL, `estado` TEXT NOT NULL, `fechaApertura` INTEGER NOT NULL, `inventarioBodegaId` INTEGER, `verificacionFisicaPor` INTEGER, `fechaVerificacionFisica` INTEGER, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `lineas_inventario` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `periodoId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `posicionId` INTEGER NOT NULL, `cantidadContada` INTEGER NOT NULL, `stockAlEntregar` INTEGER)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_lineas_inventario_periodoId_productoId_posicionId` ON `lineas_inventario` (`periodoId`, `productoId`, `posicionId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `preparaciones_caja` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `cajaId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `posicionId` INTEGER NOT NULL, `cantidad` INTEGER NOT NULL)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_preparaciones_caja_cajaId_productoId_posicionId` ON `preparaciones_caja` (`cajaId`, `productoId`, `posicionId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `borrador_inventario` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `periodoId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `posicionId` INTEGER NOT NULL, `cantidad` INTEGER NOT NULL)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_borrador_inventario_periodoId_productoId_posicionId` ON `borrador_inventario` (`periodoId`, `productoId`, `posicionId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `ajustes_inventario` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `periodoId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `posicionId` INTEGER NOT NULL, `stockAnterior` INTEGER NOT NULL, `stockNuevo` INTEGER NOT NULL, `revisadoPor` INTEGER NOT NULL, `fecha` INTEGER NOT NULL)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_ajustes_inventario_periodoId_productoId_posicionId` ON `ajustes_inventario` (`periodoId`, `productoId`, `posicionId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `bodega_sucursal` (`bodegaId` INTEGER NOT NULL, `sucursalId` INTEGER NOT NULL, PRIMARY KEY(`bodegaId`, `sucursalId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `existencias_tienda` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sucursalId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `cantidad` INTEGER NOT NULL)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_existencias_tienda_sucursalId_productoId` ON `existencias_tienda` (`sucursalId`, `productoId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `inventarios_bodega` (`id` INTEGER NOT NULL, `bodegaId` INTEGER NOT NULL, `nombre` TEXT NOT NULL, `estado` TEXT NOT NULL, `fechaApertura` INTEGER NOT NULL, `revisadoPor` INTEGER, `campaniaId` INTEGER, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_inventarios_bodega_bodegaId_nombre` ON `inventarios_bodega` (`bodegaId`, `nombre`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `zonas_inventario` (`inventarioId` INTEGER NOT NULL, `posicionId` INTEGER NOT NULL, `operadorId` INTEGER NOT NULL, `periodoId` INTEGER NOT NULL, PRIMARY KEY(`inventarioId`, `posicionId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `referencias_inventario` (`inventarioId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `posicionId` INTEGER NOT NULL, `cantidad` INTEGER NOT NULL, PRIMARY KEY(`inventarioId`, `productoId`, `posicionId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `usuario_sucursal` (`usuarioId` INTEGER NOT NULL, `sucursalId` INTEGER NOT NULL, PRIMARY KEY(`usuarioId`, `sucursalId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `movimientos_tienda` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `usuarioId` INTEGER NOT NULL, `sucursalId` INTEGER NOT NULL, `cajaId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `cantidad` INTEGER NOT NULL, `fecha` INTEGER NOT NULL)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_movimientos_tienda_cajaId_productoId` ON `movimientos_tienda` (`cajaId`, `productoId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `posiciones_tienda` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sucursalId` INTEGER NOT NULL, `codigo` TEXT NOT NULL, `zona` TEXT NOT NULL, `categoria` TEXT NOT NULL, `activa` INTEGER NOT NULL, `nombre` TEXT NOT NULL DEFAULT '', `tipo` TEXT NOT NULL DEFAULT 'ESTANTERIA', `padreId` INTEGER)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_posiciones_tienda_sucursalId_codigo` ON `posiciones_tienda` (`sucursalId`, `codigo`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `existencias_ubicacion_tienda` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sucursalId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `posicionId` INTEGER NOT NULL, `cantidad` INTEGER NOT NULL)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_existencias_ubicacion_tienda_sucursalId_productoId_posicionId` ON `existencias_ubicacion_tienda` (`sucursalId`, `productoId`, `posicionId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `movimientos_ubicacion_tienda` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `usuarioId` INTEGER NOT NULL, `sucursalId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `posicionOrigenId` INTEGER, `posicionDestinoId` INTEGER NOT NULL, `cantidad` INTEGER NOT NULL, `tipo` TEXT NOT NULL, `cajaId` INTEGER, `fecha` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `solicitudes_reposicion` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sucursalId` INTEGER NOT NULL, `bodegaId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `cantidad` INTEGER NOT NULL, `solicitadoPor` INTEGER NOT NULL, `fecha` INTEGER NOT NULL, `estado` TEXT NOT NULL, `cajaId` INTEGER, `loteId` TEXT)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_solicitudes_reposicion_sucursalId_estado` ON `solicitudes_reposicion` (`sucursalId`, `estado`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `configuracion_sucursal` (`sucursalId` INTEGER NOT NULL, `bodegaPrincipalId` INTEGER, PRIMARY KEY(`sucursalId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `minimos_sucursal` (`sucursalId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `minimo` INTEGER NOT NULL, PRIMARY KEY(`sucursalId`, `productoId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `eventos_administracion` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sucursalId` INTEGER NOT NULL, `usuarioId` INTEGER NOT NULL, `accion` TEXT NOT NULL, `detalle` TEXT NOT NULL, `fecha` INTEGER NOT NULL)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_eventos_administracion_sucursalId` ON `eventos_administracion` (`sucursalId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `solicitudes_externas` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sucursalId` INTEGER NOT NULL, `bodegaId` INTEGER NOT NULL, `solicitanteId` INTEGER NOT NULL, `motivo` TEXT NOT NULL, `estado` TEXT NOT NULL, `fecha` INTEGER NOT NULL, `fechaRevision` INTEGER, `revisadoPor` INTEGER, `fechaDecision` INTEGER, `decididoPor` INTEGER, `observacion` TEXT NOT NULL, `entregaCajaId` INTEGER, `proveedorCotizado` TEXT NOT NULL DEFAULT '')")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_solicitudes_externas_sucursalId_estado` ON `solicitudes_externas` (`sucursalId`, `estado`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_solicitudes_externas_bodegaId` ON `solicitudes_externas` (`bodegaId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `lineas_solicitud_externa` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `solicitudId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `cantidad` INTEGER NOT NULL, `precioUnitarioCotizado` INTEGER)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_lineas_solicitud_externa_solicitudId` ON `lineas_solicitud_externa` (`solicitudId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `eventos_solicitud_externa` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `solicitudId` INTEGER NOT NULL, `usuarioId` INTEGER NOT NULL, `accion` TEXT NOT NULL, `detalle` TEXT NOT NULL, `fecha` INTEGER NOT NULL)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_eventos_solicitud_externa_solicitudId` ON `eventos_solicitud_externa` (`solicitudId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `informes_inventario` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `inventarioId` INTEGER NOT NULL, `sucursalId` INTEGER NOT NULL, `bodegaId` INTEGER NOT NULL, `sucursalNombre` TEXT NOT NULL, `bodegaNombre` TEXT NOT NULL, `nombre` TEXT NOT NULL, `estadoCierre` TEXT NOT NULL, `fechaApertura` INTEGER NOT NULL, `fechaCierre` INTEGER NOT NULL, `cerradoPor` INTEGER NOT NULL, `posiciones` INTEGER NOT NULL, `productosContados` INTEGER NOT NULL, `productosSinContar` INTEGER NOT NULL, `lineasConDiferencia` INTEGER NOT NULL, `unidadesSistema` INTEGER NOT NULL, `unidadesContadas` INTEGER NOT NULL, `ajustesAplicados` INTEGER NOT NULL, `estadoRevision` TEXT NOT NULL, `revisadoPor` INTEGER, `fechaRevision` INTEGER)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_informes_inventario_inventarioId` ON `informes_inventario` (`inventarioId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_informes_inventario_estadoRevision_fechaCierre` ON `informes_inventario` (`estadoRevision`, `fechaCierre`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `detalles_informe_inventario` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `informeId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `codigo` TEXT NOT NULL, `producto` TEXT NOT NULL, `posicion` TEXT NOT NULL, `cantidadSistema` INTEGER NOT NULL, `cantidadContada` INTEGER, `ajusteAplicado` INTEGER NOT NULL)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_detalles_informe_inventario_informeId` ON `detalles_informe_inventario` (`informeId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `salidas_tienda` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sucursalId` INTEGER NOT NULL, `usuarioId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `posicionId` INTEGER NOT NULL, `cantidad` INTEGER NOT NULL, `tipo` TEXT NOT NULL, `motivo` TEXT NOT NULL, `fecha` INTEGER NOT NULL, `precioUnitarioVentaClp` INTEGER, `totalVentaClp` INTEGER)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `inventarios_tienda` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sucursalId` INTEGER NOT NULL, `nombre` TEXT NOT NULL, `estado` TEXT NOT NULL, `fechaApertura` INTEGER NOT NULL, `cerradoPor` INTEGER, `fechaCierre` INTEGER, `estadoRevision` TEXT NOT NULL, `revisadoPor` INTEGER, `fechaRevision` INTEGER, `motivoCierre` TEXT NOT NULL, `verificacionVaciaPor` INTEGER, `fechaVerificacionVacia` INTEGER, `campaniaId` INTEGER)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_inventarios_tienda_sucursalId` ON `inventarios_tienda` (`sucursalId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `lineas_inventario_tienda` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `inventarioId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `posicionId` INTEGER NOT NULL, `stockInicio` INTEGER NOT NULL, `cantidadContada` INTEGER, `contadoPor` INTEGER, `fechaConteo` INTEGER, `codigoProductoCierre` TEXT, `nombreProductoCierre` TEXT, `rutaUbicacionCierre` TEXT)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_lineas_inventario_tienda_inventarioId_productoId_posicionId` ON `lineas_inventario_tienda` (`inventarioId`, `productoId`, `posicionId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `eventos_conteo_tienda` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `inventarioId` INTEGER NOT NULL, `lineaId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `posicionId` INTEGER NOT NULL, `usuarioId` INTEGER NOT NULL, `cantidadAnterior` INTEGER, `cantidadNueva` INTEGER NOT NULL, `tipo` TEXT NOT NULL, `fecha` INTEGER NOT NULL)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_eventos_conteo_tienda_inventarioId_fecha` ON `eventos_conteo_tienda` (`inventarioId`, `fecha`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `zonas_conteo_tienda` (`inventarioId` INTEGER NOT NULL, `posicionId` INTEGER NOT NULL, `asignadoA` INTEGER NOT NULL, `confirmadoPor` INTEGER, `fechaConfirmacion` INTEGER, PRIMARY KEY(`inventarioId`, `posicionId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `verificaciones_estante_vacio` (`tipo` TEXT NOT NULL, `inventarioId` INTEGER NOT NULL, `posicionId` INTEGER NOT NULL, `usuarioId` INTEGER NOT NULL, `fecha` INTEGER NOT NULL, PRIMARY KEY(`tipo`, `inventarioId`, `posicionId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `campanias_mensuales` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `tipo` TEXT NOT NULL, `ubicacionId` INTEGER NOT NULL, `sucursalId` INTEGER NOT NULL, `mes` TEXT NOT NULL, `estado` TEXT NOT NULL, `fechaInicio` INTEGER NOT NULL, `fechaFinal` INTEGER, `revision` TEXT NOT NULL, `revisadaPor` INTEGER, `fechaRevision` INTEGER)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_campanias_mensuales_tipo_ubicacionId_mes` ON `campanias_mensuales` (`tipo`, `ubicacionId`, `mes`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `zonas_campania` (`campaniaId` INTEGER NOT NULL, `posicionId` INTEGER NOT NULL, `ruta` TEXT NOT NULL, `estado` TEXT NOT NULL, `inventarioId` INTEGER, `fechaConteo` INTEGER, PRIMARY KEY(`campaniaId`, `posicionId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `detalles_campania` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `campaniaId` INTEGER NOT NULL, `inventarioId` INTEGER NOT NULL, `posicionId` INTEGER NOT NULL, `codigo` TEXT NOT NULL, `producto` TEXT NOT NULL, `ruta` TEXT NOT NULL, `cantidadSistema` INTEGER NOT NULL, `cantidadContada` INTEGER, `ajusteAplicado` INTEGER NOT NULL, `fechaCierre` INTEGER NOT NULL)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_detalles_campania_campaniaId_posicionId` ON `detalles_campania` (`campaniaId`, `posicionId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `precios_venta_sucursal` (`sucursalId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `precioClp` INTEGER NOT NULL, `actualizadoPor` INTEGER NOT NULL, `fechaActualizacion` INTEGER NOT NULL, PRIMARY KEY(`sucursalId`, `productoId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '1ba30b8e70003308502522fcecbf03b0')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `usuarios`")
        connection.execSQL("DROP TABLE IF EXISTS `sucursales`")
        connection.execSQL("DROP TABLE IF EXISTS `bodegas`")
        connection.execSQL("DROP TABLE IF EXISTS `usuario_bodega`")
        connection.execSQL("DROP TABLE IF EXISTS `posiciones`")
        connection.execSQL("DROP TABLE IF EXISTS `productos`")
        connection.execSQL("DROP TABLE IF EXISTS `existencias`")
        connection.execSQL("DROP TABLE IF EXISTS `movimientos`")
        connection.execSQL("DROP TABLE IF EXISTS `despachos`")
        connection.execSQL("DROP TABLE IF EXISTS `conteos`")
        connection.execSQL("DROP TABLE IF EXISTS `entregas`")
        connection.execSQL("DROP TABLE IF EXISTS `cajas`")
        connection.execSQL("DROP TABLE IF EXISTS `detalle_caja`")
        connection.execSQL("DROP TABLE IF EXISTS `eventos_caja`")
        connection.execSQL("DROP TABLE IF EXISTS `incidencias_caja`")
        connection.execSQL("DROP TABLE IF EXISTS `periodos_inventario`")
        connection.execSQL("DROP TABLE IF EXISTS `lineas_inventario`")
        connection.execSQL("DROP TABLE IF EXISTS `preparaciones_caja`")
        connection.execSQL("DROP TABLE IF EXISTS `borrador_inventario`")
        connection.execSQL("DROP TABLE IF EXISTS `ajustes_inventario`")
        connection.execSQL("DROP TABLE IF EXISTS `bodega_sucursal`")
        connection.execSQL("DROP TABLE IF EXISTS `existencias_tienda`")
        connection.execSQL("DROP TABLE IF EXISTS `inventarios_bodega`")
        connection.execSQL("DROP TABLE IF EXISTS `zonas_inventario`")
        connection.execSQL("DROP TABLE IF EXISTS `referencias_inventario`")
        connection.execSQL("DROP TABLE IF EXISTS `usuario_sucursal`")
        connection.execSQL("DROP TABLE IF EXISTS `movimientos_tienda`")
        connection.execSQL("DROP TABLE IF EXISTS `posiciones_tienda`")
        connection.execSQL("DROP TABLE IF EXISTS `existencias_ubicacion_tienda`")
        connection.execSQL("DROP TABLE IF EXISTS `movimientos_ubicacion_tienda`")
        connection.execSQL("DROP TABLE IF EXISTS `solicitudes_reposicion`")
        connection.execSQL("DROP TABLE IF EXISTS `configuracion_sucursal`")
        connection.execSQL("DROP TABLE IF EXISTS `minimos_sucursal`")
        connection.execSQL("DROP TABLE IF EXISTS `eventos_administracion`")
        connection.execSQL("DROP TABLE IF EXISTS `solicitudes_externas`")
        connection.execSQL("DROP TABLE IF EXISTS `lineas_solicitud_externa`")
        connection.execSQL("DROP TABLE IF EXISTS `eventos_solicitud_externa`")
        connection.execSQL("DROP TABLE IF EXISTS `informes_inventario`")
        connection.execSQL("DROP TABLE IF EXISTS `detalles_informe_inventario`")
        connection.execSQL("DROP TABLE IF EXISTS `salidas_tienda`")
        connection.execSQL("DROP TABLE IF EXISTS `inventarios_tienda`")
        connection.execSQL("DROP TABLE IF EXISTS `lineas_inventario_tienda`")
        connection.execSQL("DROP TABLE IF EXISTS `eventos_conteo_tienda`")
        connection.execSQL("DROP TABLE IF EXISTS `zonas_conteo_tienda`")
        connection.execSQL("DROP TABLE IF EXISTS `verificaciones_estante_vacio`")
        connection.execSQL("DROP TABLE IF EXISTS `campanias_mensuales`")
        connection.execSQL("DROP TABLE IF EXISTS `zonas_campania`")
        connection.execSQL("DROP TABLE IF EXISTS `detalles_campania`")
        connection.execSQL("DROP TABLE IF EXISTS `precios_venta_sucursal`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsUsuarios: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsUsuarios.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsuarios.put("nombre", TableInfo.Column("nombre", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsuarios.put("correo", TableInfo.Column("correo", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsuarios.put("claveHash", TableInfo.Column("claveHash", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsuarios.put("claveSal", TableInfo.Column("claveSal", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsuarios.put("rol", TableInfo.Column("rol", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsuarios.put("activo", TableInfo.Column("activo", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsuarios.put("rut", TableInfo.Column("rut", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsuarios.put("sucursalResponsableId", TableInfo.Column("sucursalResponsableId",
            "INTEGER", false, 0, "NULL", TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysUsuarios: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesUsuarios: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesUsuarios.add(TableInfo.Index("index_usuarios_correo", true, listOf("correo"),
            listOf("ASC")))
        _indicesUsuarios.add(TableInfo.Index("index_usuarios_rut", true, listOf("rut"),
            listOf("ASC")))
        val _infoUsuarios: TableInfo = TableInfo("usuarios", _columnsUsuarios, _foreignKeysUsuarios,
            _indicesUsuarios)
        val _existingUsuarios: TableInfo = read(connection, "usuarios")
        if (!_infoUsuarios.equals(_existingUsuarios)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |usuarios(com.example.boxview.modelo.Usuario).
              | Expected:
              |""".trimMargin() + _infoUsuarios + """
              |
              | Found:
              |""".trimMargin() + _existingUsuarios)
        }
        val _columnsSucursales: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsSucursales.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSucursales.put("nombre", TableInfo.Column("nombre", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSucursales: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesSucursales: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoSucursales: TableInfo = TableInfo("sucursales", _columnsSucursales,
            _foreignKeysSucursales, _indicesSucursales)
        val _existingSucursales: TableInfo = read(connection, "sucursales")
        if (!_infoSucursales.equals(_existingSucursales)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |sucursales(com.example.boxview.modelo.Sucursal).
              | Expected:
              |""".trimMargin() + _infoSucursales + """
              |
              | Found:
              |""".trimMargin() + _existingSucursales)
        }
        val _columnsBodegas: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsBodegas.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBodegas.put("sucursalId", TableInfo.Column("sucursalId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBodegas.put("nombre", TableInfo.Column("nombre", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBodegas.put("activa", TableInfo.Column("activa", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysBodegas: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesBodegas: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoBodegas: TableInfo = TableInfo("bodegas", _columnsBodegas, _foreignKeysBodegas,
            _indicesBodegas)
        val _existingBodegas: TableInfo = read(connection, "bodegas")
        if (!_infoBodegas.equals(_existingBodegas)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |bodegas(com.example.boxview.modelo.Bodega).
              | Expected:
              |""".trimMargin() + _infoBodegas + """
              |
              | Found:
              |""".trimMargin() + _existingBodegas)
        }
        val _columnsUsuarioBodega: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsUsuarioBodega.put("usuarioId", TableInfo.Column("usuarioId", "INTEGER", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsuarioBodega.put("bodegaId", TableInfo.Column("bodegaId", "INTEGER", true, 2, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysUsuarioBodega: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesUsuarioBodega: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoUsuarioBodega: TableInfo = TableInfo("usuario_bodega", _columnsUsuarioBodega,
            _foreignKeysUsuarioBodega, _indicesUsuarioBodega)
        val _existingUsuarioBodega: TableInfo = read(connection, "usuario_bodega")
        if (!_infoUsuarioBodega.equals(_existingUsuarioBodega)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |usuario_bodega(com.example.boxview.modelo.UsuarioBodega).
              | Expected:
              |""".trimMargin() + _infoUsuarioBodega + """
              |
              | Found:
              |""".trimMargin() + _existingUsuarioBodega)
        }
        val _columnsPosiciones: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsPosiciones.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosiciones.put("bodegaId", TableInfo.Column("bodegaId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosiciones.put("codigo", TableInfo.Column("codigo", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosiciones.put("nombre", TableInfo.Column("nombre", "TEXT", true, 0, "''",
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosiciones.put("tipo", TableInfo.Column("tipo", "TEXT", true, 0, "'ESTANTERIA'",
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosiciones.put("padreId", TableInfo.Column("padreId", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosiciones.put("activa", TableInfo.Column("activa", "INTEGER", true, 0, "1",
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysPosiciones: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesPosiciones: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoPosiciones: TableInfo = TableInfo("posiciones", _columnsPosiciones,
            _foreignKeysPosiciones, _indicesPosiciones)
        val _existingPosiciones: TableInfo = read(connection, "posiciones")
        if (!_infoPosiciones.equals(_existingPosiciones)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |posiciones(com.example.boxview.modelo.Posicion).
              | Expected:
              |""".trimMargin() + _infoPosiciones + """
              |
              | Found:
              |""".trimMargin() + _existingPosiciones)
        }
        val _columnsProductos: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsProductos.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProductos.put("codigo", TableInfo.Column("codigo", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProductos.put("nombre", TableInfo.Column("nombre", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProductos.put("categoria", TableInfo.Column("categoria", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProductos.put("minimo", TableInfo.Column("minimo", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysProductos: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesProductos: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesProductos.add(TableInfo.Index("index_productos_codigo", true, listOf("codigo"),
            listOf("ASC")))
        val _infoProductos: TableInfo = TableInfo("productos", _columnsProductos,
            _foreignKeysProductos, _indicesProductos)
        val _existingProductos: TableInfo = read(connection, "productos")
        if (!_infoProductos.equals(_existingProductos)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |productos(com.example.boxview.modelo.Producto).
              | Expected:
              |""".trimMargin() + _infoProductos + """
              |
              | Found:
              |""".trimMargin() + _existingProductos)
        }
        val _columnsExistencias: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsExistencias.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExistencias.put("productoId", TableInfo.Column("productoId", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExistencias.put("posicionId", TableInfo.Column("posicionId", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExistencias.put("cantidad", TableInfo.Column("cantidad", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysExistencias: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesExistencias: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesExistencias.add(TableInfo.Index("index_existencias_productoId_posicionId", true,
            listOf("productoId", "posicionId"), listOf("ASC", "ASC")))
        val _infoExistencias: TableInfo = TableInfo("existencias", _columnsExistencias,
            _foreignKeysExistencias, _indicesExistencias)
        val _existingExistencias: TableInfo = read(connection, "existencias")
        if (!_infoExistencias.equals(_existingExistencias)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |existencias(com.example.boxview.modelo.Existencia).
              | Expected:
              |""".trimMargin() + _infoExistencias + """
              |
              | Found:
              |""".trimMargin() + _existingExistencias)
        }
        val _columnsMovimientos: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsMovimientos.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMovimientos.put("usuarioId", TableInfo.Column("usuarioId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMovimientos.put("bodegaId", TableInfo.Column("bodegaId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMovimientos.put("productoId", TableInfo.Column("productoId", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMovimientos.put("posicionOrigen", TableInfo.Column("posicionOrigen", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMovimientos.put("posicionDestino", TableInfo.Column("posicionDestino", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMovimientos.put("cantidad", TableInfo.Column("cantidad", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMovimientos.put("fecha", TableInfo.Column("fecha", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMovimientos.put("tipo", TableInfo.Column("tipo", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysMovimientos: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesMovimientos: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoMovimientos: TableInfo = TableInfo("movimientos", _columnsMovimientos,
            _foreignKeysMovimientos, _indicesMovimientos)
        val _existingMovimientos: TableInfo = read(connection, "movimientos")
        if (!_infoMovimientos.equals(_existingMovimientos)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |movimientos(com.example.boxview.modelo.Movimiento).
              | Expected:
              |""".trimMargin() + _infoMovimientos + """
              |
              | Found:
              |""".trimMargin() + _existingMovimientos)
        }
        val _columnsDespachos: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsDespachos.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDespachos.put("bodegaOrigenId", TableInfo.Column("bodegaOrigenId", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDespachos.put("bodegaDestinoId", TableInfo.Column("bodegaDestinoId", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDespachos.put("productoId", TableInfo.Column("productoId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDespachos.put("posicionOrigenId", TableInfo.Column("posicionOrigenId", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDespachos.put("cantidad", TableInfo.Column("cantidad", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDespachos.put("preparadorId", TableInfo.Column("preparadorId", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDespachos.put("estado", TableInfo.Column("estado", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDespachos.put("fecha", TableInfo.Column("fecha", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysDespachos: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesDespachos: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoDespachos: TableInfo = TableInfo("despachos", _columnsDespachos,
            _foreignKeysDespachos, _indicesDespachos)
        val _existingDespachos: TableInfo = read(connection, "despachos")
        if (!_infoDespachos.equals(_existingDespachos)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |despachos(com.example.boxview.modelo.Despacho).
              | Expected:
              |""".trimMargin() + _infoDespachos + """
              |
              | Found:
              |""".trimMargin() + _existingDespachos)
        }
        val _columnsConteos: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsConteos.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsConteos.put("bodegaId", TableInfo.Column("bodegaId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsConteos.put("posicionId", TableInfo.Column("posicionId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsConteos.put("productoId", TableInfo.Column("productoId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsConteos.put("asignadoA", TableInfo.Column("asignadoA", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsConteos.put("cantidadContada", TableInfo.Column("cantidadContada", "INTEGER", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsConteos.put("estado", TableInfo.Column("estado", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsConteos.put("fecha", TableInfo.Column("fecha", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysConteos: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesConteos: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoConteos: TableInfo = TableInfo("conteos", _columnsConteos, _foreignKeysConteos,
            _indicesConteos)
        val _existingConteos: TableInfo = read(connection, "conteos")
        if (!_infoConteos.equals(_existingConteos)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |conteos(com.example.boxview.modelo.Conteo).
              | Expected:
              |""".trimMargin() + _infoConteos + """
              |
              | Found:
              |""".trimMargin() + _existingConteos)
        }
        val _columnsEntregas: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsEntregas.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEntregas.put("bodegaId", TableInfo.Column("bodegaId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEntregas.put("productoId", TableInfo.Column("productoId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEntregas.put("esperada", TableInfo.Column("esperada", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEntregas.put("recibida", TableInfo.Column("recibida", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEntregas.put("estado", TableInfo.Column("estado", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysEntregas: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesEntregas: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoEntregas: TableInfo = TableInfo("entregas", _columnsEntregas, _foreignKeysEntregas,
            _indicesEntregas)
        val _existingEntregas: TableInfo = read(connection, "entregas")
        if (!_infoEntregas.equals(_existingEntregas)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |entregas(com.example.boxview.modelo.EntregaAutorizada).
              | Expected:
              |""".trimMargin() + _infoEntregas + """
              |
              | Found:
              |""".trimMargin() + _existingEntregas)
        }
        val _columnsCajas: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsCajas.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCajas.put("codigo", TableInfo.Column("codigo", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCajas.put("tipo", TableInfo.Column("tipo", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCajas.put("origen", TableInfo.Column("origen", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCajas.put("bodegaOrigenId", TableInfo.Column("bodegaOrigenId", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCajas.put("bodegaDestinoId", TableInfo.Column("bodegaDestinoId", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCajas.put("solicitadoPor", TableInfo.Column("solicitadoPor", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCajas.put("asignadoA", TableInfo.Column("asignadoA", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCajas.put("estado", TableInfo.Column("estado", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCajas.put("preparadoPor", TableInfo.Column("preparadoPor", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCajas.put("revisorId", TableInfo.Column("revisorId", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCajas.put("fecha", TableInfo.Column("fecha", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCajas.put("sucursalDestinoId", TableInfo.Column("sucursalDestinoId", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCajas: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesCajas: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesCajas.add(TableInfo.Index("index_cajas_codigo", true, listOf("codigo"),
            listOf("ASC")))
        val _infoCajas: TableInfo = TableInfo("cajas", _columnsCajas, _foreignKeysCajas,
            _indicesCajas)
        val _existingCajas: TableInfo = read(connection, "cajas")
        if (!_infoCajas.equals(_existingCajas)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |cajas(com.example.boxview.modelo.Caja).
              | Expected:
              |""".trimMargin() + _infoCajas + """
              |
              | Found:
              |""".trimMargin() + _existingCajas)
        }
        val _columnsDetalleCaja: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsDetalleCaja.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDetalleCaja.put("cajaId", TableInfo.Column("cajaId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDetalleCaja.put("productoId", TableInfo.Column("productoId", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDetalleCaja.put("cantidadEsperada", TableInfo.Column("cantidadEsperada", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDetalleCaja.put("cantidadVerificada", TableInfo.Column("cantidadVerificada",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDetalleCaja.put("posicionId", TableInfo.Column("posicionId", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDetalleCaja.put("cantidadRecibida", TableInfo.Column("cantidadRecibida", "INTEGER",
            true, 0, "0", TableInfo.CREATED_FROM_ENTITY))
        _columnsDetalleCaja.put("posicionDestinoId", TableInfo.Column("posicionDestinoId",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDetalleCaja.put("cantidadContadaFisica", TableInfo.Column("cantidadContadaFisica",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysDetalleCaja: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesDetalleCaja: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesDetalleCaja.add(TableInfo.Index("index_detalle_caja_cajaId_productoId", true,
            listOf("cajaId", "productoId"), listOf("ASC", "ASC")))
        val _infoDetalleCaja: TableInfo = TableInfo("detalle_caja", _columnsDetalleCaja,
            _foreignKeysDetalleCaja, _indicesDetalleCaja)
        val _existingDetalleCaja: TableInfo = read(connection, "detalle_caja")
        if (!_infoDetalleCaja.equals(_existingDetalleCaja)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |detalle_caja(com.example.boxview.modelo.DetalleCaja).
              | Expected:
              |""".trimMargin() + _infoDetalleCaja + """
              |
              | Found:
              |""".trimMargin() + _existingDetalleCaja)
        }
        val _columnsEventosCaja: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsEventosCaja.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEventosCaja.put("cajaId", TableInfo.Column("cajaId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEventosCaja.put("usuarioId", TableInfo.Column("usuarioId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEventosCaja.put("accion", TableInfo.Column("accion", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEventosCaja.put("observacion", TableInfo.Column("observacion", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEventosCaja.put("fecha", TableInfo.Column("fecha", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysEventosCaja: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesEventosCaja: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoEventosCaja: TableInfo = TableInfo("eventos_caja", _columnsEventosCaja,
            _foreignKeysEventosCaja, _indicesEventosCaja)
        val _existingEventosCaja: TableInfo = read(connection, "eventos_caja")
        if (!_infoEventosCaja.equals(_existingEventosCaja)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |eventos_caja(com.example.boxview.modelo.EventoCaja).
              | Expected:
              |""".trimMargin() + _infoEventosCaja + """
              |
              | Found:
              |""".trimMargin() + _existingEventosCaja)
        }
        val _columnsIncidenciasCaja: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsIncidenciasCaja.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsIncidenciasCaja.put("cajaId", TableInfo.Column("cajaId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsIncidenciasCaja.put("productoId", TableInfo.Column("productoId", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsIncidenciasCaja.put("esperado", TableInfo.Column("esperado", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsIncidenciasCaja.put("contado", TableInfo.Column("contado", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsIncidenciasCaja.put("estado", TableInfo.Column("estado", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsIncidenciasCaja.put("fecha", TableInfo.Column("fecha", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysIncidenciasCaja: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesIncidenciasCaja: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoIncidenciasCaja: TableInfo = TableInfo("incidencias_caja", _columnsIncidenciasCaja,
            _foreignKeysIncidenciasCaja, _indicesIncidenciasCaja)
        val _existingIncidenciasCaja: TableInfo = read(connection, "incidencias_caja")
        if (!_infoIncidenciasCaja.equals(_existingIncidenciasCaja)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |incidencias_caja(com.example.boxview.modelo.IncidenciaCaja).
              | Expected:
              |""".trimMargin() + _infoIncidenciasCaja + """
              |
              | Found:
              |""".trimMargin() + _existingIncidenciasCaja)
        }
        val _columnsPeriodosInventario: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsPeriodosInventario.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPeriodosInventario.put("bodegaId", TableInfo.Column("bodegaId", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPeriodosInventario.put("asignadoA", TableInfo.Column("asignadoA", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPeriodosInventario.put("nombre", TableInfo.Column("nombre", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPeriodosInventario.put("estado", TableInfo.Column("estado", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPeriodosInventario.put("fechaApertura", TableInfo.Column("fechaApertura", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPeriodosInventario.put("inventarioBodegaId", TableInfo.Column("inventarioBodegaId",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPeriodosInventario.put("verificacionFisicaPor",
            TableInfo.Column("verificacionFisicaPor", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPeriodosInventario.put("fechaVerificacionFisica",
            TableInfo.Column("fechaVerificacionFisica", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysPeriodosInventario: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesPeriodosInventario: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoPeriodosInventario: TableInfo = TableInfo("periodos_inventario",
            _columnsPeriodosInventario, _foreignKeysPeriodosInventario, _indicesPeriodosInventario)
        val _existingPeriodosInventario: TableInfo = read(connection, "periodos_inventario")
        if (!_infoPeriodosInventario.equals(_existingPeriodosInventario)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |periodos_inventario(com.example.boxview.modelo.PeriodoInventario).
              | Expected:
              |""".trimMargin() + _infoPeriodosInventario + """
              |
              | Found:
              |""".trimMargin() + _existingPeriodosInventario)
        }
        val _columnsLineasInventario: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsLineasInventario.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLineasInventario.put("periodoId", TableInfo.Column("periodoId", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLineasInventario.put("productoId", TableInfo.Column("productoId", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLineasInventario.put("posicionId", TableInfo.Column("posicionId", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLineasInventario.put("cantidadContada", TableInfo.Column("cantidadContada",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLineasInventario.put("stockAlEntregar", TableInfo.Column("stockAlEntregar",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysLineasInventario: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesLineasInventario: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesLineasInventario.add(TableInfo.Index("index_lineas_inventario_periodoId_productoId_posicionId",
            true, listOf("periodoId", "productoId", "posicionId"), listOf("ASC", "ASC", "ASC")))
        val _infoLineasInventario: TableInfo = TableInfo("lineas_inventario",
            _columnsLineasInventario, _foreignKeysLineasInventario, _indicesLineasInventario)
        val _existingLineasInventario: TableInfo = read(connection, "lineas_inventario")
        if (!_infoLineasInventario.equals(_existingLineasInventario)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |lineas_inventario(com.example.boxview.modelo.LineaInventario).
              | Expected:
              |""".trimMargin() + _infoLineasInventario + """
              |
              | Found:
              |""".trimMargin() + _existingLineasInventario)
        }
        val _columnsPreparacionesCaja: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsPreparacionesCaja.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPreparacionesCaja.put("cajaId", TableInfo.Column("cajaId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPreparacionesCaja.put("productoId", TableInfo.Column("productoId", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPreparacionesCaja.put("posicionId", TableInfo.Column("posicionId", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPreparacionesCaja.put("cantidad", TableInfo.Column("cantidad", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysPreparacionesCaja: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesPreparacionesCaja: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesPreparacionesCaja.add(TableInfo.Index("index_preparaciones_caja_cajaId_productoId_posicionId",
            true, listOf("cajaId", "productoId", "posicionId"), listOf("ASC", "ASC", "ASC")))
        val _infoPreparacionesCaja: TableInfo = TableInfo("preparaciones_caja",
            _columnsPreparacionesCaja, _foreignKeysPreparacionesCaja, _indicesPreparacionesCaja)
        val _existingPreparacionesCaja: TableInfo = read(connection, "preparaciones_caja")
        if (!_infoPreparacionesCaja.equals(_existingPreparacionesCaja)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |preparaciones_caja(com.example.boxview.modelo.PreparacionCaja).
              | Expected:
              |""".trimMargin() + _infoPreparacionesCaja + """
              |
              | Found:
              |""".trimMargin() + _existingPreparacionesCaja)
        }
        val _columnsBorradorInventario: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsBorradorInventario.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBorradorInventario.put("periodoId", TableInfo.Column("periodoId", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBorradorInventario.put("productoId", TableInfo.Column("productoId", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBorradorInventario.put("posicionId", TableInfo.Column("posicionId", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBorradorInventario.put("cantidad", TableInfo.Column("cantidad", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysBorradorInventario: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesBorradorInventario: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesBorradorInventario.add(TableInfo.Index("index_borrador_inventario_periodoId_productoId_posicionId",
            true, listOf("periodoId", "productoId", "posicionId"), listOf("ASC", "ASC", "ASC")))
        val _infoBorradorInventario: TableInfo = TableInfo("borrador_inventario",
            _columnsBorradorInventario, _foreignKeysBorradorInventario, _indicesBorradorInventario)
        val _existingBorradorInventario: TableInfo = read(connection, "borrador_inventario")
        if (!_infoBorradorInventario.equals(_existingBorradorInventario)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |borrador_inventario(com.example.boxview.modelo.BorradorInventario).
              | Expected:
              |""".trimMargin() + _infoBorradorInventario + """
              |
              | Found:
              |""".trimMargin() + _existingBorradorInventario)
        }
        val _columnsAjustesInventario: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsAjustesInventario.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAjustesInventario.put("periodoId", TableInfo.Column("periodoId", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAjustesInventario.put("productoId", TableInfo.Column("productoId", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAjustesInventario.put("posicionId", TableInfo.Column("posicionId", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAjustesInventario.put("stockAnterior", TableInfo.Column("stockAnterior", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAjustesInventario.put("stockNuevo", TableInfo.Column("stockNuevo", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAjustesInventario.put("revisadoPor", TableInfo.Column("revisadoPor", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAjustesInventario.put("fecha", TableInfo.Column("fecha", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysAjustesInventario: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesAjustesInventario: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesAjustesInventario.add(TableInfo.Index("index_ajustes_inventario_periodoId_productoId_posicionId",
            true, listOf("periodoId", "productoId", "posicionId"), listOf("ASC", "ASC", "ASC")))
        val _infoAjustesInventario: TableInfo = TableInfo("ajustes_inventario",
            _columnsAjustesInventario, _foreignKeysAjustesInventario, _indicesAjustesInventario)
        val _existingAjustesInventario: TableInfo = read(connection, "ajustes_inventario")
        if (!_infoAjustesInventario.equals(_existingAjustesInventario)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |ajustes_inventario(com.example.boxview.modelo.AjusteInventario).
              | Expected:
              |""".trimMargin() + _infoAjustesInventario + """
              |
              | Found:
              |""".trimMargin() + _existingAjustesInventario)
        }
        val _columnsBodegaSucursal: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsBodegaSucursal.put("bodegaId", TableInfo.Column("bodegaId", "INTEGER", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBodegaSucursal.put("sucursalId", TableInfo.Column("sucursalId", "INTEGER", true, 2,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysBodegaSucursal: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesBodegaSucursal: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoBodegaSucursal: TableInfo = TableInfo("bodega_sucursal", _columnsBodegaSucursal,
            _foreignKeysBodegaSucursal, _indicesBodegaSucursal)
        val _existingBodegaSucursal: TableInfo = read(connection, "bodega_sucursal")
        if (!_infoBodegaSucursal.equals(_existingBodegaSucursal)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |bodega_sucursal(com.example.boxview.modelo.BodegaSucursal).
              | Expected:
              |""".trimMargin() + _infoBodegaSucursal + """
              |
              | Found:
              |""".trimMargin() + _existingBodegaSucursal)
        }
        val _columnsExistenciasTienda: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsExistenciasTienda.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExistenciasTienda.put("sucursalId", TableInfo.Column("sucursalId", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExistenciasTienda.put("productoId", TableInfo.Column("productoId", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExistenciasTienda.put("cantidad", TableInfo.Column("cantidad", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysExistenciasTienda: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesExistenciasTienda: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesExistenciasTienda.add(TableInfo.Index("index_existencias_tienda_sucursalId_productoId",
            true, listOf("sucursalId", "productoId"), listOf("ASC", "ASC")))
        val _infoExistenciasTienda: TableInfo = TableInfo("existencias_tienda",
            _columnsExistenciasTienda, _foreignKeysExistenciasTienda, _indicesExistenciasTienda)
        val _existingExistenciasTienda: TableInfo = read(connection, "existencias_tienda")
        if (!_infoExistenciasTienda.equals(_existingExistenciasTienda)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |existencias_tienda(com.example.boxview.modelo.ExistenciaTienda).
              | Expected:
              |""".trimMargin() + _infoExistenciasTienda + """
              |
              | Found:
              |""".trimMargin() + _existingExistenciasTienda)
        }
        val _columnsInventariosBodega: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsInventariosBodega.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInventariosBodega.put("bodegaId", TableInfo.Column("bodegaId", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInventariosBodega.put("nombre", TableInfo.Column("nombre", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInventariosBodega.put("estado", TableInfo.Column("estado", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInventariosBodega.put("fechaApertura", TableInfo.Column("fechaApertura", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInventariosBodega.put("revisadoPor", TableInfo.Column("revisadoPor", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInventariosBodega.put("campaniaId", TableInfo.Column("campaniaId", "INTEGER", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysInventariosBodega: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesInventariosBodega: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesInventariosBodega.add(TableInfo.Index("index_inventarios_bodega_bodegaId_nombre",
            true, listOf("bodegaId", "nombre"), listOf("ASC", "ASC")))
        val _infoInventariosBodega: TableInfo = TableInfo("inventarios_bodega",
            _columnsInventariosBodega, _foreignKeysInventariosBodega, _indicesInventariosBodega)
        val _existingInventariosBodega: TableInfo = read(connection, "inventarios_bodega")
        if (!_infoInventariosBodega.equals(_existingInventariosBodega)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |inventarios_bodega(com.example.boxview.modelo.InventarioBodega).
              | Expected:
              |""".trimMargin() + _infoInventariosBodega + """
              |
              | Found:
              |""".trimMargin() + _existingInventariosBodega)
        }
        val _columnsZonasInventario: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsZonasInventario.put("inventarioId", TableInfo.Column("inventarioId", "INTEGER",
            true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsZonasInventario.put("posicionId", TableInfo.Column("posicionId", "INTEGER", true, 2,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsZonasInventario.put("operadorId", TableInfo.Column("operadorId", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsZonasInventario.put("periodoId", TableInfo.Column("periodoId", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysZonasInventario: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesZonasInventario: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoZonasInventario: TableInfo = TableInfo("zonas_inventario", _columnsZonasInventario,
            _foreignKeysZonasInventario, _indicesZonasInventario)
        val _existingZonasInventario: TableInfo = read(connection, "zonas_inventario")
        if (!_infoZonasInventario.equals(_existingZonasInventario)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |zonas_inventario(com.example.boxview.modelo.ZonaInventario).
              | Expected:
              |""".trimMargin() + _infoZonasInventario + """
              |
              | Found:
              |""".trimMargin() + _existingZonasInventario)
        }
        val _columnsReferenciasInventario: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsReferenciasInventario.put("inventarioId", TableInfo.Column("inventarioId",
            "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsReferenciasInventario.put("productoId", TableInfo.Column("productoId", "INTEGER",
            true, 2, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsReferenciasInventario.put("posicionId", TableInfo.Column("posicionId", "INTEGER",
            true, 3, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsReferenciasInventario.put("cantidad", TableInfo.Column("cantidad", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysReferenciasInventario: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesReferenciasInventario: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoReferenciasInventario: TableInfo = TableInfo("referencias_inventario",
            _columnsReferenciasInventario, _foreignKeysReferenciasInventario,
            _indicesReferenciasInventario)
        val _existingReferenciasInventario: TableInfo = read(connection, "referencias_inventario")
        if (!_infoReferenciasInventario.equals(_existingReferenciasInventario)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |referencias_inventario(com.example.boxview.modelo.ReferenciaInventario).
              | Expected:
              |""".trimMargin() + _infoReferenciasInventario + """
              |
              | Found:
              |""".trimMargin() + _existingReferenciasInventario)
        }
        val _columnsUsuarioSucursal: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsUsuarioSucursal.put("usuarioId", TableInfo.Column("usuarioId", "INTEGER", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsuarioSucursal.put("sucursalId", TableInfo.Column("sucursalId", "INTEGER", true, 2,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysUsuarioSucursal: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesUsuarioSucursal: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoUsuarioSucursal: TableInfo = TableInfo("usuario_sucursal", _columnsUsuarioSucursal,
            _foreignKeysUsuarioSucursal, _indicesUsuarioSucursal)
        val _existingUsuarioSucursal: TableInfo = read(connection, "usuario_sucursal")
        if (!_infoUsuarioSucursal.equals(_existingUsuarioSucursal)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |usuario_sucursal(com.example.boxview.modelo.UsuarioSucursal).
              | Expected:
              |""".trimMargin() + _infoUsuarioSucursal + """
              |
              | Found:
              |""".trimMargin() + _existingUsuarioSucursal)
        }
        val _columnsMovimientosTienda: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsMovimientosTienda.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMovimientosTienda.put("usuarioId", TableInfo.Column("usuarioId", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMovimientosTienda.put("sucursalId", TableInfo.Column("sucursalId", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMovimientosTienda.put("cajaId", TableInfo.Column("cajaId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMovimientosTienda.put("productoId", TableInfo.Column("productoId", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMovimientosTienda.put("cantidad", TableInfo.Column("cantidad", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMovimientosTienda.put("fecha", TableInfo.Column("fecha", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysMovimientosTienda: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesMovimientosTienda: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesMovimientosTienda.add(TableInfo.Index("index_movimientos_tienda_cajaId_productoId",
            false, listOf("cajaId", "productoId"), listOf("ASC", "ASC")))
        val _infoMovimientosTienda: TableInfo = TableInfo("movimientos_tienda",
            _columnsMovimientosTienda, _foreignKeysMovimientosTienda, _indicesMovimientosTienda)
        val _existingMovimientosTienda: TableInfo = read(connection, "movimientos_tienda")
        if (!_infoMovimientosTienda.equals(_existingMovimientosTienda)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |movimientos_tienda(com.example.boxview.modelo.MovimientoTienda).
              | Expected:
              |""".trimMargin() + _infoMovimientosTienda + """
              |
              | Found:
              |""".trimMargin() + _existingMovimientosTienda)
        }
        val _columnsPosicionesTienda: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsPosicionesTienda.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosicionesTienda.put("sucursalId", TableInfo.Column("sucursalId", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPosicionesTienda.put("codigo", TableInfo.Column("codigo", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosicionesTienda.put("zona", TableInfo.Column("zona", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosicionesTienda.put("categoria", TableInfo.Column("categoria", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPosicionesTienda.put("activa", TableInfo.Column("activa", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosicionesTienda.put("nombre", TableInfo.Column("nombre", "TEXT", true, 0, "''",
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosicionesTienda.put("tipo", TableInfo.Column("tipo", "TEXT", true, 0,
            "'ESTANTERIA'", TableInfo.CREATED_FROM_ENTITY))
        _columnsPosicionesTienda.put("padreId", TableInfo.Column("padreId", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysPosicionesTienda: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesPosicionesTienda: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesPosicionesTienda.add(TableInfo.Index("index_posiciones_tienda_sucursalId_codigo",
            true, listOf("sucursalId", "codigo"), listOf("ASC", "ASC")))
        val _infoPosicionesTienda: TableInfo = TableInfo("posiciones_tienda",
            _columnsPosicionesTienda, _foreignKeysPosicionesTienda, _indicesPosicionesTienda)
        val _existingPosicionesTienda: TableInfo = read(connection, "posiciones_tienda")
        if (!_infoPosicionesTienda.equals(_existingPosicionesTienda)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |posiciones_tienda(com.example.boxview.modelo.PosicionTienda).
              | Expected:
              |""".trimMargin() + _infoPosicionesTienda + """
              |
              | Found:
              |""".trimMargin() + _existingPosicionesTienda)
        }
        val _columnsExistenciasUbicacionTienda: MutableMap<String, TableInfo.Column> =
            mutableMapOf()
        _columnsExistenciasUbicacionTienda.put("id", TableInfo.Column("id", "INTEGER", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExistenciasUbicacionTienda.put("sucursalId", TableInfo.Column("sucursalId",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExistenciasUbicacionTienda.put("productoId", TableInfo.Column("productoId",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExistenciasUbicacionTienda.put("posicionId", TableInfo.Column("posicionId",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExistenciasUbicacionTienda.put("cantidad", TableInfo.Column("cantidad", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysExistenciasUbicacionTienda: MutableSet<TableInfo.ForeignKey> =
            mutableSetOf()
        val _indicesExistenciasUbicacionTienda: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesExistenciasUbicacionTienda.add(TableInfo.Index("index_existencias_ubicacion_tienda_sucursalId_productoId_posicionId",
            true, listOf("sucursalId", "productoId", "posicionId"), listOf("ASC", "ASC", "ASC")))
        val _infoExistenciasUbicacionTienda: TableInfo = TableInfo("existencias_ubicacion_tienda",
            _columnsExistenciasUbicacionTienda, _foreignKeysExistenciasUbicacionTienda,
            _indicesExistenciasUbicacionTienda)
        val _existingExistenciasUbicacionTienda: TableInfo = read(connection,
            "existencias_ubicacion_tienda")
        if (!_infoExistenciasUbicacionTienda.equals(_existingExistenciasUbicacionTienda)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |existencias_ubicacion_tienda(com.example.boxview.modelo.ExistenciaUbicacionTienda).
              | Expected:
              |""".trimMargin() + _infoExistenciasUbicacionTienda + """
              |
              | Found:
              |""".trimMargin() + _existingExistenciasUbicacionTienda)
        }
        val _columnsMovimientosUbicacionTienda: MutableMap<String, TableInfo.Column> =
            mutableMapOf()
        _columnsMovimientosUbicacionTienda.put("id", TableInfo.Column("id", "INTEGER", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMovimientosUbicacionTienda.put("usuarioId", TableInfo.Column("usuarioId", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMovimientosUbicacionTienda.put("sucursalId", TableInfo.Column("sucursalId",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMovimientosUbicacionTienda.put("productoId", TableInfo.Column("productoId",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMovimientosUbicacionTienda.put("posicionOrigenId",
            TableInfo.Column("posicionOrigenId", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMovimientosUbicacionTienda.put("posicionDestinoId",
            TableInfo.Column("posicionDestinoId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMovimientosUbicacionTienda.put("cantidad", TableInfo.Column("cantidad", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMovimientosUbicacionTienda.put("tipo", TableInfo.Column("tipo", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMovimientosUbicacionTienda.put("cajaId", TableInfo.Column("cajaId", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMovimientosUbicacionTienda.put("fecha", TableInfo.Column("fecha", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysMovimientosUbicacionTienda: MutableSet<TableInfo.ForeignKey> =
            mutableSetOf()
        val _indicesMovimientosUbicacionTienda: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoMovimientosUbicacionTienda: TableInfo = TableInfo("movimientos_ubicacion_tienda",
            _columnsMovimientosUbicacionTienda, _foreignKeysMovimientosUbicacionTienda,
            _indicesMovimientosUbicacionTienda)
        val _existingMovimientosUbicacionTienda: TableInfo = read(connection,
            "movimientos_ubicacion_tienda")
        if (!_infoMovimientosUbicacionTienda.equals(_existingMovimientosUbicacionTienda)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |movimientos_ubicacion_tienda(com.example.boxview.modelo.MovimientoUbicacionTienda).
              | Expected:
              |""".trimMargin() + _infoMovimientosUbicacionTienda + """
              |
              | Found:
              |""".trimMargin() + _existingMovimientosUbicacionTienda)
        }
        val _columnsSolicitudesReposicion: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsSolicitudesReposicion.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSolicitudesReposicion.put("sucursalId", TableInfo.Column("sucursalId", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSolicitudesReposicion.put("bodegaId", TableInfo.Column("bodegaId", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSolicitudesReposicion.put("productoId", TableInfo.Column("productoId", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSolicitudesReposicion.put("cantidad", TableInfo.Column("cantidad", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSolicitudesReposicion.put("solicitadoPor", TableInfo.Column("solicitadoPor",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSolicitudesReposicion.put("fecha", TableInfo.Column("fecha", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSolicitudesReposicion.put("estado", TableInfo.Column("estado", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSolicitudesReposicion.put("cajaId", TableInfo.Column("cajaId", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSolicitudesReposicion.put("loteId", TableInfo.Column("loteId", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSolicitudesReposicion: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesSolicitudesReposicion: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesSolicitudesReposicion.add(TableInfo.Index("index_solicitudes_reposicion_sucursalId_estado",
            false, listOf("sucursalId", "estado"), listOf("ASC", "ASC")))
        val _infoSolicitudesReposicion: TableInfo = TableInfo("solicitudes_reposicion",
            _columnsSolicitudesReposicion, _foreignKeysSolicitudesReposicion,
            _indicesSolicitudesReposicion)
        val _existingSolicitudesReposicion: TableInfo = read(connection, "solicitudes_reposicion")
        if (!_infoSolicitudesReposicion.equals(_existingSolicitudesReposicion)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |solicitudes_reposicion(com.example.boxview.modelo.SolicitudReposicion).
              | Expected:
              |""".trimMargin() + _infoSolicitudesReposicion + """
              |
              | Found:
              |""".trimMargin() + _existingSolicitudesReposicion)
        }
        val _columnsConfiguracionSucursal: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsConfiguracionSucursal.put("sucursalId", TableInfo.Column("sucursalId", "INTEGER",
            true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsConfiguracionSucursal.put("bodegaPrincipalId", TableInfo.Column("bodegaPrincipalId",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysConfiguracionSucursal: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesConfiguracionSucursal: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoConfiguracionSucursal: TableInfo = TableInfo("configuracion_sucursal",
            _columnsConfiguracionSucursal, _foreignKeysConfiguracionSucursal,
            _indicesConfiguracionSucursal)
        val _existingConfiguracionSucursal: TableInfo = read(connection, "configuracion_sucursal")
        if (!_infoConfiguracionSucursal.equals(_existingConfiguracionSucursal)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |configuracion_sucursal(com.example.boxview.modelo.ConfiguracionSucursal).
              | Expected:
              |""".trimMargin() + _infoConfiguracionSucursal + """
              |
              | Found:
              |""".trimMargin() + _existingConfiguracionSucursal)
        }
        val _columnsMinimosSucursal: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsMinimosSucursal.put("sucursalId", TableInfo.Column("sucursalId", "INTEGER", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMinimosSucursal.put("productoId", TableInfo.Column("productoId", "INTEGER", true, 2,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMinimosSucursal.put("minimo", TableInfo.Column("minimo", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysMinimosSucursal: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesMinimosSucursal: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoMinimosSucursal: TableInfo = TableInfo("minimos_sucursal", _columnsMinimosSucursal,
            _foreignKeysMinimosSucursal, _indicesMinimosSucursal)
        val _existingMinimosSucursal: TableInfo = read(connection, "minimos_sucursal")
        if (!_infoMinimosSucursal.equals(_existingMinimosSucursal)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |minimos_sucursal(com.example.boxview.modelo.MinimoSucursal).
              | Expected:
              |""".trimMargin() + _infoMinimosSucursal + """
              |
              | Found:
              |""".trimMargin() + _existingMinimosSucursal)
        }
        val _columnsEventosAdministracion: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsEventosAdministracion.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEventosAdministracion.put("sucursalId", TableInfo.Column("sucursalId", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEventosAdministracion.put("usuarioId", TableInfo.Column("usuarioId", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEventosAdministracion.put("accion", TableInfo.Column("accion", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEventosAdministracion.put("detalle", TableInfo.Column("detalle", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEventosAdministracion.put("fecha", TableInfo.Column("fecha", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysEventosAdministracion: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesEventosAdministracion: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesEventosAdministracion.add(TableInfo.Index("index_eventos_administracion_sucursalId",
            false, listOf("sucursalId"), listOf("ASC")))
        val _infoEventosAdministracion: TableInfo = TableInfo("eventos_administracion",
            _columnsEventosAdministracion, _foreignKeysEventosAdministracion,
            _indicesEventosAdministracion)
        val _existingEventosAdministracion: TableInfo = read(connection, "eventos_administracion")
        if (!_infoEventosAdministracion.equals(_existingEventosAdministracion)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |eventos_administracion(com.example.boxview.modelo.EventoAdministrativo).
              | Expected:
              |""".trimMargin() + _infoEventosAdministracion + """
              |
              | Found:
              |""".trimMargin() + _existingEventosAdministracion)
        }
        val _columnsSolicitudesExternas: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsSolicitudesExternas.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSolicitudesExternas.put("sucursalId", TableInfo.Column("sucursalId", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSolicitudesExternas.put("bodegaId", TableInfo.Column("bodegaId", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSolicitudesExternas.put("solicitanteId", TableInfo.Column("solicitanteId",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSolicitudesExternas.put("motivo", TableInfo.Column("motivo", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSolicitudesExternas.put("estado", TableInfo.Column("estado", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSolicitudesExternas.put("fecha", TableInfo.Column("fecha", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSolicitudesExternas.put("fechaRevision", TableInfo.Column("fechaRevision",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSolicitudesExternas.put("revisadoPor", TableInfo.Column("revisadoPor", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSolicitudesExternas.put("fechaDecision", TableInfo.Column("fechaDecision",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSolicitudesExternas.put("decididoPor", TableInfo.Column("decididoPor", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSolicitudesExternas.put("observacion", TableInfo.Column("observacion", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSolicitudesExternas.put("entregaCajaId", TableInfo.Column("entregaCajaId",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSolicitudesExternas.put("proveedorCotizado", TableInfo.Column("proveedorCotizado",
            "TEXT", true, 0, "''", TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSolicitudesExternas: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesSolicitudesExternas: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesSolicitudesExternas.add(TableInfo.Index("index_solicitudes_externas_sucursalId_estado",
            false, listOf("sucursalId", "estado"), listOf("ASC", "ASC")))
        _indicesSolicitudesExternas.add(TableInfo.Index("index_solicitudes_externas_bodegaId",
            false, listOf("bodegaId"), listOf("ASC")))
        val _infoSolicitudesExternas: TableInfo = TableInfo("solicitudes_externas",
            _columnsSolicitudesExternas, _foreignKeysSolicitudesExternas,
            _indicesSolicitudesExternas)
        val _existingSolicitudesExternas: TableInfo = read(connection, "solicitudes_externas")
        if (!_infoSolicitudesExternas.equals(_existingSolicitudesExternas)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |solicitudes_externas(com.example.boxview.modelo.SolicitudExterna).
              | Expected:
              |""".trimMargin() + _infoSolicitudesExternas + """
              |
              | Found:
              |""".trimMargin() + _existingSolicitudesExternas)
        }
        val _columnsLineasSolicitudExterna: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsLineasSolicitudExterna.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLineasSolicitudExterna.put("solicitudId", TableInfo.Column("solicitudId", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLineasSolicitudExterna.put("productoId", TableInfo.Column("productoId", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLineasSolicitudExterna.put("cantidad", TableInfo.Column("cantidad", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLineasSolicitudExterna.put("precioUnitarioCotizado",
            TableInfo.Column("precioUnitarioCotizado", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysLineasSolicitudExterna: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesLineasSolicitudExterna: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesLineasSolicitudExterna.add(TableInfo.Index("index_lineas_solicitud_externa_solicitudId",
            false, listOf("solicitudId"), listOf("ASC")))
        val _infoLineasSolicitudExterna: TableInfo = TableInfo("lineas_solicitud_externa",
            _columnsLineasSolicitudExterna, _foreignKeysLineasSolicitudExterna,
            _indicesLineasSolicitudExterna)
        val _existingLineasSolicitudExterna: TableInfo = read(connection,
            "lineas_solicitud_externa")
        if (!_infoLineasSolicitudExterna.equals(_existingLineasSolicitudExterna)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |lineas_solicitud_externa(com.example.boxview.modelo.LineaSolicitudExterna).
              | Expected:
              |""".trimMargin() + _infoLineasSolicitudExterna + """
              |
              | Found:
              |""".trimMargin() + _existingLineasSolicitudExterna)
        }
        val _columnsEventosSolicitudExterna: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsEventosSolicitudExterna.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEventosSolicitudExterna.put("solicitudId", TableInfo.Column("solicitudId",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEventosSolicitudExterna.put("usuarioId", TableInfo.Column("usuarioId", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEventosSolicitudExterna.put("accion", TableInfo.Column("accion", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEventosSolicitudExterna.put("detalle", TableInfo.Column("detalle", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEventosSolicitudExterna.put("fecha", TableInfo.Column("fecha", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysEventosSolicitudExterna: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesEventosSolicitudExterna: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesEventosSolicitudExterna.add(TableInfo.Index("index_eventos_solicitud_externa_solicitudId",
            false, listOf("solicitudId"), listOf("ASC")))
        val _infoEventosSolicitudExterna: TableInfo = TableInfo("eventos_solicitud_externa",
            _columnsEventosSolicitudExterna, _foreignKeysEventosSolicitudExterna,
            _indicesEventosSolicitudExterna)
        val _existingEventosSolicitudExterna: TableInfo = read(connection,
            "eventos_solicitud_externa")
        if (!_infoEventosSolicitudExterna.equals(_existingEventosSolicitudExterna)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |eventos_solicitud_externa(com.example.boxview.modelo.EventoSolicitudExterna).
              | Expected:
              |""".trimMargin() + _infoEventosSolicitudExterna + """
              |
              | Found:
              |""".trimMargin() + _existingEventosSolicitudExterna)
        }
        val _columnsInformesInventario: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsInformesInventario.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInformesInventario.put("inventarioId", TableInfo.Column("inventarioId", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInformesInventario.put("sucursalId", TableInfo.Column("sucursalId", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInformesInventario.put("bodegaId", TableInfo.Column("bodegaId", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInformesInventario.put("sucursalNombre", TableInfo.Column("sucursalNombre", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInformesInventario.put("bodegaNombre", TableInfo.Column("bodegaNombre", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInformesInventario.put("nombre", TableInfo.Column("nombre", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInformesInventario.put("estadoCierre", TableInfo.Column("estadoCierre", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInformesInventario.put("fechaApertura", TableInfo.Column("fechaApertura", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInformesInventario.put("fechaCierre", TableInfo.Column("fechaCierre", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInformesInventario.put("cerradoPor", TableInfo.Column("cerradoPor", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInformesInventario.put("posiciones", TableInfo.Column("posiciones", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInformesInventario.put("productosContados", TableInfo.Column("productosContados",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInformesInventario.put("productosSinContar", TableInfo.Column("productosSinContar",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInformesInventario.put("lineasConDiferencia",
            TableInfo.Column("lineasConDiferencia", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInformesInventario.put("unidadesSistema", TableInfo.Column("unidadesSistema",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInformesInventario.put("unidadesContadas", TableInfo.Column("unidadesContadas",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInformesInventario.put("ajustesAplicados", TableInfo.Column("ajustesAplicados",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInformesInventario.put("estadoRevision", TableInfo.Column("estadoRevision", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInformesInventario.put("revisadoPor", TableInfo.Column("revisadoPor", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInformesInventario.put("fechaRevision", TableInfo.Column("fechaRevision", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysInformesInventario: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesInformesInventario: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesInformesInventario.add(TableInfo.Index("index_informes_inventario_inventarioId",
            true, listOf("inventarioId"), listOf("ASC")))
        _indicesInformesInventario.add(TableInfo.Index("index_informes_inventario_estadoRevision_fechaCierre",
            false, listOf("estadoRevision", "fechaCierre"), listOf("ASC", "ASC")))
        val _infoInformesInventario: TableInfo = TableInfo("informes_inventario",
            _columnsInformesInventario, _foreignKeysInformesInventario, _indicesInformesInventario)
        val _existingInformesInventario: TableInfo = read(connection, "informes_inventario")
        if (!_infoInformesInventario.equals(_existingInformesInventario)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |informes_inventario(com.example.boxview.modelo.InformeInventario).
              | Expected:
              |""".trimMargin() + _infoInformesInventario + """
              |
              | Found:
              |""".trimMargin() + _existingInformesInventario)
        }
        val _columnsDetallesInformeInventario: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsDetallesInformeInventario.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDetallesInformeInventario.put("informeId", TableInfo.Column("informeId", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDetallesInformeInventario.put("productoId", TableInfo.Column("productoId",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDetallesInformeInventario.put("codigo", TableInfo.Column("codigo", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDetallesInformeInventario.put("producto", TableInfo.Column("producto", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDetallesInformeInventario.put("posicion", TableInfo.Column("posicion", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDetallesInformeInventario.put("cantidadSistema", TableInfo.Column("cantidadSistema",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDetallesInformeInventario.put("cantidadContada", TableInfo.Column("cantidadContada",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDetallesInformeInventario.put("ajusteAplicado", TableInfo.Column("ajusteAplicado",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysDetallesInformeInventario: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesDetallesInformeInventario: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesDetallesInformeInventario.add(TableInfo.Index("index_detalles_informe_inventario_informeId",
            false, listOf("informeId"), listOf("ASC")))
        val _infoDetallesInformeInventario: TableInfo = TableInfo("detalles_informe_inventario",
            _columnsDetallesInformeInventario, _foreignKeysDetallesInformeInventario,
            _indicesDetallesInformeInventario)
        val _existingDetallesInformeInventario: TableInfo = read(connection,
            "detalles_informe_inventario")
        if (!_infoDetallesInformeInventario.equals(_existingDetallesInformeInventario)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |detalles_informe_inventario(com.example.boxview.modelo.DetalleInformeInventario).
              | Expected:
              |""".trimMargin() + _infoDetallesInformeInventario + """
              |
              | Found:
              |""".trimMargin() + _existingDetallesInformeInventario)
        }
        val _columnsSalidasTienda: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsSalidasTienda.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSalidasTienda.put("sucursalId", TableInfo.Column("sucursalId", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSalidasTienda.put("usuarioId", TableInfo.Column("usuarioId", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSalidasTienda.put("productoId", TableInfo.Column("productoId", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSalidasTienda.put("posicionId", TableInfo.Column("posicionId", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSalidasTienda.put("cantidad", TableInfo.Column("cantidad", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSalidasTienda.put("tipo", TableInfo.Column("tipo", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSalidasTienda.put("motivo", TableInfo.Column("motivo", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSalidasTienda.put("fecha", TableInfo.Column("fecha", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSalidasTienda.put("precioUnitarioVentaClp",
            TableInfo.Column("precioUnitarioVentaClp", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSalidasTienda.put("totalVentaClp", TableInfo.Column("totalVentaClp", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSalidasTienda: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesSalidasTienda: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoSalidasTienda: TableInfo = TableInfo("salidas_tienda", _columnsSalidasTienda,
            _foreignKeysSalidasTienda, _indicesSalidasTienda)
        val _existingSalidasTienda: TableInfo = read(connection, "salidas_tienda")
        if (!_infoSalidasTienda.equals(_existingSalidasTienda)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |salidas_tienda(com.example.boxview.modelo.SalidaTienda).
              | Expected:
              |""".trimMargin() + _infoSalidasTienda + """
              |
              | Found:
              |""".trimMargin() + _existingSalidasTienda)
        }
        val _columnsInventariosTienda: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsInventariosTienda.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInventariosTienda.put("sucursalId", TableInfo.Column("sucursalId", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInventariosTienda.put("nombre", TableInfo.Column("nombre", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInventariosTienda.put("estado", TableInfo.Column("estado", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInventariosTienda.put("fechaApertura", TableInfo.Column("fechaApertura", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInventariosTienda.put("cerradoPor", TableInfo.Column("cerradoPor", "INTEGER", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInventariosTienda.put("fechaCierre", TableInfo.Column("fechaCierre", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInventariosTienda.put("estadoRevision", TableInfo.Column("estadoRevision", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInventariosTienda.put("revisadoPor", TableInfo.Column("revisadoPor", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInventariosTienda.put("fechaRevision", TableInfo.Column("fechaRevision", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInventariosTienda.put("motivoCierre", TableInfo.Column("motivoCierre", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInventariosTienda.put("verificacionVaciaPor",
            TableInfo.Column("verificacionVaciaPor", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInventariosTienda.put("fechaVerificacionVacia",
            TableInfo.Column("fechaVerificacionVacia", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInventariosTienda.put("campaniaId", TableInfo.Column("campaniaId", "INTEGER", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysInventariosTienda: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesInventariosTienda: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesInventariosTienda.add(TableInfo.Index("index_inventarios_tienda_sucursalId", false,
            listOf("sucursalId"), listOf("ASC")))
        val _infoInventariosTienda: TableInfo = TableInfo("inventarios_tienda",
            _columnsInventariosTienda, _foreignKeysInventariosTienda, _indicesInventariosTienda)
        val _existingInventariosTienda: TableInfo = read(connection, "inventarios_tienda")
        if (!_infoInventariosTienda.equals(_existingInventariosTienda)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |inventarios_tienda(com.example.boxview.modelo.InventarioTienda).
              | Expected:
              |""".trimMargin() + _infoInventariosTienda + """
              |
              | Found:
              |""".trimMargin() + _existingInventariosTienda)
        }
        val _columnsLineasInventarioTienda: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsLineasInventarioTienda.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLineasInventarioTienda.put("inventarioId", TableInfo.Column("inventarioId",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLineasInventarioTienda.put("productoId", TableInfo.Column("productoId", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLineasInventarioTienda.put("posicionId", TableInfo.Column("posicionId", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLineasInventarioTienda.put("stockInicio", TableInfo.Column("stockInicio", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLineasInventarioTienda.put("cantidadContada", TableInfo.Column("cantidadContada",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLineasInventarioTienda.put("contadoPor", TableInfo.Column("contadoPor", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLineasInventarioTienda.put("fechaConteo", TableInfo.Column("fechaConteo", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLineasInventarioTienda.put("codigoProductoCierre",
            TableInfo.Column("codigoProductoCierre", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLineasInventarioTienda.put("nombreProductoCierre",
            TableInfo.Column("nombreProductoCierre", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLineasInventarioTienda.put("rutaUbicacionCierre",
            TableInfo.Column("rutaUbicacionCierre", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysLineasInventarioTienda: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesLineasInventarioTienda: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesLineasInventarioTienda.add(TableInfo.Index("index_lineas_inventario_tienda_inventarioId_productoId_posicionId",
            true, listOf("inventarioId", "productoId", "posicionId"), listOf("ASC", "ASC", "ASC")))
        val _infoLineasInventarioTienda: TableInfo = TableInfo("lineas_inventario_tienda",
            _columnsLineasInventarioTienda, _foreignKeysLineasInventarioTienda,
            _indicesLineasInventarioTienda)
        val _existingLineasInventarioTienda: TableInfo = read(connection,
            "lineas_inventario_tienda")
        if (!_infoLineasInventarioTienda.equals(_existingLineasInventarioTienda)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |lineas_inventario_tienda(com.example.boxview.modelo.LineaInventarioTienda).
              | Expected:
              |""".trimMargin() + _infoLineasInventarioTienda + """
              |
              | Found:
              |""".trimMargin() + _existingLineasInventarioTienda)
        }
        val _columnsEventosConteoTienda: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsEventosConteoTienda.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEventosConteoTienda.put("inventarioId", TableInfo.Column("inventarioId", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEventosConteoTienda.put("lineaId", TableInfo.Column("lineaId", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEventosConteoTienda.put("productoId", TableInfo.Column("productoId", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEventosConteoTienda.put("posicionId", TableInfo.Column("posicionId", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEventosConteoTienda.put("usuarioId", TableInfo.Column("usuarioId", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEventosConteoTienda.put("cantidadAnterior", TableInfo.Column("cantidadAnterior",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEventosConteoTienda.put("cantidadNueva", TableInfo.Column("cantidadNueva",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEventosConteoTienda.put("tipo", TableInfo.Column("tipo", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEventosConteoTienda.put("fecha", TableInfo.Column("fecha", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysEventosConteoTienda: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesEventosConteoTienda: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesEventosConteoTienda.add(TableInfo.Index("index_eventos_conteo_tienda_inventarioId_fecha",
            false, listOf("inventarioId", "fecha"), listOf("ASC", "ASC")))
        val _infoEventosConteoTienda: TableInfo = TableInfo("eventos_conteo_tienda",
            _columnsEventosConteoTienda, _foreignKeysEventosConteoTienda,
            _indicesEventosConteoTienda)
        val _existingEventosConteoTienda: TableInfo = read(connection, "eventos_conteo_tienda")
        if (!_infoEventosConteoTienda.equals(_existingEventosConteoTienda)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |eventos_conteo_tienda(com.example.boxview.modelo.EventoConteoTienda).
              | Expected:
              |""".trimMargin() + _infoEventosConteoTienda + """
              |
              | Found:
              |""".trimMargin() + _existingEventosConteoTienda)
        }
        val _columnsZonasConteoTienda: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsZonasConteoTienda.put("inventarioId", TableInfo.Column("inventarioId", "INTEGER",
            true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsZonasConteoTienda.put("posicionId", TableInfo.Column("posicionId", "INTEGER", true,
            2, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsZonasConteoTienda.put("asignadoA", TableInfo.Column("asignadoA", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsZonasConteoTienda.put("confirmadoPor", TableInfo.Column("confirmadoPor", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsZonasConteoTienda.put("fechaConfirmacion", TableInfo.Column("fechaConfirmacion",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysZonasConteoTienda: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesZonasConteoTienda: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoZonasConteoTienda: TableInfo = TableInfo("zonas_conteo_tienda",
            _columnsZonasConteoTienda, _foreignKeysZonasConteoTienda, _indicesZonasConteoTienda)
        val _existingZonasConteoTienda: TableInfo = read(connection, "zonas_conteo_tienda")
        if (!_infoZonasConteoTienda.equals(_existingZonasConteoTienda)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |zonas_conteo_tienda(com.example.boxview.modelo.ZonaConteoTienda).
              | Expected:
              |""".trimMargin() + _infoZonasConteoTienda + """
              |
              | Found:
              |""".trimMargin() + _existingZonasConteoTienda)
        }
        val _columnsVerificacionesEstanteVacio: MutableMap<String, TableInfo.Column> =
            mutableMapOf()
        _columnsVerificacionesEstanteVacio.put("tipo", TableInfo.Column("tipo", "TEXT", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVerificacionesEstanteVacio.put("inventarioId", TableInfo.Column("inventarioId",
            "INTEGER", true, 2, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVerificacionesEstanteVacio.put("posicionId", TableInfo.Column("posicionId",
            "INTEGER", true, 3, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVerificacionesEstanteVacio.put("usuarioId", TableInfo.Column("usuarioId", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVerificacionesEstanteVacio.put("fecha", TableInfo.Column("fecha", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysVerificacionesEstanteVacio: MutableSet<TableInfo.ForeignKey> =
            mutableSetOf()
        val _indicesVerificacionesEstanteVacio: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoVerificacionesEstanteVacio: TableInfo = TableInfo("verificaciones_estante_vacio",
            _columnsVerificacionesEstanteVacio, _foreignKeysVerificacionesEstanteVacio,
            _indicesVerificacionesEstanteVacio)
        val _existingVerificacionesEstanteVacio: TableInfo = read(connection,
            "verificaciones_estante_vacio")
        if (!_infoVerificacionesEstanteVacio.equals(_existingVerificacionesEstanteVacio)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |verificaciones_estante_vacio(com.example.boxview.modelo.VerificacionEstanteVacio).
              | Expected:
              |""".trimMargin() + _infoVerificacionesEstanteVacio + """
              |
              | Found:
              |""".trimMargin() + _existingVerificacionesEstanteVacio)
        }
        val _columnsCampaniasMensuales: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsCampaniasMensuales.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCampaniasMensuales.put("tipo", TableInfo.Column("tipo", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCampaniasMensuales.put("ubicacionId", TableInfo.Column("ubicacionId", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCampaniasMensuales.put("sucursalId", TableInfo.Column("sucursalId", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCampaniasMensuales.put("mes", TableInfo.Column("mes", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCampaniasMensuales.put("estado", TableInfo.Column("estado", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCampaniasMensuales.put("fechaInicio", TableInfo.Column("fechaInicio", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCampaniasMensuales.put("fechaFinal", TableInfo.Column("fechaFinal", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCampaniasMensuales.put("revision", TableInfo.Column("revision", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCampaniasMensuales.put("revisadaPor", TableInfo.Column("revisadaPor", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCampaniasMensuales.put("fechaRevision", TableInfo.Column("fechaRevision", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCampaniasMensuales: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesCampaniasMensuales: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesCampaniasMensuales.add(TableInfo.Index("index_campanias_mensuales_tipo_ubicacionId_mes",
            true, listOf("tipo", "ubicacionId", "mes"), listOf("ASC", "ASC", "ASC")))
        val _infoCampaniasMensuales: TableInfo = TableInfo("campanias_mensuales",
            _columnsCampaniasMensuales, _foreignKeysCampaniasMensuales, _indicesCampaniasMensuales)
        val _existingCampaniasMensuales: TableInfo = read(connection, "campanias_mensuales")
        if (!_infoCampaniasMensuales.equals(_existingCampaniasMensuales)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |campanias_mensuales(com.example.boxview.modelo.CampaniaMensual).
              | Expected:
              |""".trimMargin() + _infoCampaniasMensuales + """
              |
              | Found:
              |""".trimMargin() + _existingCampaniasMensuales)
        }
        val _columnsZonasCampania: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsZonasCampania.put("campaniaId", TableInfo.Column("campaniaId", "INTEGER", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsZonasCampania.put("posicionId", TableInfo.Column("posicionId", "INTEGER", true, 2,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsZonasCampania.put("ruta", TableInfo.Column("ruta", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsZonasCampania.put("estado", TableInfo.Column("estado", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsZonasCampania.put("inventarioId", TableInfo.Column("inventarioId", "INTEGER", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsZonasCampania.put("fechaConteo", TableInfo.Column("fechaConteo", "INTEGER", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysZonasCampania: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesZonasCampania: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoZonasCampania: TableInfo = TableInfo("zonas_campania", _columnsZonasCampania,
            _foreignKeysZonasCampania, _indicesZonasCampania)
        val _existingZonasCampania: TableInfo = read(connection, "zonas_campania")
        if (!_infoZonasCampania.equals(_existingZonasCampania)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |zonas_campania(com.example.boxview.modelo.ZonaCampania).
              | Expected:
              |""".trimMargin() + _infoZonasCampania + """
              |
              | Found:
              |""".trimMargin() + _existingZonasCampania)
        }
        val _columnsDetallesCampania: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsDetallesCampania.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDetallesCampania.put("campaniaId", TableInfo.Column("campaniaId", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDetallesCampania.put("inventarioId", TableInfo.Column("inventarioId", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDetallesCampania.put("posicionId", TableInfo.Column("posicionId", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDetallesCampania.put("codigo", TableInfo.Column("codigo", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDetallesCampania.put("producto", TableInfo.Column("producto", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDetallesCampania.put("ruta", TableInfo.Column("ruta", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDetallesCampania.put("cantidadSistema", TableInfo.Column("cantidadSistema",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDetallesCampania.put("cantidadContada", TableInfo.Column("cantidadContada",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDetallesCampania.put("ajusteAplicado", TableInfo.Column("ajusteAplicado", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDetallesCampania.put("fechaCierre", TableInfo.Column("fechaCierre", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysDetallesCampania: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesDetallesCampania: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesDetallesCampania.add(TableInfo.Index("index_detalles_campania_campaniaId_posicionId",
            false, listOf("campaniaId", "posicionId"), listOf("ASC", "ASC")))
        val _infoDetallesCampania: TableInfo = TableInfo("detalles_campania",
            _columnsDetallesCampania, _foreignKeysDetallesCampania, _indicesDetallesCampania)
        val _existingDetallesCampania: TableInfo = read(connection, "detalles_campania")
        if (!_infoDetallesCampania.equals(_existingDetallesCampania)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |detalles_campania(com.example.boxview.modelo.DetalleCampania).
              | Expected:
              |""".trimMargin() + _infoDetallesCampania + """
              |
              | Found:
              |""".trimMargin() + _existingDetallesCampania)
        }
        val _columnsPreciosVentaSucursal: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsPreciosVentaSucursal.put("sucursalId", TableInfo.Column("sucursalId", "INTEGER",
            true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPreciosVentaSucursal.put("productoId", TableInfo.Column("productoId", "INTEGER",
            true, 2, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPreciosVentaSucursal.put("precioClp", TableInfo.Column("precioClp", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPreciosVentaSucursal.put("actualizadoPor", TableInfo.Column("actualizadoPor",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPreciosVentaSucursal.put("fechaActualizacion",
            TableInfo.Column("fechaActualizacion", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysPreciosVentaSucursal: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesPreciosVentaSucursal: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoPreciosVentaSucursal: TableInfo = TableInfo("precios_venta_sucursal",
            _columnsPreciosVentaSucursal, _foreignKeysPreciosVentaSucursal,
            _indicesPreciosVentaSucursal)
        val _existingPreciosVentaSucursal: TableInfo = read(connection, "precios_venta_sucursal")
        if (!_infoPreciosVentaSucursal.equals(_existingPreciosVentaSucursal)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |precios_venta_sucursal(com.example.boxview.modelo.PrecioVentaSucursal).
              | Expected:
              |""".trimMargin() + _infoPreciosVentaSucursal + """
              |
              | Found:
              |""".trimMargin() + _existingPreciosVentaSucursal)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "usuarios", "sucursales",
        "bodegas", "usuario_bodega", "posiciones", "productos", "existencias", "movimientos",
        "despachos", "conteos", "entregas", "cajas", "detalle_caja", "eventos_caja",
        "incidencias_caja", "periodos_inventario", "lineas_inventario", "preparaciones_caja",
        "borrador_inventario", "ajustes_inventario", "bodega_sucursal", "existencias_tienda",
        "inventarios_bodega", "zonas_inventario", "referencias_inventario", "usuario_sucursal",
        "movimientos_tienda", "posiciones_tienda", "existencias_ubicacion_tienda",
        "movimientos_ubicacion_tienda", "solicitudes_reposicion", "configuracion_sucursal",
        "minimos_sucursal", "eventos_administracion", "solicitudes_externas",
        "lineas_solicitud_externa", "eventos_solicitud_externa", "informes_inventario",
        "detalles_informe_inventario", "salidas_tienda", "inventarios_tienda",
        "lineas_inventario_tienda", "eventos_conteo_tienda", "zonas_conteo_tienda",
        "verificaciones_estante_vacio", "campanias_mensuales", "zonas_campania",
        "detalles_campania", "precios_venta_sucursal")
  }

  public override fun clearAllTables() {
    super.performClear(false, "usuarios", "sucursales", "bodegas", "usuario_bodega", "posiciones",
        "productos", "existencias", "movimientos", "despachos", "conteos", "entregas", "cajas",
        "detalle_caja", "eventos_caja", "incidencias_caja", "periodos_inventario",
        "lineas_inventario", "preparaciones_caja", "borrador_inventario", "ajustes_inventario",
        "bodega_sucursal", "existencias_tienda", "inventarios_bodega", "zonas_inventario",
        "referencias_inventario", "usuario_sucursal", "movimientos_tienda", "posiciones_tienda",
        "existencias_ubicacion_tienda", "movimientos_ubicacion_tienda", "solicitudes_reposicion",
        "configuracion_sucursal", "minimos_sucursal", "eventos_administracion",
        "solicitudes_externas", "lineas_solicitud_externa", "eventos_solicitud_externa",
        "informes_inventario", "detalles_informe_inventario", "salidas_tienda",
        "inventarios_tienda", "lineas_inventario_tienda", "eventos_conteo_tienda",
        "zonas_conteo_tienda", "verificaciones_estante_vacio", "campanias_mensuales",
        "zonas_campania", "detalles_campania", "precios_venta_sucursal")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(UsuariosDao::class, UsuariosDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ProductosDao::class, ProductosDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(CajasDao::class, CajasDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(InventariosDao::class, InventariosDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(TiendasDao::class, TiendasDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(AdministracionDao::class, AdministracionDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(AbastecimientoExternoDao::class,
        AbastecimientoExternoDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(CampaniasMensualesDao::class,
        CampaniasMensualesDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(InformesGerenciaDao::class,
        InformesGerenciaDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(OperacionesTiendaDao::class,
        OperacionesTiendaDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(PreciosVentaDao::class, PreciosVentaDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun usuariosDao(): UsuariosDao = _usuariosDao.value

  public override fun productosDao(): ProductosDao = _productosDao.value

  public override fun cajasDao(): CajasDao = _cajasDao.value

  public override fun inventariosDao(): InventariosDao = _inventariosDao.value

  public override fun tiendasDao(): TiendasDao = _tiendasDao.value

  public override fun administracionDao(): AdministracionDao = _administracionDao.value

  public override fun abastecimientoExternoDao(): AbastecimientoExternoDao =
      _abastecimientoExternoDao.value

  public override fun campaniasMensualesDao(): CampaniasMensualesDao = _campaniasMensualesDao.value

  public override fun informesGerenciaDao(): InformesGerenciaDao = _informesGerenciaDao.value

  public override fun operacionesTiendaDao(): OperacionesTiendaDao = _operacionesTiendaDao.value

  public override fun preciosVentaDao(): PreciosVentaDao = _preciosVentaDao.value
}
