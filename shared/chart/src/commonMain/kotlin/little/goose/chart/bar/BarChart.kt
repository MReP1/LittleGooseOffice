package little.goose.chart.bar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import little.goose.shared.common.roundTo

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
    selectedData: BarData?,
    onSelectedDataChange: (BarData?) -> Unit,
    properties: BarChartProperties = remember { BarChartProperties() }
) {
    val currentSelectedData by rememberUpdatedState(selectedData)
    val currentDataList by rememberUpdatedState(dataList)
    val currentOnSelectedDataChange by rememberUpdatedState(onSelectedDataChange)
    val colorScheme = MaterialTheme.colorScheme
    val density = LocalDensity.current

    val textMeasurer = rememberTextMeasurer(dataList.size)

    val xTextList by remember(dataList) {
        derivedStateOf { dataList.map { it.xText } }
    }

    val xTextResult = remember(properties.showXText, xTextList) {
        if (properties.showXText) {
            xTextList.map {
                textMeasurer.measure(
                    it,
                    TextStyle.Default.copy(fontSize = properties.xTextSize)
                )
            }
        } else emptyList()
    }

    val maxXTextHeight = remember(properties.showXText, xTextResult) {
        if (properties.showXText) {
            xTextResult.maxOf { it.size.height }.toFloat()
        } else 0F
    }

    val maxAmount = remember(dataList) { dataList.maxOfOrNull { it.amount } ?: 0F }

    val minAmount = remember(dataList) { dataList.minOfOrNull { it.amount } ?: 0F }

    val amountDiff = maxAmount - minAmount

    // 前面预留 1.2 个单位距离，后面预留 0.8 个单位距离
    val startOffsetUnit = 1.2F
    val endOffsetUnit = 0.8F

    val yTextStyle = remember(properties.yTextSize) {
        TextStyle.Default.copy(fontSize = properties.yTextSize)
    }

    val yTextResult = rememberMeasureAmount(yTextStyle, maxAmount, minAmount)

    val startX = remember(
        density, yTextResult
    ) {
        with(density) {
            maxOf(
                yTextResult.step1TextResult.size.width,
                yTextResult.step2TextResult.size.width,
                yTextResult.step3TextResult.size.width,
                yTextResult.step4TextResult.size.width
            ) + 6.dp.toPx()
        }
    }

    Canvas(modifier = modifier.pointerInput(startX) {
        detectTapGestures { offset ->
            val dataSize = currentDataList.size + (startOffsetUnit + endOffsetUnit)
            val singleWidth = (size.width - startX) / dataSize
            val index = ((offset.x) - startX - (startOffsetUnit * singleWidth)) / singleWidth
            if (index in 0F..currentDataList.size.toFloat()) {
                val realIndex = index.toInt().coerceIn(0..currentDataList.lastIndex)
                val data = currentDataList[realIndex]
                currentOnSelectedDataChange(if (currentSelectedData != data) data else null)
            } else {
                currentOnSelectedDataChange(null)
            }
        }
    }) {

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
            yTextResult.step1TextResult,
            topLeft = Offset(
                startX - yTextResult.step1TextResult.size.width - 2.dp.toPx(),
                step1Y - (yTextResult.step1TextResult.size.height / 2)
            )
        )
        drawText(
            yTextResult.step2TextResult,
            topLeft = Offset(
                startX - yTextResult.step2TextResult.size.width - 2.dp.toPx(),
                step2Y - (yTextResult.step2TextResult.size.height / 2)
            )
        )
        drawText(
            yTextResult.step3TextResult,
            topLeft = Offset(
                startX - yTextResult.step3TextResult.size.width - 2.dp.toPx(),
                step3Y - (yTextResult.step3TextResult.size.height / 2)
            )
        )
        drawText(
            yTextResult.step4TextResult,
            topLeft = Offset(
                startX - yTextResult.step4TextResult.size.width - 2.dp.toPx(),
                step4Y - (yTextResult.step4TextResult.size.height / 2)
            )
        )

        drawPath(
            path,
            color = if (properties.axisColor.isSpecified) properties.axisColor else colorScheme.outline
        )

        val dataSize = dataList.size + (startOffsetUnit + endOffsetUnit)
        val singleWidth = (size.width - startX) / dataSize
        var barStartX = startX + (singleWidth * 1.2).toInt()
        for (data in dataList) {
            path.reset()
            val x = barStartX
            val y =
                step4Y - ((data.amount - minAmount) / amountDiff) * (step4Y - step1Y) - 2.dp.toPx() // 往上偏移一个刻度
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
            barStartX += singleWidth
        }
    }
}

@Stable
private data class BarChartTextResult(
    val step1TextResult: TextLayoutResult,
    val step2TextResult: TextLayoutResult,
    val step3TextResult: TextLayoutResult,
    val step4TextResult: TextLayoutResult
)

@Composable
private fun rememberMeasureAmount(
    textStyle: TextStyle,
    maxAmount: Float,
    minAmount: Float
): BarChartTextResult {

    val textMeasurer = rememberTextMeasurer(4)

    val amountDiff = maxAmount - minAmount

    val step1Text = remember(maxAmount) {
        maxAmount.toString().roundTo(2)
    }

    val step2Text = remember(maxAmount, amountDiff) {
        (maxAmount - (amountDiff / 3)).toString().roundTo(2)
    }

    val step3Text = remember(maxAmount, amountDiff) {
        (maxAmount - (amountDiff / 3 * 2)).toString().roundTo(2)
    }

    val step4Text = remember(minAmount) {
        minAmount.toString().roundTo(2)
    }

    val step1TextResult = remember(step1Text, textStyle) {
        textMeasurer.measure(step1Text, textStyle)
    }

    val step2TextResult = remember(step2Text, textStyle) {
        textMeasurer.measure(step2Text, textStyle)
    }

    val step3TextResult = remember(step3Text, textStyle) {
        textMeasurer.measure(step3Text, textStyle)
    }

    val step4TextResult = remember(step4Text, textStyle) {
        textMeasurer.measure(step4Text, textStyle)
    }

    return remember(
        step1TextResult, step2TextResult, step3TextResult, step4TextResult
    ) {
        BarChartTextResult(
            step1TextResult, step2TextResult, step3TextResult, step4TextResult
        )
    }
}

private fun Path.addMarkLine(x: Float, y: Float, height: Float, width: Float) {
    addRoundRect(RoundRect(rect = Rect(x, y - (height / 2), x + width, y + (height / 2))))
}