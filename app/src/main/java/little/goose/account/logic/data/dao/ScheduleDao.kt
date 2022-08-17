package little.goose.account.logic.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import little.goose.account.logic.data.constant.TABLE_SCHEDULE
import little.goose.account.logic.data.entities.Schedule

@Dao
interface ScheduleDao {

    @Query("SELECT * FROM $TABLE_SCHEDULE ORDER BY time DESC")
    fun getAllScheduleFlow(): Flow<List<Schedule>>

    @Query("SELECT * FROM $TABLE_SCHEDULE ORDER BY time DESC")
    fun getAllSchedule(): List<Schedule>

    @Query("SELECT * FROM $TABLE_SCHEDULE WHERE time > :startTime and time < :endTime ORDER BY time DESC")
    fun getScheduleByTime(startTime: Long, endTime: Long): List<Schedule>

    @Query("SELECT * FROM $TABLE_SCHEDULE WHERE time > :startTime and time < :endTime ORDER BY time DESC")
    fun getScheduleByTimeFlow(startTime: Long, endTime: Long): Flow<List<Schedule>>

    @Query("SELECT * FROM $TABLE_SCHEDULE WHERE title LIKE '%'|| :keyWord ||'%' OR content LIKE '%'|| :keyWord ||'%' ")
    fun searchScheduleByText(keyWord: String): List<Schedule>

    @Insert
    fun addSchedule(schedule: Schedule)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addScheduleList(scheduleList: List<Schedule>)

    @Update
    fun updateSchedule(schedule: Schedule)

    @Delete
    fun deleteSchedule(schedule: Schedule)

    @Delete
    fun deleteScheduleList(scheduleList: List<Schedule>)
}