package com.huertohogar.huertohogarmovil.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation


// --- Definición de Rutas de Navegación ---

object Graph {
    const val AUTHENTICATION = "auth_graph"
    const val MAIN = "main_graph"
}

sealed class AuthScreen(val route: String) {
    object Login : AuthScreen("login")
    object Register : AuthScreen("register")
}

sealed class MainScreen(val route: String, val label: String, val icon: ImageVector) {
    object Home : MainScreen("home", "Inicio", Icons.Default.Home)
    object Products : MainScreen("products", "Productos", Icons.Default.List)
    object Cart : MainScreen("cart", "Carrito", Icons.Default.ShoppingCart)
    object Profile : MainScreen("profile", "Perfil", Icons.Default.Person)
}

val bottomNavItems = listOf(
    MainScreen.Home,
    MainScreen.Products,
    MainScreen.Cart,
    MainScreen.Profile
)

// --- Grafo de Navegación Principal de la App ---

@Composable
fun AppNavigationGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Graph.AUTHENTICATION // O Graph.MAIN si el usuario ya está logueado
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
                            // Limpia el backstack de autenticación
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
                            // Vuelve a login después de registrarse
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
            MainScreenContainer()
        }
    }
}


// --- Contenedor de la Pantalla Principal (con Scaffold y Bottom Bar) ---

@Composable
fun MainScreenContainer() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        MainNavigationGraph(
            navController = navController,
            padding = innerPadding
        )
    }
}

// --- Grafo de Navegación para las pantallas INTERNAS (Home, Products, etc.) ---

@Composable
fun MainNavigationGraph(
    navController: NavHostController,
    padding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = MainScreen.Home.route,
        modifier = Modifier.padding(padding)
    ) {
        composable(MainScreen.Home.route) {
            HomeRoute(
                onNavigateToProductDetails = { productId ->
                    // Ejemplo de navegación a detalles (requiere crear la ruta)
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
                    // navController.navigate("checkout")
                }
            )
        }
        composable(MainScreen.Profile.route) {
            ProfileRoute(
                onNavigateToLogin = {
                    // Esto requeriría un NavController de más alto nivel
                    // Por ahora, no hace nada, pero en una app real
                    // notificaría al AppNavigationGraph para volver a Auth.
                }
            )
        }
        // Aquí irían más pantallas como ProductDetailsScreen, CheckoutScreen, etc.
    }
}

// --- Composable para la Barra de Navegación Inferior ---

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        bottomNavItems.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.label) },
                label = { Text(screen.label) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // re-selecting the same item
                        launchSingleTop = true
                        // Restore state when re-selecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}