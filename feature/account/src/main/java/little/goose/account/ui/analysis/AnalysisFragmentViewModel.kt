package little.goose.account.ui.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import little.goose.account.data.entities.Transaction
import little.goose.account.data.models.TimeMoney
import little.goose.account.data.models.TransactionPercent
import little.goose.account.data.models.TransactionBalance
import little.goose.account.logic.AccountRepository
import little.goose.common.utils.getMonth
import little.goose.common.utils.getYear
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AnalysisFragmentViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val analysisHelper = AnalysisHelper(accountRepository)

    var type = 0

    private val _monthFlow = MutableStateFlow(Calendar.getInstance().getMonth())
    val monthFlow: StateFlow<Int> = _monthFlow.asStateFlow()

    private val _yearFlow = MutableStateFlow(Calendar.getInstance().getYear())
    var yearFlow: StateFlow<Int> = _yearFlow.asStateFlow()

    val transactionList = combine(_yearFlow, _monthFlow) { year, month ->
        when (type) {
            YEAR -> getTransactionListYear(year, month)
            MONTH -> getTransactionListMonth(year, month)
            else -> throw Exception()
        }
    }.flowOn(Dispatchers.Default).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = listOf()
    )

    fun setTime(year: Int, month: Int) {
        _yearFlow.value = year
        _monthFlow.value = month
    }

    fun setYear(year: Int) {
        _yearFlow.value = year
    }

    fun getListExpensePercent(): List<TransactionPercent> = analysisHelper.expensePercents.value

    fun getListIncomePercent(): List<TransactionPercent> = analysisHelper.incomePercents.value

    fun getListTransactionBalance(): List<TransactionBalance> = analysisHelper.balances.value

    fun getExpenseSumStr(): String = analysisHelper.expenseSum.value.toPlainString()

    fun getIncomeSumStr(): String = analysisHelper.incomeSum.value.toPlainString()

    fun getBalanceStr(): String = analysisHelper.balance.value.toPlainString()

    fun getTimeExpenseList(): List<TimeMoney> = analysisHelper.timeExpenses.value

    fun getTimeIncomeList(): List<TimeMoney> = analysisHelper.timeIncomes.value

    fun getTimeBalanceList(): List<TimeMoney> = analysisHelper.timeBalances.value

    private suspend fun getTransactionListYear(year: Int, month: Int): List<Transaction> {
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
            analysisHelper.analyseTransactionList(
                list, expenseSum, incomeSum, year, month, type
            )
            list
        }
    }

    private suspend fun getTransactionListMonth(year: Int, month: Int): List<Transaction> {
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
            analysisHelper.analyseTransactionList(
                list, expenseSum, incomeSum, year, month, type
            )
            list
        }
    }

    companion object {
        const val YEAR = 0
        const val MONTH = 1
    }
}