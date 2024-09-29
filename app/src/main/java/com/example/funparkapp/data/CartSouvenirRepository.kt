package com.example.funparkapp.data

class CartSouvenirRepository(private val cartSouvenirDao: CartSouvenirDao) {
    suspend fun insert(cartSouvenir: CartSouvenir) {
        cartSouvenirDao.insert(cartSouvenir)
    }

    suspend fun update(cartSouvenir: CartSouvenir) {
        cartSouvenirDao.update(cartSouvenir)
    }

    suspend fun delete(cartSouvenir: CartSouvenir) {
        cartSouvenirDao.delete(cartSouvenir)
    }

    suspend fun getAllCartItems(): List<CartSouvenir> {
        return cartSouvenirDao.getAllCartItems()
    }

    suspend fun getSelectedItems(): List<CartSouvenir> {
        return cartSouvenirDao.getSelectedItems()
    }
}