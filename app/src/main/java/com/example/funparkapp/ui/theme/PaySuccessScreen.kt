package com.example.funparkapp.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.funparkapp.R


@Composable
fun PaySuccessScreen(
    viewReceipt: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.paysuccess),
            contentDescription = stringResource(R.string.paySuccess),
            modifier = Modifier.size(350.dp)
        )
        Text(
            text = "Thank You !",
            fontSize = 45.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF00A170)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.paySuccess),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(60.dp))
        Button(
            onClick = { viewReceipt() },
            modifier = Modifier
                .width(200.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A170))
        ) {
            Text(
                text = "View Ticket",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

