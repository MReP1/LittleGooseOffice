package little.goose.schedule.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import little.goose.schedule.data.constants.TABLE_SCHEDULE

@Dao
interface ScheduleDao {

    @Query("SELECT * FROM $TABLE_SCHEDULE ORDER BY time DESC")
    fun getAllScheduleFlow(): Flow<List<little.goose.schedule.data.entities.Schedule>>

    @Query("SELECT * FROM $TABLE_SCHEDULE ORDER BY time DESC")
    suspend fun getAllSchedule(): List<little.goose.schedule.data.entities.Schedule>

    @Query("SELECT * FROM $TABLE_SCHEDULE WHERE time > :startTime and time < :endTime ORDER BY time DESC")
    suspend fun getScheduleByTime(startTime: Long, endTime: Long): List<little.goose.schedule.data.entities.Schedule>

    @Query("SELECT * FROM $TABLE_SCHEDULE WHERE time > :startTime and time < :endTime ORDER BY time DESC")
    fun getScheduleByTimeFlow(startTime: Long, endTime: Long): Flow<List<little.goose.schedule.data.entities.Schedule>>

    @Query("SELECT * FROM $TABLE_SCHEDULE WHERE title LIKE '%'|| :keyWord ||'%' OR content LIKE '%'|| :keyWord ||'%' ")
    suspend fun searchScheduleByText(keyWord: String): List<little.goose.schedule.data.entities.Schedule>

    @Insert
    suspend fun addSchedule(schedule: little.goose.schedule.data.entities.Schedule)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSchedules(scheduleList: List<little.goose.schedule.data.entities.Schedule>)

    @Update
    suspend fun updateSchedule(schedule: little.goose.schedule.data.entities.Schedule)

    @Delete
    suspend fun deleteSchedule(schedule: little.goose.schedule.data.entities.Schedule)

    @Delete
    suspend fun deleteScheduleList(scheduleList: List<little.goose.schedule.data.entities.Schedule>)
}