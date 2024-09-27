package com.example.funparkapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class PurchaseHistoryRepository(
    private val purchasedTicketDao: PurchasedTicketDao,
    private val firebaseDatabase: DatabaseReference
) {
    private val _allPurchasedTickets = MutableLiveData<List<PurchaseWithTickets>>()
    val allPurchasedTickets: LiveData<List<PurchaseWithTickets>> = _allPurchasedTickets

    init {
        fetchPurchasesFromFirebase()
        observePurchasesInRealtime()
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
        return purchasedTicketDao.getPurchaseWithTicketsById(ticketID)
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
