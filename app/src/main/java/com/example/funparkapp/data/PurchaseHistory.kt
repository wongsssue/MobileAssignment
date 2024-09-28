package com.example.funparkapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "ticket_purchased")
data class PurchaseHistory(
    @PrimaryKey
    val id: Long = 0L,
    val ticketPlan:String= "",
    val qty: Int,
    val pricePaid: Double = 0.0,
    val purchasedDate: Date = Date()
) {

        // No-argument constructor for Firebase
        constructor() : this(0L, "", 0, 0.0, Date())

}
