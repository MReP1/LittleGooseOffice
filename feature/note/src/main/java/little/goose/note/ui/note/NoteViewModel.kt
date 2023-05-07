package little.goose.note.ui.note

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import little.goose.note.data.constants.KEY_NOTE_ID
import little.goose.note.logic.DeleteNoteContentBlockUseCase
import little.goose.note.logic.GetNoteFlowUseCase
import little.goose.note.logic.GetNoteWithContentMapFlowUseCase
import little.goose.note.logic.InsertNoteContentBlockUseCase
import little.goose.note.logic.InsertNoteUseCase
import little.goose.note.logic.UpdateNoteContentBlockUseCase
import little.goose.note.logic.UpdateNoteContentBlocksUseCase
import little.goose.note.logic.UpdateNoteUseCase
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getNoteWithContentMapFlow: GetNoteWithContentMapFlowUseCase,
    insertNoteContentBlock: InsertNoteContentBlockUseCase,
    updateNoteContentBlock: UpdateNoteContentBlockUseCase,
    updateNoteContentBlocks: UpdateNoteContentBlocksUseCase,
    deleteNoteContentBlock: DeleteNoteContentBlockUseCase,
    insertNote: InsertNoteUseCase,
    updateNote: UpdateNoteUseCase,
    getNoteFlow: GetNoteFlowUseCase
) : ViewModel() {

    private val _noteScreenEvent = MutableSharedFlow<NoteScreenEvent>()
    val noteScreenEvent = _noteScreenEvent.asSharedFlow()

    val noteRouteState: StateFlow<NoteRouteState> by NoteRouteStateFlowDelegate(
        noteIdFlow = savedStateHandle.getStateFlow<Long>(KEY_NOTE_ID, -1),
        updateNoteId = { noteId -> savedStateHandle[KEY_NOTE_ID] = noteId },
        coroutineScope = viewModelScope,
        emitNoteScreenEvent = _noteScreenEvent::emit,
        updateNoteContentBlock = updateNoteContentBlock,
        updateNoteContentBlocks = updateNoteContentBlocks,
        deleteNoteContentBlock = deleteNoteContentBlock,
        insertNoteContentBlock = insertNoteContentBlock,
        insertNote = insertNote,
        updateNote = updateNote,
        getNoteWithContentMapFlow = getNoteWithContentMapFlow,
        getNoteFlow = getNoteFlow
    )

}