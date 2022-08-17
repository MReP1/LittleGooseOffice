package little.goose.account.ui.account.analysis.viewholder

import androidx.recyclerview.widget.RecyclerView
import little.goose.account.databinding.ItemTransactionAnalysisBinding
import little.goose.account.logic.data.models.TransactionPercent
import little.goose.account.ui.account.transaction.icon.TransactionIconHelper

class AnalysisTransactionRcvHolder(private val binding: ItemTransactionAnalysisBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bindData(transactionPercent: TransactionPercent) {
        binding.apply {
            ivCover.setImageResource(TransactionIconHelper.getIconPath(transactionPercent.icon_id))
            percentView.setPercent(transactionPercent.percent)
            tvMoney.text = transactionPercent.money.toPlainString()
            tvContent.text = transactionPercent.content
        }
    }
}