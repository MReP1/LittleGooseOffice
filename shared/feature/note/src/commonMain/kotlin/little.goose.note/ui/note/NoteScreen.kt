package little.goose.note.ui.note

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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