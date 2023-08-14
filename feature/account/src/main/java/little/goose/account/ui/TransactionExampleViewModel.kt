package little.goose.account.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import little.goose.account.data.constants.MoneyType
import little.goose.account.data.entities.Transaction
import little.goose.account.logic.DeleteTransactionsEventUseCase
import little.goose.account.logic.DeleteTransactionsUseCase
import little.goose.account.logic.GetAllTransactionFlowUseCase
import little.goose.account.logic.GetTransactionByDateFlowUseCase
import little.goose.account.logic.GetTransactionByYearFlowWithKeyContentUseCase
import little.goose.account.logic.GetTransactionByYearMonthFlowUseCase
import little.goose.account.logic.GetTransactionByYearMonthFlowWithKeyContentUseCase
import little.goose.account.ui.component.TransactionColumnState
import little.goose.common.utils.TimeType
import little.goose.common.utils.getDate
import little.goose.common.utils.getMonth
import little.goose.common.utils.getYear
import little.goose.common.utils.toChineseYear
import little.goose.common.utils.toChineseYearMonth
import little.goose.common.utils.toChineseYearMonthDay
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TransactionExampleViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    private val getTransactionByDateFlowUseCase: GetTransactionByDateFlowUseCase,
    private val getTransactionByYearMonthFlowUseCase: GetTransactionByYearMonthFlowUseCase,
    private val getTransactionByYearFlowWithKeyContentUseCase: GetTransactionByYearFlowWithKeyContentUseCase,
    private val getTransactionByYearMonthFlowWithKeyContentUseCase: GetTransactionByYearMonthFlowWithKeyContentUseCase,
    private val getAllTransactionFlowUseCase: GetAllTransactionFlowUseCase,
    private val deleteTransactionsUseCase: DeleteTransactionsUseCase,
    deleteTransactionsEventUseCase: DeleteTransactionsEventUseCase
) : AndroidViewModel(application) {

    sealed class Event {
        data class DeleteTransactions(val transactions: List<Transaction>) : Event()
    }

    private val args = TransactionExampleRouteArgs(savedStateHandle)

    private val time: Date get() = args.time
    private val content: String? get() = args.keyContent
    private val timeType: TimeType get() = args.timeType
    private val moneyType: MoneyType get() = args.moneyType

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

    init {
        deleteTransactionsEventUseCase().onEach {
            _event.emit(Event.DeleteTransactions(it))
        }.launchIn(viewModelScope)
    }

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
                TimeType.DATE -> getTransactionByDateFlowUseCase(
                    calendar.getYear(), calendar.getMonth(), calendar.getDate(), moneyType
                )

                TimeType.YEAR_MONTH -> getTransactionByYearMonthFlowUseCase(
                    calendar.getYear(), calendar.getMonth(), moneyType
                )

                else -> {
                    getAllTransactionFlowUseCase()
                }
            }
        } else {
            when (timeType) {
                TimeType.YEAR -> {
                    getTransactionByYearFlowWithKeyContentUseCase(
                        calendar.getYear(), content!!
                    )
                }

                TimeType.YEAR_MONTH -> {
                    getTransactionByYearMonthFlowWithKeyContentUseCase(
                        calendar.getYear(), calendar.getMonth(), content!!
                    )
                }

                else -> {
                    getAllTransactionFlowUseCase()
                }
            }
        }
    }

    private fun deleteTransactions(transactions: List<Transaction>) {
        viewModelScope.launch {
            deleteTransactionsUseCase(transactions)
            cancelMultiSelecting()
        }
    }

    private fun selectAllTransaction() {
        multiSelectedTransactions.value = transactions.value.toSet()
    }

    private fun cancelMultiSelecting() {
        multiSelectedTransactions.value = emptySet()
    }

}