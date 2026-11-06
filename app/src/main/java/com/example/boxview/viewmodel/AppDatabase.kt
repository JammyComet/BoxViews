package com.example.boxview.viewmodel

// Lógica: Configura Room, sus DAO y las migraciones.
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.boxview.modelo.*

// Tablas y version de la base de datos local.
@Database(entities = [Usuario::class, Sucursal::class, Bodega::class, UsuarioBodega::class,
    Posicion::class, Producto::class, Existencia::class, Movimiento::class,
    Despacho::class, Conteo::class, EntregaAutorizada::class, Caja::class, DetalleCaja::class, EventoCaja::class, IncidenciaCaja::class, PeriodoInventario::class, LineaInventario::class, PreparacionCaja::class, BorradorInventario::class, AjusteInventario::class,
    BodegaSucursal::class, ExistenciaTienda::class, InventarioBodega::class, ZonaInventario::class, ReferenciaInventario::class,
    UsuarioSucursal::class, MovimientoTienda::class, PosicionTienda::class, ExistenciaUbicacionTienda::class, MovimientoUbicacionTienda::class, SolicitudReposicion::class, ConfiguracionSucursal::class, MinimoSucursal::class, EventoAdministrativo::class, SolicitudExterna::class, LineaSolicitudExterna::class, EventoSolicitudExterna::class, InformeInventario::class, DetalleInformeInventario::class, SalidaTienda::class, InventarioTienda::class, LineaInventarioTienda::class, EventoConteoTienda::class, ZonaConteoTienda::class, VerificacionEstanteVacio::class, CampaniaMensual::class, ZonaCampania::class, DetalleCampania::class, PrecioVentaSucursal::class], version = 26, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuariosDao(): UsuariosDao
    abstract fun productosDao(): ProductosDao
    abstract fun cajasDao(): CajasDao
    abstract fun inventariosDao(): InventariosDao
    abstract fun tiendasDao(): TiendasDao
    abstract fun administracionDao(): AdministracionDao
    abstract fun abastecimientoExternoDao(): AbastecimientoExternoDao
    abstract fun campaniasMensualesDao(): CampaniasMensualesDao
    abstract fun informesGerenciaDao(): InformesGerenciaDao
    abstract fun operacionesTiendaDao(): OperacionesTiendaDao
    abstract fun preciosVentaDao(): PreciosVentaDao

    // acceso a los DAO
    // Acceso conjunto a los DAO del inventario.
    fun inventarioDao(): InventarioDao = InventarioDao(
        usuariosDao(),
        productosDao(),
        cajasDao(),
        inventariosDao(),
        tiendasDao(),
        administracionDao()
    )

    companion object {
        private val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `detalle_caja` ADD COLUMN `cantidadRecibida` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `detalle_caja` ADD COLUMN `posicionDestinoId` INTEGER")
                db.execSQL("CREATE TABLE IF NOT EXISTS `periodos_inventario` (`id` INTEGER NOT NULL, `bodegaId` INTEGER NOT NULL, `asignadoA` INTEGER NOT NULL, `nombre` TEXT NOT NULL, `estado` TEXT NOT NULL, `fechaApertura` INTEGER NOT NULL, PRIMARY KEY(`id`))")
                db.execSQL("CREATE TABLE IF NOT EXISTS `lineas_inventario` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `periodoId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `posicionId` INTEGER NOT NULL, `cantidadContada` INTEGER NOT NULL)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_lineas_inventario_periodoId_productoId_posicionId` ON `lineas_inventario` (`periodoId`, `productoId`, `posicionId`)")
            }
        }

        private val MIGRACION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `preparaciones_caja` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `cajaId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `posicionId` INTEGER NOT NULL, `cantidad` INTEGER NOT NULL)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_preparaciones_caja_cajaId_productoId_posicionId` ON `preparaciones_caja` (`cajaId`, `productoId`, `posicionId`)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `borrador_inventario` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `periodoId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `posicionId` INTEGER NOT NULL, `cantidad` INTEGER NOT NULL)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_borrador_inventario_periodoId_productoId_posicionId` ON `borrador_inventario` (`periodoId`, `productoId`, `posicionId`)")
                // migracion de preparaciones
                db.execSQL("INSERT OR IGNORE INTO preparaciones_caja (cajaId,productoId,posicionId,cantidad) SELECT d.cajaId,d.productoId,d.posicionId,d.cantidadVerificada FROM detalle_caja d JOIN cajas c ON c.id=d.cajaId WHERE c.tipo='SALIDA' AND c.estado IN ('ORDEN_ASIGNADA','PENDIENTE_REVISION') AND d.posicionId IS NOT NULL AND d.cantidadVerificada>0")
                db.execSQL("UPDATE cajas SET tipo='SALIDA',estado='ORDEN_ASIGNADA',revisorId=NULL WHERE codigo='CAJ-DSP-101' AND tipo='INGRESO' AND estado='EN_TRANSITO' AND NOT EXISTS (SELECT 1 FROM eventos_caja WHERE eventos_caja.cajaId=cajas.id) AND NOT EXISTS (SELECT 1 FROM detalle_caja WHERE detalle_caja.cajaId=cajas.id AND cantidadRecibida>0)")
            }
        }

        private val MIGRACION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // migracion de inventarios
                db.execSQL("ALTER TABLE `lineas_inventario` ADD COLUMN `stockAlEntregar` INTEGER")
                db.execSQL("CREATE TABLE IF NOT EXISTS `ajustes_inventario` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `periodoId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `posicionId` INTEGER NOT NULL, `stockAnterior` INTEGER NOT NULL, `stockNuevo` INTEGER NOT NULL, `revisadoPor` INTEGER NOT NULL, `fecha` INTEGER NOT NULL)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_ajustes_inventario_periodoId_productoId_posicionId` ON `ajustes_inventario` (`periodoId`, `productoId`, `posicionId`)")
            }
        }

        private val MIGRACION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `cajas` ADD COLUMN `sucursalDestinoId` INTEGER")
                db.execSQL("CREATE TABLE IF NOT EXISTS `bodega_sucursal` (`bodegaId` INTEGER NOT NULL, `sucursalId` INTEGER NOT NULL, PRIMARY KEY(`bodegaId`, `sucursalId`))")
                db.execSQL("CREATE TABLE IF NOT EXISTS `existencias_tienda` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sucursalId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `cantidad` INTEGER NOT NULL)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_existencias_tienda_sucursalId_productoId` ON `existencias_tienda` (`sucursalId`, `productoId`)")
                // relacion de bodegas y tiendas
                db.execSQL("INSERT OR IGNORE INTO bodega_sucursal(bodegaId,sucursalId) SELECT b.id,s.id FROM bodegas b CROSS JOIN sucursales s WHERE b.activa=1")
                db.execSQL("UPDATE cajas SET sucursalDestinoId=(SELECT b.sucursalId FROM bodegas b WHERE b.id=cajas.bodegaDestinoId), bodegaDestinoId=0 WHERE tipo='SALIDA' AND estado IN ('ORDEN_ASIGNADA','PENDIENTE_REVISION','EN_TRANSITO') AND bodegaDestinoId>0 AND EXISTS(SELECT 1 FROM bodegas b WHERE b.id=cajas.bodegaDestinoId)")
                // asignacion de bodega del jefe
                db.execSQL("DELETE FROM usuario_bodega WHERE usuarioId IN (SELECT id FROM usuarios WHERE rol='JEFE_BODEGA') AND bodegaId NOT IN (SELECT MIN(bodegaId) FROM usuario_bodega AS ub2 WHERE ub2.usuarioId=usuario_bodega.usuarioId)")
                crearReglaUnaBodegaPorJefe(db)
            }
        }

        private val MIGRACION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `usuarios` ADD COLUMN `rut` TEXT")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_usuarios_rut` ON `usuarios` (`rut`)")
                db.execSQL("ALTER TABLE `periodos_inventario` ADD COLUMN `inventarioBodegaId` INTEGER")
                db.execSQL("CREATE TABLE IF NOT EXISTS `inventarios_bodega` (`id` INTEGER NOT NULL, `bodegaId` INTEGER NOT NULL, `nombre` TEXT NOT NULL, `estado` TEXT NOT NULL, `fechaApertura` INTEGER NOT NULL, `revisadoPor` INTEGER, PRIMARY KEY(`id`))")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_inventarios_bodega_bodegaId_nombre` ON `inventarios_bodega` (`bodegaId`, `nombre`)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `zonas_inventario` (`inventarioId` INTEGER NOT NULL, `posicionId` INTEGER NOT NULL, `operadorId` INTEGER NOT NULL, `periodoId` INTEGER NOT NULL, PRIMARY KEY(`inventarioId`, `posicionId`))")
                db.execSQL("CREATE TABLE IF NOT EXISTS `referencias_inventario` (`inventarioId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `posicionId` INTEGER NOT NULL, `cantidad` INTEGER NOT NULL, PRIMARY KEY(`inventarioId`, `productoId`, `posicionId`))")
                crearReglaUnaBodegaPorJefe(db)
            }
        }

        private val MIGRACION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `usuario_sucursal` (`usuarioId` INTEGER NOT NULL, `sucursalId` INTEGER NOT NULL, PRIMARY KEY(`usuarioId`, `sucursalId`))")
                db.execSQL("CREATE TABLE IF NOT EXISTS `movimientos_tienda` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `usuarioId` INTEGER NOT NULL, `sucursalId` INTEGER NOT NULL, `cajaId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `cantidad` INTEGER NOT NULL, `fecha` INTEGER NOT NULL)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_movimientos_tienda_cajaId_productoId` ON `movimientos_tienda` (`cajaId`, `productoId`)")
            }
        }

        // migracion de ubicaciones de tienda
        private val MIGRACION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `posiciones_tienda` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sucursalId` INTEGER NOT NULL, `codigo` TEXT NOT NULL, `zona` TEXT NOT NULL, `categoria` TEXT NOT NULL, `activa` INTEGER NOT NULL)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_posiciones_tienda_sucursalId_codigo` ON `posiciones_tienda` (`sucursalId`, `codigo`)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `existencias_ubicacion_tienda` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sucursalId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `posicionId` INTEGER NOT NULL, `cantidad` INTEGER NOT NULL)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_existencias_ubicacion_tienda_sucursalId_productoId_posicionId` ON `existencias_ubicacion_tienda` (`sucursalId`, `productoId`, `posicionId`)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `movimientos_ubicacion_tienda` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `usuarioId` INTEGER NOT NULL, `sucursalId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `posicionOrigenId` INTEGER, `posicionDestinoId` INTEGER NOT NULL, `cantidad` INTEGER NOT NULL, `tipo` TEXT NOT NULL, `cajaId` INTEGER, `fecha` INTEGER NOT NULL)")
                db.execSQL("INSERT OR IGNORE INTO posiciones_tienda(sucursalId,codigo,zona,categoria,activa) SELECT id,'REC','RECEPCION','General',1 FROM sucursales")
                // conservar el stock anterior
                db.execSQL("INSERT OR IGNORE INTO existencias_ubicacion_tienda(sucursalId,productoId,posicionId,cantidad) SELECT e.sucursalId,e.productoId,p.id,e.cantidad FROM existencias_tienda e JOIN posiciones_tienda p ON p.sucursalId=e.sucursalId AND p.codigo='REC' WHERE e.cantidad>0")
            }
        }

        private val MIGRACION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `solicitudes_reposicion` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sucursalId` INTEGER NOT NULL, `bodegaId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `cantidad` INTEGER NOT NULL, `solicitadoPor` INTEGER NOT NULL, `fecha` INTEGER NOT NULL, `estado` TEXT NOT NULL, `cajaId` INTEGER)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_solicitudes_reposicion_sucursalId_estado` ON `solicitudes_reposicion` (`sucursalId`, `estado`)")
                crearReglaUnaTiendaPorJefe(db)
            }
        }

        // agrupar las solicitudes de un pedido
        private val MIGRACION_9_10 = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `solicitudes_reposicion` ADD COLUMN `loteId` TEXT")
                db.execSQL("DELETE FROM bodega_sucursal WHERE NOT EXISTS (SELECT 1 FROM bodegas b WHERE b.id=bodega_sucursal.bodegaId AND b.sucursalId=bodega_sucursal.sucursalId)")
                db.execSQL("INSERT OR IGNORE INTO bodega_sucursal(bodegaId,sucursalId) SELECT id,sucursalId FROM bodegas")
            }
        }

        // nombres y niveles de ubicaciones
        private val MIGRACION_10_11 = object : Migration(10, 11) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `posiciones` ADD COLUMN `nombre` TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE `posiciones` ADD COLUMN `tipo` TEXT NOT NULL DEFAULT 'ESTANTERIA'")
                db.execSQL("ALTER TABLE `posiciones` ADD COLUMN `padreId` INTEGER")
                db.execSQL("ALTER TABLE `posiciones` ADD COLUMN `activa` INTEGER NOT NULL DEFAULT 1")
                db.execSQL("UPDATE `posiciones` SET nombre=codigo WHERE nombre=''")
                db.execSQL("ALTER TABLE `posiciones_tienda` ADD COLUMN `nombre` TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE `posiciones_tienda` ADD COLUMN `tipo` TEXT NOT NULL DEFAULT 'ESTANTERIA'")
                db.execSQL("ALTER TABLE `posiciones_tienda` ADD COLUMN `padreId` INTEGER")
                db.execSQL("UPDATE `posiciones_tienda` SET nombre=codigo WHERE nombre=''")
                db.execSQL("UPDATE `posiciones_tienda` SET tipo='RECEPCION' WHERE zona='RECEPCION'")
            }
        }

        // corregir ordenes iniciales que no fueron procesadas
        private val MIGRACION_11_12 = object : Migration(11, 12) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""UPDATE cajas SET sucursalDestinoId=(SELECT b.sucursalId FROM bodegas b WHERE b.id=cajas.bodegaOrigenId)
                    WHERE ((id=3 AND codigo='CAJ-DSP-101') OR (id=4 AND codigo='CAJ-SAL-001') OR (id=5 AND codigo='CAJ-SAL-002'))
                    AND tipo='SALIDA' AND estado='ORDEN_ASIGNADA'
                    AND sucursalDestinoId!=(SELECT b.sucursalId FROM bodegas b WHERE b.id=cajas.bodegaOrigenId)
                    AND EXISTS (SELECT 1 FROM bodegas b WHERE b.id=cajas.bodegaOrigenId)
                    AND NOT EXISTS (SELECT 1 FROM eventos_caja e WHERE e.cajaId=cajas.id)
                    AND NOT EXISTS (SELECT 1 FROM preparaciones_caja p WHERE p.cajaId=cajas.id)
                    AND NOT EXISTS (SELECT 1 FROM movimientos_tienda mt WHERE mt.cajaId=cajas.id)
                    AND NOT EXISTS (SELECT 1 FROM detalle_caja d WHERE d.cajaId=cajas.id
                        AND (d.cantidadVerificada>0 OR d.cantidadRecibida>0))""".trimIndent())
            }
        }

        // configuracion por sucursal
        private val MIGRACION_12_13 = object : Migration(12, 13) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `configuracion_sucursal` (`sucursalId` INTEGER NOT NULL, `bodegaPrincipalId` INTEGER, PRIMARY KEY(`sucursalId`))")
                db.execSQL("CREATE TABLE IF NOT EXISTS `minimos_sucursal` (`sucursalId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `minimo` INTEGER NOT NULL, PRIMARY KEY(`sucursalId`,`productoId`))")
                db.execSQL("CREATE TABLE IF NOT EXISTS `eventos_administracion` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sucursalId` INTEGER NOT NULL, `usuarioId` INTEGER NOT NULL, `accion` TEXT NOT NULL, `detalle` TEXT NOT NULL, `fecha` INTEGER NOT NULL)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_eventos_administracion_sucursalId` ON `eventos_administracion` (`sucursalId`)")
                db.execSQL("INSERT OR IGNORE INTO configuracion_sucursal(sucursalId,bodegaPrincipalId) SELECT s.id,(SELECT b.id FROM bodegas b WHERE b.sucursalId=s.id AND b.activa=1 ORDER BY b.id LIMIT 1) FROM sucursales s")
                crearReglaAdministradorUnaSucursal(db)
            }
        }

        // solicitudes de abastecimiento externo
        private val MIGRACION_13_14 = object : Migration(13, 14) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""CREATE TABLE IF NOT EXISTS `solicitudes_externas` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sucursalId` INTEGER NOT NULL, `bodegaId` INTEGER NOT NULL, `solicitanteId` INTEGER NOT NULL, `motivo` TEXT NOT NULL, `estado` TEXT NOT NULL, `fecha` INTEGER NOT NULL, `fechaRevision` INTEGER, `revisadoPor` INTEGER, `fechaDecision` INTEGER, `decididoPor` INTEGER, `observacion` TEXT NOT NULL, `entregaCajaId` INTEGER)""")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_solicitudes_externas_sucursalId_estado` ON `solicitudes_externas` (`sucursalId`,`estado`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_solicitudes_externas_bodegaId` ON `solicitudes_externas` (`bodegaId`)")
                db.execSQL("""CREATE TABLE IF NOT EXISTS `lineas_solicitud_externa` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `solicitudId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `cantidad` INTEGER NOT NULL)""")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_lineas_solicitud_externa_solicitudId` ON `lineas_solicitud_externa` (`solicitudId`)")
                db.execSQL("""CREATE TABLE IF NOT EXISTS `eventos_solicitud_externa` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `solicitudId` INTEGER NOT NULL, `usuarioId` INTEGER NOT NULL, `accion` TEXT NOT NULL, `detalle` TEXT NOT NULL, `fecha` INTEGER NOT NULL)""")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_eventos_solicitud_externa_solicitudId` ON `eventos_solicitud_externa` (`solicitudId`)")
            }
        }

        // informes cerrados para gerencia
        private val MIGRACION_14_15 = object : Migration(14, 15) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""CREATE TABLE IF NOT EXISTS `informes_inventario` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `inventarioId` INTEGER NOT NULL, `sucursalId` INTEGER NOT NULL, `bodegaId` INTEGER NOT NULL, `sucursalNombre` TEXT NOT NULL, `bodegaNombre` TEXT NOT NULL, `nombre` TEXT NOT NULL, `estadoCierre` TEXT NOT NULL, `fechaApertura` INTEGER NOT NULL, `fechaCierre` INTEGER NOT NULL, `cerradoPor` INTEGER NOT NULL, `posiciones` INTEGER NOT NULL, `productosContados` INTEGER NOT NULL, `productosSinContar` INTEGER NOT NULL, `lineasConDiferencia` INTEGER NOT NULL, `unidadesSistema` INTEGER NOT NULL, `unidadesContadas` INTEGER NOT NULL, `ajustesAplicados` INTEGER NOT NULL, `estadoRevision` TEXT NOT NULL, `revisadoPor` INTEGER, `fechaRevision` INTEGER)""")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_informes_inventario_inventarioId` ON `informes_inventario` (`inventarioId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_informes_inventario_estadoRevision_fechaCierre` ON `informes_inventario` (`estadoRevision`, `fechaCierre`)")
                db.execSQL("""CREATE TABLE IF NOT EXISTS `detalles_informe_inventario` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `informeId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `codigo` TEXT NOT NULL, `producto` TEXT NOT NULL, `posicion` TEXT NOT NULL, `cantidadSistema` INTEGER NOT NULL, `cantidadContada` INTEGER, `ajusteAplicado` INTEGER NOT NULL)""")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_detalles_informe_inventario_informeId` ON `detalles_informe_inventario` (`informeId`)")
            }
        }

        private fun crearReglaAdministradorUnaSucursal(db: SupportSQLiteDatabase) {
            db.execSQL("""CREATE TRIGGER IF NOT EXISTS `admin_una_sucursal`
                BEFORE INSERT ON `usuario_sucursal`
                WHEN (SELECT rol FROM usuarios WHERE id=NEW.usuarioId)='ADMIN_SUCURSAL'
                AND EXISTS(SELECT 1 FROM usuario_sucursal WHERE usuarioId=NEW.usuarioId)
                BEGIN SELECT RAISE(ABORT, 'El administrador pertenece a una sola sucursal'); END""")
            db.execSQL("""CREATE TRIGGER IF NOT EXISTS `admin_una_sucursal_rol`
                BEFORE UPDATE OF rol ON usuarios
                WHEN NEW.rol='ADMIN_SUCURSAL' AND OLD.rol!='ADMIN_SUCURSAL'
                AND (SELECT COUNT(*) FROM usuario_sucursal WHERE usuarioId=NEW.id)>1
                BEGIN SELECT RAISE(ABORT, 'Retire los permisos adicionales'); END""")
        }

        private val MIGRACION_15_16 = object : Migration(15, 16) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `salidas_tienda` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sucursalId` INTEGER NOT NULL, `usuarioId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `posicionId` INTEGER NOT NULL, `cantidad` INTEGER NOT NULL, `tipo` TEXT NOT NULL, `motivo` TEXT NOT NULL, `fecha` INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `inventarios_tienda` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sucursalId` INTEGER NOT NULL, `nombre` TEXT NOT NULL, `estado` TEXT NOT NULL, `fechaApertura` INTEGER NOT NULL, `cerradoPor` INTEGER, `fechaCierre` INTEGER, `estadoRevision` TEXT NOT NULL, `revisadoPor` INTEGER, `fechaRevision` INTEGER, `motivoCierre` TEXT NOT NULL)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_inventarios_tienda_sucursalId` ON `inventarios_tienda` (`sucursalId`)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `lineas_inventario_tienda` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `inventarioId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL, `posicionId` INTEGER NOT NULL, `stockInicio` INTEGER NOT NULL, `cantidadContada` INTEGER, `contadoPor` INTEGER, `fechaConteo` INTEGER)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_lineas_inventario_tienda_inventarioId_productoId_posicionId` ON `lineas_inventario_tienda` (`inventarioId`, `productoId`, `posicionId`)")
            }
        }

        private val MIGRACION_16_17 = object : Migration(16, 17) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `lineas_inventario_tienda` ADD COLUMN `codigoProductoCierre` TEXT")
                db.execSQL("ALTER TABLE `lineas_inventario_tienda` ADD COLUMN `nombreProductoCierre` TEXT")
                db.execSQL("ALTER TABLE `lineas_inventario_tienda` ADD COLUMN `rutaUbicacionCierre` TEXT")
            }
        }

        private val MIGRACION_17_18 = object : Migration(17, 18) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `detalle_caja` ADD COLUMN `cantidadContadaFisica` INTEGER")
                db.execSQL("""UPDATE detalle_caja SET cantidadContadaFisica=cantidadRecibida
                    WHERE cajaId IN (SELECT id FROM cajas WHERE tipo='INGRESO')""")
                db.execSQL("""UPDATE detalle_caja SET cantidadRecibida=MIN(cantidadRecibida,cantidadEsperada)
                    WHERE cajaId IN (SELECT id FROM cajas WHERE tipo='INGRESO'
                        AND estado IN ('RECIBIDA','CON_DIFERENCIA'))""")
                db.execSQL("""UPDATE detalle_caja SET cantidadRecibida=0
                    WHERE cajaId IN (SELECT id FROM cajas WHERE tipo='INGRESO'
                        AND estado IN ('ESPERADA','EN_TRANSITO'))""")
                db.execSQL("ALTER TABLE `inventarios_tienda` ADD COLUMN `verificacionVaciaPor` INTEGER")
                db.execSQL("ALTER TABLE `inventarios_tienda` ADD COLUMN `fechaVerificacionVacia` INTEGER")
                db.execSQL("""CREATE TABLE IF NOT EXISTS `eventos_conteo_tienda` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `inventarioId` INTEGER NOT NULL, `lineaId` INTEGER NOT NULL,
                    `productoId` INTEGER NOT NULL, `posicionId` INTEGER NOT NULL,
                    `usuarioId` INTEGER NOT NULL, `cantidadAnterior` INTEGER,
                    `cantidadNueva` INTEGER NOT NULL, `tipo` TEXT NOT NULL, `fecha` INTEGER NOT NULL)""")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_eventos_conteo_tienda_inventarioId_fecha` ON `eventos_conteo_tienda` (`inventarioId`,`fecha`)")
            }
        }

        private val MIGRACION_18_19 = object : Migration(18, 19) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `periodos_inventario` ADD COLUMN `verificacionFisicaPor` INTEGER")
                db.execSQL("ALTER TABLE `periodos_inventario` ADD COLUMN `fechaVerificacionFisica` INTEGER")
            }
        }

        // Preserva la administración de trabajadores sin asignación operativa.
        private val MIGRACION_19_20 = object : Migration(19, 20) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `usuarios` ADD COLUMN `sucursalResponsableId` INTEGER DEFAULT NULL")
                db.execSQL("""UPDATE usuarios SET sucursalResponsableId=COALESCE(
                    (SELECT MIN(us.sucursalId) FROM usuario_sucursal us WHERE us.usuarioId=usuarios.id),
                    (SELECT MIN(b.sucursalId) FROM usuario_bodega ub JOIN bodegas b ON b.id=ub.bodegaId
                        WHERE ub.usuarioId=usuarios.id))
                    WHERE rol IN ('OPERADOR_BODEGA','JEFE_BODEGA','TRABAJADOR_TIENDA','JEFE_TIENDA')""")
            }
        }

        private val MIGRACION_20_21 = object : Migration(20, 21) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""CREATE TABLE IF NOT EXISTS `zonas_conteo_tienda` (
                    `inventarioId` INTEGER NOT NULL, `posicionId` INTEGER NOT NULL,
                    `asignadoA` INTEGER NOT NULL, `confirmadoPor` INTEGER,
                    `fechaConfirmacion` INTEGER,
                    PRIMARY KEY(`inventarioId`,`posicionId`))""")
                // Los inventarios de tienda anteriores conservan sus conteos y cierres.
                db.execSQL("""INSERT OR IGNORE INTO zonas_conteo_tienda(inventarioId,posicionId,asignadoA,confirmadoPor,fechaConfirmacion)
                    SELECT l.inventarioId,l.posicionId,COALESCE(MAX(l.contadoPor),0),
                        CASE WHEN i.estado='CERRADO' THEN MAX(l.contadoPor) ELSE NULL END,
                        CASE WHEN i.estado='CERRADO' THEN MAX(l.fechaConteo) ELSE NULL END
                    FROM lineas_inventario_tienda l JOIN inventarios_tienda i ON i.id=l.inventarioId
                    GROUP BY l.inventarioId,l.posicionId""")
            }
        }

        private val MIGRACION_21_22 = object : Migration(21, 22) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""CREATE TABLE IF NOT EXISTS `verificaciones_estante_vacio` (
                    `tipo` TEXT NOT NULL, `inventarioId` INTEGER NOT NULL,
                    `posicionId` INTEGER NOT NULL, `usuarioId` INTEGER NOT NULL,
                    `fecha` INTEGER NOT NULL, PRIMARY KEY(`tipo`, `inventarioId`, `posicionId`))""")
            }
        }

        private val MIGRACION_22_23 = object : Migration(22, 23) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `inventarios_bodega` ADD COLUMN `campaniaId` INTEGER")
                db.execSQL("ALTER TABLE `inventarios_tienda` ADD COLUMN `campaniaId` INTEGER")
                db.execSQL("""CREATE TABLE IF NOT EXISTS `campanias_mensuales` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `tipo` TEXT NOT NULL,
                    `ubicacionId` INTEGER NOT NULL, `sucursalId` INTEGER NOT NULL, `mes` TEXT NOT NULL, `estado` TEXT NOT NULL,
                    `fechaInicio` INTEGER NOT NULL, `fechaFinal` INTEGER,
                    `revision` TEXT NOT NULL, `revisadaPor` INTEGER, `fechaRevision` INTEGER)""")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_campanias_mensuales_tipo_ubicacionId_mes` ON `campanias_mensuales` (`tipo`, `ubicacionId`, `mes`)")
                db.execSQL("""CREATE TABLE IF NOT EXISTS `zonas_campania` (
                    `campaniaId` INTEGER NOT NULL, `posicionId` INTEGER NOT NULL, `ruta` TEXT NOT NULL,
                    `estado` TEXT NOT NULL, `inventarioId` INTEGER, `fechaConteo` INTEGER,
                    PRIMARY KEY(`campaniaId`, `posicionId`))""")
                db.execSQL("""CREATE TABLE IF NOT EXISTS `detalles_campania` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `campaniaId` INTEGER NOT NULL,
                    `inventarioId` INTEGER NOT NULL, `posicionId` INTEGER NOT NULL,
                    `codigo` TEXT NOT NULL, `producto` TEXT NOT NULL, `ruta` TEXT NOT NULL,
                    `cantidadSistema` INTEGER NOT NULL, `cantidadContada` INTEGER,
                    `ajusteAplicado` INTEGER NOT NULL, `fechaCierre` INTEGER NOT NULL)""")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_detalles_campania_campaniaId_posicionId` ON `detalles_campania` (`campaniaId`, `posicionId`)")
            }
        }

        // Ubicación física de recepción en bodegas EXISTENTES. No se mueve stock previo.
        private val MIGRACION_23_24 = object : Migration(23, 24) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""INSERT OR IGNORE INTO posiciones(id,bodegaId,codigo,nombre,tipo,padreId,activa)
                    SELECT -id,id,'REC','Zona de recepción','RECEPCION',NULL,1 FROM bodegas
                    WHERE NOT EXISTS (SELECT 1 FROM posiciones p WHERE p.bodegaId=bodegas.id AND p.tipo='RECEPCION')""")
            }
        }

        // Cotizaciones financieras; no se agregan precios a productos, stock ni pantallas de bodega.
        private val MIGRACION_24_25 = object : Migration(24, 25) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `solicitudes_externas` ADD COLUMN `proveedorCotizado` TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE `lineas_solicitud_externa` ADD COLUMN `precioUnitarioCotizado` INTEGER")
            }
        }

        // No se modifican precios antiguos ni se imputan valores a ventas previas.
        private val MIGRACION_25_26 = object : Migration(25, 26) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""CREATE TABLE IF NOT EXISTS `precios_venta_sucursal` (
                    `sucursalId` INTEGER NOT NULL, `productoId` INTEGER NOT NULL,
                    `precioClp` INTEGER NOT NULL, `actualizadoPor` INTEGER NOT NULL,
                    `fechaActualizacion` INTEGER NOT NULL,
                    PRIMARY KEY(`sucursalId`, `productoId`))""")
                db.execSQL("ALTER TABLE `salidas_tienda` ADD COLUMN `precioUnitarioVentaClp` INTEGER")
                db.execSQL("ALTER TABLE `salidas_tienda` ADD COLUMN `totalVentaClp` INTEGER")
            }
        }

        private fun crearReglaUnaTiendaPorJefe(db: SupportSQLiteDatabase) {
            db.execSQL("""CREATE TRIGGER IF NOT EXISTS `jefe_una_tienda`
                BEFORE INSERT ON `usuario_sucursal`
                WHEN (SELECT rol FROM usuarios WHERE id=NEW.usuarioId)='JEFE_TIENDA'
                AND EXISTS(SELECT 1 FROM usuario_sucursal WHERE usuarioId=NEW.usuarioId)
                BEGIN SELECT RAISE(ABORT, 'Un jefe solo puede estar asociado a una tienda'); END""")
            db.execSQL("""CREATE TRIGGER IF NOT EXISTS `jefe_una_tienda_actualizacion`
                BEFORE UPDATE OF rol ON usuarios
                WHEN NEW.rol='JEFE_TIENDA' AND OLD.rol!='JEFE_TIENDA'
                AND (SELECT COUNT(*) FROM usuario_sucursal WHERE usuarioId=NEW.id) > 1
                BEGIN SELECT RAISE(ABORT, 'Primero retira las tiendas adicionales del jefe'); END""")
        }

        private fun crearReglaUnaBodegaPorJefe(db: SupportSQLiteDatabase) {
            db.execSQL("""CREATE TRIGGER IF NOT EXISTS `jefe_una_bodega`
                BEFORE INSERT ON `usuario_bodega`
                WHEN (SELECT rol FROM usuarios WHERE id=NEW.usuarioId)='JEFE_BODEGA'
                AND EXISTS(SELECT 1 FROM usuario_bodega WHERE usuarioId=NEW.usuarioId)
                BEGIN SELECT RAISE(ABORT, 'Un jefe solo puede estar asociado a una bodega'); END""")
            db.execSQL("""CREATE TRIGGER IF NOT EXISTS `jefe_una_bodega_actualizacion`
                BEFORE UPDATE OF rol ON usuarios
                WHEN NEW.rol='JEFE_BODEGA' AND OLD.rol!='JEFE_BODEGA'
                AND (SELECT COUNT(*) FROM usuario_bodega WHERE usuarioId=NEW.id) > 1
                BEGIN SELECT RAISE(ABORT, 'Primero retira las bodegas adicionales del jefe'); END""")
        }

        @Volatile private var instancia: AppDatabase? = null
        // Reutiliza o crea la instancia de Room.
        fun getDatabase(context: Context): AppDatabase = instancia ?: synchronized(this) {
            // Archivo local y configuracion de Room.
            instancia ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java,
                "boxview_operador_cajas_v1.db").addMigrations(MIGRACION_1_2, MIGRACION_2_3, MIGRACION_3_4, MIGRACION_4_5, MIGRACION_5_6, MIGRACION_6_7, MIGRACION_7_8, MIGRACION_8_9, MIGRACION_9_10, MIGRACION_10_11, MIGRACION_11_12, MIGRACION_12_13, MIGRACION_13_14, MIGRACION_14_15, MIGRACION_15_16, MIGRACION_16_17, MIGRACION_17_18, MIGRACION_18_19, MIGRACION_19_20, MIGRACION_20_21, MIGRACION_21_22, MIGRACION_22_23, MIGRACION_23_24, MIGRACION_24_25, MIGRACION_25_26)
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) { super.onCreate(db); crearReglaUnaBodegaPorJefe(db); crearReglaUnaTiendaPorJefe(db); crearReglaAdministradorUnaSucursal(db) }
                    override fun onOpen(db: SupportSQLiteDatabase) { super.onOpen(db); crearReglaUnaBodegaPorJefe(db); crearReglaUnaTiendaPorJefe(db); crearReglaAdministradorUnaSucursal(db) }
                }).build().also { instancia = it }
        }
    }
}
