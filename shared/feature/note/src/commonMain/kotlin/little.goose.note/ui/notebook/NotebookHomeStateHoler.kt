package little.goose.note.ui.notebook

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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import little.goose.data.note.bean.NoteWithContent
import little.goose.data.note.domain.DeleteNoteAndItsBlocksListUseCase
import little.goose.data.note.domain.GetNoteWithContentFlowUseCase
import little.goose.shared.ui.architecture.MviHolder
import little.goose.shared.ui.architecture.mutableStateFlowAutoSaver
import org.koin.compose.getKoin

@Composable
fun rememberNotebookHomeStateHolder(): MviHolder<NoteColumnState, NotebookHomeEvent, NotebookIntent> {
    val koin = getKoin()
    val coroutineScope = rememberCoroutineScope()

    val getNoteWithContentFlowUseCase = remember(koin) {
        koin.get<GetNoteWithContentFlowUseCase>()
    }

    val deleteNoteAndItsBlocksListUseCase = remember(koin) {
        koin.get<DeleteNoteAndItsBlocksListUseCase>()
    }

    val noteWithContents = remember(getNoteWithContentFlowUseCase) {
        getNoteWithContentFlowUseCase().stateIn(coroutineScope, SharingStarted.Eagerly, emptyList())
    }

    val multiSelectedIds = rememberSaveable(
        saver = mutableStateFlowAutoSaver()
    ) { MutableStateFlow<Set<Long>>(emptySet()) }

    var noteColumnSavableState by rememberSaveable(stateSaver = NoteColumnState.saver) {
        mutableStateOf(
            NoteColumnState(
                noteItemStateList = mapToNoteItemStateList(
                    noteWithContents.value,
                    multiSelectedIds.value
                ),
                isMultiSelecting = multiSelectedIds.value.isNotEmpty()
            )
        )
    }

    LaunchedEffect(noteWithContents, multiSelectedIds) {
        combine(multiSelectedIds, noteWithContents) { multiSelectedNotes, noteWithContents ->
            NoteColumnState(
                noteItemStateList = mapToNoteItemStateList(noteWithContents, multiSelectedNotes),
                isMultiSelecting = multiSelectedNotes.isNotEmpty()
            )
        }.collect {
            noteColumnSavableState = it
        }
    }

    val event = remember { MutableSharedFlow<NotebookHomeEvent>() }

    val action: (NotebookIntent) -> Unit = remember {

        fun cancelMultiSelecting() {
            multiSelectedIds.value = emptySet()
        }

        fun(intent: NotebookIntent) {
            when (intent) {
                NotebookIntent.CancelMultiSelecting -> cancelMultiSelecting()

                NotebookIntent.SelectAllNotes -> {
                    multiSelectedIds.value = noteWithContents.value
                        .mapNotNull { it.note.id }.toSet()
                }

                is NotebookIntent.DeleteMultiSelectingNotes -> {
                    coroutineScope.launch {
                        deleteNoteAndItsBlocksListUseCase(multiSelectedIds.value.toList())
                        cancelMultiSelecting()
                    }
                }

                is NotebookIntent.SelectNote -> {
                    multiSelectedIds.update {
                        it.toMutableSet().apply {
                            if (intent.selected) add(intent.noteId) else remove(intent.noteId)
                        }
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
): List<NoteItemState> {
    return noteWithContents.mapNotNull { noteWithContent ->
        noteWithContent.note.id?.let { noteId ->
            NoteItemState(
                noteId,
                noteWithContent.note.title,
                noteWithContent.content.firstOrNull()?.content ?: "",
                isSelected = multiSelectedIds.contains(noteId)
            )
        }
    }
}