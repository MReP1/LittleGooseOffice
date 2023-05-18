package little.goose.note.logic

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow
import little.goose.common.utils.getOneMonthRange
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

    suspend fun insertNote(note: Note) = noteDao.insertNote(note)

    suspend fun updateNote(note: Note) = noteDao.updateNote(note)

    fun getNoteByYearMonthFlow(year: Int, month: Int): Flow<List<Note>> {
        val timeRange = getOneMonthRange(year, month)
        return noteDao.getNoteByTimeFlow(timeRange.startTime, timeRange.endTime)
    }

    suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)

    suspend fun deleteNotes(noteList: List<Note>) = noteDao.deleteNoteList(noteList)

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

    suspend fun deleteNoteContentBlocks(noteId: Long) {
        return noteDao.deleteNoteContentBlocks(noteId)
    }

    suspend fun deleteNoteContentBlocks(noteContentBlocks: List<Long>) {
        return noteDao.deleteNoteContentBlocks(noteContentBlocks)
    }

    fun getNoteWithContentMapFlow(noteId: Long) = noteDao.getNoteWithContentMapFlow(noteId)

    fun getNoteWithContentMapFlowByKeyword(keyword: String): Flow<Map<Note, List<NoteContentBlock>>> {
        return noteDao.getNoteWithContentMapFlowByKeyword(keyword)
    }

    fun getNoteWithContentMapFlow() = noteDao.getNoteWithContentMapFlow()

}