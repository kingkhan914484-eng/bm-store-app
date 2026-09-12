package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.WishlistEntity
import com.example.data.model.CategoryItem
import com.example.data.model.Product
import com.example.ui.components.ProductCard
import com.example.ui.theme.BorderLight
import com.example.ui.theme.FlipkartBackground
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartDarkBlue
import com.example.ui.theme.FlipkartGreen
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SearchScreen(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    categories: List<CategoryItem>,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    priceFilterMax: Double?,
    onPriceFilterChange: (Double?) -> Unit,
    inStockOnly: Boolean,
    onToggleInStockOnly: (Boolean) -> Unit,
    sortBy: String,
    onSortChange: (String) -> Unit,
    filteredProducts: List<Product>,
    wishlist: List<WishlistEntity>,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    onToggleWishlist: (Product) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = FlipkartBackground,
        modifier = modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Top Bar
            Surface(
                color = FlipkartBlue,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("search_back_btn")) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = SurfaceWhite
                            )
                        }

                        // Search Input Field
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceWhite)
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search Icon",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                BasicTextField(
                                    value = searchQuery,
                                    onValueChange = onSearchQueryChange,
                                    singleLine = true,
                                    textStyle = TextStyle(
                                        fontSize = 14.sp,
                                        color = TextPrimary
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("search_input_box"),
                                    decorationBox = { innerTextField ->
                                        if (searchQuery.isEmpty()) {
                                            Text(
                                                text = "Search mobiles, electronics, fashion...",
                                                color = TextSecondary,
                                                fontSize = 13.5.sp
                                            )
                                        }
                                        innerTextField()
                                    }
                                )
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(
                                        onClick = { onSearchQueryChange("") },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear",
                                            tint = TextSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Filters and Sorters Bar
            Surface(
                color = SurfaceWhite,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    // Category Chips
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { cat ->
                            val isSelected = selectedCategory.equals(cat.id, ignoreCase = true)
                            FilterChip(
                                selected = isSelected,
                                onClick = { onCategorySelect(cat.id) },
                                label = { Text(cat.name, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = FlipkartBlue,
                                    selectedLabelColor = SurfaceWhite
                                )
                            )
                        }
                    }

                    // Price & Stock Filters Row
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        item {
                            FilterChip(
                                selected = inStockOnly,
                                onClick = { onToggleInStockOnly(!inStockOnly) },
                                label = { Text("In Stock Only", fontSize = 11.5.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = FlipkartGreen,
                                    selectedLabelColor = SurfaceWhite
                                )
                            )
                        }
                        item {
                            FilterChip(
                                selected = priceFilterMax == 2000.0,
                                onClick = { onPriceFilterChange(if (priceFilterMax == 2000.0) null else 2000.0) },
                                label = { Text("Under ₹2,000", fontSize = 11.5.sp) }
                            )
                        }
                        item {
                            FilterChip(
                                selected = priceFilterMax == 10000.0,
                                onClick = { onPriceFilterChange(if (priceFilterMax == 10000.0) null else 10000.0) },
                                label = { Text("Under ₹10,000", fontSize = 11.5.sp) }
                            )
                        }
                        item {
                            FilterChip(
                                selected = priceFilterMax == 30000.0,
                                onClick = { onPriceFilterChange(if (priceFilterMax == 30000.0) null else 30000.0) },
                                label = { Text("Under ₹30,000", fontSize = 11.5.sp) }
                            )
                        }
                        // Sort options
                        item {
                            FilterChip(
                                selected = sortBy == "price_low_high",
                                onClick = { onSortChange(if (sortBy == "price_low_high") "popular" else "price_low_high") },
                                label = { Text("Price: Low to High", fontSize = 11.5.sp) }
                            )
                        }
                        item {
                            FilterChip(
                                selected = sortBy == "rating",
                                onClick = { onSortChange(if (sortBy == "rating") "popular" else "rating") },
                                label = { Text("Highest Rated", fontSize = 11.5.sp) }
                            )
                        }
                    }
                }
            }

            // Results count banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredProducts.size} Products found",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
                Text(
                    text = "BM STORE ONLINE OFFLINE SHOPPING",
                    fontSize = 11.sp,
                    color = FlipkartBlue,
                    fontWeight = FontWeight.Bold
                )
            }

            // Products Grid
            if (filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "No products",
                            tint = BorderLight,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No products found",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Try adjusting your search terms or clearing the filters above.",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredProducts, key = { it.id }) { product ->
                        val isWishlisted = wishlist.any { it.productId == product.id }
                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product) },
                            onAddToCart = { onAddToCart(product) },
                            isWishlisted = isWishlisted,
                            onToggleWishlist = { onToggleWishlist(product) }
                        )
                    }
                }
            }
        }
    }
}
