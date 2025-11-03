package com.huertohogar.huertohogarmovil.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.huertohogar.huertohogarmovil.screens.cart.CartRoute
import com.huertohogar.huertohogarmovil.screens.home.HomeRoute
import com.huertohogar.huertohogarmovil.screens.login.LoginRoute
import com.huertohogar.huertohogarmovil.screens.map.MapRoute // Importante
import com.huertohogar.huertohogarmovil.screens.products.ProductsRoute
import com.huertohogar.huertohogarmovil.screens.profile.ProfileRoute
import com.huertohogar.huertohogarmovil.screens.register.RegisterRoute
import com.huertohogar.huertohogarmovil.ui.screens.MainScreenContainer

/**
 * Grafo de Navegación Principal de la App (el de más alto nivel)
 */
@Composable
fun AppNavigationGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Graph.AUTHENTICATION
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Grafo de Autenticación (Login y Register)
        navigation(
            route = Graph.AUTHENTICATION,
            startDestination = AuthScreen.Login.route
        ) {
            composable(AuthScreen.Login.route) {
                LoginRoute(
                    onLoginSuccess = {
                        navController.navigate(Graph.MAIN) {
                            popUpTo(Graph.AUTHENTICATION) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(AuthScreen.Register.route)
                    }
                )
            }
            composable(AuthScreen.Register.route) {
                RegisterRoute(
                    onRegisterSuccess = {
                        navController.navigate(AuthScreen.Login.route) {
                            popUpTo(AuthScreen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // Grafo Principal de la App (con Bottom Bar)
        composable(route = Graph.MAIN) {
            MainScreenContainer(
                onLogout = {
                    navController.navigate(Graph.AUTHENTICATION) {
                        popUpTo(Graph.MAIN) { inclusive = true }
                    }
                }
            )
        }
    }
}

/**
 * Grafo de navegación para las pantallas INTERNAS (Home, Products, etc.)
 */
@Composable
fun MainNavigationGraph(
    navController: NavHostController,
    padding: PaddingValues,
    onLogout: () -> Unit // Parámetro 1: Cerrar Sesión
    // --- ¡AQUÍ ES DONDE FALTABA EL PARÁMETRO! ---
) {
    // Definimos la nueva ruta para el mapa
    val mapRoute = "map_screen"

    NavHost(
        navController = navController,
        startDestination = MainScreen.Home.route,
        modifier = Modifier.padding(padding)
    ) {
        composable(MainScreen.Home.route) {
            HomeRoute(
                onNavigateToProductDetails = { /* ... */ }
            )
        }
        composable(MainScreen.Products.route) {
            ProductsRoute(
                onNavigateToProductDetails = { /* ... */ }
            )
        }
        composable(MainScreen.Cart.route) {
            CartRoute(
                onNavigateToCheckout = {
                    navController.navigate(MainScreen.Home.route) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                    }
                }
            )
        }
        composable(MainScreen.Profile.route) {
            ProfileRoute(
                onNavigateToLogin = onLogout,
                // Parámetro 2: Pasamos la acción para navegar al mapa
                onNavigateToMap = { navController.navigate(mapRoute) }
            )
        }

        // Añadimos el composable del mapa
        composable(route = mapRoute) {
            MapRoute(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
