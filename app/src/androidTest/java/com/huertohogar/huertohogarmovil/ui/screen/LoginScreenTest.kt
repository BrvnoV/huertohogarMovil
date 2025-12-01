package com.huertohogar.huertohogarmovil.uitest

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription // <-- Selector confiable para pruebas
import androidx.compose.ui.test.performTextInput
import com.huertohogar.huertohogarmovil.screens.login.LoginScreen
import com.huertohogar.huertohogarmovil.ui.theme.HuertohogarMovilTheme
import org.junit.Rule
import org.junit.Test

/**
 * Prueba de instrumentación para verificar el comportamiento de la pantalla de Login.
 * Verifica la interacción con los campos de texto identificados por 'testTag' (contentDescription).
 */
class LoginScreenTest {

    // 1. Regla principal de Compose para controlar la UI
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_debe_permitir_ingresar_email_y_contrasena() {
        // Variables que simulan el estado (ViewModel)
        var emailInput = ""
        var passwordInput = ""

        // 2. Cargamos el composable de la pantalla
        composeTestRule.setContent {
            HuertohogarMovilTheme {
                LoginScreen(
                    email = emailInput,
                    password = passwordInput,
                    isLoading = false,
                    error = null,
                    // Lambdas que actualizan las variables cuando se escribe en la UI
                    onEmailChange = { emailInput = it },
                    onPasswordChange = { passwordInput = it },
                    onLoginClick = { /* Simula el click */ },
                    onRegisterClick = { /* Simula la navegación */ }
                )
            }
        }

        // --- Interacción 1: Ingresar Email ---
        val emailValue = "test@huertohogar.com"

        // 3. Localizamos el campo por su contentDescription ("email_input") y escribimos texto
        composeTestRule.onNodeWithContentDescription("email_input").performTextInput(emailValue)

        // --- Interacción 2: Ingresar Contraseña ---
        val passwordValue = "contrasena123"
        composeTestRule.onNodeWithContentDescription("password_input").performTextInput(passwordValue)

        // 4. Afirmación (Verificación): Las variables locales DEBEN coincidir con lo que se escribió en la UI
        assert(emailInput == emailValue)
        assert(passwordInput == passwordValue)
    }
}