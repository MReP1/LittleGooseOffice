package little.goose.account.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.withSaveLayer
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import little.goose.account.data.models.TransactionPercent
import little.goose.common.collections.CircularLinkList
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalTextApi::class)
@Composable
fun TransactionPercentCircleChart(
    modifier: Modifier = Modifier,
    transactionPercents: List<TransactionPercent>
) {
    val colorScheme = MaterialTheme.colorScheme
    val colors = remember {
        CircularLinkList<Color>().apply {
            add(colorScheme.primaryContainer)
            add(colorScheme.errorContainer)
            add(colorScheme.secondaryContainer)
            add(colorScheme.tertiaryContainer)
        }
    }
    val trColors = remember(transactionPercents, colors) {
        transactionPercents.map {
            val backgroundColor = colors.next()
            Pair(backgroundColor, colorScheme.contentColorFor(backgroundColor))
        }
    }

    var rotate by remember { mutableStateOf(0F) }
    val draggableState = rememberDraggableState(onDelta = { rotate += it })

    val emptyPaint = remember {
        Paint()
    }
    val blendPaint = remember {
        Paint().apply {
            blendMode = BlendMode.DstIn
            color = Color.Transparent
        }
    }

    val textMeasurer = rememberTextMeasurer(cacheSize = transactionPercents.size)

    val textureResult = remember(transactionPercents) {
        transactionPercents.map {
            textMeasurer.measure(
                buildAnnotatedString {
                    append("  ${it.content}")
                    append("\n")
                    append("${String.format("%.2f", it.percent * 100)}%")
                }
            )
        }
    }

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .rotate(rotate)
                .draggable(draggableState, Orientation.Vertical)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawIntoCanvas { canvas ->
                    var startAngle = 0F
                    val radius = kotlin.math.min(size.width, size.height) / 2
                    canvas.withSaveLayer(
                        Rect(Offset.Zero, Offset(size.width, size.height)), emptyPaint
                    ) {
                        for (index in transactionPercents.indices) {
                            val transactionPercent = transactionPercents[index]
                            val sweepAngle = transactionPercent.percent.toFloat() * 360F
                            drawArc(
                                color = trColors[index].first,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = true
                            )
                            startAngle += sweepAngle
                        }
                        // 减去一个圆，使中间透明
                        canvas.drawCircle(center, radius / 2.5F, blendPaint)
                    }
                }
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            var startAngle = rotate.toDouble()
            val radius = kotlin.math.min(size.width, size.height) / 2
            val textRadius = radius / 3 * 2
            for (index in transactionPercents.indices) {
                val transactionPercent = transactionPercents[index]
                val sweepAngle = transactionPercent.percent.toFloat() * 360.0
                val centerAngle = startAngle + (sweepAngle / 2)
                val radian = Math.toRadians(centerAngle)
                val x = center.x + textRadius * cos(radian) - textureResult[index].size.width / 2
                val y = center.y + textRadius * sin(radian) - textureResult[index].size.height / 2
                drawText(
                    textLayoutResult = textureResult[index],
                    color = trColors[index].second,
                    topLeft = Offset(x.toFloat(), y.toFloat())
                )
                startAngle += sweepAngle
            }
        }
    }
}