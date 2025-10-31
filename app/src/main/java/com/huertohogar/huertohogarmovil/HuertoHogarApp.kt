package com.huertohogar.huertohogarmovil


import android.app.Application
import com.huertohogar.huertohogarmovil.data.repository.AppRepository
import com.huertohogar.huertohogarmovil.data.repository.AppRepositoryImpl
import com.huertohogar.huertohogarmovil.repository.AppDatabase
import com.huertohogar.huertohogarmovil.repository.SessionManager


class HuertoHogarApp : Application() {

    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }

    // El tipo explícito ': SessionManager' ahora se refiere
    // al importado de ...huertohogar.repository.SessionManager
    val sessionManager: SessionManager by lazy {
        SessionManager(this)
    }

    val repository: AppRepository by lazy {
        AppRepositoryImpl(
            usuarioDao = database.usuarioDao(),
            productoDao = database.productoDao(),
            carritoDao = database.carritoDao()
        )
    }
}