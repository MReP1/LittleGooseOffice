package little.goose.account.ui.component

import ChartLabel
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import little.goose.account.data.models.TransactionPercent
import pie.PieChart
import pie.PieData

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
            modifier = Modifier
                .size(200.dp)
                .padding(8.dp),
            dataList = dataList
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

