package little.goose.note.ui.search

import LocalDataResult
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import little.goose.data.note.bean.NoteWithContent
import little.goose.data.note.domain.DeleteNoteAndItsBlocksListUseCase
import little.goose.data.note.domain.DeleteNoteIdListFlowUseCase
import little.goose.data.note.domain.GetNoteWithContentResultByKeywordFlowUseCase
import little.goose.note.ui.notebook.NoteColumnState
import little.goose.note.ui.notebook.NoteItemState
import little.goose.note.ui.notebook.NotebookIntent
import little.goose.resource.GooseRes
import org.jetbrains.compose.resources.getString
import org.koin.compose.koinInject

@Composable
fun rememberSearchNoteStateHolder(
    snackbarHostState: SnackbarHostState,
    getNoteWithContentResultByKeywordFlowUseCase: GetNoteWithContentResultByKeywordFlowUseCase =
        koinInject(),
    deleteNoteAndItsBlocksListUseCase: DeleteNoteAndItsBlocksListUseCase = koinInject(),
    deleteNoteIdListFlowUseCase: DeleteNoteIdListFlowUseCase = koinInject()
): Pair<SearchNoteScreenState, (SearchNoteIntent) -> Unit> {

    LaunchedEffect(deleteNoteIdListFlowUseCase) {
        deleteNoteIdListFlowUseCase().collect {
            snackbarHostState.showSnackbar(
                message = getString(GooseRes.string.deleted)
            )
        }
    }

    var multiSelectedNotes by rememberSaveable {
        mutableStateOf<Set<Long>>(emptySet())
    }

    var keyword by rememberSaveable {
        mutableStateOf("")
    }

    val noteWithContentsResult by produceState<LocalDataResult<List<NoteWithContent>>>(
        LocalDataResult.Data(emptyList()), keyword
    ) {
        if (keyword.isBlank()) {
            value = LocalDataResult.Data(emptyList())
        } else {
            getNoteWithContentResultByKeywordFlowUseCase(keyword).collect { value = it }
        }
    }

    val state = rememberSaveable(
        noteWithContentsResult, multiSelectedNotes,
        saver = SearchNoteState.saver
    ) {
        when (val result = noteWithContentsResult) {
            is LocalDataResult.Failure -> SearchNoteState.Empty
            LocalDataResult.Loading -> SearchNoteState.Loading
            is LocalDataResult.Data -> {
                if (result.data.isEmpty()) {
                    SearchNoteState.Empty
                } else {
                    SearchNoteState.Success(
                        NoteColumnState(
                            noteItemStateList = result.data.map { nwc ->
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
            }
        }
    }

    val screenState = remember(keyword, state) {
        SearchNoteScreenState(keyword, state)
    }

    val coroutineScope = rememberCoroutineScope()

    val mutex = remember { Mutex() }

    return screenState to { intent ->
        when (intent) {
            is SearchNoteIntent.NotebookIntent -> {
                when (val notebookIntent = intent.intent) {
                    NotebookIntent.CancelMultiSelecting -> {
                        multiSelectedNotes = emptySet()
                    }

                    is NotebookIntent.DeleteMultiSelectingNotes -> coroutineScope.launch {
                        mutex.withLock {
                            deleteNoteAndItsBlocksListUseCase(multiSelectedNotes.toList())
                            multiSelectedNotes = emptySet()
                        }
                    }

                    NotebookIntent.SelectAllNotes -> {
                        (state as? SearchNoteState.Success)
                            ?.data?.noteItemStateList?.map { it.id }?.toSet()
                            ?.let { multiSelectedNotes = it }
                    }

                    is NotebookIntent.SelectNote -> {
                        multiSelectedNotes = multiSelectedNotes.toMutableSet().apply {
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