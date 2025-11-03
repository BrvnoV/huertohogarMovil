package com.huertohogar.huertohogarmovil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.huertohogar.huertohogarmovil.data.repository.AppRepository
import com.huertohogar.huertohogarmovil.repository.SessionManager

/**
 * Esta fábrica crea nuestros ViewModels y les "inyecta" el Repositorio
 * y el SessionManager que necesitan.
 */
class ViewModelFactory(
    private val repository: AppRepository,
    private val sessionManager: SessionManager
    // El parámetro 'locationService' fue eliminado, ya que la lógica GPS es estática.
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        // Autenticación
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository, sessionManager) as T
        }
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(repository) as T
        }

        // Contenido Principal
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository, sessionManager) as T
        }
        if (modelClass.isAssignableFrom(ProductsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductsViewModel(repository, sessionManager) as T
        }
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(repository, sessionManager) as T
        }

        // Perfil y Mapa
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(repository, sessionManager) as T
        }

        // Ubicacion (Utiliza el servicio estático, no necesita inyección)
        if (modelClass.isAssignableFrom(UbicacionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UbicacionViewModel() as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
