package little.goose.account.ui.analysis.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import little.goose.account.R

class ItemTabTransactionTypeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val tvTitle: TextView
    private val tvMoney: TextView

    private val selectedColor: Int
    private val unselectedColor: Int

    init {
        val view = inflate(context, R.layout.item_tab_transaction_type, this)
        tvTitle = view.findViewById(R.id.tv_title)
        tvMoney = view.findViewById(R.id.tv_money)
        selectedColor = ContextCompat.getColor(context, little.goose.common.R.color.white)
        unselectedColor = ContextCompat.getColor(context, little.goose.common.R.color.nor_text_color)
    }

    fun setMoney(money: String) {
        tvMoney.text = money
    }

    fun setTitle(title: String) {
        tvTitle.text = title
    }

    fun setSelected() {
        tvTitle.setTextColor(selectedColor)
        tvMoney.setTextColor(selectedColor)
    }

    fun setUnselected() {
        tvTitle.setTextColor(unselectedColor)
        tvMoney.setTextColor(unselectedColor)
    }

    fun setTitle(@StringRes titleRes: Int) {
        tvTitle.text = context.getString(titleRes)
    }

}