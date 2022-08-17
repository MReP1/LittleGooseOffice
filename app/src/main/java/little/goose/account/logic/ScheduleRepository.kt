package little.goose.account.logic

import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import little.goose.account.AccountApplication
import little.goose.account.logic.data.constant.TABLE_SCHEDULE
import little.goose.account.logic.data.database.ScheduleDatabase
import little.goose.account.logic.data.entities.Schedule
import little.goose.account.utils.*
import java.util.*

object ScheduleRepository {
    private val database: ScheduleDatabase = Room.databaseBuilder(
        AccountApplication.context,
        ScheduleDatabase::class.java,
        TABLE_SCHEDULE
    ).build()
    private val scheduleDao = database.scheduleDao()

    suspend fun addSchedule(schedule: Schedule) {
        withContext(Dispatchers.IO) {
            scheduleDao.addSchedule(schedule)
        }
    }

    suspend fun addScheduleList(scheduleList: List<Schedule>) {
        withContext(Dispatchers.IO) {
            scheduleDao.addScheduleList(scheduleList)
        }
    }

    suspend fun updateSchedule(schedule: Schedule) {
        withContext(Dispatchers.IO) {
            scheduleDao.updateSchedule(schedule)
        }
    }

    fun getAllScheduleFlow(): Flow<List<Schedule>> = scheduleDao.getAllScheduleFlow()

    suspend fun getAllSchedule(): List<Schedule> = withContext(Dispatchers.IO) {
        scheduleDao.getAllSchedule()
    }

    suspend fun deleteSchedule(schedule: Schedule) {
        withContext(Dispatchers.IO) {
            scheduleDao.deleteSchedule(schedule)
        }
    }

    suspend fun deleteScheduleList(scheduleList: List<Schedule>) {
        withContext(Dispatchers.IO) {
            scheduleDao.deleteScheduleList(scheduleList)
        }
    }

    suspend fun getScheduleByDate(year: Int, month: Int, date: Int): List<Schedule> {
        return withContext(Dispatchers.IO) {
            val dayRange = getOneDayRange(year, month, date)
            val startTime = dayRange.startTime
            val endTime = dayRange.endTime
            scheduleDao.getScheduleByTime(startTime, endTime)
        }
    }

    fun getScheduleByYearMonthRaw(year: Int, month: Int): List<Schedule> {
        val monthRange = getOneMonthRange(year, month)
        return scheduleDao.getScheduleByTime(monthRange.startTime, monthRange.endTime)
    }

    suspend fun searchScheduleByText(keyWord: String) = withContext(Dispatchers.IO) {
        scheduleDao.searchScheduleByText(keyWord)
    }

    fun getScheduleByDateFlow(year: Int, month: Int, date: Int): Flow<List<Schedule>> {
        val dayRange = getOneDayRange(year, month, date)
        val startTime = dayRange.startTime
        val endTime = dayRange.endTime
        return scheduleDao.getScheduleByTimeFlow(startTime, endTime)
    }
}