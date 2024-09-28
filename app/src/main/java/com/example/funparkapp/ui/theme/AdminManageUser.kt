package com.example.funparkapp.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.funparkapp.data.UserType
import com.example.funparkapp.data.UserViewModel

data class FilterState(
    val sortBy: SortBy = SortBy.USERNAME_ASC,
    val role: String? = null
)

enum class SortBy(val displayName: String) {
    USERNAME_ASC("Username (A-Z)"),
    USERNAME_DESC("Username (Z-A)"),
    POINTS_HIGHEST("Points (Highest)"),
    POINTS_LOWEST("Points (Lowest)")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageUsersScreen(userViewModel: UserViewModel, navController: NavHostController) {
    var filterState by remember { mutableStateOf(FilterState()) }
    var searchQuery by remember { mutableStateOf("") }
    var showAddUserDialog by remember { mutableStateOf(false) }
    var showEditUserDialog by remember { mutableStateOf(false) }
    var userToEdit by remember { mutableStateOf<UserType?>(null) }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    var userToDelete by remember { mutableStateOf<UserType?>(null) }

    val filteredUsers = userViewModel.users.collectAsState().value
        .filter { user ->
            user.username.contains(searchQuery, ignoreCase = true) &&
                    (filterState.role == null || user.role == filterState.role)
        }
        .sortedWith(
            when (filterState.sortBy) {
                SortBy.USERNAME_ASC -> compareBy { it.username }
                SortBy.USERNAME_DESC -> compareByDescending { it.username }
                SortBy.POINTS_HIGHEST -> compareByDescending {it.points }
                SortBy.POINTS_LOWEST -> compareBy { it.points }
                else -> compareBy { it.username } // Default sorting
            }
        )



    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddUserDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add User")
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Manage Users") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Options (Dropdown or similar)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Sort By
                var expandedSortBy by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedSortBy,onExpandedChange = { expandedSortBy = !expandedSortBy }
                ) {
                    TextField(
                        readOnly = true,
                        value = filterState.sortBy.displayName,
                        onValueChange = { },
                        label = { Text("Sort By") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSortBy) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier.weight(1f)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedSortBy,
                        onDismissRequest = { expandedSortBy = false }
                    ) {
                        SortBy.entries.forEach { sortByOption ->
                            DropdownMenuItem(
                                text = { Text(sortByOption.displayName) },
                                onClick = {
                                    filterState = filterState.copy(sortBy = sortByOption)
                                    expandedSortBy = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Role Filter
                var expandedRole by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedRole,
                    onExpandedChange ={ expandedRole = !expandedRole }
                ) {
                    TextField(
                        readOnly = true,
                        value = filterState.role ?: "All Roles",
                        onValueChange = { },
                        label = { Text("Role") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRole) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier.weight(1f)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedRole,
                        onDismissRequest = { expandedRole = false }
                    ) {
                        val roles = listOf(null, "Admin", "Customer") // Add your roles here
                        roles.forEach { roleOption ->
                            DropdownMenuItem(
                                text = { Text(roleOption ?: "All Roles") },
                                onClick = {
                                    filterState = filterState.copy(role = roleOption)
                                    expandedRole = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User List
            LazyColumn {
                items(filteredUsers) { user ->
                    UserCard(user, userViewModel, navController,
                        onEditClick = {
                            // Handle edit action
                            userToEdit = user
                            showEditUserDialog = true
                        },
                        onDeleteClick = { selectedUser ->
                            // Handle delete action
                            showDeleteConfirmationDialog = true
                            userToDelete = selectedUser
                        }
                    )
                }
            }

            // Add User Dialog
            if (showAddUserDialog) {
                AddUserDialog(userViewModel) { showAddUserDialog = false }
            }

            // Edit User Dialog
            if (showEditUserDialog) {
                EditUserDialog(userViewModel, userToEdit) {
                    showEditUserDialog = false
                    userToEdit = null
                }
            }

            // Delete Confirmation Dialog
            if (showDeleteConfirmationDialog) {
                DeleteConfirmationDialog(
                    userToDelete?.username ?: "",
                    onConfirm = {
                        userViewModel.deleteUser(userToDelete!!)
                        showDeleteConfirmationDialog = false
                        userToDelete = null
                    },
                    onDismiss = {
                        showDeleteConfirmationDialog = false
                        userToDelete = null
                    }
                )
            }
        }
    }
}

@Composable
fun UserCard(
    user: UserType,
    userViewModel: UserViewModel,
    navController: NavHostController,
    onEditClick: () -> Unit,
    onDeleteClick: (UserType) -> Unit
) {

    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { /* Handle user click if needed */ },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Username: ${user.username}")
                Text("Email: ${user.email}")
                Text("Role: ${user.role}")
                Text("Points: ${user.points}")
            }

            IconButton(onClick = onEditClick) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit")
            }

            IconButton(onClick = { showDeleteConfirmationDialog = true  }) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete")
            }
        }
    }

    if (showDeleteConfirmationDialog) {
        DeleteConfirmationDialog(
            user.username,
            onConfirm = { onDeleteClick(user) },
            onDismiss = { showDeleteConfirmationDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserDialog(userViewModel: UserViewModel, onDismiss: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Customer") } // Default role

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New User") },
        text = {
            Column {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation() // Hide password
                )
                // Role selection (Dropdown or similar)
                var expandedRole by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedRole,
                    onExpandedChange = { expandedRole = !expandedRole }
                ) {
                    TextField(
                        readOnly = true,
                        value = role,
                        onValueChange = { },
                        label = { Text("Role") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRole) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedRole,
                        onDismissRequest = { expandedRole = false }
                    ) {
                        val roles =listOf("Admin", "Customer") // Add your roles here
                        roles.forEach { roleOption ->
                            DropdownMenuItem(
                                text = { Text(roleOption) },
                                onClick = {
                                    role = roleOption
                                    expandedRole = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    userViewModel.registerUser(username, email, password, role)
                    onDismiss()
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserDialog(userViewModel: UserViewModel, user: UserType?, onDismiss: () ->Unit) {
    var username by remember { mutableStateOf(user?.username ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var role by remember { mutableStateOf(user?.role ?: "Customer") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit User") },
        text = {
            Column {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username= it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                // Role selection (Dropdown or similar)
                var expandedRole by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedRole,
                    onExpandedChange = { expandedRole = !expandedRole }
                ) {
                    TextField(
                        readOnly = true,
                        value = role,
                        onValueChange = { },
                        label = { Text("Role") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRole) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedRole,
                        onDismissRequest = { expandedRole = false }
                    ) {
                        val roles = listOf("Admin", "Customer") // Add your roles here
                        roles.forEach {roleOption ->
                            DropdownMenuItem(
                                text = { Text(roleOption) },
                                onClick = {
                                    role = roleOption
                                    expandedRole = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Update user details in ViewModel
                    user?.let {
                        userViewModel.updateUser(it.copy(username = username, email = email, role = role))
                    }
                    onDismiss()
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun DeleteConfirmationDialog(
    username: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,title = { Text("Confirm Deletion") },
        text = { Text("Are you sure you want to delete user '$username'?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}