package com.example.funparkapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.funparkapp.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {
    private val _locations = MutableLiveData<List<Location>>()
    val locations: LiveData<List<Location>> get() = _locations

    private val firestore = FirebaseFirestore.getInstance()
    private val locationsCollection = firestore.collection("locations")

    private val allLocations = listOf(
        Location("Food Court 1", Category.FOOD, 150, 550, R.drawable.food_court1),
        Location("Food Court 2", Category.FOOD, 220, 270, R.drawable.foodcourt2),
        Location("Independence", Category.GAMES, 250, 220, R.drawable.independence_day),
        Location("Ice Age", Category.GAMES, 500, 60, R.drawable.ice_age),
        Location("Invasion Apes", Category.GAMES, 500, 60, R.drawable.invasion_apes),
        Location("Carousel", Category.GAMES, 180, 610, R.drawable.carousel),
        Location("Pirate Ship", Category.GAMES, 100, 540, R.drawable.pirate_ship),
        Location("Roller Coaster", Category.GAMES, 200, 40, R.drawable.roller_coaster_new),
        Location("Horrible Game", Category.GAMES, 270, 585, R.drawable.horrible_game),
        Location("Water Ride", Category.GAMES, 240, 685, R.drawable.water_ride),
        Location("Roller Coaster Kids", Category.GAMES, 240, 385, R.drawable.roller_coaster_new),
        Location("Souvenir Shop 1", Category.SOUVENIR, 150, 550, R.drawable.souvenir1),
        Location("Souvenir Shop 2", Category.SOUVENIR, 220, 270, R.drawable.souvenir2),
        Location("First Aid", Category.SERVICES, 140, 610, R.drawable.first_aid),
        Location("Help Desk", Category.SERVICES, 90, 260, R.drawable.help),
        Location("Toilet", Category.SERVICES, 260, 260, R.drawable.toilet)
    )

    init {
        fetchAllLocations()
    }

    private fun fetchAllLocations() {
        viewModelScope.launch {
            locationsCollection.get().addOnSuccessListener { documents ->
                val fetchedLocations = documents.mapNotNull { document ->
                    document.toObject(Location::class.java)
                }
                _locations.value = fetchedLocations
            }
        }
    }

    fun addLocation(location: Location) {
        viewModelScope.launch {
            locationsCollection.add(location).addOnSuccessListener {
                fetchAllLocations()
            }
        }
    }

    fun modifyLocation(location: Location) {
        viewModelScope.launch {
            locationsCollection.document(location.name).set(location)
                .addOnSuccessListener {
                    fetchAllLocations()
                }
        }
    }

    fun deleteLocation(locationId: String) {
        viewModelScope.launch {
            locationsCollection.document(locationId).delete()
                .addOnSuccessListener {
                    fetchAllLocations()
                }
                .addOnFailureListener { exception ->
                    Log.e("DeleteLocation", "Error deleting document", exception)
                }
        }
    }


    fun fetchLocations(category: String) {
        viewModelScope.launch {
            _locations.value = getLocationsForCategory(category)
        }
    }

    private fun getLocationsForCategory(category: String): List<Location> {
        return when (category) {
            "FOOD" -> allLocations.filter { it.category == Category.FOOD }
            "GAMES" -> allLocations.filter { it.category == Category.GAMES }
            "SOUVENIR" -> allLocations.filter { it.category == Category.SOUVENIR }
            "SERVICES" -> allLocations.filter { it.category == Category.SERVICES }
            else -> emptyList()
        }
    }
}