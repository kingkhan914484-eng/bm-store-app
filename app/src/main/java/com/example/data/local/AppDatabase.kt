package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        ProductEntity::class,
        CartEntity::class,
        OrderEntity::class,
        WishlistEntity::class,
        AddressEntity::class,
        CouponEntity::class,
        NotificationEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun addressDao(): AddressDao
    abstract fun couponDao(): CouponDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bm_store_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(context))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Seed initial data asynchronously on first run
                CoroutineScope(Dispatchers.IO).launch {
                    val database = getDatabase(context)
                    seedInitialData(database)
                }
            }
        }

        suspend fun seedInitialData(database: AppDatabase) {
            // Seed Admin and Default Customer
            val adminUser = UserEntity(
                id = "admin_01",
                name = "BM Store Admin",
                email = "admin@bmstore.com",
                phone = "+91 98765 00000",
                passwordHash = "admin123",
                role = "admin"
            )
            val demoCustomer = UserEntity(
                id = "user_01",
                name = "Rahim Ahmed",
                email = "rahim@bmstore.com",
                phone = "+91 98765 43210",
                passwordHash = "user123",
                role = "customer"
            )
            database.userDao().insertUser(adminUser)
            database.userDao().insertUser(demoCustomer)

            // Seed Initial Default Address
            val defaultAddress = AddressEntity(
                id = "addr_01",
                userId = "user_01",
                fullName = "Rahim Ahmed",
                phone = "+91 98765 43210",
                pincode = "700001",
                houseDetails = "Flat 4B, Emerald Heights, Park Street",
                city = "Kolkata",
                state = "West Bengal",
                landmark = "Near Park Street Metro Station",
                addressType = "Home",
                isDefault = true
            )
            val workAddress = AddressEntity(
                id = "addr_02",
                userId = "user_01",
                fullName = "Rahim Ahmed",
                phone = "+91 98765 43210",
                pincode = "700091",
                houseDetails = "BM STORE Tech Park, Sector V, Salt Lake",
                city = "Kolkata",
                state = "West Bengal",
                landmark = "Opposite Webel Bhavan",
                addressType = "Work",
                isDefault = false
            )
            database.addressDao().insertAddress(defaultAddress)
            database.addressDao().insertAddress(workAddress)

            // Seed Coupons
            val coupons = listOf(
                CouponEntity(
                    code = "BMSTORE50",
                    discountPercent = 0,
                    flatDiscount = 50.0,
                    minOrderAmount = 499.0,
                    description = "Flat ₹50 OFF on orders above ₹499",
                    isActive = true
                ),
                CouponEntity(
                    code = "WELCOME100",
                    discountPercent = 0,
                    flatDiscount = 100.0,
                    minOrderAmount = 999.0,
                    description = "Flat ₹100 OFF on your first big order above ₹999",
                    isActive = true
                ),
                CouponEntity(
                    code = "FESTIVE15",
                    discountPercent = 15,
                    flatDiscount = 0.0,
                    minOrderAmount = 1499.0,
                    description = "15% Instant Discount on orders above ₹1499 (Max ₹500)",
                    isActive = true
                )
            )
            database.couponDao().insertAll(coupons)

            // Seed Notifications
            val notifications = listOf(
                NotificationEntity(
                    id = "notif_01",
                    title = "Welcome to BM STORE ONLINE OFFLINE SHOPPING!",
                    message = "Experience trusted online shopping with instant offline store pickup at BM STORE.",
                    timestamp = System.currentTimeMillis() - 86400000,
                    type = "system",
                    isRead = false
                ),
                NotificationEntity(
                    id = "notif_02",
                    title = "Special Discount: BMSTORE50",
                    message = "Apply code BMSTORE50 at checkout to get ₹50 OFF on your next order!",
                    timestamp = System.currentTimeMillis() - 3600000,
                    type = "promo",
                    isRead = false
                )
            )
            notifications.forEach { database.notificationDao().insertNotification(it) }

            // Seed Products
            val products = getInitialProducts()
            database.productDao().insertAll(products)
        }

        fun getInitialProducts(): List<ProductEntity> {
            return listOf(
                ProductEntity(
                    id = "mob_01",
                    title = "Apple iPhone 15 (Black, 128 GB)",
                    category = "mobiles",
                    subcategory = "Smartphones",
                    price = 69999.0,
                    originalPrice = 79900.0,
                    discountPercent = 12,
                    rating = 4.6,
                    ratingCount = 28410,
                    imageUrl = "https://images.unsplash.com/photo-1510557880182-3d4d3cba35a5?w=500&q=80",
                    brand = "Apple",
                    description = "Dynamic Island, 48MP Main camera, USB-C, and durable color-infused glass and aluminum design.",
                    highlightsRaw = "128 GB ROM | 15.49 cm (6.1 inch) Super Retina XDR Display | 48MP + 12MP Dual Camera | A16 Bionic Chip 6 Core Processor",
                    stockCount = 15,
                    isAssured = true,
                    freeDelivery = true,
                    seller = "BM Retail Hub",
                    offlineStockAvailable = true,
                    offlineStoreLocation = "BM STORE Central Hub & Express Counter",
                    variantsRaw = "128 GB,256 GB,512 GB",
                    colorsRaw = "Black,Blue,Green,Pink,Yellow"
                ),
                ProductEntity(
                    id = "mob_02",
                    title = "Samsung Galaxy S24 5G (Onyx Black, 256 GB)",
                    category = "mobiles",
                    subcategory = "Smartphones",
                    price = 74999.0,
                    originalPrice = 89999.0,
                    discountPercent = 16,
                    rating = 4.5,
                    ratingCount = 14320,
                    imageUrl = "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=500&q=80",
                    brand = "Samsung",
                    description = "Galaxy AI is here. Search like never before, get quick language translation, and effortlessly edit your photos.",
                    highlightsRaw = "8 GB RAM | 256 GB ROM | 15.75 cm (6.2 inch) Full HD+ Dynamic AMOLED 2X | 50MP Triple Camera",
                    stockCount = 12,
                    isAssured = true,
                    freeDelivery = true,
                    seller = "BM Retail Hub",
                    offlineStockAvailable = true,
                    offlineStoreLocation = "BM STORE Central Hub & Express Counter",
                    variantsRaw = "128 GB,256 GB",
                    colorsRaw = "Onyx Black,Cobalt Violet,Amber Yellow"
                ),
                ProductEntity(
                    id = "mob_03",
                    title = "OnePlus Nord CE4 Lite 5G (Super Silver, 128 GB)",
                    category = "mobiles",
                    subcategory = "5G Mobiles",
                    price = 19999.0,
                    originalPrice = 22999.0,
                    discountPercent = 13,
                    rating = 4.3,
                    ratingCount = 31200,
                    imageUrl = "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=500&q=80",
                    brand = "OnePlus",
                    description = "120 Hz AMOLED Display, 5500 mAh battery with 80W SUPERVOOC fast charging and Sony LYT-600 Camera.",
                    highlightsRaw = "8 GB RAM | 128 GB ROM | 16.94 cm (6.67 inch) Full HD+ AMOLED 120Hz | 50MP Sony LYT-600 OIS Camera",
                    stockCount = 25,
                    isAssured = true,
                    freeDelivery = true,
                    seller = "BM Retail Hub",
                    offlineStockAvailable = true,
                    offlineStoreLocation = "BM STORE Central Hub & Express Counter",
                    variantsRaw = "128 GB,256 GB",
                    colorsRaw = "Super Silver,Mega Blue"
                ),
                ProductEntity(
                    id = "elec_01",
                    title = "Sony WH-1000XM5 Wireless Active Noise Cancelling Headphones",
                    category = "electronics",
                    subcategory = "Audio",
                    price = 26990.0,
                    originalPrice = 34990.0,
                    discountPercent = 22,
                    rating = 4.7,
                    ratingCount = 8920,
                    imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500&q=80",
                    brand = "Sony",
                    description = "Industry-leading noise cancellation with 8 microphones and Auto NC Optimizer. 30 hours battery life.",
                    highlightsRaw = "Industry-leading NC | 30-hr Battery Life | Quick 3-min Charge for 3 hours | Multipoint Connection",
                    stockCount = 8,
                    isAssured = true,
                    freeDelivery = true,
                    seller = "BM Electronics Point",
                    offlineStockAvailable = true,
                    offlineStoreLocation = "BM STORE Central Hub & Express Counter",
                    variantsRaw = "Standard",
                    colorsRaw = "Silver,Black,Midnight Blue"
                ),
                ProductEntity(
                    id = "elec_02",
                    title = "Apple MacBook Air Apple M2 - (8 GB/256 GB SSD/macOS)",
                    category = "electronics",
                    subcategory = "Laptops",
                    price = 84990.0,
                    originalPrice = 99900.0,
                    discountPercent = 14,
                    rating = 4.8,
                    ratingCount = 12900,
                    imageUrl = "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=500&q=80",
                    brand = "Apple",
                    description = "Strikingly thin design with fast M2 chip, Liquid Retina display, 1080p FaceTime HD camera.",
                    highlightsRaw = "Apple M2 Processor | 8 GB Unified Memory | 256 GB SSD Storage | 34.54 cm Liquid Retina Display",
                    stockCount = 6,
                    isAssured = true,
                    freeDelivery = true,
                    seller = "BM Electronics Point",
                    offlineStockAvailable = true,
                    offlineStoreLocation = "BM STORE Central Hub & Express Counter",
                    variantsRaw = "256 GB SSD,512 GB SSD",
                    colorsRaw = "Space Grey,Silver,Midnight,Starlight"
                ),
                ProductEntity(
                    id = "elec_03",
                    title = "Noise ColorFit Pulse 3 Smartwatch (1.96 inch Display)",
                    category = "electronics",
                    subcategory = "Wearables",
                    price = 1499.0,
                    originalPrice = 4999.0,
                    discountPercent = 70,
                    rating = 4.2,
                    ratingCount = 84300,
                    imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500&q=80",
                    brand = "Noise",
                    description = "1.96-inch TFT display, Bluetooth calling, 100+ sports modes, 7-day battery life, and complete health tracker.",
                    highlightsRaw = "1.96\" Big Display | Bluetooth Calling | 100+ Sports Modes | IP68 Water Resistant",
                    stockCount = 40,
                    isAssured = true,
                    freeDelivery = true,
                    seller = "BM Electronics Point",
                    offlineStockAvailable = true,
                    offlineStoreLocation = "BM STORE Central Hub & Express Counter",
                    variantsRaw = "Standard",
                    colorsRaw = "Jet Black,Rose Pink,Deep Wine"
                ),
                ProductEntity(
                    id = "fash_01",
                    title = "Nike Air Max Impact 4 Basketball & Casual Sneakers For Men",
                    category = "fashion",
                    subcategory = "Footwear",
                    price = 4995.0,
                    originalPrice = 8995.0,
                    discountPercent = 44,
                    rating = 4.4,
                    ratingCount = 9800,
                    imageUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500&q=80",
                    brand = "Nike",
                    description = "Max Air cushioning in the heel helps dissipate impact force. Rubber wraps up the sides for added durability.",
                    highlightsRaw = "Genuine Nike Footwear | Max Air Heel Cushioning | Breathable Mesh Upper | Durable Traction Outsole",
                    stockCount = 18,
                    isAssured = true,
                    freeDelivery = true,
                    seller = "BM Fashion Store",
                    offlineStockAvailable = true,
                    offlineStoreLocation = "BM STORE Central Hub & Express Counter",
                    variantsRaw = "UK 7,UK 8,UK 9,UK 10",
                    colorsRaw = "Red/White,Triple Black"
                ),
                ProductEntity(
                    id = "fash_02",
                    title = "Puma Motorsport Men Slim Fit Casual Cotton Polo T-Shirt",
                    category = "fashion",
                    subcategory = "Clothing",
                    price = 1299.0,
                    originalPrice = 2999.0,
                    discountPercent = 56,
                    rating = 4.3,
                    ratingCount = 6540,
                    imageUrl = "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=500&q=80",
                    brand = "Puma",
                    description = "Crafted from soft breathable 100% cotton with classic polo collar and ribbed cuffs for all-day comfort.",
                    highlightsRaw = "100% Bio-Washed Cotton | Slim Fit Design | Official Motorsport Emblem | Machine Wash Safe",
                    stockCount = 30,
                    isAssured = true,
                    freeDelivery = true,
                    seller = "BM Fashion Store",
                    offlineStockAvailable = true,
                    offlineStoreLocation = "BM STORE Central Hub & Express Counter",
                    variantsRaw = "S,M,L,XL,XXL",
                    colorsRaw = "Navy Blue,White,Classic Black"
                ),
                ProductEntity(
                    id = "app_01",
                    title = "LG 108 cm (43 inch) Ultra HD (4K) Smart WebOS TV",
                    category = "appliances",
                    subcategory = "Televisions",
                    price = 28990.0,
                    originalPrice = 49990.0,
                    discountPercent = 42,
                    rating = 4.4,
                    ratingCount = 18900,
                    imageUrl = "https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=500&q=80",
                    brand = "LG",
                    description = "Real 4K clarity with α5 AI Processor 4K Gen6, HDR10 Pro, AI Sound and Magic Remote compatibility.",
                    highlightsRaw = "4K Ultra HD (3840 x 2160) | 20W Audio Output with AI Sound | WebOS with ThinQ AI | 3 HDMI & 2 USB Ports",
                    stockCount = 10,
                    isAssured = true,
                    freeDelivery = true,
                    seller = "BM Home Appliances",
                    offlineStockAvailable = true,
                    offlineStoreLocation = "BM STORE Central Hub & Express Counter",
                    variantsRaw = "43 inch,55 inch",
                    colorsRaw = "Ceramic Black"
                ),
                ProductEntity(
                    id = "home_01",
                    title = "Sleepyhead Original 3-Layer Orthopedic Memory Foam Mattress",
                    category = "home",
                    subcategory = "Furniture",
                    price = 8499.0,
                    originalPrice = 14999.0,
                    discountPercent = 43,
                    rating = 4.5,
                    ratingCount = 22100,
                    imageUrl = "https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=500&q=80",
                    brand = "Sleepyhead",
                    description = "Pressure-relieving memory foam with high-density base support and breathable outer fabric.",
                    highlightsRaw = "Orthopedic Spinal Support | High Density Foam | 10-Year Manufacturer Warranty | Removable Washable Cover",
                    stockCount = 14,
                    isAssured = true,
                    freeDelivery = true,
                    seller = "BM Home & Living",
                    offlineStockAvailable = true,
                    offlineStoreLocation = "BM STORE Central Hub & Express Counter",
                    variantsRaw = "Single 72x36,Queen 78x60,King 78x72",
                    colorsRaw = "White & Grey"
                ),
                ProductEntity(
                    id = "groc_01",
                    title = "Fortune Sunlite Refined Sunflower Oil (5 L Can)",
                    category = "grocery",
                    subcategory = "Oils & Ghee",
                    price = 629.0,
                    originalPrice = 850.0,
                    discountPercent = 26,
                    rating = 4.6,
                    ratingCount = 43000,
                    imageUrl = "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=500&q=80",
                    brand = "Fortune",
                    description = "Enriched with Vitamins A and D. Light and healthy oil ideal for Indian cooking and deep frying.",
                    highlightsRaw = "5 Litres Pack | Zero Cholesterol | Fortified with Vitamins | Pure Refined Sunflower Oil",
                    stockCount = 50,
                    isAssured = true,
                    freeDelivery = true,
                    seller = "BM Supermarket",
                    offlineStockAvailable = true,
                    offlineStoreLocation = "BM STORE Central Hub & Express Counter",
                    variantsRaw = "1 Litre,5 Litres",
                    colorsRaw = "Standard"
                ),
                ProductEntity(
                    id = "beauty_01",
                    title = "Maybelline New York Super Stay Matte Ink Liquid Lipstick",
                    category = "beauty",
                    subcategory = "Makeup",
                    price = 459.0,
                    originalPrice = 699.0,
                    discountPercent = 34,
                    rating = 4.3,
                    ratingCount = 37800,
                    imageUrl = "https://images.unsplash.com/photo-1586495777744-4413f21062fa?w=500&q=80",
                    brand = "Maybelline",
                    description = "Flawless matte finish that stays up to 16 hours without fading, transferring, or smudging.",
                    highlightsRaw = "Up to 16HR Matte Wear | Transfer-Proof & Waterproof | Precision Arrow Applicator | Vibrant Intense Pigment",
                    stockCount = 35,
                    isAssured = true,
                    freeDelivery = true,
                    seller = "BM Beauty & Personal Care",
                    offlineStockAvailable = true,
                    offlineStoreLocation = "BM STORE Central Hub & Express Counter",
                    variantsRaw = "Standard",
                    colorsRaw = "Seductress,Pioneer,Ruler,Lover"
                )
            )
        }
    }
}
