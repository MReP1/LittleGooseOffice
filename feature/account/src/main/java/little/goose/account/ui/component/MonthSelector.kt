package little.goose.account.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import little.goose.account.R
import little.goose.design.system.theme.GooseTheme
import java.time.YearMonth

@Stable
data class MonthSelectorState(
    val year: Int = YearMonth.now().year,
    val month: Int = YearMonth.now().monthValue,
    val onTimeChange: (year: Int, month: Int) -> Unit = { _, _ -> }
)

@Composable
fun MonthSelector(
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    state: MonthSelectorState,
    onSelectTimeClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.8.dp))
    ) {
        Box(
            modifier = Modifier
                .weight(1F)
                .fillMaxHeight()
                .clip(MaterialTheme.shapes.large)
                .clickable {
                    val year = if (state.month == 1) state.year - 1 else state.year
                    val month = if (state.month == 1) 12 else state.month - 1
                    state.onTimeChange(year, month)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (state.month == 1) {
                    "12"
                } else {
                    "${state.month - 1}"
                } + stringResource(id = R.string.month)
            )
        }
        Surface(
            modifier = Modifier
                .weight(1F)
                .fillMaxHeight(),
            onClick = onSelectTimeClick,
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = MaterialTheme.shapes.large
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "${state.month}" + stringResource(id = R.string.month))
            }
        }
        Box(
            modifier = Modifier
                .weight(1F)
                .fillMaxHeight()
                .clip(MaterialTheme.shapes.large)
                .clickable {
                    val year = if (state.month == 12) state.year + 1 else state.year
                    val month = if (state.month == 12) 1 else state.month + 1
                    state.onTimeChange(year, month)
                },
            contentAlignment = Alignment.Center
        ) {
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

@Preview
@Composable
fun PreviewMonthSelector() = GooseTheme {
    MonthSelector(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RectangleShape,
        state = MonthSelectorState(2024, 12, onTimeChange = { _, _ -> }),
        onSelectTimeClick = {}
    )
}