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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.funparkapp.data.Order
import com.example.funparkapp.data.PurchaseSouvenirHistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseSouvenirHistoryScreen(
    viewModel: PurchaseSouvenirHistoryViewModel = viewModel(),
) {
    val orders = viewModel.orderDetails.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Purchase History") },
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
    val taxRate = 0.05
    val totalPriceWithTax = order.totalPrice * (1 + taxRate)

    val lightOrange = Color(0xFFFFC107)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = lightOrange) // Set the background color
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Order ID: ${order.id}", style = MaterialTheme.typography.titleMedium)
            Text("Order Date: ${order.orderDate}", style = MaterialTheme.typography.bodyLarge)
            Text("Total Price: RM ${"%.2f".format(totalPriceWithTax)}", style = MaterialTheme.typography.bodyLarge)

            order.items.forEach { item ->
                Text("Item: ${item.name}, Quantity: ${item.quantity}, Price: RM ${item.price}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}