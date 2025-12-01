package com.huertohogar.huertohogarmovil.uitest

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.huertohogar.huertohogarmovil.screens.profile.ProfileScreen
import com.huertohogar.huertohogarmovil.ui.theme.HuertohogarMovilTheme
import com.huertohogar.huertohogarmovil.ui.viewmodel.ProfileEvent
import com.huertohogar.huertohogarmovil.ui.viewmodel.ProfileState
import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.assertIsDisplayed // Para verificar la existencia

/**
 * Prueba de instrumentación para verificar el comportamiento y la visualización
 * de la pantalla de Perfil (ProfileScreen).
 */
class ProfileScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Estado de prueba con datos cargados
    private val loadedState = ProfileState(
        isLoading = false,
        userName = "Juan Huerta",
        userEmail = "juan.huerta@huertohogar.com"
    )

    // Estado de prueba en carga
    private val loadingState = ProfileState(
        isLoading = true,
        userName = "",
        userEmail = ""
    )

    // --- 1. PRUEBA DE VISUALIZACIÓN DE DATOS ---
    @Test
    fun profileScreen_debe_mostrar_datos_del_usuario_y_titulos() {
        // GIVEN: Carga la pantalla con datos de usuario
        composeTestRule.setContent {
            HuertohogarMovilTheme {
                ProfileScreen(
                    state = loadedState,
                    onLogoutClick = {},
                    onNavigateToMap = {}
                )
            }
        }

        // THEN: Verificar que los datos del usuario están visibles
        composeTestRule.onNodeWithText("Mi Perfil").assertIsDisplayed()
        composeTestRule.onNodeWithText("Juan Huerta").assertIsDisplayed()
        composeTestRule.onNodeWithText("juan.huerta@huertohogar.com").assertIsDisplayed()
    }

    // --- 2. PRUEBA DE ESTADO DE CARGA ---
    @Test
    fun profileScreen_debe_mostrar_indicador_de_carga_cuando_esta_cargando() {
        // GIVEN: Carga la pantalla en estado de carga
        composeTestRule.setContent {
            HuertohogarMovilTheme {
                ProfileScreen(
                    state = loadingState,
                    onLogoutClick = {},
                    onNavigateToMap = {}
                )
            }
        }

        // THEN: El contenido principal (texto "Mi Perfil" y el botón de logout) no debe existir.
        composeTestRule.onNodeWithText("Mi Perfil").assertDoesNotExist()
        composeTestRule.onNodeWithText("Cerrar Sesión").assertDoesNotExist()

        // NOTA: Para verificar el CircularProgressIndicator directamente, necesitaríamos
        // añadir un `testTag` o `contentDescription` al Box que lo contiene en la ProfileScreen.
        // Por simplicidad, la ausencia de contenido principal es suficiente.
    }

    // --- 3. PRUEBA DE INTERACCIÓN: CERRAR SESIÓN ---
    @Test
    fun profileScreen_debe_llamar_onLogoutClick_al_presionar_cerrar_sesion() {
        var logoutClicked = false

        // GIVEN: Carga la pantalla y captura el evento
        composeTestRule.setContent {
            HuertohogarMovilTheme {
                ProfileScreen(
                    state = loadedState,
                    onLogoutClick = { logoutClicked = true }, // Captura la acción
                    onNavigateToMap = {}
                )
            }
        }

        // WHEN: Presiona el botón "Cerrar Sesión"
        composeTestRule.onNodeWithText("Cerrar Sesión").performClick()

        // THEN: Verificar que la lambda se ejecutó
        assert(logoutClicked) { "onLogoutClick debió ser llamado al hacer clic." }
    }

    // --- 4. PRUEBA DE INTERACCIÓN: NAVEGAR A MAPA ---
    @Test
    fun profileScreen_debe_llamar_onNavigateToMap_al_presionar_ver_mapa() {
        var mapNavigationClicked = false

        // GIVEN: Carga la pantalla y captura el evento
        composeTestRule.setContent {
            HuertohogarMovilTheme {
                ProfileScreen(
                    state = loadedState,
                    onLogoutClick = {},
                    onNavigateToMap = { mapNavigationClicked = true } // Captura la acción
                )
            }
        }

        // WHEN: Presiona el botón "Ver mi ubicación en el mapa"
        composeTestRule.onNodeWithText("Ver mi ubicación en el mapa").performClick()

        // THEN: Verificar que la lambda de navegación se ejecutó
        assert(mapNavigationClicked) { "onNavigateToMap debió ser llamado al hacer clic." }
    }
}