package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.WishlistEntity
import com.example.data.model.CategoryItem
import com.example.data.model.Product
import com.example.ui.components.ProductCard
import com.example.ui.theme.DividerGray
import com.example.ui.theme.FlipkartBackground
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartYellow
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CategoriesScreen(
    categories: List<CategoryItem>,
    products: List<Product>,
    wishlist: List<WishlistEntity>,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    onToggleWishlist: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeCategory by remember { mutableStateOf(categories.firstOrNull()?.id ?: "mobiles") }

    val categoryProducts = remember(activeCategory, products) {
        if (activeCategory == "all") products
        else products.filter { it.category.equals(activeCategory, ignoreCase = true) }
    }

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(FlipkartBackground)
    ) {
        // Left Column: Category Navigation Rail
        Surface(
            color = SurfaceWhite,
            modifier = Modifier
                .width(100.dp)
                .fillMaxHeight(),
            shadowElevation = 2.dp
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("categories_rail")
            ) {
                items(categories) { cat ->
                    val isSelected = activeCategory == cat.id
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { activeCategory = cat.id }
                            .background(
                                if (isSelected) Color(0xFFE8F0FE) else SurfaceWhite
                            )
                            .padding(vertical = 12.dp, horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) FlipkartBlue else Color(0xFFF1F3F6)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getCategoryIcon(cat.id),
                                contentDescription = cat.name,
                                tint = if (isSelected) Color.White else FlipkartBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = cat.name,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) FlipkartBlue else TextPrimary,
                            textAlign = TextAlign.Center,
                            maxLines = 2
                        )
                    }
                }
            }
        }

        // Right Column: Products in Category
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(8.dp)
        ) {
            // Category title header
            Card(
                colors = CardDefaults.cardColors(containerColor = FlipkartBlue),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "${activeCategory.replaceFirstChar { it.uppercase() }} Store",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "BM STORE Online & Offline Catalog",
                        color = FlipkartYellow,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("category_products_list"),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categoryProducts) { product ->
                    val isWishlisted = wishlist.any { it.productId == product.id }
                    ProductCard(
                        product = product,
                        onClick = { onProductClick(product) },
                        onAddToCart = { onAddToCart(product) },
                        isWishlisted = isWishlisted,
                        onToggleWishlist = { onToggleWishlist(product) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(70.dp))
                }
            }
        }
    }
}

private fun getCategoryIcon(categoryId: String): ImageVector {
    return when (categoryId.lowercase()) {
        "mobiles" -> Icons.Default.Smartphone
        "electronics" -> Icons.Default.Laptop
        "fashion" -> Icons.Default.ShoppingBag
        "appliances" -> Icons.Default.Tv
        "home" -> Icons.Default.Chair
        "beauty" -> Icons.Default.Face
        "grocery" -> Icons.Default.ShoppingBasket
        else -> Icons.Default.Category
    }
}
