package com.huertohogar.huertohogarmovil.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huertohogar.huertohogarmovil.data.repository.AppRepository
import com.huertohogar.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la UI de Registro
data class RegisterState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val registerSuccess: Boolean = false
)

// Eventos
sealed class RegisterEvent {
    data class OnRegisterClick(
        val nombre: String,
        val email: String,
        val pass: String,
        val confirmPass: String
    ) : RegisterEvent()
    object OnErrorShown : RegisterEvent()
}

class RegisterViewModel(
    private val repository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterState())
    val uiState: StateFlow<RegisterState> = _uiState.asStateFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.OnRegisterClick -> {
                register(event.nombre, event.email, event.pass, event.confirmPass)
            }
            is RegisterEvent.OnErrorShown -> {
                _uiState.update { it.copy(error = null) }
            }
        }
    }

    private fun register(nombre: String, email: String, pass: String, confirmPass: String) {
        if (pass != confirmPass) {
            _uiState.update { it.copy(error = "Las contraseñas no coinciden") }
            return
        }
        if (nombre.isBlank() || email.isBlank() || pass.isBlank()) {
            _uiState.update { it.copy(error = "Todos los campos son obligatorios") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Aquí iría la lógica para hashear la contraseña (ej: SHA-256)
            val passHash = pass // Simulación

            val nuevoUsuario = Usuario(
                nombre = nombre,
                email = email,
                passwordHash = passHash
            )

            try {
                repository.registerUsuario(nuevoUsuario)
                _uiState.update { it.copy(isLoading = false, registerSuccess = true) }
            } catch (e: Exception) {
                // Capturar error (ej: email ya existe si hay una constraint UNIQUE)
                _uiState.update { it.copy(isLoading = false, error = "No se pudo registrar: ${e.message}") }
            }
        }
    }
}