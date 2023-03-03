package little.goose.account.ui.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import little.goose.account.data.entities.Transaction
import little.goose.account.logic.AccountRepository
import little.goose.account.ui.component.MonthSelectorState
import little.goose.account.ui.component.YearSelectorState
import little.goose.common.utils.getMonth
import little.goose.common.utils.getYear
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class TransactionAnalysisViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val analysisHelper = AnalysisHelper()

    enum class Type { MONTH, YEAR }

    private val _type = MutableStateFlow(Type.MONTH)
    val type = _type.asStateFlow()

    private val _year = MutableStateFlow(Calendar.getInstance().getYear())
    val year = _year.asStateFlow()

    private val _month = MutableStateFlow(Calendar.getInstance().getMonth())
    val month = _month.asStateFlow()

    private val transactions = combine(type, year, month) { type, year, month ->
        when (type) {
            Type.MONTH -> getTransactionListMonth(year, month)
            Type.YEAR -> getTransactionListYear(year, month)
        }
    }.flowOn(Dispatchers.Default).stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), listOf()
    )

    private val expensePercents get() = analysisHelper.expensePercents

    private val incomePercents get() = analysisHelper.incomePercents

    private val balances get() = analysisHelper.balances

    private val expenseSum get() = analysisHelper.expenseSum

    private val incomeSum get() = analysisHelper.incomeSum

    private val balance get() = analysisHelper.balance

    private val timeExpenses get() = analysisHelper.timeExpenses

    private val timeIncomes get() = analysisHelper.timeIncomes

    private val timeBalances get() = analysisHelper.timeBalances

    val bottomBarState = combine(type, year, month) { type, year, month ->
        TransactionAnalysisBottomBarState(
            type, year, month,
            MonthSelectorState(year, month) { y, m -> _year.value = y; _month.value = m },
            YearSelectorState(year) { y -> _year.value = y },
            onTypeChange = { t -> _type.value = t }
        )
    }.stateIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000),
        initialValue = TransactionAnalysisBottomBarState(
            type.value, year.value, month.value,
            MonthSelectorState(year.value, month.value) { y, m ->
                _year.value = y; _month.value = m
            },
            YearSelectorState(year.value) { y -> _year.value = y },
            onTypeChange = { t -> _type.value = t }
        )
    )


    private suspend fun getTransactionListYear(
        year: Int, month: Int
    ): List<Transaction> {
        return coroutineScope {
            val listDeferred = async(Dispatchers.IO) {
                accountRepository.getTransactionByYear(year)
            }
            val expenseSumDeferred = async(Dispatchers.IO) {
                accountRepository.getExpenseSumByYear(year)
            }
            val incomeSumDeferred = async(Dispatchers.IO) {
                accountRepository.getIncomeSumByYear(year)
            }
            val list = listDeferred.await()
            val expenseSum = expenseSumDeferred.await()
            val incomeSum = incomeSumDeferred.await()
            analysisHelper.analyseTransactionList(list, expenseSum, incomeSum, year, month, 0)
            list
        }
    }

    private suspend fun getTransactionListMonth(
        year: Int, month: Int
    ): List<Transaction> {
        return coroutineScope {
            val listDeferred = async(Dispatchers.IO) {
                accountRepository.getTransactionsByYearAndMonth(year, month)
            }
            val expenseSumDeferred = async(Dispatchers.IO) {
                accountRepository.getExpenseSumByYearMonth(year, month)
            }
            val incomeSumDeferred = async(Dispatchers.IO) {
                accountRepository.getIncomeSumByYearMonth(year, month)
            }
            val list = listDeferred.await()
            val expenseSum = expenseSumDeferred.await()
            val incomeSum = incomeSumDeferred.await()
            analysisHelper.analyseTransactionList(list, expenseSum, incomeSum, year, month, 1)
            list
        }
    }

}