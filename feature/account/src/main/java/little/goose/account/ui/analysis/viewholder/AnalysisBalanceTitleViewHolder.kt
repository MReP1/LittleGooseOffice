package little.goose.account.ui.analysis.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import little.goose.account.R
import little.goose.account.databinding.LayoutAnalysisBalanceTitleBinding

class AnalysisBalanceTitleViewHolder(private val binding: LayoutAnalysisBalanceTitleBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private var isInit = false

    fun bindView(isShow: Boolean = false) {
        if (isShow) {
            binding.root.visibility = View.VISIBLE
            if (!isInit) {
                binding.llBalanceTitle.apply {
                    tvOne.text = root.context.getString(R.string.date)
                    tvTwo.text = root.context.getString(R.string.income)
                    tvThree.text = root.context.getString(R.string.expense)
                    tvFour.text = root.context.getString(R.string.balance)
                }
                isInit = true
            }
        } else {
            binding.root.visibility = View.GONE
        }
    }

}