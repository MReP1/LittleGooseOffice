package little.goose.account.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import little.goose.common.utils.TimeType
import little.goose.common.utils.*
import little.goose.design.system.component.dialog.TimeSelectorCenterDialog
import little.goose.design.system.component.dialog.rememberDialogState

data class MonthSelectorState(
    val year: Int,
    val month: Int,
    val onTimeChange: (year: Int, month: Int) -> Unit
)

@Composable
fun MonthSelector(
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    state: MonthSelectorState,
) {
    val selectorTimeDialogState = rememberDialogState()
    Row(modifier = modifier.clip(shape)) {
        Surface(
            modifier = Modifier
                .weight(1F)
                .fillMaxHeight(),
            onClick = {
                val year = if (state.month == 1) state.year - 1 else state.year
                val month = if (state.month == 1) 12 else state.month - 1
                state.onTimeChange(year, month)
            },
            tonalElevation = 6.dp
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = if (state.month == 1) "12月" else "${state.month - 1}月")
            }
        }
        Surface(
            modifier = Modifier
                .weight(1F)
                .fillMaxHeight(),
            onClick = selectorTimeDialogState::show,
            tonalElevation = 2.dp
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "${state.month}月")
            }
        }
        Surface(
            modifier = Modifier
                .weight(1F)
                .fillMaxHeight(),
            onClick = {
                val year = if (state.month == 12) state.year + 1 else state.year
                val month = if (state.month == 12) 1 else state.month + 1
                state.onTimeChange(year, month)
            },
            tonalElevation = 6.dp
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = if (state.month == 12) "1月" else "${state.month + 1}月")
            }
        }
    }

    TimeSelectorCenterDialog(
        state = selectorTimeDialogState,
        initTime = remember(state.year, state.month) {
            calendar.apply { clear(); setYear(state.year); setMonth(state.month) }.time
        },
        type = TimeType.YEAR_MONTH,
        onConfirm = {
            val cal = calendar.apply { time = it }
            state.onTimeChange(cal.getYear(), cal.getMonth())
        }
    )
}