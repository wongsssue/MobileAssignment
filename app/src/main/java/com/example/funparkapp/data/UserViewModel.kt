package com.example.funparkapp.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    val userState = MutableStateFlow<UserType?>(null) // To hold the current user

    fun registerUser(username: String, email: String, password: String) {
        viewModelScope.launch {
            val user = UserType(username, email, password, points = 0)
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
}