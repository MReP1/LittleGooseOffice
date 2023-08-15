package little.goose.account.ui.analysis

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import little.goose.account.data.constants.MoneyType
import little.goose.account.data.models.TimeMoney
import little.goose.account.data.models.TransactionPercent
import little.goose.account.ui.component.TransactionPercentCircleChart
import little.goose.account.ui.component.TransactionPercentColumn
import little.goose.common.collections.CircularLinkList
import little.goose.common.utils.TimeType
import java.util.Date

@Composable
fun TransactionAnalysisCommonContent(
    modifier: Modifier,
    timeType: AnalysisHelper.TimeType,
    moneyType: MoneyType,
    timeMoneys: List<TimeMoney>,
    transactionPercents: List<TransactionPercent>,
    onNavigateToTransactionExample: (
        time: Date, timeType: TimeType, moneyType: MoneyType, iconId: Int?, content: String?
    ) -> Unit,
    onTransactionPercentClick: (TransactionPercent) -> Unit,
) {
    Surface(
        modifier = modifier
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
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

            TransactionAnalysisLineChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                timeType = timeType,
                moneyType = moneyType,
                timeMoneys = timeMoneys,
                onNavigateToTransactionExample = onNavigateToTransactionExample
            )

            Spacer(modifier = Modifier.height(16.dp))

            TransactionPercentCircleChart(
                modifier = Modifier.size(200.dp),
                transactionPercents = transactionPercents,
                colors = trColors
            )

            Spacer(modifier = Modifier.height(16.dp))

            TransactionPercentColumn(
                modifier = Modifier.wrapContentSize(),
                transactionPercents = transactionPercents,
                onTransactionPercentClick = onTransactionPercentClick,
                colors = trColors
            )
        }
    }
}