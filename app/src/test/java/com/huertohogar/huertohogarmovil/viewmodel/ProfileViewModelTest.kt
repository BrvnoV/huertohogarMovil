package com.huertohogar.huertohogarmovil.ui.viewmodel

import com.huertohogar.huertohogarmovil.repository.AppRepository
import com.huertohogar.huertohogarmovil.repository.SessionManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

// Esta anotación es necesaria para usar las funciones de testing de coroutines
@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private lateinit var viewModel: ProfileViewModel
    private val mockRepository = mockk<AppRepository>(relaxed = true)
    private val mockSessionManager = mockk<SessionManager>(relaxed = true)

    private val testDispatcher = UnconfinedTestDispatcher()

    // Datos simulados
    private val TEST_USER_ID = 1
    private val TEST_USER_NAME = "Carlos Tester"
    private val TEST_USER_EMAIL = "carlos@huerto.com"
    private val TEST_USER_MODEL = com.huertohogar.huertohogarmovil.model.Usuario(
        id = TEST_USER_ID,
        nombre = TEST_USER_NAME,
        email = TEST_USER_EMAIL,
        passwordHash = "hash"
    )

    @BeforeEach
    fun setup() {
        // 1. Establecer el Dispatcher
        Dispatchers.setMain(testDispatcher)

        // 2. Configurar la simulación de dependencias

        // Simular que el SessionManager devuelve un ID de usuario válido
        every { mockSessionManager.currentUserId } returns flowOf(TEST_USER_ID)

        // Simular que el Repository devuelve el modelo de usuario
        coEvery { mockRepository.getUsuarioById(TEST_USER_ID) } returns flowOf(TEST_USER_MODEL)

        // 3. Inicializar el ViewModel
        viewModel = ProfileViewModel(mockRepository, mockSessionManager)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `al iniciar, los detalles del perfil deben cargarse correctamente`() = runTest {
        // GIVEN: El ViewModel inicializado

        // WHEN: Obtenemos el primer valor del estado de la UI (la carga ocurre en el init)
        val state = viewModel.uiState.value

        // THEN: Los campos deben coincidir
        assertEquals(TEST_USER_NAME, state.userName, "El nombre debe ser cargado.")
        assertEquals(TEST_USER_EMAIL, state.userEmail, "El email debe ser cargado.")
        assertFalse(state.isLoading, "El estado de carga debe ser falso.")
    }

    @Test
    fun `al hacer logout, debe llamar a la funcion logout del SessionManager`() = runTest {
        // GIVEN: El ViewModel listo

        // WHEN: Se dispara el evento de Logout
        viewModel.onEvent(ProfileEvent.OnLogoutClick)

        // THEN: Se debe verificar que SessionManager.logout() haya sido llamado
        coVerify(exactly = 1) { mockSessionManager.logout() }
    }
}