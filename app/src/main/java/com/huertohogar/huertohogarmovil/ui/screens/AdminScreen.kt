package com.huertohogar.huertohogarmovil.screens.admin

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.huertohogar.huertohogarmovil.model.Producto
import com.huertohogar.huertohogarmovil.ui.viewModelFactory
import com.huertohogar.huertohogarmovil.ui.viewmodel.AdminEvent
import com.huertohogar.huertohogarmovil.ui.viewmodel.AdminState
import com.huertohogar.huertohogarmovil.ui.viewmodel.AdminViewModel
import com.huertohogar.huertohogarmovil.ui.viewmodel.ViewModelFactory

@Composable
fun AdminRoute(
    onNavigateBack: () -> Unit, // Por si quieres poner un botón de salir
    factory: ViewModelFactory = viewModelFactory(),
    viewModel: AdminViewModel = viewModel(factory = factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Mostrar mensajes (Toast) cuando se actualiza un producto
    LaunchedEffect(uiState.message) {
        uiState.message?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.onEvent(AdminEvent.OnMessageShown)
        }
    }

    AdminScreen(
        state = uiState,
        onUpdateProduct = { product ->
            viewModel.onEvent(AdminEvent.OnUpdateProduct(product))
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    state: AdminState,
    onUpdateProduct: (Producto) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Administración de Productos") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.products, key = { it.id }) { product ->
                        AdminProductItem(
                            product = product,
                            onSave = onUpdateProduct
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminProductItem(
    product: Producto,
    onSave: (Producto) -> Unit
) {
    // Estado local para editar los campos antes de guardar
    var name by remember { mutableStateOf(product.name) }
    var priceStr by remember { mutableStateOf(product.price.toString()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "ID: ${product.id}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo Nombre Editable
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre del Producto") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo Precio Editable
            OutlinedTextField(
                value = priceStr,
                onValueChange = { priceStr = it },
                label = { Text("Precio ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Guardar
            Button(
                onClick = {
                    // Convertir precio y guardar
                    val newPrice = priceStr.toIntOrNull() ?: product.price
                    val updatedProduct = product.copy(name = name, price = newPrice)
                    onSave(updatedProduct)
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Guardar Cambios")
            }
        }
    }
}
