package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SmsEntity
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BkashConversationScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val messages by viewModel.bkashMessages.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    var showMenu by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingMessage by remember { mutableStateOf<SmsEntity?>(null) }
    var editBodyInput by remember { mutableStateOf("") }
    var editDateInput by remember { mutableStateOf("") }

    var newBodyInput by remember { mutableStateOf("You have received Tk 500.00 from 01712345678. Ref Paid By_Unity Earning E-Learning Platform. Fee Tk 0.00. Balance Tk 2,000.00. TrxID DIP9X12YQZ at 25/09/2026 12:30") }

    LaunchedEffect(Unit) {
        viewModel.markConversationRead("bkash")
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
                color = Color(0xFF1E1F22),
                tonalElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFC107)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color(0xFF202124),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "bKash",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "16247 • Verified Sender",
                            color = Color(0xFF9AA0A6),
                            fontSize = 12.sp
                        )
                    }

                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Color.White)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("নতুন এসএমএস যোগ করুন (Add SMS)") },
                                onClick = {
                                    showMenu = false
                                    showAddDialog = true
                                },
                                leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) }
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = Color(0xFF1E1F22),
                tonalElevation = 4.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Can't reply to this conversation",
                        color = Color(0xFF9AA0A6),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        containerColor = Color(0xFF121316)
    ) { paddingVals ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingVals)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        color = Color(0xFF282A2D),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF8AB4F8), modifier = Modifier.size(16.dp))
                            Text(
                                text = "Messages from bKash are verified & secure",
                                color = Color(0xFF8AB4F8),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            items(messages) { msg ->
                var msgMenuExpanded by remember { mutableStateOf(false) }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Surface(
                        color = Color(0xFF282A2D),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                clipboardManager.setText(AnnotatedString(msg.body))
                                snackbarMessage = "Message text copied!"
                            }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Text(
                                text = msg.body,
                                color = Color.White,
                                fontSize = 16.sp,
                                lineHeight = 24.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .align(Alignment.End)
                            ) {
                                Text(
                                    text = msg.formattedDateTime,
                                    color = Color(0xFF9AA0A6),
                                    fontSize = 12.sp,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .clickable { msgMenuExpanded = true }
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                                DropdownMenu(
                                    expanded = msgMenuExpanded,
                                    onDismissRequest = { msgMenuExpanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("এডিট করুন (Edit)") },
                                        onClick = {
                                            msgMenuExpanded = false
                                            editingMessage = msg
                                            editBodyInput = msg.body
                                            editDateInput = msg.formattedDateTime
                                        },
                                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("ডিলিট করুন (Delete)", color = Color.Red) },
                                        onClick = {
                                            msgMenuExpanded = false
                                            viewModel.deleteMessage(msg)
                                            snackbarMessage = "Message deleted"
                                        },
                                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Message Dialog
    if (editingMessage != null) {
        AlertDialog(
            onDismissRequest = { editingMessage = null },
            title = { Text("এসএমএস এডিট করুন (Edit SMS)") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = editBodyInput,
                        onValueChange = { editBodyInput = it },
                        label = { Text("Message Body") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editDateInput,
                        onValueChange = { editDateInput = it },
                        label = { Text("Date & Time") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updated = editingMessage!!.copy(
                            body = editBodyInput,
                            formattedDateTime = editDateInput
                        )
                        viewModel.updateMessage(updated)
                        editingMessage = null
                        snackbarMessage = "Message updated successfully!"
                    }
                ) {
                    Text("সংরক্ষণ করুন (Save)")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingMessage = null }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // Add Custom Message Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("নতুন এসএমএস যোগ করুন") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = newBodyInput,
                        onValueChange = { newBodyInput = it },
                        label = { Text("Message Body") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        val nowStr = dateFormat.format(Date())
                        val newSms = SmsEntity(
                            threadId = "bkash",
                            senderName = "bKash",
                            body = newBodyInput,
                            timestamp = System.currentTimeMillis(),
                            formattedDateTime = nowStr,
                            isReceived = true
                        )
                        // Insert by updating/inserting
                        viewModel.updateMessage(newSms) // or insert
                        showAddDialog = false
                        snackbarMessage = "New message added!"
                    }
                ) {
                    Text("যোগ করুন (Add)")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}
