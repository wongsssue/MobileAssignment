package com.example.funparkapp.data

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.Date

@Dao
interface FacilityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFacility(facility: Facility)

    @Query("SELECT * FROM facilities WHERE facilityName = :name")
    fun getFacilityByName(name: String): LiveData<Facility>

    @Query("SELECT facilityID FROM facilities WHERE facilityName = :name")
    fun getFacilityIDByName(name: String): LiveData<String>

    @Query("SELECT * FROM facilities")
    fun getAllFacilities(): LiveData<List<Facility>>

    @Query(
        """
    SELECT * FROM facilities
WHERE (
    facilityAvailability = 1 
    AND DATE('now') BETWEEN 
    DATE(facilityDateFrom / 1000, 'unixepoch') 
    AND DATE(facilityDateTo / 1000, 'unixepoch')
) 
OR (
    facilityAvailability = 0 
    AND DATE('now') NOT BETWEEN 
    DATE(facilityDateFrom / 1000, 'unixepoch') 
    AND DATE(facilityDateTo / 1000, 'unixepoch')
)
    """
    )
    fun getAllActiveFacilities(): LiveData<List<Facility>>


    @Query("DELETE FROM facilities WHERE facilityName = :name")
    suspend fun deleteFacilityByName(name: String)

    @Query("UPDATE facilities SET facilityDateFrom = :facilityDateFrom, facilityDateTo = :facilityDateTo, facilityTimeFrom = :facilityTimeFrom, facilityTimeTo = :facilityTimeTo, facilityAvailability = :facilityAvailability WHERE facilityName = :name")
    suspend fun updateFacilityByName(facilityDateFrom: Date, facilityDateTo: Date, facilityTimeFrom: String, facilityTimeTo: String, facilityAvailability: Boolean, name: String)

    @Query("SELECT COUNT(*) FROM facilities")
    suspend fun getFacilityCount(): Int
}