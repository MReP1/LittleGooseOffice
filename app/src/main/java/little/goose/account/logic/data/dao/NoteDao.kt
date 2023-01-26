package little.goose.account.logic.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import little.goose.account.logic.data.constant.TABLE_NOTE
import little.goose.account.logic.data.entities.Note

@Dao
interface NoteDao {
    @Query("SELECT * FROM $TABLE_NOTE ORDER BY time DESC")
    fun getAllNoteFlow(): Flow<List<Note>>

    @Query("SELECT * FROM $TABLE_NOTE ORDER BY time DESC")
    suspend fun getAllNote(): List<Note>

    @Insert
    suspend fun addNote(note: Note): Long

    @Insert
    suspend fun addNoteList(noteList: List<Note>)

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Delete
    suspend fun deleteNoteList(noteList: List<Note>)

    @Query("SELECT * FROM $TABLE_NOTE WHERE title LIKE '%'|| :text ||'%' OR content LIKE '%'|| :text ||'%'")
    suspend fun searchNoteByText(text: String): List<Note>
}