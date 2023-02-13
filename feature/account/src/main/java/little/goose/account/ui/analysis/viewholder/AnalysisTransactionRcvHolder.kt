package little.goose.account.ui.analysis.viewholder

import androidx.recyclerview.widget.RecyclerView
import little.goose.account.databinding.ItemTransactionAnalysisBinding
import little.goose.account.ui.transaction.icon.TransactionIconHelper

class AnalysisTransactionRcvHolder(private val binding: ItemTransactionAnalysisBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bindData(transactionPercent: little.goose.account.data.models.TransactionPercent) {
        binding.apply {
            ivCover.setImageResource(TransactionIconHelper.getIconPath(transactionPercent.icon_id))
            percentView.setPercent(transactionPercent.percent)
            tvMoney.text = transactionPercent.money.toPlainString()
            tvContent.text = transactionPercent.content
        }
    }
}