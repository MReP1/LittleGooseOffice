package little.goose.account.logic

import androidx.room.Room
import kotlinx.coroutines.flow.Flow
import little.goose.account.appContext
import little.goose.account.logic.data.constant.TABLE_SCHEDULE
import little.goose.account.logic.data.database.ScheduleDatabase
import little.goose.account.logic.data.entities.Schedule
import little.goose.account.utils.getOneDayRange
import little.goose.account.utils.getOneMonthRange

object ScheduleRepository {

    private val database: ScheduleDatabase = Room.databaseBuilder(
        appContext,
        ScheduleDatabase::class.java,
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

    suspend fun searchScheduleByText(keyWord: String) = scheduleDao.searchScheduleByText(keyWord)

    fun getScheduleByDateFlow(year: Int, month: Int, date: Int): Flow<List<Schedule>> {
        val dayRange = getOneDayRange(year, month, date)
        val startTime = dayRange.startTime
        val endTime = dayRange.endTime
        return scheduleDao.getScheduleByTimeFlow(startTime, endTime)
    }

}