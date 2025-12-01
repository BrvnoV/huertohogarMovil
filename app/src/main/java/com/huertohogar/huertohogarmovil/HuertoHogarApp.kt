package com.huertohogar.huertohogarmovil

import android.app.Application
// --- IMPORTS AÑADIDOS PARA RETROFIT E INYECCIÓN ---
import com.huertohogar.huertohogarmovil.network.HuertoApiService
import com.huertohogar.huertohogarmovil.network.model.HuertoMapper
import com.huertohogar.huertohogarmovil.repository.AppRepository
import com.huertohogar.huertohogarmovil.repository.AppRepositoryImpl
import com.huertohogar.huertohogarmovil.repository.AppDatabase
import com.huertohogar.huertohogarmovil.repository.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
// --- FIN DE IMPORTS ---

class HuertohogarMovilApp : Application() {

    // URL de Fruityvice
    private val BASE_URL = "https://www.fruityvice.com/"

    // --- 1. CONFIGURACIÓN DEL CLIENTE HTTP (Singleton) ---
    private val client by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    // --- 2. INICIALIZACIÓN DE RETROFIT ---
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 3. Creación del API Service
    val apiService: HuertoApiService by lazy {
        retrofit.create(HuertoApiService::class.java)
    }

    // 4. Creación del Mapper (Objeto Singleton)
    private val huertoMapper: HuertoMapper = HuertoMapper

    // --- DEPENDENCIAS EXISTENTES (ROOM y Sesión) ---
    private val database by lazy { AppDatabase.getInstance(this) }

    val sessionManager: SessionManager by lazy { SessionManager(this) }

    // --- 5. INYECCIÓN FINAL EN EL REPOSITORY ---
    val repository: AppRepository by lazy {
        AppRepositoryImpl(
            usuarioDao = database.usuarioDao(),
            productoDao = database.productoDao(),
            carritoDao = database.carritoDao(),
            apiService = apiService, // <-- Inyección de dependencia REST
            huertoMapper = huertoMapper // <-- Inyección de dependencia Mapeador
        )
    }
}