package little.goose.note.ui.note

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import little.goose.design.system.theme.AccountTheme
import little.goose.ui.screen.LittleGooseLoadingScreen

data class NoteScaffoldState(
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
            val contentModifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPaddingValue)
            when (state) {
                NoteScreenState.Loading -> {
                    LittleGooseLoadingScreen(
                        modifier = contentModifier
                    )
                }

                is NoteScreenState.State -> {
                    NoteContent(
                        modifier = contentModifier,
                        state = state.scaffoldState.contentState,
                        blockColumnState = blockColumnState
                    )
                }
            }
        },
        bottomBar = {
            NoteBottomBar(
                modifier = Modifier.fillMaxWidth(),
                state = when (state) {
                    NoteScreenState.Loading -> remember { NoteBottomBarState() }
                    is NoteScreenState.State -> state.scaffoldState.bottomBarState
                }
            )
        }
    )
}

@Preview
@Composable
private fun PreviewNoteScreen() = AccountTheme {
    NoteScreen(
        state = NoteScreenState.State(NoteScaffoldState()),
        onBack = {},
        blockColumnState = rememberLazyListState()
    )
}