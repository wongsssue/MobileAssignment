package com.example.funparkapp.data

import androidx.lifecycle.LiveData

class PurchaseHistoryRepository(private val purchasedTicketDao: PurchasedTicketDao) {

    val allPurchasedTickets: LiveData<List<PurchaseHistory>> = purchasedTicketDao.getTicketPurchased()

    fun getPurchaseByTicketID(ticketID: Long): LiveData<PurchaseHistory> {
        return purchasedTicketDao.getPurchaseByTicketID(ticketID)
    }

    suspend fun insertPurchase(purchaseHistory: PurchaseHistory): Long {
        return purchasedTicketDao.insertPurchase(purchaseHistory)
    }

}
