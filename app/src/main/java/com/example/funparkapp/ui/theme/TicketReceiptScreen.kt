package com.example.funparkapp.ui.theme

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.funparkapp.R
import com.example.funparkapp.data.PurchaseHistoryViewModel
import com.example.funparkapp.data.SharedViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TicketReceiptScreen(
    purchaseHistoryViewModel: PurchaseHistoryViewModel,
    sharedViewModel: SharedViewModel,
    homePageClick: () -> Unit = {}
) {
    val ticketID = sharedViewModel.ticketId ?: return
    val purchaseWithTickets by purchaseHistoryViewModel.getPurchaseWithTicketsById(ticketID).observeAsState()
    val scrollState = rememberScrollState()

    purchaseWithTickets?.let { purchaseWithTickets ->
        val purchase = purchaseWithTickets.purchase
        val ticketItems = purchaseWithTickets.purchasedItems
        val qrCodeBitmap = generateQRCode(ticketID.toString())
        val validTo = calculateValidToDate(purchase.ticketPlan, purchase.purchasedDate)

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            qrCodeBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "QR Code",
                    modifier = Modifier.size(250.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(text = "Ticket ID: ", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = ticketID.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(20.dp))
            TicketDetailRow(label = "Valid From", value = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(purchase.purchasedDate))
            TicketDetailRow(label = "Valid To", value = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(validTo))
            TicketDetailRow(label = "Ticket Plan", value = purchase.ticketPlan)

            ticketItems.forEach { ticketItem ->
                TicketDetailRow(label = "Ticket Type", value = ticketItem.ticketType)
                TicketDetailRow(label = "Qty", value = ticketItem.qty.toString())
            }

            TicketDetailRow(label = "Total Price Paid", value = "RM${purchase.pricePaid.format(2)}")
            Spacer(modifier = Modifier.height(20.dp))

            Text(stringResource(R.string.return_home), fontWeight = FontWeight.Light)
            Spacer(modifier = Modifier.height(2.dp))
            Button(
                onClick = { homePageClick() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00))
            ) {
                Text(text = "Home", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    } ?: run {
        Text("Loading...")
    }
}

@Composable
fun TicketDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Box(
            modifier = Modifier
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(text = value)
        }
    }
}

private fun Double.format(digits: Int) = "%.${digits}f".format(this)

fun calculateValidToDate(ticketPlan: String, payDate: Date): Date {
    val calendar = java.util.Calendar.getInstance()
    calendar.time = payDate
    when (ticketPlan) {
        "One Day Pass" -> calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
        "Two Day Pass" -> calendar.add(java.util.Calendar.DAY_OF_YEAR, 2)
        "One Year Pass" -> calendar.add(java.util.Calendar.YEAR, 1)
    }
    return calendar.time
}


fun generateQRCode(content: String): Bitmap? {
    val writer = QRCodeWriter()
    return try {
        val bitMatrix: BitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 300, 300)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) AndroidColor.BLACK else AndroidColor.WHITE)
            }
        }
        bitmap
    } catch (e: WriterException) {
        e.printStackTrace()
        null
    }
}
