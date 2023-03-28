package little.goose.schedule.ui

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import little.goose.common.utils.toChineseMonthDayTime
import little.goose.schedule.data.entities.Schedule

@Composable
fun ScheduleItem(
    modifier: Modifier,
    schedule: Schedule,
    isMultiSelecting: Boolean,
    selected: Boolean,
    onScheduleClick: (Schedule) -> Unit,
    onCheckedChange: (Schedule, Boolean) -> Unit,
    onSelectSchedule: (Schedule, Boolean) -> Unit
) {
    Card(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {
                        if (isMultiSelecting) {
                            onSelectSchedule(schedule, !selected)
                        } else {
                            onScheduleClick(schedule)
                        }
                    },
                    onLongClick = {
                        onSelectSchedule(schedule, !selected)
                    }
                ),
            contentAlignment = Alignment.Center
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
                Checkbox(checked = schedule.isfinish, onCheckedChange = {
                    onCheckedChange(schedule, it)
                })
                Spacer(modifier = Modifier.width(8.dp))
            }
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    tint = MaterialTheme.colorScheme.tertiary,
                    contentDescription = "check",
                    modifier = Modifier.matchParentSize()
                )
            }
        }
    }
}