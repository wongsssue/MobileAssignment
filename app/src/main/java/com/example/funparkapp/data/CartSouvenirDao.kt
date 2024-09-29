package com.example.funparkapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface CartSouvenirDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cartSouvenir: CartSouvenir)

    @Update
    suspend fun update(cartSouvenir: CartSouvenir)

    @Delete
    suspend fun delete(cartSouvenir: CartSouvenir)

    @Query("SELECT * FROM cart_souvenirs")
    suspend fun getAllCartItems(): List<CartSouvenir>

    @Query("SELECT * FROM cart_souvenirs WHERE selected = 1")
    suspend fun getSelectedItems(): List<CartSouvenir> // Fetch only selected items
}
