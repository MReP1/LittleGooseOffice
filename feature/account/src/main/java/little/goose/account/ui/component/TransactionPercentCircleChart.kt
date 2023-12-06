package little.goose.account.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import little.goose.account.data.models.TransactionPercent
import little.goose.chart.ChartLabel
import little.goose.chart.pie.PieChart
import little.goose.chart.pie.PieData
import little.goose.chart.util.roundTo

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TransactionPercentCircleChart(
    modifier: Modifier = Modifier,
    transactionPercents: List<TransactionPercent>,
    colors: List<Pair<Color, Color>>
) {
    val dataList = remember(transactionPercents) {
        transactionPercents.mapIndexed { index, tp ->
            PieData(
                content = tp.content,
                amount = tp.percent.toFloat(),
                color = colors[index].first
            )
        }
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        PieChart(
            modifier = Modifier.size(200.dp),
            dataList = dataList,
            selectedContent = { data ->
                TextButton(onClick = { /*TODO*/ }) {
                    Column(
                        modifier = Modifier.wrapContentWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = data.content, color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            text = (data.amount * 100).toString().roundTo(1) + "%",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        )

        FlowRow(
            modifier = Modifier.width(172.dp),
            verticalArrangement = Arrangement.spacedBy(space = 2.dp, alignment = Alignment.Bottom),
            horizontalArrangement = Arrangement.Center
        ) {
            for (index in dataList.indices) {
                val data = dataList[index]
                ChartLabel(color = data.color, text = data.content)
                if (index < dataList.lastIndex) {
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }
        }
    }
}

