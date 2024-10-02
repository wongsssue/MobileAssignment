package com.example.funparkapp.ui.theme

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.funparkapp.data.Souvenir
import com.example.funparkapp.data.SouvenirViewModel
import com.example.funparkapp.data.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SouvenirScreen(
    souvenirViewModel: SouvenirViewModel,
    onAddToCart: (Souvenir, Int) -> Unit,
    navigateToCart: () -> Unit,
    themeViewModel: ThemeViewModel
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        LandscapeSouvenirScreen(souvenirViewModel, onAddToCart, navigateToCart, themeViewModel)
    } else {
        PortraitSouvenirScreen(souvenirViewModel, onAddToCart, navigateToCart, themeViewModel)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortraitSouvenirScreen(
    souvenirViewModel: SouvenirViewModel,
    onAddToCart: (Souvenir, Int) -> Unit,
    navigateToCart: () -> Unit,
    themeViewModel: ThemeViewModel
) {
    val searchText by souvenirViewModel.searchText.collectAsState()
    val souvenirList by souvenirViewModel.souvenirList.collectAsState()
    val cartItemCount by souvenirViewModel.cartItemCount.collectAsState()

    val isDarkMode by themeViewModel.isDarkMode.collectAsState()

    val backgroundColor = if (isDarkMode) Color.Black else Color.White
    val textColor = if (isDarkMode) Color.White else Color.Black
    val appBarColor = if (isDarkMode) Color.DarkGray else Color.LightGray
    val filteredSouvenirs = souvenirList.filter { souvenir ->
        souvenir.name.contains(searchText, ignoreCase = true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = "Souvenirs",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = textColor
                    )
                },
                actions = {
                    IconButton(onClick = navigateToCart) {
                        Box {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Cart",
                                tint = textColor
                            )
                            if (cartItemCount > 0) {
                                Badge(
                                    content = { Text(cartItemCount.toString()) },
                                    modifier = Modifier.align(Alignment.TopEnd)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = appBarColor),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            SouvenirSearchBar(
                searchText = searchText,
                onSearchTextChanged = { souvenirViewModel.updateSearchText(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredSouvenirs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No souvenirs available.", color = Color.Gray)
                }
            } else {
                LazyColumn {
                    items(filteredSouvenirs.chunked(2)) { pairOfSouvenirs ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            pairOfSouvenirs.forEach { souvenir ->
                                SouvenirItem(
                                    souvenir = souvenir,
                                    onAddToCart = { selectedSouvenir, quantity ->
                                        onAddToCart(selectedSouvenir, quantity)
                                    },
                                    isDarkMode = isDarkMode,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            if (pairOfSouvenirs.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

            }
        }

        // Floating Action Button for settings
        FloatingActionButton(
            onClick = { themeViewModel.toggleTheme() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Toggle Dark Mode",
                tint = Color.White
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandscapeSouvenirScreen(
    souvenirViewModel: SouvenirViewModel,
    onAddToCart: (Souvenir, Int) -> Unit,
    navigateToCart: () -> Unit,
    themeViewModel: ThemeViewModel
) {
    val searchText by souvenirViewModel.searchText.collectAsState()
    val souvenirList by souvenirViewModel.souvenirList.collectAsState()
    val cartItemCount by souvenirViewModel.cartItemCount.collectAsState()

    val isDarkMode by themeViewModel.isDarkMode.collectAsState()

    // Define colors based on theme
    val backgroundColor = if (isDarkMode) Color(0xFF121212) else Color(0xFFFFFFFF)
    val textColor = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF333333)
    val appBarColor = if (isDarkMode) Color(0xFF1F1F1F) else Color(0xFFF5F5F5)
    val accentColor = Color(0xFFFFA500)

    // Filtered souvenirs based on search input
    val filteredSouvenirs = souvenirList.filter { souvenir ->
        souvenir.name.contains(searchText, ignoreCase = true)
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        // Sidebar Navigation for Landscape
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(240.dp)
                .background(appBarColor, RoundedCornerShape(16.dp))
                .padding(16.dp)
                .shadow(4.dp)
        ) {
            Text(
                text = "Souvenirs",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = textColor
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Redesigned Search Bar
            OutlinedTextField(
                value = searchText,
                onValueChange = { souvenirViewModel.updateSearchText(it) },
                placeholder = { Text("Search souvenirs", color = Color.Gray) },
                textStyle = TextStyle(color = textColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.Transparent,
                    cursorColor = accentColor
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Settings Floating Action Button
            FloatingActionButton(
                onClick = { themeViewModel.toggleTheme() },
                containerColor = accentColor,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Toggle Dark Mode",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Cart Button with Badge below Settings Button
            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .size(60.dp)
                    .background(accentColor, CircleShape)
                    .align(Alignment.CenterHorizontally)
            ) {
                IconButton(onClick = navigateToCart, modifier = Modifier.align(Alignment.Center)) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Cart",
                        tint = Color.White
                    )
                }
                if (cartItemCount > 0) {
                    Badge(
                        content = { Text(cartItemCount.toString(), color = Color.White) },
                        modifier = Modifier.align(Alignment.TopEnd)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Souvenir List with Card Design
        LazyVerticalGrid(
            columns = GridCells.Fixed(3), // Set to 3 columns
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .padding(8.dp)
        ) {
            items(filteredSouvenirs) { souvenir ->
                SouvenirCard(
                    souvenir = souvenir,
                    accentColor = accentColor,
                    textColor = textColor,
                    isDarkMode = isDarkMode, // Pass the isDarkMode parameter here
                    onAddToCart = onAddToCart
                )
            }
        }
    }
}

@Composable
fun SouvenirCard(
    souvenir: Souvenir,
    accentColor: Color,
    textColor: Color,
    isDarkMode: Boolean,
    onAddToCart: (Souvenir, Int) -> Unit
) {
    // State to track the quantity of the souvenir
    var quantity by remember { mutableStateOf(1) }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) Color(0xFF424242) else Color.Transparent // Set to gray in dark mode
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Souvenir Image
            Image(
                painter = painterResource(id = souvenir.imageResource),
                contentDescription = souvenir.name,
                modifier = Modifier.size(80.dp).padding(8.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(12.dp)) // Increased height for spacing

            // Souvenir Name
            Text(
                text = souvenir.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = textColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Souvenir Price
            Text(
                text = "RM ${souvenir.price}",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Quantity Control
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { if (quantity > 1) quantity-- },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease Quantity")
                }
                Text(text = quantity.toString(), style = MaterialTheme.typography.bodyLarge)
                IconButton(
                    onClick = { quantity++ },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Increase Quantity")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Add to Cart Button
            Button(
                onClick = { onAddToCart(souvenir, quantity) }, // Pass the updated quantity
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)), // Set button color to orange
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Add to Cart", color = Color.White)
            }
        }
    }
}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SouvenirSearchBar(searchText: String, onSearchTextChanged: (String) -> Unit) {
    TextField(
        value = searchText,
        onValueChange = onSearchTextChanged,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE0E0E0)),
        placeholder = { Text("Search Souvenirs", color = Color.Gray) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color(0xFFE0E0E0),
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SouvenirItem(
    souvenir: Souvenir,
    onAddToCart: (Souvenir, Int) -> Unit,
    isDarkMode: Boolean, // Add this parameter
    modifier: Modifier = Modifier
) {
    var quantity by remember { mutableStateOf(1) }

    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) Color.DarkGray else Color.White // Set background color based on theme
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = souvenir.imageResource),
                contentDescription = souvenir.name,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Text(
                text = souvenir.name,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 8.dp),
                color = if (isDarkMode) Color.White else Color.Black // Adjust text color for dark mode
            )
            Text(
                text = "RM ${souvenir.price}",
                style = MaterialTheme.typography.bodyLarge,
                color = if (isDarkMode) Color.White else Color.Black
            )

            Row(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (quantity > 1) {
                            quantity--
                        }
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease Quantity",
                        tint = if (isDarkMode) Color.White else MaterialTheme.colorScheme.primary // Change tint based on dark mode
                    )
                }

                Text(
                    text = quantity.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = if (isDarkMode) Color.White else Color.Black // Adjust text color for dark mode
                )

                IconButton(
                    onClick = {
                        quantity++
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase Quantity",
                        tint = if (isDarkMode) Color.White else MaterialTheme.colorScheme.primary // Change tint based on dark mode
                    )
                }
            }

            Button(
                onClick = {
                    onAddToCart(souvenir, quantity)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)) // Change to orange
            ) {
                Text(text = "Add to Cart", color = Color.White)
            }
        }
    }
}
