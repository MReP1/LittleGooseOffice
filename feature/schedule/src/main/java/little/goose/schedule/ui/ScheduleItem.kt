package little.goose.schedule.ui

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import little.goose.common.utils.toChineseMonthDayTime
import little.goose.design.system.theme.AccountTheme
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
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.8.dp
    ) {
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
                    modifier = Modifier
                        .matchParentSize()
                        .alpha(0.5F)
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewScheduleItem() {
    AccountTheme {
        ScheduleItem(
            modifier = Modifier.fillMaxWidth(),
            schedule = Schedule(
                title = "title",
                content = "content"
            ),
            isMultiSelecting = true,
            selected = true,
            onScheduleClick = {},
            onCheckedChange = { _, _ -> },
            onSelectSchedule = { _, _ -> }
        )
    }
}