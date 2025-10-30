package com.huertohogar.huertohogarmovil.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
// import androidx.lifecycle.viewmodel.compose.viewModel
// import com.huertohogar.home.HomeViewModel

@Composable
fun HomeRoute(
    onNavigateToProductDetails: (productId: String) -> Unit,
    // viewModel: HomeViewModel = viewModel()
) {
    // val uiState by viewModel.uiState.collectAsState()

    // Estado de ejemplo para el prototipo
    val uiState = HomeState(
        userName = "Usuario",
        featuredProducts = listOf("Tomate Cherry", "Lechuga", "Albahaca")
    )

    HomeScreen(
        state = uiState,
        onProductClick = { onNavigateToProductDetails(it) }
    )
}

// Data class de ejemplo para el estado
data class HomeState(val userName: String, val featuredProducts: List<String>)

@Composable
fun HomeScreen(
    state: HomeState,
    onProductClick: (productId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 16.dp) // Padding para que no choque con la bottom nav
    ) {
        // --- Cabecera de bienvenida ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "¡Hola, ${state.userName}!",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "Bienvenido a tu huerto en casa.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // --- Barra de Búsqueda (Falsa) ---
        item {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Buscar productos...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // --- Categorías (Falsas) ---
        item {
            Text(
                text = "Categorías",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(listOf("Frutas", "Verduras", "Hierbas", "Ofertas")) { categoria ->
                    CategoryChip(text = categoria)
                }
            }
        }

        // --- Productos Destacados ---
        item {
            Text(
                text = "Productos Destacados",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
        }

        items(state.featuredProducts.size) { index ->
            val product = state.featuredProducts[index]
            Card(
                onClick = { onProductClick(product) }, // Usamos el nombre como ID de momento
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Aquí iría una imagen real
                    Box(
                        modifier = Modifier.size(64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("IMG")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = product, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun CategoryChip(text: String) {
    ElevatedAssistChip(
        onClick = { /* TODO */ },
        label = { Text(text, fontWeight = FontWeight.SemiBold) }
    )
}