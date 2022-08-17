package little.goose.account.ui.memorial.widget

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.card.MaterialCardView
import little.goose.account.R
import little.goose.account.databinding.LayoutMemorialCardBinding
import little.goose.account.logic.data.entities.Memorial
import little.goose.account.utils.appendTimePrefix
import little.goose.account.utils.appendTimeSuffix
import little.goose.account.utils.toChineseYearMonDayWeek
import java.util.*

class MemorialCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : MaterialCardView(context, attrs) {

    private var binding: LayoutMemorialCardBinding

    init {
        val view = inflate(context, R.layout.layout_memorial_card, this)
        binding = LayoutMemorialCardBinding.bind(view)
    }

    fun setMemorial(memorial: Memorial) {
        setContent(memorial.content.appendTimeSuffix(memorial.time))
        setMemoTime(memorial.time)
        setOriTime(memorial.time)
    }

    private fun setContent(content: String) {
        binding.tvContent.text = content
    }

    private fun setMemoTime(time: Date) {
        binding.tvMemoTime.setTime(time)
    }

    private fun setOriTime(time: Date) {
        binding.tvOriTime.text = time.toChineseYearMonDayWeek().appendTimePrefix(time)
    }
}