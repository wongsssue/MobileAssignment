package com.example.funparkapp.ui.theme

import TicketViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.funparkapp.data.Ticket
import com.example.funparkapp.data.TicketType
import com.example.funparkapp.data.UserViewModel

@Composable
fun RedeemScreen(
    ticketViewModel: TicketViewModel,
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    var selectedFilter by remember { mutableStateOf("Most Recent") }

    // Get all tickets with types from the view model
    val ticketsWithTypes by ticketViewModel.allTicketsWithTypes.observeAsState(emptyList())

    val sortedTickets = when (selectedFilter) {
        "Lowest Points" -> ticketsWithTypes.sortedBy {
            it.ticketTypes.find { type -> type.ticketType == "Adult" }?.pointsRequired ?: Int.MAX_VALUE
        }
        "Highest Points" -> ticketsWithTypes.sortedByDescending {
            it.ticketTypes.find { type -> type.ticketType == "Adult" }?.pointsRequired ?: Int.MIN_VALUE
        }
        else -> ticketsWithTypes // Default case
    }

    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showRedeemDialog by remember { mutableStateOf(false) }
    var selectedTicket by remember { mutableStateOf<Ticket?>(null) }
    var balance by remember { mutableStateOf(0)}
    val user = userViewModel.userState.collectAsState().value


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFE1B3))
            .padding(16.dp)
    ) {
        // Filters Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spinner(
                selectedOption = selectedFilter,
                options = listOf("Lowest Points", "Highest Points"),
                onOptionSelected = { selectedFilter = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Scrollable ticket list using LazyColumn
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxHeight()
        ) {
            items(sortedTickets) { ticketWithType ->
                TicketCard(
                    ticket = ticketWithType.ticket,
                    ticketTypes = ticketWithType.ticketTypes,
                    onClaim = { ticket ->
                        selectedTicket = ticket
                        showRedeemDialog = true
                    }


                )
            }
        }

        // Redeem Dialog
        if (showRedeemDialog) {
            val ticketWithType = ticketsWithTypes.find { it.ticket == selectedTicket } // Find the TicketWithType object
            val pointsRequired =ticketWithType?.ticketTypes?.find { it.ticketType == "Adult" }?.let { it.pointsRequired } ?: 0
            LaunchedEffect(user, selectedTicket) { // Add selectedTicket as a key
                if (user != null && selectedTicket != null) {
                    balance = user.points - pointsRequired
                }
            }

            AlertDialog(
                onDismissRequest = { showRedeemDialog = false },
                title = { Text("Redeem Item") },
                text = {
                    Column {
                        selectedTicket?.let { ticket ->
                            Text("Claimed Item: ${ticket.ticketPlan}", fontSize = 16.sp)
                        }
                        Text("Your Points: ${user?.points ?: 0}", fontSize = 16.sp)
                        Text("Points Required: $pointsRequired", fontSize = 16.sp)
                        if (balance >= 0) {
                            Text("Balance: $balance", fontSize = 16.sp, color = Color.Green)
                        } else {
                            Text("Points not enough", fontSize = 16.sp, color = Color.Red)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showRedeemDialog = false
                            if (balance >= 0) {
                                showConfirmationDialog = true
                                // TODO: Implement actual redemption logic here (update points, etc.)
                            }
                        },
                        enabled = balance >= 0
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    Button(onClick = { showRedeemDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Confirmation Dialog
        if (showConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmationDialog = false },
                title = { Text("Confirmation") },
                text = { Text("Successfully claimed!") },
                confirmButton = {
                    Button(onClick = {
                        showConfirmationDialog = false
                        navController.navigate(FunParkScreen1.MainMenu.name)
                    }) {
                        Text("Go to Home")
                    }
                }
            )
        }
    }
}

@Composable
fun Spinner(
    selectedOption: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { expanded = true } // Click to open options
    ) {
        // Text to display the selected option
        Text(
            text = selectedOption,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black
        )

        // Display options if expanded
        if (expanded) {
            // Use a LazyColumn to show the options
            LazyColumn {
                items(options) { option ->
                    Text(
                        text = option,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onOptionSelected(option) // Select option
                                expanded = false // Close the spinner after selection
                            }
                            .padding(8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun TicketCard(ticket: Ticket, ticketTypes: List<TicketType>, onClaim: (Ticket) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Use imageResId from the Ticket object
            Image(
                painter = painterResource(ticket.imageResId),
                contentDescription = ticket.ticketPlan,
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Display ticket plan from Ticket
            Text(text = ticket.ticketPlan, style = MaterialTheme.typography.titleLarge)

            // Show the description of the ticket plan
            Text(text = ticket.ticketPlanDescription, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            // Filter ticket types to find the "Adult" type
            val adultTicketType = ticketTypes.find { it.ticketType == "Adult" }
            adultTicketType?.let { type ->
                Text(text = "Points Required: ${type.pointsRequired}", style = MaterialTheme.typography.bodyMedium)
            }

            Button(onClick = { onClaim(ticket) }) {
                Text(text = "Claim")
            }
        }
    }
}