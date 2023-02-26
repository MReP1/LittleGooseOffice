package little.goose.account.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class MonthSelectorState(
    val year: Int,
    val month: Int,
    val onTimeChange: (year: Int, month: Int) -> Unit
)

@Composable
fun MonthSelector(
    modifier: Modifier = Modifier,
    state: MonthSelectorState
) {
    Row(modifier = modifier) {
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
            onClick = {

            },
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
}