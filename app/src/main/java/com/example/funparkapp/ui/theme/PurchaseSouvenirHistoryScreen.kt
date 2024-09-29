package com.example.funparkapp.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.funparkapp.data.Order
import com.example.funparkapp.data.PurchaseSouvenirHistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseSouvenirHistoryScreen(
    viewModel: PurchaseSouvenirHistoryViewModel = viewModel(),
    onBackToHome: () -> Unit
) {
    val orders = viewModel.orderDetails.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Purchase History") },
                navigationIcon = {
                    IconButton(onClick = {  }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (orders.value.isEmpty()) {
                Text("No purchase history found.", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn {
                    items(orders.value) { order ->
                        OrderItem(order)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItem(order: Order) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Order ID: ${order.id}", style = MaterialTheme.typography.titleMedium)
            Text("Order Date: ${order.orderDate}", style = MaterialTheme.typography.bodyLarge)
            Text("Total Price: RM ${order.totalPrice}", style = MaterialTheme.typography.bodyLarge)

            // Displaying items in the order
            order.items.forEach { item ->
                Text("Item: ${item.name}, Quantity: ${item.quantity}, Price: RM ${item.price}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}