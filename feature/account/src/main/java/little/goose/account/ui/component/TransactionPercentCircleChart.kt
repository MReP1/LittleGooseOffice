package little.goose.account.ui.component

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    Canvas(
        modifier = modifier
    ) {
        var startAngle = 0F
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
    }
}