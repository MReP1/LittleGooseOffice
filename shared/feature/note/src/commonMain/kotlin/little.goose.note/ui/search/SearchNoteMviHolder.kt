package little.goose.note.ui.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import little.goose.data.note.domain.DeleteNoteAndItsBlocksListUseCase
import little.goose.data.note.domain.DeleteNoteIdListFlowUseCase
import little.goose.data.note.domain.GetNoteWithContentByKeywordFlowUseCase
import little.goose.note.ui.notebook.NoteColumnState
import little.goose.note.ui.notebook.NoteItemState
import little.goose.note.ui.notebook.NotebookIntent
import little.goose.shared.ui.architecture.MviHolder
import little.goose.shared.ui.architecture.autoMutableStateFlowSaver
import org.koin.compose.koinInject

@Composable
fun rememberSearchNoteStateHolder(): MviHolder<SearchNoteState, SearchNoteEvent, SearchNoteIntent> {

    val coroutineScope = rememberCoroutineScope()

    val getNoteWithContentByKeywordFlowUseCase: GetNoteWithContentByKeywordFlowUseCase =
        koinInject()

    val deleteNoteAndItsBlocksListUseCase: DeleteNoteAndItsBlocksListUseCase =
        koinInject()

    val deleteNoteIdListFlowUseCase: DeleteNoteIdListFlowUseCase = koinInject()

    val event = remember {
        MutableSharedFlow<SearchNoteEvent>()
    }

    LaunchedEffect(deleteNoteIdListFlowUseCase, event) {
        deleteNoteIdListFlowUseCase().collect {
            event.emit(SearchNoteEvent.DeleteNotes)
        }
    }

    val multiSelectedNotes = rememberSaveable(saver = autoMutableStateFlowSaver()) {
        MutableStateFlow<Set<Long>>(emptySet())
    }

    var state by rememberSaveable(stateSaver = SearchNoteState.saver) {
        mutableStateOf(SearchNoteState.Empty)
    }

    var keyword by rememberSaveable {
        mutableStateOf("")
    }

    LaunchedEffect(keyword) {
        (state as? SearchNoteState.Success)?.let { success ->
            if (success.keyword == keyword) {
                return@LaunchedEffect
            }
        }

        if (keyword.isBlank()) {
            state = SearchNoteState.Empty
        } else {
            state = SearchNoteState.Loading
            combine(
                getNoteWithContentByKeywordFlowUseCase(keyword),
                multiSelectedNotes
            ) { nwcList, multiSelectedNotes ->
                state = if (nwcList.isEmpty()) {
                    SearchNoteState.Empty
                } else {
                    SearchNoteState.Success(
                        keyword, NoteColumnState(
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
            }.launchIn(this)
        }
    }

    val cancelNotesMultiSelecting = remember {
        fun() { multiSelectedNotes.value = emptySet() }
    }

    val mutex = remember { Mutex() }

    val action: (SearchNoteIntent) -> Unit = remember {
        { intent ->
            when (intent) {
                is SearchNoteIntent.NotebookIntent -> {
                    when (val notebookIntent = intent.intent) {
                        NotebookIntent.CancelMultiSelecting -> cancelNotesMultiSelecting()
                        is NotebookIntent.DeleteMultiSelectingNotes -> coroutineScope.launch {
                            mutex.withLock {
                                deleteNoteAndItsBlocksListUseCase(multiSelectedNotes.value.toList())
                                cancelNotesMultiSelecting()
                            }
                        }

                        NotebookIntent.SelectAllNotes -> {
                            (state as? SearchNoteState.Success)
                                ?.data?.noteItemStateList?.map { it.id }?.toSet()
                                ?.let(multiSelectedNotes::value::set)
                        }

                        is NotebookIntent.SelectNote -> multiSelectedNotes.update {
                            it.toMutableSet().apply {
                                if (notebookIntent.selected) {
                                    add(notebookIntent.noteId)
                                } else {
                                    remove(notebookIntent.noteId)
                                }
                            }
                        }
                    }
                }

                is SearchNoteIntent.Search -> {
                    keyword = intent.keyword
                }
            }
        }
    }

    return remember(state, event, action) {
        MviHolder(state, event, action)
    }
}