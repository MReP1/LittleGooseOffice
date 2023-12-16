package little.goose.design.catalog.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import little.goose.chart.ChartLabel
import little.goose.chart.bar.BarChart
import little.goose.chart.bar.BarChartProperties
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
                add(BarData("${it + 1}", (it + 1).toFloat()))
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
        onSelectedDataChange = onSelectedDataChange,
        properties = remember { BarChartProperties(showXText = true) }
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
                }
            }
        }
    }
}