package little.goose.design.system.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import little.goose.common.utils.DateTimeUtils
import little.goose.common.utils.TimeType
import java.util.Calendar
import java.util.Date

@Stable
class TimeSelectorState(
    private val initTime: Date
) {
    private val calendar = Calendar.getInstance().apply { time = initTime }
    var year by mutableIntStateOf(calendar.get(Calendar.YEAR))
    var month by mutableIntStateOf((calendar.get(Calendar.MONTH) + 1))
    var day by mutableIntStateOf(calendar.get(Calendar.DATE))
    var hour by mutableIntStateOf(calendar.get(Calendar.HOUR_OF_DAY))
    var minute by mutableIntStateOf(calendar.get(Calendar.MINUTE))
    val yearList = DateTimeUtils.getYearsList()
    val monthList = DateTimeUtils.getMonthsList()
    val dayList by derivedStateOf { DateTimeUtils.getDaysList(year, month).map { it.toString() } }
    val hourList = DateTimeUtils.getHoursList()
    val minuteList = DateTimeUtils.getMinuteList()

    val time: Date by derivedStateOf {
        calendar.apply { set(year, month - 1, day, hour, minute) }.time
    }
}

@Composable
fun TimeSelector(
    modifier: Modifier = Modifier,
    state: TimeSelectorState,
    isShowConfirm: Boolean = true,
    timeType: TimeType,
    onConfirm: (Date) -> Unit = {}
) {
    Column(modifier = modifier) {
        Column(
            modifier = modifier
                .weight(1F)
                .padding(horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(176.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (timeType.containYear()) {
                    val yearSelectorState = rememberLazyListState(
                        initialFirstVisibleItemIndex = state.yearList.indexOf(state.year.toString())
                    )
                    LaunchedEffect(yearSelectorState, state) {
                        snapshotFlow { state.year }.map {
                            state.yearList.indexOf(it.toString())
                        }.filter {
                            yearSelectorState.firstVisibleItemIndex != it
                        }.onEach {
                            yearSelectorState.animateScrollToItem(it)
                        }.launchIn(this)
                    }
                    ScrollSelector(
                        modifier = Modifier
                            .fillMaxHeight(),
                        items = state.yearList,
                        state = yearSelectorState,
                        onItemSelected = { _, y -> state.year = y.toInt() }
                    )
                    Text(
                        text = stringResource(id = little.goose.common.R.string.year),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 10.dp, end = 8.dp)
                    )
                }
                if (timeType.containMonth()) {
                    val monthSelectorState = rememberLazyListState(
                        initialFirstVisibleItemIndex = state.monthList.indexOf(state.month.toString())
                    )
                    LaunchedEffect(monthSelectorState, state) {
                        snapshotFlow { state.month }.map {
                            state.monthList.indexOf(it.toString())
                        }.filter {
                            monthSelectorState.firstVisibleItemIndex != it
                        }.onEach {
                            monthSelectorState.animateScrollToItem(it)
                        }.launchIn(this)
                    }
                    ScrollSelector(
                        modifier = Modifier
                            .fillMaxHeight(),
                        items = state.monthList,
                        state = monthSelectorState,
                        onItemSelected = { _, m -> state.month = m.toInt() }
                    )
                    Text(
                        text = stringResource(id = little.goose.common.R.string.month),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 6.dp, end = 8.dp)
                    )
                }
                if (timeType.containDay()) {
                    val daySelectorState = rememberLazyListState(
                        initialFirstVisibleItemIndex = state.dayList.indexOf(state.day.toString())
                    )
                    LaunchedEffect(daySelectorState, state) {
                        snapshotFlow { state.day }.map {
                            state.dayList.indexOf(it.toString())
                        }.filter {
                            daySelectorState.firstVisibleItemIndex != it
                        }.onEach {
                            daySelectorState.animateScrollToItem(it)
                        }.launchIn(this)
                    }
                    ScrollSelector(
                        modifier = Modifier
                            .fillMaxHeight(),
                        items = state.dayList,
                        state = daySelectorState,
                        onItemSelected = { _, d -> state.day = d.toInt() }
                    )
                    Text(
                        text = stringResource(id = little.goose.common.R.string.day),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 6.dp, end = 8.dp)
                    )
                }
            }
            if (timeType.containTime()) {
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(176.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    val hourSelectorStable = rememberLazyListState(
                        initialFirstVisibleItemIndex = state.hourList.indexOf(state.hour.toString())
                    )
                    LaunchedEffect(hourSelectorStable, state) {
                        snapshotFlow { state.hour }.map {
                            state.hourList.indexOf(it.toString())
                        }.filter {
                            hourSelectorStable.firstVisibleItemIndex != it
                        }.onEach {
                            hourSelectorStable.animateScrollToItem(it)
                        }.launchIn(this)
                    }
                    ScrollSelector(
                        modifier = Modifier
                            .fillMaxHeight(),
                        items = state.hourList,
                        state = hourSelectorStable,
                        onItemSelected = { _, h -> state.hour = h.toInt() }
                    )
                    Text(
                        text = stringResource(id = little.goose.common.R.string.hour),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 6.dp, end = 8.dp)
                    )
                    val minuteSelectorState = rememberLazyListState(
                        initialFirstVisibleItemIndex = state.minuteList.indexOf(state.minute.toString())
                    )
                    LaunchedEffect(minuteSelectorState, state) {
                        snapshotFlow { state.minute }.map {
                            state.minuteList.indexOf(it.toString())
                        }.filter {
                            minuteSelectorState.firstVisibleItemIndex != it
                        }.onEach {
                            minuteSelectorState.animateScrollToItem(it)
                        }.launchIn(this)
                    }
                    ScrollSelector(
                        modifier = Modifier
                            .fillMaxHeight(),
                        items = state.minuteList,
                        state = minuteSelectorState,
                        onItemSelected = { _, m -> state.minute = m.toInt() }
                    )
                    Text(
                        text = stringResource(id = little.goose.common.R.string.minute),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 6.dp, end = 8.dp)
                    )
                }
            }
        }
        if (isShowConfirm) {
            Button(
                onClick = { onConfirm(state.time) },
                shape = RectangleShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(text = stringResource(id = little.goose.common.R.string.confirm))
            }
        }
    }
}