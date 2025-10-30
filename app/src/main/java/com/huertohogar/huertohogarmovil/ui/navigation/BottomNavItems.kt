package com.huertohogar.huertohogarmovil.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Clase sellada (sealed class) que define los items de la barra de navegación inferior.
 * Cada objeto representa una pantalla.
 */
sealed class BottomNavigationItem(val route: String, val icon: ImageVector, val title: String) {
    // Define las 4 pantallas principales
    object Home : BottomNavigationItem("home", Icons.Filled.Home, "Inicio")
    object Products : BottomNavigationItem("products", Icons.Filled.List, "Productos")
    object Cart : BottomNavigationItem("cart", Icons.Filled.ShoppingCart, "Carrito")
    object Profile : BottomNavigationItem("profile", Icons.Filled.Person, "Perfil")

    // Lista estática para acceder fácilmente a todos los items en el MainScreenContainer
    companion object {
        val items = listOf(Home, Products, Cart, Profile)
    }
}

