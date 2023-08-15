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
import little.goose.account.logic.GetTransactionByIconYearMonthUseCase
import little.goose.account.logic.GetTransactionByIconYearUseCase
import little.goose.account.logic.GetTransactionByYearFlowWithKeyContentUseCase
import little.goose.account.logic.GetTransactionByYearMonthFlowUseCase
import little.goose.account.logic.GetTransactionByYearMonthFlowWithKeyContentUseCase
import little.goose.account.ui.component.TransactionColumnState
import little.goose.common.utils.TimeType
import little.goose.common.utils.getDate
import little.goose.common.utils.getMonth
import little.goose.common.utils.getYear
import little.goose.common.utils.log
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
    private val getTransactionByIconYearMonthUseCase: GetTransactionByIconYearMonthUseCase,
    private val getTransactionByIconYearUseCase: GetTransactionByIconYearUseCase,
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
    private val iconId: Int? get() = args.iconId

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
        val year = calendar.getYear()
        val month = calendar.getMonth()
        val date = calendar.getDate()
        log(timeType)
        log(year)
        log(month)
        log(iconId)
        return if (iconId != null) {
            when (timeType) {
                TimeType.YEAR_MONTH -> getTransactionByIconYearMonthUseCase(iconId!!, year, month)
                TimeType.YEAR -> getTransactionByIconYearUseCase(iconId!!, year)
                else -> getAllTransactionFlowUseCase()
            }.onEach {
                log(it)
            }
        } else if (content == null) {
            when (timeType) {
                TimeType.DATE -> getTransactionByDateFlowUseCase(year, month, date, moneyType)
                TimeType.YEAR_MONTH -> getTransactionByYearMonthFlowUseCase(year, month, moneyType)
                else -> getAllTransactionFlowUseCase()
            }
        } else {
            when (timeType) {
                TimeType.YEAR -> getTransactionByYearFlowWithKeyContentUseCase(year, content!!)
                TimeType.YEAR_MONTH ->
                    getTransactionByYearMonthFlowWithKeyContentUseCase(year, month, content!!)

                else -> getAllTransactionFlowUseCase()
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