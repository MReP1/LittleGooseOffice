package little.goose.design.catalog.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import little.goose.chart.ChartLabel
import little.goose.chart.bar.BarChart
import little.goose.chart.bar.BarData
import little.goose.chart.pie.PieChart
import little.goose.chart.pie.PieChartWithContent
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
                add(BarData("$it", it.toFloat(), Color.Red))
            }
        }
    }
    val (selectedData, onSelectedDataChange) = remember {
        mutableStateOf<BarData?>(null)
    }
    BarChart(
        modifier = modifier
            .fillMaxWidth()
            .size(200.dp),
        dataList = dataList,
        selectedData = selectedData,
        onSelectedDataChange = onSelectedDataChange
    )
    selectedData?.let {
        TextButton(onClick = {

        }) {
            Text(text = selectedData.xText)
        }
    }
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
            var selectedData1: PieData? by rememberSaveable(stateSaver = PieData.saver) {
                mutableStateOf(
                    null
                )
            }
            Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
                PieChart(
                    modifier = Modifier
                        .height(200.dp)
                        .aspectRatio(1F),
                    dataList = dataList,
                    selectedData = selectedData1,
                    onSelectedData = { selectedData1 = it }
                )
                selectedData1?.let { data ->
                    Text(
                        text = data.content,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
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
            PieChartWithContent(
                modifier = Modifier
                    .height(200.dp)
                    .aspectRatio(1F),
                dataList = dataList
            )
            FlowRow(
                modifier = Modifier.width(172.dp),
                verticalArrangement = Arrangement.spacedBy(
                    space = 2.dp,
                    alignment = Alignment.Bottom
                ),
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