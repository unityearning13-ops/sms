package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.SmsEntity
import com.example.data.model.WithdrawalTransactionEntity
import com.example.data.model.WalletStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT * FROM messages WHERE threadId = :threadId ORDER BY timestamp ASC")
    fun getMessagesForThread(threadId: String): Flow<List<SmsEntity>>

    @Query("SELECT * FROM messages WHERE threadId = :threadId ORDER BY timestamp DESC LIMIT 1")
    fun getLatestMessageForThread(threadId: String): Flow<SmsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: SmsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMessage(message: SmsEntity)

    @androidx.room.Delete
    suspend fun deleteMessage(message: SmsEntity)

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<WithdrawalTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: WithdrawalTransactionEntity)

    @Query("DELETE FROM transactions")
    suspend fun clearTransactions()

    @Query("SELECT * FROM wallet WHERE id = 1")
    fun getWalletFlow(): Flow<WalletStateEntity?>

    @Query("SELECT * FROM wallet WHERE id = 1")
    suspend fun getWalletSync(): WalletStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateWallet(wallet: WalletStateEntity)
}
