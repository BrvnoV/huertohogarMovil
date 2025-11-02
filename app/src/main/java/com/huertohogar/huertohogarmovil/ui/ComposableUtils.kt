package com.huertohogar.huertohogarmovil.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.huertohogar.huertohogarmovil.HuertoHogarApp
import com.huertohogar.huertohogarmovil.ui.viewmodel.ViewModelFactory

/**
 * Función de utilidad para obtener la ViewModelFactory desde un Composable.
 */
@Composable
fun viewModelFactory(): ViewModelFactory {
    val application = (LocalContext.current.applicationContext as HuertoHogarApp)
    // El ViewModelFactory ahora debe aceptar solo 2 parámetros
    return ViewModelFactory(application.repository, application.sessionManager)
}



