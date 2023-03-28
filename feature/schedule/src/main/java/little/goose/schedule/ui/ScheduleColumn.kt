package little.goose.schedule.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import little.goose.schedule.data.entities.Schedule

data class ScheduleColumnState(
    val schedules: List<Schedule>,
    val isMultiSelecting: Boolean,
    val multiSelectedSchedules: Set<Schedule>,
    val onSelectSchedule: (item: Schedule, selected: Boolean) -> Unit,
    val onCheckedChange: (Schedule, Boolean) -> Unit,
    val selectAllSchedules: () -> Unit,
    val cancelMultiSelecting: () -> Unit,
    val deleteSchedules: (schedules: List<Schedule>) -> Unit
)

@Composable
fun ScheduleColumn(
    modifier: Modifier = Modifier,
    state: ScheduleColumnState,
    onScheduleClick: (Schedule) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = state.schedules,
            key = { it.id ?: it }
        ) { schedule ->
            ScheduleItem(
                modifier = Modifier.fillMaxWidth(),
                schedule = schedule,
                onCheckedChange = state.onCheckedChange,
                onScheduleClick = onScheduleClick,
                isMultiSelecting = state.isMultiSelecting,
                selected = state.multiSelectedSchedules.contains(schedule),
                onSelectSchedule = state.onSelectSchedule
            )
        }
    }
}