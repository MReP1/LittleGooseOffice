package little.goose.home.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@Composable
fun IndexTopBar(
    modifier: Modifier = Modifier,
    currentDay: LocalDate,
    today: LocalDate
) {
    // 日期
    TopAppBar(
        modifier = modifier.fillMaxWidth(),
        title = {
            Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = currentDay.month.getDisplayName(
                        TextStyle.SHORT, Locale.CHINA
                    ) + currentDay.dayOfMonth + "日",
                    style = MaterialTheme.typography.headlineMedium
                )
                Column {
                    Text(
                        text = currentDay.year.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp
                    )
                    Text(
                        text = currentDay.dayOfWeek.getDisplayName(
                            TextStyle.SHORT, Locale.CHINA
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = {
                }
            ) {
                Box(modifier = Modifier.wrapContentSize()) {
                    Icon(
                        imageVector = Icons.Rounded.CalendarToday,
                        contentDescription = "Today"
                    )
                    Text(
                        text = today.dayOfMonth.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 6.dp, start = 4.dp)
                    )
                }
            }
        }
    )
}