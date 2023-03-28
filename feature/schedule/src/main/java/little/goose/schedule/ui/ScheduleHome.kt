package little.goose.schedule.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import little.goose.schedule.data.entities.Schedule

@Composable
fun ScheduleHome(
    modifier: Modifier
) {
    val viewModel: ScheduleViewModel = hiltViewModel()
    val scheduleDialogState = rememberScheduleDialogState()
    val scheduleColumnState by viewModel.scheduleColumnState.collectAsState()

    ScheduleScreen(
        modifier = modifier.fillMaxSize(),
        scheduleColumnState = scheduleColumnState,
        onScheduleClick = scheduleDialogState::show
    )

    ScheduleDialog(
        state = scheduleDialogState,
        onDelete = viewModel::deleteSchedule,
        onAdd = viewModel::addSchedule,
        onModify = viewModel::updateSchedule
    )
}

@Composable
private fun ScheduleScreen(
    modifier: Modifier = Modifier,
    scheduleColumnState: ScheduleColumnState,
    onScheduleClick: (Schedule) -> Unit
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background
    ) {
        ScheduleColumn(
            modifier = Modifier.fillMaxSize(),
            state = scheduleColumnState,
            onScheduleClick = onScheduleClick
        )
    }
}