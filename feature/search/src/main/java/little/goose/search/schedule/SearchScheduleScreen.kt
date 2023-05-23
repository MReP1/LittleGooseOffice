package little.goose.search.schedule

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.RemoveDone
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import little.goose.design.system.component.MovableActionButton
import little.goose.design.system.component.MovableActionButtonState
import little.goose.design.system.theme.AccountTheme
import little.goose.schedule.data.entities.Schedule
import little.goose.schedule.ui.ScheduleColumn
import little.goose.schedule.ui.ScheduleColumnState
import little.goose.schedule.ui.ScheduleDialog
import little.goose.schedule.ui.rememberScheduleDialogState
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
    modifier: Modifier = Modifier
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

    SearchScheduleScreen2(
        modifier = modifier,
        state = searchScheduleState,
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun SearchScheduleScreen2(
    modifier: Modifier,
    state: SearchScheduleState,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                Snackbar(snackbarData)
            }
        },
        topBar = {
            var keyword by remember { mutableStateOf("") }
            SearchTopAppBar(
                keyword = keyword,
                onKeywordChange = {
                    keyword = it
                    state.search(it)
                }
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

@Composable
internal fun SearchScheduleScreen(
    modifier: Modifier = Modifier,
    scheduleColumnState: ScheduleColumnState,
    deleteSchedule: (Schedule) -> Unit,
    addSchedule: (Schedule) -> Unit,
    modifySchedule: (Schedule) -> Unit
) {
    if (scheduleColumnState.schedules.isNotEmpty()) {
        val scheduleDialogState = rememberScheduleDialogState()
        ScheduleColumn(
            modifier = modifier.fillMaxSize(),
            state = scheduleColumnState,
            onScheduleClick = scheduleDialogState::show
        )
        ScheduleDialog(
            state = scheduleDialogState,
            onDelete = deleteSchedule,
            onAdd = addSchedule,
            onModify = modifySchedule
        )
    }

    if (scheduleColumnState.isMultiSelecting) {
        val buttonState = remember { MovableActionButtonState() }
        LaunchedEffect(buttonState) { buttonState.expend() }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            MovableActionButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                state = buttonState,
                mainButtonContent = {
                    Icon(imageVector = Icons.Rounded.Delete, contentDescription = "Delete")
                },
                onMainButtonClick = {
                    scheduleColumnState.deleteSchedules(
                        scheduleColumnState.multiSelectedSchedules.toList()
                    )
                },
                topSubButtonContent = {
                    Icon(imageVector = Icons.Rounded.DoneAll, contentDescription = "SelectAll")
                },
                onTopSubButtonClick = {
                    scheduleColumnState.selectAllSchedules()
                },
                bottomSubButtonContent = {
                    Icon(imageVector = Icons.Rounded.RemoveDone, contentDescription = "Remove")
                },
                onBottomSubButtonClick = {
                    scheduleColumnState.cancelMultiSelecting()
                }
            )
        }
    }
}

@Preview
@Composable
private fun PreviewSearchScheduleScreen() = AccountTheme {
    SearchScheduleScreen(
        scheduleColumnState = ScheduleColumnState(
            schedules = (0..5).map {
                Schedule(
                    id = it.toLong(),
                    title = "title$it",
                    content = "content$it",
                    isfinish = it % 2 == 0
                )
            },
            isMultiSelecting = true,
            multiSelectedSchedules = emptySet(),
            onSelectSchedule = { _, _ -> },
            onCheckedChange = { _, _ -> },
            selectAllSchedules = {},
            cancelMultiSelecting = {},
            deleteSchedules = {}
        ),
        deleteSchedule = {},
        addSchedule = {},
        modifySchedule = {}
    )
}