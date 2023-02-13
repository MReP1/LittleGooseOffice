package little.goose.account.ui.widget

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import little.goose.account.R
import java.util.*

class MonthSelectorCardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : MaterialCardView(context, attrs) {

    private val monthSelector: MonthSelector

    init {
        val view = inflate(context, R.layout.layout_card_month_selector, this)
        monthSelector = view.findViewById(R.id.month_selector)
        this.setCardBackgroundColor(ContextCompat.getColor(context, little.goose.common.R.color.primary_color))
    }

    fun setTime(year: Int, month: Int) {
        monthSelector.setTime(year, month)
    }

    fun setTime(date: Date) {
        monthSelector.setTime(date)
    }

    fun getTime() = monthSelector.getTime()

    fun setOnThisMonthClickListener(listener: OnClickListener) {
        monthSelector.setOnThisMonthClickListener(listener)
    }

    fun setOnMonthSelectListener(listener: MonthSelector.OnMonthSelectListener) {
        monthSelector.setOnMonthSelectListener(listener)
    }

}