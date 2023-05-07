package little.goose.note.logic

import kotlinx.coroutines.flow.Flow
import little.goose.note.data.entities.Note
import little.goose.note.data.entities.NoteContentBlock
import javax.inject.Inject

class InsertNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(
        note: Note
    ): Long {
        return repository.insertNote(note)
    }
}

class GetNoteFlowUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    operator fun invoke(noteId: Long): Flow<Note> {
        return repository.getNoteFlow(noteId)
    }
}

class UpdateNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note) {
        return repository.updateNote(note)
    }
}

class DeleteNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note) {
        note.id?.let { noteId ->
            repository.deleteNoteContentBlocks(noteId)
        }
        return repository.deleteNote(note)
    }
}

class DeleteNotesUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(notes: List<Note>) {
        val noteIds = notes.mapNotNull { it.id }
        repository.deleteNoteContentBlocks(noteIds)
        return repository.deleteNotes(notes)
    }
}

class InsertNoteContentBlockUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(
        noteContentBlock: NoteContentBlock
    ): Long {
        return repository.insertNoteContentBlock(noteContentBlock)
    }
}

class GetNoteWithContentsMapFlowUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    operator fun invoke(): Flow<Map<Note, List<NoteContentBlock>>> {
        return repository.getNoteWithContentMapFlow()
    }
}

class GetNoteWithContentMapFlowUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    operator fun invoke(noteId: Long): Flow<Map<Note, List<NoteContentBlock>>> {
        return repository.getNoteWithContentMapFlow(noteId)
    }
}

class GetNoteWithContentMapFlowByKeyword @Inject constructor(
    private val repository: NoteRepository
) {
    operator fun invoke(keyword: String): Flow<Map<Note, List<NoteContentBlock>>> {
        return repository.getNoteWithContentMapFlowByKeyword(keyword)
    }
}

class UpdateNoteContentBlockUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(
        noteContentBlock: NoteContentBlock
    ) {
        return repository.updateNoteContentBlock(noteContentBlock)
    }
}

class UpdateNoteContentBlocksUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(
        noteContentBlocks: List<NoteContentBlock>
    ) {
        return repository.updateNoteContentBlocks(noteContentBlocks)
    }
}

class DeleteNoteContentBlockUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(
        noteContentBlock: NoteContentBlock
    ) {
        return repository.deleteNoteContentBlock(noteContentBlock)
    }
}