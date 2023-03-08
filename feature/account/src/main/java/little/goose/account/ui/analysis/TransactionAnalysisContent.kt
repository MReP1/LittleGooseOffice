package little.goose.account.ui.analysis

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import little.goose.account.data.models.TransactionBalance
import little.goose.account.data.models.TransactionPercent
import little.goose.account.ui.component.TransactionPercentCircleChart
import little.goose.account.ui.component.TransactionPercentColumn
import little.goose.common.collections.CircularLinkList

data class TransactionAnalysisContentState(
    val expensePercents: List<TransactionPercent> = listOf(),
    val incomePercents: List<TransactionPercent> = listOf(),
    val balancePercents: List<TransactionBalance> = listOf()
)

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TransactionAnalysisContent(
    modifier: Modifier = Modifier,
    state: TransactionAnalysisContentState,
    pagerState: PagerState
) {
    HorizontalPager(
        count = 3,
        modifier = modifier
            .fillMaxSize(),
        state = pagerState
    ) { index ->
        when (index) {
            0 -> {
                TransactionAnalysisCommonContent(
                    modifier = Modifier.fillMaxSize(),
                    transactionPercents = state.expensePercents
                )
            }
            1 -> {
                TransactionAnalysisCommonContent(
                    modifier = Modifier.fillMaxSize(),
                    transactionPercents = state.incomePercents
                )
            }
            2 -> {

            }
        }
    }
}

@Composable
fun TransactionAnalysisCommonContent(
    modifier: Modifier,
    transactionPercents: List<TransactionPercent>
) {
    Surface(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val colorScheme = MaterialTheme.colorScheme
            val colors = remember {
                CircularLinkList<Color>().apply {
                    add(colorScheme.primaryContainer)
                    add(colorScheme.errorContainer)
                    add(colorScheme.secondaryContainer)
                    add(colorScheme.tertiaryContainer)
                }
            }
            val trColors = remember(transactionPercents, colors) {
                List(transactionPercents.size) { index ->
                    var backgroundColor = colors.next()
                    if (index == transactionPercents.lastIndex
                        && backgroundColor == colorScheme.errorContainer
                    ) {
                        colors.next()
                        backgroundColor = colors.next()
                    }
                    backgroundColor to colorScheme.contentColorFor(backgroundColor)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            TransactionPercentCircleChart(
                modifier = Modifier.size(200.dp),
                transactionPercents = transactionPercents,
                colors = trColors
            )
            Spacer(modifier = Modifier.height(16.dp))
            TransactionPercentColumn(
                modifier = Modifier.weight(1F),
                transactionPercents = transactionPercents,
                colors = trColors
            )
        }
    }
}