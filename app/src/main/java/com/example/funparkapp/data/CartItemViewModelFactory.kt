package com.example.funparkapp.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CartItemViewModelFactory(private val cartItemRepository: CartItemRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartItemViewModel(cartItemRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}