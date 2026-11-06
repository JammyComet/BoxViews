package com.example.boxview

// Inicio de BoxView: configura los ViewModel y las rutas NavHost.
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.boxview.modelo.Destinos
import com.example.boxview.ui.components.MarcoOperador
import com.example.boxview.ui.components.MarcoTienda
import com.example.boxview.ui.components.MarcoJefe
import com.example.boxview.ui.components.MarcoJefeTienda
import com.example.boxview.ui.components.MarcoAdministrador
import com.example.boxview.ui.components.MarcoGerencia
import com.example.boxview.ui.theme.TemaBoxView
import com.example.boxview.ui.view.*
import com.example.boxview.viewmodel.OperadorViewModel
import com.example.boxview.viewmodel.JefeBodegaViewModel
import com.example.boxview.viewmodel.TrabajadorTiendaViewModel
import com.example.boxview.viewmodel.JefeTiendaViewModel
import com.example.boxview.viewmodel.AdministradorViewModel
import com.example.boxview.viewmodel.AbastecimientoExternoViewModel
import com.example.boxview.viewmodel.InformeGerenciaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TemaBoxView {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    val vm: OperadorViewModel = viewModel(factory = OperadorViewModel.Factory(application))
                    val vmJefe: JefeBodegaViewModel = viewModel(factory = JefeBodegaViewModel.Factory(application))
                    val vmTienda: TrabajadorTiendaViewModel = viewModel(factory = TrabajadorTiendaViewModel.Factory(application))
                    val vmJefeTienda: JefeTiendaViewModel = viewModel(factory = JefeTiendaViewModel.Factory(application))
                    val vmAdmin: AdministradorViewModel = viewModel(factory = AdministradorViewModel.Factory(application))
                    val vmExterno: AbastecimientoExternoViewModel = viewModel(factory = AbastecimientoExternoViewModel.Factory(application))
                    val vmInforme: InformeGerenciaViewModel = viewModel(factory = InformeGerenciaViewModel.Factory(application))
                    val ir: (String) -> Unit = { ruta ->
                        navController.navigate(ruta) { launchSingleTop = true }
                    }
                    val cerrar: () -> Unit = {
                        vm.cerrarSesion()
                        vmJefe.cerrarSesion()
                        vmTienda.cerrarSesion()
                        vmJefeTienda.cerrarSesion()
                        vmAdmin.cerrarSesion()
                        navController.navigate(Destinos.Login.ruta) {
                            popUpTo(navController.graph.id) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                    NavHost(navController = navController, startDestination = Destinos.Splash.ruta) {
                        composable(Destinos.Splash.ruta) {
                            PantallaBienvenida {
                                navController.navigate(Destinos.Login.ruta) {
                                    popUpTo(Destinos.Splash.ruta) { inclusive = true }
                                }
                            }
                        }
                        composable(Destinos.Login.ruta) {
                            PantallaLoginOperador(vm) {
                                vmJefe.cerrarSesion()
                                vmTienda.cerrarSesion()
                                vmJefeTienda.cerrarSesion()
                                vmAdmin.cerrarSesion()
                                navController.navigate(Destinos.Seleccion.ruta) {
                                    popUpTo(Destinos.Login.ruta) { inclusive = true }
                                }
                            }
                        }
                        composable(Destinos.Seleccion.ruta) {
                            if (vm.sesion == null) AccesoNoDisponible(cerrar)
                            else if (vm.sesion?.rol == "ADMIN_SUCURSAL") {
                                PantallaElegirSucursalAdmin(vm, vmAdmin) {
                                    navController.navigate(Destinos.AdminInicio.ruta) {
                                        popUpTo(Destinos.Seleccion.ruta) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            } else if (vm.sesion?.rol == "GERENTE") {
                                LaunchedEffect(vm.sesion?.id) {
                                    navController.navigate(Destinos.GerenciaInicio.ruta) {
                                        popUpTo(Destinos.Seleccion.ruta) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                                CircularProgressIndicator()
                            } else if (vm.sesion?.rol in listOf("TRABAJADOR_TIENDA", "JEFE_TIENDA")) {
                                PantallaElegirTienda(vm, vmTienda) {
                                    navController.navigate(if (vm.sesion?.rol == "JEFE_TIENDA") Destinos.JefeTiendaInicio.ruta else Destinos.TiendaInicio.ruta) {
                                        popUpTo(Destinos.Seleccion.ruta) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            } else if (vm.sesion?.rol in listOf("OPERADOR_BODEGA", "JEFE_BODEGA")) PantallaElegirBodega(vm) {
                                navController.navigate(if (vm.sesion?.rol == "JEFE_BODEGA") Destinos.JefeInicio.ruta else Destinos.Inicio.ruta) {
                                    popUpTo(Destinos.Seleccion.ruta) { inclusive = true }
                                    launchSingleTop = true
                                }
                            } else AccesoNoDisponible(cerrar)
                        }
                        composable(Destinos.Inicio.ruta) {
                            MarcoOperador(vm, Destinos.Inicio.ruta, ir, cerrar) { PantallaInicioOperador(vm, ir) }
                        }
                        composable(Destinos.Stock.ruta) {
                            MarcoOperador(vm, Destinos.Stock.ruta, ir, cerrar) { PantallaStockOperador(vm, ir) }
                        }
                        composable(Destinos.Movimientos.ruta) {
                            MarcoOperador(vm, Destinos.Movimientos.ruta, ir, cerrar) { PantallaTrasladoOperador(vm) }
                        }
                        composable(Destinos.Operaciones.ruta) {
                            MarcoOperador(vm, Destinos.Operaciones.ruta, ir, cerrar) { PantallaOperacionesOperador(vm, ir) }
                        }
                        composable(Destinos.Ingresos.ruta) {
                            MarcoOperador(vm, Destinos.Ingresos.ruta, ir, cerrar) { PantallaIngresosAutorizados(vm, ir) }
                        }
                        composable(Destinos.Preparacion.ruta) {
                            MarcoOperador(vm, Destinos.Preparacion.ruta, ir, cerrar) { PantallaPrepararDespacho(vm, ir) }
                        }
                        composable(Destinos.DetalleCaja.ruta) {
                            MarcoOperador(vm, Destinos.DetalleCaja.ruta, ir, cerrar) {
                                PantallaDetalleCaja(vm, alDespachar = {
                                    navController.navigate(Destinos.Preparacion.ruta) {
                                        popUpTo(Destinos.DetalleCaja.ruta) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                })
                            }
                        }
                        composable(Destinos.Inventario.ruta) {
                            MarcoOperador(vm, Destinos.Inventario.ruta, ir, cerrar) { PantallaInventarioMensual(vm) }
                        }
                        composable(Destinos.Historial.ruta) {
                            MarcoOperador(vm, Destinos.Historial.ruta, ir, cerrar) { PantallaHistorialOperador(vm) }
                        }
                        composable(Destinos.Escaner.ruta) {
                            MarcoOperador(vm, Destinos.Escaner.ruta, ir, cerrar) { PantallaEscanerOperador(vm, ir) }
                        }
                        composable(Destinos.Perfil.ruta) {
                            MarcoOperador(vm, Destinos.Perfil.ruta, ir, cerrar) { PantallaPerfilOperador(vm, ir, cerrar) }
                        }
                        composable(Destinos.TiendaInicio.ruta) {
                            MarcoTienda(vm, vmTienda, Destinos.TiendaInicio.ruta, ir, cerrar) { PantallaInicioTienda(vm, vmTienda, ir) }
                        }
                        composable(Destinos.TiendaRecepciones.ruta) {
                            MarcoTienda(vm, vmTienda, Destinos.TiendaRecepciones.ruta, ir, cerrar) { PantallaRecepcionesTienda(vm, vmTienda, ir) }
                        }
                        composable(Destinos.TiendaDetalle.ruta) {
                            MarcoTienda(vm, vmTienda, Destinos.TiendaDetalle.ruta, ir, cerrar) {
                                PantallaDetalleRecepcionTienda(vm, vmTienda, alTerminar = {
                                    vmTienda.salirDetalle()
                                    navController.navigate(Destinos.TiendaRecepciones.ruta) {
                                        popUpTo(Destinos.TiendaDetalle.ruta) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                })
                            }
                        }
                        composable(Destinos.TiendaStock.ruta) {
                            MarcoTienda(vm, vmTienda, Destinos.TiendaStock.ruta, ir, cerrar) { PantallaStockTienda(vm, vmTienda, ir) }
                        }
                        composable(Destinos.TiendaTraslados.ruta) {
                            MarcoTienda(vm, vmTienda, Destinos.TiendaTraslados.ruta, ir, cerrar) {
                                PantallaTrasladosTienda(vm, vmTienda)
                            }
                        }
                        composable(Destinos.TiendaSalidas.ruta) {
                            MarcoTienda(vm, vmTienda, Destinos.TiendaSalidas.ruta, ir, cerrar) {
                                PantallaSalidasTienda(vm, vmTienda)
                            }
                        }
                        composable(Destinos.TiendaInventarios.ruta) {
                            if (vm.sesion?.rol == "JEFE_TIENDA") {
                                MarcoJefeTienda(vm, vmTienda, Destinos.TiendaInventarios.ruta, ir, cerrar) {
                                    PantallaInventariosTienda(vm, vmTienda)
                                }
                            } else {
                                MarcoTienda(vm, vmTienda, Destinos.TiendaInventarios.ruta, ir, cerrar) {
                                    PantallaInventariosTienda(vm, vmTienda)
                                }
                            }
                        }
                        composable(Destinos.TiendaHistorial.ruta) {
                            MarcoTienda(vm, vmTienda, Destinos.TiendaHistorial.ruta, ir, cerrar) { PantallaHistorialTienda(vm, vmTienda) }
                        }
                        composable(Destinos.TiendaPerfil.ruta) {
                            MarcoTienda(vm, vmTienda, Destinos.TiendaPerfil.ruta, ir, cerrar) { PantallaPerfilTienda(vm, vmTienda, ir, cerrar) }
                        }
                        composable(Destinos.JefeTiendaInicio.ruta) {
                            MarcoJefeTienda(vm, vmTienda, Destinos.JefeTiendaInicio.ruta, ir, cerrar) {
                                PantallaInicioJefeTienda(vm, vmTienda, vmJefeTienda, ir)
                            }
                        }
                        composable(Destinos.JefeTiendaStock.ruta) {
                            MarcoJefeTienda(vm, vmTienda, Destinos.JefeTiendaStock.ruta, ir, cerrar) {
                                PantallaStockJefeTienda(vm, vmTienda)
                            }
                        }
                        composable(Destinos.JefeTiendaReposicion.ruta) {
                            MarcoJefeTienda(vm, vmTienda, Destinos.JefeTiendaReposicion.ruta, ir, cerrar) {
                                PantallaReposicionJefeTienda(vm, vmTienda, vmJefeTienda)
                            }
                        }
                        composable(Destinos.JefeTiendaEquipo.ruta) {
                            MarcoJefeTienda(vm, vmTienda, Destinos.JefeTiendaEquipo.ruta, ir, cerrar) {
                                PantallaEquipoJefeTienda(vm, vmTienda, vmJefeTienda)
                            }
                        }
                        composable(Destinos.JefeTiendaIncidencias.ruta) {
                            MarcoJefeTienda(vm, vmTienda, Destinos.JefeTiendaIncidencias.ruta, ir, cerrar) {
                                PantallaIncidenciasJefeTienda(vm, vmTienda, vmJefeTienda)
                            }
                        }
                        composable(Destinos.JefeTiendaUbicaciones.ruta) {
                            MarcoJefeTienda(vm, vmTienda, Destinos.JefeTiendaUbicaciones.ruta, ir, cerrar) {
                                PantallaUbicacionesJefeTienda(vm, vmTienda, vmJefeTienda)
                            }
                        }
                        composable(Destinos.JefeTiendaHistorial.ruta) {
                            MarcoJefeTienda(vm, vmTienda, Destinos.JefeTiendaHistorial.ruta, ir, cerrar) {
                                PantallaHistorialJefeTienda(vm, vmTienda)
                            }
                        }
                        composable(Destinos.AdminInicio.ruta) {
                            MarcoAdministrador(vm, vmAdmin, Destinos.AdminInicio.ruta, ir, cerrar) {
                                PantallaInicioAdmin(vm, vmAdmin, ir)
                            }
                        }
                        composable(Destinos.AdminBodegas.ruta) {
                            MarcoAdministrador(vm, vmAdmin, Destinos.AdminBodegas.ruta, ir, cerrar) {
                                PantallaBodegasAdmin(vm, vmAdmin)
                            }
                        }
                        composable(Destinos.AdminPersonal.ruta) {
                            MarcoAdministrador(vm, vmAdmin, Destinos.AdminPersonal.ruta, ir, cerrar) {
                                PantallaPersonalAdmin(vm, vmAdmin)
                            }
                        }
                        composable(Destinos.AdminMinimos.ruta) {
                            MarcoAdministrador(vm, vmAdmin, Destinos.AdminMinimos.ruta, ir, cerrar) {
                                PantallaMinimosAdmin(vm, vmAdmin)
                            }
                        }
                        composable(Destinos.AdminAbastecimiento.ruta) {
                            MarcoAdministrador(vm, vmAdmin, Destinos.AdminAbastecimiento.ruta, ir, cerrar) {
                                PantallaAbastecimientoAdmin(vm, vmAdmin, vmExterno)
                            }
                        }
                        composable(Destinos.AdminPrecios.ruta) {
                            MarcoAdministrador(vm, vmAdmin, Destinos.AdminPrecios.ruta, ir, cerrar) {
                                PantallaPreciosVentaAdmin(vm, vmAdmin)
                            }
                        }
                        composable(Destinos.AdminReportes.ruta) {
                            MarcoAdministrador(vm, vmAdmin, Destinos.AdminReportes.ruta, ir, cerrar) {
                                PantallaInformesOperativosAdmin(vm, vmAdmin, vmExterno, vmInforme)
                            }
                        }
                        composable(Destinos.GerenciaInicio.ruta) {
                            MarcoGerencia(vm, vmExterno, Destinos.GerenciaInicio.ruta, ir, cerrar) {
                                PantallaInicioFinancieroGerencia(vm, vmExterno, ir)
                            }
                        }
                        composable(Destinos.GerenciaSolicitudes.ruta) {
                            MarcoGerencia(vm, vmExterno, Destinos.GerenciaSolicitudes.ruta, ir, cerrar) {
                                PantallaSolicitudesGerencia(vm, vmExterno)
                            }
                        }
                        composable(Destinos.GerenciaReportes.ruta) {
                            MarcoGerencia(vm, vmExterno, Destinos.GerenciaReportes.ruta, ir, cerrar) {
                                PantallaFinanzasGerencia(vm, vmExterno)
                            }
                        }
                        composable(Destinos.AdminHistorial.ruta) {
                            MarcoAdministrador(vm, vmAdmin, Destinos.AdminHistorial.ruta, ir, cerrar) {
                                PantallaHistorialAdmin(vmAdmin)
                            }
                        }
                        composable(Destinos.JefeAbastecimiento.ruta) {
                            MarcoJefe(vm, Destinos.JefeAbastecimiento.ruta, ir, cerrar) {
                                PantallaSolicitudesExternasJefe(vm, vmExterno)
                            }
                        }
                        composable(Destinos.JefeInicio.ruta) {
                            MarcoJefe(vm, Destinos.JefeInicio.ruta, ir, cerrar) { PantallaInicioJefe(vm, vmJefe, ir) }
                        }
                        composable(Destinos.JefeOrdenes.ruta) {
                            MarcoJefe(vm, Destinos.JefeOrdenes.ruta, ir, cerrar) { PantallaOrdenesJefe(vm, vmJefe) }
                        }
                        composable(Destinos.JefeInventarios.ruta) {
                            MarcoJefe(vm, Destinos.JefeInventarios.ruta, ir, cerrar) { PantallaInventariosJefe(vm, vmJefe) }
                        }
                        composable(Destinos.JefeIncidencias.ruta) {
                            MarcoJefe(vm, Destinos.JefeIncidencias.ruta, ir, cerrar) { PantallaIncidenciasJefe(vm, vmJefe) }
                        }
                        composable(Destinos.JefeStock.ruta) {
                            MarcoJefe(vm, Destinos.JefeStock.ruta, ir, cerrar) { PantallaStockJefe(vm) }
                        }
                        composable(Destinos.JefeUbicaciones.ruta) {
                            MarcoJefe(vm, Destinos.JefeUbicaciones.ruta, ir, cerrar) {
                                PantallaUbicacionesJefeBodega(vm, vmJefe)
                            }
                        }
                        composable(Destinos.JefeEquipo.ruta) {
                            MarcoJefe(vm, Destinos.JefeEquipo.ruta, ir, cerrar) { PantallaEquipoJefe(vm, vmJefe) }
                        }
                    }
                }
            }
        }
    }
}
