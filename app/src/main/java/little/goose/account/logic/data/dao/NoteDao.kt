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
    fun getAllNote(): List<Note>

    @Insert
    fun addNote(note: Note): Long

    @Insert
    fun addNoteList(noteList: List<Note>)

    @Update
    fun updateNote(note: Note)

    @Delete
    fun deleteNote(note: Note)

    @Delete
    fun deleteNoteList(noteList: List<Note>)

    @Query("SELECT * FROM $TABLE_NOTE WHERE title LIKE '%'|| :text ||'%' OR content LIKE '%'|| :text ||'%'")
    fun searchNoteByText(text: String): List<Note>
}