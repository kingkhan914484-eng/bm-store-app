package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Store
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DividerGray
import com.example.ui.theme.FlipkartBackground
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartDarkBlue
import com.example.ui.theme.FlipkartGreen
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class FaqData(val q: String, val a: String)

@Composable
fun HelpSupportScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedFaqIndex by remember { mutableIntStateOf(-1) }
    var userQueryText by remember { mutableStateOf("") }
    var querySubmitted by remember { mutableStateOf(false) }

    val faqs = listOf(
        FaqData(
            q = "How does BM STORE Offline Store Pickup work?",
            a = "When checking out, select 'BM STORE Offline Store Pickup'. Your order is packed and reserved immediately at BM STORE Central Hub. You can show your order ID at the express counter to pick up your package with zero waiting."
        ),
        FaqData(
            q = "What is the return and replacement policy?",
            a = "We offer a 7-day hassle-free replacement or return window on mobiles, electronics, and fashion items. You can request a return directly from the Order Details page or visit our store counter."
        ),
        FaqData(
            q = "Are products 100% genuine and original?",
            a = "Yes! Every single product at BM STORE is sourced directly from certified authorized brand distributors and comes with a 100% genuine guarantee and official manufacturer warranty."
        ),
        FaqData(
            q = "How long does home delivery take?",
            a = "Metro cities enjoy Same-Day or Next-Day express delivery. Standard pan-India deliveries take 2 to 4 business days. Real-time tracking is provided inside the app."
        ),
        FaqData(
            q = "What payment methods are supported?",
            a = "We support Google Pay, PhonePe, Paytm, all UPI IDs, Visa/Mastercard/RuPay Credit & Debit Cards, Net Banking, and Cash on Delivery (COD)."
        )
    )

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
                            text = "24x7 Customer Help Centre",
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
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Quick Contact Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Need Immediate Assistance?",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Phone, null, tint = FlipkartBlue, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Toll-Free Helpline", fontSize = 12.sp, color = TextSecondary)
                                Text("1800-267-8673 (BM-STORE)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = DividerGray)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Email, null, tint = FlipkartBlue, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Support Email", fontSize = 12.sp, color = TextSecondary)
                                Text("support@bmstore.com", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = DividerGray)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Store, null, tint = FlipkartGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Offline Store Helpdesk", fontSize = 12.sp, color = TextSecondary)
                                Text("BM STORE Central Hub & Express Counter", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }
                    }
                }

                // Send a message
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Write to Customer Support",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Describe your issue or order query. Our team responds within 2 hours.",
                            fontSize = 12.5.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = userQueryText,
                            onValueChange = {
                                userQueryText = it
                                querySubmitted = false
                            },
                            placeholder = { Text("Enter your question or order number...") },
                            modifier = Modifier.fillMaxWidth().height(100.dp).testTag("support_query_input"),
                            maxLines = 4
                        )

                        if (querySubmitted) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Thank you! Your ticket #BM-TK" + (1000..9999).random() + " has been created. A support executive will contact you shortly.",
                                color = FlipkartGreen,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (userQueryText.isNotBlank()) {
                                    querySubmitted = true
                                    userQueryText = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth().testTag("support_submit_btn")
                        ) {
                            Text("Submit Query")
                        }
                    }
                }

                // FAQs
                Text(
                    text = "Frequently Asked Questions",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(top = 4.dp)
                )

                faqs.forEachIndexed { index, faq ->
                    val isExpanded = expandedFaqIndex == index
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedFaqIndex = if (isExpanded) -1 else index }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = faq.q,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = TextSecondary
                                )
                            }

                            AnimatedVisibility(visible = isExpanded) {
                                Column {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    HorizontalDivider(color = DividerGray)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = faq.a,
                                        fontSize = 13.sp,
                                        color = TextSecondary,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
