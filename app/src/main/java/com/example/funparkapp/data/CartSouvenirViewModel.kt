package com.example.funparkapp.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class CartSouvenirViewModel(application: Application) : AndroidViewModel(application) {
    private val cartSouvenirDao = AppDatabase.getDatabase(application).cartSouvenirDao
    private val firestore: FirebaseFirestore = Firebase.firestore

    private val _cartSouvenir = MutableStateFlow<List<CartSouvenir>>(emptyList())
    val cartSouvenir: MutableStateFlow<List<CartSouvenir>> get() = _cartSouvenir

    private val _allSouvenirs = MutableStateFlow<List<Souvenir>>(emptyList())
    val allSouvenirs: StateFlow<List<Souvenir>> get() = _allSouvenirs

    private val _orderDetails = MutableStateFlow<List<OrderDetail>>(emptyList())
    val orderDetails: StateFlow<List<OrderDetail>> get() = _orderDetails

    init {
        getCartSouvenir()
        fetchSouvenirs()
    }

    fun fetchSouvenirs() {
        viewModelScope.launch {
            _allSouvenirs.value = fetchSouvenirsFromFirestore()
        }
    }

    private suspend fun fetchSouvenirsFromFirestore(): List<Souvenir> {
        return try {
            val snapshot = firestore.collection("souvenirs").get().await()
            snapshot.documents.mapNotNull { document -> document.toObject(Souvenir::class.java) }
        } catch (e: Exception) {
            Log.e("CartViewModel", "Error fetching souvenirs: ${e.message}")
            emptyList()
        }
    }



    private fun updateCartItem(cartSouvenir: CartSouvenir) {
        viewModelScope.launch {
            try {
                cartSouvenirDao.update(cartSouvenir)
                _cartSouvenir.value =
                    _cartSouvenir.value.map { if (it.souvenirId == cartSouvenir.souvenirId) cartSouvenir else it }
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error updating cart item: ${e.message}")
            }
        }
    }

    fun addCartItem(souvenir: Souvenir, quantity: Int) {
        viewModelScope.launch {
            val existingItem = _cartSouvenir.value.find { it.souvenirId == souvenir.id }
            if (existingItem != null) {
                val updatedItem = existingItem.copy(quantity = existingItem.quantity + quantity)
                _cartSouvenir.value = _cartSouvenir.value.map { item ->
                    if (item.souvenirId == updatedItem.souvenirId) updatedItem else item
                }
            } else {
                val newItem = CartSouvenir(
                    souvenirId = souvenir.id,
                    name = souvenir.name,
                    price = souvenir.price,
                    quantity = quantity,
                    imageResource = souvenir.imageResource,
                    orderDate = "" // Set as needed
                )
                _cartSouvenir.value = _cartSouvenir.value + newItem
            }
        }
    }

    fun getCartSouvenir() {
        viewModelScope.launch {
            try {
                _cartSouvenir.value = cartSouvenirDao.getAllCartItems()
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error fetching cart items: ${e.message}")
                _cartSouvenir.value = emptyList()
            }
        }
    }

    fun clearCart() {
        _cartSouvenir.value = emptyList()
        Log.d("Checkout", "Cart cleared after checkout.")
    }

    fun increaseQuantity(cartSouvenir: CartSouvenir) {
        updateItemQuantity(cartSouvenir, 1)
    }

    fun decreaseQuantity(cartSouvenir: CartSouvenir) {
        if (cartSouvenir.quantity > 1) {
            updateItemQuantity(cartSouvenir, -1)
        }
    }

    private fun updateItemQuantity(cartSouvenir: CartSouvenir, change: Int) {
        viewModelScope.launch {
            val updatedCartItems = _cartSouvenir.value.toMutableList()
            val index = updatedCartItems.indexOf(cartSouvenir)
            if (index != -1) {
                updatedCartItems[index] =
                    updatedCartItems[index].copy(quantity = updatedCartItems[index].quantity + change)
                _cartSouvenir.value = updatedCartItems
                updateCartItem(updatedCartItems[index]) // Update the DB
            }
        }
    }

    fun removeCartItem(cartSouvenir: CartSouvenir) {
        viewModelScope.launch {
            try {
                val updatedCartItems = _cartSouvenir.value.toMutableList().apply { remove(cartSouvenir) }
                _cartSouvenir.value = updatedCartItems
                cartSouvenirDao.delete(cartSouvenir)
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error removing cart item: ${e.message}")
            }
        }
    }

    fun selectAll(selectAll: Boolean) {
        viewModelScope.launch {
            val updatedCartItems =
                _cartSouvenir.value.map { cartItem -> cartItem.copy(selected = selectAll) }
            _cartSouvenir.value = updatedCartItems
            updatedCartItems.forEach { updateCartItem(it) }
        }
    }

}
