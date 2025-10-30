package com.huertohogar.huertohogarmovil

import android.app.Application
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.SessionManager
import com.huertohogar.data.AppDatabase
import com.huertohogar.huertohogarmovil.data.repository.AppRepository
import com.huertohogar.huertohogarmovil.data.repository.AppRepositoryImpl

class HuertoHogarApp : Application() {

    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }

    // Solución: Obtener CastContext y luego el SessionManager
    private val castContext: CastContext by lazy {
        CastContext.getSharedInstance(this)
    }

    val sessionManager: SessionManager by lazy {
        castContext.sessionManager
    }

    val repository: AppRepository by lazy {
        AppRepositoryImpl(
            usuarioDao = database.usuarioDao(),
            productoDao = database.productoDao(),
            carritoDao = database.carritoDao()
        )
    }
}
