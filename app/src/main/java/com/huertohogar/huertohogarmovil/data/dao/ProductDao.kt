package com.huertohogar.huertohogarmovil.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.huertohogar.huertohogarmovil.data.model.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    // Obtiene TODOS los productos de la tabla
    // Usamos Flow para que la lista de productos se actualice sola si hay cambios.
    @Query("SELECT * FROM productos")
    fun getAllProductos(): Flow<List<Producto>>

    // Inserta una lista de productos (para la carga inicial de datos)
    // Si un producto con el mismo ID ya existe, lo reemplaza.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(productos: List<Producto>)

    // Obtiene un producto específico por su ID
    @Query("SELECT * FROM productos WHERE id = :id")
    fun getProductoById(id: Int): Flow<Producto?>

    // (Opcional) Contar productos, para saber si necesitamos hacer la carga inicial
    @Query("SELECT COUNT(*) FROM productos")
    suspend fun countProductos(): Int

    // (Opcional) Borrar todos los productos (para pruebas)
    @Query("DELETE FROM productos")
    suspend fun deleteAll()
}
