package com.example.funparkapp.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PurchaseHistoryViewModelFactory(private val repository: PurchaseHistoryRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PurchaseHistoryViewModel::class.java)) {
            return PurchaseHistoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
