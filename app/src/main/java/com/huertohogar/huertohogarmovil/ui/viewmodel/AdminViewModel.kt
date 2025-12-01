package com.huertohogar.huertohogarmovil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huertohogar.huertohogarmovil.model.Producto
import com.huertohogar.huertohogarmovil.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado de la UI para la pantalla de Administración.
 */
data class AdminState(
    val products: List<Producto> = emptyList(),
    val isLoading: Boolean = false,
    val message: String? = null // Para mostrar "Producto actualizado" o errores
)

/**
 * Eventos (Acciones) que el Administrador puede realizar.
 */
sealed class AdminEvent {
    // Evento para guardar los cambios de un producto (precio, nombre, etc.)
    data class OnUpdateProduct(val product: Producto) : AdminEvent()

    // Evento para limpiar mensajes (Snackbars)
    object OnMessageShown : AdminEvent()
}

class AdminViewModel(
    private val repository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminState())
    val uiState: StateFlow<AdminState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    fun onEvent(event: AdminEvent) {
        when (event) {
            is AdminEvent.OnUpdateProduct -> {
                updateProduct(event.product)
            }
            is AdminEvent.OnMessageShown -> {
                _uiState.update { it.copy(message = null) }
            }
        }
    }

    /**
     * Carga la lista de productos y se mantiene escuchando cambios.
     */
    private fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repository.getAllProductos()
                .catch { e ->
                    _uiState.update {
                        it.copy(isLoading = false, message = "Error al cargar: ${e.message}")
                    }
                }
                .collect { products ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            products = products
                        )
                    }
                }
        }
    }

    /**
     * Llama al repositorio para actualizar un producto específico en la BD.
     */
    private fun updateProduct(product: Producto) {
        viewModelScope.launch {
            try {
                repository.updateProduct(product)
                _uiState.update { it.copy(message = "Producto '${product.name}' actualizado correctamente.") }
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "Error al actualizar: ${e.message}") }
            }
        }
    }
}