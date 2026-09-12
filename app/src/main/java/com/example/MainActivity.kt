package com.example

import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import com.example.data.model.DeliveryAddress
import com.example.ui.components.FlipkartBottomNav
import com.example.ui.components.FlipkartTopBar
import com.example.ui.screens.AccountScreen
import com.example.ui.screens.AddressManagementScreen
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.ForgotPasswordScreen
import com.example.ui.screens.HelpSupportScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LegalScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.OfflineStoreScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.OrderDetailsScreen
import com.example.ui.screens.OrderSuccessScreen
import com.example.ui.screens.OtpVerificationScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SignupScreen
import com.example.ui.screens.SplashScreen
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
    val context = LocalContext.current

    val currentTab by viewModel.currentTab.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val authError by viewModel.authError.collectAsState()
    val pendingOtpTarget by viewModel.pendingOtpTarget.collectAsState()
    val generatedOtp by viewModel.generatedOtp.collectAsState()

    val allProducts by viewModel.allProducts.collectAsState()
    val selectedProduct by viewModel.selectedProduct.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val priceFilterMax by viewModel.priceFilterMax.collectAsState()
    val inStockOnly by viewModel.inStockOnly.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()

    val cartItems by viewModel.cartItems.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val wishlist by viewModel.wishlist.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val unreadNotifications by viewModel.unreadNotificationCount.collectAsState()
    val activeCoupons by viewModel.activeCoupons.collectAsState()
    val appliedCoupon by viewModel.appliedCoupon.collectAsState()
    val couponMessage by viewModel.couponMessage.collectAsState()

    val savedAddresses by viewModel.savedAddresses.collectAsState()
    val selectedAddress by viewModel.selectedAddress.collectAsState()

    val lastPlacedOrder by viewModel.lastPlacedOrder.collectAsState()
    val selectedOrderForDetails by viewModel.selectedOrderForDetails.collectAsState()
    val isProcessingOrder by viewModel.isProcessingOrder.collectAsState()

    val adminStats by viewModel.adminStats.collectAsState()
    val legalTitle by viewModel.legalTitle.collectAsState()

    val totalCartCount = cartItems.sumOf { it.quantity }

    // Handle system back button
    BackHandler(enabled = currentScreen != ScreenState.MAIN_TABS) {
        viewModel.navigateBack()
    }

    when (currentScreen) {
        ScreenState.SPLASH -> {
            SplashScreen(
                onTimeout = {
                    viewModel.navigateTo(ScreenState.ONBOARDING)
                }
            )
        }

        ScreenState.ONBOARDING -> {
            OnboardingScreen(
                onFinish = {
                    viewModel.navigateTo(ScreenState.LOGIN)
                }
            )
        }

        ScreenState.LOGIN -> {
            LoginScreen(
                onLogin = { identifier, pass ->
                    viewModel.login(identifier, pass) { success, error ->
                        if (success) {
                            Toast.makeText(context, "Welcome to BM STORE!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, error ?: "Login failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onNavigateToSignup = { viewModel.navigateTo(ScreenState.SIGNUP) },
                onNavigateToForgotPassword = { viewModel.navigateTo(ScreenState.FORGOT_PASSWORD) },
                onContinueAsGuest = { viewModel.continueAsGuest() },
                errorMessage = authError
            )
        }

        ScreenState.SIGNUP -> {
            SignupScreen(
                onSignup = { name, email, phone, pass ->
                    viewModel.register(name, email, phone, pass) { success, error ->
                        if (success) {
                            Toast.makeText(context, "Account created! Verify OTP.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, error ?: "Sign up failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onNavigateToLogin = { viewModel.navigateTo(ScreenState.LOGIN) },
                errorMessage = authError
            )
        }

        ScreenState.OTP_VERIFY -> {
            OtpVerificationScreen(
                targetIdentifier = pendingOtpTarget,
                generatedOtp = generatedOtp,
                onVerify = { code ->
                    viewModel.verifyOtp(
                        code = code,
                        onSuccess = {
                            Toast.makeText(context, "Account verified successfully!", Toast.LENGTH_SHORT).show()
                        },
                        onError = { err ->
                            Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                        }
                    )
                },
                onResend = {
                    viewModel.sendOtp(pendingOtpTarget)
                    Toast.makeText(context, "New OTP sent to $pendingOtpTarget", Toast.LENGTH_SHORT).show()
                },
                onBack = { viewModel.navigateBack() }
            )
        }

        ScreenState.FORGOT_PASSWORD -> {
            ForgotPasswordScreen(
                onReset = { identifier, newPass ->
                    viewModel.resetPassword(identifier, newPass) { success, msg ->
                        Toast.makeText(context, msg ?: "", Toast.LENGTH_SHORT).show()
                    }
                },
                onBack = { viewModel.navigateBack() }
            )
        }

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
                            products = allProducts,
                            categories = viewModel.categories,
                            selectedCategory = selectedCategory,
                            searchQuery = searchQuery,
                            wishlist = wishlist,
                            onCategorySelect = { viewModel.selectCategory(it) },
                            onProductClick = { viewModel.openProductDetail(it) },
                            onAddToCart = {
                                viewModel.addToCart(it)
                                Toast.makeText(context, "Added to cart!", Toast.LENGTH_SHORT).show()
                            },
                            onToggleWishlist = { viewModel.toggleWishlist(it) },
                            onOpenOfflineStore = { viewModel.openOfflineStores() }
                        )

                        ShopTab.CATEGORIES -> CategoriesScreen(
                            categories = viewModel.categories,
                            products = allProducts,
                            wishlist = wishlist,
                            onProductClick = { viewModel.openProductDetail(it) },
                            onAddToCart = {
                                viewModel.addToCart(it)
                                Toast.makeText(context, "Added to cart!", Toast.LENGTH_SHORT).show()
                            },
                            onToggleWishlist = { viewModel.toggleWishlist(it) }
                        )

                        ShopTab.NOTIFICATIONS -> NotificationsScreen(
                            notifications = notifications,
                            onNotificationClick = { notif ->
                                viewModel.markNotificationAsRead(notif.id)
                            },
                            onMarkAllRead = {
                                viewModel.markAllNotificationsAsRead()
                            }
                        )

                        ShopTab.ACCOUNT -> AccountScreen(
                            user = currentUser,
                            orders = orders,
                            onOrderClick = { order -> viewModel.openOrderDetails(order) },
                            onOpenOfflineStores = { viewModel.openOfflineStores() },
                            onOpenAddressBook = { viewModel.openAddressBook() },
                            onOpenHelpSupport = { viewModel.openHelpSupport() },
                            onOpenLegal = { title -> viewModel.openLegal(title) },
                            onOpenAdminPanel = { viewModel.openAdminPanel() },
                            onUpdateProfile = { name, phone ->
                                viewModel.updateUserProfile(name, phone) {
                                    Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onLoginClick = { viewModel.navigateTo(ScreenState.LOGIN) },
                            onLogoutClick = {
                                viewModel.logout()
                                Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                            }
                        )

                        ShopTab.CART -> {
                            val deliveryAddr = selectedAddress?.let {
                                DeliveryAddress(
                                    id = it.id,
                                    fullName = it.fullName,
                                    phone = it.phone,
                                    pincode = it.pincode,
                                    houseDetails = it.houseDetails,
                                    city = it.city,
                                    state = it.state,
                                    landmark = it.landmark,
                                    addressType = it.addressType,
                                    isDefault = it.isDefault
                                )
                            } ?: DeliveryAddress()

                            CartScreen(
                                cartItems = cartItems,
                                deliveryAddress = deliveryAddr,
                                onUpdateQuantity = { id, qty -> viewModel.updateCartQuantity(id, qty) },
                                onRemoveItem = { id -> viewModel.removeFromCart(id) },
                                onPlaceOrder = { viewModel.openCheckout() },
                                onContinueShopping = { viewModel.selectTab(ShopTab.HOME) }
                            )
                        }
                    }
                }
            }
        }

        ScreenState.SEARCH -> {
            SearchScreen(
                searchQuery = searchQuery,
                onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                categories = viewModel.categories,
                selectedCategory = selectedCategory,
                onCategorySelect = { viewModel.selectCategory(it) },
                priceFilterMax = priceFilterMax,
                onPriceFilterChange = { viewModel.setPriceFilter(it) },
                inStockOnly = inStockOnly,
                onToggleInStockOnly = { viewModel.toggleInStockOnly(it) },
                sortBy = sortBy,
                onSortChange = { viewModel.setSortBy(it) },
                filteredProducts = viewModel.getFilteredProducts(),
                wishlist = wishlist,
                onProductClick = { viewModel.openProductDetail(it) },
                onAddToCart = {
                    viewModel.addToCart(it)
                    Toast.makeText(context, "Added to cart!", Toast.LENGTH_SHORT).show()
                },
                onToggleWishlist = { viewModel.toggleWishlist(it) },
                onBack = { viewModel.navigateBack() }
            )
        }

        ScreenState.PRODUCT_DETAIL -> {
            selectedProduct?.let { product ->
                val isWishlisted = wishlist.any { it.productId == product.id }
                ProductDetailScreen(
                    product = product,
                    onBack = { viewModel.navigateBack() },
                    onAddToCart = {
                        viewModel.addToCart(product)
                        Toast.makeText(context, "Added to Cart!", Toast.LENGTH_SHORT).show()
                    },
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
                selectedAddress = selectedAddress,
                onOpenAddressBook = { viewModel.openAddressBook() },
                appliedCoupon = appliedCoupon,
                onApplyCoupon = { code -> viewModel.applyCoupon(code) },
                onRemoveCoupon = { viewModel.removeCoupon() },
                couponMessage = couponMessage,
                onConfirmOrder = { method, isPickup ->
                    viewModel.placeOrder(method, isPickup) { success, err ->
                        if (!success && err != null) {
                            Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                        }
                    }
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
                    viewModel.finishOrderSuccess()
                    viewModel.selectTab(ShopTab.HOME)
                }
            )
        }

        ScreenState.ORDER_DETAILS -> {
            OrderDetailsScreen(
                order = selectedOrderForDetails,
                onCancelOrder = { orderId, reason ->
                    viewModel.cancelOrder(orderId, reason)
                    Toast.makeText(context, "Order cancellation initiated", Toast.LENGTH_SHORT).show()
                },
                onRequestReturn = { orderId ->
                    Toast.makeText(context, "Return request submitted for Order #$orderId", Toast.LENGTH_LONG).show()
                },
                onNeedHelp = { viewModel.openHelpSupport() },
                onBack = { viewModel.navigateBack() }
            )
        }

        ScreenState.ADDRESS_BOOK -> {
            AddressManagementScreen(
                addresses = savedAddresses,
                selectedAddressId = selectedAddress?.id,
                onSelectAddress = { addr ->
                    viewModel.selectDeliveryAddress(addr)
                    viewModel.navigateBack()
                },
                onSaveAddress = { fullName, phone, pincode, house, city, state, landmark, type, isDefault ->
                    viewModel.saveAddress(fullName, phone, pincode, house, city, state, landmark, type, isDefault)
                    Toast.makeText(context, "Address saved successfully!", Toast.LENGTH_SHORT).show()
                },
                onDeleteAddress = { id ->
                    viewModel.deleteAddress(id)
                    Toast.makeText(context, "Address removed", Toast.LENGTH_SHORT).show()
                },
                onSetDefaultAddress = { id ->
                    viewModel.setDefaultAddress(id)
                },
                onBack = { viewModel.navigateBack() }
            )
        }

        ScreenState.HELP_SUPPORT -> {
            HelpSupportScreen(
                onBack = { viewModel.navigateBack() }
            )
        }

        ScreenState.LEGAL_CONTENT -> {
            LegalScreen(
                title = legalTitle,
                onBack = { viewModel.navigateBack() }
            )
        }

        ScreenState.ADMIN_PANEL -> {
            AdminPanelScreen(
                stats = adminStats,
                orders = orders,
                products = allProducts,
                coupons = activeCoupons,
                onUpdateOrderStatus = { orderId, newStatus ->
                    viewModel.adminUpdateOrderStatus(orderId, newStatus)
                    Toast.makeText(context, "Order status updated to $newStatus", Toast.LENGTH_SHORT).show()
                },
                onAddProduct = { prodEntity ->
                    viewModel.adminAddProduct(prodEntity) {
                        Toast.makeText(context, "Product added to catalog!", Toast.LENGTH_SHORT).show()
                    }
                },
                onDeleteProduct = { prodId ->
                    viewModel.adminDeleteProduct(prodId)
                    Toast.makeText(context, "Product removed from catalog", Toast.LENGTH_SHORT).show()
                },
                onAddCoupon = { coupon ->
                    viewModel.adminAddCoupon(coupon) {
                        Toast.makeText(context, "Coupon '${coupon.code}' created!", Toast.LENGTH_SHORT).show()
                    }
                },
                onDeleteCoupon = { code ->
                    viewModel.adminDeleteCoupon(code)
                    Toast.makeText(context, "Coupon removed", Toast.LENGTH_SHORT).show()
                },
                onSwitchToCustomerStore = {
                    viewModel.navigateTo(ScreenState.MAIN_TABS)
                },
                onBack = { viewModel.navigateBack() }
            )
        }

        ScreenState.OFFLINE_STORE_LOCATOR -> {
            OfflineStoreScreen(
                onBack = { viewModel.navigateBack() }
            )
        }
    }
}
