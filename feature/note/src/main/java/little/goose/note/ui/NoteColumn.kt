package little.goose.note.ui

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import little.goose.note.data.entities.Note
import little.goose.note.data.entities.NoteContentBlock
import little.goose.note.logic.notes

data class NoteColumnState(
    val noteWithContents: Map<Note, List<NoteContentBlock>>,
    val isMultiSelecting: Boolean,
    val multiSelectedNotes: Set<Note>,
    val onSelectNote: (item: Note, selected: Boolean) -> Unit = { _, _ -> },
    val selectAllNotes: () -> Unit = {},
    val cancelMultiSelecting: () -> Unit = {},
    val deleteNotes: (notes: List<Note>) -> Unit = {}
)

@Composable
fun NoteColumn(
    modifier: Modifier = Modifier,
    state: NoteColumnState,
    onNoteClick: (Note) -> Unit
) {
    val notes = remember(state.noteWithContents) { state.noteWithContents.notes }
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = {
            items(
                count = notes.size,
                key = { index -> notes[index].id ?: -1 },
                itemContent = {
                    val note = notes[it]
                    val noteContentBlocks = state.noteWithContents[note] ?: emptyList()
                    NoteItem(
                        modifier = Modifier.fillMaxWidth(),
                        note = note,
                        noteContentBlocks = noteContentBlocks,
                        isMultiSelecting = state.isMultiSelecting,
                        onNoteSelect = state.onSelectNote,
                        isSelected = state.multiSelectedNotes.contains(note),
                        onNoteClick = onNoteClick
                    )
                }
            )
        }
    )
}

@Composable
fun NoteItem(
    modifier: Modifier = Modifier,
    note: Note,
    noteContentBlocks: List<NoteContentBlock>,
    isMultiSelecting: Boolean,
    onNoteSelect: (Note, Boolean) -> Unit,
    isSelected: Boolean,
    onNoteClick: (Note) -> Unit
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .combinedClickable(
                    onClick = {
                        if (isMultiSelecting) {
                            onNoteSelect(note, !isSelected)
                        } else {
                            onNoteClick(note)
                        }
                    },
                    onLongClick = {
                        onNoteSelect(note, !isSelected)
                    }
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Text(text = note.title.ifBlank { "Untitled" })
                val firstNoteContentBlock = noteContentBlocks.firstOrNull()
                if (firstNoteContentBlock != null) {
                    Text(text = firstNoteContentBlock.content)
                }
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    tint = MaterialTheme.colorScheme.tertiary,
                    contentDescription = "selected",
                    modifier = Modifier
                        .matchParentSize()
                        .alpha(0.5F)
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewNoteColumn() {
    val noteColumnState = remember {
        val notes = List(10) {
            Note.generateRandomNote()
        }
        val map = buildMap {
            notes.forEach { note ->
                put(note, List(10) {
                    NoteContentBlock.generateRandom(note.id)
                })
            }
        }
        NoteColumnState(
            noteWithContents = map,
            isMultiSelecting = true,
            mutableSetOf()
        )
    }
    NoteColumn(
        modifier = Modifier.fillMaxSize(),
        state = noteColumnState,
        onNoteClick = {}
    )
}

@Preview(widthDp = 390, heightDp = 120)
@Composable
private fun PreviewNoteItem() {
    val note = Note.generateRandomNote()
    NoteItem(
        note = note,
        noteContentBlocks = List(10) {
            NoteContentBlock.generateRandom(noteId = note.id)
        },
        isMultiSelecting = false,
        onNoteSelect = { _, _ -> },
        isSelected = false,
        onNoteClick = {}
    )
}