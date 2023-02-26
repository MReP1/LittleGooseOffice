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
import little.goose.account.ui.transaction.insertTime
import little.goose.common.receiver.DeleteItemBroadcastReceiver
import little.goose.common.utils.getMonth
import little.goose.common.utils.getYear
import little.goose.common.utils.log
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AccountFragmentViewModel @Inject constructor(
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

    val curMonthBalance =
        combine(curMonthExpenseSum, curMonthIncomeSum) { expenseSum, incomeSum ->
            expenseSum + incomeSum
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            BigDecimal(0)
        )

    val curMonthTransactionFlow = combine(year, month) { year, month ->
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

    val deleteReceiver = DeleteItemBroadcastReceiver<Transaction>()

    suspend fun getTransactionByYearAndMonthFlow(year: Int, month: Int) =
        accountRepository.getTransactionByYearMonthFlow(year, month).stateIn(viewModelScope)


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
        log("monthSelectorState")
        MonthSelectorState(year, month, ::changeTime)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        MonthSelectorState(year.value, month.value, ::changeTime)
    )

    private fun changeTime(year: Int, month: Int) {
        _year.value = year
        _month.value = month
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO + NonCancellable) {
            accountRepository.addTransaction(transaction)
        }
    }

    fun addTransactions(transactions: List<Transaction>) {
        viewModelScope.launch(Dispatchers.IO + NonCancellable) {
            accountRepository.addTransactions(transactions)
        }
    }

    fun deleteTransactions(transactions: List<Transaction>) {
        viewModelScope.launch(Dispatchers.IO + NonCancellable) {
            accountRepository.deleteTransactions(transactions)
        }
    }
}