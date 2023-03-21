package little.goose.note.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import little.goose.note.data.entities.Note
import little.goose.note.ui.note.NoteActivity
import middle.goose.richtext.RichTextView

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
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        content = {
            items(
                items = notes,
                key = { it.id ?: -1 }
            ) { note ->
                NoteItem(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .height(120.dp),
                    note = note,
                    onNoteClick = onNoteClick
                )
            }
        }
    )
}

@Composable
fun NoteItem(
    modifier: Modifier = Modifier,
    note: Note,
    onNoteClick: (Note) -> Unit
) {
    Card(
        modifier = modifier,
        onClick = {
            onNoteClick(note)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Text(text = note.title)
            AndroidView(
                factory = { RichTextView(it) },
                modifier = Modifier.weight(1f)
            ) { textureView ->
                textureView.fromHtml(note.content)
            }
        }
    }
}