package com.example.funparkapp.data

import android.util.Log
import androidx.lifecycle.LiveData

class TicketRepository(private val ticketDao: TicketDao) {

    fun getAllTickets(): LiveData<List<Ticket>> = ticketDao.getAllTickets()

    fun getAllTicketsWithTypes(): LiveData<List<TicketWithTicketType>> {
        return ticketDao.getAllTicketsWithTypes()
    }

    fun getTicketWithTicketType(ticketPlan: String): LiveData<List<TicketWithTicketType>> {
        Log.d("TicketRepository", "Fetching ticket with types for: $ticketPlan")
        return ticketDao.getTicketWithTicketType(ticketPlan)
    }

    suspend fun insertTicket(ticket: Ticket){
        Log.d("TicketRepository", "Inserting ticket: $ticket")
        ticketDao.addTicket(ticket)
    }

    suspend fun insertTicketType(ticketType: TicketType) {
        Log.d("TicketRepository", "Inserting ticket type: $ticketType")
        ticketDao.addTicketType(ticketType)
    }

    suspend fun updateTicket(ticket: Ticket) {
        ticketDao.updateTicket(ticket)
    }

    suspend fun deleteTicket(ticketPlan: String) {
        ticketDao.deleteTicket(ticketPlan)
    }

    suspend fun deleteTicketType(ticketType: TicketType) {
        Log.d("TicketRepository", "Deleting ticket type: $ticketType")
        ticketDao.deleteTicketType(ticketType)
    }


}
