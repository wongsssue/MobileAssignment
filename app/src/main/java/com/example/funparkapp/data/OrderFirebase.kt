package com.example.funparkapp.data

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore



fun saveOrderToFirebase(orderDetails: List<OrderDetail>) {
    val order = Order(items = orderDetails)

    val db = FirebaseFirestore.getInstance()
    db.collection("orders")
        .add(order)
        .addOnSuccessListener { documentReference ->
            Log.d("Checkout", "Order saved with ID: ${documentReference.id}")
        }
        .addOnFailureListener { e ->
            Log.w("Checkout", "Error saving order", e)
        }
}


fun fetchOrdersFromFirebase(
    onOrdersFetched: (List<Order>) -> Unit,
    onError: (String) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    db.collection("orders")
        .get()
        .addOnSuccessListener { documents ->
            val orders = documents.mapNotNull { document ->
                document.toObject(Order::class.java)
            }
            onOrdersFetched(orders)
        }
        .addOnFailureListener { exception ->
            onError(exception.message ?: "Error fetching orders")
        }
}


@Composable
fun DisplayOrderDetails(orders: List<Order>) {
    Column {
        orders.forEach { order ->
            Text(text = "Order ID: ${order.id}", style = MaterialTheme.typography.headlineSmall)

            order.items.forEach { item ->
                Row {
                    Image(
                        painter = painterResource(id = item.imageResource),
                        contentDescription = "Image of ${item.name}",
                        modifier = Modifier.size(80.dp)
                    )
                    Column {
                        Text(text = item.name)
                        Text(text = "Price: RM ${"%.2f".format(item.price)}")
                        Text(text = "Quantity: ${item.quantity}")
                        Text(text = "Order Date: ${item.orderDate}")
                    }
                }
            }
        }
    }
}
