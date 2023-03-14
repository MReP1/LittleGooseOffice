package little.goose.home.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.daysOfWeek
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import little.goose.home.data.CalendarModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@Composable
fun IndexScreen(
    modifier: Modifier = Modifier
) {
    val viewModel = viewModel<IndexViewModel>()
    val initMonth = remember { YearMonth.now() }
    val startMonth = remember { initMonth.minusMonths(120) }
    val endMonth = remember { initMonth.plusMonths(120) }
    val dayOfWeek = remember { daysOfWeek() }
    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = initMonth,
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
            snapshotFlow { state.firstVisibleMonth }.collect {
                viewModel.updateTime(
                    firstVisibleMonth = state.firstVisibleMonth.yearMonth,
                    lastVisibleMonth = state.lastVisibleMonth.yearMonth
                )
            }
        }
    }
    val contentHeight = remember { 58.dp }
    val currentDay by viewModel.currentDay.collectAsState()
    val currentCalendarModel by viewModel.currentCalendarModel.collectAsState()

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
                    val model by viewModel.getCalendarModelState(day.date)
                    MonthDay(
                        modifier = Modifier.height(contentHeight),
                        day = day,
                        isToday = day.date.isEqual(currentDay),
                        model = model,
                        onClick = viewModel::updateCurrentDay
                    )
                }
            )
        }
        Text(text = currentCalendarModel.toString())
    }
}

@Composable
private fun MonthDay(
    modifier: Modifier,
    day: CalendarDay,
    isToday: Boolean,
    model: CalendarModel,
    onClick: (LocalDate) -> Unit
) {
    DayContent(
        modifier = modifier,
        date = day.date,
        isCurrent = day.position == DayPosition.MonthDate,
        isToday = isToday,
        model = model,
        onClick = onClick
    )
}

@Composable
private fun DayContent(
    modifier: Modifier,
    date: LocalDate,
    isCurrent: Boolean,
    isToday: Boolean,
    model: CalendarModel,
    onClick: (LocalDate) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(date) },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val colorScheme = MaterialTheme.colorScheme
        if (model.containSomething) {
            Canvas(modifier = Modifier.size(14.dp)) {
                drawCircle(color = colorScheme.primaryContainer, radius = size.width / 4)
            }
        } else {
            Spacer(modifier = Modifier.height(14.dp))
        }
        Text(
            text = date.dayOfMonth.toString(),
            modifier = Modifier,
            color = if (isToday) {
                MaterialTheme.colorScheme.tertiary
            } else if (isCurrent) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.outlineVariant
            }
        )
        if (model.balance.signum() != 0) {
            Text(
                text = model.balance.toPlainString() ?: "",
                modifier = Modifier.height(14.dp),
                style = MaterialTheme.typography.bodySmall
            )
        } else {
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}