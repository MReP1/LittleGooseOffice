package little.goose.data.note.domain

import little.goose.data.note.NoteRepository
import little.goose.data.note.bean.NoteContentBlock

class InsertOrReplaceNoteContentBlocksUseCase(
    private val repository: NoteRepository
): suspend (List<NoteContentBlock>) -> Unit {
    override suspend operator fun invoke(noteContentBlocks: List<NoteContentBlock>) {
        repository.insertOrReplaceNoteContentBlocks(noteContentBlocks)
    }
}