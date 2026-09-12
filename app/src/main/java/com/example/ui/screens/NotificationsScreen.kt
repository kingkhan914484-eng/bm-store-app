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
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.NotificationEntity
import com.example.ui.theme.DividerGray
import com.example.ui.theme.FlipkartBackground
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartGreen
import com.example.ui.theme.FlipkartOrange
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationsScreen(
    notifications: List<NotificationEntity>,
    onNotificationClick: (NotificationEntity) -> Unit,
    onMarkAllRead: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FlipkartBackground)
    ) {
        item {
            Surface(
                color = SurfaceWhite,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "NOTIFICATIONS (${notifications.size})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "BM STORE ONLINE OFFLINE SHOPPING",
                            fontSize = 10.5.sp,
                            color = FlipkartBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (notifications.any { !it.isRead }) {
                        TextButton(onClick = onMarkAllRead) {
                            Icon(Icons.Default.DoneAll, null, modifier = Modifier.size(16.dp), tint = FlipkartBlue)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Mark All Read", fontSize = 12.sp, color = FlipkartBlue, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (notifications.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = DividerGray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No notifications yet",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Updates regarding your orders and offers will show here.",
                            fontSize = 12.5.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        } else {
            items(notifications, key = { it.id }) { item ->
                val icon = when (item.type) {
                    "order" -> Icons.Default.ShoppingBag
                    "promo" -> Icons.Default.LocalOffer
                    "store" -> Icons.Default.Store
                    else -> Icons.Default.Notifications
                }
                val iconColor = when (item.type) {
                    "order" -> FlipkartGreen
                    "promo" -> FlipkartOrange
                    "store" -> FlipkartBlue
                    else -> FlipkartBlue
                }

                Surface(
                    color = if (item.isRead) SurfaceWhite else FlipkartBlue.copy(alpha = 0.04f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNotificationClick(item) }
                        .padding(bottom = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(iconColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = iconColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.title,
                                    fontSize = 13.5.sp,
                                    fontWeight = if (item.isRead) FontWeight.SemiBold else FontWeight.Bold,
                                    color = TextPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                if (!item.isRead) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(FlipkartBlue)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = item.message,
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = dateFormatter.format(Date(item.timestamp)),
                                fontSize = 10.5.sp,
                                color = TextSecondary.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }
}
