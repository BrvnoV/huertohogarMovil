package com.huertohogar.huertohogarmovil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.huertohogar.huertohogarmovil.ui.navigation.AppNavigationGraph // <-- 1. Importamos el correcto
import com.huertohogar.huertohogarmovil.ui.navigation.Graph // <-- 2. Importamos las rutas
import com.huertohogar.huertohogarmovil.ui.theme.HuertohogarMovilTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Habilita el modo Edge-to-Edge

        setContent {
            HuertohogarMovilTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // --- ADAPTACIÓN ---

                    // 3. Creamos el NavController aquí, en el nivel más alto.
                    //    Este controlador gestionará toda la navegación de la app.
                    val navController = rememberNavController()

                    // 4. Llamamos a nuestro grafo de navegación principal
                    //    y le pasamos el controlador y la ruta inicial.
                    AppNavigationGraph(
                        navController = navController,
                        startDestination = Graph.AUTHENTICATION
                        // O puedes usar Graph.MAIN si quieres saltarte el login para probar
                    )
                }
            }
        }
    }
}