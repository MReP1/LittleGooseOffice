package little.goose.note.ui.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.RemoveDone
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import little.goose.note.ui.notebook.NoteColumn
import little.goose.note.ui.notebook.NoteColumnState
import little.goose.note.ui.notebook.NotebookIntent
import little.goose.shared.ui.button.MovableActionButton
import little.goose.shared.ui.button.MovableActionButtonState
import little.goose.shared.ui.dialog.DeleteDialog
import little.goose.shared.ui.dialog.DeleteDialogState

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun SearchNoteContent(
    modifier: Modifier = Modifier,
    noteColumnState: NoteColumnState,
    onNavigateToNote: (Long) -> Unit,
    action: (NotebookIntent) -> Unit
) {
    if (noteColumnState.noteItemStateList.isNotEmpty()) {
        NoteColumn(
            modifier = modifier.fillMaxSize(),
            state = noteColumnState,
            onNoteClick = onNavigateToNote,
            onSelectNote = { noteId, selected ->
                action(NotebookIntent.SelectNote(noteId, selected))
            }
        )
    }

    val deleteDialogState = remember { DeleteDialogState() }

    if (noteColumnState.isMultiSelecting) {
        val buttonState = remember { MovableActionButtonState() }
        LaunchedEffect(buttonState) { buttonState.expend() }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(
                    WindowInsets.ime.union(BottomAppBarDefaults.windowInsets)
                )
        ) {
            MovableActionButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                state = buttonState,
                mainButtonContent = {
                    Icon(imageVector = Icons.Rounded.Delete, contentDescription = "Delete")
                },
                onMainButtonClick = {
                    deleteDialogState.show(onConfirm = {
                        action(NotebookIntent.DeleteMultiSelectingNotes)
                    })
                },
                topSubButtonContent = {
                    Icon(imageVector = Icons.Rounded.DoneAll, contentDescription = "Select All")
                },
                onTopSubButtonClick = {
                    action(NotebookIntent.SelectAllNotes)
                },
                bottomSubButtonContent = {
                    Icon(imageVector = Icons.Rounded.RemoveDone, contentDescription = "Cancel")
                },
                onBottomSubButtonClick = {
                    action(NotebookIntent.CancelMultiSelecting)
                }
            )
        }
    }

    DeleteDialog(state = deleteDialogState)
}