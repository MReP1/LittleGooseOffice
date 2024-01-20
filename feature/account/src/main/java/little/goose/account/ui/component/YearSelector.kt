package little.goose.account.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
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

data class YearSelectorState(
    val year: Int,
    val onYearChange: (year: Int) -> Unit
)

@Composable
fun YearSelector(
    modifier: Modifier = Modifier,
    state: YearSelectorState,
    shape: Shape = RectangleShape,
    onSelectTimeClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
    ) {
        Box(
            modifier = Modifier
                .weight(1F)
                .fillMaxHeight()
                .clip(MaterialTheme.shapes.large)
                .clickable { state.onYearChange(state.year - 1) },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "${state.year - 1}" + stringResource(id = R.string.year))
        }
        Surface(
            modifier = Modifier
                .weight(1F)
                .fillMaxHeight(),
            onClick = onSelectTimeClick,
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "${state.year}" + stringResource(id = R.string.year))
            }
        }
        Box(
            modifier = Modifier
                .weight(1F)
                .fillMaxHeight()
                .clip(MaterialTheme.shapes.large)
                .clickable { state.onYearChange(state.year + 1) },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "${state.year + 1}" + stringResource(id = R.string.year))
        }
    }
}

@Preview
@Composable
fun PreviewYearSelector() = GooseTheme {
    YearSelector(
        state = YearSelectorState(2024, onYearChange = {}),
        modifier = Modifier.height(42.dp),
        shape = MaterialTheme.shapes.large,
        onSelectTimeClick = {}
    )
}