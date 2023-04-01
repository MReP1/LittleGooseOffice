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

data class YearSelectorState(
    val year: Int,
    val onYearChange: (year: Int) -> Unit
)

@Composable
fun YearSelector(
    modifier: Modifier = Modifier,
    state: YearSelectorState,
    shape: Shape = RectangleShape
) {
    Row(modifier = modifier.clip(shape)) {
        Surface(
            modifier = Modifier
                .weight(1F)
                .fillMaxHeight(),
            onClick = { state.onYearChange(state.year - 1) },
            tonalElevation = 6.dp
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "${state.year - 1}" + stringResource(id = R.string.year))
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
                Text(text = "${state.year}" + stringResource(id = R.string.year))
            }
        }
        Surface(
            modifier = Modifier
                .weight(1F)
                .fillMaxHeight(),
            onClick = {
                state.onYearChange(state.year + 1)
            },
            tonalElevation = 6.dp
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "${state.year + 1}" + stringResource(id = R.string.year))
            }
        }
    }
}