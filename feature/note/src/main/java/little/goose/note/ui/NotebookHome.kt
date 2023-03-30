package little.goose.note.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import little.goose.note.data.entities.Note
import little.goose.note.ui.note.NoteActivity

@Composable
fun NotebookHome(
    modifier: Modifier = Modifier,
    noteGridState: NoteGridState
) {
    val context = LocalContext.current
    NotebookScreen(
        modifier = modifier,
        noteGridState = noteGridState,
        onNoteClick = { NoteActivity.openEdit(context, it) }
    )
}

@Composable
fun NotebookScreen(
    modifier: Modifier = Modifier,
    noteGridState: NoteGridState,
    onNoteClick: (Note) -> Unit
) {
    NoteGrid(
        modifier = modifier,
        state = noteGridState,
        onNoteClick = onNoteClick
    )
}