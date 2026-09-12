package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderLight
import com.example.ui.theme.FlipkartBackground
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartDarkBlue
import com.example.ui.theme.FlipkartGreen
import com.example.ui.theme.FlipkartYellow
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class OnboardingStep(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconBgColor: Color
)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableIntStateOf(0) }

    val steps = listOf(
        OnboardingStep(
            title = "Online Convenience &\nOffline Store Pickup",
            subtitle = "Enjoy lightning fast home delivery or pick up directly from BM STORE Central Hub without waiting.",
            icon = Icons.Default.Store,
            iconBgColor = FlipkartBlue
        ),
        OnboardingStep(
            title = "100% Genuine Products &\nDirect Brand Warranty",
            subtitle = "Every mobile, electronics, and fashion item is verified with original brand warranty and hassle-free returns.",
            icon = Icons.Default.Security,
            iconBgColor = FlipkartGreen
        ),
        OnboardingStep(
            title = "Instant UPI, Cards &\nCash on Delivery",
            subtitle = "Pay securely via Google Pay, PhonePe, Paytm, Credit/Debit cards, or pay cash when your order arrives.",
            icon = Icons.Default.Payment,
            iconBgColor = FlipkartDarkBlue
        )
    )

    val step = steps[currentStep]

    Surface(
        color = FlipkartBackground,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar with Skip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BM STORE ONLINE OFFLINE SHOPPING",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = FlipkartBlue
                )
                Text(
                    text = "Skip",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable { onFinish() }
                        .padding(8.dp)
                )
            }

            // Center Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(step.iconBgColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(step.iconBgColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = step.icon,
                            contentDescription = step.title,
                            tint = SurfaceWhite,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))

                Text(
                    text = step.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = step.subtitle,
                    fontSize = 14.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Step Indicators
                Row(horizontalArrangement = Arrangement.Center) {
                    steps.indices.forEach { index ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(8.dp)
                                .width(if (index == currentStep) 24.dp else 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (index == currentStep) FlipkartBlue else BorderLight)
                        )
                    }
                }
            }

            // Bottom Actions
            Column(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        if (currentStep < steps.size - 1) {
                            currentStep++
                        } else {
                            onFinish()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("onboarding_next_btn")
                ) {
                    Text(
                        text = if (currentStep == steps.size - 1) "Get Started" else "Continue",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurfaceWhite
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Next",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
