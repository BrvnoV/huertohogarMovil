package com.huertohogar.huertohogarmovil.screens.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.huertohogar.huertohogarmovil.R
import com.huertohogar.huertohogarmovil.HuertoHogarApp
import com.huertohogar.huertohogarmovil.location.obtenerUbicacion
import com.huertohogar.huertohogarmovil.ui.viewModelFactory
import com.huertohogar.huertohogarmovil.ui.viewmodel.UbicacionViewModel
import com.huertohogar.huertohogarmovil.ui.viewmodel.DEFAULT_GEOPOINT
import com.huertohogar.huertohogarmovil.ui.viewmodel.ViewModelFactory
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

// --- CORRECCIONES DE TIPO DE DATO (DE FLOAT A DOUBLE) ---
private const val DEFAULT_ZOOM = 12.0 // De float a Double
private const val USER_LOCATION_ZOOM = 15.0 // De float a Double
// --- FIN DE CORRECCIONES ---

/**
 * Composable que maneja permisos y la inicialización de la ubicación.
 */
@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun MapRoute(
    onNavigateBack: () -> Unit,
    viewModel: UbicacionViewModel = viewModel(factory = viewModelFactory())
) {
    val contexto = LocalContext.current

    val permisoUbicacion = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val tienePermiso = permisoUbicacion.status is PermissionStatus.Granted

    if (tienePermiso) {
        LaunchedEffect(Unit) {
            obtenerUbicacion(contexto, viewModel)
        }
    }

    val currentGeoPoint = remember(viewModel.latitud, viewModel.longitud) {
        GeoPoint(viewModel.latitud, viewModel.longitud)
    }

    MapScreen(
        currentLocation = currentGeoPoint,
        hasPermission = tienePermiso,
        onRequestPermission = { permisoUbicacion.launchPermissionRequest() },
        onNavigateBack = onNavigateBack
    )
}

/**
 * Composable que utiliza AndroidView para renderizar el MapView nativo.
 */
@Composable
fun MapScreen(
    currentLocation: GeoPoint,
    hasPermission: Boolean,
    onRequestPermission: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    // Guardamos la referencia al MapView y su controlador
    val mapRef = remember { mutableStateOf<MapView?>(null) }
    val mapControllerRef = remember { mutableStateOf<IMapController?>(null) }
    val markerRef = remember { mutableStateOf<Marker?>(null) }

    // --- EFECTO: Mover el Mapa y el Marcador ---
    LaunchedEffect(currentLocation) {
        mapControllerRef.value?.let { controller ->
            // El zoom debe ser un Double
            controller.animateTo(currentLocation, USER_LOCATION_ZOOM, 1000L)

            // Actualizar o crear el marcador
            val map = mapRef.value
            if (map != null) {
                if (markerRef.value == null) {
                    val newMarker = Marker(map)
                    newMarker.position = currentLocation
                    newMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    newMarker.title = "Mi Ubicación"
                    map.overlays.add(newMarker)
                    markerRef.value = newMarker
                } else {
                    markerRef.value?.position = currentLocation
                }
                map.invalidate() // Forzar redibujado
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {

        // --- 1. EL MAPA USANDO ANDROIDVIEW ---
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { factoryContext ->
                // Configuración inicial de osmdroid
                Configuration.getInstance().load(
                    factoryContext,
                    factoryContext.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
                )

                // Creación del MapView nativo
                MapView(factoryContext).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)

                    // --- CORRECCIÓN: isVerticalMapRepositioningEnabled ---
                    // Esta propiedad fue eliminada en versiones recientes;
                    // simplemente se omite.
                    // --- FIN DE CORRECCIÓN ---

                    mapRef.value = this
                    mapControllerRef.value = controller

                    // --- CORRECCIÓN: DEFAULT_ZOOM ---
                    // Usamos la constante aquí directamente
                    controller.setZoom(DEFAULT_ZOOM)
                    controller.setCenter(currentLocation)
                    // --- FIN DE CORRECCIÓN ---
                }
            },
            update = { map ->
                // Lógica de actualización (mantener el mapa centrado inicialmente)
                if (currentLocation == DEFAULT_GEOPOINT) {
                    mapControllerRef.value?.setCenter(DEFAULT_GEOPOINT)
                }
            }
        )
        // --- FIN DE ANDROIDVIEW ---

        // --- 2. PANTALLA DE PERMISOS (sin cambios) ---
        if (!hasPermission) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Black.copy(alpha = 0.7f)
            ) {}
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.intro_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(100.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Permiso de Ubicación Requerido",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    "Necesitamos tu GPS para centrar el mapa.",
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
                Button(onClick = onRequestPermission) {
                    Text("Dar Permiso")
                }
            }
        }
    }
}
