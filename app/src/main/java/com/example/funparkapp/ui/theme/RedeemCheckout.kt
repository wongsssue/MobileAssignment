import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.funparkapp.data.CartItemViewModel
import com.example.funparkapp.data.SharedViewModel
import com.example.funparkapp.data.Ticket
import com.example.funparkapp.data.TicketType
import com.example.funparkapp.data.UserViewModel
import com.example.funparkapp.ui.theme.FunParkScreen1

@Composable
fun RedeemCheckoutScreen(
    navController: NavHostController,
    pointsRequired: Int,
    userViewModel: UserViewModel,
    ticketViewModel: TicketViewModel
) {
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var balance by remember { mutableStateOf(0) }
    val user = userViewModel.userState.collectAsState().value
    val ticketsWithTypes by ticketViewModel.allTicketsWithTypes.observeAsState(emptyList())

    // Find the ticket based on pointsRequired
    val claimedTicket = ticketsWithTypes.find { ticketWithType ->
        ticketWithType.ticketTypes.any { it.pointsRequired == pointsRequired }
    }?.ticket

    LaunchedEffect(user) {
        if (user != null) {
            balance = user.points - pointsRequired
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement= Arrangement.Center
    ) {
        claimedTicket?.let { ticket ->
            Text("Claimed Item: ${ticket.ticketPlan}", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(16.dp))
        }

        Text("Your Points: ${user?.points ?: 0}", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Points Required: $pointsRequired", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))

        if (balance >= 0.1) {
            Text("Balance: $balance", fontSize = 20.sp, color = Color.Green)
        } else {
            Text("Points not enough", fontSize = 20.sp, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showConfirmationDialog = true },
            enabled = balance >= 0 // Disable button if not enough points
        ) {
            Text("Confirm")
        }

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