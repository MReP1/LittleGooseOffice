package little.goose.home.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.TextStyle
import java.util.Locale

@Stable
data class IndexTopBarState(
    val currentDay: LocalDate = LocalDate.now(),
    val today: LocalDate = LocalDate.now(),
    val navigateToDate: (LocalDate) -> Unit
)

@Composable
fun IndexTopBar(
    modifier: Modifier = Modifier,
    state: IndexTopBarState
) {
    var isShowDatePicker by remember { mutableStateOf(false) }
    if (isShowDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.currentDay
                .atTime(1, 2, 3, 4)
                .toInstant(ZoneOffset.MIN)
                .toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { isShowDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { selectedMillis ->
                            val localDate = Instant.ofEpochMilli(selectedMillis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            state.navigateToDate(localDate)
                        }
                        isShowDatePicker = false
                    }
                ) {
                    Text(
                        text = stringResource(id = little.goose.common.R.string.confirm),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { isShowDatePicker = false }) {
                    Text(text = stringResource(id = little.goose.common.R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // 日期
    TopAppBar(
        modifier = modifier.fillMaxWidth(),
        title = {
            Row(
                modifier = Modifier.clickable { isShowDatePicker = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.currentDay.month.getDisplayName(
                        TextStyle.SHORT, Locale.CHINA
                    ) + state.currentDay.dayOfMonth + "日",
                    style = MaterialTheme.typography.headlineMedium
                )
                Column {
                    Text(
                        text = state.currentDay.year.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp
                    )
                    Text(
                        text = state.currentDay.dayOfWeek.getDisplayName(
                            TextStyle.SHORT, Locale.CHINA
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = { state.navigateToDate(state.today) }) {
                Box(modifier = Modifier.wrapContentSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.CalendarToday,
                        contentDescription = "Today"
                    )
                    Text(
                        text = state.today.dayOfMonth.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    )
}