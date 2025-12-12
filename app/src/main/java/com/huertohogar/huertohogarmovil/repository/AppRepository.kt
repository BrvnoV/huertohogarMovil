package com.huertohogar.huertohogarmovil.repository

import com.huertohogar.huertohogarmovil.data.dao.CarritoDao
import com.huertohogar.huertohogarmovil.data.dao.ProductoDao
import com.huertohogar.huertohogarmovil.data.dao.UsuarioDao
import com.huertohogar.huertohogarmovil.model.CarritoItem
import com.huertohogar.huertohogarmovil.model.CarritoItemConDetalles
import com.huertohogar.huertohogarmovil.model.Producto
import com.huertohogar.huertohogarmovil.model.Usuario
// --- IMPORTS NECESARIOS PARA EL CONSUMO REST ---
import com.huertohogar.huertohogarmovil.network.HuertoApiService
import com.huertohogar.huertohogarmovil.network.model.HuertoMapper
import com.huertohogar.huertohogarmovil.network.model.FruitDto
import com.huertohogar.huertohogarmovil.network.model.toProducto
// --- NUEVOS IMPORTS ---
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlin.Result

/**
 * Interfaz del Repositorio (El Contrato).
 * Define todas las operaciones que la capa ViewModel puede solicitar.
 */
interface AppRepository {
    fun getAllProductos(): Flow<List<Producto>>

    // --- NUEVO: Función para que el administrador pueda editar ---
    suspend fun updateProduct(product: Producto)

    // --- NUEVO: Sync desde Fruityvice ---
    suspend fun syncProductosFromFruityvice(): Result<Unit>

    // --- Usuario ---
    fun getUsuarioByEmail(email: String): Flow<Usuario?>
    fun getUsuarioById(id: Int): Flow<Usuario?>
    suspend fun registerUsuario(usuario: Usuario)

    // --- Carrito ---
    fun getCarritoConDetalles(userId: Int): Flow<List<CarritoItemConDetalles>>
    fun getCarritoItem(userId: Int, productId: String): Flow<CarritoItem?>
    suspend fun addItemAlCarrito(item: CarritoItem)
    suspend fun updateItemEnCarrito(item: CarritoItem)
    suspend fun removeItemDelCarrito(item: CarritoItem)
    suspend fun clearCarrito(userId: Int)

    fun getProductoById(id: String): Flow<Producto?>
}

/**
 * Implementación del Repositorio (El Chef).
 * Implementa la interfaz AppRepository directamente.
 */
class AppRepositoryImpl(
    private val usuarioDao: UsuarioDao,
    private val productoDao: ProductoDao,
    private val carritoDao: CarritoDao,
    private val apiService: HuertoApiService,
    private val huertoMapper: HuertoMapper
) : AppRepository {

    // --- PRODUCTO: AHORA DE ROOM (cacheado) ---
    override fun getAllProductos(): Flow<List<Producto>> = productoDao.getAllProductos()

    // --- ACTUALIZACIÓN DEL PRODUCTO POR EL ADMIN (Usa Room) ---
    override suspend fun updateProduct(product: Producto) {
        productoDao.insertProducto(product)
    }

    // --- NUEVO: Sync desde Fruityvice (fetch + save en DB) ---
    override suspend fun syncProductosFromFruityvice(): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.getAllFruits()
            if (response.isSuccessful && response.body() != null) {
                val frutas = response.body()!!  // List<FruitDto>
                val idsExistentes = productoDao.getAllIds()  // Chequea duplicados
                val nuevos = frutas
                    .mapNotNull { it.toProducto() }
                    .filter { !idsExistentes.contains(it.id) }  // Solo nuevos

                if (nuevos.isNotEmpty()) {
                    productoDao.insertAllIgnoringConflicts(nuevos)
                }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error API: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)  // Fallback a hardcoded
        }
    }

    // --- LÓGICA DE ROOM (Usuario y Carrito se mantienen locales) ---
    override fun getUsuarioByEmail(email: String) = usuarioDao.getUsuarioByEmail(email)
    override fun getUsuarioById(id: Int) = usuarioDao.getUsuarioById(id)
    override suspend fun registerUsuario(usuario: Usuario) = usuarioDao.insertUsuario(usuario)
    override fun getCarritoConDetalles(userId: Int) = carritoDao.getCarritoConDetalles(userId)
    override suspend fun addItemAlCarrito(item: CarritoItem) = carritoDao.insertItem(item)
    override suspend fun updateItemEnCarrito(item: CarritoItem) = carritoDao.updateItem(item)
    override suspend fun removeItemDelCarrito(item: CarritoItem) = carritoDao.deleteItem(item)
    override suspend fun clearCarrito(userId: Int) = carritoDao.clearCarrito(userId)
    override fun getCarritoItem(userId: Int, productId: String) = carritoDao.getCarritoItem(userId, productId)
    override fun getProductoById(id: String) = productoDao.getProductoById(id)
}