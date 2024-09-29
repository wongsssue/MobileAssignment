package com.example.funparkapp.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class Order(
    val id: String = UUID.randomUUID().toString(),
    val items: List<OrderDetail> = emptyList(),
    val totalPrice: Double = calculateTotalPrice(items),
    val orderDate: String = getCurrentDate()
) {
    companion object {
        private fun getCurrentDate(): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return dateFormat.format(Date())
        }

        private fun calculateTotalPrice(items: List<OrderDetail>): Double {
            return items.sumOf { it.price * it.quantity }
        }
    }
}
