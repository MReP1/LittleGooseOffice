package little.goose.data.note.domain

import little.goose.data.note.NoteRepository

class DeleteNoteAndItsBlocksListUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(noteIds: List<Long>) {
        repository.deleteNoteAndItsBlocksList(noteIds)
    }
}