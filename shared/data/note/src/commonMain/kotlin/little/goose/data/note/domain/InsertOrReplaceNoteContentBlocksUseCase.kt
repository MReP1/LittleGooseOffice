package little.goose.data.note.domain

import little.goose.data.note.NoteRepository
import little.goose.data.note.bean.NoteContentBlock
import log

class InsertOrReplaceNoteContentBlocksUseCase(
    private val repository: NoteRepository
): suspend (List<NoteContentBlock>) -> Unit {
    override suspend operator fun invoke(noteContentBlocks: List<NoteContentBlock>) {
        log("InsertOrReplaceNoteContentBlocksUseCase $noteContentBlocks")
        repository.insertOrReplaceNoteContentBlocks(noteContentBlocks)
    }
}