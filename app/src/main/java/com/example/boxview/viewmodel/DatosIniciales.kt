package com.example.boxview.viewmodel

// Lógica: Carga cuentas y datos de ejemplo en instalaciones nuevas.
import android.util.Base64
import com.example.boxview.modelo.*
import java.security.SecureRandom

// Catálogo físico de ejemplo: cada estante tiene un ID estable y pertenece a un pasillo.
// Los IDs 101..111 y 201..211 conservan los de versiones anteriores.
private data class EstanteDemo(val pasillo: Int, val codigo: String, val categoria: String)
private val estantesDemo = listOf(
    // Pasillo 1: pantalones y poleras; Pasillo 2: abrigo; 3: superiores; 4: complementos.
    EstanteDemo(1, "A-1", "Pantalones"), EstanteDemo(1, "A-2", "Pantalones"),
    EstanteDemo(1, "B-1", "Poleras"), EstanteDemo(1, "B-2", "Poleras"),
    EstanteDemo(2, "C-1", "Polerones"), EstanteDemo(2, "C-2", "Polerones"),
    EstanteDemo(2, "D-1", "Chaquetas"), EstanteDemo(3, "E-1", "Camisas"),
    EstanteDemo(3, "F-1", "Chalecos"), EstanteDemo(4, "G-1", "Faldas"),
    EstanteDemo(4, "H-1", "Vestidos")
)

private fun posicionesPara(bodegaId: Long, categoria: String): List<Long> =
    estantesDemo.mapIndexedNotNull { i, estante ->
        if (estante.categoria == categoria) bodegaId * 100 + i + 1 else null
    }

private suspend fun crearEstantesPorSeccion(dao: InventarioDao) {
    for (bodegaId in listOf(1L, 2L)) {
        if (dao.existeBodega(bodegaId) == 0) continue
        dao.guardarPosicionSiFalta(Posicion(-bodegaId, bodegaId, "REC", "Zona de recepción", "RECEPCION"))
        // No agregar pasillos/estantes vacíos sobre una bodega con ubicaciones propias.
        val existentes = dao.posicionesActuales(bodegaId)
        val catalogoLegacy = existentes.any { it.id == bodegaId * 100 + 1 &&
            it.codigo == "Sección 1-A · Pantalones" &&
            (it.nombre.isBlank() || it.nombre == it.codigo) }
        val catalogoNuevo = existentes.any { it.id == bodegaId * 100 + 1 &&
            it.codigo == "P1-A-1" }
        val recienCreada = existentes.none { it.tipo != "RECEPCION" }
        if (!recienCreada && !catalogoLegacy && !catalogoNuevo) continue
        // Si ya hubo conteos con nombres originales, no cambiar ni incorporar estantes demo.
        if (dao.historicosInventarioBodega(bodegaId) != 0) continue
        estantesDemo.map { it.pasillo }.distinct().forEach { pasillo ->
            val id = bodegaId * 1000 + pasillo
            dao.guardarPosicionSiFalta(Posicion(id, bodegaId, "P$pasillo", "Pasillo $pasillo", "SECCION"))
        }
        // Solo renombrar el catálogo legacy si nunca se realizó un inventario en esta bodega.
        val actualizarLegacy = dao.historicosInventarioBodega(bodegaId) == 0
        estantesDemo.forEachIndexed { indice, estante ->
            dao.guardarPosicionSiFalta(Posicion(
                id = bodegaId * 100 + indice + 1,
                bodegaId = bodegaId,
                codigo = "P${estante.pasillo}-${estante.codigo}",
                nombre = "Pasillo ${estante.pasillo} · Estante ${estante.codigo} · ${estante.categoria}",
                tipo = "ESTANTERIA",
                padreId = bodegaId * 1000 + estante.pasillo
            ))
            if (actualizarLegacy) {
                val codigoAnterior = listOf("1-A", "1-B", "2-A", "2-B", "3-A", "3-B",
                    "4-A", "5-A", "6-A", "7-A", "8-A")[indice]
                dao.mejorarEstanteDemoBodega(bodegaId * 100 + indice + 1, bodegaId,
                    "Sección $codigoAnterior · ${estante.categoria}",
                    "P${estante.pasillo}-${estante.codigo}",
                    "Pasillo ${estante.pasillo} · Estante ${estante.codigo} · ${estante.categoria}",
                    bodegaId * 1000 + estante.pasillo)
            }
        }
    }
}

// Los precios son de venta, por TIENDA: nunca son precios de bodega ni costos de adquisición.
private val preciosAlameda = listOf(8990L, 24990L, 19990L, 34990L, 22990L,
    26990L, 17990L, 15990L, 29990L, 12990L)
private val preciosProvidencia = listOf(9990L, 25990L, 21990L, 36990L, 23990L,
    27990L, 18990L, 16990L, 31990L, 13990L)
private val codigosCatalogoDemo = (1..10).map { "ROP-" + it.toString().padStart(3, '0') }

private suspend fun asegurarComercioInicial(dao: InventarioDao, preciosDao: PreciosVentaDao) {
    // No intervenir catálogos ajenos ni sobrescribir los precios/minimos personalizados.
    if (!codigosCatalogoDemo.withIndex().all { (i, codigo) ->
        dao.productoPorId((i + 1).toLong())?.codigo == codigo
    }) return
    for (sid in listOf(1L, 2L)) {
        if (dao.existeSucursal(sid) == 0) continue
        val correo = if (sid == 1L) "admin.alameda@boxview.cl" else "admin.providencia@boxview.cl"
        val admin = dao.usuarioPorCorreo(correo) ?: continue
        if (admin.rol != "ADMIN_SUCURSAL" || !admin.activo || dao.administradorAutorizado(admin.id, sid) != 1) continue
        for (indice in codigosCatalogoDemo.indices) {
            val pid = (indice + 1).toLong()
            val precio = (if (sid == 1L) preciosAlameda else preciosProvidencia)[indice]
            preciosDao.guardarPrecioSiFalta(PrecioVentaSucursal(sid, pid, precio, admin.id))
            // Con dos productos críticos reales por tienda para probar alertas administrativas.
            val minimo = if (indice in listOf(2, 5, 7)) 7 else 6
            dao.guardarMinimoSiFalta(MinimoSucursal(sid, pid, minimo))
        }
    }
}


