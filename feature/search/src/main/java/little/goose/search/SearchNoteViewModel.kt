package little.goose.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import little.goose.note.data.entities.Note
import little.goose.note.logic.DeleteNotesUseCase
import little.goose.note.logic.GetNoteWithContentMapFlowByKeyword
import little.goose.note.ui.NoteColumnState
import javax.inject.Inject

@HiltViewModel
class SearchNoteViewModel @Inject constructor(
    private val getNoteWithContentMapFlowByKeyword: GetNoteWithContentMapFlowByKeyword,
    private val deleteNotesUseCase: DeleteNotesUseCase
) : ViewModel() {

    private val multiSelectedNotes = MutableStateFlow<Set<Note>>(emptySet())

    private val _searchNoteEvent = MutableSharedFlow<SearchNoteEvent>()
    val searchNoteEvent: SharedFlow<SearchNoteEvent> = _searchNoteEvent.asSharedFlow()

    private val _searchNoteState = MutableStateFlow<SearchNoteState>(SearchNoteState.Empty)
    val searchNoteState: StateFlow<SearchNoteState> = _searchNoteState.asStateFlow()

    private var searchingJob: Job? = null

    fun search(keyword: String) {
        if (keyword.isBlank()) {
            _searchNoteState.value = SearchNoteState.Empty
            return
        }
        _searchNoteState.value = SearchNoteState.Loading
        searchingJob?.cancel()
        searchingJob = combine(
            getNoteWithContentMapFlowByKeyword(keyword),
            multiSelectedNotes
        ) { nwc, multiSelectedNotes ->
            if (nwc.isEmpty()) {
                SearchNoteState.Empty
            } else {
                SearchNoteState.Success(
                    NoteColumnState(
                        noteWithContents = nwc,
                        multiSelectedNotes = multiSelectedNotes,
                        isMultiSelecting = multiSelectedNotes.isNotEmpty(),
                        onSelectNote = ::selectNote,
                        selectAllNotes = ::selectAllNote,
                        deleteNotes = ::deleteNotes,
                        cancelMultiSelecting = ::cancelNotesMultiSelecting
                    )
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun selectNote(
        note: Note,
        selected: Boolean
    ) {
        multiSelectedNotes.value = multiSelectedNotes.value.toMutableSet()
            .apply {
                if (selected) add(note) else remove(note)
            }
    }

    private fun selectAllNote() {
        multiSelectedNotes.value = (searchNoteState.value as? SearchNoteState.Success)
            ?.data?.noteWithContents?.keys ?: return
    }

    private fun cancelNotesMultiSelecting() {
        multiSelectedNotes.value = emptySet()
    }

    private fun deleteNotes(notes: List<Note>) {
        viewModelScope.launch {
            deleteNotesUseCase(notes)
            _searchNoteEvent.emit(SearchNoteEvent.DeleteNotes(notes))
        }
    }

}