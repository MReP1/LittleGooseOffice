package little.goose.note.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import little.goose.note.data.entities.Note
import little.goose.note.ui.note.NoteActivity

@Composable
fun NotebookHome(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel = viewModel<NotebookViewModel>()
    val notes by viewModel.notes.collectAsState()
    NotebookScreen(
        modifier = modifier,
        notes = notes,
        onNoteClick = { NoteActivity.openEdit(context, it) }
    )
}

@Composable
fun NotebookScreen(
    modifier: Modifier = Modifier,
    notes: List<Note>,
    onNoteClick: (Note) -> Unit
) {
    NoteGrid(
        modifier = modifier,
        notes = notes,
        onNoteClick = onNoteClick
    )
}