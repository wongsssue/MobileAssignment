package com.example.funparkapp.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.funparkapp.R

@Composable
fun ReservationDoneScreen(
    reservationID: String,
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFE5B4)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.reservation_success),
            contentDescription = null,
            modifier = Modifier
                .size(250.dp)
                .padding(top = 60.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "We’ve got you confirmed for your reservation!",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(250.dp)
                .padding(top = 30.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "ReservationID: $reservationID",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2356B8),
            textAlign = TextAlign.Center,
            modifier = Modifier.width(250.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = { navController.navigate(FunParkScreen.MainMenu.name) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)),
            modifier = Modifier
                .width(250.dp)
                .padding(top = 40.dp)
        ) {
            Text(
                text = "Done",
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { navController.navigate("reservation_qr_screen/no/${reservationID}") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)),
            modifier = Modifier
                .width(250.dp)
                .padding(top = 20.dp)
        ) {
            Text(
                text = "Download QR",
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
