package com.huertohogar.huertohogarmovil.screens.cart

import android.widget.Toast
import androidx.compose.foundation.Image // <-- Importado
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color // <-- Importado
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource // <-- Importado
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.huertohogar.huertohogarmovil.HuertoHogarApp
import com.huertohogar.huertohogarmovil.R //
import com.huertohogar.huertohogarmovil.ui.viewmodel.CartEvent
import com.huertohogar.huertohogarmovil.ui.viewmodel.CartState
import com.huertohogar.huertohogarmovil.ui.viewmodel.CartUiEvent
import com.huertohogar.huertohogarmovil.ui.viewmodel.CartViewModel
import com.huertohogar.huertohogarmovil.ui.viewmodel.ViewModelFactory
import com.huertohogar.model.CarritoItemConDetalles
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CartRoute(
    onNavigateToCheckout: () -> Unit,
    factory: ViewModelFactory = viewModelFactory(),
    viewModel: CartViewModel = viewModel(factory = factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is CartUiEvent.NavigateToCheckout -> {
                    Toast.makeText(context, "¡Compra realizada!", Toast.LENGTH_LONG).show()
                    onNavigateToCheckout()
                }
                is CartUiEvent.ShowSnackbar -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    CartScreen(
        state = uiState,
        onRemoveItem = { itemDetalles ->
            viewModel.onEvent(CartEvent.OnRemoveItemClick(itemDetalles))
        },
        onIncreaseQuantity = { itemDetalles ->
            viewModel.onEvent(CartEvent.OnIncreaseQuantity(itemDetalles))
        },
        onDecreaseQuantity = { itemDetalles ->
            viewModel.onEvent(CartEvent.OnDecreaseQuantity(itemDetalles))
        },
        onCheckoutClick = {
            viewModel.onEvent(CartEvent.OnCheckoutClick)
        }
    )
}

@Composable
fun CartScreen(
    state: CartState,
    onRemoveItem: (CarritoItemConDetalles) -> Unit,
    onIncreaseQuantity: (CarritoItemConDetalles) -> Unit,
    onDecreaseQuantity: (CarritoItemConDetalles) -> Unit,
    onCheckoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // --- ¡AQUÍ ESTÁ EL CAMBIO PARA EL FONDO! ---
    Box(modifier = modifier.fillMaxSize()) {
        // 1. Imagen de fondo
        Image(
            painter = painterResource(id = R.drawable.login_background), // O R.drawable.app_background
            contentDescription = "Fondo de la aplicación",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Capa de superficie semi-transparente
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
        ) {}
        // --- FIN DEL FONDO ---

        // 3. Contenido de la pantalla (el Column)
        Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.cartItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Tu carrito está vacío",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface // Asegura texto visible
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.cartItems, key = { it.carritoItem.id }) { item ->
                        CartItemRow(
                            item = item,
                            onRemove = { onRemoveItem(item) },
                            onIncrease = { onIncreaseQuantity(item) },
                            onDecrease = { onDecreaseQuantity(item) }
                        )
                    }
                }
                CartSummary(
                    totalPrice = state.totalPrice,
                    onCheckoutClick = onCheckoutClick
                )
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CarritoItemConDetalles,
    onRemove: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageResId = context.resources.getIdentifier(
        item.producto.imageName,
        "drawable",
        context.packageName
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            // Hacemos el fondo de la Card semi-transparente
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        )
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = if (imageResId != 0) imageResId else R.drawable.ic_launcher_background),
                contentDescription = item.producto.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(MaterialTheme.shapes.small)
            )

            Column(modifier = Modifier.weight(1.0f).padding(horizontal = 8.dp)) {
                Text(
                    item.producto.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurface // Asegura texto visible
                )
                Text(
                    "$${item.producto.price}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface // Asegura texto visible
                )
            }

            // Controles de cantidad
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDecrease) {
                    Icon(Icons.Default.Remove, contentDescription = "Quitar uno")
                }
                Text(
                    "${item.carritoItem.quantity}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface // Asegura texto visible
                )
                IconButton(onClick = onIncrease) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir uno")
                }
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar item")
                }
            }
        }
    }
}

@Composable
fun CartSummary(
    totalPrice: Int,
    onCheckoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            // Hacemos el fondo de la Card semi-transparente
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Total:",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface // Asegura texto visible
                )
                Text(
                    "$${String.format("%d", totalPrice)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onCheckoutClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Text("Proceder al Pago")
            }
        }
    }
}

// --- FUNCIÓN AYUDANTE (HELPER) ---
@Composable
fun viewModelFactory(): ViewModelFactory {
    val application = (LocalContext.current.applicationContext as HuertoHogarApp)
    return ViewModelFactory(application.repository, application.sessionManager)
}