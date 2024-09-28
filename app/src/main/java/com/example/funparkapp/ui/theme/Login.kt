package com.example.funparkapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.funparkapp.data.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    var username by remember { mutableStateOf("")}
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFFFC150))
        .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        val user = userViewModel.login(username, password)
                        if (user != null) {
                            if (user.role == "Admin") {
                                navController.navigate(FunParkScreen1.AdminDashboard.name) // Navigate to admin dashboard
                            } else {
                                navController.navigate(FunParkScreen1.MainMenu.name) // Navigate to user home screen
                            }
                        } else {
                            showError = true
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))
            ) {
                Text("Login", color = Color.White)
            }

            if (showError) {
                Text("Invalid username or password", color = Color.Red)
            }


            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { navController.navigate(FunParkScreen1.Register.name) },
                colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
            ) {
                Text("Don't have an account? Register")
            }
        }

        TextButton(
            onClick = { navController.navigate(FunParkScreen1.MainMenu.name) },
            colors = ButtonDefaults.textButtonColors(contentColor = Color.White),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text("Skip Login")
        }
    }
}
