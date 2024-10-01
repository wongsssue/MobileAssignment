package com.example.funparkapp.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.funparkapp.data.FacilityViewModel
import com.example.funparkapp.data.ReservationViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReservationViewScreen(
    reservationViewModel: ReservationViewModel,
    facilityViewModel: FacilityViewModel,
    navController: NavHostController // Function to navigate to specific reservation
) {
    val reservations by reservationViewModel.allReservation.observeAsState(emptyList())
    var sortOption by remember { mutableStateOf("Most Recent") }

    val sortedResv = when (sortOption) {
        "Most Recent" -> reservations.sortedByDescending { it.purchasedDate }
        "Oldest" -> reservations.sortedBy { it.purchasedDate }
        else -> reservations
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD9D9D9)) // Set background color
    ) {
        if (reservations.isEmpty()) {
            // Display "No reservation made" if the reservations list is empty
            Text(
                text = "No reservation made",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Red,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
        } else {
            SortOptionsDropdownResv(sortOption = sortOption, onOptionSelected = { sortOption = it })
            // LazyColumn for displaying reservations
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .background(Color.Transparent)
            ) {
                items(reservations) { reservation ->
                    // Retrieve the facility for each reservation
                    val facility by facilityViewModel.getFacilityByName(reservation.facilityName).observeAsState()

                    facility?.let {
                        ReservationCard(
                            facilityName = it.facilityName,
                            facilityImage = it.facilityImage,
                            reservationID = reservation.reservationID,
                            purchasedDate = reservation.purchasedDate,
                            onViewReservationClick = { navController.navigate("reservation_qr_screen/yes/${reservation.reservationID}") } // Pass reservation ID to the function
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortOptionsDropdownResv(sortOption: String, onOptionSelected: (String) -> Unit) {
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
fun ReservationCard(
    facilityName: String,
    facilityImage: Int,
    reservationID: String,
    purchasedDate: Date,
    onViewReservationClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onViewReservationClick() }, // Handle item click here
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Facility Image
            Image(
                painter = painterResource(facilityImage),
                contentDescription = facilityName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(200.dp)
                    .height(160.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .fillMaxSize()
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Text Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                //reservation ID
                Text(
                    text = reservationID,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Facility Name
                Text(
                    text = facilityName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                //purchased date
                Text(
                    text = "Purchased Date: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(purchasedDate)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // View Reservation Button
                Button(
                    onClick = { onViewReservationClick() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00008B)), // Dark Blue color
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "View Reservation",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
