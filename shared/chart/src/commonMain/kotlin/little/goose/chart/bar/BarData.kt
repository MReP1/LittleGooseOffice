package little.goose.chart.bar

import androidx.compose.ui.graphics.Color

data class BarData(
    val xText: String,
    val amount: Float,
    val color: Color,
    val id: String = ""
)
