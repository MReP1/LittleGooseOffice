package little.goose.note.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import little.goose.note.data.entities.Note

@Composable
fun NotebookHome(
    modifier: Modifier = Modifier,
    noteColumnState: NoteColumnState,
    onNavigateToNote: (noteId: Long) -> Unit
) {
    NotebookScreen(
        modifier = modifier,
        noteColumnState = noteColumnState,
        onNoteClick = { note ->
            note.id?.let { noteId ->
                onNavigateToNote(noteId)
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