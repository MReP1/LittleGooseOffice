package little.goose.design.system.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import little.goose.common.utils.TimeType
import little.goose.common.utils.DateTimeUtils
import little.goose.design.system.R
import java.util.*

@Stable
class TimeSelectorState(
    private val initTime: Date,
    internal val type: TimeType
) {
    private val calendar = Calendar.getInstance().apply { time = initTime }
    var year by mutableStateOf(calendar.get(Calendar.YEAR))
    var month by mutableStateOf((calendar.get(Calendar.MONTH) + 1))
    var day by mutableStateOf(calendar.get(Calendar.DATE))
    var hour by mutableStateOf(calendar.get(Calendar.HOUR_OF_DAY))
    var minute by mutableStateOf(calendar.get(Calendar.MINUTE))
    val yearList = DateTimeUtils.getYearsList()
    val monthList = DateTimeUtils.getMonthsList()
    val dayList by derivedStateOf { DateTimeUtils.getDaysList(year, month).map { it.toString() } }
    val hourList = DateTimeUtils.getHoursList()
    val minuteList = DateTimeUtils.getMinuteList()

    val time by derivedStateOf {
        calendar.apply { set(year, month - 1, day, hour, minute) }.time
    }
}

@Composable
fun TimeSelector(
    modifier: Modifier = Modifier,
    state: TimeSelectorState,
    onConfirm: (Date) -> Unit
) {
    Column(modifier = modifier) {
        Button(
            onClick = { onConfirm(state.time) },
            shape = RectangleShape,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(text = stringResource(id = R.string.confirm))
        }
        Row(
            modifier = modifier.padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (state.type.containYear()) {
                val yearSelectorState = rememberLazyListState(
                    initialFirstVisibleItemIndex = state.yearList.indexOf(state.year.toString())
                )
                ScrollSelector(
                    modifier = Modifier
                        .fillMaxHeight(),
                    items = state.yearList,
                    state = yearSelectorState,
                    onItemSelected = { _, y -> state.year = y.toInt() }
                )
                Text(
                    text = stringResource(id = R.string.year),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 12.dp, end = 10.dp)
                )
            }
            if (state.type.containMonth()) {
                val monthSelectorState = rememberLazyListState(
                    initialFirstVisibleItemIndex = state.monthList.indexOf(state.month.toString())
                )
                ScrollSelector(
                    modifier = Modifier
                        .fillMaxHeight(),
                    items = state.monthList,
                    state = monthSelectorState,
                    onItemSelected = { _, m -> state.month = m.toInt() }
                )
                Text(
                    text = stringResource(id = R.string.month),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp, end = 10.dp)
                )
            }
            if (state.type.containDay()) {
                val daySelectorState = rememberLazyListState(
                    initialFirstVisibleItemIndex = state.dayList.indexOf(state.day.toString())
                )
                ScrollSelector(
                    modifier = Modifier
                        .fillMaxHeight(),
                    items = state.dayList,
                    state = daySelectorState,
                    onItemSelected = { _, d -> state.day = d.toInt() }
                )
                Text(
                    text = stringResource(id = R.string.day),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp, end = 10.dp)
                )
            }
            if (state.type.containTime()) {
                val hourSelectorStable = rememberLazyListState(
                    initialFirstVisibleItemIndex = state.hourList.indexOf(state.hour.toString())
                )
                ScrollSelector(
                    modifier = Modifier
                        .fillMaxHeight(),
                    items = state.hourList,
                    state = hourSelectorStable,
                    onItemSelected = { _, h -> state.hour = h.toInt() }
                )
                Text(
                    text = stringResource(id = R.string.hour),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp, end = 10.dp)
                )
                val minuteSelectorState = rememberLazyListState(
                    initialFirstVisibleItemIndex = state.minuteList.indexOf(state.minute.toString())
                )
                ScrollSelector(
                    modifier = Modifier
                        .fillMaxHeight(),
                    items = state.minuteList,
                    state = minuteSelectorState,
                    onItemSelected = { _, m -> state.minute = m.toInt() }
                )
                Text(
                    text = stringResource(id = R.string.minute),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp, end = 10.dp)
                )
            }
        }
    }
}