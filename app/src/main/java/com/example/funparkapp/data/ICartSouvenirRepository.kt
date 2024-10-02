package com.example.funparkapp.data

interface ICartSouvenirRepository {
    suspend fun insert(cartSouvenir: CartSouvenir)
    suspend fun update(cartSouvenir: CartSouvenir)
    suspend fun delete(cartSouvenir: CartSouvenir)
    suspend fun getAllCartItems(): List<CartSouvenir>
    suspend fun getSelectedItems(): List<CartSouvenir>
}