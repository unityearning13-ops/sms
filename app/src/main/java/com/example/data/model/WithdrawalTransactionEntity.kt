package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class WithdrawalTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val method: String,
    val accountNumber: String,
    val amount: Double,
    val fee: Double = 0.0,
    val trxId: String,
    val ref: String = "Paid By_Unity Earning E-Learning Platform",
    val timestamp: Long,
    val formattedDateTime: String,
    val status: String = "সফল (Successful)"
)
