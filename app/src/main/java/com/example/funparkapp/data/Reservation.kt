package com.example.funparkapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import java.util.Date

@Entity(tableName = "reservation")
data class Reservation(
    @PrimaryKey val reservationID: String,
    val reservationTime: String,
    val reservationPax: String,
    val facilityName: String,
    val purchasedDate: Date
)
