package com.example.funparkapp.ui.theme

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.funparkapp.R
import com.example.funparkapp.data.CartSouvenir
import com.example.funparkapp.data.CheckoutViewModel
import com.example.funparkapp.data.PaymentMethodSouvenir
import com.example.funparkapp.data.Souvenir
import com.example.funparkapp.data.ThemeViewModel

@Composable
fun CheckoutScreen(
    selectedItems: List<CartSouvenir>,
    allSouvenirs: List<Souvenir>,
    onConfirmPayment: (String) -> Unit,
    onViewHistory: () -> Unit,
    onBackToHome: () -> Unit,
    themeViewModel: ThemeViewModel,
    checkoutViewModel: CheckoutViewModel = viewModel() // Using PaymentViewModel
) {
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()

    // Determine the screen orientation
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        // Call the landscape-specific UI here
        CheckoutScreenLandscape(
            selectedItems,
            allSouvenirs,
            onConfirmPayment,
            onViewHistory,
            onBackToHome,
            themeViewModel,
            checkoutViewModel // Pass the PaymentViewModel
        )
    } else {
        // Call the portrait-specific UI
        CheckoutScreenPortrait(
            selectedItems,
            allSouvenirs,
            onConfirmPayment,
            onViewHistory,
            onBackToHome,
            themeViewModel,
            checkoutViewModel // Pass the PaymentViewModel
        )
    }
}

@Composable
fun CheckoutScreenPortrait(
    selectedItems: List<CartSouvenir>,
    allSouvenirs: List<Souvenir>,
    onConfirmPayment: (String) -> Unit,
    onViewHistory: () -> Unit,
    onBackToHome: () -> Unit,
    themeViewModel: ThemeViewModel,
    paymentViewModel: CheckoutViewModel // Receive PaymentViewModel
) {
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()
    val backgroundColor = if (isDarkMode) Color.Black else Color.White
    val textColor = if (isDarkMode) Color.White else Color.Black

    val subtotal = selectedItems.sumOf { item -> item.price * item.quantity }
    val tax = subtotal * 0.05
    val totalPrice = subtotal + tax

    var selectedPaymentMethod by remember { mutableStateOf(paymentViewModel.selectedPaymentMethod) }
    var showPaymentDialog by remember { mutableStateOf(false) }
    var showPaymentSuccess by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val paymentMethods = listOf(
        PaymentMethodSouvenir("Credit/Debit Card", R.drawable.debitorcredit),
        PaymentMethodSouvenir("TNG", R.drawable.tngo),
        PaymentMethodSouvenir("Online Banking", R.drawable.onlinebanking),
        PaymentMethodSouvenir("Boost", R.drawable.boost),
        PaymentMethodSouvenir("GrabPay", R.drawable.grabpay),
        PaymentMethodSouvenir("ShopeePay", R.drawable.shopeepay)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 64.dp)
        ) {
            if (showPaymentSuccess) {
                PaymentSuccessScreen(
                    onViewHistory = onViewHistory,
                    onBackToHome = onBackToHome
                )
            } else {
                Text(
                    text = "Checkout Details",
                    style = MaterialTheme.typography.titleLarge,
                    color = textColor
                )

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(selectedItems) { item ->
                        val souvenir = allSouvenirs.find { it.id == item.souvenirId }
                        souvenir?.let {
                            CheckoutItemRow(souvenir = it, quantity = item.quantity, isDarkMode = isDarkMode)
                        }
                    }
                }

                Text("Subtotal: RM ${"%.2f".format(subtotal)}", style = MaterialTheme.typography.bodyMedium, color = textColor)
                Text("Tax (5%): RM ${"%.2f".format(tax)}", style = MaterialTheme.typography.bodyMedium, color = textColor)
                Text("Total: RM ${"%.2f".format(totalPrice)}", style = MaterialTheme.typography.titleLarge, color = textColor)

                Spacer(modifier = Modifier.height(16.dp))

                // Button to select payment method
                Button(
                    onClick = { showPaymentDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)) // Set orange color here
                ) {
                    Text(text = if (selectedPaymentMethod.isEmpty()) "Select Payment Method" else selectedPaymentMethod, color = Color.White)
                }

                // Show the payment method dialog
                if (showPaymentDialog) {
                    PaymentMethodDialog(
                        paymentMethods,
                        onSelect = { method ->
                            selectedPaymentMethod = method.name
                            paymentViewModel.savePaymentMethod(selectedPaymentMethod) // Save the payment method in ViewModel
                            showPaymentDialog = false // Dismiss the dialog upon selection
                        },
                        onDismiss = {
                            showPaymentDialog = false // Dismiss the dialog when close is called
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm payment button
                Button(
                    onClick = {
                        if (selectedPaymentMethod.isNotEmpty()) {
                            onConfirmPayment(selectedPaymentMethod)
                            showPaymentSuccess = true
                            errorMessage = "" // Clear error message on successful payment
                        } else {
                            errorMessage = "Please select a payment method" // Set error message
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)) // Set orange color here
                ) {
                    Text("Confirm Payment", color = Color.White)
                }

                // Display error message if it exists
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red, // Error message in red
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { themeViewModel.toggleTheme() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Toggle Dark Mode",
                tint = Color.White
            )
        }
    }
}

