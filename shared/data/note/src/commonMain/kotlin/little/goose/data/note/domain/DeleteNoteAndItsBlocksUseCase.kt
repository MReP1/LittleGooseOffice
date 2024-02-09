package little.goose.data.note.domain

import little.goose.data.note.NoteRepository

class DeleteNoteAndItsBlocksUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(noteId: Long) {
        return repository.deleteNoteAndItsBlocks(noteId)
    }
}