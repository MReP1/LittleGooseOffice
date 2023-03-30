package little.goose.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import little.goose.schedule.data.entities.Schedule
import little.goose.schedule.ui.ScheduleColumn
import little.goose.schedule.ui.ScheduleColumnState
import little.goose.schedule.ui.ScheduleDialog
import little.goose.schedule.ui.rememberScheduleDialogState

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
}