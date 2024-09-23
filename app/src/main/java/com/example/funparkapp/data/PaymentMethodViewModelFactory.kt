package com.example.funparkapp.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class PaymentMethodViewModelFactory(private val repository: PaymentMethodRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentMethodViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PaymentMethodViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
