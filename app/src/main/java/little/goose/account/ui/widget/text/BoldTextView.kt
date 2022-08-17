package little.goose.account.ui.widget.text

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import little.goose.account.R

class BoldTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    init {
        context.obtainStyledAttributes(attrs, R.styleable.BoldTextView).apply {
            val weight = getInt(R.styleable.BoldTextView_weight, 1)
            setWeight(weight)
            recycle()
        }
    }

    /**
     * 0 -> regular
     * 1 -> medium
     * 2 -> bold
     * 3 -> black
     * */
    private fun setWeight(weight: Int) {
        when (weight) {
            1 -> {
                typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            }
            2 -> {
                paint.isFakeBoldText = true
            }
            3 -> {
                typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                paint.isFakeBoldText = true
            }
        }
    }

}