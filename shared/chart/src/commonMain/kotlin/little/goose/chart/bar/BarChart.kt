package little.goose.chart.bar

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import little.goose.chart.util.roundTo

data class BarChartProperties(
    val showYText: Boolean = true,
    val yTextSize: TextUnit = 10.sp,
    val showXText: Boolean = false,
    val xTextSize: TextUnit = 10.sp,
    val axisColor: Color = Color.Unspecified
)

@Composable
fun BarChart(
    modifier: Modifier = Modifier,
    dataList: List<BarData>,
    properties: BarChartProperties = remember { BarChartProperties() }
) {
    val colorScheme = MaterialTheme.colorScheme

    val textMeasurer = rememberTextMeasurer(dataList.size)

    val xTextList by remember(dataList) {
        derivedStateOf { dataList.map { it.xText } }
    }

    val xTextResult = remember(properties.showXText, xTextList) {
        if (properties.showXText) {
            xTextList.map { textMeasurer.measure(it, TextStyle.Default.copy(fontSize = properties.xTextSize)) }
        } else emptyList()
    }

    val maxXTextHeight = remember(properties.showXText, xTextResult) {
        if (properties.showXText) {
            xTextResult.maxOf { it.size.height }.toFloat()
        } else 0F
    }

    val maxAmount = remember(dataList) { dataList.maxOf { it.amount } }

    val minAmount = remember(dataList) { dataList.minOf { it.amount } }

    val amountDiff = maxAmount - minAmount

    Canvas(modifier = modifier) {

        val step1Text = maxAmount.toString().roundTo(2)
        val step2Text = (maxAmount - (amountDiff / 3)).toString().roundTo(2)
        val step3Text = (maxAmount - (amountDiff / 3 * 2)).toString().roundTo(2)
        val step4Text = minAmount.toString().roundTo(2)

        val yTextStyle = TextStyle.Default.copy(fontSize = properties.yTextSize)
        val step1TextResult = textMeasurer.measure(step1Text, yTextStyle)
        val step2TextResult = textMeasurer.measure(step2Text, yTextStyle)
        val step3TextResult = textMeasurer.measure(step3Text, yTextStyle)
        val step4TextResult = textMeasurer.measure(step4Text, yTextStyle)

        val startX = maxOf(
            step1TextResult.size.width,
            step2TextResult.size.width,
            step3TextResult.size.width,
            step4TextResult.size.width
        ) + 6.dp.toPx()

        val endX = size.width
        val startY = 0F
        val endY = size.height - maxXTextHeight
        val innerStartX = startX + 2.dp.toPx()
        val innerStartY = endY - 2.dp.toPx()

        val path = Path()

        // 上下预留 16dp
        val spaceHeight = 16.dp.toPx()
        val step = ((endY - spaceHeight) - (startY + spaceHeight)) / 3
        val step1Y = spaceHeight
        val step2Y = spaceHeight + step
        val step3Y = spaceHeight + step * 2
        val step4Y = spaceHeight + step * 3

        // 绘制四个刻度点
        path.addMarkLine(innerStartX, step1Y, 2.dp.toPx(), 2.dp.toPx())
        path.addMarkLine(innerStartX, step2Y, 2.dp.toPx(), 2.dp.toPx())
        path.addMarkLine(innerStartX, step3Y, 2.dp.toPx(), 2.dp.toPx())
        path.addMarkLine(innerStartX, step4Y, 2.dp.toPx(), 2.dp.toPx())

        // 绘制 x, y 两根线
        path.addRoundRect(RoundRect(rect = Rect(startX, startY, innerStartX, endY)))
        path.addRoundRect(RoundRect(rect = Rect(startX, innerStartY, endX, endY)))

        drawText(
            step1TextResult,
            topLeft = Offset(
                startX - step1TextResult.size.width - 2.dp.toPx(),
                step1Y - (step1TextResult.size.height / 2)
            )
        )
        drawText(
            step2TextResult,
            topLeft = Offset(
                startX - step2TextResult.size.width - 2.dp.toPx(),
                step2Y - (step2TextResult.size.height / 2)
            )
        )
        drawText(
            step3TextResult,
            topLeft = Offset(
                startX - step3TextResult.size.width - 2.dp.toPx(),
                step3Y - (step3TextResult.size.height / 2)
            )
        )
        drawText(
            step4TextResult,
            topLeft = Offset(
                startX - step4TextResult.size.width - 2.dp.toPx(),
                step4Y - (step4TextResult.size.height / 2)
            )
        )

        drawPath(path, color = if (properties.axisColor.isSpecified) properties.axisColor else colorScheme.outline)

        // 前面预留 1.2 个单位距离，后面预留 0.8 个单位距离
        val dataSize = dataList.size + 2
        val singleWidth = (size.width - startX) / dataSize
        var barStartX = startX + (singleWidth * 1.2).toInt()
        for (data in dataList) {
            path.reset()
            val x = barStartX
            val y = step4Y - ((data.amount - minAmount) / amountDiff) * (step4Y - step1Y) - 2.dp.toPx() // 往上偏移一个刻度
            val width = (singleWidth * 2 / 3)
            val height = endY - y - 2.dp.toPx()
            val cornerStart = width / 3
            val bezierHandleOffset = width / 12
            path.moveTo(x, y + height)
            path.lineTo(x, (y + cornerStart))
            path.cubicTo(x, y + bezierHandleOffset, x + bezierHandleOffset, y, x + cornerStart, y)
            path.lineTo(x + width - cornerStart, y)
            path.cubicTo(
                x + width - bezierHandleOffset, y,
                x + width, y + bezierHandleOffset,
                x + width, y + cornerStart
            )
            path.lineTo(x + width, y + height)
            path.close()
            drawPath(path, data.color)
//            drawRoundRect(
//                color = data.color,
//                topLeft = Offset(x = barStartX, y = y),
//                size = Size(
//                    width = width,
//                    height = endY - y - 2.dp.toPx()
//                ),
//                cornerRadius = CornerRadius(x = width / 4, y = width / 4)
//            )
            barStartX += singleWidth
        }
    }
}

private fun Path.addMarkLine(x: Float, y: Float, height: Float, width: Float) {
    addRoundRect(RoundRect(rect = Rect(x, y - (height / 2), x + width, y + (height / 2))))
}