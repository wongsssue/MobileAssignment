package com.example.funparkapp.data
import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await



class CartSouvenirViewModel(application: Application): AndroidViewModel(application)  {
    private val cartItemDao = AppDatabase.getDatabase(application).cartSouvenirDao
    private val firestore: FirebaseFirestore = Firebase.firestore

    private val _cartItems = MutableStateFlow<List<CartSouvenir>>(emptyList())
    val cartItems: StateFlow<List<CartSouvenir>> get() = _cartItems // Change to StateFlow<List<CartSouvenir>>

    private val _allSouvenirs = MutableStateFlow<List<Souvenir>>(emptyList())
    val allSouvenirs: StateFlow<List<Souvenir>> get() = _allSouvenirs

    var selectedItems = mutableStateMapOf<Int, Boolean>() // Assuming souvenirId is Int

    init {
        getCartItems()
        fetchSouvenirs()
    }
    fun setSelectAll(checked: Boolean, cartItems: List<CartSouvenir>) {
        viewModelScope.launch {
            if (checked) {
                cartItems.forEach { item ->
                    selectedItems[item.souvenirId] = true
                }
            } else {
                selectedItems.clear()
            }
        }
    }

    // Toggle selection of an item
    fun toggleSelection(souvenirId: Int) {
        if (selectedItems.containsKey(souvenirId)) {
            selectedItems.remove(souvenirId)
        } else {
            selectedItems[souvenirId] = true
        }
    }

    // Clear selection
    fun clearSelection() {
        selectedItems.clear()
    }

    fun saveOrderToFirebase(orderDetails: List<OrderDetail>) {
        val db = FirebaseFirestore.getInstance()
        val batch = db.batch()

        orderDetails.forEach { orderDetail ->
            // Create a new document in the "orders" collection
            val orderRef = db.collection("orders").document() // Automatically generates a document ID
            batch.set(orderRef, orderDetail) // Assuming OrderDetail is a data class
        }

        batch.commit().addOnSuccessListener {
            Log.d("CartViewModel", "Orders saved successfully")
        }.addOnFailureListener { e ->
            Log.w("CartViewModel", "Error saving orders", e)
        }
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

    private fun updateCartItem(cartItem: CartSouvenir) {
        viewModelScope.launch {
            try {
                _cartItems.value = _cartItems.value.map { if (it.souvenirId == cartItem.souvenirId) cartItem else it }
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error updating cart item: ${e.message}")
            }
        }
    }

    fun addCartItem(souvenir: Souvenir, quantity: Int) {
        viewModelScope.launch {
            val existingItem = _cartItems.value.find { it.souvenirId == souvenir.id }
            if (existingItem != null) {
                val updatedItem = existingItem.copy(quantity = existingItem.quantity + quantity)
                _cartItems.value = _cartItems.value.map { item -> if (item.souvenirId == updatedItem.souvenirId) updatedItem else item }
                cartItemDao.update(updatedItem) // Update in Room
            } else {
                val newItem = CartSouvenir(
                    souvenirId = souvenir.id,
                    name = souvenir.name,
                    price = souvenir.price,
                    quantity = quantity,
                    imageResource = souvenir.imageResource,
                    orderDate = "" // Set as needed
                )
                _cartItems.value = _cartItems.value + newItem
                cartItemDao.insert(newItem) // Insert into Room
            }
        }
    }

    private fun getCartItems() {
        viewModelScope.launch {
            try {
                _cartItems.value = cartItemDao.getAllCartItems() // Fetch from Room, ensure this returns List<CartSouvenir>
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error fetching cart items: ${e.message}")
                _cartItems.value = emptyList()
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            cartItemDao.deleteAll()
            _cartItems.value = emptyList()
            Log.d("Checkout", "Cart cleared after checkout.")
        }
    }

    fun increaseQuantity(cartItem: CartSouvenir) {
        updateItemQuantity(cartItem, 1)
    }

    fun decreaseQuantity(cartItem: CartSouvenir) {
        if (cartItem.quantity > 1) {
            updateItemQuantity(cartItem, -1)
        }
    }

    private fun updateItemQuantity(cartItem: CartSouvenir, change: Int) {
        viewModelScope.launch {
            val updatedCartItems = _cartItems.value.toMutableList()
            val index = updatedCartItems.indexOf(cartItem)
            if (index != -1) {
                updatedCartItems[index] = updatedCartItems[index].copy(quantity = updatedCartItems[index].quantity + change)
                _cartItems.value = updatedCartItems
                updateCartItem(updatedCartItems[index])
            }
        }
    }

    fun removeCartItem(cartItem: CartSouvenir) {
        viewModelScope.launch {
            try {
                val updatedCartItems = _cartItems.value.toMutableList().apply { remove(cartItem) }
                _cartItems.value = updatedCartItems
                cartItemDao.delete(cartItem)
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error removing cart item: ${e.message}")
            }
        }
    }

    fun selectAll(selectAll: Boolean) {
        viewModelScope.launch {
            val updatedCartItems = _cartItems.value.map { cartItem -> cartItem.copy(selected = selectAll) }
            _cartItems.value = updatedCartItems
            updatedCartItems.forEach { updateCartItem(it) }
        }
    }
}

