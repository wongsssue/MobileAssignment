package com.example.funparkapp.data

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "facilities")
data class Facility(
    @PrimaryKey(autoGenerate = false)
    val facilityID: String,
    val facilityName: String,
    val facilityDesc: String,
    val facilityType: String,
    val facilityMinHeight: String,
    val facilitySvCompanion: String,
    @DrawableRes val facilityImage: Int,
    val facilityDateFrom: Date,
    val facilityDateTo: Date,
    val facilityTimeFrom: String,
    val facilityTimeTo: String,
    val facilityAvailability: Boolean
)
