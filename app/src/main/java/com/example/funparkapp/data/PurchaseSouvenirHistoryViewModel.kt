package com.example.funparkapp.data

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PurchaseSouvenirHistoryViewModel : ViewModel() {
    private val _orderDetails = MutableStateFlow<List<Order>>(emptyList())
    val orderDetails: StateFlow<List<Order>> get() = _orderDetails

    init {
        fetchOrdersFromFirebase()
    }

    private fun fetchOrdersFromFirebase() {
        fetchOrdersFromFirebase(
            onOrdersFetched = { orders ->
                _orderDetails.value = orders
            },
            onError = { errorMessage ->
                Log.e("PurchaseHistory", "Error fetching orders: $errorMessage")
            }
        )
    }

    private fun fetchOrdersFromFirebase(
        onOrdersFetched: (List<Order>) -> Unit,
        onError: (String) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection("orders")
            .get()
            .addOnSuccessListener { documents ->
                documents.forEach { document ->
                    Log.d("FirestoreOrder", "Document ID: ${document.id}, Data: ${document.data}")
                }

                val orders = documents.mapNotNull { document ->
                    try {
                        document.toObject(Order::class.java)
                    } catch (e: Exception) {
                        Log.e("DeserializationError", "Failed to deserialize document ${document.id}: ${e.message}")
                        null
                    }
                }
                onOrdersFetched(orders)
            }
            .addOnFailureListener { exception ->
                onError(exception.message ?: "Error fetching orders")
            }
    }

    fun getCartItemsAndCalculateTotal() {
        viewModelScope.launch {
            val cartItems = getCartItems()
            val totalFee = calculateTotalFee(cartItems)
            val currentDate = getCurrentDate()

            Log.d("CartInfo", "Total Fee: $totalFee")
            Log.d("CartInfo", "Current Date: $currentDate")
        }
    }

    private suspend fun getCartItems(): List<CartItem> {
        return emptyList()
    }

    private fun calculateTotalFee(cartItems: List<CartItem>): Double {
        return cartItems.sumOf { it.price * it.quantity }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
