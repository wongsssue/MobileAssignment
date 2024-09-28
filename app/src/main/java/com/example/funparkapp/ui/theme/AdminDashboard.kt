package com.example.funparkapp.ui.theme // Adjust package name if needed
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.funparkapp.R

// Data class for admin features
data class AdminFeature(
    val title: String,
    val icon: Int,
    val route: String
)

// List of admin features
val adminFeatures = listOf(
    AdminFeature("Manage Users", R.drawable.manage_users_icon, FunParkScreen1.AdminManageUser.name),
    AdminFeature("Manage Redemptions", R.drawable.manage_redemptions_icon, FunParkScreen1.AdminManageRedeem.name)
    // Add more features as needed
)

// AdminDashboardScreen composable
@Composable
fun AdminDashboardScreen(navController: NavHostController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(adminFeatures) { feature ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { navController.navigate(feature.route) },
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(feature.icon),
                    contentDescription = feature.title,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(feature.title, fontSize = 18.sp)
            }
            }
        }
    }
}