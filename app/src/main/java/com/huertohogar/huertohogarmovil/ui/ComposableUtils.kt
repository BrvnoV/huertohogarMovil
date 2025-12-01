package com.huertohogar.huertohogarmovil.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.huertohogar.huertohogarmovil.HuertohogarMovilApp // <-- ¡ESTA ES LA LÍNEA CRUCIAL!
import com.huertohogar.huertohogarmovil.ui.viewmodel.ViewModelFactory

/**
 * Función de utilidad para obtener la ViewModelFactory desde un Composable.
 */
@Composable
fun viewModelFactory(): ViewModelFactory {
    // Aquí es donde se usa la referencia HuertoHogarApp
    val application = (LocalContext.current.applicationContext as HuertohogarMovilApp)
    return ViewModelFactory(application.repository, application.sessionManager)
}