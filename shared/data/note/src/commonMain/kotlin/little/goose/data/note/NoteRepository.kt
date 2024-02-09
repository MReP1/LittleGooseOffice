package little.goose.data.note

import kotlinx.coroutines.flow.Flow
import little.goose.data.note.bean.Note
import little.goose.data.note.bean.NoteContentBlock
import little.goose.data.note.bean.NoteWithContent

interface NoteRepository {

    fun getNoteFlow(noteId: Long): Flow<Note>
    fun getNoteWithContentFlow(): Flow<List<NoteWithContent>>
    suspend fun insertOrReplaceNoteContentBlocks(noteContentBlocks: List<NoteContentBlock>)
    suspend fun insertOrReplaceNoteContentBlock(noteContentBlock: NoteContentBlock): Long
    suspend fun deleteBlockWithNoteId(noteId: Long)
    suspend fun deleteBlock(noteId: Long)
    suspend fun deleteNoteAndItsBlocks(noteId: Long)
    fun getNoteWithContentFlow(noteId: Long): Flow<NoteWithContent>
    suspend fun insertOrReplaceNote(note: Note): Long
}