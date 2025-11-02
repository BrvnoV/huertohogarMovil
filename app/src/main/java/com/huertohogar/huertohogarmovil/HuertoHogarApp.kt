package com.huertohogar.huertohogarmovil

import android.app.Application
import com.google.android.gms.location.LocationServices
import com.huertohogar.huertohogarmovil.data.repository.AppRepository
import com.huertohogar.huertohogarmovil.data.repository.AppRepositoryImpl
import com.huertohogar.huertohogarmovil.repository.AppDatabase
import com.huertohogar.huertohogarmovil.repository.SessionManager

// --- IMPORTS ELIMINADOS DE LOCATION/FUSEDLOCATIONCLIENT ---
// Estos ya no son necesarios porque la función obtenerUbicacion los maneja estáticamente.
// import com.huertohogar.huertohogarmovil.location.LocationService
// import com.huertohogar.huertohogarmovil.location.LocationServiceImpl
// import com.google.android.gms.location.FusedLocationProviderClient
// --- FIN DE ELIMINACIÓN DE IMPORTS ---

class HuertoHogarApp : Application() {

    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }

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
