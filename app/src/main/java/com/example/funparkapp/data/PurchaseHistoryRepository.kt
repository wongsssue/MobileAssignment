package com.example.funparkapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PurchaseHistoryRepository(
    private val purchasedTicketDao: PurchasedTicketDao,
    private val firebaseDatabase: DatabaseReference
) {
    private val _allPurchasedTickets = MutableLiveData<List<PurchaseWithTickets>>()
    val allPurchasedTickets: LiveData<List<PurchaseWithTickets>> = _allPurchasedTickets

    init {
        observePurchasesInRealtime()
        fetchPurchasesFromFirebase()
    }

    private fun syncPurchaseToFirebase(purchaseHistory: PurchaseHistory, purchasedItems: List<PurchasedItem>) {
        val purchaseId = purchaseHistory.id
        val purchaseData = hashMapOf(
            "id" to purchaseHistory.id,
            "ticketPlan" to purchaseHistory.ticketPlan,
            "pricePaid" to purchaseHistory.pricePaid,
            "purchasedDate" to purchaseHistory.purchasedDate,
            "purchasedItems" to purchasedItems.map { item ->
                hashMapOf(
                    "itemId" to item.itemId,
                    "ticketType" to item.ticketType,
                    "qty" to item.qty
                )
            }
        )

        firebaseDatabase.child("purchaseHistory").child(purchaseId.toString()).setValue(purchaseData)
            .addOnSuccessListener {
                Log.d("PurchaseHistoryRepository", "Purchase synced to Firebase: $purchaseHistory")
                fetchPurchasesFromFirebase()
            }
            .addOnFailureListener { e ->
                Log.e("PurchaseHistoryRepository", "Failed to sync purchase to Firebase: ${e.message}")
            }
    }

    suspend fun insertPurchaseWithItems(purchase: PurchaseHistory, purchasedItems: List<PurchasedItem>) {
        purchasedTicketDao.insertPurchaseWithItems(purchase, purchasedItems)
        syncPurchaseToFirebase(purchase, purchasedItems)
    }

    private fun fetchPurchasesFromFirebase() {
        firebaseDatabase.child("purchaseHistory").get()
            .addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    val purchaseList = mutableListOf<PurchaseWithTickets>()
                    for (purchaseSnapshot in dataSnapshot.children) {
                        val purchase = purchaseSnapshot.getValue(PurchaseHistory::class.java)
                        val items = purchaseSnapshot.child("purchasedItems").children.mapNotNull { itemSnapshot ->
                            itemSnapshot.getValue(PurchasedItem::class.java)
                        }
                        purchase?.let {
                            val purchaseWithTickets = PurchaseWithTickets(it, items)
                            purchaseList.add(purchaseWithTickets)

                            CoroutineScope(Dispatchers.IO).launch {
                                purchasedTicketDao.insertPurchaseWithItems(it, items)
                            }
                        }
                    }
                    _allPurchasedTickets.postValue(purchaseList)
                }
            }
            .addOnFailureListener { e ->
                Log.e("PurchaseHistoryRepository", "Failed to fetch purchases from Firebase: ${e.message}")
            }
    }

    fun getPurchaseWithTicketsById(ticketID: Long): LiveData<PurchaseWithTickets> {
        val purchaseFromRoom = purchasedTicketDao.getPurchaseWithTicketsById(ticketID)
        val purchaseWithTicketsLiveData = MediatorLiveData<PurchaseWithTickets>()

        purchaseWithTicketsLiveData.addSource(purchaseFromRoom) { purchaseWithTickets ->
            if (purchaseWithTickets != null) {
                purchaseWithTicketsLiveData.value = purchaseWithTickets
            } else {
                fetchPurchaseWithTicketsFromFirebase(ticketID, purchaseWithTicketsLiveData)
            }
        }

        return purchaseWithTicketsLiveData
    }

    fun getPurchaseByTicketID(ticketID: Long): LiveData<PurchaseHistory> {
        return purchasedTicketDao.getPurchaseByTicketID(ticketID)
    }

    private fun fetchPurchaseWithTicketsFromFirebase(ticketID: Long, liveData: MutableLiveData<PurchaseWithTickets>) {
        firebaseDatabase.child("purchaseHistory").orderByChild("id").equalTo(ticketID.toDouble())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (purchaseSnapshot in snapshot.children) {
                            val purchase = purchaseSnapshot.getValue(PurchaseHistory::class.java)
                            val items = purchaseSnapshot.child("purchasedItems").children.mapNotNull { itemSnapshot ->
                                itemSnapshot.getValue(PurchasedItem::class.java)
                            }
                            purchase?.let {
                                val purchaseWithTickets = PurchaseWithTickets(it, items)
                                liveData.postValue(purchaseWithTickets)
                                CoroutineScope(Dispatchers.IO).launch {
                                    purchasedTicketDao.insertPurchaseWithItems(it, items)
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("PurchaseHistoryRepository", "Failed to fetch purchase by ID from Firebase: ${error.message}")
                }
            })
    }

    private fun observePurchasesInRealtime() {
        firebaseDatabase.child("purchaseHistory").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val purchaseList = mutableListOf<PurchaseWithTickets>()
                for (purchaseSnapshot in snapshot.children) {
                    val purchase = purchaseSnapshot.getValue(PurchaseHistory::class.java)
                    val items = purchaseSnapshot.child("purchasedItems").children.mapNotNull { itemSnapshot ->
                        itemSnapshot.getValue(PurchasedItem::class.java)
                    }
                    purchase?.let {
                        purchaseList.add(PurchaseWithTickets(it, items))

                        CoroutineScope(Dispatchers.IO).launch {
                            purchasedTicketDao.insertPurchaseWithItems(it, items)
                        }
                    }
                }
                _allPurchasedTickets.postValue(purchaseList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("PurchaseHistoryRepository", "Error observing purchases: ${error.message}")
            }
        })
    }
}
