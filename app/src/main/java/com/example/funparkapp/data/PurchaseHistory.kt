package com.example.funparkapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "ticket_purchased")
data class PurchaseHistory(
    @PrimaryKey
    val id: Long,
    val ticketPlan: String,
    val ticketType: String,
    val qty: Int,
    val pricePaid: Double,
    val purchasedDate: Date
)
