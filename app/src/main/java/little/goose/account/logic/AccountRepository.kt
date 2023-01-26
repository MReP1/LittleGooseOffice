package little.goose.account.logic

import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import little.goose.account.appContext
import little.goose.account.logic.data.constant.AccountConstant.EXPENSE
import little.goose.account.logic.data.constant.AccountConstant.INCOME
import little.goose.account.logic.data.constant.MoneyType
import little.goose.account.logic.data.constant.TABLE_TRANSACTION
import little.goose.account.logic.data.database.AccountDatabase
import little.goose.account.logic.data.entities.Transaction
import little.goose.account.utils.*
import java.math.BigDecimal
import java.util.*

object AccountRepository {

    private val database: AccountDatabase = Room.databaseBuilder(
        appContext,
        AccountDatabase::class.java,
        TABLE_TRANSACTION
    ).build()
    private val accountDao = database.accountDao()

    //跑全库方法，不要乱用哦
    fun getAllTransactionFlow() = accountDao.getAllTransactionFlow()

    suspend fun addTransaction(transaction: Transaction) {
        withContext(Dispatchers.IO) {
            accountDao.addTransaction(transaction)
        }
    }

    suspend fun addTransactionList(transactionList: List<Transaction>) {
        withContext(Dispatchers.IO) {
            accountDao.addTransactionList(transactionList)
        }
    }

    suspend fun updateTransaction(transaction: Transaction) {
        withContext(Dispatchers.IO) {
            accountDao.updateTransaction(transaction)
        }
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        withContext(Dispatchers.IO) {
            accountDao.deleteTransaction(transaction)
        }
    }

    suspend fun deleteTransactionList(transactionList: List<Transaction>) {
        withContext(Dispatchers.IO) {
            accountDao.deleteTransactionList(transactionList)
        }
    }

    suspend fun getTransactionCurrentMonth(): List<Transaction> {
        return withContext(Dispatchers.IO) {
            val curCalendar = Calendar.getInstance()
            val curYear = curCalendar.getYear()
            val curMonth = curCalendar.getMonth()
            getTransactionByYearAndMonth(curYear, curMonth)
        }
    }

    suspend fun getTransactionCurrentMonthFlow(): Flow<List<Transaction>> {
        return withContext(Dispatchers.IO) {
            val curCalendar = Calendar.getInstance()
            val curYear = curCalendar.getYear()
            val curMonth = curCalendar.getMonth()
            getTransactionByYearMonthFlow(curYear, curMonth)
        }
    }

    private suspend fun getTransactionByYearAndMonth(year: Int, month: Int): List<Transaction> {
        return withContext(Dispatchers.IO) {
            getTransactionByYearAndMonthRaw(year, month)
        }
    }

    suspend fun getTransactionByYearAndMonthRaw(year: Int, month: Int): List<Transaction> {
        val calendar = Calendar.getInstance()
        calendar.apply {
            clear()
            setDate(1)
            setYear(year)
            setMonth(month)
        }
        val startTime = calendar.timeInMillis
        val dayNum = DateTimeUtils.getDaysByYearMonth(year, month, calendar)
        val endTime = startTime + dayNum * 24L * 3600L * 1000L //日时分秒
        return accountDao.getTransactionByTime(startTime, endTime)
    }

    suspend fun getExpenseSumByYearMonthRaw(year: Int, month: Int): Double {
        val calendar = Calendar.getInstance()
        calendar.apply {
            clear()
            setDate(1)
            setYear(year)
            setMonth(month)
        }
        val startTime = calendar.timeInMillis
        val dayNum = DateTimeUtils.getDaysByYearMonth(year, month, calendar)
        val endTime = startTime + dayNum * 24L * 3600L * 1000L //日时分秒
        return accountDao.getTransactionExpenseSumByTime(startTime, endTime)
    }

    suspend fun getIncomeSumByYearMonthRaw(year: Int, month: Int): Double {
        val calendar = Calendar.getInstance()
        calendar.apply {
            clear()
            setDate(1)
            setYear(year)
            setMonth(month)
        }
        val startTime = calendar.timeInMillis
        val dayNum = DateTimeUtils.getDaysByYearMonth(year, month, calendar)
        val endTime = startTime + dayNum * 24L * 3600L * 1000L //日时分秒
        return accountDao.getTransactionIncomeSumByTime(startTime, endTime)
    }

