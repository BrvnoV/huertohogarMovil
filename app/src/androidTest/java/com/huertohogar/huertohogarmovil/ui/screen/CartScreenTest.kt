package com.huertohogar.huertohogarmovil.ui.screen // <--- Paquete ajustado

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.huertohogar.huertohogarmovil.model.CarritoItem
import com.huertohogar.huertohogarmovil.model.CarritoItemConDetalles
import com.huertohogar.huertohogarmovil.model.Producto
import com.huertohogar.huertohogarmovil.screens.cart.CartScreen // <--- Importación de la Screen
import com.huertohogar.huertohogarmovil.ui.theme.HuertohogarMovilTheme    // <--- Importación del Tema
import com.huertohogar.huertohogarmovil.ui.viewmodel.CartState         // <--- Importación del Estado
import com.huertohogar.huertohogarmovil.ui.viewmodel.CartEvent          // <--- Importación del Evento
import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onFirst

/**
 * Prueba de instrumentación para verificar el comportamiento y la visualización
 * de la pantalla del Carrito (CartScreen).
 */
class CartScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // 1. Datos de prueba: Producto base
    private val fakeProduct = Producto(
        id = "p1",
        name = "Manzana Fuji",
        description = "Manzana roja dulce",
        price = 2000,
        imageName = "apple"
    )

    // 2. Datos de prueba: Ítem del Carrito (cantidad 2)
    private val fakeCartItem = CarritoItemConDetalles(
        carritoItem = CarritoItem(
            id = 1,
            userId = 99,
            productId = "p1",
            quantity = 2
        ),
        producto = fakeProduct
    )

    // 3. Estado cargado (un ítem)
    private val loadedState = CartState(
        isLoading = false,
        cartItems = listOf(fakeCartItem),
        totalPrice = 4000
    )

    // 4. Estado vacío
    private val emptyState = CartState(
        isLoading = false,
        cartItems = emptyList(),
        totalPrice = 0
    )


    // ----------------------------------------------------
    // --- 1. PRUEBA DE VISUALIZACIÓN ---
    // ----------------------------------------------------
    @Test
    fun cartScreen_debe_mostrar_item_y_precio_total_cuando_esta_cargado() {
        // GIVEN: Carga la pantalla con el estado cargado
        composeTestRule.setContent {
            HuertohogarMovilTheme {
                CartScreen(
                    state = loadedState,
                    onRemoveItem = {}, onIncreaseQuantity = {},
                    onDecreaseQuantity = {}, onCheckoutClick = {}
                )
            }
        }

        // THEN: El nombre del producto, la cantidad y el total deben ser visibles.
        composeTestRule.onNodeWithText(fakeProduct.name).assertIsDisplayed()
        composeTestRule.onNodeWithText("2").assertIsDisplayed() // Cantidad
        composeTestRule.onNodeWithText("$4000").assertIsDisplayed() // Total
        composeTestRule.onNodeWithText("Proceder al Pago").assertIsDisplayed()
    }

    // ----------------------------------------------------
    // --- 2. PRUEBA DE ESTADO VACÍO ---
    // ----------------------------------------------------
    @Test
    fun cartScreen_debe_mostrar_mensaje_vacio_cuando_no_hay_items() {
        // GIVEN: Carga la pantalla con el estado vacío
        composeTestRule.setContent {
            HuertohogarMovilTheme {
                CartScreen(
                    state = emptyState,
                    onRemoveItem = {}, onIncreaseQuantity = {},
                    onDecreaseQuantity = {}, onCheckoutClick = {}
                )
            }
        }

        // THEN: Debe mostrar el mensaje de carrito vacío y no los botones de resumen
        composeTestRule.onNodeWithText("Tu carrito está vacío").assertIsDisplayed()
        composeTestRule.onNodeWithText("Proceder al Pago").assertDoesNotExist()
    }


    // ----------------------------------------------------
    // --- 3. PRUEBA DE INTERACCIÓN: AUMENTAR/DISMINUIR/ELIMINAR ---
    // ----------------------------------------------------
    @Test
    fun cartScreen_debe_enviar_evento_correcto_al_interactuar_con_item() {
        var lastEvent: CartEvent? = null

        // GIVEN: Carga la pantalla y captura los eventos
        composeTestRule.setContent {
            HuertohogarMovilTheme {
                CartScreen(
                    state = loadedState,
                    onRemoveItem = { lastEvent = CartEvent.OnRemoveItemClick(it) },
                    onIncreaseQuantity = { lastEvent = CartEvent.OnIncreaseQuantity(it) },
                    onDecreaseQuantity = { lastEvent = CartEvent.OnDecreaseQuantity(it) },
                    onCheckoutClick = {}
                )
            }
        }

        // --- WHEN: Aumentar Cantidad ---
        // Usamos onFirst() ya que puede haber múltiples botones "+" si hay más items.
        composeTestRule.onAllNodesWithContentDescription("Añadir uno").onFirst().performClick()

        // THEN: Verificar el evento de aumentar
        assert(lastEvent is CartEvent.OnIncreaseQuantity) { "El evento esperado era OnIncreaseQuantity, pero fue $lastEvent" }
        assert((lastEvent as CartEvent.OnIncreaseQuantity).item.producto.id == fakeProduct.id) { "Producto incorrecto en el evento." }


        // --- WHEN: Disminuir Cantidad ---
        composeTestRule.onAllNodesWithContentDescription("Quitar uno").onFirst().performClick()

        // THEN: Verificar el evento de disminuir
        assert(lastEvent is CartEvent.OnDecreaseQuantity) { "El evento esperado era OnDecreaseQuantity, pero fue $lastEvent" }


        // --- WHEN: Eliminar Ítem ---
        composeTestRule.onAllNodesWithContentDescription("Eliminar item").onFirst().performClick()

        // THEN: Verificar el evento de eliminar
        assert(lastEvent is CartEvent.OnRemoveItemClick) { "El evento esperado era OnRemoveItemClick, pero fue $lastEvent" }
    }


    // ----------------------------------------------------
    // --- 4. PRUEBA DE INTERACCIÓN: PROCEDER AL PAGO ---
    // ----------------------------------------------------
    @Test
    fun cartScreen_debe_enviar_evento_checkout_al_presionar_pago() {
        var lastEvent: CartEvent? = null

        // GIVEN: Carga la pantalla y captura el evento
        composeTestRule.setContent {
            HuertohogarMovilTheme {
                CartScreen(
                    state = loadedState,
                    onRemoveItem = {}, onIncreaseQuantity = {},
                    onDecreaseQuantity = {},
                    onCheckoutClick = { lastEvent = CartEvent.OnCheckoutClick } // Captura el evento
                )
            }
        }

        // WHEN: Presiona el botón "Proceder al Pago"
        composeTestRule.onNodeWithText("Proceder al Pago").performClick()

        // THEN: El último evento enviado debe ser OnCheckoutClick
        assert(lastEvent is CartEvent.OnCheckoutClick) { "El evento final debe ser OnCheckoutClick, pero fue $lastEvent" }
    }
}