package com.huertohogar.huertohogarmovil.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
// import androidx.lifecycle.viewmodel.compose.viewModel
// import com.huertohogar.cart.CartViewModel
// import com.huertohogar.cart.CartState
// import com.huertohogar.cart.CartEvent
// import com.huertohogar.data.CartItem

// Clases de datos de ejemplo
data class CartItem(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Int
)

data class CartState(
    val isLoading: Boolean = false,
    val cartItems: List<CartItem> = emptyList(),
    val totalPrice: Double = 0.0
)

@Composable
fun CartRoute(
    onNavigateToCheckout: () -> Unit,
    // viewModel: CartViewModel = viewModel()
) {
    // val uiState by viewModel.uiState.collectAsState()

    // Estado de ejemplo para el prototipo
    val items = listOf(
        CartItem("1", "Tomate Cherry", 2.99, 2),
        CartItem("2", "Lechuga", 1.49, 1)
    )
    val uiState = CartState(
        cartItems = items,
        totalPrice = items.sumOf { it.price * it.quantity }
    )

    CartScreen(
        state = uiState,
        onRemoveItem = { /* viewModel.onEvent(CartEvent.RemoveItem(it.id)) */ },
        onIncreaseQuantity = { /* ... */ },
        onDecreaseQuantity = { /* ... */ },
        onCheckoutClick = onNavigateToCheckout
    )
}

@Composable
fun CartScreen(
    state: CartState,
    onRemoveItem: (CartItem) -> Unit,
    onIncreaseQuantity: (CartItem) -> Unit,
    onDecreaseQuantity: (CartItem) -> Unit,
    onCheckoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (state.cartItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Tu carrito está vacío", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.cartItems, key = { it.id }) { item ->
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

@Composable
fun CartItemRow(
    item: CartItem,
    onRemove: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    "$${item.price} x ${item.quantity} = $${item.price * item.quantity}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            // Aquí irían botones de +/-
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar item")
            }
        }
    }
}

@Composable
fun CartSummary(
    totalPrice: Double,
    onCheckoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Divider()
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total:", style = MaterialTheme.typography.titleLarge)
            Text(
                "$${String.format("%.2f", totalPrice)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onCheckoutClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Proceder al Pago")
        }
    }
}