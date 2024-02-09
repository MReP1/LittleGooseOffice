package little.goose.data.note

import kotlinx.coroutines.flow.Flow
import little.goose.data.note.bean.Note
import little.goose.data.note.bean.NoteContentBlock
import little.goose.data.note.bean.NoteWithContent
import little.goose.data.note.local.NoteDataBase

class NoteRepositoryImpl(
    private val dataBase: NoteDataBase
) : NoteRepository {

    override fun getNoteFlow(noteId: Long): Flow<Note> {
        return dataBase.getNoteFlow(noteId)
    }

    override suspend fun insertOrReplaceNote(note: Note): Long {
        return dataBase.insertOrReplaceNote(note)
    }

    override fun getNoteWithContentFlow(noteId: Long): Flow<NoteWithContent> {
        return dataBase.getNoteWithContentFlow(noteId)
    }

    override suspend fun deleteNoteAndItsBlocks(noteId: Long) {
        return dataBase.deleteNoteAndItsBlocks(noteId)
    }

    override suspend fun deleteBlock(id: Long) {
        return dataBase.deleteBlock(id)
    }

    override suspend fun deleteBlockWithNoteId(noteId: Long) {
        return dataBase.deleteBlockWithNoteId(noteId)
    }

    override suspend fun insertOrReplaceNoteContentBlock(noteContentBlock: NoteContentBlock): Long {
        return dataBase.insertOrReplaceNoteContentBlock(noteContentBlock)
    }

    override suspend fun insertOrReplaceNoteContentBlocks(noteContentBlocks: List<NoteContentBlock>) {
        return dataBase.insertOrReplaceNoteContentBlocks(noteContentBlocks)
    }

    override fun getNoteWithContentFlow(): Flow<List<NoteWithContent>> {
        return dataBase.getNoteWithContentFlow()
    }

}