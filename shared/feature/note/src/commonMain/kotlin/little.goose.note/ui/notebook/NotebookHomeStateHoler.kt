package little.goose.note.ui.notebook

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import little.goose.data.note.bean.NoteWithContent
import little.goose.data.note.domain.DeleteNoteAndItsBlocksListUseCase
import little.goose.data.note.domain.DeleteNoteIdListFlowUseCase
import little.goose.data.note.domain.GetNoteWithContentFlowUseCase
import little.goose.shared.ui.architecture.MviHolder
import org.koin.compose.koinInject

@Composable
fun rememberNotebookHomeStateHolder(
    getNoteWithContentFlowUseCase: GetNoteWithContentFlowUseCase = koinInject(),
    deleteNoteAndItsBlocksListUseCase: DeleteNoteAndItsBlocksListUseCase = koinInject(),
    deleteNoteIdListFlowUseCase: DeleteNoteIdListFlowUseCase = koinInject()
): MviHolder<NoteColumnState, NotebookHomeEvent, NotebookIntent> {

    val coroutineScope = rememberCoroutineScope()

    var noteWithContents: List<NoteItemState> by rememberSaveable {
        mutableStateOf(emptyList())
    }

    var multiSelectedIds by rememberSaveable {
        mutableStateOf<Set<Long>>(emptySet())
    }

    LaunchedEffect(getNoteWithContentFlowUseCase) {
        withContext(Dispatchers.Default) {
            getNoteWithContentFlowUseCase().collectLatest {
                noteWithContents = mapToNoteItemStateList(it, multiSelectedIds)
            }
        }
    }

    val event = remember { MutableSharedFlow<NotebookHomeEvent>() }

    LaunchedEffect(deleteNoteIdListFlowUseCase, event) {
        deleteNoteIdListFlowUseCase().collect {
            event.emit(NotebookHomeEvent.DeleteNote)
        }
    }

    val noteColumnSavableState = remember(noteWithContents, multiSelectedIds.isNotEmpty()) {
        NoteColumnState(noteWithContents, multiSelectedIds.isNotEmpty())
    }

    val cancelMultiSelecting = remember {
        fun() { multiSelectedIds = emptySet() }
    }

    val action: (NotebookIntent) -> Unit = remember {
        fun(intent: NotebookIntent) {
            when (intent) {
                NotebookIntent.CancelMultiSelecting -> cancelMultiSelecting()

                NotebookIntent.SelectAllNotes -> {
                    multiSelectedIds = noteWithContents.map(NoteItemState::id).toSet()
                }

                is NotebookIntent.DeleteMultiSelectingNotes -> {
                    coroutineScope.launch {
                        deleteNoteAndItsBlocksListUseCase(multiSelectedIds.toList())
                        cancelMultiSelecting()
                    }
                }

                is NotebookIntent.SelectNote -> {
                    multiSelectedIds = multiSelectedIds.toMutableSet().apply {
                        if (intent.selected) add(intent.noteId) else remove(intent.noteId)
                    }
                }
            }
        }
    }

    return remember(noteColumnSavableState, event, action) {
        MviHolder(noteColumnSavableState, event, action)
    }
}

private fun mapToNoteItemStateList(
    noteWithContents: List<NoteWithContent>,
    multiSelectedIds: Set<Long>
): List<NoteItemState> = noteWithContents.mapNotNull { noteWithContent ->
    noteWithContent.note.id?.let { noteId ->
        NoteItemState(
            noteId,
            noteWithContent.note.title,
            noteWithContent.content.firstOrNull()?.content ?: "",
            isSelected = multiSelectedIds.contains(noteId)
        )
    }
}