// Ventas y mermas históricas en ambas tiendas: cantidades y precios aplicados realmente.
// Se aplican descuentos REALES a la ubicación y total; nunca se inyecta un importe sin salida.
private suspend fun crearSalidasDemo(dao: InventarioDao, preciosDao: PreciosVentaDao,
                                     operaciones: OperacionesTiendaDao) {
    data class SalidaEjemplo(val sid: Long, val producto: Long, val codigoUbicacion: String,
                             val cantidad: Int, val tipo: String)
    val ejemplos = listOf(
        SalidaEjemplo(1, 1, "2-1", 2, "VENTA"),
        SalidaEjemplo(1, 6, "1-1", 3, "VENTA"),
        SalidaEjemplo(1, 3, "3-1", 1, "VENTA"),
        SalidaEjemplo(1, 10, "2-1", 3, "VENTA"),
        SalidaEjemplo(2, 2, "1-1", 1, "VENTA"),
        SalidaEjemplo(2, 4, "4-1", 2, "VENTA"),
        SalidaEjemplo(2, 9, "8-1", 2, "VENTA"),
        SalidaEjemplo(2, 7, "6-1", 1, "VENTA"),
        SalidaEjemplo(2, 1, "2-1", 2, "VENTA"),
        SalidaEjemplo(1, 8, "7-1", 1, "MERMA"),
        SalidaEjemplo(2, 5, "5-1", 1, "MERMA")
    )
    for (ejemplo in ejemplos) {
        val correo = if (ejemplo.sid == 1L) "tienda.alameda@boxview.cl"
            else "tienda.providencia@boxview.cl"
        val uid = dao.usuarioPorCorreo(correo)?.id ?: error("Falta el trabajador de tienda")
        val posicion = dao.posicionesTiendaActuales(ejemplo.sid)
            .firstOrNull { it.codigo == ejemplo.codigoUbicacion } ?: error("Falta estante de demostración")
        val existente = dao.stockUbicacionTienda(ejemplo.sid, ejemplo.producto, posicion.id)
            ?: error("Falta stock en estante de venta")
        require(dao.restarStockUbicacionTienda(existente.id, ejemplo.cantidad) == 1) {
            "Stock de demostración insuficiente en estante"
        }
        require(operaciones.descontarTotal(ejemplo.sid, ejemplo.producto, ejemplo.cantidad) == 1) {
            "Stock general de demostración insuficiente"
        }
        val precio = if (ejemplo.tipo == "VENTA")
            preciosDao.precioActual(ejemplo.sid, ejemplo.producto)?.precioClp
                ?: error("Falta precio de venta inicial") else null
        operaciones.registrarSalida(SalidaTienda(sucursalId = ejemplo.sid, usuarioId = uid,
            productoId = ejemplo.producto, posicionId = posicion.id,
            cantidad = ejemplo.cantidad, tipo = ejemplo.tipo,
            motivo = if (ejemplo.tipo == "VENTA") "Venta registrada"
                else "Prenda dañada",
            precioUnitarioVentaClp = precio,
            totalVentaClp = precio?.times(ejemplo.cantidad.toLong())))
    }
}

// Cajas que YA SALIERON de bodega: el stock de origen se descontó una sola vez.
// Hasta que tienda confirme la comprobación, estas unidades no ingresan al stock de tienda.
// Los códigos son exactamente el texto de los PNG en CODIGOS_QR_TIENDA/.
private suspend fun registrarDespachoEnTransitoDePrueba(
    dao: InventarioDao, id: Long, codigo: String, bodegaId: Long,
    codigoJefe: String, codigoOperador: String, productoId: Long,
    categoria: String, cantidad: Int
) {
    require(dao.cajaPorCodigo(codigo) == null) { "El código $codigo ya existe" }
    val jefe = dao.usuarioPorCorreo(codigoJefe)?.id ?: error("Falta jefe de $codigo")
    val operador = dao.usuarioPorCorreo(codigoOperador)?.id ?: error("Falta operador de $codigo")
    val origen = posicionesPara(bodegaId, categoria).first()
    val existencia = dao.existencia(productoId, origen) ?: error("Sin stock para $codigo")
    require(dao.restarSiAlcanza(existencia.id, cantidad) == 1) {
        "No hay unidades disponibles para el despacho $codigo"
    }
    dao.guardarMovimiento(Movimiento(usuarioId = jefe, bodegaId = bodegaId,
        productoId = productoId, posicionOrigen = origen, posicionDestino = 0,
        cantidad = cantidad, tipo = "SALIDA_CAJA"))
    dao.guardarCaja(Caja(id, codigo, "SALIDA",
        if (bodegaId == 1L) "Bodega Alameda" else "Bodega Providencia",
        bodegaId, 0,
        if (bodegaId == 1L) "Jefe de Bodega Alameda" else "Jefe de Bodega Providencia",
        operador, "EN_TRANSITO", preparadoPor = operador,
        revisorId = jefe, sucursalDestinoId = bodegaId))
    dao.guardarDetalleCaja(DetalleCaja(cajaId = id, productoId = productoId,
        cantidadEsperada = cantidad, cantidadVerificada = cantidad, posicionId = origen))
    dao.guardarPreparacionCaja(PreparacionCaja(cajaId = id, productoId = productoId,
        posicionId = origen, cantidad = cantidad))
    dao.guardarEventoCaja(EventoCaja(cajaId = id, usuarioId = jefe,
        accion = "DESPACHO_REVISADO",
        observacion = "Despacho hacia tienda confirmado. Caja en tránsito"))
}

private suspend fun crearDespachosEnTransitoIniciales(dao: InventarioDao) {
    registrarDespachoEnTransitoDePrueba(dao, 6L, "CAJ-TRA-001", 1L,
        "jefe.bodega@boxview.cl", "bodega@boxview.cl", 2L, "Pantalones", 2)
    registrarDespachoEnTransitoDePrueba(dao, 9L, "CAJ-TRA-002", 2L,
        "jefe.providencia@boxview.cl", "bodega.providencia@boxview.cl", 5L, "Camisas", 3)
}

// Adaptación no destructiva para quien ya había instalado la versión anterior:
// 1) Solo identifica el escenario original por códigos y destinos.
// 2) Nunca cambia cajas ya recibidas ni crea ventas ficticias nuevas.
// 3) Agrega el despacho de Providencia únicamente si conserva las cajas de prueba.
// Todas las operaciones se llaman dentro de la transacción de inicialización de Room.
private suspend fun actualizarCajasQrDePruebaInstaladas(dao: InventarioDao) {
    val entradas = listOf(
        Triple("CAJ-ING-001", 1L, "Proveedor Textiles Andes"),
        Triple("CAJ-ING-002", 2L, "Proveedor Vestuario Sur")
    )
    entradas.forEach { (codigo, destino, origen) ->
        val caja = dao.cajaPorCodigo(codigo)
        if (caja != null && caja.tipo == "INGRESO" && caja.bodegaDestinoId == destino &&
            caja.origen == origen && caja.estado == "ESPERADA") {
            dao.actualizarCaja(caja.copy(estado = "EN_TRANSITO"))
        }
    }
    // En un proyecto real no se insertarían envíos simulados sobre operaciones reales.
    val envioAlameda = dao.cajaPorCodigo("CAJ-TRA-001")
    val demoReconocible = envioAlameda?.tipo == "SALIDA" &&
        envioAlameda.bodegaOrigenId == 1L && envioAlameda.sucursalDestinoId == 1L &&
        dao.cajaPorCodigo("CAJ-ING-001") != null && dao.cajaPorCodigo("CAJ-ING-002") != null &&
        dao.productoPorId(5L)?.codigo == "ROP-005"
    if (!demoReconocible || dao.cajaPorCodigo("CAJ-TRA-002") != null) return
    val origen = posicionesPara(2L, "Camisas").first()
    val existencia = dao.existencia(5L, origen) ?: return
    if (existencia.cantidad < 3) return
    if (dao.usuarioPorCorreo("jefe.providencia@boxview.cl") == null ||
        dao.usuarioPorCorreo("bodega.providencia@boxview.cl") == null) return
    registrarDespachoEnTransitoDePrueba(dao, dao.siguienteIdCaja(), "CAJ-TRA-002", 2L,
        "jefe.providencia@boxview.cl", "bodega.providencia@boxview.cl", 5L, "Camisas", 3)
}

