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
        fetchPurchasesFromFirebase()
        observePurchasesInRealtime()
    }

    private fun syncPurchaseToFirebase(
        purchaseHistory: PurchaseHistory,
        purchasedItems: List<PurchasedItem>
    ) {
        val purchaseId = purchaseHistory.id
        val purchaseData = hashMapOf(
            "id" to purchaseHistory.id,
            "pricePaid" to purchaseHistory.pricePaid,
            "purchasedDate" to purchaseHistory.purchasedDate,
            "purchasedItems" to purchasedItems.map { item ->
                hashMapOf(
                    "itemId" to item.itemId,
                    "ticketPlan" to item.ticketPlan,
                    "ticketType" to item.ticketType,
                    "qty" to item.qty
                )
            }
        )

        firebaseDatabase.child("purchaseHistory").child(purchaseId.toString())
            .setValue(purchaseData)
            .addOnSuccessListener {
                Log.d("PurchaseHistoryRepository", "Purchase synced to Firebase: $purchaseHistory")
                fetchPurchasesFromFirebase()
            }
            .addOnFailureListener { e ->
                Log.e(
                    "PurchaseHistoryRepository",
                    "Failed to sync purchase to Firebase: ${e.message}"
                )
            }
    }

    suspend fun insertPurchaseWithItems(
        purchase: PurchaseHistory,
        purchasedItems: List<PurchasedItem>
    ) {
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
                        val items =
                            purchaseSnapshot.child("purchasedItems").children.mapNotNull { itemSnapshot ->
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
                Log.e(
                    "PurchaseHistoryRepository",
                    "Failed to fetch purchases from Firebase: ${e.message}"
                )
            }
    }

    fun getPurchaseByTicketID(ticketID: Long): LiveData<PurchaseHistory> {
        return purchasedTicketDao.getPurchaseByTicketID(ticketID)
    }
    fun getPurchaseWithTicketsById(ticketID: Long): LiveData<PurchaseWithTickets> {
        val result = MediatorLiveData<PurchaseWithTickets>()

        firebaseDatabase.child("purchaseHistory").orderByChild("id").equalTo(ticketID.toDouble()).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val purchase = snapshot.children.firstOrNull()?.getValue(PurchaseHistory::class.java)
                    val items = snapshot.children.firstOrNull()?.child("purchasedItems")?.children?.mapNotNull {
                        it.getValue(PurchasedItem::class.java)
                    } ?: emptyList()

                    purchase?.let {
                        result.postValue(PurchaseWithTickets(it, items))
                    } ?: run {
                        fetchFromRoom(ticketID, result)
                    }
                } else {
                    fetchFromRoom(ticketID, result)
                }
            }
            .addOnFailureListener { e ->
                Log.e("PurchaseHistoryRepository", "Failed to fetch purchase from Firebase: ${e.message}")
                fetchFromRoom(ticketID, result)
            }

        return result
    }

    private fun fetchFromRoom(ticketID: Long, result: MediatorLiveData<PurchaseWithTickets>) {
        result.addSource(purchasedTicketDao.getPurchaseWithTicketsById(ticketID)) { purchaseWithTickets ->
            result.value = purchaseWithTickets
            result.removeSource(purchasedTicketDao.getPurchaseWithTicketsById(ticketID)) // Clean up
        }
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
