package little.goose.note.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import little.goose.note.data.entities.Note
import little.goose.note.logic.NoteRepository
import javax.inject.Inject

@HiltViewModel
class NotebookViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {

    sealed class Event {
        data class DeleteNotes(val notes: List<Note>) : Event()
    }

    private val _event = MutableSharedFlow<Event>()
    val event = _event.asSharedFlow()

    private val multiSelectedNotes = MutableStateFlow<Set<Note>>(emptySet())

    val notes = noteRepository.getAllNoteFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    val noteGridState = combine(multiSelectedNotes, notes) { multiSelectedNotes, notes ->
        NoteGridState(
            notes = notes,
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
        initialValue = NoteGridState(
            notes = notes.value,
            multiSelectedNotes = multiSelectedNotes.value,
            isMultiSelecting = multiSelectedNotes.value.isNotEmpty(),
            onSelectNote = ::selectNote,
            selectAllNotes = ::selectAllNotes,
            cancelMultiSelecting = ::cancelMultiSelecting,
            deleteNotes = ::deleteNotes
        )
    )

    private fun selectNote(note: Note, selected: Boolean) {
        multiSelectedNotes.value = multiSelectedNotes.value.toMutableSet().apply {
            if (selected) add(note) else remove(note)
        }
    }

    private fun selectAllNotes() {
        multiSelectedNotes.value = notes.value.toSet()
    }

    private fun cancelMultiSelecting() {
        multiSelectedNotes.value = emptySet()
    }

    private fun deleteNotes(notes: List<Note>) {
        viewModelScope.launch {
            noteRepository.deleteNotes(notes)
            _event.emit(Event.DeleteNotes(notes))
        }
    }

}