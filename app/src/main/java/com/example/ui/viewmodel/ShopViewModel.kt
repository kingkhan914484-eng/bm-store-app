package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.firebase.FirebaseService
import com.example.data.local.AddressEntity
import com.example.data.local.AppDatabase
import com.example.data.local.CartEntity
import com.example.data.local.CouponEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.OrderEntity
import com.example.data.local.ProductEntity
import com.example.data.local.WishlistEntity
import com.example.data.model.BannerItem
import com.example.data.model.CategoryItem
import com.example.data.model.Product
import com.example.data.model.UserSession
import com.example.data.repository.AdminStats
import com.example.data.repository.ShopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class ShopTab {
    HOME,
    CATEGORIES,
    NOTIFICATIONS,
    ACCOUNT,
    CART
}

enum class ScreenState {
    SPLASH,
    ONBOARDING,
    LOGIN,
    SIGNUP,
    OTP_VERIFY,
    FORGOT_PASSWORD,
    MAIN_TABS,
    SEARCH,
    PRODUCT_DETAIL,
    CHECKOUT,
    ORDER_SUCCESS,
    ORDER_DETAILS,
    ADDRESS_BOOK,
    HELP_SUPPORT,
    LEGAL_CONTENT,
    ADMIN_PANEL,
    OFFLINE_STORE_LOCATOR
}

class ShopViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val firebaseService = FirebaseService()
    private val repository = ShopRepository(database, firebaseService)

    // Navigation state
    private val _currentScreen = MutableStateFlow(ScreenState.SPLASH)
    val currentScreen: StateFlow<ScreenState> = _currentScreen.asStateFlow()

    private val _previousScreen = MutableStateFlow(ScreenState.MAIN_TABS)

    private val _currentTab = MutableStateFlow(ShopTab.HOME)
    val currentTab: StateFlow<ShopTab> = _currentTab.asStateFlow()

    // User Session
    private val _currentUser = MutableStateFlow(
        UserSession(id = "user_01", name = "Rahim Ahmed", email = "rahim@bmstore.com", phone = "+91 98765 43210", role = "customer", isLoggedIn = true)
    )
    val currentUser: StateFlow<UserSession> = _currentUser.asStateFlow()

    // Auth flows
    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _pendingOtpTarget = MutableStateFlow("")
    val pendingOtpTarget: StateFlow<String> = _pendingOtpTarget.asStateFlow()

    private val _generatedOtp = MutableStateFlow("123456")
    val generatedOtp: StateFlow<String> = _generatedOtp.asStateFlow()

    // Products & Filtering
    val categories: List<CategoryItem> = repository.categories
    val banners: List<BannerItem> = repository.banners

    val allProducts: StateFlow<List<Product>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    private val _selectedCategory = MutableStateFlow("all")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedSubcategory = MutableStateFlow("All")
    val selectedSubcategory: StateFlow<String> = _selectedSubcategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _priceFilterMax = MutableStateFlow<Double?>(null)
    val priceFilterMax: StateFlow<Double?> = _priceFilterMax.asStateFlow()

    private val _inStockOnly = MutableStateFlow(false)
    val inStockOnly: StateFlow<Boolean> = _inStockOnly.asStateFlow()

    private val _sortBy = MutableStateFlow("popular") // popular, price_low_high, price_high_low, rating
    val sortBy: StateFlow<String> = _sortBy.asStateFlow()

    // Cart, Wishlist, Orders
    val cartItems: StateFlow<List<CartEntity>> = repository.allCartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wishlist: StateFlow<List<WishlistEntity>> = repository.allWishlist
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationEntity>> = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationCount: StateFlow<Int> = repository.unreadNotificationCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val activeCoupons: StateFlow<List<CouponEntity>> = repository.activeCoupons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Coupons
    private val _appliedCoupon = MutableStateFlow<CouponEntity?>(null)
    val appliedCoupon: StateFlow<CouponEntity?> = _appliedCoupon.asStateFlow()

    private val _couponMessage = MutableStateFlow<String?>(null)
    val couponMessage: StateFlow<String?> = _couponMessage.asStateFlow()

    // Addresses
    private val _savedAddresses = MutableStateFlow<List<AddressEntity>>(emptyList())
    val savedAddresses: StateFlow<List<AddressEntity>> = _savedAddresses.asStateFlow()

    private val _selectedAddress = MutableStateFlow<AddressEntity?>(null)
    val selectedAddress: StateFlow<AddressEntity?> = _selectedAddress.asStateFlow()

    // Order Placement & Tracking
    private val _lastPlacedOrder = MutableStateFlow<OrderEntity?>(null)
    val lastPlacedOrder: StateFlow<OrderEntity?> = _lastPlacedOrder.asStateFlow()

    private val _selectedOrderForDetails = MutableStateFlow<OrderEntity?>(null)
    val selectedOrderForDetails: StateFlow<OrderEntity?> = _selectedOrderForDetails.asStateFlow()

    private val _isProcessingOrder = MutableStateFlow(false)
    val isProcessingOrder: StateFlow<Boolean> = _isProcessingOrder.asStateFlow()

    // Admin Dashboard
    private val _adminStats = MutableStateFlow(AdminStats(0, 0.0, 0, 0))
    val adminStats: StateFlow<AdminStats> = _adminStats.asStateFlow()

    // Legal / Support
    private val _legalTitle = MutableStateFlow("Terms and Conditions")
    val legalTitle: StateFlow<String> = _legalTitle.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureDatabaseSeeded()
            loadAddresses()
            loadAdminStats()
        }
    }

    private fun loadAddresses() {
        viewModelScope.launch {
            repository.getAddressesByUser(_currentUser.value.id).collect { list ->
                _savedAddresses.value = list
                if (_selectedAddress.value == null || !list.any { it.id == _selectedAddress.value?.id }) {
                    _selectedAddress.value = list.firstOrNull { it.isDefault } ?: list.firstOrNull()
                }
            }
        }
    }

    // ─────────────────────────────────────────────
    // NAVIGATION
    // ─────────────────────────────────────────────

    fun navigateTo(screen: ScreenState) {
        _previousScreen.value = _currentScreen.value
        _currentScreen.value = screen
    }

    fun navigateBack() {
        when (_currentScreen.value) {
            ScreenState.SPLASH -> {}
            ScreenState.ONBOARDING -> navigateTo(ScreenState.LOGIN)
            ScreenState.LOGIN -> navigateTo(ScreenState.MAIN_TABS)
            ScreenState.SIGNUP -> navigateTo(ScreenState.LOGIN)
            ScreenState.OTP_VERIFY -> navigateTo(ScreenState.SIGNUP)
            ScreenState.FORGOT_PASSWORD -> navigateTo(ScreenState.LOGIN)
            ScreenState.MAIN_TABS -> {
                if (_currentTab.value != ShopTab.HOME) {
                    _currentTab.value = ShopTab.HOME
                }
            }
            ScreenState.SEARCH -> navigateTo(ScreenState.MAIN_TABS)
            ScreenState.PRODUCT_DETAIL -> navigateTo(ScreenState.MAIN_TABS)
            ScreenState.CHECKOUT -> navigateTo(ScreenState.MAIN_TABS)
            ScreenState.ORDER_SUCCESS -> navigateTo(ScreenState.MAIN_TABS)
            ScreenState.ORDER_DETAILS -> navigateTo(ScreenState.MAIN_TABS)
            ScreenState.ADDRESS_BOOK -> navigateTo(ScreenState.MAIN_TABS)
            ScreenState.HELP_SUPPORT -> navigateTo(ScreenState.MAIN_TABS)
            ScreenState.LEGAL_CONTENT -> navigateTo(ScreenState.MAIN_TABS)
            ScreenState.ADMIN_PANEL -> navigateTo(ScreenState.MAIN_TABS)
            ScreenState.OFFLINE_STORE_LOCATOR -> navigateTo(ScreenState.MAIN_TABS)
        }
    }

    fun selectTab(tab: ShopTab) {
        _currentTab.value = tab
        _currentScreen.value = ScreenState.MAIN_TABS
    }

    // ─────────────────────────────────────────────
    // AUTHENTICATION
    // ─────────────────────────────────────────────

    fun login(identifier: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _authError.value = null
            val result = repository.login(identifier, pass)
            if (result.isSuccess) {
                val session = result.getOrThrow()
                _currentUser.value = session
                loadAddresses()
                if (session.role == "admin") {
                    loadAdminStats()
                    navigateTo(ScreenState.ADMIN_PANEL)
                } else {
                    navigateTo(ScreenState.MAIN_TABS)
                }
                onResult(true, null)
            } else {
                val msg = result.exceptionOrNull()?.message ?: "Login failed"
                _authError.value = msg
                onResult(false, msg)
            }
        }
    }

    fun register(name: String, email: String, phone: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _authError.value = null
            val result = repository.register(name, email, phone, pass)
            if (result.isSuccess) {
                _currentUser.value = result.getOrThrow()
                _pendingOtpTarget.value = phone.ifEmpty { email }
                _generatedOtp.value = (100000..999999).random().toString()
                navigateTo(ScreenState.OTP_VERIFY)
                onResult(true, null)
            } else {
                val msg = result.exceptionOrNull()?.message ?: "Registration failed"
                _authError.value = msg
                onResult(false, msg)
            }
        }
    }

    fun sendOtp(target: String) {
        _pendingOtpTarget.value = target
        _generatedOtp.value = (100000..999999).random().toString()
        navigateTo(ScreenState.OTP_VERIFY)
    }

    fun verifyOtp(code: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (code == _generatedOtp.value || code == "123456") {
            onSuccess()
            navigateTo(ScreenState.MAIN_TABS)
        } else {
            onError("Invalid OTP. Enter ${_generatedOtp.value} for testing.")
        }
    }

    fun resetPassword(identifier: String, newPass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val success = repository.resetPassword(identifier, newPass)
            if (success) {
                onResult(true, "Password updated successfully. Please log in.")
                navigateTo(ScreenState.LOGIN)
            } else {
                onResult(false, "No account found with this identifier.")
            }
        }
    }

    fun continueAsGuest() {
        _currentUser.value = UserSession(
            id = "guest_" + UUID.randomUUID().toString().take(6),
            name = "Guest Shopper",
            email = "guest@bmstore.com",
            phone = "",
            role = "customer",
            isLoggedIn = false
        )
        navigateTo(ScreenState.MAIN_TABS)
    }

    fun logout() {
        _currentUser.value = UserSession(
            id = "guest_user",
            name = "Guest User",
            email = "",
            phone = "",
            role = "customer",
            isLoggedIn = false
        )
        _appliedCoupon.value = null
        navigateTo(ScreenState.LOGIN)
    }

    fun updateUserProfile(name: String, phone: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.updateUserProfile(_currentUser.value.id, name, phone)
            if (success) {
                _currentUser.value = _currentUser.value.copy(name = name, phone = phone)
            }
            onComplete(success)
        }
    }

    // ─────────────────────────────────────────────
    // PRODUCT CATALOG & FILTERING
    // ─────────────────────────────────────────────

    fun selectCategory(categoryId: String) {
        _selectedCategory.value = categoryId
        _selectedSubcategory.value = "All"
    }

    fun selectSubcategory(sub: String) {
        _selectedSubcategory.value = sub
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openSearch() {
        navigateTo(ScreenState.SEARCH)
    }

    fun openProductDetail(product: Product) {
        _selectedProduct.value = product
        navigateTo(ScreenState.PRODUCT_DETAIL)
    }

    fun setPriceFilter(max: Double?) {
        _priceFilterMax.value = max
    }

    fun toggleInStockOnly(value: Boolean) {
        _inStockOnly.value = value
    }

    fun setSortBy(sort: String) {
        _sortBy.value = sort
    }

    // Derived product list for search and browsing
    fun getFilteredProducts(): List<Product> {
        val all = allProducts.value
        val cat = _selectedCategory.value
        val sub = _selectedSubcategory.value
        val query = _searchQuery.value.trim()
        val maxPrice = _priceFilterMax.value
        val stockOnly = _inStockOnly.value

        var list = all

        if (cat.lowercase() != "all") {
            list = list.filter { it.category.equals(cat, ignoreCase = true) }
        }

        if (sub != "All" && sub.isNotBlank()) {
            list = list.filter { it.subcategory.contains(sub, ignoreCase = true) || it.title.contains(sub, ignoreCase = true) }
        }

        if (query.isNotBlank()) {
            list = list.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.brand.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true)
            }
        }

        if (maxPrice != null) {
            list = list.filter { it.price <= maxPrice }
        }

        if (stockOnly) {
            list = list.filter { it.stockCount > 0 }
        }

        return when (_sortBy.value) {
            "price_low_high" -> list.sortedBy { it.price }
            "price_high_low" -> list.sortedByDescending { it.price }
            "rating" -> list.sortedByDescending { it.rating }
            else -> list // popular
        }
    }

    // ─────────────────────────────────────────────
    // CART & WISHLIST
    // ─────────────────────────────────────────────

    fun addToCart(
        product: Product,
        quantity: Int = 1,
        variant: String = "Standard",
        color: String = "Default"
    ) {
        viewModelScope.launch {
            repository.addToCart(product, quantity, variant, color)
        }
    }

    fun updateCartQuantity(cartItemId: String, quantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(cartItemId, quantity)
        }
    }

    fun removeFromCart(cartItemId: String) {
        viewModelScope.launch {
            repository.removeFromCart(cartItemId)
        }
    }

    fun toggleWishlist(product: Product) {
        viewModelScope.launch {
            val isWishlisted = wishlist.value.any { it.productId == product.id }
            repository.toggleWishlist(product, isWishlisted)
        }
    }

    // ─────────────────────────────────────────────
    // COUPON LOGIC
    // ─────────────────────────────────────────────

    fun applyCoupon(code: String) {
        viewModelScope.launch {
            val items = cartItems.value
            val subtotal = items.sumOf { it.price * it.quantity }
            val result = repository.validateCoupon(code, subtotal)
            if (result.isSuccess) {
                _appliedCoupon.value = result.getOrThrow()
                _couponMessage.value = "Coupon '${code.uppercase()}' applied successfully!"
            } else {
                _couponMessage.value = result.exceptionOrNull()?.message ?: "Invalid coupon"
            }
        }
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
        _couponMessage.value = "Coupon removed."
    }

    fun calculateDiscount(subtotal: Double): Double {
        val coupon = _appliedCoupon.value ?: return 0.0
        return if (coupon.discountPercent > 0) {
            val percentDiscount = (subtotal * coupon.discountPercent) / 100.0
            percentDiscount
        } else {
            coupon.flatDiscount
        }
    }

    // ─────────────────────────────────────────────
    // ADDRESSES
    // ─────────────────────────────────────────────

    fun selectDeliveryAddress(address: AddressEntity) {
        _selectedAddress.value = address
    }

    fun saveAddress(
        fullName: String,
        phone: String,
        pincode: String,
        house: String,
        city: String,
        state: String,
        landmark: String,
        type: String,
        isDefault: Boolean
    ) {
        viewModelScope.launch {
            val address = AddressEntity(
                id = "addr_" + UUID.randomUUID().toString().take(8),
                userId = _currentUser.value.id,
                fullName = fullName,
                phone = phone,
                pincode = pincode,
                houseDetails = house,
                city = city,
                state = state,
                landmark = landmark,
                addressType = type,
                isDefault = isDefault
            )
            repository.saveAddress(address)
            _selectedAddress.value = address
        }
    }

    fun deleteAddress(id: String) {
        viewModelScope.launch {
            repository.deleteAddress(id)
        }
    }

    fun setDefaultAddress(id: String) {
        viewModelScope.launch {
            repository.setDefaultAddress(_currentUser.value.id, id)
        }
    }

    // ─────────────────────────────────────────────
    // CHECKOUT & ORDERS
    // ─────────────────────────────────────────────

    fun openCheckout() {
        navigateTo(ScreenState.CHECKOUT)
    }

    fun openOfflineStores() {
        navigateTo(ScreenState.OFFLINE_STORE_LOCATOR)
    }

    fun openAddressBook() {
        navigateTo(ScreenState.ADDRESS_BOOK)
    }

    fun openHelpSupport() {
        navigateTo(ScreenState.HELP_SUPPORT)
    }

    fun openAdminPanel() {
        loadAdminStats()
        navigateTo(ScreenState.ADMIN_PANEL)
    }

    fun openLegal(title: String) {
        _legalTitle.value = title
        navigateTo(ScreenState.LEGAL_CONTENT)
    }

    fun openOrderDetails(order: OrderEntity) {
        _selectedOrderForDetails.value = order
        navigateTo(ScreenState.ORDER_DETAILS)
    }

    fun placeOrder(
        paymentMethod: String,
        isOfflinePickup: Boolean,
        onComplete: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            _isProcessingOrder.value = true
            val items = cartItems.value
            if (items.isEmpty()) {
                _isProcessingOrder.value = false
                onComplete(false, "Cart is empty")
                return@launch
            }

            val address = _selectedAddress.value
            if (address == null && !isOfflinePickup) {
                _isProcessingOrder.value = false
                onComplete(false, "Please select or add a delivery address")
                return@launch
            }

            val subtotal = items.sumOf { it.price * it.quantity }
            val discount = calculateDiscount(subtotal)
            val deliveryCharge = if (subtotal >= 500 || isOfflinePickup) 0.0 else 40.0
            val grandTotal = maxOf(0.0, subtotal - discount + deliveryCharge)

            val deliveryAddressStr = if (isOfflinePickup) {
                "BM STORE Central Hub & Express Counter (Instant Pickup)"
            } else {
                "${address?.fullName}, ${address?.houseDetails}, ${address?.city} - ${address?.pincode}, Ph: ${address?.phone}"
            }

            val txnId = "TXN-" + (10000000..99999999).random()
            val paymentStatus = if (paymentMethod.contains("Cash on Delivery", ignoreCase = true)) "Pending COD" else "Paid Successfully"

            val order = repository.placeOrder(
                userId = _currentUser.value.id,
                customerName = _currentUser.value.name,
                customerPhone = _currentUser.value.phone,
                cartItems = items,
                totalAmount = grandTotal,
                discountAmount = discount,
                deliveryCharge = deliveryCharge,
                deliveryAddress = deliveryAddressStr,
                paymentMethod = paymentMethod,
                paymentStatus = paymentStatus,
                transactionId = txnId,
                isOfflinePickup = isOfflinePickup
            )

            _lastPlacedOrder.value = order
            _appliedCoupon.value = null
            _isProcessingOrder.value = false
            navigateTo(ScreenState.ORDER_SUCCESS)
            onComplete(true, null)
        }
    }

    fun finishOrderSuccess() {
        selectTab(ShopTab.ACCOUNT)
    }

    fun cancelOrder(orderId: String, reason: String) {
        viewModelScope.launch {
            repository.cancelOrder(orderId, reason)
            val updated = repository.getOrderById(orderId)
            _selectedOrderForDetails.value = updated
        }
    }

    fun markNotificationAsRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    // ─────────────────────────────────────────────
    // ADMIN ACTIONS
    // ─────────────────────────────────────────────

    fun loadAdminStats() {
        viewModelScope.launch {
            _adminStats.value = repository.getAdminStats()
        }
    }

    fun adminAddProduct(product: ProductEntity, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.addProduct(product)
            loadAdminStats()
            onDone()
        }
    }

    fun adminUpdateProduct(product: ProductEntity, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.updateProduct(product)
            loadAdminStats()
            onDone()
        }
    }

    fun adminDeleteProduct(productId: String) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
            loadAdminStats()
        }
    }

    fun adminUpdateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, newStatus)
            loadAdminStats()
        }
    }

    fun adminAddCoupon(coupon: CouponEntity, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.addCoupon(coupon)
            onDone()
        }
    }

    fun adminDeleteCoupon(code: String) {
        viewModelScope.launch {
            repository.deleteCoupon(code)
        }
    }
}
