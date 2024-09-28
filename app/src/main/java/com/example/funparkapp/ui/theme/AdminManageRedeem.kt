package com.example.funparkapp.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.funparkapp.data.RedeemHistory
import com.example.funparkapp.data.RedeemHistoryViewModel

@Composable
fun ManageRedemptionsScreen(redeemHistoryViewModel: RedeemHistoryViewModel) {
    val redemptions by redeemHistoryViewModel.getAllRedemptions().collectAsState(initial = emptyList())

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "Total Redemptions: ${redemptions.size}",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(redemptions) { redemption ->
                RedemptionItemCard(redemption)
            }
        }
    }
}

@Composable
fun RedemptionItemCard(redemption: RedeemHistory) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Redemption ID: ${redemption.redeemId}")
            Text("Ticket Plan: ${redemption.ticketPlan}")
            Text("Redeem Time: ${redemption.redeemTime}")
            Text("Claimed By: ${redemption.username}")
        }
    }
}