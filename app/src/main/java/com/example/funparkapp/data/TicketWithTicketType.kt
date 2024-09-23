package com.example.funparkapp.data

import androidx.room.Embedded
import androidx.room.Relation

data class TicketWithTicketType(
    @Embedded val ticket: Ticket,
    @Relation(
        parentColumn = "ticketPlan",
        entityColumn = "ticketPlan"
    )
    val ticketTypes: List<TicketType>
)
