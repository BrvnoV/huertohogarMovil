package com.huertohogar.huertohogarmovil.ui.screens

import com.huertohogar.huertohogarmovil.ui.components.HuertoTextField
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp


@Composable
fun RegisterRoute(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    // viewModel: RegisterViewModel = viewModel()
) {
    // val uiState by viewModel.uiState.collectAsState()

    // Estado de ejemplo para el prototipo
    var nombre by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    val isLoading = false
    val error: String? = null

    RegisterScreen(
        nombre = nombre,
        email = email,
        password = password,
        confirmPassword = confirmPassword,
        isLoading = isLoading,
        error = error,
        onNombreChange = { nombre = it },
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onConfirmPasswordChange = { confirmPassword = it },
        onRegisterClick = {
            // viewModel.onEvent(RegisterEvent.OnRegisterClick(nombre, email, password, confirmPassword))
            // Simulación de éxito:
            onRegisterSuccess()
        },
        onLoginClick = onNavigateToLogin
    )
}

@Composable
fun RegisterScreen(
    nombre: String,
    email: String,
    password: String,
    confirmPassword: String,
    isLoading: Boolean,
    error: String?,
    onNombreChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Crea tu Cuenta",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(32.dp))

        // --- ADAPTADO ---
        HuertoTextField(
            value = nombre,
            onValueChange = onNombreChange,
            label = "Nombre Completo",
            imeAction = ImeAction.Next,
            modifier = Modifier.fillMaxWidth()
        )
        // --- FIN ADAPTADO ---

        Spacer(modifier = Modifier.height(16.dp))

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
            imeAction = ImeAction.Next,
            modifier = Modifier.fillMaxWidth()
        )
        // --- FIN ADAPTADO ---

        Spacer(modifier = Modifier.height(16.dp))

        // --- ADAPTADO ---
        HuertoTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = "Confirmar Contraseña",
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            onImeAction = onRegisterClick,
            modifier = Modifier.fillMaxWidth()
        )
        // --- FIN ADAPTADO ---

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = onRegisterClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse")
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
        TextButton(onClick = onLoginClick) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}