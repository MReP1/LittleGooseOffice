package little.goose.account.ui.analysis

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import little.goose.account.R
import little.goose.account.ui.component.MonthSelector
import little.goose.account.ui.component.MonthSelectorState
import little.goose.account.ui.component.YearSelector
import little.goose.account.ui.component.YearSelectorState

data class TransactionAnalysisBottomBarState(
    val timeType: AnalysisHelper.TimeType,
    val year: Int,
    val month: Int,
    val monthSelectorState: MonthSelectorState,
    val yearSelectorState: YearSelectorState,
    val onTypeChange: (timeType: AnalysisHelper.TimeType) -> Unit
)

@Composable
fun TransactionAnalysisBottomBar(
    modifier: Modifier = Modifier,
    state: TransactionAnalysisBottomBarState,
    onSelectTimeClick: () -> Unit
) {
    val windowInsets =
        WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(windowInsets),
    ) {
        val selectorModifier = remember {
            Modifier
                .padding(horizontal = 24.dp)
                .height(42.dp)
                .fillMaxWidth()
        }

        if (state.timeType == AnalysisHelper.TimeType.YEAR) {
            YearSelector(
                modifier = selectorModifier,
                state = state.yearSelectorState,
                shape = RoundedCornerShape(18.dp),
                onSelectTimeClick = onSelectTimeClick
            )
        } else {
            MonthSelector(
                modifier = selectorModifier,
                state = state.monthSelectorState,
                shape = RoundedCornerShape(18.dp),
                onSelectTimeClick = onSelectTimeClick
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavigationBarItem(
                selected = state.timeType == AnalysisHelper.TimeType.YEAR,
                onClick = { state.onTypeChange(AnalysisHelper.TimeType.YEAR) },
                icon = {
                    Text(
                        text = "${state.year}" + stringResource(id = R.string.year),
                        Modifier.padding(6.dp)
                    )
                }
            )
            NavigationBarItem(
                selected = state.timeType == AnalysisHelper.TimeType.MONTH,
                onClick = { state.onTypeChange(AnalysisHelper.TimeType.MONTH) },
                icon = {
                    Text(
                        text = if (state.timeType == AnalysisHelper.TimeType.MONTH) {
                            "${state.month}" + stringResource(id = R.string.month)
                        } else stringResource(id = R.string.month),
                        Modifier.padding(6.dp)
                    )
                }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Preview(widthDp = 380, heightDp = 200)
@Composable
private fun PreviewTransactionAnalysisBottomBar() {
    TransactionAnalysisBottomBar(
        modifier = Modifier.fillMaxSize(),
        state = TransactionAnalysisBottomBarState(
            AnalysisHelper.TimeType.YEAR, 0, 0,
            MonthSelectorState(0, 0) { _, _ -> },
            YearSelectorState(0) {}
        ) {},
        onSelectTimeClick = {}
    )
}