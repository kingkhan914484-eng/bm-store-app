package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.components.FlipkartBottomNav
import com.example.ui.components.FlipkartTopBar
import com.example.ui.screens.AccountScreen
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.OfflineStoreScreen
import com.example.ui.screens.OrderSuccessScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.theme.FlipkartBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ScreenState
import com.example.ui.viewmodel.ShopTab
import com.example.ui.viewmodel.ShopViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ShopViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: ShopViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val selectedProduct by viewModel.selectedProduct.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val wishlist by viewModel.wishlist.collectAsState()
    val deliveryAddress by viewModel.deliveryAddress.collectAsState()
    val lastPlacedOrder by viewModel.lastPlacedOrder.collectAsState()
    val isProcessingOrder by viewModel.isProcessingOrder.collectAsState()

    val totalCartCount = cartItems.sumOf { it.quantity }

    // Handle system back navigation
    BackHandler(enabled = currentScreen != ScreenState.MAIN_TABS) {
        viewModel.navigateBack()
    }

    when (currentScreen) {
        ScreenState.MAIN_TABS -> {
            Scaffold(
                topBar = {
                    FlipkartTopBar(
                        searchQuery = searchQuery,
                        onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                        cartItemCount = totalCartCount,
                        wishlistCount = wishlist.size,
                        onCartClick = { viewModel.selectTab(ShopTab.CART) },
                        onWishlistClick = { viewModel.selectTab(ShopTab.ACCOUNT) },
                        onNotificationClick = { viewModel.selectTab(ShopTab.NOTIFICATIONS) },
                        onOfflineStoreClick = { viewModel.openOfflineStores() }
                    )
                },
                bottomBar = {
                    FlipkartBottomNav(
                        selectedTab = currentTab,
                        onTabSelected = { viewModel.selectTab(it) },
                        cartItemCount = totalCartCount
                    )
                },
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(FlipkartBackground)
                        .padding(innerPadding)
                ) {
                    when (currentTab) {
                        ShopTab.HOME -> HomeScreen(
                            products = viewModel.allProducts,
                            categories = viewModel.categories,
                            selectedCategory = selectedCategory,
                            searchQuery = searchQuery,
                            wishlist = wishlist,
                            onCategorySelect = { viewModel.selectCategory(it) },
                            onProductClick = { viewModel.openProductDetail(it) },
                            onAddToCart = { viewModel.addToCart(it) },
                            onToggleWishlist = { viewModel.toggleWishlist(it) },
                            onOpenOfflineStore = { viewModel.openOfflineStores() }
                        )

                        ShopTab.CATEGORIES -> CategoriesScreen(
                            categories = viewModel.categories,
                            products = viewModel.allProducts,
                            wishlist = wishlist,
                            onProductClick = { viewModel.openProductDetail(it) },
                            onAddToCart = { viewModel.addToCart(it) },
                            onToggleWishlist = { viewModel.toggleWishlist(it) }
                        )

                        ShopTab.NOTIFICATIONS -> NotificationsScreen()

                        ShopTab.ACCOUNT -> AccountScreen(
                            orders = orders,
                            onOpenOfflineStores = { viewModel.openOfflineStores() }
                        )

                        ShopTab.CART -> CartScreen(
                            cartItems = cartItems,
                            deliveryAddress = deliveryAddress,
                            onUpdateQuantity = { id, qty -> viewModel.updateCartQuantity(id, qty) },
                            onRemoveItem = { id -> viewModel.removeFromCart(id) },
                            onPlaceOrder = { viewModel.openCheckout() },
                            onContinueShopping = { viewModel.selectTab(ShopTab.HOME) }
                        )
                    }
                }
            }
        }

        ScreenState.PRODUCT_DETAIL -> {
            selectedProduct?.let { product ->
                val isWishlisted = wishlist.any { it.productId == product.id }
                ProductDetailScreen(
                    product = product,
                    onBack = { viewModel.navigateBack() },
                    onAddToCart = { viewModel.addToCart(product) },
                    onBuyNow = {
                        viewModel.addToCart(product)
                        viewModel.openCheckout()
                    },
                    isWishlisted = isWishlisted,
                    onToggleWishlist = { viewModel.toggleWishlist(product) }
                )
            }
        }

        ScreenState.CHECKOUT -> {
            CheckoutScreen(
                cartItems = cartItems,
                deliveryAddress = deliveryAddress,
                onAddressChange = { viewModel.updateAddress(it) },
                onConfirmOrder = { method, isPickup ->
                    viewModel.placeOrder(method, isPickup)
                },
                onBack = { viewModel.navigateBack() },
                isProcessing = isProcessingOrder
            )
        }

        ScreenState.ORDER_SUCCESS -> {
            OrderSuccessScreen(
                order = lastPlacedOrder,
                onViewOrders = { viewModel.finishOrderSuccess() },
                onContinueShopping = {
                    viewModel.navigateBack()
                    viewModel.selectTab(ShopTab.HOME)
                }
            )
        }

        ScreenState.OFFLINE_STORE_LOCATOR -> {
            OfflineStoreScreen(
                onBack = { viewModel.navigateBack() }
            )
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    androidx.compose.material3.Text(text = "Hello $name!", modifier = modifier)
}

