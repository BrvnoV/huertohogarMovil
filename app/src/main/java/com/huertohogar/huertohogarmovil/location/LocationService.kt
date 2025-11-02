package com.huertohogar.huertohogarmovil.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.huertohogar.huertohogarmovil.ui.viewmodel.UbicacionViewModel

/**
 * Función que encapsula la lógica para obtener la última ubicación conocida
 * y actualizar el ViewModel.
 *
 * NOTA: Esta función es un reemplazo directo de la función de tu clase.
 *
 * @param contexto Contexto de la aplicación.
 * @param viewModel El UbicacionViewModel para actualizar las coordenadas.
 */
@SuppressLint("MissingPermission")
fun obtenerUbicacion(contexto: Context, viewModel: UbicacionViewModel) {
    val proveedorUbicacion = LocationServices.getFusedLocationProviderClient(contexto)

    // Solicitamos la última ubicación conocida
    proveedorUbicacion.lastLocation.addOnSuccessListener { ubicacion ->
        if (ubicacion != null) {
            // Si la ubicación es válida, actualizamos el ViewModel
            viewModel.actualizarUbicacion(ubicacion.latitude, ubicacion.longitude)
        } else {
            // Si no se encuentra la última ubicación, podríamos intentar una más fresca,
            // pero para este ejemplo, simplemente usamos el default.
            // (La lógica del ViewModel ya maneja el default)
        }
    }
}
