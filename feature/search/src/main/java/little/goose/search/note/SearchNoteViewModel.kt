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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import little.goose.data.note.domain.DeleteNoteAndItsBlocksListUseCase
import little.goose.data.note.domain.GetNoteWithContentByKeywordFlowUseCase
import little.goose.note.ui.notebook.NoteColumnState
import little.goose.note.ui.notebook.NoteItemState
import little.goose.note.ui.notebook.NotebookIntent

class SearchNoteViewModel(
    private val getNoteWithContentByKeywordFlowUseCase: GetNoteWithContentByKeywordFlowUseCase,
    private val deleteNoteAndItsBlocksListUseCase: DeleteNoteAndItsBlocksListUseCase
) : ViewModel() {

    private val multiSelectedNotes = MutableStateFlow<Set<Long>>(emptySet())

    private val _searchNoteEvent = MutableSharedFlow<SearchNoteEvent>()
    val searchNoteEvent: SharedFlow<SearchNoteEvent> = _searchNoteEvent.asSharedFlow()

    private val _searchNoteState = MutableStateFlow<SearchNoteState>(SearchNoteState.Empty)
    val searchNoteState: StateFlow<SearchNoteState> = _searchNoteState.asStateFlow()

    private var searchingJob: Job? = null

    private fun search(keyword: String) {
        if (keyword.isBlank()) {
            _searchNoteState.value = SearchNoteState.Empty
            return
        }
        _searchNoteState.value = SearchNoteState.Loading
        searchingJob?.cancel()
        searchingJob = combine(
            getNoteWithContentByKeywordFlowUseCase(keyword), multiSelectedNotes
        ) { nwcList, multiSelectedNotes ->
            _searchNoteState.value = if (nwcList.isEmpty()) {
                SearchNoteState.Empty
            } else {
                SearchNoteState.Success(
                    NoteColumnState(
                        noteItemStateList = nwcList.map { nwc ->
                            NoteItemState(
                                id = nwc.note.id!!,
                                title = nwc.note.title,
                                content = nwc.content.firstOrNull()?.content ?: "",
                                isSelected = multiSelectedNotes.contains(nwc.note.id!!)
                            )
                        },
                        isMultiSelecting = multiSelectedNotes.isNotEmpty()
                    )
                )
            }
        }.launchIn(viewModelScope)
    }

    fun action(intent: SearchNoteIntent) {
        when (intent) {
            is SearchNoteIntent.NotebookIntent -> {
                when (val notebookIntent = intent.intent) {
                    NotebookIntent.CancelMultiSelecting -> cancelNotesMultiSelecting()
                    NotebookIntent.SelectAllNotes -> selectAllNote()
                    is NotebookIntent.DeleteMultiSelectingNotes -> deleteNotes(
                        multiSelectedNotes.value.toList()
                    )

                    is NotebookIntent.SelectNote -> selectNote(
                        notebookIntent.noteId,
                        notebookIntent.selected
                    )
                }
            }

            is SearchNoteIntent.Search -> {
                search(intent.keyword)
            }
        }
    }

    private fun selectNote(noteId: Long, selected: Boolean) {
        multiSelectedNotes.update {
            it.toMutableSet().apply {
                if (selected) add(noteId) else remove(noteId)
            }
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