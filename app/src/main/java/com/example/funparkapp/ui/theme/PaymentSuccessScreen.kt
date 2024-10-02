package com.example.funparkapp.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.funparkapp.R

@Composable
fun PaymentSuccessScreen(onViewHistory: () -> Unit, onBackToHome: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.paysuccess),
            contentDescription = "Payment Success",
            modifier = Modifier.size(150.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Payment Successful!", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        // View Purchase History Button
        Button(
            onClick = onViewHistory,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)), // Set button color to orange
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("View Purchase History", color = Color.White) // Set text color to white
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Back to Home Button
        Button(
            onClick = onBackToHome,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)), // Set button color to orange
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Back to Home", color = Color.White) // Set text color to white
        }
    }
}

