package little.goose.note.ui.note

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import little.goose.design.system.theme.AccountTheme

data class NoteScreenState(
    val contentState: NoteContentState = NoteContentState(),
    val bottomBarState: NoteBottomBarState = NoteBottomBarState()
)

@Composable
fun NoteScreen(
    modifier: Modifier = Modifier,
    state: NoteScreenState,
    blockColumnState: LazyListState,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            NoteTopBar(
                modifier = Modifier.fillMaxWidth(),
                onBack = onBack
            )
        },
        content = { scaffoldPaddingValue ->
            NoteContent(
                modifier = Modifier.padding(scaffoldPaddingValue),
                state = state.contentState,
                blockColumnState = blockColumnState
            )
        },
        bottomBar = {
            NoteBottomBar(
                modifier = Modifier.fillMaxWidth(),
                state = state.bottomBarState
            )
        }
    )
}

@Preview
@Composable
private fun PreviewNoteScreen() = AccountTheme {
    NoteScreen(
        state = NoteScreenState(),
        onBack = {},
        blockColumnState = rememberLazyListState()
    )
}