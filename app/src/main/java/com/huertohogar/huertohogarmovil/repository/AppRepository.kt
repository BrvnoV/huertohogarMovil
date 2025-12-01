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
// --- FIN DE IMPORTS ---
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import retrofit2.HttpException

/**
 * Interfaz del Repositorio (El Contrato).
 * Define todas las operaciones que la capa ViewModel puede solicitar.
 */
interface AppRepository {
    fun getAllProductos(): Flow<List<Producto>>

    // --- NUEVO: Función para que el administrador pueda editar ---
    suspend fun updateProduct(product: Producto)

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
    private val productoDao: ProductoDao, // <-- Este DAO se usará para actualizar el producto
    private val carritoDao: CarritoDao,
    private val apiService: HuertoApiService,
    private val huertoMapper: HuertoMapper
) : AppRepository {

    // --- PRODUCTO: IMPLEMENTACIÓN CON RETROFIT (Carga desde el microservicio) ---
    override fun getAllProductos(): Flow<List<Producto>> = flow {
        try {
            val response = apiService.getAllFruits()

            if (response.isSuccessful && response.body() != null) {
                val productosListos = huertoMapper.fromResponseToDomain(response.body()!!)
                emit(productosListos)
            } else {
                println("Error HTTP al obtener productos: ${response.code()}")
                emit(emptyList())
            }
        } catch (e: HttpException) {
            println("Error de red o servidor: ${e.code()}")
            emit(emptyList())
        } catch (e: Exception) {
            println("Error de conexión: ${e.message}")
            emit(emptyList())
        }
    }

    // --- NUEVA FUNCIÓN: ACTUALIZACIÓN DEL PRODUCTO POR EL ADMIN (Usa Room) ---
    override suspend fun updateProduct(product: Producto) {
        // La actualización se realiza directamente en la tabla de productos de Room.
        // Asume que ProductoDao tiene un método para insertar/actualizar (generalmente llamado insert o update).
        productoDao.insertProducto(product)
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