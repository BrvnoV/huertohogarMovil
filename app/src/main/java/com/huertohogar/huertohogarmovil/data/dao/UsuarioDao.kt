package com.huertohogar.huertohogarmovil.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.huertohogar.huertohogarmovil.model.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    /**
     * Inserta un nuevo usuario. Si el email ya existe, falla (se maneja en el ViewModel/Repo).
     * Usamos REPLACE para fines de prototipo, pero IGNORE o ABORT sería más estricto.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsuario(usuario: Usuario)

    /**
     * Busca un usuario por su email.
     * Se usa para el Login.
     */
    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    fun getUsuarioByEmail(email: String): Flow<Usuario?>

    /**
     * Busca un usuario por su ID.
     * Se usa para cargar el perfil.
     */
    @Query("SELECT * FROM usuarios WHERE id = :userId LIMIT 1")
    fun getUsuarioById(userId: Int): Flow<Usuario?>
}