package little.goose.chart.pie

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    dataList: List<PieData>
) {
    val amountSum = remember(dataList) {
        dataList.map { it.amount }
            .takeIf { it.isNotEmpty() }
            ?.reduce { acc, amount -> acc + amount } ?: 1F
    }

    Row(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 50.dp.toPx()
            val topLeftOffset = if (size.width > size.height) {
                Offset((size.width - size.height) / 2 + (strokeWidth / 2), strokeWidth / 2)
            } else {
                Offset(strokeWidth / 2, (size.height - size.width) / 2 + (strokeWidth / 2))
            }
            var startAngle = 0F
            for (data in dataList) {
                val sweepAngle = (data.amount / amountSum) * 360F
                drawArc(
                    color = data.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    topLeft = topLeftOffset,
                    size = Size(size.minDimension - strokeWidth, size.minDimension - strokeWidth),
                    useCenter = false,
                    style = Stroke(strokeWidth)
                )
                startAngle += sweepAngle
            }
        }
    }
}