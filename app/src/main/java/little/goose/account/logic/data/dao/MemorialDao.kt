package little.goose.account.logic.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import little.goose.account.logic.data.constant.TABLE_MEMORIAL
import little.goose.account.logic.data.entities.Memorial

@Dao
interface MemorialDao {
    @Insert
    fun addMemorial(memorial: Memorial): Long

    @Insert
    fun addMemorials(memorials: List<Memorial>)

    @Update
    fun updateMemorial(memorial: Memorial)

    @Update
    fun updateMemorials(memorials: List<Memorial>)

    @Query("SELECT * FROM $TABLE_MEMORIAL ORDER BY time DESC")
    fun getAllMemorial(): List<Memorial>

    @Query("SELECT * FROM $TABLE_MEMORIAL ORDER BY time DESC")
    fun getAllMemorialFlow(): Flow<List<Memorial>>

    @Query("SELECT * FROM $TABLE_MEMORIAL WHERE content LIKE '%'|| :keyword ||'%' ORDER BY time DESC")
    fun searchMemorialByText(keyword: String): List<Memorial>

    @Query("SELECT * FROM $TABLE_MEMORIAL WHERE isTop = 1 ORDER BY time DESC")
    fun getMemorialAtTop(): List<Memorial>

    @Query("SELECT * FROM $TABLE_MEMORIAL WHERE time > :startTime and time < :endTime ORDER BY time DESC")
    fun getMemorialByTime(startTime: Long, endTime: Long): List<Memorial>

    @Query("SELECT * FROM $TABLE_MEMORIAL WHERE time > :startTime and time < :endTime ORDER BY time DESC")
    fun getMemorialByTimeFlow(startTime: Long, endTime: Long): Flow<List<Memorial>>
    @Delete
    fun deleteMemorial(memorial: Memorial)

    @Delete
    fun deleteMemorialList(memorials: List<Memorial>)
}