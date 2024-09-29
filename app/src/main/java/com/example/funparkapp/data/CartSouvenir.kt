package com.example.funparkapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_souvenirs")
data class CartSouvenir(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val souvenirId: Int,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageResource: Int,
    var selected: Boolean = false,
    val orderDate: String? = null
)