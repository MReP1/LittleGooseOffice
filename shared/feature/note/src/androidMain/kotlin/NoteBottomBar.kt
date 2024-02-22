import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import little.goose.note.ui.note.NoteBottomBar
import little.goose.note.ui.note.NoteBottomBarState

@Preview
@Composable
private fun PreviewNoteBottomBarEditing() {
    NoteBottomBar(
        state = NoteBottomBarState.Editing,
        action = {}
    )
}

@Preview
@Composable
private fun PreviewNoteBottomBarLoading() {
    NoteBottomBar(
        state = NoteBottomBarState.Loading,
        action = {}
    )
}

@Preview
@Composable
private fun PreviewNoteBottomBarPreview() {
    NoteBottomBar(
        state = NoteBottomBarState.Preview,
        action = {}
    )
}