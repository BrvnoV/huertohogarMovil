package com.huertohogar.huertohogarmovil.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.huertohogar.huertohogarmovil.data.model.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    // --- Funciones para Login y Registro ---

    // Busca un usuario por username y password (para el login)
    @Query("SELECT * FROM usuarios WHERE username = :username AND pass = :pass LIMIT 1")
    suspend fun getUser(username: String, pass: String): Usuario?

    // Busca un usuario solo por username (para verificar si existe antes de registrar)
    @Query("SELECT * FROM usuarios WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): Usuario?

    // Inserta un nuevo usuario. Si el username ya existe, aborta la transacción.
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(usuario: Usuario)

    // --- Funciones para el Perfil de Usuario ---

    // Obtiene un usuario por su ID (para mostrar el perfil del usuario logueado)
    // Usamos Flow para que la UI se actualice automáticamente si los datos cambian.
    @Query("SELECT * FROM usuarios WHERE id = :id")
    fun getUserById(id: Int): Flow<Usuario?>

    // Actualiza los datos de un usuario (para guardar foto de perfil o ubicación)
    @Update
    suspend fun updateUser(usuario: Usuario)
}
