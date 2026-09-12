package com.example.data.firebase

import android.util.Log
import com.example.data.local.OrderEntity
import com.example.data.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class FirebaseService {
    private val TAG = "BM_STORE_FIREBASE"
    private var firestore: FirebaseFirestore? = null

    init {
        try {
            firestore = FirebaseFirestore.getInstance()
            Log.d(TAG, "Firebase Firestore successfully initialized")
        } catch (e: Exception) {
            Log.w(TAG, "Firebase not initialized or running in local offline mode: ${e.message}")
        }
    }

    val isFirebaseConnected: Boolean
        get() = firestore != null

    suspend fun syncOrderToFirebase(order: OrderEntity): Boolean {
        val db = firestore ?: return false
        return try {
            val orderData = hashMapOf(
                "orderId" to order.orderId,
                "timestamp" to order.timestamp,
                "itemCount" to order.itemCount,
                "totalAmount" to order.totalAmount,
                "status" to order.status,
                "paymentMethod" to order.paymentMethod,
                "deliveryAddress" to order.deliveryAddress,
                "isOfflinePickup" to order.isOfflinePickup,
                "storeName" to "BM STORE ONLINE OFFLINE SHOPPING",
                "syncedAt" to System.currentTimeMillis()
            )
            db.collection("bm_orders")
                .document(order.orderId)
                .set(orderData, SetOptions.merge())
                .await()
            Log.d(TAG, "Order ${order.orderId} successfully synced to Firebase")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing order to Firebase: ${e.message}")
            false
        }
    }

    suspend fun syncCartItemToFirebase(userId: String, productId: String, quantity: Int): Boolean {
        val db = firestore ?: return false
        return try {
            val cartData = hashMapOf(
                "productId" to productId,
                "quantity" to quantity,
                "updatedAt" to System.currentTimeMillis()
            )
            db.collection("bm_users")
                .document(userId)
                .collection("cart")
                .document(productId)
                .set(cartData, SetOptions.merge())
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing cart to Firebase: ${e.message}")
            false
        }
    }
}
