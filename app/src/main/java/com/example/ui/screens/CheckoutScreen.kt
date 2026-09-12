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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.data.local.AddressEntity
import com.example.data.local.CartEntity
import com.example.data.local.CouponEntity
import com.example.data.model.DeliveryAddress
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

@Composable
fun CheckoutScreen(
    cartItems: List<CartEntity>,
    selectedAddress: AddressEntity?,
    onOpenAddressBook: () -> Unit,
    appliedCoupon: CouponEntity?,
    onApplyCoupon: (String) -> Unit,
    onRemoveCoupon: () -> Unit,
    couponMessage: String?,
    onConfirmOrder: (paymentMethod: String, isOfflinePickup: Boolean) -> Unit,
    onBack: () -> Unit,
    isProcessing: Boolean,
    modifier: Modifier = Modifier
) {
    var selectedPaymentMethod by remember { mutableStateOf("UPI") }
    var isOfflinePickup by remember { mutableStateOf(false) }
    var couponInput by remember { mutableStateOf("") }

    val subtotal = cartItems.sumOf { it.price * it.quantity }
    val discount = if (appliedCoupon != null) {
        if (appliedCoupon.discountPercent > 0) {
            (subtotal * appliedCoupon.discountPercent) / 100.0
        } else {
            appliedCoupon.flatDiscount
        }
    } else 0.0

    val deliveryFee = if (subtotal >= 500 || isOfflinePickup) 0.0 else 40.0
    val totalAmount = maxOf(0.0, subtotal - discount + deliveryFee)

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
                    .padding(horizontal = 8.dp, vertical = 8.dp),
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
                                text = "Change / Add",
                                color = FlipkartBlue,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable { onOpenAddressBook() }
                                    .padding(4.dp)
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
                            text = "Contact: +91 98765 43210 • Open 9:00 AM - 10:00 PM (Zero Waiting)",
                            fontSize = 12.sp,
                            color = FlipkartGreen,
                            fontWeight = FontWeight.Medium
                        )
                    } else if (selectedAddress != null) {
                        Text(
                            text = "${selectedAddress.fullName} • ${selectedAddress.phone}",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${selectedAddress.houseDetails}, ${selectedAddress.city}, ${selectedAddress.state} - ${selectedAddress.pincode}",
                            fontSize = 12.5.sp,
                            color = TextSecondary
                        )
                    } else {
                        Text(
                            text = "No address selected. Please tap 'Change / Add' to select an address.",
                            color = Color(0xFFD32F2F),
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Coupon Code Card
            Surface(
                color = SurfaceWhite,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "APPLY COUPON CODE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (appliedCoupon != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(FlipkartGreen.copy(alpha = 0.1f))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "'${appliedCoupon.code}' Applied!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = FlipkartGreen
                                )
                                Text(
                                    text = "Saved ₹${discount.toInt()} on this order",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                            IconButton(onClick = onRemoveCoupon, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, "Remove Coupon", tint = Color(0xFFD32F2F))
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = couponInput,
                                onValueChange = { couponInput = it.uppercase() },
                                placeholder = { Text("Try BMSTORE50, WELCOME100") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("checkout_coupon_input")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (couponInput.isNotBlank()) {
                                        onApplyCoupon(couponInput)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Apply")
                            }
                        }
                    }

                    if (!couponMessage.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = couponMessage,
                            fontSize = 12.sp,
                            color = if (appliedCoupon != null) FlipkartGreen else Color(0xFFD32F2F)
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
                        subtitle = "Pay cash or UPI when order arrives",
                        icon = Icons.Default.Money,
                        isSelected = selectedPaymentMethod == "Cash on Delivery",
                        onSelect = { selectedPaymentMethod = "Cash on Delivery" }
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

            // Price Details Breakdown
            Surface(
                color = SurfaceWhite,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "PRICE DETAILS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Price (${cartItems.size} items)", fontSize = 13.5.sp, color = TextPrimary)
                        Text(formatPrice(subtotal), fontSize = 13.5.sp, color = TextPrimary)
                    }

                    if (discount > 0) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Coupon Discount", fontSize = 13.5.sp, color = FlipkartGreen)
                            Text("-${formatPrice(discount)}", fontSize = 13.5.sp, color = FlipkartGreen, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Delivery Charges", fontSize = 13.5.sp, color = TextPrimary)
                        Text(
                            text = if (deliveryFee == 0.0) "FREE" else formatPrice(deliveryFee),
                            fontSize = 13.5.sp,
                            color = if (deliveryFee == 0.0) FlipkartGreen else TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = DividerGray)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Payable Amount", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(
                            text = formatPrice(totalAmount),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = FlipkartDarkBlue
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
                        text = formatPrice(totalAmount),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Total Amount",
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
    val borderColor = if (isSelected) FlipkartBlue else BorderLight
    val bgColor = if (isSelected) FlipkartBlue.copy(alpha = 0.05f) else SurfaceWhite

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(10.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) FlipkartBlue else TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) FlipkartBlue else TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 10.5.sp,
                color = TextSecondary,
                lineHeight = 14.sp
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
            .clickable { onSelect() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(selectedColor = FlipkartBlue)
        )
        Spacer(modifier = Modifier.width(6.dp))
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
                fontSize = 13.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 11.5.sp,
                color = TextSecondary
            )
        }
    }
}
