package com.example.funparkapp.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.funparkapp.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.funparkapp.data.CartItemViewModel

@Composable
fun ShoppingCartScreen(
    cartItemViewModel: CartItemViewModel,
    checkout: () -> Unit,
    continueShopping: () -> Unit
) {
    val cartItems by cartItemViewModel.allCartItems.observeAsState(emptyList())
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(scrollState)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.shoppingCart),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        cartItems.forEach { cartItem ->
            Card(
                modifier = Modifier.padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
                    DetailRow(label = "Ticket Plan", value = cartItem.ticketPlan)
                    DetailRow(label = "Ticket Type", value = cartItem.ticketType)
                    DetailRow(label = "Price", value = "RM${cartItem.price.format(2)}")
                    DetailRow(label = "Qty", value = cartItem.quantity.toString())

                    Button(
                        onClick = { cartItemViewModel.deleteCartItem(cartItem) },
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .align(Alignment.End),
                        border = BorderStroke(2.dp, Color.Red),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Remove",
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier.padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(10.dp)
            ) {

                Button(
                    onClick = { checkout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF8C00)
                    )
                ) {
                    Text(
                        text = stringResource(R.string.checkOut)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { continueShopping() },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(2.dp, Color(0xFFFF8C00)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    )
                ) {
                    Text(
                        text = "Continue Shopping",
                        color = Color(0xFFFF8C00)
                    )
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall
            )
        }
        Box(
            modifier = Modifier
                .weight(1f),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(text = value)
        }
    }
}

private fun Double.format(digits: Int) = "%.${digits}f".format(this)

