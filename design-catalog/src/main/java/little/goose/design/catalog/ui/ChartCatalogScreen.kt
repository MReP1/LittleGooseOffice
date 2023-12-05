package little.goose.design.catalog.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import little.goose.chart.ChartLabel
import little.goose.chart.bar.BarChart
import little.goose.chart.bar.BarData
import little.goose.chart.pie.PieChart
import little.goose.chart.pie.PieData

@Composable
internal fun ChartCatalogScreen(
    modifier: Modifier
) {
    Surface(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            BarChartCatalog(modifier = Modifier.fillMaxWidth())
            PieChartCatalog(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BarChartCatalog(
    modifier: Modifier = Modifier
) {
    val dataList = remember {
        buildList {
            repeat(4) {
                add(BarData("$it", it.toFloat(), "$it", Color.Red))
            }
        }
    }
    BarChart(
        modifier = Modifier
            .fillMaxWidth()
            .size(200.dp),
        dataList = dataList
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Preview(showBackground = true)
@Composable
private fun PieChartCatalog(
    modifier: Modifier = Modifier
) {
    val dataList = remember {
        listOf(
            PieData("123", 15F, Color.Red),
            PieData("123", 16F, Color.Black),
            PieData("Sample", 50F, Color.Gray),
            PieData("Sample2", 32F, Color.Green),
            PieData("Sample3", 24F, Color.Magenta)
        )
    }
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row {
            Spacer(modifier = Modifier.weight(1F))
            PieChart(
                modifier = Modifier
                    .height(200.dp)
                    .aspectRatio(1F)
                    .padding(8.dp),
                dataList = dataList
            )
            Column(modifier = Modifier.weight(1F)) {
                Spacer(modifier = Modifier.height(16.dp))
                dataList.forEach { data ->
                    ChartLabel(
                        modifier = Modifier,
                        color = data.color,
                        text = data.content
                    )
                }
            }
        }

        Column(
            modifier = Modifier.width(200.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PieChart(
                modifier = Modifier
                    .height(200.dp)
                    .aspectRatio(1F)
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
}