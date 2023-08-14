package little.goose.search.note

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.isImeVisible
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
import androidx.compose.ui.tooling.preview.Preview
import little.goose.design.system.component.MovableActionButton
import little.goose.design.system.component.MovableActionButtonState
import little.goose.design.system.component.dialog.DeleteDialog
import little.goose.design.system.component.dialog.DeleteDialogState
import little.goose.design.system.theme.AccountTheme
import little.goose.note.data.entities.Note
import little.goose.note.data.entities.NoteContentBlock
import little.goose.note.ui.NoteColumn
import little.goose.note.ui.NoteColumnState

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun SearchNoteContent(
    modifier: Modifier = Modifier,
    noteColumnState: NoteColumnState,
    onNavigateToNote: (Long) -> Unit
) {
    if (noteColumnState.noteWithContents.isNotEmpty()) {
        NoteColumn(
            modifier = modifier.fillMaxSize(),
            state = noteColumnState,
            onNoteClick = { note ->
                note.id?.let { onNavigateToNote(it) }
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
                    if (WindowInsets.isImeVisible) {
                        WindowInsets.ime.union(BottomAppBarDefaults.windowInsets)
                    } else {
                        BottomAppBarDefaults.windowInsets
                    }
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
                        noteColumnState.deleteNotes(noteColumnState.multiSelectedNotes.toList())
                    })
                },
                topSubButtonContent = {
                    Icon(imageVector = Icons.Rounded.DoneAll, contentDescription = "Select All")
                },
                onTopSubButtonClick = {
                    noteColumnState.selectAllNotes()
                },
                bottomSubButtonContent = {
                    Icon(imageVector = Icons.Rounded.RemoveDone, contentDescription = "Cancel")
                },
                onBottomSubButtonClick = {
                    noteColumnState.cancelMultiSelecting()
                }
            )
        }
    }

    DeleteDialog(state = deleteDialogState)
}

@Preview
@Composable
private fun PreviewSearchNoteContent() = AccountTheme {
    SearchNoteContent(
        noteColumnState = NoteColumnState(
            noteWithContents = mapOf(
                Note() to listOf(NoteContentBlock(content = "Preview")),
                Note() to listOf(NoteContentBlock(content = "Preview")),
                Note() to listOf(NoteContentBlock(content = "Preview"))
            ),
            isMultiSelecting = false,
            multiSelectedNotes = emptySet()
        ),
        onNavigateToNote = {}
    )
}