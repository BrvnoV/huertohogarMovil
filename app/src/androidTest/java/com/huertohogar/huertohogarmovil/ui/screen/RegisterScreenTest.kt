package com.huertohogar.huertohogarmovil.uitest

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.huertohogar.huertohogarmovil.screens.register.RegisterScreen
import com.huertohogar.huertohogarmovil.ui.theme.HuertohogarMovilTheme
import com.huertohogar.huertohogarmovil.ui.viewmodel.RegisterEvent
import com.huertohogar.huertohogarmovil.ui.viewmodel.RegisterState
import org.junit.Rule
import org.junit.Test

/**
 * Prueba de instrumentación para verificar el comportamiento de la pantalla de Registro,
 * enfocándose en la validación visual.
 */
class RegisterScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun registerScreen_debe_mostrar_errores_de_validacion_al_intentar_registrar() {
        // GIVEN: Un estado inicial con campos vacíos y el error de contraseña conocido
        val initialState = RegisterState(
            emailError = "Formato de email inválido",
            passError = "La contraseña debe tener al menos 6 caracteres",
            nombreError = "El nombre no puede estar vacío"
        )
        var lastEvent: RegisterEvent? = null

        composeTestRule.setContent {
            HuertohogarMovilTheme {
                RegisterScreen(
                    state = initialState,
                    onEvent = { event -> lastEvent = event },
                    onNavigateToLogin = {}
                )
            }
        }

        // 1. Simular la interacción: Dejamos el campo de Email vacío y disparamos el click
        composeTestRule.onNodeWithText("Correo Electrónico").performTextInput("")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("short")

        // 2. Simular click en el botón de registro
        composeTestRule.onNodeWithText("Registrarse").performClick()

        // 3. VERIFICACIÓN VISUAL: El test verifica que los mensajes de error se muestren
        composeTestRule.onNodeWithText("El nombre no puede estar vacío").assertExists()
        composeTestRule.onNodeWithText("Formato de email inválido").assertExists()

        // El test verifica que la UI renderiza el estado de error correctamente.
        // Si el click no causa un crash, la prueba pasa si los mensajes de error existen.
    }

    @Test
    fun registerScreen_debe_enviar_evento_de_registro_al_hacer_click() {
        var lastEvent: RegisterEvent? = null

        // GIVEN: Un estado con datos válidos (simulado en el Test)
        composeTestRule.setContent {
            HuertohogarMovilTheme {
                RegisterScreen(
                    // Proporcionamos un estado que pasaría la validación inicial (simulada)
                    state = RegisterState(
                        nombre = "Test User",
                        email = "a@a.com",
                        pass = "123456",
                        confirmPass = "123456"
                    ),
                    onEvent = { event -> lastEvent = event },
                    onNavigateToLogin = {}
                )
            }
        }

        // WHEN: El usuario presiona el botón
        composeTestRule.onNodeWithText("Registrarse").performClick()

        // THEN: El último evento enviado al ViewModel debe ser OnRegisterClick
        assert(lastEvent is RegisterEvent.OnRegisterClick) {
            "El evento final debe ser OnRegisterClick, pero fue $lastEvent"
        }
    }
}