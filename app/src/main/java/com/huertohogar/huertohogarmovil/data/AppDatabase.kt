package com.huertohogar.huertohogarmovil.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.huertohogar.huertohogarmovil.data.dao.ProductoDao
import com.huertohogar.huertohogarmovil.data.dao.UsuarioDao
import com.huertohogar.huertohogarmovil.data.model.Producto
import com.huertohogar.huertohogarmovil.data.model.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Usuario::class, Producto::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // DAOs abstractos que Room implementará
    abstract fun usuarioDao(): UsuarioDao
    abstract fun productoDao(): ProductoDao

    companion object {
        // Volatile asegura que la instancia sea siempre visible para todos los hilos
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            // synchronized evita que dos hilos creen la instancia al mismo tiempo
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "huerto_hogar_db"
                )
                    .addCallback(RoomDatabaseCallback(context)) // Añadimos el callback para pre-cargar datos
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * Callback para pre-poblar la base de datos la primera vez que se crea.
     */
    private class RoomDatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Usamos un CoroutineScope para lanzar las operaciones de inserción en un hilo de fondo
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    prePopulateUsuarios(database.usuarioDao())
                    prePopulateProductos(database.productoDao())
                }
            }
        }

        /**
         * Inserta el usuario de prueba 'huerto' / '123123'
         */
        suspend fun prePopulateUsuarios(usuarioDao: UsuarioDao) {
            val testUser = Usuario(
                username = "huerto",
                pass = "123123", // Recuerda: en una app real, hashear esto.
                nombre = "Usuario de Prueba",
                direccion = "Av. Siempre Viva 123"
            )
            usuarioDao.insertUser(testUser)
        }

        /**
         * Inserta la lista inicial de productos del huerto
         */
        suspend fun prePopulateProductos(productoDao: ProductoDao) {
            val productosIniciales = listOf(
                Producto(nombre = "Tomate Cherry (Bandeja 250g)", descripcion = "Pequeños tomates dulces y jugosos, cultivados localmente. Perfectos para ensaladas frescas, snacks o para asar.", precio = 2490.0, imagenNombre = "tomate_cherry"),
                Producto(nombre = "Lechuga Hidropónica Costina (Unidad)", descripcion = "Lechuga fresca y crujiente cultivada sin pesticidas. Hojas firmes ideales para sándwiches y ensaladas César.", precio = 1390.0, imagenNombre = "lechuga"),
                Producto(nombre = "Frutillas Orgánicas (Bandeja 500g)", descripcion = "Selección de frutillas maduradas al sol, libres de químicos. Sabor intenso y dulce garantizado.", precio = 3990.0, imagenNombre = "frutillas"),
                Producto(nombre = "Planta de Albahaca Viva (Maceta)", descripcion = "¡Cosecha tus propias hojas! Planta de albahaca genovesa lista para mantener en tu cocina y usarla fresca en pestos y salsas.", precio = 2990.0, imagenNombre = "albahaca"),
                Producto(nombre = "Zanahorias (Manojo 500g)", descripcion = "Manojo de zanahorias tiernas y dulces, recién cosechadas. Ideales para jugos, guisos o para comer crudas.", precio = 1290.0, imagenNombre = "zanahorias"),
                Producto(nombre = "Arándanos Premium (Bandeja 125g)", descripcion = "Arándanos grandes y firmes, seleccionados por su alto contenido de antioxidantes. El topping perfecto para tu yogurt o avena.", precio = 2190.0, imagenNombre = "arandanos"),
                Producto(nombre = "Pimentón Rojo (Unidad)", descripcion = "Pimentón rojo carnoso y de sabor dulce. Aporta un color vibrante y un toque ahumado a tus sofritos y asados.", precio = 1190.0, imagenNombre = "pimenton_rojo"),
                Producto(nombre = "Palta Hass (Malla 1kg)", descripcion = "Paltas (aguacates) Hass de primera calidad, en su punto óptimo de maduración. Cremosas e ideales para tostadas.", precio = 5990.0, imagenNombre = "palta"),
                Producto(nombre = "Limón Sutil (Malla 1kg)", descripcion = "Limones sutiles (tipo lima) muy jugosos y aromáticos. Esenciales para aderezos, ceviches y pisco sour.", precio = 2290.0, imagenNombre = "limon_sutil"),
                Producto(nombre = "Zapallo Italiano (Zucchini)", descripcion = "Zucchini tierno y versátil. Perfecto para saltear, hacer cremas o como alternativa baja en carbohidratos para pastas.", precio = 990.0, imagenNombre = "zapallo_italiano")
            )
            productoDao.insertAll(productosIniciales)
        }
    }
}
