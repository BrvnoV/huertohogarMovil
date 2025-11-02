package com.huertohogar.huertohogarmovil.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import org.osmdroid.util.GeoPoint

/**
 * Coordenadas por defecto (Santiago, Chile)
 */
private val DEFAULT_LATLNG = LatLng(-33.4489, -70.6693)
val DEFAULT_GEOPOINT = GeoPoint(-33.4489, -70.6693) // Para el mapa de OSM

/**
 * ViewModel basado en tu lógica de clase, pero modificado para ser un Singleton.
 * Usa un StateHolder simple (var by mutableStateOf) para las coordenadas.
 */
class UbicacionViewModel : ViewModel() {

    // Estado observable de las coordenadas (el valor inicial es el de Santiago)
    var latitud by mutableStateOf<Double>(DEFAULT_LATLNG.latitude)
        private set
    var longitud by mutableStateOf<Double>(DEFAULT_LATLNG.longitude)
        private set

    /**
     * Actualiza las coordenadas y dispara la recompocisión de la UI (el mapa).
     */
    fun actualizarUbicacion(lat: Double, lon: Double) {
        latitud = lat
        longitud = lon
    }
}
