package com.example.funparkapp.ui.theme


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.funparkapp.R
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.funparkapp.data.Facility
import com.example.funparkapp.data.FacilityViewModel
import com.example.funparkapp.data.ReservationViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun ReservationManagementScreen(
    FacilityViewModel: FacilityViewModel,
) {
    val context = LocalContext.current
    val facilities by FacilityViewModel.allFacility.observeAsState(emptyList())
    var facilityNameUpdate by remember { mutableStateOf("") }
    var facilityAvailability by remember { mutableStateOf(false) }
    var facilityDateFromOriginal by remember { mutableStateOf("") }
    var facilityDateToOriginal by remember { mutableStateOf("") }
    var facilityTimeFromOriginal by remember { mutableStateOf("") }
    var facilityTimeToOriginal by remember { mutableStateOf("") }
    var isFormVisibleAdd by remember { mutableStateOf(false) }
    var isFormVisibleUpdate by remember { mutableStateOf(false) }
    var facilityCount by remember { mutableStateOf(0) }

    // Call getReservationCount when the composable is first composed
    LaunchedEffect(Unit) {
        FacilityViewModel.getFacilityCount { count ->
            facilityCount = count // Update state with the result
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    isFormVisibleAdd = true  // You can implement form to add/edit facilities if required
                }
            ) {
                Icon(painter = painterResource(id = R.drawable.add), contentDescription = "Add Facility")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (isFormVisibleAdd) {
                FacilityFormAdd(
                    facilities,
                    facilityCount,
                    onCancelClick = { isFormVisibleAdd = false },
                    onSubmitClick = { facility: Facility ->
                        FacilityViewModel.insert(facility)
                        Toast.makeText(context, "Facility Added!", Toast.LENGTH_SHORT).show()
                        isFormVisibleAdd = false
                    }
                )
            }
            if (isFormVisibleUpdate) {
                FacilityFormUpdate(
                    facilityAvailability,
                    facilityDateFromOriginal,
                    facilityDateToOriginal,
                    facilityTimeFromOriginal,
                    facilityTimeToOriginal,
                    facilityNameUpdate,
                    onCancelClick = { isFormVisibleUpdate = false },
                    onSubmitClick = { parsedDateFrom, parsedDateTo, facilityTimeFrom, facilityTimeTo, isAvailable, facilityNameUpdate ->
                        FacilityViewModel.update(parsedDateFrom, parsedDateTo, facilityTimeFrom, facilityTimeTo, isAvailable, facilityNameUpdate)
                        val availabilityStatus = if (isAvailable) "available" else "unavailable"
                        Toast.makeText(context, "Facility: $facilityNameUpdate updated to $availabilityStatus!", Toast.LENGTH_SHORT).show()
                        isFormVisibleUpdate = false
                    }
                )
            }

            LazyColumn {
                items(facilities) { facility ->
                    FacilityCardManagement(
                        facilityAvailability = facility.facilityAvailability,
                        facilityTimeFrom = facility.facilityTimeFrom,
                        facilityTimeTo = facility.facilityTimeTo,
                        facilityName = facility.facilityName,
                        facilityImage = facility.facilityImage,
                        onEditClick = {
                            facilityAvailability = facility.facilityAvailability
                            facilityDateFromOriginal = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(facility.facilityDateFrom)
                            facilityDateToOriginal = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(facility.facilityDateTo)
                            facilityTimeFromOriginal = facility.facilityTimeFrom
                            facilityTimeToOriginal = facility.facilityTimeTo
                            facilityNameUpdate = facility.facilityName
                            isFormVisibleUpdate = true
                        },
                        onDeleteClick = {
                            FacilityViewModel.delete(facility.facilityName)
                            Toast.makeText(context, "Facility Deleted!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FacilityFormAdd(
    facilities: List<Facility>,
    count: Int,
    onCancelClick: () -> Unit,
    onSubmitClick: (Facility) -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    var facilityName by remember { mutableStateOf("") }
    var facilityDescription by remember { mutableStateOf("") }
    var facilityType by remember { mutableStateOf("") }
    var facilityMinHeight by remember { mutableStateOf("") }
    var facilitySvCompanion by remember { mutableStateOf("") }
    var isAvailable by remember { mutableStateOf(true) }
    var facilityDateFrom by remember { mutableStateOf("") }
    var facilityDateTo by remember { mutableStateOf("") }
    var facilityTimeFrom by remember { mutableStateOf("") }
    var facilityTimeTo by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    // Create launcher to select an image from the gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    fun uriToInt(context: Context, uri: Uri): Int {
        return context.resources.getIdentifier(uri.lastPathSegment, "drawable", context.packageName)
    }


    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Helper function to show DatePicker dialog
    fun showDatePicker(onDateSelected: (String) -> Unit) {
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = "$dayOfMonth/${month + 1}/$year"
                onDateSelected(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Set the minimum date to the current date
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()

        datePickerDialog.show()
    }

    // Helper function to show TimePicker dialog
    fun showTimePicker(onTimeSelected: (String) -> Unit) {
        val timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val selectedTime = String.format("%02d:%02d", hourOfDay, minute)

                // Check if the selected time is between 10:00 and 18:00
                if (hourOfDay in 10..17 || (hourOfDay == 18 && minute == 0)) {
                    onTimeSelected(selectedTime) // Valid time, proceed with selection
                } else {
                    Toast.makeText(context, "Please select a time between 10:00 and 18:00", Toast.LENGTH_SHORT).show()
                }
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // 24-hour format
        )

        // Show the time picker
        timePickerDialog.show()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Facility Name
                item {
                    EditFieldResvManagement(
                        value = facilityName,
                        text = "Enter Facility Name:",
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        onValueChange = { facilityName = it }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Facility Description
                item {
                    EditFieldResvManagement(
                        value = facilityDescription,
                        text = "Enter Description:",
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        onValueChange = { facilityDescription = it }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Facility Type
                item {
                    EditFieldResvManagement(
                        value = facilityType,
                        text = "Enter Type:",
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        onValueChange = { facilityType = it }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Min Height
                item {
                    EditFieldResvManagement(
                        value = facilityMinHeight,
                        text = "Enter Min Height:",
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        onValueChange = { facilityMinHeight = it }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Supervisor Companion
                item {
                    EditFieldResvManagement(
                        value = facilitySvCompanion,
                        text = "Enter Companion:",
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        onValueChange = { facilitySvCompanion = it }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Upload Image
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .border(
                                width = 1.dp,
                                color = Color.Gray,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .height(150.dp)
                            .clickable {
                                galleryLauncher.launch("image/*")
                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (imageUri != null) {
                            // Load and display the selected image
                            Image(
                                painter = rememberImagePainter(imageUri),
                                contentDescription = "Selected Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            // Show the default upload icon and text when no image is selected
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.upload),
                                    contentDescription = "Upload Icon",
                                    modifier = Modifier.size(48.dp),
                                    tint = Color.Black
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "Upload facility image", color = Color.Gray)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }

                // Select Availability (Yes/No Buttons)
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Select Availability:",
                            modifier = Modifier.width(150.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))

                        Button(
                            onClick = { isAvailable = true },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isAvailable == true) Color.Green else Color.LightGray)
                        ) {
                            Text(text = "Yes")
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Button(
                            onClick = { isAvailable = false },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isAvailable == false) Color.Red else Color.LightGray)
                        ) {
                            Text(text = "No")
                        }
                    }
                }

                if (!isAvailable)
                {
                    // Select date
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Select Date:",
                                modifier = Modifier.width(150.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                    }

                    // DatePicker and TimePicker fields
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "From:",
                                modifier = Modifier.width(48.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))

                            TextField(
                                value = facilityDateFrom,
                                onValueChange = { facilityDateFrom = it },
                                modifier = Modifier
                                    .width(125.dp)
                                    .clickable {
                                        showDatePicker { selectedDate ->
                                            facilityDateFrom = selectedDate

                                            // Check if the selected DateFrom is greater than DateTo
                                            if (facilityDateTo.isNotEmpty() && SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(
                                                    facilityDateFrom)!! > SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(facilityDateTo)!!) {
                                                // Show error message and reset DateFrom
                                                Toast.makeText(context, "To Date cannot be less than From Date", Toast.LENGTH_SHORT).show()
                                                facilityDateFrom = "" // Resetting DateFrom
                                            }
                                        }
                                    },
                                enabled = false
                            )
                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = "To:",
                                modifier = Modifier.width(25.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))

                            TextField(
                                value = facilityDateTo,
                                onValueChange = { facilityDateTo = it },
                                modifier = Modifier
                                    .width(125.dp)
                                    .clickable {
                                        showDatePicker { selectedDate ->
                                            facilityDateTo = selectedDate

                                            // Check if the selected DateFrom is greater than DateTo
                                            if (facilityDateFrom.isNotEmpty() && SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(
                                                    facilityDateFrom)!! > SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(facilityDateTo)!!) {
                                                // Show error message and reset DateTo
                                                Toast.makeText(context, "To Date cannot be less than From Date", Toast.LENGTH_SHORT).show()
                                                facilityDateTo = "" // Resetting DateTo
                                            }
                                        }
                                    },
                                enabled = false,
                                readOnly = true
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Select time
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Select Time:",
                                modifier = Modifier.width(150.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                    }

                    // From and To Time pickers
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "From:",
                                modifier = Modifier.width(48.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))

                            TextField(
                                value = facilityTimeFrom,
                                onValueChange = { facilityTimeFrom = it },
                                modifier = Modifier
                                    .width(125.dp)
                                    .clickable {
                                        showTimePicker { selectedTime ->
                                            facilityTimeFrom = selectedTime

                                            // Check if the selected TimeFrom is greater than TimeTo
                                            if (facilityTimeTo.isNotEmpty() && SimpleDateFormat("HH:mm", Locale.getDefault()).parse(
                                                    facilityTimeFrom)!! > SimpleDateFormat("HH:mm", Locale.getDefault()).parse(facilityTimeTo)!!) {
                                                // Show error message and reset TimeFrom
                                                Toast.makeText(context, "To Time cannot be less than Time Date", Toast.LENGTH_SHORT).show()
                                                facilityTimeFrom = "" // Resetting TimeFrom
                                            }
                                        }
                                    },
                                enabled = false
                            )
                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = "To:",
                                modifier = Modifier.width(25.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))

                            TextField(
                                value = facilityTimeTo,
                                onValueChange = { facilityTimeTo = it },
                                modifier = Modifier
                                    .width(125.dp)
                                    .clickable {
                                        showTimePicker { selectedTime ->
                                            facilityTimeTo = selectedTime

                                            // Check if the selected TimeFrom is greater than TimeTo
                                            if (facilityTimeFrom.isNotEmpty() && SimpleDateFormat("HH:mm", Locale.getDefault()).parse(
                                                    facilityTimeFrom)!! > SimpleDateFormat("HH:mm", Locale.getDefault()).parse(facilityTimeTo)!!) {
                                                // Show error message and reset TimeTo
                                                Toast.makeText(context, "To Time cannot be less than Time Date", Toast.LENGTH_SHORT).show()
                                                facilityTimeTo = "" // Resetting TimeTo
                                            }
                                        }
                                    },
                                enabled = false,
                                readOnly = true
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                // Buttons for Submit and Cancel
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = onCancelClick,
                            modifier = Modifier.width(100.dp),
                            border = BorderStroke(2.dp, Color.Red),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                        ) {
                            Text(text = "Cancel", color = Color.Red)
                        }
                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                // Check if the facility name already exists
                                val existingFacilityNames = facilities.map { it.facilityName }

                                if (existingFacilityNames.contains(facilityName)) {
                                    Toast.makeText(context, "Facility already exists.", Toast.LENGTH_SHORT).show()
                                } else if (!isAvailable)
                                {
                                    if (facilityName.isEmpty() || facilityDescription.isEmpty() || facilityType.isEmpty() || facilityMinHeight.isEmpty() || facilitySvCompanion.isEmpty())
                                    {
                                        Toast.makeText(
                                            context,
                                            "Please enter facility details.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else if (facilityDateTo.isEmpty() || facilityDateFrom.isEmpty()) {
                                        Toast.makeText(
                                            context,
                                            "Please select date.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else if (facilityTimeTo.isEmpty() || facilityTimeFrom.isEmpty()) {
                                        Toast.makeText(
                                            context,
                                            "Please select time.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    else {
                                        val resourceId: Int = R.drawable.andromenda_base
                                        val parsedDateFrom = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(facilityDateFrom)!!
                                        val parsedDateTo = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(facilityDateTo)!!
                                        val faciCount = count + 1
                                        val facilityID = if (faciCount < 10) {
                                            "F00$faciCount" // For single-digit numbers
                                        } else {
                                            "F0$faciCount" // For double-digit numbers
                                        }

                                        val facility = Facility(
                                            facilityID = facilityID,
                                            facilityName = facilityName,
                                            facilityDesc = facilityDescription,
                                            facilityType = facilityType,
                                            facilityMinHeight = facilityMinHeight,
                                            facilitySvCompanion = facilitySvCompanion,
                                            facilityImage = resourceId,
                                            facilityDateFrom = parsedDateFrom,
                                            facilityDateTo = parsedDateTo,
                                            facilityTimeFrom = facilityTimeFrom,
                                            facilityTimeTo = facilityTimeTo,
                                            facilityAvailability = isAvailable
                                        )
                                        onSubmitClick(facility)
                                    }
                                }
                                else {
                                    val resourceId: Int = R.drawable.andromenda_base
                                    val faciCount = count + 1
                                    val facilityID = if (faciCount < 10) {
                                        "F00$faciCount" // For single-digit numbers
                                    } else {
                                        "F0$faciCount" // For double-digit numbers
                                    }

                                    val facility = Facility(
                                        facilityID = facilityID,
                                        facilityName = facilityName,
                                        facilityDesc = facilityDescription,
                                        facilityType = facilityType,
                                        facilityMinHeight = facilityMinHeight,
                                        facilitySvCompanion = facilitySvCompanion,
                                        facilityImage = resourceId,
                                        facilityDateFrom = dateFormat.parse("2022-01-01")!!,
                                        facilityDateTo = dateFormat.parse("2028-12-31")!!,
                                        facilityTimeFrom = "10:00",
                                        facilityTimeTo = "18:00",
                                        facilityAvailability = isAvailable
                                    )
                                    onSubmitClick(facility)
                                }
                            },
                            modifier = Modifier.width(100.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00))
                        ) {
                            Text(text = "Add")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FacilityFormUpdate(
    facilityAvailability: Boolean,
    facilityDateFromOriginal: String,
    facilityDateToOriginal: String,
    facilityTimeFromOriginal: String,
    facilityTimeToOriginal: String,
    facilityNameUpdate: String,
    onCancelClick: () -> Unit,
    onSubmitClick: (Date, Date, String, String, Boolean, String) -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    var isAvailable by remember { mutableStateOf(true) }
    var facilityDateFrom by remember { mutableStateOf("") }
    var facilityDateTo by remember { mutableStateOf("") }
    var facilityTimeFrom by remember { mutableStateOf("") }
    var facilityTimeTo by remember { mutableStateOf("") }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Helper function to show DatePicker dialog
    fun showDatePicker(onDateSelected: (String) -> Unit) {
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = "$dayOfMonth/${month + 1}/$year"
                onDateSelected(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Set the minimum date to the current date
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()

        datePickerDialog.show()
    }

    // Helper function to show TimePicker dialog
    fun showTimePicker(onTimeSelected: (String) -> Unit) {
        val timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val selectedTime = String.format("%02d:%02d", hourOfDay, minute)

                // Check if the selected time is between 10:00 and 18:00
                if (hourOfDay in 10..17 || (hourOfDay == 18 && minute == 0)) {
                    onTimeSelected(selectedTime) // Valid time, proceed with selection
                } else {
                    Toast.makeText(context, "Please select a time between 10:00 and 18:00", Toast.LENGTH_SHORT).show()
                }
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // 24-hour format
        )

        // Show the time picker
        timePickerDialog.show()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Select Availability (Yes/No Buttons)
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Select Availability:",
                            modifier = Modifier.width(150.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))

                        Button(
                            onClick = { isAvailable = true },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isAvailable == true) Color.Green else Color.LightGray)
                        ) {
                            Text(text = "Yes")
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Button(
                            onClick = { isAvailable = false },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isAvailable == false) Color.Red else Color.LightGray)
                        ) {
                            Text(text = "No")
                        }
                    }
                }

                if (!isAvailable)
                {
                    // Select date
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Select Date:",
                                modifier = Modifier.width(150.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                    }

                    // DatePicker and TimePicker fields
                    // Date picker validation logic
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "From:",
                                modifier = Modifier.width(48.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))

                            TextField(
                                value = facilityDateFrom,
                                onValueChange = { facilityDateFrom = it },
                                modifier = Modifier
                                    .width(125.dp)
                                    .clickable {
                                        showDatePicker { selectedDate ->
                                            facilityDateFrom = selectedDate

                                            // Check if the selected DateFrom is greater than DateTo
                                            if (facilityDateTo.isNotEmpty() && SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(
                                                    facilityDateFrom
                                                )!! > SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(
                                                    facilityDateTo
                                                )!!) {
                                                // Show error message and reset DateFrom
                                                Toast.makeText(context, "To Date cannot be less than From Date", Toast.LENGTH_SHORT).show()
                                                facilityDateFrom = "" // Resetting DateFrom
                                            }
                                        }
                                    },
                                enabled = false
                            )
                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = "To:",
                                modifier = Modifier.width(25.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))

                            TextField(
                                value = facilityDateTo,
                                onValueChange = { facilityDateTo = it },
                                modifier = Modifier
                                    .width(125.dp)
                                    .clickable {
                                        showDatePicker { selectedDate ->
                                            facilityDateTo = selectedDate

                                            // Check if the selected DateFrom is greater than DateTo
                                            if (facilityDateFrom.isNotEmpty() && SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(
                                                    facilityDateFrom
                                                )!! > SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(
                                                    facilityDateTo
                                                )!!) {
                                                // Show error message and reset DateFrom
                                                Toast.makeText(context, "To Date cannot be less than From Date", Toast.LENGTH_SHORT).show()
                                                facilityDateTo = "" // Resetting DateFrom
                                            }
                                        }
                                    },
                                enabled = false,
                                readOnly = true
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Select time
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Select Time:",
                                modifier = Modifier.width(150.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                    }

                    // Time picker validation logic
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "From:",
                                modifier = Modifier.width(48.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))

                            TextField(
                                value = facilityTimeFrom,
                                onValueChange = { facilityTimeFrom = it },
                                modifier = Modifier
                                    .width(125.dp)
                                    .clickable {
                                        showTimePicker { selectedTime ->
                                            facilityTimeFrom = selectedTime

                                            // Check if the selected TimeFrom is greater than TimeTo
                                            if (facilityTimeTo.isNotEmpty() && SimpleDateFormat("HH:mm", Locale.getDefault()).parse(
                                                    facilityTimeFrom)!! > SimpleDateFormat("HH:mm", Locale.getDefault()).parse(facilityTimeTo)!!) {
                                                // Show error message and reset TimeFrom
                                                Toast.makeText(context, "To Time cannot be less than Time Date", Toast.LENGTH_SHORT).show()
                                                facilityTimeFrom = "" // Resetting TimeFrom
                                            }
                                        }
                                    },
                                enabled = false
                            )
                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = "To:",
                                modifier = Modifier.width(25.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))

                            TextField(
                                value = facilityTimeTo,
                                onValueChange = { facilityTimeTo = it },
                                modifier = Modifier
                                    .width(125.dp)
                                    .clickable {
                                        showTimePicker { selectedTime ->
                                            facilityTimeTo = selectedTime

                                            // Check if the selected TimeFrom is greater than TimeTo
                                            if (facilityTimeFrom.isNotEmpty() && SimpleDateFormat("HH:mm", Locale.getDefault()).parse(
                                                    facilityTimeFrom)!! > SimpleDateFormat("HH:mm", Locale.getDefault()).parse(facilityTimeTo)!!) {
                                                // Show error message and reset TimeFrom
                                                Toast.makeText(context, "To Time cannot be less than Time Date", Toast.LENGTH_SHORT).show()
                                                facilityTimeTo = "" // Resetting TimeFrom
                                            }
                                        }
                                    },
                                enabled = false,
                                readOnly = true
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                // Buttons for Submit and Cancel
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = onCancelClick,
                            modifier = Modifier.width(100.dp),
                            border = BorderStroke(2.dp, Color.Red),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                        ) {
                            Text(text = "Cancel", color = Color.Red)
                        }
                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if(facilityAvailability && isAvailable)
                                {
                                    Toast.makeText(
                                        context,
                                        "Facility is already available!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                else if (!facilityAvailability && !isAvailable && facilityDateFromOriginal == facilityDateFrom && facilityDateToOriginal == facilityDateTo && facilityTimeFromOriginal == facilityTimeFrom && facilityTimeToOriginal == facilityTimeTo)
                                {
                                    Toast.makeText(
                                        context,
                                        "Facility is already unavailable!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                else {
                                    if (!isAvailable)
                                    {
                                        if (facilityDateTo.isEmpty() || facilityDateFrom.isEmpty()) {
                                            Toast.makeText(
                                                context,
                                                "Please select date.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else if (facilityTimeTo.isEmpty() || facilityTimeFrom.isEmpty()) {
                                            Toast.makeText(
                                                context,
                                                "Please select time.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        else {
                                            val parsedDateFrom =
                                                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(
                                                    facilityDateFrom
                                                )!!
                                            val parsedDateTo =
                                                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(
                                                    facilityDateTo
                                                )!!

                                            onSubmitClick(
                                                parsedDateFrom,
                                                parsedDateTo,
                                                facilityTimeFrom,
                                                facilityTimeTo,
                                                isAvailable,
                                                facilityNameUpdate
                                            )
                                        }
                                    }
                                    else {
                                        onSubmitClick(
                                            dateFormat.parse("2022-01-01")!!,
                                            dateFormat.parse("2028-12-31")!!,
                                            "10:00",
                                            "18:00",
                                            isAvailable,
                                            facilityNameUpdate
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.width(100.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00))
                        ) {
                            Text(text = "Update")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FacilityCardManagement(
    facilityAvailability: Boolean,
    facilityTimeFrom: String,
    facilityTimeTo: String,
    facilityName: String,
    facilityImage: Int,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(15.dp)
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(facilityImage),
                    contentDescription = facilityName,
                    modifier = Modifier
                        .width(350.dp)
                        .height(150.dp),
                    contentScale = ContentScale.FillBounds
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Facility Name Text
            Text(
                text = facilityName,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Facility Availability Text
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                // Check facility availability and show appropriate text and drawable
                val (availabilityText, drawable) = when {
                    facilityAvailability -> Pair("Available (Normal Operating Hours)", R.drawable.check_box)
                    facilityTimeFrom == "10:00" && facilityTimeTo == "18:00" -> Pair("Unavailable (Whole Day)", R.drawable.cross)
                    else -> Pair("Unavailable (Few Hours of the Day)", R.drawable.time_filled)
                }

                // Show the icon (drawable) based on the condition
                Icon(
                    painter = painterResource(id = drawable),
                    contentDescription = null,
                    tint = when {
                        facilityAvailability -> Color.Green // Available
                        !facilityAvailability && facilityTimeFrom != "10:00" && facilityTimeTo != "18:00" -> Color.Yellow // Unavailable, but not full day
                        else -> Color.Red // Unavailable for the whole day (10:00 to 18:00)
                    },
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp)) // Spacing between icon and text

                // Show the text based on availability
                Text(
                    text = availabilityText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = when {
                        facilityAvailability -> Color.Green // Available
                        !facilityAvailability && facilityTimeFrom != "10:00" && facilityTimeTo != "18:00" -> Color.Yellow // Unavailable, but not full day
                        else -> Color.Red // Unavailable for the whole day (10:00 to 18:00)
                    }
                )
            }


            Spacer(modifier = Modifier.height(10.dp))

            // Row with Edit and Delete ImageButtons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 0.dp, end = 12.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                // Edit ImageButton
                IconButton(
                    onClick = onEditClick
                ) {
                    Icon(
                        painter = painterResource(R.drawable.edit_button),
                        contentDescription = "Edit Facility",
                        modifier = Modifier.size(32.dp),
                        tint = Color.Unspecified // Default tint for edit icon
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Delete ImageButton with red tint
                IconButton(
                    onClick = onDeleteClick
                ) {
                    Icon(
                        painter = painterResource(R.drawable.delete_button),
                        contentDescription = "Delete Facility",
                        modifier = Modifier.size(32.dp),
                        tint = Color.Red // Red color for delete icon
                    )
                }
            }
        }
    }
}




@Composable
fun EditFieldResvManagement(
    value: String,
    text: String,
    keyboardOptions: KeyboardOptions,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 1.dp)
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            modifier = Modifier.padding(end = 8.dp).width(140.dp)
        )
        TextField(
            value = value,
            singleLine = true,
            keyboardOptions = keyboardOptions,
            onValueChange = onValueChange,
            modifier = modifier.weight(1f)
        )
    }
}


