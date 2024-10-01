package com.example.funparkapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PurchaseHistoryViewModel(private val repository: PurchaseHistoryRepository) : ViewModel() {
    val allPurchasedTickets: LiveData<List<PurchaseWithTickets>> = repository.allPurchasedTickets
    private val _qtyMap = MediatorLiveData<Map<String, Int>>() // Use MediatorLiveData
    val qtyMap: LiveData<Map<String, Int>> = _qtyMap

    fun getPurchaseWithTicketsById(ticketID: Long): LiveData<PurchaseWithTickets> {
        return repository.getPurchaseWithTicketsById(ticketID)
    }

    fun getPurchaseByTicketID(ticketID: Long): LiveData<PurchaseHistory> {
        return repository.getPurchaseByTicketID(ticketID)
    }

    fun getPurchasedItemByTicketID(ticketID: Long): LiveData<PurchasedItem> {
        return repository.getPurchasedItemByTicketID(ticketID)
    }

    init {

        _qtyMap.addSource(repository.allPurchasedTickets) { purchaseList ->
            val qtyMap = mutableMapOf<String, Int>()
            for(purchaseWithTickets in purchaseList) {
                for (item in purchaseWithTickets.purchasedItems) {
                    qtyMap[item.ticketType] = (qtyMap[item.ticketType] ?: 0) + item.qty
                }
            }
            _qtyMap.value = qtyMap // Update _qtyMap directly
        }
    }

    fun insertPurchaseWithItems(purchaseHistory: PurchaseHistory, purchasedItems: List<PurchasedItem>, onResult: (Long) -> Unit) {
        viewModelScope.launch {
            repository.insertPurchaseWithItems(purchaseHistory, purchasedItems)
            onResult(purchaseHistory.id)
        }
    }
}
