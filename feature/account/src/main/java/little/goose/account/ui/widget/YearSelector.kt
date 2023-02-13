package little.goose.account.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import little.goose.account.R
import little.goose.common.utils.DateTimeUtils
import little.goose.common.utils.getYear
import java.util.*

class YearSelector @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val tvLastYear: TextView
    private val tvThisYear: TextView
    private val tvNextYear: TextView

    private var onYearSelectListener: OnYearSelectListener? = null

    private var lastYear = -1
    private var thisYear = -1
    private var nextYear = -1

    init {
        inflate(context, R.layout.layout_time_selector, this).apply {
            tvLastYear = findViewById(R.id.tv_last)
            tvThisYear = findViewById(R.id.tv_this)
            tvNextYear = findViewById(R.id.tv_next)
        }
        val attributeTypes = context.obtainStyledAttributes(attrs, R.styleable.YearSelector)
        val color = attributeTypes.getColorStateList(R.styleable.YearSelector_unSelectTextColor)
        color?.let {
            tvLastYear.setTextColor(it)
            tvNextYear.setTextColor(it)
        }
        attributeTypes.recycle()
        initData()
        initClick()
    }

    private fun initData() {
        initYear(DateTimeUtils.getCurrentYear())
    }

    private fun initClick() {
        tvLastYear.setOnClickListener {
            setYear(lastYear)
        }
        tvNextYear.setOnClickListener {
            setYear(nextYear)
        }
    }

    private fun initYear(year: Int) {
        thisYear = year
        setLastYear(year - 1)
        setThisYear(year)
        setNextYear(year + 1)
    }

    fun setYear(year: Int) {
        onYearSelectListener?.onYearSelect(year)
        initYear(year)
    }

    fun setYear(date: Date) {
        val calendar = Calendar.getInstance().apply { time = date }
        setYear(calendar.getYear())
    }

    fun setOnThisYearClickListener(listener: OnClickListener) {
        tvThisYear.setOnClickListener(listener)
    }

    fun getYear() = thisYear

    fun setOnYearSelectListener(listener: OnYearSelectListener) {
        onYearSelectListener = listener
    }

    interface OnYearSelectListener {
        fun onYearSelect(year: Int)
    }

    private fun setLastYear(year: Int) {
        lastYear = year
        tvLastYear.setYear(year)
    }

    private fun setThisYear(year: Int) {
        thisYear = year
        tvThisYear.setYear(year)
    }

    private fun setNextYear(year: Int) {
        nextYear = year
        tvNextYear.setYear(year)
    }
}

private fun TextView.setYear(year: Int) {
    this.text = year.toString()
}