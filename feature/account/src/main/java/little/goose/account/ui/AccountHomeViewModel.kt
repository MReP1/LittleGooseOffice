package little.goose.account.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import little.goose.account.data.constants.AccountConstant
import little.goose.account.data.entities.Transaction
import little.goose.account.logic.DeleteTransactionsUseCase
import little.goose.account.logic.GetAllTransactionExpenseSumFlowUseCase
import little.goose.account.logic.GetAllTransactionIncomeSumFlowUseCase
import little.goose.account.logic.GetTransactionByYearMonthFlowUseCase
import little.goose.account.ui.component.AccountTitleState
import little.goose.account.ui.component.MonthSelectorState
import little.goose.account.ui.component.TransactionColumnState
import little.goose.account.utils.insertTime
import little.goose.common.utils.getMonth
import little.goose.common.utils.getYear
import java.math.BigDecimal
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class AccountHomeViewModel @Inject constructor(
    getAllTransactionExpenseSumFlowUseCase: GetAllTransactionExpenseSumFlowUseCase,
    getAllTransactionIncomeSumFlowUseCase: GetAllTransactionIncomeSumFlowUseCase,
    private val getTransactionByYearMonthFlowUseCase: GetTransactionByYearMonthFlowUseCase,
    private val deleteTransactionsUseCase: DeleteTransactionsUseCase,
) : ViewModel() {

    sealed class Event {
        data class DeleteTransactions(val transactions: List<Transaction>) : Event()
    }

    private val _event = MutableSharedFlow<Event>()
    val event = _event.asSharedFlow()

    private val _year = MutableStateFlow(Calendar.getInstance().getYear())
    val year = _year.asStateFlow()

    private val _month = MutableStateFlow(Calendar.getInstance().getMonth())
    val month = _month.asStateFlow()

    private val totalExpenseSum = getAllTransactionExpenseSumFlowUseCase()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            BigDecimal(0)
        )

    private val totalIncomeSum = getAllTransactionIncomeSumFlowUseCase()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            BigDecimal(0)
        )

    private val totalBalance = combine(
        totalExpenseSum, totalIncomeSum
    ) { expenseSum, incomeSum ->
        expenseSum + incomeSum
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        BigDecimal(0)
    )

    private val curMonthExpenseSum = MutableStateFlow(BigDecimal(0))

    private val curMonthIncomeSum = MutableStateFlow(BigDecimal(0))

    private val curMonthBalance = combine(
        curMonthExpenseSum, curMonthIncomeSum
    ) { expenseSum, incomeSum ->
        expenseSum + incomeSum
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        BigDecimal(0)
    )

    private val multiSelectedTransactions = MutableStateFlow<Set<Transaction>>(emptySet())

    private val curMonthTransactionFlow = combine(year, month) { year, month ->
        getTransactionByYearMonthFlowUseCase(year, month)
    }.flatMapLatest { it }.onEach { transactions ->
        curMonthExpenseSum.value = BigDecimal(0)
        curMonthIncomeSum.value = BigDecimal(0)
        for (transaction in transactions) {
            when (transaction.type) {
                AccountConstant.EXPENSE -> curMonthExpenseSum.value += transaction.money
                AccountConstant.INCOME -> curMonthIncomeSum.value += transaction.money
            }
        }
        multiSelectedTransactions.value = emptySet()
    }.flowOn(
        Dispatchers.Default
    ).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val curMonthTransactionWithTime = curMonthTransactionFlow.map {
        it.insertTime()
    }.flowOn(Dispatchers.Default).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val transactionColumnState = combine(
        curMonthTransactionWithTime,
        multiSelectedTransactions
    ) { transactions, multiSelectedTransactions ->
        TransactionColumnState(
            transactions = transactions,
            isMultiSelecting = multiSelectedTransactions.isNotEmpty(),
            multiSelectedTransactions = multiSelectedTransactions,
            onTransactionSelected = ::selectTransaction,
            selectAllTransactions = ::selectAllTransaction,
            cancelMultiSelecting = ::cancelMultiSelecting,
            deleteTransactions = ::deleteTransactions
        )
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000),
        TransactionColumnState(
            curMonthTransactionWithTime.value,
            multiSelectedTransactions.value.isNotEmpty(),
            multiSelectedTransactions.value,
            ::selectTransaction,
            ::selectAllTransaction,
            ::cancelMultiSelecting,
            ::deleteTransactions
        )
    )

    private fun selectTransaction(transaction: Transaction, selected: Boolean) {
        multiSelectedTransactions.value = multiSelectedTransactions.value.toMutableSet().apply {
            if (selected) add(transaction) else remove(transaction)
        }
    }

    val accountTitleState = combine(
        curMonthExpenseSum, curMonthIncomeSum, curMonthBalance,
        totalExpenseSum, totalIncomeSum, totalBalance
    ) {
        AccountTitleState(it[0], it[1], it[2], it[3], it[4], it[5])
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AccountTitleState()
    )

    val monthSelectorState = combine(year, month) { year, month ->
        MonthSelectorState(year, month, ::changeTime)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        MonthSelectorState(year.value, month.value, ::changeTime)
    )

    init {
        deleteTransactionsUseCase.deleteTransactionEvent
            .onEach { _event.emit(Event.DeleteTransactions(it)) }
            .launchIn(viewModelScope)
    }

    private fun changeTime(year: Int, month: Int) {
        _year.value = year
        _month.value = month
    }

    private fun deleteTransactions(transactions: List<Transaction>) {
        viewModelScope.launch {
            deleteTransactionsUseCase(transactions)
        }
    }

    private fun selectAllTransaction() {
        multiSelectedTransactions.value = curMonthTransactionFlow.value.toSet()
    }

    private fun cancelMultiSelecting() {
        multiSelectedTransactions.value = emptySet()
    }
}