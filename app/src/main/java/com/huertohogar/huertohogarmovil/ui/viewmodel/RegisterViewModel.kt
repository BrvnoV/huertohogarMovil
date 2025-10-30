package com.huertohogar.huertohogarmovil.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.huertohogar.huertohogarmovil.data.model.Usuario
import com.huertohogar.huertohogarmovil.data.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    // Instancia del repositorio
    private val repository = AppRepository(application)

    // --- Estado de la UI ---

    // Campos del formulario
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var nombre by mutableStateOf("")
    var direccion by mutableStateOf("")

    // Estado de la operación de registro
    var isLoading by mutableStateOf(false)
    var registerError by mutableStateOf<String?>(null)

    /**
     * Si el registro es exitoso, esto se pondrá en true.
     * La UI observará este valor para navegar de vuelta al Login.
     */
    var registerSuccess by mutableStateOf(false)

    // --- Lógica de Negocio ---

    fun register() {
        // Validación de campos vacíos
        if (username.isBlank() || password.isBlank() || confirmPassword.isBlank() || nombre.isBlank() || direccion.isBlank()) {
            registerError = "Todos los campos son obligatorios."
            return
        }

        // Validación de contraseñas
        if (password != confirmPassword) {
            registerError = "Las contraseñas no coinciden."
            return
        }

        // Inicia el proceso en un hilo de fondo
        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true
            registerError = null
            try {
                val newUser = Usuario(
                    username = username.trim(),
                    pass = password, // En una app real, hashear la contraseña
                    nombre = nombre.trim(),
                    direccion = direccion.trim()
                )

                // Intentamos registrar (el repo lanzará excepción si el usuario ya existe)
                repository.register(newUser)

                // ¡Éxito!
                registerSuccess = true

            } catch (e: Exception) {
                // Falla: (Usuario ya existe, u otro error de BD)
                registerError = e.message ?: "Error desconocido al registrar."
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        registerError = null
    }
}
