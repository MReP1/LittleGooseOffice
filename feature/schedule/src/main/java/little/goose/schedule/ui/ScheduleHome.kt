package little.goose.schedule.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import little.goose.common.utils.progressWith
import little.goose.design.system.theme.AccountTheme
import little.goose.schedule.data.entities.Schedule
import little.goose.ui.icon.PullToSearchIcon
import little.goose.ui.surface.PullSurface

@Composable
fun ScheduleHome(
    modifier: Modifier,
    scheduleColumnState: ScheduleColumnState,
    onNavigateToSearch: () -> Unit,
    onNavigateToScheduleDialog: (Long) -> Unit
) {
    PullSurface(
        modifier = modifier,
        onPull = onNavigateToSearch,
        backgroundContent = { progress ->
            PullToSearchIcon(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .size(48.dp)
                    .offset(y = 32.dp * (progress - 1F))
                    .scale(progress.coerceIn(0.75F, 1F))
                    .alpha(progress.coerceIn(0.75F, 1F)),
                progress = progress.progressWith(0.66F, 0F, 1F),
                contentDescription = "Search",
            )
        },
        content = {
            ScheduleColumn(
                modifier = Modifier.fillMaxSize(),
                state = scheduleColumnState,
                onScheduleClick = { schedule ->
                    schedule.id?.let { id -> onNavigateToScheduleDialog(id) }
                }
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