// Solicitudes de ejemplo enlazadas a los mismos ingresos esperados, para probar 4 roles.
// Solo se llama al crear una base completamente nueva.
private suspend fun crearSolicitudesDemo(dao: InventarioDao, externo: AbastecimientoExternoDao) {
    val gerente = dao.usuarioPorCorreo("gerencia@boxview.cl")?.id ?: return
    for (sid in listOf(1L, 2L)) {
        val jefe = dao.usuarioPorCorreo(if (sid == 1L) "jefe.bodega@boxview.cl"
            else "jefe.providencia@boxview.cl")?.id ?: continue
        val admin = dao.usuarioPorCorreo(if (sid == 1L) "admin.alameda@boxview.cl"
            else "admin.providencia@boxview.cl")?.id ?: continue
        val cajaId = sid
        val proveedor = if (sid == 1L) "Textiles Andes" else "Vestuario Sur"
        val detalles = dao.detallesCaja(cajaId)
        if (detalles.isEmpty()) continue
        val solicitud = externo.nuevaSolicitud(SolicitudExterna(
            sucursalId = sid, bodegaId = sid, solicitanteId = jefe,
            motivo = "Reposición de temporada para bodega y tienda", estado = "ENTREGA_PROGRAMADA",
            revisadoPor = admin, decididoPor = gerente, entregaCajaId = cajaId,
            proveedorCotizado = proveedor
        ))
        detalles.forEach { d ->
            val costo = listOf(4200L, 11500L, 9600L, 16800L, 10200L,
                13200L, 8600L, 7400L, 14200L, 6100L)[d.productoId.toInt() - 1]
            externo.nuevaLinea(LineaSolicitudExterna(solicitudId = solicitud,
                productoId = d.productoId, cantidad = d.cantidadEsperada, precioUnitarioCotizado = costo))
        }
        externo.nuevoEvento(EventoSolicitudExterna(solicitudId = solicitud, usuarioId = jefe,
            accion = "CREADA", detalle = "Solicitud de abastecimiento"))
        externo.nuevoEvento(EventoSolicitudExterna(solicitudId = solicitud, usuarioId = admin,
            accion = "ENVIADA_GERENCIA", detalle = "Cotización recibida de $proveedor"))
        externo.nuevoEvento(EventoSolicitudExterna(solicitudId = solicitud, usuarioId = gerente,
            accion = "APROBADA", detalle = "Compra autorizada para pruebas de recepción"))
        externo.nuevoEvento(EventoSolicitudExterna(solicitudId = solicitud, usuarioId = admin,
            accion = "ENTREGA_PROGRAMADA", detalle = "Caja CAJ-ING-00$sid, sin ingreso físico de stock"))
    }
    // Una solicitud esperando la cotización de Administración y otra esperando a Gerencia.
    val jefe1 = dao.usuarioPorCorreo("jefe.bodega@boxview.cl")?.id ?: return
    val jefe2 = dao.usuarioPorCorreo("jefe.providencia@boxview.cl")?.id ?: return
    val admin2 = dao.usuarioPorCorreo("admin.providencia@boxview.cl")?.id ?: return
    val pendiente = externo.nuevaSolicitud(SolicitudExterna(sucursalId = 1, bodegaId = 1,
        solicitanteId = jefe1, motivo = "Reponer pantalones con alta rotación"))
    externo.nuevaLinea(LineaSolicitudExterna(solicitudId = pendiente, productoId = 2, cantidad = 14))
    externo.nuevoEvento(EventoSolicitudExterna(solicitudId = pendiente,
        usuarioId = jefe1, accion = "CREADA", detalle = "Pendiente de cotización de Administración"))
    val pendienteGerencia = externo.nuevaSolicitud(SolicitudExterna(sucursalId = 2, bodegaId = 2,
        solicitanteId = jefe2, motivo = "Reposición de polerones para la temporada",
        estado = "EN_GERENCIA", revisadoPor = admin2, proveedorCotizado = "Textiles Cordillera"))
    externo.nuevaLinea(LineaSolicitudExterna(solicitudId = pendienteGerencia, productoId = 3,
        cantidad = 12, precioUnitarioCotizado = 10500L))
    externo.nuevoEvento(EventoSolicitudExterna(solicitudId = pendienteGerencia,
        usuarioId = jefe2, accion = "CREADA", detalle = "Solicitud enviada a Administración"))
    externo.nuevoEvento(EventoSolicitudExterna(solicitudId = pendienteGerencia,
        usuarioId = admin2, accion = "ENVIADA_GERENCIA", detalle = "Cotización completa"))
}

/**
 * Recuperación para una instalación NUEVA si falla la carga opcional de demostración.
 * Solo se llama cuando la transacción de carga inicial se revirtió y no hay usuarios.
 * Crea cuentas y ámbitos mínimos. NO toca contraseñas ni stock de instalaciones previas.
 * Se ejecuta por separado, dentro de su propia transacción de Room.
 */
internal suspend fun asegurarAccesoBasicoTrasFalloInicial(
    dao: InventarioDao, calcularHash: (String, ByteArray) -> String
) {
    require(dao.numeroUsuarios() == 0) {
        "No se pueden restablecer contraseñas de una instalación existente"
    }
    dao.guardarSucursal(Sucursal(1, "BoxView Alameda"))
    dao.guardarSucursal(Sucursal(2, "BoxView Providencia"))
    dao.guardarBodega(Bodega(1, 1, "Bodega Alameda"))
    dao.guardarBodega(Bodega(2, 2, "Bodega Providencia"))
    val sal = ByteArray(16).also { SecureRandom().nextBytes(it) }
    dao.guardarUsuario(Usuario(1, "Camila Soto", "bodega@boxview.cl",
        calcularHash("12345", sal), Base64.encodeToString(sal, Base64.NO_WRAP), "OPERADOR_BODEGA"))
    dao.guardarPermiso(UsuarioBodega(1, 1))
    asegurarOperadorProvidencia(dao, calcularHash)
    asegurarJefeDemo(dao, calcularHash)
    asegurarTrabajadoresTienda(dao, calcularHash)
    asegurarJefesTienda(dao, calcularHash)
    asegurarAdministradoresSucursal(dao, calcularHash)
    asegurarGerencia(dao, calcularHash)
    asegurarAbastecimiento(dao)
}

/** Exclusivamente para recuperar una demo que se quedó con el login pero sin catálogo.
 * No se utiliza cuando ya existen productos, cajas, compras o ventas de usuarios.
 */
