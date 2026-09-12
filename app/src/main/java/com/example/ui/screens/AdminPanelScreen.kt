package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CouponEntity
import com.example.data.local.OrderEntity
import com.example.data.local.ProductEntity
import com.example.data.model.Product
import com.example.data.repository.AdminStats
import com.example.ui.components.formatPrice
import com.example.ui.theme.BorderLight
import com.example.ui.theme.DividerGray
import com.example.ui.theme.FlipkartBackground
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartDarkBlue
import com.example.ui.theme.FlipkartGreen
import com.example.ui.theme.FlipkartOrange
import com.example.ui.theme.FlipkartYellow
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    stats: AdminStats,
    orders: List<OrderEntity>,
    products: List<Product>,
    coupons: List<CouponEntity>,
    onUpdateOrderStatus: (orderId: String, newStatus: String) -> Unit,
    onAddProduct: (ProductEntity) -> Unit,
    onDeleteProduct: (productId: String) -> Unit,
    onAddCoupon: (CouponEntity) -> Unit,
    onDeleteCoupon: (code: String) -> Unit,
    onSwitchToCustomerStore: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Overview", "Orders", "Products", "Coupons")
    var showAddProductSheet by remember { mutableStateOf(false) }
    var showAddCouponSheet by remember { mutableStateOf(false) }

    Surface(
        color = FlipkartBackground,
        modifier = modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Admin Header
            Surface(
                color = FlipkartDarkBlue,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SurfaceWhite)
                        }
                        Column {
                            Text(
                                text = "Admin Control Centre",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = SurfaceWhite
                            )
                            Text(
                                text = "BM STORE ONLINE OFFLINE SHOPPING",
                                fontSize = 11.sp,
                                color = SurfaceWhite.copy(alpha = 0.85f)
                            )
                        }
                    }

                    Button(
                        onClick = onSwitchToCustomerStore,
                        colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.testTag("admin_switch_store_btn")
                    ) {
                        Text("Open Store", fontSize = 12.sp)
                    }
                }
            }

            // Tab Bar
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = SurfaceWhite
            ) {
                tabs.forEachIndexed { index, tabTitle ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(tabTitle, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> AdminOverviewTab(stats, orders.take(5))
                1 -> AdminOrdersTab(orders, onUpdateOrderStatus)
                2 -> AdminProductsTab(products, onAddClick = { showAddProductSheet = true }, onDeleteClick = onDeleteProduct)
                3 -> AdminCouponsTab(coupons, onAddClick = { showAddCouponSheet = true }, onDeleteClick = onDeleteCoupon)
            }
        }

        // Add Product Bottom Sheet
        if (showAddProductSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAddProductSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                AdminAddProductForm(
                    onSave = { product ->
                        onAddProduct(product)
                        showAddProductSheet = false
                    }
                )
            }
        }

        // Add Coupon Bottom Sheet
        if (showAddCouponSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAddCouponSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                AdminAddCouponForm(
                    onSave = { coupon ->
                        onAddCoupon(coupon)
                        showAddCouponSheet = false
                    }
                )
            }
        }
    }
}