    suspend fun getTransactionByYearRaw(year: Int): List<Transaction> {
        val calendar = Calendar.getInstance()
        calendar.apply {
            clear()
            setDate(1)
            setYear(year)
        }
        val startTime = calendar.timeInMillis
        calendar.setYear(year + 1)
        val endTime = calendar.timeInMillis
        return accountDao.getTransactionByTime(startTime, endTime)
    }

    suspend fun getExpenseSumByYearRaw(year: Int): Double {
        val calendar = Calendar.getInstance()
        calendar.apply {
            clear()
            setDate(1)
            setYear(year)
        }
        val startTime = calendar.timeInMillis
        calendar.setYear(year + 1)
        val endTime = calendar.timeInMillis
        return accountDao.getTransactionExpenseSumByTime(startTime, endTime)
    }

    suspend fun getIncomeSumByYearRaw(year: Int): Double {
        val calendar = Calendar.getInstance()
        calendar.apply {
            clear()
            setDate(1)
            setYear(year)
        }
        val startTime = calendar.timeInMillis
        calendar.setYear(year + 1)
        val endTime = calendar.timeInMillis
        return accountDao.getTransactionIncomeSumByTime(startTime, endTime)
    }

    fun getTransactionByDateFlow(
        year: Int, month: Int, date: Int, moneyType: MoneyType = MoneyType.BALANCE
    ): Flow<List<Transaction>> {
        val dayRange = getOneDayRange(year, month, date)
        val startTime = dayRange.startTime
        val endTime = dayRange.endTime
        return when (moneyType) {
            MoneyType.INCOME -> accountDao.getTransactionByTimeFlowWithType(
                startTime, endTime, INCOME
            )
            MoneyType.EXPENSE -> accountDao.getTransactionByTimeFlowWithType(
                startTime, endTime, EXPENSE
            )
            else -> accountDao.getTransactionByTimeFlow(startTime, endTime)
        }
    }

    fun getTransactionByYearMonthFlow(
        year: Int, month: Int, moneyType: MoneyType = MoneyType.BALANCE
    ): Flow<List<Transaction>> {
        val calendar = Calendar.getInstance()
        calendar.apply {
            clear()
            setDate(1)
            setYear(year)
            setMonth(month)
        }
        val startTime = calendar.timeInMillis
        val dayNum = DateTimeUtils.getDaysByYearMonth(year, month, calendar)
        val endTime = startTime + dayNum * 24L * 3600L * 1000L //日时分秒
        return when (moneyType) {
            MoneyType.INCOME -> accountDao.getTransactionByTimeFlowWithType(
                startTime, endTime, INCOME
            )
            MoneyType.EXPENSE -> accountDao.getTransactionByTimeFlowWithType(
                startTime, endTime, EXPENSE
            )
            else -> accountDao.getTransactionByTimeFlow(startTime, endTime)
        }
    }

    fun getTransactionByYearFlowWithKeyContent(
        year: Int,
        keyContent: String
    ): Flow<List<Transaction>> {
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.setYear(year)
        calendar.setMonth(1)
        calendar.setDate(1)
        val startTime = calendar.timeInMillis
        calendar.setYear(year + 1)
        val endTime = calendar.timeInMillis
        return accountDao.getTransactionByTimeFlowWithKeyContent(startTime, endTime, keyContent)
    }

    fun getTransactionByYearMonthFlowWithKeyContent(
        year: Int,
        month: Int,
        keyContent: String
    ): Flow<List<Transaction>> {
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.setYear(year)
        calendar.setMonth(month)
        calendar.setDate(1)
        val startTime = calendar.timeInMillis
        calendar.setMonth(month + 1)
        val endTime = calendar.timeInMillis
        return accountDao.getTransactionByTimeFlowWithKeyContent(startTime, endTime, keyContent)
    }

    suspend fun getAllTransactionExpenseSumRaw(): BigDecimal {
        val sum = accountDao.getAllTransactionExpenseSum()
        return if (sum == 0.00) {
            BigDecimal(0)
        } else {
            BigDecimal(sum).getRoundTwo()
        }
    }

    suspend fun getAllTransactionIncomeSumRaw(): BigDecimal {
        val sum = accountDao.getAllTransactionIncomeSum()
        return if (sum == 0.00) {
            BigDecimal(0)
        } else {
            BigDecimal(sum).getRoundTwo()
        }
    }

    suspend fun searchTransactionByMoney(money: String) = withContext(Dispatchers.IO) {
        accountDao.searchTransactionByMoney(money)
    }

    suspend fun searchTransactionByText(text: String) = withContext(Dispatchers.IO) {
        accountDao.searchTransactionByText(text)
    }
}