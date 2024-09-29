package com.example.funparkapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "ticket_purchased")
data class PurchaseHistory(
    @PrimaryKey
    val id: Long = 0L,
    val pricePaid: Double = 0.0,
    val purchasedDate: Date = Date()
)



