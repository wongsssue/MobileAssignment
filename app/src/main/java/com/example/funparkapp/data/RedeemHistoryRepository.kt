package com.example.funparkapp.data

import androidx.lifecycle.LiveData

class RedeemHistoryRepository(private val redeemHistoryDao: RedeemHistoryDao) {
    val allRedeemHistory: LiveData<List<RedeemHistory>> = redeemHistoryDao.getAllRedeemHistory()

    suspend fun insertRedeemHistory(redeemHistory: RedeemHistory): Long {
        return redeemHistoryDao.insertRedeemHistory(redeemHistory)
    }

    fun getRedeemHistoryByTicketId(username: String): LiveData<List<RedeemHistory>> {
        return redeemHistoryDao.getRedeemHistoryByUsername(username)
    }

    fun getRedemptionHistoryByUsername(username: String) = redeemHistoryDao.getRedemptionHistoryByUsername(username)

    fun getAllRedemptions() = redeemHistoryDao.getAllRedemptions()
}