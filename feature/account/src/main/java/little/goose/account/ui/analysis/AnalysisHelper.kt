package little.goose.account.ui.analysis

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.zip
import little.goose.account.data.constants.AccountConstant.EXPENSE
import little.goose.account.data.constants.AccountConstant.INCOME
import little.goose.account.data.entities.Transaction
import little.goose.account.data.models.TimeMoney
import little.goose.account.data.models.TransactionBalance
import little.goose.account.data.models.TransactionPercent
import little.goose.account.logic.GetExpenseSumFlowByYearMonthUseCase
import little.goose.account.logic.GetExpenseSumFlowByYearUseCase
import little.goose.account.logic.GetIncomeSumFlowByYearMonthUseCase
import little.goose.account.logic.GetIncomeSumFlowByYearUseCase
import little.goose.account.logic.GetTransactionsFlowByYearAndMonthUseCase
import little.goose.account.logic.GetTransactionsFlowByYearUseCase
import little.goose.common.utils.DateTimeUtils
import little.goose.common.utils.getMonth
import little.goose.common.utils.getRealDate
import little.goose.common.utils.getRealMonth
import little.goose.common.utils.getRoundTwo
import little.goose.common.utils.getYear
import little.goose.common.utils.setDate
import little.goose.common.utils.setMonth
import little.goose.common.utils.setYear
import java.math.BigDecimal
import java.util.Calendar
import kotlin.math.min

class AnalysisHelper(
    private val getTransactionsFlowByYearUseCase: GetTransactionsFlowByYearUseCase,
    private val getExpenseSumFlowByYearUseCase: GetExpenseSumFlowByYearUseCase,
    private val getIncomeSumFlowByYearUseCase: GetIncomeSumFlowByYearUseCase,
    private val getTransactionsFlowByYearAndMonthUseCase: GetTransactionsFlowByYearAndMonthUseCase,
    private val getExpenseSumFlowByYearMonthUseCase: GetExpenseSumFlowByYearMonthUseCase,
    private val getIncomeSumFlowByYearMonthUseCase: GetIncomeSumFlowByYearMonthUseCase
) {
    private val calendar = Calendar.getInstance()

    enum class TimeType { MONTH, YEAR }

    private val _timeType = MutableStateFlow(TimeType.MONTH)
    val timeType = _timeType.asStateFlow()

    private val _year = MutableStateFlow(Calendar.getInstance().getYear())
    val year = _year.asStateFlow()

    private val _month = MutableStateFlow(Calendar.getInstance().getMonth())
    val month = _month.asStateFlow()

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

    private val analysisFlow = combine(_timeType, _year, _month) { timeType, year, month ->
        when (timeType) {
            TimeType.MONTH -> getTransactionsFlowByYearAndMonthUseCase(year, month).zip(
                getExpenseSumFlowByYearMonthUseCase(year, month).zip(
                    getIncomeSumFlowByYearMonthUseCase(year, month)
                ) { expenseSum, incomeSum -> expenseSum to incomeSum }
            ) { transactions, pair ->
                Triple(transactions, pair.first, pair.second)
            }

            TimeType.YEAR -> getTransactionsFlowByYearUseCase(year).zip(
                getExpenseSumFlowByYearUseCase(year)
                    .zip(getIncomeSumFlowByYearUseCase(year)
                ) { expenseSum, incomeSum -> expenseSum to incomeSum }
            ) { transactions, pair ->
                Triple(transactions, pair.first, pair.second)
            }
        }
    }.flatMapLatest { it }.onEach { (transactions, expenseSum, incomeSum) ->
        analyseTransactionList(
            transactions, expenseSum, incomeSum,
            year.value, month.value, timeType.value
        )
    }.flowOn(Dispatchers.IO)

    private var analysisJob: Job? = null

    internal fun bindCoroutineScope(coroutineScope: CoroutineScope) {
        analysisJob?.cancel()
        analysisJob = analysisFlow.launchIn(coroutineScope)
    }

    internal fun changeTimeType(type: TimeType) {
        _timeType.value = type
    }

    internal fun changeYear(year: Int) {
        _year.value = year
    }

    internal fun changeTime(
        year: Int, month: Int
    ) {
        _year.value = year
        _month.value = month
    }

    private fun analyseTransactionList(
        list: List<Transaction>,
        expenseSum: Double,
        incomeSum: Double,
        year: Int,
        month: Int,
        type: TimeType
    ) {
        mapExpensePercent.clear()
        mapIncomePercent.clear()
        mapBalance.clear()

        cachedTimeExpenseList.clear()
        cachedTimeIncomeList.clear()
        cachedTimeBalanceList.clear()

        when (type) {
            TimeType.YEAR -> {
                initTimeListYear(year)
                dealWithListYear(list)
            }

            TimeType.MONTH -> {
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