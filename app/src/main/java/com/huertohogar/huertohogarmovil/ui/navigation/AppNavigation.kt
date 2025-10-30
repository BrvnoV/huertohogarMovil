package com.huertohogar.huertohogarmovil.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.huertohogar.huertohogarmovil.data.model.Usuario
import com.huertohogar.huertohogarmovil.ui.screens.LoginScreen
import com.huertohogar.huertohogarmovil.ui.screens.MainScreenContainer
import com.huertohogar.huertohogarmovil.ui.screens.RegisterScreen

// --- Definición de Rutas ---

/**
 * Rutas de navegación para la app.
 */
object AppRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main/{userId}" // Ruta principal, requiere el ID del usuario

    // Función helper para crear la ruta MAIN con el ID
    fun mainRoute(userId: Int): String {
        return "main/$userId"
    }
}

// --- Grafo de Navegación (NavHost) ---

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.LOGIN // La app siempre empieza en el Login
    ) {

        /**
         * Pantalla de Login
         */
        composable(AppRoutes.LOGIN) {
            LoginScreen(
                onRegisterClick = {
                    // Navega a la pantalla de registro
                    navController.navigate(AppRoutes.REGISTER)
                },
                onLoginSuccess = { usuario ->
                    // Navega a la pantalla principal (Main) y limpia
                    // el stack de navegación para que el usuario no pueda "volver" al login.
                    navController.navigate(AppRoutes.mainRoute(usuario.id)) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        /**
         * Pantalla de Registro
         */
        composable(AppRoutes.REGISTER) {
            RegisterScreen(
                onBackToLogin = {
                    // Vuelve a la pantalla de login
                    navController.popBackStack()
                }
            )
        }

        /**
         * Pantalla Principal (Contenedor)
         * Esta pantalla recibirá el ID del usuario como argumento.
         * (La crearemos en el siguiente paso)
         */
        composable(AppRoutes.MAIN) { backStackEntry ->
            // Extraemos el ID del usuario de la ruta
            val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull()

            if (userId != null) {
                // Pasamos el ID del usuario a la pantalla principal
                MainScreenContainer(userId = userId)
            } else {
                // Fallback: Si el ID es nulo, volver al login
                navController.popBackStack(AppRoutes.LOGIN, inclusive = false)
            }
        }
    }
}
