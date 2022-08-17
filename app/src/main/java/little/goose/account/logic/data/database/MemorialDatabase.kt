package little.goose.account.logic.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import little.goose.account.logic.data.dao.MemorialDao
import little.goose.account.logic.data.entities.Memorial
import little.goose.account.logic.data.entities.Note

@Database(entities = [Memorial::class], version = 1, exportSchema = false)
@TypeConverters(CommonTypeConverters::class)
abstract class MemorialDatabase: RoomDatabase() {
    abstract fun memorialDao(): MemorialDao
}