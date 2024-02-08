package little.goose.data.note.local

import kotlinx.coroutines.flow.Flow
import little.goose.data.note.bean.Note
import little.goose.data.note.bean.NoteContentBlock
import little.goose.data.note.bean.NoteWithContent

interface NoteDataBase {

    fun getNoteFlow(noteId: Long): Flow<Note>

    suspend fun insertOrReplaceNote(note: Note): Long

    fun getNoteWithContentFlow(noteId: Long): Flow<NoteWithContent>

    suspend fun deleteNoteAndItsBlocks(noteId: Long)

    suspend fun deleteBlock(id: Long)

    suspend fun deleteBlockWithNoteId(noteId: Long)

    suspend fun insertOrReplaceNoteContentBlock(noteContentBlock: NoteContentBlock): Long

    suspend fun insertOrReplaceNoteContentBlocks(noteContentBlock: List<NoteContentBlock>)

}