private suspend fun entornoDemoSoloAcceso(dao: InventarioDao, externo: AbastecimientoExternoDao,
                                          operaciones: OperacionesTiendaDao): Boolean {
    if (dao.numeroProductos() != 0 || dao.numeroCajas() != 0 ||
        externo.numeroSolicitudes() != 0 || operaciones.numeroSalidas() != 0) return false
    if (dao.existeBodega(1L) == 0 || dao.existeBodega(2L) == 0 ||
        dao.existeSucursal(1L) == 0 || dao.existeSucursal(2L) == 0) return false
    val operador = dao.usuarioPorCorreo("bodega@boxview.cl")
    val gerencia = dao.usuarioPorCorreo("gerencia@boxview.cl")
    return operador?.rol == "OPERADOR_BODEGA" && gerencia?.rol == "GERENTE" &&
        dao.usuarioPorCorreo("admin.alameda@boxview.cl")?.rol == "ADMIN_SUCURSAL"
}

/** Escenario autoconsistente: cada cifra en paneles procede de tablas Room relacionadas.
 * Llamar únicamente en primera instalación o en la demo vacía recuperada.
 */
private suspend fun crearEscenarioOperativoDemo(dao: InventarioDao, preciosDao: PreciosVentaDao,
                                                externo: AbastecimientoExternoDao,
                                                operaciones: OperacionesTiendaDao,
                                                informes: InformesGerenciaDao,
                                                calcularHash: (String, ByteArray) -> String) {
    val operadorProvidencia = dao.usuarioPorCorreo("bodega.providencia@boxview.cl")?.id
        ?: error("Falta el operador de bodega Providencia")
    crearEstantesPorSeccion(dao)
    val ropa = listOf(
        Triple("ROP-001", "Polera básica blanca", "Poleras"),
        Triple("ROP-002", "Jeans azul clásico", "Pantalones"),
        Triple("ROP-003", "Polerón oversize", "Polerones"),
        Triple("ROP-004", "Chaqueta de mezclilla", "Chaquetas"),
        Triple("ROP-005", "Camisa de lino", "Camisas"),
        Triple("ROP-006", "Pantalón cargo negro", "Pantalones"),
        Triple("ROP-007", "Chaleco tejido beige", "Chalecos"),
        Triple("ROP-008", "Falda plisada", "Faldas"),
        Triple("ROP-009", "Vestido midi", "Vestidos"),
        Triple("ROP-010", "Polera gráfica", "Poleras")
    )
    ropa.forEachIndexed { i, item ->
        val pid = (i + 1).toLong()
        dao.guardarProducto(Producto(pid, item.first, item.second, item.third, minimo = 6))
        // Las cajas ESPERADAS no alteran las existencias: esto es stock previo real del ejemplo.
        val totalesAlameda = listOf(42, 36, 30, 25, 26, 28, 21, 18, 24, 33)
        val totalesProvidencia = listOf(33, 29, 26, 23, 21, 25, 18, 19, 17, 27)
        listOf(1L to totalesAlameda[i], 2L to totalesProvidencia[i]).forEach { (bodega, total) ->
            val ubicaciones = posicionesPara(bodega, item.third)
            val recepcion = if (i in listOf(0, 1, 9)) 3 else 0
            val disponibleEstantes = total - recepcion
            val unidadesPrimero = if (ubicaciones.size > 1) (disponibleEstantes + 1) / 2 else disponibleEstantes
            dao.guardarExistencia(Existencia(productoId = pid, posicionId = ubicaciones.first(),
                cantidad = unidadesPrimero))
            if (ubicaciones.size > 1) {
                dao.guardarExistencia(Existencia(productoId = pid, posicionId = ubicaciones[1],
                    cantidad = disponibleEstantes - unidadesPrimero))
            }
            if (recepcion > 0) dao.guardarExistencia(Existencia(productoId = pid,
                posicionId = -bodega, cantidad = recepcion))
        }
    }
    // stock inicial de tiendas
    for (sucursal in listOf(1L, 2L)) {
        for (producto in 1L..10L) {
            dao.guardarExistenciaTienda(ExistenciaTienda(sucursalId = sucursal,
                productoId = producto, cantidad = (if (sucursal == 1L)
                    listOf(16, 12, 4, 11, 9, 5, 8, 3, 10, 14)
                else listOf(18, 11, 13, 4, 10, 9, 3, 12, 7, 15))[producto.toInt() - 1]))
        }
    }
    asegurarJefeDemo(dao, calcularHash)
    asegurarTrabajadoresTienda(dao, calcularHash)
    asegurarJefesTienda(dao, calcularHash)
    asegurarUbicacionesTienda(dao, escenarioInicial = true)
    asegurarAdministradoresSucursal(dao, calcularHash)
    asegurarGerencia(dao, calcularHash)
    asegurarAbastecimiento(dao)
    asegurarComercioInicial(dao, preciosDao)
    // Reposición interna pendiente, visible para Jefatura de tienda y Jefatura de bodega.
    val jefeTiendaAlameda = dao.usuarioPorCorreo("jefe.tienda.alameda@boxview.cl")?.id
    val jefeTiendaProvidencia = dao.usuarioPorCorreo("jefe.tienda.providencia@boxview.cl")?.id
    if (jefeTiendaAlameda != null) dao.guardarSolicitud(SolicitudReposicion(sucursalId = 1,
        bodegaId = 1, productoId = 8, cantidad = 8, solicitadoPor = jefeTiendaAlameda))
    if (jefeTiendaProvidencia != null) dao.guardarSolicitud(SolicitudReposicion(sucursalId = 2,
        bodegaId = 2, productoId = 7, cantidad = 10, solicitadoPor = jefeTiendaProvidencia))
    dao.guardarEntrega(EntregaAutorizada(1, 1, 4, 12))
    dao.guardarEntrega(EntregaAutorizada(2, 2, 5, 8))
    dao.guardarCaja(Caja(1, "CAJ-ING-001", "INGRESO", "Proveedor Textiles Andes", 0, 1,
        "Área de abastecimiento", 1, "EN_TRANSITO"))
    dao.guardarDetalleCaja(DetalleCaja(cajaId = 1, productoId = 1, cantidadEsperada = 12))
    dao.guardarDetalleCaja(DetalleCaja(cajaId = 1, productoId = 2, cantidadEsperada = 8))
    dao.guardarDetalleCaja(DetalleCaja(cajaId = 1, productoId = 4, cantidadEsperada = 5))
    dao.guardarCaja(Caja(2, "CAJ-ING-002", "INGRESO", "Proveedor Vestuario Sur", 0, 2,
        "Área de abastecimiento", operadorProvidencia, "EN_TRANSITO"))
    dao.guardarDetalleCaja(DetalleCaja(cajaId = 2, productoId = 5, cantidadEsperada = 10))
    dao.guardarDetalleCaja(DetalleCaja(cajaId = 2, productoId = 9, cantidadEsperada = 6))
    dao.guardarCaja(Caja(3, "CAJ-DSP-101", "SALIDA", "Bodega Alameda", 1, 0,
        "Jefe de Bodega Alameda", 1, "ORDEN_ASIGNADA", sucursalDestinoId = 1))
    dao.guardarDetalleCaja(DetalleCaja(cajaId = 3, productoId = 3, cantidadEsperada = 4))
    dao.guardarDetalleCaja(DetalleCaja(cajaId = 3, productoId = 6, cantidadEsperada = 3))
    dao.guardarCaja(Caja(4, "CAJ-SAL-001", "SALIDA", "Bodega Alameda", 1, 0,
        "Jefe de Bodega Alameda", 1, "ORDEN_ASIGNADA", sucursalDestinoId = 1))
    dao.guardarDetalleCaja(DetalleCaja(cajaId = 4, productoId = 1, cantidadEsperada = 5))
    dao.guardarDetalleCaja(DetalleCaja(cajaId = 4, productoId = 4, cantidadEsperada = 4))
    dao.guardarCaja(Caja(5, "CAJ-SAL-002", "SALIDA", "Bodega Providencia", 2, 0,
        "Jefe de Bodega Providencia", operadorProvidencia, "ORDEN_ASIGNADA", sucursalDestinoId = 2))
    dao.guardarDetalleCaja(DetalleCaja(cajaId = 5, productoId = 2, cantidadEsperada = 3))
    dao.guardarDetalleCaja(DetalleCaja(cajaId = 5, productoId = 5, cantidadEsperada = 2))
    crearSolicitudesDemo(dao, externo)
    crearDespachosEnTransitoIniciales(dao)
    crearSalidasDemo(dao, preciosDao, operaciones)
    crearRecepcionesFinalizadasDemo(dao, externo)
    crearMovimientosInternosDemo(dao)
    crearInventariosCerradosDemo(dao, operaciones, informes)
}

