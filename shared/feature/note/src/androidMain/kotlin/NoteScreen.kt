import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text2.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import little.goose.note.ui.NoteBlockState
import little.goose.note.ui.NoteBottomBarState
import little.goose.note.ui.NoteContentState
import little.goose.note.ui.NoteScreen

@Preview
@Composable
fun PreviewNoteScreenLoading() {
    NoteScreen(
        onBack = {},
        modifier = Modifier.fillMaxSize(),
        noteContentState = NoteContentState.Loading,
        bottomBarState = NoteBottomBarState.Loading,
        blockColumnState = rememberLazyListState()
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun PreviewNoteScreenEditing() {
    NoteScreen(
        onBack = {},
        modifier = Modifier.fillMaxSize(),
        noteContentState = NoteContentState.Edit(
            titleState = rememberTextFieldState(System.currentTimeMillis().toString()),
            contentStateList = List(10) {
                NoteBlockState(
                    it.toLong(),
                    rememberTextFieldState(System.currentTimeMillis().toString())
                )
            },
            onBlockDelete = {}
        ),
        bottomBarState = NoteBottomBarState.Editing(),
        blockColumnState = rememberLazyListState()
    )
}

@Preview
@Composable
fun PreviewNoteScreenPreviewing() {
    NoteScreen(
        onBack = {},
        modifier = Modifier.fillMaxSize(),
        noteContentState = NoteContentState.Preview(
            content = """
                # Title
                
                ## Title2
                
                Hello world!
                
            """.trimIndent()
        ),
        bottomBarState = NoteBottomBarState.Preview(),
        blockColumnState = rememberLazyListState()
    )
}