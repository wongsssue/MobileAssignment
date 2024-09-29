package com.example.funparkapp.data

data class Location(
    val name: String = "",
    val category: Category = Category.GAMES,
    val x: Int = 0,
    val y: Int = 0,
    val imageResId: Int = 0
)
