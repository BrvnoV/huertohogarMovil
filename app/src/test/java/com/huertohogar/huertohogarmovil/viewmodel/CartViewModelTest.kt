package com.huertohogar.huertohogarmovil.ui.viewmodel

import com.huertohogar.huertohogarmovil.model.CarritoItem
import com.huertohogar.huertohogarmovil.model.Producto
import com.huertohogar.huertohogarmovil.model.CarritoItemConDetalles
import com.huertohogar.huertohogarmovil.repository.AppRepository
import com.huertohogar.huertohogarmovil.repository.SessionManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
// --- ANOTACIONES DE JUNIT 5 (JUPITER) ---
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
// Importamos las aserciones de Jupiter (JUnit 5)
import org.junit.jupiter.api.Assertions.assertEquals
// --- FIN DE ANOTACIONES ---

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {

    private lateinit var viewModel: CartViewModel
    private val mockRepository = mockk<AppRepository>(relaxed = true)
    private val mockSessionManager = mockk<SessionManager>(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    // Datos de prueba
    private val testUser = 1
    private val testProduct = Producto("1", "Tomate", "Desc", 1000, "tomate")
    private val testItem = CarritoItem(1, testUser, "1", 2)
    private val testItemDetails = CarritoItemConDetalles(testItem, testProduct)

    // Datos de prueba para el caso de cantidad 1
    private val itemQtyOne = testItem.copy(quantity = 1)
    private val itemDetailsQtyOne = testItemDetails.copy(carritoItem = itemQtyOne)


    // @BeforeEach se ejecuta antes de cada prueba
    @BeforeEach
    fun setup() {
        // 1. Configuración de Dispatcher
        Dispatchers.setMain(testDispatcher)

        // 2. Configurar el Manager para el ID de Usuario
        every { mockSessionManager.currentUserId } returns flowOf(testUser)

        // 3. Configurar el Repository para devolver una lista NO VACÍA (para la inicialización del ViewModel)
        coEvery { mockRepository.getCarritoConDetalles(testUser) } returns flowOf(listOf(testItemDetails))

        // 4. Inicializar el ViewModel
        viewModel = CartViewModel(mockRepository, mockSessionManager)

        // 5. Correr las coroutines de inicialización (observación de SessionManager y Repository)
        // ESTO ES CLAVE: Carga el estado inicial (cartItems) ANTES de que corran los tests.
        testDispatcher.scheduler.advanceUntilIdle()
    }

    // @AfterEach limpia el ambiente después de cada prueba
    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `al decrementar cantidad y es mayor a 1, debe llamar a updateItem`() = runTest(testDispatcher) {
        // GIVEN: El item tiene 2 unidades
        val currentQuantity = testItem.quantity // 2

        // WHEN: Se presiona el boton de decrementar
        viewModel.onEvent(CartEvent.OnDecreaseQuantity(testItemDetails))

        // Ejecutar la coroutine del evento
        testDispatcher.scheduler.advanceUntilIdle()

        // THEN: Se debe llamar a updateItemEnCarrito con quantity=1
        coVerify(exactly = 1) {
            mockRepository.updateItemEnCarrito(
                testItem.copy(quantity = currentQuantity - 1)
            )
        }
        coVerify(exactly = 0) { mockRepository.removeItemDelCarrito(any()) }
    }

    @Test
    fun `al decrementar cantidad a 1, debe llamar a removeItem`() = runTest(testDispatcher) {
        // GIVEN: La prueba usa el itemDetailsQtyOne (quantity=1)

        // Ejecutar coroutines pendientes (inicialización)
        testDispatcher.scheduler.advanceUntilIdle()

        // WHEN: Se presiona el boton de decrementar
        viewModel.onEvent(CartEvent.OnDecreaseQuantity(itemDetailsQtyOne))

        // Ejecutar la coroutine del evento
        testDispatcher.scheduler.advanceUntilIdle()

        // THEN: Se debe llamar a removeItemDelCarrito con el item de quantity=1
        coVerify(exactly = 1) { mockRepository.removeItemDelCarrito(itemQtyOne) }
        coVerify(exactly = 0) { mockRepository.updateItemEnCarrito(any()) }
    }

    @Test
    fun `al presionar checkout, debe limpiar el carrito`() = runTest(testDispatcher) {
        // GIVEN: El setup asegura que el carrito tiene items

        // Ejecutar coroutines pendientes (inicialización)
        testDispatcher.scheduler.advanceUntilIdle()

        // WHEN: Se presiona el checkout
        viewModel.onEvent(CartEvent.OnCheckoutClick)

        // Ejecutar la coroutine del evento
        testDispatcher.scheduler.advanceUntilIdle()

        // THEN: Se debe llamar a clearCarrito con el ID de usuario correcto
        coVerify(exactly = 1) { mockRepository.clearCarrito(testUser) }
    }
}