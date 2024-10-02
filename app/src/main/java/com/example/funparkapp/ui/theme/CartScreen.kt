package com.example.funparkapp.ui.theme

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.funparkapp.data.CartSouvenirViewModel
import com.example.funparkapp.data.CartSouvenir
import com.example.funparkapp.data.OrderDetail
import com.example.funparkapp.data.Souvenir
import com.example.funparkapp.data.ThemeViewModel
import com.example.funparkapp.data.saveOrderToFirebase


@Composable
fun CartScreen(
    cartItems: List<CartSouvenir>,
    onRemove: (CartSouvenir) -> Unit,
    onIncreaseQuantity: (CartSouvenir) -> Unit,
    onDecreaseQuantity: (CartSouvenir) -> Unit,
    onSelectAll: (Boolean) -> Unit,
    onCheckout: (List<OrderDetail>) -> Unit,
    allSouvenirs: List<Souvenir>,
    themeViewModel: ThemeViewModel
) {
    // Observe the dark mode state
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()
    val backgroundColor = if (isDarkMode) Color.Black else Color.White
    val textColor = if (isDarkMode) Color.White else Color.Black
    val cardColor = if (isDarkMode) Color.DarkGray else Color.White

    // State variables
    var selectAll by remember { mutableStateOf(false) }
    val selectedItems = remember { mutableStateListOf<CartSouvenir>() }
    var showDialog by remember { mutableStateOf(false) }

    // Detect current configuration
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val cartViewModel: CartSouvenirViewModel = viewModel()

    // Use a Box to enable alignment
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        // Main content in Column
        if (isLandscape) {
            // Landscape layout
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                // Cart Items List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp)
                ) {
                    items(cartItems) { item ->
                        val souvenir = allSouvenirs.find { it.id == item.souvenirId }
                        souvenir?.let {
                            CartItemRow(
                                cartItem = item,
                                souvenir = it,
                                onRemove = { onRemove(item) },
                                onIncreaseQuantity = { onIncreaseQuantity(item) },
                                onDecreaseQuantity = { onDecreaseQuantity(item) },
                                isSelected = item.selected,
                                onSelect = {
                                    item.selected = !item.selected
                                    if (item.selected) {
                                        selectedItems.add(item)
                                    } else {
                                        selectedItems.remove(item)
                                    }
                                    selectAll = selectedItems.size == cartItems.size
                                },
                                isDarkMode = isDarkMode // Pass the dark mode state
                            )
                        }
                    }
                }

                // Checkout Section
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(200.dp) // Fixed width for checkout section
                        .background(cardColor, RoundedCornerShape(16.dp)) // Rounded corners
                        .shadow(4.dp) // Shadow for depth
                        .padding(16.dp) // Padding inside checkout section
                ) {
                    SelectAllRow(
                        selectAll,
                        onSelectAll = { checked ->
                            selectAll = checked
                            onSelectAll(checked)
                            selectedItems.clear() // Clear selected items before re-adding
                            cartItems.forEach { item ->
                                item.selected = checked
                                if (checked) {
                                    selectedItems.add(item) // Add item if checked
                                }
                            }
                        },
                        textColor = textColor // Pass text color for SelectAllRow
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CheckoutSection(selectedItems, onCheckoutClicked = {
                        if (selectedItems.isNotEmpty()) {
                            showDialog = true
                        }
                    }, isDarkMode = isDarkMode)
                }
            }
        } else {
            // Portrait layout
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                SelectAllRow(
                    selectAll,
                    onSelectAll = { checked ->
                        selectAll = checked
                        onSelectAll(checked) // This should correctly call the function you defined
                        cartItems.forEach { item ->
                            item.selected = checked
                            if (checked) {
                                if (!selectedItems.contains(item)) selectedItems.add(item)
                            } else {
                                selectedItems.clear()
                            }
                        }
                    },
                    textColor = textColor // This line is fine, as it's for text color (if you have it in your parameters)
                )
                // Empty Cart Message
                if (cartItems.isEmpty()) {
                    EmptyCartMessage() // Pass text color for EmptyCartMessage
                } else {
                    // Cart Items List
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(cartItems) { item ->
                            val souvenir = allSouvenirs.find { it.id == item.souvenirId }
                            souvenir?.let {
                                CartItemRow(
                                    cartItem = item,
                                    souvenir = it,
                                    onRemove = { onRemove(item) },
                                    onIncreaseQuantity = { onIncreaseQuantity(item) },
                                    onDecreaseQuantity = { onDecreaseQuantity(item) },
                                    isSelected = item.selected,
                                    onSelect = {
                                        item.selected = !item.selected
                                        if (item.selected) {
                                            selectedItems.add(item)
                                        } else {
                                            selectedItems.remove(item)
                                        }
                                        selectAll = selectedItems.size == cartItems.size
                                    },
                                    isDarkMode = isDarkMode // Pass the dark mode state
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Checkout Section
                    CheckoutSection(selectedItems, onCheckoutClicked = {
                        if (selectedItems.isNotEmpty()) {
                            showDialog = true
                        }
                    }, isDarkMode = isDarkMode)
                }
            }
        }

        // Floating Action Button for Dark Mode Toggle
        FloatingActionButton(
            onClick = { themeViewModel.toggleTheme() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .background(Color(0xFFFFA500), shape = CircleShape) // Accent color for FAB
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Toggle Dark Mode",
                tint = Color.White
            )
        }

        // Confirmation Dialog for Checkout
        if (showDialog) {
            ConfirmOrderDialog(
                selectedItems = selectedItems,
                allSouvenirs = allSouvenirs,
                cartViewModel = cartViewModel,
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
}

@Composable
fun CartItemRow(
    cartItem: CartSouvenir,
    souvenir: Souvenir,
    onRemove: () -> Unit,
    onIncreaseQuantity: () -> Unit,
    onDecreaseQuantity: () -> Unit,
    isSelected: Boolean,
    onSelect: () -> Unit,
    isDarkMode: Boolean // Receive dark mode state
) {
    val subtotal = cartItem.quantity * souvenir.price
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(if (isDarkMode) Color(0xFF444444) else MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onSelect() },
            colors = CheckboxDefaults.colors(
                checkedColor = if (isDarkMode) Color.White else Color(0xFFFFA500), // Change to orange
                uncheckedColor = if (isDarkMode) Color.White.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
            ),
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
                .weight(1f)
                .padding(end = 16.dp)
        ) {
            Text(
                text = souvenir.name,
                style = MaterialTheme.typography.titleMedium,
                color = if (isDarkMode) Color.White else Color.Black
            )
            Text(
                text = "Quantity: ${cartItem.quantity}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDarkMode) Color.LightGray else Color.Gray
            )
            Text(
                text = "Subtotal: RM %.2f".format(subtotal),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDarkMode) Color.LightGray else Color.Gray
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onDecreaseQuantity,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease Quantity",
                    tint = Color(0xFFFFA500) // Change to orange
                )
            }
            IconButton(
                onClick = onIncreaseQuantity,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase Quantity",
                    tint = Color(0xFFFFA500) // Change to orange
                )
            }
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove Item",
                    tint = if (isDarkMode) Color.Red else Color(0xFFFFA500) // Change to orange for light mode
                )
            }
        }
    }
}

