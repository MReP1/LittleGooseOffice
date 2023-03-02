package little.goose.account.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import little.goose.account.data.constants.MoneyType
import little.goose.account.data.entities.Transaction
import little.goose.account.logic.AccountRepository
import little.goose.common.constants.KEY_CONTENT
import little.goose.common.constants.KEY_MONEY_TYPE
import little.goose.common.constants.KEY_TIME
import little.goose.common.constants.KEY_TIME_TYPE
import little.goose.common.dialog.time.TimeType
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

    sealed class Event {
        data class DeleteTransaction(val transaction: Transaction) : Event()
        data class InsertTransaction(val transaction: Transaction) : Event()
    }

    private val time: Date = savedStateHandle[KEY_TIME]!!
    private val content: String? = savedStateHandle[KEY_CONTENT]
    private val timeType: TimeType = savedStateHandle[KEY_TIME_TYPE]!!
    private val moneyType: MoneyType = savedStateHandle[KEY_MONEY_TYPE]!!

    private val _event = MutableSharedFlow<Event>()
    val event = _event.asSharedFlow()

    val transactions = getTransactionFlow().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private fun getTransactionFlow(): Flow<List<Transaction>> {
        val calendar = Calendar.getInstance()
        calendar.time = time
        return if (content == null) {
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

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            accountRepository.deleteTransaction(transaction)
            _event.emit(Event.DeleteTransaction(transaction))
        }
    }

    fun insertTransaction(transaction: Transaction) {
        viewModelScope.launch {
            accountRepository.insertTransaction(transaction)
            _event.emit(Event.InsertTransaction(transaction))
        }
    }
}