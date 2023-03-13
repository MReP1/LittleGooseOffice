package little.goose.note.logic

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow
import little.goose.common.utils.getOneMonthRange
import little.goose.note.data.constants.TABLE_NOTE
import little.goose.note.data.database.NoteDatabase
import little.goose.note.data.entities.Note

class NoteRepository(context: Context) {
    private val database: NoteDatabase = Room.databaseBuilder(
        context,
        NoteDatabase::class.java,
        TABLE_NOTE
    ).build()

    private val noteDao = database.noteDao()

    suspend fun addNote(note: Note) = noteDao.addNote(note)

    suspend fun addNoteList(noteList: List<Note>) = noteDao.addNoteList(noteList)

    suspend fun updateNote(note: Note) = noteDao.updateNote(note)

    suspend fun getAllNote() = noteDao.getAllNote()

    fun getNoteByYearMonthFlow(year: Int, month: Int): Flow<List<Note>> {
        val timeRange = getOneMonthRange(year, month)
        return noteDao.getNoteByTimeFlow(timeRange.startTime, timeRange.endTime)
    }

    fun getAllNoteFlow() = noteDao.getAllNoteFlow()

    suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)

    suspend fun deleteNoteList(noteList: List<Note>) = noteDao.deleteNoteList(noteList)

    suspend fun searchNoteByText(text: String) = noteDao.searchNoteByText(text)

}