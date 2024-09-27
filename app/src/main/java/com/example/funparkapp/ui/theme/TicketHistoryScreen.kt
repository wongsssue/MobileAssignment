package com.example.funparkapp.ui.theme


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.livedata.observeAsState
import com.example.funparkapp.data.PurchaseHistoryViewModel
import com.example.funparkapp.data.SharedViewModel

@Composable
fun TicketPurchasedHistoryScreen(
    purchaseHistoryViewModel: PurchaseHistoryViewModel,
    sharedViewModel: SharedViewModel,
    viewTicket: () -> Unit,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    val allTickets by purchaseHistoryViewModel.allPurchasedTickets.observeAsState(emptyList())

    LazyColumn(modifier = Modifier.padding(15.dp)) {
        items(allTickets) { purchaseHistory ->
            TicketDetailCard(
                ticketId = purchaseHistory.purchase.id,
                ticketPlan = purchaseHistory.purchase.ticketPlan,
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

@Composable
fun TicketDetailCard(
    ticketId: Long,
    ticketPlan: String,
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
                    text = ticketPlan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Ticket ID: $ticketId",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 5.dp)
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
