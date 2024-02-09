package little.goose.data.note.domain

import kotlinx.coroutines.flow.Flow
import little.goose.data.note.NoteRepository
import little.goose.data.note.bean.NoteWithContent

class GetNoteWithContentFlowWithNoteIdUseCase(
    private val repository: NoteRepository
) {
    operator fun invoke(noteId: Long): Flow<NoteWithContent> {
        return repository.getNoteWithContentFlow(noteId)
    }
}