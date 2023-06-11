package little.goose.search.note

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
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import little.goose.note.data.entities.Note
import little.goose.note.logic.DeleteNotesAndItsBlocksUseCase
import little.goose.note.logic.GetNoteWithContentMapFlowByKeyword
import little.goose.note.ui.NoteColumnState
import javax.inject.Inject

@HiltViewModel
class SearchNoteViewModel @Inject constructor(
    private val getNoteWithContentMapFlowByKeyword: GetNoteWithContentMapFlowByKeyword,
    private val deleteNotesAndItsBlocksUseCase: DeleteNotesAndItsBlocksUseCase
) : ViewModel() {

    private val multiSelectedNotes = MutableStateFlow<Set<Note>>(emptySet())

    private val _searchNoteEvent = MutableSharedFlow<SearchNoteEvent>()
    val searchNoteEvent: SharedFlow<SearchNoteEvent> = _searchNoteEvent.asSharedFlow()

    private val _searchNoteState =
        MutableStateFlow<SearchNoteState>(SearchNoteState.Empty(::search))
    val searchNoteState: StateFlow<SearchNoteState> = _searchNoteState.asStateFlow()

    private var searchingJob: Job? = null

    init {
        deleteNotesAndItsBlocksUseCase.deleteNotesEvent.onEach {
            _searchNoteEvent.emit(SearchNoteEvent.DeleteNotes(it))
        }.launchIn(viewModelScope)
    }

    fun search(keyword: String) {
        if (keyword.isBlank()) {
            _searchNoteState.value = SearchNoteState.Empty(::search)
            return
        }
        _searchNoteState.value = SearchNoteState.Loading(::search)
        searchingJob?.cancel()
        searchingJob = combine(
            getNoteWithContentMapFlowByKeyword(keyword),
            multiSelectedNotes
        ) { nwc, multiSelectedNotes ->
            _searchNoteState.value = if (nwc.isEmpty()) {
                SearchNoteState.Empty(::search)
            } else {
                SearchNoteState.Success(
                    NoteColumnState(
                        noteWithContents = nwc,
                        multiSelectedNotes = multiSelectedNotes,
                        isMultiSelecting = multiSelectedNotes.isNotEmpty(),
                        onSelectNote = ::selectNote,
                        selectAllNotes = ::selectAllNote,
                        deleteNotes = ::deleteNotes,
                        cancelMultiSelecting = ::cancelNotesMultiSelecting,
                    ),
                    search = ::search
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
            deleteNotesAndItsBlocksUseCase(notes)
            cancelNotesMultiSelecting()
        }
    }

}