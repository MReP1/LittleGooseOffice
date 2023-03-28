package little.goose.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import little.goose.schedule.ui.ScheduleColumn
import little.goose.schedule.ui.ScheduleColumnState

@Composable
internal fun SearchScheduleScreen(
    modifier: Modifier = Modifier,
    scheduleColumnState: ScheduleColumnState
) {
    if (scheduleColumnState.schedules.isNotEmpty()) {
        ScheduleColumn(
            modifier = modifier.fillMaxSize(),
            state = scheduleColumnState,
            onScheduleClick = {

            }
        )
    }
}