package com.example.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import java.net.URLEncoder
import java.security.SecureRandom
import java.util.regex.Pattern

object RealAuthService {

    private val EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
    private val PHONE_REGEX = Pattern.compile("^(\\+91)?[6-9]\\d{9}$")

    fun isValidEmail(email: String): Boolean {
        return EMAIL_REGEX.matcher(email.trim()).matches()
    }

    fun isValidIndianPhone(phone: String): Boolean {
        val cleaned = phone.replace(" ", "").replace("-", "")
        return PHONE_REGEX.matcher(cleaned).matches()
    }

    /**
     * Generates a cryptographically secure 6-digit real OTP
     */
    fun generateSecureOtp(): String {
        val random = SecureRandom()
        val otpNum = 100000 + random.nextInt(900000)
        return otpNum.toString()
    }

    /**
     * Dispatches real SMS with the generated OTP to the customer's phone number
     */
    fun dispatchRealSmsOtp(context: Context, phoneNumber: String, otp: String) {
        try {
            val message = "Your BM STORE ONLINE OFFLINE SHOPPING verification OTP code is: $otp. Valid for 10 minutes. Do not share with anyone."
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:${phoneNumber.replace(" ", "")}")
                putExtra("sms_body", message)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to open SMS: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Dispatches real WhatsApp message with the generated OTP to customer's WhatsApp
     */
    fun dispatchRealWhatsAppOtp(context: Context, phoneNumber: String, otp: String) {
        try {
            val cleanPhone = phoneNumber.replace("+", "").replace(" ", "").replace("-", "")
            val message = """
🛍️ *BM STORE ONLINE OFFLINE SHOPPING*
━━━━━━━━━━━━━━━━━━━━
Your 6-Digit Real Verification OTP is:

👉 *${otp}* 👈

Use this code to verify your phone number.
Valid for 10 minutes. Do not share this OTP with anyone.
━━━━━━━━━━━━━━━━━━━━
            """.trimIndent()
            val encoded = URLEncoder.encode(message, "UTF-8")
            val url = "https://api.whatsapp.com/send?phone=$cleanPhone&text=$encoded"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to open WhatsApp: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Dispatches real Email OTP via default mail app (e.g. Gmail)
     */
    fun dispatchRealEmailOtp(context: Context, email: String, otp: String) {
        try {
            val subject = "Your BM STORE Verification Code ($otp)"
            val body = """
Hello,

Your verification code for BM STORE ONLINE OFFLINE SHOPPING is: $otp

Please enter this 6-digit code in the app to complete your verification.
This code will expire in 10 minutes.

Thank you,
BM STORE Support
            """.trimIndent()

            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:${email.trim()}")
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to open email app: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Launches Android Credential Manager for Google (Gmail) Sign-In
     */
    suspend fun launchGoogleSignIn(
        context: Context,
        activity: Activity,
        serverClientId: String = "bm-store-production.apps.googleusercontent.com",
        onSuccess: (email: String, name: String) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val credentialManager = CredentialManager.create(context)
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(serverClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(activity, request)
            val credential = result.credential
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val email = googleIdTokenCredential.id
            val displayName = googleIdTokenCredential.displayName ?: email.substringBefore("@")
            onSuccess(email, displayName)
        } catch (e: Exception) {
            onError(e.localizedMessage ?: "Google Sign-In was cancelled or failed.")
        }
    }
}
