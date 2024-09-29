package com.example.funparkapp.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
    val searchText by souvenirViewModel.searchText.collectAsState()
    val souvenirList by souvenirViewModel.souvenirList.collectAsState()
    val cartItemCount by souvenirViewModel.cartItemCount.collectAsState()

    val filteredSouvenirs = souvenirList.filter { souvenir ->
        souvenir.name.contains(searchText, ignoreCase = true)
    }

    val backgroundColor = Color.White
    val appBarColor = Color.White
    val textColor = Color.Black

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(backgroundColor)
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
                            tint = Color.Black
                        )
                        if (cartItemCount > 0) {
                            Badge(
                                content = { Text(cartItemCount.toString()) },
                                modifier = Modifier.align(Alignment.TopEnd)
                            )
                        }
                    }
                }
                IconButton(onClick = { themeViewModel.toggleTheme() }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Toggle Dark Mode",
                        tint = Color.Black // Updated to black
                    )
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
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
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
    modifier: Modifier = Modifier
) {
    var quantity by remember { mutableStateOf(1) }

    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
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
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Text(
                text = "RM ${souvenir.price}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
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
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Decrease Quantity",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = quantity.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(horizontal = 16.dp)
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
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Button(
                onClick = {
                    onAddToCart(souvenir, quantity)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = "Add to Cart", color = Color.White)
            }
        }
    }
}

