package com.example.data.model

data class Product(
    val id: String,
    val title: String,
    val category: String,
    val price: Double,
    val originalPrice: Double,
    val discountPercent: Int,
    val rating: Double,
    val ratingCount: Int,
    val imageUrl: String,
    val isAssured: Boolean = true,
    val freeDelivery: Boolean = true,
    val brand: String,
    val description: String,
    val highlights: List<String>,
    val seller: String = "BM Retail Store",
    val offlineStockAvailable: Boolean = true,
    val offlineStoreLocation: String = "BM STORE Central Hub & Express Counter"
)

data class CategoryItem(
    val id: String,
    val name: String,
    val iconName: String,
    val bannerText: String = "Min. 50% Off"
)

data class BannerItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val tag: String,
    val categoryTarget: String
)

data class DeliveryAddress(
    val fullName: String = "Rahim Ahmed",
    val phone: String = "+91 98765 43210",
    val pincode: String = "700001",
    val houseDetails: String = "Plot 42, Green Park",
    val city: String = "Kolkata",
    val state: String = "West Bengal",
    val landmark: String = "Near Metro Station",
    val addressType: String = "Home"
)
