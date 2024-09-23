package com.example.funparkapp.ui.theme

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.funparkapp.R

@Composable
fun MainMenuScreen(
    modifier: Modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
    onTicketClick: () -> Unit = {},
    onReserveClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            menuCard(
                menuImg = R.drawable.ticket,
                menuDescription = R.string.buy_ticket,
                onNextClick = onTicketClick
            )
            Spacer(modifier = Modifier.height(20.dp))
            menuCard(
                menuImg = R.drawable.reservation,
                menuDescription = R.string.reserve,
                onNextClick = onReserveClick
            )
        }
    }
}

@Composable
fun menuCard(
    @DrawableRes menuImg: Int,
    @StringRes menuDescription: Int,
    onNextClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                onNextClick() },
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(menuImg),
                contentDescription = stringResource(menuDescription),
                modifier = Modifier
                    .size(100.dp)
                    .clickable { onNextClick() }
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(menuDescription),
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainMenuPreview() {
    MaterialTheme {
        MainMenuScreen(
            onTicketClick = {},
            onReserveClick = {}
        )
    }
}
