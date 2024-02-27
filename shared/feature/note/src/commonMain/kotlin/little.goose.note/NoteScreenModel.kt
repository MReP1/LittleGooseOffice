package little.goose.note

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
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
) : ScreenModel {

    val noteScreenStateHolder = NoteScreenStateHolder(
        noteId,
        screenModelScope,
        insertOrReplaceNoteContentBlocks,
        getNoteWithContentFlowWithNoteId,
        insertOrReplaceNoteContentBlock,
        insertOrReplaceNote,
        deleteNoteAndItsBlocks,
        deleteNoteContentBlockUseCase
    )

    override fun onDispose() {
        super.onDispose()
        noteScreenStateHolder.clear()
    }

}