@Composable
fun AdminOverviewTab(stats: AdminStats, recentOrders: List<OrderEntity>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Business Key Metrics", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(
                title = "Total Orders",
                value = stats.totalOrders.toString(),
                icon = Icons.Default.ShoppingBag,
                color = FlipkartBlue,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Total Revenue",
                value = formatPrice(stats.totalRevenue),
                icon = Icons.Default.AttachMoney,
                color = FlipkartGreen,
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(
                title = "Catalog Items",
                value = stats.totalProducts.toString(),
                icon = Icons.Default.Inventory,
                color = FlipkartOrange,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Total Users",
                value = stats.totalCustomers.toString(),
                icon = Icons.Default.People,
                color = FlipkartDarkBlue,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Recent Customer Orders", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

        if (recentOrders.isEmpty()) {
            Card(colors = CardDefaults.cardColors(containerColor = SurfaceWhite)) {
                Text("No orders recorded yet.", modifier = Modifier.padding(16.dp), color = TextSecondary)
            }
        } else {
            recentOrders.forEach { order ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("#${order.orderId}", fontWeight = FontWeight.Bold, color = FlipkartBlue)
                            Text(formatPrice(order.totalAmount), fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Text("Customer: ${order.customerName}", fontSize = 12.5.sp, color = TextSecondary)
                        Text("Status: ${order.status}", fontSize = 12.5.sp, color = FlipkartGreen, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
            Text(title, fontSize = 12.sp, color = TextSecondary)
        }
    }
}

@Composable
fun AdminOrdersTab(
    orders: List<OrderEntity>,
    onUpdateOrderStatus: (orderId: String, newStatus: String) -> Unit
) {
    val statuses = listOf("Confirmed & Processing", "Packed at BM Store", "Shipped & Out for Delivery", "Ready for Store Pickup", "Delivered", "Cancelled")

    if (orders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No orders placed yet.")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(orders, key = { it.orderId }) { order ->
                var showStatusDialog by remember { mutableStateOf(false) }

                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Order #${order.orderId}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = FlipkartBlue)
                            Text(formatPrice(order.totalAmount), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Customer: ${order.customerName} (${order.customerPhone})", fontSize = 13.sp)
                        Text("Address: ${order.deliveryAddress}", fontSize = 12.sp, color = TextSecondary)
                        Text("Items: ${order.itemsSummary}", fontSize = 12.sp, color = TextPrimary)

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = DividerGray)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Current Status:", fontSize = 11.sp, color = TextSecondary)
                                Text(order.status, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FlipkartGreen)
                            }

                            Button(
                                onClick = { showStatusDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Update Status", fontSize = 12.sp)
                            }
                        }
                    }
                }

                if (showStatusDialog) {
                    AlertDialog(
                        onDismissRequest = { showStatusDialog = false },
                        title = { Text("Update Order #${order.orderId} Status") },
                        text = {
                            Column {
                                statuses.forEach { s ->
                                    Button(
                                        onClick = {
                                            onUpdateOrderStatus(order.orderId, s)
                                            showStatusDialog = false
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (order.status == s) FlipkartGreen else FlipkartBackground
                                        ),
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                    ) {
                                        Text(s, color = if (order.status == s) SurfaceWhite else TextPrimary)
                                    }
                                }
                            }
                        },
                        confirmButton = {},
                        dismissButton = {
                            TextButton(onClick = { showStatusDialog = false }) { Text("Cancel") }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AdminProductsTab(
    products: List<Product>,
    onAddClick: () -> Unit,
    onDeleteClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            color = SurfaceWhite,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            Button(
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(containerColor = FlipkartGreen),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add New Product to Store")
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(products, key = { it.id }) { product ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
                            Text("Category: ${product.category.uppercase()} • Stock: ${product.stockCount}", fontSize = 12.sp, color = TextSecondary)
                            Text("Price: ${formatPrice(product.price)} (MRP: ${formatPrice(product.originalPrice)})", fontSize = 13.sp, color = FlipkartBlue, fontWeight = FontWeight.SemiBold)
                        }

                        IconButton(onClick = { onDeleteClick(product.id) }) {
                            Icon(Icons.Default.Delete, "Delete", tint = Color(0xFFD32F2F))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminCouponsTab(
    coupons: List<CouponEntity>,
    onAddClick: () -> Unit,
    onDeleteClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            color = SurfaceWhite,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            Button(
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create New Promotional Coupon")
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(coupons, key = { it.code }) { coupon ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(FlipkartYellow)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(coupon.code, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = FlipkartDarkBlue)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    if (coupon.discountPercent > 0) "${coupon.discountPercent}% OFF" else "₹${coupon.flatDiscount.toInt()} FLAT OFF",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = FlipkartGreen
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(coupon.description, fontSize = 12.sp, color = TextSecondary)
                            Text("Min Order: ₹${coupon.minOrderAmount.toInt()}", fontSize = 11.5.sp, color = TextPrimary)
                        }

                        IconButton(onClick = { onDeleteClick(coupon.code) }) {
                            Icon(Icons.Default.Delete, "Delete", tint = Color(0xFFD32F2F))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminAddProductForm(
    onSave: (ProductEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("mobiles") }
    var priceStr by remember { mutableStateOf("") }
    var originalPriceStr by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var stockStr by remember { mutableStateOf("20") }
    var description by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Add New Product", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Product Title *") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = brand, onValueChange = { brand = it }, label = { Text("Brand Name *") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category (mobiles, electronics, fashion...)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(value = priceStr, onValueChange = { priceStr = it }, label = { Text("Selling Price (₹) *") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(value = originalPriceStr, onValueChange = { originalPriceStr = it }, label = { Text("MRP (₹) *") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        }

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = stockStr, onValueChange = { stockStr = it }, label = { Text("Stock Quantity") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), maxLines = 3)

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val price = priceStr.toDoubleOrNull() ?: 199.0
                val orig = originalPriceStr.toDoubleOrNull() ?: price
                val discount = if (orig > price) (((orig - price) / orig) * 100).toInt() else 0
                val entity = ProductEntity(
                    id = "prod_" + UUID.randomUUID().toString().take(8),
                    title = title.ifEmpty { "New BM Product" },
                    category = category.lowercase().trim(),
                    price = price,
                    originalPrice = orig,
                    discountPercent = discount,
                    rating = 4.5,
                    ratingCount = 1,
                    imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500&q=80",
                    brand = brand.ifEmpty { "BM Assured" },
                    description = description.ifEmpty { "Genuine product from BM STORE ONLINE OFFLINE SHOPPING." },
                    highlightsRaw = "100% Genuine | Brand Warranty | BM Assured",
                    stockCount = stockStr.toIntOrNull() ?: 20,
                    isAssured = true,
                    freeDelivery = true,
                    seller = "BM Retail Hub",
                    offlineStockAvailable = true,
                    offlineStoreLocation = "BM STORE Central Hub & Express Counter"
                )
                onSave(entity)
            },
            colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Publish Product")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun AdminAddCouponForm(
    onSave: (CouponEntity) -> Unit
) {
    var code by remember { mutableStateOf("") }
    var flatDiscountStr by remember { mutableStateOf("100") }
    var minAmountStr by remember { mutableStateOf("500") }
    var description by remember { mutableStateOf("Flat ₹100 discount on orders above ₹500") }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text("Create Promotional Coupon", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(value = code, onValueChange = { code = it.uppercase() }, label = { Text("Coupon Code (e.g. MEGA100)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = flatDiscountStr, onValueChange = { flatDiscountStr = it }, label = { Text("Flat Discount (₹)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = minAmountStr, onValueChange = { minAmountStr = it }, label = { Text("Min. Order Amount (₹)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val coupon = CouponEntity(
                    code = code.ifEmpty { "BM" + (100..999).random() },
                    discountPercent = 0,
                    flatDiscount = flatDiscountStr.toDoubleOrNull() ?: 50.0,
                    minOrderAmount = minAmountStr.toDoubleOrNull() ?: 499.0,
                    description = description,
                    isActive = true
                )
                onSave(coupon)
            },
            colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Create Coupon")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
