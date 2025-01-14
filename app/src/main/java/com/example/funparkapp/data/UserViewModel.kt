package com.example.funparkapp.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    val userState = MutableStateFlow<UserType?>(null)
    private val _loggedInUser = MutableStateFlow<UserType?>(null)
    val loggedInUser: StateFlow<UserType?> get() = _loggedInUser


    private val _users = MutableStateFlow<List<UserType>>(emptyList())
    val users: StateFlow<List<UserType>> = _users
    init {
        viewModelScope.launch {
            _users.value = userRepository.getAllUsers()
        }
    }

    fun registerUser(username: String, email: String, password: String, role: String = "Customer") {
        viewModelScope.launch {
            val user = UserType(username, email, password, points = 0, role)
            userRepository.registerUser(user)
            userState.value = user // Update user state
        }
    }

    fun changeUsername(oldUsername: String, newUsername: String, onError: (String) -> Unit) {
        viewModelScope.launch {
            val existingUser = userRepository.getUserByUsername(newUsername)
            if (existingUser != null) {
                onError("Username already in use")
            } else {
                userRepository.changeUsername(oldUsername, newUsername)
                val currentUser = userRepository.getUserByUsername(oldUsername)
                userState.value = currentUser // Update user state
            }
        }
    }

    fun changePassword(username: String, newPassword: String, onError: (String) -> Unit) {
        viewModelScope.launch {
            val currentUser = userRepository.getUserByUsername(username)
            if (currentUser?.password == newPassword) {
                onError("Password is the same as the previous password")
            } else {
                userRepository.changePassword(username, newPassword)
                userState.value = currentUser // Update user state
            }
        }
    }

    suspend fun getUserByUsername(username: String): UserType? {
        return userRepository.getUserByUsername(username)
    }

    fun signOut() {
        // Add sign-out logic here, e.g., clear userState
        userState.value = null
    }

    suspend fun login(username: String, password: String): UserType? {
        return try {
            val user = userRepository.getUserByUsername(username)
            if (user != null && user.password == password) {
                _loggedInUser.value = user // Set the logged-in user
                user // Return the user object
            }else {
                null // Return null if login fails
            }
        } catch (e: Exception) {
            null // Return null if an exception occurs
        }
    }

    fun logout() {
        _loggedInUser.value = null
    }

    fun getLoggedInUser(): UserType? {
        return _loggedInUser.value
    }

    fun updateUser(user:UserType) {
        viewModelScope.launch {
            userRepository.updateUser(user)
            // Refresh the user list after updating
            _users.value = userRepository.getAllUsers()
        }
    }

    fun deleteUser(user: UserType) {
        viewModelScope.launch {
            userRepository.deleteUser(user)
            // Refresh the user list after deleting
            _users.value = userRepository.getAllUsers()
        }
    }

    fun updateUserPoints(username: String, newPoints: Int) {
        viewModelScope.launch {// 1. Update points in the data source
            userRepository.updateUserPoints(username, newPoints)

            // 2. Update userState flow
            userState.value = userRepository.getUserByUsername(username)
        }
    }
}