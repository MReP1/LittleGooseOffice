package little.goose.account.logic.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import little.goose.account.logic.data.dao.AccountDao
import little.goose.account.logic.data.entities.Transaction
import little.goose.common.converters.CommonTypeConverters

@Database(entities = [Transaction::class], version = 1, exportSchema = false)
@TypeConverters(CommonTypeConverters::class)
abstract class AccountDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
}