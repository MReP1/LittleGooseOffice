package little.goose.account.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import little.goose.account.data.constants.AccountConstant
import little.goose.account.data.entities.Transaction
import little.goose.account.logic.AccountRepository
import little.goose.account.ui.component.AccountTitleState
import little.goose.account.ui.component.MonthSelectorState
import little.goose.account.ui.component.TransactionColumnState
import little.goose.account.utils.insertTime
import little.goose.common.utils.getMonth
import little.goose.common.utils.getYear
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AccountHomeViewModel @Inject constructor(
    private val accountRepository: AccountRepository
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

    val totalExpenseSum = accountRepository
        .getAllTransactionExpenseSumFlow()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            BigDecimal(0)
        )

    val totalIncomeSum = accountRepository
        .getAllTransactionIncomeSumFlow()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            BigDecimal(0)
        )

    val totalBalance = combine(totalExpenseSum, totalIncomeSum) { expenseSum, incomeSum ->
        expenseSum + incomeSum
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        BigDecimal(0)
    )

    private val _curMonthExpenseSum = MutableStateFlow(BigDecimal(0))
    val curMonthExpenseSum = _curMonthExpenseSum.asStateFlow()

    private val _curMonthIncomeSum = MutableStateFlow(BigDecimal(0))
    val curMonthIncomeSum = _curMonthIncomeSum.asStateFlow()

    val curMonthBalance = combine(curMonthExpenseSum, curMonthIncomeSum) { expenseSum, incomeSum ->
        expenseSum + incomeSum
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        BigDecimal(0)
    )

    private val multiSelectedTransactions = MutableStateFlow<Set<Transaction>>(emptySet())

    private val curMonthTransactionFlow = combine(year, month) { year, month ->
        accountRepository.getTransactionByYearMonthFlow(year, month)
    }.flatMapLatest { it }.onEach { transactions ->
        _curMonthExpenseSum.value = BigDecimal(0)
        _curMonthIncomeSum.value = BigDecimal(0)
        for (transaction in transactions) {
            when (transaction.type) {
                AccountConstant.EXPENSE -> _curMonthExpenseSum.value += transaction.money
                AccountConstant.INCOME -> _curMonthIncomeSum.value += transaction.money
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

    val curMonthTransactionWithTime = curMonthTransactionFlow.map {
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

    fun changeTime(year: Int, month: Int) {
        _year.value = year
        _month.value = month
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            accountRepository.deleteTransaction(transaction)
            _event.emit(Event.DeleteTransactions(listOf(transaction)))
        }
    }

    private fun deleteTransactions(transactions: List<Transaction>) {
        viewModelScope.launch {
            accountRepository.deleteTransactions(transactions)
            _event.emit(Event.DeleteTransactions(transactions))
        }
    }

    private fun selectAllTransaction() {
        multiSelectedTransactions.value = curMonthTransactionFlow.value.toSet()
    }

    private fun cancelMultiSelecting() {
        multiSelectedTransactions.value = emptySet()
    }
}