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

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    // Instancia del repositorio
    private val repository = AppRepository(application)

    // --- Estado de la UI (manejado por Compose) ---

    // Campos del formulario
    var username by mutableStateOf("")
    var password by mutableStateOf("")

    // Estado de la operación de login
    var isLoading by mutableStateOf(false)
    var loginError by mutableStateOf<String?>(null)

    /**
     * El usuario que ha iniciado sesión.
     * La UI observará esto: si no es null, significa que el login fue exitoso
     * y podemos navegar a la pantalla principal.
     */
    var loggedInUser by mutableStateOf<Usuario?>(null)

    // --- Lógica de Negocio ---

    /**
     * Intenta iniciar sesión.
     * Se ejecuta en un hilo de fondo (IO) para no bloquear la UI.
     */
    fun login() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true
            loginError = null
            try {
                val user = repository.login(username, password)
                if (user != null) {
                    // ¡Éxito! Actualizamos el estado con el usuario
                    loggedInUser = user
                } else {
                    // Falla: Credenciales incorrectas
                    loginError = "Usuario o contraseña incorrecta."
                }
            } catch (e: Exception) {
                // Falla: Error inesperado (ej. DB)
                loginError = "Error al intentar iniciar sesión: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Limpia el mensaje de error (ej. cuando el usuario vuelve a escribir)
     */
    fun clearError() {
        loginError = null
    }
}
