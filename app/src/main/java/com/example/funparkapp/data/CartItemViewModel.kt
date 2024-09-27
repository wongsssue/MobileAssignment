package com.example.funparkapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class CartItemViewModel(private val repository: CartItemRepository) : ViewModel() {
    val allCartItems: LiveData<List<CartItem>> = repository.getAllCartItems

    val totalPrice: LiveData<Double> = repository.getTotalPrice()

    fun insertCartItem(cartItem: CartItem) = viewModelScope.launch {
        repository.insertCartItem(cartItem)
    }

    fun deleteCartItem(cartItem: CartItem) = viewModelScope.launch {
        repository.deleteCartItem(cartItem)
    }

    fun deleteCartItemById(itemId: Int) = viewModelScope.launch {
        repository.deleteCartItemById(itemId)
    }
}

