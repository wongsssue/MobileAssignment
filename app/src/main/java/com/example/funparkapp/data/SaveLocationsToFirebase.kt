package com.example.funparkapp.data

import android.util.Log
import com.example.funparkapp.R
import com.google.firebase.firestore.FirebaseFirestore

fun saveLocationsToFirebase() {
    val firestore = FirebaseFirestore.getInstance()

    // Sample data
    val locations = listOf(
        Location("Food Court 1", Category.FOOD, 150, 550, R.drawable.food_court1),
        Location("Food Court 2", Category.FOOD, 220, 270, R.drawable.foodcourt2),
        Location("Independence", Category.GAMES, 350, 300, R.drawable.independence_day),
        Location("Ice Age", Category.GAMES, 500, 60, R.drawable.ice_age),
        Location("Invasion Apes", Category.GAMES, 500, 60, R.drawable.invasion_apes),
        Location("Carousel", Category.GAMES, 500, 60, R.drawable.carousel),
        Location("Pirate Ship", Category.GAMES, 500, 60, R.drawable.pirate_ship),
        Location("Roller Coaster", Category.GAMES, 500, 60, R.drawable.roller_coaster_new),
        Location("Horrible Game", Category.GAMES, 500, 60, R.drawable.horrible_game),
        Location("Souvenir Shop 1", Category.SOUVENIR, 70, 80, R.drawable.souvenir1),
        Location("Souvenir Shop 2", Category.SOUVENIR, 70, 80, R.drawable.souvenir2),
        Location("First Aid", Category.SERVICES, 900, 100, R.drawable.first_aid),
        Location("Help Desk", Category.SERVICES, 110, 120, R.drawable.help),
        Location("Toilet", Category.SERVICES, 110, 120, R.drawable.toilet)
    )

    val locationsByCategory = locations.groupBy { it.category }

    for ((category, locationList) in locationsByCategory) {
        val categoryRef = firestore.collection("locations").document(category.name)

        val locationsToSave = locationList.map { location ->
            mapOf(
                "name" to location.name,
                "latitude" to location.x,
                "longitude" to location.y,
                "imageResId" to location.imageResId
            )
        }

        categoryRef.set(mapOf("locations" to locationsToSave))
            .addOnSuccessListener {
                Log.d("Firestore", "Locations saved successfully for $category")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error saving locations for $category", e)
            }
    }
}
