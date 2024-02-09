@file:OptIn(ExperimentalFoundationApi::class)

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text2.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import little.goose.note.ui.note.NoteContentBlockItem
import little.goose.note.ui.note.NoteContentBlockTextField

@Preview
@Composable
private fun PreviewNoteContentBlockItem() {
    NoteContentBlockItem(
        modifier = Modifier.fillMaxWidth(),
        textFieldState = rememberTextFieldState(initialText = "Hello Horld!"),
        onBlockDelete = {}
    )
}

@Preview
@Composable
private fun PreviewNoteContentBlockTextField() {
    NoteContentBlockTextField(
        modifier = Modifier.fillMaxWidth(),
        textFieldState = rememberTextFieldState(initialText = "Hello Horld!")
    )
}