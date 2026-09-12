package com.example.data.model

import com.example.data.local.ProductEntity

data class Product(
    val id: String,
    val title: String,
    val category: String,
    val subcategory: String = "General",
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
    val stockCount: Int = 20,
    val seller: String = "BM Retail Hub",
    val offlineStockAvailable: Boolean = true,
    val offlineStoreLocation: String = "BM STORE Central Hub & Express Counter",
    val variants: List<String> = emptyList(),
    val colors: List<String> = emptyList()
) {
    fun toEntity(): ProductEntity {
        return ProductEntity(
            id = id,
            title = title,
            category = category,
            subcategory = subcategory,
            price = price,
            originalPrice = originalPrice,
            discountPercent = discountPercent,
            rating = rating,
            ratingCount = ratingCount,
            imageUrl = imageUrl,
            brand = brand,
            description = description,
            highlightsRaw = highlights.joinToString(" | "),
            stockCount = stockCount,
            isAssured = isAssured,
            freeDelivery = freeDelivery,
            seller = seller,
            offlineStockAvailable = offlineStockAvailable,
            offlineStoreLocation = offlineStoreLocation,
            variantsRaw = variants.joinToString(","),
            colorsRaw = colors.joinToString(",")
        )
    }

    companion object {
        fun fromEntity(entity: ProductEntity): Product {
            return Product(
                id = entity.id,
                title = entity.title,
                category = entity.category,
                subcategory = entity.subcategory,
                price = entity.price,
                originalPrice = entity.originalPrice,
                discountPercent = entity.discountPercent,
                rating = entity.rating,
                ratingCount = entity.ratingCount,
                imageUrl = entity.imageUrl,
                isAssured = entity.isAssured,
                freeDelivery = entity.freeDelivery,
                brand = entity.brand,
                description = entity.description,
                highlights = entity.highlightsRaw.split("|").map { it.trim() }.filter { it.isNotEmpty() },
                stockCount = entity.stockCount,
                seller = entity.seller,
                offlineStockAvailable = entity.offlineStockAvailable,
                offlineStoreLocation = entity.offlineStoreLocation,
                variants = if (entity.variantsRaw.isNotBlank()) entity.variantsRaw.split(",").map { it.trim() } else emptyList(),
                colors = if (entity.colorsRaw.isNotBlank()) entity.colorsRaw.split(",").map { it.trim() } else emptyList()
            )
        }
    }
}

data class CategoryItem(
    val id: String,
    val name: String,
    val iconName: String,
    val bannerText: String = "Min. 50% Off",
    val subcategories: List<String> = listOf("All", "Popular", "New Arrivals", "Trending")
)

data class BannerItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val tag: String,
    val categoryTarget: String
)

data class DeliveryAddress(
    val id: String = "default_id",
    val fullName: String = "Rahim Ahmed",
    val phone: String = "+91 98765 43210",
    val pincode: String = "700001",
    val houseDetails: String = "Flat 4B, Emerald Heights, Park Street",
    val city: String = "Kolkata",
    val state: String = "West Bengal",
    val landmark: String = "Near Metro Station",
    val addressType: String = "Home",
    val isDefault: Boolean = true
)

data class UserSession(
    val id: String = "guest_user",
    val name: String = "Guest User",
    val email: String = "guest@bmstore.com",
    val phone: String = "",
    val role: String = "customer", // "customer" or "admin"
    val isLoggedIn: Boolean = false
)

data class ReviewItem(
    val id: String,
    val author: String,
    val rating: Int,
    val title: String,
    val comment: String,
    val date: String,
    val verifiedBuyer: Boolean = true
)

data class FaqItem(
    val question: String,
    val answer: String,
    val category: String = "General"
)
