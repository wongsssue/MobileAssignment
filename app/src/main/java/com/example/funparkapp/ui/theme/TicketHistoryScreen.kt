@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.funparkapp.ui.theme


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.funparkapp.data.PurchaseHistoryViewModel
import com.example.funparkapp.data.SharedViewModel


@Composable
fun TicketPurchasedHistoryScreen(
    purchaseHistoryViewModel: PurchaseHistoryViewModel,
    sharedViewModel: SharedViewModel,
    viewTicket: () -> Unit
) {
    val allTickets by purchaseHistoryViewModel.allPurchasedTickets.observeAsState(emptyList())
    var sortOption by remember { mutableStateOf("Most Recent") }

    val sortedTickets = when (sortOption) {
        "Most Recent" -> allTickets.sortedByDescending { it.purchase.purchasedDate }
        "Oldest" -> allTickets.sortedBy { it.purchase.purchasedDate }
        else -> allTickets
    }

    Column(modifier = Modifier.padding(15.dp)) {
        SortOptionsDropdown(sortOption = sortOption, onOptionSelected = { sortOption = it })
        LazyColumn(modifier = Modifier.padding(top = 15.dp)) {
            items(sortedTickets) { purchaseHistory ->
                TicketDetailCard(
                    ticketId = purchaseHistory.purchase.id,
                    purchasedDate = purchaseHistory.purchase.purchasedDate,
                    pricePaid = purchaseHistory.purchase.pricePaid,
                    viewTicket = {
                        sharedViewModel.ticketId = purchaseHistory.purchase.id
                        viewTicket()
                    }
                )
            }
        }
    }
}


@Composable
fun SortOptionsDropdown(sortOption: String, onOptionSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val sortOptions = listOf("Most Recent", "Oldest")

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 0.dp)
    ) {
        TextField(
            value = sortOption,
            onValueChange = {},
            readOnly = true,
            label = { Text("Sort by date") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
                .padding(14.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF0F0F0),
                unfocusedContainerColor = Color(0xFFF0F0F0),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Gray
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(10.dp))
        ) {
            sortOptions.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(Color.White)
                )
            }
        }
    }
}


@Composable
fun TicketDetailCard(
    ticketId: Long,
    purchasedDate: Date,
    pricePaid: Double,
    viewTicket: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val formattedDate = dateFormatter.format(purchasedDate)

    Card(
        modifier = Modifier
            .padding(15.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Ticket ID: $ticketId",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 15.dp, top = 10.dp)
                )
                Text(
                    text = "Price Paid: RM${"%.2f".format(pricePaid)}",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
                Text(
                    text = "Purchased On: $formattedDate",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = viewTicket,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333)),
                    modifier = Modifier
                        .width(180.dp)
                        .padding(top = 8.dp, bottom = 8.dp)
                ) {
                    Text(
                        text = "View Ticket",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
