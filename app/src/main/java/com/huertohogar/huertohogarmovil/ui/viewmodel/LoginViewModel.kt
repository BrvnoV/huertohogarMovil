package com.huertohogar.huertohogarmovil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huertohogar.huertohogarmovil.data.repository.AppRepository
import com.huertohogar.huertohogarmovil.repository.SessionManager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la UI de Login
data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginSuccess: Boolean = false
)

// Eventos que la UI puede enviar al ViewModel
sealed class LoginEvent {
    data class OnLoginClick(val email: String, val pass: String) : LoginEvent()
    object OnErrorShown : LoginEvent()
}

class LoginViewModel(
    private val repository: AppRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginState())
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnLoginClick -> {
                login(event.email, event.pass)
            }
            is LoginEvent.OnErrorShown -> {
                _uiState.update { it.copy(error = null) }
            }
        }
    }

    private fun login(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // --- 5. CORRECCIÓN LÓGICA: Usar .firstOrNull() en lugar de .collect() ---
            // Obtenemos el primer (y único) usuario que coincida
            val usuario = repository.getUsuarioByEmail(email).firstOrNull()

            if (usuario == null) {
                _uiState.update { it.copy(isLoading = false, error = "Usuario no encontrado") }
            } else {
                // --- 6. CORRECCIÓN LÓGICA: Comparar con passwordHash ---
                // (Tu usuario de prueba tiene "huerto@hogar.com" y "123123" como hash)
                if (pass == usuario.passwordHash) {

                    // --- 7. CORRECCIÓN LÓGICA: Guardar sesión ---
                    sessionManager.login(usuario.id) // <-- Guardamos el ID del usuario

                    _uiState.update { it.copy(isLoading = false, loginSuccess = true) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Contraseña incorrecta") }
                }
            }
        }
    }
}