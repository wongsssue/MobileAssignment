package com.example.funparkapp.data

import androidx.lifecycle.LiveData
import com.example.funparkapp.data.Reservation
import com.example.funparkapp.data.ReservationDao

class ReservationRepository(private val reservationDao: ReservationDao) {

    val allReservation: LiveData<List<Reservation>> = reservationDao.getAllReservations()

    // Function to insert a new reservation
    suspend fun insertReservation(reservation: Reservation) {
        reservationDao.insertReservation(reservation)
    }

    // Function to get the total count of reservations
    suspend fun getReservationCount(): Int {
        return reservationDao.getReservationCount()
    }

    // Function to get a reservation by its ID
    fun getReservationById(reservationID: String): LiveData<Reservation> {
        return reservationDao.getReservationById(reservationID)
    }

    // Function to delete a reservation by its ID
    suspend fun deleteReservationById(reservationID: String) {
        return reservationDao.deleteReservationById(reservationID)
    }
}
