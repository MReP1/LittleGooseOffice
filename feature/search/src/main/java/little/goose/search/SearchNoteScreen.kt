package little.goose.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.RemoveDone
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import little.goose.design.system.component.MovableActionButton
import little.goose.design.system.component.MovableActionButtonState
import little.goose.note.ui.NoteGrid
import little.goose.note.ui.NoteGridState
import little.goose.note.ui.note.NoteActivity

@Composable
internal fun SearchNoteScreen(
    modifier: Modifier = Modifier,
    noteGridState: NoteGridState,
) {
    val context = LocalContext.current
    if (noteGridState.notes.isNotEmpty()) {
        NoteGrid(
            modifier = modifier.fillMaxSize(),
            state = noteGridState,
            onNoteClick = {
                NoteActivity.openEdit(context, it)
            }
        )
    }

    if (noteGridState.isMultiSelecting) {
        val buttonState = remember { MovableActionButtonState() }
        LaunchedEffect(buttonState) { buttonState.expend() }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            MovableActionButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                state = buttonState,
                mainButtonContent = {
                    Icon(imageVector = Icons.Rounded.Delete, contentDescription = "Delete")
                },
                onMainButtonClick = {
                    noteGridState.deleteNotes(noteGridState.multiSelectedNotes.toList())
                },
                topSubButtonContent = {
                    Icon(imageVector = Icons.Rounded.DoneAll, contentDescription = "Select All")
                },
                onTopSubButtonClick = {
                    noteGridState.selectAllNotes()
                },
                bottomSubButtonContent = {
                    Icon(imageVector = Icons.Rounded.RemoveDone, contentDescription = "Cancel")
                },
                onBottomSubButtonClick = {
                    noteGridState.cancelMultiSelecting()
                }
            )
        }
    }
}