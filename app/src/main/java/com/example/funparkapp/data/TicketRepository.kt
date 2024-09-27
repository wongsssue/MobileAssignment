package com.example.funparkapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class TicketRepository(
    private val ticketDao: TicketDao,
    private val firebaseDatabase: DatabaseReference,
    private val coroutineScope: CoroutineScope
) {
    fun getAllTickets(): LiveData<List<Ticket>> = ticketDao.getAllTickets()

    fun getTicketWithTicketType(ticketPlan: String): LiveData<List<TicketWithTicketType>> {
        Log.d("TicketRepository", "Fetching ticket with types for: $ticketPlan")
        return ticketDao.getTicketWithTicketType(ticketPlan)
    }

    suspend fun insertTicket(ticket: Ticket) {
        Log.d("TicketRepository", "Inserting ticket: $ticket")
        ticketDao.addTicket(ticket)
        val ticketTypes = ticketDao.getTicketTypesByPlan(ticket.ticketPlan)
        syncTicketToFirebase(ticket, ticketTypes)
    }

    suspend fun insertTicketType(ticketType: TicketType) {
        Log.d("TicketRepository", "Inserting ticket type: $ticketType")
        val existingType = ticketDao.findTicketType(ticketType.ticketType, ticketType.ticketPlan)
        if (existingType == null) {
            ticketDao.addTicketType(ticketType)
        } else {
            Log.d("TicketRepository", "Ticket type already exists: $ticketType")
        }
    }

    suspend fun deleteTicket(ticketPlan: String) {
        ticketDao.deleteTicket(ticketPlan)
        deleteTicketFromFirebase(ticketPlan)
    }

    fun syncTicketToFirebase(ticket: Ticket, ticketTypes: List<TicketType>): Task<Void> {
        val ticketId = ticket.ticketPlan
        val ticketData = hashMapOf(
            "ticketPlan" to ticket.ticketPlan,
            "ticketPlanDescription" to ticket.ticketPlanDescription,
            "imageResId" to ticket.imageResId,
            "ticketTypes" to ticketTypes.map { type ->
                hashMapOf(
                    "ticketType" to type.ticketType,
                    "price" to type.price,
                    "ticketDescription" to type.ticketDescription
                )
            }
        )

        return firebaseDatabase.child("tickets").child(ticketId).setValue(ticketData)
            .addOnSuccessListener {
                Log.d("TicketRepository", "Ticket synced to Firebase: $ticket")
            }
            .addOnFailureListener { e ->
                Log.e("TicketRepository", "Failed to sync ticket to Firebase: ${e.message}")
            }
    }


    private fun deleteTicketFromFirebase(ticketPlan: String) {
        firebaseDatabase.child("tickets").child(ticketPlan).removeValue()
            .addOnSuccessListener {
                Log.d("TicketRepository", "Ticket deleted from Firebase: $ticketPlan")
            }
            .addOnFailureListener { e ->
                Log.e("TicketRepository", "Failed to delete ticket from Firebase: ${e.message}")
            }
    }
    suspend fun syncTicketsFromFirebase(): LiveData<List<Ticket>> {
        val firebaseTickets = MutableLiveData<List<Ticket>>()

        firebaseDatabase.child("tickets").get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val ticketList = mutableListOf<Ticket>()

                for (ticketSnapshot in dataSnapshot.children) {
                    val ticket = ticketSnapshot.getValue(Ticket::class.java)
                    ticket?.let {
                        ticketList.add(it)
                        // Sync TicketType details
                        val ticketTypes = mutableListOf<TicketType>()
                        ticketSnapshot.child("ticketTypes").children.forEach { typeSnapshot ->
                            val ticketType = typeSnapshot.getValue(TicketType::class.java)
                            ticketType?.let { type ->
                                val ticketTypeEntity = type.copy(ticketPlan = it.ticketPlan)
                                ticketTypes.add(ticketTypeEntity)
                            }
                        }
                        // Insert ticket and its types into Room
                        coroutineScope.launch {
                            insertTicket(it)
                            ticketTypes.forEach { insertTicketType(it) }
                        }
                    }
                }
                // Update LiveData with fetched tickets
                firebaseTickets.postValue(ticketList)
            }
        }.addOnFailureListener { e ->
            Log.e("TicketRepository", "Failed to fetch tickets from Firebase: ${e.message}")
        }

        return firebaseTickets
    }

}
