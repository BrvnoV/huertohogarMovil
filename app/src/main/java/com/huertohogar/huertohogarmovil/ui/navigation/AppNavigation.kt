package com.huertohogar.huertohogarmovil.ui.navigation


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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.huertohogar.huertohogarmovil.screens.cart.CartRoute


/**
 * Grafo de Navegación Principal de la App (el de más alto nivel)
 * Define el flujo entre Autenticación y la App Principal.
 */
@Composable
fun AppNavigationGraph(
    navController: NavHostController,
    startDestination: String
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
                        // Al iniciar sesión, ir al grafo principal
                        navController.navigate(Graph.MAIN) {
                            // Limpiar el backstack para no volver a Login
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
                        // Al registrarse, volver a Login
                        navController.navigate(AuthScreen.Login.route) {
                            popUpTo(AuthScreen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack() // Volver a la pantalla anterior (Login)
                    }
                )
            }
        }

        // Grafo Principal de la App (con Bottom Bar)
        composable(route = Graph.MAIN) {
            // Este es el punto de entrada a la app (post-login).
            // Llama al MainScreenContainer y le pasa el evento onLogout.
            MainScreenContainer(
                onLogout = {
                    // Si el usuario cierra sesión (desde ProfileScreen),
                    // volvemos al grafo de autenticación.
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
 * Este NavHost va *dentro* del Scaffold en MainScreenContainer.
 */
@Composable
fun MainNavigationGraph(
    navController: NavHostController,
    padding: PaddingValues,
    onLogout: () -> Unit // Recibe el evento de logout desde arriba
) {
    NavHost(
        navController = navController,
        startDestination = MainScreen.Home.route,
        modifier = Modifier.padding(padding) // Aplica el padding del Scaffold
    ) {
        composable(MainScreen.Home.route) {
            HomeRoute(
                onNavigateToProductDetails = { productId ->
                    // Aquí puedes añadir navegación a detalles de producto
                    // navController.navigate("productDetails/$productId")
                }
            )
        }
        composable(MainScreen.Products.route) {
            ProductsRoute(
                onNavigateToProductDetails = { productId ->
                    // navController.navigate("productDetails/$productId")
                }
            )
        }
        composable(MainScreen.Cart.route) {
            CartRoute(
                onNavigateToCheckout = {
                    // --- CORRECCIÓN ---
                    // Al "pagar", volvemos a Home y limpiamos la pila
                    // para que el usuario no pueda "volver" al carrito vacío.
                    navController.navigate(MainScreen.Home.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(MainScreen.Profile.route) {
            ProfileRoute(
                // Pasa el evento de logout a la pantalla de Perfil
                onNavigateToLogin = onLogout
            )
        }
    }
}