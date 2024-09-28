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

@Composable
fun RegisterScreen(
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

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
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation())
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (password == confirmPassword) {
                        userViewModel.registerUser(username, email, password,role = "Customer")
                        navController.navigate(FunParkScreen1.Login.name)
                    } else {
                        showError = true // Show error message
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))
            ) {
                Text("Register", color = Color.White)
            }

            if (showError) {Text(
                text = "Passwords do not match",
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { navController.navigate(FunParkScreen1.Login.name) },
                colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
            ) {
                Text("Already have an account? Login")
            }
        }

        // "Skip Register" button within the Box scope
        TextButton(
            onClick = { navController.navigate(FunParkScreen1.MainMenu.name) },
            colors = ButtonDefaults.textButtonColors(contentColor = Color.White),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text("Skip Register")
        }
    }
}

