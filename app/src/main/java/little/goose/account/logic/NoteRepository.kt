package little.goose.account.logic

import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import little.goose.account.AccountApplication
import little.goose.account.appContext
import little.goose.account.logic.data.constant.TABLE_NOTE
import little.goose.account.logic.data.database.NoteDatabase
import little.goose.account.logic.data.entities.Note

object NoteRepository {
    private val database: NoteDatabase = Room.databaseBuilder(
        appContext,
        NoteDatabase::class.java,
        TABLE_NOTE
    ).build()
    private val noteDao = database.noteDao()

    suspend fun addNote(note: Note) = withContext(Dispatchers.IO) {
        noteDao.addNote(note)
    }

    suspend fun addNoteList(noteList: List<Note>) {
        withContext(Dispatchers.IO) {
            noteDao.addNoteList(noteList)
        }
    }

    suspend fun updateNote(note: Note) {
        withContext(Dispatchers.IO) {
            noteDao.updateNote(note)
        }
    }

    suspend fun getAllNote() = withContext(Dispatchers.IO) {
        noteDao.getAllNote()
    }

    fun getAllNoteFlow() = noteDao.getAllNoteFlow()

    suspend fun deleteNote(note: Note) {
        withContext(Dispatchers.IO) {
            noteDao.deleteNote(note)
        }
    }

    suspend fun deleteNoteList(noteList: List<Note>) {
        withContext(Dispatchers.IO) {
            noteDao.deleteNoteList(noteList)
        }
    }

    suspend fun searchNoteByText(text: String) = withContext(Dispatchers.IO) {
        noteDao.searchNoteByText(text)
    }

}