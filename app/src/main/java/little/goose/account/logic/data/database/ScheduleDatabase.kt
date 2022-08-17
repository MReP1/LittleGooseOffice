package little.goose.account.logic.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import little.goose.account.logic.data.dao.ScheduleDao
import little.goose.account.logic.data.entities.Schedule

@Database(entities = [Schedule::class], version = 1, exportSchema = false)
@TypeConverters(CommonTypeConverters::class)
abstract class ScheduleDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
}