package com.huertohogar.huertohogarmovil.data.repository

import com.huertohogar.huertohogarmovil.data.dao.CarritoDao
import com.huertohogar.huertohogarmovil.data.dao.ProductoDao
import com.huertohogar.huertohogarmovil.data.dao.UsuarioDao
import com.huertohogar.model.CarritoItem
import com.huertohogar.model.CarritoItemConDetalles
import com.huertohogar.model.Producto
import com.huertohogar.model.Usuario
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz para el repositorio. Útil para Inyección de Dependencias y testing.
 */
interface AppRepository {
    // --- Usuario ---
    fun getUsuarioByEmail(email: String): Flow<Usuario?>
    fun getUsuarioById(id: Int): Flow<Usuario?>
    suspend fun registerUsuario(usuario: Usuario)

    // --- Producto ---
    fun getAllProductos(): Flow<List<Producto>>
    fun getProductoById(id: String): Flow<Producto?>

    // --- Carrito (ACTUALIZADO) ---

    // fun getCarrito(userId: Int): Flow<List<CarritoItem>> // <-- Esta es la versión simple, la eliminamos

    /**
     * Busca un item específico en el carrito de un usuario.
     * Usado por ProductsViewModel.
     */
    fun getCarritoItem(userId: Int, productId: String): Flow<CarritoItem?> // <-- NUEVO

    /**
     * Obtiene los items del carrito con todos los detalles del producto.
     * Usado por CartViewModel.
     */
    fun getCarritoConDetalles(userId: Int): Flow<List<CarritoItemConDetalles>> // <-- NUEVO

    suspend fun addItemAlCarrito(item: CarritoItem)
    suspend fun updateItemEnCarrito(item: CarritoItem)
    suspend fun removeItemDelCarrito(item: CarritoItem)
    suspend fun clearCarrito(userId: Int)
}

/**
 * Implementación del repositorio.
 * (En una app real, esto se proveería con Hilt/Dagger)
 */
class AppRepositoryImpl(
    private val usuarioDao: UsuarioDao,
    private val productoDao: ProductoDao,
    private val carritoDao: CarritoDao
) : AppRepository {

    // --- Usuario ---
    override fun getUsuarioByEmail(email: String): Flow<Usuario?> = usuarioDao.getUsuarioByEmail(email)
    override fun getUsuarioById(id: Int): Flow<Usuario?> = usuarioDao.getUsuarioById(id)
    override suspend fun registerUsuario(usuario: Usuario) = usuarioDao.insertUsuario(usuario)

    // --- Producto ---
    override fun getAllProductos(): Flow<List<Producto>> = productoDao.getAllProductos()
    override fun getProductoById(id: String): Flow<Producto?> = productoDao.getProductoById(id)

    // --- Carrito (ACTUALIZADO) ---

    // override fun getCarrito(userId: Int): Flow<List<CarritoItem>> = carritoDao.getCarritoDelUsuario(userId) // <-- ELIMINADO

    override fun getCarritoItem(userId: Int, productId: String): Flow<CarritoItem?> = // <-- NUEVO
        carritoDao.getCarritoItem(userId, productId)

    override fun getCarritoConDetalles(userId: Int): Flow<List<CarritoItemConDetalles>> = // <-- NUEVO
        carritoDao.getCarritoConDetalles(userId)

    override suspend fun addItemAlCarrito(item: CarritoItem) = carritoDao.insertItem(item)
    override suspend fun updateItemEnCarrito(item: CarritoItem) = carritoDao.updateItem(item)
    override suspend fun removeItemDelCarrito(item: CarritoItem) = carritoDao.deleteItem(item)
    override suspend fun clearCarrito(userId: Int) = carritoDao.clearCarrito(userId)
}