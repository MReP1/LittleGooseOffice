package little.goose.data.note.domain

import kotlinx.coroutines.flow.Flow
import little.goose.data.note.NoteRepository
import little.goose.data.note.bean.Note

class GetNoteFlowUseCase(
    private val repository: NoteRepository
) {
    operator fun invoke(noteId: Long): Flow<Note> {
        return repository.getNoteFlow(noteId)
    }
}