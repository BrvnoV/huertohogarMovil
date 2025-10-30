package com.huertohogar.huertohogarmovil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.huertohogar.huertohogarmovil.data.model.Usuario
import com.huertohogar.huertohogarmovil.ui.components.ErrorText
import com.huertohogar.huertohogarmovil.ui.components.LoadingIndicator
import com.huertohogar.huertohogarmovil.ui.components.PasswordTextField
import com.huertohogar.huertohogarmovil.ui.components.StandardTextField
import com.huertohogar.huertohogarmovil.ui.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    // Acción para navegar a la pantalla de registro
    onRegisterClick: () -> Unit,
    // Acción que se ejecuta cuando el login es exitoso
    onLoginSuccess: (Usuario) -> Unit,
    // Instancia del ViewModel
    viewModel: LoginViewModel = viewModel()
) {
    // Observamos el estado del usuario logueado
    val loggedInUser = viewModel.loggedInUser

    // Si el usuario logueado cambia (de null a un Usuario),
    // ejecutamos la acción de login exitoso.
    LaunchedEffect(loggedInUser) {
        loggedInUser?.let {
            onLoginSuccess(it)
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp), // Padding lateral
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Huerto Hogar",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Campo de Usuario
            StandardTextField(
                value = viewModel.username,
                onValueChange = {
                    viewModel.username = it
                    viewModel.clearError()
                },
                label = "Usuario (huerto)",
                isError = viewModel.loginError != null,
                keyboardType = KeyboardType.Text
            )

            // Campo de Contraseña
            PasswordTextField(
                value = viewModel.password,
                onValueChange = {
                    viewModel.password = it
                    viewModel.clearError()
                },
                label = "Contraseña (123123)",
                isError = viewModel.loginError != null
            )

            // Espaciador
            Spacer(modifier = Modifier.height(16.dp))

            // Mostramos el indicador de carga o el botón de login
            if (viewModel.isLoading) {
                LoadingIndicator()
            } else {
                // Mostramos el mensaje de error si existe
                viewModel.loginError?.let {
                    ErrorText(error = it)
                }

                Button(
                    onClick = { viewModel.login() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Iniciar Sesión")
                }
            }

            // Botón para ir a Registro
            TextButton(
                onClick = onRegisterClick,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = "¿No tienes cuenta? Regístrate aquí",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
