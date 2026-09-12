package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.WishlistEntity
import com.example.data.model.CategoryItem
import com.example.data.model.Product
import com.example.ui.components.ProductCard
import com.example.ui.theme.BorderLight
import com.example.ui.theme.FlipkartBackground
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartDarkBlue
import com.example.ui.theme.FlipkartGreen
import com.example.ui.theme.FlipkartOrange
import com.example.ui.theme.FlipkartYellow
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    products: List<Product>,
    categories: List<CategoryItem>,
    selectedCategory: String,
    searchQuery: String,
    wishlist: List<WishlistEntity>,
    onCategorySelect: (String) -> Unit,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    onToggleWishlist: (Product) -> Unit,
    onOpenOfflineStore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredProducts = remember(products, selectedCategory, searchQuery) {
        products.filter { product ->
            val matchCategory = selectedCategory == "all" || product.category.equals(selectedCategory, ignoreCase = true)
            val matchQuery = searchQuery.isBlank() ||
                    product.title.contains(searchQuery, ignoreCase = true) ||
                    product.brand.contains(searchQuery, ignoreCase = true) ||
                    product.description.contains(searchQuery, ignoreCase = true)
            matchCategory && matchQuery
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FlipkartBackground)
            .testTag("home_screen_list")
    ) {
        // 1. Categories Horizontal Row (Flipkart Icon Circles)
        item {
            CategoryHorizontalBar(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelect = onCategorySelect
            )
        }

        // 2. Hero Promotional Banner Carousel
        item {
            HeroBannerSection(
                onBannerClick = { onCategorySelect("electronics") }
            )
        }

        // 3. Quick Action Strips (SuperCoins, Scan & Pay, Offline Store)
        item {
            QuickActionsBar(onOfflineStoreClick = onOpenOfflineStore)
        }

        // 4. "Deals of the Day" with Timer
        item {
            DealsOfTheDayHeader()
        }

        // 5. Featured Deals Horizontal Scroll
        item {
            FeaturedDealsRow(
                products = products.take(5),
                wishlist = wishlist,
                onProductClick = onProductClick,
                onAddToCart = onAddToCart,
                onToggleWishlist = onToggleWishlist
            )
        }

        // 6. Section Header for Catalog
        item {
            SectionHeader(
                title = if (selectedCategory == "all") "Suggested for You" else "Products in ${selectedCategory.replaceFirstChar { it.uppercase() }}",
                subtitle = "Based on your browsing & offline store stock"
            )
        }

        // 7. Products 2-Column Grid
        item {
            if (filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.LocalOffer,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No products found for \"$searchQuery\"",
                            fontSize = 15.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                ProductsGrid(
                    products = filteredProducts,
                    wishlist = wishlist,
                    onProductClick = onProductClick,
                    onAddToCart = onAddToCart,
                    onToggleWishlist = onToggleWishlist
                )
            }
        }

        // Bottom spacer
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun CategoryHorizontalBar(
    categories: List<CategoryItem>,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit
) {
    Surface(
        color = SurfaceWhite,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategory.equals(cat.id, ignoreCase = true)
                Column(
                    modifier = Modifier
                        .clickable { onCategorySelect(cat.id) }
                        .testTag("cat_chip_${cat.id}"),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) FlipkartBlue else Color(0xFFF0F4FF)
                            )
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) FlipkartYellow else Color(0xFFE2E8F0),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        val icon = getCategoryIcon(cat.id)
                        Icon(
                            imageVector = icon,
                            contentDescription = cat.name,
                            tint = if (isSelected) Color.White else FlipkartBlue,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = cat.name,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) FlipkartBlue else TextPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroBannerSection(onBannerClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onBannerClick)
            .testTag("hero_banner")
    ) {
        // Use generated hero banner drawable
        Image(
            painter = painterResource(id = R.drawable.bm_hero_banner),
            contentDescription = "BM STORE Big Shopping Days Banner",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        )

        // Overlay badge
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(FlipkartOrange)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "BM STORE • ONLINE & OFFLINE SHOPPING",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun QuickActionsBar(onOfflineStoreClick: () -> Unit) {
    Surface(
        color = SurfaceWhite,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp),
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            QuickActionItem(
                icon = Icons.Default.MonetizationOn,
                title = "SuperCoins",
                subtitle = "420 Balance",
                tint = FlipkartYellow
            ) { }

            QuickActionItem(
                icon = Icons.Default.Store,
                title = "Offline Store",
                subtitle = "Scan & Pickup",
                tint = FlipkartBlue,
                onClick = onOfflineStoreClick
            )

            QuickActionItem(
                icon = Icons.Default.CheckCircle,
                title = "BM Assured",
                subtitle = "100% Original",
                tint = FlipkartGreen
            ) { }
        }
    }
}

@Composable
private fun QuickActionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (tint == FlipkartYellow) Color(0xFFB8860B) else tint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(
                text = title,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 9.5.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun DealsOfTheDayHeader() {
    var secondsLeft by remember { mutableStateOf(52920L) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            if (secondsLeft > 0) secondsLeft--
        }
    }

    val hours = secondsLeft / 3600
    val minutes = (secondsLeft % 3600) / 60
    val seconds = secondsLeft % 60
    val timeFormatted = String.format("%02dh : %02dm : %02ds", hours, minutes, seconds)

    Surface(
        color = SurfaceWhite,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp)
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
                    text = "Deals of the Day",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = FlipkartOrange,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = timeFormatted,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = FlipkartOrange
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(FlipkartBlue)
                    .clickable { }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "VIEW ALL",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FeaturedDealsRow(
    products: List<Product>,
    wishlist: List<WishlistEntity>,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    onToggleWishlist: (Product) -> Unit
) {
    Surface(
        color = SurfaceWhite,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(products) { product ->
                val isWishlisted = wishlist.any { it.productId == product.id }
                ProductCard(
                    product = product,
                    onClick = { onProductClick(product) },
                    onAddToCart = { onAddToCart(product) },
                    isWishlisted = isWishlisted,
                    onToggleWishlist = { onToggleWishlist(product) },
                    modifier = Modifier.width(180.dp)
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Surface(
        color = SurfaceWhite,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
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

@Composable
private fun ProductsGrid(
    products: List<Product>,
    wishlist: List<WishlistEntity>,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    onToggleWishlist: (Product) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        // Chunk into pairs of 2 for grid layout inside LazyColumn
        val chunked = products.chunked(2)
        chunked.forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (item in rowItems) {
                    val isWishlisted = wishlist.any { it.productId == item.id }
                    Box(modifier = Modifier.weight(1f)) {
                        ProductCard(
                            product = item,
                            onClick = { onProductClick(item) },
                            onAddToCart = { onAddToCart(item) },
                            isWishlisted = isWishlisted,
                            onToggleWishlist = { onToggleWishlist(item) }
                        )
                    }
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
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
