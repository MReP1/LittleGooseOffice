package little.goose.account.ui.account

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import little.goose.common.dialog.time.TimeType
import little.goose.common.receiver.DeleteItemBroadcastReceiver
import little.goose.account.logic.AccountRepository
import little.goose.account.logic.data.constant.*
import little.goose.account.logic.data.entities.Transaction
import little.goose.common.constants.NOTIFY_DELETE_TRANSACTION
import little.goose.common.utils.getDate
import little.goose.common.utils.getMonth
import little.goose.common.utils.getYear
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TransactionExampleViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

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
        registerForever(application, NOTIFY_DELETE_TRANSACTION) { _, transaction ->
            _deleteTransaction.value = transaction
        }
    }

    suspend fun undo() {
        deleteTransaction.value?.let { AccountRepository.addTransaction(it) }
        _deleteTransaction.value = null
    }

    override fun onCleared() {
        super.onCleared()
        deleteReceiver.unregisterReceiver(getApplication())
    }
}