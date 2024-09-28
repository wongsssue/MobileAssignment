package com.example.funparkapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.funparkapp.R

// Fixed the data class declaration
data class MenuItem(val title: String, val route: String? = null, val icon: Int? = null)

val menuItems = listOf(
    MenuItem("Home", FunParkScreen.MainMenu.name),
    MenuItem("Account", FunParkScreen.Account.name),
    MenuItem("Reservations", FunParkScreen.TicketMenu.name), // Example: Navigate to TicketMenu for reservations
    MenuItem("Tickets", FunParkScreen.TicketMenu.name),
    MenuItem("Redeem", FunParkScreen.Redeem.name),
    MenuItem("Map"),    // No route for this item yet
    MenuItem("Souvenirs"), // No route for this item yet
    MenuItem("Purchase History", FunParkScreen.TicketHistory.name) // Fixed missing comma
)

@Composable
fun MenuScreen(
    onMenuItemClick: (String) -> Unit,
    onSignOutClick: () -> Unit,
    navController: NavHostController
) {
    val menuItemModifier = Modifier
        .fillMaxWidth()
        .padding(8.dp) // Adjusted padding to give items better spacing
        .background(Color(0xFFFFC150)) // Background color for each item

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFC150))
            .padding(16.dp)
    ) {
        items(menuItems) { item ->
            Surface(
                modifier = menuItemModifier.clickable {
                    item.route?.let { route ->
                        navController.navigate(route)
                    } ?: onMenuItemClick(item.title)
                },
                color = Color(0xFFFFC150),
                shape = RoundedCornerShape(12.dp)
            ) {
                ListItem(
                    headlineContent = { Text(item.title) },
                    leadingContent = {
                        item.icon?.let {
                            Icon(
                                painter = painterResource(id = it),
                                contentDescription = item.title,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                )
            }
        }
        item {
            SignOutItem { onSignOutClick() }
        }
    }
}

@Composable
fun SignOutItem(onSignOutClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp) // Adjusted padding to match list item spacing
            .clickable { onSignOutClick() },
        color = Color(0xFFFFC150),
        shape = RoundedCornerShape(12.dp)
    ) {
        ListItem(
            headlineContent = { Text("Sign Out") },
            leadingContent = {
                Icon(
                    painter = painterResource(id = R.drawable.signouticon), // Ensure this resource exists
                    contentDescription = "Sign out icon",
                    modifier = Modifier.size(24.dp)
                )
            }
        )
    }
}

