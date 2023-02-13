package little.goose.account.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import little.goose.account.R
import little.goose.common.utils.getMonth
import little.goose.common.utils.getYear
import java.util.*
import kotlin.collections.HashMap

class MonthSelector @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val tvLastMonth: TextView
    private val tvThisMonth: TextView
    private val tvNextMonth: TextView

    private var lastMonth = -1
    private var thisMonth = -1
    private var nextMonth = -1
    private var year = 2000
    private var callback: OnMonthSelectListener? = null

    init {
        inflate(context, R.layout.layout_time_selector, this).apply {
            tvLastMonth = findViewById(R.id.tv_last)
            tvThisMonth = findViewById(R.id.tv_this)
            tvNextMonth = findViewById(R.id.tv_next)
        }
        val attributeTypes = context.obtainStyledAttributes(attrs, R.styleable.MonthSelector)
        val color = attributeTypes.getColorStateList(R.styleable.MonthSelector_unSelectTextColor)
        color?.let {
            tvLastMonth.setTextColor(it)
            tvNextMonth.setTextColor(it)
        }
        attributeTypes.recycle()
        initData()
        initClick()
    }

    private fun initData() {
        Calendar.getInstance().apply {
            initMonth(getMonth())
            year = getYear()
        }
    }

    private fun initMonth(month: Int) {
        when (month) {
            1 -> {
                setLastMonth(12)
                setThisMonth(month)
                setNextMonth(month + 1)
            }
            12 -> {
                setLastMonth(month - 1)
                setThisMonth(month)
                setNextMonth(1)
            }
            else -> {
                setLastMonth(month - 1)
                setThisMonth(month)
                setNextMonth(month + 1)
            }
        }
    }

    private fun setMonth(month: Int) {
        when (month) {
            1 -> {
                if (thisMonth == 12) {
                    year++
                }
                callback?.onMonthSelect(year, month)
                setLastMonth(12)
                setThisMonth(month)
                setNextMonth(month + 1)
            }
            12 -> {
                if (thisMonth == 1) {
                    year--
                }
                callback?.onMonthSelect(year, month)
                setLastMonth(month - 1)
                setThisMonth(month)
                setNextMonth(1)
            }
            else -> {
                callback?.onMonthSelect(year, month)
                setLastMonth(month - 1)
                setThisMonth(month)
                setNextMonth(month + 1)
            }
        }
    }

    private fun initClick() {
        tvLastMonth.setOnClickListener {
            setMonth(lastMonth)
        }
        tvNextMonth.setOnClickListener {
            setMonth(nextMonth)
        }
    }

    fun setOnThisMonthClickListener(listener: OnClickListener) {
        tvThisMonth.setOnClickListener(listener)
    }

    fun setOnMonthSelectListener(callback: OnMonthSelectListener) {
        this.callback = callback
    }

    fun getTime() = HashMap<Int, Int>().apply {
        put(YEAR, year)
        put(MONTH, thisMonth)
    }

    fun setTime(year: Int, month: Int) {
        this.year = year
        setMonth(month)
    }

    fun setTime(date: Date) {
        Calendar.getInstance().apply {
            time = date
            setTime(getYear(), getMonth())
        }
    }

    private fun setLastMonth(month: Int) {
        tvLastMonth.setMonth(month)
        lastMonth = month
    }

    private fun setThisMonth(month: Int) {
        tvThisMonth.setMonth(month)
        thisMonth = month
    }

    private fun setNextMonth(month: Int) {
        tvNextMonth.setMonth(month)
        nextMonth = month
    }

    interface OnMonthSelectListener {
        fun onMonthSelect(year: Int, month: Int)
    }

    companion object {
        const val YEAR = 0
        const val MONTH = 1
    }
}

private fun TextView.setMonth(month: Int) {
    this.text = context.getString(R.string.some_month, month)
}