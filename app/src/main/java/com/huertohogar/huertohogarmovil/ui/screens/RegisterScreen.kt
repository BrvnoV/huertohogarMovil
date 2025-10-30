package com.huertohogar.huertohogarmovil.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.huertohogar.huertohogarmovil.ui.components.ErrorText
import com.huertohogar.huertohogarmovil.ui.components.LoadingIndicator
import com.huertohogar.huertohogarmovil.ui.components.PasswordTextField
import com.huertohogar.huertohogarmovil.ui.components.StandardTextField
import com.huertohogar.huertohogarmovil.ui.viewmodel.RegisterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    // Acción para volver a la pantalla de login
    onBackToLogin: () -> Unit,
    // Instancia del ViewModel
    viewModel: RegisterViewModel = viewModel()
) {
    val context = LocalContext.current

    // Observamos el estado de registro exitoso
    val registerSuccess = viewModel.registerSuccess

    // Si el registro es exitoso, mostramos un Toast y volvemos al Login
    LaunchedEffect(registerSuccess) {
        if (registerSuccess) {
            Toast.makeText(context, "¡Registro exitoso! Inicia sesión.", Toast.LENGTH_LONG).show()
            onBackToLogin()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Cuenta") },
                navigationIcon = {
                    IconButton(onClick = onBackToLogin) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()), // Para pantallas pequeñas
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Campo de Nombre
            StandardTextField(
                value = viewModel.nombre,
                onValueChange = {
                    viewModel.nombre = it
                    viewModel.clearError()
                },
                label = "Nombre Completo",
                isError = viewModel.registerError != null
            )

            // Campo de Dirección
            StandardTextField(
                value = viewModel.direccion,
                onValueChange = {
                    viewModel.direccion = it
                    viewModel.clearError()
                },
                label = "Dirección",
                isError = viewModel.registerError != null
            )

            // Campo de Usuario
            StandardTextField(
                value = viewModel.username,
                onValueChange = {
                    viewModel.username = it
                    viewModel.clearError()
                },
                label = "Nombre de Usuario",
                isError = viewModel.registerError != null
            )

            // Campo de Contraseña
            PasswordTextField(
                value = viewModel.password,
                onValueChange = {
                    viewModel.password = it
                    viewModel.clearError()
                },
                label = "Contraseña",
                isError = viewModel.registerError != null
            )

            // Campo de Confirmar Contraseña
            PasswordTextField(
                value = viewModel.confirmPassword,
                onValueChange = {
                    viewModel.confirmPassword = it
                    viewModel.clearError()
                },
                label = "Confirmar Contraseña",
                isError = viewModel.registerError != null
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mostramos el indicador de carga o el botón de registro
            if (viewModel.isLoading) {
                LoadingIndicator()
            } else {
                // Mostramos el mensaje de error si existe
                viewModel.registerError?.let {
                    ErrorText(error = it)
                }

                Button(
                    onClick = { viewModel.register() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrarse")
                }
            }
        }
    }
}
