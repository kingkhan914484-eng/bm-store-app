package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.local.OrderEntity
import java.net.URLEncoder

object OrderNotificationManager {

    private const val CHANNEL_ID = "bm_store_orders_channel"
    private const val CHANNEL_NAME = "BM STORE Order Alerts"
    private const val PREFS_NAME = "bm_store_notification_prefs"
    private const val KEY_ADMIN_PHONE = "key_admin_phone"
    const val DEFAULT_ADMIN_PHONE = "+919876543210"

    fun getAdminPhone(context: Context): String {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_ADMIN_PHONE, DEFAULT_ADMIN_PHONE) ?: DEFAULT_ADMIN_PHONE
    }

    fun setAdminPhone(context: Context, phone: String) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_ADMIN_PHONE, phone.trim()).apply()
    }

    /**
     * Shows a real Android Status Bar Push Notification on device
     */
    fun showSystemOrderNotification(context: Context, order: OrderEntity) {
        try {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Instant notifications when new orders are placed in BM STORE"
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 300, 200, 300)
                }
                notificationManager.createNotificationChannel(channel)
            }

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                order.orderId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val bigText = StringBuilder().apply {
                append("🛍️ Order #${order.orderId}\n")
                append("👤 Customer: ${order.customerName} (${order.customerPhone})\n")
                append("📍 Address: ${order.deliveryAddress}\n")
                append("📦 Mode: ${if (order.isOfflinePickup) "Store Pickup" else "Doorstep Delivery"}\n")
                append("💳 Payment: ${order.paymentMethod} (₹${order.totalAmount.toInt()})\n")
                append("📝 Items: ${order.itemsSummary}")
            }.toString()

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("🛒 New Order Received! #${order.orderId}")
                .setContentText("₹${order.totalAmount.toInt()} from ${order.customerName} (${order.paymentMethod})")
                .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setVibrate(longArrayOf(0, 300, 200, 300))
                .build()

            notificationManager.notify(order.orderId.hashCode(), notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Formats and opens WhatsApp to send order summary to store owner or customer
     */
    fun sendWhatsAppOrderAlert(
        context: Context,
        order: OrderEntity,
        targetPhone: String? = null
    ) {
        try {
            val phoneToUse = (targetPhone ?: getAdminPhone(context))
                .replace("+", "")
                .replace(" ", "")
                .replace("-", "")

            val message = """
🛍️ *NEW ORDER ALERT - BM STORE* 🛍️
━━━━━━━━━━━━━━━━━━━━
📋 *Order ID:* #${order.orderId}
👤 *Customer:* ${order.customerName}
📞 *Mobile:* ${order.customerPhone}
📍 *Address:* ${order.deliveryAddress}
📦 *Delivery Mode:* ${if (order.isOfflinePickup) "Store Pickup" else "Home Delivery"}

🛒 *Items Ordered:*
${order.itemsSummary}

💰 *Total Amount:* ₹${order.totalAmount.toInt()}
💳 *Payment Method:* ${order.paymentMethod}
📊 *Order Status:* ${order.status}
━━━━━━━━━━━━━━━━━━━━
*BM STORE ONLINE OFFLINE SHOPPING*
            """.trimIndent()

            val encodedMessage = URLEncoder.encode(message, "UTF-8")
            val url = "https://api.whatsapp.com/send?phone=$phoneToUse&text=$encodedMessage"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Could not open WhatsApp: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Formats and triggers SMS to send order summary to store owner or customer
     */
    fun sendSmsOrderAlert(
        context: Context,
        order: OrderEntity,
        targetPhone: String? = null
    ) {
        try {
            val phoneToUse = targetPhone ?: getAdminPhone(context)
            val smsText = "BM STORE: New Order #${order.orderId} from ${order.customerName} (${order.customerPhone}). Amount: Rs.${order.totalAmount.toInt()} (${order.paymentMethod}). Mode: ${if (order.isOfflinePickup) "Store Pickup" else "Home Delivery"}. Items: ${order.itemsSummary}. Address: ${order.deliveryAddress}"

            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:${phoneToUse.replace(" ", "")}")
                putExtra("sms_body", smsText)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Could not open SMS app: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
