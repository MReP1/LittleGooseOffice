package little.goose.memorial.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import little.goose.memorial.data.constants.TABLE_MEMORIAL
import little.goose.memorial.data.entities.Memorial

@Dao
interface MemorialDao {
    @Insert
    suspend fun addMemorial(memorial: Memorial): Long

    @Insert
    suspend fun addMemorials(memorials: List<Memorial>)

    @Update
    suspend fun updateMemorial(memorial: Memorial)

    @Update
    suspend fun updateMemorials(memorials: List<Memorial>)

    @Query("SELECT * FROM $TABLE_MEMORIAL ORDER BY time DESC")
    suspend fun getAllMemorial(): List<Memorial>

    @Query("SELECT * FROM $TABLE_MEMORIAL WHERE content LIKE '%'|| :keyword ||'%' ORDER BY time DESC")
    fun searchMemorialByTextFlow(keyword: String): Flow<List<Memorial>>

    @Query("SELECT * FROM $TABLE_MEMORIAL WHERE isTop = 1 ORDER BY time DESC")
    fun getMemorialAtTop(): Flow<List<Memorial>>

    @Query("SELECT * FROM $TABLE_MEMORIAL WHERE time > :startTime and time < :endTime ORDER BY time DESC")
    suspend fun getMemorialByTime(startTime: Long, endTime: Long): List<Memorial>

    @Delete
    suspend fun deleteMemorial(memorial: Memorial)

    @Delete
    suspend fun deleteMemorials(memorials: List<Memorial>)

    @Query("SELECT * FROM $TABLE_MEMORIAL ORDER BY time DESC")
    fun getAllMemorialFlow(): Flow<List<Memorial>>

    @Query("SELECT * FROM $TABLE_MEMORIAL WHERE time > :startTime and time < :endTime ORDER BY time DESC")
    fun getMemorialByTimeFlow(startTime: Long, endTime: Long): Flow<List<Memorial>>
}