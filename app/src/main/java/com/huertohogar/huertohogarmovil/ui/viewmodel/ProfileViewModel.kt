package com.huertohogar.huertohogarmovil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huertohogar.huertohogarmovil.data.repository.AppRepository
import com.huertohogar.huertohogarmovil.repository.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first // <-- 3. Importar 'first'
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la UI de Perfil
data class ProfileState(
    val isLoading: Boolean = true,
    val userName: String = "",
    val userEmail: String = "",
    val error: String? = null //
)

// Eventos
sealed class ProfileEvent {
    object OnLogoutClick : ProfileEvent()
    object OnErrorShown : ProfileEvent()
}

class ProfileViewModel(
    private val repository: AppRepository,
    private val sessionManager: com.google.android.gms.cast.framework.SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileState())
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    fun onEvent(event: ProfileEvent) {
        when(event) {
            ProfileEvent.OnLogoutClick -> {
                logout()
            }
            ProfileEvent.OnErrorShown -> {
                _uiState.update { it.copy(error = null) }
            }
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // --- 5. CORRECCIÓN: Obtener ID real de la sesión ---
            val currentUserId = sessionManager.currentUserId.first() // Obtenemos el ID guardado

            // Manejamos el caso de que no haya sesión
            if (currentUserId == SessionManager.NO_USER_ID) {
                _uiState.update { it.copy(isLoading = false, error = "Sesión no encontrada") }
                // En un caso real, esto debería forzar un logout automático
                return@launch
            }
            // --- FIN DE CORRECCIÓN ---

            // Usamos .collect() para que si el usuario cambia sus datos en otro lugar,
            // esta pantalla se actualice automáticamente.
            repository.getUsuarioById(currentUserId).collect { usuario ->
                if (usuario != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            userName = usuario.nombre,
                            userEmail = usuario.email
                        )
                    }
                } else {
                    // El ID de sesión existe pero el usuario no está en la BD (error)
                    _uiState.update { it.copy(isLoading = false, error = "Error al cargar datos del usuario") }
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            // --- 6. CORRECCIÓN: Llamar al SessionManager ---
            sessionManager.logout()
            // El evento de navegación se maneja en la UI (ProfileRoute)
            // que está escuchando el 'onLogout'
        }
    }
}