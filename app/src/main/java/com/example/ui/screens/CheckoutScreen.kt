package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.data.local.CartEntity
import com.example.data.model.DeliveryAddress
import com.example.ui.components.formatPrice
import com.example.ui.theme.DividerGray
import com.example.ui.theme.FlipkartBackground
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartGreen
import com.example.ui.theme.FlipkartOrange
import com.example.ui.theme.FlipkartYellow
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CheckoutScreen(
    cartItems: List<CartEntity>,
    deliveryAddress: DeliveryAddress,
    onAddressChange: (DeliveryAddress) -> Unit,
    onConfirmOrder: (paymentMethod: String, isOfflinePickup: Boolean) -> Unit,
    onBack: () -> Unit,
    isProcessing: Boolean,
    modifier: Modifier = Modifier
) {
    var selectedPaymentMethod by remember { mutableStateOf("UPI") }
    var isOfflinePickup by remember { mutableStateOf(false) }
    var isEditingAddress by remember { mutableStateOf(false) }

    var fullName by remember { mutableStateOf(deliveryAddress.fullName) }
    var phone by remember { mutableStateOf(deliveryAddress.phone) }
    var pincode by remember { mutableStateOf(deliveryAddress.pincode) }
    var houseDetails by remember { mutableStateOf(deliveryAddress.houseDetails) }
    var city by remember { mutableStateOf(deliveryAddress.city) }

    val totalAmount = cartItems.sumOf { it.price * it.quantity }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FlipkartBackground)
            .statusBarsPadding()
    ) {
        // Top bar
        Surface(color = FlipkartBlue, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("checkout_back_btn")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "Order Summary & Payment",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "BM STORE ONLINE OFFLINE SHOPPING",
                        color = FlipkartYellow,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            // Delivery Mode Selector (Online Delivery vs Offline Store Pickup)
            Surface(
                color = SurfaceWhite,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "CHOOSE FULFILLMENT MODE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        DeliveryModeCard(
                            title = "Online Home Delivery",
                            subtitle = "Delivered to your doorstep",
                            icon = Icons.Default.LocalShipping,
                            isSelected = !isOfflinePickup,
                            onClick = { isOfflinePickup = false },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        DeliveryModeCard(
                            title = "BM Offline Store",
                            subtitle = "Instant counter pickup",
                            icon = Icons.Default.Store,
                            isSelected = isOfflinePickup,
                            onClick = { isOfflinePickup = true },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Address Details
            Surface(
                color = SurfaceWhite,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isOfflinePickup) "PICKUP STORE LOCATION" else "DELIVERY ADDRESS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        if (!isOfflinePickup) {
                            Text(
                                text = if (isEditingAddress) "Save" else "Edit",
                                color = FlipkartBlue,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable {
                                    if (isEditingAddress) {
                                        onAddressChange(
                                            deliveryAddress.copy(
                                                fullName = fullName,
                                                phone = phone,
                                                pincode = pincode,
                                                houseDetails = houseDetails,
                                                city = city
                                            )
                                        )
                                    }
                                    isEditingAddress = !isEditingAddress
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (isOfflinePickup) {
                        Text(
                            text = "BM STORE Central Hub & Express Counter",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Plot 108, Commercial Avenue, Kolkata - 700001",
                            fontSize = 12.5.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "Contact: +91 98765 43210 • Open 9:00 AM - 10:00 PM",
                            fontSize = 12.sp,
                            color = FlipkartGreen,
                            fontWeight = FontWeight.Medium
                        )
                    } else if (isEditingAddress) {
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone Number") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                        OutlinedTextField(
                            value = houseDetails,
                            onValueChange = { houseDetails = it },
                            label = { Text("House No. / Building / Street") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = city,
                                onValueChange = { city = it },
                                label = { Text("City") },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 4.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = pincode,
                                onValueChange = { pincode = it },
                                label = { Text("PIN Code") },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 4.dp)
                            )
                        }
                    } else {
                        Text(
                            text = "${deliveryAddress.fullName} • ${deliveryAddress.phone}",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${deliveryAddress.houseDetails}, ${deliveryAddress.city} - ${deliveryAddress.pincode}",
                            fontSize = 12.5.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Payment Options
            Surface(
                color = SurfaceWhite,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "PAYMENT OPTIONS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    PaymentOptionRow(
                        title = "UPI (Google Pay, PhonePe, Paytm)",
                        subtitle = "Instant payment via any UPI App",
                        icon = Icons.Default.Payments,
                        isSelected = selectedPaymentMethod == "UPI",
                        onSelect = { selectedPaymentMethod = "UPI" }
                    )

                    PaymentOptionRow(
                        title = "Credit / Debit / ATM Card",
                        subtitle = "Visa, MasterCard, RuPay, Maestro",
                        icon = Icons.Default.CreditCard,
                        isSelected = selectedPaymentMethod == "Card",
                        onSelect = { selectedPaymentMethod = "Card" }
                    )

                    PaymentOptionRow(
                        title = "Net Banking",
                        subtitle = "All major Indian banks supported",
                        icon = Icons.Default.AccountBalance,
                        isSelected = selectedPaymentMethod == "NetBanking",
                        onSelect = { selectedPaymentMethod = "NetBanking" }
                    )

                    PaymentOptionRow(
                        title = "Cash on Delivery",
                        subtitle = "Pay cash or UPI at delivery",
                        icon = Icons.Default.Money,
                        isSelected = selectedPaymentMethod == "COD",
                        onSelect = { selectedPaymentMethod = "COD" }
                    )

                    if (isOfflinePickup) {
                        PaymentOptionRow(
                            title = "Pay at BM STORE Counter",
                            subtitle = "Pay by Cash, Card, or UPI at pickup",
                            icon = Icons.Default.Store,
                            isSelected = selectedPaymentMethod == "StoreCounter",
                            onSelect = { selectedPaymentMethod = "StoreCounter" }
                        )
                    }
                }
            }

            // Firebase & Security info badge
            Surface(
                color = SurfaceWhite,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = null,
                        tint = FlipkartGreen,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Connected to BM Cloud & Firebase",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Orders and status sync seamlessly with Firebase Firestore",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(70.dp))
        }

        // Sticky Bottom Bar
        Surface(
            color = SurfaceWhite,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "₹${formatPrice(totalAmount)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Total Payable",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Button(
                    onClick = { onConfirmOrder(selectedPaymentMethod, isOfflinePickup) },
                    enabled = !isProcessing,
                    colors = ButtonDefaults.buttonColors(containerColor = FlipkartOrange),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("confirm_order_btn")
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("PROCESSING...", color = Color.White, fontWeight = FontWeight.Bold)
                    } else {
                        Text(
                            text = "CONFIRM & PAY",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeliveryModeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) FlipkartBlue else DividerGray,
                shape = RoundedCornerShape(6.dp)
            )
            .background(
                if (isSelected) Color(0xFFF0F5FF) else SurfaceWhite,
                shape = RoundedCornerShape(6.dp)
            )
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        Column {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) FlipkartBlue else TextSecondary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) FlipkartBlue else TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun PaymentOptionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(selectedColor = FlipkartBlue)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) FlipkartBlue else TextSecondary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextSecondary
            )
        }
    }
}