suspend fun cargarDatosIniciales(dao: InventarioDao, preciosDao: PreciosVentaDao,
                               externo: AbastecimientoExternoDao,
                               operaciones: OperacionesTiendaDao,
                               informes: InformesGerenciaDao,
                               calcularHash: (String, ByteArray) -> String) {
    if (dao.numeroUsuarios() != 0) {
        asegurarJefeDemo(dao, calcularHash)
        asegurarOperadorProvidencia(dao, calcularHash)
        asegurarTrabajadoresTienda(dao, calcularHash)
        asegurarJefesTienda(dao, calcularHash)
        asegurarAdministradoresSucursal(dao, calcularHash)
        asegurarGerencia(dao, calcularHash)
        asegurarAbastecimiento(dao)
        dao.limpiarBodegasExtraJefes()
        // Una v28 anterior pudo haber recuperado SOLO las cuentas tras fallar el sembrado.
        // Se reintenta el catálogo únicamente si la base es una instalación demo vacía;
        // nunca se añaden ventas ni compras de muestra a una base con operaciones reales.
        if (entornoDemoSoloAcceso(dao, externo, operaciones)) {
            crearEscenarioOperativoDemo(dao, preciosDao, externo, operaciones, informes, calcularHash)
            return
        }
        crearEstantesPorSeccion(dao)
        asegurarUbicacionesTienda(dao)
        asegurarComercioInicial(dao, preciosDao)
        actualizarCajasQrDePruebaInstaladas(dao)
        return
    }
    val sal = ByteArray(16).also { SecureRandom().nextBytes(it) }
    dao.guardarUsuario(Usuario(1, "Camila Soto", "bodega@boxview.cl", calcularHash("12345", sal),
        Base64.encodeToString(sal, Base64.NO_WRAP), "OPERADOR_BODEGA"))
    dao.guardarSucursal(Sucursal(1, "BoxView Alameda"))
    dao.guardarSucursal(Sucursal(2, "BoxView Providencia"))
    dao.guardarBodega(Bodega(1, 1, "Bodega Alameda"))
    dao.guardarBodega(Bodega(2, 2, "Bodega Providencia"))
    dao.guardarPermiso(UsuarioBodega(1, 1)) // cuenta inicial de Alameda
    asegurarOperadorProvidencia(dao, calcularHash)
    crearEscenarioOperativoDemo(dao, preciosDao, externo, operaciones, informes, calcularHash)
}

// HISTÓRICOS EN ROOM: compras recibidas que incrementan físicamente el stock de recepción.
// Son distintas de las dos entregas programadas que el operador aún puede revisar.
private suspend fun crearRecepcionesFinalizadasDemo(dao: InventarioDao,
                                                     externo: AbastecimientoExternoDao) {
    val gerente = dao.usuarioPorCorreo("gerencia@boxview.cl")?.id ?: error("Falta gerencia")
    val ejemplos = listOf(
        Triple(1L, 3L, 7),    // Alameda: polerones, sin discrepancias
        Triple(2L, 7L, 5)     // Providencia: chalecos, faltante de 2 respecto a los 7 solicitados
    )
    for ((sid, pid, recibido) in ejemplos) {
        val jefe = dao.usuarioPorCorreo(if (sid == 1L) "jefe.bodega@boxview.cl"
            else "jefe.providencia@boxview.cl")?.id ?: error("Falta jefe de bodega")
        val admin = dao.usuarioPorCorreo(if (sid == 1L) "admin.alameda@boxview.cl"
            else "admin.providencia@boxview.cl")?.id ?: error("Falta administración")
        val operador = dao.usuarioPorCorreo(if (sid == 1L) "bodega@boxview.cl"
            else "bodega.providencia@boxview.cl")?.id ?: error("Falta operador")
        val esperado = 7
        val cajaId = 6L + sid
        val proveedor = if (sid == 1L) "Textiles Andes" else "Vestuario Sur"
        val estado = if (recibido == esperado) "RECIBIDA" else "CON_DIFERENCIA"
        val codigo = "CAJ-ING-HIST-00$sid"
        val solicitudId = externo.nuevaSolicitud(SolicitudExterna(sucursalId = sid,
            bodegaId = sid, solicitanteId = jefe, motivo = "Abastecimiento recibido previamente",
            estado = estado, revisadoPor = admin, decididoPor = gerente,
            entregaCajaId = cajaId, proveedorCotizado = proveedor))
        externo.nuevaLinea(LineaSolicitudExterna(solicitudId = solicitudId, productoId = pid,
            cantidad = esperado, precioUnitarioCotizado = if (sid == 1L) 9600L else 8600L))
        externo.nuevoEvento(EventoSolicitudExterna(solicitudId = solicitudId, usuarioId = jefe,
            accion = "CREADA", detalle = "Solicitud histórica de abastecimiento"))
        externo.nuevoEvento(EventoSolicitudExterna(solicitudId = solicitudId, usuarioId = admin,
            accion = "ENVIADA_GERENCIA", detalle = "Cotización revisada por Administración"))
        externo.nuevoEvento(EventoSolicitudExterna(solicitudId = solicitudId, usuarioId = gerente,
            accion = "APROBADA", detalle = "Monto de compra aprobado"))
        externo.nuevoEvento(EventoSolicitudExterna(solicitudId = solicitudId, usuarioId = admin,
            accion = "ENTREGA_PROGRAMADA", detalle = "Ingreso asignado al operador"))
        externo.nuevoEvento(EventoSolicitudExterna(solicitudId = solicitudId, usuarioId = operador,
            accion = estado, detalle = "Recepción física confirmada: $recibido de $esperado"))
        dao.guardarCaja(Caja(cajaId, codigo, "INGRESO", "Proveedor $proveedor", 0,
            sid, "Administración", operador, estado))
        dao.guardarDetalleCaja(DetalleCaja(cajaId = cajaId, productoId = pid,
            cantidadEsperada = esperado, cantidadVerificada = recibido,
            cantidadRecibida = recibido, posicionDestinoId = -sid,
            cantidadContadaFisica = recibido))
        val existente = dao.existencia(pid, -sid)
        if (existente == null) dao.guardarExistencia(Existencia(productoId = pid,
            posicionId = -sid, cantidad = recibido))
        else dao.sumar(existente.id, recibido)
        dao.guardarMovimiento(Movimiento(usuarioId = operador, bodegaId = sid,
            productoId = pid, posicionOrigen = 0, posicionDestino = -sid,
            cantidad = recibido, tipo = "INGRESO_CAJA"))
        dao.guardarEventoCaja(EventoCaja(cajaId = cajaId, usuarioId = operador,
            accion = "RECEPCION_CONFIRMADA", observacion = "Aceptadas $recibido unidades en recepción"))
        if (recibido != esperado) dao.guardarIncidenciaCaja(IncidenciaCaja(
            cajaId = cajaId, productoId = pid, esperado = esperado, contado = recibido))
    }
}

