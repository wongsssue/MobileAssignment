package com.example.funparkapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item_purchased")
data class PurchasedItem(
    @PrimaryKey(autoGenerate = true)
    val itemId: Long = 0L,
    val id: Long = 0L,
    val ticketPlan:String= "",
    val ticketType: String = "",
    val qty: Int = 0
)
