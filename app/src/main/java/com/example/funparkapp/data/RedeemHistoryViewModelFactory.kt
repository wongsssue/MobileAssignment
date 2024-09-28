package com.example.funparkapp.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RedeemHistoryViewModelFactory(private val redeemHistoryRepository: RedeemHistoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RedeemHistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RedeemHistoryViewModel(redeemHistoryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}