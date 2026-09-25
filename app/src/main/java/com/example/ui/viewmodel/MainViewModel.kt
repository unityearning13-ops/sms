package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ConversationItem
import com.example.data.model.SmsEntity
import com.example.data.model.WalletStateEntity
import com.example.data.model.WithdrawalTransactionEntity
import com.example.data.repository.AppRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppRepository(application)

    val walletState: StateFlow<WalletStateEntity?> = repository.walletState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WalletStateEntity())

    val allTransactions: StateFlow<List<WithdrawalTransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bkashMessages: StateFlow<List<SmsEntity>> = repository.getMessagesForThread("bkash")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val latestBkashMessage: StateFlow<SmsEntity?> = repository.getLatestMessageForThread("bkash")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _conversations = MutableStateFlow<List<ConversationItem>>(emptyList())
    val conversations: StateFlow<List<ConversationItem>> = _conversations.asStateFlow()

    private val _lastCreatedTransaction = MutableStateFlow<WithdrawalTransactionEntity?>(null)
    val lastCreatedTransaction: StateFlow<WithdrawalTransactionEntity?> = _lastCreatedTransaction.asStateFlow()

    private val _isWithdrawalSuccessPopupVisible = MutableStateFlow(false)
    val isWithdrawalSuccessPopupVisible: StateFlow<Boolean> = _isWithdrawalSuccessPopupVisible.asStateFlow()

    init {
        loadConversations()
        startPeriodicAutoRestoreCheck()
    }

    private fun loadConversations() {
        _conversations.value = repository.getStaticConversationList()
    }

    private fun startPeriodicAutoRestoreCheck() {
        viewModelScope.launch {
            while (true) {
                repository.checkAndAutoRestoreBalance()
                delay(1000)
            }
        }
    }

    fun submitWithdrawal(
        method: String,
        accountNumber: String,
        amount: Double,
        onSuccess: (WithdrawalTransactionEntity) -> Unit
    ) {
        viewModelScope.launch {
            val transaction = repository.processWithdrawal(method, accountNumber, amount)
            _lastCreatedTransaction.value = transaction
            _isWithdrawalSuccessPopupVisible.value = true

            val currentList = _conversations.value.toMutableList()
            val index = currentList.indexOfFirst { it.id == "bkash" }
            if (index != -1) {
                val item = currentList[index]
                currentList[index] = item.copy(
                    lastMessage = "You have received Tk %.2f from %s. Ref Paid By_Unity...".format(java.util.Locale.US, transaction.amount, transaction.accountNumber),
                    timestampText = "Just now",
                    unreadCount = item.unreadCount + 1
                )
                _conversations.value = currentList
            }

            onSuccess(transaction)
        }
    }

    fun markConversationRead(id: String) {
        val currentList = _conversations.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            val item = currentList[index]
            if (item.unreadCount > 0) {
                currentList[index] = item.copy(unreadCount = 0)
                _conversations.value = currentList
            }
        }
    }

    fun dismissSuccessPopup() {
        _isWithdrawalSuccessPopupVisible.value = false
    }

    fun setCustomBalance(newBalance: Double) {
        viewModelScope.launch {
            repository.setCustomOriginalBalance(newBalance)
        }
    }

    fun restoreBalanceNow() {
        viewModelScope.launch {
            repository.forceRestoreBalance()
        }
    }

    fun generateSampleHistory() {
        viewModelScope.launch {
            repository.generateSampleTransactions()
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearTransactions()
        }
    }

    fun updateMessage(message: SmsEntity) {
        viewModelScope.launch {
            repository.updateMessage(message)
        }
    }

    fun deleteMessage(message: SmsEntity) {
        viewModelScope.launch {
            repository.deleteMessage(message)
        }
    }
}
