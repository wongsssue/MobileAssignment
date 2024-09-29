package com.example.funparkapp.data


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.annotation.Keep

@Keep
@Entity(tableName = "souvenir_table")
data class Souvenir(
    @PrimaryKey
    var id: Int =0, // Change this to String
    var firestoreId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val imageResource: Int = 0,
    var quantity: Int = 1
)
