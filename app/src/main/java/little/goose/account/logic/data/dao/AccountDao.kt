package little.goose.account.logic.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import little.goose.account.logic.data.constant.AccountConstant.EXPENSE
import little.goose.account.logic.data.constant.AccountConstant.INCOME
import little.goose.account.logic.data.constant.MoneyType
import little.goose.account.logic.data.constant.TABLE_TRANSACTION
import little.goose.account.logic.data.entities.Transaction

@Dao
interface AccountDao {

    @Query("SELECT * FROM $TABLE_TRANSACTION ORDER BY time DESC")
    fun getAllTransactionFlow(): Flow<List<Transaction>>

    @Query("SELECT * FROM $TABLE_TRANSACTION ORDER BY time DESC")
    fun getAllTransaction(): List<Transaction>

    @Query("SELECT * FROM $TABLE_TRANSACTION WHERE time > :startTime and time < :endTime ORDER BY time DESC")
    fun getTransactionByTime(startTime: Long, endTime: Long): List<Transaction>

    @Query("SELECT * FROM $TABLE_TRANSACTION WHERE time > :startTime and time < :endTime ORDER BY time DESC")
    fun getTransactionByTimeFlow(startTime: Long, endTime: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM $TABLE_TRANSACTION WHERE time > :startTime and time < :endTime and type == :moneyType ORDER BY time DESC")
    fun getTransactionByTimeFlowWithType(
        startTime: Long, endTime: Long, moneyType: Int
    ): Flow<List<Transaction>>

    @Query("SELECT * FROM $TABLE_TRANSACTION WHERE time > :startTime and time < :endTime and content == :keyContent ORDER BY time DESC")
    fun getTransactionByTimeFlowWithKeyContent(
        startTime: Long, endTime: Long, keyContent: String
    ): Flow<List<Transaction>>

    @Insert
    fun addTransaction(transaction: Transaction)

    @Insert
    fun addTransactionList(transactionList: List<Transaction>)

    @Update
    fun updateTransaction(transaction: Transaction)

    @Delete
    fun deleteTransaction(transaction: Transaction)

    @Delete
    fun deleteTransactionList(transactionList: List<Transaction>)

    @Query("SELECT SUM(money) FROM $TABLE_TRANSACTION")
    fun getAllTransactionSum(): Double

    @Query("SELECT SUM(money) FROM $TABLE_TRANSACTION WHERE type = $EXPENSE")
    fun getAllTransactionExpenseSum(): Double

    @Query("SELECT SUM(money) FROM $TABLE_TRANSACTION WHERE type = $INCOME")
    fun getAllTransactionIncomeSum(): Double

    @Query("SELECT SUM(money) FROM $TABLE_TRANSACTION WHERE time > :startTime and time < :endTime and type = $EXPENSE")
    fun getTransactionExpenseSumByTime(startTime: Long, endTime: Long): Double

    @Query("SELECT SUM(money) FROM $TABLE_TRANSACTION WHERE time > :startTime and time < :endTime and type = $INCOME")
    fun getTransactionIncomeSumByTime(startTime: Long, endTime: Long): Double

    @Query("SELECT * FROM $TABLE_TRANSACTION WHERE money LIKE '%'|| :money ||'%' OR description LIKE '%'|| :money ||'%' ")
    fun searchTransactionByMoney(money: String): List<Transaction>

    @Query("SELECT * FROM $TABLE_TRANSACTION WHERE description LIKE '%'|| :text ||'%' OR content LIKE '%'|| :text ||'%'")
    fun searchTransactionByText(text: String): List<Transaction>
}