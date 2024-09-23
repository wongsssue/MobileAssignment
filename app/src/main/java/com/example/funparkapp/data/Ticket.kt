package com.example.funparkapp.data

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tickets")
data class Ticket(
    @PrimaryKey(autoGenerate = false)
    val ticketPlan: String,
    val ticketPlanDescription: String,
    @DrawableRes val imageResId: Int
)