// Un traslado interno por bodega, sin crear ni destruir stock.
private suspend fun crearMovimientosInternosDemo(dao: InventarioDao) {
    for (bid in listOf(1L, 2L)) {
        val posiciones = posicionesPara(bid, "Poleras")
        val origen = dao.existencia(1L, posiciones[0]) ?: error("Falta polera origen")
        val destino = dao.existencia(1L, posiciones[1]) ?: error("Falta polera destino")
        val operador = dao.usuarioPorCorreo(if (bid == 1L) "bodega@boxview.cl"
            else "bodega.providencia@boxview.cl")?.id ?: error("Falta operador")
        require(dao.restarSiAlcanza(origen.id, 2) == 1) { "Stock demo insuficiente" }
        dao.sumar(destino.id, 2)
        dao.guardarMovimiento(Movimiento(usuarioId = operador, bodegaId = bid,
            productoId = 1L, posicionOrigen = posiciones[0], posicionDestino = posiciones[1],
            cantidad = 2, tipo = "TRASLADO"))
    }
}

// Conteos HISTÓRICOS cerrados, no campañas activas: muestran informes sin inmovilizar estantes.
// Las fotografías se capturan después de los movimientos de la demostración.
private suspend fun crearInventariosCerradosDemo(dao: InventarioDao,
                                                 operaciones: OperacionesTiendaDao,
                                                 informes: InformesGerenciaDao) {
    val ahora = System.currentTimeMillis()
    val antes = ahora - 28L * 24 * 60 * 60 * 1000
    for (sid in listOf(1L, 2L)) {
        val jefeBodega = dao.usuarioPorCorreo(if (sid == 1L) "jefe.bodega@boxview.cl"
            else "jefe.providencia@boxview.cl")?.id ?: error("Sin jefatura")
        val operador = dao.usuarioPorCorreo(if (sid == 1L) "bodega@boxview.cl"
            else "bodega.providencia@boxview.cl")?.id ?: error("Sin operador")
        val jefeTienda = dao.usuarioPorCorreo(if (sid == 1L) "jefe.tienda.alameda@boxview.cl"
            else "jefe.tienda.providencia@boxview.cl")?.id ?: error("Sin jefatura tienda")
        val trabajador = dao.usuarioPorCorreo(if (sid == 1L) "tienda.alameda@boxview.cl"
            else "tienda.providencia@boxview.cl")?.id ?: error("Sin trabajador tienda")
        val ib = 50L + sid
        val periodo = 60L + sid
        val pos = posicionesPara(sid, "Poleras").first()
        val fisico = dao.existencia(1L, pos)?.cantidad ?: error("Sin stock de poleras")
        dao.guardarInventarioBodega(InventarioBodega(id = ib, bodegaId = sid,
            nombre = "Conteo histórico de poleras $sid", estado = "CERRADO",
            fechaApertura = antes, revisadoPor = jefeBodega))
        dao.guardarPeriodoInventario(PeriodoInventario(id = periodo, bodegaId = sid,
            asignadoA = operador, nombre = "Conteo histórico de poleras", estado = "CERRADO",
            fechaApertura = antes, inventarioBodegaId = ib,
            verificacionFisicaPor = operador, fechaVerificacionFisica = antes))
        dao.guardarZonaInventario(ZonaInventario(ib, pos, operador, periodo))
        dao.guardarReferenciaInventario(ReferenciaInventario(ib, 1L, pos, fisico))
        dao.guardarLineaInventario(LineaInventario(periodoId = periodo, productoId = 1L,
            posicionId = pos, cantidadContada = fisico, stockAlEntregar = fisico))
        val informeId = informes.guardarInforme(InformeInventario(inventarioId = ib,
            sucursalId = sid, bodegaId = sid,
            sucursalNombre = if (sid == 1L) "BoxView Alameda" else "BoxView Providencia",
            bodegaNombre = if (sid == 1L) "Bodega Alameda" else "Bodega Providencia",
            nombre = "Conteo histórico de poleras", estadoCierre = "CERRADO",
            fechaApertura = antes, fechaCierre = antes + 3_600_000L,
            cerradoPor = jefeBodega, posiciones = 1, productosContados = 1,
            productosSinContar = 0, lineasConDiferencia = 0, unidadesSistema = fisico,
            unidadesContadas = fisico, ajustesAplicados = 0))
        informes.guardarDetalleInforme(DetalleInformeInventario(informeId = informeId,
            productoId = 1L, codigo = "ROP-001", producto = "Polera básica blanca",
            posicion = "Pasillo 1 · Estante B-1", cantidadSistema = fisico,
            cantidadContada = fisico, ajusteAplicado = false))
        val pTienda = dao.posicionesTiendaActuales(sid).firstOrNull { it.codigo == "2-1" }
            ?: error("Falta estante de tienda")
        val tFisico = dao.stockUbicacionTienda(sid, 1L, pTienda.id)?.cantidad
            ?: error("Falta stock tienda")
        val it = operaciones.abrirInventario(InventarioTienda(sucursalId = sid,
            nombre = "Conteo histórico sala de ventas", estado = "CERRADO",
            fechaApertura = antes, cerradoPor = jefeTienda,
            fechaCierre = antes + 3_600_000L, motivoCierre = "Conteo de control anterior"))
        operaciones.agregarLineaInventario(LineaInventarioTienda(inventarioId = it,
            productoId = 1L, posicionId = pTienda.id, stockInicio = tFisico,
            cantidadContada = tFisico, contadoPor = trabajador,
            fechaConteo = antes + 1_800_000L, codigoProductoCierre = "ROP-001",
            nombreProductoCierre = "Polera básica blanca", rutaUbicacionCierre = pTienda.nombre))
        operaciones.agregarZonaTienda(ZonaConteoTienda(inventarioId = it,
            posicionId = pTienda.id, asignadoA = trabajador,
            confirmadoPor = trabajador, fechaConfirmacion = antes + 3_000_000L))
    }
}

