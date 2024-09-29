package com.example.funparkapp.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class OrderDetail(
    val id: Int = 0,
    val name: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
    val imageResource: Int = 0,
    val orderDate: String = getCurrentDate()
) {
    companion object {

        fun getCurrentDate(): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return dateFormat.format(Date())
        }
    }
}
