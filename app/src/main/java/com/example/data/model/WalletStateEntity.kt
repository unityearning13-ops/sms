package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallet")
data class WalletStateEntity(
    @PrimaryKey val id: Int = 1,
    val originalBalance: Double = 3400.00,
    val currentBalance: Double = 3400.00,
    val lastWithdrawalTimestamp: Long = 0L,
    val autoRestoreMinutes: Int = 5
)
