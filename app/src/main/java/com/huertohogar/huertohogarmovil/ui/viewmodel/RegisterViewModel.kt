package com.huertohogar.huertohogarmovil.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huertohogar.huertohogarmovil.repository.AppRepository
import com.huertohogar.huertohogarmovil.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// --- ESTADO Y EVENTOS (Sin cambios) ---

data class RegisterState(
    val nombre: String = "",
    val email: String = "",
    val pass: String = "",
    val confirmPass: String = "",
    val nombreError: String? = null,
    val emailError: String? = null,
    val passError: String? = null,
    val confirmPassError: String? = null,
    val isLoading: Boolean = false,
    val generalError: String? = null,
    val registerSuccess: Boolean = false
)

sealed class RegisterEvent {
    data class OnNombreChange(val value: String) : RegisterEvent()
    data class OnEmailChange(val value: String) : RegisterEvent()
    data class OnPasswordChange(val value: String) : RegisterEvent()
    data class OnConfirmPassChange(val value: String) : RegisterEvent()
    object OnRegisterClick : RegisterEvent()
    object OnErrorShown : RegisterEvent()
}

// --- VIEWMODEL CON LÓGICA INTEGRADA ---

class RegisterViewModel(
    private val repository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterState())
    val uiState: StateFlow<RegisterState> = _uiState.asStateFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.OnNombreChange -> {
                _uiState.update { it.copy(
                    nombre = event.value,
                    // Llama a la función privada de validación
                    nombreError = validateName(event.value)
                )}
            }
            is RegisterEvent.OnEmailChange -> {
                _uiState.update { it.copy(
                    email = event.value,
                    emailError = validateEmail(event.value)
                )}
            }
            is RegisterEvent.OnPasswordChange -> {
                _uiState.update { it.copy(
                    pass = event.value,
                    passError = validatePassword(event.value),
                    confirmPassError = validatePasswordMatch(event.value, it.confirmPass)
                )}
            }
            is RegisterEvent.OnConfirmPassChange -> {
                _uiState.update { it.copy(
                    confirmPass = event.value,
                    confirmPassError = validatePasswordMatch(it.pass, event.value)
                )}
            }
            is RegisterEvent.OnRegisterClick -> {
                submitRegistration()
            }
            is RegisterEvent.OnErrorShown -> {
                _uiState.update { it.copy(generalError = null) }
            }
        }
    }

    private fun submitRegistration() {
        val state = _uiState.value

        // 1. Ejecuta todas las validaciones
        val nameError = validateName(state.nombre)
        val emailError = validateEmail(state.email)
        val passError = validatePassword(state.pass)
        val confirmPassError = validatePasswordMatch(state.pass, state.confirmPass)

        val hasError = listOf(nameError, emailError, passError, confirmPassError).any { it != null }

        if (hasError) {
            _uiState.update { it.copy(
                nombreError = nameError,
                emailError = emailError,
                passError = passError,
                confirmPassError = confirmPassError
            )}
            return
        }

        // 4. Si no hay errores, procede con el registro
        viewModelScope.launch {
            // ... (Lógica de registro se mantiene igual) ...
            _uiState.update { it.copy(isLoading = true) }
            val passHash = state.pass
            val nuevoUsuario =
                Usuario(nombre = state.nombre, email = state.email, passwordHash = passHash)

            try {
                repository.registerUsuario(nuevoUsuario)
                _uiState.update { it.copy(isLoading = false, registerSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, generalError = "Error: ${e.message}") }
            }
        }
    }

    // --- ¡AQUÍ ESTÁ LA LÓGICA DE VALIDACIÓN! ---
    // (Movida desde Validator.kt y hecha 'private')

    private fun validateName(name: String): String? {
        return when {
            name.isBlank() -> "El nombre no puede estar vacío"
            name.length < 3 -> "El nombre debe tener al menos 3 caracteres"
            else -> null // El nombre es válido
        }
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "El email no puede estar vacío"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Formato de email inválido"
            else -> null // El email es válido
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "La contraseña no puede estar vacía"
            password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            else -> null // La contraseña es válida
        }
    }

    private fun validatePasswordMatch(pass1: String, pass2: String): String? {
        return when {
            pass1 != pass2 -> "Las contraseñas no coinciden"
            else -> null // Las contraseñas coinciden
        }
    }
}