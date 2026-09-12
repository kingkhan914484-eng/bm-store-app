package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :identifier OR phone = :identifier LIMIT 1")
    suspend fun getUserByEmailOrPhone(identifier: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY price ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: String): ProductEntity?

    @Query("SELECT * FROM products WHERE category = :category")
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE title LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchProducts(query: String): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Query("UPDATE products SET stockCount = :newStock WHERE id = :productId")
    suspend fun updateStock(productId: String, newStock: Int)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProductById(id: String)

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int
}

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): Flow<List<CartEntity>>

    @Query("SELECT * FROM cart_items WHERE id = :id LIMIT 1")
    suspend fun getCartItem(id: String): CartEntity?

    @Query("SELECT * FROM cart_items WHERE productId = :productId LIMIT 1")
    suspend fun getCartItemByProduct(productId: String): CartEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: CartEntity)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE id = :id")
    suspend fun updateQuantity(id: String, quantity: Int)

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun deleteCartItem(id: String)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY timestamp DESC")
    fun getOrdersByUser(userId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE orderId = :orderId LIMIT 1")
    suspend fun getOrderById(orderId: String): OrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("UPDATE orders SET status = :newStatus, trackingUpdatedTimestamp = :updatedAt WHERE orderId = :orderId")
    suspend fun updateOrderStatus(orderId: String, newStatus: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE orders SET status = 'Cancelled', cancelReason = :reason, trackingUpdatedTimestamp = :updatedAt WHERE orderId = :orderId")
    suspend fun cancelOrder(orderId: String, reason: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE orders SET syncedWithFirebase = 1 WHERE orderId = :orderId")
    suspend fun markOrderSynced(orderId: String)

    @Query("SELECT COUNT(*) FROM orders")
    suspend fun getOrderCount(): Int

    @Query("SELECT SUM(totalAmount) FROM orders WHERE status != 'Cancelled'")
    suspend fun getTotalRevenue(): Double?
}

@Dao
interface WishlistDao {
    @Query("SELECT * FROM wishlist")
    fun getAllWishlist(): Flow<List<WishlistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToWishlist(item: WishlistEntity)

    @Query("DELETE FROM wishlist WHERE productId = :productId")
    suspend fun removeFromWishlist(productId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist WHERE productId = :productId)")
    fun isInWishlist(productId: String): Flow<Boolean>
}

@Dao
interface AddressDao {
    @Query("SELECT * FROM addresses WHERE userId = :userId")
    fun getAddressesByUser(userId: String): Flow<List<AddressEntity>>

    @Query("SELECT * FROM addresses WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultAddress(): AddressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: AddressEntity)

    @Update
    suspend fun updateAddress(address: AddressEntity)

    @Query("DELETE FROM addresses WHERE id = :id")
    suspend fun deleteAddress(id: String)

    @Query("UPDATE addresses SET isDefault = 0 WHERE userId = :userId")
    suspend fun clearDefault(userId: String)

    @Query("UPDATE addresses SET isDefault = 1 WHERE id = :id")
    suspend fun setDefault(id: String)
}

@Dao
interface CouponDao {
    @Query("SELECT * FROM coupons WHERE isActive = 1")
    fun getActiveCoupons(): Flow<List<CouponEntity>>

    @Query("SELECT * FROM coupons WHERE code = :code AND isActive = 1 LIMIT 1")
    suspend fun getCouponByCode(code: String): CouponEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoupon(coupon: CouponEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(coupons: List<CouponEntity>)

    @Query("DELETE FROM coupons WHERE code = :code")
    suspend fun deleteCoupon(code: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM notifications")
    suspend fun clearAll()
}
