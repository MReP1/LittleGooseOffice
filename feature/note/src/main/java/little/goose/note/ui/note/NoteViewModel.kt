package little.goose.note.ui.note

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import little.goose.note.data.constants.KEY_NOTE_ID
import little.goose.note.logic.DeleteNoteContentBlockUseCase
import little.goose.note.logic.DeleteNotesAndItsBlocksUseCase
import little.goose.note.logic.GetNoteFlowUseCase
import little.goose.note.logic.GetNoteWithContentMapFlowUseCase
import little.goose.note.logic.InsertNoteContentBlockUseCase
import little.goose.note.logic.InsertNoteUseCase
import little.goose.note.logic.UpdateNoteContentBlockUseCase
import little.goose.note.logic.UpdateNoteContentBlocksUseCase
import little.goose.note.logic.UpdateNoteUseCase
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getNoteWithContentMapFlow: GetNoteWithContentMapFlowUseCase,
    insertNoteContentBlock: InsertNoteContentBlockUseCase,
    updateNoteContentBlock: UpdateNoteContentBlockUseCase,
    updateNoteContentBlocks: UpdateNoteContentBlocksUseCase,
    deleteNoteContentBlock: DeleteNoteContentBlockUseCase,
    getNoteFlow: GetNoteFlowUseCase,
    insertNote: InsertNoteUseCase,
    private val deleteNotesAndItsBlocksUseCase: DeleteNotesAndItsBlocksUseCase,
    private val updateNote: UpdateNoteUseCase
) : ViewModel() {

    private val _noteScreenEvent = MutableSharedFlow<NoteScreenEvent>()
    val noteScreenEvent = _noteScreenEvent.asSharedFlow()

    val noteScreenState: StateFlow<NoteScreenState> by NoteRouteStateFlowDelegate(
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

    override fun onCleared() {
        super.onCleared()

        // Update note last modified time
        val noteState = noteScreenState.value as? NoteScreenState.State ?: return
        val note = noteState.scaffoldState.contentState.note.takeIf { it.id != null} ?: return
        viewModelScope.launch(NonCancellable) {
            if (noteState.scaffoldState.contentState.content.isEmpty()) {
                deleteNotesAndItsBlocksUseCase(listOf(note))
            } else {
                updateNote(note.copy(time = Date()))
            }
        }
    }
}