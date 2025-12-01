package com.huertohogar.huertohogarmovil.ui.viewmodel

import com.huertohogar.huertohogarmovil.model.Producto
import com.huertohogar.huertohogarmovil.model.CarritoItem
import com.huertohogar.huertohogarmovil.repository.AppRepository
import com.huertohogar.huertohogarmovil.repository.SessionManager
import io.mockk.coEvery
import io.mockk.coVerify
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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

// Esta anotación es necesaria para usar las funciones de testing de coroutines
@OptIn(ExperimentalCoroutinesApi::class)
class ProductsViewModelTest {

    private lateinit var viewModel: ProductsViewModel
    private val mockRepository = mockk<AppRepository>(relaxed = true)
    private val mockSessionManager = mockk<SessionManager>(relaxed = true)

    private val testDispatcher = UnconfinedTestDispatcher()

    // --- DATOS DE PRUEBA SIMULADOS ---
    private val FAKE_USER_ID = 1
    private val FAKE_PRODUCT_LIST = listOf(
        Producto("1", "Manzana", "Fresca", 1500, "apple"),
        Producto("2", "Pera", "Jugosa", 1200, "pear")
    )
    private val FAKE_CART_ITEM = CarritoItem(1, FAKE_USER_ID, "1", 1)


    // @BeforeEach se ejecuta antes de cada prueba
    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Configuración de Mocks
        every { mockSessionManager.currentUserId } returns flowOf(FAKE_USER_ID)
        coEvery { mockRepository.getAllProductos() } returns flowOf(FAKE_PRODUCT_LIST)
        // Simulamos que el item NO existe en el carrito al añadir
        coEvery { mockRepository.getCarritoItem(FAKE_USER_ID, "1") } returns flowOf(null)

        // Inicializar el ViewModel
        viewModel = ProductsViewModel(mockRepository, mockSessionManager)
    }

    // @AfterEach limpia el ambiente después de cada prueba
    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `al iniciar, la lista de productos debe cargarse desde el repositorio`() = runTest {
        // WHEN: Obtenemos el primer valor del flow de productos
        val products = viewModel.products.first()

        // THEN: La lista debe coincidir con los datos simulados
        assertTrue(products.isNotEmpty())
        assertEquals(2, products.size)
    }

    @Test
    fun `al agregar un producto, debe llamar a addItemAlCarrito`() = runTest {
        // GIVEN: El producto a añadir
        val productToAdd = FAKE_PRODUCT_LIST.first() // <-- Ahora usa la constante del Setup

        // WHEN: Se simula la acción de añadir
        viewModel.onEvent(ProductsEvent.OnAddToCartClick(productToAdd))

        // THEN: Se debe llamar a addItemAlCarrito una vez con la cantidad 1
        coVerify(exactly = 1) {
            mockRepository.addItemAlCarrito(match {
                it.productId == productToAdd.id && it.quantity == 1
            })
        }
    }
}