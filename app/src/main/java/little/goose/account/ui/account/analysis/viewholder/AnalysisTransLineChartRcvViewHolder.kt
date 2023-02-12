package little.goose.account.ui.account.analysis.viewholder

import android.annotation.SuppressLint
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import little.goose.account.R
import little.goose.common.dialog.time.TimeType
import little.goose.account.databinding.LayoutHeaderTransLineChartBinding
import little.goose.account.logic.data.constant.MoneyType
import little.goose.account.logic.data.models.TimeMoney
import little.goose.account.ui.account.TransactionExampleActivity
import little.goose.account.ui.account.analysis.AnalysisFragmentViewModel.Companion.MONTH
import little.goose.common.utils.toChineseMonth
import little.goose.common.utils.toChineseMonthDay
import java.math.BigDecimal

const val DEFAULT = 0
const val BALANCE = 1
class AnalysisTransLineChartRcvViewHolder(
    private val binding: LayoutHeaderTransLineChartBinding,
    private val timeType: Int
) : RecyclerView.ViewHolder(binding.root) {

    private val entries = ArrayList<Entry>()
    private lateinit var lineDataSet: LineDataSet
    private lateinit var lineData: LineData

    private var isInit = false

    private var type = DEFAULT

    fun bindData(list: List<TimeMoney>, type: Int = DEFAULT) {
        this.type = type
        if (!isInit) {
            initLineChart(list)
            isInit = true
        } else {
            updateData(list)
        }
    }

    private fun initLineChart(list: List<TimeMoney>) {
        listTransform(list)

        binding.lineChart.apply {
            description = null
            legend.isEnabled = false
            xAxis.isEnabled = false
            axisRight.isEnabled = false
            if (type == DEFAULT) {
                axisLeft.axisMinimum = 0F
            }
            setScaleEnabled(false)
            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                @SuppressLint("SetTextI18n")
                override fun onValueSelected(entry: Entry?, highLight: Highlight?) {
                    if (entry == null || highLight == null) return
                    binding.tvDetail.visibility = View.VISIBLE
                    val timeMoney = entry.data as TimeMoney

                    val timeTypeTemp = if (timeType == MONTH) {
                        TimeType.DATE
                    }  else TimeType.YEAR_MONTH

                    val moneyType = if (type == BALANCE) {
                        MoneyType.BALANCE
                    } else {
                        if (timeMoney.money.signum() < 0) {
                            MoneyType.EXPENSE
                        } else MoneyType.INCOME
                    }

                    binding.llDetail.visibility = View.VISIBLE
                    binding.llDetail.setOnClickListener {
                        TransactionExampleActivity.open(
                            context, timeMoney.time, timeTypeTemp, moneyType
                        )
                    }

                    val timeText = if (timeType == MONTH) {
                        timeMoney.time.toChineseMonthDay()
                    } else {
                        timeMoney.time.toChineseMonth()
                    }

                    val typeText = if (timeMoney.money.signum() < 0) {
                        getResString(R.string.consume)
                    } else {
                        getResString(R.string.income)
                    }

                    val moneyText = timeMoney.money.abs().toPlainString()
                    val noTransactionText = getResString(R.string.no_transaction)
                    if (timeMoney.money == BigDecimal(0)) {
                        binding.tvDetail.text = noTransactionText
                        binding.ivArrow.visibility = View.GONE
                    } else {
                        binding.tvDetail.text = "$timeText ${typeText}了${moneyText}元"
                        binding.ivArrow.visibility = View.VISIBLE
                    }
                }

                override fun onNothingSelected() {
                    binding.apply {
                        tvDetail.setCompoundDrawables(null, null, null, null)
                        tvDetail.visibility = View.INVISIBLE
                        ivArrow.visibility = View.GONE
                    }
                }
            })
            data = this@AnalysisTransLineChartRcvViewHolder.lineData
        }
    }

    private fun updateData(list: List<TimeMoney>) {
        listTransform(list)
        binding.apply {
            tvDetail.visibility = View.INVISIBLE
            ivArrow.visibility = View.GONE
            lineChart.apply {
                highlightValue(null)
                data = this@AnalysisTransLineChartRcvViewHolder.lineData
            }
        }
    }

    private fun listTransform(list: List<TimeMoney>) {
        entries.clear()

        if (this.type == BALANCE) {
            for (index in list.indices) {
                entries.add(Entry(index.toFloat(), list[index].money.toFloat(), list[index]))
            }
        } else {
            for (index in list.indices) {
                entries.add(Entry(index.toFloat(), list[index].money.abs().toFloat(), list[index]))
            }
        }
        lineDataSet = LineDataSet(entries, null).apply {
            lineWidth = 1.6f
            circleRadius = 3f
            color = getResColor(R.color.red_200)
            setCircleColor(getResColor(R.color.red_500))
            valueTextSize = 0f
            setDrawCircleHole(false)
        }
        if (!isInit) {
            lineData = LineData(lineDataSet)
        } else {
            lineData.removeDataSet(0)
            lineData.addDataSet(lineDataSet)
        }
    }

    private fun getResColor(@ColorRes colorRes: Int): Int {
        return ContextCompat.getColor(binding.root.context, colorRes)
    }

    private fun getResString(@StringRes stringRes: Int): String {
        return binding.root.context.getString(stringRes)
    }
}