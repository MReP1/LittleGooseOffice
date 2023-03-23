package little.goose.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import little.goose.note.data.entities.Note
import little.goose.note.ui.NoteGrid
import little.goose.note.ui.note.NoteActivity

@Composable
internal fun SearchNoteScreen(
    modifier: Modifier = Modifier,
    notes: List<Note>
) {
    val context = LocalContext.current
    if (notes.isNotEmpty()) {
        NoteGrid(
            modifier = modifier.fillMaxSize(),
            notes = notes,
            onNoteClick = {
                NoteActivity.openEdit(context, it)
            }
        )
    }
}