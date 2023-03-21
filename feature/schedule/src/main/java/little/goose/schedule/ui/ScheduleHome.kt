package little.goose.schedule.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import little.goose.common.utils.*
import little.goose.schedule.data.entities.Schedule

@Composable
fun ScheduleHome(
    modifier: Modifier,
    onScheduleClick: (Schedule) -> Unit
) {
    val viewModel: ScheduleViewModel = hiltViewModel()
    val schedules by viewModel.schedules.collectAsState()
    ScheduleScreen(
        modifier = modifier.fillMaxSize(),
        schedules = schedules,
        onScheduleClick = onScheduleClick,
        onCheckedChange = { schedule, isChecked ->
            viewModel.updateSchedule(schedule.copy(isfinish = isChecked))
        }
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