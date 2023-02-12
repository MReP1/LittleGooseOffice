package little.goose.schedule.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import little.goose.common.converters.CommonTypeConverters
import little.goose.schedule.data.dao.ScheduleDao

@Database(entities = [little.goose.schedule.data.entities.Schedule::class], version = 1, exportSchema = false)
@TypeConverters(CommonTypeConverters::class)
abstract class ScheduleDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
}