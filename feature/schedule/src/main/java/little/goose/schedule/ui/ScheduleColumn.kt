package little.goose.schedule.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import little.goose.common.utils.toChineseMonthDayTime
import little.goose.schedule.data.entities.Schedule

@Composable
fun ScheduleColumn(
    modifier: Modifier = Modifier,
    schedules: List<Schedule>,
    onScheduleClick: (Schedule) -> Unit,
    onCheckedChange: (Schedule, Boolean) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
    ) {
        items(
            items = schedules,
            key = { it.id ?: -1 }
        ) { schedule ->
            ScheduleItem(
                modifier = Modifier.fillMaxWidth(),
                schedule = schedule,
                onCheckedChange = {
                    onCheckedChange(schedule, it)
                },
                onScheduleClick = onScheduleClick
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ScheduleItem(
    modifier: Modifier,
    schedule: Schedule,
    onScheduleClick: (Schedule) -> Unit,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        onClick = {
            onScheduleClick(schedule)
        },
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1F)) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = schedule.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (schedule.content.isNotBlank()) {
                    Text(
                        text = schedule.content,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Text(
                    text = schedule.time.toChineseMonthDayTime(),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Checkbox(checked = schedule.isfinish, onCheckedChange = onCheckedChange)
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}