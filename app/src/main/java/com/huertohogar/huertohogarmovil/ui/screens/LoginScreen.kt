package com.huertohogar.huertohogarmovil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.huertohogar.huertohogarmovil.HuertoHogarApp
import com.huertohogar.huertohogarmovil.ui.components.HuertoTextField
import com.huertohogar.huertohogarmovil.ui.viewModelFactory
import com.huertohogar.huertohogarmovil.ui.viewmodel.LoginEvent
import com.huertohogar.huertohogarmovil.ui.viewmodel.LoginViewModel
import com.huertohogar.huertohogarmovil.ui.viewmodel.ViewModelFactory

@Composable
fun LoginRoute(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    // Conectamos el ViewModel usando nuestra Factory
    factory: ViewModelFactory = viewModelFactory(),
    viewModel: LoginViewModel = viewModel(factory = factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    // Escuchamos por el evento de login exitoso
    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            onLoginSuccess()
        }
    }

    var email by rememberSaveable { mutableStateOf("huerto@hogar.com") } // Pre-cargado
    var password by rememberSaveable { mutableStateOf("123123") } // Pre-cargado

    LoginScreen(
        email = email,
        password = password,
        isLoading = uiState.isLoading,
        error = uiState.error,
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onLoginClick = {
            viewModel.onEvent(LoginEvent.OnLoginClick(email, password))
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
    Box(modifier = modifier.fillMaxSize()) {
        // --- NUEVO: Fondo ---
        AsyncImage(
            model = "https://images.unsplash.com/photo-1593951033320-910a906d40da?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3wzOTU5M3wwfDF8c2VhcmNofDEzfHx2ZWdldGFibGUlMjBnYXJkZW58ZW58MHx8fHwxNzMwNTk1OTcxfDA&ixlib=rb-4.0.3&q=80&w=1080",
            contentDescription = "Fondo de huerto",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // Capa oscura semitransparente sobre la imagen
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black.copy(alpha = 0.5f)
        ) {}

        // Contenido del Login
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .systemBarsPadding(), // Añade padding para la barra de estado/navegación
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Huerto Hogar",
                style = MaterialTheme.typography.displayMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Usamos Card para el formulario
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    HuertoTextField(
                        value = email,
                        onValueChange = onEmailChange,
                        label = "Correo Electrónico",
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    HuertoTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        label = "Contraseña",
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                        onImeAction = onLoginClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else {
                        Button(
                            onClick = onLoginClick,
                            modifier = Modifier.fillMaxWidth(),
                            // Usamos el color 'Terracotta'
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary
                            )
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
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onRegisterClick) {
                Text("¿No tienes cuenta? Regístrate aquí", color = Color.White)
            }
        }
    }
}
