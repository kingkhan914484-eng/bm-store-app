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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AddressEntity
import com.example.ui.theme.BorderLight
import com.example.ui.theme.DividerGray
import com.example.ui.theme.FlipkartBackground
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartDarkBlue
import com.example.ui.theme.FlipkartGreen
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressManagementScreen(
    addresses: List<AddressEntity>,
    selectedAddressId: String?,
    onSelectAddress: (AddressEntity) -> Unit,
    onSaveAddress: (
        fullName: String,
        phone: String,
        pincode: String,
        house: String,
        city: String,
        state: String,
        landmark: String,
        type: String,
        isDefault: Boolean
    ) -> Unit,
    onDeleteAddress: (String) -> Unit,
    onSetDefaultAddress: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddSheet by remember { mutableStateOf(false) }

    Surface(
        color = FlipkartBackground,
        modifier = modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Surface(
                color = FlipkartBlue,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = SurfaceWhite
                        )
                    }
                    Column {
                        Text(
                            text = "My Delivery Addresses",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = SurfaceWhite
                        )
                        Text(
                            text = "BM STORE ONLINE OFFLINE SHOPPING",
                            fontSize = 11.sp,
                            color = SurfaceWhite.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // Add Address Button Bar
            Surface(
                color = SurfaceWhite,
                shadowElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showAddSheet = true }
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Address",
                        tint = FlipkartBlue
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "+ Add a new delivery address",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = FlipkartBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Address List
            if (addresses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "No Address",
                            tint = BorderLight,
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No saved addresses found",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Add a delivery address to enjoy fast checkout.",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showAddSheet = true },
                            colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue)
                        ) {
                            Text("Add Address Now")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(addresses, key = { it.id }) { address ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = address.fullName,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(FlipkartBackground)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = address.addressType.uppercase(),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = FlipkartDarkBlue
                                            )
                                        }
                                        if (address.isDefault) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(FlipkartGreen.copy(alpha = 0.15f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "DEFAULT",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = FlipkartGreen
                                                )
                                            }
                                        }
                                    }

                                    IconButton(
                                        onClick = { onDeleteAddress(address.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = TextSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "${address.houseDetails}, ${address.city}, ${address.state} - ${address.pincode}",
                                    fontSize = 13.5.sp,
                                    color = TextSecondary,
                                    lineHeight = 18.sp
                                )

                                if (address.landmark.isNotBlank()) {
                                    Text(
                                        text = "Landmark: ${address.landmark}",
                                        fontSize = 12.5.sp,
                                        color = TextSecondary
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Phone: ${address.phone}",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )

                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = DividerGray)
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (!address.isDefault) {
                                        Text(
                                            text = "Set as Default",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = FlipkartBlue,
                                            modifier = Modifier
                                                .clickable { onSetDefaultAddress(address.id) }
                                                .padding(4.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "Primary Delivery Address",
                                            fontSize = 12.sp,
                                            color = FlipkartGreen,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Button(
                                        onClick = { onSelectAddress(address) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (selectedAddressId == address.id) FlipkartGreen else FlipkartBlue
                                        ),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = if (selectedAddressId == address.id) "Selected" else "Deliver Here",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add Address Modal Sheet
        if (showAddSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAddSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                AddAddressForm(
                    onSave = { fullName, phone, pincode, house, city, state, landmark, type, isDefault ->
                        onSaveAddress(fullName, phone, pincode, house, city, state, landmark, type, isDefault)
                        showAddSheet = false
                    },
                    onCancel = { showAddSheet = false }
                )
            }
        }
    }
}

@Composable
fun AddAddressForm(
    onSave: (fullName: String, phone: String, pincode: String, house: String, city: String, state: String, landmark: String, type: String, isDefault: Boolean) -> Unit,
    onCancel: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var pincode by remember { mutableStateOf("") }
    var house by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("West Bengal") }
    var landmark by remember { mutableStateOf("") }
    var addressType by remember { mutableStateOf("Home") }
    var isDefault by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Add New Delivery Address",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it; errorMessage = null },
            label = { Text("Full Name *") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("addr_fullname_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it; errorMessage = null },
            label = { Text("Mobile Number (10 digits) *") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("addr_phone_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = pincode,
                onValueChange = { pincode = it; errorMessage = null },
                label = { Text("Pincode (6 digits) *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f).testTag("addr_pincode_input")
            )
            Spacer(modifier = Modifier.width(10.dp))
            OutlinedTextField(
                value = city,
                onValueChange = { city = it; errorMessage = null },
                label = { Text("City *") },
                singleLine = true,
                modifier = Modifier.weight(1f).testTag("addr_city_input")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = house,
            onValueChange = { house = it; errorMessage = null },
            label = { Text("House No., Building Name, Street *") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("addr_house_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = landmark,
            onValueChange = { landmark = it },
            label = { Text("Landmark (Optional)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("addr_landmark_input")
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text("Address Type", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Row(modifier = Modifier.padding(top = 4.dp)) {
            FilterChip(
                selected = addressType == "Home",
                onClick = { addressType = "Home" },
                label = { Text("Home") },
                leadingIcon = { Icon(Icons.Default.Home, null, modifier = Modifier.size(16.dp)) }
            )
            Spacer(modifier = Modifier.width(10.dp))
            FilterChip(
                selected = addressType == "Work",
                onClick = { addressType = "Work" },
                label = { Text("Work / Office") },
                leadingIcon = { Icon(Icons.Default.Work, null, modifier = Modifier.size(16.dp)) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isDefault, onCheckedChange = { isDefault = it })
            Text("Make this my default delivery address", fontSize = 13.sp)
        }

        if (!errorMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMessage ?: "", color = Color(0xFFD32F2F), fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (fullName.isBlank() || phone.isBlank() || pincode.isBlank() || house.isBlank() || city.isBlank()) {
                    errorMessage = "Please fill in all mandatory fields (*)"
                    return@Button
                }
                if (phone.length < 10) {
                    errorMessage = "Please enter a valid 10-digit mobile number"
                    return@Button
                }
                if (pincode.length < 6) {
                    errorMessage = "Please enter a valid 6-digit pincode"
                    return@Button
                }
                onSave(fullName, phone, pincode, house, city, state, landmark, addressType, isDefault)
            },
            colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("addr_save_btn")
        ) {
            Text("Save Address", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
