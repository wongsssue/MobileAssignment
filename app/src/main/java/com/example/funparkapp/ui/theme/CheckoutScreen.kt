package com.example.funparkapp.ui.theme

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.util.Date
import com.example.funparkapp.R
import com.example.funparkapp.data.CartItemViewModel
import com.example.funparkapp.data.PaymentMethodViewModel
import com.example.funparkapp.data.PurchaseHistory
import com.example.funparkapp.data.PurchaseHistoryViewModel
import com.example.funparkapp.data.PurchasedItem
import com.example.funparkapp.data.SharedViewModel

@Composable
fun CheckoutScreen(
    paySuccess:() -> Unit,
    cartItemViewModel: CartItemViewModel,
    purchaseHistoryViewModel: PurchaseHistoryViewModel,
    paymentMethodViewModel: PaymentMethodViewModel,
    sharedViewModel: SharedViewModel
) {

    val cartItems by cartItemViewModel.allCartItems.observeAsState(emptyList())
    val totalPrice by cartItemViewModel.totalPrice.observeAsState(0.0)
    val scrollState = rememberScrollState()

    val subtotal =  totalPrice ?: 0.0
    val tax = subtotal * 0.05
    val total = subtotal + tax

    var showPaymentMethodDialog by remember { mutableStateOf(false) }
    var selectedPaymentMethod by remember { mutableStateOf("Online Banking") }
    var selectedPaymentImage by remember { mutableStateOf(R.drawable.onlinebanking) }

    Column(
        modifier = Modifier
            .padding(start = 16.dp, top = 30.dp, end = 16.dp, bottom = 70.dp)
            .verticalScroll(scrollState)
            .fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.checkOut),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(25.dp))

        val groupedCartItems = cartItems.groupBy { Pair(it.ticketPlan, it.ticketType) }

        groupedCartItems.forEach { (ticketKey, items) ->
            val ticketPlan = ticketKey.first
            val ticketType = ticketKey.second
            val totalQuantity = items.sumOf { it.quantity }
            val price =
                items.firstOrNull()?.price ?: 0.0

            Card(
                modifier = Modifier.padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
                    DetailRow(label = "Ticket Plan", value = ticketPlan)
                    DetailRow(label = "Ticket Type", value = ticketType)
                    DetailRow(label = "Price per Ticket", value = "RM${price.format(2)}")
                    DetailRow(label = "Qty", value = totalQuantity.toString())
                    DetailRow(label = "Total", value = "RM${(price * totalQuantity).format(2)}")
                }
            }
        }

        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = "Totals",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                PayRow(label = "Subtotal:", value = "RM${subtotal.format(2)}")
                PayRow(label = "Tax (5%):", value = "RM${tax.format(2)}")
                PayRow(label = "Total:", value = "RM${total.format(2)}")
            }
        }
        Spacer(modifier = Modifier.height(25.dp))

        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(
                modifier = Modifier.padding(15.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.payment_method),
                        contentDescription = "Payment Method",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Payment Method", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.weight(1f))
                    Image(
                        painter = painterResource(R.drawable.edit),
                        contentDescription = "Edit Payment Method",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { showPaymentMethodDialog = true }
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    PayMethod(
                        selectedPaymentMethod = selectedPaymentMethod,
                        selectedPaymentImage = selectedPaymentImage
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "RM${total.format(2)}")
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                val generatedPurchaseId = (100000..999999).random().toLong()
                val purchaseHistory = PurchaseHistory(
                    id = generatedPurchaseId,
                    pricePaid = total,
                    purchasedDate = Date()
                )
                val purchasedItems = cartItems.map { cartItem ->
                    PurchasedItem(
                        itemId = 0L,
                        id = generatedPurchaseId,
                        ticketPlan = cartItem.ticketPlan,
                        ticketType = cartItem.ticketType,
                        qty = cartItem.quantity
                    )
                }

                purchaseHistoryViewModel.insertPurchaseWithItems(
                    purchaseHistory = purchaseHistory,
                    purchasedItems = purchasedItems
                ) { purchaseId ->
                    sharedViewModel.ticketId = purchaseId
                    paySuccess()

                    cartItems.forEach { cartItemViewModel.deleteCartItem(it) }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(text = "Place Order", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
    if (showPaymentMethodDialog) {
        Dialog(onDismissRequest = { showPaymentMethodDialog = false }) {
            PaymentMethodForm(
                paymentMethodViewModel = paymentMethodViewModel,
                backToCheckout = { paymentMethod, paymentImage ->
                    selectedPaymentMethod = paymentMethod
                    selectedPaymentImage = paymentImage
                    showPaymentMethodDialog = false
                }
            )
        }
    }
}

@Composable
fun PayMethod(
    selectedPaymentMethod: String,
    selectedPaymentImage: Int,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)
    ) {
        Image(
            painter = painterResource(id = selectedPaymentImage),
            contentDescription = selectedPaymentMethod,
            modifier = Modifier.size(30.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = selectedPaymentMethod)
    }
}


@Composable
fun PayRow(label: String, value: String) {
    Card(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = label, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold
            )
            Text(
                text = value,
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.End
            )
        }
    }
}

@Composable
fun PaymentMethodForm(
    paymentMethodViewModel: PaymentMethodViewModel,
    backToCheckout: (paymentMethod: String, paymentImage: Int) -> Unit
) {

    val scrollState = rememberScrollState()
    val paymentMethods by paymentMethodViewModel.allPaymentMethods.observeAsState(emptyList())
    val uniquePaymentMethods = paymentMethods.distinctBy { it.paymentMethod }
    var selectedMethod by remember { mutableStateOf(paymentMethods.firstOrNull()?.paymentMethod ?: "") }
    var selectedImage by remember { mutableStateOf(paymentMethods.firstOrNull()?.paymentMethodImg ?: 0) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(scrollState)
            .fillMaxSize()
    ) {
        uniquePaymentMethods.forEach { method ->
            payMethodRow(
                payMethodImg = method.paymentMethodImg,
                payMethod = method.paymentMethod,
                isSelected = selectedMethod == method.paymentMethod,
                onSelectPayMethod = {
                    selectedMethod = method.paymentMethod
                    selectedImage = method.paymentMethodImg
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(30.dp))
        Button(
            onClick = {
                backToCheckout(selectedMethod, selectedImage)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF8C00)
            )
        ) {
            Text(
                text = "Confirm",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
fun payMethodRow(
    @DrawableRes payMethodImg: Int,
    payMethod: String,
    isSelected: Boolean,
    onSelectPayMethod: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectPayMethod() }
            .padding(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp)
        ) {
            Image(
                painter = painterResource(payMethodImg),
                contentDescription = payMethod,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = payMethod,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.weight(1f))
            RadioButton(
                selected = isSelected,
                onClick = { onSelectPayMethod() },
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color(0xFFFF8C00),
                    unselectedColor = Color.Gray
                )
            )
        }
    }
}


private fun Double.format(digits: Int) = "%.${digits}f".format(this)

