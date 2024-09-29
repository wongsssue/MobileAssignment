package com.example.funparkapp.data

import android.annotation.SuppressLint
import android.util.Log
import com.example.funparkapp.R
import com.google.firebase.firestore.FirebaseFirestore

object DataInitializer {
    @SuppressLint("StaticFieldLeak")
    private val db = FirebaseFirestore.getInstance()

    object SouvenirData {
        val souvenirs = listOf(
            Souvenir(1, "firestore_id_1", "Keychain", 5.99, R.drawable.keychain),
            Souvenir(2, "firestore_id_2", "Mug", 9.99, R.drawable.mug),
            Souvenir(3, "firestore_id_3", "T-shirt", 19.99, R.drawable.shirt),
            Souvenir(4, "firestore_id_4", "Book", 14.99, R.drawable.book),
            Souvenir(5, "firestore_id_5", "Bear", 7.99, R.drawable.bear),
            Souvenir(6, "firestore_id_6", "Bag", 12.99, R.drawable.bag),
            Souvenir(7, "firestore_id_7", "Disney Cup", 24.99, R.drawable.disney_cup),
            Souvenir(8, "firestore_id_8", "Cool Bear", 24.99, R.drawable.cool_bear)
        )
    }


    fun addSouvenirsToFirestore() {
        SouvenirData.souvenirs.forEach { souvenir ->
            db.collection("souvenirs").document(souvenir.id.toString())
                .set(souvenir)
                .addOnSuccessListener {
                    Log.d("Firestore", "Souvenir added: ${souvenir.name}")
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error adding souvenir", exception)
                }
        }
    }
}
