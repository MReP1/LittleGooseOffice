package little.goose.data.note.domain

import little.goose.data.note.NoteRepository
import little.goose.data.note.bean.Note
import log

class InsertOrReplaceNoteUseCase(
    private val repository: NoteRepository
) : suspend (Note) -> Long {
    override suspend operator fun invoke(note: Note): Long {
        log("InsertOrReplaceNoteUseCase $note")

        return repository.insertOrReplaceNote(note)
    }
}