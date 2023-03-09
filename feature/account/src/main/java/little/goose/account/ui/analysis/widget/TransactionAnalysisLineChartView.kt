package little.goose.account.ui.analysis.widget

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.ColorInt
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import little.goose.account.data.models.TimeMoney

class TransactionAnalysisLineChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LineChart(context, attrs) {

    private val entries = ArrayList<Entry>()
    private val lineData = LineData()
    private lateinit var lineDataSet: LineDataSet

    init {
        description = null
        legend.isEnabled = false
        xAxis.isEnabled = false
        axisRight.isEnabled = false
        setScaleEnabled(false)
    }

    enum class Type {
        Common, Balance
    }

    fun bindData(
        list: List<TimeMoney>,
        type: Type,
        @ColorInt lineColor: Int,
        @ColorInt circleColor: Int
    ) {
        entries.clear()
        when (type) {
            Type.Common -> {
                axisLeft.axisMinimum = 0F
            }
            Type.Balance -> {
                axisLeft.resetAxisMinimum()
            }
        }
        when (type) {
            Type.Common -> for (index in list.indices) {
                entries.add(Entry(index.toFloat(), list[index].money.abs().toFloat(), list[index]))
            }
            Type.Balance -> for (index in list.indices) {
                entries.add(Entry(index.toFloat(), list[index].money.toFloat(), list[index]))
            }
        }
        lineDataSet = LineDataSet(entries, null).apply {
            lineWidth = 1.6f
            circleRadius = 3f
            color = lineColor
            setCircleColor(circleColor)
            valueTextSize = 0f
            setDrawCircleHole(false)
        }
        lineData.removeDataSet(0)
        lineData.addDataSet(lineDataSet)
        data = lineData
        highlightValue(null)
    }
}