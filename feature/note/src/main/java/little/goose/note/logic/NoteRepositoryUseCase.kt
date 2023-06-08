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

class DeleteNoteAndItsBlocksUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note) {
        return repository.deleteNoteAndItsBlocks(note)
    }
}

class DeleteNotesAndItsBlocksUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(notes: List<Note>) {
        return repository.deleteNotesAndItsBlocks(notes)
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