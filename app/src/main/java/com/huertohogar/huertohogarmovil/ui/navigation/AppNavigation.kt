package com.huertohogar.huertohogarmovil.ui.navigation

import com.huertohogar.huertohogarmovil.ui.screens.CartRoute
import com.huertohogar.huertohogarmovil.ui.screens.HomeRoute
import com.huertohogar.huertohogarmovil.ui.screens.LoginRoute
import com.huertohogar.huertohogarmovil.ui.screens.MainScreenContainer
import com.huertohogar.huertohogarmovil.ui.screens.ProductsRoute
import com.huertohogar.huertohogarmovil.ui.screens.ProfileRoute
import com.huertohogar.huertohogarmovil.ui.screens.RegisterRoute
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation


// --- Grafo de Navegación Principal de la App ---
@Composable
fun AppNavigationGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Graph.AUTHENTICATION
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Grafo de Autenticación
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
            // ADAPTACIÓN: Llamamos al MainScreenContainer y le pasamos el
            // evento onLogout para que pueda navegar hacia atrás (al Auth Graph).
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

// --- Grafo de Navegación para las pantallas INTERNAS (Home, Products, etc.) ---
@Composable
fun MainNavigationGraph(
    navController: NavHostController,
    padding: PaddingValues,
    onLogout: () -> Unit // ADAPTACIÓN: Recibe el evento de logout
) {
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
                onNavigateToCheckout = { /* ... */ }
            )
        }
        composable(MainScreen.Profile.route) {
            ProfileRoute(
                // ADAPTACIÓN: Pasamos el evento de logout
                onNavigateToLogin = onLogout
            )
        }
    }
}