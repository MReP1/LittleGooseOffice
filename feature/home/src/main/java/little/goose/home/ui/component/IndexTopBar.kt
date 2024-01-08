package little.goose.home.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate

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

    TopAppBar(
        modifier = modifier.fillMaxWidth(),
        title = {
            // 日期
            IndexCalendarLabel(
                date = state.currentDay,
                navigateToDate = state.navigateToDate
            )
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