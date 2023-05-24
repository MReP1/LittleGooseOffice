package little.goose.memorial.logic

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow
import little.goose.common.utils.getOneDayRange
import little.goose.common.utils.getOneMonthRange
import little.goose.memorial.data.constants.TABLE_MEMORIAL
import little.goose.memorial.data.database.MemorialDatabase
import little.goose.memorial.data.entities.Memorial

class MemorialRepository(context: Context) {

    private val database: MemorialDatabase = Room.databaseBuilder(
        context,
        MemorialDatabase::class.java,
        TABLE_MEMORIAL
    ).build()

    private val memorialDao = database.memorialDao()

    fun getAllMemorialFlow() = memorialDao.getAllMemorialFlow()

    suspend fun insertMemorial(memorial: Memorial) = memorialDao.insertMemorial(memorial)

    suspend fun insertMemorials(memorials: List<Memorial>) = memorialDao.insertMemorials(memorials)

    suspend fun deleteMemorials(memorials: List<Memorial>) = memorialDao.deleteMemorials(memorials)

    suspend fun deleteMemorial(memorial: Memorial) = memorialDao.deleteMemorial(memorial)

    suspend fun updateMemorial(memorial: Memorial) = memorialDao.updateMemorial(memorial)

    fun searchMemorialByTextFlow(keyword: String) = memorialDao.searchMemorialByTextFlow(keyword)

    fun getMemorialAtTopFlow() = memorialDao.getMemorialAtTop()

    suspend fun updateMemorials(memorials: List<Memorial>) = memorialDao.updateMemorials(memorials)

    fun getMemorialFlow(id: Long) = memorialDao.getMemorialFlow(id)

    suspend fun getMemorialsByYearMonth(year: Int, month: Int): List<Memorial> {
        val monthRange = getOneMonthRange(year, month)
        return memorialDao.getMemorialByTime(monthRange.startTime, monthRange.endTime)
    }

    fun getMemorialsByYearMonthFlow(year: Int, month: Int): Flow<List<Memorial>> {
        val monthRange = getOneMonthRange(year, month)
        return memorialDao.getMemorialByTimeFlow(monthRange.startTime, monthRange.endTime)
    }

    fun getMemorialsByDateFlow(year: Int, month: Int, day: Int): Flow<List<Memorial>> {
        val dayRange = getOneDayRange(year, month, day)
        return memorialDao.getMemorialByTimeFlow(dayRange.startTime, dayRange.endTime)
    }

}