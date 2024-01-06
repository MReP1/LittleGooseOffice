package little.goose.home.ui.index

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import little.goose.account.data.entities.Transaction
import little.goose.memorial.data.entities.Memorial
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@Stable
data class IndexHomeState(
    val today: LocalDate,
    val currentDay: LocalDate,
    val initMonth: YearMonth,
    val startMonth: YearMonth,
    val endMonth: YearMonth,
    val dayOfWeek: List<DayOfWeek>,
    val onCurrentDayChange: (LocalDate) -> Unit,
    val getDayContentFlow: (LocalDate) -> StateFlow<IndexDayContent>
)

@Stable
data class IndexDayContent(
    val money: String? = null,
    val memorials: List<Memorial> = listOf(),
    val transactions: List<Transaction> = listOf(),
    val expense: String = "",
    val income: String = ""
)