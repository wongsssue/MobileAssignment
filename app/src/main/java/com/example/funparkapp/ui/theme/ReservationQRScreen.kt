package com.example.funparkapp.ui.theme

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
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
import com.example.funparkapp.data.ReservationViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ReservationQRScreen(
    viewCancel: String,
    reservationID: String,
    reservationViewModel: ReservationViewModel,
    navController: NavHostController,
    goToDone: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val reservation by reservationViewModel.getReservationById(reservationID).observeAsState()

    reservation?.let { r ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFE5B4)) // Light orange background
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Reservation ID Title
            Text(
                text = "Reservation ID: $reservationID",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // QR Code
            Image(
                painter = painterResource(id = R.drawable.qr_code), // Replace with actual QR code resource
                contentDescription = "QR Code",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .size(310.dp)
                    .padding(bottom = 8.dp)
            )

            // QR Code Note
            Text(
                text = "Note: Please present your QR code at the counter.",
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, Color.Black))
                    .background(Color.Transparent)
                    .padding(16.dp)

            ) {
                // Facility Name
                Text(
                    text = "Facility: ${r.facilityName}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Spacer(modifier = Modifier.height(15.dp))

                // Date
                Text(
                    text = "Date: ${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(r.purchasedDate)}",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Spacer(modifier = Modifier.height(15.dp))

                // Slot Time
                Text(
                    text = "Slot: ${r.reservationTime}",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Spacer(modifier = Modifier.height(15.dp))

                // No of Pax
                Text(
                    text = "No Of Pax: ${r.reservationPax}",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Spacer(modifier = Modifier.height(15.dp))
            }


            Spacer(modifier = Modifier.height(40.dp))

            // Buttons for Cancel and Done
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if(viewCancel == "yes") {
                    // Cancel Button
                    Button(
                        onClick = {
                            reservationViewModel.deleteReservationById(reservationID)
                            Toast.makeText(context, "Reservation cancelled!", Toast.LENGTH_SHORT).show()
                            navController.navigate(FunParkScreen.MainMenu.name)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)),
                        modifier = Modifier
                            .width(130.dp)
                            .height(40.dp)
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }

                // Done Button
                Button(
                    onClick = { navController.navigate(FunParkScreen.MainMenu.name) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)), // Orange color
                    modifier = Modifier
                        .width(130.dp)
                        .height(40.dp)
                ) {
                    Text(
                        text = "Done",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}
