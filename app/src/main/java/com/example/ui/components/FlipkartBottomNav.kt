package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DividerGray
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartOrange
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ShopTab

@Composable
fun FlipkartBottomNav(
    selectedTab: ShopTab,
    onTabSelected: (ShopTab) -> Unit,
    cartItemCount: Int,
    notificationCount: Int = 2,
    modifier: Modifier = Modifier
) {
    Surface(
        color = SurfaceWhite,
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            HorizontalDivider(thickness = 0.8.dp, color = DividerGray)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavItem(
                    label = "Home",
                    selectedIcon = Icons.Filled.Home,
                    unselectedIcon = Icons.Outlined.Home,
                    isSelected = selectedTab == ShopTab.HOME,
                    onClick = { onTabSelected(ShopTab.HOME) },
                    testTag = "nav_home"
                )
                NavItem(
                    label = "Categories",
                    selectedIcon = Icons.Filled.Category,
                    unselectedIcon = Icons.Outlined.Category,
                    isSelected = selectedTab == ShopTab.CATEGORIES,
                    onClick = { onTabSelected(ShopTab.CATEGORIES) },
                    testTag = "nav_categories"
                )
                NavItem(
                    label = "Notifications",
                    selectedIcon = Icons.Filled.Notifications,
                    unselectedIcon = Icons.Outlined.Notifications,
                    isSelected = selectedTab == ShopTab.NOTIFICATIONS,
                    onClick = { onTabSelected(ShopTab.NOTIFICATIONS) },
                    badgeCount = notificationCount,
                    testTag = "nav_notifications"
                )
                NavItem(
                    label = "Account",
                    selectedIcon = Icons.Filled.Person,
                    unselectedIcon = Icons.Outlined.Person,
                    isSelected = selectedTab == ShopTab.ACCOUNT,
                    onClick = { onTabSelected(ShopTab.ACCOUNT) },
                    testTag = "nav_account"
                )
                NavItem(
                    label = "Cart",
                    selectedIcon = Icons.Filled.ShoppingCart,
                    unselectedIcon = Icons.Outlined.ShoppingCart,
                    isSelected = selectedTab == ShopTab.CART,
                    onClick = { onTabSelected(ShopTab.CART) },
                    badgeCount = cartItemCount,
                    testTag = "nav_cart"
                )
            }
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    badgeCount: Int = 0,
    testTag: String
) {
    val interactionSource = remember { MutableInteractionSource() }
    val activeColor = FlipkartBlue
    val inactiveColor = TextSecondary

    Column(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        BadgedBox(
            badge = {
                if (badgeCount > 0) {
                    Badge(
                        containerColor = FlipkartOrange,
                        contentColor = Color.White
                    ) {
                        Text(
                            text = if (badgeCount > 99) "99+" else "$badgeCount",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        ) {
            Icon(
                imageVector = if (isSelected) selectedIcon else unselectedIcon,
                contentDescription = label,
                tint = if (isSelected) activeColor else inactiveColor,
                modifier = Modifier.size(23.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) activeColor else inactiveColor
        )
    }
}
