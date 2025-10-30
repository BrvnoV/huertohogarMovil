package com.huertohogar.huertohogarmovil.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huertohogar.huertohogarmovil.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la UI de Perfil
data class ProfileState(
    val isLoading: Boolean = true,
    val userName: String = "",
    val userEmail: String = ""
)

// Eventos
sealed class ProfileEvent {
    object OnLogoutClick : ProfileEvent()
}

class ProfileViewModel(
    private val repository: AppRepository
    // private val sessionManager: SessionManager
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
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Obtener el ID del usuario logueado (simulación)
            // val currentUserId = sessionManager.getUserId().first()
            val currentUserId = 1 // SIMULACIÓN

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
                    // Usuario no encontrado, forzar logout
                    _uiState.update { it.copy(isLoading = false) }
                    // sessionManager.clearSession()
                    // Aquí se debería emitir un evento de "logout forzado"
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            // sessionManager.clearSession()
            // El evento de navegación se maneja en la UI
        }
    }
}