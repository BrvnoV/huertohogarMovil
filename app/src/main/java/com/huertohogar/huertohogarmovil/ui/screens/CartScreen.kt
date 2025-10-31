package com.huertohogar.huertohogarmovil.screens.cart

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.huertohogar.huertohogarmovil.HuertoHogarApp

import com.huertohogar.huertohogarmovil.ui.viewmodel.CartEvent
import com.huertohogar.huertohogarmovil.ui.viewmodel.CartState
import com.huertohogar.huertohogarmovil.ui.viewmodel.CartUiEvent
import com.huertohogar.huertohogarmovil.ui.viewmodel.CartViewModel
import com.huertohogar.huertohogarmovil.ui.viewmodel.ViewModelFactory
import com.huertohogar.model.CarritoItemConDetalles
// --- FIN DE CORRECCIÓN ---
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CartRoute(
    onNavigateToCheckout: () -> Unit,
    // Conectamos el ViewModel
    factory: ViewModelFactory = viewModelFactory(), // Usa el helper de abajo
    viewModel: CartViewModel = viewModel(factory = factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Escuchamos eventos para navegar o mostrar Toasts
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is CartUiEvent.NavigateToCheckout -> {
                    Toast.makeText(context, "¡Compra realizada! (Simulación)", Toast.LENGTH_LONG).show()
                    onNavigateToCheckout() // Navegamos a Home
                }
                is CartUiEvent.ShowSnackbar -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    CartScreen(
        state = uiState,
        // --- CORRECCIÓN DE LÓGICA ---
        // Usamos el patrón onEvent para enviar acciones al ViewModel
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
        // --- FIN DE CORRECCIÓN ---
    )
}

@Composable
fun CartScreen(
    state: CartState, // <-- Usamos el tipo importado
    onRemoveItem: (CarritoItemConDetalles) -> Unit,
    onIncreaseQuantity: (CarritoItemConDetalles) -> Unit,
    onDecreaseQuantity: (CarritoItemConDetalles) -> Unit,
    onCheckoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
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

@Composable
fun CartItemRow(
    item: CarritoItemConDetalles,
    onRemove: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder de imagen
            Box(Modifier.size(64.dp), contentAlignment = Alignment.Center) { Text("IMG") }

            Column(modifier = Modifier.weight(1.0f).padding(horizontal = 8.dp)) {
                Text(item.producto.name, style = MaterialTheme.typography.titleMedium, maxLines = 2)
                Text(
                    "$${item.producto.price}", // <-- Símbolo de moneda corregido
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Controles de cantidad
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDecrease) {
                    Icon(Icons.Default.Remove, contentDescription = "Quitar uno")
                }
                Text("${item.carritoItem.quantity}", style = MaterialTheme.typography.bodyLarge)
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
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total:", style = MaterialTheme.typography.titleLarge)
                Text(
                    "$${String.format("%d", totalPrice)}", // <-- Símbolo de moneda corregido
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
// Pégala al final del archivo para obtener la ViewModelFactory.

@Composable
fun viewModelFactory(): ViewModelFactory {
    val application = (LocalContext.current.applicationContext as HuertoHogarApp)
    return ViewModelFactory(application.repository, application.sessionManager)
}