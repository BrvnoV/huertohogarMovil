package com.huertohogar.huertohogarmovil


import android.app.Application
import com.google.android.gms.cast.framework.SessionManager
import com.huertohogar.data.AppDatabase
import com.huertohogar.huertohogarmovil.data.repository.AppRepository
import com.huertohogar.huertohogarmovil.data.repository.AppRepositoryImpl
import kotlin.getValue



// <-- BORRA TODOS LOS IMPORTS MENOS EL DE ARRIBA, LUEGO USA ALT+ENTER ABAJO

class HuertoHogarApp : Application() {

    // Al poner el tipo explícito ': AppDatabase', te pedirá importar 'AppDatabase'
    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }

    // Al poner el tipo explícito ': SessionManager', te pedirá importar 'SessionManager'
    val sessionManager: SessionManager by lazy {
        SessionManager(this)
    }

    // Al poner el tipo explícito ': AppRepository', te pedirá importar 'AppRepository'
    // Y al usar 'AppRepositoryImpl', te pedirá importar 'AppRepositoryImpl'
    val repository: AppRepository by lazy {
        AppRepositoryImpl(
            usuarioDao = database.usuarioDao(),
            productoDao = database.productoDao(),
            carritoDao = database.carritoDao()
        )
    }
}