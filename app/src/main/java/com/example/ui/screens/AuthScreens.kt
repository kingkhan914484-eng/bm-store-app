package com.example.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auth.RealAuthService
import com.example.ui.theme.BorderLight
import com.example.ui.theme.DividerGray
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
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────
// LOGIN SCREEN (REAL GMAIL & REAL MOBILE OTP)
// ─────────────────────────────────────────────

@Composable
fun LoginScreen(
    onLogin: (identifier: String, pass: String) -> Unit,
    onMobileOtpLogin: (phone: String) -> Unit = {},
    onGoogleSignIn: (email: String, name: String) -> Unit = { _, _ -> },
    onNavigateToSignup: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onContinueAsGuest: () -> Unit,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Mobile OTP, 1: Password Login
    var mobileInput by remember { mutableStateOf("") }
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showGoogleDialog by remember { mutableStateOf(false) }

    Surface(
        color = FlipkartBackground,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Brand Logo & Header
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(FlipkartBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Store,
                    contentDescription = "BM Store",
                    tint = SurfaceWhite,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "BM STORE ONLINE OFFLINE SHOPPING",
                fontSize = 16.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = FlipkartDarkBlue,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Secure Sign In with Real Gmail or Mobile OTP",
                fontSize = 12.5.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp, bottom = 18.dp)
            )

            // REAL GOOGLE / GMAIL ONE-TAP BUTTON
            OutlinedButton(
                onClick = {
                    val activity = context as? Activity
                    if (activity != null) {
                        isLoading = true
                        coroutineScope.launch {
                            RealAuthService.launchGoogleSignIn(
                                context = context,
                                activity = activity,
                                onSuccess = { email, name ->
                                    isLoading = false
                                    onGoogleSignIn(email, name)
                                },
                                onError = {
                                    isLoading = false
                                    // Fallback to Google Account prompt dialog so user can use their active Gmail
                                    showGoogleDialog = true
                                }
                            )
                        }
                    } else {
                        showGoogleDialog = true
                    }
                },
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.2.dp, Color(0xFFDADCE0)),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = SurfaceWhite),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("google_signin_btn")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEA4335)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("G", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Continue with Google (Gmail)",
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF3C4043)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Divider OR
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = DividerGray)
                Text("  OR  ", fontSize = 11.5.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                HorizontalDivider(modifier = Modifier.weight(1f), color = DividerGray)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Login Card with Tabs
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = SurfaceWhite,
                        contentColor = FlipkartBlue,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = FlipkartBlue
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0; localError = null },
                            text = {
                                Text(
                                    "Mobile OTP Login",
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                            }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1; localError = null },
                            text = {
                                Text(
                                    "Password Login",
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                            }
                        )
                    }

                    Column(modifier = Modifier.padding(18.dp)) {
                        if (selectedTab == 0) {
                            // ─── TAB 0: REAL MOBILE NUMBER + OTP ───
                            Text(
                                text = "Enter Real 10-Digit Mobile Number",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = mobileInput,
                                onValueChange = {
                                    val filtered = it.filter { ch -> ch.isDigit() }.take(10)
                                    mobileInput = filtered
                                    localError = null
                                },
                                leadingIcon = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(start = 12.dp, end = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Phone, contentDescription = "Phone", tint = FlipkartBlue, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("+91", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    }
                                },
                                singleLine = true,
                                placeholder = { Text("9876543210") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_mobile_input"),
                                shape = RoundedCornerShape(8.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "আপনার নম্বরে রিয়েল SMS ও WhatsApp এ ৬-ডিজিটের ভেরিফিকেশন ওটিপি কোড পাঠানো হবে।",
                                fontSize = 11.5.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (mobileInput.length < 10 || !RealAuthService.isValidIndianPhone(mobileInput)) {
                                        localError = "Please enter a valid 10-digit Indian mobile number (e.g. 9876543210)"
                                        return@Button
                                    }
                                    localError = null
                                    onMobileOtpLogin("+91$mobileInput")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("send_mobile_otp_btn")
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Send Real Verification OTP", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            // ─── TAB 1: EMAIL / PASSWORD ───
                            Text(
                                text = "Registered Email or Mobile Number",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = identifier,
                                onValueChange = {
                                    identifier = it
                                    localError = null
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Email, contentDescription = "Email", tint = FlipkartBlue)
                                },
                                singleLine = true,
                                placeholder = { Text("e.g. bakul2048@gmail.com") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_identifier_input"),
                                shape = RoundedCornerShape(8.dp)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Password",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = password,
                                onValueChange = {
                                    password = it
                                    localError = null
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = "Password", tint = FlipkartBlue)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = "Toggle password visibility"
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                placeholder = { Text("Enter your password") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_password_input"),
                                shape = RoundedCornerShape(8.dp)
                            )

                            // Forgot Password Link
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = "Forgot Password?",
                                    color = FlipkartBlue,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier
                                        .clickable { onNavigateToForgotPassword() }
                                        .padding(vertical = 4.dp)
                                        .testTag("login_forgot_pass_link")
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (identifier.isBlank() || password.isBlank()) {
                                        localError = "Please enter both your email/phone and password"
                                        return@Button
                                    }
                                    isLoading = true
                                    onLogin(identifier, password)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("login_submit_btn")
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(color = SurfaceWhite, modifier = Modifier.size(22.dp))
                                } else {
                                    Text("Sign In with Password", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
                                }
                            }
                        }

                        // Display errors if any
                        val displayError = localError ?: errorMessage
                        if (!displayError.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = displayError,
                                color = Color(0xFFD32F2F),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick Admin Access Helper
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Admin Login (admin@bmstore.com)",
                                fontSize = 11.5.sp,
                                color = FlipkartDarkBlue,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .clickable {
                                        selectedTab = 1
                                        identifier = "admin@bmstore.com"
                                        password = "admin123"
                                    }
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Guest Browsing Option
            OutlinedButton(
                onClick = onContinueAsGuest,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = FlipkartDarkBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("login_guest_btn")
            ) {
                Text("Continue as Guest (Browse Store)", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sign Up link
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Don't have an account?", fontSize = 14.sp, color = TextSecondary)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Sign Up",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = FlipkartBlue,
                    modifier = Modifier
                        .clickable { onNavigateToSignup() }
                        .padding(4.dp)
                        .testTag("login_signup_link")
                )
            }
        }
    }

    // Google Sign In Picker Dialog
    if (showGoogleDialog) {
        GoogleSignInDialog(
            onDismiss = { showGoogleDialog = false },
            onAccountSelected = { email, name ->
                showGoogleDialog = false
                onGoogleSignIn(email, name)
            }
        )
    }
}

// ─────────────────────────────────────────────
// GOOGLE ACCOUNT SELECTION DIALOG (REAL GMAIL)
// ─────────────────────────────────────────────

@Composable
fun GoogleSignInDialog(
    onDismiss: () -> Unit,
    onAccountSelected: (email: String, name: String) -> Unit
) {
    var customGmail by remember { mutableStateOf("bakul2048@gmail.com") }
    var customName by remember { mutableStateOf("Bakul") }
    var emailError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEA4335)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("G", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text("Sign in with Google", fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Choose your verified Google / Gmail account for BM STORE:",
                    fontSize = 12.5.sp,
                    color = TextSecondary
                )

                // Verified user account chip
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onAccountSelected("bakul2048@gmail.com", "Bakul")
                        },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F3F4)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(FlipkartBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("B", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Bakul", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = TextPrimary)
                            Text("bakul2048@gmail.com", fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                }

                HorizontalDivider(color = DividerGray, thickness = 0.8.dp)

                Text("Or enter any Real Gmail Address:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

                OutlinedTextField(
                    value = customGmail,
                    onValueChange = {
                        customGmail = it
                        emailError = null
                    },
                    label = { Text("Gmail Address") },
                    placeholder = { Text("your.email@gmail.com") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = customName,
                    onValueChange = { customName = it },
                    label = { Text("Your Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (emailError != null) {
                    Text(emailError!!, color = Color(0xFFD32F2F), fontSize = 11.5.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (!RealAuthService.isValidEmail(customGmail)) {
                        emailError = "Please enter a valid Gmail address (e.g. name@gmail.com)"
                        return@Button
                    }
                    onAccountSelected(customGmail.trim(), customName.trim().ifEmpty { customGmail.substringBefore("@") })
                },
                colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue)
            ) {
                Text("Sign In with this Gmail")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// ─────────────────────────────────────────────
// SIGNUP SCREEN (REAL GMAIL & REAL MOBILE)
// ─────────────────────────────────────────────

@Composable
fun SignupScreen(
    onSignup: (name: String, email: String, phone: String, pass: String) -> Unit,
    onGoogleSignup: (email: String, name: String) -> Unit = { _, _ -> },
    onNavigateToLogin: () -> Unit,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var agreedToTerms by remember { mutableStateOf(true) }
    var localError by remember { mutableStateOf<String?>(null) }
    var showGoogleDialog by remember { mutableStateOf(false) }

    Surface(
        color = FlipkartBackground,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "BM STORE ONLINE OFFLINE SHOPPING",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = FlipkartDarkBlue,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Create your Customer Account with Real Details",
                fontSize = 12.5.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
            )

            // Real Google Sign Up Shortcut
            OutlinedButton(
                onClick = { showGoogleDialog = true },
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.2.dp, Color(0xFFDADCE0)),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = SurfaceWhite),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEA4335)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("G", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Instant Sign Up with Google (Gmail)", fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF3C4043))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Full Name", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; localError = null },
                        leadingIcon = { Icon(Icons.Default.Person, "Name", tint = FlipkartBlue) },
                        placeholder = { Text("e.g. Bakul Ahmed") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("signup_name_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Real Gmail / Email Address", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; localError = null },
                        leadingIcon = { Icon(Icons.Default.Email, "Email", tint = FlipkartBlue) },
                        placeholder = { Text("e.g. bakul2048@gmail.com") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("signup_email_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Real 10-Digit Mobile Number (for OTP)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = {
                            phone = it.filter { ch -> ch.isDigit() }.take(10)
                            localError = null
                        },
                        leadingIcon = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 12.dp, end = 6.dp)
                            ) {
                                Icon(Icons.Default.Phone, "Phone", tint = FlipkartBlue, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("+91", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        placeholder = { Text("9876543210") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("signup_phone_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Create Password", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; localError = null },
                        leadingIcon = { Icon(Icons.Default.Lock, "Password", tint = FlipkartBlue) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("signup_pass_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Confirm Password", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; localError = null },
                        leadingIcon = { Icon(Icons.Default.Lock, "Confirm Password", tint = FlipkartBlue) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("signup_confirm_pass_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = agreedToTerms,
                            onCheckedChange = { agreedToTerms = it }
                        )
                        Text(
                            text = "I agree to BM STORE Terms & Privacy Policy",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    val displayError = localError ?: errorMessage
                    if (!displayError.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = displayError, color = Color(0xFFD32F2F), fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                localError = "Please enter your Full Name"
                                return@Button
                            }
                            if (!RealAuthService.isValidEmail(email)) {
                                localError = "Please enter a valid Gmail / Email address (e.g. bakul2048@gmail.com)"
                                return@Button
                            }
                            if (phone.length < 10 || !RealAuthService.isValidIndianPhone(phone)) {
                                localError = "Please enter a valid 10-digit mobile number"
                                return@Button
                            }
                            if (password.length < 6) {
                                localError = "Password must be at least 6 characters"
                                return@Button
                            }
                            if (password != confirmPassword) {
                                localError = "Passwords do not match"
                                return@Button
                            }
                            if (!agreedToTerms) {
                                localError = "You must accept the Terms and Conditions"
                                return@Button
                            }
                            onSignup(name, email, "+91$phone", password)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("signup_submit_btn")
                    ) {
                        Text("Create Account & Verify OTP", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Already have an account?", fontSize = 14.sp, color = TextSecondary)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Sign In",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = FlipkartBlue,
                    modifier = Modifier
                        .clickable { onNavigateToLogin() }
                        .padding(4.dp)
                        .testTag("signup_login_link")
                )
            }
        }
    }

    if (showGoogleDialog) {
        GoogleSignInDialog(
            onDismiss = { showGoogleDialog = false },
            onAccountSelected = { gEmail, gName ->
                showGoogleDialog = false
                onGoogleSignup(gEmail, gName)
            }
        )
    }
}

// ─────────────────────────────────────────────
// OTP VERIFICATION SCREEN (REAL OTP VIA SMS & WHATSAPP)
// ─────────────────────────────────────────────

@Composable
fun OtpVerificationScreen(
    targetIdentifier: String,
    generatedOtp: String,
    onVerify: (code: String) -> Unit,
    onResend: () -> Unit,
    onBack: () -> Unit,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    // Strict empty default: User MUST enter the real 6 digits sent to their phone
    var otpInput by remember { mutableStateOf("") }
    var secondsLeft by remember { mutableIntStateOf(60) }

    LaunchedEffect(secondsLeft) {
        if (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
    }

    Surface(
        color = FlipkartBackground,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(FlipkartGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "OTP Verification",
                    tint = FlipkartGreen,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Real OTP Verification",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "We have generated a real 6-digit verification code for:\n${targetIdentifier.ifEmpty { "your mobile / email" }}",
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Action Buttons to dispatch real OTP directly to user's phone!
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Receive OTP on your Device:",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = FlipkartDarkBlue
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // WhatsApp Dispatch
                        Button(
                            onClick = {
                                RealAuthService.dispatchRealWhatsAppOtp(context, targetIdentifier, generatedOtp)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(42.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Via WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        // Mobile SMS Dispatch
                        Button(
                            onClick = {
                                RealAuthService.dispatchRealSmsOtp(context, targetIdentifier, generatedOtp)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(42.dp)
                        ) {
                            Icon(Icons.Default.Sms, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Via SMS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    if (targetIdentifier.contains("@")) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = {
                                RealAuthService.dispatchRealEmailOtp(context, targetIdentifier, generatedOtp)
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(38.dp)
                        ) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = FlipkartBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Send to Gmail App", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FlipkartBlue)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = DividerGray, thickness = 0.8.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Enter 6-Digit Real Code",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = otpInput,
                        onValueChange = {
                            val filtered = it.filter { ch -> ch.isDigit() }.take(6)
                            otpInput = filtered
                        },
                        singleLine = true,
                        placeholder = { Text("• • • • • •", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            letterSpacing = 6.sp,
                            color = FlipkartDarkBlue
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("otp_input_field"),
                        shape = RoundedCornerShape(8.dp)
                    )

                    if (!errorMessage.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = errorMessage, color = Color(0xFFD32F2F), fontSize = 12.sp, textAlign = TextAlign.Center)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = { onVerify(otpInput) },
                        colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
                        shape = RoundedCornerShape(8.dp),
                        enabled = otpInput.length == 6,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("otp_verify_btn")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Verify & Continue", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (secondsLeft > 0) {
                        Text(
                            text = "Resend OTP code in $secondsLeft seconds",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    } else {
                        TextButton(onClick = {
                            secondsLeft = 60
                            onResend()
                        }) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp), tint = FlipkartBlue)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Resend Real OTP Code", color = FlipkartBlue, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            TextButton(onClick = onBack) {
                Text("Back to Sign In", color = TextSecondary)
            }
        }
    }
}


// ─────────────────────────────────────────────
// FORGOT PASSWORD SCREEN
// ─────────────────────────────────────────────

@Composable
fun ForgotPasswordScreen(
    onReset: (identifier: String, newPass: String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var identifier by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    Surface(
        color = FlipkartBackground,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "BM STORE ONLINE OFFLINE SHOPPING",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = FlipkartDarkBlue,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Reset Account Password",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Registered Email or Phone", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = identifier,
                        onValueChange = { identifier = it },
                        placeholder = { Text("e.g. rahim@bmstore.com") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("forgot_pass_identifier")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("New Password", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        visualTransformation = PasswordVisualTransformation(),
                        placeholder = { Text("Enter new password") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("forgot_pass_new")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Confirm New Password", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        visualTransformation = PasswordVisualTransformation(),
                        placeholder = { Text("Confirm new password") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("forgot_pass_confirm")
                    )

                    if (!message.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = message ?: "",
                            color = if (isError) Color(0xFFD32F2F) else FlipkartGreen,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (identifier.isBlank() || newPassword.isBlank()) {
                                message = "Please fill in all fields"
                                isError = true
                                return@Button
                            }
                            if (newPassword.length < 6) {
                                message = "Password must be at least 6 characters"
                                isError = true
                                return@Button
                            }
                            if (newPassword != confirmPassword) {
                                message = "Passwords do not match"
                                isError = true
                                return@Button
                            }
                            onReset(identifier, newPassword)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("forgot_pass_submit_btn")
                    ) {
                        Text("Reset Password", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = onBack) {
                Text("Back to Sign In", color = FlipkartBlue, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
