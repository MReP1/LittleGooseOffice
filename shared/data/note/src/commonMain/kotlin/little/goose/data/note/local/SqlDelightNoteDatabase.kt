package little.goose.data.note.local

import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
import little.goose.data.note.bean.Note
import little.goose.data.note.bean.NoteContentBlock
import little.goose.data.note.bean.NoteWithContent
import little.goose.note.GooseNoteDatabase
import log

class SqlDelightNoteDatabase(
    private val database: GooseNoteDatabase
) : NoteDataBase {

    override fun getNoteFlow(noteId: Long): Flow<Note> {
        return database.gooseNoteQueries
            .getNote(noteId) { id, title, time -> Note(id, title, time) }
            .asFlow()
            .mapNotNull { it.executeAsOneOrNull() }
    }

    override suspend fun insertOrReplaceNote(note: Note): Long = withContext(Dispatchers.IO) {
        database.gooseNoteQueries.transactionWithResult {
            database.gooseNoteQueries.insertOrReplaceNote(note.id, note.title, note.time)
            database.gooseNoteQueries.lastInsertedNoteRowId().executeAsList().first()
        }
    }

    override fun getNoteWithContentFlow(noteId: Long): Flow<NoteWithContent> {
        return flow {
            val channel = Channel<Unit>(Channel.CONFLATED)
            channel.trySend(Unit)
            val noteQuery = database.gooseNoteQueries.getNote(noteId)
            val noteContentBlockQuery = database.gooseNoteQueries.getNoteContentBlocks(noteId)
            val noteListener = Query.Listener {
                log("noteListener")
                channel.trySend(Unit)
            }
            val noteContentBlockListener = Query.Listener {
                log("noteContentBlockListener!")
                channel.trySend(Unit)
            }
            noteQuery.addListener(noteListener)
            noteContentBlockQuery.addListener(noteContentBlockListener)
            try {
                for (item in channel) {
                    log("fetch something in channel")
                    var note: Note? = null
                    val blocks = mutableListOf<NoteContentBlock>()
                    database.gooseNoteQueries.getNoteWithContent(
                        noteId
                    ) { id: Long, title: String, time: Long,
                        blockId: Long, noteId: Long, content: String, sectionIndex: Long ->
                        if (note == null) {
                            note = Note(id, title, time)
                        }
                        blocks.add(NoteContentBlock(blockId, noteId, content, sectionIndex))
                    }.executeAsList()
                    val actualNote = note
                        ?: database.gooseNoteQueries.getNote(noteId).executeAsOneOrNull()?.let {
                            log("execute On or null")
                            Note(id = it.id, title = it.title, time = it.time)
                        }
                    actualNote?.let {
                        emit(NoteWithContent(it, blocks))
                    }
                }
            } finally {
                noteQuery.removeListener(noteListener)
                noteContentBlockQuery.removeListener(noteContentBlockListener)
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun deleteNoteAndItsBlocks(noteId: Long) = withContext(Dispatchers.IO) {
        database.gooseNoteQueries.deleteNoteAndItsBlocks(noteId)
    }

    override suspend fun deleteBlock(id: Long) = withContext(Dispatchers.IO) {
        database.gooseNoteQueries.deleteNoteContentBlock(id)
    }

    override suspend fun deleteBlockWithNoteId(noteId: Long) = withContext(Dispatchers.IO) {
        database.gooseNoteQueries.deleteNoteContentBlockWithNoteId(noteId)
    }

    override suspend fun insertOrReplaceNoteContentBlock(
        noteContentBlock: NoteContentBlock
    ) = withContext(Dispatchers.IO) {
        database.transactionWithResult {
            database.gooseNoteQueries.insertOrReplaceNoteContentBlock(
                noteContentBlock.id,
                noteContentBlock.noteId!!,
                noteContentBlock.content,
                noteContentBlock.sectionIndex
            )
            database.gooseNoteQueries.lastInsertedNoteContentBlockId().executeAsList().first()
        }
    }

    override suspend fun insertOrReplaceNoteContentBlocks(noteContentBlock: List<NoteContentBlock>) {
        return database.transaction {
            noteContentBlock.forEach { block ->
                database.gooseNoteQueries.insertOrReplaceNoteContentBlock(
                    block.id, block.noteId!!, block.content, block.sectionIndex
                )
            }
        }
    }


}