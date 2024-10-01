package com.example.funparkapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface PurchasedTicketDao {

    @Query("SELECT * FROM ticket_purchased WHERE id = :ticketID")
    fun getPurchaseByTicketID(ticketID: Long): LiveData<PurchaseHistory>

    @Transaction
    @Query("SELECT * FROM ticket_purchased")
    fun getAllPurchasesWithTickets(): LiveData<List<PurchaseWithTickets>>

    @Transaction
    @Query("SELECT * FROM ticket_purchased WHERE id = :ticketID")
    fun getPurchaseWithTicketsById(ticketID: Long): LiveData<PurchaseWithTickets>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPurchase(purchase: PurchaseHistory): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPurchasedItem(purchasedItem: PurchasedItem): Long

    @Transaction
    suspend fun insertPurchaseWithItems(
        purchase: PurchaseHistory,
        items: List<PurchasedItem>
    ) {
        val purchaseId = insertPurchase(purchase)
        for (item in items) {
            insertPurchasedItem(item.copy(id = purchaseId))
        }
    }

    @Query("SELECT * FROM item_purchased WHERE id = :ticketID")
    fun getPurchasedItemByTicketID(ticketID: Long): LiveData<PurchasedItem>
}
