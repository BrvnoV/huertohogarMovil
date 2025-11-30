package com.huertohogar.huertohogarmovil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huertohogar.huertohogarmovil.repository.AppRepository
import com.huertohogar.huertohogarmovil.model.CarritoItemConDetalles
import com.huertohogar.huertohogarmovil.repository.SessionManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// --- ESTADOS Y EVENTOS DE LA UI ---

/**
 * Representa el estado actual de la pantalla del carrito.
 */
data class CartState(
    val isLoading: Boolean = true,
    val cartItems: List<CarritoItemConDetalles> = emptyList(),
    val totalPrice: Int = 0, // Mantenemos Int para precios enteros ($2490)
    val error: String? = null
)

/**
 * Representa las acciones que el usuario puede realizar en la pantalla.
 */
sealed class CartEvent {
    data class OnRemoveItemClick(val item: CarritoItemConDetalles) : CartEvent()
    data class OnIncreaseQuantity(val item: CarritoItemConDetalles) : CartEvent()
    data class OnDecreaseQuantity(val item: CarritoItemConDetalles) : CartEvent()
    object OnCheckoutClick : CartEvent()
}

/**
 * Eventos de una sola vez que el ViewModel envía a la UI (ej. para navegar o mostrar un snackbar).
 */
sealed class CartUiEvent {
    object NavigateToCheckout : CartUiEvent()
    data class ShowSnackbar(val message: String) : CartUiEvent()
}


// --- VIEWMODEL ---

class CartViewModel(
    private val repository: AppRepository,
    private val sessionManager:SessionManager
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<CartUiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    /**
     * Flujo que emite el estado de la UI del carrito. Se actualiza automáticamente
     * cuando cambian los datos del carrito en la base de datos.
     */
    val uiState: StateFlow<CartState> = sessionManager.currentUserId
        .flatMapLatest { userId ->
            // Si no hay un usuario logueado, muestra el carrito vacío.
            if (userId == SessionManager.NO_USER_ID) {
                MutableStateFlow(CartState(isLoading = false))
            } else {
                // Si hay un usuario, obtiene sus artículos del carrito desde el repositorio.
                repository.getCarritoConDetalles(userId)
                    .map { items ->
                        CartState(
                            isLoading = false,
                            cartItems = items,
                            totalPrice = items.sumOf { it.producto.price * it.carritoItem.quantity }
                        )
                    }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CartState(isLoading = true)
        )

    /**
     * Punto de entrada para todos los eventos de la UI.
     * Centraliza la lógica de manejo de acciones del usuario.
     */
    fun onEvent(event: CartEvent) {
        viewModelScope.launch {
            when (event) {
                is CartEvent.OnRemoveItemClick -> {
                    // Esta es la lógica correcta
                    repository.removeItemDelCarrito(event.item.carritoItem)
                }
                is CartEvent.OnIncreaseQuantity -> {
                    val item = event.item.carritoItem
                    val actualizado = item.copy(quantity = item.quantity + 1)
                    repository.updateItemEnCarrito(actualizado)
                }
                is CartEvent.OnDecreaseQuantity -> {
                    val item = event.item.carritoItem
                    if (item.quantity > 1) {
                        val actualizado = item.copy(quantity = item.quantity - 1)
                        repository.updateItemEnCarrito(actualizado)
                    } else {
                        // Si la cantidad es 1, eliminar el artículo del carrito
                        repository.removeItemDelCarrito(item)
                    }
                }
                is CartEvent.OnCheckoutClick -> {
                    if (uiState.value.cartItems.isNotEmpty()) {
                        // Simula el proceso de pago: limpia el carrito y notifica a la UI para navegar.
                        val userId = sessionManager.currentUserId.first()
                        if (userId != SessionManager.NO_USER_ID) {
                            repository.clearCarrito(userId)
                            _eventFlow.emit(CartUiEvent.NavigateToCheckout)
                        }
                    } else {
                        _eventFlow.emit(CartUiEvent.ShowSnackbar("Tu carrito está vacío"))
                    }
                }
            }
        }
    }

}