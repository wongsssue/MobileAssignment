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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.funparkapp.data.FacilityViewModel
import com.example.funparkapp.data.ReservationViewModel

@Composable
fun ReservationViewScreen(
    reservationViewModel: ReservationViewModel,
    facilityViewModel: FacilityViewModel,
    goToSpecificResv: (String) -> Unit // Function to navigate to specific reservation
) {
    val reservations by reservationViewModel.allReservation.observeAsState(emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD9D9D9)) // Set background color
    ) {
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
                        reservationID = reservation.reservationID, // Pass reservation ID
                        onViewReservationClick = { goToSpecificResv(reservation.reservationID) } // Pass reservation ID to the function
                    )
                }
            }
        }
    }
}

@Composable
fun ReservationCard(
    facilityName: String,
    facilityImage: Int,
    reservationID: String,
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
                Spacer(modifier = Modifier.height(15.dp))

                // Facility Name
                Text(
                    text = facilityName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
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
