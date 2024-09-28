package com.example.funparkapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.funparkapp.R
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FacilityViewModel(private val repository: FacilityRepository) : ViewModel() {

    val allFacility: LiveData<List<Facility>> = repository.allFacility
    val allActiveFacility: LiveData<List<Facility>> = repository.allActiveFacility

    init {
        viewModelScope.launch {
            initializeData()
        }
    }

    private suspend fun initializeData() {
        val facilities = repository.allFacility.value
        val activeFacilities = repository.allActiveFacility.value
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        if (facilities.isNullOrEmpty()) {
            Log.d("FacilityViewModel", "Initializing data...")

            val facilityList = listOf(
                Facility(
                    facilityID = "F001",
                    facilityName = "Independence Day: Defiance",
                    facilityDesc = "Gear up and get ready to be part of an exhilarating space endeavour of galactic proportions.",
                    facilityType = "Family, Thrill, Adventure, 3D interactive, Indoor, Ride Through Darkness, Ok On rainy days",
                    facilityMinHeight = "100cm",
                    facilitySvCompanion = "Height to ride with a supervising companion between 100cm - 140cm",
                    facilityImage = R.drawable.independence_day,
                    facilityDateFrom = dateFormat.parse("2022-01-01")!!,
                    facilityDateTo = dateFormat.parse("2028-12-31")!!,
                    facilityTimeFrom = "10:00",
                    facilityTimeTo = "18:00",
                    facilityAvailability = true
                ),
                Facility(
                    facilityID = "F002",
                    facilityName = "ESD Global Defender",
                    facilityDesc = "Board a hybrid aerospace fighter and take flight through the galactic skies.",
                    facilityType = "Family, Thrill, Adventure, 3D interactive, Indoor, Ride Through Darkness, Ok On rainy days",
                    facilityMinHeight = "100cm",
                    facilitySvCompanion = "Height to ride with a supervising companion between 100cm - 140cm",
                    facilityImage = R.drawable.ed_global,
                    facilityDateFrom = dateFormat.parse("2022-01-01")!!,
                    facilityDateTo = dateFormat.parse("2028-12-31")!!,
                    facilityTimeFrom = "10:00",
                    facilityTimeTo = "18:00",
                    facilityAvailability = true
                ),
                Facility(
                    facilityID = "F003",
                    facilityName = "Terraform Tower Challenge",
                    facilityDesc = "Blast off to the sky on this vertical reality thrill ride before extraterrestrial beings strike!",
                    facilityType = "Family ,Thrill,Adventure,3D interactive,Indoor,Ride Through Darkness,Ok On rainy days",
                    facilityMinHeight = "100cm",
                    facilitySvCompanion = "Height to ride with a supervising companion between 100cm - 140cm",
                    facilityImage = R.drawable.terraform,
                    facilityDateFrom = dateFormat.parse("2022-01-01")!!,
                    facilityDateTo = dateFormat.parse("2028-12-31")!!,
                    facilityTimeFrom = "10:00",
                    facilityTimeTo = "18:00",
                    facilityAvailability = true
                ),
                Facility(
                    facilityID = "F004",
                    facilityName = "Invasion of the Planet of the Apes",
                    facilityDesc = "A pulse-pounding, 3D indoor ride featuring Caesar and other iconic primates battling for survival.",
                    facilityType = "Family ,Thrill,Adventure,3D interactive,Indoor,Ride Through Darkness,Ok On rainy days",
                    facilityMinHeight = "100cm",
                    facilitySvCompanion = "Height to ride with a supervising companion between 100cm - 140cm",
                    facilityImage = R.drawable.invasion_apes,
                    facilityDateFrom = dateFormat.parse("2022-01-01")!!,
                    facilityDateTo = dateFormat.parse("2028-12-31")!!,
                    facilityTimeFrom = "10:00",
                    facilityTimeTo = "18:00",
                    facilityAvailability = true
                ),
                Facility(
                    facilityID = "F005",
                    facilityName = "Alpha Fighters Pilot",
                    facilityDesc = "Strap yourself in for a thrilling space battle simulation of twists and 360 degree loops.",
                    facilityType = "Family ,Thrill,Adventure,3D interactive,Indoor,Ride Through Darkness,Ok On rainy days",
                    facilityMinHeight = "100cm",
                    facilitySvCompanion = "Height to ride with a supervising companion between 100cm - 140cm",
                    facilityImage = R.drawable.alpha,
                    facilityDateFrom = dateFormat.parse("2022-01-01")!!,
                    facilityDateTo = dateFormat.parse("2028-12-31")!!,
                    facilityTimeFrom = "10:00",
                    facilityTimeTo = "18:00",
                    facilityAvailability = true
                ),
                Facility(
                    facilityID = "F006",
                    facilityName = "Samba Gliders",
                    facilityDesc = "An ultimate bird’s-eye view of Rio from this family roller-coaster ride.",
                    facilityType = "Family ,Thrill,Adventure,3D interactive,Indoor,Ride Through Darkness,Ok On rainy days",
                    facilityMinHeight = "100cm",
                    facilitySvCompanion = "Height to ride with a supervising companion between 100cm - 140cm",
                    facilityImage = R.drawable.samba_gliders,
                    facilityDateFrom = dateFormat.parse("2022-01-01")!!,
                    facilityDateTo = dateFormat.parse("2028-12-31")!!,
                    facilityTimeFrom = "10:00",
                    facilityTimeTo = "18:00",
                    facilityAvailability = true
                )
            )

            facilityList.forEach { facility ->
                repository.insertFacility(facility)
            }
        }
    }

    fun insert(facility: Facility) = viewModelScope.launch {
        repository.insertFacility(facility)
    }

    fun update(facilityDateFrom: Date, facilityDateTo: Date, facilityTimeFrom: String, facilityTimeTo: String, facilityAvailability: Boolean, facilityname: String) = viewModelScope.launch {
        repository.updateFacilityByName(facilityDateFrom, facilityDateTo, facilityTimeFrom, facilityTimeTo, facilityAvailability, facilityname)
    }

    fun delete(name: String) = viewModelScope.launch {
        repository.deleteFacilityByName(name)
    }

    fun getFacilityCount(onResult: (Int) -> Unit) {
        viewModelScope.launch {
            val count = repository.getFacilityCount()
            onResult(count)
        }
    }

    fun getFacilityByName(name: String): LiveData<Facility> {
        return repository.getFacilityByName(name)
    }

    fun getFacilityIDByName(name: String): LiveData<String> {
        return repository.getFacilityIDByName(name)
    }
}
