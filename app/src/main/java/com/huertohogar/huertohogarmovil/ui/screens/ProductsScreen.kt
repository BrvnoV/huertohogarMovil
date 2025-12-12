package com.huertohogar.huertohogarmovil.screens.products

// Imports necesarios para la UI, estado y ViewModel
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.huertohogar.huertohogarmovil.R
import com.huertohogar.huertohogarmovil.model.Producto
import com.huertohogar.huertohogarmovil.ui.viewModelFactory
import com.huertohogar.huertohogarmovil.ui.viewmodel.ProductsEvent
import com.huertohogar.huertohogarmovil.ui.viewmodel.ProductsUiEvent
import com.huertohogar.huertohogarmovil.ui.viewmodel.ProductsViewModel
import com.huertohogar.huertohogarmovil.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProductsRoute(
        onNavigateToProductDetails: (productId: String) -> Unit,
        factory: ViewModelFactory = viewModelFactory(),
        viewModel: ProductsViewModel = viewModel(factory = factory)
) {
    val products by viewModel.products.collectAsState(initial = emptyList())
    val snackbarHostState = remember { SnackbarHostState() }

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
            onAddToCartClick = { viewModel.onEvent(ProductsEvent.OnAddToCartClick(it)) }
    )
}

@Composable
fun ProductsScreen(
        products: List<Producto>,
        snackbarHostState: SnackbarHostState,
        onProductClick: (Producto) -> Unit,
        onAddToCartClick: (Producto) -> Unit,
        modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
                painter = painterResource(id = R.drawable.login_background),
                contentDescription = "Fondo de la aplicación",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
        )

        Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
        ) {}

        Scaffold(
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                containerColor = Color.Transparent
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                if (products.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
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
}

@Composable
fun ProductCard(
        product: Producto,
        onProductClick: () -> Unit,
        onAddToCartClick: () -> Unit,
        modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sanitizedImageName = product.imageName.toLowerCase()
    val imageResId =
            context.resources.getIdentifier(sanitizedImageName, "drawable", context.packageName)

    Card(
            onClick = onProductClick,
            modifier = modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp),
            colors =
                    CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                    )
    ) {
        Column {
            if (imageResId != 0) {
                Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().aspectRatio(1f)
                )
            } else {
                AsyncImage(
                        model =
                                "https://ui-avatars.com/api/?name=${product.name}&background=random&size=200",
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                        placeholder = painterResource(R.drawable.ic_launcher_background),
                        error = painterResource(R.drawable.ic_launcher_background)
                )
            }

            Column(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        minLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                        text = "$${product.price}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                        onClick = onAddToCartClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                                ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondary
                                ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                            text = "Añadir",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }
    }
}
