package little.goose.note.ui.search

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import little.goose.shared.ui.screen.LittleGooseEmptyScreen
import little.goose.shared.ui.screen.LittleGooseLoadingScreen
import little.goose.shared.ui.search.SearchScreen


@Composable
fun SearchNoteScreen(
    modifier: Modifier = Modifier,
    state: SearchNoteState,
    snackbarHostState: SnackbarHostState,
    action: (SearchNoteIntent) -> Unit,
    onNavigateToNote: (Long) -> Unit,
    onBack: () -> Unit
) {
    var keyword by rememberSaveable { mutableStateOf("") }
    SearchScreen(
        modifier = modifier,
        keyword = keyword,
        onKeywordChange = {
            keyword = it
            action(SearchNoteIntent.Search(it))
        },
        snackbarHostState = snackbarHostState,
        onBack = onBack
    ) {
        AnimatedContent(
            modifier = Modifier.fillMaxSize(),
            transitionSpec = {
                val durationMillis = 320
                if (this.initialState is SearchNoteState.Success &&
                    this.targetState is SearchNoteState.Success
                ) {
                    fadeIn(
                        initialAlpha = 0.8F, animationSpec = tween(durationMillis = 100)
                    ) togetherWith fadeOut(
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
                    ) togetherWith fadeOut(
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
                        action = { action(SearchNoteIntent.NotebookIntent(it)) }
                    )
                }
            }
        }
    }
}