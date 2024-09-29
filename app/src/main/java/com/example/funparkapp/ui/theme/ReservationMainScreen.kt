package com.example.funparkapp.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.funparkapp.data.FacilityViewModel

@Composable
fun ReservationMainScreen(
    viewOnly: String,
    facilityViewModel: FacilityViewModel,
    navController: NavHostController,
) {
    val facilities by facilityViewModel.allActiveFacility.observeAsState(emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD9D9D9)) // Set background color
    ) {
        // LazyColumn for displaying facilities
        LazyColumn(
            modifier = Modifier.weight(1f)
                .background(Color.Transparent)
        ) {
            items(facilities) { facility ->
                FacilityCard(
                    facilityName = facility.facilityName,
                    facilityDesc = facility.facilityDesc,
                    facilityImage = facility.facilityImage,
                    onFacilityClick = {
                        navController.navigate("reservation_selection_screen/${viewOnly}/${facility.facilityName}")
                    }
                )
            }
        }

        // Show Text only when scrolling ends
        Text(
            text = "*These attractions are exclusive to VQ. Please reserve to avoid disappointment.",
            fontSize = 16.sp,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth() // Make sure it takes the full width
                .padding(top = 16.dp) // Add some spacing above the text
        )
    }
}

@Composable
fun FacilityCard(
    facilityName: String,
    facilityDesc: String,
    facilityImage: Int,
    onFacilityClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onFacilityClick() }, // Handle item click here
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Facility Image
            Image(
                painter = painterResource(facilityImage),
                contentDescription = facilityName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(200.dp)
                    .height(160.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .fillMaxSize()
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Text Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                // Facility Name
                Text(
                    text = facilityName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Facility Description
                Text(
                    text = facilityDesc,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
