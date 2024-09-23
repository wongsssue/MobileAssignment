package com.example.funparkapp.data

import androidx.lifecycle.LiveData

class CartItemRepository(private val cartItemDao: CartItemDao) {
    val getAllCartItems: LiveData<List<CartItem>> = cartItemDao.getAllCartItems()

    suspend fun insertCartItem(cartItem: CartItem) {
        cartItemDao.insertCartItem(cartItem)
    }

    suspend fun deleteCartItem(cartItem: CartItem) {
        cartItemDao.deleteCartItem(cartItem)
    }

    suspend fun deleteCartItemById(itemId: Int) {
        cartItemDao.deleteCartItemById(itemId)
    }

    fun getTotalPrice(): LiveData<Double> {
        return cartItemDao.getTotalPrice()
    }
}