package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.OrderEntity
import com.example.data.model.UserSession
import com.example.ui.components.formatPrice
import com.example.ui.theme.DividerGray
import com.example.ui.theme.FlipkartBackground
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartDarkBlue
import com.example.ui.theme.FlipkartGold
import com.example.ui.theme.FlipkartGreen
import com.example.ui.theme.FlipkartOrange
import com.example.ui.theme.FlipkartYellow
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AccountScreen(
    user: UserSession,
    orders: List<OrderEntity>,
    onOrderClick: (OrderEntity) -> Unit,
    onOpenOfflineStores: () -> Unit,
    onOpenAddressBook: () -> Unit,
    onOpenHelpSupport: () -> Unit,
    onOpenLegal: (String) -> Unit,
    onOpenAdminPanel: () -> Unit,
    onUpdateProfile: (name: String, phone: String) -> Unit,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FlipkartBackground)
            .testTag("account_screen_list")
    ) {
        // User Profile Header Card
        item {
            Surface(
                color = FlipkartBlue,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(52.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = user.name,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(FlipkartYellow)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (user.role == "admin") "ADMIN" else "PLUS",
                                        color = FlipkartDarkBlue,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${user.phone.ifEmpty { "Mobile not set" }} • ${user.email.ifEmpty { "Guest" }}",
                                fontSize = 11.5.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Text(
                                text = "BM STORE ONLINE OFFLINE SHOPPING",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = FlipkartYellow
                            )
                        }
                    }

                    if (user.isLoggedIn) {
                        IconButton(onClick = { showEditProfileDialog = true }) {
                            Icon(Icons.Default.Edit, "Edit Profile", tint = SurfaceWhite)
                        }
                    }
                }
            }
        }

        // Admin Access Banner (if user is admin or quick link)
        if (user.role == "admin") {
            item {
                Surface(
                    color = FlipkartDarkBlue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenAdminPanel() }
                        .padding(bottom = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AdminPanelSettings, null, tint = FlipkartYellow, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Admin Control Centre", color = SurfaceWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Manage orders, products, coupons & view stats", color = FlipkartYellow, fontSize = 11.sp)
                            }
                        }
                        Icon(Icons.Default.ChevronRight, null, tint = SurfaceWhite)
                    }
                }
            }
        }

        // SuperCoins Banner
        item {
            Surface(
                color = SurfaceWhite,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = FlipkartGold,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "450 BM SuperCoins Balance",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Redeem SuperCoins for extra discounts online & in-store",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                }
            }
        }

        // My Orders Section Header
        item {
            Surface(
                color = SurfaceWhite,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "MY ORDERS (${orders.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // Orders List
        if (orders.isEmpty()) {
            item {
                Surface(
                    color = SurfaceWhite,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.ShoppingBag,
                                contentDescription = null,
                                tint = DividerGray,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No orders placed yet",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Browse our catalog and place your first order!",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        } else {
            items(orders, key = { it.orderId }) { order ->
                OrderItemCard(
                    order = order,
                    onClick = { onOrderClick(order) }
                )
            }
        }

        // Quick Service & Navigation Links
        item {
            Surface(
                color = SurfaceWhite,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Column {
                    AccountOptionRow(
                        icon = Icons.Default.LocationOn,
                        title = "Saved Delivery Addresses",
                        subtitle = "Manage home, work & pickup addresses",
                        onClick = onOpenAddressBook
                    )
                    HorizontalDivider(color = DividerGray, modifier = Modifier.padding(horizontal = 14.dp))

                    AccountOptionRow(
                        icon = Icons.Default.Store,
                        title = "BM STORE Offline Hub & Pickup Counters",
                        subtitle = "View physical store address, timing & directions",
                        onClick = onOpenOfflineStores
                    )
                    HorizontalDivider(color = DividerGray, modifier = Modifier.padding(horizontal = 14.dp))

                    AccountOptionRow(
                        icon = Icons.Default.Headphones,
                        title = "24x7 Customer Help Centre",
                        subtitle = "FAQs, order returns, helpline & queries",
                        onClick = onOpenHelpSupport
                    )
                    HorizontalDivider(color = DividerGray, modifier = Modifier.padding(horizontal = 14.dp))

                    AccountOptionRow(
                        icon = Icons.Default.Policy,
                        title = "Terms & Conditions",
                        subtitle = "Official BM STORE service agreement",
                        onClick = { onOpenLegal("Terms and Conditions") }
                    )
                    HorizontalDivider(color = DividerGray, modifier = Modifier.padding(horizontal = 14.dp))

                    AccountOptionRow(
                        icon = Icons.Default.Lock,
                        title = "Privacy & Return Policy",
                        subtitle = "Data security & 7-day hassle free returns",
                        onClick = { onOpenLegal("Privacy Policy") }
                    )

                    if (user.role != "admin") {
                        HorizontalDivider(color = DividerGray, modifier = Modifier.padding(horizontal = 14.dp))
                        AccountOptionRow(
                            icon = Icons.Default.AdminPanelSettings,
                            title = "Store Manager Admin Login",
                            subtitle = "Authorized BM Store staff portal",
                            onClick = onOpenAdminPanel
                        )
                    }
                }
            }
        }

        // Account Auth Actions
        item {
            Surface(
                color = SurfaceWhite,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    if (user.isLoggedIn) {
                        Button(
                            onClick = { showLogoutDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = FlipkartBackground),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("account_logout_btn")
                        ) {
                            Icon(Icons.Default.ExitToApp, null, tint = Color(0xFFD32F2F))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Log Out of Account", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = onLoginClick,
                            colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("account_login_btn")
                        ) {
                            Icon(Icons.Default.Login, null, tint = SurfaceWhite)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sign In / Create Account", color = SurfaceWhite, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        var newName by remember { mutableStateOf(user.name) }
        var newPhone by remember { mutableStateOf(user.phone) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Profile Details") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newPhone,
                        onValueChange = { newPhone = it },
                        label = { Text("Mobile Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateProfile(newName, newPhone)
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue)
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Sign Out") },
            text = { Text("Are you sure you want to sign out from BM STORE ONLINE OFFLINE SHOPPING?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogoutClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Sign Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Stay Logged In") }
            }
        )
    }
}

@Composable
private fun OrderItemCard(
    order: OrderEntity,
    onClick: () -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    val formattedDate = dateFormatter.format(Date(order.timestamp))

    Surface(
        color = SurfaceWhite,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(bottom = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (order.status == "Cancelled") Color(0xFFD32F2F) else FlipkartGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = order.status,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (order.status == "Cancelled") Color(0xFFD32F2F) else FlipkartGreen
                    )
                }

                if (order.isOfflinePickup) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(FlipkartOrange.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "STORE PICKUP",
                            color = FlipkartOrange,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = order.itemsSummary.ifEmpty { "BM STORE Order #${order.orderId}" },
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total: ${formatPrice(order.totalAmount)} (${order.paymentMethod})",
                    fontSize = 12.5.sp,
                    color = TextSecondary
                )
                Text(
                    text = "Track Order >",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = FlipkartBlue
                )
            }
        }
    }
}

@Composable
private fun AccountOptionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = FlipkartBlue,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 11.5.sp,
                    color = TextSecondary
                )
            }
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}
