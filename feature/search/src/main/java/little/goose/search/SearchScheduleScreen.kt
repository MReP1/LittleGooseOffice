package little.goose.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import little.goose.schedule.data.entities.Schedule
import little.goose.schedule.ui.ScheduleColumn

@Composable
internal fun SearchScheduleScreen(
    modifier: Modifier = Modifier,
    schedules: List<Schedule>
) {
    if (schedules.isNotEmpty()) {
        ScheduleColumn(
            modifier = modifier.fillMaxSize(),
            schedules = schedules,
            onScheduleClick = {

            },
            onCheckedChange = { schedule, checked ->

            }
        )
    }
}