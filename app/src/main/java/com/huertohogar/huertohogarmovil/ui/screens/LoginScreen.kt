package com.huertohogar.huertohogarmovil.screens.login


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.huertohogar.huertohogarmovil.R
import com.huertohogar.huertohogarmovil.HuertoHogarApp
import com.huertohogar.huertohogarmovil.ui.components.HuertoTextField
import com.huertohogar.huertohogarmovil.ui.viewmodel.LoginEvent
import com.huertohogar.huertohogarmovil.ui.viewmodel.LoginViewModel
import com.huertohogar.huertohogarmovil.ui.viewmodel.ViewModelFactory
// --- FIN DE IMPORTS ---

@Composable
fun LoginRoute(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
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

    // Usuario de prueba pre-cargado
    var email by rememberSaveable { mutableStateOf("huerto@hogar.com") }
    var password by rememberSaveable { mutableStateOf("123123") }

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

        // --- ¡FONDO CORREGIDO! ---
        // Usamos Image local, no AsyncImage
        Image(
            // Carga la imagen que pusiste en 'drawable'
            painter = painterResource(id = R.drawable.login_background),
            contentDescription = "Fondo de huerto",
            contentScale = ContentScale.Crop, // Rellena toda la pantalla
            modifier = Modifier.fillMaxSize()
        )
        // --- FIN DE FONDO ---

        // Capa oscura semitransparente sobre la imagen para legibilidad
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

            // --- ¡LOGO AÑADIDO! ---
            Image(
                // Carga el logo que pusiste en 'drawable'
                painter = painterResource(id = R.drawable.intro_logo),
                contentDescription = "Logo de Huerto Hogar",
                modifier = Modifier
                    .size(200.dp) // Ajusta el tamaño según tu logo
                    .clip(CircleShape) // Opcional: para logos redondos
            )
            // --- FIN DE LOGO ---

            Spacer(modifier = Modifier.height(24.dp))

            // Usamos Card para el formulario
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    // El fondo de la tarjeta es semi-transparente
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
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

// --- FUNCIÓN AYUDANTE (HELPER) ---
@Composable
fun viewModelFactory(): ViewModelFactory {
    val application = (LocalContext.current.applicationContext as HuertoHogarApp)
    return ViewModelFactory(application.repository, application.sessionManager)
}