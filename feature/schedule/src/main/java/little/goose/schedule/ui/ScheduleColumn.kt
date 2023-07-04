package little.goose.schedule.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import little.goose.design.system.theme.AccountTheme
import little.goose.schedule.data.entities.Schedule

data class ScheduleColumnState(
    val schedules: List<Schedule> = emptyList(),
    val isMultiSelecting: Boolean = false,
    val multiSelectedSchedules: Set<Schedule> = emptySet(),
    val onSelectSchedule: (item: Schedule, selected: Boolean) -> Unit = {_, _ -> },
    val onCheckedChange: (Schedule, Boolean) -> Unit = {_, _ -> },
    val selectAllSchedules: () -> Unit = {},
    val cancelMultiSelecting: () -> Unit = {},
    val deleteSchedules: (schedules: List<Schedule>) -> Unit = {}
)

@Composable
fun ScheduleColumn(
    modifier: Modifier = Modifier,
    state: ScheduleColumnState,
    onScheduleClick: (Schedule) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
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

@Preview
@Composable
private fun PreviewScheduleColumn() = AccountTheme {
    ScheduleColumn(
        state = ScheduleColumnState(
            schedules = (0..5).map {
                Schedule(
                    id = it.toLong(),
                    title = "title$it",
                    content = "content$it",
                    isfinish = it % 2 == 0
                )
            },
            isMultiSelecting = false,
            multiSelectedSchedules = emptySet(),
            onSelectSchedule = { _, _ -> },
            onCheckedChange = { _, _ -> },
            selectAllSchedules = {},
            cancelMultiSelecting = {},
            deleteSchedules = {}
        ),
        onScheduleClick = {}
    )
}