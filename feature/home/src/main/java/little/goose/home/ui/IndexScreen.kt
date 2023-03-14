package little.goose.home.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import little.goose.home.ui.component.IndexScheduleCard
import little.goose.home.ui.component.IndexTransactionCard
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
    val initDay = remember { LocalDate.now() }
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

        // 日期
        TopAppBar(
            modifier = Modifier.fillMaxWidth(),
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
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Today"
                        )
                        Text(
                            text = initDay.dayOfMonth.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 6.dp, start = 4.dp)
                        )
                    }
                }
            }
        )

        // 星期
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

        // 日历
        Box(
            modifier = Modifier.animateContentSize()
        ) {
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
                        isToday = day.date.isEqual(initDay),
                        model = model,
                        onClick = viewModel::updateCurrentDay
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            IndexTransactionCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .fillMaxWidth()
                    .heightIn(0.dp, 160.dp),
                transactions = currentCalendarModel.value.transactions,
                income = currentCalendarModel.value.income,
                expense = currentCalendarModel.value.expense
            )
            IndexScheduleCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .fillMaxWidth()
                    .heightIn(0.dp, 120.dp),
                schedules = currentCalendarModel.value.schedules,
                onCheckChange = viewModel::checkSchedule
            )
        }
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