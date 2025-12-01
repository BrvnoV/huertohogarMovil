package com.huertohogar.huertohogarmovil.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.huertohogar.huertohogarmovil.data.dao.CarritoDao
import com.huertohogar.huertohogarmovil.data.dao.ProductoDao
import com.huertohogar.huertohogarmovil.data.dao.UsuarioDao
import com.huertohogar.huertohogarmovil.model.Producto
import com.huertohogar.huertohogarmovil.model.Usuario
import com.huertohogar.huertohogarmovil.model.CarritoItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Usuario::class, Producto::class, CarritoItem::class],
    version = 9, // Sube este número si necesitas forzar una recreación
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // DAOs abstractos que Room implementará
    abstract fun usuarioDao(): UsuarioDao
    abstract fun productoDao(): ProductoDao
    abstract fun carritoDao(): CarritoDao

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
                    .addCallback(RoomDatabaseCallback(context)) // Añadimos el callback
                    .fallbackToDestructiveMigration() // Borra la DB si la versión cambia
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * Callback para pre-poblar la base de datos la primera vez que se crea.
     * ¡Actualizado para no crear usuarios de prueba!
     */
    private class RoomDatabaseCallback(private val context: Context) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    prePopulateProductos(database.productoDao())
                }
            }
        }

        /**
         * Inserta la lista inicial de productos del huerto
         * (Usa los nombres de los archivos en 'drawable')
         */
        suspend fun prePopulateProductos(productoDao: ProductoDao) {
            val productosIniciales = listOf(
                Producto(
                    id = "1",
                    name = "Tomate Cherry (Bandeja 250g)",
                    description = "Pequeños tomates dulces...",
                    price = 2490,
                    imageName = "tomate_cherry"
                ),

                Producto(id = "2", name = "Lechuga Hidropónica Costina (Unidad)", description = "Lechuga fresca y crujiente...", price = 1390,
                    imageName = "lechuga"),

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
            productoDao.insertAll(productosIniciales)
        }
    }
}