package little.goose.note.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import little.goose.note.data.entities.Note
import little.goose.note.logic.DeleteNotesAndItsBlocksUseCase
import little.goose.note.logic.DeleteNotesEventUseCase
import little.goose.note.logic.GetNoteWithContentsMapFlowUseCase
import javax.inject.Inject

@HiltViewModel
class NotebookViewModel @Inject constructor(
    getNoteWithContentsMapFlow: GetNoteWithContentsMapFlowUseCase,
    private val deleteNotesAndItsBlocksUseCase: DeleteNotesAndItsBlocksUseCase,
    deleteNotesEventUseCase: DeleteNotesEventUseCase
) : ViewModel() {

    sealed class Event {
        data class DeleteNotes(val notes: List<Note>) : Event()
    }

    private val _event = MutableSharedFlow<Event>()
    val event = _event.asSharedFlow()

    private val multiSelectedNotes = MutableStateFlow<Set<Note>>(emptySet())

    private val noteWithContents = getNoteWithContentsMapFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyMap()
    )

    val noteColumnState = combine(
        multiSelectedNotes, noteWithContents
    ) { multiSelectedNotes, noteWithContents ->
        NoteColumnState(
            noteWithContents = noteWithContents,
            multiSelectedNotes = multiSelectedNotes,
            isMultiSelecting = multiSelectedNotes.isNotEmpty(),
            onSelectNote = ::selectNote,
            selectAllNotes = ::selectAllNotes,
            cancelMultiSelecting = ::cancelMultiSelecting,
            deleteNotes = ::deleteNotes
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = NoteColumnState(
            noteWithContents = noteWithContents.value,
            multiSelectedNotes = multiSelectedNotes.value,
            isMultiSelecting = multiSelectedNotes.value.isNotEmpty(),
            onSelectNote = ::selectNote,
            selectAllNotes = ::selectAllNotes,
            cancelMultiSelecting = ::cancelMultiSelecting,
            deleteNotes = ::deleteNotes
        )
    )

    init {
        deleteNotesEventUseCase().onEach {
            _event.emit(Event.DeleteNotes(it))
        }.launchIn(viewModelScope)
    }

    private fun selectNote(note: Note, selected: Boolean) {
        multiSelectedNotes.value = multiSelectedNotes.value.toMutableSet().apply {
            if (selected) add(note) else remove(note)
        }
    }

    private fun selectAllNotes() {
        multiSelectedNotes.value = noteWithContents.value.keys
    }

    private fun cancelMultiSelecting() {
        multiSelectedNotes.value = emptySet()
    }

    private fun deleteNotes(notes: List<Note>) {
        viewModelScope.launch {
            deleteNotesAndItsBlocksUseCase(notes)
            cancelMultiSelecting()
        }
    }

}