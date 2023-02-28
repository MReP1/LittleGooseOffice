package little.goose.account.logic

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import little.goose.account.data.constants.AccountConstant.EXPENSE
import little.goose.account.data.constants.AccountConstant.INCOME
import little.goose.account.data.constants.MoneyType
import little.goose.account.data.constants.TABLE_TRANSACTION
import little.goose.account.data.entities.Transaction
import little.goose.common.utils.*
import java.math.BigDecimal
import java.util.*

class AccountRepository(
    context: Context
) {

    private val database: little.goose.account.data.database.AccountDatabase = Room.databaseBuilder(
        context,
        little.goose.account.data.database.AccountDatabase::class.java,
        TABLE_TRANSACTION
    ).build()
    private val accountDao = database.accountDao()

    //跑全库方法，不要乱用哦
    fun getAllTransactionFlow() = accountDao.getAllTransactionFlow()

    suspend fun insertTransaction(transaction: Transaction) =
        accountDao.insertTransaction(transaction)

    suspend fun addTransactions(transactions: List<Transaction>) =
        accountDao.insertTransactions(transactions)


    suspend fun updateTransaction(transaction: Transaction) =
        accountDao.updateTransaction(transaction)


    suspend fun deleteTransaction(transaction: Transaction) =
        accountDao.deleteTransaction(transaction)

    suspend fun deleteTransactions(transactionList: List<Transaction>) =
        accountDao.deleteTransactions(transactionList)


    suspend fun getTransactionCurrentMonth(): List<Transaction> {
        val curCalendar = Calendar.getInstance()
        val curYear = curCalendar.getYear()
        val curMonth = curCalendar.getMonth()
        return getTransactionsByYearAndMonth(curYear, curMonth)
    }

    fun getTransactionCurrentMonthFlow(): Flow<List<Transaction>> {
        val curCalendar = Calendar.getInstance()
        val curYear = curCalendar.getYear()
        val curMonth = curCalendar.getMonth()
        return getTransactionByYearMonthFlow(curYear, curMonth)
    }

    suspend fun getTransactionsByYearAndMonth(
        year: Int,
        month: Int
    ): List<Transaction> {
        val calendar = Calendar.getInstance().apply {
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

    suspend fun getExpenseSumByYearMonth(year: Int, month: Int): Double {
        val calendar = Calendar.getInstance().apply {
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

    suspend fun getIncomeSumByYearMonth(year: Int, month: Int): Double {
        val calendar = Calendar.getInstance().apply {
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

    suspend fun getTransactionByYear(year: Int): List<Transaction> {
        val calendar = Calendar.getInstance().apply {
            clear()
            setDate(1)
            setYear(year)
        }
        val startTime = calendar.timeInMillis
        calendar.setYear(year + 1)
        val endTime = calendar.timeInMillis
        return accountDao.getTransactionByTime(startTime, endTime)
    }

    suspend fun getExpenseSumByYear(year: Int): Double {
        val calendar = Calendar.getInstance().apply {
            clear()
            setDate(1)
            setYear(year)
        }
        val startTime = calendar.timeInMillis
        calendar.setYear(year + 1)
        val endTime = calendar.timeInMillis
        return accountDao.getTransactionExpenseSumByTime(startTime, endTime)
    }

    suspend fun getIncomeSumByYear(year: Int): Double {
        val calendar = Calendar.getInstance().apply {
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
        val calendar = Calendar.getInstance().apply {
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

    suspend fun getAllTransactionExpenseSum(): BigDecimal {
        val sum = accountDao.getAllTransactionExpenseSum()
        return if (sum == 0.00) {
            BigDecimal(0)
        } else {
            BigDecimal(sum).getRoundTwo()
        }
    }

    fun getAllTransactionExpenseSumFlow(): Flow<BigDecimal> {
        return accountDao.getAllTransactionExpenseSumFlow().map { sum ->
            if (sum == 0.00) {
                BigDecimal(0)
            } else {
                BigDecimal(sum).getRoundTwo()
            }
        }
    }

    suspend fun getAllTransactionIncomeSum(): BigDecimal {
        val sum = accountDao.getAllTransactionIncomeSum()
        return if (sum == 0.00) {
            BigDecimal(0)
        } else {
            BigDecimal(sum).getRoundTwo()
        }
    }

    fun getAllTransactionIncomeSumFlow(): Flow<BigDecimal> {
        return accountDao.getAllTransactionIncomeSumFlow().map { sum ->
            if (sum == 0.00) {
                BigDecimal(0)
            } else {
                BigDecimal(sum).getRoundTwo()
            }
        }
    }

    suspend fun searchTransactionByMoney(money: String) = accountDao.searchTransactionByMoney(money)

    suspend fun searchTransactionByText(text: String) = accountDao.searchTransactionByText(text)
}