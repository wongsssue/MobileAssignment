package com.example.funparkapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.funparkapp.data.Reservation
import kotlinx.coroutines.launch

class ReservationViewModel(private val repository: ReservationRepository) : ViewModel() {

    val allReservation: LiveData<List<Reservation>> = repository.allReservation

    // Function to get a reservation by its ID
    fun getReservationById(reservationID: String): LiveData<Reservation> {
        return repository.getReservationById(reservationID)
    }

    // Function to delete a reservation by its ID
    fun deleteReservationById(reservationID: String) = viewModelScope.launch {
        repository.deleteReservationById(reservationID)
    }

    fun insert(reservation: Reservation) = viewModelScope.launch {
        repository.insertReservation(reservation)
    }

    // Function to get the total count of reservations
    fun getReservationCount(onResult: (Int) -> Unit) {
        viewModelScope.launch {
            val count = repository.getReservationCount()
            onResult(count)
        }
    }
}
