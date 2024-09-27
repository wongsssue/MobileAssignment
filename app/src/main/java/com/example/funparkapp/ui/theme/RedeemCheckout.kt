package com.example.funparkapp.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.funparkapp.data.CartItemViewModel
import com.example.funparkapp.data.SharedViewModel
import com.example.funparkapp.data.Ticket
import com.example.funparkapp.data.TicketType
import com.example.funparkapp.data.UserViewModel

@Composable
fun RedemptionCheckoutScreen(
    ticket: Ticket,
    ticketTypes: List<TicketType>,
    userPoints: Int,
    onConfirmRedemption: () -> Unit,
    onCancel: () -> Unit
) {
    val adultTicketType = ticketTypes.find { it.ticketType == "Adult" }
    val pointsRequired = adultTicketType?.pointsRequired ?: 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Redeem Ticket", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Ticket Plan: ${ticket.ticketPlan}")
        Text("Points Required: $pointsRequired")
        Text("Your Points: $userPoints")

        Spacer(modifier = Modifier.height(16.dp))

        if (userPoints >= pointsRequired) {
            Button(onClick = onConfirmRedemption) {
                Text("Confirm Redemption")
            }
        } else {
            Text("You do not have enough points to redeem this ticket.", color = Color.Red)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onCancel) {
            Text("Cancel")
        }
    }
}
