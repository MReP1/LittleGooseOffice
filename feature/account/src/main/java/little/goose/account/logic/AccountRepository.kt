package little.goose.account.logic

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import little.goose.account.data.constants.AccountConstant.EXPENSE
import little.goose.account.data.constants.AccountConstant.INCOME
import little.goose.account.data.constants.MoneyType
import little.goose.account.data.constants.TABLE_TRANSACTION
import little.goose.account.data.database.AccountDatabase
import little.goose.account.data.entities.Transaction
import little.goose.common.utils.DateTimeUtils
import little.goose.common.utils.getOneDayRange
import little.goose.common.utils.getRoundTwo
import little.goose.common.utils.setDate
import little.goose.common.utils.setMonth
import little.goose.common.utils.setYear
import java.math.BigDecimal
import java.util.Calendar

class AccountRepository(
    context: Context
) {

    private val database: AccountDatabase = Room.databaseBuilder(
        context,
        AccountDatabase::class.java,
        TABLE_TRANSACTION
    ).build()
    private val accountDao = database.accountDao()

    private val _deleteTransactionsEvent = MutableSharedFlow<List<Transaction>>()
    val deleteTransactionsEvent = _deleteTransactionsEvent.asSharedFlow()

    //跑全库方法，不要乱用哦
    fun getAllTransactionFlow() = accountDao.getAllTransactionFlow()

    fun getTransactionByIdFlow(id: Long) = accountDao.getTransactionById(id)

    suspend fun insertTransaction(transaction: Transaction) =
        accountDao.insertTransaction(transaction)

    suspend fun updateTransaction(transaction: Transaction) =
        accountDao.updateTransaction(transaction)

    suspend fun deleteTransaction(transaction: Transaction) {
        accountDao.deleteTransaction(transaction)
        _deleteTransactionsEvent.emit(listOf(transaction))
    }

    suspend fun deleteTransactions(transactionList: List<Transaction>) {
        accountDao.deleteTransactions(transactionList)
        _deleteTransactionsEvent.emit(transactionList)
    }

    fun getTransactionsFlowByYearAndMonth(
        year: Int,
        month: Int
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
        return accountDao.getTransactionByTimeFlow(startTime, endTime)
    }

    fun getExpenseSumFlowByYearMonth(year: Int, month: Int): Flow<Double> {
        val calendar = Calendar.getInstance().apply {
            clear()
            setDate(1)
            setYear(year)
            setMonth(month)
        }
        val startTime = calendar.timeInMillis
        val dayNum = DateTimeUtils.getDaysByYearMonth(year, month, calendar)
        val endTime = startTime + dayNum * 24L * 3600L * 1000L //日时分秒
        return accountDao.getTransactionExpenseSumByTimeFlow(startTime, endTime)
    }

    fun getIncomeSumFlowByYearMonth(year: Int, month: Int): Flow<Double> {
        val calendar = Calendar.getInstance().apply {
            clear()
            setDate(1)
            setYear(year)
            setMonth(month)
        }
        val startTime = calendar.timeInMillis
        val dayNum = DateTimeUtils.getDaysByYearMonth(year, month, calendar)
        val endTime = startTime + dayNum * 24L * 3600L * 1000L //日时分秒
        return accountDao.getTransactionIncomeSumByTimeFlow(startTime, endTime)
    }

    fun getTransactionByYearFlow(year: Int): Flow<List<Transaction>> {
        val calendar = Calendar.getInstance().apply {
            clear()
            setDate(1)
            setYear(year)
        }
        val startTime = calendar.timeInMillis
        calendar.setYear(year + 1)
        val endTime = calendar.timeInMillis
        return accountDao.getTransactionByTimeFlow(startTime, endTime)
    }

    fun getExpenseSumFlowByYear(year: Int): Flow<Double> {
        val calendar = Calendar.getInstance().apply {
            clear()
            setDate(1)
            setYear(year)
        }
        val startTime = calendar.timeInMillis
        calendar.setYear(year + 1)
        val endTime = calendar.timeInMillis
        return accountDao.getTransactionExpenseSumByTimeFlow(startTime, endTime)
    }

    fun getIncomeSumFlowByYear(year: Int): Flow<Double> {
        val calendar = Calendar.getInstance().apply {
            clear()
            setDate(1)
            setYear(year)
        }
        val startTime = calendar.timeInMillis
        calendar.setYear(year + 1)
        val endTime = calendar.timeInMillis
        return accountDao.getTransactionIncomeSumByTimeFlow(startTime, endTime)
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

    fun getAllTransactionExpenseSumFlow(): Flow<BigDecimal> {
        return accountDao.getAllTransactionExpenseSumFlow().map { sum ->
            if (sum == 0.00) {
                BigDecimal(0)
            } else {
                BigDecimal(sum).getRoundTwo()
            }
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

    fun searchTransactionByMoneyFlow(money: String) = accountDao.searchTransactionByMoneyFlow(money)

    fun searchTransactionByTextFlow(text: String) = accountDao.searchTransactionByTextFlow(text)
}