package com.example.funparkapp.ui.theme

import TicketViewModel
import androidx.annotation.DrawableRes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.funparkapp.R
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect


@Composable
fun SelectTicketScreen(
    ticketViewModel: TicketViewModel,
    goToShoppingCart: () -> Unit,
    goToSpecificTicketPlan: (String) -> Unit
) {

//    val tickets by ticketViewModel.allTickets.observeAsState(emptyList())
    val firebaseTickets by ticketViewModel.firebaseTickets.observeAsState(emptyList())
    LaunchedEffect(Unit) {
        ticketViewModel.syncTicketsFromFirebase()
    }
    Column(modifier = Modifier
        .fillMaxSize()
       ) {
        Box(
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 0.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 2.dp)
                ) {
                    Text(
                        text = stringResource(R.string.save_time),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Box(
                    modifier = Modifier
                        .size(45.dp)
                ) {
                    Image(
                        painter = painterResource(R.mipmap.ic_launcher),
                        contentDescription = stringResource(R.string.shoppingCart),
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                goToShoppingCart()
                            }
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(firebaseTickets) { ticket ->
                TicketCard(
                    ticketPlan = ticket.ticketPlan,
                    ticketImg = ticket.imageResId,
                    ticketPlanDescription = ticket.ticketPlanDescription,
                    getTicketClick = { goToSpecificTicketPlan(ticket.ticketPlan) }
                )
            }
        }
    }
}

@Composable
fun TicketCard(
    @DrawableRes ticketImg: Int,
    ticketPlan: String,
    ticketPlanDescription: String,
    getTicketClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(start = 0.dp, top = 10.dp, end = 0.dp, bottom = 10.dp)
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Image(
            painter = painterResource(ticketImg),
            contentDescription =ticketPlan,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = ticketPlan,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = ticketPlanDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { getTicketClick() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF8C00)
                )
            ) {
                Text(text = "Get Ticket")
            }
        }
    }
}

