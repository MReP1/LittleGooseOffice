package little.goose.data.note.domain

import little.goose.data.note.NoteRepository

class DeleteBlockWithNoteIdUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(noteId: Long) {
        return repository.deleteBlockWithNoteId(noteId)
    }
}