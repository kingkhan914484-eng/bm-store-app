package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FlipkartBackground
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LegalScreen(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val content = when {
        title.contains("Privacy", ignoreCase = true) -> {
            """
            1. Collection of Information
            BM STORE ONLINE OFFLINE SHOPPING collects personal details like your name, delivery address, phone number, and transaction logs solely to fulfill orders, process payments, and ensure seamless delivery.

            2. Data Security
            All sensitive information, including passwords and payment transaction tokens, are encrypted and securely stored. We never sell or rent your personal information to third-party advertisers.

            3. Offline Store Verification
            When collecting your order at BM STORE Central Hub or express pickup points, your phone number and 4-digit order pickup code are verified to protect against unauthorized handovers.

            4. Contact Us
            For privacy inquiries, reach us at privacy@bmstore.com.
            """.trimIndent()
        }
        title.contains("Return", ignoreCase = true) -> {
            """
            1. 7-Day Replacement & Return Policy
            Products eligible for returns can be returned within 7 days of delivery or in-store pickup, provided the item is in unused condition with all original packaging, tags, and accessories intact.

            2. Instant In-Store Drop-off
            Customers can drop off returned items directly at the BM STORE Central Hub express counter for immediate verification and expedited refund initiation.

            3. Refund Process
            Prepaid orders will have refunds credited back to the original source (UPI/Bank/Card) within 3 to 5 business days. Cash on Delivery orders are refunded via direct bank transfer or UPI.
            """.trimIndent()
        }
        else -> {
            """
            1. Introduction
            Welcome to BM STORE ONLINE OFFLINE SHOPPING. By accessing or using our mobile application and store services, you agree to be bound by these Terms and Conditions.

            2. Authentic Products Guarantee
            All goods sold on BM STORE are genuine and backed by authorized brand warranties. Both online delivery and offline store pickup are governed by strict quality controls.

            3. User Responsibilities
            Users agree to provide accurate delivery and contact credentials. Misuse of coupon codes, fraudulent chargebacks, or non-authentic reviews will lead to account termination.

            4. Pricing & Promotions
            Prices, discounts, and promotional offers are subject to change. Flash sales and coupon redemption limits apply as described during checkout.
            """.trimIndent()
        }
    }

    Surface(
        color = FlipkartBackground,
        modifier = modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                            text = title,
                            fontSize = 17.sp,
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
                    .padding(16.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Official Policies & Guidelines",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = content,
                            fontSize = 13.5.sp,
                            color = TextSecondary,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }
    }
}
