package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.model.ConversationItem
import com.example.data.model.SmsEntity
import com.example.data.model.WalletStateEntity
import com.example.data.model.WithdrawalTransactionEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppRepository(context: Context) {
    private val dao = AppDatabase.getDatabase(context).appDao()

    val walletState: Flow<WalletStateEntity?> = dao.getWalletFlow()
    val allTransactions: Flow<List<WithdrawalTransactionEntity>> = dao.getAllTransactions()

    fun getMessagesForThread(threadId: String): Flow<List<SmsEntity>> {
        return dao.getMessagesForThread(threadId)
    }

    fun getLatestMessageForThread(threadId: String): Flow<SmsEntity?> {
        return dao.getLatestMessageForThread(threadId)
    }

    suspend fun processWithdrawal(method: String, accountNumber: String, amount: Double): WithdrawalTransactionEntity {
        val wallet = dao.getWalletSync() ?: WalletStateEntity()
        val newCurrentBalance = amount + 50.75
        val updatedWallet = wallet.copy(
            currentBalance = newCurrentBalance,
            lastWithdrawalTimestamp = System.currentTimeMillis()
        )
        dao.updateWallet(updatedWallet)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val trxId = (1..10).map { chars.random() }.joinToString("")
        val formattedDate = dateFormat.format(Date())

        val transaction = WithdrawalTransactionEntity(
            method = method,
            accountNumber = accountNumber,
            amount = amount,
            trxId = trxId,
            timestamp = System.currentTimeMillis(),
            formattedDateTime = formattedDate
        )
        dao.insertTransaction(transaction)

        val smsBody = "You have received Tk %.2f from 01339786909. Ref Paid By_Unity Earning E-Learning Platform. Fee Tk 0.00. Balance Tk %.2f. TrxID %s at %s"
            .format(Locale.US, amount, newCurrentBalance, trxId, formattedDate)

        val sms = SmsEntity(
            threadId = "bkash",
            senderName = "bKash",
            body = smsBody,
            timestamp = System.currentTimeMillis(),
            formattedDateTime = formattedDate,
            amount = amount,
            trxId = trxId,
            isReceived = true
        )
        dao.insertMessage(sms)

        return transaction
    }

    suspend fun updateMessage(message: SmsEntity) {
        dao.updateMessage(message)
    }

    suspend fun deleteMessage(message: SmsEntity) {
        dao.deleteMessage(message)
    }

    suspend fun checkAndAutoRestoreBalance() {
        val wallet = dao.getWalletSync() ?: return
        if (wallet.lastWithdrawalTimestamp > 0L) {
            val elapsed = System.currentTimeMillis() - wallet.lastWithdrawalTimestamp
            val restoreDurationMs = wallet.autoRestoreMinutes * 60 * 1000L
            if (elapsed >= restoreDurationMs) {
                dao.updateWallet(
                    wallet.copy(
                        currentBalance = wallet.originalBalance,
                        lastWithdrawalTimestamp = 0L
                    )
                )
            }
        }
    }

    suspend fun forceRestoreBalance() {
        val wallet = dao.getWalletSync() ?: return
        dao.updateWallet(
            wallet.copy(
                currentBalance = wallet.originalBalance,
                lastWithdrawalTimestamp = 0L
            )
        )
    }

    suspend fun setCustomOriginalBalance(newBalance: Double) {
        val wallet = dao.getWalletSync() ?: WalletStateEntity()
        dao.updateWallet(
            wallet.copy(
                originalBalance = newBalance,
                currentBalance = newBalance,
                lastWithdrawalTimestamp = 0L
            )
        )
    }

    suspend fun generateSampleTransactions() {
        val methods = listOf("bKash", "Nagad", "Rocket", "Upay", "Binance")
        val numbers = listOf("01712345678", "01898765432", "01911223344", "01655443322")
        val amounts = listOf(500.0, 1000.0, 1500.0, 2000.0, 3000.0)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        for (i in 1..4) {
            val method = methods.random()
            val number = numbers.random()
            val amount = amounts.random()
            val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
            val trxId = (1..10).map { chars.random() }.joinToString("")
            val time = System.currentTimeMillis() - (i * 3600000L * 12)
            val formattedDate = dateFormat.format(Date(time))

            dao.insertTransaction(
                WithdrawalTransactionEntity(
                    method = method,
                    accountNumber = number,
                    amount = amount,
                    trxId = trxId,
                    timestamp = time,
                    formattedDateTime = formattedDate,
                    status = "সফল (Successful)"
                )
            )
        }
    }

    suspend fun clearTransactions() {
        dao.clearTransactions()
    }

    fun getStaticConversationList(): List<ConversationItem> {
        return listOf(
            ConversationItem(
                id = "bkash",
                senderName = "bKash",
                lastMessage = "You have received Tk 1,000.00 from 01339786909...",
                timestampText = "Thu",
                avatarBgColorHex = 0xFFFFC107,
                isPinned = true,
                unreadCount = 0,
                isVerified = true
            ),
            ConversationItem(
                id = "nagad",
                senderName = "NAGAD",
                lastMessage = "Money Received....",
                timestampText = "Thu",
                avatarBgColorHex = 0xFF4CAF50,
                isPinned = true,
                unreadCount = 0,
                isVerified = true
            ),
            ConversationItem(
                id = "16216",
                senderName = "16216",
                lastMessage = "Tk 50.00 transferred to bKash A/C: ***90...",
                timestampText = "Thu",
                avatarBgColorHex = 0xFF9C27B0,
                isPinned = false,
                unreadCount = 0
            ),
            ConversationItem(
                id = "gp_170min",
                senderName = "GP 170MIN",
                lastMessage = "আজকের অফার ১৭০মিনিট ১২৫টাকা ১৫দিন, ডায়াল *১২১*৫...",
                timestampText = "Thu",
                avatarBgColorHex = 0xFF009688,
                isPinned = false,
                unreadCount = 10
            ),
            ConversationItem(
                id = "gp_1gb_100min",
                senderName = "GP1GB100Min",
                lastMessage = "১GB + ১০০ মিনিট ৭ দিন ১০৮ টাকায়! ডায়াল *১২১*...",
                timestampText = "Thu",
                avatarBgColorHex = 0xFF00BCD4,
                isPinned = false,
                unreadCount = 1
            ),
            ConversationItem(
                id = "gp_bkash",
                senderName = "GP bKash",
                lastMessage = "আজ দুপুর ২টা-সন্ধ্যা ৬টা, বিকাশ থেকে জিপি নাম্বারে ১০৮ টাকা...",
                timestampText = "Thu",
                avatarBgColorHex = 0xFFFF9800,
                isPinned = false,
                unreadCount = 2
            ),
            ConversationItem(
                id = "gp_30gb_210tk",
                senderName = "GP30GB210TK",
                lastMessage = "আজকের অফার ৩০জিবি ২১০টাকা ৭দিন। ডায়াল *১২১*৫২১০#",
                timestampText = "Thu",
                avatarBgColorHex = 0xFFE91E63,
                isPinned = false,
                unreadCount = 16
            ),
            ConversationItem(
                id = "gp_2_5gb",
                senderName = "GP 2.5GB",
                lastMessage = "আজকের অফার! ২.৫জিবি ৩দিন ৫৭টাকা। ডায়াল *১২১*৫৮৬#",
                timestampText = "Thu",
                avatarBgColorHex = 0xFF795548,
                isPinned = false,
                unreadCount = 1
            ),
            ConversationItem(
                id = "citytouch",
                senderName = "CITYTOUCH",
                lastMessage = "সিটিটাচ থেকে যেকোনো নম্বরে পছন্দের মোবাইল অপারেটরে...",
                timestampText = "Thu",
                avatarBgColorHex = 0xFFF44336,
                isPinned = false,
                unreadCount = 0
            )
        )
    }
}
