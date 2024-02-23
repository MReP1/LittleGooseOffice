package little.goose.note.ui.note

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier

@Stable
data class NoteScreenState(
    val contentState: NoteContentState,
    val bottomBarState: NoteBottomBarState
)

@Composable
fun NoteScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    noteScreenState: NoteScreenState,
    blockColumnState: LazyListState,
    action: (NoteScreenIntent) -> Unit
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
                state = noteScreenState.contentState,
                blockColumnState = blockColumnState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                action = action
            )
        },
        bottomBar = {
            NoteBottomBar(
                state = noteScreenState.bottomBarState,
                modifier = Modifier.fillMaxWidth(),
                action = action
            )
        }
    )
}