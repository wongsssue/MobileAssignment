package com.example.funparkapp.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

fun getOrderHistory(onComplete: (List<OrderDetail>) -> Unit, onError: (Exception) -> Unit) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    if (userId == null) {
        Log.w("OrderHistory", "User not authenticated")
        onError(Exception("User not authenticated"))
        return
    }

    val db = FirebaseFirestore.getInstance()

    db.collection("orders")
        .whereEqualTo("userId", userId)
        .get()
        .addOnSuccessListener { result ->
            val orderDetails = result.documents.mapNotNull { document ->
                document.toObject<OrderDetail>()
            }
            onComplete(orderDetails)
        }
        .addOnFailureListener { exception ->
            Log.w("OrderHistory", "Error retrieving order history: ", exception)
            onError(exception)
        }
}

