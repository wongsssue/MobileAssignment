package com.example.funparkapp.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SouvenirRepository {
    private val db = FirebaseFirestore.getInstance()
    private val souvenirCollection = db.collection("souvenirs")


    suspend fun addSouvenir(souvenir: Souvenir) {
        val documentReference = souvenirCollection.add(souvenir).await()
        souvenir.firestoreId = documentReference.id
    }


    suspend fun deleteSouvenir(souvenir: Souvenir) {
        souvenirCollection.document(souvenir.firestoreId).delete().await()
    }

    suspend fun modifySouvenir(souvenir: Souvenir) {
        souvenirCollection.document(souvenir.firestoreId).set(souvenir).await()
    }
}
