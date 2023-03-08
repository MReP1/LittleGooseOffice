package little.goose.account.ui.analysis

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import little.goose.account.data.constants.AccountConstant.EXPENSE
import little.goose.account.data.constants.AccountConstant.INCOME
import little.goose.account.data.entities.Transaction
import little.goose.account.data.models.TimeMoney
import little.goose.account.data.models.TransactionBalance
import little.goose.account.data.models.TransactionPercent
import little.goose.account.logic.AccountRepository
import little.goose.account.ui.analysis.AnalysisFragmentViewModel.Companion.MONTH
import little.goose.account.ui.analysis.AnalysisFragmentViewModel.Companion.YEAR
import little.goose.common.utils.*
import java.math.BigDecimal
import java.util.*
import kotlin.math.min

class AnalysisHelper(
    private val accountRepository: AccountRepository
) {

    private val calendar = Calendar.getInstance()

    private val mapExpensePercent: HashMap<Int, TransactionPercent> = HashMap()
    private val _expensePercentList = MutableStateFlow(listOf<TransactionPercent>())
    val expensePercents = _expensePercentList.asStateFlow()

    private val mapIncomePercent: HashMap<Int, TransactionPercent> = HashMap()
    private val _incomePercentList = MutableStateFlow(listOf<TransactionPercent>())
    val incomePercents = _incomePercentList.asStateFlow()

    private val mapBalance: HashMap<Int, TransactionBalance> = HashMap()
    private val _balances = MutableStateFlow(listOf<TransactionBalance>())
    val balances = _balances.asStateFlow()

    private val cachedTimeExpenseList = ArrayList<TimeMoney>()
    private val _timeExpenseList = MutableStateFlow<List<TimeMoney>>(emptyList())
    val timeExpenses = _timeExpenseList.asStateFlow()

    private val cachedTimeIncomeList = ArrayList<TimeMoney>()
    private val _timeIncomeList = MutableStateFlow<List<TimeMoney>>(emptyList())
    val timeIncomes = _timeIncomeList.asStateFlow()

    private val cachedTimeBalanceList = ArrayList<TimeMoney>()
    private val _timeBalanceList = MutableStateFlow<List<TimeMoney>>(emptyList())
    val timeBalances = _timeBalanceList.asStateFlow()

    private var cachedExpenseSum = BigDecimal(0)
    private val _expenseSum = MutableStateFlow(BigDecimal(0))
    val expenseSum = _expenseSum.asStateFlow()

    private var cachedIncomeSum = BigDecimal(0)
    private val _incomeSum = MutableStateFlow(BigDecimal(0))
    val incomeSum = _incomeSum.asStateFlow()

    private var cachedBalance = BigDecimal(0)
    private val _balance = MutableStateFlow(BigDecimal(0))
    val balance = _balance.asStateFlow()

    suspend fun updateTransactionListYear(
        year: Int
    ) = withContext(Dispatchers.IO) {
        val listDeferred = async { accountRepository.getTransactionByYear(year) }
        val expenseSumDeferred = async { accountRepository.getExpenseSumByYear(year) }
        val incomeSumDeferred = async { accountRepository.getIncomeSumByYear(year) }
        val list = listDeferred.await()
        val expenseSum = expenseSumDeferred.await()
        val incomeSum = incomeSumDeferred.await()
        analyseTransactionList(list, expenseSum, incomeSum, year, -1, 0)
    }

    suspend fun updateTransactionListMonth(
        year: Int, month: Int
    ) = withContext(Dispatchers.IO) {
        val listDeferred = async { accountRepository.getTransactionsByYearAndMonth(year, month) }
        val expenseSumDeferred = async { accountRepository.getExpenseSumByYearMonth(year, month) }
        val incomeSumDeferred = async { accountRepository.getIncomeSumByYearMonth(year, month) }
        val list = listDeferred.await()
        val expenseSum = expenseSumDeferred.await()
        val incomeSum = incomeSumDeferred.await()
        analyseTransactionList(list, expenseSum, incomeSum, year, month, 1)
    }

    fun analyseTransactionList(
        list: List<Transaction>,
        expenseSum: Double,
        incomeSum: Double,
        year: Int,
        month: Int,
        type: Int
    ) {
        mapExpensePercent.clear()
        mapIncomePercent.clear()
        mapBalance.clear()

        cachedTimeExpenseList.clear()
        cachedTimeIncomeList.clear()
        cachedTimeBalanceList.clear()

        when (type) {
            YEAR -> {
                initTimeListYear(year)
                dealWithListYear(list)
            }
            MONTH -> {
                initTimeListMonth(year, month)
                dealWithListMonth(list)
            }
        }

        //将balanceList的balance通过 expense + income 算出来
        for (value in mapExpensePercent) {
            value.value.also { transPercent ->
                transPercent.percent = transPercent.money.toDouble() / expenseSum
            }
        }
        for (value in mapIncomePercent) {
            value.value.also { transPercent ->
                transPercent.percent = transPercent.money.toDouble() / incomeSum
            }
        }
        for (value in mapBalance) {
            value.value.also { transactionBalance ->
                transactionBalance.balance =
                    transactionBalance.expense + transactionBalance.income
            }
        }
        this.cachedExpenseSum = BigDecimal(expenseSum).getRoundTwo()
        this.cachedIncomeSum = BigDecimal(incomeSum).getRoundTwo()
        this.cachedBalance = this.cachedExpenseSum + this.cachedIncomeSum

        //算出balanceTimeList的money
        for (index in 0 until min(cachedTimeExpenseList.size, cachedTimeIncomeList.size)) {
            cachedTimeBalanceList[index].money = cachedTimeBalanceList[index].money +
                    cachedTimeExpenseList[index].money + cachedTimeIncomeList[index].money
        }

        _expensePercentList.value = mapExpensePercent.values.toList()
        _incomePercentList.value = mapIncomePercent.values.toList()
        _balances.value = mapBalance.values.sortedBy { it.time }.toList()

        _incomeSum.value = cachedIncomeSum
        _expenseSum.value = cachedExpenseSum
        _balance.value = cachedBalance

        _timeExpenseList.value = cachedTimeExpenseList.toList()
        _timeIncomeList.value = cachedTimeIncomeList.toList()
        _timeBalanceList.value = cachedTimeBalanceList.toList()
    }

    private fun initTimeListMonth(year: Int, month: Int) {
        calendar.apply {
            clear()
            setYear(year)
            setMonth(month)
            val days = DateTimeUtils.getDaysByCalendar(calendar)
            for (day in 1..days) {
                setDate(day)
                val time = calendar.time
                cachedTimeExpenseList.add(TimeMoney(time, BigDecimal(0)))
                cachedTimeIncomeList.add(TimeMoney(time, BigDecimal(0)))
                cachedTimeBalanceList.add(TimeMoney(time, BigDecimal(0)))
            }
        }
    }

    private fun initTimeListYear(year: Int) {
        calendar.apply {
            clear()
            setYear(year)
            for (month in 1..12) {
                setMonth(month)
                val time = calendar.time
                cachedTimeExpenseList.add(TimeMoney(time, BigDecimal(0)))
                cachedTimeIncomeList.add(TimeMoney(time, BigDecimal(0)))
                cachedTimeBalanceList.add(TimeMoney(time, BigDecimal(0)))
            }
        }
    }

    private fun dealWithListYear(list: List<Transaction>) {
        for (value in list) {
            when (value.type) {
                EXPENSE -> {
                    //处理TransactionPercent
                    dealWithExpense(value)
                    //处理TransactionBalance
                    dealWithBalanceOfExpenseYear(value)
                }
                INCOME -> {
                    //处理TransactionPercent
                    dealWithIncome(value)
                    //处理TransactionBalance
                    dealWithBalanceOfIncomeYear(value)
                }
            }
        }
    }

    private fun dealWithListMonth(list: List<Transaction>) {
        for (value in list) {
            when (value.type) {
                EXPENSE -> {
                    //处理TransactionPercent
                    dealWithExpense(value)
                    //处理TransactionBalance
                    dealWithBalanceOfExpenseMonth(value)
                }
                INCOME -> {
                    //处理TransactionPercent
                    dealWithIncome(value)
                    //处理TransactionBalance
                    dealWithBalanceOfIncomeMonth(value)
                }
            }
        }
    }

    private fun dealWithBalanceOfExpenseMonth(value: Transaction) {
        val date = value.time.getRealDate()
        val transactionBalance = mapBalance[date]
        if (transactionBalance != null) {
            transactionBalance.expense += value.money
        } else {
            mapBalance[date] = TransactionBalance(
                value.time, value.money, BigDecimal(0), BigDecimal(0)
            )
        }
        cachedTimeExpenseList[date - 1].money += value.money
    }

    private fun dealWithBalanceOfExpenseYear(value: Transaction) {
        val month = value.time.getRealMonth()
        val transactionBalance = mapBalance[month]
        if (transactionBalance != null) {
            transactionBalance.expense += value.money
        } else {
            mapBalance[month] = TransactionBalance(
                value.time, value.money, BigDecimal(0), BigDecimal(0)
            )
        }
        cachedTimeExpenseList[month - 1].money += value.money
    }

    private fun dealWithBalanceOfIncomeMonth(value: Transaction) {
        val date = value.time.getRealDate()
        val transactionBalance = mapBalance[date]
        if (transactionBalance != null) {
            transactionBalance.income += value.money
        } else {
            mapBalance[date] = TransactionBalance(
                value.time, BigDecimal(0), value.money, BigDecimal(0)
            )
        }
        cachedTimeIncomeList[date - 1].money += value.money
    }

    private fun dealWithBalanceOfIncomeYear(value: Transaction) {
        val month = value.time.getRealMonth()
        val transactionBalance = mapBalance[month]
        if (transactionBalance != null) {
            transactionBalance.income += value.money
        } else {
            mapBalance[month] = TransactionBalance(
                value.time, BigDecimal(0), value.money, BigDecimal(0)
            )
        }
        cachedTimeIncomeList[month - 1].money += value.money
    }

    private fun dealWithExpense(value: Transaction) {
        val expensePercent = mapExpensePercent[value.icon_id]
        if (expensePercent != null) {
            expensePercent.money += value.money
        } else {
            mapExpensePercent[value.icon_id] =
                TransactionPercent(value.icon_id, value.content, value.money, 0.0)
        }
    }

    private fun dealWithIncome(value: Transaction) {
        val incomePercent = mapIncomePercent[value.icon_id]
        if (incomePercent != null) {
            incomePercent.money += value.money
        } else {
            mapIncomePercent[value.icon_id] =
                TransactionPercent(value.icon_id, value.content, value.money, 0.0)
        }
    }

}