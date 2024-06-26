package little.goose.home.ui.index

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.yearMonth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNot
import little.goose.account.data.entities.Transaction
import little.goose.design.system.theme.GooseTheme
import little.goose.design.system.theme.LocalWindowSizeClass
import little.goose.design.system.util.PreviewMultipleScreenSizes
import little.goose.home.ui.component.DayContent
import little.goose.home.ui.component.IndexCalendarLabel
import little.goose.home.ui.component.IndexMemorialCard
import little.goose.home.ui.component.IndexTransactionCard
import little.goose.home.ui.component.IndexTransactionCardState
import little.goose.memorial.data.entities.Memorial
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale

@Composable
fun IndexHome(
    modifier: Modifier = Modifier,
    state: IndexHomeState,
    onTransactionAdd: (Date) -> Unit,
    onTransactionClick: (Transaction) -> Unit,
    onMemorialClick: (Memorial) -> Unit
) {

    val windowSizeClass = LocalWindowSizeClass.current
    val isWidthCompat = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    val isHeightCompat = windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact

    Row(modifier = modifier) {

        if (!isWidthCompat) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                if (!isHeightCompat) {
                    IndexCalendarLabel(
                        modifier = Modifier.padding(start = 8.dp),
                        date = state.currentDay,
                        navigateToDate = state.onCurrentDayChange
                    )
                }
                IndexCalendar(
                    modifier = Modifier.width(360.dp),
                    state = state
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1F)
                .fillMaxHeight()
        ) {

            if (isWidthCompat) {
                IndexCalendar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    state = state
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            val currentDayContentState = state.getDayContentFlow(state.currentDay).collectAsState()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top
            ) {
                currentDayContentState.value.memorials.firstOrNull()?.let { memorial ->
                    IndexMemorialCard(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        memorial = memorial,
                        onMemorialClick = onMemorialClick
                    )
                }

                val indexTransactionCardState = remember(
                    currentDayContentState.value,
                    state.currentDay
                ) {
                    IndexTransactionCardState(
                        currentDayContentState.value.expense,
                        currentDayContentState.value.income,
                        currentDayContentState.value.transactions,
                        state.currentDay
                    )
                }
                IndexTransactionCard(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .fillMaxWidth()
                        .heightIn(0.dp, 160.dp),
                    state = indexTransactionCardState,
                    onTransactionAdd = onTransactionAdd,
                    onTransactionClick = onTransactionClick
                )
            }
        }
    }
}

@Composable
fun IndexCalendar(
    modifier: Modifier,
    state: IndexHomeState,
) {
    Column(modifier = modifier) {
        val currentCurrentDay by rememberUpdatedState(newValue = state.currentDay)
        val textMeasurer = rememberTextMeasurer(cacheSize = 256)
        val calendarState = rememberCalendarState(
            startMonth = state.startMonth,
            endMonth = state.endMonth,
            firstVisibleMonth = state.initMonth,
            firstDayOfWeek = state.dayOfWeek.first(),
            outDateStyle = OutDateStyle.EndOfRow
        )

        LaunchedEffect(calendarState) {
            // select day of current month when scroll finished.
            snapshotFlow { calendarState.isScrollInProgress }.filterNot { it }.collect {
                val currentMonth = calendarState.firstVisibleMonth.yearMonth
                val visibleRange = currentMonth.atStartOfMonth()..currentMonth.atEndOfMonth()
                if (currentCurrentDay !in visibleRange) {
                    state.onCurrentDayChange(currentMonth.atStartOfMonth())
                }
            }
        }

        LaunchedEffect(state.currentDay) {
            // when select a day not in current month, scroll to that month.
            val currentMonth = calendarState.firstVisibleMonth.yearMonth
            val visibleRange = currentMonth.atStartOfMonth()..currentMonth.atEndOfMonth()
            if (state.currentDay !in visibleRange) {
                calendarState.animateScrollToMonth(state.currentDay.yearMonth)
            }
        }


        // 星期
        Row(modifier = Modifier.fillMaxWidth()) {
            for (day in state.dayOfWeek) {
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
        HorizontalCalendar(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            state = calendarState,
            userScrollEnabled = true,
            dayContent = { day ->
                val dayContent by state.getDayContentFlow(day.date).collectAsState()
                DayContent(
                    onClick = { state.onCurrentDayChange(day.date) },
                    modifier = Modifier.height(54.dp),
                    dateText = day.date.dayOfMonth.toString(),
                    money = dayContent.money,
                    textMeasurer = textMeasurer,
                    isToday = day.date == state.today,
                    isCurrentDay = day.date == state.currentDay,
                    isCurrentMonth = day.position == DayPosition.MonthDate,
                    drawPoint = dayContent.memorials.isNotEmpty()
                )
            }
        )
    }
}

@PreviewMultipleScreenSizes
@Composable
fun PreviewIndexHome() = GooseTheme {
    Surface {
        IndexHome(
            state = IndexHomeState(
                today = LocalDate.now(),
                currentDay = LocalDate.now(),
                initMonth = YearMonth.now(),
                startMonth = YearMonth.now().minusMonths(12),
                endMonth = YearMonth.now().plusMonths(12),
                dayOfWeek = daysOfWeek(),
                onCurrentDayChange = {},
                getDayContentFlow = {
                    MutableStateFlow(
                        IndexDayContent(
                            memorials = listOf(Memorial()),
                            transactions = listOf(
                                Transaction(), Transaction()
                            )
                        )
                    )
                }
            ),
            onTransactionAdd = {},
            onTransactionClick = {},
            onMemorialClick = {}
        )
    }
}