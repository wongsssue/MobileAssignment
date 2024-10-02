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
    suspend fun insert(cartItem: CartSouvenir)

    @Update
    suspend fun update(cartItem: CartSouvenir)

    @Delete
    suspend fun delete(cartItem: CartSouvenir)

    @Query("SELECT * FROM cart_souvenirs")
    suspend fun getAllCartItems(): List<CartSouvenir>

    @Query("DELETE FROM cart_souvenirs")
    suspend fun deleteAll()
}