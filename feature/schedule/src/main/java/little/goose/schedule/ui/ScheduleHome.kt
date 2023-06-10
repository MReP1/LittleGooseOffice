package little.goose.schedule.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import little.goose.schedule.data.entities.Schedule

@Composable
fun ScheduleHome(
    modifier: Modifier,
    scheduleColumnState: ScheduleColumnState,
    onNavigateToScheduleDialog: (Long) -> Unit
) {
    ScheduleScreen(
        modifier = modifier.fillMaxSize(),
        scheduleColumnState = scheduleColumnState,
        onScheduleClick = { schedule ->
            schedule.id?.let { id -> onNavigateToScheduleDialog(id) }
        }
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