package com.example.funparkapp.ui.theme

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.funparkapp.data.FacilityViewModel
import com.example.funparkapp.data.PurchaseHistory
import com.example.funparkapp.data.PurchaseHistoryViewModel
import com.example.funparkapp.data.PurchasedItem
import com.example.funparkapp.globalVariable.TicketIDName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

data class PurchaseWithTickets(
    val purchaseHistory: PurchaseHistory,
    val purchasedItems: List<PurchasedItem>
)

@Composable
fun ReservationSelectionScreen(
    viewOnly: String,
    facilityName: String,
    facilityViewModel: FacilityViewModel,
    purchaseHistoryViewModel: PurchaseHistoryViewModel,
    navController: NavHostController,
) {
    val context = LocalContext.current
    val facility by facilityViewModel.getFacilityByName(facilityName).observeAsState()
    val purchaseHistory by purchaseHistoryViewModel.getPurchaseByTicketID(TicketIDName.ticketIDName!!).observeAsState()

    val qtyMap by purchaseHistoryViewModel.qtyMap.observeAsState(emptyMap()) // Observe qtyMap// Calculate total quantity from qtyMap
    val totalQty = qtyMap.values.sum()

    // Update paxList based on totalQty
    var paxList by remember { mutableStateOf(getPaxList(totalQty)) }

    LaunchedEffect(qtyMap) { // Update paxList when qtyMap changes
        paxList = getPaxList(totalQty)
    }
    // States for spinner selections
    var selectedTime by remember { mutableStateOf("") }
    var selectedPax by remember { mutableStateOf("") }
    var timeSlots by remember { mutableStateOf(listOf<String>()) }

    facility?.let { f ->
        // Populate time slots
        if (timeSlots.isEmpty()) {
            timeSlots = populateTimeSlots(f.facilityTimeFrom, f.facilityTimeTo)
        }

        Column(
            modifier = Modifier
                .background(Color(0xFFFFE5B4))
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Top image
            Image(
                painter = painterResource(
                    id = context.resources.getIdentifier(
                        f.facilityImage.toString(),
                        "drawable",
                        context.packageName
                    )
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .height(220.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Facility title
            Text(
                text = f.facilityName,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 16.dp),
                style = TextStyle(color = Color.Black)
            )

            Spacer(modifier = Modifier.height(15.dp))

            // Facility description
            Text(
                text = f.facilityDesc,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 16.dp),
                style = TextStyle(color = Color.Gray)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Facility type
            Text(
                text = "Type: ${f.facilityType}",
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp),
                style = TextStyle(color = Color.Black)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Min height
            Text(
                text = "Min Height: ${f.facilityMinHeight}",
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp),
                style = TextStyle(color = Color.Black)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Supervising companion details
            Text(
                text = "Supervising Companion: ${f.facilitySvCompanion}",
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp),
                style = TextStyle(color = Color.Black)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Conditionally show unavailable or available section
            if (viewOnly == "yes") {
                // Unavailable layout
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFCFCBCB))
                ) {
                    Text(
                        text = "Oops, it appears you do not have a valid ticket. Please purchase or link an existing ticket.",
                        modifier = Modifier.padding(10.dp),
                        style = TextStyle(color = Color.Gray),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                }
            } else {
                // Available layout
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Please select a time slot",
                        fontSize = 16.sp,
                        color = Color.Black
                    )

                    // Time slot dropdown
                    DropdownMenu(
                        items = timeSlots,
                        selectedItem = selectedTime,
                        onItemSelected = { selectedTime = it }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Please select number of pax",
                        fontSize = 16.sp,
                        color = Color.Black
                    )

                    // Pax dropdown
                    DropdownMenu(
                        items = paxList,
                        selectedItem = selectedPax,
                        onItemSelected = { selectedPax = it }
                    )

                    Spacer(modifier = Modifier.height(60.dp))

                    // Reserve Now Button
                    Button(
                        onClick = {
                            if (selectedTime.isEmpty()) {
                                Toast.makeText(
                                    context,
                                    "Please select a time slot.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else if (selectedPax.isEmpty()) {
                                Toast.makeText(
                                    context,
                                    "Please select number of pax.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                navController.navigate("reservation_summary_screen/${facility!!.facilityImage}/${facility!!.facilityName}/${selectedTime}/${selectedPax}")
                            }
                        },

                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)),
                        modifier = Modifier
                            .size(180.dp, 60.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "Reserve Now",
                            color = Color.White,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// Function to populate time slots based on the given start and end times
private fun populateTimeSlots(
    facilityTimeFrom: String,
    facilityTimeTo: String
): List<String> {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val timeSlots = mutableListOf<String>()

    val startTime = timeFormat.parse(facilityTimeFrom)
    val endTime = timeFormat.parse(facilityTimeTo)

    if (startTime != null && endTime != null) {
        var currentTime = startTime.time

        while (currentTime <= endTime.time) {
            timeSlots.add(timeFormat.format(Date(currentTime)))
            currentTime += TimeUnit.MINUTES.toMillis(30)
        }
    }

    return timeSlots
}

// Dropdown Menu Composable
@Composable
fun DropdownMenu(
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }
            .border(BorderStroke(1.dp, Color.Black))
            .background(Color.Transparent)
            .padding(14.dp)
    ) {
        Text(
            text = if (selectedItem.isEmpty()) "Select an option" else selectedItem,
            color = Color.Black
        )

        androidx.compose.material3.DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun getPaxList(totalQty: Int): List<String> {
    return if (totalQty > 0) {
        (1..totalQty).map { it.toString() }
    } else {
        (1..15).map { it.toString() } // Default range if qtyMap is empty
    }
}
