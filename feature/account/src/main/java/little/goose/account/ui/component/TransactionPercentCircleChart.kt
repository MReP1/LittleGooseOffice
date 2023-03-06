package little.goose.account.ui.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import little.goose.account.data.models.TransactionPercent
import little.goose.common.collections.CircularLinkList

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
            add(colorScheme.tertiaryContainer)
        }
    }
    Spacer(
        modifier = modifier.drawWithCache {
            val emptyPaint = Paint()
            val blendPaint = Paint().apply {
                blendMode = BlendMode.DstIn
                shader = LinearGradientShader(
                    Offset.Zero, Offset(size.width, size.height),
                    listOf(Color.Transparent, Color.Transparent)
                )
            }
            onDrawWithContent {
                var startAngle = 0F
                drawIntoCanvas { canvas ->
                    val radius = kotlin.math.min(size.width, size.height)
                    canvas.withSaveLayer(
                        Rect(Offset.Zero, Offset(size.width, size.height)), emptyPaint
                    ) {
                        for (transactionPercent in transactionPercents) {
                            val sweepAngle = transactionPercent.percent.toFloat() * 360F
                            drawArc(
                                color = colors.next(),
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = true
                            )
                            startAngle += sweepAngle
                        }
                        // 减去一个圆，使中间透明
                        canvas.drawCircle(center, radius / 5, blendPaint)
                    }
                }
            }
        }
    )
}