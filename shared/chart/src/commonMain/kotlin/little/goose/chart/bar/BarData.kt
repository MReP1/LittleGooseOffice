package little.goose.chart.bar

import androidx.compose.runtime.saveable.listSaver
import androidx.compose.ui.graphics.Color

data class BarData(
    val xText: String,
    val amount: Float,
    val color: Color,
    val id: String = ""
) {
    companion object {
        val saver = listSaver<BarData?, Any?>(
            save = { barData ->
                barData?.let {
                    listOf(it.xText, it.amount, it.color, it.id)
                } ?: emptyList()
            },
            restore = {
                if (it.isEmpty()) null else {
                    BarData(it[0] as String, it[1] as Float, it[2] as Color, it[3] as String)
                }
            }
        )
    }
}
