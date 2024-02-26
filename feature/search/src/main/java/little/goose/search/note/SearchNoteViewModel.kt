package little.goose.search.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import little.goose.data.note.domain.DeleteNoteAndItsBlocksListUseCase
import little.goose.data.note.domain.GetNoteWithContentByKeywordFlowUseCase
import little.goose.note.ui.NoteColumnState
import little.goose.note.ui.NoteItemState
import little.goose.note.ui.NotebookIntent

class SearchNoteViewModel(
    private val getNoteWithContentByKeywordFlowUseCase: GetNoteWithContentByKeywordFlowUseCase,
    private val deleteNoteAndItsBlocksListUseCase: DeleteNoteAndItsBlocksListUseCase
) : ViewModel() {

    private val multiSelectedNotes = MutableStateFlow<Set<Long>>(emptySet())

    private val _searchNoteEvent = MutableSharedFlow<SearchNoteEvent>()
    val searchNoteEvent: SharedFlow<SearchNoteEvent> = _searchNoteEvent.asSharedFlow()

    private val _searchNoteState =
        MutableStateFlow<SearchNoteState>(SearchNoteState.Empty(::search))
    val searchNoteState: StateFlow<SearchNoteState> = _searchNoteState.asStateFlow()

    private var searchingJob: Job? = null

    fun search(keyword: String) {
        if (keyword.isBlank()) {
            _searchNoteState.value = SearchNoteState.Empty(::search)
            return
        }
        _searchNoteState.value = SearchNoteState.Loading(::search)
        searchingJob?.cancel()
        searchingJob = combine(
            getNoteWithContentByKeywordFlowUseCase(keyword).map {
                buildMap {
                    for (nwc in it) {
                        put(nwc.note, nwc.content)
                    }
                }
            },
            multiSelectedNotes
        ) { nwc, multiSelectedNotes ->
            _searchNoteState.value = if (nwc.isEmpty()) {
                SearchNoteState.Empty(::search)
            } else {
                SearchNoteState.Success(
                    NoteColumnState(
                        noteItemStateList = nwc.map { (note, noteContentBlocks) ->
                            NoteItemState(
                                note.id!!,
                                note.title,
                                noteContentBlocks.firstOrNull()?.content ?: ""
                            )
                        },
                        multiSelectedNotes = multiSelectedNotes,
                        isMultiSelecting = multiSelectedNotes.isNotEmpty()
                    ),
                    search = ::search
                )
            }
        }.launchIn(viewModelScope)
    }

    fun action(intent: NotebookIntent) {
        when (intent) {
            NotebookIntent.CancelMultiSelecting -> cancelNotesMultiSelecting()
            NotebookIntent.SelectAllNotes -> selectAllNote()
            is NotebookIntent.DeleteNotes -> deleteNotes(intent.noteIds)
            is NotebookIntent.SelectNote -> selectNote(intent.noteId, intent.selectNote)
        }
    }

    private fun selectNote(noteId: Long, selected: Boolean) {
        multiSelectedNotes.value = multiSelectedNotes.value.toMutableSet()
            .apply {
                if (selected) add(noteId) else remove(noteId)
            }
    }

    private fun selectAllNote() {
        multiSelectedNotes.value = (searchNoteState.value as? SearchNoteState.Success)
            ?.data?.noteItemStateList?.map { it.id }?.toSet() ?: return
    }

    private fun cancelNotesMultiSelecting() {
        multiSelectedNotes.value = emptySet()
    }

    private fun deleteNotes(noteIds: List<Long>) {
        viewModelScope.launch {
            deleteNoteAndItsBlocksListUseCase(noteIds)
            cancelNotesMultiSelecting()
        }
    }

}