package com.example.funparkapp.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class SouvenirViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val repository = SouvenirRepository()

    private val _souvenirList = MutableStateFlow<List<Souvenir>>(emptyList())
    val souvenirList: StateFlow<List<Souvenir>> = _souvenirList

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText


    private val _cartItemCount = MutableStateFlow(0)
    val cartItemCount: StateFlow<Int> get() = _cartItemCount

    init {
        fetchSouvenirs()
    }
    fun addSouvenir(souvenir: Souvenir) {
        val souvenirId = UUID.randomUUID().toString()
        val newSouvenir = souvenir.copy(firestoreId = souvenirId)

        viewModelScope.launch {
            repository.addSouvenir(newSouvenir)
            fetchSouvenirs()
        }
    }

    fun deleteSouvenir(souvenir: Souvenir) {
        viewModelScope.launch {
            repository.deleteSouvenir(souvenir)
            fetchSouvenirs()
        }
    }

    fun modifySouvenir(souvenir: Souvenir) {
        viewModelScope.launch {
            repository.modifySouvenir(souvenir)
            fetchSouvenirs()
        }
    }

    private fun fetchSouvenirs() {
        db.collection("souvenirs")
            .get()
            .addOnSuccessListener { documents ->
                val souvenirs = documents.map { document ->
                    document.toObject(Souvenir::class.java).copy(firestoreId = document.id)
                }
                _souvenirList.value = souvenirs
            }
            .addOnFailureListener { exception ->

            }
    }

    fun updateSearchText(newText: String) {
        _searchText.value = newText
    }

    fun addToCart(souvenir: Souvenir, quantity: Int) {
        // Logic to add to cart...
        _cartItemCount.value += quantity
    }
}
