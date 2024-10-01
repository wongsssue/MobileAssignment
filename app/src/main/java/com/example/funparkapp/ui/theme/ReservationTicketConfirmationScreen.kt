package com.example.funparkapp.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.funparkapp.R
import com.example.funparkapp.data.PurchaseHistoryViewModel
import com.example.funparkapp.globalVariable.TicketIDName
import java.util.Date

@Composable
fun ReservationTicketConfirmationScreen(
    purchaseHistoryViewModel: PurchaseHistoryViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    var ticketID by remember { mutableStateOf(0L) }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.amusement_park), // Change to appropriate drawable resource
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Transparent black overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x6DFFFFFF))
        )

        // Centered content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.Center)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Enter Ticket ID Label
                Text(
                    text = "Enter Ticket ID:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color.Black
                )

                // Ticket ID TextBox
                BasicTextField(
                    value = ticketID.toString(), // Convert Long to String for display
                    onValueChange = {
                        ticketID = it.toLongOrNull() ?: 0L // Convert String input to Long, default to 0 if invalid
                    },
                    modifier = Modifier
                        .background(Color(0xFFD9D9D9))
                        .fillMaxWidth()
                        .padding(12.dp)
                )

                // Display error message if any
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }

                // Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Skip Button
                    TextButton(
                        onClick = {
                            // Skip logic: Pass viewOnly = true
                            navController.navigate("${FunParkScreen.RVMainScreen.name}/yes")
                        }
                    ) {
                        Text(
                            text = "Skip",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1373CB)
                        )
                    }

                    // Next Button
                    Button(
                        onClick = {
                            if (ticketID == 0L) {
                                // If ticketID is 0 (default value), show error
                                errorMessage = "Please enter a valid Ticket ID"
                            } else {
                                // Check if the ticket exists
                                purchaseHistoryViewModel.getPurchasedItemByTicketID(ticketID).observeForever { purchase ->
                                    if (purchase != null) {
                                        // Retrieve validFrom and validTo from the purchase object
                                        val validFrom = purchase.validFrom
                                        val validTo = purchase.validTo

                                        val today = Date() // Current date
                                        // Check if today's date is within the validFrom and validTo range
                                        if (today < validFrom || today > validTo) {
                                            errorMessage = "Ticket expired!"
                                        } else {
                                            // Ticket is valid, go to RV with viewOnly = false
                                            TicketIDName.ticketIDName = ticketID
                                            navController.navigate("${FunParkScreen.RVMainScreen.name}/no")
                                        }
                                    } else {
                                        // Ticket not found
                                        errorMessage = "Ticket not found!"
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)),
                        modifier = Modifier.width(160.dp)
                    ) {
                        Text(
                            text = "Next",
                            color = Color.White
                        )
                    }

                }
            }
        }
    }
}
