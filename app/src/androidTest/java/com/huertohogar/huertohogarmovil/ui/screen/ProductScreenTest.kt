package com.huertohogar.huertohogarmovil.uitest

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.huertohogar.huertohogarmovil.model.Producto
import com.huertohogar.huertohogarmovil.screens.products.ProductsScreen
import com.huertohogar.huertohogarmovil.ui.theme.HuertohogarMovilTheme
import com.huertohogar.huertohogarmovil.ui.viewmodel.ProductsEvent
import com.huertohogar.huertohogarmovil.ui.viewmodel.ProductsState
import org.junit.Rule
import org.junit.Test

/**
 * Prueba de instrumentación para verificar la visualización y la interacción
 * de la pantalla de Productos (Catálogo).
 */
class ProductsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Datos de prueba que simulan los productos cargados de la API
    private val fakeProducts = listOf(
        Producto("1", "Manzana Fuji", "Importada", 2000, "apple"),
        Producto("2", "Naranja Chilena", "Dulce", 1500, "orange")
    )

    // El estado que pasaremos a la pantalla
    private val initialState = ProductsState(
        isLoading = false,
        products = fakeProducts,
        error = null
    )

    @Test
    fun productsScreen_debe_mostrar_productos_cargados_y_precios() {
        // GIVEN: Carga la pantalla con productos simulados
        composeTestRule.setContent {
            HuertohogarMovilTheme {
                ProductsScreen(
                    products = initialState.products,
                    snackbarHostState = androidx.compose.material3.SnackbarHostState(),
                    onProductClick = {},
                    onAddToCartClick = {}
                )
            }
        }

        // THEN: Verificar que el nombre y el precio del primer producto estén visibles
        composeTestRule.onNodeWithText("Manzana Fuji").assertExists()
        composeTestRule.onNodeWithText("$2000").assertExists()
    }

    @Test
    fun productsScreen_debe_enviar_evento_de_anadir_al_carrito() {
        var lastEvent: ProductsEvent? = null

        // GIVEN: Carga la pantalla y captura el evento
        composeTestRule.setContent {
            HuertohogarMovilTheme {
                ProductsScreen(
                    products = initialState.products,
                    snackbarHostState = androidx.compose.material3.SnackbarHostState(),
                    onProductClick = {},
                    onAddToCartClick = { product ->
                        // Capturamos el evento y su contenido (el producto)
                        lastEvent = ProductsEvent.OnAddToCartClick(product)
                    }
                )
            }
        }

        // WHEN: El usuario presiona el botón 'Añadir'
        // NOTA: Usamos el texto del botón que definimos en ProductCard ("Añadir")
        composeTestRule.onNodeWithText("Añadir", ignoreCase = true).performClick()

        // THEN: El último evento capturado debe ser OnAddToCartClick y debe contener el producto correcto
        assert(lastEvent is ProductsEvent.OnAddToCartClick) {
            "El evento esperado era OnAddToCartClick, pero fue $lastEvent"
        }

        val addedProduct = (lastEvent as ProductsEvent.OnAddToCartClick).producto
        assert(addedProduct.name == "Manzana Fuji") {
            "El producto añadido debería ser Manzana Fuji (el primer elemento encontrado)."
        }
    }
}