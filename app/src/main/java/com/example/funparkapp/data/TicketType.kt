package com.example.funparkapp.data


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(tableName = "ticket_type")
data class TicketType(
    @PrimaryKey(autoGenerate = true)
    val ticketTypeId: Long = 0,
    val ticketType: String = "",
    val ticketDescription: String = "",
    val price: Double = 0.0,
    val ticketPlan:String = "",
    val pointsRequired: Int = 0
)
