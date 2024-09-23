package com.example.funparkapp.data

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payMethod")
data class PaymentMethod(
    @PrimaryKey(autoGenerate = true)
    val paymentMethodID: Int = 0,
    val paymentMethod: String,
    @DrawableRes val paymentMethodImg: Int
)
