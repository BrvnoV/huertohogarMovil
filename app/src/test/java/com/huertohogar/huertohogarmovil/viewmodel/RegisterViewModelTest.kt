package com.huertohogar.huertohogarmovil.ui.viewmodel

import com.huertohogar.huertohogarmovil.repository.AppRepository
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

// Esta anotación es necesaria para usar las funciones de testing de coroutines
@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    private lateinit var viewModel: RegisterViewModel
    // MockK: Creamos un objeto simulado (Mock) del AppRepository.
    // Esto asegura que la prueba sea "unitaria" y no toque la base de datos real.
    private val mockRepository = mockk<AppRepository>(relaxed = true)

    // TestDispatcher: Herramienta para controlar el tiempo de las coroutines.
    private val testDispatcher = UnconfinedTestDispatcher()

    // @BeforeEach se ejecuta antes de cada prueba (@Test)
    @BeforeEach
    fun setup() {
        // Establecer el Main dispatcher para que todas las coroutines del ViewModel
        // se ejecuten inmediatamente en nuestro dispatcher de prueba.
        Dispatchers.setMain(testDispatcher)

        // Inicializar el ViewModel, inyectando el Repository simulado.
        viewModel = RegisterViewModel(mockRepository)
    }

    // @AfterEach limpia el ambiente después de cada prueba
    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cuando la contrasena es menor a 6 caracteres, debe mostrar error`() = runTest {
        // GIVEN (Dado): Una contrasena demasiado corta
        val contrasenaCorta = "12345"

        // WHEN (Cuando): Se simula la escritura en el campo de contrasena
        viewModel.onEvent(RegisterEvent.OnPasswordChange(contrasenaCorta))

        // THEN (Entonces): El error de contrasena NO debe ser nulo.
        val state = viewModel.uiState.value
        assertNotNull(state.passError, "Debería mostrar un error indicando la longitud mínima.")
    }

    @Test
    fun `cuando las contrasenas coinciden, el error de confirmacion debe ser nulo`() = runTest {
        // GIVEN (Dado): Una contrasena valida y que coincide
        val contrasenaValida = "password123"

        // WHEN (Cuando): Se simulan ambos inputs
        viewModel.onEvent(RegisterEvent.OnPasswordChange(contrasenaValida))
        viewModel.onEvent(RegisterEvent.OnConfirmPassChange(contrasenaValida))

        // THEN (Entonces): El error de confirmacion DEBE ser nulo.
        val state = viewModel.uiState.value
        assertNull(state.confirmPassError, "No debería haber error de confirmación.")
    }
}