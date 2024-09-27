package com.example.funparkapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val ticketPlan: String,
    val ticketType: String,
    val price: Double,
    val quantity: Int
)
