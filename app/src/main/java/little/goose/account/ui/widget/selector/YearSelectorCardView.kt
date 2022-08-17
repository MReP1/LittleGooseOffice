package little.goose.account.ui.widget.selector

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import little.goose.account.R
import java.util.*

class YearSelectorCardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : MaterialCardView(context, attrs) {

    private val yearSelector: YearSelector

    init {
        inflate(context, R.layout.layout_card_year_selector, this).apply {
            yearSelector = findViewById(R.id.year_selector)
        }
        this.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary_color))
    }

    fun setYear(year: Int) {
        yearSelector.setYear(year)
    }

    fun setYear(date: Date) {
        yearSelector.setYear(date)
    }

    fun getYear() = yearSelector.getYear()

    fun setOnThisYearClickListener(listener: OnClickListener) {
        yearSelector.setOnThisYearClickListener(listener)
    }

    fun setOnYearSelectListener(listener: YearSelector.OnYearSelectListener) {
        yearSelector.setOnYearSelectListener(listener)
    }

}