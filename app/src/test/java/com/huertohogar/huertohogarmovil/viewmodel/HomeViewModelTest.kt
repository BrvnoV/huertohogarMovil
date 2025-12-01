package com.huertohogar.huertohogarmovil.ui.viewmodel

import com.huertohogar.huertohogarmovil.model.Producto
import com.huertohogar.huertohogarmovil.repository.AppRepository
import com.huertohogar.huertohogarmovil.repository.SessionManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private val mockRepository = mockk<AppRepository>(relaxed = true)
    private val mockSessionManager = mockk<SessionManager>(relaxed = true)

    private val testDispatcher = UnconfinedTestDispatcher()

    // Datos simulados para la prueba
    private val TEST_USER_ID = 1
    private val TEST_USER_NAME = "Carlos Tester"
    private val TEST_PRODUCT_LIST = List(6) {
        Producto(it.toString(), "Item $it", "Desc", 100 * it, "img")
    }
    private val TEST_USER_MODEL = com.huertohogar.huertohogarmovil.model.Usuario(
        id = TEST_USER_ID,
        nombre = TEST_USER_NAME,
        email = "test@user.com",
        passwordHash = "hash"
    )

    @BeforeEach
    fun setup() {
        // 1. Establecer el Dispatcher
        Dispatchers.setMain(testDispatcher)

        // 2. Configurar la simulación de dependencias

        // Simular que el SessionManager devuelve un ID de usuario válido
        every { mockSessionManager.currentUserId } returns flowOf(TEST_USER_ID)

        // Simular que el Repository devuelve el modelo de usuario (para obtener el nombre)
        coEvery { mockRepository.getUsuarioById(TEST_USER_ID) } returns flowOf(TEST_USER_MODEL)

        // Simular que el Repository devuelve la lista de productos
        coEvery { mockRepository.getAllProductos() } returns flowOf(TEST_PRODUCT_LIST)

        // 3. Inicializar el ViewModel
        viewModel = HomeViewModel(mockRepository, mockSessionManager)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `al iniciar, el nombre de usuario debe cargarse correctamente`() = runTest {
        // GIVEN: El ViewModel inicializado

        // WHEN: Obtenemos el primer valor del estado de la UI
        val state = viewModel.uiState.first()

        // THEN: El nombre del estado debe coincidir con el usuario simulado
        assertEquals(TEST_USER_NAME, state.userName, "El nombre del usuario debe ser cargado desde el repositorio.")
        assertFalse(state.isLoading, "El estado de carga debe ser falso al terminar la carga.")
    }

    @Test
    fun `la lista de productos destacados debe contener solo 5 elementos`() = runTest {
        // GIVEN: El repositorio devuelve 6 productos (en setup)

        // WHEN: Obtenemos el estado
        val state = viewModel.uiState.first()

        // THEN: La lista de destacados debe ser limitada a 5.
        assertEquals(5, state.featuredProducts.size, "La lista de productos destacados debe estar limitada a 5 elementos.")
        assertEquals("Item 0", state.featuredProducts.first().name, "El primer producto debe ser 'Item 0'.")
    }
}