package com.example.funparkapp.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FacilityViewModelFactory(private val facilityRepository: FacilityRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FacilityViewModel::class.java)) {
            return FacilityViewModel(facilityRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
