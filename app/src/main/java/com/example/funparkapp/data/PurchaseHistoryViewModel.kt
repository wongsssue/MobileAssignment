package com.example.funparkapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PurchaseHistoryViewModel(private val repository: PurchaseHistoryRepository) : ViewModel() {

    val allPurchasedTickets: LiveData<List<PurchaseHistory>> = repository.allPurchasedTickets

    fun getPurchaseByTicketID(ticketID: Long): LiveData<PurchaseHistory> {
        return repository.getPurchaseByTicketID(ticketID)
    }

    fun insertPurchase(purchaseHistory: PurchaseHistory, onResult: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.insertPurchase(purchaseHistory)
            onResult(id)
        }
    }
}
