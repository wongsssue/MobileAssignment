package com.example.funparkapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface PaymentMethodDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPayMethod(paymentMethod: PaymentMethod)

    @Update
    suspend fun updatePayMethod(paymentMethod: PaymentMethod)

    @Query("DELETE FROM payMethod WHERE paymentMethodID = :paymentMethodID")
    suspend fun deletePayMethod(paymentMethodID: Int)

    @Delete
    suspend fun deletePayMethod(paymentMethod: PaymentMethod)

    @Query("SELECT * FROM payMethod")
    fun getAllPayMethods(): LiveData<List<PaymentMethod>>
}
