package com.example.funparkapp.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore



fun saveOrderToFirebase(orderDetails: List<OrderDetail>) {
    val order = Order(items = orderDetails)

    val db = FirebaseFirestore.getInstance()
    db.collection("orders")
        .add(order)
        .addOnSuccessListener { documentReference ->
            Log.d("Checkout", "Order saved with ID: ${documentReference.id}")
        }
        .addOnFailureListener { e ->
            Log.w("Checkout", "Error saving order", e)
        }
}
