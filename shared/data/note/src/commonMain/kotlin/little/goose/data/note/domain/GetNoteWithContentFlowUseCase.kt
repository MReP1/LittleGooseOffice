package little.goose.data.note.domain

import kotlinx.coroutines.flow.Flow
import little.goose.data.note.NoteRepository
import little.goose.data.note.bean.NoteWithContent

class GetNoteWithContentFlowUseCase(
    private val repository: NoteRepository
) {
    operator fun invoke(): Flow<List<NoteWithContent>> {
        return repository.getNoteWithContentFlow()
    }
}