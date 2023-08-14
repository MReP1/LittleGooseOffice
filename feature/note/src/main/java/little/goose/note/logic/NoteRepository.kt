package little.goose.note.logic

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import little.goose.note.data.constants.TABLE_NOTE
import little.goose.note.data.database.NoteDatabase
import little.goose.note.data.entities.Note
import little.goose.note.data.entities.NoteContentBlock

class NoteRepository(context: Context) {

    private val database: NoteDatabase = Room.databaseBuilder(
        context, NoteDatabase::class.java, TABLE_NOTE
    ).addMigrations(NoteDatabase.MIGRATION_1_2)
        .addMigrations(NoteDatabase.MIGRATION_2_3)
        .addMigrations(NoteDatabase.MIGRATION_3_4)
        .build()

    private val noteDao = database.noteDao()

    private val _deleteNotesEvent: MutableSharedFlow<List<Note>> = MutableSharedFlow()
    val deleteNotesEvent = _deleteNotesEvent.asSharedFlow()

    suspend fun insertNote(note: Note) = noteDao.insertNote(note)

    suspend fun updateNote(note: Note) = noteDao.updateNote(note)

    suspend fun deleteNoteAndItsBlocks(note: Note) {
        noteDao.deleteNoteAndItsBlocks(note)
        _deleteNotesEvent.emit(listOf(note))
    }

    suspend fun deleteNotesAndItsBlocks(notes: List<Note>) {
        noteDao.deleteNotesAndItsBlocks(notes)
        _deleteNotesEvent.emit(notes)
    }

    fun getNoteFlow(noteId: Long) = noteDao.getNoteFlow(noteId)

    /**
     * Content Block
     */

    suspend fun updateNoteContentBlock(noteContentBlock: NoteContentBlock) {
        noteDao.updateNoteContentBlock(noteContentBlock)
    }

    suspend fun updateNoteContentBlocks(noteContentBlocks: List<NoteContentBlock>) {
        noteDao.updateNoteContentBlocks(noteContentBlocks)
    }

    suspend fun insertNoteContentBlock(noteContentBlock: NoteContentBlock): Long {
        return noteDao.insertNoteContentBlock(noteContentBlock)
    }

    suspend fun deleteNoteContentBlock(noteContentBlock: NoteContentBlock) {
        return noteDao.deleteNoteContentBlock(noteContentBlock)
    }

    fun getNoteWithContentMapFlow(noteId: Long) = noteDao.getNoteWithContentMapFlow(noteId)

    fun getNoteWithContentMapFlowByKeyword(keyword: String): Flow<Map<Note, List<NoteContentBlock>>> {
        return noteDao.getNoteWithContentMapFlowByKeyword(keyword)
    }

    fun getNoteWithContentMapFlow() = noteDao.getNoteWithContentMapFlow()

}