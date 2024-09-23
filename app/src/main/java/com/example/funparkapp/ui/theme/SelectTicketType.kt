package com.example.funparkapp.ui.theme

import TicketViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.funparkapp.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.funparkapp.data.CartItem
import com.example.funparkapp.data.CartItemViewModel
import com.example.funparkapp.data.TicketType


@Composable
fun SelectTicketType(
    ticketPlan: String,
    ticketViewModel: TicketViewModel,
    cartItemViewModel: CartItemViewModel,
    goToShoppingCart: () -> Unit = {}
) {
    val ticketWithTypes by ticketViewModel.getTicketWithTicketType(ticketPlan).observeAsState(emptyList())
    var selectedTicketType by remember { mutableStateOf<TicketType?>(null) }
    var quantity by remember { mutableStateOf(0) }
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(start = 0.dp, top = 0.dp, end = 0.dp, bottom = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 0.dp, top = 12.dp, end = 0.dp, bottom = 0.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 0.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = ticketPlan,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 2.dp),
                        color = Color(0xFFFF8C00),
                    )
                    Image(
                        painter = painterResource(R.mipmap.ic_launcher),
                        contentDescription = stringResource(R.string.shoppingCart),
                        modifier = Modifier
                            .size(45.dp)
                            .clickable { goToShoppingCart() }
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            if (ticketWithTypes.isNotEmpty()) {
                ticketWithTypes.forEach { ticketWithType ->
                    // Display ticket image based on the fetched data
                    Image(
                        painter = painterResource(ticketWithType.ticket.imageResId),
                        contentDescription = ticketWithType.ticket.ticketPlan,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(13.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    ) {
                        // Iterate over ticketTypes
                        if (ticketWithType.ticketTypes.isNotEmpty()) {
                            ticketWithType.ticketTypes.forEach { ticketType ->
                                priceCard(
                                    ticketType = ticketType.ticketType,
                                    price = ticketType.price,
                                    ticketTypeDescription = ticketType.ticketDescription,
                                    onQuantityChanged = { newQuantity ->
                                        selectedTicketType = ticketType
                                        quantity = newQuantity
                                    }
                                )
                                Spacer(modifier = Modifier.height(30.dp))
                            }
                        } else {
                            Text(
                                text = "No ticket types available",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = "No ticket types available",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
        Button(
            onClick = {
                selectedTicketType?.let {
                    val cartItem = CartItem(
                        ticketPlan = ticketPlan,
                        ticketType = it.ticketType,
                        price = it.price,
                        quantity = quantity
                    )
                    cartItemViewModel.insertCartItem(cartItem)
                    quantity = 0
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF8C00)
            ),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Add To Cart", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun priceCard(
    ticketType: String,
    price: Double,
    ticketTypeDescription: String,
    onQuantityChanged: (Int) -> Unit
) {
    var qty by remember { mutableStateOf(0) }
    Card(
        modifier = Modifier
            .padding(start = 16.dp, top = 10.dp, end = 16.dp, bottom = 8.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = ticketType,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    text = "RM${price}",
                    fontSize = 17.sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 0.dp)
                ) {
                    Image(
                        painter = painterResource(R.mipmap.remove),
                        contentDescription = "Remove",
                        modifier = Modifier
                            .size(30.dp)
                            .clickable {
                                if (qty > 0) {
                                    qty--
                                    onQuantityChanged(qty)
                                }
                            }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = qty.toString(), fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(
                        painter = painterResource(R.mipmap.add),
                        contentDescription = "Add",
                        modifier = Modifier
                            .size(30.dp)
                            .clickable {
                                qty++
                                onQuantityChanged(qty)
                            }
                    )
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = ticketTypeDescription,
                fontSize = 13.sp
            )
        }
    }
}

