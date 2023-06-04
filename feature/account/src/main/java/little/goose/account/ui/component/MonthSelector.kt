package little.goose.account.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import little.goose.account.R

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
    onSelectTimeClick: () -> Unit
) {
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
                Text(
                    text = if (state.month == 1) {
                        "12"
                    } else {
                        "${state.month - 1}"
                    } + stringResource(id = R.string.month)
                )
            }
        }
        Surface(
            modifier = Modifier
                .weight(1F)
                .fillMaxHeight(),
            onClick = onSelectTimeClick,
            tonalElevation = 2.dp
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "${state.month}" + stringResource(id = R.string.month))
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
                Text(
                    text = if (state.month == 12) {
                        "1"
                    } else {
                        "${state.month + 1}"
                    } + stringResource(id = R.string.month)
                )
            }
        }
    }
}