package com.example.funparkapp.ui.theme

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.funparkapp.R
import com.example.funparkapp.data.Category
import com.example.funparkapp.data.Location
import com.example.funparkapp.data.MapViewModel
import kotlin.math.roundToInt

@Composable
fun MapScreen(navController: NavHostController, viewModel: MapViewModel) {
    var selectedCategory by remember { mutableStateOf(Category.GAMES) }
    val selectedLocation = remember { mutableStateOf<Location?>(null) }

    LaunchedEffect(selectedCategory) {
        viewModel.fetchLocations(selectedCategory.name)
    }

    val locations by viewModel.locations.observeAsState(emptyList())

    Scaffold(
        topBar = { CustomTopBar() },
        bottomBar = { BottomNavigationBar { category -> selectedCategory = category } },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            ScalableMap(locations, viewModel) { location ->
                selectedLocation.value = location
            }
        }

        selectedLocation.value?.let { location ->
            ShowLocationDetailsDialog(location, navController) {
                selectedLocation.value = null
            }
        }
    }
}

@Composable
fun CustomTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = androidx.compose.ui.graphics.Color(0xFFFFA500))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Map",
            style = MaterialTheme.typography.titleLarge.copy(
                color = androidx.compose.ui.graphics.Color.White,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun ThemeParkMap() {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.map),
            contentDescription = "Theme Park Map",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun ThemeParkMapWithMarkers(
    selectedCategory: Category,
    viewModel: MapViewModel,
    onMarkerClick: (Location) -> Unit
) {
    // Fetch locations based on the selected category
    val locations by viewModel.locations.observeAsState(emptyList())

    Box(modifier = Modifier.fillMaxSize()) {
        ThemeParkMap()

        locations.forEach { location ->
            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = location.name,
                modifier = Modifier
                    .offset(x = location.x.dp, y = location.y.dp)
                    .size(30.dp)
                    .clickable { onMarkerClick(location) }
            )
        }
    }
}

@Composable
fun ScalableMap(
    locations: List<Location>,
    viewModel: MapViewModel,
    onMarkerClick: (Location) -> Unit
) {
    val scale = remember { mutableStateOf(1f) }
    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }

    Box(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTransformGestures { _, pan, zoom, _ ->
                scale.value *= zoom
                offsetX.value += pan.x
                offsetY.value += pan.y
            }
        }
    ) {
        // Fixed box for the map
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(scale.value)
                .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
        ) {
            ThemeParkMapWithMarkers(selectedCategory = Category.GAMES, viewModel = viewModel, onMarkerClick = onMarkerClick)
        }

        // Zoom controls
        ZoomControls(
            onZoomIn = { scale.value *= 1.2f },
            onZoomOut = { scale.value /= 1.2f }
        )
    }
}

@Composable
fun BottomNavigationBar(onCategorySelected: (Category) -> Unit) {
    val currentCategory = remember { mutableStateOf(Category.SOUVENIR) }

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Souvenirs") },
            label = { Text("Souvenirs") },
            selected = currentCategory.value == Category.SOUVENIR,
            onClick = {
                currentCategory.value = Category.SOUVENIR
                onCategorySelected(Category.SOUVENIR)
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Fastfood, contentDescription = "Food") },
            label = { Text("Food") },
            selected = currentCategory.value == Category.FOOD,
            onClick = {
                currentCategory.value = Category.FOOD
                onCategorySelected(Category.FOOD)
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.VideogameAsset, contentDescription = "Games") },
            label = { Text("Games") },
            selected = currentCategory.value == Category.GAMES,
            onClick = {
                currentCategory.value = Category.GAMES
                onCategorySelected(Category.GAMES)
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Build, contentDescription = "Services") },
            label = { Text("Services") },
            selected = currentCategory.value == Category.SERVICES,
            onClick = {
                currentCategory.value = Category.SERVICES
                onCategorySelected(Category.SERVICES)
            }
        )
    }
}

@Composable
fun ZoomControls(onZoomIn: () -> Unit, onZoomOut: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = onZoomOut) {
            Text(text = "-", fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(onClick = onZoomIn) {
            Text(text = "+", fontSize = 20.sp)
        }
    }
}

@Composable
fun ShowLocationDetailsDialog(location: Location, navController: NavHostController, onDismiss: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val foodMenus = mapOf(
        "Food Court 1" to listOf("Burger - RM 10.90 ", "Fries - RM 6.90 ", "Soda - RM 3.90"),
        "Food Court 2" to listOf("Pizza - RM 10.90", "Pasta - RM 12.90", "Salad- RM 9.90")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Location: ${location.name}", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Image(
                    painter = painterResource(id = location.imageResId),
                    contentDescription = location.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(bottom = 16.dp)
                        .border(1.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )

                Text(
                    text = "This is ${location.name}. ${if (location.category == Category.FOOD) "Check the menu below!" else ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (location.category == Category.FOOD) {
                    Button(
                        onClick = { showMenu = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("View Menu")
                    }
                }

                when {
                    location.category == Category.SERVICES && location.name == "First Aid" -> {
                        TextButton(onClick = {
                            context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:112")))
                            onDismiss()
                        }) {
                            Text("Call First Aid")
                        }
                    }
                    location.category == Category.SERVICES && location.name == "Help" -> {
                        TextButton(onClick = {
                            context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:123")))
                            onDismiss()
                        }) {
                            Text("Call Help")
                        }
                    }
                    location.category == Category.SOUVENIR -> {
                        TextButton(onClick = {
                            navController.navigate("souvenir")
                            onDismiss()
                        }) {
                            Text("Go to Souvenir Page")
                        }
                    }
                    location.category == Category.GAMES -> {
                        TextButton(onClick = {

                            onDismiss()
                        }) {
                            Text("Make Reservation")
                        }
                    }
                }

                // Show menu if applicable
                if (showMenu && location.category == Category.FOOD) {
                    Text("Menu for ${location.name}:")
                    foodMenus[location.name]?.forEach { menuItem ->
                        Text(menuItem)
                    }
                }
            }
        },
        confirmButton = { Button(onClick = onDismiss) { Text("Close") } }
    )
}
