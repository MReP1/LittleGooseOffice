package little.goose.account.logic

import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import little.goose.account.appContext
import little.goose.account.logic.data.constant.TABLE_MEMORIAL
import little.goose.account.logic.data.database.MemorialDatabase
import little.goose.account.logic.data.entities.Memorial
import little.goose.account.utils.getOneDayRange
import little.goose.account.utils.getOneMonthRange

object MemorialRepository {
    private val database: MemorialDatabase = Room.databaseBuilder(
        appContext,
        MemorialDatabase::class.java,
        TABLE_MEMORIAL
    ).build()
    private val memorialDao = database.memorialDao()

    fun getAllMemorialFlow() = memorialDao.getAllMemorialFlow()

    suspend fun getAllMemorial() = withContext(Dispatchers.IO) {
        memorialDao.getAllMemorial()
    }

    suspend fun addMemorial(memorial: Memorial) = withContext(Dispatchers.IO) {
        memorialDao.addMemorial(memorial)
    }

    suspend fun addMemorials(memorials: List<Memorial>) = withContext(Dispatchers.IO) {
        memorialDao.addMemorials(memorials)
    }

    suspend fun deleteMemorials(memorials: List<Memorial>) = withContext(Dispatchers.IO) {
        memorialDao.deleteMemorialList(memorials)
    }

    suspend fun deleteMemorial(memorial: Memorial) = withContext(Dispatchers.IO) {
        memorialDao.deleteMemorial(memorial)
    }

    suspend fun updateMemorial(memorial: Memorial) = withContext(Dispatchers.IO) {
        memorialDao.updateMemorial(memorial)
    }

    suspend fun searchMemorialByText(keyword: String) = withContext(Dispatchers.IO) {
        memorialDao.searchMemorialByText(keyword)
    }

    suspend fun getMemorialAtTop() = withContext(Dispatchers.IO) {
        memorialDao.getMemorialAtTop()
    }

    suspend fun updateMemorials(memorials: List<Memorial>) = withContext(Dispatchers.IO) {
        memorialDao.updateMemorials(memorials)
    }

    fun getMemorialsByYearMonthRaw(year: Int, month: Int): List<Memorial> {
        val monthRange = getOneMonthRange(year, month)
        return memorialDao.getMemorialByTime(monthRange.startTime, monthRange.endTime)
    }

    fun getMemorialsByDateFlow(year: Int, month: Int, day: Int): Flow<List<Memorial>> {
        val dayRange = getOneDayRange(year, month, day)
        return memorialDao.getMemorialByTimeFlow(dayRange.startTime, dayRange.endTime)
    }

}