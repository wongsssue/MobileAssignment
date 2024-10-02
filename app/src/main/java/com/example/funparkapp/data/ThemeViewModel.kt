package com.example.funparkapp.data

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ThemeViewModel : ViewModel() {
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> get() = _isDarkMode
    fun toggleTheme() {
        _isDarkMode.value = !_isDarkMode.value
    }
}
