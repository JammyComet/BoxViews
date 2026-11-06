package com.example.boxview.viewmodel

// Lógica: Centraliza comprobaciones de permisos por rol.
import com.example.boxview.modelo.InventarioDao

// La misma regla se aplica desde jefatura y administración.
internal suspend fun comprobarRetiroOperadorBodega(dao: InventarioDao, uid: Long, bid: Long) {
    require(dao.autorizado(uid, bid) == 1) { "El operador no tiene acceso activo a esta bodega" }
    require(dao.bodegasActualesEmpleado(uid).any { it.id != bid && it.activa }) {
        "El operador debe conservar al menos una bodega activa. Asigne otra antes de retirarlo."
    }
    require(dao.ordenesActivasOperador(uid, bid) == 0) {
        "El operador tiene órdenes de salida pendientes"
    }
    require(dao.ingresosPendientesOperador(uid, bid) == 0) {
        "El operador tiene recepciones de proveedor asignadas pendientes"
    }
    require(dao.periodoPendiente(bid, uid) == 0) {
        "El operador tiene tareas de inventario pendientes"
    }
}