// operador asignado exclusivamente a Providencia en los datos de prueba
private suspend fun asegurarOperadorProvidencia(dao: InventarioDao,
                                              calcularHash: (String, ByteArray) -> String) {
    val correo = "bodega.providencia@boxview.cl"
    if (dao.usuarioPorCorreo(correo) != null || dao.existeBodega(2L) == 0) return
    val sal = ByteArray(16).also { SecureRandom().nextBytes(it) }
    val id = dao.siguienteIdUsuario()
    dao.guardarUsuario(Usuario(id, "Felipe Vargas", correo, calcularHash("12345", sal),
        Base64.encodeToString(sal, Base64.NO_WRAP), "OPERADOR_BODEGA"))
    dao.guardarPermiso(UsuarioBodega(id, 2L))
}

// crear cuentas si faltan
private suspend fun asegurarJefeDemo(dao: InventarioDao, calcularHash: (String, ByteArray) -> String) {
    crearJefeSiFalta(dao, calcularHash, "Daniel Rojas", "jefe.bodega@boxview.cl", 1L)
    crearJefeSiFalta(dao, calcularHash, "Natalia Torres", "jefe.providencia@boxview.cl", 2L)
}

private suspend fun crearJefeSiFalta(dao: InventarioDao, calcularHash: (String, ByteArray) -> String,
                                    nombre: String, correo: String, bodegaId: Long) {
    if (dao.usuarioPorCorreo(correo) != null || dao.existeBodega(bodegaId) == 0) return
    val sal = ByteArray(16).also { SecureRandom().nextBytes(it) }
    val id = dao.siguienteIdUsuario()
    dao.guardarUsuario(Usuario(id, nombre, correo, calcularHash("12345", sal),
        Base64.encodeToString(sal, Base64.NO_WRAP), "JEFE_BODEGA"))
    dao.guardarPermiso(UsuarioBodega(id, bodegaId))
}

// relacion entre bodegas y tiendas
private suspend fun asegurarAbastecimiento(dao: InventarioDao) {
    for (bodegaId in listOf(1L, 2L)) {
        if (dao.existeBodega(bodegaId) == 0) continue
        if (dao.existeSucursal(bodegaId) == 1)
            dao.guardarAbastecimiento(BodegaSucursal(bodegaId, bodegaId))
    }
}

// usuarios iniciales de tienda
private suspend fun asegurarTrabajadoresTienda(dao: InventarioDao, calcularHash: (String, ByteArray) -> String) {
    crearTrabajadorTiendaSiFalta(dao, calcularHash, "Valentina Muñoz", "tienda.alameda@boxview.cl", 1L)
    crearTrabajadorTiendaSiFalta(dao, calcularHash, "Tomás Herrera", "tienda.providencia@boxview.cl", 2L)
}

private suspend fun crearTrabajadorTiendaSiFalta(dao: InventarioDao, calcularHash: (String, ByteArray) -> String,
                                               nombre: String, correo: String, sucursalId: Long) {
    if (dao.usuarioPorCorreo(correo) != null || dao.existeSucursal(sucursalId) == 0) return
    val sal = ByteArray(16).also { SecureRandom().nextBytes(it) }
    val id = dao.siguienteIdUsuario()
    dao.guardarUsuario(Usuario(id, nombre, correo, calcularHash("12345", sal),
        Base64.encodeToString(sal, Base64.NO_WRAP), "TRABAJADOR_TIENDA"))
    dao.asignarUsuarioTienda(UsuarioSucursal(id, sucursalId))
}


