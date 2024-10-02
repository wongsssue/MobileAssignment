package com.example.funparkapp.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.funparkapp.R
import com.example.funparkapp.data.Souvenir
import com.example.funparkapp.data.SouvenirViewModel

@Composable
fun AdminScreen(
    souvenirViewModel: SouvenirViewModel,
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Admin Panel",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            SquareButton(
                text = "Add Souvenir",
                color = Color(0xFFFFA500),
                onClick = { navController.navigate("add_souvenir") }
            )

            SquareButton(
                text = "Delete Souvenir",
                color = Color(0xFFFFA500),
                onClick = { navController.navigate("delete_souvenir") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SquareButton(
                text = "View Souvenirs",
                color = Color(0xFFFFA500),
                onClick = { navController.navigate("view_souvenir") }
            )

            SquareButton(
                text = "Modify Souvenir",
                color = Color(0xFFFFA500),
                onClick = { navController.navigate("modify_souvenir") }
            )
        }
    }
}

@Composable
fun SquareButton(
    text: String,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(150.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = Color.White, fontWeight = FontWeight.Bold)
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSouvenirScreen(
    souvenirViewModel: SouvenirViewModel,
    navController: NavHostController
) {
    var souvenirName by remember { mutableStateOf("") }
    var souvenirPrice by remember { mutableStateOf("") }
    var selectedImageRes by remember { mutableStateOf(R.drawable.dragon) }

    val imageResources = listOf(R.drawable.dragon, R.drawable.harry, R.drawable.toy)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Add New Souvenir",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            TextField(
                value = souvenirName,
                onValueChange = { souvenirName = it },
                label = { Text("Souvenir Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Blue,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedLabelColor = Color.Blue,
                    unfocusedLabelColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = souvenirPrice,
                onValueChange = { souvenirPrice = it },
                label = { Text("Souvenir Price") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Blue,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedLabelColor = Color.Blue,
                    unfocusedLabelColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Select Image", fontWeight = FontWeight.Bold, fontSize = 22.sp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                imageResources.forEach { imageRes ->
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .clickable { selectedImageRes = imageRes }
                            .border(2.dp, if (selectedImageRes == imageRes) Color.Blue else Color.Gray, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    souvenirViewModel.addSouvenir(
                        Souvenir(name = souvenirName, price = souvenirPrice.toDouble(), imageResource = selectedImageRes)
                    )
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
            ) {
                Text(text = "Add Souvenir", color = Color.White)
            }
        }
    }
}




@Composable
fun DeleteSouvenirScreen(
    souvenirViewModel: SouvenirViewModel,
    navController: NavHostController
) {
    val souvenirs by souvenirViewModel.souvenirList.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).background(Color(0xFFF7F7F7))) {
        Text(text = "Delete Souvenir", fontWeight = FontWeight.Bold, fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

        LazyColumn {
            items(souvenirs) { souvenir ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(Color.White)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            souvenirViewModel.deleteSouvenir(souvenir)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = souvenir.name, modifier = Modifier.weight(1f), color = Color.Black)
                    Text(text = "Delete", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


@Composable
fun ViewSouvenirsScreen(souvenirViewModel: SouvenirViewModel) {
    val souvenirs by souvenirViewModel.souvenirList.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp) // Increased padding
            .background(Color(0xFFF7F7F7))
    ) {
        Text(
            text = "Souvenir List",
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyColumn {
            items(souvenirs) { souvenir ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(Color.White)
                        .clip(RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = souvenir.imageResource),
                        contentDescription = null,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = souvenir.name,
                            modifier = Modifier.padding(bottom = 4.dp),
                            color = Color.Black,
                            fontSize = 22.sp
                        )
                        Text(
                            text = "RM ${souvenir.price}",
                            color = Color.Gray,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifySouvenirScreen(
    souvenirViewModel: SouvenirViewModel,
    navController: NavHostController
) {
    var selectedSouvenir by remember { mutableStateOf<Souvenir?>(null) }
    val souvenirs by souvenirViewModel.souvenirList.collectAsState()
    var isModifying by remember { mutableStateOf(false) }

    // Validation error state
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .background(Color(0xFFF7F7F7))
    ) {
        Text(
            text = "Modify Souvenir",
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyColumn {
            items(souvenirs) { souvenir ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(Color.White)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(enabled = !isModifying) {
                            selectedSouvenir = souvenir
                            isModifying = true
                        }
                ) {
                    Text(
                        text = souvenir.name,
                        modifier = Modifier.weight(1f),
                        color = Color.Black,
                        fontSize = 20.sp
                    )
                }
            }
        }

        selectedSouvenir?.let { souvenir ->
            var souvenirName by remember { mutableStateOf(souvenir.name) }
            var souvenirPrice by remember { mutableStateOf(souvenir.price.toString()) }

            Spacer(modifier = Modifier.height(24.dp))

            // Enhanced TextField for Souvenir Name
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
            ) {
                TextField(
                    value = souvenirName,
                    onValueChange = { souvenirName = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    textStyle = TextStyle(fontSize = 20.sp),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Enhanced TextField for Souvenir Price
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
            ) {
                TextField(
                    value = souvenirPrice,
                    onValueChange = { souvenirPrice = it },
                    label = { Text("Price") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), // Ensure only number input
                    textStyle = TextStyle(fontSize = 20.sp),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Display error message if validation fails
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        // Validate input
                        if (souvenirName.isBlank()) {
                            errorMessage = "Souvenir name cannot be empty"
                        } else if (souvenirPrice.isBlank() || souvenirPrice.toDoubleOrNull() == null) {
                            errorMessage = "Please enter a valid price"
                        } else {
                            // If valid, modify souvenir
                            souvenirViewModel.modifySouvenir(
                                souvenir.copy(name = souvenirName, price = souvenirPrice.toDouble())
                            )
                            navController.popBackStack()
                            isModifying = false
                            selectedSouvenir = null
                            errorMessage = "" // Clear error message after success
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                ) {
                    Text(text = "Modify", color = Color.White, fontSize = 20.sp)
                }

                Button(
                    onClick = {
                        isModifying = false
                        selectedSouvenir = null
                        errorMessage = "" // Clear error message when canceling
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(text = "Cancel", color = Color.White, fontSize = 20.sp)
                }
            }
        }
    }
}






