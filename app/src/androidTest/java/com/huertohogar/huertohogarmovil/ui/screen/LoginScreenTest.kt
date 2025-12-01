package com.huertohogar.huertohogarmovil.uitest

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performTextInput
import com.huertohogar.huertohogarmovil.screens.login.LoginScreen
import com.huertohogar.huertohogarmovil.ui.theme.HuertohogarMovilTheme
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_debe_permitir_ingresar_email_y_contrasena() {
        // Variables que ahora son 'State' observables, como en la aplicación real.
        var emailInput by mutableStateOf("")
        var passwordInput by mutableStateOf("")

        // 2. Cargamos el composable de la pantalla
        composeTestRule.setContent {
            HuertohogarMovilTheme {
                LoginScreen(
                    // Pasamos los valores
                    email = emailInput,
                    password = passwordInput,
                    isLoading = false,
                    error = null,
                    // Las lambdas de cambio ahora actualizan el 'State'
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
        // NOTA: Debes asegurarte de que HuertoTextField tenga el modifier
        // Modifier.testTag("email_input") o Modifier.semantics { contentDescription = "email_input" }
        composeTestRule.onNodeWithContentDescription("email_input").performTextInput(emailValue)

        // --- Interacción 2: Ingresar Contraseña ---
        val passwordValue = "contrasena123"
        composeTestRule.onNodeWithContentDescription("password_input").performTextInput(passwordValue)

        // 4. Afirmación (Verificación): El State (variables) deben coincidir con lo que se escribió.
        // Las aserciones deben pasar porque el 'State' fue actualizado por las lambdas.
        assert(emailInput == emailValue)
        assert(passwordInput == passwordValue)
    }
}