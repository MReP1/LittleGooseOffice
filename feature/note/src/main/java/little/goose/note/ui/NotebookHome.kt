package little.goose.note.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import little.goose.note.data.entities.Note
import little.goose.note.ui.note.NoteActivity

@Composable
fun NotebookHome(
    modifier: Modifier = Modifier,
    noteColumnState: NoteColumnState
) {
    val context = LocalContext.current
    NotebookScreen(
        modifier = modifier,
        noteColumnState = noteColumnState,
        onNoteClick = { note ->
            note.id?.let { noteId ->
                NoteActivity.openEdit(context, noteId)
            }
        }
    )
}

@Composable
fun NotebookScreen(
    modifier: Modifier = Modifier,
    noteColumnState: NoteColumnState,
    onNoteClick: (Note) -> Unit
) {
    NoteColumn(
        modifier = modifier,
        state = noteColumnState,
        onNoteClick = onNoteClick
    )
}