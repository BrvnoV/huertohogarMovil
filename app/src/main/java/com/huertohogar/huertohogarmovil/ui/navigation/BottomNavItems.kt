package com.huertohogar.huertohogarmovil.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

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