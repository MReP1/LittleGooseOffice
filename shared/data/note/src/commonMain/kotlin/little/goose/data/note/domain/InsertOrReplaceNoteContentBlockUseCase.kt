package little.goose.data.note.domain

import little.goose.data.note.NoteRepository
import little.goose.data.note.bean.NoteContentBlock
import log

class InsertOrReplaceNoteContentBlockUseCase(
    private val repository: NoteRepository
) : suspend (NoteContentBlock) -> Long {
    override suspend operator fun invoke(noteContentBlock: NoteContentBlock): Long {
        log("InsertOrReplaceNoteContentBlockUseCase $noteContentBlock")
        return repository.insertOrReplaceNoteContentBlock(noteContentBlock)
    }
}