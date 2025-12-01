package com.huertohogar.huertohogarmovil.data.dao

import androidx.room.*
import com.huertohogar.huertohogarmovil.model.CarritoItem
import com.huertohogar.huertohogarmovil.model.CarritoItemConDetalles
import kotlinx.coroutines.flow.Flow

@Dao
interface CarritoDao {

    /**
     * Obtiene todos los items del carrito para un usuario específico.
     * (Esta es la versión simple, la de abajo 'getCarritoConDetalles' es la que usaremos)
     */
    @Query("SELECT * FROM carrito_items WHERE userId = :userId")
    fun getCarritoDelUsuario(userId: Int): Flow<List<CarritoItem>>

    /**
     * Inserta un item en el carrito.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: CarritoItem)

    /**
     * Actualiza un item (ej: cambiar cantidad).
     */
    @Update
    suspend fun updateItem(item: CarritoItem)

    /**
     * Elimina un item del carrito.
     */
    @Delete
    suspend fun deleteItem(item: CarritoItem)

    /**
     * Limpia todo el carrito de un usuario (ej: después de pagar).
     */
    @Query("DELETE FROM carrito_items WHERE userId = :userId")
    suspend fun clearCarrito(userId: Int)


    /**
     * ¡NUEVO! Busca un item específico en el carrito de un usuario.
     * Útil para saber si un producto ya fue agregado.
     */
    @Query("SELECT * FROM carrito_items WHERE userId = :userId AND productId = :productId LIMIT 1")
    fun getCarritoItem(userId: Int, productId: String): Flow<CarritoItem?>

    /**
     * ¡NUEVO Y CLAVE! Obtiene todos los items del carrito de un usuario
     * PERO TAMBIÉN trae los detalles del producto asociado.
     * Usa la clase CarritoItemConDetalles.
     */
    @Transaction // <-- Esto es importante para que la consulta de relación funcione
    @Query("SELECT * FROM carrito_items WHERE userId = :userId")
    fun getCarritoConDetalles(userId: Int): Flow<List<CarritoItemConDetalles>>

    // --- CORRECCIÓN: Se eliminaron las funciones duplicadas de aquí ---
}