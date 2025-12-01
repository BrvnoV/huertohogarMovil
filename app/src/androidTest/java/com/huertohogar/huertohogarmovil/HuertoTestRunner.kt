package com.huertohogar.huertohogarmovil

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.huertohogar.huertohogarmovil.HuertohogarMovilApp


/**
 * Custom Test Runner para asegurar que la clase HuertoHogarApp se cargue correctamente
 * durante las pruebas de instrumentación.
 */
class HuertoTestRunner : AndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        // Forzamos al sistema de pruebas a cargar HuertoHogarApp al inicio.
        return super.newApplication(cl, HuertohogarMovilApp::class.java.name, context)
    }
}