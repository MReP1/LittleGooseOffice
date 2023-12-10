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
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import little.goose.shared.common.roundTo

@Stable
data class BarChartProperties(
    val showYText: Boolean = true,
    val yTextSize: TextUnit = 10.sp,
    val showXText: Boolean = false,
    val xTextSize: TextUnit = 10.sp,
    val axisColor: Color = Color.Unspecified,
    val splitCount: Int = 4,
    // 默认前面预留 1.2 个单位距离，后面预留 0.8 个单位距离
    val startOffsetUnit: Float = 1.2F,
    val endOffsetUnit: Float = 0.8F,
    val topSpaceOffset: Dp = 18.dp,
    val bottomSpaceOffset: Dp = 14.dp,
    val lineWidth: Dp = 2.dp
) {

    val selectedDataColor: Color
        @Composable get() = MaterialTheme.colorScheme.primary

    val defaultDataColor: Color
        @Composable get() = MaterialTheme.colorScheme.tertiary

    init {
        require(splitCount > 2) {
            "BarChartProperties splitCount must be more than 2"
        }
    }
}

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
            xTextList.map { text ->
                textMeasurer.measure(
                    text,
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

    val yTextResult = rememberMeasureAmountResult(
        textMeasurer, properties.yTextSize, properties.splitCount, maxAmount, minAmount
    )

    val startX = remember(density, yTextResult) {
        with(density) {
            (yTextResult.maxOfOrNull { it.size.width } ?: 0) + 6.dp.toPx()
        }
    }

    val path = remember { Path() }

    val defaultDataColor = properties.defaultDataColor
    val selectedDataColor = properties.selectedDataColor

    Canvas(modifier = modifier.pointerInput(startX) {
        detectTapGestures { offset ->
            val dataSize =
                currentDataList.size + (properties.startOffsetUnit + properties.endOffsetUnit)
            val singleWidth = (size.width - startX) / dataSize
            val index =
                ((offset.x) - startX - (properties.startOffsetUnit * singleWidth)) / singleWidth
            if (index in 0F..currentDataList.size.toFloat()) {
                val realIndex = index.toInt().coerceIn(0..currentDataList.lastIndex)
                val data = currentDataList[realIndex]
                currentOnSelectedDataChange(if (currentSelectedData != data) data else null)
            } else {
                currentOnSelectedDataChange(null)
            }
        }
    }) {
        path.reset()

        val endX = size.width
        val startY = 0F
        val endY = size.height - maxXTextHeight
        val innerStartX = startX + properties.lineWidth.toPx()
        val innerStartY = endY - properties.lineWidth.toPx()

        val ySpaceHeight =
            (endY - properties.bottomSpaceOffset.toPx()) - (startY + properties.topSpaceOffset.toPx())

        val step = ySpaceHeight / (properties.splitCount - 1)
        var stepY = properties.topSpaceOffset.toPx()

        // 绘制 x, y 两根线
        path.addRoundRect(RoundRect(rect = Rect(startX, startY, innerStartX, endY)))
        path.addRoundRect(RoundRect(rect = Rect(startX, innerStartY, endX, endY)))

        yTextResult.forEach { textLayoutResult ->
            path.addMarkLine(
                innerStartX,
                stepY,
                properties.lineWidth.toPx(),
                properties.lineWidth.toPx()
            )
            drawText(
                textLayoutResult, topLeft = Offset(
                    x = startX - textLayoutResult.size.width - properties.lineWidth.toPx(),
                    y = stepY - (textLayoutResult.size.height / 2)
                )
            )
            stepY += step
        }

        drawPath(
            path,
            color = if (properties.axisColor.isSpecified) properties.axisColor else colorScheme.outline
        )

        val dataSize = dataList.size + (properties.startOffsetUnit + properties.endOffsetUnit)
        val singleWidth = (size.width - startX) / dataSize
        var barStartX = startX + (singleWidth * 1.2).toInt()

        val spaceBottom = endY - properties.bottomSpaceOffset.toPx()

        for (data in dataList) {
            path.reset()
            val x = barStartX
            val y =
                spaceBottom - properties.lineWidth.toPx() /* 往上偏移一个刻度 */ - ((data.amount - minAmount) / amountDiff) * ySpaceHeight
            val width = (singleWidth * 2 / 3)
            val height = endY - y - properties.lineWidth.toPx()
            val cornerStart = width / 3
            val bezierHandleOffset = width / 12

            barStartX += singleWidth

            if (y == endY || height.toInt() == 0) {
                continue
            }

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
            drawPath(
                path,
                color = if (data.color.isSpecified) data.color
                else if (data == selectedData) selectedDataColor
                else defaultDataColor
            )
        }
    }
}

@Composable
private fun rememberMeasureAmountResult(
    textMeasurer: TextMeasurer,
    textSize: TextUnit,
    splitCount: Int,
    maxAmount: Float,
    minAmount: Float
): List<TextLayoutResult> {
    val stepTexts = remember(maxAmount, minAmount) {
        val amountDiff = maxAmount - minAmount
        val unitDiff = amountDiff / (splitCount - 1)
        List(splitCount) { index ->
            (maxAmount - (unitDiff * index)).toString().roundTo(2)
        }
    }

    val yTextStyle = remember(textSize) {
        TextStyle.Default.copy(fontSize = textSize)
    }

    return remember(stepTexts) {
        stepTexts.map { text ->
            textMeasurer.measure(text, yTextStyle)
        }
    }
}

private fun Path.addMarkLine(x: Float, y: Float, height: Float, width: Float) {
    addRoundRect(RoundRect(rect = Rect(x, y - (height / 2), x + width, y + (height / 2))))
}