@Composable
fun SelectAllRow(selectAll: Boolean, onSelectAll: (Boolean) -> Unit, textColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = selectAll,
            onCheckedChange = { onSelectAll(it) },
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFFFFA500), // Accent color for checkbox
                uncheckedColor = Color.Gray // Color for unchecked state
            )
        )

        Text(
            text = "Select All",
            style = MaterialTheme.typography.bodyLarge,
            color = textColor,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun CheckoutSection(selectedItems: List<CartSouvenir>, onCheckoutClicked: () -> Unit, isDarkMode: Boolean) {
    // Calculate total quantity and total price
    val totalQuantity = selectedItems.sumOf { it.quantity }
    val totalPrice = selectedItems.sumOf { it.price * it.quantity }

    Column {
        Text(
            text = "Selected Items: ${selectedItems.size}",
            style = MaterialTheme.typography.bodyLarge,
            color = if (isDarkMode) Color.White else Color.Black
        )

        Text(
            text = "Total Quantity: $totalQuantity",
            style = MaterialTheme.typography.bodyLarge,
            color = if (isDarkMode) Color.White else Color.Black
        )

        Text(
            text = "Total Price: RM %.2f".format(totalPrice),
            style = MaterialTheme.typography.bodyLarge,
            color = if (isDarkMode) Color.White else Color.Black
        )

        Button(
            onClick = onCheckoutClicked,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)), // Set button color to orange
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text(text = "Checkout", color = Color.White) // Set text color to white for contrast
        }
    }
}



@Composable
fun EmptyCartMessage() {
    Text(
        text = "Your cart is empty",
        style = MaterialTheme.typography.headlineSmall,
        color = Color.Gray,
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center) // Center the message in the view
    )
}

@Composable
fun ConfirmOrderDialog(
    selectedItems: List<CartSouvenir>,
    allSouvenirs: List<Souvenir>,
    cartViewModel: CartSouvenirViewModel,
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
                Button(
                    onClick = {
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
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)), // Set button color to orange
                    modifier = Modifier.padding(8.dp) // Optional: add some padding
                ) {
                    Text("Confirm", color = Color.White) // Set text color to white
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)), // Set button color to orange
                    modifier = Modifier.padding(8.dp) // Optional: add some padding
                ) {
                    Text("Cancel", color = Color.White) // Set text color to white
                }
            }
        )
    }
}