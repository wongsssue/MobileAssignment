package com.example.funparkapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RedeemHistoryViewModel(private val redeemHistoryRepository: RedeemHistoryRepository) : ViewModel() {
    val allRedeemHistory: LiveData<List<RedeemHistory>> = redeemHistoryRepository.allRedeemHistory

    fun insertRedeemHistory(redeemHistory: RedeemHistory, onResult: (Long) -> Unit) {
        viewModelScope.launch {
            val redeemId = redeemHistoryRepository.insertRedeemHistory(redeemHistory) // Get generated redeemId
            onResult(redeemId) // Pass redeemId to the callback
        }
    }

    fun getRedeemHistoryByTicketId(username: String): LiveData<List<RedeemHistory>> {
        return redeemHistoryRepository.getRedeemHistoryByTicketId(username)
    }

    fun getRedemptionHistoryByUsername(username: String) = redeemHistoryRepository.getRedemptionHistoryByUsername(username)

    fun getAllRedemptions() = redeemHistoryRepository.getAllRedemptions()
}