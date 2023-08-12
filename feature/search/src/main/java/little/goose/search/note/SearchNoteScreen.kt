package little.goose.search.note

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import little.goose.design.system.theme.AccountTheme
import little.goose.note.data.entities.Note
import little.goose.note.data.entities.NoteContentBlock
import little.goose.note.ui.NoteColumnState
import little.goose.search.SearchState
import little.goose.search.component.SearchScaffold
import little.goose.ui.screen.LittleGooseEmptyScreen
import little.goose.ui.screen.LittleGooseLoadingScreen

sealed interface SearchNoteState : SearchState {
    data class Loading(
        override val search: (String) -> Unit
    ) : SearchNoteState

    data class Success(
        val data: NoteColumnState,
        override val search: (String) -> Unit
    ) : SearchNoteState

    data class Empty(
        override val search: (String) -> Unit
    ) : SearchNoteState
}

sealed interface SearchNoteEvent {
    data class DeleteNotes(val notes: List<Note>) : SearchNoteEvent
}

@Composable
internal fun SearchNoteRoute(
    modifier: Modifier = Modifier,
    onNavigateToNote: (Long) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<SearchNoteViewModel>()
    val searchNoteState by viewModel.searchNoteState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.searchNoteEvent.collectLatest { event ->
            when (event) {
                is SearchNoteEvent.DeleteNotes -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(little.goose.common.R.string.deleted)
                    )
                }
            }
        }
    }

    SearchNoteScreen(
        modifier = modifier,
        state = searchNoteState,
        snackbarHostState = snackbarHostState,
        onNavigateToNote = onNavigateToNote,
        onBack = onBack
    )
}

@Composable
fun SearchNoteScreen(
    modifier: Modifier = Modifier,
    state: SearchNoteState,
    snackbarHostState: SnackbarHostState,
    onNavigateToNote: (Long) -> Unit,
    onBack: () -> Unit
) {
    var keyword by rememberSaveable { mutableStateOf("") }
    SearchScaffold(
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        keyword = keyword,
        onKeywordChange = {
            keyword = it
            state.search(it)
        },
        onBack = onBack
    ) {
        AnimatedContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            transitionSpec = {
                val durationMillis = 320
                if (this.initialState is SearchNoteState.Success &&
                    this.targetState is SearchNoteState.Success
                ) {
                    fadeIn(
                        initialAlpha = 0.8F, animationSpec = tween(durationMillis = 100)
                    ) with fadeOut(
                        animationSpec = tween(durationMillis = 100)
                    )
                } else {
                    fadeIn(
                        animationSpec = tween(durationMillis)
                    ) + slideIntoContainer(
                        towards = if (targetState is SearchNoteState.Success)
                            AnimatedContentTransitionScope.SlideDirection.Down
                        else AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        ),
                        initialOffset = { offset -> offset / 2 }
                    ) with fadeOut(
                        animationSpec = tween(durationMillis)
                    ) + slideOutOfContainer(
                        towards = if (targetState is SearchNoteState.Success)
                            AnimatedContentTransitionScope.SlideDirection.Down
                        else AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(durationMillis),
                        targetOffset = { offset -> offset / 2 }
                    )
                }
            },
            targetState = state,
            label = "search note content"
        ) { state ->
            when (state) {
                is SearchNoteState.Empty -> {
                    LittleGooseEmptyScreen(
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is SearchNoteState.Loading -> {
                    LittleGooseLoadingScreen(
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is SearchNoteState.Success -> {
                    SearchNoteContent(
                        modifier = Modifier.fillMaxSize(),
                        noteColumnState = state.data,
                        onNavigateToNote = onNavigateToNote,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewSearchNoteScreen() = AccountTheme {
    SearchNoteScreen(
        state = SearchNoteState.Success(
            data = NoteColumnState(
                noteWithContents = mapOf(
                    Note() to listOf(NoteContentBlock(content = "Preview"))
                ),
                isMultiSelecting = false,
                multiSelectedNotes = emptySet()
            ),
            search = {}
        ),
        snackbarHostState = SnackbarHostState(),
        onNavigateToNote = {},
        onBack = {}
    )
}