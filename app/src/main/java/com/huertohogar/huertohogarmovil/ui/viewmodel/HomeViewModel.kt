package com.huertohogar.huertohogarmovil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huertohogar.huertohogarmovil.data.repository.AppRepository
import com.huertohogar.huertohogarmovil.model.Producto
import com.huertohogar.huertohogarmovil.repository.SessionManager
import kotlinx.coroutines.flow.*

/**
 * Estado de la UI para la pantalla de Home.
 */
data class HomeState(
    val userName: String = "Usuario",
    val featuredProducts: List<Producto> = emptyList(),
    val isLoading: Boolean = true
)

/**
 * ViewModel para la pantalla principal.
 */
class HomeViewModel(
    private val repository: AppRepository, // <-- 4. Corregido (privado y tipo)
    private val sessionManager: SessionManager // <-- Usamos TU SessionManager
) : ViewModel() {

    // Combinamos dos flujos: el del ID del usuario y el de la lista de productos.
    // La UI se actualizará automáticamente si cualquiera de ellos cambia.
    val uiState: StateFlow<HomeState> = sessionManager.currentUserId // <-- ¡Esto ahora funciona!
        .flatMapLatest { userId ->
            // 1. Creamos un flujo para obtener los datos del usuario
            val userFlow = if (userId != SessionManager.NO_USER_ID) { // <-- ¡Esto ahora funciona!
                repository.getUsuarioById(userId)
            } else {
                flowOf(null) // Emitir nulo si no hay sesión
            }

            // 2. Obtenemos el flujo de productos
            val productsFlow = repository.getAllProductos()

            // 3. Los combinamos en un único estado (HomeState)
            userFlow.combine(productsFlow) { user, products ->
                HomeState(
                    userName = user?.nombre ?: "Usuario",
                    // Tomamos solo los primeros 5 como "destacados"
                    featuredProducts = products.take(5),
                    isLoading = false
                )
            }
        }.stateIn( // Convertimos el Flow en un StateFlow
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeState(isLoading = true) // Estado inicial de carga
        )
}