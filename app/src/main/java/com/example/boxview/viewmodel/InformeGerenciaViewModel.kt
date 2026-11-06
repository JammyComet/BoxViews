package com.example.boxview.viewmodel

// Lógica: Consulta y revisa informes para gerencia.
import com.example.boxview.mensajeErrorUsuario
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.example.boxview.modelo.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class InformeGerenciaViewModel(app: Application) : AndroidViewModel(app) {
    // Conexion con la capa de datos.
    private val db = AppDatabase.getDatabase(app)
    private val dao = db.informesGerenciaDao()

    // Informes observados desde Room.
    fun informes(): Flow<List<InformeInventario>> = dao.informes()
    fun campaniasFinalizadas() = db.campaniasMensualesDao().finalizadas()
    private fun resumirDetallesMensuales(detalles: List<DetalleCampania>): List<ResumenProductoInventario> =
        resumirProductosInventario(detalles.map { d ->
            FilaResumenInventario(d.codigo, d.producto, d.cantidadSistema, d.cantidadContada)
        })

    suspend fun datosCampaniasParaExportar(gerente: Usuario): Pair<List<CampaniaMensual>, Map<Long, List<ResumenProductoInventario>>> {
        require(dao.gerenteValido(gerente.id) == 1) { "Solo gerencia puede exportar" }
        val campanias = db.campaniasMensualesDao()
        val finalizadas = campanias.todasCampanias().filter { it.estado == "FINALIZADA" }
        val ids = finalizadas.map { it.id }.toSet()
        val resumen = campanias.todosDetalles().filter { it.campaniaId in ids }
            .groupBy { it.campaniaId }.mapValues { (_, detalle) -> resumirDetallesMensuales(detalle) }
        return finalizadas to resumen
    }

    fun informesConsolidadosNuevos() = db.campaniasMensualesDao().pendientesGerencia()
    fun resumenCampania(id: Long): Flow<List<ResumenProductoInventario>> =
        db.campaniasMensualesDao().observarDetalles(id).map { resumirDetallesMensuales(it) }

    fun revisarCampania(gerente: Usuario, id: Long, respuesta: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                // Operacion agrupada en Room.
                db.withTransaction {
                    require(dao.gerenteValido(gerente.id) == 1) { "Sin permiso de gerencia" }
                    require(db.campaniasMensualesDao().revisar(id, gerente.id, System.currentTimeMillis()) == 1) {
                        "El informe no está finalizado o ya se revisó"
                    }
                }
                respuesta(true, "Informe mensual revisado")
            } catch (e: Exception) { respuesta(false, mensajeErrorUsuario(e, "Error al revisar el informe")) }
        }
    }
    // Los reportes de stock ahora los revisa la Administración de la sucursal, no Gerencia.
    fun revisarAdmin(admin: Usuario, sucursalId: Long, tipo: String, informeId: Long,
                     respuesta: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                db.withTransaction {
                    require(admin.rol == "ADMIN_SUCURSAL" &&
                        db.abastecimientoExternoDao().adminAutorizadoExterno(admin.id, sucursalId) == 1) {
                        "Sucursal no autorizada"
                    }
                    val revisado = when (tipo) {
                        "MENSUAL" -> {
                            val c = db.campaniasMensualesDao().buscarId(informeId)
                                ?: error("Campaña no encontrada")
                            require(c.sucursalId == sucursalId && c.estado == "FINALIZADA") { "Fuera de sucursal" }
                            db.campaniasMensualesDao().revisar(informeId, admin.id, System.currentTimeMillis())
                        }
                        "BODEGA" -> {
                            require(dao.sucursalInformeBodega(informeId) == sucursalId) { "Fuera de sucursal" }
                            dao.marcarRevisado(informeId, admin.id, System.currentTimeMillis())
                        }
                        "TIENDA" -> {
                            require(dao.sucursalInventarioTienda(informeId) == sucursalId) { "Fuera de sucursal" }
                            dao.revisarTienda(informeId, admin.id, System.currentTimeMillis())
                        }
                        else -> error("Tipo de informe desconocido")
                    }
                    require(revisado == 1) { "El informe ya está revisado o no corresponde" }
                }
                respuesta(true, "Informe revisado")
            } catch (e: Exception) { respuesta(false, mensajeErrorUsuario(e, "No se pudo revisar")) }
        }
    }

    fun nuevos(): Flow<Int> = dao.nuevos()
    fun detalles(id: Long): Flow<List<DetalleInformeInventario>> = dao.detalles(id)
    fun informesTienda(): Flow<List<InventarioTienda>> = dao.informesTienda()
    fun nuevosTienda(): Flow<Int> = dao.nuevosTienda()
    fun detalleTienda(id: Long): Flow<List<LineaInventarioTienda>> = dao.detalleTienda(id)

    fun revisarTienda(gerente: Usuario, id: Long, respuesta: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                db.withTransaction {
                    require(dao.gerenteValido(gerente.id) == 1) { "Sin permiso de gerencia" }
                    require(dao.revisarTienda(id, gerente.id, System.currentTimeMillis()) == 1) {
                        "Informe no disponible o ya revisado"
                    }
                }
                respuesta(true, "Informe de tienda revisado")
            } catch (e: Exception) { respuesta(false, mensajeErrorUsuario(e, "No se pudo revisar el informe")) }
        }
    }

    fun marcarRevisado(gerente: Usuario, id: Long, respuesta: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val texto = db.withTransaction {
                    require(dao.gerenteValido(gerente.id) == 1) { "Solo gerencia puede revisar informes" }
                    require(dao.marcarRevisado(id, gerente.id, System.currentTimeMillis()) == 1) {
                        "El informe ya fue revisado o no existe"
                    }
                    "Informe marcado como revisado"
                }
                respuesta(true, texto)
            } catch (e: Exception) {
                respuesta(false, mensajeErrorUsuario(e, "No se pudo revisar el informe"))
            }
        }
    }

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = InformeGerenciaViewModel(app) as T
    }
}
