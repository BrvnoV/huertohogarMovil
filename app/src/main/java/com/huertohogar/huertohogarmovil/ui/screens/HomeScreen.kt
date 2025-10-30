package com.huertohogar.huertohogarmovil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
// import androidx.lifecycle.viewmodel.compose.viewModel
// import com.huertohogar.home.HomeViewModel
// import com.huertohogar.home.HomeState

@Composable
fun HomeRoute(
    onNavigateToProductDetails: (productId: String) -> Unit,
    // viewModel: HomeViewModel = viewModel()
) {
    // val uiState by viewModel.uiState.collectAsState()

    // Estado de ejemplo para el prototipo
    val uiState = HomeState(
        userName = "Usuario",
        featuredProducts = listOf("Producto Estrella 1", "Producto Estrella 2")
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
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "¡Hola, ${state.userName}!",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Bienvenido a tu huerto en casa. Descubre nuestros productos frescos.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Productos Destacados",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(state.featuredProducts.size) { index ->
            val product = state.featuredProducts[index]
            Card(
                onClick = { onProductClick(product) }, // Usamos el nombre como ID de momento
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Aquí iría una imagen (ej. AsyncImage)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = product, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}