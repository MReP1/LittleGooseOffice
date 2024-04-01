package little.goose.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import little.goose.data.note.domain.DeleteBlockUseCase
import little.goose.data.note.domain.DeleteNoteAndItsBlocksUseCase
import little.goose.data.note.domain.GetNoteWithContentFlowWithNoteIdUseCase
import little.goose.data.note.domain.InsertOrReplaceNoteContentBlockUseCase
import little.goose.data.note.domain.InsertOrReplaceNoteContentBlocksUseCase
import little.goose.data.note.domain.InsertOrReplaceNoteUseCase
import little.goose.note.logic.note.NoteScreenStateHolder

class NoteScreenModel(
    noteId: Long,
    insertOrReplaceNoteContentBlocks: InsertOrReplaceNoteContentBlocksUseCase,
    getNoteWithContentFlowWithNoteId: GetNoteWithContentFlowWithNoteIdUseCase,
    insertOrReplaceNoteContentBlock: InsertOrReplaceNoteContentBlockUseCase,
    insertOrReplaceNote: InsertOrReplaceNoteUseCase,
    deleteNoteAndItsBlocks: DeleteNoteAndItsBlocksUseCase,
    deleteNoteContentBlockUseCase: DeleteBlockUseCase
) : ViewModel() {

    val noteScreenStateHolder = NoteScreenStateHolder(
        noteId,
        viewModelScope,
        insertOrReplaceNoteContentBlocks,
        getNoteWithContentFlowWithNoteId,
        insertOrReplaceNoteContentBlock,
        insertOrReplaceNote,
        deleteNoteAndItsBlocks,
        deleteNoteContentBlockUseCase
    )

    override fun onCleared() {
        super.onCleared()
        noteScreenStateHolder.clear()
    }

}