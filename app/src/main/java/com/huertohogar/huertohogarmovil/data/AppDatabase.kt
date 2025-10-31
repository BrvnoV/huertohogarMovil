package com.huertohogar.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.huertohogar.huertohogarmovil.data.dao.CarritoDao
import com.huertohogar.huertohogarmovil.data.dao.ProductoDao
import com.huertohogar.huertohogarmovil.data.dao.UsuarioDao
import com.huertohogar.model.CarritoItem
import com.huertohogar.model.Producto
import com.huertohogar.model.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Usuario::class, Producto::class, CarritoItem::class], // <-- ADAPTADO
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // DAOs abstractos que Room implementará
    abstract fun usuarioDao(): UsuarioDao
    abstract fun productoDao(): ProductoDao
    abstract fun carritoDao(): CarritoDao // <-- ADAPTADO

    companion object {
        // Volatile asegura que la instancia sea siempre visible para todos los hilos
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Usamos tu método getInstance
        fun getInstance(context: Context): AppDatabase {
            // synchronized evita que dos hilos creen la instancia al mismo tiempo
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "huerto_hogar_db"
                )
                    .addCallback(RoomDatabaseCallback(context)) // Añadimos tu callback
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * Tu Callback para pre-poblar la base de datos la primera vez que se crea.
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
         * ADAPTADO: Inserta el usuario de prueba usando el modelo Usuario (con email y passwordHash)
         */
        suspend fun prePopulateUsuarios(usuarioDao: UsuarioDao) {
            // Adaptado para coincidir con el modelo Usuario (email, nombre, passwordHash)
            val testUser = Usuario(
                email = "huerto@hogar.com", // Usamos email para el login
                passwordHash = "123123", // En app real, hashear "123123"
                nombre = "Naya"
            )
            usuarioDao.insertUsuario(testUser)
        }

        /**
         * ADAPTADO: Inserta la lista inicial de productos usando el modelo Producto (id String, price Int, etc.)
         */
        suspend fun prePopulateProductos(productoDao: ProductoDao) {

            // --- ¡LISTA ACTUALIZADA CON NOMBRES DE ARCHIVO! ---
            // (Asegúrate de que estos nombres coincidan con los que pusiste en drawable)
            val productosIniciales = listOf(
                Producto(id = "1", name = "Tomate Cherry (Bandeja 250g)", description = "Pequeños tomates dulces...", price = 2490,
                    imageName = "tomate_cherry"), // <-- Sin .jpg

                Producto(id = "2", name = "Lechuga Hidropónica Costina (Unidad)", description = "Lechuga fresca y crujiente...", price = 1390,
                    imageName = "lechuga"), // <-- Sin .png

                Producto(id = "3", name = "Frutillas Orgánicas (Bandeja 500g)", description = "Selección de frutillas maduradas...", price = 3990,
                    imageName = "frutillas"),

                Producto(id = "4", name = "Planta de Albahaca Viva (Maceta)", description = "¡Cosecha tus propias hojas!...", price = 2990,
                    imageName = "albahaca"),

                Producto(id = "5", name = "Zanahorias (Manojo 500g)", description = "Manojo de zanahorias tiernas...", price = 1290,
                    imageName = "zanahorias"),

                Producto(id = "6", name = "Arándanos Premium (Bandeja 125g)", description = "Arándanos grandes y firmes...", price = 2190,
                    imageName = "arandanos"),

                Producto(id = "7", name = "Pimentón Rojo (Unidad)", description = "Pimentón rojo carnoso...", price = 1190,
                    imageName = "pimenton_rojo"),

                Producto(id = "8", name = "Palta Hass (Malla 1kg)", description = "Paltas (aguacates) Hass...", price = 5990,
                    imageName = "palta_hass"),

                Producto(id = "9", name = "Limón Sutil (Malla 1kg)", description = "Limones sutiles (tipo lima)...", price = 2290,
                    imageName = "limon"),

                Producto(id = "10", name = "Zapallo Italiano (Zucchini)", description = "Zucchini tierno y versátil...", price = 990,
                    imageName = "zapallo_italiano")
            )
            // --- FIN DE LA LISTA ---

            productoDao.insertAll(productosIniciales)
        }
    }
}