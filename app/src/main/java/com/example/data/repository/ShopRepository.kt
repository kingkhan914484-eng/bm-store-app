package com.example.data.repository

import com.example.data.firebase.FirebaseService
import com.example.data.local.AddressEntity
import com.example.data.local.AppDatabase
import com.example.data.local.CartEntity
import com.example.data.local.CouponEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.OrderEntity
import com.example.data.local.ProductEntity
import com.example.data.local.UserEntity
import com.example.data.local.WishlistEntity
import com.example.data.model.BannerItem
import com.example.data.model.CategoryItem
import com.example.data.model.DeliveryAddress
import com.example.data.model.Product
import com.example.data.model.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class ShopRepository(
    private val database: AppDatabase,
    private val firebaseService: FirebaseService = FirebaseService()
) {
    // Reactive flows from Room
    val allProducts: Flow<List<Product>> = database.productDao().getAllProducts().map { entities ->
        entities.map { Product.fromEntity(it) }
    }

    val allCartItems: Flow<List<CartEntity>> = database.cartDao().getAllCartItems()
    val allOrders: Flow<List<OrderEntity>> = database.orderDao().getAllOrders()
    val allWishlist: Flow<List<WishlistEntity>> = database.wishlistDao().getAllWishlist()
    val activeCoupons: Flow<List<CouponEntity>> = database.couponDao().getActiveCoupons()
    val notifications: Flow<List<NotificationEntity>> = database.notificationDao().getAllNotifications()
    val unreadNotificationCount: Flow<Int> = database.notificationDao().getUnreadCount()

    // Categories
    val categories: List<CategoryItem> = listOf(
        CategoryItem("all", "All", "explore", "Top Deals", listOf("All", "Top Deals", "Trending", "New")),
        CategoryItem("mobiles", "Mobiles", "smartphone", "Up to 40% Off", listOf("All", "Smartphones", "5G Mobiles", "Budget Phones", "Accessories")),
        CategoryItem("electronics", "Electronics", "laptop", "Min. 50% Off", listOf("All", "Laptops", "Audio", "Wearables", "Smart Gadgets")),
        CategoryItem("fashion", "Fashion", "apparel", "60-80% Off", listOf("All", "Clothing", "Footwear", "Watches", "Accessories")),
        CategoryItem("appliances", "Appliances", "tv", "Best Deals", listOf("All", "Televisions", "Refrigerators", "Washing Machines", "Kitchen")),
        CategoryItem("home", "Home", "chair", "From ₹99", listOf("All", "Furniture", "Mattresses", "Home Decor", "Lighting")),
        CategoryItem("beauty", "Beauty & Toys", "face", "Extra 15% Off", listOf("All", "Makeup", "Skincare", "Toys", "Grooming")),
        CategoryItem("grocery", "Grocery", "shopping_basket", "Up to 50% Off", listOf("All", "Oils & Ghee", "Staples", "Snacks", "Beverages"))
    )

    // Banners
    val banners: List<BannerItem> = listOf(
        BannerItem(
            id = "b1",
            title = "MEGA FESTIVAL OF DISCOUNTS",
            subtitle = "50% - 80% OFF on Top Brands & Electronics",
            tag = "SALE LIVE",
            categoryTarget = "electronics"
        ),
        BannerItem(
            id = "b2",
            title = "BM STORE 5G REVOLUTION",
            subtitle = "Next-Gen Smartphones with Instant Store Pickup",
            tag = "SPECIAL OFFER",
            categoryTarget = "mobiles"
        ),
        BannerItem(
            id = "b3",
            title = "ONLINE SHOPPING + OFFLINE TRUST",
            subtitle = "Shop Online or Pick Up at BM STORE Central Hub",
            tag = "EXCLUSIVE",
            categoryTarget = "all"
        )
    )

    // Initialization check: Ensure products & initial records exist in database
    suspend fun ensureDatabaseSeeded() {
        val count = database.productDao().getProductCount()
        if (count == 0) {
            AppDatabase.seedInitialData(database)
        }
    }

    // ─────────────────────────────────────────────
    // AUTHENTICATION & USER MANAGEMENT
    // ─────────────────────────────────────────────

    suspend fun login(identifier: String, password: String):Result<UserSession> {
        val cleanIdentifier = identifier.trim()
        val user = database.userDao().getUserByEmailOrPhone(cleanIdentifier)
            ?: return Result.failure(Exception("No account found with this email or mobile number."))

        if (user.passwordHash != password) {
            return Result.failure(Exception("Incorrect password. Please try again or use Forgot Password."))
        }

        return Result.success(
            UserSession(
                id = user.id,
                name = user.name,
                email = user.email,
                phone = user.phone,
                role = user.role,
                isLoggedIn = true
            )
        )
    }

    suspend fun register(name: String, email: String, phone: String, password: String): Result<UserSession> {
        val cleanEmail = email.trim().lowercase()
        val cleanPhone = phone.trim()

        if (database.userDao().getUserByEmailOrPhone(cleanEmail) != null) {
            return Result.failure(Exception("An account with this email already exists."))
        }
        if (cleanPhone.isNotEmpty() && database.userDao().getUserByEmailOrPhone(cleanPhone) != null) {
            return Result.failure(Exception("An account with this phone number already exists."))
        }

        val newUser = UserEntity(
            id = "user_" + UUID.randomUUID().toString().take(8),
            name = name.trim(),
            email = cleanEmail,
            phone = cleanPhone,
            passwordHash = password,
            role = "customer"
        )
        database.userDao().insertUser(newUser)

        // Seed default address for new user
        val defaultAddress = AddressEntity(
            id = "addr_" + UUID.randomUUID().toString().take(8),
            userId = newUser.id,
            fullName = newUser.name,
            phone = newUser.phone.ifEmpty { "+91 98765 43210" },
            pincode = "700001",
            houseDetails = "House No. 12, Main Street",
            city = "Kolkata",
            state = "West Bengal",
            landmark = "City Center",
            addressType = "Home",
            isDefault = true
        )
        database.addressDao().insertAddress(defaultAddress)

        return Result.success(
            UserSession(
                id = newUser.id,
                name = newUser.name,
                email = newUser.email,
                phone = newUser.phone,
                role = newUser.role,
                isLoggedIn = true
            )
        )
    }

    suspend fun resetPassword(identifier: String, newPassword: String): Boolean {
        val user = database.userDao().getUserByEmailOrPhone(identifier.trim()) ?: return false
        val updatedUser = user.copy(passwordHash = newPassword)
        database.userDao().updateUser(updatedUser)
        return true
    }

    suspend fun updateUserProfile(userId: String, name: String, phone: String): Boolean {
        val user = database.userDao().getUserById(userId) ?: return false
        val updated = user.copy(name = name, phone = phone)
        database.userDao().updateUser(updated)
        return true
    }

    // ─────────────────────────────────────────────
    // PRODUCT CATALOG & SEARCH
    // ─────────────────────────────────────────────

    suspend fun getProductById(id: String): Product? {
        val entity = database.productDao().getProductById(id) ?: return null
        return Product.fromEntity(entity)
    }

    fun getProductsByCategory(category: String): Flow<List<Product>> {
        return if (category.equals("all", ignoreCase = true)) {
            allProducts
        } else {
            database.productDao().getProductsByCategory(category.lowercase()).map { list ->
                list.map { Product.fromEntity(it) }
            }
        }
    }

    fun searchProducts(query: String): Flow<List<Product>> {
        return database.productDao().searchProducts(query.trim()).map { list ->
            list.map { Product.fromEntity(it) }
        }
    }

    // ─────────────────────────────────────────────
    // CART & WISHLIST
    // ─────────────────────────────────────────────

    suspend fun addToCart(
        product: Product,
        quantity: Int = 1,
        selectedVariant: String = "Standard",
        selectedColor: String = "Default"
    ) {
        val cartItemId = "${product.id}_${selectedVariant}_$selectedColor"
        val existing = database.cartDao().getCartItem(cartItemId)
        val newQty = (existing?.quantity ?: 0) + quantity

        // Check stock availability
        val cappedQty = if (product.stockCount > 0) minOf(newQty, product.stockCount) else 1

        val entity = CartEntity(
            id = cartItemId,
            productId = product.id,
            title = product.title,
            category = product.category,
            price = product.price,
            originalPrice = product.originalPrice,
            discountPercent = product.discountPercent,
            imageUrl = product.imageUrl,
            quantity = cappedQty,
            selectedVariant = selectedVariant,
            selectedColor = selectedColor,
            seller = product.seller,
            isAssured = product.isAssured,
            offlineStockAvailable = product.offlineStockAvailable
        )
        database.cartDao().insertOrUpdate(entity)
        firebaseService.syncCartItemToFirebase("user_default", product.id, cappedQty)
    }

    suspend fun updateCartQuantity(cartItemId: String, quantity: Int) {
        if (quantity <= 0) {
            database.cartDao().deleteCartItem(cartItemId)
        } else {
            database.cartDao().updateQuantity(cartItemId, quantity)
        }
    }

    suspend fun removeFromCart(cartItemId: String) {
        database.cartDao().deleteCartItem(cartItemId)
    }

    suspend fun clearCart() {
        database.cartDao().clearCart()
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

    // ─────────────────────────────────────────────
    // ADDRESS MANAGEMENT
    // ─────────────────────────────────────────────

    fun getAddressesByUser(userId: String): Flow<List<AddressEntity>> {
        return database.addressDao().getAddressesByUser(userId)
    }

    suspend fun saveAddress(address: AddressEntity) {
        if (address.isDefault) {
            database.addressDao().clearDefault(address.userId)
        }
        database.addressDao().insertAddress(address)
    }

    suspend fun deleteAddress(addressId: String) {
        database.addressDao().deleteAddress(addressId)
    }

    suspend fun setDefaultAddress(userId: String, addressId: String) {
        database.addressDao().clearDefault(userId)
        database.addressDao().setDefault(addressId)
    }

    // ─────────────────────────────────────────────
    // COUPON & DISCOUNT ENGINE
    // ─────────────────────────────────────────────

    suspend fun validateCoupon(code: String, subtotal: Double): Result<CouponEntity> {
        val coupon = database.couponDao().getCouponByCode(code.trim().uppercase())
            ?: return Result.failure(Exception("Invalid or expired coupon code"))

        if (subtotal < coupon.minOrderAmount) {
            return Result.failure(Exception("Minimum order value for ${coupon.code} is ₹${coupon.minOrderAmount.toInt()}"))
        }

        return Result.success(coupon)
    }

    // ─────────────────────────────────────────────
    // ORDER MANAGEMENT & TRACKING
    // ─────────────────────────────────────────────

    suspend fun placeOrder(
        userId: String,
        customerName: String,
        customerPhone: String,
        cartItems: List<CartEntity>,
        totalAmount: Double,
        discountAmount: Double,
        deliveryCharge: Double,
        deliveryAddress: String,
        paymentMethod: String,
        paymentStatus: String,
        transactionId: String,
        isOfflinePickup: Boolean = false
    ): OrderEntity {
        val orderId = "BM-" + (1000000..9999999).random()
        val itemsSummary = cartItems.joinToString(", ") { "${it.title} [${it.selectedVariant}] x${it.quantity}" }

        val order = OrderEntity(
            orderId = orderId,
            userId = userId,
            customerName = customerName,
            customerPhone = customerPhone,
            timestamp = System.currentTimeMillis(),
            itemCount = cartItems.sumOf { it.quantity },
            totalAmount = totalAmount,
            discountAmount = discountAmount,
            deliveryCharge = deliveryCharge,
            status = if (isOfflinePickup) "Ready for Store Pickup" else "Confirmed & Processing",
            paymentMethod = paymentMethod,
            paymentStatus = paymentStatus,
            paymentTransactionId = transactionId,
            deliveryAddress = deliveryAddress,
            isOfflinePickup = isOfflinePickup,
            itemsSummary = itemsSummary,
            syncedWithFirebase = false
        )

        // Save order to Room
        database.orderDao().insertOrder(order)

        // Decrement product stocks
        for (item in cartItems) {
            val product = database.productDao().getProductById(item.productId)
            if (product != null) {
                val updatedStock = maxOf(0, product.stockCount - item.quantity)
                database.productDao().updateStock(product.id, updatedStock)
            }
        }

        // Clear cart
        database.cartDao().clearCart()

        // Create notification
        val notification = NotificationEntity(
            id = "notif_" + UUID.randomUUID().toString().take(8),
            title = "Order Placed Successfully!",
            message = "Your order #$orderId has been placed. Total: ₹${totalAmount.toInt()}.",
            timestamp = System.currentTimeMillis(),
            type = "order",
            isRead = false,
            orderId = orderId
        )
        database.notificationDao().insertNotification(notification)

        // Sync to Firebase
        val synced = firebaseService.syncOrderToFirebase(order)
        if (synced) {
            database.orderDao().markOrderSynced(order.orderId)
        }

        return order
    }

    suspend fun cancelOrder(orderId: String, reason: String) {
        database.orderDao().cancelOrder(orderId, reason)
        // Add notification
        val notif = NotificationEntity(
            id = "notif_" + UUID.randomUUID().toString().take(8),
            title = "Order #$orderId Cancelled",
            message = "Your order has been cancelled. Reason: $reason.",
            timestamp = System.currentTimeMillis(),
            type = "order",
            isRead = false,
            orderId = orderId
        )
        database.notificationDao().insertNotification(notif)
    }

    suspend fun getOrderById(orderId: String): OrderEntity? {
        return database.orderDao().getOrderById(orderId)
    }

    // ─────────────────────────────────────────────
    // NOTIFICATIONS
    // ─────────────────────────────────────────────

    suspend fun markNotificationAsRead(id: String) {
        database.notificationDao().markAsRead(id)
    }

    suspend fun markAllNotificationsAsRead() {
        database.notificationDao().markAllAsRead()
    }

    // ─────────────────────────────────────────────
    // ADMIN PANEL MANAGEMENT
    // ─────────────────────────────────────────────

    suspend fun getAdminStats(): AdminStats {
        val totalOrders = database.orderDao().getOrderCount()
        val totalRevenue = database.orderDao().getTotalRevenue() ?: 0.0
        val totalProducts = database.productDao().getProductCount()
        val totalCustomers = database.userDao().getUserCount()
        return AdminStats(
            totalOrders = totalOrders,
            totalRevenue = totalRevenue,
            totalProducts = totalProducts,
            totalCustomers = totalCustomers
        )
    }

    suspend fun addProduct(product: ProductEntity) {
        database.productDao().insertProduct(product)
    }

    suspend fun updateProduct(product: ProductEntity) {
        database.productDao().updateProduct(product)
    }

    suspend fun deleteProduct(productId: String) {
        database.productDao().deleteProductById(productId)
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String) {
        database.orderDao().updateOrderStatus(orderId, newStatus)
        // Notify customer
        val notif = NotificationEntity(
            id = "notif_" + UUID.randomUUID().toString().take(8),
            title = "Order Update: $newStatus",
            message = "Your order #$orderId status has been updated to: $newStatus.",
            timestamp = System.currentTimeMillis(),
            type = "order",
            isRead = false,
            orderId = orderId
        )
        database.notificationDao().insertNotification(notif)
    }

    suspend fun addCoupon(coupon: CouponEntity) {
        database.couponDao().insertCoupon(coupon)
    }

    suspend fun deleteCoupon(code: String) {
        database.couponDao().deleteCoupon(code)
    }
}

data class AdminStats(
    val totalOrders: Int,
    val totalRevenue: Double,
    val totalProducts: Int,
    val totalCustomers: Int
)
