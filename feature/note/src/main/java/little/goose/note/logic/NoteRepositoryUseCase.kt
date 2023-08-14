package little.goose.note.logic

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
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

class DeleteNotesAndItsBlocksUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(notes: List<Note>) {
        repository.deleteNotesAndItsBlocks(notes)
    }
}

class DeleteNotesEventUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    operator fun invoke(): Flow<List<Note>> {
        return repository.deleteNotesEvent
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
        return repository.getNoteWithContentMapFlow().map {
            it.toSortedMap(compareByDescending { note -> note.time.time })
        }.flowOn(Dispatchers.Default)
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