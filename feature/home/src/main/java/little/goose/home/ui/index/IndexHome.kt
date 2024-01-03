package little.goose.home.ui.index

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.yearMonth
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNot
import little.goose.account.data.entities.Transaction
import little.goose.home.data.CalendarModel
import little.goose.home.ui.component.IndexMemorialCard
import little.goose.home.ui.component.IndexTransactionCard
import little.goose.home.ui.component.IndexTransactionCardState
import little.goose.home.ui.component.MonthDay
import little.goose.memorial.data.entities.Memorial
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale

@Stable
data class IndexScreenState(
    val today: LocalDate,
    val currentDay: LocalDate,
    val currentCalendarModel: State<CalendarModel>,
    val updateMonth: (YearMonth, YearMonth) -> Unit,
    val updateCurrentDay: (LocalDate) -> Unit,
    val getCalendarModelState: (LocalDate) -> State<CalendarModel>
)

@Composable
fun IndexHome(
    modifier: Modifier = Modifier,
    state: IndexScreenState,
    onTransactionAdd: (Date) -> Unit,
    onTransactionClick: (Transaction) -> Unit,
    onMemorialClick: (Memorial) -> Unit
) {
    val initMonth = remember { YearMonth.now() }
    val startMonth = remember { initMonth.minusMonths(120) }
    val endMonth = remember { initMonth.plusMonths(120) }
    val daysOfWeek = remember { daysOfWeek() }

    val currentState by rememberUpdatedState(newValue = state)

    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = initMonth,
        firstDayOfWeek = daysOfWeek.first(),
        outDateStyle = OutDateStyle.EndOfRow
    )

    LaunchedEffect(calendarState) {
        snapshotFlow { calendarState.isScrollInProgress }.filterNot { it }.collectLatest {
            val firstYearMonth = calendarState.firstVisibleMonth.yearMonth
            val lastYearMonth = calendarState.lastVisibleMonth.yearMonth
            currentState.updateMonth(firstYearMonth, lastYearMonth)
            val visibleRange =
                firstYearMonth.atStartOfMonth()..firstYearMonth.atEndOfMonth()
            if (currentState.currentDay !in visibleRange) {
                currentState.updateCurrentDay(firstYearMonth.atStartOfMonth())
            }
        }
    }

    LaunchedEffect(currentState.currentDay) {
        val currentMonth = calendarState.firstVisibleMonth.yearMonth
        val visibleRange = currentMonth.atStartOfMonth()..currentMonth.atEndOfMonth()
        if (currentState.currentDay !in visibleRange) {
            calendarState.animateScrollToMonth(currentState.currentDay.yearMonth)
        }
    }

    Column(modifier = modifier) {

        // 星期
        Row(modifier = Modifier.fillMaxWidth()) {
            for (day in daysOfWeek) {
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
                val model by currentState.getCalendarModelState(day.date)
                MonthDay(
                    modifier = Modifier.height(58.dp),
                    day = day,
                    isCurrentDay = day.date == currentState.currentDay,
                    isToday = day.date.isEqual(currentState.today),
                    model = model,
                    onClick = currentState.updateCurrentDay
                )
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            if (currentState.currentCalendarModel.value.memorials.isNotEmpty()) {
                IndexMemorialCard(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    memorial = currentState.currentCalendarModel.value.memorials.first(),
                    onMemorialClick = onMemorialClick
                )
            }
            val indexTransactionCardState by remember(currentState.currentDay) {
                derivedStateOf {
                    IndexTransactionCardState(
                        currentState.currentCalendarModel.value.expense,
                        currentState.currentCalendarModel.value.income,
                        currentState.currentCalendarModel.value.transactions,
                        currentState.currentDay
                    )
                }
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