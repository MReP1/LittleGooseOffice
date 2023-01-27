package little.goose.account.ui.account.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import little.goose.account.logic.AccountRepository
import little.goose.account.logic.data.entities.Transaction
import little.goose.account.logic.data.models.TimeMoney
import little.goose.account.logic.data.models.TransactionBalance
import little.goose.account.logic.data.models.TransactionPercent
import little.goose.account.utils.getMonth
import little.goose.account.utils.getYear
import java.util.*

class AnalysisFragmentViewModel : ViewModel() {

    private val analysisHelper = AnalysisHelper()

    var type = 0
    private val _monthFlow = MutableStateFlow(-1)
    val monthFlow: StateFlow<Int> = _monthFlow
    private val _yearFlow = MutableStateFlow(2010)
    var yearFlow: StateFlow<Int> = _yearFlow
    private val _transactionList: MutableStateFlow<List<Transaction>> =
        MutableStateFlow(emptyList())
    val transactionList: StateFlow<List<Transaction>> = _transactionList

    fun setTime(year: Int, month: Int) {
        _yearFlow.tryEmit(year)
        _monthFlow.tryEmit(month)
    }

    fun setMonth(month: Int) {
        _monthFlow.tryEmit(month)
    }

    fun setYear(year: Int) {
        _yearFlow.tryEmit(year)
    }

    fun getListExpensePercent(): List<TransactionPercent> =
        analysisHelper.mapExpensePercent.map { it.value }

    fun getListIncomePercent(): List<TransactionPercent> =
        analysisHelper.mapIncomePercent.map { it.value }

    fun getListTransactionBalance(): List<TransactionBalance> =
        analysisHelper.mapBalance.map { it.value }.sortedBy { it.time }

    fun getExpenseSumStr(): String = analysisHelper.expenseSum.toPlainString()
    fun getIncomeSumStr(): String = analysisHelper.incomeSum.toPlainString()
    fun getBalanceStr(): String = analysisHelper.balance.toPlainString()

    fun getTimeExpenseList(): List<TimeMoney> = analysisHelper.timeExpenseList
    fun getTimeIncomeList(): List<TimeMoney> = analysisHelper.timeIncomeList
    fun getTimeBalanceList(): List<TimeMoney> = analysisHelper.timeBalanceList

    fun updateTransactionListYear() {
        viewModelScope.launch(Dispatchers.Default) {
            val listDeferred = async(Dispatchers.IO) {
                AccountRepository.getTransactionByYear(yearFlow.value)
            }
            val expenseSumDeferred = async(Dispatchers.IO) {
                AccountRepository.getExpenseSumByYear(yearFlow.value)
            }
            val incomeSumDeferred = async(Dispatchers.IO) {
                AccountRepository.getIncomeSumByYear(yearFlow.value)
            }
            val list = listDeferred.await()
            val expenseSum = expenseSumDeferred.await()
            val incomeSum = incomeSumDeferred.await()
            analysisHelper.analyseTransactionList(
                list,
                expenseSum,
                incomeSum,
                yearFlow.value,
                monthFlow.value,
                type
            )
            _transactionList.emit(list)
        }
    }

    fun updateTransactionListMonth() {
        viewModelScope.launch(Dispatchers.Default) {
            val listDeferred = async(Dispatchers.IO) {
                AccountRepository.getTransactionsByYearAndMonth(yearFlow.value, monthFlow.value)
            }
            val expenseSumDeferred = async(Dispatchers.IO) {
                AccountRepository.getExpenseSumByYearMonth(yearFlow.value, monthFlow.value)
            }
            val incomeSumDeferred = async(Dispatchers.IO) {
                AccountRepository.getIncomeSumByYearMonth(yearFlow.value, monthFlow.value)
            }
            val list = listDeferred.await()
            val expenseSum = expenseSumDeferred.await()
            val incomeSum = incomeSumDeferred.await()
            analysisHelper.analyseTransactionList(
                list,
                expenseSum,
                incomeSum,
                yearFlow.value,
                monthFlow.value,
                type
            )
            _transactionList.emit(list)
        }
    }

    init {
        Calendar.getInstance().apply {
            _yearFlow.value = getYear()
            _monthFlow.value = getMonth()
        }
    }

    companion object {
        const val YEAR = 0
        const val MONTH = 1
    }
}