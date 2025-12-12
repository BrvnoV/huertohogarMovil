package com.huertohogar.huertohogarmovil.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huertohogar.huertohogarmovil.repository.AppRepository
import com.huertohogar.huertohogarmovil.model.CarritoItem
import com.huertohogar.huertohogarmovil.model.Producto
import com.huertohogar.huertohogarmovil.repository.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Estado de la UI
data class ProductsState(
    val isLoading: Boolean = false,
    val products: List<Producto> = emptyList(),
    val error: String? = null
)

// Eventos de la UI
sealed class ProductsEvent {
    data class OnAddToCartClick(val producto: Producto) : ProductsEvent()
}

// Eventos para "efectos secundarios" (como mostrar un Snackbar)
sealed class ProductsUiEvent {
    data class ShowSnackbar(val message: String) : ProductsUiEvent()
}

class ProductsViewModel(
    private val repository: AppRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    // Flujo de todos los productos
    val products: Flow<List<Producto>> = repository.getAllProductos()

    // Flujo para eventos (ej: mostrar Snackbar)
    private val _eventFlow = MutableSharedFlow<ProductsUiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: ProductsEvent) {
        when (event) {
            is ProductsEvent.OnAddToCartClick -> {
                addToCart(event.producto)
            }
        }
    }

    private fun addToCart(producto: Producto) {
        viewModelScope.launch {
            // 1. Obtener el ID del usuario actual
            val userId = sessionManager.currentUserId.first()

            if (userId == SessionManager.NO_USER_ID) {
                _eventFlow.emit(ProductsUiEvent.ShowSnackbar("Error: No has iniciado sesión"))
                return@launch
            }

            // 2. Verificar si el item ya existe en el carrito
            val itemExistente = repository.getCarritoItem(userId, producto.id).first()

            if (itemExistente == null) {
                // 3. Si no existe, crearlo con cantidad 1
                // Esta línea ahora funciona gracias al import corregido
                val nuevoItem = CarritoItem(
                    userId = userId,
                    productId = producto.id,
                    quantity = 1
                )
                repository.addItemAlCarrito(nuevoItem)
            } else {
                // 4. Si ya existe, actualizarlo (cantidad + 1)
                val itemActualizado = itemExistente.copy(
                    quantity = itemExistente.quantity + 1
                )
                repository.updateItemEnCarrito(itemActualizado)
            }

            // 5. Enviar evento a la UI para mostrar confirmación
            _eventFlow.emit(ProductsUiEvent.ShowSnackbar("${producto.name} agregado al carrito!"))

        }
    }

    // NUEVO: Llama sync en init para fetch de API y cache en DB (mezcla con hardcoded)
    init {
        viewModelScope.launch {
            try {
                val result = repository.syncProductosFromFruityvice()
                if (result.isSuccess) {
                    Log.d("ProductsVM", "Sync de API exitoso: productos cargados desde Fruityvice")
                } else {
                    Log.e("ProductsVM", "Sync de API falló: ${result.exceptionOrNull()?.message}")
                    // Opcional: Emite snackbar para usuario
                    // _eventFlow.emit(ProductsUiEvent.ShowSnackbar("No se pudieron cargar productos nuevos. Usando locales."))
                }
            } catch (e: Exception) {
                Log.e("ProductsVM", "Error inesperado en sync: ${e.message}")
            }
            // UI se actualiza automáticamente via Flow de DB
        }
    }
}