package little.goose.home.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import little.goose.common.utils.getRealTime
import little.goose.schedule.data.entities.Schedule

@Composable
fun IndexScheduleCard(
    modifier: Modifier,
    schedules: List<Schedule>,
    onCheckChange: (Schedule, Boolean) -> Unit
) {
    Card(modifier = modifier) {
        IndexScheduleColumn(
            schedules = schedules,
            onCheckChange = onCheckChange
        )
    }
}

@Composable
private fun IndexScheduleColumn(
    modifier: Modifier = Modifier,
    schedules: List<Schedule>,
    onCheckChange: (Schedule, Boolean) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 6.dp)
    ) {
        items(schedules) { schedule ->
            IndexScheduleItem(
                schedule = schedule,
                onCheckChange = onCheckChange
            )
        }
    }
}

@Composable
private fun IndexScheduleItem(
    modifier: Modifier = Modifier,
    schedule: Schedule,
    onCheckChange: (Schedule, Boolean) -> Unit
) {
    Row(
        modifier = modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            modifier = Modifier.size(24.dp),
            checked = schedule.isfinish,
            onCheckedChange = { onCheckChange(schedule, it) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = schedule.title)
        Spacer(modifier = Modifier.weight(1F))
        Text(text = schedule.time.getRealTime())
    }
}