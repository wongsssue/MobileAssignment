package com.example.funparkapp.ui.theme

import TicketViewModel
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.funparkapp.R
import com.example.funparkapp.data.Ticket


@Composable
fun AdminTicketScreen(
    ticketViewModel: TicketViewModel,
) {

    val firebaseTickets by ticketViewModel.firebaseTickets.observeAsState(emptyList())
    var isFormVisible by remember { mutableStateOf(false) }

    val drawableImages = listOf(
        R.drawable.onedaypass,
        R.drawable.twodaypass,
        R.drawable.ultimatepass
    )
    LaunchedEffect(Unit) {
        ticketViewModel.syncTicketsFromFirebase()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    isFormVisible = true
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Ticket")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (isFormVisible) {
                TicketForm(
                    onCancelClick = { isFormVisible = false },
                    onSubmitClick = { ticketPlan, ticketDescription, newTicketTypes ->
                        val randomImageResId = drawableImages.random()
                        val newTicket = Ticket(
                            ticketPlan = ticketPlan,
                            ticketPlanDescription = ticketDescription,
                            imageResId = randomImageResId
                        )
                        ticketViewModel.insert(newTicket, newTicketTypes)
                        isFormVisible = false
                    }
                )
            }

            LazyColumn {
                items(firebaseTickets) { ticket ->
                    TicketCard(
                        ticketPlan = ticket.ticketPlan,
                        ticketViewModel = ticketViewModel,
                        onDeleteClick = {
                            ticketViewModel.delete(ticket)
                        }
                    )
                }
            }
        }
    }
}
@Composable
fun TicketForm(
    onCancelClick: () -> Unit,
    onSubmitClick: (String, String, Map<String, Pair<Double, String>>) -> Unit
) {
    var ticketPlan by remember { mutableStateOf("") }
    var ticketPlanDescription by remember { mutableStateOf("") }
    var ticketTypeCount by remember { mutableStateOf(1) }
    var ticketTypes by remember { mutableStateOf(mutableMapOf<String, Pair<Double, String>>()) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    EditField(
                        value = ticketPlan,
                        text = "Ticket Plan",
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        onValueChange = { ticketPlan = it }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
                item {
                    EditField(
                        value = ticketPlanDescription,
                        text = "Ticket Plan Description",
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        onValueChange = { ticketPlanDescription = it }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                item {
                    EditField(
                        value = ticketTypeCount.toString(),
                        text = "Number of Ticket Types",
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        onValueChange = {
                            ticketTypeCount = it.toIntOrNull() ?: 1
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                items(ticketTypeCount) { index ->
                    var ticketType by remember { mutableStateOf("") }
                    var ticketPrice by remember { mutableStateOf("") }
                    var ticketTypeDescription by remember { mutableStateOf("") }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        EditField(
                            value = ticketType,
                            text = "Enter Ticket Type",
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            onValueChange = { ticketType = it }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        EditField(
                            value = ticketPrice,
                            text = "Price",
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            onValueChange = { ticketPrice = it }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        EditField(
                            value = ticketTypeDescription,
                            text = "Ticket Type Description",
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            onValueChange = { ticketTypeDescription = it }
                        )

                        if (ticketType.isNotBlank() && ticketPrice.isNotBlank() && ticketTypeDescription.isNotBlank()) {
                            ticketTypes[ticketType] = Pair(ticketPrice.toDouble(), ticketTypeDescription)
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = onCancelClick,
                            modifier = Modifier.width(120.dp),
                            border = BorderStroke(2.dp, Color.Red),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                        ) {
                            Text(text = "Cancel", color = Color.Red)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                onSubmitClick(ticketPlan, ticketPlanDescription, ticketTypes.toMap())
                            },
                            modifier = Modifier.width(120.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00))
                        ) {
                            Text(text = "Submit")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TicketCard(
    ticketPlan: String,
    ticketViewModel: TicketViewModel,
    onDeleteClick: () -> Unit
) {
    val ticketWithTypes by ticketViewModel.getTicketWithTicketType(ticketPlan).observeAsState(emptyList())
    val uniqueTicketTypes = ticketWithTypes.flatMap { it.ticketTypes }
        .distinctBy { it.ticketType }

    Card(
        modifier = Modifier
            .padding(15.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = ticketPlan,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(10.dp))
            uniqueTicketTypes.forEach { ticketType ->
                TicketTypeInfo(
                    ticketType = ticketType.ticketType,
                    price = ticketType.price.toString(),
                    description = ticketType.ticketDescription
                )
                Spacer(modifier = Modifier.height(15.dp))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 0.dp, end = 12.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                Button(
                    onClick =onDeleteClick,
                    border = BorderStroke(2.dp, Color.Red),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    )) {
                    Text(text = "Delete Ticket", color = Color.Red)
                }
            }

        }
    }
}

@Composable
fun TicketTypeInfo(ticketType: String, price: String, description: String) {
    Column {
        Text(text = ticketType, fontWeight = FontWeight.Bold)
        Text(text = "Price: $price")
        Text(text = "Description: $description")
    }
}

@Composable
fun EditField(
    value: String,
    text : String,
    keyboardOptions: KeyboardOptions,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column{
        Text(
            text = text,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 5.dp)
        )
        TextField(
            value = value,
            singleLine = true,
            keyboardOptions = keyboardOptions,
            onValueChange = onValueChange,
            modifier = modifier
        )
    }
}

