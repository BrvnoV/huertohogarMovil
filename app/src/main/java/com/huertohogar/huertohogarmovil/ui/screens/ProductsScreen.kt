package com.huertohogar.huertohogarmovil.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.huertohogar.huertohogarmovil.HuertoHogarApp
import com.huertohogar.huertohogarmovil.ui.viewModelFactory

import com.huertohogar.huertohogarmovil.ui.viewmodel.ProductsEvent
import com.huertohogar.huertohogarmovil.ui.viewmodel.ProductsUiEvent
import com.huertohogar.huertohogarmovil.ui.viewmodel.ProductsViewModel
import com.huertohogar.huertohogarmovil.ui.viewmodel.ViewModelFactory
import com.huertohogar.model.Producto
import kotlinx.coroutines.flow.collectLatest

/**
 * Composable "inteligente" (stateful).
 * Se conecta al ViewModel para obtener la lógica y el estado.
 */
@Composable
fun ProductsRoute(
    onNavigateToProductDetails: (productId: String) -> Unit,
    // Conectamos el ViewModel usando nuestra Factory
    factory: ViewModelFactory = viewModelFactory(),
    viewModel: ProductsViewModel = viewModel(factory = factory)
) {
    // Obtenemos los productos del ViewModel
    // Usamos collectAsState con un valor inicial de lista vacía
    val products by viewModel.products.collectAsState(initial = emptyList())

    // Estado para el Snackbar (mensajes emergentes)
    val snackbarHostState = remember { SnackbarHostState() }

    // Escuchamos por eventos de la UI (ej: mostrar Snackbar)
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is ProductsUiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    ProductsScreen(
        products = products,
        snackbarHostState = snackbarHostState,
        onProductClick = { onNavigateToProductDetails(it.id) },
        onAddToCartClick = {
            viewModel.onEvent(ProductsEvent.OnAddToCartClick(it))
        }
    )
}

/**
 * Composable "tonto" (stateless).
 * Solo muestra la UI basada en el estado que recibe.
 */
@Composable
fun ProductsScreen(
    products: List<Producto>,
    snackbarHostState: SnackbarHostState,
    onProductClick: (Producto) -> Unit,
    onAddToCartClick: (Producto) -> Unit,
    modifier: Modifier = Modifier
) {
    // Usamos Scaffold para poder mostrar el Snackbar
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues) // Padding del Scaffold
        ) {

            if (products.isEmpty()) {
                // Muestra un indicador de carga mientras los productos
                // se leen de la base de datos
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            // Cuadrícula de productos
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // Siempre 2 columnas
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(products, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        onProductClick = { onProductClick(product) },
                        onAddToCartClick = { onAddToCartClick(product) }
                    )
                }
            }
        }
    }
}

/**
 * Tarjeta individual para cada producto en la cuadrícula.
 */
@Composable
fun ProductCard(
    product: Producto,
    onProductClick: () -> Unit,
    onAddToCartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onProductClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp), // Sombra suave
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface // Color Crema
        )
    ) {
        Column {
            // Placeholder para la imagen del producto
            // (En un futuro, aquí usarías AsyncImage de Coil para cargar product.imageUrl)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f) // Imagen cuadrada
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ){
                Text("IMG", style = MaterialTheme.typography.titleMedium)
            }

            // Detalles del producto
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    minLines = 2, // Fija la altura a 2 líneas
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${product.price}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary // Color Verde Oscuro
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onAddToCartClick, // Pasa el evento hacia arriba
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        // Usamos el color secundario (Verde Medio)
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Añadir",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondary // Texto Blanco
                    )
                }
            }
        }
    }
}

