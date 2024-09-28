package com.example.funparkapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RedeemHistoryDao {
    @Insert
    suspend fun insertRedeemHistory(redeemHistory: RedeemHistory): Long

    @Query("SELECT * FROM redeem_history WHERE username = :username")
    fun getRedeemHistoryByUsername(username: String): LiveData<List<RedeemHistory>>

    @Query("SELECT * FROM redeem_history")
    fun getAllRedeemHistory(): LiveData<List<RedeemHistory>>

    @Query("SELECT * FROM redeem_history WHERE username = :username")
    fun getRedemptionHistoryByUsername(username: String): Flow<List<RedeemHistory>>

    @Query("SELECT * FROM redeem_history")
    fun getAllRedemptions(): Flow<List<RedeemHistory>>
}