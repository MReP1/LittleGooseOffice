package little.goose.account.ui.account

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import little.goose.account.common.dialog.time.TimeType
import little.goose.account.common.receiver.DeleteItemBroadcastReceiver
import little.goose.account.logic.AccountRepository
import little.goose.account.logic.data.constant.*
import little.goose.account.logic.data.entities.Transaction
import little.goose.account.utils.*
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TransactionExampleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val time: Date = savedStateHandle[KEY_TIME]!!
    private val content: String? = savedStateHandle[KEY_CONTENT]
    private val timeType: TimeType = savedStateHandle[KEY_TIME_TYPE]!!
    private val moneyType: MoneyType = savedStateHandle[KEY_MONEY_TYPE]!!

    val transactions: Flow<List<Transaction>> = run {
        val calendar = Calendar.getInstance()
        calendar.time = time
        if (content == null) {
            when (timeType) {
                TimeType.DATE -> AccountRepository.getTransactionByDateFlow(
                    calendar.getYear(), calendar.getMonth(), calendar.getDate(), moneyType
                )
                TimeType.YEAR_MONTH -> AccountRepository.getTransactionByYearMonthFlow(
                    calendar.getYear(), calendar.getMonth(), moneyType
                )
                else -> {
                    AccountRepository.getAllTransactionFlow()
                }
            }
        } else {
            when (timeType) {
                TimeType.YEAR -> {
                    AccountRepository.getTransactionByYearFlowWithKeyContent(
                        calendar.getYear(), content
                    )
                }
                TimeType.YEAR_MONTH -> {
                    AccountRepository.getTransactionByYearMonthFlowWithKeyContent(
                        calendar.getYear(), calendar.getMonth(), content
                    )
                }
                else -> {
                    AccountRepository.getAllTransactionFlow()
                }
            }
        }
    }

    private val _deleteTransaction = MutableStateFlow<Transaction?>(null)
    val deleteTransaction = _deleteTransaction.asStateFlow()

    private val deleteReceiver = DeleteItemBroadcastReceiver<Transaction>().apply {
        registerForever(NOTIFY_DELETE_TRANSACTION) { _, transaction ->
            _deleteTransaction.value = transaction
        }
    }

    suspend fun undo() {
        deleteTransaction.value?.let { AccountRepository.addTransaction(it) }
        _deleteTransaction.value = null
    }

    override fun onCleared() {
        super.onCleared()
        deleteReceiver.unregisterReceiver()
    }
}