package com.example.boxview.modelo

// Modelo: Reúne los DAO para acceder al inventario.
// acceso a los DAO de Room
// Delegacion a los DAO especializados.
class InventarioDao(
    usuariosDao: UsuariosDao,
    productosDao: ProductosDao,
    cajasDao: CajasDao,
    inventariosDao: InventariosDao,
    tiendasDao: TiendasDao,
    administracionDao: AdministracionDao
) : UsuariosDao by usuariosDao,
    ProductosDao by productosDao,
    CajasDao by cajasDao,
    InventariosDao by inventariosDao,
    TiendasDao by tiendasDao,
    AdministracionDao by administracionDao
