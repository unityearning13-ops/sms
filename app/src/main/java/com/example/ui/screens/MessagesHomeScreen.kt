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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConversationItem
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesHomeScreen(
    viewModel: MainViewModel,
    onOpenBkashChat: () -> Unit,
    onOpenPortal: () -> Unit,
    onOpenWebPortal: () -> Unit
) {
    val conversations by viewModel.conversations.collectAsState()
    var showMenu by remember { mutableStateOf(false) }
    var showBalanceDialog by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

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
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { showMenu = true }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Messages",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { snackbarMessage = "Searching conversations..." }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Box {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF4285F4),
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { showMenu = true }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "U",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Unity Earning Withdrawal Portal") },
                                onClick = {
                                    showMenu = false
                                    onOpenPortal()
                                },
                                leadingIcon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color(0xFFE91E63)) }
                            )
                            DropdownMenuItem(
                                text = { Text("Open in Browser Portal") },
                                onClick = {
                                    showMenu = false
                                    onOpenWebPortal()
                                },
                                leadingIcon = { Icon(Icons.Default.Public, contentDescription = null, tint = Color(0xFF4285F4)) }
                            )
                            DropdownMenuItem(
                                text = { Text("Copy Withdrawal Link") },
                                onClick = {
                                    showMenu = false
                                    clipboardManager.setText(AnnotatedString("https://unityearning-learning-platform.com/withdraw"))
                                    snackbarMessage = "উইথড্রয়াল পোর্টাল লিংক কপি করা হয়েছে!"
                                },
                                leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Set Custom Balance") },
                                onClick = {
                                    showMenu = false
                                    showBalanceDialog = true
                                },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FloatingActionButton(
                    onClick = { onOpenPortal() },
                    containerColor = Color(0xFF2C3038),
                    contentColor = Color(0xFF8AB4F8),
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "Portal AI")
                }

                ExtendedFloatingActionButton(
                    onClick = { onOpenPortal() },
                    icon = { Icon(Icons.Default.Chat, contentDescription = null, tint = Color(0xFF1E1F22)) },
                    text = { Text("Start chat", color = Color(0xFF1E1F22), fontWeight = FontWeight.Bold) },
                    containerColor = Color(0xFFC2E7FF),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        },
        containerColor = Color(0xFF121316)
    ) { paddingVals ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingVals)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Conversations",
                        color = Color(0xFF9AA0A6),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                items(conversations) { item ->
                    ConversationRow(item = item, onClick = {
                        if (item.id == "bkash") {
                            onOpenBkashChat()
                        } else {
                            onOpenPortal()
                        }
                    })
                }
            }

            Surface(
                color = Color(0xFF282A2D),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = null,
                        tint = Color(0xFF8AB4F8),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Pinned 2 of 20 conversations",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    if (showBalanceDialog) {
        var inputVal by remember { mutableStateOf("1450.00") }
        AlertDialog(
            onDismissRequest = { showBalanceDialog = false },
            title = { Text("Configure Starting Balance") },
            text = {
                Column {
                    Text("Enter your desired starting balance amount in Taka:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inputVal,
                        onValueChange = { inputVal = it },
                        singleLine = true,
                        label = { Text("Balance (৳)") }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val amt = inputVal.toDoubleOrNull() ?: 1450.00
                    viewModel.setCustomBalance(amt)
                    showBalanceDialog = false
                    snackbarMessage = "Balance updated to ৳$amt successfully!"
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBalanceDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ConversationRow(item: ConversationItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color(item.avatarBgColorHex)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color(0xFF202124), // dark person silhouette icon
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.senderName,
                    color = if (item.unreadCount > 0) Color.White else Color(0xFFBDC1C6),
                    fontSize = 16.sp,
                    fontWeight = if (item.unreadCount > 0) FontWeight.Bold else FontWeight.SemiBold,
                    modifier = Modifier.weight(1f, fill = false)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = item.lastMessage,
                color = if (item.unreadCount > 0) Color.White else Color(0xFF9AA0A6),
                fontSize = 14.sp,
                fontWeight = if (item.unreadCount > 0) FontWeight.Medium else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = item.timestampText,
                color = Color(0xFF9AA0A6),
                fontSize = 12.sp
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (item.isPinned) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = "Pinned",
                        tint = Color(0xFF8AB4F8),
                        modifier = Modifier.size(14.dp)
                    )
                }
                if (item.unreadCount > 0) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF8AB4F8),
                        modifier = Modifier.size(20.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${item.unreadCount}",
                                color = Color(0xFF1E1F22),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
