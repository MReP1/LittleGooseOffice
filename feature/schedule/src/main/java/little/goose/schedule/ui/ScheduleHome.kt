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
    val schedules by viewModel.schedules.collectAsState()
    val scheduleDialogState = rememberScheduleDialogState()
    ScheduleScreen(
        modifier = modifier.fillMaxSize(),
        schedules = schedules,
        onScheduleClick = {
            scheduleDialogState.show(it)
        },
        onCheckedChange = { schedule, isChecked ->
            viewModel.updateSchedule(schedule.copy(isfinish = isChecked))
        }
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
    schedules: List<Schedule>,
    onScheduleClick: (Schedule) -> Unit,
    onCheckedChange: (Schedule, Boolean) -> Unit
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background
    ) {
        ScheduleColumn(
            modifier = Modifier.fillMaxSize(),
            schedules = schedules,
            onScheduleClick = onScheduleClick,
            onCheckedChange = onCheckedChange
        )
    }
}