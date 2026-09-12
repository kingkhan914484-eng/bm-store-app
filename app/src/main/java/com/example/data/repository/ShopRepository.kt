package com.example.data.repository

import com.example.data.firebase.FirebaseService
import com.example.data.local.AppDatabase
import com.example.data.local.CartEntity
import com.example.data.local.OrderEntity
import com.example.data.local.WishlistEntity
import com.example.data.model.BannerItem
import com.example.data.model.CategoryItem
import com.example.data.model.DeliveryAddress
import com.example.data.model.Product
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class ShopRepository(
    private val database: AppDatabase,
    private val firebaseService: FirebaseService = FirebaseService()
) {
    val allCartItems: Flow<List<CartEntity>> = database.cartDao().getAllCartItems()
    val allOrders: Flow<List<OrderEntity>> = database.orderDao().getAllOrders()
    val allWishlist: Flow<List<WishlistEntity>> = database.wishlistDao().getAllWishlist()

    val categories: List<CategoryItem> = listOf(
        CategoryItem("all", "All", "explore", "Top Offers"),
        CategoryItem("mobiles", "Mobiles", "smartphone", "Up to 40% Off"),
        CategoryItem("electronics", "Electronics", "laptop", "Min. 50% Off"),
        CategoryItem("fashion", "Fashion", "apparel", "60-80% Off"),
        CategoryItem("appliances", "Appliances", "tv", "Best Deals"),
        CategoryItem("home", "Home", "chair", "From ₹99"),
        CategoryItem("beauty", "Beauty & Toys", "face", "Extra 15% Off"),
        CategoryItem("grocery", "Grocery", "shopping_basket", "Up to 50% Off")
    )

    val banners: List<BannerItem> = listOf(
        BannerItem(
            id = "b1",
            title = "BIG SHOPPING DAYS",
            subtitle = "50% - 80% OFF on Top Brands & Electronics",
            tag = "SALE LIVE",
            categoryTarget = "electronics"
        ),
        BannerItem(
            id = "b2",
            title = "BM STORE MEGA DISCOUNTS",
            subtitle = "Flagship Smartphones & 5G Devices from ₹9,999",
            tag = "LIMITED TIME",
            categoryTarget = "mobiles"
        ),
        BannerItem(
            id = "b3",
            title = "ONLINE & OFFLINE SPECIAL",
            subtitle = "Buy Online or Instant Store Pickup at BM STORE",
            tag = "SPECIAL OFFER",
            categoryTarget = "all"
        )
    )

    val sampleProducts: List<Product> = listOf(
        Product(
            id = "mob_01",
            title = "Apple iPhone 15 (Black, 128 GB)",
            category = "mobiles",
            price = 69999.0,
            originalPrice = 79900.0,
            discountPercent = 12,
            rating = 4.6,
            ratingCount = 28410,
            imageUrl = "https://images.unsplash.com/photo-1510557880182-3d4d3cba35a5?w=500&q=80",
            isAssured = true,
            freeDelivery = true,
            brand = "Apple",
            description = "Dynamic Island, 48MP Main camera, USB-C, and durable color-infused glass and aluminum design.",
            highlights = listOf("128 GB ROM", "15.49 cm (6.1 inch) Super Retina XDR Display", "48MP + 12MP Dual Camera", "A16 Bionic Chip 6 Core Processor")
        ),
        Product(
            id = "mob_02",
            title = "Samsung Galaxy S24 5G (Onyx Black, 256 GB)",
            category = "mobiles",
            price = 74999.0,
            originalPrice = 89999.0,
            discountPercent = 16,
            rating = 4.5,
            ratingCount = 14320,
            imageUrl = "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=500&q=80",
            isAssured = true,
            freeDelivery = true,
            brand = "Samsung",
            description = "Galaxy AI is here. Search like never before, get quick language translation, and effortlessly edit your photos.",
            highlights = listOf("8 GB RAM | 256 GB ROM", "15.75 cm (6.2 inch) Full HD+ Dynamic AMOLED 2X", "50MP + 12MP + 10MP Triple Camera", "Exynos 2400 Processor")
        ),
        Product(
            id = "mob_03",
            title = "OnePlus Nord CE4 Lite 5G (Super Silver, 128 GB)",
            category = "mobiles",
            price = 19999.0,
            originalPrice = 22999.0,
            discountPercent = 13,
            rating = 4.3,
            ratingCount = 31200,
            imageUrl = "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=500&q=80",
            isAssured = true,
            freeDelivery = true,
            brand = "OnePlus",
            description = "120 Hz AMOLED Display, 5500 mAh battery with 80W SUPERVOOC fast charging and Sony LYT-600 Camera.",
            highlights = listOf("8 GB RAM | 128 GB ROM", "16.94 cm (6.67 inch) Full HD+ AMOLED 120Hz", "50MP Sony LYT-600 OIS Camera", "Snapdragon 695 5G Processor")
        ),
        Product(
            id = "elec_01",
            title = "Sony WH-1000XM5 Wireless Active Noise Cancelling Headphones",
            category = "electronics",
            price = 26990.0,
            originalPrice = 34990.0,
            discountPercent = 22,
            rating = 4.7,
            ratingCount = 8920,
            imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500&q=80",
            isAssured = true,
            freeDelivery = true,
            brand = "Sony",
            description = "Industry-leading noise cancellation with 8 microphones and Auto NC Optimizer. 30 hours battery life.",
            highlights = listOf("Industry-leading NC", "30-hr Battery Life", "Quick 3-min Charge for 3 hours", "Multipoint Connection")
        ),
        Product(
            id = "elec_02",
            title = "Apple MacBook Air Apple M2 - (8 GB/256 GB SSD/macOS)",
            category = "electronics",
            price = 84990.0,
            originalPrice = 99900.0,
            discountPercent = 14,
            rating = 4.8,
            ratingCount = 12900,
            imageUrl = "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=500&q=80",
            isAssured = true,
            freeDelivery = true,
            brand = "Apple",
            description = "Strikingly thin design with fast M2 chip, Liquid Retina display, 1080p FaceTime HD camera.",
            highlights = listOf("Apple M2 Processor", "8 GB Unified Memory", "256 GB SSD Storage", "34.54 cm (13.6 inch) Liquid Retina Display")
        ),
        Product(
            id = "elec_03",
            title = "Noise ColorFit Pulse 3 Smartwatch (1.96 inch TFT Display)",
            category = "electronics",
            price = 1499.0,
            originalPrice = 4999.0,
            discountPercent = 70,
            rating = 4.2,
            ratingCount = 84300,
            imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500&q=80",
            isAssured = true,
            freeDelivery = true,
            brand = "Noise",
            description = "1.96-inch TFT display, Bluetooth calling, 100+ sports modes, 7-day battery life, and complete health tracker.",
            highlights = listOf("1.96\" Big Display", "Bluetooth Calling", "100+ Sports Modes", "IP68 Water Resistant")
        ),
        Product(
            id = "fash_01",
            title = "Nike Air Max Impact 4 Basketball & Casual Sneakers For Men",
            category = "fashion",
            price = 4995.0,
            originalPrice = 8995.0,
            discountPercent = 44,
            rating = 4.4,
            ratingCount = 9800,
            imageUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500&q=80",
            isAssured = true,
            freeDelivery = true,
            brand = "Nike",
            description = "Max Air cushioning in the heel helps dissipate impact force. Rubber wraps up the sides for added durability.",
            highlights = listOf("Genuine Nike Footwear", "Max Air Heel Cushioning", "Breathable Mesh Upper", "Durable Traction Outsole")
        ),
        Product(
            id = "fash_02",
            title = "Puma Motorsport Men Slim Fit Casual Cotton Polo T-Shirt",
            category = "fashion",
            price = 1299.0,
            originalPrice = 2999.0,
            discountPercent = 56,
            rating = 4.3,
            ratingCount = 6540,
            imageUrl = "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=500&q=80",
            isAssured = true,
            freeDelivery = true,
            brand = "Puma",
            description = "Crafted from soft breathable 100% cotton with classic polo collar and ribbed cuffs for all-day comfort.",
            highlights = listOf("100% Bio-Washed Cotton", "Slim Fit Design", "Official Motorsport Emblem", "Machine Wash Safe")
        ),
        Product(
            id = "app_01",
            title = "LG 108 cm (43 inch) Ultra HD (4K) Smart WebOS TV",
            category = "appliances",
            price = 28990.0,
            originalPrice = 49990.0,
            discountPercent = 42,
            rating = 4.4,
            ratingCount = 18900,
            imageUrl = "https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=500&q=80",
            isAssured = true,
            freeDelivery = true,
            brand = "LG",
            description = "Real 4K clarity with α5 AI Processor 4K Gen6, HDR10 Pro, AI Sound and Magic Remote compatibility.",
            highlights = listOf("4K Ultra HD (3840 x 2160)", "20W Audio Output with AI Sound", "WebOS with ThinQ AI", "3 HDMI & 2 USB Ports")
        ),
        Product(
            id = "home_01",
            title = "Sleepyhead Original 3-Layer Orthopedic Memory Foam Mattress",
            category = "home",
            price = 8499.0,
            originalPrice = 14999.0,
            discountPercent = 43,
            rating = 4.5,
            ratingCount = 22100,
            imageUrl = "https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=500&q=80",
            isAssured = true,
            freeDelivery = true,
            brand = "Sleepyhead",
            description = "Pressure-relieving memory foam with high-density base support and breathable outer fabric.",
            highlights = listOf("Orthopedic Spinal Support", "High Density Foam", "10-Year Manufacturer Warranty", "Removable Washable Cover")
        ),
        Product(
            id = "groc_01",
            title = "Fortune Sunlite Refined Sunflower Oil (5 L Can)",
            category = "grocery",
            price = 629.0,
            originalPrice = 850.0,
            discountPercent = 26,
            rating = 4.6,
            ratingCount = 43000,
            imageUrl = "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=500&q=80",
            isAssured = true,
            freeDelivery = true,
            brand = "Fortune",
            description = "Enriched with Vitamins A and D. Light and healthy oil ideal for Indian cooking and deep frying.",
            highlights = listOf("5 Litres Pack", "Zero Cholesterol", "Fortified with Vitamins", "Pure Refined Sunflower Oil")
        ),
        Product(
            id = "beauty_01",
            title = "Maybelline New York Super Stay Matte Ink Liquid Lipstick",
            category = "beauty",
            price = 459.0,
            originalPrice = 699.0,
            discountPercent = 34,
            rating = 4.3,
            ratingCount = 37800,
            imageUrl = "https://images.unsplash.com/photo-1586495777744-4413f21062fa?w=500&q=80",
            isAssured = true,
            freeDelivery = true,
            brand = "Maybelline",
            description = "Flawless matte finish that stays up to 16 hours without fading, transferring, or smudging.",
            highlights = listOf("Up to 16HR Matte Wear", "Transfer-Proof & Waterproof", "Precision Arrow Applicator", "Vibrant Intense Pigment")
        )
    )

    fun getProductById(id: String): Product? {
        return sampleProducts.find { it.id == id }
    }

    fun getProductsByCategory(categoryId: String): List<Product> {
        return if (categoryId.lowercase() == "all") {
            sampleProducts
        } else {
            sampleProducts.filter { it.category.equals(categoryId, ignoreCase = true) }
        }
    }

    fun searchProducts(query: String): List<Product> {
        if (query.isBlank()) return sampleProducts
        return sampleProducts.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.brand.contains(query, ignoreCase = true) ||
            it.category.contains(query, ignoreCase = true) ||
            it.description.contains(query, ignoreCase = true)
        }
    }

    suspend fun addToCart(product: Product, quantity: Int = 1) {
        val existing = database.cartDao().getCartItem(product.id)
        val newQty = (existing?.quantity ?: 0) + quantity
        val entity = CartEntity(
            productId = product.id,
            title = product.title,
            category = product.category,
            price = product.price,
            originalPrice = product.originalPrice,
            discountPercent = product.discountPercent,
            imageUrl = product.imageUrl,
            quantity = newQty,
            seller = product.seller,
            isAssured = product.isAssured,
            offlineStockAvailable = product.offlineStockAvailable
        )
        database.cartDao().insertOrUpdate(entity)
        firebaseService.syncCartItemToFirebase("user_default", product.id, newQty)
    }

    suspend fun updateCartQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) {
            database.cartDao().deleteCartItem(productId)
            firebaseService.syncCartItemToFirebase("user_default", productId, 0)
        } else {
            database.cartDao().updateQuantity(productId, quantity)
            firebaseService.syncCartItemToFirebase("user_default", productId, quantity)
        }
    }

    suspend fun removeFromCart(productId: String) {
        database.cartDao().deleteCartItem(productId)
        firebaseService.syncCartItemToFirebase("user_default", productId, 0)
    }

    suspend fun toggleWishlist(product: Product, isInWishlist: Boolean) {
        if (isInWishlist) {
            database.wishlistDao().removeFromWishlist(product.id)
        } else {
            val entity = WishlistEntity(
                productId = product.id,
                title = product.title,
                price = product.price,
                originalPrice = product.originalPrice,
                discountPercent = product.discountPercent,
                imageUrl = product.imageUrl,
                rating = product.rating
            )
            database.wishlistDao().addToWishlist(entity)
        }
    }

    fun isItemInWishlist(productId: String): Flow<Boolean> {
        return database.wishlistDao().isInWishlist(productId)
    }

    suspend fun placeOrder(
        cartItems: List<CartEntity>,
        totalAmount: Double,
        deliveryAddress: DeliveryAddress,
        paymentMethod: String,
        isOfflinePickup: Boolean = false
    ): OrderEntity {
        val orderId = "BM-" + UUID.randomUUID().toString().take(8).uppercase()
        val summary = cartItems.joinToString(", ") { "${it.title} (x${it.quantity})" }
        val order = OrderEntity(
            orderId = orderId,
            timestamp = System.currentTimeMillis(),
            itemCount = cartItems.sumOf { it.quantity },
            totalAmount = totalAmount,
            status = if (isOfflinePickup) "Ready for Store Pickup" else "Confirmed & Processing",
            paymentMethod = paymentMethod,
            deliveryAddress = "${deliveryAddress.fullName}, ${deliveryAddress.houseDetails}, ${deliveryAddress.city} - ${deliveryAddress.pincode}",
            isOfflinePickup = isOfflinePickup,
            syncedWithFirebase = false
        )

        // Save locally to Room
        database.orderDao().insertOrder(order)
        database.cartDao().clearCart()

        // Sync to Firebase Firestore
        val synced = firebaseService.syncOrderToFirebase(order)
        if (synced) {
            database.orderDao().markOrderSynced(order.orderId)
        }

        return order
    }
}
