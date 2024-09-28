package com.example.funparkapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import com.google.firebase.firestore.auth.User

@Entity(tableName = "redeem_history")
data class RedeemHistory(
    @PrimaryKey(autoGenerate = true) val redeemId: Long = 0,
    val ticketPlan: String,
    val redeemTime: String,
    val username: String,
)