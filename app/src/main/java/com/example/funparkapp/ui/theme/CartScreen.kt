package com.example.funparkapp.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.funparkapp.data.CartSouvenir
import com.example.funparkapp.data.OrderDetail
import com.example.funparkapp.data.Souvenir
import com.example.funparkapp.data.saveOrderToFirebase
@Composable
fun CartScreen(
    cartItems: List<CartSouvenir>,
    onRemove: (CartSouvenir) -> Unit,
    onIncreaseQuantity: (CartSouvenir) -> Unit,
    onDecreaseQuantity: (CartSouvenir) -> Unit,
    onSelectAll: (Boolean) -> Unit,
    onCheckout: (List<OrderDetail>) -> Unit,
    allSouvenirs: List<Souvenir>
) {
    var selectAll by remember { mutableStateOf(false) }
    val selectedItems = remember { mutableStateListOf<CartSouvenir>() }
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Select All Row
        SelectAllRow(selectAll) { checked ->
            selectAll = checked
            onSelectAll(checked)

            cartItems.forEach { item ->
                item.selected = checked
                if (checked) {
                    if (!selectedItems.contains(item)) selectedItems.add(item)
                } else {
                    selectedItems.clear()
                }
            }
        }

        // Empty Cart Message
        if (cartItems.isEmpty()) {
            EmptyCartMessage()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(cartItems) { item ->
                    val souvenir = allSouvenirs.find { it.id == item.souvenirId }
                    souvenir?.let {
                        CartItemRow(
                            cartSouvenir = item,
                            souvenir = it,
                            onRemove = { onRemove(item) },
                            onIncreaseQuantity = { onIncreaseQuantity(item) },
                            onDecreaseQuantity = { onDecreaseQuantity(item) },
                            isSelected = item.selected, // Check selected state
                            onSelect = {
                                item.selected = !item.selected
                                if (item.selected) {
                                    selectedItems.add(item)
                                } else {
                                    selectedItems.remove(item)
                                }
                                selectAll = selectedItems.size == cartItems.size
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Checkout Section
            CheckoutSection(selectedItems) {
                if (selectedItems.isNotEmpty()) {
                    showDialog = true
                }
            }
        }
    }

    if (showDialog) {
        ConfirmOrderDialog(
            selectedItems = selectedItems,
            allSouvenirs = allSouvenirs,
            onCheckout = { orderDetails ->
                onCheckout(orderDetails)
                selectedItems.clear()
                selectAll = false
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}


@Composable
fun SelectAllRow(selectAll: Boolean, onSelectAllChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Checkbox(
            checked = selectAll,
            onCheckedChange = onSelectAllChange
        )
        Text(text = "Select All", modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
fun EmptyCartMessage() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "No items found", style = MaterialTheme.typography.headlineSmall, color = Color.Gray)
    }
}


@Composable
fun ConfirmOrderDialog(
    selectedItems: List<CartSouvenir>,
    allSouvenirs: List<Souvenir>,
    onCheckout: (List<OrderDetail>) -> Unit,
    onDismiss: () -> Unit
) {
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                onDismiss()
            },
            title = { Text("Confirm Order") },
            text = {
                Column {
                    Text("Are you sure you want to confirm your order?")
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn {
                        items(selectedItems) { item ->
                            val souvenir = allSouvenirs.find { it.id == item.souvenirId }
                            souvenir?.let {
                                val subtotal = item.quantity * it.price
                                Text(
                                    text = "${it.name} - Quantity: ${item.quantity} - Subtotal: RM ${"%.2f".format(subtotal)}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val orderDetails = selectedItems.mapNotNull { item ->
                        val souvenir = allSouvenirs.find { it.id == item.souvenirId }
                        souvenir?.let {
                            OrderDetail(
                                id = item.id,
                                name = it.name,
                                price = it.price,
                                quantity = item.quantity,
                                imageResource = it.imageResource,
                                orderDate = System.currentTimeMillis().toString()
                            )
                        }
                    }

                    saveOrderToFirebase(orderDetails)
                    onCheckout(orderDetails)
                    showDialog = false
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showDialog = false
                    onDismiss()
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}




@Composable
fun CartItemRow(
    cartSouvenir: CartSouvenir,
    souvenir: Souvenir,
    onRemove: () -> Unit,
    onIncreaseQuantity: () -> Unit,
    onDecreaseQuantity: () -> Unit,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val subtotal = cartSouvenir.quantity * souvenir.price

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onSelect() },
            modifier = Modifier.padding(end = 16.dp)
        )

        Image(
            painter = painterResource(id = souvenir.imageResource),
            contentDescription = "Image of ${souvenir.name}",
            modifier = Modifier
                .size(80.dp)
                .padding(end = 16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
        )

        Column(
            modifier = Modifier
                .padding(8.dp)
                .weight(1f)
        ) {
            Text(
                text = souvenir.name,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Price: RM ${"%.2f".format(souvenir.price)}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Subtotal: RM ${"%.2f".format(subtotal)}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                IconButton(onClick = { onDecreaseQuantity() }) {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Decrease Quantity")
                }
                Text(text = cartSouvenir.quantity.toString(), modifier = Modifier.padding(horizontal = 8.dp))
                IconButton(onClick = { onIncreaseQuantity() }) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Increase Quantity")
                }
            }
        }

        IconButton(onClick = { onRemove() }) {
            Icon(imageVector = Icons.Filled.Delete, contentDescription = "Remove Item")
        }
    }
}