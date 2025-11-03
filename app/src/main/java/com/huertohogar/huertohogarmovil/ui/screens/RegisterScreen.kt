package com.huertohogar.huertohogarmovil.screens.register

import android.widget.Toast
import androidx.compose.foundation.Image // <-- Importado
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.huertohogar.huertohogarmovil.HuertoHogarApp
import com.huertohogar.huertohogarmovil.R // <-- Importado
import com.huertohogar.huertohogarmovil.ui.components.HuertoTextField
import com.huertohogar.huertohogarmovil.ui.viewModelFactory
import com.huertohogar.huertohogarmovil.ui.viewmodel.RegisterEvent
import com.huertohogar.huertohogarmovil.ui.viewmodel.RegisterState
import com.huertohogar.huertohogarmovil.ui.viewmodel.RegisterViewModel
import com.huertohogar.huertohogarmovil.ui.viewmodel.ViewModelFactory

/**
 * Composable "inteligente" (stateful).
 * Conecta el ViewModel con la UI.
 */
@Composable
fun RegisterRoute(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    factory: ViewModelFactory = viewModelFactory(),
    viewModel: RegisterViewModel = viewModel(factory = factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Escucha por el evento de éxito de registro
    LaunchedEffect(uiState.registerSuccess) {
        if (uiState.registerSuccess) {
            Toast.makeText(context, "¡Registro exitoso! Inicia sesión.", Toast.LENGTH_LONG).show()
            onRegisterSuccess()
        }
    }

    RegisterScreen(
        state = uiState,
        onEvent = viewModel::onEvent, // Pasamos la función onEvent a la UI
        onNavigateToLogin = onNavigateToLogin
    )
}

/**
 * Composable "tonto" (stateless).
 * Solo muestra el estado y envía eventos.
 */
@Composable
fun RegisterScreen(
    state: RegisterState,
    onEvent: (RegisterEvent) -> Unit, // Recibe el manejador de eventos
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // 1. Fondo
        Image(
            painter = painterResource(id = R.drawable.login_background),
            contentDescription = "Fondo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // 2. Capa Oscura
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black.copy(alpha = 0.6f)
        ) {}

        // 3. Contenido
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()) // Permite scroll si el teclado tapa
                .systemBarsPadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Crea tu Cuenta",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White // Texto visible sobre fondo oscuro
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Card semi-transparente para el formulario
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // --- CAMPO NOMBRE ---
                    HuertoTextField(
                        value = state.nombre,
                        onValueChange = { onEvent(RegisterEvent.OnNombreChange(it)) }, // Envía evento
                        label = "Nombre Completo",
                        imeAction = ImeAction.Next,
                        isError = state.nombreError != null, // Muestra error si no es null
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (state.nombreError != null) {
                        Text(text = state.nombreError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // --- CAMPO EMAIL ---
                    HuertoTextField(
                        value = state.email,
                        onValueChange = { onEvent(RegisterEvent.OnEmailChange(it)) }, // Envía evento
                        label = "Correo Electrónico",
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                        isError = state.emailError != null, // Muestra error si no es null
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (state.emailError != null) {
                        Text(text = state.emailError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // --- CAMPO CONTRASEÑA ---
                    HuertoTextField(
                        value = state.pass,
                        onValueChange = { onEvent(RegisterEvent.OnPasswordChange(it)) }, // Envía evento
                        label = "Contraseña (mín. 6 caracteres)",
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next,
                        isError = state.passError != null, // Muestra error si no es null
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (state.passError != null) {
                        Text(text = state.passError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // --- CAMPO CONFIRMAR CONTRASEÑA ---
                    HuertoTextField(
                        value = state.confirmPass,
                        onValueChange = { onEvent(RegisterEvent.OnConfirmPassChange(it)) }, // Envía evento
                        label = "Confirmar Contraseña",
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                        onImeAction = { onEvent(RegisterEvent.OnRegisterClick) }, // Clic en "Done"
                        isError = state.confirmPassError != null, // Muestra error si no es null
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (state.confirmPassError != null) {
                        Text(text = state.confirmPassError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    // --- BOTÓN DE REGISTRO ---
                    if (state.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else {
                        Button(
                            onClick = { onEvent(RegisterEvent.OnRegisterClick) }, // Envía evento
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Registrarse")
                        }
                    }

                    // Error general (ej: email ya existe)
                    if (state.generalError != null) {
                        Text(
                            text = state.generalError,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onNavigateToLogin) {
                Text("¿Ya tienes cuenta? Inicia sesión", color = Color.White)
            }
        }
    }
}
