package com.example.funparkapp.data

import androidx.lifecycle.LiveData

class PaymentMethodRepository(private val paymentMethodDao: PaymentMethodDao) {

    val allPaymentMethods: LiveData<List<PaymentMethod>> = paymentMethodDao.getAllPayMethods()

    suspend fun addPaymentMethod(paymentMethod: PaymentMethod) {
        paymentMethodDao.addPayMethod(paymentMethod)
    }

    suspend fun updatePaymentMethod(paymentMethod: PaymentMethod) {
        paymentMethodDao.updatePayMethod(paymentMethod)
    }

    suspend fun deletePaymentMethod(paymentMethod: PaymentMethod) {
        paymentMethodDao.deletePayMethod(paymentMethod)
    }

    suspend fun deletePaymentMethodById(paymentMethodID: Int) {
        paymentMethodDao.deletePayMethod(paymentMethodID)
    }
}
