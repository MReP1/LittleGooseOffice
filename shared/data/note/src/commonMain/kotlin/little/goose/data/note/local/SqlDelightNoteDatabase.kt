package little.goose.data.note.local

import app.cash.sqldelight.ExecutableQuery
import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
import little.goose.data.note.bean.Note
import little.goose.data.note.bean.NoteContentBlock
import little.goose.data.note.bean.NoteWithContent
import little.goose.note.GooseNoteDatabase

class SqlDelightNoteDatabase(
    private val database: GooseNoteDatabase
) : NoteDatabase {

    private val _deleteNoteIdListSharedFlow = MutableSharedFlow<List<Long>>()
    override val deleteNoteIdListFlow = _deleteNoteIdListSharedFlow.asSharedFlow()

    override fun getNoteFlow(noteId: Long): Flow<Note> {
        return database.gooseNoteQueries
            .getNote(noteId) { id, title, time, _ -> Note(id, title, time) }
            .asFlow()
            .mapNotNull { it.executeAsOneOrNull() }
    }

    override suspend fun insertOrReplaceNote(note: Note): Long {
        return withContext(Dispatchers.IO) {
            database.gooseNoteQueries.transactionWithResult {
                database.gooseNoteQueries.insertOrReplaceNote(note.id, note.title, note.time, "")
                database.gooseNoteQueries.lastInsertedNoteRowId().executeAsList().first()
            }
        }
    }

    override fun getNoteWithContentFlow(noteId: Long): Flow<NoteWithContent> {
        return flow {
            val channel = Channel<Unit>(Channel.CONFLATED)
            channel.trySend(Unit)
            val noteQuery = database.gooseNoteQueries.getNote(noteId)
            val noteContentBlockQuery = database.gooseNoteQueries.getNoteContentBlocks(noteId)
            val noteListener = Query.Listener { channel.trySend(Unit) }
            val noteContentBlockListener = Query.Listener { channel.trySend(Unit) }
            noteQuery.addListener(noteListener)
            noteContentBlockQuery.addListener(noteContentBlockListener)
            try {
                for (item in channel) {
                    var note: Note? = null
                    val blocks = mutableListOf<NoteContentBlock>()
                    database.gooseNoteQueries.getNoteWithContentWithNoteId(
                        noteId
                    ) { id: Long, title: String, time: Long, _,
                        blockId: Long, noteId: Long?, sectionIndex: Long, blockContent: String ->
                        if (note == null) {
                            note = Note(id, title, time)
                        }
                        blocks.add(NoteContentBlock(blockId, noteId, blockContent, sectionIndex))
                    }.executeAsList()
                    val actualNote = note
                        ?: database.gooseNoteQueries.getNote(noteId).executeAsOneOrNull()?.let {
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

    override fun getNoteWithContentFlow(): Flow<List<NoteWithContent>> {
        return getNoteWithContentFlow(database.gooseNoteQueries::getNoteWithContents)
    }

    override fun getNoteWithContentFlowByKeyword(keyword: String): Flow<List<NoteWithContent>> {
        return getNoteWithContentFlow {
            database.gooseNoteQueries.getNoteWithContentsByKeyword(keyword, it)
        }
    }

    private fun getNoteWithContentFlow(
        fetcher: (mapper: (id: Long, title: String, time: Long, content: String, blockId: Long, noteId: Long?, index: Long, blockContent: String) -> Unit) -> ExecutableQuery<Unit>
    ): Flow<List<NoteWithContent>> {
        return flow<List<NoteWithContent>> {
            val channel = Channel<Unit>(Channel.CONFLATED)
            channel.trySend(Unit)
            val allNoteQuery = database.gooseNoteQueries.getAllNote()
            val allNoteContentBlockQuery = database.gooseNoteQueries.getAllNoteContentBlock()
            val noteListener = Query.Listener { channel.trySend(Unit) }
            val noteContentBlockListener = Query.Listener { channel.trySend(Unit) }
            allNoteQuery.addListener(noteListener)
            allNoteContentBlockQuery.addListener(noteContentBlockListener)
            try {
                var map = mutableMapOf<Note, MutableList<NoteContentBlock>>()
                var resultList = mutableListOf<NoteWithContent>()
                for (item in channel) {
                    fetcher { id: Long, title: String, time: Long, _,
                              blockId: Long, noteId: Long?, sectionIndex: Long, blockContent: String ->
                        map.getOrPut(Note(id, title, time)) { mutableListOf() }.add(
                            NoteContentBlock(blockId, noteId, blockContent, sectionIndex)
                        )
                    }.executeAsList()
                    map.forEach { (note, blocks) ->
                        resultList.add(NoteWithContent(note, blocks))
                    }
                    resultList.sortByDescending { it.note.time }
                    emit(resultList)
                    map = mutableMapOf()
                    resultList = mutableListOf()
                }
            } finally {
                allNoteQuery.removeListener(noteListener)
                allNoteContentBlockQuery.removeListener(noteContentBlockListener)
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun deleteNoteAndItsBlocks(noteId: Long) {
        withContext(Dispatchers.IO) {
            database.gooseNoteQueries.deleteNoteAndItsBlocks(noteId)
            _deleteNoteIdListSharedFlow.emit(listOf(noteId))
        }
    }

    override suspend fun deleteNoteAndItsBlocksList(noteIds: List<Long>) {
        withContext(Dispatchers.IO) {
            database.transaction {
                noteIds.forEach { noteId ->
                    database.gooseNoteQueries.deleteNoteAndItsBlocks(noteId)
                }
            }
            _deleteNoteIdListSharedFlow.emit(noteIds)
        }
    }

    override suspend fun deleteBlock(id: Long) {
        withContext(Dispatchers.IO) {
            database.gooseNoteQueries.deleteNoteContentBlock(id)
        }
    }

    override suspend fun deleteBlockWithNoteId(noteId: Long) {
        withContext(Dispatchers.IO) {
            database.gooseNoteQueries.deleteNoteContentBlockWithNoteId(noteId)
        }
    }

    override suspend fun insertOrReplaceNoteContentBlock(
        noteContentBlock: NoteContentBlock
    ) = withContext(Dispatchers.IO) {
        database.transactionWithResult {
            database.gooseNoteQueries.insertOrReplaceNoteContentBlock(
                noteContentBlock.id,
                noteContentBlock.noteId!!,
                noteContentBlock.sectionIndex,
                noteContentBlock.content,
            )
            database.gooseNoteQueries.lastInsertedNoteContentBlockId().executeAsList().first()
        }
    }

    override suspend fun insertOrReplaceNoteContentBlocks(noteContentBlocks: List<NoteContentBlock>) {
        return database.transaction {
            noteContentBlocks.forEach { block ->
                database.gooseNoteQueries.insertOrReplaceNoteContentBlock(
                    block.id, block.noteId!!, block.sectionIndex, block.content
                )
            }
        }
    }

}