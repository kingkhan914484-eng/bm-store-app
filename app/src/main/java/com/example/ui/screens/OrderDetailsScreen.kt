package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.OrderEntity
import com.example.ui.components.formatPrice
import com.example.ui.theme.BorderLight
import com.example.ui.theme.DividerGray
import com.example.ui.theme.FlipkartBackground
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartDarkBlue
import com.example.ui.theme.FlipkartGreen
import com.example.ui.theme.FlipkartOrange
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrderDetailsScreen(
    order: OrderEntity?,
    onCancelOrder: (orderId: String, reason: String) -> Unit,
    onRequestReturn: (orderId: String) -> Unit,
    onNeedHelp: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (order == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Order details not found")
        }
        return
    }

    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    val formattedDate = dateFormatter.format(Date(order.timestamp))

    var showCancelDialog by remember { mutableStateOf(false) }
    var cancelReason by remember { mutableStateOf("Found cheaper price elsewhere") }
    val reasons = listOf(
        "Found cheaper price elsewhere",
        "Ordered by mistake / Duplicate order",
        "Delivery time is too long",
        "Need to change delivery address or payment method",
        "Other reasons"
    )

    var showReturnNotice by remember { mutableStateOf(false) }

    Surface(
        color = FlipkartBackground,
        modifier = modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Surface(
                color = FlipkartBlue,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = SurfaceWhite
                        )
                    }
                    Column {
                        Text(
                            text = "Order Details: #${order.orderId}",
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
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Status Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Status: ${order.status}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (order.status == "Cancelled") Color(0xFFD32F2F) else FlipkartGreen
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Placed on $formattedDate",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }

                            if (order.isOfflinePickup) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(FlipkartOrange.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "STORE PICKUP",
                                        color = FlipkartOrange,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        if (order.cancelReason != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Cancellation Reason: ${order.cancelReason}",
                                fontSize = 12.5.sp,
                                color = Color(0xFFD32F2F),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = DividerGray)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Tracking Timeline
                        Text(
                            text = "Order Tracking Timeline",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        val isCancelled = order.status == "Cancelled"
                        val steps = if (order.isOfflinePickup) {
                            listOf("Order Placed", "Confirmed & Processing", "Packed at BM Store", "Ready for Pickup", "Picked Up")
                        } else {
                            listOf("Order Placed", "Confirmed & Processing", "Packed", "Shipped & Out for Delivery", "Delivered")
                        }

                        val activeIndex = when {
                            isCancelled -> 0
                            order.status.contains("Delivered", ignoreCase = true) || order.status.contains("Picked Up", ignoreCase = true) -> 4
                            order.status.contains("Shipped", ignoreCase = true) || order.status.contains("Ready", ignoreCase = true) -> 3
                            order.status.contains("Packed", ignoreCase = true) -> 2
                            order.status.contains("Confirmed", ignoreCase = true) -> 1
                            else -> 0
                        }

                        steps.forEachIndexed { index, stepName ->
                            val isCompleted = index <= activeIndex && !isCancelled
                            val isCurrent = index == activeIndex && !isCancelled

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isCompleted) FlipkartGreen else BorderLight
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isCompleted) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = SurfaceWhite,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(SurfaceWhite)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = stepName,
                                        fontSize = 13.sp,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isCompleted) TextPrimary else TextSecondary
                                    )
                                }
                            }

                            if (index < steps.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .padding(start = 11.dp)
                                        .width(2.dp)
                                        .height(18.dp)
                                        .background(if (index < activeIndex && !isCancelled) FlipkartGreen else BorderLight)
                                )
                            }
                        }
                    }
                }

                // Ordered Items Summary
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Items Ordered (${order.itemCount})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = order.itemsSummary.ifEmpty { "1x BM Store Assured Product" },
                            fontSize = 13.sp,
                            color = TextPrimary,
                            lineHeight = 18.sp
                        )
                    }
                }

                // Delivery Address & Customer
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (order.isOfflinePickup) "Pickup Location" else "Delivery Details",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Customer: ${order.customerName}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = order.deliveryAddress,
                            fontSize = 13.sp,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }

                // Payment Info & Price Summary
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Price & Payment Details",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Payment Mode", fontSize = 13.sp, color = TextSecondary)
                            Text(order.paymentMethod, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Payment Status", fontSize = 13.sp, color = TextSecondary)
                            Text(
                                order.paymentStatus,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (order.paymentStatus.contains("Paid")) FlipkartGreen else FlipkartOrange
                            )
                        }

                        if (order.paymentTransactionId.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Transaction ID", fontSize = 12.sp, color = TextSecondary)
                                Text(order.paymentTransactionId, fontSize = 12.sp, color = TextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = DividerGray)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Amount Paid", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(
                                text = formatPrice(order.totalAmount),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = FlipkartDarkBlue
                            )
                        }
                    }
                }

                // Action Buttons: Cancel, Return, Support
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (order.status != "Cancelled" && order.status != "Delivered") {
                            Button(
                                onClick = { showCancelDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth().testTag("cancel_order_btn")
                            ) {
                                Icon(Icons.Default.Cancel, null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Cancel Order")
                            }
                        }

                        if (order.status == "Delivered") {
                            Button(
                                onClick = { showReturnNotice = true },
                                colors = ButtonDefaults.buttonColors(containerColor = FlipkartOrange),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth().testTag("return_order_btn")
                            ) {
                                Text("Request Return / Exchange")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = onNeedHelp,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.HeadsetMic, null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Need Help with this Order?")
                        }
                    }
                }
            }
        }

        // Cancel Confirmation Dialog
        if (showCancelDialog) {
            AlertDialog(
                onDismissRequest = { showCancelDialog = false },
                title = { Text("Cancel Order #${order.orderId}") },
                text = {
                    Column {
                        Text("Please tell us the reason for cancellation:")
                        Spacer(modifier = Modifier.height(8.dp))
                        reasons.forEach { r ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            ) {
                                RadioButton(
                                    selected = cancelReason == r,
                                    onClick = { cancelReason = r }
                                )
                                Text(text = r, fontSize = 13.sp)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onCancelOrder(order.orderId, cancelReason)
                            showCancelDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                    ) {
                        Text("Confirm Cancellation")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCancelDialog = false }) {
                        Text("Keep Order")
                    }
                }
            )
        }

        // Return Request Alert
        if (showReturnNotice) {
            AlertDialog(
                onDismissRequest = { showReturnNotice = false },
                title = { Text("Return & Refund Request") },
                text = {
                    Text(
                        "Your return request for Order #${order.orderId} has been initiated. Our BM STORE logistics partner will inspect the product and process your refund within 2-3 business days."
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        onRequestReturn(order.orderId)
                        showReturnNotice = false
                    }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}
