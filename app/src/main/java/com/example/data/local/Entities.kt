package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartEntity(
    @PrimaryKey val productId: String,
    val title: String,
    val category: String,
    val price: Double,
    val originalPrice: Double,
    val discountPercent: Int,
    val imageUrl: String,
    val quantity: Int,
    val seller: String,
    val isAssured: Boolean = true,
    val offlineStockAvailable: Boolean = true
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val timestamp: Long,
    val itemCount: Int,
    val totalAmount: Double,
    val status: String,
    val paymentMethod: String,
    val deliveryAddress: String,
    val isOfflinePickup: Boolean = false,
    val syncedWithFirebase: Boolean = false
)

@Entity(tableName = "wishlist")
data class WishlistEntity(
    @PrimaryKey val productId: String,
    val title: String,
    val price: Double,
    val originalPrice: Double,
    val discountPercent: Int,
    val imageUrl: String,
    val rating: Double
)
