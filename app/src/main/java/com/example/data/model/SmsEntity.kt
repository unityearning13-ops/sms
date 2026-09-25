package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class SmsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val threadId: String,
    val senderName: String,
    val body: String,
    val timestamp: Long,
    val formattedDateTime: String,
    val amount: Double? = null,
    val trxId: String? = null,
    val isReceived: Boolean = true
)
