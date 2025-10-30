package com.huertohogar.huertohogarmovil.ui.screens

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.huertohogar.huertohogarmovil.data.MyApplication
import com.huertohogar.huertohogarmovil.ui.viewmodel.ProfileViewModel
import com.huertohogar.huertohogarmovil.ui.viewmodel.ProfileViewModelFactory

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ProfileScreen(
    // ¡AQUÍ ESTÁ EL USERID! Lo recibe desde MainScreenContainer
    userId: String,
    // Obtenemos el ViewModel usando la Factory para pasar el Repositorio
    viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(
            (LocalContext.current.applicationContext as MyApplication).appRepository
        )
    )
) {
    val context = LocalContext.current
    val userState by viewModel.userState.collectAsState()
    val locationState by viewModel.locationState.collectAsState()

    // --- Permisos ---
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            // Permiso de Galería (ajustado para SDKs)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
        )
    )

    // --- Lanzador de Galería ---
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Cuando el usuario selecciona una imagen, actualizamos el ViewModel
            viewModel.updateProfileImage(it.toString())
        }
    }

    // --- Efecto de Carga Inicial ---
    LaunchedEffect(userId) {
        // Carga los datos del usuario al entrar a la pantalla
        viewModel.loadUser(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text("Mi Perfil", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        // --- Carga de UI ---
        when (val state = userState) {
            is ProfileViewModel.UserState.Loading -> {
                CircularProgressIndicator()
            }
            is ProfileViewModel.UserState.Success -> {
                val user = state.user

                // --- Sección de Foto de Perfil ---
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .clickable {
                            // Pedir permisos y luego abrir galería
                            permissionState.launchMultiplePermissionRequest()
                            if (permissionState.allPermissionsGranted) {
                                galleryLauncher.launch("image/*")
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(context)
                                .data(user.profilePictureUri ?: "https://placehold.co/400x400/A8D5BA/333333?text=${user.username.firstOrNull()?.uppercaseChar() ?: 'U'}") // Placeholder
                                .crossfade(true)
                                .build()
                        ),
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    user.username,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "ID: ${user.id}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(32.dp))

                // --- Sección de Ubicación (GPS) ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Mi Ubicación",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Contenido del estado de ubicación
                        when (val locState = locationState) {
                            is ProfileViewModel.LocationState.Loading -> {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                    Text("Obteniendo ubicación...", modifier = Modifier.padding(start = 8.dp))
                                }
                            }
                            is ProfileViewModel.LocationState.Success -> {
                                Text("Latitud: ${locState.location.latitude}")
                                Text("Longitud: ${locState.location.longitude}")
                            }
                            is ProfileViewModel.LocationState.Error -> {
                                Text(locState.message, color = MaterialTheme.colorScheme.error)
                            }
                            is ProfileViewModel.LocationState.Idle -> {
                                // Muestra la última ubicación guardada si existe
                                if (user.lastLatitude != 0.0 && user.lastLongitude != 0.0) {
                                    Text("Última guardada:")
                                    Text("Latitud: ${user.lastLatitude}")
                                    Text("Longitud: ${user.lastLongitude}")
                                } else {
                                    Text("No se ha guardado ninguna ubicación.")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                // Pedir permisos de GPS antes de solicitar ubicación
                                permissionState.launchMultiplePermissionRequest()
                                if (permissionState.permissions.any { it.permission.startsWith("android.permission.ACCESS_") && it.hasPermission }) {
                                    viewModel.fetchAndUpdateLocation()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Actualizar Ubicación GPS")
                        }
                    }
                }
            }
            is ProfileViewModel.UserState.Error -> {
                Text(state.message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

