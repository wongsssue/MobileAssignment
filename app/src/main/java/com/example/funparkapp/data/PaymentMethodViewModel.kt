package com.example.funparkapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.funparkapp.R
import kotlinx.coroutines.launch

class PaymentMethodViewModel(private val repository: PaymentMethodRepository) : ViewModel() {

    val allPaymentMethods: LiveData<List<PaymentMethod>> = repository.allPaymentMethods

    init {
        viewModelScope.launch {
            initializeData()
        }
    }

    private suspend fun initializeData() {

        if (allPaymentMethods.value.isNullOrEmpty()) {
            val defaultPaymentMethods = listOf(
                PaymentMethod(paymentMethod = "Credit/Debit Card", paymentMethodImg = R.drawable.debitorcredit),
                PaymentMethod(paymentMethod = "Touch 'n Go", paymentMethodImg = R.drawable.tngo),
                PaymentMethod(paymentMethod = "Online Banking", paymentMethodImg = R.drawable.onlinebanking),
                PaymentMethod(paymentMethod = "Boost", paymentMethodImg = R.drawable.boost),
                PaymentMethod(paymentMethod = "GrabPay", paymentMethodImg = R.drawable.grabpay),
                PaymentMethod(paymentMethod = "ShopeePay", paymentMethodImg = R.drawable.shopeepay)
            )
            defaultPaymentMethods.forEach { repository.addPaymentMethod(it) }
        }
    }

    fun addPaymentMethod(paymentMethod: PaymentMethod) {
        viewModelScope.launch {
            repository.addPaymentMethod(paymentMethod)
        }
    }

    fun updatePaymentMethod(paymentMethod: PaymentMethod) {
        viewModelScope.launch {
            repository.updatePaymentMethod(paymentMethod)
        }
    }

    fun deletePaymentMethod(paymentMethod: PaymentMethod) {
        viewModelScope.launch {
            repository.deletePaymentMethod(paymentMethod)
        }
    }

    fun deletePaymentMethodById(paymentMethodID: Int) {
        viewModelScope.launch {
            repository.deletePaymentMethodById(paymentMethodID)
        }
    }
}
