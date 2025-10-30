package com.huertohogar.huertohogarmovil.ui.screens

import com.huertohogar.huertohogarmovil.ui.components.HuertoTextField
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp


@Composable
fun LoginRoute(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    // viewModel: LoginViewModel = viewModel()
) {
    // const val uiState by viewModel.uiState.collectAsState()

    // Estado de ejemplo para el prototipo
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val isLoading = false
    val error: String? = null

    LoginScreen(
        email = email,
        password = password,
        isLoading = isLoading,
        error = error,
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onLoginClick = {
            // viewModel.onEvent(LoginEvent.OnLoginClick(email, password))
            // Simulación de éxito:
            onLoginSuccess()
        },
        onRegisterClick = onNavigateToRegister
    )
}

@Composable
fun LoginScreen(
    email: String,
    password: String,
    isLoading: Boolean,
    error: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bienvenido a Huerto Hogar",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(32.dp))

        // --- ADAPTADO ---
        HuertoTextField(
            value = email,
            onValueChange = onEmailChange,
            label = "Correo Electrónico",
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
            modifier = Modifier.fillMaxWidth()
        )
        // --- FIN ADAPTADO ---

        Spacer(modifier = Modifier.height(16.dp))

        // --- ADAPTADO ---
        HuertoTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = "Contraseña",
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            onImeAction = onLoginClick, // Ejecuta el login al presionar 'Done'
            modifier = Modifier.fillMaxWidth()
        )
        // --- FIN ADAPTADO ---

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar Sesión")
            }
        }

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onRegisterClick) {
            Text("¿No tienes cuenta? Regístrate aquí")
        }
    }
}