import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import little.goose.note.ui.NoteBottomBar
import little.goose.note.ui.NoteBottomBarState

@Preview
@Composable
private fun PreviewNoteBottomBarEditing() {
    NoteBottomBar(
        state = NoteBottomBarState.Editing()
    )
}

@Preview
@Composable
private fun PreviewNoteBottomBarLoading() {
    NoteBottomBar(
        state = NoteBottomBarState.Loading
    )
}

@Preview
@Composable
private fun PreviewNoteBottomBarPreview() {
    NoteBottomBar(
        state = NoteBottomBarState.Preview()
    )
}