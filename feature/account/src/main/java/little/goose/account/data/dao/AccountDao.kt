package little.goose.account.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import little.goose.account.data.constants.AccountConstant.EXPENSE
import little.goose.account.data.constants.AccountConstant.INCOME
import little.goose.account.data.constants.TABLE_TRANSACTION
import little.goose.account.data.entities.Transaction

@Dao
interface AccountDao {

    @Query("SELECT * FROM $TABLE_TRANSACTION ORDER BY time DESC")
    fun getAllTransactionFlow(): Flow<List<Transaction>>

    @Query("SELECT * FROM $TABLE_TRANSACTION ORDER BY time DESC")
    suspend fun getAllTransaction(): List<Transaction>

    @Query("SELECT * FROM $TABLE_TRANSACTION WHERE time > :startTime and time < :endTime ORDER BY time DESC")
    suspend fun getTransactionByTime(startTime: Long, endTime: Long): List<Transaction>

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
    suspend fun addTransaction(transaction: Transaction)

    @Insert
    suspend fun addTransactions(transactionList: List<Transaction>)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Delete
   suspend fun deleteTransactions(transactionList: List<Transaction>)

    @Query("SELECT SUM(money) FROM $TABLE_TRANSACTION")
    suspend fun getAllTransactionSum(): Double

    @Query("SELECT SUM(money) FROM $TABLE_TRANSACTION WHERE type = $EXPENSE")
    suspend fun getAllTransactionExpenseSum(): Double

    @Query("SELECT SUM(money) FROM $TABLE_TRANSACTION WHERE type = $INCOME")
    suspend fun getAllTransactionIncomeSum(): Double

    @Query("SELECT SUM(money) FROM $TABLE_TRANSACTION WHERE time > :startTime and time < :endTime and type = $EXPENSE")
    suspend fun getTransactionExpenseSumByTime(startTime: Long, endTime: Long): Double

    @Query("SELECT SUM(money) FROM $TABLE_TRANSACTION WHERE time > :startTime and time < :endTime and type = $INCOME")
    suspend fun getTransactionIncomeSumByTime(startTime: Long, endTime: Long): Double

    @Query("SELECT * FROM $TABLE_TRANSACTION WHERE money LIKE '%'|| :money ||'%' OR description LIKE '%'|| :money ||'%' ")
    suspend fun searchTransactionByMoney(money: String): List<Transaction>

    @Query("SELECT * FROM $TABLE_TRANSACTION WHERE description LIKE '%'|| :text ||'%' OR content LIKE '%'|| :text ||'%'")
    suspend fun searchTransactionByText(text: String): List<Transaction>
}