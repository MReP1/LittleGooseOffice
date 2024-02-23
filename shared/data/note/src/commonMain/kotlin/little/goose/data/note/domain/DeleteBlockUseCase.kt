package little.goose.data.note.domain

import little.goose.data.note.NoteRepository

class DeleteBlockUseCase(
    private val repository: NoteRepository
) : suspend (Long) -> Unit {
    override suspend operator fun invoke(id: Long) {
        return repository.deleteBlock(id)
    }
}