package com.example.data.model

data class ConversationItem(
    val id: String,
    val senderName: String,
    val lastMessage: String,
    val timestampText: String,
    val avatarBgColorHex: Long,
    val avatarText: String? = null,
    val isPinned: Boolean = false,
    val unreadCount: Int = 0,
    val isVerified: Boolean = false
)
