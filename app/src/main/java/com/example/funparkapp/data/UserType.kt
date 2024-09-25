package com.example.funparkapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserType(
    @PrimaryKey val username: String,
    val email: String,
    val password: String, // Store hashed password
    val points: Int
)