// Distribución física inicial de tienda: sala de ventas, reserva y recepción separadas.
// En bases antiguas con saldo sin ubicación, ese remanente se coloca en RECEPCIÓN,
// no se atribuye silenciosamente a un estante ya contabilizado.
suspend fun asegurarUbicacionesTienda(dao: InventarioDao, soloSucursalId: Long? = null,
                                     escenarioInicial: Boolean = false) {
    data class Ubicacion(val codigo: String, val zona: String, val categoria: String,
                         val pasillo: String?, val nombre: String)
    val definiciones = listOf(
        Ubicacion("REC", "RECEPCION", "General", null, "Zona de recepción"),
        Ubicacion("1-1", "VENTAS", "Pantalones", "Pasillo 1", "Pasillo 1 · Estante A-1 · Pantalones"),
        Ubicacion("1-2", "ALMACEN", "Pantalones", "Reserva de tienda", "Reserva · Estante A-2 · Pantalones"),
        Ubicacion("2-1", "VENTAS", "Poleras", "Pasillo 1", "Pasillo 1 · Estante B-1 · Poleras"),
        Ubicacion("2-2", "ALMACEN", "Poleras", "Reserva de tienda", "Reserva · Estante B-2 · Poleras"),
        Ubicacion("3-1", "VENTAS", "Polerones", "Pasillo 2", "Pasillo 2 · Estante C-1 · Polerones"),
        Ubicacion("3-2", "ALMACEN", "Polerones", "Reserva de tienda", "Reserva · Estante C-2 · Polerones"),
        Ubicacion("4-1", "VENTAS", "Chaquetas", "Pasillo 2", "Pasillo 2 · Estante D-1 · Chaquetas"),
        Ubicacion("5-1", "VENTAS", "Camisas", "Pasillo 3", "Pasillo 3 · Estante E-1 · Camisas"),
        Ubicacion("6-1", "VENTAS", "Chalecos", "Pasillo 3", "Pasillo 3 · Estante F-1 · Chalecos"),
        Ubicacion("7-1", "VENTAS", "Faldas", "Pasillo 4", "Pasillo 4 · Estante G-1 · Faldas"),
        Ubicacion("8-1", "VENTAS", "Vestidos", "Pasillo 4", "Pasillo 4 · Estante H-1 · Vestidos")
    )
    val sucursales = dao.sucursalesActuales().filter { soloSucursalId == null || it.id == soloSucursalId }
    for (sucursal in sucursales) {
        val sid = sucursal.id
        val antes = dao.posicionesTiendaActuales(sid)
        val crearNuevas = antes.isEmpty() || (antes.size == 1 && antes[0].codigo == "REC")
        val demoAntiguo = !crearNuevas && dao.historicosInventarioTienda(sid) == 0 &&
            definiciones.drop(1).all { def ->
                antes.any { it.codigo == def.codigo && it.nombre == def.codigo && it.tipo == "ESTANTERIA" }
            }
        if (crearNuevas || demoAntiguo) {
            // Cada zona tiene su sección padre, sin mezclar VENTAS y ALMACEN.
            for ((zona, nombre, codigo) in listOf(
                Triple("VENTAS", "Pasillo 1", "PV-1"),
                Triple("VENTAS", "Pasillo 2", "PV-2"),
                Triple("VENTAS", "Pasillo 3", "PV-3"),
                Triple("VENTAS", "Pasillo 4", "PV-4"),
                Triple("ALMACEN", "Reserva de tienda", "ALM-1")
            )) dao.guardarPosicionTienda(PosicionTienda(sucursalId = sid, codigo = codigo,
                zona = zona, categoria = "General", nombre = nombre, tipo = "SECCION"))
            val actuales = dao.posicionesTiendaActuales(sid)
            for (def in definiciones) {
                val padre = actuales.firstOrNull { it.tipo == "SECCION" && it.nombre == def.pasillo && it.zona == def.zona }
                if (actuales.any { it.codigo == def.codigo }) {
                    if (demoAntiguo && padre != null) dao.mejorarEstanteDemoTienda(sid,
                        def.codigo, def.nombre, padre.id)
                    continue
                }
                dao.guardarPosicionTienda(PosicionTienda(sucursalId = sid,
                    codigo = def.codigo, zona = def.zona, categoria = def.categoria,
                    nombre = def.nombre, tipo = if (def.zona == "RECEPCION") "RECEPCION" else "ESTANTERIA",
                    padreId = padre?.id))
            }
        }
        if (dao.posicionesTiendaActuales(sid).none { it.codigo == "REC" })
            dao.guardarPosicionTienda(PosicionTienda(sucursalId = sid, codigo = "REC",
                zona = "RECEPCION", categoria = "General", nombre = "Zona de recepción", tipo = "RECEPCION"))
        val todas = dao.posicionesTiendaActuales(sid)
        val recepcion = todas.firstOrNull { it.codigo == "REC" } ?: continue
        dao.stockTiendaActual(sid).forEach { item ->
            if (item.cantidad > 0 && dao.totalUbicadoTienda(sid, item.productoId) == 0) {
                val producto = dao.productoPorId(item.productoId)
                val ventas = todas.firstOrNull { it.categoria == producto?.categoria &&
                    it.zona == "VENTAS" && it.tipo != "SECCION" } ?: recepcion
                val reserva = todas.firstOrNull { it.categoria == producto?.categoria &&
                    it.zona == "ALMACEN" && it.tipo != "SECCION" }
                // Al migrar existencias históricas sin ubicación no inventamos estantes.
                val recepcionDemo = if ((crearNuevas || escenarioInicial) && item.productoId in listOf(1L, 2L, 10L) &&
                    item.cantidad >= 8) 2 else 0
                val unidadesReserva = if ((crearNuevas || escenarioInicial) && reserva != null && item.cantidad >= 3)
                    maxOf(1, item.cantidad / 3) else 0
                val unidadesVentas = if ((crearNuevas || escenarioInicial) && ventas != recepcion)
                    item.cantidad - recepcionDemo - unidadesReserva else 0
                if (unidadesVentas > 0) dao.guardarStockUbicacionTienda(ExistenciaUbicacionTienda(
                    sucursalId = sid, productoId = item.productoId, posicionId = ventas.id,
                    cantidad = unidadesVentas))
                if (unidadesReserva > 0 && reserva != null) dao.guardarStockUbicacionTienda(
                    ExistenciaUbicacionTienda(sucursalId = sid, productoId = item.productoId,
                        posicionId = reserva.id, cantidad = unidadesReserva))
                val unidadesRecepcion = item.cantidad - unidadesVentas - unidadesReserva
                if (unidadesRecepcion > 0) dao.guardarStockUbicacionTienda(
                    ExistenciaUbicacionTienda(sucursalId = sid, productoId = item.productoId,
                        posicionId = recepcion.id, cantidad = unidadesRecepcion))
            }
        }
    }
}

// jefaturas de tienda iniciales
private suspend fun asegurarJefesTienda(dao: InventarioDao, calcularHash: (String, ByteArray) -> String) {
    val responsables = listOf(
        Triple("Fernanda Pérez", "jefe.tienda.alameda@boxview.cl", 1L),
        Triple("Diego Morales", "jefe.tienda.providencia@boxview.cl", 2L)
    )
    for ((nombre, correo, sid) in responsables) {
        if (dao.existeSucursal(sid) == 0) continue
        val anterior = dao.usuarioPorCorreo(correo)
        if (anterior != null) {
            if (anterior.activo && anterior.rol == "JEFE_TIENDA" && dao.numeroTiendasUsuario(anterior.id) == 0)
                dao.asignarUsuarioTienda(UsuarioSucursal(anterior.id, sid))
            continue
        }
        val sal = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val id = dao.siguienteIdUsuario()
        dao.guardarUsuario(Usuario(id, nombre, correo, calcularHash("12345", sal),
            Base64.encodeToString(sal, Base64.NO_WRAP), "JEFE_TIENDA"))
        dao.asignarUsuarioTienda(UsuarioSucursal(id, sid))
    }
}

// administradores iniciales de cada sucursal
private suspend fun asegurarAdministradoresSucursal(dao: InventarioDao, hash: (String, ByteArray) -> String) {
    val datos = listOf(Triple(1L, "Administración Alameda", "admin.alameda@boxview.cl"),
        Triple(2L, "Administración Providencia", "admin.providencia@boxview.cl"))
    datos.forEach { (sid, nombre, correo) ->
        if (dao.existeSucursal(sid) == 0) return@forEach
        if (dao.usuarioPorCorreo(correo) == null) {
            val sal = ByteArray(16).also { SecureRandom().nextBytes(it) }
            val id = dao.siguienteIdUsuario()
            dao.guardarUsuario(Usuario(id, nombre, correo, hash("12345", sal),
                Base64.encodeToString(sal, Base64.NO_WRAP), "ADMIN_SUCURSAL"))
            dao.asignarUsuarioTienda(UsuarioSucursal(id, sid))
        }
        val bodega = dao.sucursalesActuales().firstOrNull { it.id == sid }?.let { _ -> sid }
        dao.crearConfiguracionSucursal(ConfiguracionSucursal(sid,
            bodega?.takeIf { dao.abasteceSucursal(it, sid) == 1 }))
    }
}

// cuenta inicial de gerencia
internal suspend fun asegurarGerencia(dao: InventarioDao, hash: (String, ByteArray) -> String) {
    val correo = "gerencia@boxview.cl"
    if (dao.usuarioPorCorreo(correo) != null) return
    val sal = ByteArray(16).also { SecureRandom().nextBytes(it) }
    dao.guardarUsuario(Usuario(dao.siguienteIdUsuario(), "Gerencia BoxView", correo,
        hash("12345", sal), Base64.encodeToString(sal, Base64.NO_WRAP), "GERENTE"))
}
