package little.goose.memorial.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import little.goose.common.converters.CommonTypeConverters
import little.goose.memorial.data.dao.MemorialDao
import little.goose.memorial.data.entities.Memorial

@Database(entities = [Memorial::class], version = 1, exportSchema = false)
@TypeConverters(CommonTypeConverters::class)
abstract class MemorialDatabase: RoomDatabase() {
    abstract fun memorialDao(): MemorialDao
}