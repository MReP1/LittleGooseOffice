package little.goose.chart.pie

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toOffset
import kotlin.math.PI
import kotlin.math.atan2

@Stable
data class PieChartProperties(
    val strokeWidth: Dp = 50.dp,
    val selectedStrokeWidth: Dp = 58.dp
)

@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    dataList: List<PieData>,
    pieChartProperties: PieChartProperties = PieChartProperties(),
    selectedContent: @Composable (PieData) -> Unit = { data ->
        Text(text = data.content, style = MaterialTheme.typography.labelSmall, color = LocalContentColor.current)
    }
) {
    val amountSum = remember(dataList) {
        dataList.map { it.amount }
            .takeIf { it.isNotEmpty() }
            ?.reduce { acc, amount -> acc + amount } ?: 1F
    }

    var selectedData: PieData? by remember { mutableStateOf(null) }

    Box(
        modifier = modifier.padding((pieChartProperties.selectedStrokeWidth - pieChartProperties.strokeWidth) / 2),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(dataList) {
                    detectTapGestures { offset ->
                        val center = this.size.center.toOffset()
                        val centerOffset = offset - center
                        val angle = atan2(centerOffset.y, centerOffset.x) * 180 / PI
                        val realAngle = if (angle > 0) angle else angle + 360
                        var currentAngle = 0F
                        for (index in dataList.indices) {
                            val data = dataList[index]
                            val sweepAngle = (data.amount / amountSum) * 360F
                            if (realAngle in currentAngle..(sweepAngle + currentAngle)) {
                                selectedData = if (selectedData != data) data else null
                            }
                            currentAngle += sweepAngle
                        }
                    }
                }
        ) {
            val strokeWidth = pieChartProperties.strokeWidth.toPx()
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
                    style = Stroke(if (data == selectedData) pieChartProperties.selectedStrokeWidth.toPx() else strokeWidth)
                )
                startAngle += sweepAngle
            }
        }

        selectedData?.let { data ->
            selectedContent(data)
        }
    }
}