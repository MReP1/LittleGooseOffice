package little.goose.account.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
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

    private val _multiSelectedTransactions = MutableStateFlow<Set<Transaction>>(emptySet())
    val multiSelectedTransactions = _multiSelectedTransactions.asStateFlow()

    val isMultiSelecting = multiSelectedTransactions.map { it.isNotEmpty() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

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
        _multiSelectedTransactions.value = emptySet()
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
        multiSelectedTransactions,
        isMultiSelecting
    ) { transactions, multiSelectedTransactions, isMultiSelecting ->
        TransactionColumnState(
            transactions = transactions,
            isMultiSelecting = isMultiSelecting,
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
            isMultiSelecting.value,
            multiSelectedTransactions.value,
            ::selectTransaction,
            ::selectAllTransaction,
            ::cancelMultiSelecting,
            ::deleteTransactions
        )
    )

    private fun selectTransaction(transaction: Transaction, selected: Boolean) {
        _multiSelectedTransactions.value = _multiSelectedTransactions.value.toMutableSet().apply {
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

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch(NonCancellable) {
            accountRepository.insertTransaction(transaction)
        }
    }

    fun addTransactions(transactions: List<Transaction>) {
        viewModelScope.launch(NonCancellable) {
            accountRepository.addTransactions(transactions)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            accountRepository.deleteTransaction(transaction)
        }
    }

    private fun deleteTransactions(transactions: List<Transaction>) {
        viewModelScope.launch {
            accountRepository.deleteTransactions(transactions)
        }
    }

    private fun selectAllTransaction() {
        _multiSelectedTransactions.value = curMonthTransactionFlow.value.toSet()
    }

    private fun cancelMultiSelecting() {
        _multiSelectedTransactions.value = emptySet()
    }
}