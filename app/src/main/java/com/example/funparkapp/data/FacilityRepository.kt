package com.example.funparkapp.data

import androidx.lifecycle.LiveData
import androidx.room.Query
import java.util.Date

class FacilityRepository(private val facilityDao: FacilityDao) {

    val allFacility: LiveData<List<Facility>> = facilityDao.getAllFacilities()
    val allActiveFacility: LiveData<List<Facility>> = facilityDao.getAllActiveFacilities()

    suspend fun insertFacility(facility: Facility) {
        facilityDao.insertFacility(facility)
    }

    fun getFacilityByName(name: String): LiveData<Facility> {
        return facilityDao.getFacilityByName(name)
    }

    suspend fun deleteFacilityByName(name: String) {
        facilityDao.deleteFacilityByName(name)
    }

    suspend fun updateFacilityByName(facilityDateFrom: Date, facilityDateTo: Date, facilityTimeFrom: String, facilityTimeTo: String, facilityAvailability: Boolean, name: String) {
        facilityDao.updateFacilityByName(facilityDateFrom, facilityDateTo, facilityTimeFrom, facilityTimeTo, facilityAvailability, name)
    }

    suspend fun getFacilityCount(): Int {
        return facilityDao.getFacilityCount()
    }

    fun getFacilityIDByName(name: String): LiveData<String> {
        return facilityDao.getFacilityIDByName(name)
    }
}
