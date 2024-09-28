package com.example.funparkapp.ui.theme

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.example.funparkapp.data.RedeemHistory
import com.example.funparkapp.data.RedeemHistoryViewModel
import com.example.funparkapp.data.UserViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import java.util.EnumMap

@Composable
fun RedemptionHistoryScreen(
    userViewModel: UserViewModel,
    redeemHistoryViewModel: RedeemHistoryViewModel
) {
    val loggedInUser by userViewModel.loggedInUser.collectAsState()
    val username = loggedInUser?.username
    var redemptionHistory by remember { mutableStateOf<List<RedeemHistory>>(emptyList()) }

    LaunchedEffect(key1 = username) {
        username?.let {
            redeemHistoryViewModel.getRedemptionHistoryByUsername(it).collect { history ->
                redemptionHistory = history
            }
        }
    }

    LazyColumn {
        items(redemptionHistory) { historyItem ->
            RedemptionHistoryItemWithQrCode(historyItem)
        }
    }
}

@Composable
fun RedemptionHistoryItemWithQrCode(historyItem: RedeemHistory) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center)
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Display history item details
            Text("Ticket Plan: ${historyItem.ticketPlan}")
            Text("Redeem Time: ${historyItem.redeemTime}")
            // ... other details

            // Display QR code
            val qrCodeBitmap = rememberQrCodeBitmap(historyItem.ticketPlan) // Generate QR code bitmap
            Image(
                bitmap = qrCodeBitmap.asImageBitmap(),
                contentDescription = "QR Code for ${historyItem.ticketPlan}",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun rememberQrCodeBitmap(content: String): Bitmap {
    val size = 256 // Adjust size as needed
    val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
    hints[EncodeHintType.CHARACTER_SET] = "UTF-8"

    val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
    val width = bitMatrix.width
    val height = bitMatrix.height
    val pixels = IntArray(width * height)

    for (y in 0 until height) {
        val offset = y * width
        for (x in 0 until width) {
            pixels[offset + x] = if (bitMatrix[x, y]) Color.Black.toArgb() else Color.White.toArgb()
        }
    }

    return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
        setPixels(pixels, 0, width, 0,0, width, height)
    }
}