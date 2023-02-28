package little.goose.account.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import little.goose.account.data.constants.MoneyType
import little.goose.account.logic.AccountRepository
import little.goose.common.constants.*
import little.goose.common.dialog.time.TimeType
import little.goose.common.receiver.DeleteItemBroadcastReceiver
import little.goose.common.utils.getDate
import little.goose.common.utils.getMonth
import little.goose.common.utils.getYear
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TransactionExampleViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    private val accountRepository: AccountRepository
) : AndroidViewModel(application) {

    private val time: Date = savedStateHandle[KEY_TIME]!!
    private val content: String? = savedStateHandle[KEY_CONTENT]
    private val timeType: TimeType = savedStateHandle[KEY_TIME_TYPE]!!
    private val moneyType: MoneyType = savedStateHandle[KEY_MONEY_TYPE]!!

    val transactions: Flow<List<little.goose.account.data.entities.Transaction>> = run {
        val calendar = Calendar.getInstance()
        calendar.time = time
        if (content == null) {
            when (timeType) {
                TimeType.DATE -> accountRepository.getTransactionByDateFlow(
                    calendar.getYear(), calendar.getMonth(), calendar.getDate(), moneyType
                )
                TimeType.YEAR_MONTH -> accountRepository.getTransactionByYearMonthFlow(
                    calendar.getYear(), calendar.getMonth(), moneyType
                )
                else -> {
                    accountRepository.getAllTransactionFlow()
                }
            }
        } else {
            when (timeType) {
                TimeType.YEAR -> {
                    accountRepository.getTransactionByYearFlowWithKeyContent(
                        calendar.getYear(), content
                    )
                }
                TimeType.YEAR_MONTH -> {
                    accountRepository.getTransactionByYearMonthFlowWithKeyContent(
                        calendar.getYear(), calendar.getMonth(), content
                    )
                }
                else -> {
                    accountRepository.getAllTransactionFlow()
                }
            }
        }
    }

    private val _deleteTransaction = MutableStateFlow<little.goose.account.data.entities.Transaction?>(null)
    val deleteTransaction = _deleteTransaction.asStateFlow()

    private val deleteReceiver = DeleteItemBroadcastReceiver<little.goose.account.data.entities.Transaction>().apply {
        registerForever(application, NOTIFY_DELETE_TRANSACTION) { _, transaction ->
            _deleteTransaction.value = transaction
        }
    }

    suspend fun undo() {
        deleteTransaction.value?.let { accountRepository.insertTransaction(it) }
        _deleteTransaction.value = null
    }

    override fun onCleared() {
        super.onCleared()
        deleteReceiver.unregisterReceiver(getApplication())
    }
}