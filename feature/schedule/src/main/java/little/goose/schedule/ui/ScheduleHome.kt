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
    modifier: Modifier,
    scheduleColumnState: ScheduleColumnState,
    deleteSchedule: (Schedule) -> Unit,
    addSchedule: (Schedule) -> Unit,
    modifySchedule: (Schedule) -> Unit
) {
    val scheduleDialogState = rememberScheduleDialogState()

    ScheduleScreen(
        modifier = modifier.fillMaxSize(),
        scheduleColumnState = scheduleColumnState,
        onScheduleClick = scheduleDialogState::show
    )

    ScheduleDialog(
        state = scheduleDialogState,
        onDelete = deleteSchedule,
        onAdd = addSchedule,
        onModify = modifySchedule
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