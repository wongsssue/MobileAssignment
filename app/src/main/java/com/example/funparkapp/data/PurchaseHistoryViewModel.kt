package com.example.funparkapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PurchaseHistoryViewModel(private val repository: PurchaseHistoryRepository) : ViewModel() {
    val allPurchasedTickets: LiveData<List<PurchaseWithTickets>> = repository.allPurchasedTickets

    fun getPurchaseWithTicketsById(ticketID: Long): LiveData<PurchaseWithTickets> {
        return repository.getPurchaseWithTicketsById(ticketID)
    }

    fun getPurchaseByTicketID(ticketID: Long): LiveData<PurchaseHistory> {
        return repository.getPurchaseByTicketID(ticketID)
    }

    fun insertPurchaseWithItems(purchaseHistory: PurchaseHistory, purchasedItems: List<PurchasedItem>, onResult: (Long) -> Unit) {
        viewModelScope.launch {
            repository.insertPurchaseWithItems(purchaseHistory, purchasedItems)
            onResult(purchaseHistory.id)
        }
    }
}
