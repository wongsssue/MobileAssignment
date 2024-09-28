package com.example.funparkapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.funparkapp.data.Facility

@Dao
interface ReservationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReservation(reservation: Reservation)

    @Query("SELECT * FROM reservation")
    fun getAllReservations(): LiveData<List<Reservation>>

    @Query("SELECT COUNT(*) FROM reservation")
    suspend fun getReservationCount(): Int

    @Query("SELECT * FROM reservation WHERE reservationID = :reservationID")
    fun getReservationById(reservationID: String): LiveData<Reservation>

    @Query("DELETE FROM reservation WHERE reservationID = :reservationID")
    suspend fun deleteReservationById(reservationID: String)
}
