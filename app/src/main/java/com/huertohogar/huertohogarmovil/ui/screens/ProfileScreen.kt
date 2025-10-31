package com.huertohogar.huertohogarmovil.screens.profile

// --- ¡IMPORTS AÑADIDOS! ---
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.huertohogar.huertohogarmovil.HuertoHogarApp
import com.huertohogar.huertohogarmovil.R
import com.huertohogar.huertohogarmovil.ui.viewmodel.ProfileEvent
import com.huertohogar.huertohogarmovil.ui.viewmodel.ProfileState
import com.huertohogar.huertohogarmovil.ui.viewmodel.ProfileViewModel
import com.huertohogar.huertohogarmovil.ui.viewmodel.ViewModelFactory
// --- FIN DE IMPORTS ---

@Composable
fun ProfileRoute(
    onNavigateToLogin: () -> Unit,
    factory: ViewModelFactory = viewModelFactory(),
    viewModel: ProfileViewModel = viewModel(factory = factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    ProfileScreen(
        state = uiState,
        onLogoutClick = {
            // Notificamos al ViewModel que el usuario quiere salir
            viewModel.onEvent(ProfileEvent.OnLogoutClick)
            // Ejecutamos la navegación (que se pasa desde AppNavigationGraph)
            onNavigateToLogin()
        }
    )
}

@Composable
fun ProfileScreen(
    state: ProfileState,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // --- ¡AQUÍ ESTÁ EL CAMBIO PARA EL FONDO! ---
    Box(modifier = modifier.fillMaxSize()) {
        // 1. Imagen de fondo
        Image(
            painter = painterResource(id = R.drawable.profile_background), // O R.drawable.app_background
            contentDescription = "Fondo de la aplicación",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Capa de superficie semi-transparente
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
        ) {}
        // --- FIN DEL FONDO ---

        // 3. Contenido de la pantalla
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Text(
                    "Mi Perfil",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface // Asegura texto visible
                )
                Spacer(modifier = Modifier.height(32.dp))

                // Card semi-transparente para la información
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        ProfileInfoRow(
                            icon = Icons.Default.Person,
                            text = state.userName,
                            color = MaterialTheme.colorScheme.onSurface // Asegura texto visible
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        ProfileInfoRow(
                            icon = Icons.Default.Email,
                            text = state.userEmail,
                            color = MaterialTheme.colorScheme.onSurface // Asegura texto visible
                        )
                    }
                }

                // (Opcional) Puedes añadir más cards aquí para "Direcciones", "Pedidos", etc.

                Spacer(modifier = Modifier.weight(1f)) // Empuja el botón al fondo

                Button(
                    onClick = onLogoutClick,
                    // Usamos el color 'tertiary' (Terracotta) para el logout
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar Sesión")
                }
            }
        }
    }
}

@Composable
fun ProfileInfoRow(
    icon: ImageVector,
    text: String,
    color: Color, // Para pasar el color del texto
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary, // Icono en verde
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, style = MaterialTheme.typography.bodyLarge, color = color)
    }
}

// --- FUNCIÓN AYUDANTE (HELPER) ---
@Composable
fun viewModelFactory(): ViewModelFactory {
    val application = (LocalContext.current.applicationContext as HuertoHogarApp)
    return ViewModelFactory(application.repository, application.sessionManager)
}