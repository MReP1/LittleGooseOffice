package little.goose.data.note.local

import app.cash.sqldelight.coroutines.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
import little.goose.note.GooseNoteDatabase
import littlegoosenote.GooseNote

class SqlDelightNoteDatabase(
    private val database: GooseNoteDatabase
) {

    fun getNoteFlow(noteId: Long): Flow<GooseNote> {
        return database.gooseNoteQueries
            .getNote(noteId)
            .asFlow()
            .mapNotNull { it.executeAsOneOrNull() }
    }

    suspend fun insertOrReplaceNote(note: GooseNote): Long = withContext(Dispatchers.IO) {
        database.gooseNoteQueries
            .insertOrReplaceNote(note.id, note.title, note.time)
            .executeAsOne()
    }

    suspend fun getNoteWithContentFlow(noteId: Long) {
        database.gooseNoteQueries.getNoteWithContent(noteId).executeAsList().map {

        }
    }

    suspend fun deleteNoteAndItsBlocks(noteId: Long) = withContext(Dispatchers.IO) {
        database.gooseNoteQueries.deleteNoteAndItsBlocks(noteId)
    }

    suspend fun deleteBlock(id: Long) = withContext(Dispatchers.IO) {
        database.gooseNoteQueries.deleteNoteContentBlock(id)
    }

    suspend fun deleteBlockWithNoteId(noteId: Long) = withContext(Dispatchers.IO) {
        database.gooseNoteQueries.deleteNoteContentBlockWithNoteId(noteId)
    }

}