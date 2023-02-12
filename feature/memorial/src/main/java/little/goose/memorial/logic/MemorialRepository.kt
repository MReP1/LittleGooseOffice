package little.goose.memorial.logic

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow
import little.goose.common.utils.getOneDayRange
import little.goose.common.utils.getOneMonthRange
import little.goose.memorial.data.constant.TABLE_MEMORIAL
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

    suspend fun addMemorial(memorial: Memorial) = memorialDao.addMemorial(memorial)

    suspend fun addMemorials(memorials: List<Memorial>) = memorialDao.addMemorials(memorials)

    suspend fun deleteMemorials(memorials: List<Memorial>) = memorialDao.deleteMemorials(memorials)

    suspend fun deleteMemorial(memorial: Memorial) = memorialDao.deleteMemorial(memorial)

    suspend fun updateMemorial(memorial: Memorial) = memorialDao.updateMemorial(memorial)

    suspend fun searchMemorialByText(keyword: String) = memorialDao.searchMemorialByText(keyword)

    fun getMemorialAtTopFlow() = memorialDao.getMemorialAtTop()

    suspend fun updateMemorials(memorials: List<Memorial>) = memorialDao.updateMemorials(memorials)

    suspend fun getMemorialsByYearMonth(year: Int, month: Int): List<Memorial> {
        val monthRange = getOneMonthRange(year, month)
        return memorialDao.getMemorialByTime(monthRange.startTime, monthRange.endTime)
    }

    fun getMemorialsByDateFlow(year: Int, month: Int, day: Int): Flow<List<Memorial>> {
        val dayRange = getOneDayRange(year, month, day)
        return memorialDao.getMemorialByTimeFlow(dayRange.startTime, dayRange.endTime)
    }

}