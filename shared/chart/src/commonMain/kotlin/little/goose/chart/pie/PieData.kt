package little.goose.chart.pie

import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

@Stable
data class PieData(
    val content: String,
    val amount: Float,
    val color: Color,
    val id: String = ""
) {
    companion object {
        val saver = listSaver<PieData?, Any?>(
            save = { pieData ->
                pieData?.let {
                    listOf(it.content, it.amount, it.color.toArgb(), it.id)
                } ?: emptyList()
            },
            restore = {
                if (it.isEmpty()) null else {
                    PieData(it[0] as String, it[1] as Float, Color(it[2] as Int), it[3] as String)
                }
            }
        )
    }
}