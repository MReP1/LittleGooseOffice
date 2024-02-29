package little.goose.data.note.domain

import kotlinx.coroutines.flow.Flow
import little.goose.data.note.NoteRepository

class DeleteNoteIdListFlowUseCase(
    private val repository: NoteRepository
) {
    operator fun invoke(): Flow<List<Long>> = repository.deleteNoteIdListFlow
}