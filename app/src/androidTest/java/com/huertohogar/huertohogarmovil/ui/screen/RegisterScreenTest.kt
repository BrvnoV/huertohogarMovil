package com.huertohogar.huertohogarmovil.uitest

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.huertohogar.huertohogarmovil.screens.register.RegisterScreen
import com.huertohogar.huertohogarmovil.ui.theme.HuertohogarMovilTheme
import com.huertohogar.huertohogarmovil.ui.viewmodel.RegisterEvent
import com.huertohogar.huertohogarmovil.ui.viewmodel.RegisterState
import org.junit.Rule
import org.junit.Test

/**
 * Prueba de instrumentación para verificar el comportamiento de la pantalla de Registro.
 */
class RegisterScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun registerScreen_debe_mostrar_errores_de_validacion_tras_intento_fallido() {
        // GIVEN: Estado inicial limpio
        var testState by mutableStateOf(RegisterState())
        var lastEvent: RegisterEvent? = null

        // Mensajes de error esperados (usados en la aserción)
        val expectedEmailError = "Formato de email inválido"
        val expectedNameError = "El nombre no puede estar vacío"

        composeTestRule.setContent {
            HuertohogarMovilTheme {
                RegisterScreen(
                    state = testState,
                    onEvent = { event ->
                        lastEvent = event

                        // SIMULACIÓN DE LA LÓGICA DEL VIEWMODEL:
                        // Si se hace clic en Registrarse, el ViewModel actualiza el estado con errores
                        if (event is RegisterEvent.OnRegisterClick) {
                            testState = testState.copy(
                                emailError = expectedEmailError,
                                nombreError = expectedNameError,
                                passError = "La contraseña debe tener al menos 6 caracteres"
                            )
                        }
                    },
                    onNavigateToLogin = {}
                )
            }
        }

        // 1. WHEN: Intentar hacer clic sin llenar los campos (dispara el evento OnRegisterClick)
        composeTestRule.onNodeWithText("Registrarse").performClick()

        // 2. THEN: Verificar que los mensajes de error APARECEN en la UI
        composeTestRule.onNodeWithText(expectedNameError).assertExists()
        composeTestRule.onNodeWithText(expectedEmailError).assertExists()
        // Opcional: Verificar el error de contraseña
        composeTestRule.onNodeWithText("La contraseña debe tener al menos 6 caracteres").assertExists()
    }

    @Test
    fun registerScreen_debe_enviar_evento_de_registro_al_hacer_click() {
        var lastEvent: RegisterEvent? = null

        // GIVEN: Un estado con datos válidos simulados (no se usan para la aserción,
        // pero aseguran que la UI se comporte como si los datos fueran válidos).
        val validState = RegisterState(
            nombre = "Test User",
            email = "a@a.com",
            pass = "123456",
            confirmPass = "123456"
        )

        composeTestRule.setContent {
            HuertohogarMovilTheme {
                RegisterScreen(
                    state = validState,
                    // Capturamos el evento. Es un 'object' simple: RegisterEvent.OnRegisterClick
                    onEvent = { event -> lastEvent = event },
                    onNavigateToLogin = {}
                )
            }
        }

        // WHEN: El usuario presiona el botón
        composeTestRule.onNodeWithText("Registrarse").performClick()

        // THEN: El último evento enviado al ViewModel debe ser OnRegisterClick
        // Nota: Solo verificamos el tipo de evento, no los datos internos, ya que el evento no los transporta.
        assert(lastEvent is RegisterEvent.OnRegisterClick) {
            "El evento final debe ser OnRegisterClick, pero fue $lastEvent"
        }
    }
}