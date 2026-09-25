package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WithdrawalTransactionEntity
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebPortalScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNavigateToBkash: () -> Unit
) {
    val wallet by viewModel.walletState.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedMethod by remember { mutableStateOf("bKash") }
    var expandedMethodMenu by remember { mutableStateOf(false) }
    var accountNumber by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var lastCreatedTrx by remember { mutableStateOf<WithdrawalTransactionEntity?>(null) }

    val methods = listOf(
        Triple("bKash", Color(0xFFE2136E), "বিকাশ"),
        Triple("Nagad", Color(0xFFF15A24), "নগদ"),
        Triple("Rocket", Color(0xFF8C3189), "রকেট"),
        Triple("Upay", Color(0xFF00A699), "উপায়"),
        Triple("mCash", Color(0xFF0056B3), "এমক্যাশ"),
        Triple("Bank", Color(0xFF107C41), "ব্যাংক"),
        Triple("Paytm", Color(0xFF00BAF2), "পেটিএম"),
        Triple("Google Pay", Color(0xFF4285F4), "গুগল পে")
    )

    var timeRemainingSec by remember { mutableStateOf(0L) }
    LaunchedEffect(wallet?.lastWithdrawalTimestamp) {
        val lastTs = wallet?.lastWithdrawalTimestamp ?: 0L
        if (lastTs > 0L) {
            while (true) {
                val elapsed = System.currentTimeMillis() - lastTs
                val totalDuration = (wallet?.autoRestoreMinutes ?: 5) * 60 * 1000L
                val remaining = totalDuration - elapsed
                if (remaining <= 0) {
                    timeRemainingSec = 0
                    break
                } else {
                    timeRemainingSec = remaining / 1000
                }
                delay(1000L)
            }
        } else {
            timeRemainingSec = 0
        }
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            snackbarMessage = null
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Surface(
                color = Color(0xFF35363A),
                tonalElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }

                        Surface(
                            color = Color(0xFF202124),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF8AB4F8), modifier = Modifier.size(14.dp))
                                Text(
                                    text = "unityearning-learning-platform.com/withdraw",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString("https://unityearning-learning-platform.com/withdraw"))
                            snackbarMessage = "Link copied!"
                        }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy Link", tint = Color.White)
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFFF2F4F8)
    ) { paddingVals ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingVals)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 20.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Unity Earning Learning Platform", color = Color(0xFF1E1F22), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                val balance = wallet?.currentBalance ?: 1450.0
                                Text(
                                    text = "Balance: ৳ %,.2f".format(java.util.Locale.US, balance),
                                    color = Color(0xFF1A73E8),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Withdrawal Request Form",
                            color = Color(0xFF1E1F22),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        // Please Select Dropdown
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(text = "Payment Method", color = Color(0xFF5F6368), fontSize = 13.sp)

                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedButton(
                                    onClick = { expandedMethodMenu = true },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFF8F9FA)),
                                    border = BorderStroke(1.dp, Color(0xFFDADCE0)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val currentMethodObj = methods.firstOrNull { it.first == selectedMethod }
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(currentMethodObj?.second ?: Color.Gray),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                            }
                                            Text(
                                                text = "${currentMethodObj?.first ?: "Please Select"} (${currentMethodObj?.third ?: ""})",
                                                color = Color(0xFF202124),
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 15.sp
                                            )
                                        }
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(0xFF5F6368))
                                    }
                                }

                                DropdownMenu(
                                    expanded = expandedMethodMenu,
                                    onDismissRequest = { expandedMethodMenu = false },
                                    modifier = Modifier.fillMaxWidth(0.85f)
                                ) {
                                    methods.forEach { (engName, colorTag, bngName) ->
                                        DropdownMenuItem(
                                            text = {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(28.dp)
                                                            .clip(CircleShape)
                                                            .background(colorTag),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(text = engName.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                    }
                                                    Column {
                                                        Text(text = engName, fontWeight = FontWeight.Bold, color = Color(0xFF202124), fontSize = 15.sp)
                                                        Text(text = bngName, color = Color(0xFF5F6368), fontSize = 12.sp)
                                                    }
                                                }
                                            },
                                            onClick = {
                                                selectedMethod = engName
                                                expandedMethodMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(text = "$selectedMethod Account / Method Number", color = Color(0xFF5F6368), fontSize = 13.sp)
                            OutlinedTextField(
                                value = accountNumber,
                                onValueChange = { accountNumber = it },
                                placeholder = { Text("e.g. 01712345678", color = Color(0xFF9AA0A6)) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF1A73E8),
                                    unfocusedBorderColor = Color(0xFFDADCE0),
                                    focusedTextColor = Color(0xFF202124),
                                    unfocusedTextColor = Color(0xFF202124),
                                    cursorColor = Color(0xFF1A73E8)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(text = "Withdrawal Amount (BDT)", color = Color(0xFF5F6368), fontSize = 13.sp)
                            OutlinedTextField(
                                value = amountInput,
                                onValueChange = { amountInput = it },
                                placeholder = { Text("Enter amount (e.g. 1000)", color = Color(0xFF9AA0A6)) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF1A73E8),
                                    unfocusedBorderColor = Color(0xFFDADCE0),
                                    focusedTextColor = Color(0xFF202124),
                                    unfocusedTextColor = Color(0xFF202124),
                                    cursorColor = Color(0xFF1A73E8)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                if (accountNumber.isBlank()) {
                                    snackbarMessage = "দয়া করে আপনার $selectedMethod অ্যাকাউন্ট নম্বর লিখুন!"
                                    return@Button
                                }
                                val amt = amountInput.toDoubleOrNull()
                                if (amt == null || amt <= 0) {
                                    snackbarMessage = "দয়া করে সঠিক উইথড্র অ্যামাউন্ট লিখুন!"
                                    return@Button
                                }
                                viewModel.submitWithdrawal(selectedMethod, accountNumber, amt) { trx ->
                                    lastCreatedTrx = trx
                                    showSuccessDialog = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            Text(
                                text = "Create Request (উইথড্র রিকোয়েস্ট)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSuccessDialog && lastCreatedTrx != null) {
        val trx = lastCreatedTrx!!
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            containerColor = Color.White,
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF34A853)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "পেমেন্ট সফলভাবে রিসিভ হয়েছে!",
                        color = Color(0xFF202124),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "আপনার উইথড্র রিকোয়েস্ট সফলভাবে সিস্টেমে পাঠানো হয়েছে এবং অফলাইন এসএমএস ইনবক্সে যুক্ত হয়েছে।",
                        color = Color(0xFF5F6368),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = Color(0xFFF8F9FA),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            PortalDetailRow("Method:", trx.method)
                            PortalDetailRow("Account:", trx.accountNumber)
                            PortalDetailRow("Amount:", "৳ %,.2f".format(java.util.Locale.US, trx.amount))
                            PortalDetailRow("TrxID:", trx.trxId)
                            PortalDetailRow("Time:", trx.formattedDateTime)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onNavigateToBkash()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "ঠিক আছে (OK)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        )
    }
}
