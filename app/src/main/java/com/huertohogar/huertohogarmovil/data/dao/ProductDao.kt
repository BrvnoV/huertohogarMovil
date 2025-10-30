package com.huertohogar.huertohogarmovil.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.huertohogar.model.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    /**
     * Obtiene todos los productos de la base de datos.
     * Devuelve un Flow para que la UI se actualice automáticamente.
     */
    @Query("SELECT * FROM productos")
    fun getAllProductos(): Flow<List<Producto>>

    /**
     * Obtiene un producto específico por su ID.
     */
    @Query("SELECT * FROM productos WHERE id = :productoId")
    fun getProductoById(productoId: String): Flow<Producto?>

    /**
     * Inserta una lista de productos.
     * Si un producto ya existe (misma ID), se reemplaza.
     * Útil para pre-cargar la base de datos.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(productos: List<Producto>)

    /**
     * Inserta un solo producto.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducto(producto: Producto)
}