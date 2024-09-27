package com.example.funparkapp.data

import androidx.room.Embedded
import androidx.room.Relation

data class PurchaseWithTickets(
    @Embedded val purchase: PurchaseHistory,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val purchasedItems: List<PurchasedItem>
)
