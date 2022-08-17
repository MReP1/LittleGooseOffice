package little.goose.account.ui.memorial.widget

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.card.MaterialCardView
import little.goose.account.R
import little.goose.account.databinding.LayoutMemorialTitleCardBinding
import little.goose.account.logic.data.entities.Memorial
import little.goose.account.utils.appendTimePrefix
import little.goose.account.utils.appendTimeSuffix
import little.goose.account.utils.toChineseYearMonDayWeek

class MemorialTitleCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : MaterialCardView(context, attrs) {

    private var binding: LayoutMemorialTitleCardBinding

    init {
        val view = inflate(context, R.layout.layout_memorial_title_card, this)
        binding = LayoutMemorialTitleCardBinding.bind(view)
    }

    fun setMemorial(memorial: Memorial) {
        binding.apply {
            tvTitleMemoTime.setTime(memorial.time)
            tvTitleOriTime.text =
                memorial.time.toChineseYearMonDayWeek().appendTimePrefix(memorial.time)
            tvTitleContent.text = memorial.content.appendTimeSuffix(memorial.time)
        }
    }
}