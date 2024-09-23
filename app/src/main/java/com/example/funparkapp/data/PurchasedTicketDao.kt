package com.example.funparkapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PurchasedTicketDao {
    @Query("SELECT * FROM ticket_purchased")
    fun getTicketPurchased(): LiveData<List<PurchaseHistory>>

    @Query("SELECT * FROM ticket_purchased WHERE id = :ticketID")
    fun getPurchaseByTicketID(ticketID: Long): LiveData<PurchaseHistory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchase(purchase: PurchaseHistory):Long

}