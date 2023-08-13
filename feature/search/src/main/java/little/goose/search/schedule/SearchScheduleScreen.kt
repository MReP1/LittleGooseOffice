package little.goose.search.schedule

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
import little.goose.schedule.data.entities.Schedule
import little.goose.schedule.ui.ScheduleColumnState
import little.goose.search.SearchState
import little.goose.search.component.SearchScreen
import little.goose.ui.screen.LittleGooseEmptyScreen
import little.goose.ui.screen.LittleGooseLoadingScreen

sealed interface SearchScheduleState : SearchState {

    data class Loading(
        override val search: (String) -> Unit
    ) : SearchScheduleState

    data class Success(
        val data: ScheduleColumnState,
        val addSchedule: (Schedule) -> Unit,
        val modifySchedule: (Schedule) -> Unit,
        override val search: (String) -> Unit
    ) : SearchScheduleState

    data class Empty(
        override val search: (String) -> Unit
    ) : SearchScheduleState
}

sealed interface SearchScheduleEvent {
    data class DeleteSchedules(val schedules: List<Schedule>) : SearchScheduleEvent
}

@Composable
internal fun SearchScheduleRoute(
    modifier: Modifier = Modifier,
    onNavigateToScheduleDialog: (Long) -> Unit,
    onBack: () -> Unit
) {
    val viewModel = hiltViewModel<SearchScheduleViewModel>()
    val searchScheduleState by viewModel.searchScheduleState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.searchScheduleEvent.collectLatest { event ->
            when (event) {
                is SearchScheduleEvent.DeleteSchedules -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(little.goose.common.R.string.deleted)
                    )
                }
            }
        }
    }

    SearchScheduleScreen(
        modifier = modifier,
        state = searchScheduleState,
        snackbarHostState = snackbarHostState,
        onNavigateToScheduleDialog = onNavigateToScheduleDialog,
        onBack = onBack
    )
}

@Composable
fun SearchScheduleScreen(
    modifier: Modifier,
    state: SearchScheduleState,
    snackbarHostState: SnackbarHostState,
    onNavigateToScheduleDialog: (Long) -> Unit,
    onBack: () -> Unit
) {
    var keyword by rememberSaveable { mutableStateOf("") }
    SearchScreen(
        modifier = modifier,
        keyword = keyword,
        onKeywordChange = {
            keyword = it
            state.search(it)
        },
        snackbarHostState = snackbarHostState,
        onBack = onBack
    ) {
        AnimatedContent(
            modifier = Modifier.fillMaxSize(),
            transitionSpec = {
                val durationMillis = 320
                if (this.initialState is SearchScheduleState.Success &&
                    this.targetState is SearchScheduleState.Success
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
                        towards = if (targetState is SearchScheduleState.Success)
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
                        towards = if (targetState is SearchScheduleState.Success)
                            AnimatedContentTransitionScope.SlideDirection.Down
                        else AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(durationMillis),
                        targetOffset = { offset -> offset / 2 }
                    )
                }
            },
            targetState = state,
            label = "search schedule content"
        ) { state ->
            when (state) {
                is SearchScheduleState.Empty -> {
                    LittleGooseEmptyScreen(modifier = Modifier.fillMaxSize())
                }

                is SearchScheduleState.Loading -> {
                    LittleGooseLoadingScreen(modifier = Modifier.fillMaxSize())
                }

                is SearchScheduleState.Success -> {
                    SearchScheduleContent(
                        modifier = Modifier.fillMaxSize(),
                        scheduleColumnState = state.data,
                        onNavigateToScheduleDialog = onNavigateToScheduleDialog
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewSearchScheduleScreen() = AccountTheme {
    SearchScheduleScreen(
        modifier = Modifier,
        state = SearchScheduleState.Success(
            data = ScheduleColumnState(
                schedules = listOf(
                    Schedule(title = "title", content = "content")
                ),
                isMultiSelecting = false,
                multiSelectedSchedules = emptySet(),
            ),
            addSchedule = {},
            modifySchedule = {},
            search = {}
        ),
        snackbarHostState = SnackbarHostState(),
        onNavigateToScheduleDialog = {},
        onBack = {}
    )
}