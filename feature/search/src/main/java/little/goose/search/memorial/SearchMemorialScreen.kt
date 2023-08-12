package little.goose.search.memorial

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
import little.goose.memorial.data.entities.Memorial
import little.goose.memorial.ui.component.MemorialColumnState
import little.goose.search.SearchState
import little.goose.search.component.SearchScaffold
import little.goose.ui.screen.LittleGooseEmptyScreen
import little.goose.ui.screen.LittleGooseLoadingScreen

sealed interface SearchMemorialState : SearchState {
    data class Empty(
        override val search: (String) -> Unit
    ) : SearchMemorialState

    data class Loading(
        override val search: (String) -> Unit
    ) : SearchMemorialState

    data class Success(
        val data: MemorialColumnState,
        override val search: (String) -> Unit
    ) : SearchMemorialState
}

sealed interface SearchMemorialEvent {
    data class DeleteMemorials(val memorials: List<Memorial>) : SearchMemorialEvent
}

@Composable
internal fun SearchMemorialRoute(
    modifier: Modifier = Modifier,
    onNavigateToMemorialDialog: (Long) -> Unit,
    onBack: () -> Unit
) {
    val viewModel = hiltViewModel<SearchMemorialViewModel>()
    val context = LocalContext.current
    val searchMemorialState by viewModel.searchMemorialState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(viewModel.searchMemorialEvent) {
        viewModel.searchMemorialEvent.collectLatest { event ->
            when (event) {
                is SearchMemorialEvent.DeleteMemorials -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(little.goose.common.R.string.deleted)
                    )
                }
            }
        }
    }
    SearchMemorialScreen(
        modifier = modifier,
        state = searchMemorialState,
        snackbarHostState = snackbarHostState,
        onNavigateToMemorialDialog = onNavigateToMemorialDialog,
        onBack = onBack
    )
}

@Composable
fun SearchMemorialScreen(
    modifier: Modifier = Modifier,
    state: SearchMemorialState,
    snackbarHostState: SnackbarHostState,
    onNavigateToMemorialDialog: (Long) -> Unit,
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
        }, onBack = onBack
    ) {
        AnimatedContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            transitionSpec = {
                val durationMillis = 320
                if (this.initialState is SearchMemorialState.Success &&
                    this.targetState is SearchMemorialState.Success
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
                        towards = if (targetState is SearchMemorialState.Success)
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
                        towards = if (targetState is SearchMemorialState.Success)
                            AnimatedContentTransitionScope.SlideDirection.Down
                        else AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(durationMillis),
                        targetOffset = { offset -> offset / 2 }
                    )
                }
            },
            targetState = state,
            label = "search memorial content"
        ) { state ->
            when (state) {
                is SearchMemorialState.Empty -> {
                    LittleGooseEmptyScreen(modifier = Modifier.fillMaxSize())
                }

                is SearchMemorialState.Loading -> {
                    LittleGooseLoadingScreen(modifier = Modifier.fillMaxSize())
                }

                is SearchMemorialState.Success -> {
                    SearchMemorialContent(
                        modifier = Modifier.fillMaxSize(),
                        memorialColumnState = state.data,
                        onNavigateToMemorial = onNavigateToMemorialDialog
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewSearchMemorialScreen() = AccountTheme {
    SearchMemorialScreen(
        state = SearchMemorialState.Success(
            data = MemorialColumnState(
                memorials = listOf(
                    Memorial(id = 1, content = "纪念日"),
                    Memorial(id = 2, content = "纪念日")
                ),
            ),
            search = {}
        ),
        snackbarHostState = SnackbarHostState(),
        onNavigateToMemorialDialog = {},
        onBack = {}
    )
}