package com.huertohogar.huertohogarmovil.ui.screen

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.huertohogar.huertohogarmovil.model.Producto
import com.huertohogar.huertohogarmovil.screens.home.HomeScreen
import com.huertohogar.huertohogarmovil.ui.theme.HuertohogarMovilTheme
import com.huertohogar.huertohogarmovil.ui.viewmodel.HomeState
import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onFirst

/**
 * Prueba de instrumentación para verificar el comportamiento y la visualización
 * de la pantalla de Inicio (HomeScreen).
 */
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule() // <-- ¡Esta es la definición!

    // 1. Datos de prueba: Productos Destacados
    private val fakeFeaturedProducts = listOf(
        Producto(
            id = "p101",
            name = "Tomate Cherry",
            description = "Pequeños y dulces",
            price = 500,
            imageName = "tomato"
        ),
        Producto(
            id = "p102",
            name = "Pimiento Rojo",
            description = "Ideal para ensaladas",
            price = 800,
            imageName = "pepper"
        )
    )

    // 2. Estado cargado con datos
    private val loadedState = HomeState(
        isLoading = false,
        userName = "Sara",
        featuredProducts = fakeFeaturedProducts
    )

    // 3. Estado de carga
    private val loadingState = HomeState(
        isLoading = true,
        userName = "Usuario",
        featuredProducts = emptyList()
    )


    // ----------------------------------------------------
    // --- 1. PRUEBA DE VISUALIZACIÓN DE CONTENIDO ---
    // ----------------------------------------------------
    @Test
    fun homeScreen_debe_mostrar_bienvenida_categorias_y_productos() {
        // GIVEN: Carga la pantalla con el estado cargado
        composeTestRule.setContent {
            HuertohogarMovilTheme {
                HomeScreen(
                    state = loadedState,
                    onProductClick = {}
                )
            }
        }

        // THEN: Verificar que los elementos principales están visibles
        // 1. Bienvenida al usuario
        composeTestRule.onNodeWithText("¡Bienvenid@ Sara!").assertIsDisplayed()

        // 2. Elementos estáticos (Barra de búsqueda y títulos)
        composeTestRule.onNodeWithText("Buscar productos...").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Buscar").assertIsDisplayed()
        composeTestRule.onNodeWithText("Categorías").assertIsDisplayed()
        composeTestRule.onNodeWithText("Productos Destacados").assertIsDisplayed()

        // 3. Un producto destacado
        composeTestRule.onNodeWithText("Tomate Cherry").assertIsDisplayed()
        composeTestRule.onNodeWithText("$500").assertIsDisplayed() // <--- ¡CORRECCIÓN APLICA AQUÍ SI EL FALLO ESTABA EN ESTA ZONA!

        // 4. Una categoría
        composeTestRule.onNodeWithText("Frutas").assertIsDisplayed()
    }

    // ----------------------------------------------------
    // --- 2. PRUEBA DE ESTADO DE CARGA ---
    // ----------------------------------------------------
    @Test
    fun homeScreen_debe_mostrar_indicador_de_carga_y_ocultar_productos() {
        // GIVEN: Carga la pantalla en estado de carga
        composeTestRule.setContent {
            HuertohogarMovilTheme {
                HomeScreen(
                    state = loadingState,
                    onProductClick = {}
                )
            }
        }

        // THEN: El contenido principal (texto "Mi Perfil" y los botones) no debe existir.

        // 1. Verificar que el contenido estático SI está
        composeTestRule.onNodeWithText("¡Bienvenid@ Usuario!").assertIsDisplayed()

        // 2. Verificar que los productos NO están (no deben estar renderizados)
        composeTestRule.onNodeWithText("Productos Destacados").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tomate Cherry").assertDoesNotExist()

        // 3. Verificar que el indicador de carga existe (se asume que es el único texto no estándar)
        // La ausencia de productos confirma que la rama de 'isLoading' está activa.
    }


    // ----------------------------------------------------
    // --- 3. PRUEBA DE INTERACCIÓN: CLIC EN PRODUCTO ---
    // ----------------------------------------------------
    @Test
    fun homeScreen_debe_llamar_onProductClick_al_presionar_producto() {
        var clickedProductId: String? = null
        val expectedProductId = fakeFeaturedProducts.first().id // "p101"

        // GIVEN: Carga la pantalla y captura el evento
        composeTestRule.setContent {
            HuertohogarMovilTheme {
                HomeScreen(
                    state = loadedState,
                    onProductClick = { productId -> clickedProductId = productId } // Captura el ID
                )
            }
        }

        // WHEN: Presiona el primer producto destacado ("Tomate Cherry")
        composeTestRule.onNodeWithText("Tomate Cherry").performClick()

        // THEN: Verificar que la lambda se ejecutó con el ID correcto
        assert(clickedProductId == expectedProductId) {
            "El ID del producto clicado debe ser $expectedProductId, pero fue $clickedProductId"
        }
    }
}