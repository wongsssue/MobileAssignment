package com.example.funparkapp.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
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
import com.example.funparkapp.data.Reservation
import com.example.funparkapp.data.Facility
import com.example.funparkapp.data.FacilityViewModel
import com.example.funparkapp.data.ReservationViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun ReservationSummaryScreen(
    facilityImage: Int,
    facilityName: String,
    reservationTime: String,
    reservationPax: String,
    reservationViewModel: ReservationViewModel,
    facilityViewModel: FacilityViewModel,
    goToCancel: (String) -> Unit,
    navController: NavHostController
) {
    val context = LocalContext.current
    val facilityID by facilityViewModel.getFacilityIDByName(facilityName)
        .observeAsState(initial = "")
    var isFormVisible by remember { mutableStateOf(false) }
    var reservationCount by remember { mutableStateOf(0) }

    reservationViewModel.getReservationCount { count ->
        reservationCount = count
    }

    val updatedReservationCount = reservationCount + 1
    val reservationID = if (updatedReservationCount < 10) {
        "R00$updatedReservationCount" // For single-digit numbers
    } else {
        "R0$updatedReservationCount" // For double-digit numbers
    }

    // Only create the reservation object once the facility ID is available
    val reservation = Reservation(
        reservationID = reservationID,
        reservationTime = reservationTime,
        reservationPax = reservationPax,
        facilityName = facilityName,
        purchasedDate = Date()
    )

    Column(
        modifier = Modifier
            .background(Color(0xFFFFE5B4))
            .padding(16.dp)
            .fillMaxSize()
    ) {
        // Show confirmation dialog if `isFormVisible` is true
        if (isFormVisible) {
            ShowCustomConfirmationDialog(
                onCancelClick = { isFormVisible = false },
                onSubmitClick = {
                    reservationViewModel.insert(reservation)
                    isFormVisible = false
                    navController.navigate("reservation_done_screen/${reservationID}")
                }
            )
        }

        // Title
        Text(
            text = "Reservation Summary",
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 30.dp),
            style = TextStyle(color = Color.Black)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Facility Image
        Image(
            painter = painterResource(id = facilityImage),
            contentDescription = "Facility Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .align(Alignment.CenterHorizontally)
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Facility Name
        Text(
            text = facilityName,
            fontWeight = FontWeight.Bold,
            fontSize = 21.sp,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 20.dp),
            style = TextStyle(color = Color.Black)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Time
        Text(
            text = "Time: $reservationTime",
            fontWeight = FontWeight.Bold,
            fontSize = 21.sp,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 20.dp),
            style = TextStyle(color = Color.Black)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Pax
        Text(
            text = "No Of Pax: $reservationPax",
            fontWeight = FontWeight.Bold,
            fontSize = 21.sp,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 20.dp),
            style = TextStyle(color = Color.Black)
        )

        Spacer(modifier = Modifier.height(70.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Cancel Button
            Button(
                onClick = {goToCancel("no")},
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)),
                modifier = Modifier.width(160.dp)
            ) {
                Text(
                    text = "Cancel",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Confirm Button
            Button(
                onClick = {
                    isFormVisible = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)),
                modifier = Modifier.width(160.dp)
            ) {
                Text(
                    text = "Confirm",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ShowCustomConfirmationDialog(
    onCancelClick: () -> Unit,
    onSubmitClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .size(250.dp, 200.dp)
            .background(Color(0xFFFFA500)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
                .background(Color(0xFFFFA500)),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
                    .background(Color(0xFFFFA500))
            ) {
                // Dialog Message
                Text(
                    text = "Confirm want to save reservation?",
                    color = Color.White,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    // "No" Button
                    Button(
                        onClick = onCancelClick,
                        modifier = Modifier
                            .width(100.dp)
                            .background(Color.Blue),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Blue
                        )
                    ) {
                        Text(text = "No", color = Color.White)
                    }

                    // "Yes" Button
                    Button(
                        onClick = onSubmitClick,
                        modifier = Modifier
                            .width(100.dp)
                            .background(Color.Blue),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Blue
                        )
                    ) {
                        Text(text = "Yes", color = Color.White)
                    }
                }
            }
        }
    }
}
