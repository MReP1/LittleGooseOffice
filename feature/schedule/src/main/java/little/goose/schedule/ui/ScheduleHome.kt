package little.goose.schedule.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import little.goose.design.system.theme.AccountTheme
import little.goose.schedule.data.entities.Schedule
import little.goose.ui.surface.PullSurface

@Composable
fun ScheduleHome(
    modifier: Modifier,
    scheduleColumnState: ScheduleColumnState,
    onNavigateToSearch: () -> Unit,
    onNavigateToScheduleDialog: (Long) -> Unit
) {
    ScheduleScreen(
        modifier = modifier.fillMaxSize(),
        scheduleColumnState = scheduleColumnState,
        onNavigateToSearch = onNavigateToSearch,
        onScheduleClick = { schedule ->
            schedule.id?.let { id -> onNavigateToScheduleDialog(id) }
        }
    )
}

@Composable
private fun ScheduleScreen(
    modifier: Modifier = Modifier,
    scheduleColumnState: ScheduleColumnState,
    onNavigateToSearch: () -> Unit,
    onScheduleClick: (Schedule) -> Unit
) {
    PullSurface(
        modifier = modifier,
        onPull = onNavigateToSearch,
        backgroundContent = { progress ->
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "Search",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp)
                    .size(min(48.dp, 24.dp + 24.dp * progress))
                    .alpha(progress.coerceIn(0.62F, 1F))
            )
        },
        content = {
            ScheduleColumn(
                modifier = Modifier.fillMaxSize(),
                state = scheduleColumnState,
                onScheduleClick = onScheduleClick
            )
        }
    )
}

@Preview
@Composable
private fun PreviewScheduleHome() = AccountTheme {
    ScheduleHome(
        modifier = Modifier.fillMaxSize(),
        scheduleColumnState = ScheduleColumnState(
            schedules = (1L..15L).map { id ->
                Schedule(id = id)
            }
        ),
        onNavigateToSearch = {},
        onNavigateToScheduleDialog = {}
    )
}