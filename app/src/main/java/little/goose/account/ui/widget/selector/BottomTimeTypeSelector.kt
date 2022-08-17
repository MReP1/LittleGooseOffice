package little.goose.account.ui.widget.selector

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import little.goose.account.R

class BottomTimeTypeSelector @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val tvYear: TextView
    private val tvMonth: TextView
    private var onTypeChangeListener: OnTypeChangeListener? = null

    init {
        val view = inflate(context, R.layout.layout_bottom_time_type_selector, this)
        tvYear = view.findViewById(R.id.tv_year)
        tvMonth = view.findViewById(R.id.tv_month)
        initView()
    }

    private fun initView() {
        tvYear.setOnClickListener {
            tvYear.setBackgroundResource(R.color.cancel_button)
            tvYear.setTextColor(ContextCompat.getColor(context, R.color.white))
            tvMonth.setBackgroundResource(R.color.primary_color)
            tvMonth.setTextColor(ContextCompat.getColor(context, R.color.nor_text_color))
            onTypeChangeListener?.onYearSelect(tvYear)
        }
        tvMonth.setOnClickListener {
            tvYear.setBackgroundResource(R.color.primary_color)
            tvYear.setTextColor(ContextCompat.getColor(context, R.color.nor_text_color))
            tvMonth.setBackgroundResource(R.color.cancel_button)
            tvMonth.setTextColor(ContextCompat.getColor(context, R.color.white))
            onTypeChangeListener?.onMonthSelect(tvMonth)
        }
    }

    fun setOnTypeChangeListener(listener: OnTypeChangeListener) {
        onTypeChangeListener = listener
    }

    fun setMonth(month: Int) {
        if (month > 0) {
            tvMonth.text = resources.getString(R.string.some_month, month)
        } else {
            tvMonth.text = resources.getString(R.string.month)
        }
    }

    fun setYear(year: Int) {
        tvYear.text = resources.getString(R.string.some_year, year)
    }

    interface OnTypeChangeListener {
        fun onYearSelect(view: TextView)
        fun onMonthSelect(view: TextView)
    }
}