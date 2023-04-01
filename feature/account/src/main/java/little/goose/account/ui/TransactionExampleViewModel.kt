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
import little.goose.account.ui.component.TransactionColumnState
import little.goose.common.constants.KEY_CONTENT
import little.goose.common.constants.KEY_MONEY_TYPE
import little.goose.common.constants.KEY_TIME
import little.goose.common.constants.KEY_TIME_TYPE
import little.goose.common.utils.TimeType
import little.goose.common.utils.*
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TransactionExampleViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    private val accountRepository: AccountRepository
) : AndroidViewModel(application) {

    sealed class Event {
        data class DeleteTransaction(val transactions: List<Transaction>) : Event()
        data class InsertTransaction(val transaction: List<Transaction>) : Event()
    }

    private val time: Date = savedStateHandle[KEY_TIME]!!
    private val content: String? = savedStateHandle[KEY_CONTENT]
    private val timeType: TimeType = savedStateHandle[KEY_TIME_TYPE]!!
    private val moneyType: MoneyType = savedStateHandle[KEY_MONEY_TYPE]!!

    var deletingTransactions: List<Transaction> = emptyList()

    val title = when (timeType) {
        TimeType.DATE -> time.toChineseYearMonthDay()
        TimeType.YEAR_MONTH -> time.toChineseYearMonth()
        TimeType.YEAR -> time.toChineseYear()
        else -> throw IllegalArgumentException()
    }

    private val _event = MutableSharedFlow<Event>()
    val event = _event.asSharedFlow()

    private val multiSelectedTransactions = MutableStateFlow<Set<Transaction>>(mutableSetOf())

    val transactions = getTransactionFlow().onEach {
        multiSelectedTransactions.value = emptySet()
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val transactionColumnState = combine(
        transactions,
        multiSelectedTransactions
    ) { transactions, multiSelectedTransactions ->
        TransactionColumnState(
            transactions = transactions,
            isMultiSelecting = multiSelectedTransactions.isNotEmpty(),
            multiSelectedTransactions = multiSelectedTransactions,
            onTransactionSelected = ::selectedTransaction,
            selectAllTransactions = ::selectAllTransaction,
            cancelMultiSelecting = ::cancelMultiSelecting,
            deleteTransactions = ::deleteTransactions
        )
    }.stateIn(
        scope = viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        TransactionColumnState(
            transactions.value,
            multiSelectedTransactions.value.isNotEmpty(),
            multiSelectedTransactions.value,
            ::selectedTransaction,
            ::selectAllTransaction,
            ::cancelMultiSelecting,
            deleteTransactions = ::deleteTransactions
        )
    )

    private fun selectedTransaction(transaction: Transaction, selected: Boolean) {
        multiSelectedTransactions.value = multiSelectedTransactions.value.toMutableSet().apply {
            if (selected) add(transaction) else remove(transaction)
        }
    }

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
            val transactions = listOf(transaction)
            deletingTransactions = transactions
            _event.emit(Event.DeleteTransaction(transactions))
        }
    }

    fun insertTransactions(transaction: List<Transaction>) {
        viewModelScope.launch {
            accountRepository.addTransactions(transaction)
            deletingTransactions = emptyList()
            _event.emit(Event.InsertTransaction(transaction))
        }
    }

    private fun deleteTransactions(transactions: List<Transaction>) {
        viewModelScope.launch {
            accountRepository.deleteTransactions(transactions)
            deletingTransactions = transactions
            _event.emit(Event.DeleteTransaction(transactions))
        }
    }

    private fun selectAllTransaction() {
        multiSelectedTransactions.value = transactions.value.toSet()
    }

    private fun cancelMultiSelecting() {
        multiSelectedTransactions.value = emptySet()
    }

}