package little.goose.search.schedule

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
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
import little.goose.search.R
import little.goose.search.SearchState
import little.goose.search.component.SearchTopAppBar
import little.goose.ui.screen.LittleGooseEmptyScreen
import little.goose.ui.screen.LittleGooseLoadingScreen

sealed interface SearchScheduleState : SearchState {

    data class Loading(
        override val search: (String) -> Unit
    ) : SearchScheduleState

    data class Success(
        val data: ScheduleColumnState,
        val deleteSchedule: (Schedule) -> Unit,
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
fun SearchScheduleRoute(
    modifier: Modifier = Modifier,
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
                    snackbarHostState.showSnackbar(message = context.getString(R.string.deleted))
                }
            }
        }
    }

    SearchScheduleScreen(
        modifier = modifier,
        state = searchScheduleState,
        snackbarHostState = snackbarHostState,
        onBack = onBack
    )
}

@Composable
fun SearchScheduleScreen(
    modifier: Modifier,
    state: SearchScheduleState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                Snackbar(snackbarData)
            }
        },
        topBar = {
            var keyword by rememberSaveable { mutableStateOf("") }
            SearchTopAppBar(
                keyword = keyword,
                onKeywordChange = {
                    keyword = it
                    state.search(it)
                },
                onBack = onBack
            )
        },
        content = {
            val contentModifier = Modifier
                .fillMaxSize()
                .padding(it)
            when (state) {
                is SearchScheduleState.Empty -> {
                    LittleGooseEmptyScreen(modifier = contentModifier)
                }

                is SearchScheduleState.Loading -> {
                    LittleGooseLoadingScreen(modifier = contentModifier)
                }

                is SearchScheduleState.Success -> {
                    SearchScheduleContent(
                        modifier = contentModifier,
                        scheduleColumnState = state.data,
                        addSchedule = state.addSchedule,
                        modifySchedule = state.modifySchedule,
                        deleteSchedule = state.deleteSchedule
                    )
                }
            }
        }
    )
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
            deleteSchedule = {},
            search = {}
        ),
        snackbarHostState = SnackbarHostState(),
        onBack = {}
    )
}