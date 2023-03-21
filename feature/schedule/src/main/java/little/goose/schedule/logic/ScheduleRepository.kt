package little.goose.schedule.logic

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow
import little.goose.common.utils.getOneDayRange
import little.goose.common.utils.getOneMonthRange
import little.goose.schedule.data.constants.TABLE_SCHEDULE
import little.goose.schedule.data.entities.Schedule

class ScheduleRepository(context: Context) {

    private val database: little.goose.schedule.data.database.ScheduleDatabase = Room.databaseBuilder(
        context,
        little.goose.schedule.data.database.ScheduleDatabase::class.java,
        TABLE_SCHEDULE
    ).build()

    private val scheduleDao = database.scheduleDao()

    suspend fun addSchedule(schedule: Schedule) = scheduleDao.addSchedule(schedule)

    suspend fun addSchedules(schedules: List<Schedule>) = scheduleDao.addSchedules(schedules)

    suspend fun updateSchedule(schedule: Schedule) = scheduleDao.updateSchedule(schedule)

    fun getAllScheduleFlow(): Flow<List<Schedule>> = scheduleDao.getAllScheduleFlow()

    suspend fun getAllSchedule(): List<Schedule> = scheduleDao.getAllSchedule()

    suspend fun deleteSchedule(schedule: Schedule) = scheduleDao.deleteSchedule(schedule)

    suspend fun deleteSchedules(schedules: List<Schedule>) =
        scheduleDao.deleteScheduleList(schedules)

    suspend fun getScheduleByYearMonth(year: Int, month: Int): List<Schedule> {
        val monthRange = getOneMonthRange(year, month)
        return scheduleDao.getScheduleByTime(monthRange.startTime, monthRange.endTime)
    }

    fun getScheduleByYearMonthFlow(year: Int, month: Int): Flow<List<Schedule>> {
        val monthRange = getOneMonthRange(year, month)
        return scheduleDao.getScheduleByTimeFlow(monthRange.startTime, monthRange.endTime)
    }

    fun searchScheduleByTextFlow(keyWord: String) = scheduleDao.searchScheduleByTextFlow(keyWord)

    fun getScheduleByDateFlow(year: Int, month: Int, date: Int): Flow<List<Schedule>> {
        val dayRange = getOneDayRange(year, month, date)
        val startTime = dayRange.startTime
        val endTime = dayRange.endTime
        return scheduleDao.getScheduleByTimeFlow(startTime, endTime)
    }

}