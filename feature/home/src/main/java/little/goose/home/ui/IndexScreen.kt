package little.goose.home.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@Composable
fun IndexScreen(
    modifier: Modifier = Modifier
) {
    val viewModel = viewModel<IndexViewModel>()
    val currentMonth = remember { YearMonth.now() }
    val currentDay = remember { LocalDate.now() }
    val startMonth = remember { currentMonth.minusMonths(120) }
    val endMonth = remember { currentMonth.plusMonths(120) }
    val dayOfWeek = remember { daysOfWeek() }
    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = dayOfWeek.first(),
        outDateStyle = OutDateStyle.EndOfRow
    )
    var visibleMonth by remember(state) { mutableStateOf(state.firstVisibleMonth) }
    LaunchedEffect(state) {
        launch(Dispatchers.Default) {
            snapshotFlow { state.isScrollInProgress }
                .filter { scrolling -> !scrolling }
                .collect { visibleMonth = state.firstVisibleMonth }
        }
        launch(Dispatchers.Default) {
            snapshotFlow { state.firstVisibleMonth }
                .collect {

                }
        }
    }
    val contentHeight = remember { 58.dp }
    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth()) {
            for (day in dayOfWeek) {
                Box(
                    modifier = Modifier
                        .weight(1F)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = day.getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                }
            }
        }
        Box(modifier = Modifier.animateContentSize()) {
            HorizontalCalendar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(contentHeight * visibleMonth.weekDays.size),
                state = state,
                userScrollEnabled = true,
                dayContent = { day ->
                    MonthDay(
                        modifier = Modifier.height(contentHeight),
                        day = day,
                        isToday = day.date.isEqual(currentDay)
                    )
                }
            )
        }
    }
}

@Composable
private fun MonthDay(
    modifier: Modifier,
    day: CalendarDay,
    isToday: Boolean
) {
    DayContent(
        modifier = modifier,
        date = day.date,
        isCurrent = day.position == DayPosition.MonthDate,
        isToday = isToday
    )
}

@Composable
private fun DayContent(
    modifier: Modifier,
    date: LocalDate,
    isCurrent: Boolean,
    isToday: Boolean
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            modifier = Modifier.align(Alignment.Center),
            color = if (isToday) {
                MaterialTheme.colorScheme.tertiary
            } else if (isCurrent) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.outlineVariant
            }
        )
    }
}