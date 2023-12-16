package little.goose.note.ui.note

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text2.input.rememberTextFieldState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun NoteScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    noteContentState: NoteContentState,
    bottomBarState: NoteBottomBarState,
    blockColumnState: LazyListState
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            NoteTopBar(
                onBack = onBack,
                modifier = Modifier.fillMaxWidth()
            )
        },
        content = { paddingValues ->
            NoteContent(
                state = noteContentState,
                blockColumnState = blockColumnState,
                onAddBlock = (bottomBarState as? NoteBottomBarState.Editing)?.onBlockAdd ?: {},
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        },
        bottomBar = {
            NoteBottomBar(
                state = bottomBarState,
                modifier = Modifier.fillMaxWidth()
            )
        }
    )
}

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