@Composable
fun CheckoutScreenLandscape(
    selectedItems: List<CartSouvenir>,
    allSouvenirs: List<Souvenir>,
    onConfirmPayment: (String) -> Unit,
    onViewHistory: () -> Unit,
    onBackToHome: () -> Unit,
    themeViewModel: ThemeViewModel,
    checkoutViewModel: CheckoutViewModel // Receive PaymentViewModel
) {
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()
    val backgroundColor = if (isDarkMode) Color.Black else Color.White
    val textColor = if (isDarkMode) Color.White else Color.Black


    val subtotal = selectedItems.sumOf { item -> item.price * item.quantity }
    val tax = subtotal * 0.05
    val totalPrice = subtotal + tax

    var selectedPaymentMethod by remember { mutableStateOf(checkoutViewModel.selectedPaymentMethod) }
    var showPaymentDialog by remember { mutableStateOf(false) }
    var showPaymentSuccess by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val paymentMethods = listOf(
        PaymentMethodSouvenir("Credit/Debit Card", R.drawable.debitorcredit),
        PaymentMethodSouvenir("TNG", R.drawable.tngo),
        PaymentMethodSouvenir("Online Banking", R.drawable.onlinebanking),
        PaymentMethodSouvenir("Boost", R.drawable.boost),
        PaymentMethodSouvenir("GrabPay", R.drawable.grabpay),
        PaymentMethodSouvenir("ShopeePay", R.drawable.shopeepay)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Column: Selected Items
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = "Checkout Details",
                    style = MaterialTheme.typography.titleLarge,
                    color = textColor
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(selectedItems) { item ->
                        val souvenir = allSouvenirs.find { it.id == item.souvenirId }
                        if (souvenir != null) {
                            CheckoutItemRow(
                                souvenir = souvenir,
                                quantity = item.quantity,
                                isDarkMode = isDarkMode
                            )
                        }
                    }
                }


                Text(
                    "Subtotal: RM ${"%.2f".format(subtotal)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
                Text(
                    "Tax (5%): RM ${"%.2f".format(tax)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
                Text(
                    "Total: RM ${"%.2f".format(totalPrice)}",
                    style = MaterialTheme.typography.titleLarge,
                    color = textColor
                )
            }

            // Display error message if it exists
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Right Column: Payment and Confirm Buttons
            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .padding(start = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center // Center vertically
            ) {
                // Button to select payment method
                Button(
                    onClick = { showPaymentDialog = true },
                    modifier = Modifier
                        .size(120.dp) // Ensure the button is a circle by setting equal width and height
                        .clip(CircleShape), // Make the button circular
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)) // Set orange color here
                ) {
                    Text(
                        text = if (selectedPaymentMethod.isEmpty()) "Select Payment\nMethod" else selectedPaymentMethod,
                        color = Color.White,
                        textAlign = TextAlign.Center // Center text alignment
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (selectedPaymentMethod.isNotEmpty()) {
                            onConfirmPayment(selectedPaymentMethod)
                            showPaymentSuccess = true
                            errorMessage = ""
                        } else {
                            errorMessage = "Please select a payment method"
                        }
                    },
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))
                ) {
                    Text(
                        "Confirm Payment",
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        FloatingActionButton(
            onClick = { themeViewModel.toggleTheme() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Toggle Dark Mode",
                tint = Color.White
            )
        }
    }
}

@Composable
fun CheckoutItemRow(souvenir: Souvenir, quantity: Int, isDarkMode: Boolean) {
    val backgroundColor = if (isDarkMode) Color.LightGray else MaterialTheme.colorScheme.surface
    val textColor = if (isDarkMode) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onBackground

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = souvenir.imageResource),
            contentDescription = "Image of ${souvenir.name}",
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .padding(end = 16.dp)
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = souvenir.name,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Quantity: $quantity",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "RM ${"%.2f".format(souvenir.price * quantity)}",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )
        }
    }
}



@Composable
fun PaymentMethodDialog(
    paymentMethods: List<PaymentMethodSouvenir>,
    onSelect: (PaymentMethodSouvenir) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Select Payment Method") },
        text = {
            Column {
                paymentMethods.forEach { method ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { onSelect(method) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = method.imageRes),
                            contentDescription = method.name,
                            modifier = Modifier.size(40.dp).padding(end = 8.dp)
                        )
                        Text(text = method.name)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)) // Set orange color here
            ) {
                Text("Close", color = Color.White) // Set text color to white for contrast
            }
        }
    )
}
