package little.goose.account.ui.account.transaction

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import little.goose.account.appScope
import little.goose.account.logic.AccountRepository
import little.goose.account.logic.data.constant.AccountConstant.EXPENSE
import little.goose.account.logic.data.constant.AccountConstant.INCOME
import little.goose.account.logic.data.constant.AccountConstant.TIME
import little.goose.account.logic.data.entities.Transaction
import little.goose.common.utils.getDate
import little.goose.common.utils.getMonth
import little.goose.common.utils.getYear
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object TransactionHelper {
    var listTransaction: List<Transaction> = emptyList()
    private var expenseSum: BigDecimal = BigDecimal(0)
    private var incomeSum: BigDecimal = BigDecimal(0)
    private var balance: BigDecimal = BigDecimal(0)

    var curMonthExpenseSum: BigDecimal = BigDecimal(0)
    var curMonthIncomeSum: BigDecimal = BigDecimal(0)
    var curMonthBalance: BigDecimal = BigDecimal(0)

    private val mutex = Mutex()

    const val KEY_EXPENSE = 0
    const val KEY_INCOME = 1
    const val KEY_BALANCE = 2

    suspend fun initTransaction() {
        listTransaction = AccountRepository.getTransactionCurrentMonth().also {
            for (transaction in it) {
                when (transaction.type) {
                    EXPENSE -> curMonthExpenseSum += transaction.money
                    INCOME -> curMonthIncomeSum += transaction.money
                }
            }
            curMonthBalance = curMonthExpenseSum + curMonthIncomeSum
        }
        updateSum()
    }

    //通过list来更新Helper
    fun updateMoneyFromTransactionListWithMonth(list: List<Transaction>) {
        var curMonthExpSum = BigDecimal("0")
        var curMonthIncSum = BigDecimal("0")
        for (value in list) {
            when (value.type) {
                EXPENSE -> {
                    curMonthExpSum += value.money
                }
                INCOME -> {
                    curMonthIncSum += value.money
                }
            }
        }
        updateCurMonthSum(curMonthExpSum, curMonthIncSum)
        updateSum()
    }

    //更新总额和结余
    private fun updateCurMonthSum(
        curMonthExpSum: BigDecimal,
        curMonthIncSum: BigDecimal
    ) {
        curMonthExpenseSum = curMonthExpSum
        curMonthIncomeSum = curMonthIncSum
        curMonthBalance = curMonthIncomeSum + curMonthExpenseSum
    }

    private fun updateSum() {
        appScope.launch {
            mutex.withLock {
                val expenseSumDeferred = async(Dispatchers.IO) {
                    AccountRepository.getAllTransactionExpenseSum()
                }
                val incomeSumDeferred = async(Dispatchers.IO) {
                    AccountRepository.getAllTransactionIncomeSum()
                }
                expenseSum = expenseSumDeferred.await()
                incomeSum = incomeSumDeferred.await()
                balance = expenseSum + incomeSum
            }
        }
    }

    //获得支出和收入的总额Map
    fun getSumFromTransactionList(list: List<Transaction>): Map<Int, BigDecimal> {
        var expSum = BigDecimal("0")
        var incSum = BigDecimal("0")
        for (value in list) {
            when (value.type) {
                EXPENSE -> expSum += value.money
                INCOME -> incSum += value.money
            }
        }
        return HashMap<Int, BigDecimal>().apply {
            put(EXPENSE, expSum)
            put(INCOME, incSum)
        }
    }

    fun updateTransactionList(list: List<Transaction>) {
        listTransaction = list
    }

    suspend fun getAllSum() = withContext(Dispatchers.IO) {
        mutex.withLock {
            HashMap<Int, BigDecimal>().apply {
                put(KEY_EXPENSE, expenseSum)
                put(KEY_INCOME, incomeSum)
                put(KEY_BALANCE, balance)
            }
        }
    }

}

fun List<Transaction>.insertTime(): List<Transaction> {
    val arrayList = ArrayList<Transaction>()
    var timeString = ""
    if (this.isEmpty()) {
        return emptyList()
    }
    Calendar.getInstance().apply {
        for (transaction in this@insertTime) {
            time = transaction.time
            val year = getYear()
            val month = getMonth()
            val date = getDate()
            val tempTime = "${year}年${month}月${date}日"
            val monthDayTime = "${month}月${date}日"
            if (tempTime != timeString) {
                arrayList.add(
                    Transaction(null, TIME, BigDecimal(0), "", monthDayTime, Date(), 1)
                )
                timeString = tempTime
            }
            arrayList.add(transaction)
        }
    }
    return arrayList
}