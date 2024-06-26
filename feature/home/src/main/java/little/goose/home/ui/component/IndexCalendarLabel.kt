package little.goose.home.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import little.goose.common.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun IndexCalendarLabel(
    modifier: Modifier = Modifier,
    date: LocalDate,
    navigateToDate: (LocalDate) -> Unit = {}
) {

    var isShowDatePicker by remember { mutableStateOf(false) }
    if (isShowDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = date
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
                            navigateToDate(localDate)
                        }
                        isShowDatePicker = false
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.confirm),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { isShowDatePicker = false }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Row(
        modifier = modifier.clickable(onClick = { isShowDatePicker = true }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = date.month.getDisplayName(
                TextStyle.SHORT, Locale.CHINA
            ) + date.dayOfMonth + "日",
            style = MaterialTheme.typography.headlineMedium
        )
        Column {
            Text(
                text = date.year.toString(),
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp
            )
            Text(
                text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.CHINA),
                style = MaterialTheme.typography.bodySmall,
                fontSize = 10.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewIndexTimeLabel() {
    IndexCalendarLabel(date = LocalDate.now())
}