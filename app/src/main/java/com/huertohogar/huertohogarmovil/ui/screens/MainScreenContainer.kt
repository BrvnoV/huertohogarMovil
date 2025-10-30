package com.huertohogar.huertohogarmovil.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.huertohogar.huertohogarmovil.ui.navigation.MainNavigationGraph
import com.huertohogar.huertohogarmovil.ui.navigation.bottomNavItems

// --- Contenedor de la Pantalla Principal (con Scaffold y Bottom Bar) ---
@Composable
fun MainScreenContainer(
    onLogout: () -> Unit // ADAPTACIÓN: Acepta el evento de logout
) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        // Y se lo pasa al grafo interno
        MainNavigationGraph(
            navController = navController,
            padding = innerPadding,
            onLogout = onLogout // ADAPTACIÓN: Pasa el evento
        )
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
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}