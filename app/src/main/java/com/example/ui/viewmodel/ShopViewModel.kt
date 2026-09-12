package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.firebase.FirebaseService
import com.example.data.local.AppDatabase
import com.example.data.local.CartEntity
import com.example.data.local.OrderEntity
import com.example.data.local.WishlistEntity
import com.example.data.model.BannerItem
import com.example.data.model.CategoryItem
import com.example.data.model.DeliveryAddress
import com.example.data.model.Product
import com.example.data.repository.ShopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ShopTab {
    HOME,
    CATEGORIES,
    NOTIFICATIONS,
    ACCOUNT,
    CART
}

enum class ScreenState {
    MAIN_TABS,
    PRODUCT_DETAIL,
    CHECKOUT,
    ORDER_SUCCESS,
    OFFLINE_STORE_LOCATOR
}

class ShopViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val firebaseService = FirebaseService()
    private val repository = ShopRepository(database, firebaseService)

    private val _currentTab = MutableStateFlow(ShopTab.HOME)
    val currentTab: StateFlow<ShopTab> = _currentTab.asStateFlow()

    private val _currentScreen = MutableStateFlow(ScreenState.MAIN_TABS)
    val currentScreen: StateFlow<ScreenState> = _currentScreen.asStateFlow()

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("all")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _deliveryAddress = MutableStateFlow(DeliveryAddress())
    val deliveryAddress: StateFlow<DeliveryAddress> = _deliveryAddress.asStateFlow()

    private val _lastPlacedOrder = MutableStateFlow<OrderEntity?>(null)
    val lastPlacedOrder: StateFlow<OrderEntity?> = _lastPlacedOrder.asStateFlow()

    private val _isProcessingOrder = MutableStateFlow(false)
    val isProcessingOrder: StateFlow<Boolean> = _isProcessingOrder.asStateFlow()

    val categories: List<CategoryItem> = repository.categories
    val banners: List<BannerItem> = repository.banners
    val allProducts: List<Product> = repository.sampleProducts

    val cartItems: StateFlow<List<CartEntity>> = repository.allCartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wishlist: StateFlow<List<WishlistEntity>> = repository.allWishlist
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(tab: ShopTab) {
        _currentTab.value = tab
        _currentScreen.value = ScreenState.MAIN_TABS
    }

    fun selectCategory(categoryId: String) {
        _selectedCategory.value = categoryId
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openProductDetail(product: Product) {
        _selectedProduct.value = product
        _currentScreen.value = ScreenState.PRODUCT_DETAIL
    }

    fun navigateBack() {
        if (_currentScreen.value != ScreenState.MAIN_TABS) {
            _currentScreen.value = ScreenState.MAIN_TABS
        }
    }

    fun openCheckout() {
        _currentScreen.value = ScreenState.CHECKOUT
    }

    fun openOfflineStores() {
        _currentScreen.value = ScreenState.OFFLINE_STORE_LOCATOR
    }

    fun addToCart(product: Product, quantity: Int = 1) {
        viewModelScope.launch {
            repository.addToCart(product, quantity)
        }
    }

    fun updateCartQuantity(productId: String, quantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(productId, quantity)
        }
    }

    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            repository.removeFromCart(productId)
        }
    }

    fun toggleWishlist(product: Product) {
        viewModelScope.launch {
            val isWishlisted = wishlist.value.any { it.productId == product.id }
            repository.toggleWishlist(product, isWishlisted)
        }
    }

    fun updateAddress(newAddress: DeliveryAddress) {
        _deliveryAddress.value = newAddress
    }

    fun placeOrder(paymentMethod: String, isOfflinePickup: Boolean) {
        viewModelScope.launch {
            _isProcessingOrder.value = true
            val items = cartItems.value
            val total = items.sumOf { it.price * it.quantity }
            val order = repository.placeOrder(
                cartItems = items,
                totalAmount = total,
                deliveryAddress = _deliveryAddress.value,
                paymentMethod = paymentMethod,
                isOfflinePickup = isOfflinePickup
            )
            _lastPlacedOrder.value = order
            _isProcessingOrder.value = false
            _currentScreen.value = ScreenState.ORDER_SUCCESS
        }
    }

    fun finishOrderSuccess() {
        _currentScreen.value = ScreenState.MAIN_TABS
        _currentTab.value = ShopTab.ACCOUNT
    }
}
