package com.huertohogar.huertohogarmovil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
// import androidx.lifecycle.viewmodel.compose.viewModel
// import coil.compose.AsyncImage // Para cargar imágenes
// import com.huertohogar.products.ProductsViewModel
// import com.huertohogar.products.ProductsState
// import com.huertohogar.products.ProductEvent
// import com.huertohogar.data.Product

// Clases de datos actualizadas con los productos reales
data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Int, // Cambiado a Int para precios como $2.490
    val imageUrl: String
)

data class ProductsState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val error: String? = null
)

@Composable
fun ProductsRoute(
    onNavigateToProductDetails: (productId: String) -> Unit,
    // viewModel: ProductsViewModel = viewModel()
) {
    // val uiState by viewModel.uiState.collectAsState()

    // --- DATOS ACTUALIZADOS ---
    // Estado de ejemplo para el prototipo, con datos reales
    val realProducts = listOf(
        Product(
            id = "1",
            name = "Tomate Cherry (Bandeja 250g)",
            description = "Pequeños tomates dulces y jugosos, cultivados localmente. Perfectos para ensaladas frescas, snacks o para asar.",
            price = 2490,
            imageUrl = "https://i.imgur.com/example.png" // Placeholder
        ),
        Product(
            id = "2",
            name = "Lechuga Hidropónica Costina (Unidad)",
            description = "Lechuga fresca y crujiente cultivada sin pesticidas. Hojas firmes ideales para sándwiches y ensaladas César.",
            price = 1390,
            imageUrl = "https://i.imgur.com/example.png" // Placeholder
        ),
        Product(
            id = "3",
            name = "Frutillas Orgánicas (Bandeja 500g)",
            description = "Selección de frutillas maduradas al sol, libres de químicos. Sabor intenso y dulce garantizado.",
            price = 3990,
            imageUrl = "https://i.imgur.com/example.png" // Placeholder
        ),
        Product(
            id = "4",
            name = "Planta de Albahaca Viva (Maceta)",
            description = "¡Cosecha tus propias hojas! Planta de albahaca genovesa lista para mantener en tu cocina y usarla fresca en pestos y salsas.",
            price = 2990,
            imageUrl = "https://i.imgur.com/example.png" // Placeholder
        ),
        Product(
            id = "5",
            name = "Zanahorias (Manojo 500g)",
            description = "Manojo de zanahorias tiernas y dulces, recién cosechadas. Ideales para jugos, guisos o para comer crudas.",
            price = 1290,
            imageUrl = "https://i.imgur.com/example.png" // Placeholder
        ),
        Product(
            id = "6",
            name = "Arándanos Premium (Bandeja 125g)",
            description = "Arándanos grandes y firmes, seleccionados por su alto contenido de antioxidantes. El topping perfecto para tu yogurt o avena.",
            price = 2190,
            imageUrl = "https://i.imgur.com/example.png" // Placeholder
        ),
        Product(
            id = "7",
            name = "Pimentón Rojo (Unidad)",
            description = "Pimentón rojo carnoso y de sabor dulce. Aporta un color vibrante y un toque ahumado a tus sofritos y asados.",
            price = 1190,
            imageUrl = "https://i.imgur.com/example.png" // Placeholder
        ),
        Product(
            id = "8",
            name = "Palta Hass (Malla 1kg)",
            description = "Paltas (aguacates) Hass de primera calidad, en su punto óptimo de maduración. Cremosas e ideales para tostadas.",
            price = 5990,
            imageUrl = "https://i.imgur.com/example.png" // Placeholder
        ),
        Product(
            id = "9",
            name = "Limón Sutil (Malla 1kg)",
            description = "Limones sutiles (tipo lima) muy jugosos y aromáticos. Esenciales para aderezos, ceviches y pisco sour.",
            price = 2290,
            imageUrl = "https://i.imgur.com/example.png" // Placeholder
        ),
        Product(
            id = "10",
            name = "Zapallo Italiano (Zucchini)",
            description = "Zucchini tierno y versátil. Perfecto para saltear, hacer cremas o como alternativa baja en carbohidratos para pastas.",
            price = 990,
            imageUrl = "https://i.imgur.com/example.png" // Placeholder
        )
    )

    val uiState = ProductsState(
        isLoading = false,
        products = realProducts
    )
    // --- FIN DE DATOS ACTUALIZADOS ---

    ProductsScreen(
        state = uiState,
        onProductClick = { onNavigateToProductDetails(it.id) },
        onAddToCartClick = { /* viewModel.onEvent(ProductEvent.AddToCart(it)) */ }
    )
}

@Composable
fun ProductsScreen(
    state: ProductsState,
    onProductClick: (Product) -> Unit,
    onAddToCartClick: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        if (state.error != null) {
            Text(
                text = state.error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state.products, key = { it.id }) { product ->
                ProductCard(
                    product = product,
                    onProductClick = { onProductClick(product) },
                    onAddToCartClick = { onAddToCartClick(product) }
                )
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onProductClick: () -> Unit,
    onAddToCartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onProductClick,
        modifier = modifier
    ) {
        Column {
            // AsyncImage(
            //     model = product.imageUrl,
            //     contentDescription = product.name,
            //     modifier = Modifier
            //         .fillMaxWidth()
            //         .aspectRatio(1f),
            //     contentScale = ContentScale.Crop
            // )
            Box( // Placeholder para la imagen
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            ){
                Text("Imagen", modifier = Modifier.align(Alignment.Center))
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = product.name, style = MaterialTheme.typography.titleMedium, maxLines = 2)
                Text(
                    text = "$${product.price}", // Se mostrará como $2490, $1390, etc.
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        onAddToCartClick()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Añadir")
                }
            }
        }
    }
}