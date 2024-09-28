package com.example.funparkapp.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.funparkapp.R
import com.example.funparkapp.data.UserType
import com.example.funparkapp.data.UserViewModel
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ButtonDefaults

@Composable
fun AccountScreen(
    user: UserType,
    viewModel: UserViewModel,
    onUsernameChange: (String) -> Unit, // Modified to accept new username
    onPasswordChange: (String) -> Unit, // Modified to accept new password
    onSignOut: () -> Unit
) {
    val currentUser by viewModel.loggedInUser.collectAsState()

    if (currentUser == null) {
        Text("User not logged in")
        return
    }

    var showUsernameDialog by remember { mutableStateOf(false) }
    var newUsername by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf("") }

    var showPasswordDialog by remember { mutableStateOf(false) }
    var newPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Row for User Icon, Username, and Points
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.usericon),
                contentDescription = "User Icon",
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.width(16.dp)) // Space between icon and text
            Column {
                Text(text = currentUser!!.username, style = MaterialTheme.typography.titleLarge)
                Text(text = "Points: ${user.points}", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))

        // Change Username Button
        Button(
            onClick = { showUsernameDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))
        ) {
            Text("Change Username")
        }
        if (showUsernameDialog) {
            UsernameDialog(
                newUsername = newUsername,
                onUsernameChange = { newUsername = it },
                usernameError = usernameError,
                onConfirm = {
                    viewModel.changeUsername(user.username, newUsername) { error ->
                        usernameError = error
                    }
                    showUsernameDialog = false // Close dialog regardless of success
                },
                onDismiss = { showUsernameDialog = false }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Change Password Button
        Button(
            onClick = { showPasswordDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))
        ) {
            Text("Change Password")
        }
        if (showPasswordDialog) {
            PasswordDialog(
                newPassword = newPassword,
                onPasswordChange = { newPassword = it },
                passwordError = passwordError,
                onConfirm = {
                    viewModel.changePassword(user.username, newPassword) { error ->
                        passwordError = error
                    }
                    showPasswordDialog = false // Close dialog regardless of success
                },
                onDismiss = { showPasswordDialog = false }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Sign Out Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    viewModel.logout() // Clear the user state on sign out
                    onSignOut()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Sign Out")
            }
        }
    }
}


// Username Dialog
@Composable
fun UsernameDialog(
    newUsername: String,
    onUsernameChange: (String) -> Unit,
    usernameError: String,
    onConfirm: () -> Unit,
    onDismiss: ()-> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Username") },
        text = {
            Column {
                TextField(
                    value = newUsername,
                    onValueChange = onUsernameChange,
                    label = { Text("New Username") }
                )
                if (usernameError.isNotEmpty()) {
                    Text(
                        text = usernameError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Change")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Password Dialog (Similar structure to UsernameDialog)
@Composable
fun PasswordDialog(
    newPassword: String,
    onPasswordChange: (String) -> Unit,
    passwordError: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Password") },
        text = {
            Column {
                TextField(
                    value = newPassword,
                    onValueChange = onPasswordChange,
                    label = { Text("New Password") }
                )
                if (passwordError.isNotEmpty()) {
                    Text(
                        text = passwordError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Change")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}