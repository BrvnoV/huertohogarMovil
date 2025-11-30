package com.huertohogar.huertohogarmovil.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
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
import com.huertohogar.huertohogarmovil.HuertohogarMovilApp
import com.huertohogar.huertohogarmovil.R
import com.huertohogar.huertohogarmovil.ui.viewModelFactory
import com.huertohogar.huertohogarmovil.ui.viewmodel.ProfileEvent
import com.huertohogar.huertohogarmovil.ui.viewmodel.ProfileState
import com.huertohogar.huertohogarmovil.ui.viewmodel.ProfileViewModel
import com.huertohogar.huertohogarmovil.ui.viewmodel.ViewModelFactory

@Composable
fun ProfileRoute(
    onNavigateToLogin: () -> Unit,
    onNavigateToMap: () -> Unit, // Recibe la acción de navegación al mapa
    factory: ViewModelFactory = viewModelFactory(),
    viewModel: ProfileViewModel = viewModel(factory = factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    ProfileScreen(
        state = uiState,
        onLogoutClick = {
            viewModel.onEvent(ProfileEvent.OnLogoutClick)
            onNavigateToLogin() // Ejecuta la navegación fuera del grafo principal
        },
        onNavigateToMap = onNavigateToMap // Pasa la acción al composable de la UI
    )
}

@Composable
fun ProfileScreen(
    state: ProfileState,
    onLogoutClick: () -> Unit,
    onNavigateToMap: () -> Unit, // Recibe la acción de navegación al mapa
    modifier: Modifier = Modifier
) {
    // --- FONDO DE LA PANTALLA ---
    Box(modifier = modifier.fillMaxSize()) {
        // 1. Imagen de fondo
        Image(
            painter = painterResource(id = R.drawable.login_background),
            contentDescription = "Fondo de la aplicación",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Capa de superficie semi-transparente
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
        ) {}
        // --- FIN DE FONDO ---

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
                    color = MaterialTheme.colorScheme.onSurface
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
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        ProfileInfoRow(
                            icon = Icons.Default.Email,
                            text = state.userEmail,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // --- BOTÓN PARA EL MAPA ---
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onNavigateToMap, // Llama a la navegación al mapa
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Mapa",
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Ver mi ubicación en el mapa")
                }
                // --- FIN DE BOTÓN ---

                Spacer(modifier = Modifier.weight(1f)) // Empuja el botón de logout al fondo

                Button(
                    onClick = onLogoutClick,
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
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, style = MaterialTheme.typography.bodyLarge, color = color)
    }
}
