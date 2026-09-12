package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val passwordHash: String,
    val role: String = "customer", // "customer" or "admin"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val subcategory: String = "General",
    val price: Double,
    val originalPrice: Double,
    val discountPercent: Int,
    val rating: Double = 4.5,
    val ratingCount: Int = 1200,
    val imageUrl: String,
    val brand: String,
    val description: String,
    val highlightsRaw: String = "",
    val stockCount: Int = 20,
    val isAssured: Boolean = true,
    val freeDelivery: Boolean = true,
    val seller: String = "BM Retail Hub",
    val offlineStockAvailable: Boolean = true,
    val offlineStoreLocation: String = "BM STORE Central Hub & Express Counter",
    val variantsRaw: String = "", // e.g. "S,M,L,XL" or "128 GB,256 GB"
    val colorsRaw: String = "" // e.g. "Black,Blue,Silver"
)

@Entity(tableName = "cart_items")
data class CartEntity(
    @PrimaryKey val id: String, // productId + variant combo
    val productId: String,
    val title: String,
    val category: String,
    val price: Double,
    val originalPrice: Double,
    val discountPercent: Int,
    val imageUrl: String,
    val quantity: Int,
    val selectedVariant: String = "Standard",
    val selectedColor: String = "Default",
    val seller: String = "BM Retail Hub",
    val isAssured: Boolean = true,
    val offlineStockAvailable: Boolean = true
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val userId: String = "guest_user",
    val customerName: String = "Valued Customer",
    val customerPhone: String = "",
    val timestamp: Long,
    val itemCount: Int,
    val totalAmount: Double,
    val discountAmount: Double = 0.0,
    val deliveryCharge: Double = 0.0,
    val status: String, // Order Placed, Confirmed, Processing, Packed, Shipped, Out for Delivery, Delivered, Cancelled
    val paymentMethod: String,
    val paymentStatus: String = "Paid",
    val paymentTransactionId: String = "",
    val deliveryAddress: String,
    val isOfflinePickup: Boolean = false,
    val itemsSummary: String = "",
    val cancelReason: String? = null,
    val trackingUpdatedTimestamp: Long = System.currentTimeMillis(),
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

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey val id: String,
    val userId: String = "guest_user",
    val fullName: String,
    val phone: String,
    val pincode: String,
    val houseDetails: String,
    val city: String,
    val state: String,
    val landmark: String = "",
    val addressType: String = "Home", // "Home" or "Work"
    val isDefault: Boolean = false
)

@Entity(tableName = "coupons")
data class CouponEntity(
    @PrimaryKey val code: String,
    val discountPercent: Int = 0,
    val flatDiscount: Double = 0.0,
    val minOrderAmount: Double = 0.0,
    val description: String,
    val isActive: Boolean = true
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val type: String = "order", // order, promo, system
    val isRead: Boolean = false,
    val orderId: String? = null
)
