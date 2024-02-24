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
sealed class NoteScreenState {

    data object Loading : NoteScreenState()

    data class Success(
        val contentState: NoteContentState,
        val bottomBarState: NoteBottomBarState
    ) : NoteScreenState()

}

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
            when (noteScreenState) {
                NoteScreenState.Loading -> {
                    // Loading Screen
                }

                is NoteScreenState.Success -> {
                    NoteContent(
                        state = noteScreenState.contentState,
                        blockColumnState = blockColumnState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        action = action
                    )
                }
            }
        },
        bottomBar = {
            NoteBottomBar(
                state = when (noteScreenState) {
                    NoteScreenState.Loading -> NoteBottomBarState.Loading
                    is NoteScreenState.Success -> noteScreenState.bottomBarState
                },
                modifier = Modifier.fillMaxWidth(),
                action = action
            )
        }
    )
}