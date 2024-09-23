package com.example.funparkapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface TicketDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTicket(ticket: Ticket)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTicketType(ticketType: TicketType)

    @Update
    suspend fun updateTicket(ticket: Ticket)

    @Query("DELETE FROM tickets WHERE ticketPlan = :ticketPlan")
    suspend fun deleteTicket(ticketPlan: String)

    @Delete
    suspend fun deleteTicketType(ticketType: TicketType)

    @Query("SELECT * FROM tickets ")
    fun getAllTickets(): LiveData<List<Ticket>>

    @Transaction
    @Query("SELECT * FROM tickets")
    fun getAllTicketsWithTypes(): LiveData<List<TicketWithTicketType>>

    @Transaction
    @Query("SELECT * FROM tickets WHERE ticketPlan = :ticketPlan")
    fun getTicketWithTicketType(ticketPlan: String): LiveData<List<TicketWithTicketType>>
}
