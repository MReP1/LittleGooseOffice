package little.goose.account.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import little.goose.account.data.entities.Transaction
import little.goose.common.converters.CommonTypeConverters

@Database(entities = [Transaction::class], version = 1, exportSchema = false)
@TypeConverters(CommonTypeConverters::class)
abstract class AccountDatabase : RoomDatabase() {
    abstract fun accountDao(): little.goose.account.data.dao.AccountDao
}