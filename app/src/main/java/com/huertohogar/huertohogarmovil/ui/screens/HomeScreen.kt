package com.huertohogar.huertohogarmovil.screens.home

// --- ¡IMPORTS AÑADIDOS! ---
import androidx.compose.foundation.Image
import androidx.compose.foundation.background // <-- Posiblemente necesario para el color de superficie
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.huertohogar.huertohogarmovil.HuertoHogarApp
import com.huertohogar.huertohogarmovil.R
import com.huertohogar.huertohogarmovil.ui.viewModelFactory
import com.huertohogar.huertohogarmovil.ui.viewmodel.HomeState
import com.huertohogar.huertohogarmovil.ui.viewmodel.HomeViewModel
import com.huertohogar.huertohogarmovil.ui.viewmodel.ViewModelFactory
import com.huertohogar.huertohogarmovil.model.Producto

// --- FIN DE IMPORTS ---

@Composable
fun HomeRoute(
    onNavigateToProductDetails: (productId: String) -> Unit,
    factory: ViewModelFactory = viewModelFactory(),
    viewModel: HomeViewModel = viewModel(factory = factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeScreen(
        state = uiState,
        onProductClick = { onNavigateToProductDetails(it) }
    )
}

@Composable
fun HomeScreen(
    state: HomeState,
    onProductClick: (productId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    // --- ¡AQUÍ ESTÁ EL CAMBIO PARA EL FONDO! ---
    Box(modifier = modifier.fillMaxSize()) {
        // Imagen de fondo (ajusta el nombre del drawable si es diferente)
        Image(
            painter = painterResource(id = R.drawable.login_background), // O R.drawable.app_background
            contentDescription = "Fondo de la aplicación",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Opcional: una capa oscura o de color para mejorar la legibilidad del texto
        Surface(
            modifier = Modifier.fillMaxSize(),
            // Puedes ajustar el alpha para controlar la opacidad
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
        ) {}
        // --- FIN DEL FONDO ---

        // El LazyColumn va ENCIMA del fondo
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // --- Cabecera de bienvenida (ajusta color de fondo si es necesario) ---
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        // Ajustamos el color de la tarjeta para que también sea semi-transparente
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    ),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "¡Bienvenid@ ${state.userName}!",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = "Bienvenido a tu huerto en casa.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            // --- Barra de Búsqueda (ajusta color de fondo si es necesario) ---
            item {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Buscar productos...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        // Para que el fondo del TextField también sea semi-transparente
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), MaterialTheme.shapes.medium)
                )
            }

            // --- Categorías (ajusta color de fondo si es necesario) ---
            item {
                Text(
                    text = "Categorías",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                    color = MaterialTheme.colorScheme.onSurface // Asegura que el texto sea visible
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
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface // Asegura que el texto sea visible
                )
            }

            if (state.isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                items(state.featuredProducts, key = { it.id }) { product ->
                    ProductHomeCard(
                        product = product,
                        onProductClick = { onProductClick(product.id) }
                    )
                }
            }
        }
    }
}

/**
 * Un Chip decorativo para las categorías (ajusta el color de fondo).
 */
@Composable
fun CategoryChip(text: String) {
    ElevatedAssistChip(
        onClick = { /* TODO: Navegar a categoría */ },
        label = { Text(text, fontWeight = FontWeight.SemiBold) },
        colors = AssistChipDefaults.elevatedAssistChipColors(
            containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f) // Semi-transparente
        )
    )
}

/**
 * Una Card de producto para la Home, ¡CORREGIDA con transparencia!
 */
@Composable
fun ProductHomeCard(
    product: Producto,
    onProductClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageResId = context.resources.getIdentifier(
        product.imageName,
        "drawable",
        context.packageName
    )

    Card(
        onClick = onProductClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            // Hacemos el fondo de la Card semi-transparente
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = if (imageResId != 0) imageResId else R.drawable.ic_launcher_background),
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(MaterialTheme.shapes.small)
            )

            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface // Asegura visibilidad del texto
                )
                Text(
                    text = "$${product.price}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

