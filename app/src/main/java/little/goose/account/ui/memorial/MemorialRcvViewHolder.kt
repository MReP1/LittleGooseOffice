package little.goose.account.ui.memorial

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import little.goose.account.R
import little.goose.account.appContext
import little.goose.account.common.MultipleChoseHandler
import little.goose.account.databinding.ItemMemorialBinding
import little.goose.account.logic.data.entities.Memorial
import little.goose.account.utils.appendTimeSuffix
import little.goose.account.utils.isFuture

class MemorialRcvViewHolder(
    private val binding: ItemMemorialBinding,
    private val multipleChoseHandler: MultipleChoseHandler<Memorial>? = null
) : RecyclerView.ViewHolder(binding.root) {

    fun bindView(memorial: Memorial) {
        initSelect(memorial)
        binding.apply {
            tvMemorialTitle.text = memorial.content.appendTimeSuffix(memorial.time)
            tvMemorialTime.setTime(memorial.time)
            if (memorial.time.isFuture()) {
                tvMemorialTime.setBackgroundColor(
                    ContextCompat.getColor(appContext, R.color.red_500)
                )
                tvDay.setBackgroundColor(
                    ContextCompat.getColor(appContext, R.color.red_700)
                )
            } else {
                tvMemorialTime.setBackgroundColor(
                    ContextCompat.getColor(appContext, R.color.green_500)
                )
                tvDay.setBackgroundColor(
                    ContextCompat.getColor(appContext, R.color.green_700)
                )
            }
        }
    }

    fun setSelect(isSelect: Boolean) {
        if (isSelect) {
            binding.ivBgVector.visibility = View.VISIBLE
        } else {
            binding.ivBgVector.visibility = View.GONE
        }
    }

    private fun initSelect(memorial: Memorial) {
        multipleChoseHandler?.itemList?.contains(memorial)?.let { isSelect ->
            //fixme 每个ViewHolder都得遍历一遍，性能太差，需要优化
            setSelect(isSelect)
        }
    }

}