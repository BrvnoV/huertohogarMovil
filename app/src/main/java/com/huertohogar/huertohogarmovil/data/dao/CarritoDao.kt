package com.huertohogar.huertohogarmovil.data.dao

import androidx.room.*
import com.huertohogar.model.CarritoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CarritoDao {

    /**
     * Obtiene todos los items del carrito para un usuario específico.
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
}