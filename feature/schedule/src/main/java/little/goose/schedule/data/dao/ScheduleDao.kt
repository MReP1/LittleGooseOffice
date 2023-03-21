package little.goose.schedule.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import little.goose.schedule.data.constants.TABLE_SCHEDULE
import little.goose.schedule.data.entities.Schedule

@Dao
interface ScheduleDao {

    @Query("SELECT * FROM $TABLE_SCHEDULE ORDER BY time DESC")
    fun getAllScheduleFlow(): Flow<List<Schedule>>

    @Query("SELECT * FROM $TABLE_SCHEDULE ORDER BY time DESC")
    suspend fun getAllSchedule(): List<Schedule>

    @Query("SELECT * FROM $TABLE_SCHEDULE WHERE time > :startTime and time < :endTime ORDER BY time DESC")
    suspend fun getScheduleByTime(startTime: Long, endTime: Long): List<Schedule>

    @Query("SELECT * FROM $TABLE_SCHEDULE WHERE time > :startTime and time < :endTime ORDER BY time DESC")
    fun getScheduleByTimeFlow(startTime: Long, endTime: Long): Flow<List<Schedule>>

    @Query("SELECT * FROM $TABLE_SCHEDULE WHERE title LIKE '%'|| :keyWord ||'%' OR content LIKE '%'|| :keyWord ||'%' ")
    fun searchScheduleByTextFlow(keyWord: String): Flow<List<Schedule>>

    @Insert
    suspend fun addSchedule(schedule: Schedule)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSchedules(scheduleList: List<Schedule>)

    @Update
    suspend fun updateSchedule(schedule: Schedule)

    @Delete
    suspend fun deleteSchedule(schedule: Schedule)

    @Delete
    suspend fun deleteScheduleList(scheduleList: List<Schedule>)
}