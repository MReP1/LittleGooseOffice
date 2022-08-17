package little.goose.account.ui.account.analysis.viewholder

import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import little.goose.account.R
import little.goose.account.databinding.LayoutHeaderTransCircleChartBinding
import little.goose.account.logic.data.models.TransactionPercent

class AnalysisTransCircleGraphViewHolder(
    private val binding: LayoutHeaderTransCircleChartBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val entries = ArrayList<PieEntry>()
    private val pieDataSet = PieDataSet(entries, null)
    private val pieData = PieData(pieDataSet)

    private val defaultColorList = listOf(
        getResColor(R.color.red_700),
        getResColor(R.color.red_500),
        getResColor(R.color.red_200)
    )

    private val listColors = ArrayList<Int>()

    private var isInit = false

    fun bindData(list: List<TransactionPercent>) {
        if (!isInit) {
            initChart(list)
            isInit = true
        } else {
            updateData(list)
        }
    }

    private fun initChart(list: List<TransactionPercent>) {
        listTransform(list)
        listColors.getColorList(entries.size)
        pieDataSet.apply {
            valueTextSize = 14F
            valueTextColor = getResColor(R.color.white)
            colors = listColors
        }
        pieData.setValueFormatter(PercentFormatter(binding.pieChart))
        binding.pieChart.apply {
            data = pieData
            setUsePercentValues(true)
            setEntryLabelTextSize(12F)
            setHoleColor(getResColor(R.color.primary_color))
            setCenterTextColor(getResColor(R.color.nor_text_color))
            setCenterTextSize(14F)
            description = null //去掉图表描述
            legend.isEnabled = false //隐藏标注
            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(entry: Entry?, highLight: Highlight?) {
                    val transPercent = entry?.data as? TransactionPercent ?: return
                    binding.pieChart.centerText =
                        transPercent.content + "\n" + transPercent.money.abs().toPlainString()
                }

                override fun onNothingSelected() {
                    binding.pieChart.centerText = ""
                }
            })
        }
    }

    private fun updateData(list: List<TransactionPercent>) {
        listTransform(list)
        listColors.getColorList(entries.size)
        binding.pieChart.apply {
            highlightValue(null) //重置选中
            centerText = "" //重置中心文字
            data = pieData //重置数据
        }
    }

    private fun listTransform(list: List<TransactionPercent>) {
        entries.clear()
        for (transPercent in list) {
            //太小了显示很丑 省略掉
            if (transPercent.percent > 0.02) {
                entries.add(
                    PieEntry(
                        transPercent.percent.toFloat(),
                        transPercent.content,
                        transPercent
                    )
                )
            }
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun getResColor(@ColorRes colorRes: Int): Int {
        return ContextCompat.getColor(binding.root.context, colorRes)
    }

    private fun ArrayList<Int>.getColorList(count: Int) {
        this.clear()
        var firstColor: Int = -1
        var lastColor: Int = -1
        for (i in 1..count) {
            var currentColor: Int
            //保证最后一个颜色和第一个颜色不同
            if (i == count) {
                currentColor = defaultColorList.firstOrNull {
                    it != firstColor && it != lastColor
                } ?: lastColor
                this.add(currentColor)
                break
            }
            when {
                i % 3 == 0 -> this.add(defaultColorList[2].also { currentColor = it })
                i % 2 != 0 -> this.add(defaultColorList[1].also { currentColor = it })
                else -> {
                    currentColor = defaultColorList[0] //赋值最近的颜色
                    if (i == 0) firstColor = currentColor //赋值第一个颜色
                    this.add(currentColor)
                }
            }

            if (i == 1) {
                firstColor = currentColor
            }

            lastColor = currentColor
        }
    }

}