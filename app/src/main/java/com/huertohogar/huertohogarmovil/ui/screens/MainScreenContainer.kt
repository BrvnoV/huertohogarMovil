package com.huertohogar.huertohogarmovil.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.huertohogar.huertohogarmovil.ui.navigation.BottomNavigationItem

/**
 * Este es el contenedor principal de la app después del login.
 * Alberga la navegación inferior (BottomBar) y las pantallas principales.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContainer(userId: String) {
    // Este es el NavController para la navegación INFERIOR (Home, Products, etc.)
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                // Itera sobre los items definidos en BottomNavItems.kt
                BottomNavigationItem.items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.route == screen.route,
                        onClick = {
                            bottomNavController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Evita múltiples copias del mismo destino
                                launchSingleTop = true
                                // Restaura el estado al re-seleccionar
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // Este NavHost es para el contenido de las pantallas de la barra inferior
        NavHost(
            navController = bottomNavController,
            // La ruta inicial de la barra inferior
            startDestination = BottomNavigationItem.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Aplica el padding del Scaffold
        ) {
            // Rutas para cada item de la barra inferior
            // Pasamos el userId a las pantallas que lo necesiten (ej. Perfil)
            composable(BottomNavigationItem.Home.route) {
                HomeScreen()
            }
            composable(BottomNavigationItem.Products.route) {
                ProductsScreen()
            }
            composable(BottomNavigationItem.Cart.route) {
                CartScreen()
            }
            composable(BottomNavigationItem.Profile.route) {
                // La pantalla de perfil sí necesita saber qué usuario es
                ProfileScreen(userId = userId)
            }
        }
    }
}

