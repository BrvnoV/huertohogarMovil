
package com.huertohogar.huertohogarmovil.ui.screens


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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
// import androidx.lifecycle.viewmodel.compose.viewModel
// import com.huertohogar.profile.ProfileViewModel
// import com.huertohogar.profile.ProfileState
// import com.huertohogar.profile.ProfileEvent

data class ProfileState(
    val userName: String = "",
    val userEmail: String = "",
    val isLoading: Boolean = false
)

@Composable
fun ProfileRoute(
    onNavigateToLogin: () -> Unit,
    // viewModel: ProfileViewModel = viewModel()
) {
    // val uiState by viewModel.uiState.collectAsState()

    // Estado de ejemplo para el prototipo
    val uiState = ProfileState(
        userName = "Nombre Apellido",
        userEmail = "usuario@ejemplo.com"
    )

    ProfileScreen(
        state = uiState,
        onLogoutClick = {
            // viewModel.onEvent(ProfileEvent.Logout)
            // Después del logout, navegamos a login
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            Text("Mi Perfil", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(32.dp))

            ProfileInfoRow(icon = Icons.Default.Person, text = state.userName)
            Spacer(modifier = Modifier.height(16.dp))
            ProfileInfoRow(icon = Icons.Default.Email, text = state.userEmail)

            Spacer(modifier = Modifier.weight(1f)) // Empuja el botón al fondo

            Button(
                onClick = onLogoutClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar Sesión")
            }
        }
    }
}

@Composable
fun ProfileInfoRow(
    icon: ImageVector,
    text: String,
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
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}