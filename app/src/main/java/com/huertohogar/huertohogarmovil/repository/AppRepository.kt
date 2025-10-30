package com.huertohogar.huertohogarmovil.data.repository

import android.app.Application
import com.huertohogar.huertohogarmovil.data.AppDatabase
import com.huertohogar.huertohogarmovil.data.dao.ProductoDao
import com.huertohogar.huertohogarmovil.data.dao.UsuarioDao
import com.huertohogar.huertohogarmovil.data.model.Usuario
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio que maneja el acceso a datos desde la BD (Room) y
 * (en el futuro) desde servicios de red o hardware (GPS).
 *
 * Es la ÚNICA fuente de verdad para los ViewModels.
 */
class AppRepository(application: Application) {

    // Instanciamos la base de datos y los DAOs
    private val database = AppDatabase.getInstance(application)
    private val usuarioDao: UsuarioDao = database.usuarioDao()
    private val productoDao: ProductoDao = database.productoDao()

    // --- API de Productos ---

    /**
     * Expone la lista completa de productos como un Flow.
     * La UI observará este Flow para actualizarse automáticamente.
     */
    val todosLosProductos = productoDao.getAllProductos()

    // --- API de Usuarios (Login/Registro) ---

    /**
     * Intenta loguear a un usuario. Retorna el Usuario si éxito, null si falla.
     */
    suspend fun login(username: String, pass: String): Usuario? {
        return usuarioDao.getUser(username, pass)
    }

    /**
     * Registra un nuevo usuario.
     * Lanza una excepción (que el ViewModel manejará) si el username ya existe.
     */
    suspend fun register(usuario: Usuario) {
        // Verificamos si el usuario ya existe
        val existingUser = usuarioDao.getUserByUsername(usuario.username)
        if (existingUser != null) {
            throw Exception("El nombre de usuario '${usuario.username}' ya está en uso.")
        }
        // Si no existe, lo insertamos
        usuarioDao.insertUser(usuario)
    }

    // --- API de Perfil de Usuario ---

    /**
     * Obtiene los datos de un usuario por su ID como un Flow.
     * Se usa para la pantalla de Perfil.
     */
    fun getUsuarioById(id: Int): Flow<Usuario?> {
        return usuarioDao.getUserById(id)
    }

    /**
     * Actualiza los datos del usuario (foto de perfil, ubicación, etc.)
     */
    suspend fun actualizarUsuario(usuario: Usuario) {
        usuarioDao.updateUser(usuario)
    }

    // --- API de Ubicación (GPS) ---

    // TODO: Más adelante implementaremos aquí la lógica para obtener
    // la ubicación desde LocationService.

}
