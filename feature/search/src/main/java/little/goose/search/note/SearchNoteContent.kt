package little.goose.search.note

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
import androidx.compose.ui.tooling.preview.Preview
import little.goose.design.system.component.MovableActionButton
import little.goose.design.system.component.MovableActionButtonState
import little.goose.design.system.theme.AccountTheme
import little.goose.note.data.entities.Note
import little.goose.note.data.entities.NoteContentBlock
import little.goose.note.ui.NoteColumn
import little.goose.note.ui.NoteColumnState
import little.goose.note.ui.note.NoteActivity

@Composable
internal fun SearchNoteContent(
    modifier: Modifier = Modifier,
    noteColumnState: NoteColumnState,
) {
    val context = LocalContext.current
    if (noteColumnState.noteWithContents.isNotEmpty()) {
        NoteColumn(
            modifier = modifier.fillMaxSize(),
            state = noteColumnState,
            onNoteClick = { note ->
                note.id?.let { noteId ->
                    NoteActivity.openEdit(context, noteId)
                }
            }
        )
    }

    if (noteColumnState.isMultiSelecting) {
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
                    noteColumnState.deleteNotes(noteColumnState.multiSelectedNotes.toList())
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
}

@Preview
@Composable
private fun PreviewSearchNoteContent() = AccountTheme {
    SearchNoteContent(
        noteColumnState = NoteColumnState(
            noteWithContents = mapOf(
                Note() to listOf(NoteContentBlock(content = "Preview"))
            ),
            isMultiSelecting = false,
            multiSelectedNotes = emptySet()
        )
    )
}