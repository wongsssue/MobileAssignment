package com.example.funparkapp.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.funparkapp.R
import com.example.funparkapp.data.CartSouvenir
import com.example.funparkapp.data.Souvenir

@Composable
fun CheckoutScreen(
    selectedItems: List<CartSouvenir>,
    allSouvenirs: List<Souvenir>,
    onConfirmPayment: (String) -> Unit,
    onViewHistory: () -> Unit,
    onBackToHome: () -> Unit
) {
    val subtotal = selectedItems.sumOf { item -> item.price * item.quantity }
    val tax = subtotal * 0.05
    val totalPrice = subtotal + tax

    var selectedPaymentMethod by remember { mutableStateOf("") }
    var showPaymentDialog by remember { mutableStateOf(false) }
    var showPaymentSuccess by remember { mutableStateOf(false) }

    val paymentMethods = listOf(
        PaymentMethod("Credit/Debit Card", R.drawable.debitorcredit),
        PaymentMethod("TNG", R.drawable.tngo),
        PaymentMethod("Online Banking", R.drawable.onlinebanking),
        PaymentMethod("Boost", R.drawable.boost),
        PaymentMethod("GrabPay", R.drawable.grabpay),
        PaymentMethod("ShopeePay", R.drawable.shopeepay)
    )

    Column(modifier = Modifier.padding(16.dp)) {
        if (showPaymentSuccess) {
            PaymentSuccessScreen(
                onViewHistory = onViewHistory,
                onBackToHome = onBackToHome
            )
        } else {
            Text(text = "Checkout Details", style = MaterialTheme.typography.titleLarge)

            // List selected items with images
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(selectedItems) { item ->
                    val souvenir = allSouvenirs.find { it.id == item.souvenirId }
                    souvenir?.let {
                        CheckoutItemRow(souvenir = it, quantity = item.quantity)
                    }
                }
            }
            Text("Subtotal: RM ${"%.2f".format(subtotal)}", style = MaterialTheme.typography.bodyMedium)
            Text("Tax (5%): RM ${"%.2f".format(tax)}", style = MaterialTheme.typography.bodyMedium)
            Text("Total: RM ${"%.2f".format(totalPrice)}", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showPaymentDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = if (selectedPaymentMethod.isEmpty()) "Select Payment Method" else selectedPaymentMethod)
            }

            if (showPaymentDialog) {
                PaymentMethodDialog(paymentMethods) { method ->
                    selectedPaymentMethod = method.name
                    showPaymentDialog = false
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onConfirmPayment(selectedPaymentMethod)
                    showPaymentSuccess = true
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Confirm Payment")
            }
        }
    }
}

@Composable
fun CheckoutItemRow(souvenir: Souvenir, quantity: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
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

        Column {
            Text(text = souvenir.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = "Quantity: $quantity", style = MaterialTheme.typography.bodyMedium)
            Text(text = "RM ${"%.2f".format(souvenir.price * quantity)}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun PaymentMethodDialog(paymentMethods: List<PaymentMethod>, onSelect: (PaymentMethod) -> Unit) {
    AlertDialog(
        onDismissRequest = { onSelect(PaymentMethod("", 0)) },
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
            Button(onClick = { }) {
                Text("Close")
            }
        }
    )
}

data class PaymentMethod(val name: String, val imageRes: Int)

@Composable
fun CheckoutSection(selectedItems: List<CartSouvenir>, onCheckoutClicked: () -> Unit) {
    val totalQuantity = selectedItems.sumOf { it.quantity }
    val totalPrice = selectedItems.sumOf { item -> item.price * item.quantity }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        if (totalQuantity > 0) {
            Text(text = "Total Quantity: $totalQuantity")
            Text(text = "Total Price: RM ${"%.2f".format(totalPrice)}")

            Button(
                onClick = onCheckoutClicked,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Checkout")
            }
        } else {
            Text(text = "No items selected for checkout")
        }
    }
}
