package com.example.funparkapp.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CheckoutViewModel : ViewModel() {
    private var _selectedPaymentMethod by mutableStateOf("")
    val selectedPaymentMethod: String
        get() = _selectedPaymentMethod

    fun savePaymentMethod(method: String) {
        _selectedPaymentMethod = method
    }
}